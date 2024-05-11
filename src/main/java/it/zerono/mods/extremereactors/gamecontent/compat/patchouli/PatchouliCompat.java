/*
 *
 * Patchouli.java
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

package it.zerono.mods.extremereactors.gamecontent.compat.patchouli;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.config.Config;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.FluidizerPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.MultiblockFluidizer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.IReactorPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.MultiblockReprocessor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.ReprocessorPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.MultiblockTurbine;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.TurbinePartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineRotorComponentBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorBladeState;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorShaftState;
import it.zerono.mods.zerocore.lib.block.BlockFacings;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock;
import it.zerono.mods.zerocore.lib.client.model.data.multiblock.CuboidPartVariantsModelData;
import it.zerono.mods.zerocore.lib.compat.patchouli.IPatchouliClientService;
import it.zerono.mods.zerocore.lib.compat.patchouli.IPatchouliService;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public final class PatchouliCompat {

    public static final ResourceLocation HANDBOOK_ID = ExtremeReactors.ROOT_LOCATION.buildWithSuffix("erguide");

    public static void initialize() {

        if (Config.CLIENT.disablePatchouliTweaks.get()) {
            return;
        }

        IPatchouliClientService.SERVICE.get().initialize();

        final var patchouli = IPatchouliService.SERVICE.get();

        //noinspection unchecked
        patchouli.registerMultiblock(ExtremeReactors.ROOT_LOCATION.buildWithSuffix("bookfirstbasicreactor"),
                bs -> bs.getBlock().defaultBlockState(),
                bs -> (bs.getBlock() instanceof MultiblockPartBlock) ?
                        CuboidPartVariantsModelData.from(((MultiblockPartBlock<MultiblockReactor, IReactorPartType>)bs.getBlock()).getPartType().getByteHashCode(), 0, BlockFacings.ALL) :
                        ModelData.EMPTY,
                new String[][] {
                        {
                            "CCC",
                            "CRC",
                            "CCC",
                        },
                        {
                            "CCC",
                            "EFA",
                            "CXC",
                        },
                        {
                            "CCC",
                            "C0C",
                            "CCC",
                        }
                    },
                    '0', Content.Blocks.REACTOR_CASING_BASIC.get(),
                    'C', Content.Blocks.REACTOR_CASING_BASIC.get(),
                    'F', Content.Blocks.REACTOR_FUELROD_BASIC.get(),
                    'R', Content.Blocks.REACTOR_CONTROLROD_BASIC.get(),
                    'A', Content.Blocks.REACTOR_SOLID_ACCESSPORT_BASIC.get(),
                    'E', Content.Blocks.REACTOR_POWERTAP_FE_ACTIVE_BASIC.get(),
                    'X', Content.Blocks.REACTOR_CONTROLLER_BASIC.get()
        );

        patchouli.registerMultiblock(ExtremeReactors.ROOT_LOCATION.buildWithSuffix("bookturbinerotor"),
                bs -> bs.getBlock().defaultBlockState(),
                PatchouliCompat::turbineRotorModelDataMapper,
                new String[][] {
                                {
                                        "       ",
                                        "       ",
                                        "       ",
                                        "   X   ",
                                        "       ",
                                        "       ",
                                        "       ",
                                },
                                {
                                        "       ",
                                        "       ",
                                        "       ",
                                        "   S   ",
                                        "       ",
                                        "       ",
                                        "       ",
                                },
                                {
                                        "       ",
                                        "       ",
                                        "   1   ",
                                        "  2A4  ",
                                        "   3   ",
                                        "       ",
                                        "       ",
                                },
                                {
                                        "       ",
                                        "   1   ",
                                        "   1   ",
                                        " 22A44 ",
                                        "   3   ",
                                        "   3   ",
                                        "       ",
                                },
                                {
                                        "   1   ",
                                        "   1   ",
                                        "   1   ",
                                        "   C   ",
                                        "   3   ",
                                        "   3   ",
                                        "   3   ",
                                },
                                {
                                        "       ",
                                        "       ",
                                        "       ",
                                        "222B444",
                                        "       ",
                                        "       ",
                                        "       ",
                                },
                                {
                                        "   1   ",
                                        "   1   ",
                                        "   1   ",
                                        "222A444",
                                        "   3   ",
                                        "   3   ",
                                        "   3   ",
                                },
                                {
                                        "       ",
                                        "       ",
                                        "       ",
                                        "   S   ",
                                        "       ",
                                        "       ",
                                        "       ",
                                },
                                {
                                        "       ",
                                        "       ",
                                        "       ",
                                        "   0   ",
                                        "       ",
                                        "       ",
                                        "       ",
                                }
                        },
                        '0', Content.Blocks.TURBINE_ROTORBEARING_BASIC.get().defaultBlockState(),
                        'S', Content.Blocks.TURBINE_ROTORSHAFT_BASIC.get().defaultBlockState().setValue(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.Y_NOBLADES),
                        'A', Content.Blocks.TURBINE_ROTORSHAFT_BASIC.get().defaultBlockState().setValue(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.Y_XZ),
                        'B', Content.Blocks.TURBINE_ROTORSHAFT_BASIC.get().defaultBlockState().setValue(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.Y_Z),
                        'C', Content.Blocks.TURBINE_ROTORSHAFT_BASIC.get().defaultBlockState().setValue(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.Y_X),
                        '1', Content.Blocks.TURBINE_ROTORBLADE_BASIC.get().defaultBlockState().setValue(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.Y_X_NEG),
                        '3', Content.Blocks.TURBINE_ROTORBLADE_BASIC.get().defaultBlockState().setValue(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.Y_X_POS),
                        '2', Content.Blocks.TURBINE_ROTORBLADE_BASIC.get().defaultBlockState().setValue(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.Y_Z_NEG),
                        '4', Content.Blocks.TURBINE_ROTORBLADE_BASIC.get().defaultBlockState().setValue(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.Y_Z_POS),
                        'X', Content.Blocks.TURBINE_CASING_BASIC.get().defaultBlockState()
        );

        patchouli.registerMultiblock(ExtremeReactors.ROOT_LOCATION.buildWithSuffix("bookturbinerotor_coil"),
                bs -> bs.getBlock().defaultBlockState(),
                PatchouliCompat::turbineRotorModelDataMapper,
                new String[][] {
                                {
                                        "       ",
                                        "       ",
                                        "       ",
                                        "   X   ",
                                        "       ",
                                        "       ",
                                        "       ",
                                },
                                {
                                        "       ",
                                        "       ",
                                        "  IGG  ",
                                        "  GSI  ",
                                        "  GII  ",
                                        "       ",
                                        "       ",
                                },
                                {
                                        "       ",
                                        "       ",
                                        "   1   ",
                                        "  2A4  ",
                                        "   3   ",
                                        "       ",
                                        "       ",
                                },
                                {
                                        "       ",
                                        "   1   ",
                                        "   1   ",
                                        " 22A44 ",
                                        "   3   ",
                                        "   3   ",
                                        "       ",
                                },
                                {
                                        "   1   ",
                                        "   1   ",
                                        "   1   ",
                                        "   C   ",
                                        "   3   ",
                                        "   3   ",
                                        "   3   ",
                                },
                                {
                                        "       ",
                                        "       ",
                                        "       ",
                                        "222B444",
                                        "       ",
                                        "       ",
                                        "       ",
                                },
                                {
                                        "   1   ",
                                        "   1   ",
                                        "   1   ",
                                        "222A444",
                                        "   3   ",
                                        "   3   ",
                                        "   3   ",
                                },
                                {
                                        "       ",
                                        "       ",
                                        "       ",
                                        "   S   ",
                                        "       ",
                                        "       ",
                                        "       ",
                                },
                                {
                                        "       ",
                                        "       ",
                                        "       ",
                                        "   0   ",
                                        "       ",
                                        "       ",
                                        "       ",
                                }
                        },
                        '0', Content.Blocks.TURBINE_ROTORBEARING_BASIC.get().defaultBlockState(),
                        'S', Content.Blocks.TURBINE_ROTORSHAFT_BASIC.get().defaultBlockState().setValue(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.Y_NOBLADES),
                        'A', Content.Blocks.TURBINE_ROTORSHAFT_BASIC.get().defaultBlockState().setValue(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.Y_XZ),
                        'B', Content.Blocks.TURBINE_ROTORSHAFT_BASIC.get().defaultBlockState().setValue(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.Y_Z),
                        'C', Content.Blocks.TURBINE_ROTORSHAFT_BASIC.get().defaultBlockState().setValue(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.Y_X),
                        '1', Content.Blocks.TURBINE_ROTORBLADE_BASIC.get().defaultBlockState().setValue(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.Y_X_NEG),
                        '3', Content.Blocks.TURBINE_ROTORBLADE_BASIC.get().defaultBlockState().setValue(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.Y_X_POS),
                        '2', Content.Blocks.TURBINE_ROTORBLADE_BASIC.get().defaultBlockState().setValue(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.Y_Z_NEG),
                        '4', Content.Blocks.TURBINE_ROTORBLADE_BASIC.get().defaultBlockState().setValue(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.Y_Z_POS),
                        'X', Content.Blocks.TURBINE_CASING_BASIC.get().defaultBlockState(),
                        'I', Blocks.IRON_BLOCK.defaultBlockState(),
                        'G', Blocks.GOLD_BLOCK.defaultBlockState()
        );

        //noinspection unchecked
        patchouli.registerMultiblock(ExtremeReactors.ROOT_LOCATION.buildWithSuffix("bookreprocessor"),
                bs -> bs.getBlock().defaultBlockState(),
                bs -> (bs.getBlock() instanceof MultiblockPartBlock) ?
                        CuboidPartVariantsModelData.from(((MultiblockPartBlock<MultiblockReprocessor, ReprocessorPartType>)bs.getBlock()).getPartType().ordinal(), 0, BlockFacings.ALL) :
                        ModelData.EMPTY,
                new String[][] {
                                {
                                        "CCC",
                                        "CWC",
                                        "CCC",
                                },
                                {
                                        "CCC",
                                        "C C",
                                        "CCC",
                                },
                                {
                                        "CCC",
                                        "C A",
                                        "CCC",
                                },
                                {
                                        "CCC",
                                        "C C",
                                        "CCC",
                                },
                                {
                                        "CCC",
                                        "E C",
                                        "CCC",
                                },
                                {
                                        "COC",
                                        "C C",
                                        "CXC",
                                },
                                {
                                        "CCC",
                                        "C0C",
                                        "CCC",
                                }
                        },
                        '0', Content.Blocks.REPROCESSOR_COLLECTOR.get(),
                        'C', Content.Blocks.REPROCESSOR_CASING.get(),
                        'W', Content.Blocks.REPROCESSOR_WASTEINJECTOR.get(),
                        'O', Content.Blocks.REPROCESSOR_OUTPUTPORT.get(),
                        'A', Content.Blocks.REPROCESSOR_FLUIDINJECTOR.get(),
                        'E', Content.Blocks.REPROCESSOR_POWERPORT.get(),
                        'X', Content.Blocks.REPROCESSOR_CONTROLLER.get()
        );

        //noinspection unchecked
        patchouli.registerMultiblock(ExtremeReactors.ROOT_LOCATION.buildWithSuffix("bookfluidizer"),
                bs -> bs.getBlock().defaultBlockState(),
                bs -> (bs.getBlock() instanceof MultiblockPartBlock) ?
                        CuboidPartVariantsModelData.from(((MultiblockPartBlock<MultiblockFluidizer, FluidizerPartType>)bs.getBlock()).getPartType().ordinal(), 0, BlockFacings.ALL) :
                        ModelData.EMPTY,
                new String[][] {
                                {
                                        "CCCCC",
                                        "CCCPC",
                                        "CCCCC",
                                        "CCCCC",
                                        "CCCCC",
                                },
                                {
                                        "CGGGC",
                                        "O   G",
                                        "X   G",
                                        "I   G",
                                        "CCCCC",
                                },
                                {
                                        "CGGGC",
                                        "G   G",
                                        "G   G",
                                        "G   G",
                                        "CCCCC",
                                },
                                {
                                        "CCCCC",
                                        "CCCCC",
                                        "CC0CC",
                                        "CCCCC",
                                        "CCCCC",
                                }
                        },
                        '0', Content.Blocks.FLUIDIZER_CASING.get(),
                        'C', Content.Blocks.FLUIDIZER_CASING.get(),
                        'G', Content.Blocks.FLUIDIZER_GLASS.get(),
                        'P', Content.Blocks.FLUIDIZER_POWERPORT.get(),
                        'O', Content.Blocks.FLUIDIZER_OUTPUTPORT.get(),
                        'I', Content.Blocks.FLUIDIZER_SOLIDINJECTOR.get(),
                        'X', Content.Blocks.FLUIDIZER_CONTROLLER.get()
        );
    }

    //region internals

    private static ModelData turbineRotorModelDataMapper(final BlockState state) {

        final Block block = state.getBlock();

        if (block instanceof TurbineRotorComponentBlock rotorComponent) {

            switch (rotorComponent.getComponentType()) {

                case Shaft:
                    return CuboidPartVariantsModelData.from(TurbinePartType.RotorShaft.ordinal(),
                            state.getValue(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE).ordinal(), BlockFacings.ALL);

                case Blade:
                    return CuboidPartVariantsModelData.from(TurbinePartType.RotorBlade.ordinal(),
                            state.getValue(TurbineRotorComponentBlock.ROTOR_BLADE_STATE).ordinal(), BlockFacings.ALL);
            }
        }

        if (block instanceof MultiblockPartBlock) {

            @SuppressWarnings("unchecked")
            final MultiblockPartBlock<MultiblockTurbine, TurbinePartType> part = (MultiblockPartBlock<MultiblockTurbine, TurbinePartType>)block;

            return CuboidPartVariantsModelData.from(part.getPartType().getByteHashCode(), 0, BlockFacings.ALL);
        }

        return ModelData.EMPTY;
    }

    //endregion
}
