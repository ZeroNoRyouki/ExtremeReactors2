/*
 *
 * PassiveFluidPortBlock.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common.part;

import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartType;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;

public class PassiveFluidPortBlock<Controller extends IMultiblockController<Controller>,
        PartType extends Enum<PartType> & IMultiblockPartType>
    extends IOPortBlock<Controller, PartType> {

    public PassiveFluidPortBlock(final MultiblockPartProperties<PartType> properties) {
        super(properties);
    }

    //region Block

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos position, PlayerEntity player,
                                             Hand hand, BlockRayTraceResult hit) {

        if (Hand.MAIN_HAND == hand) {

            final ItemStack heldItem = player.getHeldItemMainhand();

            if (!heldItem.isEmpty()) {

                if (FluidUtil.getFluidHandler(world, position, null)
                        .map(port -> FluidUtil.interactWithFluidHandler(player, hand, port))
                        .orElse(false)) {
                    return ActionResultType.SUCCESS;
                }
            }
        }

        return super.onBlockActivated(state, world, position, player, hand, hit);
    }

    //endregion
}
