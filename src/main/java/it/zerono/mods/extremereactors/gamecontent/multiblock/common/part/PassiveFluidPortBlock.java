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

import it.zerono.mods.zerocore.base.multiblock.part.io.IOPortBlock;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartType;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidUtil;

import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock.MultiblockPartProperties;

public class PassiveFluidPortBlock<Controller extends IMultiblockController<Controller>,
        PartType extends Enum<PartType> & IMultiblockPartType>
    extends IOPortBlock<Controller, PartType> {

    public PassiveFluidPortBlock(final MultiblockPartProperties<PartType> properties) {
        super(properties);
    }

    //region Block

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos position, Player player,
                                             InteractionHand hand, BlockHitResult hit) {

        if (InteractionHand.MAIN_HAND == hand) {

            final ItemStack heldItem = player.getMainHandItem();

            if (!heldItem.isEmpty()) {

                if (FluidUtil.getFluidHandler(world, position, null)
                        .map(port -> FluidUtil.interactWithFluidHandler(player, hand, port))
                        .orElse(false)) {
                    return InteractionResult.SUCCESS;
                }
            }
        }

        return super.use(state, world, position, player, hand, hit);
    }

    //endregion
}
