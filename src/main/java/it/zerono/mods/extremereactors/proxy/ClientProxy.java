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
import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.extremereactors.api.reactor.ModeratorsRegistry;
import it.zerono.mods.extremereactors.api.reactor.ReactantMappingsRegistry;
import it.zerono.mods.extremereactors.api.turbine.CoilMaterialRegistry;
import it.zerono.mods.extremereactors.config.Config;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CachedSprites;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.GuiTheme;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.client.model.EnergizerModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.client.screen.EnergizerChargingPortScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.client.screen.EnergizerControllerScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.client.screen.EnergizerPowerPortScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.FluidizerTankData;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.client.model.FluidizerClientTankData;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.client.model.FluidizerGlassModelBuilder;
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
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.screen.TurbineChargingPortScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.screen.TurbineControllerScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.screen.TurbineFluidPortScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.screen.TurbineRedstonePortScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.zerocore.lib.client.model.ICustomModelBuilder;
import it.zerono.mods.zerocore.lib.client.model.ModBakedModelSupplier;
import it.zerono.mods.zerocore.lib.item.TintedBucketItem;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ClientProxy
        implements IForgeProxy, ResourceManagerReloadListener {

    public ClientProxy() {
        this._modelBuilders = initModels();
    }

    public static Supplier<BakedModel> getModelSupplier(final ResourceLocation modelId) {
        return s_bakedModelSupplier.getOrCreate(modelId);
    }

    //region IForgeProxy

    @Override
    public void initialize(IEventBus modEventBus) {

        modEventBus.addListener(ClientProxy::onClientInit);
        modEventBus.addListener(this::onRegisterModels);
        modEventBus.addListener(this::onModelBake);
        modEventBus.addListener(ClientProxy::onRegisterBlockColorHandlers);
        modEventBus.addListener(ClientProxy::onRegisterItemColorHandlers);

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
                        new FluidizerGlassModelBuilder(),
                        new EnergizerModelBuilder())
        ).collect(ImmutableList.toImmutableList());
    }

    private static void registerScreens() {

        // Reactor GUIs
        registerScreen(Content.ContainerTypes.REACTOR_CONTROLLER, ReactorControllerScreen::new);
        registerScreen(Content.ContainerTypes.REACTOR_SOLID_ACCESSPORT, ReactorSolidAccessPortScreen::new);
        registerScreen(Content.ContainerTypes.REACTOR_FLUID_ACCESSPORT, ReactorFluidAccessPortScreen::new);
        registerScreen(Content.ContainerTypes.REACTOR_REDSTONEPORT, ReactorRedstonePortScreen::new);
        registerScreen(Content.ContainerTypes.REACTOR_CONTROLROD, ReactorControlRodScreen::new);
        registerScreen(Content.ContainerTypes.REACTOR_CHARGINGPORT, ReactorChargingPortScreen::new);
        registerScreen(Content.ContainerTypes.REACTOR_FLUIDPORT, ReactorFluidPortScreen::new);

        // Turbine GUIs
        registerScreen(Content.ContainerTypes.TURBINE_CONTROLLER, TurbineControllerScreen::new);
        registerScreen(Content.ContainerTypes.TURBINE_CHARGINGPORT, TurbineChargingPortScreen::new);
        registerScreen(Content.ContainerTypes.TURBINE_FLUIDPORT, TurbineFluidPortScreen::new);
        registerScreen(Content.ContainerTypes.TURBINE_REDSTONEPORT, TurbineRedstonePortScreen::new);

        // Reprocessor GUIs
        registerScreen(Content.ContainerTypes.REPROCESSOR_CONTROLLER, ReprocessorControllerScreen::new);
        registerScreen(Content.ContainerTypes.REPROCESSOR_ACCESSPORT, ReprocessorAccessPortScreen::new);

        // Fluidizer GUIS
        registerScreen(Content.ContainerTypes.FLUIDIZER_SOLID_INJECTOR, FluidizerSolidInjectorScreen::new);
        registerScreen(Content.ContainerTypes.FLUIDIZER_CONTROLLER, FluidizerControllerScreen::new);

        // Energizer GUIS
        registerScreen(Content.ContainerTypes.ENERGIZER_CONTROLLER, EnergizerControllerScreen::new);
        registerScreen(Content.ContainerTypes.ENERGIZER_POWERPORT, EnergizerPowerPortScreen::new);
        registerScreen(Content.ContainerTypes.ENERGIZER_CHARGINGPORT, EnergizerChargingPortScreen::new);
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

    private static <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>>
        void registerScreen(final Supplier<? extends MenuType<? extends M>> type,
                        final MenuScreens.ScreenConstructor<M, U> factory) {
        MenuScreens.register(type.get(), factory);
    }

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

        //delete
        Log.LOGGER.error("ER CLIENT PROXY onClientInit");

        CachedSprites.initialize();

        event.enqueueWork(() -> {

            registerRenderTypes();
            registerTileRenderers();
            registerScreens();

            // Patchouli multiblock rendering do not support ModelData-based models
            PatchouliCompat.initialize();
        });
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

    private static final ModBakedModelSupplier s_bakedModelSupplier = new ModBakedModelSupplier();

    private final List<ICustomModelBuilder> _modelBuilders;

    private Map<Item, Set<Component>> _apiTooltipCache;

    //endregion
}
