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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.extremereactors.gamecontent.fluid.ReactantFluidBlock;
import it.zerono.mods.extremereactors.gamecontent.fluid.ReactorFluidType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.container.ChargingPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.fluidport.FluidPortType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.FluidizerPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.MultiblockFluidizer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.container.FluidizerControllerContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.container.FluidizerSolidInjectorContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part.*;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerFluidMixingRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerSolidMixingRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerSolidRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.IFluidizerRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.Reactants;
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
import it.zerono.mods.extremereactors.loader.forge.gamecontent.worldgen.OreBiomeModifier;
import it.zerono.mods.zerocore.base.multiblock.part.GenericDeviceBlock;
import it.zerono.mods.zerocore.base.multiblock.part.GlassBlock;
import it.zerono.mods.zerocore.base.multiblock.part.io.IOPortBlock;
import it.zerono.mods.zerocore.lib.block.ModBlock;
import it.zerono.mods.zerocore.lib.block.ModOreBlock;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.fluid.SimpleFluidTypeRenderProperties;
import it.zerono.mods.zerocore.lib.item.ModItem;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.recipe.ModRecipe;
import it.zerono.mods.zerocore.lib.recipe.ModRecipeType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Consumer;
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
        Biomes.initialize(bus);
        CreativeTabs.initialize(bus);

        bus.addListener(Content::onCommonInit);
    }

    public static final class Blocks {

        private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ExtremeReactors.MOD_ID);

        static void initialize(final IEventBus bus) {
            BLOCKS.register(bus);
        }

        public static Collection<RegistryObject<Block>> getAll() {
            return BLOCKS.getEntries();
        }

        //region metals

        public static final RegistryObject<ModBlock> YELLORIUM_BLOCK = registerMetalBlock(Reactants.Yellorium.getBlockName(), DyeColor.YELLOW);
        public static final RegistryObject<ModBlock> CYANITE_BLOCK = registerMetalBlock(Reactants.Cyanite.getBlockName(), DyeColor.LIGHT_BLUE);
        public static final RegistryObject<ModBlock> GRAPHITE_BLOCK = registerMetalBlock("graphite_block", DyeColor.GRAY);
        public static final RegistryObject<ModBlock> BLUTONIUM_BLOCK = registerMetalBlock(Reactants.Blutonium.getBlockName(), DyeColor.PURPLE);
        public static final RegistryObject<ModBlock> MAGENTITE_BLOCK = registerMetalBlock(Reactants.Magentite.getBlockName(), DyeColor.MAGENTA);

        public static final RegistryObject<ModBlock> LUDICRITE_BLOCK = registerMetalBlock("ludicrite_block", DyeColor.PURPLE);
        public static final RegistryObject<ModBlock> RIDICULITE_BLOCK = registerMetalBlock("ridiculite_block", DyeColor.PINK);
        public static final RegistryObject<ModBlock> INANITE_BLOCK = registerMetalBlock("inanite_block", DyeColor.RED);

        //endregion
        //region ores

        public static final RegistryObject<ModBlock> YELLORITE_ORE_BLOCK = registerOreBlock("yellorite_ore", DyeColor.YELLOW, 0, 0);
        public static final RegistryObject<ModBlock> ANGLESITE_ORE_BLOCK = registerOreBlock("anglesite_ore", DyeColor.ORANGE);
        public static final RegistryObject<ModBlock> BENITOITE_ORE_BLOCK = registerOreBlock("benitoite_ore", DyeColor.LIGHT_BLUE);

        //endregion
        //region fluids

        public static final RegistryObject<LiquidBlock> STEAM = BLOCKS.register("steam",
                () -> new LiquidBlock(Fluids.STEAM_SOURCE,
                        Block.Properties.of()
                                .mapColor(MapColor.WATER)
                                .replaceable()
                                .pushReaction(PushReaction.DESTROY)
                                .liquid()
                                .noCollission()
                                .lightLevel($ -> 6)
                                .strength(100.0F)
                                .noLootTable()
                ));

        //region reactants

        public static final RegistryObject<ReactantFluidBlock> YELLORIUM_FLUID = registerReactantFluidBlock(Reactants.Yellorium, Fluids.YELLORIUM_SOURCE);

        public static final RegistryObject<ReactantFluidBlock> CYANITE_FLUID = registerReactantFluidBlock(Reactants.Cyanite, Fluids.CYANITE_SOURCE);

        public static final RegistryObject<ReactantFluidBlock> BLUTONIUM_FLUID = registerReactantFluidBlock(Reactants.Blutonium, Fluids.BLUTONIUM_SOURCE);

        public static final RegistryObject<ReactantFluidBlock> MAGENTITE_FLUID = registerReactantFluidBlock(Reactants.Magentite, Fluids.MAGENTITE_SOURCE);

        public static final RegistryObject<ReactantFluidBlock> VERDERIUM_FLUID = registerReactantFluidBlock(Reactants.Verderium, Fluids.VERDERIUM_SOURCE);

        public static final RegistryObject<ReactantFluidBlock> ROSSINITE_FLUID = registerReactantFluidBlock(Reactants.Rossinite, Fluids.ROSSINITE_SOURCE);

        //endregion
        //region moderators

        public static final RegistryObject<LiquidBlock> CRYOMISI_FLUID = registerModeratorLiquidBlock("cryomisi_fluid", Fluids.CRYOMISI_SOURCE);

        public static final RegistryObject<LiquidBlock> TANGERIUM_FLUID = registerModeratorLiquidBlock("tangerium_fluid", Fluids.TANGERIUM_SOURCE);

        public static final RegistryObject<LiquidBlock> REDFRIGIUM_FLUID = registerModeratorLiquidBlock("redfrigium_fluid", Fluids.REDFRIGIUM_SOURCE);

        //endregion
        //endregion
        //region reactor basic

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
        //region reactor reinforced

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
        //region turbine basic

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
        //region turbine reinforced

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
                    () -> new ModBlock(Block.Properties.of().mapColor(color).sound(SoundType.METAL)));
        }

        private static RegistryObject<ModBlock> registerOreBlock(final String name, final DyeColor color) {
            return registerOreBlock(name, color, 3, 5);
        }

        private static RegistryObject<ModBlock> registerOreBlock(final String name, final DyeColor color,
                                                                 final int minDroppedXP, final int maxDroppedXP) {
            return BLOCKS.register(name,
                    () -> new ModOreBlock(Block.Properties.of()
                            .mapColor(color)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops()
                            .instrument(NoteBlockInstrument.BASEDRUM)
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

        private static RegistryObject<LiquidBlock> registerModeratorLiquidBlock(final String name, final Supplier<FlowingFluid> source) {
            return BLOCKS.register(name, () -> new LiquidBlock(source, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WATER)
                    .replaceable()
                    .pushReaction(PushReaction.DESTROY)
                    .liquid()
                    .noCollission()
                    .strength(100.0F)
                    .noLootTable()));
        }

        private static RegistryObject<ReactantFluidBlock> registerReactantFluidBlock(final Reactants reactant,
                                                                                     final Supplier<? extends FlowingFluid> fluid) {
            return BLOCKS.register(reactant.getFluidName(), () -> new ReactantFluidBlock(reactant, fluid));
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

        public static final RegistryObject<ModItem> YELLORIUM_INGOT = registerItemGeneric(Reactants.Yellorium.getIngotName());
        public static final RegistryObject<ModItem> CYANITE_INGOT = registerItemGeneric(Reactants.Cyanite.getIngotName());
        public static final RegistryObject<ModItem> GRAPHITE_INGOT = registerItemGeneric("graphite_ingot");
        public static final RegistryObject<ModItem> BLUTONIUM_INGOT = registerItemGeneric(Reactants.Blutonium.getIngotName());
        public static final RegistryObject<ModItem> MAGENTITE_INGOT = registerItemGeneric(Reactants.Magentite.getIngotName());
        public static final RegistryObject<ModItem> LUDICRITE_INGOT = registerItemGeneric("ludicrite_ingot");
        public static final RegistryObject<ModItem> RIDICULITE_INGOT = registerItemGeneric("ridiculite_ingot");
        public static final RegistryObject<ModItem> INANITE_INGOT = registerItemGeneric("inanite_ingot");

        public static final RegistryObject<ModItem> YELLORIUM_NUGGET = registerItemGeneric(Reactants.Yellorium.getNuggetName());
        public static final RegistryObject<ModItem> BLUTONIUM_NUGGET = registerItemGeneric(Reactants.Blutonium.getNuggetName());

        public static final RegistryObject<ModItem> YELLORIUM_DUST = registerItemGeneric(Reactants.Yellorium.getDustName());
        public static final RegistryObject<ModItem> CYANITE_DUST = registerItemGeneric(Reactants.Cyanite.getDustName());
        public static final RegistryObject<ModItem> GRAPHITE_DUST = registerItemGeneric("graphite_dust");
        public static final RegistryObject<ModItem> BLUTONIUM_DUST = registerItemGeneric(Reactants.Blutonium.getDustName());
        public static final RegistryObject<ModItem> MAGENTITE_DUST = registerItemGeneric(Reactants.Magentite.getDustName());
        public static final RegistryObject<ModItem> LUDICRITE_DUST = registerItemGeneric("ludicrite_dust");
        public static final RegistryObject<ModItem> RIDICULITE_DUST = registerItemGeneric("ridiculite_dust");
        public static final RegistryObject<ModItem> INANITE_DUST = registerItemGeneric("inanite_dust");

        public static final RegistryObject<BlockItem> YELLORIUM_BLOCK = registerItemBlock(Reactants.Yellorium.getBlockName(), () -> Blocks.YELLORIUM_BLOCK);
        public static final RegistryObject<BlockItem> CYANITE_BLOCK = registerItemBlock(Reactants.Cyanite.getBlockName(), () -> Blocks.CYANITE_BLOCK);
        public static final RegistryObject<BlockItem> GRAPHITE_BLOCK = registerItemBlock("graphite_block", () -> Blocks.GRAPHITE_BLOCK);
        public static final RegistryObject<BlockItem> BLUTONIUM_BLOCK = registerItemBlock(Reactants.Blutonium.getBlockName(), () -> Blocks.BLUTONIUM_BLOCK);
        public static final RegistryObject<BlockItem> MAGENTITE_BLOCK = registerItemBlock(Reactants.Magentite.getBlockName(), () -> Blocks.MAGENTITE_BLOCK);
        public static final RegistryObject<BlockItem> LUDICRITE_BLOCK = registerItemBlock("ludicrite_block", () -> Blocks.LUDICRITE_BLOCK);
        public static final RegistryObject<BlockItem> RIDICULITE_BLOCK = registerItemBlock("ridiculite_block", () -> Blocks.RIDICULITE_BLOCK);
        public static final RegistryObject<BlockItem> INANITE_BLOCK = registerItemBlock("inanite_block", () -> Blocks.INANITE_BLOCK);

        //endregion
        //region crystals

        public static final RegistryObject<ModItem> ANGLESITE_CRYSTAL = registerItemGeneric("anglesite_crystal");
        public static final RegistryObject<ModItem> BENITOITE_CRYSTAL = registerItemGeneric("benitoite_crystal");

        //endregion
        //region ores

        public static final RegistryObject<BlockItem> YELLORITE_ORE_BLOCK = registerItemBlock("yellorite_ore", () -> Blocks.YELLORITE_ORE_BLOCK);
        public static final RegistryObject<BlockItem> ANGLESITE_ORE_BLOCK = registerItemBlock("anglesite_ore", () -> Blocks.ANGLESITE_ORE_BLOCK);
        public static final RegistryObject<BlockItem> BENITOITE_ORE_BLOCK = registerItemBlock("benitoite_ore", () -> Blocks.BENITOITE_ORE_BLOCK);

        //endregion
        //region fluids

        public static final RegistryObject<BucketItem> STEAM_BUCKET = registerBucket("steam_bucket", Fluids.STEAM_SOURCE);

        //region reactants

        public static final RegistryObject<BucketItem> YELLORIUM_BUCKET = registerBucket(Reactants.Yellorium.getBucketName(), Fluids.YELLORIUM_SOURCE);
        public static final RegistryObject<BucketItem> CYANITE_BUCKET = registerBucket(Reactants.Cyanite.getBucketName(), Fluids.CYANITE_SOURCE);
        public static final RegistryObject<BucketItem> BLUTONIUM_BUCKET = registerBucket(Reactants.Blutonium.getBucketName(), Fluids.BLUTONIUM_SOURCE);
        public static final RegistryObject<BucketItem> MAGENTITE_BUCKET = registerBucket(Reactants.Magentite.getBucketName(), Fluids.MAGENTITE_SOURCE);
        public static final RegistryObject<BucketItem> VERDERIUM_BUCKET = registerBucket(Reactants.Verderium.getBucketName(), Fluids.VERDERIUM_SOURCE);
        public static final RegistryObject<BucketItem> ROSSINITE_BUCKET = registerBucket(Reactants.Rossinite.getBucketName(), Fluids.ROSSINITE_SOURCE);

        //endregion
        //region moderators

        public static final RegistryObject<BucketItem> CRYOMISI_BUCKET = registerBucket("cryomisi_bucket", Fluids.CRYOMISI_SOURCE);

        public static final RegistryObject<BucketItem> TANGERIUM_BUCKET = registerBucket("tangerium_bucket", Fluids.TANGERIUM_SOURCE);

        public static final RegistryObject<BucketItem> REDFRIGIUM_BUCKET = registerBucket("redfrigium_bucket", Fluids.REDFRIGIUM_SOURCE);

        //endregion
        //endregion
        //region reactor basic

        public static final RegistryObject<BlockItem> REACTOR_CASING_BASIC = registerItemBlock("basic_reactorcasing", () -> Blocks.REACTOR_CASING_BASIC::get);
        public static final RegistryObject<BlockItem> REACTOR_GLASS_BASIC = registerItemBlock("basic_reactorglass", () -> Blocks.REACTOR_GLASS_BASIC::get);
        public static final RegistryObject<BlockItem> REACTOR_CONTROLLER_BASIC = registerItemBlock("basic_reactorcontroller", () -> Blocks.REACTOR_CONTROLLER_BASIC::get);
        public static final RegistryObject<BlockItem> REACTOR_FUELROD_BASIC = registerItemBlock("basic_reactorfuelrod", () -> Blocks.REACTOR_FUELROD_BASIC::get);
        public static final RegistryObject<BlockItem> REACTOR_CONTROLROD_BASIC = registerItemBlock("basic_reactorcontrolrod", () -> Blocks.REACTOR_CONTROLROD_BASIC::get);
        public static final RegistryObject<BlockItem> REACTOR_SOLID_ACCESSPORT_BASIC = registerItemBlock("basic_reactorsolidaccessport", () -> Blocks.REACTOR_SOLID_ACCESSPORT_BASIC::get);
        public static final RegistryObject<BlockItem> REACTOR_POWERTAP_FE_ACTIVE_BASIC = registerItemBlock("basic_reactorpowertapfe_active", () -> Blocks.REACTOR_POWERTAP_FE_ACTIVE_BASIC::get);
        public static final RegistryObject<BlockItem> REACTOR_POWERTAP_FE_PASSIVE_BASIC = registerItemBlock("basic_reactorpowertapfe_passive", () -> Blocks.REACTOR_POWERTAP_FE_PASSIVE_BASIC::get);
        public static final RegistryObject<BlockItem> REACTOR_REDSTONEPORT_BASIC = registerItemBlock("basic_reactorredstoneport", () -> Blocks.REACTOR_REDSTONEPORT_BASIC::get);
        public static final RegistryObject<BlockItem> REACTOR_CHARGINGPORT_FE_BASIC = registerItemBlock("basic_reactorchargingportfe", () -> Blocks.REACTOR_CHARGINGPORT_FE_BASIC::get);

        //endregion
        //region reactor reinforced

        public static final RegistryObject<BlockItem> REACTOR_CASING_REINFORCED = registerItemBlock("reinforced_reactorcasing", () -> Blocks.REACTOR_CASING_REINFORCED::get);
        public static final RegistryObject<BlockItem> REACTOR_GLASS_REINFORCED = registerItemBlock("reinforced_reactorglass", () -> Blocks.REACTOR_GLASS_REINFORCED::get);
        public static final RegistryObject<BlockItem> REACTOR_CONTROLLER_REINFORCED = registerItemBlock("reinforced_reactorcontroller", () -> Blocks.REACTOR_CONTROLLER_REINFORCED::get);
        public static final RegistryObject<BlockItem> REACTOR_FUELROD_REINFORCED = registerItemBlock("reinforced_reactorfuelrod", () -> Blocks.REACTOR_FUELROD_REINFORCED::get);
        public static final RegistryObject<BlockItem> REACTOR_CONTROLROD_REINFORCED = registerItemBlock("reinforced_reactorcontrolrod", () -> Blocks.REACTOR_CONTROLROD_REINFORCED::get);
        public static final RegistryObject<BlockItem> REACTOR_SOLID_ACCESSPORT_REINFORCED = registerItemBlock("reinforced_reactorsolidaccessport", () -> Blocks.REACTOR_SOLID_ACCESSPORT_REINFORCED::get);
        public static final RegistryObject<BlockItem> REACTOR_FLUID_ACCESSPORT_REINFORCED = registerItemBlock("reinforced_reactorfluidaccessport", () -> Blocks.REACTOR_FLUID_ACCESSPORT_REINFORCED::get);
        public static final RegistryObject<BlockItem> REACTOR_POWERTAP_FE_ACTIVE_REINFORCED = registerItemBlock("reinforced_reactorpowertapfe_active", () -> Blocks.REACTOR_POWERTAP_FE_ACTIVE_REINFORCED::get);
        public static final RegistryObject<BlockItem> REACTOR_POWERTAP_FE_PASSIVE_REINFORCED = registerItemBlock("reinforced_reactorpowertapfe_passive", () -> Blocks.REACTOR_POWERTAP_FE_PASSIVE_REINFORCED::get);
        public static final RegistryObject<BlockItem> REACTOR_COMPUTERPORT_REINFORCED = registerItemBlock("reinforced_reactorcomputerport", () -> Blocks.REACTOR_COMPUTERPORT_REINFORCED::get);
        public static final RegistryObject<BlockItem> REACTOR_REDSTONEPORT_REINFORCED = registerItemBlock("reinforced_reactorredstoneport", () -> Blocks.REACTOR_REDSTONEPORT_REINFORCED::get);
        public static final RegistryObject<BlockItem> REACTOR_FLUIDPORT_FORGE_ACTIVE_REINFORCED = registerItemBlock("reinforced_reactorfluidport_forge_active", () -> Blocks.REACTOR_FLUIDTPORT_FORGE_ACTIVE_REINFORCED::get);
        public static final RegistryObject<BlockItem> REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED = registerItemBlock("reinforced_reactorfluidport_forge_passive", () -> Blocks.REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED::get);
        public static final RegistryObject<BlockItem> REACTOR_FLUIDPORT_MEKANISM_PASSIVE_REINFORCED = registerItemBlock("reinforced_reactorfluidport_mekanism_passive", () -> Blocks.REACTOR_FLUIDPORT_MEKANISM_PASSIVE_REINFORCED::get);
        public static final RegistryObject<BlockItem> REACTOR_CREATIVE_WATER_GENERATOR_REINFORCED = registerItemBlock("reinforced_reactorcreativewatergenerator", () -> Blocks.REACTOR_CREATIVE_WATER_GENERATOR_REINFORCED::get);
        public static final RegistryObject<BlockItem> REACTOR_CHARGINGPORT_FE_REINFORCED = registerItemBlock("reinforced_reactorchargingportfe", () -> Blocks.REACTOR_CHARGINGPORT_FE_REINFORCED::get);

        //endregion
        //region turbine basic

        public static final RegistryObject<BlockItem> TURBINE_CASING_BASIC = registerItemBlock("basic_turbinecasing", () -> Blocks.TURBINE_CASING_BASIC::get);
        public static final RegistryObject<BlockItem> TURBINE_GLASS_BASIC = registerItemBlock("basic_turbineglass", () -> Blocks.TURBINE_GLASS_BASIC::get);
        public static final RegistryObject<BlockItem> TURBINE_CONTROLLER_BASIC = registerItemBlock("basic_turbinecontroller", () -> Blocks.TURBINE_CONTROLLER_BASIC::get);
        public static final RegistryObject<BlockItem> TURBINE_ROTORBEARING_BASIC = registerItemBlock("basic_turbinerotorbearing", () -> Blocks.TURBINE_ROTORBEARING_BASIC::get);
        public static final RegistryObject<BlockItem> TURBINE_ROTORSHAFT_BASIC = registerItemBlock("basic_turbinerotorshaft", () -> Blocks.TURBINE_ROTORSHAFT_BASIC::get);
        public static final RegistryObject<BlockItem> TURBINE_ROTORBLADE_BASIC = registerItemBlock("basic_turbinerotorblade", () -> Blocks.TURBINE_ROTORBLADE_BASIC::get);
        public static final RegistryObject<BlockItem> TURBINE_POWERTAP_FE_ACTIVE_BASIC = registerItemBlock("basic_turbinepowertapfe_active", () -> Blocks.TURBINE_POWERTAP_FE_ACTIVE_BASIC::get);
        public static final RegistryObject<BlockItem> TURBINE_POWERTAP_FE_PASSIVE_BASIC = registerItemBlock("basic_turbinepowertapfe_passive", () -> Blocks.TURBINE_POWERTAP_FE_PASSIVE_BASIC::get);
        public static final RegistryObject<BlockItem> TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC = registerItemBlock("basic_turbinefluidport_forge_active", () -> Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC::get);
        public static final RegistryObject<BlockItem> TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC = registerItemBlock("basic_turbinefluidport_forge_passive", () -> Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC::get);
        public static final RegistryObject<BlockItem> TURBINE_REDSTONEPORT_BASIC = registerItemBlock("basic_turbineredstoneport", () -> Blocks.TURBINE_REDSTONEPORT_BASIC::get);
        public static final RegistryObject<BlockItem> TURBINE_CREATIVE_STEAM_GENERATOR_BASIC = registerItemBlock("basic_turbinecreativesteamgenerator", () -> Blocks.TURBINE_CREATIVE_STEAM_GENERATOR_BASIC::get);
        public static final RegistryObject<BlockItem> TURBINE_CHARGINGPORT_FE_BASIC = registerItemBlock("basic_turbinechargingportfe", () -> Blocks.TURBINE_CHARGINGPORT_FE_BASIC::get);

        //endregion
        //region turbine reinforced

        public static final RegistryObject<BlockItem> TURBINE_CASING_REINFORCED = registerItemBlock("reinforced_turbinecasing", () -> Blocks.TURBINE_CASING_REINFORCED::get);
        public static final RegistryObject<BlockItem> TURBINE_GLASS_REINFORCED = registerItemBlock("reinforced_turbineglass", () -> Blocks.TURBINE_GLASS_REINFORCED::get);
        public static final RegistryObject<BlockItem> TURBINE_CONTROLLER_REINFORCED = registerItemBlock("reinforced_turbinecontroller", () -> Blocks.TURBINE_CONTROLLER_REINFORCED::get);
        public static final RegistryObject<BlockItem> TURBINE_ROTORBEARING_REINFORCED = registerItemBlock("reinforced_turbinerotorbearing", () -> Blocks.TURBINE_ROTORBEARING_REINFORCED::get);
        public static final RegistryObject<BlockItem> TURBINE_ROTORSHAFT_REINFORCED = registerItemBlock("reinforced_turbinerotorshaft", () -> Blocks.TURBINE_ROTORSHAFT_REINFORCED::get);
        public static final RegistryObject<BlockItem> TURBINE_ROTORBLADE_REINFORCED = registerItemBlock("reinforced_turbinerotorblade", () -> Blocks.TURBINE_ROTORBLADE_REINFORCED::get);
        public static final RegistryObject<BlockItem> TURBINE_POWERTAP_FE_ACTIVE_REINFORCED = registerItemBlock("reinforced_turbinepowertapfe_active", () -> Blocks.TURBINE_POWERTAP_FE_ACTIVE_REINFORCED::get);
        public static final RegistryObject<BlockItem> TURBINE_POWERTAP_FE_PASSIVE_REINFORCED = registerItemBlock("reinforced_turbinepowertapfe_passive", () -> Blocks.TURBINE_POWERTAP_FE_PASSIVE_REINFORCED::get);
        public static final RegistryObject<BlockItem> TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED = registerItemBlock("reinforced_turbinefluidport_forge_active", () -> Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED::get);
        public static final RegistryObject<BlockItem> TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED = registerItemBlock("reinforced_turbinefluidport_forge_passive", () -> Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED::get);
        public static final RegistryObject<BlockItem> TURBINE_REDSTONEPORT_REINFORCED = registerItemBlock("reinforced_turbineredstoneport", () -> Blocks.TURBINE_REDSTONEPORT_REINFORCED::get);
        public static final RegistryObject<BlockItem> TURBINE_COMPUTERPORT_REINFORCED = registerItemBlock("reinforced_turbinecomputerport", () -> Blocks.TURBINE_COMPUTERPORT_REINFORCED::get);
        public static final RegistryObject<BlockItem> TURBINE_CREATIVE_STEAM_GENERATOR_REINFORCED = registerItemBlock("reinforced_turbinecreativesteamgenerator", () -> Blocks.TURBINE_CREATIVE_STEAM_GENERATOR_REINFORCED::get);
        public static final RegistryObject<BlockItem> TURBINE_CHARGINGPORT_FE_REINFORCED = registerItemBlock("reinforced_turbinechargingportfe", () -> Blocks.TURBINE_CHARGINGPORT_FE_REINFORCED::get);

        //endregion
        //region reprocessor

        public static final RegistryObject<BlockItem> REPROCESSOR_CASING = registerItemBlock("reprocessorcasing", () -> Blocks.REPROCESSOR_CASING::get);
        public static final RegistryObject<BlockItem> REPROCESSOR_GLASS = registerItemBlock("reprocessorglass", () -> Blocks.REPROCESSOR_GLASS::get);
        public static final RegistryObject<BlockItem> REPROCESSOR_CONTROLLER = registerItemBlock("reprocessorcontroller", () -> Blocks.REPROCESSOR_CONTROLLER::get);
        public static final RegistryObject<BlockItem> REPROCESSOR_WASTEINJECTOR = registerItemBlock("reprocessorwasteinjector", () -> Blocks.REPROCESSOR_WASTEINJECTOR::get);
        public static final RegistryObject<BlockItem> REPROCESSOR_FLUIDINJECTOR = registerItemBlock("reprocessorfluidinjector", () -> Blocks.REPROCESSOR_FLUIDINJECTOR::get);
        public static final RegistryObject<BlockItem> REPROCESSOR_OUTPUTPORT = registerItemBlock("reprocessoroutputport", () -> Blocks.REPROCESSOR_OUTPUTPORT::get);
        public static final RegistryObject<BlockItem> REPROCESSOR_POWERPORT = registerItemBlock("reprocessorpowerport", () -> Blocks.REPROCESSOR_POWERPORT::get);
        public static final RegistryObject<BlockItem> REPROCESSOR_COLLECTOR = registerItemBlock("reprocessorcollector", () -> Blocks.REPROCESSOR_COLLECTOR::get);

        //endregion
        //region fluidizer

        public static final RegistryObject<BlockItem> FLUIDIZER_CASING = registerItemBlock("fluidizercasing", () -> Blocks.FLUIDIZER_CASING::get);
        public static final RegistryObject<BlockItem> FLUIDIZER_GLASS = registerItemBlock("fluidizerglass", () -> Blocks.FLUIDIZER_GLASS::get);
        public static final RegistryObject<BlockItem> FLUIDIZER_CONTROLLER = registerItemBlock("fluidizercontroller", () -> Blocks.FLUIDIZER_CONTROLLER::get);
        public static final RegistryObject<BlockItem> FLUIDIZER_SOLIDINJECTOR = registerItemBlock("fluidizersolidinjector", () -> Blocks.FLUIDIZER_SOLIDINJECTOR::get);
        public static final RegistryObject<BlockItem> FLUIDIZER_FLUIDINJECTOR = registerItemBlock("fluidizerfluidinjector", () -> Blocks.FLUIDIZER_FLUIDINJECTOR::get);
        public static final RegistryObject<BlockItem> FLUIDIZER_OUTPUTPORT = registerItemBlock("fluidizeroutputport", () -> Blocks.FLUIDIZER_OUTPUTPORT::get);
        public static final RegistryObject<BlockItem> FLUIDIZER_POWERPORT = registerItemBlock("fluidizerpowerport", () -> Blocks.FLUIDIZER_POWERPORT::get);

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
                    () -> new ModItem(new Item.Properties().stacksTo(maxStack)));
        }

        private static RegistryObject<BlockItem> registerItemBlock(final String name,
                                                                   final Supplier<Supplier<ModBlock>> blockSupplier) {
            return ITEMS.register(name,
                    () -> blockSupplier.get().get().createBlockItem(new Item.Properties().stacksTo(64)));
        }

        private static RegistryObject<BucketItem> registerBucket(final String name, final Supplier<? extends Fluid> sourceFluid) {
            return ITEMS.register(name, () -> new BucketItem(sourceFluid, new Item.Properties()
                    .craftRemainder(net.minecraft.world.item.Items.BUCKET)
                    .stacksTo(1)));
        }

        //endregion
    }

    public static final class Fluids {

        private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, ExtremeReactors.MOD_ID);
        private static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, ExtremeReactors.MOD_ID);

        static void initialize(final IEventBus bus) {

            FLUIDS.register(bus);
            FLUID_TYPES.register(bus);
        }

        public static RegistryObject<FluidType> STEAM_FLUID_TYPE = FLUID_TYPES.register("steam",
                () -> new FluidType(FluidType.Properties.create()
                        .descriptionId("fluid." + ExtremeReactors.MOD_ID + ".steam")
                        .density(1)
                        .lightLevel(6)
                        .canDrown(false)
                        .canSwim(false)
                        .canPushEntity(false)
                        .fallDistanceModifier(0.1f)
                        .canExtinguish(false)
                        .canConvertToSource(false)
                        .supportsBoating(false)
                        .pathType(BlockPathTypes.WALKABLE)
                        .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                        .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                        .canHydrate(false)
                        .temperature(100)
                        .rarity(Rarity.COMMON)) {

                    @Override
                    public void initializeClient(final Consumer<IClientFluidTypeExtensions> consumer) {
                        consumer.accept(new SimpleFluidTypeRenderProperties(0xffffffff, CommonConstants.FLUID_TEXTURE_SOURCE_WATER,
                                CommonConstants.FLUID_TEXTURE_FLOWING_WATER, CommonConstants.FLUID_TEXTURE_OVERLAY_WATER));
                    }
                });

        public static final RegistryObject<FlowingFluid> STEAM_SOURCE = registerSteam("steam", ForgeFlowingFluid.Source::new);
        public static final RegistryObject<FlowingFluid> STEAM_FLOWING = registerSteam("steam_flowing", ForgeFlowingFluid.Flowing::new);

        //region reactants

        public static RegistryObject<FluidType> YELLORIUM_FLUID_TYPE = registerReactantFluidType(Reactants.Yellorium);

        public static final RegistryObject<FlowingFluid> YELLORIUM_SOURCE = FLUIDS.register(Reactants.Yellorium.getFluidSourceName(),
                () -> new ForgeFlowingFluid.Source(new ForgeFlowingFluid.Properties(YELLORIUM_FLUID_TYPE, Fluids.YELLORIUM_SOURCE, Fluids.YELLORIUM_FLOWING)
                        .bucket(Items.YELLORIUM_BUCKET)
                        .block(Blocks.YELLORIUM_FLUID)));

        public static final RegistryObject<FlowingFluid> YELLORIUM_FLOWING = FLUIDS.register(Reactants.Yellorium.getFluidFlowingName(),
                () -> new ForgeFlowingFluid.Flowing(new ForgeFlowingFluid.Properties(YELLORIUM_FLUID_TYPE, Fluids.YELLORIUM_SOURCE, Fluids.YELLORIUM_FLOWING)
                        .bucket(Items.YELLORIUM_BUCKET)
                        .block(Blocks.YELLORIUM_FLUID)));

        public static RegistryObject<FluidType> CYANITE_FLUID_TYPE = registerReactantFluidType(Reactants.Cyanite);

        public static final RegistryObject<FlowingFluid> CYANITE_SOURCE = FLUIDS.register(Reactants.Cyanite.getFluidSourceName(),
                () -> new ForgeFlowingFluid.Source(new ForgeFlowingFluid.Properties(CYANITE_FLUID_TYPE, Fluids.CYANITE_SOURCE, Fluids.CYANITE_FLOWING)
                        .bucket(Items.CYANITE_BUCKET)
                        .block(Blocks.CYANITE_FLUID)));

        public static final RegistryObject<FlowingFluid> CYANITE_FLOWING = FLUIDS.register(Reactants.Cyanite.getFluidFlowingName(),
                () -> new ForgeFlowingFluid.Flowing(new ForgeFlowingFluid.Properties(CYANITE_FLUID_TYPE, Fluids.CYANITE_SOURCE, Fluids.CYANITE_FLOWING)
                        .bucket(Items.CYANITE_BUCKET)
                        .block(Blocks.CYANITE_FLUID)));

        public static RegistryObject<FluidType> BLUTONIUM_FLUID_TYPE = registerReactantFluidType(Reactants.Blutonium);

        public static final RegistryObject<FlowingFluid> BLUTONIUM_SOURCE = FLUIDS.register(Reactants.Blutonium.getFluidSourceName(),
                () -> new ForgeFlowingFluid.Source(new ForgeFlowingFluid.Properties(BLUTONIUM_FLUID_TYPE, Fluids.BLUTONIUM_SOURCE, Fluids.BLUTONIUM_FLOWING)
                        .bucket(Items.BLUTONIUM_BUCKET)
                        .block(Blocks.BLUTONIUM_FLUID)));

        public static final RegistryObject<FlowingFluid> BLUTONIUM_FLOWING = FLUIDS.register(Reactants.Blutonium.getFluidFlowingName(),
                () -> new ForgeFlowingFluid.Flowing(new ForgeFlowingFluid.Properties(BLUTONIUM_FLUID_TYPE, Fluids.BLUTONIUM_SOURCE, Fluids.BLUTONIUM_FLOWING)
                        .bucket(Items.BLUTONIUM_BUCKET)
                        .block(Blocks.BLUTONIUM_FLUID)));

        public static RegistryObject<FluidType> MAGENTITE_FLUID_TYPE = registerReactantFluidType(Reactants.Magentite);

        public static final RegistryObject<FlowingFluid> MAGENTITE_SOURCE = FLUIDS.register(Reactants.Magentite.getFluidSourceName(),
                () -> new ForgeFlowingFluid.Source(new ForgeFlowingFluid.Properties(MAGENTITE_FLUID_TYPE, Fluids.MAGENTITE_SOURCE, Fluids.MAGENTITE_FLOWING)
                        .bucket(Items.MAGENTITE_BUCKET)
                        .block(Blocks.MAGENTITE_FLUID)));

        public static final RegistryObject<FlowingFluid> MAGENTITE_FLOWING = FLUIDS.register(Reactants.Magentite.getFluidFlowingName(),
                () -> new ForgeFlowingFluid.Flowing(new ForgeFlowingFluid.Properties(MAGENTITE_FLUID_TYPE, Fluids.MAGENTITE_SOURCE, Fluids.MAGENTITE_FLOWING)
                        .bucket(Items.MAGENTITE_BUCKET)
                        .block(Blocks.MAGENTITE_FLUID)));

        public static RegistryObject<FluidType> VERDERIUM_FLUID_TYPE = registerReactantFluidType(Reactants.Verderium);

        public static final RegistryObject<FlowingFluid> VERDERIUM_SOURCE = FLUIDS.register(Reactants.Verderium.getFluidSourceName(),
                () -> new ForgeFlowingFluid.Source(new ForgeFlowingFluid.Properties(VERDERIUM_FLUID_TYPE, Fluids.VERDERIUM_SOURCE, Fluids.VERDERIUM_FLOWING)
                        .bucket(Items.VERDERIUM_BUCKET)
                        .block(Blocks.VERDERIUM_FLUID)));

        public static final RegistryObject<FlowingFluid> VERDERIUM_FLOWING = FLUIDS.register(Reactants.Verderium.getFluidFlowingName(),
                () -> new ForgeFlowingFluid.Flowing(new ForgeFlowingFluid.Properties(VERDERIUM_FLUID_TYPE, Fluids.VERDERIUM_SOURCE, Fluids.VERDERIUM_FLOWING)
                        .bucket(Items.VERDERIUM_BUCKET)
                        .block(Blocks.VERDERIUM_FLUID)));

        public static RegistryObject<FluidType> ROSSINITE_FLUID_TYPE = registerReactantFluidType(Reactants.Rossinite);

        public static final RegistryObject<FlowingFluid> ROSSINITE_SOURCE = FLUIDS.register(Reactants.Rossinite.getFluidSourceName(),
                () -> new ForgeFlowingFluid.Source(new ForgeFlowingFluid.Properties(ROSSINITE_FLUID_TYPE, Fluids.ROSSINITE_SOURCE, Fluids.ROSSINITE_FLOWING)
                        .bucket(Items.ROSSINITE_BUCKET)
                        .block(Blocks.ROSSINITE_FLUID)));

        public static final RegistryObject<FlowingFluid> ROSSINITE_FLOWING = FLUIDS.register(Reactants.Rossinite.getFluidFlowingName(),
                () -> new ForgeFlowingFluid.Flowing(new ForgeFlowingFluid.Properties(ROSSINITE_FLUID_TYPE, Fluids.ROSSINITE_SOURCE, Fluids.ROSSINITE_FLOWING)
                        .bucket(Items.ROSSINITE_BUCKET)
                        .block(Blocks.ROSSINITE_FLUID)));

        //endregion
        //region moderators

        public static RegistryObject<FluidType> CRYOMISI_FLUID_TYPE = FLUID_TYPES.register("cryomisi",
                () -> ReactorFluidType.of("cryomisi", 0xf5002d, 1000, 6, Rarity.RARE));

        public static final RegistryObject<FlowingFluid> CRYOMISI_SOURCE = FLUIDS.register("cryomisi",
                () -> new ForgeFlowingFluid.Source(new ForgeFlowingFluid.Properties(CRYOMISI_FLUID_TYPE, Fluids.CRYOMISI_SOURCE, Fluids.CRYOMISI_FLOWING)
                        .bucket(Items.CRYOMISI_BUCKET)
                        .block(Blocks.CRYOMISI_FLUID)));

        public static final RegistryObject<FlowingFluid> CRYOMISI_FLOWING = FLUIDS.register("cryomisi_flowing",
                () -> new ForgeFlowingFluid.Flowing(new ForgeFlowingFluid.Properties(CRYOMISI_FLUID_TYPE, Fluids.CRYOMISI_SOURCE, Fluids.CRYOMISI_FLOWING)
                        .bucket(Items.CRYOMISI_BUCKET)
                        .block(Blocks.CRYOMISI_FLUID)));

        public static RegistryObject<FluidType> TANGERIUM_FLUID_TYPE = FLUID_TYPES.register("tangerium",
                () -> ReactorFluidType.of("tangerium", 0xcf463b, 1500, 5, Rarity.RARE));

        public static final RegistryObject<FlowingFluid> TANGERIUM_SOURCE = FLUIDS.register("tangerium",
                () -> new ForgeFlowingFluid.Source(new ForgeFlowingFluid.Properties(TANGERIUM_FLUID_TYPE, Fluids.TANGERIUM_SOURCE, Fluids.TANGERIUM_FLOWING)
                        .bucket(Items.TANGERIUM_BUCKET)
                        .block(Blocks.TANGERIUM_FLUID)));

        public static final RegistryObject<FlowingFluid> TANGERIUM_FLOWING = FLUIDS.register("tangerium_flowing",
                () -> new ForgeFlowingFluid.Flowing(new ForgeFlowingFluid.Properties(TANGERIUM_FLUID_TYPE, Fluids.TANGERIUM_SOURCE, Fluids.TANGERIUM_FLOWING)
                        .bucket(Items.TANGERIUM_BUCKET)
                        .block(Blocks.TANGERIUM_FLUID)));

        public static RegistryObject<FluidType> REDFRIGIUM_FLUID_TYPE = FLUID_TYPES.register("redfrigium",
                () -> ReactorFluidType.of("redfrigium", 0xfcb3c1, 1500, 8, Rarity.EPIC));

        public static final RegistryObject<FlowingFluid> REDFRIGIUM_SOURCE = FLUIDS.register("redfrigium",
                () -> new ForgeFlowingFluid.Source(new ForgeFlowingFluid.Properties(REDFRIGIUM_FLUID_TYPE, Fluids.REDFRIGIUM_SOURCE, Fluids.REDFRIGIUM_FLOWING)
                        .bucket(Items.REDFRIGIUM_BUCKET)
                        .block(Blocks.REDFRIGIUM_FLUID)));

        public static final RegistryObject<FlowingFluid> REDFRIGIUM_FLOWING = FLUIDS.register("redfrigium_flowing",
                () -> new ForgeFlowingFluid.Flowing(new ForgeFlowingFluid.Properties(REDFRIGIUM_FLUID_TYPE, Fluids.REDFRIGIUM_SOURCE, Fluids.REDFRIGIUM_FLOWING)
                        .bucket(Items.REDFRIGIUM_BUCKET)
                        .block(Blocks.REDFRIGIUM_FLUID)));

        //endregion
        //region internals

        private static RegistryObject<FlowingFluid> registerSteam(final String name, final NonNullFunction<ForgeFlowingFluid.Properties, FlowingFluid> factory) {
            return FLUIDS.register(name, () -> factory.apply(new ForgeFlowingFluid.Properties(STEAM_FLUID_TYPE, STEAM_SOURCE, STEAM_FLOWING)
                    .bucket(Items.STEAM_BUCKET)
                    .block(Blocks.STEAM)));
        }

        private static RegistryObject<FluidType> registerReactantFluidType(final Reactants reactant) {
            return FLUID_TYPES.register(reactant.getFluidName(), () -> ReactorFluidType.of(reactant));
        }

        //endregion
    }

    public static final class TileEntityTypes {

        private static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES =
                DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ExtremeReactors.MOD_ID);

        static void initialize(final IEventBus bus) {
            TILE_ENTITIES.register(bus);
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

        public static final RegistryObject<BlockEntityType<ReactorFluidAccessPortEntity>> REACTOR_FLUID_ACCESSPORT =
                registerBlockEntity("reactoraccessportfluid", ReactorFluidAccessPortEntity::new,
                        () -> Blocks.REACTOR_FLUID_ACCESSPORT_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<ReactorFluidPortEntity>> REACTOR_FLUIDPORT_FORGE_ACTIVE =
                registerBlockEntity("reactorfluidport_forge_active",
                        (position, blockState) -> new ReactorFluidPortEntity(FluidPortType.Forge, IoMode.Active, TileEntityTypes.REACTOR_FLUIDPORT_FORGE_ACTIVE.get(), position, blockState),
                        () -> Blocks.REACTOR_FLUIDTPORT_FORGE_ACTIVE_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<ReactorFluidPortEntity>> REACTOR_FLUIDPORT_FORGE_PASSIVE =
                registerBlockEntity("reactorfluidport_forge_passive",
                        (position, blockState) -> new ReactorFluidPortEntity(FluidPortType.Forge, IoMode.Passive, TileEntityTypes.REACTOR_FLUIDPORT_FORGE_PASSIVE.get(), position, blockState),
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
                        (position, blockState) -> new ReactorPowerTapEntity(EnergySystem.ForgeEnergy, IoMode.Active, TileEntityTypes.REACTOR_POWERTAP_FE_ACTIVE.get(), position, blockState),
                        () -> Blocks.REACTOR_POWERTAP_FE_ACTIVE_BASIC::get,
                        () -> Blocks.REACTOR_POWERTAP_FE_ACTIVE_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<ReactorPowerTapEntity>> REACTOR_POWERTAP_FE_PASSIVE =
                registerBlockEntity("reactorpowertap_fe_passive",
                        (position, blockState) -> new ReactorPowerTapEntity(EnergySystem.ForgeEnergy, IoMode.Passive, TileEntityTypes.REACTOR_POWERTAP_FE_PASSIVE.get(), position, blockState),
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
                        (position, blockState) -> new ReactorChargingPortEntity(EnergySystem.ForgeEnergy, TileEntityTypes.REACTOR_CHARGINGPORT_FE.get(), position, blockState),
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
                        (position, blockState) -> new TurbineFluidPortEntity(FluidPortType.Forge, IoMode.Active, TileEntityTypes.TURBINE_FLUIDPORT_FORGE_ACTIVE.get(), position, blockState),
                        () -> Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC::get,
                        () -> Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<TurbineFluidPortEntity>> TURBINE_FLUIDPORT_FORGE_PASSIVE =
                registerBlockEntity("turbinefluidport_forge_passive",
                        (position, blockState) -> new TurbineFluidPortEntity(FluidPortType.Forge, IoMode.Passive, TileEntityTypes.TURBINE_FLUIDPORT_FORGE_PASSIVE.get(), position, blockState),
                        () -> Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC::get,
                        () -> Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<TurbineCreativeSteamGenerator>> TURBINE_CREATIVE_STEAM_GENERATOR =
                registerBlockEntity("turbinecreativesteamgenerator",
                        TurbineCreativeSteamGenerator::new,
                        () -> Blocks.TURBINE_CREATIVE_STEAM_GENERATOR_BASIC::get);

        public static final RegistryObject<BlockEntityType<TurbinePowerTapEntity>> TURBINE_POWERTAP_FE_ACTIVE =
                registerBlockEntity("turbinepowertap_fe_active",
                        (position, blockState) -> new TurbinePowerTapEntity(EnergySystem.ForgeEnergy, IoMode.Active, TileEntityTypes.TURBINE_POWERTAP_FE_ACTIVE.get(), position, blockState),
                        () -> Blocks.TURBINE_POWERTAP_FE_ACTIVE_BASIC::get,
                        () -> Blocks.TURBINE_POWERTAP_FE_ACTIVE_REINFORCED::get);

        public static final RegistryObject<BlockEntityType<TurbinePowerTapEntity>> TURBINE_POWERTAP_FE_PASSIVE =
                registerBlockEntity("turbinepowertap_fe_passive",
                        (position, blockState) -> new TurbinePowerTapEntity(EnergySystem.ForgeEnergy, IoMode.Passive, TileEntityTypes.TURBINE_POWERTAP_FE_PASSIVE.get(), position, blockState),
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
                        (position, blockState) -> new TurbineChargingPortEntity(EnergySystem.ForgeEnergy, TileEntityTypes.TURBINE_CHARGINGPORT_FE.get(), position, blockState),
                        () -> Blocks.TURBINE_CHARGINGPORT_FE_BASIC::get,
                        () -> Blocks.TURBINE_CHARGINGPORT_FE_REINFORCED::get);

        //endregion
        //region reprocessor

        public static final RegistryObject<BlockEntityType<ReprocessorCasingEntity>> REPROCESSOR_CASING =
                registerBlockEntity("reprocessorcasing", ReprocessorCasingEntity::new, () -> Blocks.REPROCESSOR_CASING::get);

        public static final RegistryObject<BlockEntityType<ReprocessorGlassEntity>> REPROCESSOR_GLASS =
                registerBlockEntity("reprocessorglass", ReprocessorGlassEntity::new, () -> Blocks.REPROCESSOR_GLASS::get);

        public static final RegistryObject<BlockEntityType<ReprocessorControllerEntity>> REPROCESSOR_CONTROLLER =
                registerBlockEntity("reprocessorcontroller", ReprocessorControllerEntity::new, () -> Blocks.REPROCESSOR_CONTROLLER::get);

        public static final RegistryObject<BlockEntityType<ReprocessorAccessPortEntity>> REPROCESSOR_WASTEINJECTOR =
                registerBlockEntity("reprocessorwasteinjector",
                        (position, blockState) -> new ReprocessorAccessPortEntity(TileEntityTypes.REPROCESSOR_WASTEINJECTOR.get(), IoDirection.Input, position, blockState),
                        () -> Blocks.REPROCESSOR_WASTEINJECTOR::get);

        public static final RegistryObject<BlockEntityType<ReprocessorFluidPortEntity>> REPROCESSOR_FLUIDINJECTOR =
                registerBlockEntity("reprocessorfluidinjector", ReprocessorFluidPortEntity::new, () -> Blocks.REPROCESSOR_FLUIDINJECTOR::get);

        public static final RegistryObject<BlockEntityType<ReprocessorAccessPortEntity>> REPROCESSOR_OUTPUTPORT =
                registerBlockEntity("reprocessoroutputport",
                        (position, blockState) -> new ReprocessorAccessPortEntity(TileEntityTypes.REPROCESSOR_OUTPUTPORT.get(), IoDirection.Output, position, blockState),
                        () -> Blocks.REPROCESSOR_OUTPUTPORT::get);

        public static final RegistryObject<BlockEntityType<ReprocessorPowerPortEntity>> REPROCESSOR_POWERPORT =
                registerBlockEntity("reprocessorpowerport", ReprocessorPowerPortEntity::new, () -> Blocks.REPROCESSOR_POWERPORT::get);

        public static final RegistryObject<BlockEntityType<ReprocessorCollectorEntity>> REPROCESSOR_COLLECTOR =
                registerBlockEntity("reprocessorcollector", ReprocessorCollectorEntity::new, () -> Blocks.REPROCESSOR_COLLECTOR::get);

        //endregion
        //region fluidizer

        public static final RegistryObject<BlockEntityType<FluidizerCasingEntity>> FLUIDIZER_CASING =
                registerBlockEntity("fluidizercasing", FluidizerCasingEntity::new, () -> Blocks.FLUIDIZER_CASING::get);

        public static final RegistryObject<BlockEntityType<FluidizerGlassEntity>> FLUIDIZER_GLASS =
                registerBlockEntity("fluidizerglass", FluidizerGlassEntity::new, () -> Blocks.FLUIDIZER_GLASS::get);

        public static final RegistryObject<BlockEntityType<FluidizerControllerEntity>> FLUIDIZER_CONTROLLER =
                registerBlockEntity("fluidizercontroller", FluidizerControllerEntity::new, () -> Blocks.FLUIDIZER_CONTROLLER::get);

        public static final RegistryObject<BlockEntityType<FluidizerSolidInjectorEntity>> FLUIDIZER_SOLIDINJECTOR =
                registerBlockEntity("fluidizersolidinjector", FluidizerSolidInjectorEntity::new, () -> Blocks.FLUIDIZER_SOLIDINJECTOR::get);

        public static final RegistryObject<BlockEntityType<FluidizerFluidInjectorEntity>> FLUIDIZER_FLUIDINJECTOR =
                registerBlockEntity("fluidizefluidinjector", FluidizerFluidInjectorEntity::new, () -> Blocks.FLUIDIZER_FLUIDINJECTOR::get);

        public static final RegistryObject<BlockEntityType<FluidizerOutputPortEntity>> FLUIDIZER_OUTPUTPORT =
                registerBlockEntity("fluidizeoutputport", FluidizerOutputPortEntity::new, () -> Blocks.FLUIDIZER_OUTPUTPORT::get);

        public static final RegistryObject<BlockEntityType<FluidizerPowerPortEntity>> FLUIDIZER_POWERPORT =
                registerBlockEntity("fluidizerpowerport", FluidizerPowerPortEntity::new, () -> Blocks.FLUIDIZER_POWERPORT::get);

        //endregion
        //region internals

        @SuppressWarnings("ConstantConditions")
        @SafeVarargs
        private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerBlockEntity(final String name,
                                                                                                      final BlockEntityType.BlockEntitySupplier<T> factory,
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
                DeferredRegister.create(ForgeRegistries.MENU_TYPES, ExtremeReactors.MOD_ID);

        static void initialize(final IEventBus bus) {
            CONTAINERS.register(bus);
        }

        //region Reactor

        public static final RegistryObject<MenuType<ModTileContainer<ReactorControllerEntity>>> REACTOR_CONTROLLER =
                registerContainer("reactorcontroller", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.REACTOR_CONTROLLER.get(), windowId, inv, data));

        public static final RegistryObject<MenuType<ReactorSolidAccessPortContainer>> REACTOR_SOLID_ACCESSPORT =
                registerContainer("reactoraccessport", ReactorSolidAccessPortContainer::new);

        public static final RegistryObject<MenuType<ModTileContainer<ReactorFluidAccessPortEntity>>> REACTOR_FLUID_ACCESSPORT =
                registerContainer("reactoraccessportfluid", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.REACTOR_FLUID_ACCESSPORT.get(), windowId, inv, data));

        public static final RegistryObject<MenuType<ModTileContainer<ReactorRedstonePortEntity>>> REACTOR_REDSTONEPORT =
                registerContainer("reactorredstoneport", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.REACTOR_REDSTONEPORT.get(), windowId, inv, data));

        public static final RegistryObject<MenuType<ModTileContainer<ReactorControlRodEntity>>> REACTOR_CONTROLROD =
                registerContainer("reactorcontrolrod", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.REACTOR_CONTROLROD.get(), windowId, inv, data));

        public static final RegistryObject<MenuType<ChargingPortContainer<ReactorChargingPortEntity>>> REACTOR_CHARGINGPORT =
                registerContainer("reactorchargingport",
                        (windowId, inv, data) -> new ChargingPortContainer<>(windowId, Content.ContainerTypes.REACTOR_CHARGINGPORT.get(), inv, data));

        public static final RegistryObject<MenuType<ModTileContainer<ReactorFluidPortEntity>>> REACTOR_FLUIDPORT =
                registerContainer("reactorfluidport", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.REACTOR_FLUIDPORT.get(), windowId, inv, data));

        //endregion
        //region Turbine

        public static final RegistryObject<MenuType<ModTileContainer<TurbineControllerEntity>>> TURBINE_CONTROLLER =
                registerContainer("turbinecontroller", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.TURBINE_CONTROLLER.get(), windowId, inv, data));

        public static final RegistryObject<MenuType<ModTileContainer<TurbineRedstonePortEntity>>> TURBINE_REDSTONEPORT =
                registerContainer("turbineredstoneport", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.TURBINE_REDSTONEPORT.get(), windowId, inv, data));

        public static final RegistryObject<MenuType<ChargingPortContainer<TurbineChargingPortEntity>>> TURBINE_CHARGINGPORT =
                registerContainer("turbinechargingport",
                        (windowId, inv, data) -> new ChargingPortContainer<>(windowId, Content.ContainerTypes.TURBINE_CHARGINGPORT.get(), inv, data));

        public static final RegistryObject<MenuType<ModTileContainer<TurbineFluidPortEntity>>> TURBINE_FLUIDPORT =
                registerContainer("turbinefluidport", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.TURBINE_FLUIDPORT.get(), windowId, inv, data));

        //endregion
        //region Reprocessor

        public static final RegistryObject<MenuType<ModTileContainer<ReprocessorControllerEntity>>> REPROCESSOR_CONTROLLER =
                registerContainer("reprocessorcontroller", (windowId, inv, data) ->
                        ModTileContainer.empty(Content.ContainerTypes.REPROCESSOR_CONTROLLER.get(), windowId, inv, data));

        public static final RegistryObject<MenuType<ReprocessorAccessPortContainer>> REPROCESSOR_ACCESSPORT =
                registerContainer("reprocessoraccessport", ReprocessorAccessPortContainer::new);

        //endregion
        //region Fluidizer

        public static final RegistryObject<MenuType<FluidizerControllerContainer>> FLUIDIZER_CONTROLLER =
                registerContainer("fluidizercontroller", FluidizerControllerContainer::new);

        public static final RegistryObject<MenuType<FluidizerSolidInjectorContainer>> FLUIDIZER_SOLID_INJECTOR =
                registerContainer("fluidizersolidinjector", FluidizerSolidInjectorContainer::new);

        //endregion
        //region internals

        private static <C extends AbstractContainerMenu> RegistryObject<MenuType<C>> registerContainer(final String name,
                                                                                                       final IContainerFactory<C> factory) {
            return CONTAINERS.register(name, () -> IForgeMenuType.create(factory));
        }

        //endregion
    }

    public static final class Recipes {

        private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
                DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ExtremeReactors.MOD_ID);

        static void initialize(final IEventBus bus) {
            SERIALIZERS.register(bus);
        }

        //region Reprocessor

        public static final ModRecipeType<ReprocessorRecipe> REPROCESSOR_RECIPE_TYPE = ModRecipeType.create(ReprocessorRecipe.ID);

        public static final RegistryObject<RecipeSerializer<ReprocessorRecipe>> REPROCESSOR_RECIPE_SERIALIZER =
                SERIALIZERS.register(ReprocessorRecipe.NAME, ReprocessorRecipe::serializer);

        //endregion
        //region Fluidizer

        public static final ModRecipeType<ModRecipe> FLUIDIZER_RECIPE_TYPE =
                ModRecipeType.create(ExtremeReactors.ROOT_LOCATION.buildWithSuffix("fluidizer"));

        public static final RegistryObject<RecipeSerializer<FluidizerSolidRecipe>> FLUIDIZER_SOLID_RECIPE_SERIALIZER =
                SERIALIZERS.register(IFluidizerRecipe.Type.Solid.getRecipeName(), FluidizerSolidRecipe::serializer);
        public static final RegistryObject<RecipeSerializer<FluidizerSolidMixingRecipe>> FLUIDIZER_SOLIDMIXING_RECIPE_SERIALIZER =
                SERIALIZERS.register(IFluidizerRecipe.Type.SolidMixing.getRecipeName(), FluidizerSolidMixingRecipe::serializer);
        public static final RegistryObject<RecipeSerializer<FluidizerFluidMixingRecipe>> FLUIDIZER_FLUIDMIXING_RECIPE_SERIALIZER =
                SERIALIZERS.register(IFluidizerRecipe.Type.FluidMixing.getRecipeName(), FluidizerFluidMixingRecipe::serializer);

        //endregion
    }

    public static final class Biomes {

        private static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS =
                DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, ExtremeReactors.MOD_ID);

        static void initialize(final IEventBus bus) {
            BIOME_MODIFIER_SERIALIZERS.register(bus);
        }

        public static final RegistryObject<Codec<OreBiomeModifier>> OREGEN_YELLORITE = register("oregen_yellorite", OreBiomeModifier::yellorite);

        public static final RegistryObject<Codec<OreBiomeModifier>> OREGEN_ANGLESITE = register("oregen_anglesite", OreBiomeModifier::anglesite);

        public static final RegistryObject<Codec<OreBiomeModifier>> OREGEN_BENITOITE = register("oregen_benitoite", OreBiomeModifier::benitoite);

        //region internals

        private static RegistryObject<Codec<OreBiomeModifier>> register(final String name,
                                                                        final BiFunction<HolderSet<Biome>, Holder<PlacedFeature>, OreBiomeModifier> factory) {
            return BIOME_MODIFIER_SERIALIZERS.register(name, () ->
                    RecordCodecBuilder.create(builder -> builder.group(
                            Biome.LIST_CODEC.fieldOf("biomes").forGetter(OreBiomeModifier::validBiomes),
                            PlacedFeature.CODEC.fieldOf("feature").forGetter(OreBiomeModifier::feature)
                    ).apply(builder, factory)));
        }

        //endregion
    }

    public static final class CreativeTabs {

        private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ExtremeReactors.MOD_ID);

        static void initialize(final IEventBus bus) {

            final var tabGeneral = ExtremeReactors.ROOT_LOCATION.buildWithSuffix("tab.general");
            final var tabReactor = ExtremeReactors.ROOT_LOCATION.buildWithSuffix("tab.reactor");
            final var tabTurbine = ExtremeReactors.ROOT_LOCATION.buildWithSuffix("tab.turbine");

            TABS.register(bus);

            TABS.register("tab.general", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.bigreactors.general"))
                    .icon(() -> new ItemStack(Items.YELLORITE_ORE_BLOCK.get()))
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .withTabsAfter(tabReactor)
                    .displayItems((parameters, output) -> {

                        acceptAll(output, Blocks.YELLORITE_ORE_BLOCK, Blocks.ANGLESITE_ORE_BLOCK, Blocks.BENITOITE_ORE_BLOCK,
                                Items.YELLORIUM_INGOT, Items.YELLORIUM_DUST, Blocks.YELLORIUM_BLOCK,
                                Items.BLUTONIUM_INGOT, Items.BLUTONIUM_DUST, Blocks.BLUTONIUM_BLOCK,
                                Items.CYANITE_INGOT, Items.CYANITE_DUST, Blocks.CYANITE_BLOCK,
                                Items.MAGENTITE_INGOT, Items.MAGENTITE_DUST, Blocks.MAGENTITE_BLOCK,
                                Items.GRAPHITE_INGOT, Items.GRAPHITE_DUST, Blocks.GRAPHITE_BLOCK,
                                Items.LUDICRITE_INGOT, Items.LUDICRITE_DUST, Blocks.LUDICRITE_BLOCK,
                                Items.RIDICULITE_INGOT, Items.RIDICULITE_DUST, Blocks.RIDICULITE_BLOCK,
                                Items.INANITE_INGOT, Items.INANITE_DUST, Blocks.INANITE_BLOCK,
                                Items.YELLORIUM_NUGGET, Items.BLUTONIUM_NUGGET,
                                Items.ANGLESITE_CRYSTAL, Items.BENITOITE_CRYSTAL,
                                Items.WRENCH);

                        PatchouliCompat.consumeBookStack(PatchouliCompat.HANDBOOK_ID, output::accept);

                        acceptAll(output, Blocks.REPROCESSOR_CASING, Blocks.REPROCESSOR_GLASS,
                                Blocks.REPROCESSOR_CONTROLLER, Blocks.REPROCESSOR_COLLECTOR,
                                Blocks.REPROCESSOR_WASTEINJECTOR, Blocks.REPROCESSOR_FLUIDINJECTOR,
                                Blocks.REPROCESSOR_OUTPUTPORT, Blocks.REPROCESSOR_POWERPORT);

                        acceptAll(output, Items.STEAM_BUCKET, Items.YELLORIUM_BUCKET, Items.CYANITE_BUCKET,
                                Items.BLUTONIUM_BUCKET, Items.MAGENTITE_BUCKET, Items.VERDERIUM_BUCKET,
                                Items.ROSSINITE_BUCKET, Items.CRYOMISI_BUCKET, Items.TANGERIUM_BUCKET,
                                Items.REDFRIGIUM_BUCKET);

                        acceptAll(output, Blocks.FLUIDIZER_CASING, Blocks.FLUIDIZER_GLASS,
                                Blocks.FLUIDIZER_CONTROLLER, Blocks.FLUIDIZER_SOLIDINJECTOR,
                                Blocks.FLUIDIZER_FLUIDINJECTOR, Blocks.FLUIDIZER_OUTPUTPORT,
                                Blocks.FLUIDIZER_POWERPORT);
                    })
                    .build()
            );

            TABS.register("tab.reactor", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.bigreactors.reactor"))
                    .icon(() -> new ItemStack(Items.REACTOR_FUELROD_BASIC.get()))
                    .noScrollBar()
                    .withTabsBefore(tabGeneral)
                    .withTabsAfter(tabTurbine)
                    .displayItems((parameters, output) -> acceptAll(output,
                            Blocks.REACTOR_CONTROLLER_BASIC, Blocks.REACTOR_CASING_BASIC,
                            Blocks.REACTOR_GLASS_BASIC, Blocks.REACTOR_FUELROD_BASIC,
                            Blocks.REACTOR_CONTROLROD_BASIC, Blocks.REACTOR_SOLID_ACCESSPORT_BASIC,
                            Blocks.REACTOR_CHARGINGPORT_FE_BASIC,
                            Blocks.REACTOR_POWERTAP_FE_ACTIVE_BASIC, Blocks.REACTOR_POWERTAP_FE_PASSIVE_BASIC,
                            Blocks.REACTOR_REDSTONEPORT_BASIC,

                            Blocks.REACTOR_CONTROLLER_REINFORCED, Blocks.REACTOR_CASING_REINFORCED,
                            Blocks.REACTOR_GLASS_REINFORCED, Blocks.REACTOR_FUELROD_REINFORCED,
                            Blocks.REACTOR_CONTROLROD_REINFORCED, Blocks.REACTOR_SOLID_ACCESSPORT_REINFORCED,
                            Blocks.REACTOR_FLUID_ACCESSPORT_REINFORCED,
                            Blocks.REACTOR_POWERTAP_FE_ACTIVE_REINFORCED, Blocks.REACTOR_POWERTAP_FE_PASSIVE_REINFORCED,
                            Blocks.REACTOR_CHARGINGPORT_FE_REINFORCED,
                            Blocks.REACTOR_REDSTONEPORT_REINFORCED, Blocks.REACTOR_COMPUTERPORT_REINFORCED,
                            Blocks.REACTOR_FLUIDTPORT_FORGE_ACTIVE_REINFORCED, Blocks.REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED,
                            Blocks.REACTOR_FLUIDPORT_MEKANISM_PASSIVE_REINFORCED,
                            Blocks.REACTOR_CREATIVE_WATER_GENERATOR_REINFORCED))
                    .build()
            );

            TABS.register("tab.turbine", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.bigreactors.turbine"))
                    .icon(() -> new ItemStack(Items.TURBINE_ROTORSHAFT_BASIC.get()))
                    .noScrollBar()
                    .withTabsBefore(tabReactor)
                    .displayItems((parameters, output) -> acceptAll(output,
                            Blocks.TURBINE_CONTROLLER_BASIC, Blocks.TURBINE_CASING_BASIC,
                            Blocks.TURBINE_GLASS_BASIC, Blocks.TURBINE_ROTORBEARING_BASIC,
                            Blocks.TURBINE_ROTORSHAFT_BASIC, Blocks.TURBINE_ROTORBLADE_BASIC,
                            Blocks.TURBINE_POWERTAP_FE_ACTIVE_BASIC, Blocks.TURBINE_POWERTAP_FE_PASSIVE_BASIC,
                            Blocks.TURBINE_CHARGINGPORT_FE_BASIC,
                            Blocks.TURBINE_REDSTONEPORT_BASIC,
                            Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC, Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC,
                            Blocks.TURBINE_CREATIVE_STEAM_GENERATOR_BASIC,

                            Blocks.TURBINE_CONTROLLER_REINFORCED, Blocks.TURBINE_CASING_REINFORCED,
                            Blocks.TURBINE_GLASS_REINFORCED, Blocks.TURBINE_ROTORBEARING_REINFORCED,
                            Blocks.TURBINE_ROTORSHAFT_REINFORCED, Blocks.TURBINE_ROTORBLADE_REINFORCED,
                            Blocks.TURBINE_POWERTAP_FE_ACTIVE_REINFORCED, Blocks.TURBINE_POWERTAP_FE_PASSIVE_REINFORCED,
                            Blocks.TURBINE_CHARGINGPORT_FE_REINFORCED,
                            Blocks.TURBINE_REDSTONEPORT_REINFORCED, Blocks.TURBINE_COMPUTERPORT_REINFORCED,
                            Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED, Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED,
                            Blocks.TURBINE_CREATIVE_STEAM_GENERATOR_REINFORCED))
                    .build()
            );
        }

        //region internals

        @SafeVarargs
        static void acceptAll(CreativeModeTab.Output output, Supplier<? extends ItemLike>... items) {

            for (var item : items) {
                output.accept(item.get(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            }
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
