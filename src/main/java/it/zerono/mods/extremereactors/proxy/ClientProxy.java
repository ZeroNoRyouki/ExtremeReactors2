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
import it.zerono.mods.extremereactors.api.reactor.ModeratorsRegistry;
import it.zerono.mods.extremereactors.api.reactor.ReactantMappingsRegistry;
import it.zerono.mods.extremereactors.api.turbine.CoilMaterialRegistry;
import it.zerono.mods.extremereactors.config.Config;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CachedSprites;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.FuelRodsLayout;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.ClientFuelRodsLayout;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.model.ReactorFuelRodModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.model.ReactorGlassModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.model.ReactorModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.render.FuelRodEntityRenderer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.screen.ReactorControlRodScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.screen.ReactorControllerScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.screen.ReactorRedstonePortScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.screen.ReactorSolidAccessPortScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.model.TurbineGlassModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.model.TurbineModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.model.TurbineRotorModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.render.rotor.RotorBearingEntityRenderer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.screen.TurbineControllerScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.model.ICustomModelBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ClientProxy
        implements IProxy, ISelectiveResourceReloadListener {

    public ClientProxy() {

        this._modelBuilders = initModels();

        IEventBus bus;

        bus = Mod.EventBusSubscriber.Bus.MOD.bus().get();
        bus.register(this);

        bus = Mod.EventBusSubscriber.Bus.FORGE.bus().get();
        bus.addListener(this::onItemTooltip);
        bus.addListener(EventPriority.LOWEST, this::onVanillaTagsUpdated);

        CodeHelper.addResourceReloadListener(this);
    }

    /**
     * Called on the physical client to perform client-specific initialization tasks
     *
     * @param event the event
     */
    @SubscribeEvent
    public void onClientInit(final FMLClientSetupEvent event) {

        CachedSprites.initialize();

        event.enqueueWork(() -> {

            registerRenderTypes();
            registerTileRenderers();
            registerScreens();
        });
    }

    @SubscribeEvent
    public void onRegisterModels(final ModelRegistryEvent event) {
        this._modelBuilders.forEach(ICustomModelBuilder::onRegisterModels);
    }

    @SubscribeEvent
    public void onModelBake(final ModelBakeEvent event) {
        this._modelBuilders.forEach(builder -> builder.onBakeModels(event));
    }

    public void onItemTooltip(final ItemTooltipEvent event) {

        if (!Config.CLIENT.disableApiTooltips.get() && event.getFlags().isAdvanced()) {
            event.getToolTip().addAll(this.getApiTooltipCache().getOrDefault(event.getItemStack().getItem(), Collections.emptySet()));
        }
    }

    //region IProxy

    @Override
    public FuelRodsLayout createFuelRodsLayout(Direction direction, int length) {
        return new ClientFuelRodsLayout(direction, length);
    }

    //endregion
    //region ISelectiveResourceReloadListener

    /**
     * A version of onResourceManager that selectively chooses {@link IResourceType}s
     * to reload.
     * When using this, the given predicate should be called to ensure the relevant resources should
     * be reloaded at this time.
     *
     * @param resourceManager   the resource manager being reloaded
     * @param resourcePredicate predicate to test whether any given resource type should be reloaded
     */
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        this.invalidateApiTooltipCache();
    }

    //endregion
    //region internals

    private static List<ICustomModelBuilder> initModels() {

        //noinspection UnstableApiUsage
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
                        ))
        ).collect(ImmutableList.toImmutableList());
    }

    private static void registerScreens() {

        // Reactor GUIs
        registerScreen(Content.ContainerTypes.REACTOR_CONTROLLER, ReactorControllerScreen::new);
        registerScreen(Content.ContainerTypes.REACTOR_SOLID_ACCESSPORT, ReactorSolidAccessPortScreen::new);
        registerScreen(Content.ContainerTypes.REACTOR_REDSTONEPORT, ReactorRedstonePortScreen::new);
        registerScreen(Content.ContainerTypes.REACTOR_CONTROLROD, ReactorControlRodScreen::new);

        // Turbine GUIs
        registerScreen(Content.ContainerTypes.TURBINE_CONTROLLER, TurbineControllerScreen::new);
//        registerScreen(Content.ContainerTypes.REACTOR_REDSTONEPORT, ReactorRedstonePortScreen::new);
    }

    private static void registerRenderTypes() {

        registerRenderType(RenderType.getTranslucent(),
                Content.Blocks.REACTOR_GLASS_BASIC, Content.Blocks.REACTOR_GLASS_REINFORCED,
                Content.Blocks.TURBINE_GLASS_BASIC, Content.Blocks.TURBINE_GLASS_REINFORCED);

        registerRenderType(RenderType.getCutout(),
                Content.Blocks.REACTOR_FUELROD_BASIC, Content.Blocks.REACTOR_FUELROD_REINFORCED,
                Content.Blocks.TURBINE_ROTORBLADE_BASIC, Content.Blocks.TURBINE_ROTORBLADE_REINFORCED,
                Content.Blocks.TURBINE_ROTORSHAFT_BASIC, Content.Blocks.TURBINE_ROTORSHAFT_REINFORCED);
    }

    private static void registerTileRenderers() {

        ClientRegistry.bindTileEntityRenderer(Content.TileEntityTypes.REACTOR_FUELROD.get(), FuelRodEntityRenderer::new);
        ClientRegistry.bindTileEntityRenderer(Content.TileEntityTypes.TURBINE_ROTORBEARING.get(), RotorBearingEntityRenderer::new);
    }

    //region registration helpers

    private static <M extends Container, U extends Screen & IHasContainer<M>>
        void registerScreen(final Supplier<? extends ContainerType<? extends M>> type,
                        final ScreenManager.IScreenFactory<M, U> factory) {
        ScreenManager.registerFactory(type.get(), factory);
    }

    @SafeVarargs
    private static void registerRenderType(RenderType type, Supplier<? extends Block>... blocks) {

        for (final Supplier<? extends Block> block : blocks) {
            RenderTypeLookup.setRenderLayer(block.get(), type);
        }
    }

    //endregion
    //region api tooltip cache

    private void onVanillaTagsUpdated(final TagsUpdatedEvent.VanillaTagTypes event) {
        this.invalidateApiTooltipCache();
    }

    private Map<Item, Set<ITextComponent>> getApiTooltipCache() {

        if (null == this._apiTooltipCache) {
            this._apiTooltipCache = buildApiTooltipCache();
        }

        return this._apiTooltipCache;
    }

    private void invalidateApiTooltipCache() {
        this._apiTooltipCache = null;
    }

    private static Map<Item, Set<ITextComponent>> buildApiTooltipCache() {

        final Map<Item, Set<ITextComponent>> wipCache = Maps.newHashMap();

        // fill items from the API

        ReactantMappingsRegistry.fillReactantsTooltips(wipCache, Sets::newHashSet);
        ModeratorsRegistry.fillModeratorsTooltips(wipCache, Sets::newHashSet);
        CoilMaterialRegistry.fillModeratorsTooltips(wipCache, Sets::newHashSet);

        return new Object2ObjectArrayMap<>(wipCache);
    }

    //endregion

    private final List<ICustomModelBuilder> _modelBuilders;

    private Map<Item, Set<ITextComponent>> _apiTooltipCache;

    //endregion
}
