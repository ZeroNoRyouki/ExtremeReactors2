/*
 *
 * IOPortBlock.java
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

import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.zerocore.lib.block.INeighborChangeListener;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartType;
import it.zerono.mods.zerocore.lib.data.IIoEntity;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import java.util.Optional;

public class IOPortBlock<Controller extends IMultiblockController<Controller>,
                            PartType extends Enum<PartType> & IMultiblockPartType>
        extends GenericDeviceBlock<Controller, PartType>
        implements INeighborChangeListener.Notifier {

    public IOPortBlock(final MultiblockPartProperties<PartType> properties) {
        super(properties);
    }

    //region Block

    @Override
    public void onRemove(BlockState state, World world, BlockPos position, BlockState newState, boolean isMoving) {

        if (state.getBlock() != newState.getBlock()) {
            this.getIIoEntity(world, position).ifPresent(ioe -> ioe.onBlockReplaced(state, world, position, newState, isMoving));
        }

        super.onRemove(state, world, position, newState, isMoving);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos position, PlayerEntity player,
                                             Hand hand, BlockRayTraceResult hit) {

        if (Hand.MAIN_HAND == hand && player.getMainHandItem().getItem().is(ContentTags.Items.WRENCH)) {

            this.callOnLogicalServer(world, w -> this.getIIoEntity(w, position).ifPresent(IIoEntity::toggleIoDirection));
            return ActionResultType.SUCCESS;
        }

        return super.use(state, world, position, player, hand, hit);
    }

    //endregion
    //region internals

    private Optional<IIoEntity> getIIoEntity(final World world, final BlockPos position) {
        return WorldHelper.getTile(world, position)
                .filter(te -> te instanceof IIoEntity)
                .map(te -> (IIoEntity)te);
    }

    //endregion
}
