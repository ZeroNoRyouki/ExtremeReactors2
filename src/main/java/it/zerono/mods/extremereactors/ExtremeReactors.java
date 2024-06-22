/*
 *
 * ExtremeReactors.java
 *
 * This file is part of Extreme Reactors 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * DO NOT REMOVE OR EDIT THIS HEADER
 *
 */

package it.zerono.mods.extremereactors;

import it.zerono.mods.extremereactors.api.internal.modpack.wrapper.ApiWrapper;
import it.zerono.mods.extremereactors.config.Config;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.command.ExtremeReactorsCommand;
import it.zerono.mods.extremereactors.gamecontent.mekanism.IMekanismService;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.network.UpdateFluidizerFluidStatus;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.network.UpdateClientsFuelRodsLayout;
import it.zerono.mods.extremereactors.proxy.IForgeProxy;
import it.zerono.mods.extremereactors.proxy.IProxy;
import it.zerono.mods.extremereactors.proxy.ServerProxy;
import it.zerono.mods.zerocore.base.multiblock.part.io.fluid.IFluidPort;
import it.zerono.mods.zerocore.base.multiblock.part.io.power.IPowerPort;
import it.zerono.mods.zerocore.lib.compat.SidedDependencyServiceLoader;
import it.zerono.mods.zerocore.lib.compat.computer.IComputerCraftService;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.network.NetworkHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.Nullable;

@Mod(value = ExtremeReactors.MOD_ID)
public class ExtremeReactors {

    public static final String MOD_ID = "bigreactors";
    public static final String MOD_NAME = "Extreme Reactors 2";
    public static ResourceLocationBuilder ROOT_LOCATION = ResourceLocationBuilder.of(MOD_ID);

    public static ExtremeReactors getInstance() {
        return s_instance;
    }

    public static IProxy getProxy() {
        return s_proxy.get();
    }

    public ExtremeReactors(IEventBus modBus, ModContainer container, Dist distribution) {

        s_instance = this;
        this._network = new NetworkHandler();

        s_proxy = new SidedDependencyServiceLoader<>(IProxy.class, ServerProxy::new);
        if (s_proxy.get() instanceof IForgeProxy forgeProxy) {
            forgeProxy.initialize(modBus);
        }

        Config.initialize(container);
        Content.initialize(modBus);

        modBus.addListener(ExtremeReactors::onInterModProcess);
        modBus.addListener(ExtremeReactors::onRegisterCapabilities);
        modBus.addListener(ExtremeReactors::onRegisterPackets);

        NeoForge.EVENT_BUS.addListener(ExtremeReactors::onRegisterCommands);
    }

    public void sendPacket(CustomPacketPayload packet, ServerLevel level, BlockPos center, int radius) {
        this._network.sendToAllAround(center, radius, level, null, packet);
    }

    //region internals

    private static void onInterModProcess(InterModProcessEvent event) {

        // API messages

        // - Reactor reactants/mapping/reactions
        imcProcessAPIMessages(event, "reactant-register");
        imcProcessAPIMessages(event, "mapping-register");
        imcProcessAPIMessages(event, "reaction-register");
        imcProcessAPIMessages(event, "reaction-remove");
        imcProcessAPIMessages(event, "mapping-remove");
        imcProcessAPIMessages(event, "reactant-remove");

        // - Reactor Moderators
        imcProcessAPIMessages(event, "moderator-s-register");
        imcProcessAPIMessages(event, "moderator-f-register");
        imcProcessAPIMessages(event, "moderator-s-remove");
        imcProcessAPIMessages(event, "moderator-f-remove");

        // - Coolants / Vapors
        imcProcessAPIMessages(event, "fluid-register");
        imcProcessAPIMessages(event, "fluid-mapping-register");
        imcProcessAPIMessages(event, "fluid-transition-register");
        imcProcessAPIMessages(event, "fluid-transition-remove");
        imcProcessAPIMessages(event, "fluid-mapping-remove");
        imcProcessAPIMessages(event, "fluid-remove");

        // - Turbine CoilMaterials
        imcProcessAPIMessages(event, "coilmaterial-register");
        imcProcessAPIMessages(event, "coilmaterial-remove");

        // ModPack API Wrapper
        ApiWrapper.processFile();
    }

    private static void imcProcessAPIMessages(InterModProcessEvent event, String method) {
        event.getIMCStream((method::equals)).map(imc -> (Runnable) imc.messageSupplier().get()).forEach(Runnable::run);
    }

    private static void onRegisterCommands(RegisterCommandsEvent event) {
        ExtremeReactorsCommand.register(event.getDispatcher(), event.getBuildContext());
    }

