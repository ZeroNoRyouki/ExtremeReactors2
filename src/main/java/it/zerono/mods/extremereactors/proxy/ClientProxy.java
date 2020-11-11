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
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import it.zerono.mods.extremereactors.config.Turbine;
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
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.zerocore.lib.client.model.ICustomModelBuilder;
import net.minecraft.block.Block;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.Direction;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientProxy implements IProxy {

    public ClientProxy() {

        this._modelBuilders = initModels();
        Mod.EventBusSubscriber.Bus.MOD.bus().get().register(this);
    }

    /**
     * Called on the physical client to perform client-specific initialization tasks
     *
     * @param event
     */
    @SubscribeEvent
    public void onClientInit(final FMLClientSetupEvent event) {

        CachedSprites.initialize();

        registerRenderTypes();
        registerTileRenderers();
        registerScreens();
    }

    @SubscribeEvent
    public void onRegisterModels(final ModelRegistryEvent event) {
        this._modelBuilders.forEach(ICustomModelBuilder::onRegisterModels);
    }

    @SubscribeEvent
    public void onModelBake(final ModelBakeEvent event) {
        this._modelBuilders.forEach(builder -> builder.onBakeModels(event));
    }

    //region IProxy

    @Override
    public FuelRodsLayout createFuelRodsLayout(Direction direction, int length) {
        return new ClientFuelRodsLayout(direction, length);
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
                                new TurbineGlassModelBuilder(v)
//                                new ReactorFuelRodModelBuilder(v) //TODO rotor?
                        ))
        ).collect(ImmutableList.toImmutableList());

//        //noinspection UnstableApiUsage
//        return Arrays.stream(ReactorVariant.values())
//                .flatMap(v -> Stream.of(
//                        new ReactorModelBuilder(v),
//                        new ReactorGlassModelBuilder(v),
//                        new ReactorFuelRodModelBuilder(v)
//                ))
//                .collect(ImmutableList.toImmutableList());
    }

    private static void registerScreens() {

        // Reactor GUIs
        registerScreen(Content.ContainerTypes.REACTOR_CONTROLLER, ReactorControllerScreen::new);
        registerScreen(Content.ContainerTypes.REACTOR_SOLID_ACCESSPORT, ReactorSolidAccessPortScreen::new);
        registerScreen(Content.ContainerTypes.REACTOR_REDSTONEPORT, ReactorRedstonePortScreen::new);
        registerScreen(Content.ContainerTypes.REACTOR_CONTROLROD, ReactorControlRodScreen::new);

        // Turbine GUIs
//        registerScreen(Content.ContainerTypes.TURBINE_CONTROLLER, ReactorControllerScreen::new);
//        registerScreen(Content.ContainerTypes.REACTOR_REDSTONEPORT, ReactorRedstonePortScreen::new);
    }

    private static void registerRenderTypes() {

        registerRenderType(RenderType.getTranslucent(),
                Content.Blocks.REACTOR_GLASS_BASIC, Content.Blocks.REACTOR_GLASS_REINFORCED);

        registerRenderType(RenderType.getCutout(),
                Content.Blocks.REACTOR_FUELROD_BASIC, Content.Blocks.REACTOR_FUELROD_REINFORCED,
                Content.Blocks.TURBINE_ROTORBLADE_BASIC, Content.Blocks.TURBINE_ROTORBLADE_REINFORCED,
                Content.Blocks.TURBINE_ROTORSHAFT_BASIC, Content.Blocks.TURBINE_ROTORSHAFT_REINFORCED);
    }

    private static void registerTileRenderers() {

        ClientRegistry.bindTileEntityRenderer(Content.TileEntityTypes.REACTOR_FUELROD.get(), FuelRodEntityRenderer::new);
        //TODO turbine rotor
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

    private final List<ICustomModelBuilder> _modelBuilders;

    //endregion
}
