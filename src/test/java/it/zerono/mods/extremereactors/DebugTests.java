/*
 *
 * DebugTests.java
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

package it.zerono.mods.extremereactors;

import it.zerono.mods.extremereactors.api.internal.modpack.wrapper.ApiWrapper;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.MultiblockTurbine;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.debug.DebugHelper;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;

public class DebugTests {

    public static void runTest(int test, @Nullable Player player, Level world, BlockPos clickedPos) {

        switch (test) {

            case 1:
                highlightSaveDelegate(world, clickedPos);
                break;

            case 2:
                resetReactorData(world, clickedPos);
                break;

            case 4:
                highlightBlockShape(world, clickedPos);
                break;

            case 5:
                CodeHelper.callOnLogicalServer(world, ApiWrapper::generateSampleFile);
                break;

            case 6:
                CodeHelper.callOnLogicalServer(world, ApiWrapper::processFile);
                break;

            case 10:
                createRods(world, clickedPos);
                break;

            case 11:
                createOccludedRods(world, clickedPos);
                break;

            default:
                unknownTest(world, clickedPos);
                break;
        }
    }

    private static void highlightSaveDelegate(Level world, BlockPos clickedPos) {

        WorldHelper.getMultiblockPartFrom(world, clickedPos)
                .flatMap(part -> part.getMultiblockController()
                        .flatMap(IMultiblockController::getReferenceCoord))
                .ifPresent(pos -> {

                    if (DebugHelper.VoxelShapeType.None != DebugHelper.getBlockVoxelShapeHighlight(world, pos)) {
                        DebugHelper.removeVoxelShapeHighlight(world, pos);
                    } else {
                        DebugHelper.addVoxelShapeHighlight(world, pos, DebugHelper.VoxelShapeType.General);
                    }
                });
    }

    private static void resetReactorData(Level world, BlockPos clickedPos) {

        WorldHelper.getMultiblockPartFrom(world, clickedPos)
                .flatMap(IMultiblockPart::getMultiblockController)
                .filter(controller -> controller instanceof MultiblockReactor)
                .map(controller -> (MultiblockReactor)controller)
                .ifPresent(MultiblockReactor::reset);

        WorldHelper.getMultiblockPartFrom(world, clickedPos)
                .flatMap(IMultiblockPart::getMultiblockController)
                .filter(controller -> controller instanceof MultiblockTurbine)
                .map(controller -> (MultiblockTurbine)controller)
                .ifPresent(MultiblockTurbine::reset);
    }

    private static void highlightBlockShape(Level world, BlockPos clickedPos) {

        if (DebugHelper.VoxelShapeType.None != DebugHelper.getBlockVoxelShapeHighlight(world, clickedPos)) {
            DebugHelper.removeVoxelShapeHighlight(world, clickedPos);
        } else {
            DebugHelper.addVoxelShapeHighlight(world, clickedPos, DebugHelper.VoxelShapeType.General);
        }
    }

    private static void unknownTest(Level world, BlockPos clickedPos) {

    }

    private static void createRods(Level world, BlockPos clickedPos) {

        final int start_y = 5;
        final int end_y = 254;
        final int n_column = 127;
        final BlockState ROD = Content.Blocks.REACTOR_FUELROD_REINFORCED.get().defaultBlockState();
        final BlockState CAP = Content.Blocks.REACTOR_CONTROLROD_REINFORCED.get().defaultBlockState();

        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int x = clickedPos.getX(), z = clickedPos.getZ();

        for (int c = 0; c < n_column; ++c) {

            for (int y = start_y; y <= end_y; ++y) {

                pos.set(x, y, z);
                world.setBlock(pos, ROD, Constants.BlockFlags.NO_NEIGHBOR_DROPS | Constants.BlockFlags.NO_RERENDER);
            }

            pos.set(x, end_y + 1, z);
            world.setBlock(pos, CAP, Constants.BlockFlags.NO_NEIGHBOR_DROPS | Constants.BlockFlags.NO_RERENDER);

            z -= 2;
        }
    }

    private static void createOccludedRods(Level world, BlockPos clickedPos) {

        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        final int start_y = 5, end_y = 254;
//        final int start_x = 1745, end_x = 1998;
        final int start_x = 1881, end_x = 1890;//1920
        final int start_z = 2001, end_z = 2254;

        final BlockState ROD = Content.Blocks.REACTOR_FUELROD_REINFORCED.get().defaultBlockState();
        final BlockState CAP = Content.Blocks.REACTOR_CONTROLROD_REINFORCED.get().defaultBlockState();
        final BlockState MODERATOR = Blocks.DIAMOND_BLOCK.defaultBlockState();
        final BlockState CASING = Content.Blocks.REACTOR_CASING_REINFORCED.get().defaultBlockState();
        BlockState pillar1, pillar2, top1, top2;
        boolean flip = false;

        for (int x = start_x; x <= end_x; ++x, flip = !flip) {
            for (int z = start_z; z <= end_z; z += 2) {

                if (flip) {

                    pillar1 = MODERATOR;
                    pillar2 = ROD;
                    top1 = CASING;
                    top2 = CAP;


                } else {

                    pillar1 = ROD;
                    pillar2 = MODERATOR;
                    top1 = CAP;
                    top2 = CASING;
                }

                for (int y = start_y; y <= end_y; ++y) {

                    pos.set(x, y, z);
                    world.setBlock(pos, pillar1, Constants.BlockFlags.NO_NEIGHBOR_DROPS | Constants.BlockFlags.NO_RERENDER);
                    pos.set(x, y, z + 1);
                    world.setBlock(pos, pillar2, Constants.BlockFlags.NO_NEIGHBOR_DROPS | Constants.BlockFlags.NO_RERENDER);
                }

                pos.set(x, end_y + 1, z);
                world.setBlock(pos, top1, Constants.BlockFlags.NO_NEIGHBOR_DROPS | Constants.BlockFlags.NO_RERENDER);
                pos.set(x, end_y + 1, z + 1);
                world.setBlock(pos, top2, Constants.BlockFlags.NO_NEIGHBOR_DROPS | Constants.BlockFlags.NO_RERENDER);
            }
        }
    }
}
