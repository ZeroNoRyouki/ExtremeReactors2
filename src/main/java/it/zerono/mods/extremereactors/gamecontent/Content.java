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

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.extremereactors.gamecontent.fluid.ReactantFluidBlock;
import it.zerono.mods.extremereactors.gamecontent.fluid.ReactorFluidType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.container.ChargingPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.container.FluidPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.PowerTapBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.fluidport.FluidPortType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.EnergizerPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.IEnergizerPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.MultiBlockEnergizer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.container.EnergizerChargingPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.container.EnergizerControllerContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.container.EnergizerPowerPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.part.*;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.variant.EnergizerVariant;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.FluidizerPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.IFluidizerPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.MultiblockFluidizer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.container.FluidizerControllerContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.container.FluidizerSolidInjectorContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part.*;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerFluidMixingRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerSolidMixingRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerSolidRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.IFluidizerRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.IReactorPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.Reactants;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactorPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.component.ReactorFluidAccessPortComponent;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.container.*;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.*;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.IMultiblockReactorVariant;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.IReprocessorPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.MultiblockReprocessor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.ReprocessorPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.container.ReprocessorAccessPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.container.ReprocessorControllerContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.part.*;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.recipe.ReprocessorRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.ITurbinePartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.MultiblockTurbine;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.TurbinePartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.container.TurbineControllerContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.container.TurbineRedstonePortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.*;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.IMultiblockTurbineVariant;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.extremereactors.loader.forge.gamecontent.worldgen.OreBiomeModifier;
import it.zerono.mods.zerocore.base.multiblock.part.GenericDeviceBlock;
import it.zerono.mods.zerocore.base.multiblock.part.GlassBlock;
import it.zerono.mods.zerocore.base.multiblock.part.io.IOPortBlock;
import it.zerono.mods.zerocore.lib.block.ModBlock;
import it.zerono.mods.zerocore.lib.block.ModOreBlock;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock;
import it.zerono.mods.zerocore.lib.compat.patchouli.IPatchouliService;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.data.ModCodecs;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.fluid.SimpleFluidTypeRenderProperties;
import it.zerono.mods.zerocore.lib.item.ModItem;
import it.zerono.mods.zerocore.lib.item.TintedBucketItem;
import it.zerono.mods.zerocore.lib.recipe.ModRecipe;
import it.zerono.mods.zerocore.lib.recipe.ModRecipeType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
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
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Content {

    public static void initialize(IEventBus bus) {

        Blocks.initialize(bus);
        Items.initialize(bus);
        Fluids.initialize(bus);
        TileEntityTypes.initialize(bus);
        ContainerTypes.initialize(bus);
        DataComponents.initialize(bus);
        Recipes.initialize(bus);
        Biomes.initialize(bus);
        CreativeTabs.initialize(bus);

        bus.addListener(Content::onCommonInit);
    }

    public static final class Blocks {

        private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, ExtremeReactors.MOD_ID);

        static void initialize(final IEventBus bus) {
            BLOCKS.register(bus);
        }

        public static Collection<DeferredHolder<Block, ? extends Block>> getAll() {
            return BLOCKS.getEntries();
        }

        //region metals

        public static final Supplier<ModBlock> YELLORIUM_BLOCK = registerMetalBlock(Reactants.Yellorium.getBlockName(), DyeColor.YELLOW);
        public static final Supplier<ModBlock> RAW_YELLORIUM_BLOCK = registerMetalBlock("raw_yellorium_block", DyeColor.YELLOW);
        public static final Supplier<ModBlock> CYANITE_BLOCK = registerMetalBlock(Reactants.Cyanite.getBlockName(), DyeColor.LIGHT_BLUE);
        public static final Supplier<ModBlock> GRAPHITE_BLOCK = registerMetalBlock("graphite_block", DyeColor.GRAY);
        public static final Supplier<ModBlock> BLUTONIUM_BLOCK = registerMetalBlock(Reactants.Blutonium.getBlockName(), DyeColor.PURPLE);
        public static final Supplier<ModBlock> MAGENTITE_BLOCK = registerMetalBlock(Reactants.Magentite.getBlockName(), DyeColor.MAGENTA);

        public static final Supplier<ModBlock> LUDICRITE_BLOCK = registerMetalBlock("ludicrite_block", DyeColor.PURPLE);
        public static final Supplier<ModBlock> RIDICULITE_BLOCK = registerMetalBlock("ridiculite_block", DyeColor.PINK);
        public static final Supplier<ModBlock> INANITE_BLOCK = registerMetalBlock("inanite_block", DyeColor.RED);
        public static final Supplier<ModBlock> INSANITE_BLOCK = registerMetalBlock("insanite_block", DyeColor.RED);

        //endregion
        //region ores

        public static final Supplier<ModBlock> YELLORITE_ORE_BLOCK = registerOreBlock("yellorite_ore", DyeColor.YELLOW, 0, 0);
        public static final Supplier<ModBlock> DEEPSLATE_YELLORITE_ORE_BLOCK = registerOreBlock("deepslate_yellorite_ore", DyeColor.YELLOW, 0, 0);
        public static final Supplier<ModBlock> ANGLESITE_ORE_BLOCK = registerOreBlock("anglesite_ore", DyeColor.ORANGE);
        public static final Supplier<ModBlock> BENITOITE_ORE_BLOCK = registerOreBlock("benitoite_ore", DyeColor.LIGHT_BLUE);

        //endregion
        //region fluids

        public static final Supplier<LiquidBlock> STEAM = BLOCKS.register("steam",
                () -> new LiquidBlock(Fluids.STEAM_SOURCE.get(),
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

        public static final Supplier<ReactantFluidBlock> YELLORIUM_FLUID = registerReactantFluidBlock(Reactants.Yellorium, Fluids.YELLORIUM_SOURCE);

        public static final Supplier<ReactantFluidBlock> CYANITE_FLUID = registerReactantFluidBlock(Reactants.Cyanite, Fluids.CYANITE_SOURCE);

        public static final Supplier<ReactantFluidBlock> BLUTONIUM_FLUID = registerReactantFluidBlock(Reactants.Blutonium, Fluids.BLUTONIUM_SOURCE);

        public static final Supplier<ReactantFluidBlock> MAGENTITE_FLUID = registerReactantFluidBlock(Reactants.Magentite, Fluids.MAGENTITE_SOURCE);

        public static final Supplier<ReactantFluidBlock> VERDERIUM_FLUID = registerReactantFluidBlock(Reactants.Verderium, Fluids.VERDERIUM_SOURCE);

        public static final Supplier<ReactantFluidBlock> ROSSINITE_FLUID = registerReactantFluidBlock(Reactants.Rossinite, Fluids.ROSSINITE_SOURCE);

        //endregion
        //region moderators

        public static final Supplier<LiquidBlock> CRYOMISI_FLUID = registerModeratorLiquidBlock("cryomisi_fluid", Fluids.CRYOMISI_SOURCE);

        public static final Supplier<LiquidBlock> TANGERIUM_FLUID = registerModeratorLiquidBlock("tangerium_fluid", Fluids.TANGERIUM_SOURCE);

        public static final Supplier<LiquidBlock> REDFRIGIUM_FLUID = registerModeratorLiquidBlock("redfrigium_fluid", Fluids.REDFRIGIUM_SOURCE);

        //endregion
        //endregion
        //region reactor basic

        public static final Supplier<MultiblockPartBlock<MultiblockReactor, IReactorPartType>> REACTOR_CASING_BASIC =
                registerReactorBlock("basic_reactorcasing", ReactorVariant.Basic, ReactorPartType.Casing);

        public static final Supplier<GlassBlock<MultiblockReactor, IReactorPartType>> REACTOR_GLASS_BASIC =
                registerReactorBlock("basic_reactorglass", ReactorVariant.Basic, ReactorPartType.Glass);

        public static final Supplier<GenericDeviceBlock<MultiblockReactor, IReactorPartType>> REACTOR_CONTROLLER_BASIC =
                registerReactorBlock("basic_reactorcontroller", ReactorVariant.Basic, ReactorPartType.Controller);

        public static final Supplier<ReactorFuelRodBlock> REACTOR_FUELROD_BASIC =
                registerReactorBlock("basic_reactorfuelrod", ReactorVariant.Basic, ReactorPartType.FuelRod);

        public static final Supplier<GenericDeviceBlock<MultiblockReactor, IReactorPartType>> REACTOR_CONTROLROD_BASIC =
                registerReactorBlock("basic_reactorcontrolrod", ReactorVariant.Basic, ReactorPartType.ControlRod);

        public static final Supplier<IOPortBlock<MultiblockReactor, IReactorPartType>> REACTOR_SOLID_ACCESSPORT_BASIC =
                registerReactorBlock("basic_reactorsolidaccessport", ReactorVariant.Basic, ReactorPartType.SolidAccessPort);

        public static final Supplier<GenericDeviceBlock<MultiblockReactor, IReactorPartType>> REACTOR_POWERTAP_FE_ACTIVE_BASIC =
                registerReactorBlock("basic_reactorpowertapfe_active", ReactorVariant.Basic, ReactorPartType.ActivePowerTapFE);

        public static final Supplier<GenericDeviceBlock<MultiblockReactor, IReactorPartType>> REACTOR_POWERTAP_FE_PASSIVE_BASIC =
                registerReactorBlock("basic_reactorpowertapfe_passive", ReactorVariant.Basic, ReactorPartType.PassivePowerTapFE);

        public static final Supplier<ReactorRedstonePortBlock> REACTOR_REDSTONEPORT_BASIC =
                registerReactorBlock("basic_reactorredstoneport", ReactorVariant.Basic, ReactorPartType.RedstonePort);

        public static final Supplier<GenericDeviceBlock<MultiblockReactor, IReactorPartType>> REACTOR_CHARGINGPORT_FE_BASIC =
                registerReactorBlock("basic_reactorchargingportfe", ReactorVariant.Basic, ReactorPartType.ChargingPortFE);

        //endregion
        //region reactor reinforced

        public static final Supplier<MultiblockPartBlock<MultiblockReactor, IReactorPartType>> REACTOR_CASING_REINFORCED =
                registerReactorBlock("reinforced_reactorcasing", ReactorVariant.Reinforced, ReactorPartType.Casing);

        public static final Supplier<GlassBlock<MultiblockReactor, IReactorPartType>> REACTOR_GLASS_REINFORCED =
                registerReactorBlock("reinforced_reactorglass", ReactorVariant.Reinforced, ReactorPartType.Glass);

        public static final Supplier<GenericDeviceBlock<MultiblockReactor, IReactorPartType>> REACTOR_CONTROLLER_REINFORCED =
                registerReactorBlock("reinforced_reactorcontroller", ReactorVariant.Reinforced, ReactorPartType.Controller);

        public static final Supplier<ReactorFuelRodBlock> REACTOR_FUELROD_REINFORCED =
                registerReactorBlock("reinforced_reactorfuelrod", ReactorVariant.Reinforced, ReactorPartType.FuelRod);

        public static final Supplier<GenericDeviceBlock<MultiblockReactor, IReactorPartType>> REACTOR_CONTROLROD_REINFORCED =
                registerReactorBlock("reinforced_reactorcontrolrod", ReactorVariant.Reinforced, ReactorPartType.ControlRod);

        public static final Supplier<IOPortBlock<MultiblockReactor, IReactorPartType>> REACTOR_SOLID_ACCESSPORT_REINFORCED =
                registerReactorBlock("reinforced_reactorsolidaccessport", ReactorVariant.Reinforced, ReactorPartType.SolidAccessPort);

        public static final Supplier<IOPortBlock<MultiblockReactor, IReactorPartType>> REACTOR_FLUID_ACCESSPORT_REINFORCED =
                registerReactorBlock("reinforced_reactorfluidaccessport", ReactorVariant.Reinforced, ReactorPartType.FluidAccessPort);

        public static final Supplier<GenericDeviceBlock<MultiblockReactor, IReactorPartType>> REACTOR_POWERTAP_FE_ACTIVE_REINFORCED =
                registerReactorBlock("reinforced_reactorpowertapfe_active", ReactorVariant.Reinforced, ReactorPartType.ActivePowerTapFE);

        public static final Supplier<GenericDeviceBlock<MultiblockReactor, IReactorPartType>> REACTOR_POWERTAP_FE_PASSIVE_REINFORCED =
                registerReactorBlock("reinforced_reactorpowertapfe_passive", ReactorVariant.Reinforced, ReactorPartType.PassivePowerTapFE);

        public static final Supplier<IOPortBlock<MultiblockReactor, IReactorPartType>> REACTOR_FLUIDTPORT_FORGE_ACTIVE_REINFORCED =
                registerReactorBlock("reinforced_reactorfluidport_forge_active", ReactorVariant.Reinforced, ReactorPartType.ActiveFluidPortForge);

        public static final Supplier<IOPortBlock<MultiblockReactor, IReactorPartType>> REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED =
                registerReactorBlock("reinforced_reactorfluidport_forge_passive", ReactorVariant.Reinforced, ReactorPartType.PassiveFluidPortForge);

        public static final Supplier<IOPortBlock<MultiblockReactor, IReactorPartType>> REACTOR_FLUIDPORT_MEKANISM_PASSIVE_REINFORCED =
                registerReactorBlock("reinforced_reactorfluidport_mekanism_passive", ReactorVariant.Reinforced, ReactorPartType.PassiveFluidPortMekanism);

        public static final Supplier<GenericDeviceBlock<MultiblockReactor, IReactorPartType>> REACTOR_CREATIVE_WATER_GENERATOR_REINFORCED =
                registerReactorBlock("reinforced_reactorcreativewatergenerator", ReactorVariant.Reinforced, ReactorPartType.CreativeWaterGenerator);

        public static final Supplier<ReactorRedstonePortBlock> REACTOR_REDSTONEPORT_REINFORCED =
                registerReactorBlock("reinforced_reactorredstoneport", ReactorVariant.Reinforced, ReactorPartType.RedstonePort);

        public static final Supplier<GenericDeviceBlock<MultiblockReactor, IReactorPartType>> REACTOR_COMPUTERPORT_REINFORCED =
                registerReactorBlock("reinforced_reactorcomputerport", ReactorVariant.Reinforced, ReactorPartType.ComputerPort);

        public static final Supplier<GenericDeviceBlock<MultiblockReactor, IReactorPartType>> REACTOR_CHARGINGPORT_FE_REINFORCED =
                registerReactorBlock("reinforced_reactorchargingportfe", ReactorVariant.Reinforced, ReactorPartType.ChargingPortFE);

        //endregion
        //region turbine basic

        public static final Supplier<MultiblockPartBlock<MultiblockTurbine, ITurbinePartType>> TURBINE_CASING_BASIC =
                registerTurbineBlock("basic_turbinecasing", TurbineVariant.Basic, TurbinePartType.Casing);

        public static final Supplier<GlassBlock<MultiblockTurbine, ITurbinePartType>> TURBINE_GLASS_BASIC =
                registerTurbineBlock("basic_turbineglass", TurbineVariant.Basic, TurbinePartType.Glass);

        public static final Supplier<GenericDeviceBlock<MultiblockTurbine, ITurbinePartType>> TURBINE_CONTROLLER_BASIC =
                registerTurbineBlock("basic_turbinecontroller", TurbineVariant.Basic, TurbinePartType.Controller);

        public static final Supplier<TurbineRotorBearingBlock> TURBINE_ROTORBEARING_BASIC =
                registerTurbineBlock("basic_turbinerotorbearing", TurbineVariant.Basic, TurbinePartType.RotorBearing);

        public static final Supplier<TurbineRotorComponentBlock> TURBINE_ROTORSHAFT_BASIC =
                registerTurbineBlock("basic_turbinerotorshaft", TurbineVariant.Basic, TurbinePartType.RotorShaft);

        public static final Supplier<TurbineRotorComponentBlock> TURBINE_ROTORBLADE_BASIC =
                registerTurbineBlock("basic_turbinerotorblade", TurbineVariant.Basic, TurbinePartType.RotorBlade);

        public static final Supplier<GenericDeviceBlock<MultiblockTurbine, ITurbinePartType>> TURBINE_POWERTAP_FE_ACTIVE_BASIC =
                registerTurbineBlock("basic_turbinepowertapfe_active", TurbineVariant.Basic, TurbinePartType.ActivePowerTapFE);

        public static final Supplier<GenericDeviceBlock<MultiblockTurbine, ITurbinePartType>> TURBINE_POWERTAP_FE_PASSIVE_BASIC =
                registerTurbineBlock("basic_turbinepowertapfe_passive", TurbineVariant.Basic, TurbinePartType.PassivePowerTapFE);

        public static final Supplier<IOPortBlock<MultiblockTurbine, ITurbinePartType>> TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC =
                registerTurbineBlock("basic_turbinefluidport_forge_active", TurbineVariant.Basic, TurbinePartType.ActiveFluidPortForge);

        public static final Supplier<IOPortBlock<MultiblockTurbine, ITurbinePartType>> TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC =
                registerTurbineBlock("basic_turbinefluidport_forge_passive", TurbineVariant.Basic, TurbinePartType.PassiveFluidPortForge);

        public static final Supplier<GenericDeviceBlock<MultiblockTurbine, ITurbinePartType>> TURBINE_CREATIVE_STEAM_GENERATOR_BASIC =
                registerTurbineBlock("basic_turbinecreativesteamgenerator", TurbineVariant.Basic, TurbinePartType.CreativeSteamGenerator);

        public static final Supplier<TurbineRedstonePortBlock> TURBINE_REDSTONEPORT_BASIC =
                registerTurbineBlock("basic_turbineredstoneport", TurbineVariant.Basic, TurbinePartType.RedstonePort);

        public static final Supplier<GenericDeviceBlock<MultiblockTurbine, ITurbinePartType>> TURBINE_CHARGINGPORT_FE_BASIC =
                registerTurbineBlock("basic_turbinechargingportfe", TurbineVariant.Basic, TurbinePartType.ChargingPortFE);

        //endregion
        //region turbine reinforced

        public static final Supplier<MultiblockPartBlock<MultiblockTurbine, ITurbinePartType>> TURBINE_CASING_REINFORCED =
                registerTurbineBlock("reinforced_turbinecasing", TurbineVariant.Reinforced, TurbinePartType.Casing);

        public static final Supplier<GlassBlock<MultiblockTurbine, ITurbinePartType>> TURBINE_GLASS_REINFORCED =
                registerTurbineBlock("reinforced_turbineglass", TurbineVariant.Reinforced, TurbinePartType.Glass);

        public static final Supplier<GenericDeviceBlock<MultiblockTurbine, ITurbinePartType>> TURBINE_CONTROLLER_REINFORCED =
                registerTurbineBlock("reinforced_turbinecontroller", TurbineVariant.Reinforced, TurbinePartType.Controller);

        public static final Supplier<TurbineRotorBearingBlock> TURBINE_ROTORBEARING_REINFORCED =
                registerTurbineBlock("reinforced_turbinerotorbearing", TurbineVariant.Reinforced, TurbinePartType.RotorBearing);

        public static final Supplier<TurbineRotorComponentBlock> TURBINE_ROTORSHAFT_REINFORCED =
                registerTurbineBlock("reinforced_turbinerotorshaft", TurbineVariant.Reinforced, TurbinePartType.RotorShaft);

        public static final Supplier<TurbineRotorComponentBlock> TURBINE_ROTORBLADE_REINFORCED =
                registerTurbineBlock("reinforced_turbinerotorblade", TurbineVariant.Reinforced, TurbinePartType.RotorBlade);

        public static final Supplier<GenericDeviceBlock<MultiblockTurbine, ITurbinePartType>> TURBINE_POWERTAP_FE_ACTIVE_REINFORCED =
                registerTurbineBlock("reinforced_turbinepowertapfe_active", TurbineVariant.Reinforced, TurbinePartType.ActivePowerTapFE);

        public static final Supplier<GenericDeviceBlock<MultiblockTurbine, ITurbinePartType>> TURBINE_POWERTAP_FE_PASSIVE_REINFORCED =
                registerTurbineBlock("reinforced_turbinepowertapfe_passive", TurbineVariant.Reinforced, TurbinePartType.PassivePowerTapFE);

        public static final Supplier<IOPortBlock<MultiblockTurbine, ITurbinePartType>> TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED =
                registerTurbineBlock("reinforced_turbinefluidport_forge_active", TurbineVariant.Reinforced, TurbinePartType.ActiveFluidPortForge);

        public static final Supplier<IOPortBlock<MultiblockTurbine, ITurbinePartType>> TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED =
                registerTurbineBlock("reinforced_turbinefluidport_forge_passive", TurbineVariant.Reinforced, TurbinePartType.PassiveFluidPortForge);

        public static final Supplier<GenericDeviceBlock<MultiblockTurbine, ITurbinePartType>> TURBINE_CREATIVE_STEAM_GENERATOR_REINFORCED =
                registerTurbineBlock("reinforced_turbinecreativesteamgenerator", TurbineVariant.Reinforced, TurbinePartType.CreativeSteamGenerator);

        public static final Supplier<TurbineRedstonePortBlock> TURBINE_REDSTONEPORT_REINFORCED =
                registerTurbineBlock("reinforced_turbineredstoneport", TurbineVariant.Reinforced, TurbinePartType.RedstonePort);

        public static final Supplier<GenericDeviceBlock<MultiblockTurbine, ITurbinePartType>> TURBINE_COMPUTERPORT_REINFORCED =
                registerTurbineBlock("reinforced_turbinecomputerport", TurbineVariant.Reinforced, TurbinePartType.ComputerPort);

        public static final Supplier<GenericDeviceBlock<MultiblockTurbine, ITurbinePartType>> TURBINE_CHARGINGPORT_FE_REINFORCED =
                registerTurbineBlock("reinforced_turbinechargingportfe", TurbineVariant.Reinforced, TurbinePartType.ChargingPortFE);

        //endregion
        //region reprocessor

        public static final Supplier<MultiblockPartBlock<MultiblockReprocessor, IReprocessorPartType>> REPROCESSOR_CASING =
                registerReprocessorBlock("reprocessorcasing", ReprocessorPartType.Casing);

        public static final Supplier<GlassBlock<MultiblockReprocessor, IReprocessorPartType>> REPROCESSOR_GLASS =
                registerReprocessorBlock("reprocessorglass", ReprocessorPartType.Glass);

        public static final Supplier<GenericDeviceBlock<MultiblockReprocessor, IReprocessorPartType>> REPROCESSOR_CONTROLLER =
                registerReprocessorBlock("reprocessorcontroller", ReprocessorPartType.Controller);

        public static final Supplier<GenericDeviceBlock<MultiblockReprocessor, IReprocessorPartType>> REPROCESSOR_WASTEINJECTOR =
                registerReprocessorBlock("reprocessorwasteinjector", ReprocessorPartType.WasteInjector);

        public static final Supplier<GenericDeviceBlock<MultiblockReprocessor, IReprocessorPartType>> REPROCESSOR_FLUIDINJECTOR =
                registerReprocessorBlock("reprocessorfluidinjector", ReprocessorPartType.FluidInjector);

        public static final Supplier<GenericDeviceBlock<MultiblockReprocessor, IReprocessorPartType>> REPROCESSOR_OUTPUTPORT =
                registerReprocessorBlock("reprocessoroutputport", ReprocessorPartType.OutputPort);

        public static final Supplier<GenericDeviceBlock<MultiblockReprocessor, IReprocessorPartType>> REPROCESSOR_POWERPORT =
                registerReprocessorBlock("reprocessorpowerport", ReprocessorPartType.PowerPort);

        public static final Supplier<GenericDeviceBlock<MultiblockReprocessor, IReprocessorPartType>> REPROCESSOR_COLLECTOR =
                registerReprocessorBlock("reprocessorcollector", ReprocessorPartType.Collector);

        //endregion
        //region fluidizer

        public static final Supplier<MultiblockPartBlock<MultiblockFluidizer, IFluidizerPartType>> FLUIDIZER_CASING =
                registerFluidizerBlock("fluidizercasing", FluidizerPartType.Casing);

        public static final Supplier<GlassBlock<MultiblockFluidizer, IFluidizerPartType>> FLUIDIZER_GLASS =
                registerFluidizerBlock("fluidizerglass", FluidizerPartType.Glass);

        public static final Supplier<GenericDeviceBlock<MultiblockFluidizer, IFluidizerPartType>> FLUIDIZER_CONTROLLER =
                registerFluidizerBlock("fluidizercontroller", FluidizerPartType.Controller);

        public static final Supplier<GenericDeviceBlock<MultiblockFluidizer, IFluidizerPartType>> FLUIDIZER_SOLIDINJECTOR =
                registerFluidizerBlock("fluidizersolidinjector", FluidizerPartType.SolidInjector);

        public static final Supplier<GenericDeviceBlock<MultiblockFluidizer, IFluidizerPartType>> FLUIDIZER_FLUIDINJECTOR =
                registerFluidizerBlock("fluidizerfluidinjector", FluidizerPartType.FluidInjector);

        public static final Supplier<GenericDeviceBlock<MultiblockFluidizer, IFluidizerPartType>> FLUIDIZER_OUTPUTPORT =
                registerFluidizerBlock("fluidizeroutputport", FluidizerPartType.OutputPort);

        public static final Supplier<GenericDeviceBlock<MultiblockFluidizer, IFluidizerPartType>> FLUIDIZER_POWERPORT =
                registerFluidizerBlock("fluidizerpowerport", FluidizerPartType.PowerPort);

        //endregion
        //region energizer

        public static final Supplier<ModBlock> ENERGIZER_CELL = registerMetalBlock("energizercell", DyeColor.GRAY);

        public static final Supplier<MultiblockPartBlock<MultiBlockEnergizer, IEnergizerPartType>> ENERGIZER_CASING =
                registerEnergizerBlock("energizercasing", EnergizerPartType.Casing);

        public static final Supplier<GenericDeviceBlock<MultiBlockEnergizer, IEnergizerPartType>> ENERGIZER_CONTROLLER =
                registerEnergizerBlock("energizercontroller", EnergizerPartType.Controller);

        public static final Supplier<PowerTapBlock<MultiBlockEnergizer, IEnergizerPartType>> ENERGIZER_POWERPORT_FE =
                registerEnergizerBlock("energizerpowerportfe", EnergizerPartType.PowerPortFE);

        public static final Supplier<PowerTapBlock<MultiBlockEnergizer, IEnergizerPartType>> ENERGIZER_CHARGINGPORT_FE =
                registerEnergizerBlock("energizerchargingportfe", EnergizerPartType.ChargingPortFE);

        public static final Supplier<GenericDeviceBlock<MultiBlockEnergizer, IEnergizerPartType>> ENERGIZER_STATUS_DISPLAY =
                registerEnergizerBlock("energizerstatus", EnergizerPartType.StatusDisplay);

        //endregion
        //region internals

        private static Supplier<ModBlock> registerMetalBlock(final String name, final DyeColor color) {
            return BLOCKS.register(name,
                    () -> new ModBlock(Block.Properties.of().mapColor(color).sound(SoundType.METAL)));
        }

        private static Supplier<ModBlock> registerOreBlock(final String name, final DyeColor color) {
            return registerOreBlock(name, color, 3, 5);
        }

        private static Supplier<ModBlock> registerOreBlock(final String name, final DyeColor color,
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
        private static <T extends MultiblockPartBlock<MultiblockReactor, IReactorPartType>>
        Supplier<T> registerReactorBlock(final String name,
                                         final ReactorVariant variant,
                                         final IReactorPartType partType) {
            return BLOCKS.register(name, () -> (T) (partType.createBlock(variant)));
        }

        @SuppressWarnings("unchecked")
        private static <T extends MultiblockPartBlock<MultiblockTurbine, ITurbinePartType>>
        Supplier<T> registerTurbineBlock(final String name,
                                         final TurbineVariant variant,
                                         final ITurbinePartType partType) {
            return BLOCKS.register(name, () -> (T) (partType.createBlock(variant)));
        }

        @SuppressWarnings("unchecked")
        private static <T extends MultiblockPartBlock<MultiblockReprocessor, IReprocessorPartType>>
        Supplier<T> registerReprocessorBlock(final String name, final ReprocessorPartType partType) {
            return BLOCKS.register(name, () -> (T) (partType.createBlock()));
        }

        @SuppressWarnings("unchecked")
        private static <T extends MultiblockPartBlock<MultiblockFluidizer, IFluidizerPartType>>
        Supplier<T> registerFluidizerBlock(final String name, final FluidizerPartType partType) {
            return BLOCKS.register(name, () -> (T) (partType.createBlock()));
        }

        @SuppressWarnings("unchecked")
        private static <T extends MultiblockPartBlock<MultiBlockEnergizer, IEnergizerPartType>>
        Supplier<T> registerEnergizerBlock(String name, EnergizerPartType partType) {
            return BLOCKS.register(name, () -> (T) (partType.createBlock(EnergizerVariant.INSTANCE)));
        }

        private static Supplier<LiquidBlock> registerModeratorLiquidBlock(String name,
                                                                          Supplier<@NotNull FlowingFluid> source) {
            return BLOCKS.register(name, () -> new LiquidBlock(source.get(), BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WATER)
                    .replaceable()
                    .pushReaction(PushReaction.DESTROY)
                    .liquid()
                    .noCollission()
                    .strength(100.0F)
                    .noLootTable()));
        }

        private static Supplier<ReactantFluidBlock> registerReactantFluidBlock(final Reactants reactant,
                                                                               final Supplier<? extends FlowingFluid> fluid) {
            return BLOCKS.register(reactant.getFluidName(), () -> new ReactantFluidBlock(reactant, fluid));
        }

        //endregion
    }

    @SuppressWarnings("unused")
    public static final class Items {

        private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, ExtremeReactors.MOD_ID);

        static void initialize(final IEventBus bus) {
            ITEMS.register(bus);
        }

        //region metals

        public static final Supplier<ModItem> YELLORIUM_INGOT = registerItemGeneric(Reactants.Yellorium.getIngotName());
        public static final Supplier<ModItem> RAW_YELLORIUM = registerItemGeneric("raw_yellorium");
        public static final Supplier<ModItem> CYANITE_INGOT = registerItemGeneric(Reactants.Cyanite.getIngotName());
        public static final Supplier<ModItem> GRAPHITE_INGOT = registerItemGeneric("graphite_ingot");
        public static final Supplier<ModItem> BLUTONIUM_INGOT = registerItemGeneric(Reactants.Blutonium.getIngotName());
        public static final Supplier<ModItem> MAGENTITE_INGOT = registerItemGeneric(Reactants.Magentite.getIngotName());
        public static final Supplier<ModItem> LUDICRITE_INGOT = registerItemGeneric("ludicrite_ingot");
        public static final Supplier<ModItem> RIDICULITE_INGOT = registerItemGeneric("ridiculite_ingot");
        public static final Supplier<ModItem> INANITE_INGOT = registerItemGeneric("inanite_ingot");
        public static final Supplier<ModItem> INSANITE_INGOT = registerItemGeneric("insanite_ingot");

        public static final Supplier<ModItem> YELLORIUM_NUGGET = registerItemGeneric(Reactants.Yellorium.getNuggetName());
        public static final Supplier<ModItem> BLUTONIUM_NUGGET = registerItemGeneric(Reactants.Blutonium.getNuggetName());

        public static final Supplier<ModItem> YELLORIUM_DUST = registerItemGeneric(Reactants.Yellorium.getDustName());
        public static final Supplier<ModItem> CYANITE_DUST = registerItemGeneric(Reactants.Cyanite.getDustName());
        public static final Supplier<ModItem> GRAPHITE_DUST = registerItemGeneric("graphite_dust");
        public static final Supplier<ModItem> BLUTONIUM_DUST = registerItemGeneric(Reactants.Blutonium.getDustName());
        public static final Supplier<ModItem> MAGENTITE_DUST = registerItemGeneric(Reactants.Magentite.getDustName());
        public static final Supplier<ModItem> LUDICRITE_DUST = registerItemGeneric("ludicrite_dust");
        public static final Supplier<ModItem> RIDICULITE_DUST = registerItemGeneric("ridiculite_dust");
        public static final Supplier<ModItem> INANITE_DUST = registerItemGeneric("inanite_dust");
        public static final Supplier<ModItem> INSANITE_DUST = registerItemGeneric("insanite_dust");

        public static final Supplier<BlockItem> YELLORIUM_BLOCK = registerItemBlock(Reactants.Yellorium.getBlockName(), () -> Blocks.YELLORIUM_BLOCK);
        public static final Supplier<BlockItem> RAW_YELLORIUM_BLOCK = registerItemBlock("raw_yellorium_block", () -> Blocks.RAW_YELLORIUM_BLOCK);
        public static final Supplier<BlockItem> CYANITE_BLOCK = registerItemBlock(Reactants.Cyanite.getBlockName(), () -> Blocks.CYANITE_BLOCK);
        public static final Supplier<BlockItem> GRAPHITE_BLOCK = registerItemBlock("graphite_block", () -> Blocks.GRAPHITE_BLOCK);
        public static final Supplier<BlockItem> BLUTONIUM_BLOCK = registerItemBlock(Reactants.Blutonium.getBlockName(), () -> Blocks.BLUTONIUM_BLOCK);
        public static final Supplier<BlockItem> MAGENTITE_BLOCK = registerItemBlock(Reactants.Magentite.getBlockName(), () -> Blocks.MAGENTITE_BLOCK);
        public static final Supplier<BlockItem> LUDICRITE_BLOCK = registerItemBlock("ludicrite_block", () -> Blocks.LUDICRITE_BLOCK);
        public static final Supplier<BlockItem> RIDICULITE_BLOCK = registerItemBlock("ridiculite_block", () -> Blocks.RIDICULITE_BLOCK);
        public static final Supplier<BlockItem> INANITE_BLOCK = registerItemBlock("inanite_block", () -> Blocks.INANITE_BLOCK);
        public static final Supplier<BlockItem> INSANITE_BLOCK = registerItemBlock("insanite_block", () -> Blocks.INSANITE_BLOCK);

        //endregion
        //region crystals

        public static final Supplier<ModItem> ANGLESITE_CRYSTAL = registerItemGeneric("anglesite_crystal");
        public static final Supplier<ModItem> BENITOITE_CRYSTAL = registerItemGeneric("benitoite_crystal");

        //endregion
        //region ores

        public static final Supplier<BlockItem> YELLORITE_ORE_BLOCK = registerItemBlock("yellorite_ore", () -> Blocks.YELLORITE_ORE_BLOCK);
        public static final Supplier<BlockItem> DEEPSLATE_YELLORITE_ORE_BLOCK = registerItemBlock("deepslate_yellorite_ore", () -> Blocks.DEEPSLATE_YELLORITE_ORE_BLOCK);
        public static final Supplier<BlockItem> ANGLESITE_ORE_BLOCK = registerItemBlock("anglesite_ore", () -> Blocks.ANGLESITE_ORE_BLOCK);
        public static final Supplier<BlockItem> BENITOITE_ORE_BLOCK = registerItemBlock("benitoite_ore", () -> Blocks.BENITOITE_ORE_BLOCK);

        //endregion
        //region fluids

        public static final Supplier<TintedBucketItem> STEAM_BUCKET = registerTintedBucket("steam_bucket", Fluids.STEAM_SOURCE);

        //region reactants

        public static final Supplier<TintedBucketItem> YELLORIUM_BUCKET = registerReactantBucket(Reactants.Yellorium, Fluids.YELLORIUM_SOURCE);
        public static final Supplier<TintedBucketItem> CYANITE_BUCKET = registerReactantBucket(Reactants.Cyanite, Fluids.CYANITE_SOURCE);
        public static final Supplier<TintedBucketItem> BLUTONIUM_BUCKET = registerReactantBucket(Reactants.Blutonium, Fluids.BLUTONIUM_SOURCE);
        public static final Supplier<TintedBucketItem> MAGENTITE_BUCKET = registerReactantBucket(Reactants.Magentite, Fluids.MAGENTITE_SOURCE);
        public static final Supplier<TintedBucketItem> VERDERIUM_BUCKET = registerReactantBucket(Reactants.Verderium, Fluids.VERDERIUM_SOURCE);
        public static final Supplier<TintedBucketItem> ROSSINITE_BUCKET = registerReactantBucket(Reactants.Rossinite, Fluids.ROSSINITE_SOURCE);

        //endregion
        //region moderators

        public static final Supplier<TintedBucketItem> CRYOMISI_BUCKET = registerTintedBucket("cryomisi_bucket", Fluids.CRYOMISI_SOURCE);

        public static final Supplier<TintedBucketItem> TANGERIUM_BUCKET = registerTintedBucket("tangerium_bucket", Fluids.TANGERIUM_SOURCE);

        public static final Supplier<TintedBucketItem> REDFRIGIUM_BUCKET = registerTintedBucket("redfrigium_bucket", Fluids.REDFRIGIUM_SOURCE);

        //endregion
        //endregion
        //region reactor basic

        public static final Supplier<BlockItem> REACTOR_CASING_BASIC = registerItemBlock("basic_reactorcasing", () -> Blocks.REACTOR_CASING_BASIC::get);
        public static final Supplier<BlockItem> REACTOR_GLASS_BASIC = registerItemBlock("basic_reactorglass", () -> Blocks.REACTOR_GLASS_BASIC::get);
        public static final Supplier<BlockItem> REACTOR_CONTROLLER_BASIC = registerItemBlock("basic_reactorcontroller", () -> Blocks.REACTOR_CONTROLLER_BASIC::get);
        public static final Supplier<BlockItem> REACTOR_FUELROD_BASIC = registerItemBlock("basic_reactorfuelrod", () -> Blocks.REACTOR_FUELROD_BASIC::get);
        public static final Supplier<BlockItem> REACTOR_CONTROLROD_BASIC = registerItemBlock("basic_reactorcontrolrod", () -> Blocks.REACTOR_CONTROLROD_BASIC::get);
        public static final Supplier<BlockItem> REACTOR_SOLID_ACCESSPORT_BASIC = registerItemBlock("basic_reactorsolidaccessport", () -> Blocks.REACTOR_SOLID_ACCESSPORT_BASIC::get);
        public static final Supplier<BlockItem> REACTOR_POWERTAP_FE_ACTIVE_BASIC = registerItemBlock("basic_reactorpowertapfe_active", () -> Blocks.REACTOR_POWERTAP_FE_ACTIVE_BASIC::get);
        public static final Supplier<BlockItem> REACTOR_POWERTAP_FE_PASSIVE_BASIC = registerItemBlock("basic_reactorpowertapfe_passive", () -> Blocks.REACTOR_POWERTAP_FE_PASSIVE_BASIC::get);
        public static final Supplier<BlockItem> REACTOR_REDSTONEPORT_BASIC = registerItemBlock("basic_reactorredstoneport", () -> Blocks.REACTOR_REDSTONEPORT_BASIC::get);
        public static final Supplier<BlockItem> REACTOR_CHARGINGPORT_FE_BASIC = registerItemBlock("basic_reactorchargingportfe", () -> Blocks.REACTOR_CHARGINGPORT_FE_BASIC::get);

        //endregion
        //region reactor reinforced

        public static final Supplier<BlockItem> REACTOR_CASING_REINFORCED = registerItemBlock("reinforced_reactorcasing", () -> Blocks.REACTOR_CASING_REINFORCED::get);
        public static final Supplier<BlockItem> REACTOR_GLASS_REINFORCED = registerItemBlock("reinforced_reactorglass", () -> Blocks.REACTOR_GLASS_REINFORCED::get);
        public static final Supplier<BlockItem> REACTOR_CONTROLLER_REINFORCED = registerItemBlock("reinforced_reactorcontroller", () -> Blocks.REACTOR_CONTROLLER_REINFORCED::get);
        public static final Supplier<BlockItem> REACTOR_FUELROD_REINFORCED = registerItemBlock("reinforced_reactorfuelrod", () -> Blocks.REACTOR_FUELROD_REINFORCED::get);
        public static final Supplier<BlockItem> REACTOR_CONTROLROD_REINFORCED = registerItemBlock("reinforced_reactorcontrolrod", () -> Blocks.REACTOR_CONTROLROD_REINFORCED::get);
        public static final Supplier<BlockItem> REACTOR_SOLID_ACCESSPORT_REINFORCED = registerItemBlock("reinforced_reactorsolidaccessport", () -> Blocks.REACTOR_SOLID_ACCESSPORT_REINFORCED::get);
        public static final Supplier<BlockItem> REACTOR_FLUID_ACCESSPORT_REINFORCED = registerItemBlock("reinforced_reactorfluidaccessport", () -> Blocks.REACTOR_FLUID_ACCESSPORT_REINFORCED::get);
        public static final Supplier<BlockItem> REACTOR_POWERTAP_FE_ACTIVE_REINFORCED = registerItemBlock("reinforced_reactorpowertapfe_active", () -> Blocks.REACTOR_POWERTAP_FE_ACTIVE_REINFORCED::get);
        public static final Supplier<BlockItem> REACTOR_POWERTAP_FE_PASSIVE_REINFORCED = registerItemBlock("reinforced_reactorpowertapfe_passive", () -> Blocks.REACTOR_POWERTAP_FE_PASSIVE_REINFORCED::get);
        public static final Supplier<BlockItem> REACTOR_COMPUTERPORT_REINFORCED = registerItemBlock("reinforced_reactorcomputerport", () -> Blocks.REACTOR_COMPUTERPORT_REINFORCED::get);
        public static final Supplier<BlockItem> REACTOR_REDSTONEPORT_REINFORCED = registerItemBlock("reinforced_reactorredstoneport", () -> Blocks.REACTOR_REDSTONEPORT_REINFORCED::get);
        public static final Supplier<BlockItem> REACTOR_FLUIDPORT_FORGE_ACTIVE_REINFORCED = registerItemBlock("reinforced_reactorfluidport_forge_active", () -> Blocks.REACTOR_FLUIDTPORT_FORGE_ACTIVE_REINFORCED::get);
        public static final Supplier<BlockItem> REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED = registerItemBlock("reinforced_reactorfluidport_forge_passive", () -> Blocks.REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED::get);
        public static final Supplier<BlockItem> REACTOR_FLUIDPORT_MEKANISM_PASSIVE_REINFORCED = registerItemBlock("reinforced_reactorfluidport_mekanism_passive", () -> Blocks.REACTOR_FLUIDPORT_MEKANISM_PASSIVE_REINFORCED::get);
        public static final Supplier<BlockItem> REACTOR_CREATIVE_WATER_GENERATOR_REINFORCED = registerItemBlock("reinforced_reactorcreativewatergenerator", () -> Blocks.REACTOR_CREATIVE_WATER_GENERATOR_REINFORCED::get);
        public static final Supplier<BlockItem> REACTOR_CHARGINGPORT_FE_REINFORCED = registerItemBlock("reinforced_reactorchargingportfe", () -> Blocks.REACTOR_CHARGINGPORT_FE_REINFORCED::get);

        //endregion
        //region turbine basic

        public static final Supplier<BlockItem> TURBINE_CASING_BASIC = registerItemBlock("basic_turbinecasing", () -> Blocks.TURBINE_CASING_BASIC::get);
        public static final Supplier<BlockItem> TURBINE_GLASS_BASIC = registerItemBlock("basic_turbineglass", () -> Blocks.TURBINE_GLASS_BASIC::get);
        public static final Supplier<BlockItem> TURBINE_CONTROLLER_BASIC = registerItemBlock("basic_turbinecontroller", () -> Blocks.TURBINE_CONTROLLER_BASIC::get);
        public static final Supplier<BlockItem> TURBINE_ROTORBEARING_BASIC = registerItemBlock("basic_turbinerotorbearing", () -> Blocks.TURBINE_ROTORBEARING_BASIC::get);
        public static final Supplier<BlockItem> TURBINE_ROTORSHAFT_BASIC = registerItemBlock("basic_turbinerotorshaft", () -> Blocks.TURBINE_ROTORSHAFT_BASIC::get);
        public static final Supplier<BlockItem> TURBINE_ROTORBLADE_BASIC = registerItemBlock("basic_turbinerotorblade", () -> Blocks.TURBINE_ROTORBLADE_BASIC::get);
        public static final Supplier<BlockItem> TURBINE_POWERTAP_FE_ACTIVE_BASIC = registerItemBlock("basic_turbinepowertapfe_active", () -> Blocks.TURBINE_POWERTAP_FE_ACTIVE_BASIC::get);
        public static final Supplier<BlockItem> TURBINE_POWERTAP_FE_PASSIVE_BASIC = registerItemBlock("basic_turbinepowertapfe_passive", () -> Blocks.TURBINE_POWERTAP_FE_PASSIVE_BASIC::get);
        public static final Supplier<BlockItem> TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC = registerItemBlock("basic_turbinefluidport_forge_active", () -> Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC::get);
        public static final Supplier<BlockItem> TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC = registerItemBlock("basic_turbinefluidport_forge_passive", () -> Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC::get);
        public static final Supplier<BlockItem> TURBINE_REDSTONEPORT_BASIC = registerItemBlock("basic_turbineredstoneport", () -> Blocks.TURBINE_REDSTONEPORT_BASIC::get);
        public static final Supplier<BlockItem> TURBINE_CREATIVE_STEAM_GENERATOR_BASIC = registerItemBlock("basic_turbinecreativesteamgenerator", () -> Blocks.TURBINE_CREATIVE_STEAM_GENERATOR_BASIC::get);
        public static final Supplier<BlockItem> TURBINE_CHARGINGPORT_FE_BASIC = registerItemBlock("basic_turbinechargingportfe", () -> Blocks.TURBINE_CHARGINGPORT_FE_BASIC::get);

        //endregion
        //region turbine reinforced

        public static final Supplier<BlockItem> TURBINE_CASING_REINFORCED = registerItemBlock("reinforced_turbinecasing", () -> Blocks.TURBINE_CASING_REINFORCED::get);
        public static final Supplier<BlockItem> TURBINE_GLASS_REINFORCED = registerItemBlock("reinforced_turbineglass", () -> Blocks.TURBINE_GLASS_REINFORCED::get);
        public static final Supplier<BlockItem> TURBINE_CONTROLLER_REINFORCED = registerItemBlock("reinforced_turbinecontroller", () -> Blocks.TURBINE_CONTROLLER_REINFORCED::get);
        public static final Supplier<BlockItem> TURBINE_ROTORBEARING_REINFORCED = registerItemBlock("reinforced_turbinerotorbearing", () -> Blocks.TURBINE_ROTORBEARING_REINFORCED::get);
        public static final Supplier<BlockItem> TURBINE_ROTORSHAFT_REINFORCED = registerItemBlock("reinforced_turbinerotorshaft", () -> Blocks.TURBINE_ROTORSHAFT_REINFORCED::get);
        public static final Supplier<BlockItem> TURBINE_ROTORBLADE_REINFORCED = registerItemBlock("reinforced_turbinerotorblade", () -> Blocks.TURBINE_ROTORBLADE_REINFORCED::get);
        public static final Supplier<BlockItem> TURBINE_POWERTAP_FE_ACTIVE_REINFORCED = registerItemBlock("reinforced_turbinepowertapfe_active", () -> Blocks.TURBINE_POWERTAP_FE_ACTIVE_REINFORCED::get);
        public static final Supplier<BlockItem> TURBINE_POWERTAP_FE_PASSIVE_REINFORCED = registerItemBlock("reinforced_turbinepowertapfe_passive", () -> Blocks.TURBINE_POWERTAP_FE_PASSIVE_REINFORCED::get);
        public static final Supplier<BlockItem> TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED = registerItemBlock("reinforced_turbinefluidport_forge_active", () -> Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED::get);
        public static final Supplier<BlockItem> TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED = registerItemBlock("reinforced_turbinefluidport_forge_passive", () -> Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED::get);
        public static final Supplier<BlockItem> TURBINE_REDSTONEPORT_REINFORCED = registerItemBlock("reinforced_turbineredstoneport", () -> Blocks.TURBINE_REDSTONEPORT_REINFORCED::get);
        public static final Supplier<BlockItem> TURBINE_COMPUTERPORT_REINFORCED = registerItemBlock("reinforced_turbinecomputerport", () -> Blocks.TURBINE_COMPUTERPORT_REINFORCED::get);
        public static final Supplier<BlockItem> TURBINE_CREATIVE_STEAM_GENERATOR_REINFORCED = registerItemBlock("reinforced_turbinecreativesteamgenerator", () -> Blocks.TURBINE_CREATIVE_STEAM_GENERATOR_REINFORCED::get);
        public static final Supplier<BlockItem> TURBINE_CHARGINGPORT_FE_REINFORCED = registerItemBlock("reinforced_turbinechargingportfe", () -> Blocks.TURBINE_CHARGINGPORT_FE_REINFORCED::get);

        //endregion
        //region reprocessor

        public static final Supplier<BlockItem> REPROCESSOR_CASING = registerItemBlock("reprocessorcasing", () -> Blocks.REPROCESSOR_CASING::get);
        public static final Supplier<BlockItem> REPROCESSOR_GLASS = registerItemBlock("reprocessorglass", () -> Blocks.REPROCESSOR_GLASS::get);
        public static final Supplier<BlockItem> REPROCESSOR_CONTROLLER = registerItemBlock("reprocessorcontroller", () -> Blocks.REPROCESSOR_CONTROLLER::get);
        public static final Supplier<BlockItem> REPROCESSOR_WASTEINJECTOR = registerItemBlock("reprocessorwasteinjector", () -> Blocks.REPROCESSOR_WASTEINJECTOR::get);
        public static final Supplier<BlockItem> REPROCESSOR_FLUIDINJECTOR = registerItemBlock("reprocessorfluidinjector", () -> Blocks.REPROCESSOR_FLUIDINJECTOR::get);
        public static final Supplier<BlockItem> REPROCESSOR_OUTPUTPORT = registerItemBlock("reprocessoroutputport", () -> Blocks.REPROCESSOR_OUTPUTPORT::get);
        public static final Supplier<BlockItem> REPROCESSOR_POWERPORT = registerItemBlock("reprocessorpowerport", () -> Blocks.REPROCESSOR_POWERPORT::get);
        public static final Supplier<BlockItem> REPROCESSOR_COLLECTOR = registerItemBlock("reprocessorcollector", () -> Blocks.REPROCESSOR_COLLECTOR::get);

        //endregion
        //region fluidizer

        public static final Supplier<BlockItem> FLUIDIZER_CASING = registerItemBlock("fluidizercasing", () -> Blocks.FLUIDIZER_CASING::get);
        public static final Supplier<BlockItem> FLUIDIZER_GLASS = registerItemBlock("fluidizerglass", () -> Blocks.FLUIDIZER_GLASS::get);
        public static final Supplier<BlockItem> FLUIDIZER_CONTROLLER = registerItemBlock("fluidizercontroller", () -> Blocks.FLUIDIZER_CONTROLLER::get);
        public static final Supplier<BlockItem> FLUIDIZER_SOLIDINJECTOR = registerItemBlock("fluidizersolidinjector", () -> Blocks.FLUIDIZER_SOLIDINJECTOR::get);
        public static final Supplier<BlockItem> FLUIDIZER_FLUIDINJECTOR = registerItemBlock("fluidizerfluidinjector", () -> Blocks.FLUIDIZER_FLUIDINJECTOR::get);
        public static final Supplier<BlockItem> FLUIDIZER_OUTPUTPORT = registerItemBlock("fluidizeroutputport", () -> Blocks.FLUIDIZER_OUTPUTPORT::get);
        public static final Supplier<BlockItem> FLUIDIZER_POWERPORT = registerItemBlock("fluidizerpowerport", () -> Blocks.FLUIDIZER_POWERPORT::get);

        //endregion
        //region Energizer

        public static final Supplier<ModItem> ENERGY_CORE = registerItemGeneric("energycore");
        public static final Supplier<BlockItem> ENERGY_CELL = registerItemBlock("energizercell", () -> Blocks.ENERGIZER_CELL);
        public static final Supplier<BlockItem> ENERGIZER_CASING = registerItemBlock("energizercasing", () -> Blocks.ENERGIZER_CASING::get);
        public static final Supplier<BlockItem> ENERGIZER_CONTROLLER = registerItemBlock("energizercontroller", () -> Blocks.ENERGIZER_CONTROLLER::get);
        public static final Supplier<BlockItem> ENERGIZER_POWERPORT_FE = registerItemBlock("energizerpowerport_fe", () -> Blocks.ENERGIZER_POWERPORT_FE::get);
        public static final Supplier<BlockItem> ENERGIZER_CHARGINGPORT_FE = registerItemBlock("energizerchargingport_fe", () -> Blocks.ENERGIZER_CHARGINGPORT_FE::get);
        public static final Supplier<BlockItem> ENERGIZER_STATUS_DISPLAY = registerItemBlock("energizerstatus", () -> Blocks.ENERGIZER_STATUS_DISPLAY::get);

        //endregion
        //region misc

        public static final Supplier<ModItem> WRENCH = registerItemGeneric("wrench", 1);

        //endregion
        //region internals

        private static Supplier<ModItem> registerItemGeneric(final String name) {
            return registerItemGeneric(name, 64);
        }

        private static Supplier<ModItem> registerItemGeneric(final String name, final int maxStack) {
            return ITEMS.register(name,
                    () -> new ModItem(new Item.Properties().stacksTo(maxStack)));
        }

        private static Supplier<BlockItem> registerItemBlock(final String name,
                                                             final Supplier<Supplier<ModBlock>> blockSupplier) {
            return ITEMS.register(name,
                    () -> blockSupplier.get().get().createBlockItem(new Item.Properties().stacksTo(64)));
        }

        private static Supplier<TintedBucketItem> registerTintedBucket(String name,
                                                                       Supplier<? extends Fluid> sourceFluid) {
            return ITEMS.register(name, () -> new TintedBucketItem(sourceFluid.get(), bucketProperties()));
        }

        private static Supplier<TintedBucketItem> registerReactantBucket(Reactants reactant,
                                                                         Supplier<? extends Fluid> sourceFluid) {
            return ITEMS.register(reactant.getBucketName(),
                    () -> new TintedBucketItem(sourceFluid.get(), bucketProperties(),
                            (stack, tintIndex) -> 1 == tintIndex ? 0xFF000000 | reactant.getColour() : 0xFFFFFFFF));
        }

        private static Item.Properties bucketProperties() {
            return new Item.Properties()
                    .craftRemainder(net.minecraft.world.item.Items.BUCKET)
                    .stacksTo(1);
        }

        //endregion
    }

    public static final class DataComponents {

        private static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, ExtremeReactors.MOD_ID);

        static void initialize(final IEventBus bus) {
            DATA_COMPONENTS.register(bus);
        }

        public static final Supplier<@NotNull DataComponentType<ReactorFluidAccessPortComponent>> REACTOR_FLUID_ACCESSPORT_COMPONENT_TYPE =
                registerComponent("reactor_fluid_access_port", ReactorFluidAccessPortComponent.CODECS);

        //region internals

        private static <Type> Supplier<@NotNull DataComponentType<Type>>
        registerComponent(String name, ModCodecs<Type, ? super RegistryFriendlyByteBuf> codecs) {
            return DATA_COMPONENTS.register(name, () -> DataComponentType.<Type>builder()
                    .persistent(codecs.codec())
                    .networkSynchronized(codecs.streamCodec())
                    .build());
        }

        //endregion
    }

    public static final class Fluids {

        private static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(BuiltInRegistries.FLUID, ExtremeReactors.MOD_ID);
        private static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.FLUID_TYPES, ExtremeReactors.MOD_ID);

        static void initialize(final IEventBus bus) {

            FLUIDS.register(bus);
            FLUID_TYPES.register(bus);
        }

        public static Supplier<FluidType> STEAM_FLUID_TYPE = FLUID_TYPES.register("steam",
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
                        .pathType(PathType.WALKABLE)
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

        public static final Supplier<FlowingFluid> STEAM_SOURCE = registerSteam("steam", BaseFlowingFluid.Source::new);
        public static final Supplier<FlowingFluid> STEAM_FLOWING = registerSteam("steam_flowing", BaseFlowingFluid.Flowing::new);

        //region reactants

        public static Supplier<FluidType> YELLORIUM_FLUID_TYPE = registerReactantFluidType(Reactants.Yellorium);

        public static final Supplier<FlowingFluid> YELLORIUM_SOURCE = FLUIDS.register(Reactants.Yellorium.getFluidSourceName(),
                () -> new BaseFlowingFluid.Source(new BaseFlowingFluid.Properties(YELLORIUM_FLUID_TYPE, Fluids.YELLORIUM_SOURCE, Fluids.YELLORIUM_FLOWING)
                        .bucket(Items.YELLORIUM_BUCKET)
                        .block(Blocks.YELLORIUM_FLUID)));

        public static final Supplier<FlowingFluid> YELLORIUM_FLOWING = FLUIDS.register(Reactants.Yellorium.getFluidFlowingName(),
                () -> new BaseFlowingFluid.Flowing(new BaseFlowingFluid.Properties(YELLORIUM_FLUID_TYPE, Fluids.YELLORIUM_SOURCE, Fluids.YELLORIUM_FLOWING)
                        .bucket(Items.YELLORIUM_BUCKET)
                        .block(Blocks.YELLORIUM_FLUID)));

        public static Supplier<FluidType> CYANITE_FLUID_TYPE = registerReactantFluidType(Reactants.Cyanite);

        public static final Supplier<FlowingFluid> CYANITE_SOURCE = FLUIDS.register(Reactants.Cyanite.getFluidSourceName(),
                () -> new BaseFlowingFluid.Source(new BaseFlowingFluid.Properties(CYANITE_FLUID_TYPE, Fluids.CYANITE_SOURCE, Fluids.CYANITE_FLOWING)
                        .bucket(Items.CYANITE_BUCKET)
                        .block(Blocks.CYANITE_FLUID)));

        public static final Supplier<FlowingFluid> CYANITE_FLOWING = FLUIDS.register(Reactants.Cyanite.getFluidFlowingName(),
                () -> new BaseFlowingFluid.Flowing(new BaseFlowingFluid.Properties(CYANITE_FLUID_TYPE, Fluids.CYANITE_SOURCE, Fluids.CYANITE_FLOWING)
                        .bucket(Items.CYANITE_BUCKET)
                        .block(Blocks.CYANITE_FLUID)));

        public static Supplier<FluidType> BLUTONIUM_FLUID_TYPE = registerReactantFluidType(Reactants.Blutonium);

        public static final Supplier<FlowingFluid> BLUTONIUM_SOURCE = FLUIDS.register(Reactants.Blutonium.getFluidSourceName(),
                () -> new BaseFlowingFluid.Source(new BaseFlowingFluid.Properties(BLUTONIUM_FLUID_TYPE, Fluids.BLUTONIUM_SOURCE, Fluids.BLUTONIUM_FLOWING)
                        .bucket(Items.BLUTONIUM_BUCKET)
                        .block(Blocks.BLUTONIUM_FLUID)));

        public static final Supplier<FlowingFluid> BLUTONIUM_FLOWING = FLUIDS.register(Reactants.Blutonium.getFluidFlowingName(),
                () -> new BaseFlowingFluid.Flowing(new BaseFlowingFluid.Properties(BLUTONIUM_FLUID_TYPE, Fluids.BLUTONIUM_SOURCE, Fluids.BLUTONIUM_FLOWING)
                        .bucket(Items.BLUTONIUM_BUCKET)
                        .block(Blocks.BLUTONIUM_FLUID)));

        public static Supplier<FluidType> MAGENTITE_FLUID_TYPE = registerReactantFluidType(Reactants.Magentite);

        public static final Supplier<FlowingFluid> MAGENTITE_SOURCE = FLUIDS.register(Reactants.Magentite.getFluidSourceName(),
                () -> new BaseFlowingFluid.Source(new BaseFlowingFluid.Properties(MAGENTITE_FLUID_TYPE, Fluids.MAGENTITE_SOURCE, Fluids.MAGENTITE_FLOWING)
                        .bucket(Items.MAGENTITE_BUCKET)
                        .block(Blocks.MAGENTITE_FLUID)));

        public static final Supplier<FlowingFluid> MAGENTITE_FLOWING = FLUIDS.register(Reactants.Magentite.getFluidFlowingName(),
                () -> new BaseFlowingFluid.Flowing(new BaseFlowingFluid.Properties(MAGENTITE_FLUID_TYPE, Fluids.MAGENTITE_SOURCE, Fluids.MAGENTITE_FLOWING)
                        .bucket(Items.MAGENTITE_BUCKET)
                        .block(Blocks.MAGENTITE_FLUID)));

        public static Supplier<FluidType> VERDERIUM_FLUID_TYPE = registerReactantFluidType(Reactants.Verderium);

        public static final Supplier<FlowingFluid> VERDERIUM_SOURCE = FLUIDS.register(Reactants.Verderium.getFluidSourceName(),
                () -> new BaseFlowingFluid.Source(new BaseFlowingFluid.Properties(VERDERIUM_FLUID_TYPE, Fluids.VERDERIUM_SOURCE, Fluids.VERDERIUM_FLOWING)
                        .bucket(Items.VERDERIUM_BUCKET)
                        .block(Blocks.VERDERIUM_FLUID)));

        public static final Supplier<FlowingFluid> VERDERIUM_FLOWING = FLUIDS.register(Reactants.Verderium.getFluidFlowingName(),
                () -> new BaseFlowingFluid.Flowing(new BaseFlowingFluid.Properties(VERDERIUM_FLUID_TYPE, Fluids.VERDERIUM_SOURCE, Fluids.VERDERIUM_FLOWING)
                        .bucket(Items.VERDERIUM_BUCKET)
                        .block(Blocks.VERDERIUM_FLUID)));

        public static Supplier<FluidType> ROSSINITE_FLUID_TYPE = registerReactantFluidType(Reactants.Rossinite);

        public static final Supplier<FlowingFluid> ROSSINITE_SOURCE = FLUIDS.register(Reactants.Rossinite.getFluidSourceName(),
                () -> new BaseFlowingFluid.Source(new BaseFlowingFluid.Properties(ROSSINITE_FLUID_TYPE, Fluids.ROSSINITE_SOURCE, Fluids.ROSSINITE_FLOWING)
                        .bucket(Items.ROSSINITE_BUCKET)
                        .block(Blocks.ROSSINITE_FLUID)));

        public static final Supplier<FlowingFluid> ROSSINITE_FLOWING = FLUIDS.register(Reactants.Rossinite.getFluidFlowingName(),
                () -> new BaseFlowingFluid.Flowing(new BaseFlowingFluid.Properties(ROSSINITE_FLUID_TYPE, Fluids.ROSSINITE_SOURCE, Fluids.ROSSINITE_FLOWING)
                        .bucket(Items.ROSSINITE_BUCKET)
                        .block(Blocks.ROSSINITE_FLUID)));

        //endregion
        //region moderators

        public static Supplier<FluidType> CRYOMISI_FLUID_TYPE = FLUID_TYPES.register("cryomisi",
                () -> ReactorFluidType.of("cryomisi", 0xf5002d, 1000, 6, Rarity.RARE));

        public static final Supplier<FlowingFluid> CRYOMISI_SOURCE = FLUIDS.register("cryomisi",
                () -> new BaseFlowingFluid.Source(new BaseFlowingFluid.Properties(CRYOMISI_FLUID_TYPE, Fluids.CRYOMISI_SOURCE, Fluids.CRYOMISI_FLOWING)
                        .bucket(Items.CRYOMISI_BUCKET)
                        .block(Blocks.CRYOMISI_FLUID)));

        public static final Supplier<FlowingFluid> CRYOMISI_FLOWING = FLUIDS.register("cryomisi_flowing",
                () -> new BaseFlowingFluid.Flowing(new BaseFlowingFluid.Properties(CRYOMISI_FLUID_TYPE, Fluids.CRYOMISI_SOURCE, Fluids.CRYOMISI_FLOWING)
                        .bucket(Items.CRYOMISI_BUCKET)
                        .block(Blocks.CRYOMISI_FLUID)));

        public static Supplier<FluidType> TANGERIUM_FLUID_TYPE = FLUID_TYPES.register("tangerium",
                () -> ReactorFluidType.of("tangerium", 0xcf463b, 1500, 5, Rarity.RARE));

        public static final Supplier<FlowingFluid> TANGERIUM_SOURCE = FLUIDS.register("tangerium",
                () -> new BaseFlowingFluid.Source(new BaseFlowingFluid.Properties(TANGERIUM_FLUID_TYPE, Fluids.TANGERIUM_SOURCE, Fluids.TANGERIUM_FLOWING)
                        .bucket(Items.TANGERIUM_BUCKET)
                        .block(Blocks.TANGERIUM_FLUID)));

        public static final Supplier<FlowingFluid> TANGERIUM_FLOWING = FLUIDS.register("tangerium_flowing",
                () -> new BaseFlowingFluid.Flowing(new BaseFlowingFluid.Properties(TANGERIUM_FLUID_TYPE, Fluids.TANGERIUM_SOURCE, Fluids.TANGERIUM_FLOWING)
                        .bucket(Items.TANGERIUM_BUCKET)
                        .block(Blocks.TANGERIUM_FLUID)));

        public static Supplier<FluidType> REDFRIGIUM_FLUID_TYPE = FLUID_TYPES.register("redfrigium",
                () -> ReactorFluidType.of("redfrigium", 0xfcb3c1, 1500, 8, Rarity.EPIC));

        public static final Supplier<FlowingFluid> REDFRIGIUM_SOURCE = FLUIDS.register("redfrigium",
                () -> new BaseFlowingFluid.Source(new BaseFlowingFluid.Properties(REDFRIGIUM_FLUID_TYPE, Fluids.REDFRIGIUM_SOURCE, Fluids.REDFRIGIUM_FLOWING)
                        .bucket(Items.REDFRIGIUM_BUCKET)
                        .block(Blocks.REDFRIGIUM_FLUID)));

        public static final Supplier<FlowingFluid> REDFRIGIUM_FLOWING = FLUIDS.register("redfrigium_flowing",
                () -> new BaseFlowingFluid.Flowing(new BaseFlowingFluid.Properties(REDFRIGIUM_FLUID_TYPE, Fluids.REDFRIGIUM_SOURCE, Fluids.REDFRIGIUM_FLOWING)
                        .bucket(Items.REDFRIGIUM_BUCKET)
                        .block(Blocks.REDFRIGIUM_FLUID)));

        //endregion
        //region internals

        private static Supplier<FlowingFluid> registerSteam(String name,
                                                            Function<BaseFlowingFluid.@NotNull Properties, FlowingFluid> factory) {
            return FLUIDS.register(name, () -> factory.apply(new BaseFlowingFluid.Properties(STEAM_FLUID_TYPE, STEAM_SOURCE, STEAM_FLOWING)
                    .bucket(Items.STEAM_BUCKET)
                    .block(Blocks.STEAM)));
        }

        private static Supplier<FluidType> registerReactantFluidType(final Reactants reactant) {
            return FLUID_TYPES.register(reactant.getFluidName(), () -> ReactorFluidType.of(reactant));
        }

        //endregion
    }

    public static final class TileEntityTypes {

        private static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES =
                DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ExtremeReactors.MOD_ID);

        static void initialize(final IEventBus bus) {
            TILE_ENTITIES.register(bus);
        }

        //region Reactor

        public static final Supplier<BlockEntityType<ReactorCasingEntity>> REACTOR_CASING =
                registerBlockEntity("reactorcasing", ReactorCasingEntity::new,
                        () -> Blocks.REACTOR_CASING_BASIC::get,
                        () -> Blocks.REACTOR_CASING_REINFORCED::get);

        public static final Supplier<BlockEntityType<ReactorGlassEntity>> REACTOR_GLASS =
                registerBlockEntity("reactorglass", ReactorGlassEntity::new,
                        () -> Blocks.REACTOR_GLASS_BASIC::get,
                        () -> Blocks.REACTOR_GLASS_REINFORCED::get);

        public static final Supplier<BlockEntityType<ReactorControllerEntity>> REACTOR_CONTROLLER =
                registerBlockEntity("reactorcontroller", ReactorControllerEntity::new,
                        () -> Blocks.REACTOR_CONTROLLER_BASIC::get,
                        () -> Blocks.REACTOR_CONTROLLER_REINFORCED::get);

        public static final Supplier<BlockEntityType<ReactorFuelRodEntity>> REACTOR_FUELROD =
                registerBlockEntity("reactorfuelrod", ReactorFuelRodEntity::new,
                        () -> Blocks.REACTOR_FUELROD_BASIC::get,
                        () -> Blocks.REACTOR_FUELROD_REINFORCED::get);

        public static final Supplier<BlockEntityType<ReactorControlRodEntity>> REACTOR_CONTROLROD =
                registerBlockEntity("reactorcontrolrod", ReactorControlRodEntity::new,
                        () -> Blocks.REACTOR_CONTROLROD_BASIC::get,
                        () -> Blocks.REACTOR_CONTROLROD_REINFORCED::get);

        public static final Supplier<BlockEntityType<ReactorSolidAccessPortEntity>> REACTOR_SOLID_ACCESSPORT =
                registerBlockEntity("reactoraccessport", ReactorSolidAccessPortEntity::new,
                        () -> Blocks.REACTOR_SOLID_ACCESSPORT_BASIC::get,
                        () -> Blocks.REACTOR_SOLID_ACCESSPORT_REINFORCED::get);

        public static final Supplier<BlockEntityType<ReactorFluidAccessPortEntity>> REACTOR_FLUID_ACCESSPORT =
                registerBlockEntity("reactoraccessportfluid", ReactorFluidAccessPortEntity::new,
                        () -> Blocks.REACTOR_FLUID_ACCESSPORT_REINFORCED::get);

        public static final Supplier<BlockEntityType<ReactorFluidPortEntity>> REACTOR_FLUIDPORT_FORGE_ACTIVE =
                registerBlockEntity("reactorfluidport_forge_active",
                        (position, blockState) -> new ReactorFluidPortEntity(FluidPortType.Forge, IoMode.Active, TileEntityTypes.REACTOR_FLUIDPORT_FORGE_ACTIVE.get(), position, blockState),
                        () -> Blocks.REACTOR_FLUIDTPORT_FORGE_ACTIVE_REINFORCED::get);

        public static final Supplier<BlockEntityType<ReactorFluidPortEntity>> REACTOR_FLUIDPORT_FORGE_PASSIVE =
                registerBlockEntity("reactorfluidport_forge_passive",
                        (position, blockState) -> new ReactorFluidPortEntity(FluidPortType.Forge, IoMode.Passive, TileEntityTypes.REACTOR_FLUIDPORT_FORGE_PASSIVE.get(), position, blockState),
                        () -> Blocks.REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED::get);

        public static final Supplier<BlockEntityType<ReactorMekanismFluidPortEntity>> REACTOR_FLUIDPORT_MEKANISM_PASSIVE =
                registerBlockEntity("reactorfluidport_mekanism_passive",
                        ReactorMekanismFluidPortEntity::new,
                        () -> Blocks.REACTOR_FLUIDPORT_MEKANISM_PASSIVE_REINFORCED::get);

        public static final Supplier<BlockEntityType<ReactorCreativeWaterGenerator>> REACTOR_CREATIVE_WATER_GENERATOR =
                registerBlockEntity("reactorcreativewatergenerator",
                        ReactorCreativeWaterGenerator::new,
                        () -> Blocks.REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED::get);

        public static final Supplier<BlockEntityType<ReactorPowerTapEntity>> REACTOR_POWERTAP_FE_ACTIVE =
                registerBlockEntity("reactorpowertap_fe_active",
                        (position, blockState) -> new ReactorPowerTapEntity(EnergySystem.ForgeEnergy, IoMode.Active, TileEntityTypes.REACTOR_POWERTAP_FE_ACTIVE.get(), position, blockState),
                        () -> Blocks.REACTOR_POWERTAP_FE_ACTIVE_BASIC::get,
                        () -> Blocks.REACTOR_POWERTAP_FE_ACTIVE_REINFORCED::get);

        public static final Supplier<BlockEntityType<ReactorPowerTapEntity>> REACTOR_POWERTAP_FE_PASSIVE =
                registerBlockEntity("reactorpowertap_fe_passive",
                        (position, blockState) -> new ReactorPowerTapEntity(EnergySystem.ForgeEnergy, IoMode.Passive, TileEntityTypes.REACTOR_POWERTAP_FE_PASSIVE.get(), position, blockState),
                        () -> Blocks.REACTOR_POWERTAP_FE_PASSIVE_BASIC::get,
                        () -> Blocks.REACTOR_POWERTAP_FE_PASSIVE_REINFORCED::get);

        public static final Supplier<BlockEntityType<ReactorComputerPortEntity>> REACTOR_COMPUTERPORT =
                registerBlockEntity("reactorcomputerport", ReactorComputerPortEntity::new,
                        () -> Blocks.REACTOR_COMPUTERPORT_REINFORCED::get);

        public static final Supplier<BlockEntityType<ReactorRedstonePortEntity>> REACTOR_REDSTONEPORT =
                registerBlockEntity("reactorredstoneport", ReactorRedstonePortEntity::new,
                        () -> Blocks.REACTOR_REDSTONEPORT_BASIC::get,
                        () -> Blocks.REACTOR_REDSTONEPORT_REINFORCED::get);

        public static final Supplier<BlockEntityType<ReactorChargingPortEntity>> REACTOR_CHARGINGPORT_FE =
                registerBlockEntity("reactorchargingport_fe",
                        (position, blockState) -> new ReactorChargingPortEntity(EnergySystem.ForgeEnergy, TileEntityTypes.REACTOR_CHARGINGPORT_FE.get(), position, blockState),
                        () -> Blocks.REACTOR_CHARGINGPORT_FE_BASIC::get,
                        () -> Blocks.REACTOR_CHARGINGPORT_FE_REINFORCED::get);

        //endregion
        //region Turbine

        public static final Supplier<BlockEntityType<TurbineCasingEntity>> TURBINE_CASING =
                registerBlockEntity("turbinecasing", TurbineCasingEntity::new,
                        () -> Blocks.TURBINE_CASING_BASIC::get,
                        () -> Blocks.TURBINE_CASING_REINFORCED::get);

        public static final Supplier<BlockEntityType<TurbineGlassEntity>> TURBINE_GLASS =
                registerBlockEntity("turbineglass", TurbineGlassEntity::new,
                        () -> Blocks.TURBINE_GLASS_BASIC::get,
                        () -> Blocks.TURBINE_GLASS_REINFORCED::get);

        public static final Supplier<BlockEntityType<TurbineControllerEntity>> TURBINE_CONTROLLER =
                registerBlockEntity("turbinecontroller", TurbineControllerEntity::new,
                        () -> Blocks.TURBINE_CONTROLLER_BASIC::get,
                        () -> Blocks.TURBINE_CONTROLLER_REINFORCED::get);

        public static final Supplier<BlockEntityType<TurbineRotorBearingEntity>> TURBINE_ROTORBEARING =
                registerBlockEntity("turbinerotorbearing", TurbineRotorBearingEntity::new,
                        () -> Blocks.TURBINE_ROTORBEARING_BASIC::get,
                        () -> Blocks.TURBINE_ROTORBEARING_REINFORCED::get);

        public static final Supplier<BlockEntityType<TurbineRotorComponentEntity>> TURBINE_ROTORSHAFT =
                registerBlockEntity("turbinerotorshaft", TurbineRotorComponentEntity::shaft,
                        () -> Blocks.TURBINE_ROTORSHAFT_BASIC::get,
                        () -> Blocks.TURBINE_ROTORSHAFT_REINFORCED::get);

        public static final Supplier<BlockEntityType<TurbineRotorComponentEntity>> TURBINE_ROTORBLADE =
                registerBlockEntity("turbinerotorblade", TurbineRotorComponentEntity::blade,
                        () -> Blocks.TURBINE_ROTORBLADE_BASIC::get,
                        () -> Blocks.TURBINE_ROTORBLADE_REINFORCED::get);

        public static final Supplier<BlockEntityType<TurbineFluidPortEntity>> TURBINE_FLUIDPORT_FORGE_ACTIVE =
                registerBlockEntity("turbinefluidport_forge_active",
                        (position, blockState) -> new TurbineFluidPortEntity(FluidPortType.Forge, IoMode.Active, TileEntityTypes.TURBINE_FLUIDPORT_FORGE_ACTIVE.get(), position, blockState),
                        () -> Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC::get,
                        () -> Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED::get);

        public static final Supplier<BlockEntityType<TurbineFluidPortEntity>> TURBINE_FLUIDPORT_FORGE_PASSIVE =
                registerBlockEntity("turbinefluidport_forge_passive",
                        (position, blockState) -> new TurbineFluidPortEntity(FluidPortType.Forge, IoMode.Passive, TileEntityTypes.TURBINE_FLUIDPORT_FORGE_PASSIVE.get(), position, blockState),
                        () -> Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC::get,
                        () -> Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED::get);

        public static final Supplier<BlockEntityType<TurbineCreativeSteamGenerator>> TURBINE_CREATIVE_STEAM_GENERATOR =
                registerBlockEntity("turbinecreativesteamgenerator",
                        TurbineCreativeSteamGenerator::new,
                        () -> Blocks.TURBINE_CREATIVE_STEAM_GENERATOR_BASIC::get);

        public static final Supplier<BlockEntityType<TurbinePowerTapEntity>> TURBINE_POWERTAP_FE_ACTIVE =
                registerBlockEntity("turbinepowertap_fe_active",
                        (position, blockState) -> new TurbinePowerTapEntity(EnergySystem.ForgeEnergy, IoMode.Active, TileEntityTypes.TURBINE_POWERTAP_FE_ACTIVE.get(), position, blockState),
                        () -> Blocks.TURBINE_POWERTAP_FE_ACTIVE_BASIC::get,
                        () -> Blocks.TURBINE_POWERTAP_FE_ACTIVE_REINFORCED::get);

        public static final Supplier<BlockEntityType<TurbinePowerTapEntity>> TURBINE_POWERTAP_FE_PASSIVE =
                registerBlockEntity("turbinepowertap_fe_passive",
                        (position, blockState) -> new TurbinePowerTapEntity(EnergySystem.ForgeEnergy, IoMode.Passive, TileEntityTypes.TURBINE_POWERTAP_FE_PASSIVE.get(), position, blockState),
                        () -> Blocks.TURBINE_POWERTAP_FE_PASSIVE_BASIC::get,
                        () -> Blocks.TURBINE_POWERTAP_FE_PASSIVE_REINFORCED::get);

        public static final Supplier<BlockEntityType<TurbineComputerPortEntity>> TURBINE_COMPUTERPORT =
                registerBlockEntity("turbinecomputerport", TurbineComputerPortEntity::new,
                        () -> Blocks.TURBINE_COMPUTERPORT_REINFORCED::get);

        public static final Supplier<BlockEntityType<TurbineRedstonePortEntity>> TURBINE_REDSTONEPORT =
                registerBlockEntity("turbineredstoneport", TurbineRedstonePortEntity::new,
                        () -> Blocks.TURBINE_REDSTONEPORT_BASIC::get,
                        () -> Blocks.TURBINE_REDSTONEPORT_REINFORCED::get);

        public static final Supplier<BlockEntityType<TurbineChargingPortEntity>> TURBINE_CHARGINGPORT_FE =
                registerBlockEntity("turbinechargingport_fe",
                        (position, blockState) -> new TurbineChargingPortEntity(EnergySystem.ForgeEnergy, TileEntityTypes.TURBINE_CHARGINGPORT_FE.get(), position, blockState),
                        () -> Blocks.TURBINE_CHARGINGPORT_FE_BASIC::get,
                        () -> Blocks.TURBINE_CHARGINGPORT_FE_REINFORCED::get);

        //endregion
        //region reprocessor

        public static final Supplier<BlockEntityType<ReprocessorCasingEntity>> REPROCESSOR_CASING =
                registerBlockEntity("reprocessorcasing", ReprocessorCasingEntity::new, () -> Blocks.REPROCESSOR_CASING::get);

        public static final Supplier<BlockEntityType<ReprocessorGlassEntity>> REPROCESSOR_GLASS =
                registerBlockEntity("reprocessorglass", ReprocessorGlassEntity::new, () -> Blocks.REPROCESSOR_GLASS::get);

        public static final Supplier<BlockEntityType<ReprocessorControllerEntity>> REPROCESSOR_CONTROLLER =
                registerBlockEntity("reprocessorcontroller", ReprocessorControllerEntity::new, () -> Blocks.REPROCESSOR_CONTROLLER::get);

        public static final Supplier<BlockEntityType<ReprocessorAccessPortEntity>> REPROCESSOR_WASTEINJECTOR =
                registerBlockEntity("reprocessorwasteinjector",
                        (position, blockState) -> new ReprocessorAccessPortEntity(TileEntityTypes.REPROCESSOR_WASTEINJECTOR.get(), IoDirection.Input, position, blockState),
                        () -> Blocks.REPROCESSOR_WASTEINJECTOR::get);

        public static final Supplier<BlockEntityType<ReprocessorFluidPortEntity>> REPROCESSOR_FLUIDINJECTOR =
                registerBlockEntity("reprocessorfluidinjector", ReprocessorFluidPortEntity::new, () -> Blocks.REPROCESSOR_FLUIDINJECTOR::get);

        public static final Supplier<BlockEntityType<ReprocessorAccessPortEntity>> REPROCESSOR_OUTPUTPORT =
                registerBlockEntity("reprocessoroutputport",
                        (position, blockState) -> new ReprocessorAccessPortEntity(TileEntityTypes.REPROCESSOR_OUTPUTPORT.get(), IoDirection.Output, position, blockState),
                        () -> Blocks.REPROCESSOR_OUTPUTPORT::get);

        public static final Supplier<BlockEntityType<ReprocessorPowerPortEntity>> REPROCESSOR_POWERPORT =
                registerBlockEntity("reprocessorpowerport", ReprocessorPowerPortEntity::new, () -> Blocks.REPROCESSOR_POWERPORT::get);

        public static final Supplier<BlockEntityType<ReprocessorCollectorEntity>> REPROCESSOR_COLLECTOR =
                registerBlockEntity("reprocessorcollector", ReprocessorCollectorEntity::new, () -> Blocks.REPROCESSOR_COLLECTOR::get);

        //endregion
        //region fluidizer

        public static final Supplier<BlockEntityType<FluidizerCasingEntity>> FLUIDIZER_CASING =
                registerBlockEntity("fluidizercasing", FluidizerCasingEntity::new, () -> Blocks.FLUIDIZER_CASING::get);

        public static final Supplier<BlockEntityType<FluidizerGlassEntity>> FLUIDIZER_GLASS =
                registerBlockEntity("fluidizerglass", FluidizerGlassEntity::new, () -> Blocks.FLUIDIZER_GLASS::get);

        public static final Supplier<BlockEntityType<FluidizerControllerEntity>> FLUIDIZER_CONTROLLER =
                registerBlockEntity("fluidizercontroller", FluidizerControllerEntity::new, () -> Blocks.FLUIDIZER_CONTROLLER::get);

        public static final Supplier<BlockEntityType<FluidizerSolidInjectorEntity>> FLUIDIZER_SOLIDINJECTOR =
                registerBlockEntity("fluidizersolidinjector", FluidizerSolidInjectorEntity::new, () -> Blocks.FLUIDIZER_SOLIDINJECTOR::get);

        public static final Supplier<BlockEntityType<FluidizerFluidInjectorEntity>> FLUIDIZER_FLUIDINJECTOR =
                registerBlockEntity("fluidizefluidinjector", FluidizerFluidInjectorEntity::new, () -> Blocks.FLUIDIZER_FLUIDINJECTOR::get);

        public static final Supplier<BlockEntityType<FluidizerOutputPortEntity>> FLUIDIZER_OUTPUTPORT =
                registerBlockEntity("fluidizeoutputport", FluidizerOutputPortEntity::new, () -> Blocks.FLUIDIZER_OUTPUTPORT::get);

        public static final Supplier<BlockEntityType<FluidizerPowerPortEntity>> FLUIDIZER_POWERPORT =
                registerBlockEntity("fluidizerpowerport", FluidizerPowerPortEntity::new, () -> Blocks.FLUIDIZER_POWERPORT::get);

        //endregion
        //region Energizer

        public static final Supplier<BlockEntityType<EnergizerCasingEntity>> ENERGIZER_CASING =
                registerBlockEntity("energizercasing", EnergizerCasingEntity::new, () -> Blocks.ENERGIZER_CASING::get);

        public static final Supplier<BlockEntityType<EnergizerControllerEntity>> ENERGIZER_CONTROLLER =
                registerBlockEntity("energizercontroller", EnergizerControllerEntity::new, () -> Blocks.ENERGIZER_CONTROLLER::get);

        public static final Supplier<BlockEntityType<EnergizerPowerPortEntity>> ENERGIZER_POWERPORT_FE =
                registerBlockEntity("energizerpowerport_fe",
                        (position, state) -> new EnergizerPowerPortEntity(EnergySystem.ForgeEnergy,
                                TileEntityTypes.ENERGIZER_POWERPORT_FE.get(), position, state),
                        () -> Blocks.ENERGIZER_POWERPORT_FE::get);

        public static final Supplier<BlockEntityType<EnergizerChargingPortEntity>> ENERGIZER_CHARGINGPORT_FE =
                registerBlockEntity("energizerchargingport_fe",
                        (position, state) -> new EnergizerChargingPortEntity(EnergySystem.ForgeEnergy,
                                TileEntityTypes.ENERGIZER_CHARGINGPORT_FE.get(), position, state),
                        () -> Blocks.ENERGIZER_CHARGINGPORT_FE::get);

        public static final Supplier<BlockEntityType<EnergizerStatusDisplayEntity>> ENERGIZER_STATUS_DISPLAY =
                registerBlockEntity("energizerstatus", EnergizerStatusDisplayEntity::new, () -> Blocks.ENERGIZER_STATUS_DISPLAY::get);

        //endregion
        //region internals

        @SuppressWarnings("ConstantConditions")
        @SafeVarargs
        private static <T extends BlockEntity> Supplier<BlockEntityType<T>> registerBlockEntity(final String name,
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
                DeferredRegister.create(BuiltInRegistries.MENU, ExtremeReactors.MOD_ID);

        static void initialize(final IEventBus bus) {
            CONTAINERS.register(bus);
        }

        //region Reactor

        public static final Supplier<MenuType<ReactorControllerContainer>> REACTOR_CONTROLLER =
                registerContainer("reactorcontroller", ReactorControllerContainer::new);

        public static final Supplier<MenuType<ReactorSolidAccessPortContainer>> REACTOR_SOLID_ACCESSPORT =
                registerContainer("reactoraccessport", ReactorSolidAccessPortContainer::new);

        public static final Supplier<MenuType<ReactorFluidAccessPortContainer>> REACTOR_FLUID_ACCESSPORT =
                registerContainer("reactoraccessportfluid", ReactorFluidAccessPortContainer::new);

        public static final Supplier<MenuType<ReactorRedstonePortContainer>> REACTOR_REDSTONEPORT =
                registerContainer("reactorredstoneport", ReactorRedstonePortContainer::new);

        public static final Supplier<MenuType<ReactorControlRodContainer>> REACTOR_CONTROLROD =
                registerContainer("reactorcontrolrod", ReactorControlRodContainer::new);

        public static final Supplier<MenuType<ChargingPortContainer<MultiblockReactor, IMultiblockReactorVariant, ReactorChargingPortEntity>>> REACTOR_CHARGINGPORT =
                registerContainer("reactorchargingport",
                        (windowId, inv, data) -> new ChargingPortContainer<>(windowId, Content.ContainerTypes.REACTOR_CHARGINGPORT.get(), inv, data));

        public static final Supplier<MenuType<FluidPortContainer<MultiblockReactor, IMultiblockReactorVariant, ReactorFluidPortEntity>>> REACTOR_FLUIDPORT =
                registerContainer("reactorfluidport", (windowId, inv, data) ->
                        new FluidPortContainer<>(windowId, ContainerTypes.REACTOR_FLUIDPORT.get(), inv, data));

        //endregion
        //region Turbine

        public static final Supplier<MenuType<TurbineControllerContainer>> TURBINE_CONTROLLER =
                registerContainer("turbinecontroller", TurbineControllerContainer::new);

        public static final Supplier<MenuType<TurbineRedstonePortContainer>> TURBINE_REDSTONEPORT =
                registerContainer("turbineredstoneport", TurbineRedstonePortContainer::new);

        public static final Supplier<MenuType<ChargingPortContainer<MultiblockTurbine, IMultiblockTurbineVariant, TurbineChargingPortEntity>>> TURBINE_CHARGINGPORT =
                registerContainer("turbinechargingport",
                        (windowId, inv, data) -> new ChargingPortContainer<>(windowId, Content.ContainerTypes.TURBINE_CHARGINGPORT.get(), inv, data));

        public static final Supplier<MenuType<FluidPortContainer<MultiblockTurbine, IMultiblockTurbineVariant, TurbineFluidPortEntity>>> TURBINE_FLUIDPORT =
                registerContainer("turbinefluidport", (windowId, inv, data) ->
                        new FluidPortContainer<>(windowId, ContainerTypes.TURBINE_FLUIDPORT.get(), inv, data));

        //endregion
        //region Reprocessor

        public static final Supplier<MenuType<ReprocessorControllerContainer>> REPROCESSOR_CONTROLLER =
                registerContainer("reprocessorcontroller", ReprocessorControllerContainer::new);

        public static final Supplier<MenuType<ReprocessorAccessPortContainer>> REPROCESSOR_ACCESSPORT =
                registerContainer("reprocessoraccessport", ReprocessorAccessPortContainer::new);

        //endregion
        //region Fluidizer

        public static final Supplier<MenuType<FluidizerControllerContainer>> FLUIDIZER_CONTROLLER =
                registerContainer("fluidizercontroller", FluidizerControllerContainer::new);

        public static final Supplier<MenuType<FluidizerSolidInjectorContainer>> FLUIDIZER_SOLID_INJECTOR =
                registerContainer("fluidizersolidinjector", FluidizerSolidInjectorContainer::new);

        //endregion
        //region Energizer

        public static final Supplier<MenuType<EnergizerControllerContainer>> ENERGIZER_CONTROLLER =
                registerContainer("energizercontroller", EnergizerControllerContainer::new);

        public static final Supplier<MenuType<EnergizerPowerPortContainer>> ENERGIZER_POWERPORT =
                registerContainer("energizerpowerport", EnergizerPowerPortContainer::new);

        public static final Supplier<MenuType<EnergizerChargingPortContainer>> ENERGIZER_CHARGINGPORT =
                registerContainer("energizerchargingport", EnergizerChargingPortContainer::new);

        //endregion
        //region internals

        private static <C extends AbstractContainerMenu> Supplier<MenuType<C>> registerContainer(final String name,
                                                                                                 final IContainerFactory<C> factory) {
            return CONTAINERS.register(name, () -> IMenuTypeExtension.create(factory));
        }

        //endregion
    }

    public static final class Recipes {

        private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
                DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, ExtremeReactors.MOD_ID);

        private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
                DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, ExtremeReactors.MOD_ID);

        static void initialize(final IEventBus bus) {

            RECIPE_TYPES.register(bus);
            SERIALIZERS.register(bus);
        }

        //region Reprocessor

        public static final Supplier<ModRecipeType<ReprocessorRecipe>> REPROCESSOR_RECIPE_TYPE =
                registerRecipe(ReprocessorRecipe.ID);

        public static final Supplier<RecipeSerializer<ReprocessorRecipe>> REPROCESSOR_RECIPE_SERIALIZER =
                SERIALIZERS.register(ReprocessorRecipe.NAME, ReprocessorRecipe::createSerializer);

        //endregion
        //region Fluidizer

        public static final Supplier<ModRecipeType<ModRecipe>> FLUIDIZER_RECIPE_TYPE =
                registerRecipe(IFluidizerRecipe.ID);

        public static final Supplier<RecipeSerializer<FluidizerSolidRecipe>> FLUIDIZER_SOLID_RECIPE_SERIALIZER =
                SERIALIZERS.register(IFluidizerRecipe.Type.Solid.getRecipeName(), FluidizerSolidRecipe::createSerializer);
        public static final Supplier<RecipeSerializer<FluidizerSolidMixingRecipe>> FLUIDIZER_SOLIDMIXING_RECIPE_SERIALIZER =
                SERIALIZERS.register(IFluidizerRecipe.Type.SolidMixing.getRecipeName(), FluidizerSolidMixingRecipe::createSerializer);
        public static final Supplier<RecipeSerializer<FluidizerFluidMixingRecipe>> FLUIDIZER_FLUIDMIXING_RECIPE_SERIALIZER =
                SERIALIZERS.register(IFluidizerRecipe.Type.FluidMixing.getRecipeName(), FluidizerFluidMixingRecipe::createSerializer);

        //endregion
        //region internals

        private static <Recipe extends ModRecipe> Supplier<ModRecipeType<Recipe>> registerRecipe(ResourceLocation recipeId) {
            return RECIPE_TYPES.register(recipeId.getPath(), () -> ModRecipeType.create(recipeId));
        }

        //endregion
    }

    public static final class Biomes {

        private static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS =
                DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, ExtremeReactors.MOD_ID);

        static void initialize(final IEventBus bus) {
            BIOME_MODIFIER_SERIALIZERS.register(bus);
        }

        public static final Supplier<MapCodec<OreBiomeModifier>> OREGEN_YELLORITE = register("oregen_yellorite", OreBiomeModifier::yellorite);

        public static final Supplier<MapCodec<OreBiomeModifier>> OREGEN_ANGLESITE = register("oregen_anglesite", OreBiomeModifier::anglesite);

        public static final Supplier<MapCodec<OreBiomeModifier>> OREGEN_BENITOITE = register("oregen_benitoite", OreBiomeModifier::benitoite);

        //region internals

        private static Supplier<@NotNull MapCodec<OreBiomeModifier>> register(String name,
                                                                              BiFunction<@NotNull HolderSet<Biome>, @NotNull Holder<PlacedFeature>, @NotNull OreBiomeModifier> factory) {
            return BIOME_MODIFIER_SERIALIZERS.register(name, () ->
                    RecordCodecBuilder.mapCodec(builder -> builder.group(
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

                        acceptAll(output, Blocks.YELLORITE_ORE_BLOCK, Blocks.DEEPSLATE_YELLORITE_ORE_BLOCK,
                                Blocks.ANGLESITE_ORE_BLOCK, Blocks.BENITOITE_ORE_BLOCK,
                                Items.YELLORIUM_INGOT, Items.RAW_YELLORIUM, Items.RAW_YELLORIUM_BLOCK,
                                Items.YELLORIUM_DUST, Blocks.YELLORIUM_BLOCK,
                                Items.BLUTONIUM_INGOT, Items.BLUTONIUM_DUST, Blocks.BLUTONIUM_BLOCK,
                                Items.CYANITE_INGOT, Items.CYANITE_DUST, Blocks.CYANITE_BLOCK,
                                Items.MAGENTITE_INGOT, Items.MAGENTITE_DUST, Blocks.MAGENTITE_BLOCK,
                                Items.GRAPHITE_INGOT, Items.GRAPHITE_DUST, Blocks.GRAPHITE_BLOCK,
                                Items.LUDICRITE_INGOT, Items.LUDICRITE_DUST, Blocks.LUDICRITE_BLOCK,
                                Items.RIDICULITE_INGOT, Items.RIDICULITE_DUST, Blocks.RIDICULITE_BLOCK,
                                Items.INANITE_INGOT, Items.INANITE_DUST, Blocks.INANITE_BLOCK,
                                Items.INSANITE_INGOT, Items.INSANITE_DUST, Blocks.INSANITE_BLOCK,
                                Items.YELLORIUM_NUGGET, Items.BLUTONIUM_NUGGET,
                                Items.ANGLESITE_CRYSTAL, Items.BENITOITE_CRYSTAL,
                                Items.WRENCH);

                        IPatchouliService.SERVICE.get().consumeBookStack(PatchouliCompat.HANDBOOK_ID, output::accept);

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

                        acceptAll(output, Items.ENERGY_CORE, Blocks.ENERGIZER_CELL,
                                Blocks.ENERGIZER_CASING, Blocks.ENERGIZER_CONTROLLER,
                                Blocks.ENERGIZER_POWERPORT_FE, Blocks.ENERGIZER_CHARGINGPORT_FE,
                                Blocks.ENERGIZER_STATUS_DISPLAY);
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
