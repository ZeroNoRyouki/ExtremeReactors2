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
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactorPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.MultiblockTurbine;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.TurbinePartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineRotorComponentBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorBladeState;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorShaftState;
import it.zerono.mods.zerocore.lib.block.BlockFacings;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock;
import it.zerono.mods.zerocore.lib.client.model.data.multiblock.CuboidPartVariantsModelData;
import it.zerono.mods.zerocore.lib.compat.patchouli.Patchouli;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import vazkii.patchouli.api.PatchouliAPI;

public final class PatchouliCompat {

    public static final ResourceLocation HANDBOOK_ID = ExtremeReactors.newID("erguide");

    public static void initialize() {

        if (Config.CLIENT.disablePatchouliTweaks.get()) {
            return;
        }

        Patchouli.initialize();

        //noinspection unchecked
        Patchouli.registerMultiblock(ExtremeReactors.newID("bookfirstbasicreactor"),
                PatchouliAPI.get().makeMultiblock(new String[][] {
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
                    'X', Content.Blocks.REACTOR_CONTROLLER_BASIC.get()),
                bs -> bs.getBlock().getDefaultState(),
                bs -> (bs.getBlock() instanceof MultiblockPartBlock) ?
                        new CuboidPartVariantsModelData(((MultiblockPartBlock<MultiblockReactor, ReactorPartType>)bs.getBlock()).getPartType().ordinal(), 0, BlockFacings.ALL) :
                        EmptyModelData.INSTANCE
        );

        //noinspection unchecked
        Patchouli.registerMultiblock(ExtremeReactors.newID("bookturbinerotor"),
                PatchouliAPI.get().makeMultiblock(new String[][] {
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
                        '0', Content.Blocks.TURBINE_ROTORBEARING_BASIC.get().getDefaultState(),
                        'S', Content.Blocks.TURBINE_ROTORSHAFT_BASIC.get().getDefaultState().with(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.Y_NOBLADES),
                        'A', Content.Blocks.TURBINE_ROTORSHAFT_BASIC.get().getDefaultState().with(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.Y_XZ),
                        'B', Content.Blocks.TURBINE_ROTORSHAFT_BASIC.get().getDefaultState().with(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.Y_Z),
                        'C', Content.Blocks.TURBINE_ROTORSHAFT_BASIC.get().getDefaultState().with(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.Y_X),
                        '1', Content.Blocks.TURBINE_ROTORBLADE_BASIC.get().getDefaultState().with(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.Y_X_NEG),
                        '3', Content.Blocks.TURBINE_ROTORBLADE_BASIC.get().getDefaultState().with(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.Y_X_POS),
                        '2', Content.Blocks.TURBINE_ROTORBLADE_BASIC.get().getDefaultState().with(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.Y_Z_NEG),
                        '4', Content.Blocks.TURBINE_ROTORBLADE_BASIC.get().getDefaultState().with(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.Y_Z_POS),
                        'X', Content.Blocks.TURBINE_CASING_BASIC.get().getDefaultState()),
                bs -> bs.getBlock().getDefaultState(),
                PatchouliCompat::turbineRotorModelDataMapper
        );


        //noinspection unchecked
        Patchouli.registerMultiblock(ExtremeReactors.newID("bookturbinerotor_coil"),
                PatchouliAPI.get().makeMultiblock(new String[][] {
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
                        '0', Content.Blocks.TURBINE_ROTORBEARING_BASIC.get().getDefaultState(),
                        'S', Content.Blocks.TURBINE_ROTORSHAFT_BASIC.get().getDefaultState().with(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.Y_NOBLADES),
                        'A', Content.Blocks.TURBINE_ROTORSHAFT_BASIC.get().getDefaultState().with(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.Y_XZ),
                        'B', Content.Blocks.TURBINE_ROTORSHAFT_BASIC.get().getDefaultState().with(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.Y_Z),
                        'C', Content.Blocks.TURBINE_ROTORSHAFT_BASIC.get().getDefaultState().with(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.Y_X),
                        '1', Content.Blocks.TURBINE_ROTORBLADE_BASIC.get().getDefaultState().with(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.Y_X_NEG),
                        '3', Content.Blocks.TURBINE_ROTORBLADE_BASIC.get().getDefaultState().with(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.Y_X_POS),
                        '2', Content.Blocks.TURBINE_ROTORBLADE_BASIC.get().getDefaultState().with(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.Y_Z_NEG),
                        '4', Content.Blocks.TURBINE_ROTORBLADE_BASIC.get().getDefaultState().with(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.Y_Z_POS),
                        'X', Content.Blocks.TURBINE_CASING_BASIC.get().getDefaultState(),
                        'I', Blocks.IRON_BLOCK.getDefaultState(),
                        'G', Blocks.GOLD_BLOCK.getDefaultState()),
                bs -> bs.getBlock().getDefaultState(),
                PatchouliCompat::turbineRotorModelDataMapper
        );
    }

    //region internals

    private static IModelData turbineRotorModelDataMapper(final BlockState state) {

        final Block block = state.getBlock();

        if (block instanceof MultiblockPartBlock) {

            @SuppressWarnings("unchecked")
            final MultiblockPartBlock<MultiblockTurbine, TurbinePartType> part = (MultiblockPartBlock<MultiblockTurbine, TurbinePartType>)block;

            switch (part.getPartType()) {

                case RotorShaft:
                    return new CuboidPartVariantsModelData(TurbinePartType.RotorShaft.ordinal(), state.get(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE).ordinal(), BlockFacings.ALL);

                case RotorBlade:
                    return new CuboidPartVariantsModelData(TurbinePartType.RotorBlade.ordinal(), state.get(TurbineRotorComponentBlock.ROTOR_BLADE_STATE).ordinal(), BlockFacings.ALL);

                default:
                    return new CuboidPartVariantsModelData(part.getPartType().ordinal(), 0, BlockFacings.ALL);
            }
        }

        return EmptyModelData.INSTANCE;
    }

    //endregion
}
