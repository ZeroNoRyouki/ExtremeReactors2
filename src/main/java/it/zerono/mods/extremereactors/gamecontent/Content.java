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
import it.zerono.mods.extremereactors.gamecontent.fluid.ReactantFluid;
import it.zerono.mods.extremereactors.gamecontent.fluid.SteamFluid;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.container.ChargingPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.GenericDeviceBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.GlassBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.IOPortBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.fluidport.FluidPortType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.FluidizerPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.MultiblockFluidizer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part.*;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerFluidMixingRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerSolidMixingRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerSolidRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactorPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.container.ReactorSolidAccessPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.*;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.MultiblockReprocessor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.ReprocessorPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.container.ReprocessorAccessPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.part.*;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.recipe.ReprocessorRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.MultiblockTurbine;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.TurbinePartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.*;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.zerocore.lib.block.ModBlock;
import it.zerono.mods.zerocore.lib.block.ModOreBlock;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.item.ModItem;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.recipe.ModRecipe;
import it.zerono.mods.zerocore.lib.recipe.ModRecipeType;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
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

        final IEventBus bus = Mod.EventBusSubscriber.Bus.MOD.bus().get();

        Blocks.initialize(bus);
        Items.initialize(bus);
        Fluids.initialize(bus);
        TileEntityTypes.initialize(bus);
        ContainerTypes.initialize(bus);
        Recipes.initialize(bus);

        bus.addListener(Content::onCommonInit);
    }

    public static final class Blocks {

        private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ExtremeReactors.MOD_ID);

        static void initialize(final IEventBus bus) {
            BLOCKS.register(bus);
        }

        //region metals

        public static final RegistryObject<ModBlock> YELLORIUM_BLOCK = registerMetalBlock("yellorium_block", DyeColor.YELLOW);
        public static final RegistryObject<ModBlock> CYANITE_BLOCK = registerMetalBlock("cyanite_block", DyeColor.LIGHT_BLUE);
        public static final RegistryObject<ModBlock> GRAPHITE_BLOCK = registerMetalBlock("graphite_block", DyeColor.GRAY);
        public static final RegistryObject<ModBlock> BLUTONIUM_BLOCK = registerMetalBlock("blutonium_block", DyeColor.PURPLE);
        public static final RegistryObject<ModBlock> MAGENTITE_BLOCK = registerMetalBlock("magentite_block", DyeColor.MAGENTA);

        //endregion
        //region ores

        public static final RegistryObject<ModBlock> YELLORITE_ORE_BLOCK = registerOreBlock("yellorite_ore", DyeColor.YELLOW, 0, 0);
        public static final RegistryObject<ModBlock> ANGLESITE_ORE_BLOCK = registerOreBlock("anglesite_ore", DyeColor.ORANGE);
        public static final RegistryObject<ModBlock> BENITOITE_ORE_BLOCK = registerOreBlock("benitoite_ore", DyeColor.LIGHT_BLUE);

        //endregion
        //region fluids

        public static final RegistryObject<FlowingFluidBlock> STEAM = BLOCKS.register("steam",
                () -> new FlowingFluidBlock(Fluids.STEAM_SOURCE,
                        Block.Properties.of(Material.WATER)
                                .noCollission()
                                .strength(100.0F)
                                .noDrops()
                ));

        public static final RegistryObject<ReactantFluid.Block> YELLORIUM_FLUID = BLOCKS.register("yellorium_fluid",
                () -> new ReactantFluid.Block(Fluids.YELLORIUM_SOURCE));

        public static final RegistryObject<ReactantFluid.Block> CYANITE_FLUID = BLOCKS.register("cyanite_fluid",
                () -> new ReactantFluid.Block(Fluids.CYANITE_SOURCE));

        public static final RegistryObject<ReactantFluid.Block> BLUTONIUM_FLUID = BLOCKS.register("blutonium_fluid",
                () -> new ReactantFluid.Block(Fluids.BLUTONIUM_SOURCE));

        public static final RegistryObject<ReactantFluid.Block> MAGENTITE_FLUID = BLOCKS.register("magentite_fluid",
                () -> new ReactantFluid.Block(Fluids.MAGENTITE_SOURCE));

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

        public static final RegistryObject<IOPortBlock<MultiblockReactor, ReactorPartType>> REACTOR_FLUID_ACCESSPORT_REINFORCED =
                registerReactorBlock("reinforced_reactorfluidaccessport", ReactorVariant.Reinforced, ReactorPartType.FluidAccessPort);

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
        //region reprocessor

        public static final RegistryObject<MultiblockPartBlock<MultiblockReprocessor, ReprocessorPartType>> REPROCESSOR_CASING =
                registerReprocessorBlock("reprocessorcasing", ReprocessorPartType.Casing);

        public static final RegistryObject<GlassBlock<MultiblockReprocessor, ReprocessorPartType>> REPROCESSOR_GLASS =
                registerReprocessorBlock("reprocessorglass", ReprocessorPartType.Glass);

        public static final RegistryObject<GenericDeviceBlock<MultiblockReprocessor, ReprocessorPartType>> REPROCESSOR_CONTROLLER =
                registerReprocessorBlock("reprocessorcontroller", ReprocessorPartType.Controller);

        public static final RegistryObject<GenericDeviceBlock<MultiblockReprocessor, ReprocessorPartType>> REPROCESSOR_WASTEINJECTOR =
                registerReprocessorBlock("reprocessorwasteinjector", ReprocessorPartType.WasteInjector);

        public static final RegistryObject<GenericDeviceBlock<MultiblockReprocessor, ReprocessorPartType>> REPROCESSOR_FLUIDINJECTOR =
                registerReprocessorBlock("reprocessorfluidinjector", ReprocessorPartType.FluidInjector);

        public static final RegistryObject<GenericDeviceBlock<MultiblockReprocessor, ReprocessorPartType>> REPROCESSOR_OUTPUTPORT =
                registerReprocessorBlock("reprocessoroutputport", ReprocessorPartType.OutputPort);

        public static final RegistryObject<GenericDeviceBlock<MultiblockReprocessor, ReprocessorPartType>> REPROCESSOR_POWERPORT =
                registerReprocessorBlock("reprocessorpowerport", ReprocessorPartType.PowerPort);

        public static final RegistryObject<GenericDeviceBlock<MultiblockReprocessor, ReprocessorPartType>> REPROCESSOR_COLLECTOR =
                registerReprocessorBlock("reprocessorcollector", ReprocessorPartType.Collector);

        //endregion
        //region fluidizer

        public static final RegistryObject<MultiblockPartBlock<MultiblockFluidizer, FluidizerPartType>> FLUIDIZER_CASING =
                registerFluidizerBlock("fluidizercasing", FluidizerPartType.Casing);

        public static final RegistryObject<GlassBlock<MultiblockFluidizer, FluidizerPartType>> FLUIDIZER_GLASS =
                registerFluidizerBlock("fluidizerglass", FluidizerPartType.Glass);

        public static final RegistryObject<GenericDeviceBlock<MultiblockFluidizer, FluidizerPartType>> FLUIDIZER_CONTROLLER =
                registerFluidizerBlock("fluidizercontroller", FluidizerPartType.Controller);

        public static final RegistryObject<GenericDeviceBlock<MultiblockFluidizer, FluidizerPartType>> FLUIDIZER_SOLIDINJECTOR =
                registerFluidizerBlock("fluidizersolidinjector", FluidizerPartType.SolidInjector);

        public static final RegistryObject<GenericDeviceBlock<MultiblockFluidizer, FluidizerPartType>> FLUIDIZER_FLUIDINJECTOR =
                registerFluidizerBlock("fluidizerfluidinjector", FluidizerPartType.FluidInjector);

        public static final RegistryObject<GenericDeviceBlock<MultiblockFluidizer, FluidizerPartType>> FLUIDIZER_OUTPUTPORT =
                registerFluidizerBlock("fluidizeroutputport", FluidizerPartType.OutputPort);

        public static final RegistryObject<GenericDeviceBlock<MultiblockFluidizer, FluidizerPartType>> FLUIDIZER_POWERPORT =
                registerFluidizerBlock("fluidizerpowerport", FluidizerPartType.PowerPort);

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

        @SuppressWarnings("unchecked")
        private static <T extends MultiblockPartBlock<MultiblockReprocessor, ReprocessorPartType>>
        RegistryObject<T> registerReprocessorBlock(final String name, final ReprocessorPartType partType) {
            return BLOCKS.register(name, () -> (T) (partType.createBlock()));
        }

        @SuppressWarnings("unchecked")
        private static <T extends MultiblockPartBlock<MultiblockFluidizer, FluidizerPartType>>
        RegistryObject<T> registerFluidizerBlock(final String name, final FluidizerPartType partType) {
            return BLOCKS.register(name, () -> (T) (partType.createBlock()));
        }

        //endregion
    }

    @SuppressWarnings("unused")
    public static final class Items {

        private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExtremeReactors.MOD_ID);

        static void initialize(final IEventBus bus) {
            ITEMS.register(bus);
        }

        //region metals

        public static final RegistryObject<ModItem> YELLORIUM_INGOT = registerItemGeneric("yellorium_ingot");
        public static final RegistryObject<ModItem> CYANITE_INGOT = registerItemGeneric("cyanite_ingot");
        public static final RegistryObject<ModItem> GRAPHITE_INGOT = registerItemGeneric("graphite_ingot");
        public static final RegistryObject<ModItem> BLUTONIUM_INGOT = registerItemGeneric("blutonium_ingot");
        public static final RegistryObject<ModItem> MAGENTITE_INGOT = registerItemGeneric("magentite_ingot");

        public static final RegistryObject<ModItem> YELLORIUM_DUST = registerItemGeneric("yellorium_dust");
        public static final RegistryObject<ModItem> CYANITE_DUST = registerItemGeneric("cyanite_dust");
        public static final RegistryObject<ModItem> GRAPHITE_DUST = registerItemGeneric("graphite_dust");
        public static final RegistryObject<ModItem> BLUTONIUM_DUST = registerItemGeneric("blutonium_dust");
        public static final RegistryObject<ModItem> MAGENTITE_DUST = registerItemGeneric("magentite_dust");

        public static final RegistryObject<BlockItem> YELLORIUM_BLOCK = registerItemBlock("yellorium_block", () -> Blocks.YELLORIUM_BLOCK, ItemGroups.GENERAL);
        public static final RegistryObject<BlockItem> CYANITE_BLOCK = registerItemBlock("cyanite_block", () -> Blocks.CYANITE_BLOCK, ItemGroups.GENERAL);
        public static final RegistryObject<BlockItem> GRAPHITE_BLOCK = registerItemBlock("graphite_block", () -> Blocks.GRAPHITE_BLOCK, ItemGroups.GENERAL);
        public static final RegistryObject<BlockItem> BLUTONIUM_BLOCK = registerItemBlock("blutonium_block", () -> Blocks.BLUTONIUM_BLOCK, ItemGroups.GENERAL);
        public static final RegistryObject<BlockItem> MAGENTITE_BLOCK = registerItemBlock("magentite_block", () -> Blocks.MAGENTITE_BLOCK, ItemGroups.GENERAL);

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
                () -> new BucketItem(Fluids.STEAM_SOURCE, bucketProperties()));

        public static final RegistryObject<Item> YELLORIUM_BUCKET = ITEMS.register("yellorium_bucket",
                () -> new BucketItem(Fluids.YELLORIUM_SOURCE, bucketProperties()));

        public static final RegistryObject<Item> CYANITE_BUCKET = ITEMS.register("cyanite_bucket",
                () -> new BucketItem(Fluids.CYANITE_SOURCE, bucketProperties()));

        public static final RegistryObject<Item> BLUTONIUM_BUCKET = ITEMS.register("blutonium_bucket",
                () -> new BucketItem(Fluids.BLUTONIUM_SOURCE, bucketProperties()));

        public static final RegistryObject<Item> MAGENTITE_BUCKET = ITEMS.register("magentite_bucket",
                () -> new BucketItem(Fluids.MAGENTITE_SOURCE, bucketProperties()));

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
        public static final RegistryObject<BlockItem> REACTOR_FLUID_ACCESSPORT_REINFORCED = registerItemBlock("reinforced_reactorfluidaccessport", () -> Blocks.REACTOR_FLUID_ACCESSPORT_REINFORCED::get, ItemGroups.REACTOR);
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
        //region reprocessor

        public static final RegistryObject<BlockItem> REPROCESSOR_CASING = registerItemBlock("reprocessorcasing", () -> Blocks.REPROCESSOR_CASING::get, ItemGroups.GENERAL);
        public static final RegistryObject<BlockItem> REPROCESSOR_GLASS = registerItemBlock("reprocessorglass", () -> Blocks.REPROCESSOR_GLASS::get, ItemGroups.GENERAL);
        public static final RegistryObject<BlockItem> REPROCESSOR_CONTROLLER = registerItemBlock("reprocessorcontroller", () -> Blocks.REPROCESSOR_CONTROLLER::get, ItemGroups.GENERAL);
        public static final RegistryObject<BlockItem> REPROCESSOR_WASTEINJECTOR = registerItemBlock("reprocessorwasteinjector", () -> Blocks.REPROCESSOR_WASTEINJECTOR::get, ItemGroups.GENERAL);
        public static final RegistryObject<BlockItem> REPROCESSOR_FLUIDINJECTOR = registerItemBlock("reprocessorfluidinjector", () -> Blocks.REPROCESSOR_FLUIDINJECTOR::get, ItemGroups.GENERAL);
        public static final RegistryObject<BlockItem> REPROCESSOR_OUTPUTPORT = registerItemBlock("reprocessoroutputport", () -> Blocks.REPROCESSOR_OUTPUTPORT::get, ItemGroups.GENERAL);
        public static final RegistryObject<BlockItem> REPROCESSOR_POWERPORT = registerItemBlock("reprocessorpowerport", () -> Blocks.REPROCESSOR_POWERPORT::get, ItemGroups.GENERAL);
        public static final RegistryObject<BlockItem> REPROCESSOR_COLLECTOR = registerItemBlock("reprocessorcollector", () -> Blocks.REPROCESSOR_COLLECTOR::get, ItemGroups.GENERAL);

        //endregion
        //region fluidizer

        public static final RegistryObject<BlockItem> FLUIDIZER_CASING = registerItemBlock("fluidizercasing", () -> Blocks.FLUIDIZER_CASING::get, ItemGroups.GENERAL);
        public static final RegistryObject<BlockItem> FLUIDIZER_GLASS = registerItemBlock("fluidizerglass", () -> Blocks.FLUIDIZER_GLASS::get, ItemGroups.GENERAL);
        public static final RegistryObject<BlockItem> FLUIDIZER_CONTROLLER = registerItemBlock("fluidizercontroller", () -> Blocks.FLUIDIZER_CONTROLLER::get, ItemGroups.GENERAL);
        public static final RegistryObject<BlockItem> FLUIDIZER_SOLIDINJECTOR = registerItemBlock("fluidizersolidinjector", () -> Blocks.FLUIDIZER_SOLIDINJECTOR::get, ItemGroups.GENERAL);
        public static final RegistryObject<BlockItem> FLUIDIZER_FLUIDINJECTOR = registerItemBlock("fluidizerfluidinjector", () -> Blocks.FLUIDIZER_FLUIDINJECTOR::get, ItemGroups.GENERAL);
        public static final RegistryObject<BlockItem> FLUIDIZER_OUTPUTPORT = registerItemBlock("fluidizeroutputport", () -> Blocks.FLUIDIZER_OUTPUTPORT::get, ItemGroups.GENERAL);
        public static final RegistryObject<BlockItem> FLUIDIZER_POWERPORT = registerItemBlock("fluidizerpowerport", () -> Blocks.FLUIDIZER_POWERPORT::get, ItemGroups.GENERAL);

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
                                                                   final ItemGroup group) {
            return ITEMS.register(name,
                    () -> blockSupplier.get().get().createBlockItem(new Item.Properties().tab(group).stacksTo(64)));
        }

        private static Item.Properties bucketProperties() {
            return new Item.Properties()
                    .craftRemainder(net.minecraft.item.Items.BUCKET)
                    .stacksTo(1)
                    .tab(ItemGroup.TAB_MISC);
        }

        //endregion
    }

    public static final class Fluids {

        private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, ExtremeReactors.MOD_ID);

        static void initialize(final IEventBus bus) {
            FLUIDS.register(bus);
        }

        public static final RegistryObject<ForgeFlowingFluid> STEAM_SOURCE = FLUIDS.register("steam", SteamFluid.Source::new);
        public static final RegistryObject<ForgeFlowingFluid> STEAM_FLOWING = FLUIDS.register("steam_flowing", SteamFluid.Flowing::new);

        public static final RegistryObject<ReactantFluid.Source> YELLORIUM_SOURCE = FLUIDS.register("yellorium",
                () -> new ReactantFluid.Source(Fluids.YELLORIUM_SOURCE, Fluids.YELLORIUM_FLOWING, Blocks.YELLORIUM_FLUID,
                        Items.YELLORIUM_BUCKET, CommonConstants.REACTANT_COLOUR_YELLORIUM, Fluids::yelloriumEffect));

        public static final RegistryObject<ReactantFluid.Flowing> YELLORIUM_FLOWING = FLUIDS.register("yellorium_flowing",
                () -> new ReactantFluid.Flowing(Fluids.YELLORIUM_SOURCE, Fluids.YELLORIUM_FLOWING, Blocks.YELLORIUM_FLUID,
                        Items.YELLORIUM_BUCKET, CommonConstants.REACTANT_COLOUR_YELLORIUM, Fluids::yelloriumEffect));

        public static final RegistryObject<ReactantFluid.Source> CYANITE_SOURCE = FLUIDS.register("cyanite",
                () -> new ReactantFluid.Source(Fluids.CYANITE_SOURCE, Fluids.CYANITE_FLOWING, Blocks.CYANITE_FLUID,
                        Items.CYANITE_BUCKET, CommonConstants.REACTANT_COLOUR_CYANITE, Fluids::cyaniteEffect));

        public static final RegistryObject<ReactantFluid.Flowing> CYANITE_FLOWING = FLUIDS.register("cyanite_flowing",
                () -> new ReactantFluid.Flowing(Fluids.CYANITE_SOURCE, Fluids.CYANITE_FLOWING, Blocks.CYANITE_FLUID,
                        Items.CYANITE_BUCKET, CommonConstants.REACTANT_COLOUR_CYANITE, Fluids::cyaniteEffect));

        public static final RegistryObject<ReactantFluid.Source> BLUTONIUM_SOURCE = FLUIDS.register("blutonium",
                () -> new ReactantFluid.Source(Fluids.BLUTONIUM_SOURCE, Fluids.BLUTONIUM_FLOWING, Blocks.BLUTONIUM_FLUID,
                        Items.BLUTONIUM_BUCKET, CommonConstants.REACTANT_COLOUR_BLUTONIUM, Fluids::blutoniumEffect));

        public static final RegistryObject<ReactantFluid.Flowing> BLUTONIUM_FLOWING = FLUIDS.register("blutonium_flowing",
                () -> new ReactantFluid.Flowing(Fluids.BLUTONIUM_SOURCE, Fluids.BLUTONIUM_FLOWING, Blocks.BLUTONIUM_FLUID,
                        Items.BLUTONIUM_BUCKET, CommonConstants.REACTANT_COLOUR_BLUTONIUM, Fluids::blutoniumEffect));

        public static final RegistryObject<ReactantFluid.Source> MAGENTITE_SOURCE = FLUIDS.register("magentite",
                () -> new ReactantFluid.Source(Fluids.MAGENTITE_SOURCE, Fluids.MAGENTITE_FLOWING, Blocks.MAGENTITE_FLUID,
                        Items.MAGENTITE_BUCKET, CommonConstants.REACTANT_COLOUR_MAGENTITE, Fluids::magentiteEffect));

        public static final RegistryObject<ReactantFluid.Flowing> MAGENTITE_FLOWING = FLUIDS.register("magentite_flowing",
                () -> new ReactantFluid.Flowing(Fluids.MAGENTITE_SOURCE, Fluids.MAGENTITE_FLOWING, Blocks.MAGENTITE_FLUID,
                        Items.MAGENTITE_BUCKET, CommonConstants.REACTANT_COLOUR_MAGENTITE, Fluids::magentiteEffect));

        //region internals

        private static void yelloriumEffect(final LivingEntity entity) {
            applyEffects(entity, Effects.WEAKNESS, Effects.MOVEMENT_SLOWDOWN);
        }

        private static void cyaniteEffect(final LivingEntity entity) {
            applyEffects(entity, Effects.WEAKNESS, Effects.MOVEMENT_SLOWDOWN, Effects.HUNGER);
        }

        private static void blutoniumEffect(final LivingEntity entity) {
            applyEffects(entity, Effects.WEAKNESS, Effects.MOVEMENT_SLOWDOWN, Effects.HUNGER, Effects.POISON);
        }

        private static void magentiteEffect(final LivingEntity entity) {
            applyEffects(entity, Effects.WEAKNESS, Effects.MOVEMENT_SLOWDOWN, Effects.HUNGER, Effects.WITHER);
        }

        private static void applyEffects(final LivingEntity entity, Effect... effects) {

            for (final Effect effect : effects) {
                entity.addEffect(new EffectInstance(effect, 400, 0, true, true, true));
            }
        }

        //endregion
    }

    public static final class TileEntityTypes {

        private static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES =
                DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ExtremeReactors.MOD_ID);

        static void initialize(final IEventBus bus) {
            TILE_ENTITIES.register(bus);
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

        public static final RegistryObject<TileEntityType<ReactorFluidAccessPortEntity>> REACTOR_FLUID_ACCESSPORT =
                registerBlockEntity("reactoraccessportfluid", ReactorFluidAccessPortEntity::new,
                        () -> Blocks.REACTOR_FLUID_ACCESSPORT_REINFORCED::get);

        public static final RegistryObject<TileEntityType<ReactorFluidPortEntity>> REACTOR_FLUIDPORT_FORGE_ACTIVE =
                registerBlockEntity("reactorfluidport_forge_active",
                        () -> new ReactorFluidPortEntity(FluidPortType.Forge, IoMode.Active, TileEntityTypes.REACTOR_FLUIDPORT_FORGE_ACTIVE.get()),
                        () -> Blocks.REACTOR_FLUIDTPORT_FORGE_ACTIVE_REINFORCED::get);

        public static final RegistryObject<TileEntityType<ReactorFluidPortEntity>> REACTOR_FLUIDPORT_FORGE_PASSIVE =
                registerBlockEntity("reactorfluidport_forge_passive",
                        () -> new ReactorFluidPortEntity(FluidPortType.Forge, IoMode.Passive, TileEntityTypes.REACTOR_FLUIDPORT_FORGE_PASSIVE.get()),
                        () -> Blocks.REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED::get);

        public static final RegistryObject<TileEntityType<ReactorMekanismFluidPortEntity>> REACTOR_FLUIDPORT_MEKANISM_PASSIVE =
                registerBlockEntity("reactorfluidport_mekanism_passive",
                        ReactorMekanismFluidPortEntity::new,
                        () -> Blocks.REACTOR_FLUIDPORT_MEKANISM_PASSIVE_REINFORCED::get);

        public static final RegistryObject<TileEntityType<ReactorCreativeWaterGenerator>> REACTOR_CREATIVE_WATER_GENERATOR =
                registerBlockEntity("reactorcreativewatergenerator",
                        ReactorCreativeWaterGenerator::new,
                        () -> Blocks.REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED::get);

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

        public static final RegistryObject<TileEntityType<ReactorChargingPortEntity>> REACTOR_CHARGINGPORT_FE =
                registerBlockEntity("reactorchargingport_fe",
                        () -> new ReactorChargingPortEntity(EnergySystem.ForgeEnergy, TileEntityTypes.REACTOR_CHARGINGPORT_FE.get()),
                        () -> Blocks.REACTOR_CHARGINGPORT_FE_BASIC::get,
                        () -> Blocks.REACTOR_CHARGINGPORT_FE_REINFORCED::get);

        //endregion
        //region Turbine

        public static final RegistryObject<TileEntityType<TurbineCasingEntity>> TURBINE_CASING =
                registerBlockEntity("turbinecasing", TurbineCasingEntity::new,
                        () -> Blocks.TURBINE_CASING_BASIC::get,
                        () -> Blocks.TURBINE_CASING_REINFORCED::get);

        public static final RegistryObject<TileEntityType<TurbineGlassEntity>> TURBINE_GLASS =
                registerBlockEntity("turbineglass", TurbineGlassEntity::new,
                        () -> Blocks.TURBINE_GLASS_BASIC::get,
                        () -> Blocks.TURBINE_GLASS_REINFORCED::get);

        public static final RegistryObject<TileEntityType<TurbineControllerEntity>> TURBINE_CONTROLLER =
                registerBlockEntity("turbinecontroller", TurbineControllerEntity::new,
                        () -> Blocks.TURBINE_CONTROLLER_BASIC::get,
                        () -> Blocks.TURBINE_CONTROLLER_REINFORCED::get);

        public static final RegistryObject<TileEntityType<TurbineRotorBearingEntity>> TURBINE_ROTORBEARING =
                registerBlockEntity("turbinerotorbearing", TurbineRotorBearingEntity::new,
                        () -> Blocks.TURBINE_ROTORBEARING_BASIC::get,
                        () -> Blocks.TURBINE_ROTORBEARING_REINFORCED::get);

        public static final RegistryObject<TileEntityType<TurbineRotorComponentEntity>> TURBINE_ROTORSHAFT =
                registerBlockEntity("turbinerotorshaft", TurbineRotorComponentEntity::shaft,
                        () -> Blocks.TURBINE_ROTORSHAFT_BASIC::get,
                        () -> Blocks.TURBINE_ROTORSHAFT_REINFORCED::get);

        public static final RegistryObject<TileEntityType<TurbineRotorComponentEntity>> TURBINE_ROTORBLADE =
                registerBlockEntity("turbinerotorblade", TurbineRotorComponentEntity::blade,
                        () -> Blocks.TURBINE_ROTORBLADE_BASIC::get,
                        () -> Blocks.TURBINE_ROTORBLADE_REINFORCED::get);

        public static final RegistryObject<TileEntityType<TurbineFluidPortEntity>> TURBINE_FLUIDPORT_FORGE_ACTIVE =
                registerBlockEntity("turbinefluidport_forge_active",
                        () -> new TurbineFluidPortEntity(FluidPortType.Forge, IoMode.Active, TileEntityTypes.TURBINE_FLUIDPORT_FORGE_ACTIVE.get()),
                        () -> Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC::get,
                        () -> Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED::get);

        public static final RegistryObject<TileEntityType<TurbineFluidPortEntity>> TURBINE_FLUIDPORT_FORGE_PASSIVE =
                registerBlockEntity("turbinefluidport_forge_passive",
                        () -> new TurbineFluidPortEntity(FluidPortType.Forge, IoMode.Passive, TileEntityTypes.TURBINE_FLUIDPORT_FORGE_PASSIVE.get()),
                        () -> Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC::get,
                        () -> Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED::get);

        public static final RegistryObject<TileEntityType<TurbineCreativeSteamGenerator>> TURBINE_CREATIVE_STEAM_GENERATOR =
                registerBlockEntity("turbinecreativesteamgenerator",
                        TurbineCreativeSteamGenerator::new,
                        () -> Blocks.TURBINE_CREATIVE_STEAM_GENERATOR_BASIC::get);

        public static final RegistryObject<TileEntityType<TurbinePowerTapEntity>> TURBINE_POWERTAP_FE_ACTIVE =
                registerBlockEntity("turbinepowertap_fe_active",
                        () -> new TurbinePowerTapEntity(EnergySystem.ForgeEnergy, IoMode.Active, TileEntityTypes.TURBINE_POWERTAP_FE_ACTIVE.get()),
                        () -> Blocks.TURBINE_POWERTAP_FE_ACTIVE_BASIC::get,
                        () -> Blocks.TURBINE_POWERTAP_FE_ACTIVE_REINFORCED::get);

        public static final RegistryObject<TileEntityType<TurbinePowerTapEntity>> TURBINE_POWERTAP_FE_PASSIVE =
                registerBlockEntity("turbinepowertap_fe_passive",
                        () -> new TurbinePowerTapEntity(EnergySystem.ForgeEnergy, IoMode.Passive, TileEntityTypes.TURBINE_POWERTAP_FE_PASSIVE.get()),
                        () -> Blocks.TURBINE_POWERTAP_FE_PASSIVE_BASIC::get,
                        () -> Blocks.TURBINE_POWERTAP_FE_PASSIVE_REINFORCED::get);

        public static final RegistryObject<TileEntityType<TurbineComputerPortEntity>> TURBINE_COMPUTERPORT =
                registerBlockEntity("turbinecomputerport", TurbineComputerPortEntity::new,
                        () -> Blocks.TURBINE_COMPUTERPORT_REINFORCED::get);

        public static final RegistryObject<TileEntityType<TurbineRedstonePortEntity>> TURBINE_REDSTONEPORT =
                registerBlockEntity("turbineredstoneport", TurbineRedstonePortEntity::new,
                        () -> Blocks.TURBINE_REDSTONEPORT_BASIC::get,
                        () -> Blocks.TURBINE_REDSTONEPORT_REINFORCED::get);

        public static final RegistryObject<TileEntityType<TurbineChargingPortEntity>> TURBINE_CHARGINGPORT_FE =
                registerBlockEntity("turbinechargingport_fe",
                        () -> new TurbineChargingPortEntity(EnergySystem.ForgeEnergy, TileEntityTypes.TURBINE_CHARGINGPORT_FE.get()),
                        () -> Blocks.TURBINE_CHARGINGPORT_FE_BASIC::get,
                        () -> Blocks.TURBINE_CHARGINGPORT_FE_REINFORCED::get);

        //endregion
        //region reprocessor

        public static final RegistryObject<TileEntityType<ReprocessorCasingEntity>> REPROCESSOR_CASING =
                registerBlockEntity("reprocessorcasing", ReprocessorCasingEntity::new, () -> Blocks.REPROCESSOR_CASING::get);

        public static final RegistryObject<TileEntityType<ReprocessorGlassEntity>> REPROCESSOR_GLASS =
                registerBlockEntity("reprocessorglass", ReprocessorGlassEntity::new, () -> Blocks.REPROCESSOR_GLASS::get);

        public static final RegistryObject<TileEntityType<ReprocessorControllerEntity>> REPROCESSOR_CONTROLLER =
                registerBlockEntity("reprocessorcontroller", ReprocessorControllerEntity::new, () -> Blocks.REPROCESSOR_CONTROLLER::get);

        public static final RegistryObject<TileEntityType<ReprocessorAccessPortEntity>> REPROCESSOR_WASTEINJECTOR =
                registerBlockEntity("reprocessorwasteinjector",
                        () -> new ReprocessorAccessPortEntity(TileEntityTypes.REPROCESSOR_WASTEINJECTOR.get(), IoDirection.Input),
                        () -> Blocks.REPROCESSOR_WASTEINJECTOR::get);

        public static final RegistryObject<TileEntityType<ReprocessorFluidPortEntity>> REPROCESSOR_FLUIDINJECTOR =
                registerBlockEntity("reprocessorfluidinjector", ReprocessorFluidPortEntity::new, () -> Blocks.REPROCESSOR_FLUIDINJECTOR::get);

        public static final RegistryObject<TileEntityType<ReprocessorAccessPortEntity>> REPROCESSOR_OUTPUTPORT =
                registerBlockEntity("reprocessoroutputport",
                        () -> new ReprocessorAccessPortEntity(TileEntityTypes.REPROCESSOR_OUTPUTPORT.get(), IoDirection.Output),
                        () -> Blocks.REPROCESSOR_OUTPUTPORT::get);

        public static final RegistryObject<TileEntityType<ReprocessorPowerPortEntity>> REPROCESSOR_POWERPORT =
                registerBlockEntity("reprocessorpowerport", ReprocessorPowerPortEntity::new, () -> Blocks.REPROCESSOR_POWERPORT::get);

        public static final RegistryObject<TileEntityType<ReprocessorCollectorEntity>> REPROCESSOR_COLLECTOR =
                registerBlockEntity("reprocessorcollector", ReprocessorCollectorEntity::new, () -> Blocks.REPROCESSOR_COLLECTOR::get);

        //endregion
        //region fluidizer

        public static final RegistryObject<TileEntityType<FluidizerCasingEntity>> FLUIDIZER_CASING =
                registerBlockEntity("fluidizercasing", FluidizerCasingEntity::new, () -> Blocks.FLUIDIZER_CASING::get);

        public static final RegistryObject<TileEntityType<FluidizerGlassEntity>> FLUIDIZER_GLASS =
                registerBlockEntity("fluidizerglass", FluidizerGlassEntity::new, () -> Blocks.FLUIDIZER_GLASS::get);

        public static final RegistryObject<TileEntityType<FluidizerControllerEntity>> FLUIDIZER_CONTROLLER =
                registerBlockEntity("fluidizercontroller", FluidizerControllerEntity::new, () -> Blocks.FLUIDIZER_CONTROLLER::get);

        public static final RegistryObject<TileEntityType<FluidizerSolidInjectorEntity>> FLUIDIZER_SOLIDINJECTOR =
                registerBlockEntity("fluidizersolidinjector", FluidizerSolidInjectorEntity::new, () -> Blocks.FLUIDIZER_SOLIDINJECTOR::get);

        public static final RegistryObject<TileEntityType<FluidizerFluidInjectorEntity>> FLUIDIZER_FLUIDINJECTOR =
                registerBlockEntity("fluidizefluidinjector", FluidizerFluidInjectorEntity::new, () -> Blocks.FLUIDIZER_FLUIDINJECTOR::get);

        public static final RegistryObject<TileEntityType<FluidizerOutputPortEntity>> FLUIDIZER_OUTPUTPORT =
                registerBlockEntity("fluidizeoutputport", FluidizerOutputPortEntity::new, () -> Blocks.FLUIDIZER_OUTPUTPORT::get);

        public static final RegistryObject<TileEntityType<FluidizerPowerPortEntity>> FLUIDIZER_POWERPORT =
                registerBlockEntity("fluidizerpowerport", FluidizerPowerPortEntity::new, () -> Blocks.FLUIDIZER_POWERPORT::get);

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

                return TileEntityType.Builder.of(factory, validBlocks).build(null);
            });
        }

        //endregion
    }

    public static final class ContainerTypes {

        private static final DeferredRegister<ContainerType<?>> CONTAINERS =
                DeferredRegister.create(ForgeRegistries.CONTAINERS, ExtremeReactors.MOD_ID);

        static void initialize(final IEventBus bus) {
            CONTAINERS.register(bus);
        }

        //region Reactor

        public static final RegistryObject<ContainerType<ModTileContainer<ReactorControllerEntity>>> REACTOR_CONTROLLER =
                registerContainer("reactorcontroller", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.REACTOR_CONTROLLER.get(), windowId, data));

        public static final RegistryObject<ContainerType<ReactorSolidAccessPortContainer>> REACTOR_SOLID_ACCESSPORT =
                registerContainer("reactoraccessport", ReactorSolidAccessPortContainer::new);

        public static final RegistryObject<ContainerType<ModTileContainer<ReactorFluidAccessPortEntity>>> REACTOR_FLUID_ACCESSPORT =
                registerContainer("reactoraccessportfluid", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.REACTOR_FLUID_ACCESSPORT.get(), windowId, data));

        public static final RegistryObject<ContainerType<ModTileContainer<ReactorRedstonePortEntity>>> REACTOR_REDSTONEPORT =
                registerContainer("reactorredstoneport", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.REACTOR_REDSTONEPORT.get(), windowId, data));

        public static final RegistryObject<ContainerType<ModTileContainer<ReactorControlRodEntity>>> REACTOR_CONTROLROD =
                registerContainer("reactorcontrolrod", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.REACTOR_CONTROLROD.get(), windowId, data));

        public static final RegistryObject<ContainerType<ChargingPortContainer<ReactorChargingPortEntity>>> REACTOR_CHARGINGPORT =
                registerContainer("reactorchargingport",
                        (windowId, inv, data) -> new ChargingPortContainer<>(windowId, Content.ContainerTypes.REACTOR_CHARGINGPORT.get(), inv, data));

        public static final RegistryObject<ContainerType<ModTileContainer<ReactorFluidPortEntity>>> REACTOR_FLUIDPORT =
                registerContainer("reactorfluidport", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.REACTOR_FLUIDPORT.get(), windowId, data));

        //endregion
        //region Turbine

        public static final RegistryObject<ContainerType<ModTileContainer<TurbineControllerEntity>>> TURBINE_CONTROLLER =
                registerContainer("turbinecontroller", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.TURBINE_CONTROLLER.get(), windowId, data));

        public static final RegistryObject<ContainerType<ModTileContainer<TurbineRedstonePortEntity>>> TURBINE_REDSTONEPORT =
                registerContainer("turbineredstoneport", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.TURBINE_REDSTONEPORT.get(), windowId, data));

        public static final RegistryObject<ContainerType<ChargingPortContainer<TurbineChargingPortEntity>>> TURBINE_CHARGINGPORT =
                registerContainer("turbinechargingport",
                        (windowId, inv, data) -> new ChargingPortContainer<>(windowId, Content.ContainerTypes.TURBINE_CHARGINGPORT.get(), inv, data));

        public static final RegistryObject<ContainerType<ModTileContainer<TurbineFluidPortEntity>>> TURBINE_FLUIDPORT =
                registerContainer("turbinefluidport", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.TURBINE_FLUIDPORT.get(), windowId, data));

        //endregion
        //region Reprocessor

        public static final RegistryObject<ContainerType<ModTileContainer<ReprocessorControllerEntity>>> REPROCESSOR_CONTROLLER =
                registerContainer("reprocessorcontroller", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.REPROCESSOR_CONTROLLER.get(), windowId, data));

