/*
 *
 * TurbinePowerTapEntity.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part;

import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.MultiblockTurbine;
import it.zerono.mods.zerocore.base.multiblock.part.io.power.IPowerPortHandler;
import it.zerono.mods.zerocore.lib.block.INeighborChangeListener;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.Nullable;

public class TurbinePowerTapEntity
        extends AbstractTurbinePowerTapEntity
        implements INeighborChangeListener {

    public TurbinePowerTapEntity(final EnergySystem system, final IoMode mode, final BlockEntityType<?> entityType,
                                 final BlockPos position, final BlockState blockState) {

        super(system, entityType, position, blockState);
        this.setHandler(IPowerPortHandler.create(system, mode, this));
    }

    //region INeighborChangeListener

    @Override
    public void onNeighborBlockChanged(BlockState state, @Nullable Orientation orientation, boolean isMoving) {

        if (this.isConnected()) {
            this.getPowerPortHandler().onPortChanged();
        }
    }

    /**
     * Called when a neighboring TileEntity on a side of this TileEntity changes, is created or is destroyed
     *
     * @param state            the BlockState of this TileEntity block
     * @param neighborPosition position of neighbor
     */
    @Override
    public void onNeighborTileChanged(BlockState state, BlockPos neighborPosition) {

        if (this.isConnected()) {
            this.getPowerPortHandler().onPortChanged();
        }
    }

    //endregion
    //region AbstractCuboidMultiblockPart

    @Override
    public void onAttached(MultiblockTurbine newController) {

        super.onAttached(newController);
        this.getPowerPortHandler().onPortChanged();
    }

    @Override
    public void onPostMachineAssembled(MultiblockTurbine controller) {

        super.onPostMachineAssembled(controller);
        this.getPowerPortHandler().onPortChanged();
    }

    //endregion
}
