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
import it.zerono.mods.extremereactors.config.conditions.ConfigCondition;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.command.ExtremeReactorsCommand;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.network.UpdateClientsFuelRodsLayout;
import it.zerono.mods.extremereactors.proxy.IProxy;
import it.zerono.mods.extremereactors.proxy.ProxySafeReferent;
import it.zerono.mods.zerocore.lib.init.IModInitializationHandler;
import it.zerono.mods.zerocore.lib.network.IModMessage;
import it.zerono.mods.zerocore.lib.network.NetworkHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

@Mod(value = ExtremeReactors.MOD_ID)
public class ExtremeReactors implements IModInitializationHandler {

    public static final String MOD_ID = "bigreactors";
    public static final String MOD_NAME = "Extreme Reactors 2";

    public static ExtremeReactors getInstance() {
        return s_instance;
    }

    public static IProxy getProxy() {
        return s_proxy;
    }

    public static ResourceLocation newID(final String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public ExtremeReactors() {

        s_instance = this;
        this._network = new NetworkHandler(newID("network"), "1");

        Config.initialize();
        Content.initialize();

        s_proxy = DistExecutor.safeRunForDist(() -> ProxySafeReferent::client, () -> ProxySafeReferent::server);

        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        modBus.addListener(this::onCommonInit);
        modBus.addListener(this::onInterModProcess);
        modBus.addListener(this::onRegisterRecipeSerializer);

        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);

//        WorldGen.initialize();
    }

    /**
     * Called on both the physical client and the physical server to perform common initialization tasks
     * @param event the event
     */
    @Override
    public void onCommonInit(FMLCommonSetupEvent event) {
        this._network.registerMessage(UpdateClientsFuelRodsLayout.class, UpdateClientsFuelRodsLayout::new);
    }

    /**
     * Retrieve and process inter-mods messages and process them
     * <p>
     * See {@link InterModComms}
     *
     * @param event the event
     */
    @Override
    public void onInterModProcess(InterModProcessEvent event) {

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

    public void onRegisterRecipeSerializer(final RegisterEvent event) {
        event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS,
                helper -> CraftingHelper.register(ConfigCondition.Serializer.INSTANCE));
    }

    public <T extends IModMessage> void sendPacket(final T packet, final Level world, final BlockPos center, final int radius) {
        this._network.sendToAllAround(packet, center.getX(), center.getY(), center.getZ(), radius, world.dimension());
    }

    //region internals

    private void imcProcessAPIMessages(InterModProcessEvent event, String method) {
        event.getIMCStream((method::equals)).map(imc -> (Runnable) imc.messageSupplier().get()).forEach(Runnable::run);
    }

    private void onRegisterCommands(final RegisterCommandsEvent event) {
        ExtremeReactorsCommand.register(event.getDispatcher());
    }

    private static ExtremeReactors s_instance;
    private static IProxy s_proxy;
    private final NetworkHandler _network;

    //endregion
}
