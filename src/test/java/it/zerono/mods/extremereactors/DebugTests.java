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

import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.zerocore.lib.debug.DebugHelper;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DebugTests {

    public static void runTest(int test, @Nullable PlayerEntity player, World world, BlockPos clickedPos) {

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

            default:
                unknownTest(world, clickedPos);
                break;
        }
    }

    private static void highlightSaveDelegate(World world, BlockPos clickedPos) {

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

    private static void resetReactorData(World world, BlockPos clickedPos) {

        WorldHelper.getMultiblockPartFrom(world, clickedPos)
                .flatMap(IMultiblockPart::getMultiblockController)
                .filter(controller -> controller instanceof MultiblockReactor)
                .map(controller -> (MultiblockReactor)controller)
                .ifPresent(MultiblockReactor::reset);
    }

    private static void highlightBlockShape(World world, BlockPos clickedPos) {

        if (DebugHelper.VoxelShapeType.None != DebugHelper.getBlockVoxelShapeHighlight(world, clickedPos)) {
            DebugHelper.removeVoxelShapeHighlight(world, clickedPos);
        } else {
            DebugHelper.addVoxelShapeHighlight(world, clickedPos, DebugHelper.VoxelShapeType.General);
        }
    }

    private static void unknownTest(World world, BlockPos clickedPos) {

    }
}
