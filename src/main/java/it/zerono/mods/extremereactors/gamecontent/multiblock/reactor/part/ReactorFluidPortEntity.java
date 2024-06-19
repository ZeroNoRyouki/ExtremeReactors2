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
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.container.FluidPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.fluidport.FluidPortType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.IReactorPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactorPartType;
import it.zerono.mods.zerocore.base.multiblock.part.io.fluid.IFluidPort;
import it.zerono.mods.zerocore.base.multiblock.part.io.fluid.IFluidPortHandler;
import it.zerono.mods.zerocore.lib.block.INeighborChangeListener;
import it.zerono.mods.zerocore.lib.block.TileCommandDispatcher;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.IoMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;
import org.jetbrains.annotations.Nullable;

public class ReactorFluidPortEntity
        extends AbstractReactorEntity
        implements IFluidPort, INeighborChangeListener, MenuProvider {

    public ReactorFluidPortEntity(final FluidPortType type, final IoMode mode, final BlockEntityType<?> entityType,
                                  final BlockPos position, final BlockState blockState) {

        super(entityType, position, blockState);
        this._handler = FluidPortType.create(type, mode, this);
        this.setIoDirection(IoDirection.Input);

        this.setCommandDispatcher(TileCommandDispatcher.<ReactorFluidPortEntity>builder()
                .addServerHandler(CommonConstants.COMMAND_SET_INPUT, tile -> tile.setIoDirection(IoDirection.Input))
                .addServerHandler(CommonConstants.COMMAND_SET_OUTPUT, tile -> tile.setIoDirection(IoDirection.Output))
                .build(this));
    }

    //region IFluidPort

    @Override
    public IFluidPortHandler getFluidPortHandler() {
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

        this.getFluidPortHandler().onPortChanged();
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

        this.getFluidPortHandler().onPortChanged();
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
        this.getFluidPortHandler().update(this::getUpdatedHandler);
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
    public void syncDataFrom(CompoundTag data, HolderLookup.Provider registries, SyncReason syncReason) {

        super.syncDataFrom(data, registries, syncReason);
        this.setIoDirection(IoDirection.read(data, "iodir", IoDirection.Input));
    }

    @Override
    public CompoundTag syncDataTo(CompoundTag data, HolderLookup.Provider registries, SyncReason syncReason) {

        super.syncDataTo(data, registries, syncReason);
        IoDirection.write(data, "iodir", this.getIoDirection());
        return data;
    }

    //endregion
    //region MenuProvider

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
        return new FluidPortContainer<>(windowId, Content.ContainerTypes.REACTOR_FLUIDPORT.get(), inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return super.getPartDisplayName();
    }

    //endregion
    //region client render support

    @Override
    protected int getUpdatedModelVariantIndex() {

        final int connectedOffset = this.isMachineAssembled() && this.getFluidPortHandler().isConnected() ? 1 : 0;

        return this.getIoDirection().isInput() ? connectedOffset : 2 + connectedOffset;
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

        final IReactorPartType partType = this.getPartTypeOrDefault(ReactorPartType.Casing);

        return (ReactorPartType.ActiveFluidPortForge == partType || ReactorPartType.PassiveFluidPortForge == partType) &&
                this.isMachineAssembled();
    }

    //endregion
    //region AbstractCuboidMultiblockPart

    @Override
    public void onPostMachineAssembled(MultiblockReactor controller) {

        super.onPostMachineAssembled(controller);
        this.getFluidPortHandler().update(this::getUpdatedHandler);
        this.notifyOutwardNeighborsOfStateChange();
    }

    @Override
    public void onPostMachineBroken() {

        super.onPostMachineBroken();
        this.getFluidPortHandler().update(this::getUpdatedHandler);
        this.notifyOutwardNeighborsOfStateChange();
    }

    @Override
    public void onAttached(MultiblockReactor newController) {

        super.onAttached(newController);
        this.getFluidPortHandler().update(this::getUpdatedHandler);
    }

    @Override
    public void onAssimilated(MultiblockReactor newController) {

        super.onAssimilated(newController);
        this.getFluidPortHandler().update(this::getUpdatedHandler);
    }

    @Override
    public void onDetached(MultiblockReactor oldController) {

        super.onDetached(oldController);
        this.getFluidPortHandler().update(this::getUpdatedHandler);
    }

    //endregion
    //region internals

    private IFluidHandler getUpdatedHandler(IoDirection direction) {
        return this.getMultiblockController()
                .flatMap(c -> c.getFluidHandler(direction))
                .orElse(EmptyFluidHandler.INSTANCE);
    }

    private final IFluidPortHandler _handler;
    private IoDirection _direction;

    //endregion
}
