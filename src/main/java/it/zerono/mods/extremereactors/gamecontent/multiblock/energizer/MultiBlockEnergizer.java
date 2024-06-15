/*
 * MultiBlockEnergizer
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * Do not remove or edit this header
 *
 */

package it.zerono.mods.extremereactors.gamecontent.multiblock.energizer;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.AbstractEnergyGeneratorMultiblockController;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.part.AbstractEnergizerEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.part.EnergizerChargingPortEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.part.EnergizerControllerEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.part.EnergizerPowerPortEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.variant.EnergizerVariant;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.variant.IMultiblockEnergizerVariant;
import it.zerono.mods.zerocore.base.multiblock.part.io.power.IPowerPort;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.IActivableMachine;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.IDebuggable;
import it.zerono.mods.zerocore.lib.block.ModBlock;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import it.zerono.mods.zerocore.lib.data.stack.OperationMode;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.energy.MeteredWideEnergyBuffer;
import it.zerono.mods.zerocore.lib.energy.WideEnergyBuffer;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockMachine;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.LogicalSide;

import java.util.List;

public class MultiBlockEnergizer
        extends AbstractEnergyGeneratorMultiblockController<MultiBlockEnergizer, IMultiblockEnergizerVariant>
        implements IMultiblockMachine, IActivableMachine, ISyncableEntity, IDebuggable {

    public MultiBlockEnergizer(Level world) {

        super(world);
        this._meteredBuffer = (MeteredWideEnergyBuffer) this.getEnergyBuffer();
        this._attachedChargingPorts = ObjectLists.emptyList();
    }

    @Override
    protected WideEnergyBuffer createBuffer(EnergySystem system, WideAmount capacity) {
        return new MeteredWideEnergyBuffer(system, capacity);
    }

    public WideAmount getEnergyIoRate(EnergySystem system) {
        return this._meteredBuffer.getIoRateLastTick(system);
    }

    public WideAmount getEnergyInsertedLastTick(EnergySystem system) {
        return this._meteredBuffer.getInsertedLastTick(system);
    }

    public WideAmount getEnergyExtractedLastTick(EnergySystem system) {
        return this._meteredBuffer.getExtractedLastTick(system);
    }

    /**
     * Reset the internal data
     * --- FOR TESTING PURPOSES ONLY ---
     */
    public void reset() {

        this.setMachineActive(false);
        this.getEnergyBuffer().empty();
    }

    //region IWideEnergyStorage2

    @Override
    public WideAmount insertEnergy(EnergySystem system, WideAmount maxAmount, OperationMode mode) {

        if (this.isMachineActive()) {
            return super.insertEnergy(system, maxAmount, mode);
        }

        return WideAmount.ZERO;
    }

    @Override
    public WideAmount extractEnergy(EnergySystem system, WideAmount maxAmount, OperationMode mode) {

        if (this.isMachineActive()) {
            return super.extractEnergy(system, maxAmount, mode);
        }

        return WideAmount.ZERO;
    }

    //endregion
    //region IActivableMachine

    /**
     * @return true if the machine is active, false otherwise
     */
    @Override
    public boolean isMachineActive() {
        return this._active;
    }

    /**
     * Change the state of the machine
     *
     * @param active if true, activate the machine; if false, deactivate it
     */
    @Override
    public void setMachineActive(boolean active) {

        if (this.isMachineActive() == active) {
            return;
        }

        this._active = active;

        if (active) {
            this.getConnectedParts().forEach(IMultiblockPart::onMachineActivated);
        } else {
            this.getConnectedParts().forEach(IMultiblockPart::onMachineDeactivated);
        }

        this.callOnLogicalServer(this::markReferenceCoordForUpdate);
    }

    //endregion
    //region ISyncableEntity

    @Override
    public void syncDataFrom(CompoundTag data, SyncReason syncReason) {

        super.syncDataFrom(data, syncReason);
        this.syncBooleanElementFrom("active", data, b -> this._active = b);
    }

    @Override
    public CompoundTag syncDataTo(CompoundTag data, SyncReason syncReason) {

        super.syncDataTo(data, syncReason);
        this.syncBooleanElementTo("active", data, this.isMachineActive());

        return data;
    }

    //endregion
    //region IDebuggable

    @Override
    public void getDebugMessages(final LogicalSide side, final IDebugMessages messages) {

        if (!this.isAssembled()) {
            return;
        }

        this._meteredBuffer.getDebugMessages(side, messages);
    }

    //endregion
    //region AbstractEnergyGeneratorMultiblockController

    @Override
    public IMultiblockEnergizerVariant getVariant() {
        return EnergizerVariant.INSTANCE;
    }

    @Override
    protected void sendClientUpdates() {

        final ProfilerFiller profiler = this.getWorld().getProfiler();

        profiler.push("sendTickUpdate");
        this.sendUpdates();
        profiler.pop();
    }

    //endregion
    //region AbstractMultiblockController

    @Override
    public boolean isPartCompatible(final IMultiblockPart<MultiBlockEnergizer> part) {
        return part instanceof AbstractEnergizerEntity;
    }

    @Override
    protected void onPartAdded(IMultiblockPart<MultiBlockEnergizer> newPart) {

        if (newPart instanceof EnergizerChargingPortEntity) {

            if (ObjectLists.<IPowerPort>emptyList() == this._attachedChargingPorts) {
                this._attachedChargingPorts = new ObjectArrayList<>(4);
            }

            this._attachedChargingPorts.add((IPowerPort) newPart);
        }
    }

    /**
     * Called when a part is removed from the machine. Good time to clean up lists.
     *
     * @param oldPart The part being removed.
     */
    @Override
    protected void onPartRemoved(IMultiblockPart<MultiBlockEnergizer> oldPart) {

        if (oldPart instanceof EnergizerChargingPortEntity &&
                ObjectLists.<IPowerPort>emptyList() != this._attachedChargingPorts) {
            this._attachedChargingPorts.remove(oldPart);
        }
    }

    /**
     * Called when a machine is assembled from a disassembled state.
     */
    @Override
    protected void onMachineAssembled() {

        //resize energy buffer

        this.getEnergyBuffer().setCapacity(this.getVariant()
                .getStorageCapacityPerBlock().multiply(this.getBoundingBox().getInternalVolume()));
        this.getEnergyBuffer().setMaxTransfer(this.getVariant().getMaxEnergyExtractionRate());

        // re-render the whole reactor
        this.callOnLogicalSide(
                this::markReferenceCoordForUpdate,
                () -> {
                    // Make sure our fuel rods re-render
                    this.markMultiblockForRenderUpdate();
                }
        );

        super.onMachineAssembled();
    }

    /**
     * Called when a machine is disassembled from an assembled state.
     * This happens due to user or in-game actions (e.g. explosions)
     */
    @Override
    protected void onMachineDisassembled() {

        this.setMachineActive(false);

        // do not call setMachineActive() here
        this._active = false;

        this.markMultiblockForRenderUpdate();
    }

    @Override
    protected boolean isMachineWhole(IMultiblockValidator validatorCallback) {

        // Ensure that there is at least one Controller attached

        if (!this.isAnyPartConnected(part -> part instanceof EnergizerControllerEntity)) {

            validatorCallback.setLastError("multiblock.validation.energizer.too_few_controllers");
            return false;
        }

        // Perform base checks...

        return super.isMachineWhole(validatorCallback);
    }

    @Override
    protected void onAssimilate(IMultiblockController<MultiBlockEnergizer> assimilated) {

        if (!(assimilated instanceof MultiBlockEnergizer otherEnergizer)) {

            Log.LOGGER.warn(Log.ENERGIZER, "[{}] Energizer @ {} is attempting to assimilate a non-Energizer machine! That machine's data will be lost!",
                    CodeHelper.getWorldSideName(this.getWorld()), this.getReferenceCoord());
            return;
        }

        this.getEnergyBuffer().merge(otherEnergizer.getEnergyBuffer());
    }

    @Override
    protected void onAssimilated(IMultiblockController<MultiBlockEnergizer> assimilator) {
        this._attachedChargingPorts.clear();
    }

    @Override
    protected boolean updateServer() {

        final ProfilerFiller profiler = this.getWorld().getProfiler();

        profiler.push("Extreme Reactors|Energizer update"); // main section

        profiler.push("Charging");

        if (this.isMachineActive()) {
            this.distributeEnergyEqually();
        }

        profiler.popPush("Tickables");
        this._meteredBuffer.tick();

        profiler.popPush("Updates");
        this.checkAndSendClientUpdates();

        profiler.popPush("Mark4Update");

        profiler.pop(); // Mark4Update
        profiler.pop(); // main section

        return this.getEnergyBuffer().modified();
    }

    protected boolean isBlockGoodForFrame(Level world, int x, int y, int z, IMultiblockValidator validatorCallback) {
        return invalidBlockForExterior(world, x, y, z, validatorCallback);
    }

    @Override
    protected boolean isBlockGoodForTop(Level world, int x, int y, int z, IMultiblockValidator validatorCallback) {
        return invalidBlockForExterior(world, x, y, z, validatorCallback);
    }

    @Override
    protected boolean isBlockGoodForBottom(Level world, int x, int y, int z, IMultiblockValidator validatorCallback) {
        return invalidBlockForExterior(world, x, y, z, validatorCallback);
    }

    @Override
    protected boolean isBlockGoodForSides(Level world, int x, int y, int z, IMultiblockValidator validatorCallback) {
        return invalidBlockForExterior(world, x, y, z, validatorCallback);
    }

    @Override
    protected boolean isBlockGoodForInterior(Level world, int x, int y, int z, IMultiblockValidator validatorCallback) {

        final BlockPos position = new BlockPos(x, y, z);
        final BlockState blockState = world.getBlockState(position);

        if (blockState.is(Content.Blocks.ENERGIZER_CELL.get())) {
            return true;
        }

        // Give up ...
        validatorCallback.setLastError(position, "multiblock.validation.energizer.invalid_block_for_interior",
                ModBlock.getNameForTranslation(blockState.getBlock()));
        return false;
    }

    //endregion
    //region internals

    private static boolean invalidBlockForExterior(Level world, int x, int y, int z, IMultiblockValidator validatorCallback) {

        final BlockPos position = new BlockPos(x, y, z);

        validatorCallback.setLastError(position, "multiblock.validation.energizer.invalid_block_for_exterior",
                ModBlock.getNameForTranslation(world.getBlockState(position).getBlock()));
        return false;
    }

    private void distributeEnergyEqually() {

        final WideEnergyBuffer energyBuffer = this.getEnergyBuffer();
        final WideAmount amountDistributed = distributeEnergyEqually(energyBuffer.getEnergyStored(),
                this._attachedChargingPorts);

        if (amountDistributed.greaterThan(WideAmount.ZERO)) {
            energyBuffer.shrink(amountDistributed);
        }
    }

    private final MeteredWideEnergyBuffer _meteredBuffer;
    private List<IPowerPort> _attachedChargingPorts;

    //endregion
}
