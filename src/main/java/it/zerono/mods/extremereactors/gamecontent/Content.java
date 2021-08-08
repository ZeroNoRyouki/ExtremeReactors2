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
import it.zerono.mods.extremereactors.gamecontent.fluid.SteamFluid;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.container.ChargingPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.GenericDeviceBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.GlassBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.IOPortBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.fluidport.FluidPortType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactorPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.container.ReactorSolidAccessPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.*;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.MultiblockTurbine;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.TurbinePartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.*;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.zerocore.lib.block.ModBlock;
import it.zerono.mods.zerocore.lib.block.ModOreBlock;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.item.ModItem;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.item.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

public final class Content {

    public static void initialize() {

        Blocks.initialize();
        Items.initialize();
        Fluids.initialize();
        TileEntityTypes.initialize();
        ContainerTypes.initialize();

        Mod.EventBusSubscriber.Bus.MOD.bus().get().addListener(Content::onCommonInit);
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

        public static final RegistryObject<ModBlock> YELLORITE_ORE_BLOCK = registerOreBlock("yellorite_ore", DyeColor.YELLOW, 0, 0);
        public static final RegistryObject<ModBlock> ANGLESITE_ORE_BLOCK = registerOreBlock("anglesite_ore", DyeColor.ORANGE);
        public static final RegistryObject<ModBlock> BENITOITE_ORE_BLOCK = registerOreBlock("benitoite_ore", DyeColor.LIGHT_BLUE);

        //endregion
        //region fluids

        public static final RegistryObject<LiquidBlock> STEAM = BLOCKS.register("steam",
                () -> new LiquidBlock(Fluids.STEAM_SOURCE,
                        Block.Properties.of(Material.WATER)
                                .noCollission()
                                .strength(100.0F)
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

        public static final RegistryObject<GenericDeviceBlock<MultiblockReactor, ReactorPartType>> REACTOR_CHARGINGPORT_FE_BASIC =
                registerReactorBlock("basic_reactorchargingportfe", ReactorVariant.Basic, ReactorPartType.ChargingPortFE);

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

        public static final RegistryObject<IOPortBlock<MultiblockReactor, ReactorPartType>> REACTOR_FLUIDTPORT_FORGE_ACTIVE_REINFORCED =
                registerReactorBlock("reinforced_reactorfluidport_forge_active", ReactorVariant.Reinforced, ReactorPartType.ActiveFluidPortForge);

        public static final RegistryObject<IOPortBlock<MultiblockReactor, ReactorPartType>> REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED =
                registerReactorBlock("reinforced_reactorfluidport_forge_passive", ReactorVariant.Reinforced, ReactorPartType.PassiveFluidPortForge);

        public static final RegistryObject<IOPortBlock<MultiblockReactor, ReactorPartType>> REACTOR_FLUIDPORT_MEKANISM_PASSIVE_REINFORCED =
                registerReactorBlock("reinforced_reactorfluidport_mekanism_passive", ReactorVariant.Reinforced, ReactorPartType.PassiveFluidPortMekanism);

        public static final RegistryObject<GenericDeviceBlock<MultiblockReactor, ReactorPartType>> REACTOR_CREATIVE_WATER_GENERATOR_REINFORCED =
                registerReactorBlock("reinforced_reactorcreativewatergenerator", ReactorVariant.Reinforced, ReactorPartType.CreativeWaterGenerator);

        public static final RegistryObject<ReactorRedstonePortBlock> REACTOR_REDSTONEPORT_REINFORCED =
                registerReactorBlock("reinforced_reactorredstoneport", ReactorVariant.Reinforced, ReactorPartType.RedstonePort);

        public static final RegistryObject<GenericDeviceBlock<MultiblockReactor, ReactorPartType>> REACTOR_COMPUTERPORT_REINFORCED =
                registerReactorBlock("reinforced_reactorcomputerport", ReactorVariant.Reinforced, ReactorPartType.ComputerPort);

        public static final RegistryObject<GenericDeviceBlock<MultiblockReactor, ReactorPartType>> REACTOR_CHARGINGPORT_FE_REINFORCED =
                registerReactorBlock("reinforced_reactorchargingportfe", ReactorVariant.Reinforced, ReactorPartType.ChargingPortFE);

        //endregion
        //endregion

        //region turbine
        //region basic

        public static final RegistryObject<MultiblockPartBlock<MultiblockTurbine, TurbinePartType>> TURBINE_CASING_BASIC =
                registerTurbineBlock("basic_turbinecasing", TurbineVariant.Basic, TurbinePartType.Casing);

        public static final RegistryObject<GlassBlock<MultiblockTurbine, TurbinePartType>> TURBINE_GLASS_BASIC =
                registerTurbineBlock("basic_turbineglass", TurbineVariant.Basic, TurbinePartType.Glass);

        public static final RegistryObject<GenericDeviceBlock<MultiblockTurbine, TurbinePartType>> TURBINE_CONTROLLER_BASIC =
                registerTurbineBlock("basic_turbinecontroller", TurbineVariant.Basic, TurbinePartType.Controller);

        public static final RegistryObject<TurbineRotorBearingBlock> TURBINE_ROTORBEARING_BASIC =
                registerTurbineBlock("basic_turbinerotorbearing", TurbineVariant.Basic, TurbinePartType.RotorBearing);

        public static final RegistryObject<GenericDeviceBlock<MultiblockTurbine, TurbinePartType>> TURBINE_ROTORSHAFT_BASIC =
                registerTurbineBlock("basic_turbinerotorshaft", TurbineVariant.Basic, TurbinePartType.RotorShaft);

        public static final RegistryObject<GenericDeviceBlock<MultiblockTurbine, TurbinePartType>> TURBINE_ROTORBLADE_BASIC =
                registerTurbineBlock("basic_turbinerotorblade", TurbineVariant.Basic, TurbinePartType.RotorBlade);

        public static final RegistryObject<GenericDeviceBlock<MultiblockTurbine, TurbinePartType>> TURBINE_POWERTAP_FE_ACTIVE_BASIC =
                registerTurbineBlock("basic_turbinepowertapfe_active", TurbineVariant.Basic, TurbinePartType.ActivePowerTapFE);

        public static final RegistryObject<GenericDeviceBlock<MultiblockTurbine, TurbinePartType>> TURBINE_POWERTAP_FE_PASSIVE_BASIC =
                registerTurbineBlock("basic_turbinepowertapfe_passive", TurbineVariant.Basic, TurbinePartType.PassivePowerTapFE);

        public static final RegistryObject<IOPortBlock<MultiblockTurbine, TurbinePartType>> TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC =
                registerTurbineBlock("basic_turbinefluidport_forge_active", TurbineVariant.Basic, TurbinePartType.ActiveFluidPortForge);

        public static final RegistryObject<IOPortBlock<MultiblockTurbine, TurbinePartType>> TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC =
                registerTurbineBlock("basic_turbinefluidport_forge_passive", TurbineVariant.Basic, TurbinePartType.PassiveFluidPortForge);

        public static final RegistryObject<GenericDeviceBlock<MultiblockTurbine, TurbinePartType>> TURBINE_CREATIVE_STEAM_GENERATOR_BASIC =
                registerTurbineBlock("basic_turbinecreativesteamgenerator", TurbineVariant.Basic, TurbinePartType.CreativeSteamGenerator);

        public static final RegistryObject<TurbineRedstonePortBlock> TURBINE_REDSTONEPORT_BASIC =
                registerTurbineBlock("basic_turbineredstoneport", TurbineVariant.Basic, TurbinePartType.RedstonePort);

        public static final RegistryObject<GenericDeviceBlock<MultiblockTurbine, TurbinePartType>> TURBINE_CHARGINGPORT_FE_BASIC =
                registerTurbineBlock("basic_turbinechargingportfe", TurbineVariant.Basic, TurbinePartType.ChargingPortFE);

        //endregion
        //region reinforced

        public static final RegistryObject<MultiblockPartBlock<MultiblockTurbine, TurbinePartType>> TURBINE_CASING_REINFORCED =
                registerTurbineBlock("reinforced_turbinecasing", TurbineVariant.Reinforced, TurbinePartType.Casing);

        public static final RegistryObject<GlassBlock<MultiblockTurbine, TurbinePartType>> TURBINE_GLASS_REINFORCED =
                registerTurbineBlock("reinforced_turbineglass", TurbineVariant.Reinforced, TurbinePartType.Glass);

        public static final RegistryObject<GenericDeviceBlock<MultiblockTurbine, TurbinePartType>> TURBINE_CONTROLLER_REINFORCED =
                registerTurbineBlock("reinforced_turbinecontroller", TurbineVariant.Reinforced, TurbinePartType.Controller);

        public static final RegistryObject<TurbineRotorBearingBlock> TURBINE_ROTORBEARING_REINFORCED =
                registerTurbineBlock("reinforced_turbinerotorbearing", TurbineVariant.Reinforced, TurbinePartType.RotorBearing);

        public static final RegistryObject<GenericDeviceBlock<MultiblockTurbine, TurbinePartType>> TURBINE_ROTORSHAFT_REINFORCED =
                registerTurbineBlock("reinforced_turbinerotorshaft", TurbineVariant.Reinforced, TurbinePartType.RotorShaft);

        public static final RegistryObject<GenericDeviceBlock<MultiblockTurbine, TurbinePartType>> TURBINE_ROTORBLADE_REINFORCED =
                registerTurbineBlock("reinforced_turbinerotorblade", TurbineVariant.Reinforced, TurbinePartType.RotorBlade);

        public static final RegistryObject<GenericDeviceBlock<MultiblockTurbine, TurbinePartType>> TURBINE_POWERTAP_FE_ACTIVE_REINFORCED =
                registerTurbineBlock("reinforced_turbinepowertapfe_active", TurbineVariant.Reinforced, TurbinePartType.ActivePowerTapFE);

        public static final RegistryObject<GenericDeviceBlock<MultiblockTurbine, TurbinePartType>> TURBINE_POWERTAP_FE_PASSIVE_REINFORCED =
                registerTurbineBlock("reinforced_turbinepowertapfe_passive", TurbineVariant.Reinforced, TurbinePartType.PassivePowerTapFE);

        public static final RegistryObject<IOPortBlock<MultiblockTurbine, TurbinePartType>> TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED =
                registerTurbineBlock("reinforced_turbinefluidport_forge_active", TurbineVariant.Reinforced, TurbinePartType.ActiveFluidPortForge);

        public static final RegistryObject<IOPortBlock<MultiblockTurbine, TurbinePartType>> TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED =
                registerTurbineBlock("reinforced_turbinefluidport_forge_passive", TurbineVariant.Reinforced, TurbinePartType.PassiveFluidPortForge);

        public static final RegistryObject<GenericDeviceBlock<MultiblockTurbine, TurbinePartType>> TURBINE_CREATIVE_STEAM_GENERATOR_REINFORCED =
                registerTurbineBlock("reinforced_turbinecreativesteamgenerator", TurbineVariant.Reinforced, TurbinePartType.CreativeSteamGenerator);

        public static final RegistryObject<TurbineRedstonePortBlock> TURBINE_REDSTONEPORT_REINFORCED =
                registerTurbineBlock("reinforced_turbineredstoneport", TurbineVariant.Reinforced, TurbinePartType.RedstonePort);

        public static final RegistryObject<GenericDeviceBlock<MultiblockTurbine, TurbinePartType>> TURBINE_COMPUTERPORT_REINFORCED =
                registerTurbineBlock("reinforced_turbinecomputerport", TurbineVariant.Reinforced, TurbinePartType.ComputerPort);

        public static final RegistryObject<GenericDeviceBlock<MultiblockTurbine, TurbinePartType>> TURBINE_CHARGINGPORT_FE_REINFORCED =
                registerTurbineBlock("reinforced_turbinechargingportfe", TurbineVariant.Reinforced, TurbinePartType.ChargingPortFE);

        //endregion
        //endregion

        //region internals

        private static RegistryObject<ModBlock> registerMetalBlock(final String name, final DyeColor color) {
            return BLOCKS.register(name,
                    () -> new ModBlock(Block.Properties.of(Material.METAL, color).sound(SoundType.METAL)));
        }

        private static RegistryObject<ModBlock> registerOreBlock(final String name, final DyeColor color) {
            return registerOreBlock(name, color, 3, 5);
        }

        private static RegistryObject<ModBlock> registerOreBlock(final String name, final DyeColor color,
                                                                 final int minDroppedXP, final int maxDroppedXP) {
            return BLOCKS.register(name,
                    () -> new ModOreBlock(Block.Properties.of(Material.STONE, color)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops()
                            .strength(3.0F, 3.0F), minDroppedXP, maxDroppedXP));
        }

        @SuppressWarnings("unchecked")
        private static <T extends MultiblockPartBlock<MultiblockReactor, ReactorPartType>>
            RegistryObject<T> registerReactorBlock(final String name,
                                                   final ReactorVariant variant,
                                                   final ReactorPartType partType) {
            return BLOCKS.register(name, () -> (T) (partType.createBlock(variant)));
        }

        @SuppressWarnings("unchecked")
        private static <T extends MultiblockPartBlock<MultiblockTurbine, TurbinePartType>>
        RegistryObject<T> registerTurbineBlock(final String name,
                                               final TurbineVariant variant,
                                               final TurbinePartType partType) {
            return BLOCKS.register(name, () -> (T) (partType.createBlock(variant)));
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
                        .craftRemainder(net.minecraft.world.item.Items.BUCKET)
                        .stacksTo(1)
                        .tab(CreativeModeTab.TAB_MISC)));

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
        public static final RegistryObject<BlockItem> REACTOR_CHARGINGPORT_FE_BASIC = registerItemBlock("basic_reactorchargingportfe", () -> Blocks.REACTOR_CHARGINGPORT_FE_BASIC::get, ItemGroups.REACTOR);
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
        public static final RegistryObject<BlockItem> REACTOR_FLUIDPORT_FORGE_ACTIVE_REINFORCED = registerItemBlock("reinforced_reactorfluidport_forge_active", () -> Blocks.REACTOR_FLUIDTPORT_FORGE_ACTIVE_REINFORCED::get, ItemGroups.REACTOR);
        public static final RegistryObject<BlockItem> REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED = registerItemBlock("reinforced_reactorfluidport_forge_passive", () -> Blocks.REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED::get, ItemGroups.REACTOR);
        public static final RegistryObject<BlockItem> REACTOR_FLUIDPORT_MEKANISM_PASSIVE_REINFORCED = registerItemBlock("reinforced_reactorfluidport_mekanism_passive", () -> Blocks.REACTOR_FLUIDPORT_MEKANISM_PASSIVE_REINFORCED::get, ItemGroups.REACTOR);
        public static final RegistryObject<BlockItem> REACTOR_CREATIVE_WATER_GENERATOR_REINFORCED = registerItemBlock("reinforced_reactorcreativewatergenerator", () -> Blocks.REACTOR_CREATIVE_WATER_GENERATOR_REINFORCED::get, ItemGroups.REACTOR);
        public static final RegistryObject<BlockItem> REACTOR_CHARGINGPORT_FE_REINFORCED = registerItemBlock("reinforced_reactorchargingportfe", () -> Blocks.REACTOR_CHARGINGPORT_FE_REINFORCED::get, ItemGroups.REACTOR);
        //endregion
        //endregion

        //region turbine
        //region basic
        public static final RegistryObject<BlockItem> TURBINE_CASING_BASIC = registerItemBlock("basic_turbinecasing", () -> Blocks.TURBINE_CASING_BASIC::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_GLASS_BASIC = registerItemBlock("basic_turbineglass", () -> Blocks.TURBINE_GLASS_BASIC::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_CONTROLLER_BASIC = registerItemBlock("basic_turbinecontroller", () -> Blocks.TURBINE_CONTROLLER_BASIC::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_ROTORBEARING_BASIC = registerItemBlock("basic_turbinerotorbearing", () -> Blocks.TURBINE_ROTORBEARING_BASIC::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_ROTORSHAFT_BASIC = registerItemBlock("basic_turbinerotorshaft", () -> Blocks.TURBINE_ROTORSHAFT_BASIC::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_ROTORBLADE_BASIC = registerItemBlock("basic_turbinerotorblade", () -> Blocks.TURBINE_ROTORBLADE_BASIC::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_POWERTAP_FE_ACTIVE_BASIC = registerItemBlock("basic_turbinepowertapfe_active", () -> Blocks.TURBINE_POWERTAP_FE_ACTIVE_BASIC::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_POWERTAP_FE_PASSIVE_BASIC = registerItemBlock("basic_turbinepowertapfe_passive", () -> Blocks.TURBINE_POWERTAP_FE_PASSIVE_BASIC::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC = registerItemBlock("basic_turbinefluidport_forge_active", () -> Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC = registerItemBlock("basic_turbinefluidport_forge_passive", () -> Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_REDSTONEPORT_BASIC = registerItemBlock("basic_turbineredstoneport", () -> Blocks.TURBINE_REDSTONEPORT_BASIC::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_CREATIVE_STEAM_GENERATOR_BASIC = registerItemBlock("basic_turbinecreativesteamgenerator", () -> Blocks.TURBINE_CREATIVE_STEAM_GENERATOR_BASIC::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_CHARGINGPORT_FE_BASIC = registerItemBlock("basic_turbinechargingportfe", () -> Blocks.TURBINE_CHARGINGPORT_FE_BASIC::get, ItemGroups.TURBINE);
        //endregion
        //region reinforced
        public static final RegistryObject<BlockItem> TURBINE_CASING_REINFORCED = registerItemBlock("reinforced_turbinecasing", () -> Blocks.TURBINE_CASING_REINFORCED::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_GLASS_REINFORCED = registerItemBlock("reinforced_turbineglass", () -> Blocks.TURBINE_GLASS_REINFORCED::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_CONTROLLER_REINFORCED = registerItemBlock("reinforced_turbinecontroller", () -> Blocks.TURBINE_CONTROLLER_REINFORCED::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_ROTORBEARING_REINFORCED = registerItemBlock("reinforced_turbinerotorbearing", () -> Blocks.TURBINE_ROTORBEARING_REINFORCED::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_ROTORSHAFT_REINFORCED = registerItemBlock("reinforced_turbinerotorshaft", () -> Blocks.TURBINE_ROTORSHAFT_REINFORCED::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_ROTORBLADE_REINFORCED = registerItemBlock("reinforced_turbinerotorblade", () -> Blocks.TURBINE_ROTORBLADE_REINFORCED::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_POWERTAP_FE_ACTIVE_REINFORCED = registerItemBlock("reinforced_turbinepowertapfe_active", () -> Blocks.TURBINE_POWERTAP_FE_ACTIVE_REINFORCED::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_POWERTAP_FE_PASSIVE_REINFORCED = registerItemBlock("reinforced_turbinepowertapfe_passive", () -> Blocks.TURBINE_POWERTAP_FE_PASSIVE_REINFORCED::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED = registerItemBlock("reinforced_turbinefluidport_forge_active", () -> Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED = registerItemBlock("reinforced_turbinefluidport_forge_passive", () -> Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_REDSTONEPORT_REINFORCED = registerItemBlock("reinforced_turbineredstoneport", () -> Blocks.TURBINE_REDSTONEPORT_REINFORCED::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_COMPUTERPORT_REINFORCED = registerItemBlock("reinforced_turbinecomputerport", () -> Blocks.TURBINE_COMPUTERPORT_REINFORCED::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_CREATIVE_STEAM_GENERATOR_REINFORCED = registerItemBlock("reinforced_turbinecreativesteamgenerator", () -> Blocks.TURBINE_CREATIVE_STEAM_GENERATOR_REINFORCED::get, ItemGroups.TURBINE);
        public static final RegistryObject<BlockItem> TURBINE_CHARGINGPORT_FE_REINFORCED = registerItemBlock("reinforced_turbinechargingportfe", () -> Blocks.TURBINE_CHARGINGPORT_FE_REINFORCED::get, ItemGroups.TURBINE);
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
                    () -> new ModItem(new Item.Properties().tab(ItemGroups.GENERAL).stacksTo(maxStack)));
        }

        private static RegistryObject<BlockItem> registerItemBlock(final String name,
                                                                   final Supplier<Supplier<ModBlock>> blockSupplier,
                                                                   final CreativeModeTab group) {
            return ITEMS.register(name,
                    () -> blockSupplier.get().get().createBlockItem(new Item.Properties().tab(group).stacksTo(64)));
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

        private static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES =
                DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ExtremeReactors.MOD_ID);

        static void initialize() {
            TILE_ENTITIES.register(Mod.EventBusSubscriber.Bus.MOD.bus().get());
        }

        //region Reactor

        public static final RegistryObject<BlockEntityType<ReactorCasingEntity>> REACTOR_CASING =
                registerBlockEntity("reactorcasing", ReactorCasingEntity::new,
                        () -> Blocks.REACTOR_CASING_BASIC::get,
                        () -> Blocks.REACTOR_CASING_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<ReactorGlassEntity>> REACTOR_GLASS =
                registerBlockEntity("reactorglass", ReactorGlassEntity::new,
                        () -> Blocks.REACTOR_GLASS_BASIC::get,
                        () -> Blocks.REACTOR_GLASS_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<ReactorControllerEntity>> REACTOR_CONTROLLER =
                registerBlockEntity("reactorcontroller", ReactorControllerEntity::new,
                        () -> Blocks.REACTOR_CONTROLLER_BASIC::get,
                        () -> Blocks.REACTOR_CONTROLLER_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<ReactorFuelRodEntity>> REACTOR_FUELROD =
                registerBlockEntity("reactorfuelrod", ReactorFuelRodEntity::new,
                        () -> Blocks.REACTOR_FUELROD_BASIC::get,
                        () -> Blocks.REACTOR_FUELROD_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<ReactorControlRodEntity>> REACTOR_CONTROLROD =
                registerBlockEntity("reactorcontrolrod", ReactorControlRodEntity::new,
                        () -> Blocks.REACTOR_CONTROLROD_BASIC::get,
                        () -> Blocks.REACTOR_CONTROLROD_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<ReactorSolidAccessPortEntity>> REACTOR_SOLID_ACCESSPORT =
                registerBlockEntity("reactoraccessport", ReactorSolidAccessPortEntity::new,
                        () -> Blocks.REACTOR_SOLID_ACCESSPORT_BASIC::get,
                        () -> Blocks.REACTOR_SOLID_ACCESSPORT_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<ReactorFluidPortEntity>> REACTOR_FLUIDPORT_FORGE_ACTIVE =
                registerBlockEntity("reactorfluidport_forge_active",
                        () -> new ReactorFluidPortEntity(FluidPortType.Forge, IoMode.Active, TileEntityTypes.REACTOR_FLUIDPORT_FORGE_ACTIVE.get()),
                        () -> Blocks.REACTOR_FLUIDTPORT_FORGE_ACTIVE_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<ReactorFluidPortEntity>> REACTOR_FLUIDPORT_FORGE_PASSIVE =
                registerBlockEntity("reactorfluidport_forge_passive",
                        () -> new ReactorFluidPortEntity(FluidPortType.Forge, IoMode.Passive, TileEntityTypes.REACTOR_FLUIDPORT_FORGE_PASSIVE.get()),
                        () -> Blocks.REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<ReactorMekanismFluidPortEntity>> REACTOR_FLUIDPORT_MEKANISM_PASSIVE =
                registerBlockEntity("reactorfluidport_mekanism_passive",
                        ReactorMekanismFluidPortEntity::new,
                        () -> Blocks.REACTOR_FLUIDPORT_MEKANISM_PASSIVE_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<ReactorCreativeWaterGenerator>> REACTOR_CREATIVE_WATER_GENERATOR =
                registerBlockEntity("reactorcreativewatergenerator",
                        ReactorCreativeWaterGenerator::new,
                        () -> Blocks.REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<ReactorPowerTapEntity>> REACTOR_POWERTAP_FE_ACTIVE =
                registerBlockEntity("reactorpowertap_fe_active",
                        () -> new ReactorPowerTapEntity(EnergySystem.ForgeEnergy, IoMode.Active, TileEntityTypes.REACTOR_POWERTAP_FE_ACTIVE.get()),
                        () -> Blocks.REACTOR_POWERTAP_FE_ACTIVE_BASIC::get,
                        () -> Blocks.REACTOR_POWERTAP_FE_ACTIVE_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<ReactorPowerTapEntity>> REACTOR_POWERTAP_FE_PASSIVE =
                registerBlockEntity("reactorpowertap_fe_passive",
                        () -> new ReactorPowerTapEntity(EnergySystem.ForgeEnergy, IoMode.Passive, TileEntityTypes.REACTOR_POWERTAP_FE_PASSIVE.get()),
                        () -> Blocks.REACTOR_POWERTAP_FE_PASSIVE_BASIC::get,
                        () -> Blocks.REACTOR_POWERTAP_FE_PASSIVE_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<ReactorComputerPortEntity>> REACTOR_COMPUTERPORT =
                registerBlockEntity("reactorcomputerport", ReactorComputerPortEntity::new,
                        () -> Blocks.REACTOR_COMPUTERPORT_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<ReactorRedstonePortEntity>> REACTOR_REDSTONEPORT =
                registerBlockEntity("reactorredstoneport", ReactorRedstonePortEntity::new,
                        () -> Blocks.REACTOR_REDSTONEPORT_BASIC::get,
                        () -> Blocks.REACTOR_REDSTONEPORT_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<ReactorChargingPortEntity>> REACTOR_CHARGINGPORT_FE =
                registerBlockEntity("reactorchargingport_fe",
                        () -> new ReactorChargingPortEntity(EnergySystem.ForgeEnergy, TileEntityTypes.REACTOR_CHARGINGPORT_FE.get()),
                        () -> Blocks.REACTOR_CHARGINGPORT_FE_BASIC::get,
                        () -> Blocks.REACTOR_CHARGINGPORT_FE_REINFORCED::get);

        //endregion
        //region Turbine

        public static final RegistryObject<BlockEntityType<TurbineCasingEntity>> TURBINE_CASING =
                registerBlockEntity("turbinecasing", TurbineCasingEntity::new,
                        () -> Blocks.TURBINE_CASING_BASIC::get,
                        () -> Blocks.TURBINE_CASING_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<TurbineGlassEntity>> TURBINE_GLASS =
                registerBlockEntity("turbineglass", TurbineGlassEntity::new,
                        () -> Blocks.TURBINE_GLASS_BASIC::get,
                        () -> Blocks.TURBINE_GLASS_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<TurbineControllerEntity>> TURBINE_CONTROLLER =
                registerBlockEntity("turbinecontroller", TurbineControllerEntity::new,
                        () -> Blocks.TURBINE_CONTROLLER_BASIC::get,
                        () -> Blocks.TURBINE_CONTROLLER_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<TurbineRotorBearingEntity>> TURBINE_ROTORBEARING =
                registerBlockEntity("turbinerotorbearing", TurbineRotorBearingEntity::new,
                        () -> Blocks.TURBINE_ROTORBEARING_BASIC::get,
                        () -> Blocks.TURBINE_ROTORBEARING_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<TurbineRotorComponentEntity>> TURBINE_ROTORSHAFT =
                registerBlockEntity("turbinerotorshaft", TurbineRotorComponentEntity::shaft,
                        () -> Blocks.TURBINE_ROTORSHAFT_BASIC::get,
                        () -> Blocks.TURBINE_ROTORSHAFT_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<TurbineRotorComponentEntity>> TURBINE_ROTORBLADE =
                registerBlockEntity("turbinerotorblade", TurbineRotorComponentEntity::blade,
                        () -> Blocks.TURBINE_ROTORBLADE_BASIC::get,
                        () -> Blocks.TURBINE_ROTORBLADE_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<TurbineFluidPortEntity>> TURBINE_FLUIDPORT_FORGE_ACTIVE =
                registerBlockEntity("turbinefluidport_forge_active",
                        () -> new TurbineFluidPortEntity(FluidPortType.Forge, IoMode.Active, TileEntityTypes.TURBINE_FLUIDPORT_FORGE_ACTIVE.get()),
                        () -> Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC::get,
                        () -> Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<TurbineFluidPortEntity>> TURBINE_FLUIDPORT_FORGE_PASSIVE =
                registerBlockEntity("turbinefluidport_forge_passive",
                        () -> new TurbineFluidPortEntity(FluidPortType.Forge, IoMode.Passive, TileEntityTypes.TURBINE_FLUIDPORT_FORGE_PASSIVE.get()),
                        () -> Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC::get,
                        () -> Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<TurbineCreativeSteamGenerator>> TURBINE_CREATIVE_STEAM_GENERATOR =
                registerBlockEntity("turbinecreativesteamgenerator",
                        TurbineCreativeSteamGenerator::new,
                        () -> Blocks.TURBINE_CREATIVE_STEAM_GENERATOR_BASIC::get);

        public static final RegistryObject<BlockEntityType<TurbinePowerTapEntity>> TURBINE_POWERTAP_FE_ACTIVE =
                registerBlockEntity("turbinepowertap_fe_active",
                        () -> new TurbinePowerTapEntity(EnergySystem.ForgeEnergy, IoMode.Active, TileEntityTypes.TURBINE_POWERTAP_FE_ACTIVE.get()),
                        () -> Blocks.TURBINE_POWERTAP_FE_ACTIVE_BASIC::get,
                        () -> Blocks.TURBINE_POWERTAP_FE_ACTIVE_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<TurbinePowerTapEntity>> TURBINE_POWERTAP_FE_PASSIVE =
                registerBlockEntity("turbinepowertap_fe_passive",
                        () -> new TurbinePowerTapEntity(EnergySystem.ForgeEnergy, IoMode.Passive, TileEntityTypes.TURBINE_POWERTAP_FE_PASSIVE.get()),
                        () -> Blocks.TURBINE_POWERTAP_FE_PASSIVE_BASIC::get,
                        () -> Blocks.TURBINE_POWERTAP_FE_PASSIVE_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<TurbineComputerPortEntity>> TURBINE_COMPUTERPORT =
                registerBlockEntity("turbinecomputerport", TurbineComputerPortEntity::new,
                        () -> Blocks.TURBINE_COMPUTERPORT_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<TurbineRedstonePortEntity>> TURBINE_REDSTONEPORT =
                registerBlockEntity("turbineredstoneport", TurbineRedstonePortEntity::new,
                        () -> Blocks.TURBINE_REDSTONEPORT_BASIC::get,
                        () -> Blocks.TURBINE_REDSTONEPORT_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<TurbineChargingPortEntity>> TURBINE_CHARGINGPORT_FE =
                registerBlockEntity("turbinechargingport_fe",
                        () -> new TurbineChargingPortEntity(EnergySystem.ForgeEnergy, TileEntityTypes.TURBINE_CHARGINGPORT_FE.get()),
                        () -> Blocks.TURBINE_CHARGINGPORT_FE_BASIC::get,
                        () -> Blocks.TURBINE_CHARGINGPORT_FE_REINFORCED::get);

        //endregion
        //region internals

        @SuppressWarnings("ConstantConditions")
        @SafeVarargs
        private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerBlockEntity(final String name,
                                                                                                    final Supplier<T> factory,
                                                                                                    final Supplier<Supplier<Block>>... validBlockSuppliers) {
            return TILE_ENTITIES.register(name, () -> {

                final Block[] validBlocks = new Block[validBlockSuppliers.length];

                if (validBlockSuppliers.length > 0) {
                    for (int i = 0; i < validBlockSuppliers.length; ++i) {
                        validBlocks[i] = validBlockSuppliers[i].get().get();
                    }
                }

                return BlockEntityType.Builder.of(factory, validBlocks).build(null);
            });
        }

        //endregion
    }

    public static final class ContainerTypes {

        private static final DeferredRegister<MenuType<?>> CONTAINERS =
                DeferredRegister.create(ForgeRegistries.CONTAINERS, ExtremeReactors.MOD_ID);

        static void initialize() {
            CONTAINERS.register(Mod.EventBusSubscriber.Bus.MOD.bus().get());
        }

        //region Reactor

        public static final RegistryObject<MenuType<ModTileContainer<ReactorControllerEntity>>> REACTOR_CONTROLLER =
                registerContainer("reactorcontroller", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.REACTOR_CONTROLLER.get(), windowId, data));

        public static final RegistryObject<MenuType<ReactorSolidAccessPortContainer>> REACTOR_SOLID_ACCESSPORT =
                registerContainer("reactoraccessport", ReactorSolidAccessPortContainer::new);

        public static final RegistryObject<MenuType<ModTileContainer<ReactorRedstonePortEntity>>> REACTOR_REDSTONEPORT =
                registerContainer("reactorredstoneport", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.REACTOR_REDSTONEPORT.get(), windowId, data));

        public static final RegistryObject<MenuType<ModTileContainer<ReactorControlRodEntity>>> REACTOR_CONTROLROD =
                registerContainer("reactorcontrolrod", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.REACTOR_CONTROLROD.get(), windowId, data));

        public static final RegistryObject<MenuType<ChargingPortContainer<ReactorChargingPortEntity>>> REACTOR_CHARGINGPORT =
                registerContainer("reactorchargingport",
                        (windowId, inv, data) -> new ChargingPortContainer<>(windowId, Content.ContainerTypes.REACTOR_CHARGINGPORT.get(), inv, data));

        public static final RegistryObject<MenuType<ModTileContainer<ReactorFluidPortEntity>>> REACTOR_FLUIDPORT =
                registerContainer("reactorfluidport", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.REACTOR_FLUIDPORT.get(), windowId, data));

        //endregion
        //region Turbine

        public static final RegistryObject<MenuType<ModTileContainer<TurbineControllerEntity>>> TURBINE_CONTROLLER =
                registerContainer("turbinecontroller", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.TURBINE_CONTROLLER.get(), windowId, data));

        public static final RegistryObject<MenuType<ModTileContainer<TurbineRedstonePortEntity>>> TURBINE_REDSTONEPORT =
                registerContainer("turbineredstoneport", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.TURBINE_REDSTONEPORT.get(), windowId, data));

        public static final RegistryObject<MenuType<ChargingPortContainer<TurbineChargingPortEntity>>> TURBINE_CHARGINGPORT =
                registerContainer("turbinechargingport",
                        (windowId, inv, data) -> new ChargingPortContainer<>(windowId, Content.ContainerTypes.TURBINE_CHARGINGPORT.get(), inv, data));

        public static final RegistryObject<MenuType<ModTileContainer<TurbineFluidPortEntity>>> TURBINE_FLUIDPORT =
                registerContainer("turbinefluidport", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.TURBINE_FLUIDPORT.get(), windowId, data));

        //endregion
        //region internals

        private static <C extends AbstractContainerMenu> RegistryObject<MenuType<C>> registerContainer(final String name,
                                                                                                final IContainerFactory<C> factory) {
            return CONTAINERS.register(name,
                    () -> IForgeContainerType.create(factory));
        }

        //endregion
    }

    //region internals

    private static void onCommonInit(final FMLCommonSetupEvent event) {

        ReactorGameData.register();
        TurbineGameData.register();
    }

    //endregion
}