//        public static final RegistryObject<ContainerType<ReprocessorControllerContainer>> REPROCESSOR_CONTROLLER =
//                registerContainer("reprocessorcontroller", ReprocessorControllerContainer::new);

        public static final RegistryObject<ContainerType<ReprocessorAccessPortContainer>> REPROCESSOR_ACCESSPORT =
                registerContainer("reprocessoraccessport", ReprocessorAccessPortContainer::new);

        //endregion
        //region internals

        private static <C extends Container> RegistryObject<ContainerType<C>> registerContainer(final String name,
                                                                                                final IContainerFactory<C> factory) {
            return CONTAINERS.register(name,
                    () -> IForgeContainerType.create(factory));
        }

        //endregion
    }

    public static final class Recipes {

        private static final DeferredRegister<IRecipeSerializer<?>> SERIALIZERS =
                DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ExtremeReactors.MOD_ID);

        static void initialize(final IEventBus bus) {
            SERIALIZERS.register(bus);
        }

        //region Reprocessor

        public static final ModRecipeType<ReprocessorRecipe> REPROCESSOR_RECIPE_TYPE = ModRecipeType.create(ReprocessorRecipe.ID);

        public static final RegistryObject<IRecipeSerializer<ReprocessorRecipe>> REPROCESSOR_RECIPE_SERIALIZER =
                SERIALIZERS.register(ReprocessorRecipe.NAME, ReprocessorRecipe::serializer);

        //endregion
        //region Fluidizer

        public static final ModRecipeType<ModRecipe> FLUIDIZER_RECIPE_TYPE =
                ModRecipeType.create(ExtremeReactors.newID("fluidizer"));

        public static final RegistryObject<IRecipeSerializer<FluidizerSolidRecipe>> FLUIDIZER_SOLID_RECIPE_SERIALIZER =
                SERIALIZERS.register(FluidizerSolidRecipe.NAME, FluidizerSolidRecipe::serializer);
        public static final RegistryObject<IRecipeSerializer<FluidizerSolidMixingRecipe>> FLUIDIZER_SOLIDMIXING_RECIPE_SERIALIZER =
                SERIALIZERS.register(FluidizerSolidMixingRecipe.NAME, FluidizerSolidMixingRecipe::serializer);
        public static final RegistryObject<IRecipeSerializer<FluidizerFluidMixingRecipe>> FLUIDIZER_FLUIDMIXING_RECIPE_SERIALIZER =
                SERIALIZERS.register(FluidizerFluidMixingRecipe.NAME, FluidizerFluidMixingRecipe::serializer);

        //endregion
    }

    //region internals

    private static void onCommonInit(final FMLCommonSetupEvent event) {

        ReactorGameData.register();
        TurbineGameData.register();
    }

    //endregion
}
