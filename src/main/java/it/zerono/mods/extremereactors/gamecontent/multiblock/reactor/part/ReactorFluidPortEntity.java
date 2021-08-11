/*
 *
 * ReactorFluidPortEntity.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part;

import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.fluidport.FluidPortType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.fluidport.IFluidPort;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.fluidport.IFluidPortHandler;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactorPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.IMultiblockReactorVariant;
import it.zerono.mods.zerocore.lib.block.INeighborChangeListener;
import it.zerono.mods.zerocore.lib.block.TileCommandDispatcher;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReactorFluidPortEntity
        extends AbstractReactorEntity
        implements IFluidPort<MultiblockReactor, IMultiblockReactorVariant>, INeighborChangeListener, MenuProvider {

    public ReactorFluidPortEntity(final FluidPortType type, final IoMode mode, final BlockEntityType<?> entityType,
                                  final BlockPos position, final BlockState blockState) {

        super(entityType, position, blockState);
        this._handler = IFluidPortHandler.create(type, mode, this);
        this.setIoDirection(IoDirection.Input);

        this.setCommandDispatcher(TileCommandDispatcher.<ReactorFluidPortEntity>builder()
                .addServerHandler(CommonConstants.COMMAND_SET_INPUT, tile -> tile.setIoDirection(IoDirection.Input))
                .addServerHandler(CommonConstants.COMMAND_SET_OUTPUT, tile -> tile.setIoDirection(IoDirection.Output))
                .build(this));
    }

    //region IFluidPort

    @Override
    public IFluidPortHandler<MultiblockReactor, IMultiblockReactorVariant> getFluidPortHandler() {
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
        this.executeOnController(MultiblockReactor::onFluidPortChanged);
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
    public void syncDataFrom(final CompoundTag data, final SyncReason syncReason) {

        super.syncDataFrom(data, syncReason);
        this.setIoDirection(IoDirection.read(data, "iodir", IoDirection.Input));
    }

    @Override
    public CompoundTag syncDataTo(final CompoundTag data, final SyncReason syncReason) {

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
    public AbstractContainerMenu createMenu(final int windowId, final Inventory inventory, final Player player) {
        return ModTileContainer.empty(Content.ContainerTypes.REACTOR_FLUIDPORT.get(), windowId, this);
    }

    @Override
    public Component getDisplayName() {
        return super.getPartDisplayName();
    }

    //endregion
    //region client render support

    @Override
    protected int getUpdatedModelVariantIndex() {

        if (this.isMachineAssembled()) {

            final int connectedOffset = this.getFluidPortHandler().isConnected() ? 1 : 0;

            return this.getIoDirection().isInput() ? 0 + connectedOffset : 2 + connectedOffset;

        } else {

            return 0;
        }
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
    public boolean canOpenGui(Level world, BlockPos position, BlockState state) {

        final ReactorPartType partType = this.getPartTypeOrDefault(ReactorPartType.Casing);

        return (ReactorPartType.ActiveFluidPortForge == partType || ReactorPartType.PassiveFluidPortForge == partType) &&
                this.isMachineAssembled();
    }

    //endregion
    //region AbstractCuboidMultiblockPart

    @Override
    public void onPostMachineAssembled(MultiblockReactor controller) {

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
    public void onAttached(MultiblockReactor newController) {

        super.onAttached(newController);
        this.getFluidPortHandler().update();
    }

    @Override
    public void onAssimilated(MultiblockReactor newController) {

        super.onAssimilated(newController);
        this.getFluidPortHandler().update();
    }

    @Override
    public void onDetached(MultiblockReactor oldController) {

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

    private final IFluidPortHandler<MultiblockReactor, IMultiblockReactorVariant> _handler;
    private IoDirection _direction;

    //endregion
}