    private static void onRegisterPackets(RegisterPayloadHandlersEvent event) {

        final PayloadRegistrar registrar = event.registrar(MOD_ID).versioned("2.0.0");

        registrar.playToClient(UpdateClientsFuelRodsLayout.TYPE, UpdateClientsFuelRodsLayout.STREAM_CODEC,
                UpdateClientsFuelRodsLayout::handlePacket);
        registrar.playToClient(UpdateFluidizerFluidStatus.TYPE, UpdateFluidizerFluidStatus.STREAM_CODEC,
                UpdateFluidizerFluidStatus::handlePacket);
    }

    private static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {

        // Reactor

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Content.TileEntityTypes.REACTOR_SOLID_ACCESSPORT.get(),
                (be, context) -> be.getItemHandler());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Content.TileEntityTypes.REACTOR_FLUID_ACCESSPORT.get(),
                (be, context) -> be.getFluidHandler());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Content.TileEntityTypes.REACTOR_FLUIDPORT_FORGE_ACTIVE.get(),
                ExtremeReactors::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Content.TileEntityTypes.REACTOR_FLUIDPORT_FORGE_PASSIVE.get(),
                ExtremeReactors::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, Content.TileEntityTypes.REACTOR_POWERTAP_FE_ACTIVE.get(),
                ExtremeReactors::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, Content.TileEntityTypes.REACTOR_POWERTAP_FE_PASSIVE.get(),
                ExtremeReactors::getEnergyStorageCapability);
        IComputerCraftService.SERVICE.get().registerCapabilityProvider(event, Content.TileEntityTypes.REACTOR_COMPUTERPORT.get());
        IMekanismService.SERVICE.get().registerGasCapabilityProvider(event, Content.TileEntityTypes.REACTOR_FLUIDPORT_MEKANISM_PASSIVE.get());

        // Turbine

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Content.TileEntityTypes.TURBINE_FLUIDPORT_FORGE_ACTIVE.get(),
                ExtremeReactors::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Content.TileEntityTypes.TURBINE_FLUIDPORT_FORGE_PASSIVE.get(),
                ExtremeReactors::getFluidHandlerCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, Content.TileEntityTypes.TURBINE_POWERTAP_FE_ACTIVE.get(),
                ExtremeReactors::getEnergyStorageCapability);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, Content.TileEntityTypes.TURBINE_POWERTAP_FE_PASSIVE.get(),
                ExtremeReactors::getEnergyStorageCapability);
        IComputerCraftService.SERVICE.get().registerCapabilityProvider(event, Content.TileEntityTypes.TURBINE_COMPUTERPORT.get());

        // Reprocessor

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Content.TileEntityTypes.REPROCESSOR_FLUIDINJECTOR.get(),
                (be, context) -> be.getHandler());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Content.TileEntityTypes.REPROCESSOR_WASTEINJECTOR.get(),
                (be, context) -> be.getHandler());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Content.TileEntityTypes.REPROCESSOR_OUTPUTPORT.get(),
                (be, context) -> be.getHandler());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, Content.TileEntityTypes.REPROCESSOR_POWERPORT.get(),
                (be, context) -> be.getHandler());

        // Fluidizer

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, Content.TileEntityTypes.FLUIDIZER_SOLIDINJECTOR.get(),
                (be, context) -> be.getItemHandler());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Content.TileEntityTypes.FLUIDIZER_FLUIDINJECTOR.get(),
                (be, context) -> be.getFluidHandler());
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, Content.TileEntityTypes.FLUIDIZER_OUTPUTPORT.get(),
                (be, context) -> be.getHandler());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, Content.TileEntityTypes.FLUIDIZER_POWERPORT.get(),
                ExtremeReactors::getEnergyStorageCapability);

        // Energizer

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, Content.TileEntityTypes.ENERGIZER_POWERPORT_FE.get(),
                ExtremeReactors::getEnergyStorageCapability);
    }

    @Nullable
    private static IFluidHandler getFluidHandlerCapability(IFluidPort port, Direction context) {

        if (port.getFluidPortHandler() instanceof IFluidHandler handler) {
            return handler;
        }

        return null;
    }

    @Nullable
    private static IEnergyStorage getEnergyStorageCapability(IPowerPort port, Direction context) {

        if (port.getPowerPortHandler() instanceof IEnergyStorage handler) {
            return handler;
        }

        return null;
    }

    private static ExtremeReactors s_instance;
    private static SidedDependencyServiceLoader<IProxy> s_proxy;
    private final NetworkHandler _network;

    //endregion
}
