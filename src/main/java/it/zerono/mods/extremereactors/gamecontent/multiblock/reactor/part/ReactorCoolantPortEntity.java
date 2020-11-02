/*
 *
 * ReactorCoolantPortEntity.java
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

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.FluidType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.zerocore.lib.data.IIoEntity;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.fluid.FluidHelper;
import it.zerono.mods.zerocore.lib.multiblock.ITickableMultiblockPart;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReactorCoolantPortEntity extends AbstractReactorEntity implements IIoEntity, ITickableMultiblockPart {

    @SuppressWarnings("ConstantConditions")
    public ReactorCoolantPortEntity() {
        this(null); //TODO fix TE
    }

    ReactorCoolantPortEntity(final TileEntityType<?> type) {

        super(type);
        this.setIoDirection(IoDirection.Input);
        this._fluidCapability = LazyOptional.of(this::createFluidCapability);
    }

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
        this.updateCapabilityForwarder();
        this.getMultiblockController().ifPresent(MultiblockReactor::onCoolantPortChanged);
        this.notifyBlockUpdate();

//        this.getPartWorld()
//                .filter(CodeHelper::calledByLogicalServer)
//                .ifPresent(world -> {
//                    this.notifyOutwardNeighborsOfStateChange();
//                    this.markDirty();
//                });

        this.callOnLogicalSide(
                () -> {
                    this.notifyOutwardNeighborsOfStateChange();
                    this.markDirty();
                },
                this::markForRenderUpdate
        );

        this.notifyNeighborsOfTileChange();
    }

    //endregion
    //region ITickableMultiblockPart

    /**
     * Called once every tick from the multiblock server-side tick loop.
     */
    @Override
    public void onMultiblockServerTick() {

        if (this.getIoDirection().isOutput()) {
            this.getOutwardDirection().ifPresent(this::transferGas);
        }
    }

    //endregion
    //region ISyncableEntity

    @Override
    public void syncDataFrom(CompoundNBT data, SyncReason syncReason) {

        super.syncDataFrom(data, syncReason);
        this.setIoDirection(IoDirection.read(data, "iodir", IoDirection.Input));
    }

    @Override
    public CompoundNBT syncDataTo(CompoundNBT data, SyncReason syncReason) {

        super.syncDataTo(data, syncReason);
        IoDirection.write(data, "iodir", this.getIoDirection());
        return data;
    }

    //endregion
    //region AbstractCuboidMultiblockPart

    @Override
    public void onPostMachineAssembled(MultiblockReactor controller) {

        super.onPostMachineAssembled(controller);
        this.notifyOutwardNeighborsOfStateChange();
    }

    @Override
    public void onPostMachineBroken() {

        super.onPostMachineBroken();
        this.notifyOutwardNeighborsOfStateChange();
    }

    @Override
    public void onAttached(MultiblockReactor newController) {

        super.onAttached(newController);
        this.updateCapabilityForwarder();
    }

    @Override
    public void onAssimilated(MultiblockReactor newController) {

        super.onAssimilated(newController);
        this.updateCapabilityForwarder();
    }

    @Override
    public void onDetached(MultiblockReactor oldController) {

        super.onDetached(oldController);
        this.updateCapabilityForwarder();
    }

    //endregion
    //region TileEntity

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {

        if (!this.isRemoved() && CAPAB_FLUID_HANDLER == capability) {
            return this._fluidCapability.cast();
        }

        return super.getCapability(capability, side);
    }

    /**
     * invalidates a tile entity
     */
    @Override
    public void remove() {

        super.remove();
        this._fluidCapability.invalidate();
    }

    //endregion
    //region internals

    private void updateCapabilityForwarder() {
        this._fluidCapability.invalidate();
    }

    private IFluidHandler createFluidCapability() {
        return this.getMultiblockController()
                .flatMap(reactor -> reactor.getFluidHandler(FluidType.from(this.getIoDirection())))
                .orElse(EmptyFluidHandler.INSTANCE);
    }

    private void transferGas(Direction direction) {

        final BlockPos targetPosition = this.getWorldPosition().offset(direction);

        this.getMultiblockController()
                .flatMap(MultiblockReactor::getGasHandler)
                .ifPresent(source -> FluidHelper.tryFluidTransfer(source, this.getPartWorldOrFail(), targetPosition,
                        direction.getOpposite(), Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE));
    }

    @CapabilityInject(IFluidHandler.class)
    private static Capability<IFluidHandler> CAPAB_FLUID_HANDLER = null;

    private final LazyOptional<IFluidHandler> _fluidCapability;
    private IoDirection _direction;

    //endregion
}
