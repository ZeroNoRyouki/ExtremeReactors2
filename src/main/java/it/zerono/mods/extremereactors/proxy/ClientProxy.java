/*
 *
 * ClientProxy.java
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

package it.zerono.mods.extremereactors.proxy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.zerono.mods.extremereactors.CommonLocations;
import it.zerono.mods.extremereactors.api.reactor.ModeratorsRegistry;
import it.zerono.mods.extremereactors.api.reactor.ReactantMappingsRegistry;
import it.zerono.mods.extremereactors.api.turbine.CoilMaterialRegistry;
import it.zerono.mods.extremereactors.config.Config;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CachedSprites;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.ChargingPortScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.FluidPortScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.GuiTheme;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.container.ChargingPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.client.model.FluidizerGlassModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.client.model.FluidizerModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.client.screen.FluidizerControllerScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.client.screen.FluidizerSolidInjectorScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.FuelRodsLayout;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.ClientFuelRodsLayout;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.model.ReactorFuelRodBlockColor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.model.ReactorFuelRodModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.model.ReactorGlassModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.model.ReactorModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.screen.*;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorChargingPortEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorFluidPortEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.client.model.ReprocessorGlassModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.client.model.ReprocessorIOModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.client.model.ReprocessorModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.client.render.ReprocessorCollectorRender;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.client.screen.ReprocessorAccessPortScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.client.screen.ReprocessorControllerScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.model.TurbineGlassModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.model.TurbineModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.model.TurbineRotorModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.render.rotor.RotorBearingEntityRenderer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.screen.TurbineControllerScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.screen.TurbineRedstonePortScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineChargingPortEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineFluidPortEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.zerocore.lib.client.model.ICustomModelBuilder;
import it.zerono.mods.zerocore.lib.client.model.ModBakedModelSupplier;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ClientProxy
        implements IForgeProxy, ResourceManagerReloadListener {

    public static Supplier<BakedModel> getModelSupplier(final ResourceLocation modelId) {
        return s_bakedModelSupplier.getOrCreate(modelId);
    }

    //region IForgeProxy

    @Override
    public void initialize(IEventBus modEventBus) {

        s_bakedModelSupplier = new ModBakedModelSupplier(modEventBus);

        this._modelBuilders = initModels();

        modEventBus.addListener(ClientProxy::onClientInit);
        modEventBus.addListener(this::onRegisterModels);
        modEventBus.addListener(this::onModelBake);
        modEventBus.addListener(ClientProxy::onColorHandlerEvent);
        modEventBus.addListener(ClientProxy::onRegisterMenuScreensEvent);

        NeoForge.EVENT_BUS.addListener(this::onAddReloadListener);
        NeoForge.EVENT_BUS.addListener(this::onItemTooltip);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onVanillaTagsUpdated);
    }

    @Override
    public FuelRodsLayout createFuelRodsLayout(Direction direction, int length) {
        return new ClientFuelRodsLayout(direction, length);
    }

    //endregion
    //region ResourceManagerReloadListener

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        this.invalidateApiTooltipCache();
    }

    //endregion
    //region internals

    private static List<ICustomModelBuilder> initModels() {
        return Streams.concat(
                Arrays.stream(ReactorVariant.values())
                        .flatMap(v -> Stream.of(
                                new ReactorModelBuilder(v),
                                new ReactorGlassModelBuilder(v),
                                new ReactorFuelRodModelBuilder(v)
                        )),
                Arrays.stream(TurbineVariant.values())
                        .flatMap(v -> Stream.of(
                                new TurbineModelBuilder(v),
                                new TurbineGlassModelBuilder(v),
                                new TurbineRotorModelBuilder(v)
                        )),
                Stream.of(new ReprocessorModelBuilder(),
                        new ReprocessorIOModelBuilder(),
                        new ReprocessorGlassModelBuilder(),
                        new FluidizerModelBuilder(),
                        new FluidizerGlassModelBuilder())
        ).collect(ImmutableList.toImmutableList());
    }

    private static void registerRenderTypes() {

        registerRenderType(RenderType.translucent(),
                Content.Blocks.REACTOR_GLASS_BASIC, Content.Blocks.REACTOR_GLASS_REINFORCED,
                Content.Blocks.TURBINE_GLASS_BASIC, Content.Blocks.TURBINE_GLASS_REINFORCED,
                Content.Blocks.REPROCESSOR_GLASS, Content.Blocks.FLUIDIZER_GLASS);

        registerRenderType(RenderType.cutout(),
                Content.Blocks.TURBINE_ROTORBLADE_BASIC, Content.Blocks.TURBINE_ROTORBLADE_REINFORCED,
                Content.Blocks.TURBINE_ROTORSHAFT_BASIC, Content.Blocks.TURBINE_ROTORSHAFT_REINFORCED);
    }

    private static void registerTileRenderers() {

        BlockEntityRenderers.register(Content.TileEntityTypes.TURBINE_ROTORBEARING.get(), RotorBearingEntityRenderer::new);
        BlockEntityRenderers.register(Content.TileEntityTypes.REPROCESSOR_COLLECTOR.get(), ReprocessorCollectorRender::new);
    }

    //region registration helpers

    @SafeVarargs
    private static void registerRenderType(RenderType type, Supplier<? extends Block>... blocks) {

        // TODO check json models
        for (final Supplier<? extends Block> block : blocks) {
            ItemBlockRenderTypes.setRenderLayer(block.get(), type);
        }
    }

    //endregion
    //region api tooltip cache

    private void onVanillaTagsUpdated(final TagsUpdatedEvent event) {
        this.invalidateApiTooltipCache();
    }

    private Map<Item, Set<Component>> getApiTooltipCache() {

        if (null == this._apiTooltipCache) {
            this._apiTooltipCache = buildApiTooltipCache();
        }

        return this._apiTooltipCache;
    }

    private void invalidateApiTooltipCache() {
        this._apiTooltipCache = null;
    }

    private static Map<Item, Set<Component>> buildApiTooltipCache() {

        final Map<Item, Set<Component>> wipCache = Maps.newHashMap();

        // fill items from the API

        ReactantMappingsRegistry.fillReactantsTooltips(wipCache, Sets::newHashSet);
        ModeratorsRegistry.fillModeratorsTooltips(wipCache, Sets::newHashSet);
        CoilMaterialRegistry.fillCoilsTooltips(wipCache, Sets::newHashSet);

        return new Object2ObjectArrayMap<>(wipCache);
    }

    //endregion

    private static void onClientInit(FMLClientSetupEvent event) {

        CachedSprites.initialize();

        event.enqueueWork(() -> {

            registerRenderTypes();
            registerTileRenderers();

            // Patchouli multiblock rendering do not support ModelData-based models
            PatchouliCompat.initialize();
        });
    }

    private static void onRegisterMenuScreensEvent(RegisterMenuScreensEvent event) {

        // Reactor GUIs
        event.register(Content.ContainerTypes.REACTOR_CONTROLLER.get(), ReactorControllerScreen::new);
        event.register(Content.ContainerTypes.REACTOR_SOLID_ACCESSPORT.get(), ReactorSolidAccessPortScreen::new);
        event.register(Content.ContainerTypes.REACTOR_FLUID_ACCESSPORT.get(), ReactorFluidAccessPortScreen::new);
        event.register(Content.ContainerTypes.REACTOR_REDSTONEPORT.get(), ReactorRedstonePortScreen::new);
        event.register(Content.ContainerTypes.REACTOR_CONTROLROD.get(), ReactorControlRodScreen::new);
        event.register(Content.ContainerTypes.REACTOR_CHARGINGPORT.get(),
                (ChargingPortContainer<ReactorChargingPortEntity> container, Inventory inventory, Component title) ->
                        new ChargingPortScreen<>(container, inventory, title, CommonLocations.REACTOR.buildWithSuffix("part-forgechargingport")));
        event.register(Content.ContainerTypes.REACTOR_FLUIDPORT.get(),
                (ModTileContainer<ReactorFluidPortEntity> container, Inventory inventory, Component title) ->
                        new FluidPortScreen<>(container, inventory, title, CommonLocations.REACTOR.buildWithSuffix("part-forgefluidport")));
        // Turbine GUIs
        event.register(Content.ContainerTypes.TURBINE_CONTROLLER.get(), TurbineControllerScreen::new);
        event.register(Content.ContainerTypes.TURBINE_CHARGINGPORT.get(),
                (ChargingPortContainer<TurbineChargingPortEntity> container, Inventory inventory, Component title) ->
                        new ChargingPortScreen<>(container, inventory, title, CommonLocations.TURBINE.buildWithSuffix("part-forgechargingport")));
        event.register(Content.ContainerTypes.TURBINE_FLUIDPORT.get(),
                (ModTileContainer<TurbineFluidPortEntity> container, Inventory inventory, Component title) ->
                        new FluidPortScreen<>(container, inventory, title, CommonLocations.TURBINE.buildWithSuffix("part-forgefluidport")));
        event.register(Content.ContainerTypes.TURBINE_REDSTONEPORT.get(), TurbineRedstonePortScreen::new);

        // Reprocessor GUIs
        event.register(Content.ContainerTypes.REPROCESSOR_CONTROLLER.get(), ReprocessorControllerScreen::new);
        event.register(Content.ContainerTypes.REPROCESSOR_ACCESSPORT.get(), ReprocessorAccessPortScreen::new);

        // Fluidizer GUIS
        event.register(Content.ContainerTypes.FLUIDIZER_SOLID_INJECTOR.get(), FluidizerSolidInjectorScreen::new);
        event.register(Content.ContainerTypes.FLUIDIZER_CONTROLLER.get(), FluidizerControllerScreen::new);
    }

    private void onAddReloadListener(AddReloadListenerEvent event) {

        event.addListener(this);
        event.addListener(GuiTheme.ER);
    }

    private void onRegisterModels(final ModelEvent.RegisterAdditional event) {
        this._modelBuilders.forEach(b -> b.onRegisterModels(event));
    }

    private void onModelBake(final ModelEvent.ModifyBakingResult event) {
        this._modelBuilders.forEach(builder -> builder.onBakeModels(event));
    }

    private void onItemTooltip(final ItemTooltipEvent event) {

        if (!Config.CLIENT.disableApiTooltips.get() && event.getFlags().isAdvanced()) {
            event.getToolTip().addAll(this.getApiTooltipCache().getOrDefault(event.getItemStack().getItem(), Collections.emptySet()));
        }
    }

    private static void onColorHandlerEvent(final RegisterColorHandlersEvent.Block event) {
        event.register(new ReactorFuelRodBlockColor(),
                Content.Blocks.REACTOR_FUELROD_BASIC.get(),
                Content.Blocks.REACTOR_FUELROD_REINFORCED.get());
    }

    private static ModBakedModelSupplier s_bakedModelSupplier;

    private List<ICustomModelBuilder> _modelBuilders;

    private Map<Item, Set<Component>> _apiTooltipCache;

    //endregion
}
