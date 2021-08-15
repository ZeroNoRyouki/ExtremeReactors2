/*
 *
 * TurbineVaporPortEntity.java
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

import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.fluidport.FluidPortType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.fluidport.IFluidPort;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.fluidport.IFluidPortHandler;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.MultiblockTurbine;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.IMultiblockTurbineVariant;
import it.zerono.mods.zerocore.lib.block.INeighborChangeListener;
import it.zerono.mods.zerocore.lib.block.TileCommandDispatcher;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TurbineFluidPortEntity
        extends AbstractTurbineEntity
        implements IFluidPort<MultiblockTurbine, IMultiblockTurbineVariant>, INeighborChangeListener, INamedContainerProvider {

    public TurbineFluidPortEntity(final FluidPortType type, final IoMode mode, final TileEntityType<?> entityType) {

        super(entityType);
        this._handler = IFluidPortHandler.create(type, mode, this);
        this.setIoDirection(IoDirection.Input);

        this.setCommandDispatcher(TileCommandDispatcher.<TurbineFluidPortEntity>builder()
                .addServerHandler(CommonConstants.COMMAND_SET_INPUT, tile -> tile.setIoDirection(IoDirection.Input))
                .addServerHandler(CommonConstants.COMMAND_SET_OUTPUT, tile -> tile.setIoDirection(IoDirection.Output))
                .build(this));
    }

    //region ICoolantPort

    @Override
    public IFluidPortHandler<MultiblockTurbine, IMultiblockTurbineVariant> getFluidPortHandler() {
        return this._handler;
    }

    //endregion
    //region INeighborChangeListener

    /**
     * Called when a neighboring Block on a side of this TileEntity changes
     *
     * @param state the BlockState of this TileEntity block
     * @param neighborPosition position of neighbor
     * @param isMoving ?
     */
    @Override
    public void onNeighborBlockChanged(final BlockState state, final BlockPos neighborPosition, final boolean isMoving) {

        if (this.isConnected()) {
            this.getFluidPortHandler().checkConnections(this.getLevel(), this.getWorldPosition());
        }

        this.requestClientRenderUpdate();
    }

    /**
     * Called when a neighboring TileEntity on a side of this TileEntity changes, is created or is destroyed
     *
     * @param state the BlockState of this TileEntity block
     * @param neighborPosition position of neighbor
     */
    @Override
    public void onNeighborTileChanged(final BlockState state, final BlockPos neighborPosition) {

        if (this.isConnected()) {
            this.getFluidPortHandler().checkConnections(this.getLevel(), this.getWorldPosition());
        }

        this.requestClientRenderUpdate();
    }

    //endregion
    //region IIoEntity

    @Override
    public IoDirection getIoDirection() {
        return this._direction;
    }

    @Override
    public void setIoDirection(IoDirection direction) {

        if (this.getIoDirection() == direction) {
            return;
        }

        this._direction = direction;
        this.getFluidPortHandler().update();
        this.executeOnController(MultiblockTurbine::onFluidPortChanged);
        this.notifyBlockUpdate();

        this.callOnLogicalSide(
                () -> {
                    this.notifyOutwardNeighborsOfStateChange();
                    this.setChanged();
                },
                this::markForRenderUpdate
        );
    }

    //endregion
    //region ISyncableEntity

    @Override
    public void syncDataFrom(final CompoundNBT data, final SyncReason syncReason) {

        super.syncDataFrom(data, syncReason);
        this.setIoDirection(IoDirection.read(data, "iodir", IoDirection.Input));
    }

    @Override
    public CompoundNBT syncDataTo(final CompoundNBT data, final SyncReason syncReason) {

        super.syncDataTo(data, syncReason);
        IoDirection.write(data, "iodir", this.getIoDirection());
        return data;
    }

    //endregion
    //region INamedContainerProvider

    /**
     * Create the SERVER-side container for this TileEntity
     * @param windowId  the window id
     * @param inventory the player inventory
     * @param player    the player
     * @return the container to use on the server
     */
    @Nullable
    @Override
    public Container createMenu(final int windowId, final PlayerInventory inventory, final PlayerEntity player) {
        return ModTileContainer.empty(Content.ContainerTypes.TURBINE_FLUIDPORT.get(), windowId, this);
    }

    @Override
    public ITextComponent getDisplayName() {
        return super.getPartDisplayName();
    }

    //endregion
    //region client render support

    @Override
    protected int getUpdatedModelVariantIndex() {

        final int connectedOffset = this.isMachineAssembled() && this.getFluidPortHandler().isConnected() ? 1 : 0;

        return this.getIoDirection().isInput() ? 2 + connectedOffset : 0 + connectedOffset;
    }

    //endregion
    //region AbstractModBlockEntity

    /**
     * Check if the tile entity has a GUI or not
     * Override in derived classes to return true if your tile entity got a GUI
     *
     * @param world
     * @param position
     * @param state
     */
    @Override
    public boolean canOpenGui(World world, BlockPos position, BlockState state) {
        return this.isMachineAssembled();
    }

    //endregion
    //region AbstractCuboidMultiblockPart

    @Override
    public void onPostMachineAssembled(MultiblockTurbine controller) {

        super.onPostMachineAssembled(controller);
        this.getFluidPortHandler().update();
        this.notifyOutwardNeighborsOfStateChange();
    }

    @Override
    public void onPostMachineBroken() {

        super.onPostMachineBroken();
        this.getFluidPortHandler().update();
        this.notifyOutwardNeighborsOfStateChange();
    }

    @Override
    public void onAttached(MultiblockTurbine newController) {

        super.onAttached(newController);
        this.getFluidPortHandler().update();
    }

    @Override
    public void onAssimilated(MultiblockTurbine newController) {

        super.onAssimilated(newController);
        this.getFluidPortHandler().update();
    }

    @Override
    public void onDetached(MultiblockTurbine oldController) {

        super.onDetached(oldController);
        this.getFluidPortHandler().update();
    }

    //endregion
    //region TileEntity

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {

        final LazyOptional<T> cap = this.getFluidPortHandler().getCapability(capability, side);

        return null != cap ? cap : super.getCapability(capability, side);
    }

    /**
     * invalidates a tile entity
     */
    @Override
    public void setRemoved() {

        super.setRemoved();
        this.getFluidPortHandler().invalidate();
    }

    //endregion
    //region internals

    private final IFluidPortHandler<MultiblockTurbine, IMultiblockTurbineVariant> _handler;
    private IoDirection _direction;

    //endregion
}
