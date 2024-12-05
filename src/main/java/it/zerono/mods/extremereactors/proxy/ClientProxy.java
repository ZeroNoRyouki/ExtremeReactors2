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

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.zerono.mods.extremereactors.api.reactor.ModeratorsRegistry;
import it.zerono.mods.extremereactors.api.reactor.ReactantMappingsRegistry;
import it.zerono.mods.extremereactors.api.turbine.CoilMaterialRegistry;
import it.zerono.mods.extremereactors.config.Config;
import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.extremereactors.gamecontent.fluid.ReactorFluidRenderProperties;
import it.zerono.mods.extremereactors.gamecontent.fluid.ReactorFluidType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CachedSprites;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.GuiTheme;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.client.model.EnergizerModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.client.screen.EnergizerChargingPortScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.client.screen.EnergizerControllerScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.client.screen.EnergizerPowerPortScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.FluidizerTankData;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.client.model.FluidizerClientTankData;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.client.model.FluidizerGlassModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.client.model.FluidizerIOModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.client.model.FluidizerModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.client.render.FluidizerControllerEntityRenderer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.client.screen.FluidizerControllerScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.client.screen.FluidizerSolidInjectorScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part.FluidizerControllerEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.FuelRodsLayout;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.ClientFuelRodsLayout;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.model.ReactorFuelRodBlockColor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.model.ReactorFuelRodModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.model.ReactorGlassModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.model.ReactorModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.screen.*;
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
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.screen.TurbineChargingPortScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.screen.TurbineControllerScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.screen.TurbineFluidPortScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.screen.TurbineRedstonePortScreen;
import it.zerono.mods.zerocore.lib.client.model.ICustomModelBuilder;
import it.zerono.mods.zerocore.lib.client.model.ModBakedModelSupplier;
import it.zerono.mods.zerocore.lib.fluid.SimpleFluidTypeRenderProperties;
import it.zerono.mods.zerocore.lib.item.TintedBucketItem;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ClientProxy
        implements IForgeProxy, ResourceManagerReloadListener {

    public static Supplier<BakedModel> getModelSupplier(final ModelResourceLocation modelId) {
        return s_bakedModelSupplier.getOrCreate(modelId);
    }

    //region IForgeProxy

    @Override
    public void initialize(IEventBus modEventBus) {

        s_bakedModelSupplier = new ModBakedModelSupplier(modEventBus);

        this._modelBuilders = Suppliers.memoize(ClientProxy::initModels);

        modEventBus.addListener(ClientProxy::onClientInit);
        modEventBus.addListener(this::onRegisterModels);
        modEventBus.addListener(this::onModelBake);
        modEventBus.addListener(ClientProxy::onRegisterBlockColorHandlers);
        modEventBus.addListener(ClientProxy::onRegisterItemColorHandlers);
        modEventBus.addListener(ClientProxy::onRegisterMenuScreensEvent);
        modEventBus.addListener(ClientProxy::onRegisterClientExtensionsEvent);

        NeoForge.EVENT_BUS.addListener(this::onAddReloadListener);
        NeoForge.EVENT_BUS.addListener(this::onItemTooltip);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onVanillaTagsUpdated);
    }

    //region IProxy

    @Override
    public FuelRodsLayout createFuelRodsLayout(Direction direction, int length) {
        return new ClientFuelRodsLayout(direction, length);
    }

    @Override
    public FluidizerTankData createFluidizerTankData(FluidizerControllerEntity controllerEntity) {
        return new FluidizerClientTankData(controllerEntity);
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
                Stream.of(new ReactorModelBuilder.Basic(),
                        new ReactorGlassModelBuilder.Basic(),
                        ReactorFuelRodModelBuilder.basic(),
                        new ReactorModelBuilder.Reinforced(),
                        new ReactorGlassModelBuilder.Reinforced(),
                        ReactorFuelRodModelBuilder.reinforced()
                ),
                Stream.of(new TurbineModelBuilder.Basic(),
                        new TurbineGlassModelBuilder.Basic(),
                        new TurbineRotorModelBuilder.Basic(),
                        new TurbineModelBuilder.Reinforced(),
                        new TurbineGlassModelBuilder.Reinforced(),
                        new TurbineRotorModelBuilder.Reinforced()
                ),
                Stream.of(new ReprocessorModelBuilder(),
                        new ReprocessorIOModelBuilder(),
                        new ReprocessorGlassModelBuilder(),
                        new FluidizerModelBuilder(),
                        new FluidizerIOModelBuilder(),
                        new FluidizerGlassModelBuilder(),
                        new EnergizerModelBuilder()
                )
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
        BlockEntityRenderers.register(Content.TileEntityTypes.FLUIDIZER_CONTROLLER.get(), FluidizerControllerEntityRenderer::new);
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
        event.register(Content.ContainerTypes.REACTOR_CHARGINGPORT.get(), ReactorChargingPortScreen::new);
        event.register(Content.ContainerTypes.REACTOR_FLUIDPORT.get(), ReactorFluidPortScreen::new);

        // Turbine GUIs
        event.register(Content.ContainerTypes.TURBINE_CONTROLLER.get(), TurbineControllerScreen::new);
        event.register(Content.ContainerTypes.TURBINE_CHARGINGPORT.get(), TurbineChargingPortScreen::new);
        event.register(Content.ContainerTypes.TURBINE_FLUIDPORT.get(), TurbineFluidPortScreen::new);
        event.register(Content.ContainerTypes.TURBINE_REDSTONEPORT.get(), TurbineRedstonePortScreen::new);

        // Reprocessor GUIs
        event.register(Content.ContainerTypes.REPROCESSOR_CONTROLLER.get(), ReprocessorControllerScreen::new);
        event.register(Content.ContainerTypes.REPROCESSOR_ACCESSPORT.get(), ReprocessorAccessPortScreen::new);

        // Fluidizer GUIS
        event.register(Content.ContainerTypes.FLUIDIZER_SOLID_INJECTOR.get(), FluidizerSolidInjectorScreen::new);
        event.register(Content.ContainerTypes.FLUIDIZER_CONTROLLER.get(), FluidizerControllerScreen::new);

        // Energizer GUIS
        event.register(Content.ContainerTypes.ENERGIZER_CONTROLLER.get(), EnergizerControllerScreen::new);
        event.register(Content.ContainerTypes.ENERGIZER_POWERPORT.get(), EnergizerPowerPortScreen::new);
        event.register(Content.ContainerTypes.ENERGIZER_CHARGINGPORT.get(), EnergizerChargingPortScreen::new);
    }

    private static void onRegisterClientExtensionsEvent(RegisterClientExtensionsEvent event) {

        Content.Fluids.forEachType(type -> {

            if (type instanceof ReactorFluidType reactorFluidType) {
                event.registerFluidType(new ReactorFluidRenderProperties(reactorFluidType), reactorFluidType);
            }
        });

        event.registerFluidType(new SimpleFluidTypeRenderProperties(0xffffffff,
                        CommonConstants.FLUID_TEXTURE_SOURCE_WATER, CommonConstants.FLUID_TEXTURE_FLOWING_WATER,
                        CommonConstants.FLUID_TEXTURE_OVERLAY_WATER), Content.Fluids.STEAM_FLUID_TYPE.get());
    }

    private void onAddReloadListener(AddReloadListenerEvent event) {

        event.addListener(this);
        event.addListener(GuiTheme.ER);
    }

    private void onRegisterModels(final ModelEvent.RegisterAdditional event) {
        this._modelBuilders.get().forEach(b -> b.onRegisterModels(event));
    }

    private void onModelBake(final ModelEvent.ModifyBakingResult event) {
        this._modelBuilders.get().forEach(builder -> builder.onBakeModels(event));
    }

    private void onItemTooltip(final ItemTooltipEvent event) {

        if (!Config.CLIENT.disableApiTooltips.get() && event.getFlags().isAdvanced()) {
            event.getToolTip().addAll(this.getApiTooltipCache().getOrDefault(event.getItemStack().getItem(), Collections.emptySet()));
        }
    }

    private static void onRegisterBlockColorHandlers(final RegisterColorHandlersEvent.Block event) {
        event.register(new ReactorFuelRodBlockColor(),
                Content.Blocks.REACTOR_FUELROD_BASIC.get(),
                Content.Blocks.REACTOR_FUELROD_REINFORCED.get());
    }

    private static void onRegisterItemColorHandlers(RegisterColorHandlersEvent.Item event) {
        event.register(TintedBucketItem::getTintColour,
                Content.Items.YELLORIUM_BUCKET.get(), Content.Items.CYANITE_BUCKET.get(),
                Content.Items.BLUTONIUM_BUCKET.get(), Content.Items.MAGENTITE_BUCKET.get(),
                Content.Items.VERDERIUM_BUCKET.get(), Content.Items.ROSSINITE_BUCKET.get(),
                Content.Items.STEAM_BUCKET.get(),
                Content.Items.CRYOMISI_BUCKET.get(), Content.Items.TANGERIUM_BUCKET.get(),
                Content.Items.REDFRIGIUM_BUCKET.get());
    }

    private static ModBakedModelSupplier s_bakedModelSupplier;

    private Supplier<List<ICustomModelBuilder>> _modelBuilders;

    private Map<Item, Set<Component>> _apiTooltipCache;

    //endregion
}
