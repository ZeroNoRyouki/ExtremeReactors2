/*
 *
 * MultiblockReactor.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.extremereactors.api.radiation.RadiationPacket;
import it.zerono.mods.extremereactors.api.reactor.*;
import it.zerono.mods.extremereactors.api.reactor.radiation.EnergyConversion;
import it.zerono.mods.extremereactors.api.reactor.radiation.IRadiationModerator;
import it.zerono.mods.extremereactors.api.reactor.radiation.IrradiationData;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.*;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.network.UpdateClientsFuelRodsLayout;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.*;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.IMultiblockReactorVariant;
import it.zerono.mods.zerocore.base.multiblock.part.io.power.IPowerPort;
import it.zerono.mods.zerocore.base.multiblock.part.io.power.IPowerPortHandler;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.IDebuggable;
import it.zerono.mods.zerocore.lib.block.ModBlock;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartTypeProvider;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.data.geometry.CuboidBoundingBox;
import it.zerono.mods.zerocore.lib.data.stack.AllowedHandlerAction;
import it.zerono.mods.zerocore.lib.data.stack.OperationMode;
import it.zerono.mods.zerocore.lib.energy.WideEnergyBuffer;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.lib.multiblock.ITickableMultiblockPart;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockPart;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;
import java.util.Optional;
import java.util.function.DoubleSupplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MultiblockReactor
        extends AbstractFluidGeneratorMultiblockController<MultiblockReactor, IMultiblockReactorVariant>
        implements IReactorMachine, IReactorEnvironment, IReactorWriter, IDebuggable {

    public MultiblockReactor(final Level world, final IMultiblockReactorVariant variant) {

        super(world);
        this._variant = variant;

        this._fuelContainer = new FuelContainer();
        this._fluidContainer = new FluidContainer(FLUID_CONTAINER_ACCESS);
        this._fuelHeat = new Heat();
        this._reactorHeat = new Heat();
        this._fuelRodsLayout = FuelRodsLayout.EMPTY;
        this._uiStats = new Stats(this._fuelContainer);

        this._mode = OperationalMode.Passive;
        this._wasteEjectionSetting = WasteEjectionSetting.Automatic;
        this._reactorVolume = 0;
        this._fuelToReactorHeatTransferCoefficient = 0f;
        this._reactorToCoolantSystemHeatTransferCoefficient = 0f;
        this._reactorHeatLossCoefficient = 0f;

        this._attachedTickables = ObjectLists.emptyList();
        this._attachedControlRods = Lists.newLinkedList();
        this._attachedFuelRods = new FuelRodsMap();
        this._attachedSolidAccessPorts = new ObjectArrayList<>(8);
        this._attachedFluidAccessPorts = new ObjectArrayList<>(8);
        this._attachedPowerTaps = ObjectLists.emptyList();
        this._attachedFluidPorts = this._attachedOutputFluidPorts = this._attachedInputFluidPorts = ObjectLists.emptyList();

        this._logic = new ReactorLogic(this, this.getEnergyBuffer());

        this._sendUpdateFuelRodsLayoutDelayedRunnable = CodeHelper.delayedRunnable(this::sendUpdateFuelRodsLayout, 20 * 10);
        this._sendUpdateFuelRodsLayout = false;
    }

    /**
     * Reset the internal data
     * --- FOR TESTING PURPOSES ONLY ---
     */
    public void reset() {

        this.setMachineActive(false);
        this._fuelContainer.reset();
        this._fluidContainer.reset();
        this._fuelHeat.set(0);
        this._reactorHeat.set(0);
        this._uiStats.setAmountGeneratedLastTick(0);
        this._uiStats.setFuelConsumedLastTick(0);
        this._fuelToReactorHeatTransferCoefficient = 0f;
        this._reactorToCoolantSystemHeatTransferCoefficient = 0f;
        this._reactorHeatLossCoefficient = 0f;
        this._logic.reset();
        this.getEnergyBuffer().empty();

        this.resizeFuelContainer();
        this.calculateReactorVolume();
        this.updateFuelToReactorHeatTransferCoefficient();
        this.updateReactorToCoolantSystemHeatTransferCoefficient();
        this.updateReactorHeatLossCoefficient();
        this.resizeFluidContainer();

        this._fuelRodsLayout.reset();
        this._attachedFuelRods.markFuelRodsForRenderUpdate();
        this._attachedFuelRods.reset();
    }

    public Optional<ReactorControlRodEntity> getControlRodByIndex(int index) {

        if (index < 0 || index > this.getControlRodsCount()) {
            return Optional.empty();
        } else {
            return Optional.of(this._attachedControlRods.get(index));
        }
    }

    public void onFluidPortChanged() {
        this.rebuildFluidPortsSubsets();
    }

    public void onUpdateClientsFuelRodsLayout(final UpdateClientsFuelRodsLayout message) {

        if (this.calledByLogicalClient()) {

            this._fuelContainer.syncDataFrom(message.getFuelContainerData(), SyncReason.NetworkUpdate);
            this.updateClientFuelRodsLayout();
        }
    }

    public void voidReactants() {

        this._fuelContainer.voidFuel();
        this._fuelContainer.voidWaste();
    }

    //region active-coolant system

    @Override
    public Optional<IFluidHandler> getLiquidHandler() {
        return this.getFluidHandler(IoDirection.Input);
    }

    @Override
    public Optional<IFluidHandler> getGasHandler() {
        return this.getFluidHandler(IoDirection.Output);
    }

    @Override
    public Optional<IFluidHandler> getFluidHandler(final IoDirection portDirection) {
        return this.getOperationalMode().isActive() ? Optional.of(this._fluidContainer.getWrapper(portDirection)) : Optional.empty();
    }

    //endregion
    //region IActivableMachine

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
            this.forEachConnectedParts(IMultiblockPart::onMachineActivated);
        } else {
            this.forEachConnectedParts(IMultiblockPart::onMachineDeactivated);
        }

        this.callOnLogicalServer(this::markReferenceCoordForUpdate);
    }

    //endregion
    //region IReactorMachine

    @Override
    public IReactorEnvironment getEnvironment() {
        return this;
    }

    @Override
    public IHeat getFuelHeat() {
        return this._fuelHeat;
    }

    @Override
    public IFuelContainer getFuelContainer() {
        return this._fuelContainer;
    }

    @Override
    public IFluidContainer getFluidContainer() {
        return this._fluidContainer;
    }

    @Override
    public Stats getUiStats() {
        return this._uiStats;
    }

    /**
     * Perform a refueling cycle, ejecting waste and inserting new fuel into the Reactor
     */
    @Override
    public boolean performRefuelingCycle() {

        boolean changed = false;

        if (this.getWasteEjectionMode().isAutomatic()) {

            this.ejectWaste(false);
            changed = true;
        }

        changed |= this.refuelFluid();
        changed |= this.refuelSolid();

        return changed;
    }

    /**
     * Output power/gas to active ports
     */
    @Override
    public void performOutputCycle() {

        final ProfilerFiller profiler = this.getWorld().getProfiler();

        if (this.getOperationalMode().isPassive()) {

            // Distribute available power equally to all the Power Taps
            profiler.push("Power");
            this.distributeEnergyEqually();

        } else {

            // Distribute available gas equally to all the Coolant Ports in output mode
            profiler.push("Gas");
            this.distributeGasEqually();
        }

        profiler.pop();
    }

    /**
     * Input fluid from active ports
     */
    @Override
    public boolean performInputCycle() {

        final ProfilerFiller profiler = this.getWorld().getProfiler();
        boolean changed = false;

        if (this.getOperationalMode().isActive()) {

            // Acquire new fluids from Active Fluid Ports in input mode
            profiler.push("Coolant");
            changed = this.acquireFluidEqually();
        }

        profiler.pop();
        return changed;
    }

    //endregion
    //region IReactorEnvironment

    @Override
    public boolean isSimulator() {
        return false;
    }

    @Override
    public IHeat getReactorHeat() {
        return this._reactorHeat;
    }

    @Override
    public int getReactorVolume() {
        return this._reactorVolume;
    }

    @Override
    public float getFuelToReactorHeatTransferCoefficient() {
        return this._fuelToReactorHeatTransferCoefficient;
    }

    @Override
    public float getReactorToCoolantSystemHeatTransferCoefficient() {
        return this._reactorToCoolantSystemHeatTransferCoefficient;
    }

    @Override
    public float getReactorHeatLossCoefficient() {
        return this._reactorHeatLossCoefficient;
    }

    @Override
    public IIrradiationSource getNextIrradiationSource() {
        return this._attachedFuelRods.getNextIrradiationSource();
    }

    @SuppressWarnings("unchecked")
    @Override
    public int getPartsCount(IReactorPartType type) {

        if (type instanceof ReactorPartType partType) {
            switch (partType) {

                case ControlRod:
                    return this._attachedControlRods.size();

                case FuelRod:
                    return this._attachedFuelRods.size();

                case SolidAccessPort:
                    return this._attachedSolidAccessPorts.size();

                case FluidAccessPort:
                    return this._attachedFluidAccessPorts.size();

                case ActivePowerTapFE:
                case PassivePowerTapFE:
                case ChargingPortFE:
                    return this._attachedPowerTaps.size();

                case ActiveFluidPortForge:
                case PassiveFluidPortForge:
                    return this._attachedFluidPorts.size();
            }
        }

        return this.getPartsCount(part -> part instanceof IMultiblockPartTypeProvider &&
                ((IMultiblockPartTypeProvider<MultiblockReactor, IReactorPartType>)part).getPartType()
                        .filter(partType -> partType == type)
                        .isPresent());
    }

    /**
     * Get a Moderator from the Reactor internal volume
     *
     * @param position the position to look up. Must be inside the Reactor internal volume
     * @return the Moderator at the requested position, if the position is valid and a Moderator is found there
     */
    @Override
    public IRadiationModerator getModerator(final BlockPos position) {

        if (!this.getBoundingBox().contains(position)) {
            return MODERATOR_NONE;
        }

        final Level world = this.getWorld();
        final BlockState blockState = world.getBlockState(position);

        if (blockState.isAir()) {
            return MODERATOR_AIR;
        }

        if (blockState.hasBlockEntity()) {

            final BlockEntity te = WorldHelper.getLoadedTile(world, position);

            return te instanceof IRadiationModerator ? (IRadiationModerator)te : MODERATOR_NONE;

        } else {

            return (data, packet) -> applyModerator(data, packet, ReactantHelper.getModeratorFrom(blockState, Moderator.AIR));
        }
    }

    private static void applyModerator(final IrradiationData data, final RadiationPacket radiation, final Moderator moderator) {

        final float radiationAbsorbed = radiation.intensity * moderator.getAbsorption() * (1f - radiation.hardness);

        radiation.intensity = Math.max(0f, radiation.intensity - radiationAbsorbed);
        radiation.hardness /= moderator.getModeration();
        data.environmentEnergyAbsorption += moderator.getHeatEfficiency() * radiationAbsorbed * EnergyConversion.ENERGY_PER_RADIATION_UNIT;
    }

    /**
     * Perform a refueling cycle (eject waste, push new fuel in, etc)
     */
    @Override
    public void refuel() {
        // ??????????????????????
    }

    //endregion
    //region IReactorReader

    @Override
    public OperationalMode getOperationalMode() {
        return this._mode;
    }

    @Override
    public int getFuelAmount() {
        return this._fuelContainer.getFuelAmount();
    }

    @Override
    public int getWasteAmount() {
        return this._fuelContainer.getWasteAmount();
    }

    @Override
    public int getCapacity() {

        if (this.calledByLogicalClient() && !this.isAssembled()) {
            return this.getFuelRodsCount() * ReactorFuelRodEntity.FUEL_CAPACITY_PER_FUEL_ROD;
        } else {
            return this._fuelContainer.getCapacity();
        }
    }

    @Override
    public Optional<Reactant> getFuel() {
        return this._fuelContainer.getFuel();
    }

    @Override
    public FuelProperties getFuelProperties() {
        return this._fuelContainer.getFuelProperties();
    }

    @Override
    public Optional<Reactant> getWaste() {
        return this._fuelContainer.getWaste();
    }

    @Override
    public float getFuelFertility() {
        return this._logic.getFertility();
    }

    @Override
    public DoubleSupplier getFuelHeatValue() {
        return this._fuelHeat;
    }

    @Override
    public DoubleSupplier getReactorHeatValue() {
        return this._reactorHeat;
    }

    @Override
    public WasteEjectionSetting getWasteEjectionMode() {
        return this._wasteEjectionSetting;
    }

    @Override
    public FuelRodsLayout getFuelRodsLayout() {
        return this._fuelRodsLayout;
    }

    @Override
    public int getFuelRodsCount() {
        return this._attachedFuelRods.size();
    }

    @Override
    public int getControlRodsCount() {
        return this._attachedControlRods.size();
    }

    @Override
    public int getPowerTapsCount() {
        return this._attachedPowerTaps.size();
    }

    //TODO used???
    public List<BlockPos> getControlRodLocations() {
        return this._attachedControlRods.stream()
                .map(IMultiblockPart::getWorldPosition)
                .collect(Collectors.toList());
    }

    @Override
    public int getCoolantAmount() {
        return this.getOperationalMode().isPassive() ? 0 : this.getFluidContainer().getLiquidAmount();
    }

    @Override
    public int getVaporAmount() {
        return this.getOperationalMode().isPassive() ? 0 : this.getFluidContainer().getGasAmount();
    }

    //endregion
    //region IReactorWriter

    /**
     * Directly set the waste ejection setting
     *
     * @param newSetting the new waste ejection setting.
     */
    @Override
    public void setWasteEjectionMode(WasteEjectionSetting newSetting) {

        if (this.getWasteEjectionMode() != newSetting) {

            this._wasteEjectionSetting = newSetting;
            this.markReferenceCoordDirty();
        }
    }

    @Override
    public void setControlRodsInsertionRatio(final int newRatio) {
        if (this.isAssembled()) {
            ReactorControlRodEntity.setInsertionRatio(this._attachedControlRods, Mth.clamp(newRatio, 0, 100));
        }
    }

    @Override
    public void changeControlRodsInsertionRatio(int delta) {

        if (this.isAssembled()) {
            ReactorControlRodEntity.changeInsertionRatio(this._attachedControlRods, delta);
        }
    }

    /**
     * Attempt to eject fuel contained in the Reactor
     */
    @Override
    public void ejectFuel() {
        this.ejectFuel(false);
    }

    /**
     * Attempt to eject fuel contained in the Reactor
     *
     * @param voidLeftover if true, any remaining fuel will be voided
     */
    @Override
    public void ejectFuel(final boolean voidLeftover) {
        this.ejectReactant(ReactantType.Fuel, voidLeftover, this.getInputFluidAccessPorts(), this.getInputSolidAccessPorts());
    }

    /**
     * Attempt to eject fuel contained in the Reactor
     *
     * @param voidLeftover if true, any remaining fuel will be voided
     * @param portPosition coordinates of the Access Port to witch distribute fuel
     */
    @Override
    public void ejectFuel(final boolean voidLeftover, final BlockPos portPosition) {
        this.ejectReactant(ReactantType.Fuel, voidLeftover,
                this.getInputFluidAccessPorts().filter(port -> portPosition.equals(port.getWorldPosition())),
                this.getInputSolidAccessPorts().filter(port -> portPosition.equals(port.getWorldPosition())));
    }

    /**
     * Attempt to eject waste contained in the Reactor
     */
    @Override
    public void ejectWaste() {
        this.ejectWaste(false);
    }

    /**
     * Attempt to eject waste contained in the Reactor
     *
     * @param voidLeftover if true, any remaining waste will be voided
     */
    @Override
    public void ejectWaste(final boolean voidLeftover) {
        this.ejectReactant(ReactantType.Waste, voidLeftover, this.getOutputFluidAccessPorts(), this.getOutputSolidAccessPorts());
    }

    /**
     * Attempt to eject waste contained in the Reactor
     *
     * @param voidLeftover if true, any remaining waste will be voided
     * @param portPosition coordinates of the Access Port to witch distribute waste
     */
    @Override
    public void ejectWaste(final boolean voidLeftover, final BlockPos portPosition) {
        this.ejectReactant(ReactantType.Waste, voidLeftover,
                this.getInputFluidAccessPorts().filter(port -> portPosition.equals(port.getWorldPosition())),
                this.getInputSolidAccessPorts().filter(port -> portPosition.equals(port.getWorldPosition())));
    }

    //endregion
    //region ISyncableEntity

    /**
     * Sync the entity data from the given NBT compound
     *
     * @param data       the data
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public void syncDataFrom(CompoundTag data, SyncReason syncReason) {

        super.syncDataFrom(data, syncReason);

        if (data.contains("wasteeject")) {
            this._wasteEjectionSetting = WasteEjectionSetting.read(data, "wasteeject", WasteEjectionSetting.Automatic);
        }

        this._logic.syncDataFrom(data, syncReason);

        this.syncChildDataEntityFrom(this._fuelContainer, "fuelcontainer", data, syncReason);
        this.syncChildDataEntityFrom(this._fluidContainer, "fluidcontainer", data, syncReason);
        this.syncChildDataEntityFrom(this._fuelHeat, "fuelheat", data, syncReason);
        this.syncChildDataEntityFrom(this._reactorHeat, "reactorheat", data, syncReason);

        if (syncReason.isNetworkUpdate()) {

            this.syncChildDataEntityFrom(this._uiStats, "stats", data, syncReason);
            this.updateClientFuelRodsLayout();
        }
    }

    /**
     * Sync the entity data to the given NBT compound
     *
     * @param data       the data
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public CompoundTag syncDataTo(CompoundTag data, SyncReason syncReason) {

        super.syncDataTo(data, syncReason);

        WasteEjectionSetting.write(data, "wasteeject", this.getWasteEjectionMode());

        this._logic.syncDataTo(data, syncReason);

        this.syncChildDataEntityTo(this._fuelContainer, "fuelcontainer", data, syncReason);
        this.syncChildDataEntityTo(this._fluidContainer, "fluidcontainer", data, syncReason);
        this.syncChildDataEntityTo(this._fuelHeat, "fuelheat", data, syncReason);
        this.syncChildDataEntityTo(this._reactorHeat, "reactorheat", data, syncReason);

        if (syncReason.isNetworkUpdate()) {
            this.syncChildDataEntityTo(this._uiStats, "stats", data, syncReason);
        }

        return data;
    }

    //endregion
    //region AbstractFluidGeneratorMultiblockController

    @Override
    public IMultiblockReactorVariant getVariant() {
        return this._variant;
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
    public boolean isPartCompatible(final IMultiblockPart<MultiblockReactor> part) {
        return (part instanceof AbstractReactorEntity) &&
                ((AbstractReactorEntity) part).getMultiblockVariant()
                        .filter(variant -> this.getVariant() == variant)
                        .isPresent();
    }

    /**
     * Called when a new part is added to the machine. Good time to register things into lists.
     *
     * @param newPart The part being added.
     */
    @Override
    protected void onPartAdded(IMultiblockPart<MultiblockReactor> newPart) {

        if (newPart instanceof ITickableMultiblockPart && this.calledByLogicalServer()) {

            if (ObjectLists.<ITickableMultiblockPart>emptyList() == this._attachedTickables) {
                this._attachedTickables = new ObjectArrayList<>(4);
            }

            this._attachedTickables.add((ITickableMultiblockPart) newPart);
        }

        if (newPart instanceof ReactorControlRodEntity) {
            this._attachedControlRods.add((ReactorControlRodEntity) newPart);
        } else if (newPart instanceof ReactorFuelRodEntity) {
            this._attachedFuelRods.add((ReactorFuelRodEntity) newPart);
        } else if (newPart instanceof ReactorSolidAccessPortEntity) {
            this._attachedSolidAccessPorts.add((ReactorSolidAccessPortEntity) newPart);
        } else if (newPart instanceof ReactorFluidAccessPortEntity) {
            this._attachedFluidAccessPorts.add((ReactorFluidAccessPortEntity) newPart);
        } else if (newPart instanceof ReactorPowerTapEntity || newPart instanceof ReactorChargingPortEntity) {

            if (ObjectLists.<IPowerPort>emptyList() == this._attachedPowerTaps) {
                this._attachedPowerTaps = new ObjectArrayList<>(4);
            }

            this._attachedPowerTaps.add((IPowerPort) newPart);

        } else if (newPart instanceof ReactorFluidPortEntity) {

            if (ObjectLists.<ReactorFluidPortEntity>emptyList() == this._attachedFluidPorts) {
                this._attachedFluidPorts = new ObjectArrayList<>(4);
            }

            this._attachedFluidPorts.add((ReactorFluidPortEntity) newPart);
        }
    }

    /**
     * Called when a part is removed from the machine. Good time to clean up lists.
     *
     * @param oldPart The part being removed.
     */
    @Override
    protected void onPartRemoved(IMultiblockPart<MultiblockReactor> oldPart) {

        if (oldPart instanceof ITickableMultiblockPart && this.calledByLogicalServer() &&
                ObjectLists.<ITickableMultiblockPart>emptyList() != this._attachedTickables) {
            this._attachedTickables.remove(oldPart);
        }

        if (oldPart instanceof ReactorControlRodEntity) {
            this._attachedControlRods.remove(oldPart);
        } else if (oldPart instanceof ReactorFuelRodEntity) {
            this._attachedFuelRods.remove((ReactorFuelRodEntity) oldPart);
        } else if (oldPart instanceof ReactorSolidAccessPortEntity) {
            this._attachedSolidAccessPorts.remove(oldPart);
        } else if (oldPart instanceof ReactorFluidAccessPortEntity) {
            this._attachedFluidAccessPorts.remove(oldPart);
        } else if ((oldPart instanceof ReactorPowerTapEntity || oldPart instanceof ReactorChargingPortEntity) &&
                ObjectLists.<IPowerPort>emptyList() != this._attachedPowerTaps) {
            this._attachedPowerTaps.remove(oldPart);
        } else if (oldPart instanceof ReactorFluidPortEntity && ObjectLists.<ReactorFluidPortEntity>emptyList() != this._attachedFluidPorts) {
            this._attachedFluidPorts.remove(oldPart);
        }
    }

    /**
     * Called when a machine is assembled from a disassembled state.
     */
    @Override
    protected void onMachineAssembled() {

        // set the output EnergySystem
        if (this._attachedPowerTaps.isEmpty()) {
            this.setOutputEnergySystem(INTERNAL_ENERGY_SYSTEM);
        } else {
            CodeHelper.optionalIfPresentOrThrow(this._attachedPowerTaps.stream()
                            .map(IPowerPort::getPowerPortHandler)
                            .map(IPowerPortHandler::getEnergySystem)
                            .findFirst(),
                    this::setOutputEnergySystem);
        }

        // operation mode : if any coolant ports is present we are in active mode
        this._mode = this.isAnyPartConnected(part -> part instanceof ReactorFluidPortEntity) ?
                OperationalMode.Active : OperationalMode.Passive;

        // interior visible?
        this.setInteriorInvisible(!this.isAnyPartConnected(part -> part instanceof ReactorGlassEntity));

        // build a new fuel rods layout and link all the fuel rods to their control rods
        this._fuelRodsLayout = this.createFuelRodsLayout();
        this._attachedControlRods.forEach(rod -> rod.linkToFuelRods(this._fuelRodsLayout.getRodLength()));

        // set fuel rods occlusion status
        this._fuelRodsLayout.updateFuelRodsOcclusion(this.getWorld(), this._attachedFuelRods, this.isInteriorInvisible());

        // gather outgoing coolant ports
        this.rebuildFluidPortsSubsets();

        // update internal data


        //resize energy buffer

        this.getEnergyBuffer().setCapacity(WideAmount.from((long) this.getVariant().getPartEnergyCapacity() * this.getPartsCount()));
        this.getEnergyBuffer().setMaxExtract(this.getVariant().getMaxEnergyExtractionRate());

        //TODO check/fix

        this.resizeFuelContainer();
        this.calculateReactorVolume();
        this.updateFuelToReactorHeatTransferCoefficient();
        this.updateReactorToCoolantSystemHeatTransferCoefficient();
        this.updateReactorHeatLossCoefficient();
        this.resizeFluidContainer();

        // re-render the whole reactor
        this.callOnLogicalSide(
                this::markReferenceCoordForUpdate,
                () -> {
                    // Make sure our fuel rods re-render
                    this.updateClientFuelRodsLayout();
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

        // Ensure that there is at least one Controller and one Control Rod attached

        if (this.getControlRodsCount() < 1) {

            validatorCallback.setLastError("multiblock.validation.reactor.too_few_rods");
            return false;
        }

        if (!this.isAnyPartConnected(part -> part instanceof ReactorControllerEntity)) {

            validatorCallback.setLastError("multiblock.validation.reactor.too_few_controllers");
            return false;
        }

        // Validate Control Rods orientation and Fuel Assemblies

        if (!this.validateFuelAssemblies(validatorCallback)) {
            return false;
        }

        // Check if the machine has a single power system

        if (!this.validateEnergySystems(validatorCallback)) {
            return false;
        }

        // Perform base checks...

        return super.isMachineWhole(validatorCallback);
    }

    /**
     * Callback. Called after this controller assimilates all the blocks
     * from another controller.
     * Use this to absorb that controller's game data.
     *
     * @param assimilated The controller whose uniqueness was added to our own.
     */
    @Override
    protected void onAssimilate(IMultiblockController<MultiblockReactor> assimilated) {

        if (!(assimilated instanceof MultiblockReactor)) {

            Log.LOGGER.warn(Log.REACTOR, "[{}] Reactor @ {} is attempting to assimilate a non-Reactor machine! That machine's data will be lost!",
                    CodeHelper.getWorldSideName(this.getWorld()), this.getReferenceCoord());
            return;
        }

        final MultiblockReactor otherReactor = (MultiblockReactor)assimilated;

        if (otherReactor._reactorHeat.getAsDouble() > this._reactorHeat.getAsDouble()) {
            this._reactorHeat.set(otherReactor._reactorHeat.getAsDouble());
        }

        if (otherReactor._fuelHeat.getAsDouble() > this._fuelHeat.getAsDouble()) {
            this._fuelHeat.set(otherReactor._fuelHeat.getAsDouble());
        }

        final WideAmount otherEnergy = otherReactor.getEnergyBuffer().getEnergyStored(INTERNAL_ENERGY_SYSTEM);

        if (otherEnergy.greaterThan(this.getEnergyBuffer().getEnergyStored(INTERNAL_ENERGY_SYSTEM))) {
            this.getEnergyBuffer().setEnergyStored(otherEnergy, INTERNAL_ENERGY_SYSTEM);
        }

        this._logic.syncDataFrom(otherReactor._logic);
        this._fuelContainer.syncDataFrom(otherReactor._fuelContainer);
        this._fluidContainer.syncDataFrom(otherReactor._fluidContainer);
    }

    /**
     * Callback. Called after this controller is assimilated into another controller.
     * All blocks have been stripped out of this object and handed over to the
     * other controller.
     * This is intended primarily for cleanup.
     *
     * @param assimilator The controller which has assimilated this controller.
     */
    @Override
    protected void onAssimilated(IMultiblockController<MultiblockReactor> assimilator) {

        this._attachedTickables.clear();
        this._attachedControlRods.clear();
        this._attachedFuelRods.clear();
        this._attachedSolidAccessPorts.clear();
        this._attachedFluidAccessPorts.clear();
        this._attachedPowerTaps.clear();
        this._attachedFluidPorts.clear();
        this._attachedOutputFluidPorts = this._attachedInputFluidPorts = ObjectLists.emptyList();
        this._fuelRodsLayout = FuelRodsLayout.EMPTY;
    }

    /**
     * The server-side update loop! Use this similarly to a TileEntity's update loop.
     * You do not need to call your superclass' update() if you're directly
     * derived from AbstractMultiblockController. This is a callback.
     * Note that this will only be called when the machine is assembled.
     *
     * @return True if the multiblock should save data, i.e. its internal game state has changed. False otherwise.
     */
    @Override
    protected boolean updateServer() {

        final ProfilerFiller profiler = this.getWorld().getProfiler();

        profiler.push("Extreme Reactors|Reactor update"); // main section

        profiler.push("Generate");
        final boolean updateResult = this._logic.update();

        //////////////////////////////////////////////////////////////////////////////
        // TICKABLES
        //////////////////////////////////////////////////////////////////////////////

        profiler.popPush("Tickables");
        this._attachedTickables.forEach(ITickableMultiblockPart::onMultiblockServerTick);

        //////////////////////////////////////////////////////////////////////////////
        // SEND CLIENT UPDATES
        //////////////////////////////////////////////////////////////////////////////

        profiler.popPush("Updates");
        this.checkAndSendClientUpdates();

        //////////////////////////////////////////////////////////////////////////////
        // UPDATE REFERENCE COORDINATES
        //////////////////////////////////////////////////////////////////////////////

        profiler.popPush("Mark4Update");

        if (!this._sendUpdateFuelRodsLayout && updateResult) {
            this._sendUpdateFuelRodsLayout = true;
        }

        if (this._sendUpdateFuelRodsLayout) {
            this._sendUpdateFuelRodsLayoutDelayedRunnable.run();
        }

        profiler.pop(); // Mark4Update
        profiler.pop(); // main section
        return updateResult;
    }

    /**
     * The "frame" consists of the outer edges of the machine, plus the corners.
     *
     * @param world             Level object for the world in which this controller is located.
     * @param x                 X coordinate of the block being tested
     * @param y                 Y coordinate of the block being tested
     * @param z                 Z coordinate of the block being tested
     * @param validatorCallback the validator, for error reporting
     */
    @Override
    protected boolean isBlockGoodForFrame(Level world, int x, int y, int z, IMultiblockValidator validatorCallback) {
        return invalidBlockForExterior(world, x, y, z, validatorCallback);
    }

    /**
     * The top consists of the top face, minus the edges.
     *
     * @param world             Level object for the world in which this controller is located.
     * @param x                 X coordinate of the block being tested
     * @param y                 Y coordinate of the block being tested
     * @param z                 Z coordinate of the block being tested
     * @param validatorCallback the validator, for error reporting
     */
    @Override
    protected boolean isBlockGoodForTop(Level world, int x, int y, int z, IMultiblockValidator validatorCallback) {
        return invalidBlockForExterior(world, x, y, z, validatorCallback);
    }

    /**
     * The bottom consists of the bottom face, minus the edges.
     *
     * @param world             Level object for the world in which this controller is located.
     * @param x                 X coordinate of the block being tested
     * @param y                 Y coordinate of the block being tested
     * @param z                 Z coordinate of the block being tested
     * @param validatorCallback the validator, for error reporting
     */
    @Override
    protected boolean isBlockGoodForBottom(Level world, int x, int y, int z, IMultiblockValidator validatorCallback) {
        return invalidBlockForExterior(world, x, y, z, validatorCallback);
    }

    /**
     * The sides consists of the N/E/S/W-facing faces, minus the edges.
     *
     * @param world             Level object for the world in which this controller is located.
     * @param x                 X coordinate of the block being tested
     * @param y                 Y coordinate of the block being tested
     * @param z                 Z coordinate of the block being tested
     * @param validatorCallback the validator, for error reporting
     */
    @Override
    protected boolean isBlockGoodForSides(Level world, int x, int y, int z, IMultiblockValidator validatorCallback) {
        return invalidBlockForExterior(world, x, y, z, validatorCallback);
    }

    /**
     * The interior is any block that does not touch blocks outside the machine.
     *
     * @param world             Level object for the world in which this controller is located.
     * @param x                 X coordinate of the block being tested
     * @param y                 Y coordinate of the block being tested
     * @param z                 Z coordinate of the block being tested
     * @param validatorCallback the validator, for error reporting
     */
    @Override
    protected boolean isBlockGoodForInterior(Level world, int x, int y, int z, IMultiblockValidator validatorCallback) {

        final BlockPos position = new BlockPos(x, y, z);
        final BlockState blockState = world.getBlockState(position);

        // Check against registered moderators
        if (ReactantHelper.isValidModerator(blockState)) {
            return true;
        }

        // Give up ...
        validatorCallback.setLastError(position, "multiblock.validation.reactor.invalid_block_for_interior",
                ModBlock.getNameForTranslation(blockState.getBlock()));
        return false;
    }

    //endregion
    //region IDebuggable

    @Override
    public void getDebugMessages(final LogicalSide side, final IDebugMessages messages) {

        if (!this.isAssembled()) {
            return;
        }

        messages.addUnlocalized("Active: %s", this.isMachineActive());

        this.getEnergyBuffer().getDebugMessages(side, messages);
        messages.addUnlocalized("Casing Heat: %1$.4f C; Fuel Heat: %2$.4f C", this._reactorHeat.getAsDouble(), this._fuelHeat.getAsDouble());
        messages.add(side, this._fuelContainer, "Reactant Tanks:");

        final boolean activeCooling = this.getOperationalMode().isActive();

        messages.addUnlocalized("Actively Cooled: %1$s", activeCooling);

        if (activeCooling) {

            messages.addUnlocalized("Coolant Tanks:");
            this._fluidContainer.getDebugMessages(side, messages);
        }
    }

    //endregion
    //region internals

    private Stream<IFuelSource<ItemStack>> getInputSolidAccessPorts() {
        return this._attachedSolidAccessPorts.stream()
                .filter(port -> null != port && port.isConnected() && port.getIoDirection().isInput())
                .map(port -> port);
    }

    private Stream<IFuelSource<ItemStack>> getOutputSolidAccessPorts() {
        return this._attachedSolidAccessPorts.stream()
                .filter(port -> null != port && port.isConnected() && port.getIoDirection().isOutput())
                .map(port -> port);
    }

    private Stream<IFuelSource<FluidStack>> getInputFluidAccessPorts() {
        return this._attachedFluidAccessPorts.stream()
                .filter(port -> null != port && port.isConnected() && port.getIoDirection().isInput())
                .map(port -> port);
    }

    private Stream<IFuelSource<FluidStack>> getOutputFluidAccessPorts() {
        return this._attachedFluidAccessPorts.stream()
                .filter(port -> null != port && port.isConnected() && port.getIoDirection().isOutput())
                .map(port -> port);
    }

    private void rebuildFluidPortsSubsets() {

        final List<ReactorFluidPortEntity> input = Lists.newArrayListWithCapacity(this._attachedFluidPorts.size());
        final List<ReactorFluidPortEntity> output = Lists.newArrayListWithCapacity(this._attachedFluidPorts.size());

        for (final ReactorFluidPortEntity port : this._attachedFluidPorts) {
            if (port.getFluidPortHandler().isActive()) {
                (port.getIoDirection().isInput() ? input : output).add(port);
            }
        }

        this._attachedInputFluidPorts = new ObjectArrayList<>(input);
        this._attachedOutputFluidPorts = new ObjectArrayList<>(output);
    }

    private FuelRodsLayout createFuelRodsLayout() {

        final Direction direction = this.getControlRodByIndex(0)
                .flatMap(AbstractCuboidMultiblockPart::getOutwardDirection)
                .orElse(Direction.UP);

        final int length;

        switch (direction.getAxis()) {

            default:
            case X:
                length = this.mapBoundingBoxCoordinates((min, max) -> Math.max(1, max.getX() - min.getX() - 1), 0);
                break;

            case Y:
                length = this.mapBoundingBoxCoordinates((min, max) -> Math.max(1, max.getY() - min.getY() - 1), 0);
                break;

            case Z:
                length = this.mapBoundingBoxCoordinates((min, max) -> Math.max(1, max.getZ() - min.getZ() - 1), 0);
                break;
        }

        return ExtremeReactors.getProxy().createFuelRodsLayout(direction, length);
    }

    private void updateClientFuelRodsLayout() {

        if (this.isAssembled() && this._fuelRodsLayout.isNotEmpty()) {

            final IntSet updatedIndices = this._fuelRodsLayout.updateFuelData(this._fuelContainer, this.getFuelRodsCount());

            if (!updatedIndices.isEmpty()) {

                // re-render all the fuel rod blocks when the fuel status changes

                if (updatedIndices.contains(-1)) {
                    this._attachedFuelRods.markFuelRodsForRenderUpdate();
                } else {
                    this._attachedFuelRods.markFuelRodsForRenderUpdate(updatedIndices);
                }
            }
        }
    }

    private void sendUpdateFuelRodsLayout() {

        if (!this.getReferenceTracker().isInvalid()) {

            final CuboidBoundingBox bb = this.getBoundingBox();
            final int radius = Math.max(bb.getLengthX(), bb.getLengthZ()) + 32;

            //noinspection ConstantConditions
            ExtremeReactors.getInstance().sendPacket(new UpdateClientsFuelRodsLayout((AbstractReactorEntity)this.getReferenceTracker().get(), this._fuelContainer),
                    this.getWorld(), bb.getCenter(), radius);

            this._sendUpdateFuelRodsLayout = false;
        }
    }

    private static boolean invalidBlockForExterior(Level world, int x, int y, int z, IMultiblockValidator validatorCallback) {

        final BlockPos position = new BlockPos(x, y, z);

        validatorCallback.setLastError(position, "multiblock.validation.reactor.invalid_block_for_exterior",
                ModBlock.getNameForTranslation(world.getBlockState(position).getBlock()));
        return false;
    }

    //region Reactor UPDATE helpers

    /**
     * Reactor UPDATE
     * Inject new solid fuel into the Reactor
     */
    private boolean refuelSolid() {

        // is there any space for fuel?
        if (this._fuelContainer.getFreeSpace(ReactantType.Fuel) < ReactantMappingsRegistry.STANDARD_SOLID_REACTANT_AMOUNT) {
            return false;
        }

        if (ReactantHelper.refuelSolid(this._fuelContainer, this.getInputSolidAccessPorts(), this.getVariant())) {

            this.markReferenceCoordForUpdate();
            this.markReferenceCoordDirty();
            return true;
        }

        return false;
    }

    /**
     * Reactor UPDATE
     * Inject new fluid fuel into the Reactor
     */
    private boolean refuelFluid() {

        // is there any space for fuel?
        if (this._fuelContainer.getFreeSpace(ReactantType.Fuel) < ReactantMappingsRegistry.STANDARD_FLUID_REACTANT_AMOUNT) {
            return false;
        }

        if (ReactantHelper.refuelFluid(this._fuelContainer, this.getInputFluidAccessPorts(), this.getVariant())) {

            this.markReferenceCoordForUpdate();
            this.markReferenceCoordDirty();
            return true;
        }

        return false;
    }

    /**
     * Reactor UPDATE
     * Distribute the available energy equally between all the Active Power Taps
     */
    private void distributeEnergyEqually() {

        final WideEnergyBuffer energyBuffer = this.getEnergyBuffer();
        final WideAmount amountDistributed = distributeEnergyEqually(energyBuffer.getEnergyStored(),
                this._attachedPowerTaps);

        if (amountDistributed.greaterThan(WideAmount.ZERO)) {
            energyBuffer.shrink(amountDistributed);
        }
    }

    /**
     * Reactor UPDATE
     * Distribute the available gas equally between all the Active Coolant Ports
     */
    private void distributeGasEqually() {

        final int amountDistributed = distributeFluidEqually(this._fluidContainer.getStackCopy(FluidType.Gas), this._attachedOutputFluidPorts);

        if (amountDistributed > 0) {
            this._fluidContainer.extract(FluidType.Gas, amountDistributed, OperationMode.Execute);
        }
    }

    /**
     * Reactor UPDATE
     * Acquire fluids, up to the available space, equally from all the Active Coolant Ports
     */
    private boolean acquireFluidEqually() {
        return acquireFluidEqually(this._fluidContainer.getWrapper(IoDirection.Input),
                this._fluidContainer.getFreeSpace(FluidType.Liquid), this._attachedInputFluidPorts) > 0;
    }

    //endregion
    //region internal data update

    private void resizeFuelContainer() {
        // Recalculate size of the fuel/waste container tank via fuel rods
        this._fuelContainer.setCapacity(this.getFuelRodsCount() * ReactorFuelRodEntity.FUEL_CAPACITY_PER_FUEL_ROD);
    }

    private void resizeFluidContainer() {

        if (this.getOperationalMode().isActive()) {

            final int outerVolume = this.getBoundingBox().getVolume() - this.getReactorVolume();

            this._fluidContainer.setCapacity(Mth.clamp(outerVolume * this.getVariant().getPartFluidCapacity(),
                    0, this.getVariant().getMaxFluidCapacity()));

        } else {

            this._fluidContainer.setCapacity(0);
        }
    }

    private void calculateReactorVolume() {

        final CuboidBoundingBox bb = this.getBoundingBox();
        final BlockPos min = bb.getMin().offset(1, 1, 1);
        final BlockPos max = bb.getMax().offset(-1, -1, -1);

        this._reactorVolume = CodeHelper.mathVolume(min, max);
    }

    private void updateFuelToReactorHeatTransferCoefficient() {
        // Calculate heat transfer based on fuel rod environment
        this._fuelToReactorHeatTransferCoefficient = this._attachedFuelRods.getHeatTransferRate();
    }

    private void updateReactorToCoolantSystemHeatTransferCoefficient() {
        // Calculate heat transfer to coolant system based on reactor interior surface area.
        // This is pretty simple to start with - surface area of the rectangular prism defining the interior.
        this._reactorToCoolantSystemHeatTransferCoefficient = IHeatEntity.CONDUCTIVITY_IRON * internalSurfaceArea(this.getBoundingBox());
    }

    private void updateReactorHeatLossCoefficient() {
        // Calculate passive heat loss to external surface area
        this._reactorHeatLossCoefficient = REACTOR_HEAT_LOSS_CONDUCTIVITY * externalSurfaceArea(this.getBoundingBox());
    }

    private static int internalSurfaceArea(final CuboidBoundingBox bb) {

        final int xSize = bb.getLengthX() - 2;
        final int ySize = bb.getLengthY() - 2;
        final int zSize = bb.getLengthZ() - 2;

        return  2 * (xSize * ySize + xSize * zSize + ySize * zSize);
    }

    private static int externalSurfaceArea(final CuboidBoundingBox bb) {

        final int xSize = bb.getLengthX();
        final int ySize = bb.getLengthY();
        final int zSize = bb.getLengthZ();

        return  2 * (xSize * ySize + xSize * zSize + ySize * zSize);
    }

    //endregion
    //region isMachineWhole-helpers

    /**
     * isMachineWhole-helper
     * Check that all Control Rods are on the same face of the Reactor
     *
     * @param firstDirection the direction to check all the Control Rods against
     * @return true if the Control Rods face in the same direction, false otherwise
     */
    private boolean validateControlRodsOrientation(Direction firstDirection) {
        return this._attachedControlRods.stream()
                .map(ReactorControlRodEntity::getOutwardFacingFromWorldPosition)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .allMatch(direction -> firstDirection == direction);
    }

    /**
     * isMachineWhole-helper
     * Validate a single Fuel Assembly
     *
     * @param validatorCallback the validator, for error reporting
     * @param controlRodPosition the coordinate of the Control Rod of the Fuel Assembly to check
     * @param scanDirection the direction to follow while walking the Fuel Assembly
     * @return the number of valid Fuel Rods founds or zero if an error was found
     */
    private int validateFuelAssembly(final IMultiblockValidator validatorCallback, final BlockPos controlRodPosition,
                                     final Direction scanDirection) {

        final BlockPos.MutableBlockPos scanPosition = controlRodPosition.mutable();
        int validRodsFound = 0;

        while (true) {

            scanPosition.move(scanDirection);

            final IMultiblockPart<MultiblockReactor> part = this._connectedParts.get(scanPosition);

            if (part instanceof ReactorFuelRodEntity) {

                // found a valid Fuel Rod
                ++validRodsFound;

            } else if (part instanceof AbstractReactorEntity) {

                // we hit some other Reactor parts on the walls

                if (ReactorPartType.Casing != ((AbstractReactorEntity)part).getPartTypeOrDefault(ReactorPartType.Glass)) {

                    // a Reactor Casing is the only valid base for a fuel assembly

                    validatorCallback.setLastError(scanPosition, "multiblock.validation.reactor.invalid_base_for_fuel_assembly");
                    return 0;
                }

                // found a Reactor Casing: stop the scan
                return validRodsFound;

            } else {

                // found an invalid tile entity (or no tile entity at all)
                validatorCallback.setLastError(scanPosition, "multiblock.validation.reactor.invalid_block_in_fuel_assembly");
                return 0;
            }
        }
    }

    /**
     * isMachineWhole-helper
     * Check if all Fuel Assemblies are correctly constructed
     *
     * @param validatorCallback the validator, for error reporting
     * @return true if all Fuel Assemblies are correctly constructed, false otherwise
     */
    private boolean validateFuelAssemblies(IMultiblockValidator validatorCallback) {

        // ensure that Control Rods are placed only on one face of the Reactor ...

        final Optional<Direction> firstDirection = this._attachedControlRods.get(0).getOutwardFacingFromWorldPosition();

        if (!firstDirection.isPresent()) {

            validatorCallback.setLastError("multiblock.validation.reactor.invalid_control_side");
            return false;

        }

        if (!firstDirection.map(this::validateControlRodsOrientation).orElse(false)) {

            validatorCallback.setLastError("multiblock.validation.reactor.invalid_control_side");
            return false;
        }

        // ... and that Fuel Rods follow the orientation of Control Rods

        final Direction scanDirection = firstDirection.get().getOpposite();
        int validRodsFound = 0;

        for (final ReactorControlRodEntity controlRod : this._attachedControlRods) {

            final int found = this.validateFuelAssembly(validatorCallback, controlRod.getWorldPosition(), scanDirection);

            if (0 == found) {
                return false;
            }

            validRodsFound += found;
        }

        if (this.getFuelRodsCount() != validRodsFound) {

            validatorCallback.setLastError("multiblock.validation.reactor.invalid_fuel_rods");
            return false;
        }

        // all OK ...

        return true;
    }

    /**
     * isMachineWhole-helper
     * Check if there is only one type of EnergySystems in the Reactor
     *
     * @param validatorCallback the validator, for error reporting
     * @return true if validation is passed, false otherwise
     */
    private boolean validateEnergySystems(IMultiblockValidator validatorCallback) {

        if (!this._attachedPowerTaps.isEmpty()) {

            if (1 != this._attachedPowerTaps.stream()
                    .map(IPowerPort::getPowerPortHandler)
                    .map(IPowerPortHandler::getEnergySystem)
                    .distinct()
                    .limit(2)
                    .count()) {

                // there must be only one output energy system for each Reactor
                validatorCallback.setLastError("multiblock.validation.reactor.mixed_power_systems");
                return false;
            }
        }

        return true;
    }

    //endregion

    private void ejectReactant(final ReactantType type, final boolean voidLeftover,
                               final Stream<IFuelSource<FluidStack>> fluidFuelSources,
                               final Stream<IFuelSource<ItemStack>> solidFuelSources) {

        if (ReactantHelper.ejectReactant(type, true, this._fuelContainer, voidLeftover, fluidFuelSources) ||
                ReactantHelper.ejectReactant(type, false, this._fuelContainer, voidLeftover, solidFuelSources)) {

            this.markReferenceCoordForUpdate();
            this.markReferenceCoordDirty();
        }
    }

    private static final IFluidContainerAccess FLUID_CONTAINER_ACCESS = new IFluidContainerAccess() {

        @Override
        public AllowedHandlerAction getAllowedActionFor(final FluidType fluidType) {

            switch (fluidType) {

                default:
                case Gas:
                    return AllowedHandlerAction.ExtractOnly;

                case Liquid:
                    return AllowedHandlerAction.InsertOnly;
            }
        }

        @Override
        public FluidType getFluidTypeFrom(final IoDirection portDirection) {

            switch (portDirection) {

                default:
                case Input:
                    return FluidType.Liquid;

                case Output:
                    return FluidType.Gas;
            }
        }
    };

    // circa 1FE per tick per external surface block
    private static final float REACTOR_HEAT_LOSS_CONDUCTIVITY = 0.001f;

    private static final IRadiationModerator MODERATOR_NONE = (data, packet) -> {};
    private static final IRadiationModerator MODERATOR_AIR = (data, packet) -> applyModerator(data, packet, Moderator.AIR);

    private final ReactorLogic _logic;
    private final IMultiblockReactorVariant _variant;
    private final Heat _fuelHeat;
    private final Heat _reactorHeat;
    private final FuelContainer _fuelContainer;
    private final FluidContainer _fluidContainer;
    private final Stats _uiStats;

    private FuelRodsLayout _fuelRodsLayout;
    private WasteEjectionSetting _wasteEjectionSetting;
    private OperationalMode _mode;
    private int _reactorVolume;
    private float _fuelToReactorHeatTransferCoefficient;
    private float _reactorToCoolantSystemHeatTransferCoefficient;
    private float _reactorHeatLossCoefficient;
    private boolean _sendUpdateFuelRodsLayout;
    private final Runnable _sendUpdateFuelRodsLayoutDelayedRunnable;

    private List<ITickableMultiblockPart> _attachedTickables;
    private final List<ReactorControlRodEntity> _attachedControlRods;
    private final FuelRodsMap _attachedFuelRods;
    private final List<ReactorSolidAccessPortEntity> _attachedSolidAccessPorts;
    private final List<ReactorFluidAccessPortEntity> _attachedFluidAccessPorts;
    private List<IPowerPort> _attachedPowerTaps;
    private List<ReactorFluidPortEntity> _attachedFluidPorts;
    private List<ReactorFluidPortEntity> _attachedOutputFluidPorts;
    private List<ReactorFluidPortEntity> _attachedInputFluidPorts;

    //endregion
}
