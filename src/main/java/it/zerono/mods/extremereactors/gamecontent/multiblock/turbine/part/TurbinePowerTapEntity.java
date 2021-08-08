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

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.powertap.IPowerTapHandler;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.MultiblockTurbine;
import it.zerono.mods.zerocore.lib.block.INeighborChangeListener;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TurbinePowerTapEntity
        extends AbstractTurbinePowerTapEntity
        implements INeighborChangeListener {

    public TurbinePowerTapEntity(final EnergySystem system, final IoMode mode, final BlockEntityType<?> entityType) {

        super(system, entityType);
        this.setHandler(IPowerTapHandler.create(system, mode, this));
    }

    //region INeighborChangeListener

    /**
     * Called when a neighboring Block on a side of this TileEntity changes
     *
     * @param state            the BlockState of this TileEntity block
     * @param neighborPosition position of neighbor
     * @param isMoving ???
     */
    @Override
    public void onNeighborBlockChanged(BlockState state, BlockPos neighborPosition, boolean isMoving) {

        if (this.isConnected()) {
            this.getPowerTapHandler().checkConnections(this.getLevel(), this.getWorldPosition());
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
            this.getPowerTapHandler().checkConnections(this.getLevel(), this.getWorldPosition());
        }
    }

    //endregion
    //region AbstractCuboidMultiblockPart

    @Override
    public void onAttached(MultiblockTurbine newController) {

        super.onAttached(newController);
        this.getPowerTapHandler().checkConnections(this.getLevel(), this.getWorldPosition());
    }

    @Override
    public void onPostMachineAssembled(MultiblockTurbine controller) {

        super.onPostMachineAssembled(controller);
        this.getPowerTapHandler().checkConnections(this.getLevel(), this.getWorldPosition());
        this.notifyNeighborsOfTileChange();
    }

    //endregion
    //region TileEntity

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {

        final LazyOptional<T> cap = this.getPowerTapHandler().getCapability(capability, side);

        return null != cap ? cap : super.getCapability(capability, side);
    }

    //endregion
}
