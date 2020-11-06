/*
 *
 * Content.java
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

package it.zerono.mods.extremereactors.gamecontent;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.config.Config;
import it.zerono.mods.extremereactors.gamecontent.fluid.SteamFluid;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.GenericDeviceBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.GlassBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.IOPortBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.coolantport.CoolantPortType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactorPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.container.ReactorSolidAccessPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.*;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
import it.zerono.mods.zerocore.lib.block.ModBlock;
import it.zerono.mods.zerocore.lib.block.ModOreBlock;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.item.ModItem;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.world.WorldGenManager;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public final class Content {

    public static void initialize() {

        Blocks.initialize();
        Items.initialize();
        Fluids.initialize();
        TileEntityTypes.initialize();
        ContainerTypes.initialize();

        Blocks.initializeWorldGen();

        final IEventBus bus = Mod.EventBusSubscriber.Bus.MOD.bus().get();

        bus.addListener(Content::onCommonInit);
    }

    public static final class Blocks {

        private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ExtremeReactors.MOD_ID);

        static void initialize() {
            BLOCKS.register(Mod.EventBusSubscriber.Bus.MOD.bus().get());
        }

        //region metals

        public static final RegistryObject<ModBlock> YELLORIUM_BLOCK = registerMetalBlock("yellorium_block", DyeColor.YELLOW);
        public static final RegistryObject<ModBlock> CYANITE_BLOCK = registerMetalBlock("cyanite_block", DyeColor.LIGHT_BLUE);
        public static final RegistryObject<ModBlock> GRAPHITE_BLOCK = registerMetalBlock("graphite_block", DyeColor.GRAY);

        //endregion
        //region ores

        public static final RegistryObject<ModBlock> YELLORITE_ORE_BLOCK = registerOreBlock("yellorite_ore", DyeColor.YELLOW);
        public static final RegistryObject<ModBlock> ANGLESITE_ORE_BLOCK = registerOreBlock("anglesite_ore", DyeColor.ORANGE);
        public static final RegistryObject<ModBlock> BENITOITE_ORE_BLOCK = registerOreBlock("benitoite_ore", DyeColor.LIGHT_BLUE);

        //endregion
        //region fluids

        public static final RegistryObject<FlowingFluidBlock> STEAM = BLOCKS.register("steam",
                () -> new FlowingFluidBlock(Fluids.STEAM_SOURCE,
                        Block.Properties.create(Material.WATER)
                                .doesNotBlockMovement()
                                .hardnessAndResistance(100.0F)
                                .noDrops()
                ));

        //endregion
        //region reactor
        //region basic

        public static final RegistryObject<MultiblockPartBlock<MultiblockReactor, ReactorPartType>> REACTOR_CASING_BASIC =
                registerReactorBlock("basic_reactorcasing", ReactorVariant.Basic, ReactorPartType.Casing);

        public static final RegistryObject<GlassBlock<MultiblockReactor, ReactorPartType>> REACTOR_GLASS_BASIC =
                registerReactorBlock("basic_reactorglass", ReactorVariant.Basic, ReactorPartType.Glass);

        public static final RegistryObject<GenericDeviceBlock<MultiblockReactor, ReactorPartType>> REACTOR_CONTROLLER_BASIC =
                registerReactorBlock("basic_reactorcontroller", ReactorVariant.Basic, ReactorPartType.Controller);

        public static final RegistryObject<ReactorFuelRodBlock> REACTOR_FUELROD_BASIC =
                registerReactorBlock("basic_reactorfuelrod", ReactorVariant.Basic, ReactorPartType.FuelRod);

        public static final RegistryObject<GenericDeviceBlock<MultiblockReactor, ReactorPartType>> REACTOR_CONTROLROD_BASIC =
                registerReactorBlock("basic_reactorcontrolrod", ReactorVariant.Basic, ReactorPartType.ControlRod);

        public static final RegistryObject<IOPortBlock<MultiblockReactor, ReactorPartType>> REACTOR_SOLID_ACCESSPORT_BASIC =
                registerReactorBlock("basic_reactorsolidaccessport", ReactorVariant.Basic, ReactorPartType.SolidAccessPort);

        public static final RegistryObject<GenericDeviceBlock<MultiblockReactor, ReactorPartType>> REACTOR_POWERTAP_FE_ACTIVE_BASIC =
                registerReactorBlock("basic_reactorpowertapfe_active", ReactorVariant.Basic, ReactorPartType.ActivePowerTapFE);

        public static final RegistryObject<GenericDeviceBlock<MultiblockReactor, ReactorPartType>> REACTOR_POWERTAP_FE_PASSIVE_BASIC =
                registerReactorBlock("basic_reactorpowertapfe_passive", ReactorVariant.Basic, ReactorPartType.PassivePowerTapFE);

        public static final RegistryObject<ReactorRedstonePortBlock> REACTOR_REDSTONEPORT_BASIC =
                registerReactorBlock("basic_reactorredstoneport", ReactorVariant.Basic, ReactorPartType.RedstonePort);

        //endregion
        //region reinforced

        public static final RegistryObject<MultiblockPartBlock<MultiblockReactor, ReactorPartType>> REACTOR_CASING_REINFORCED =
                registerReactorBlock("reinforced_reactorcasing", ReactorVariant.Reinforced, ReactorPartType.Casing);

        public static final RegistryObject<GlassBlock<MultiblockReactor, ReactorPartType>> REACTOR_GLASS_REINFORCED =
                registerReactorBlock("reinforced_reactorglass", ReactorVariant.Reinforced, ReactorPartType.Glass);

        public static final RegistryObject<GenericDeviceBlock<MultiblockReactor, ReactorPartType>> REACTOR_CONTROLLER_REINFORCED =
                registerReactorBlock("reinforced_reactorcontroller", ReactorVariant.Reinforced, ReactorPartType.Controller);

        public static final RegistryObject<ReactorFuelRodBlock> REACTOR_FUELROD_REINFORCED =
                registerReactorBlock("reinforced_reactorfuelrod", ReactorVariant.Reinforced, ReactorPartType.FuelRod);

        public static final RegistryObject<GenericDeviceBlock<MultiblockReactor, ReactorPartType>> REACTOR_CONTROLROD_REINFORCED =
                registerReactorBlock("reinforced_reactorcontrolrod", ReactorVariant.Reinforced, ReactorPartType.ControlRod);

        public static final RegistryObject<IOPortBlock<MultiblockReactor, ReactorPartType>> REACTOR_SOLID_ACCESSPORT_REINFORCED =
                registerReactorBlock("reinforced_reactorsolidaccessport", ReactorVariant.Reinforced, ReactorPartType.SolidAccessPort);

        public static final RegistryObject<GenericDeviceBlock<MultiblockReactor, ReactorPartType>> REACTOR_POWERTAP_FE_ACTIVE_REINFORCED =
                registerReactorBlock("reinforced_reactorpowertapfe_active", ReactorVariant.Reinforced, ReactorPartType.ActivePowerTapFE);

        public static final RegistryObject<GenericDeviceBlock<MultiblockReactor, ReactorPartType>> REACTOR_POWERTAP_FE_PASSIVE_REINFORCED =
                registerReactorBlock("reinforced_reactorpowertapfe_passive", ReactorVariant.Reinforced, ReactorPartType.PassivePowerTapFE);

        public static final RegistryObject<IOPortBlock<MultiblockReactor, ReactorPartType>> REACTOR_COOLANTPORT_FORGE_ACTIVE_REINFORCED =
                registerReactorBlock("reinforced_reactorcoolantport_forge_active", ReactorVariant.Reinforced, ReactorPartType.ActiveCoolantPortForge);

        public static final RegistryObject<IOPortBlock<MultiblockReactor, ReactorPartType>> REACTOR_COOLANTPORT_FORGE_PASSIVE_REINFORCED =
                registerReactorBlock("reinforced_reactorcoolantport_forge_passive", ReactorVariant.Reinforced, ReactorPartType.PassiveCoolantPortForge);

        public static final RegistryObject<ReactorRedstonePortBlock> REACTOR_REDSTONEPORT_REINFORCED =
                registerReactorBlock("reinforced_reactorredstoneport", ReactorVariant.Reinforced, ReactorPartType.RedstonePort);

        public static final RegistryObject<ReactorRedstonePortBlock> REACTOR_COMPUTERPORT_REINFORCED =
                registerReactorBlock("reinforced_reactorcomputerport", ReactorVariant.Reinforced, ReactorPartType.ComputerPort);

        //endregion
        //endregion
        //region internals

        private static RegistryObject<ModBlock> registerMetalBlock(final String name, final DyeColor color) {
            return BLOCKS.register(name,
                    () -> new ModBlock(Block.Properties.create(Material.IRON, color).sound(SoundType.METAL)));
        }

        private static RegistryObject<ModBlock> registerOreBlock(final String name, final DyeColor color) {
            return BLOCKS.register(name,
                    () -> new ModOreBlock(Block.Properties.create(Material.ROCK, color)
                            .sound(SoundType.STONE)
                            .setRequiresTool()
                            .hardnessAndResistance(3.0F, 3.0F), 3, 5));
        }

        @SuppressWarnings("unchecked")
        private static <T extends MultiblockPartBlock<MultiblockReactor, ReactorPartType>>
            RegistryObject<T> registerReactorBlock(final String name,
                                                   final ReactorVariant variant,
                                                   final ReactorPartType partType) {
            return BLOCKS.register(name, () -> (T) (partType.createBlock(variant)));
        }

        static void initializeWorldGen() {

            if (Config.COMMON.worldgen.enableWorldGen.get()) {

                if (Config.COMMON.worldgen.yelloriteOreEnableWorldGen.get()) {
                    // yellorite ore
                    WorldGenManager.INSTANCE.addOre(WorldGenManager.exceptNether().and(WorldGenManager.exceptTheEnd()),
                            WorldGenManager.oreFeature(Blocks.YELLORITE_ORE_BLOCK,
                                    WorldGenManager.oreMatch(Tags.Blocks.STONE),
                                    Config.COMMON.worldgen.yelloriteOreMaxClustersPerChunk.get(),
                                    Config.COMMON.worldgen.yelloriteOrePerCluster.get(),
                                    15, 5, Config.COMMON.worldgen.yelloriteOreMaxY.get()));
                }

                if (Config.COMMON.worldgen.anglesiteOreEnableWorldGen.get()) {
                    // anglesite ore
                    WorldGenManager.INSTANCE.addOre(WorldGenManager.onlyTheEnd(),
                            WorldGenManager.oreFeature(Blocks.ANGLESITE_ORE_BLOCK,
                                    WorldGenManager.oreMatch(Tags.Blocks.END_STONES),
                                    Config.COMMON.worldgen.anglesiteOreMaxClustersPerChunk.get(),
                                    Config.COMMON.worldgen.anglesiteOrePerCluster.get(),
                                    5, 5, 200));
                }

                if (Config.COMMON.worldgen.benitoiteOreEnableWorldGen.get()) {
                    // benitoite ore
                    WorldGenManager.INSTANCE.addOre(WorldGenManager.onlyNether(),
                            WorldGenManager.oreFeature(Blocks.BENITOITE_ORE_BLOCK,
                                    WorldGenManager.oreMatch(Tags.Blocks.NETHERRACK),
                                    Config.COMMON.worldgen.benitoiteOreMaxClustersPerChunk.get(),
                                    Config.COMMON.worldgen.benitoiteOrePerCluster.get(),
                                    5, 5, 256));
                }
            }
        }

        //endregion
    }

    @SuppressWarnings("unused")
    public static final class Items {

        private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExtremeReactors.MOD_ID);

        static void initialize() {
            ITEMS.register(Mod.EventBusSubscriber.Bus.MOD.bus().get());
        }

        //region metals

        public static final RegistryObject<ModItem> YELLORIUM_INGOT = registerItemGeneric("yellorium_ingot");
        public static final RegistryObject<ModItem> CYANITE_INGOT = registerItemGeneric("cyanite_ingot");
        public static final RegistryObject<ModItem> GRAPHITE_INGOT = registerItemGeneric("graphite_ingot");

        public static final RegistryObject<ModItem> YELLORIUM_DUST = registerItemGeneric("yellorium_dust");
        public static final RegistryObject<ModItem> CYANITE_DUST = registerItemGeneric("cyanite_dust");
        public static final RegistryObject<ModItem> GRAPHITE_DUST = registerItemGeneric("graphite_dust");

        public static final RegistryObject<BlockItem> YELLORIUM_BLOCK = registerItemBlock("yellorium_block", () -> Blocks.YELLORIUM_BLOCK, ItemGroups.GENERAL);
        public static final RegistryObject<BlockItem> CYANITE_BLOCK = registerItemBlock("cyanite_block", () -> Blocks.CYANITE_BLOCK, ItemGroups.GENERAL);
        public static final RegistryObject<BlockItem> GRAPHITE_BLOCK = registerItemBlock("graphite_block", () -> Blocks.GRAPHITE_BLOCK, ItemGroups.GENERAL);

        //endregion
        //region crystals

        public static final RegistryObject<ModItem> ANGLESITE_CRYSTAL = registerItemGeneric("anglesite_crystal");
        public static final RegistryObject<ModItem> BENITOITE_CRYSTAL = registerItemGeneric("benitoite_crystal");

        //endregion
        //region ores

        public static final RegistryObject<BlockItem> YELLORITE_ORE_BLOCK = registerItemBlock("yellorite_ore", () -> Blocks.YELLORITE_ORE_BLOCK, ItemGroups.GENERAL);
        public static final RegistryObject<BlockItem> ANGLESITE_ORE_BLOCK = registerItemBlock("anglesite_ore", () -> Blocks.ANGLESITE_ORE_BLOCK, ItemGroups.GENERAL);
        public static final RegistryObject<BlockItem> BENITOITE_ORE_BLOCK = registerItemBlock("benitoite_ore", () -> Blocks.BENITOITE_ORE_BLOCK, ItemGroups.GENERAL);

        //endregion
        //region fluids

        public static final RegistryObject<Item> STEAM_BUCKET = ITEMS.register("steam_bucket",
                () -> new BucketItem(Fluids.STEAM_SOURCE, new Item.Properties()
                        .containerItem(net.minecraft.item.Items.BUCKET)
                        .maxStackSize(1)
                        .group(ItemGroup.MISC)));

        //endregion
        //region reactor
        //region basic
        public static final RegistryObject<BlockItem> REACTOR_CASING_BASIC = registerItemBlock("basic_reactorcasing", () -> Blocks.REACTOR_CASING_BASIC::get, ItemGroups.REACTOR);
        public static final RegistryObject<BlockItem> REACTOR_GLASS_BASIC = registerItemBlock("basic_reactorglass", () -> Blocks.REACTOR_GLASS_BASIC::get, ItemGroups.REACTOR);
        public static final RegistryObject<BlockItem> REACTOR_CONTROLLER_BASIC = registerItemBlock("basic_reactorcontroller", () -> Blocks.REACTOR_CONTROLLER_BASIC::get, ItemGroups.REACTOR);
        public static final RegistryObject<BlockItem> REACTOR_FUELROD_BASIC = registerItemBlock("basic_reactorfuelrod", () -> Blocks.REACTOR_FUELROD_BASIC::get, ItemGroups.REACTOR);
        public static final RegistryObject<BlockItem> REACTOR_CONTROLROD_BASIC = registerItemBlock("basic_reactorcontrolrod", () -> Blocks.REACTOR_CONTROLROD_BASIC::get, ItemGroups.REACTOR);
        public static final RegistryObject<BlockItem> REACTOR_SOLID_ACCESSPORT_BASIC = registerItemBlock("basic_reactorsolidaccessport", () -> Blocks.REACTOR_SOLID_ACCESSPORT_BASIC::get, ItemGroups.REACTOR);
        public static final RegistryObject<BlockItem> REACTOR_POWERTAP_FE_ACTIVE_BASIC = registerItemBlock("basic_reactorpowertapfe_active", () -> Blocks.REACTOR_POWERTAP_FE_ACTIVE_BASIC::get, ItemGroups.REACTOR);
        public static final RegistryObject<BlockItem> REACTOR_POWERTAP_FE_PASSIVE_BASIC = registerItemBlock("basic_reactorpowertapfe_passive", () -> Blocks.REACTOR_POWERTAP_FE_PASSIVE_BASIC::get, ItemGroups.REACTOR);
        public static final RegistryObject<BlockItem> REACTOR_REDSTONEPORT_BASIC = registerItemBlock("basic_reactorredstoneport", () -> Blocks.REACTOR_REDSTONEPORT_BASIC::get, ItemGroups.REACTOR);
        //endregion
        //region reinforced
        public static final RegistryObject<BlockItem> REACTOR_CASING_REINFORCED = registerItemBlock("reinforced_reactorcasing", () -> Blocks.REACTOR_CASING_REINFORCED::get, ItemGroups.REACTOR);
        public static final RegistryObject<BlockItem> REACTOR_GLASS_REINFORCED = registerItemBlock("reinforced_reactorglass", () -> Blocks.REACTOR_GLASS_REINFORCED::get, ItemGroups.REACTOR);
        public static final RegistryObject<BlockItem> REACTOR_CONTROLLER_REINFORCED = registerItemBlock("reinforced_reactorcontroller", () -> Blocks.REACTOR_CONTROLLER_REINFORCED::get, ItemGroups.REACTOR);
        public static final RegistryObject<BlockItem> REACTOR_FUELROD_REINFORCED = registerItemBlock("reinforced_reactorfuelrod", () -> Blocks.REACTOR_FUELROD_REINFORCED::get, ItemGroups.REACTOR);
        public static final RegistryObject<BlockItem> REACTOR_CONTROLROD_REINFORCED = registerItemBlock("reinforced_reactorcontrolrod", () -> Blocks.REACTOR_CONTROLROD_REINFORCED::get, ItemGroups.REACTOR);
        public static final RegistryObject<BlockItem> REACTOR_SOLID_ACCESSPORT_REINFORCED = registerItemBlock("reinforced_reactorsolidaccessport", () -> Blocks.REACTOR_SOLID_ACCESSPORT_REINFORCED::get, ItemGroups.REACTOR);
        public static final RegistryObject<BlockItem> REACTOR_POWERTAP_FE_ACTIVE_REINFORCED = registerItemBlock("reinforced_reactorpowertapfe_active", () -> Blocks.REACTOR_POWERTAP_FE_ACTIVE_REINFORCED::get, ItemGroups.REACTOR);
        public static final RegistryObject<BlockItem> REACTOR_POWERTAP_FE_PASSIVE_REINFORCED = registerItemBlock("reinforced_reactorpowertapfe_passive", () -> Blocks.REACTOR_POWERTAP_FE_PASSIVE_REINFORCED::get, ItemGroups.REACTOR);
        public static final RegistryObject<BlockItem> REACTOR_COMPUTERPORT_REINFORCED = registerItemBlock("reinforced_reactorcomputerport", () -> Blocks.REACTOR_COMPUTERPORT_REINFORCED::get, ItemGroups.REACTOR);
        public static final RegistryObject<BlockItem> REACTOR_REDSTONEPORT_REINFORCED = registerItemBlock("reinforced_reactorredstoneport", () -> Blocks.REACTOR_REDSTONEPORT_REINFORCED::get, ItemGroups.REACTOR);
        public static final RegistryObject<BlockItem> REACTOR_COOLANTPORT_FORGE_ACTIVE_REINFORCED = registerItemBlock("reinforced_reactorcoolantport_forge_active", () -> Blocks.REACTOR_COOLANTPORT_FORGE_ACTIVE_REINFORCED::get, ItemGroups.REACTOR);
        public static final RegistryObject<BlockItem> REACTOR_COOLANTPORT_FORGE_PASSIVE_REINFORCED = registerItemBlock("reinforced_reactorcoolantport_forge_passive", () -> Blocks.REACTOR_COOLANTPORT_FORGE_PASSIVE_REINFORCED::get, ItemGroups.REACTOR);

        //endregion
        //endregion
        //region misc

        public static final RegistryObject<ModItem> WRENCH = registerItemGeneric("wrench", 1);

        //endregion

        //region internals

        private static RegistryObject<ModItem> registerItemGeneric(final String name) {
            return registerItemGeneric(name, 64);
        }

        private static RegistryObject<ModItem> registerItemGeneric(final String name, final int maxStack) {
            return ITEMS.register(name,
                    () -> new ModItem(new Item.Properties().group(ItemGroups.GENERAL).maxStackSize(maxStack)));
        }

        private static RegistryObject<BlockItem> registerItemBlock(final String name,
                                                                   final Supplier<Supplier<ModBlock>> blockSupplier,
                                                                   final ItemGroup group) {
            return ITEMS.register(name,
                    () -> blockSupplier.get().get().createBlockItem(new Item.Properties().group(group).maxStackSize(64)));
        }

        //endregion
    }

    public static final class Fluids {

        private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, ExtremeReactors.MOD_ID);

        static void initialize() {
            FLUIDS.register(Mod.EventBusSubscriber.Bus.MOD.bus().get());
        }

        public static final RegistryObject<ForgeFlowingFluid> STEAM_SOURCE = FLUIDS.register("steam", SteamFluid.Source::new);
        public static final RegistryObject<ForgeFlowingFluid> STEAM_FLOWING = FLUIDS.register("steam_flowing", SteamFluid.Flowing::new);
    }

    public static final class TileEntityTypes {

        private static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES =
                DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ExtremeReactors.MOD_ID);

        static void initialize() {
            TILE_ENTITIES.register(Mod.EventBusSubscriber.Bus.MOD.bus().get());
        }

        //region Reactor

        public static final RegistryObject<TileEntityType<ReactorCasingEntity>> REACTOR_CASING =
                registerBlockEntity("reactorcasing", ReactorCasingEntity::new,
                        () -> Blocks.REACTOR_CASING_BASIC::get,
                        () -> Blocks.REACTOR_CASING_REINFORCED::get);

        public static final RegistryObject<TileEntityType<ReactorGlassEntity>> REACTOR_GLASS =
                registerBlockEntity("reactorglass", ReactorGlassEntity::new,
                        () -> Blocks.REACTOR_GLASS_BASIC::get,
                        () -> Blocks.REACTOR_GLASS_REINFORCED::get);

        public static final RegistryObject<TileEntityType<ReactorControllerEntity>> REACTOR_CONTROLLER =
                registerBlockEntity("reactorcontroller", ReactorControllerEntity::new,
                        () -> Blocks.REACTOR_CONTROLLER_BASIC::get,
                        () -> Blocks.REACTOR_CONTROLLER_REINFORCED::get);

        public static final RegistryObject<TileEntityType<ReactorFuelRodEntity>> REACTOR_FUELROD =
                registerBlockEntity("reactorfuelrod", ReactorFuelRodEntity::new,
                        () -> Blocks.REACTOR_FUELROD_BASIC::get,
                        () -> Blocks.REACTOR_FUELROD_REINFORCED::get);

        public static final RegistryObject<TileEntityType<ReactorControlRodEntity>> REACTOR_CONTROLROD =
                registerBlockEntity("reactorcontrolrod", ReactorControlRodEntity::new,
                        () -> Blocks.REACTOR_CONTROLROD_BASIC::get,
                        () -> Blocks.REACTOR_CONTROLROD_REINFORCED::get);

        public static final RegistryObject<TileEntityType<ReactorSolidAccessPortEntity>> REACTOR_SOLID_ACCESSPORT =
                registerBlockEntity("reactoraccessport", ReactorSolidAccessPortEntity::new,
                        () -> Blocks.REACTOR_SOLID_ACCESSPORT_BASIC::get,
                        () -> Blocks.REACTOR_SOLID_ACCESSPORT_REINFORCED::get);

        public static final RegistryObject<TileEntityType<ReactorCoolantPortEntity>> REACTOR_COOLANTPORT_FORGE_ACTIVE =
                registerBlockEntity("reactorcoolantport_forge_active",
                        () -> new ReactorCoolantPortEntity(CoolantPortType.Forge, IoMode.Active, TileEntityTypes.REACTOR_COOLANTPORT_FORGE_ACTIVE.get()),
                        () -> Blocks.REACTOR_COOLANTPORT_FORGE_ACTIVE_REINFORCED::get);

        public static final RegistryObject<TileEntityType<ReactorCoolantPortEntity>> REACTOR_COOLANTPORT_FORGE_PASSIVE =
                registerBlockEntity("reactorcoolantport_forge_passive",
                        () -> new ReactorCoolantPortEntity(CoolantPortType.Forge, IoMode.Passive, TileEntityTypes.REACTOR_COOLANTPORT_FORGE_PASSIVE.get()),
                        () -> Blocks.REACTOR_COOLANTPORT_FORGE_PASSIVE_REINFORCED::get);

        //TODO coolant port mekanism TE

        //TODO imp 4 other variants
//        public static final RegistryObject<TileEntityType<ReactorCreativeCoolantPortEntity>> REACTOR_CREATIVECOOLANTPORT =
//                registerBlockEntity("reactorcreativecoolantport", ReactorCreativeCoolantPortEntity::new, () -> Blocks.REACTOR_XXXXXX_BASIC::get);

        public static final RegistryObject<TileEntityType<ReactorPowerTapEntity>> REACTOR_POWERTAP_FE_ACTIVE =
                registerBlockEntity("reactorpowertap_fe_active",
                        () -> new ReactorPowerTapEntity(EnergySystem.ForgeEnergy, IoMode.Active, TileEntityTypes.REACTOR_POWERTAP_FE_ACTIVE.get()),
                        () -> Blocks.REACTOR_POWERTAP_FE_ACTIVE_BASIC::get,
                        () -> Blocks.REACTOR_POWERTAP_FE_ACTIVE_REINFORCED::get);

        public static final RegistryObject<TileEntityType<ReactorPowerTapEntity>> REACTOR_POWERTAP_FE_PASSIVE =
                registerBlockEntity("reactorpowertap_fe_passive",
                        () -> new ReactorPowerTapEntity(EnergySystem.ForgeEnergy, IoMode.Passive, TileEntityTypes.REACTOR_POWERTAP_FE_PASSIVE.get()),
                        () -> Blocks.REACTOR_POWERTAP_FE_PASSIVE_BASIC::get,
                        () -> Blocks.REACTOR_POWERTAP_FE_PASSIVE_REINFORCED::get);

        public static final RegistryObject<TileEntityType<ReactorComputerPortEntity>> REACTOR_COMPUTERPORT =
                registerBlockEntity("reactorcomputerport", ReactorComputerPortEntity::new,
                        () -> Blocks.REACTOR_COMPUTERPORT_REINFORCED::get);

        public static final RegistryObject<TileEntityType<ReactorRedstonePortEntity>> REACTOR_REDSTONEPORT =
                registerBlockEntity("reactorredstoneport", ReactorRedstonePortEntity::new,
                        () -> Blocks.REACTOR_REDSTONEPORT_BASIC::get,
                        () -> Blocks.REACTOR_REDSTONEPORT_REINFORCED::get);

        //endregion
        //region internals

        @SuppressWarnings("ConstantConditions")
        @SafeVarargs
        private static <T extends TileEntity> RegistryObject<TileEntityType<T>> registerBlockEntity(final String name,
                                                                                                    final Supplier<T> factory,
                                                                                                    final Supplier<Supplier<Block>>... validBlockSuppliers) {
            return TILE_ENTITIES.register(name, () -> {

                final Block[] validBlocks = new Block[validBlockSuppliers.length];

                if (validBlockSuppliers.length > 0) {
                    for (int i = 0; i < validBlockSuppliers.length; ++i) {
                        validBlocks[i] = validBlockSuppliers[i].get().get();
                    }
                }

                return TileEntityType.Builder.create(factory, validBlocks).build(null);
            });
        }

        //endregion
    }

    public static final class ContainerTypes {

        private static final DeferredRegister<ContainerType<?>> CONTAINERS =
                DeferredRegister.create(ForgeRegistries.CONTAINERS, ExtremeReactors.MOD_ID);

        static void initialize() {
            CONTAINERS.register(Mod.EventBusSubscriber.Bus.MOD.bus().get());
        }

        //region Reactor

        public static final RegistryObject<ContainerType<ModTileContainer<ReactorControllerEntity>>> REACTOR_CONTROLLER =
                registerContainer("reactorcontroller", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.REACTOR_CONTROLLER.get(), windowId, data));

        public static final RegistryObject<ContainerType<ReactorSolidAccessPortContainer>> REACTOR_SOLID_ACCESSPORT =
                registerContainer("reactoraccessport", ReactorSolidAccessPortContainer::new);

        public static final RegistryObject<ContainerType<ModTileContainer<ReactorRedstonePortEntity>>> REACTOR_REDSTONEPORT =
                registerContainer("reactorredstoneport", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.REACTOR_REDSTONEPORT.get(), windowId, data));

        public static final RegistryObject<ContainerType<ModTileContainer<ReactorControlRodEntity>>> REACTOR_CONTROLROD =
                registerContainer("reactorcontrolrod", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.REACTOR_CONTROLROD.get(), windowId, data));

        //endregion
        //region internals

        private static <C extends Container> RegistryObject<ContainerType<C>> registerContainer(final String name,
                                                                                                final IContainerFactory<C> factory) {
            return CONTAINERS.register(name,
                    () -> IForgeContainerType.create(factory));
        }

        //endregion
    }

    //region internals

    private static void onCommonInit(final FMLCommonSetupEvent event) {
        ReactorGameData.register();
    }

    //endregion
}
