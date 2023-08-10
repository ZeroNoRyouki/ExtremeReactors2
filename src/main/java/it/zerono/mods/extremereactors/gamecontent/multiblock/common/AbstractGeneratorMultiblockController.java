/*
 *
 * AbstractGeneratorMultiblockController.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common;

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockGeneratorVariant;
import it.zerono.mods.zerocore.base.multiblock.AbstractMultiblockController;
import it.zerono.mods.zerocore.base.multiblock.part.io.IIOPortHandler;
import it.zerono.mods.zerocore.base.multiblock.part.io.fluid.IFluidPort;
import it.zerono.mods.zerocore.base.multiblock.part.io.power.IPowerPort;
import it.zerono.mods.zerocore.base.multiblock.part.io.power.IPowerPortHandler;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.data.stack.OperationMode;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.energy.IWideEnergyStorage2;
import it.zerono.mods.zerocore.lib.energy.WideEnergyBuffer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Collection;
import java.util.Optional;

@SuppressWarnings({"WeakerAccess"})
public abstract class AbstractGeneratorMultiblockController<Controller extends AbstractGeneratorMultiblockController<Controller, V>,
            V extends IMultiblockGeneratorVariant>
        extends AbstractMultiblockController<Controller, V>
        implements IWideEnergyStorage2 {

    public AbstractGeneratorMultiblockController(World world) {

        super(world);
        this._energyBuffer = new WideEnergyBuffer(INTERNAL_ENERGY_SYSTEM, WideAmount.ZERO);
        this._outputEnergySystem = INTERNAL_ENERGY_SYSTEM;

        this.setInteriorInvisible(false);
    }

    //region Energy production and storage

    protected WideEnergyBuffer getEnergyBuffer() {
        return this._energyBuffer;
    }

    public EnergySystem getOutputEnergySystem() {
        return this._outputEnergySystem;
    }

    protected void setOutputEnergySystem(final EnergySystem system) {
        this._outputEnergySystem = system;
    }

    public double getEnergyStoredPercentage() {
        return this.getEnergyStored(INTERNAL_ENERGY_SYSTEM).percentage(this.getCapacity(INTERNAL_ENERGY_SYSTEM));
    }

    /**
     * Distribute the given amount of energy equally between the specified Active Power Taps
     *
     * @param energyAmount the amount of energy to distribute
     * @param powerTaps the Power Taps
     * @return the amount of energy distributed
     */
    protected static WideAmount distributeEnergyEqually(WideAmount energyAmount,
                                                        Collection<? extends IPowerPort> powerTaps) {

        if (energyAmount.isZero() || powerTaps.isEmpty()) {
            return WideAmount.ZERO;
        }

        energyAmount = energyAmount.divide(powerTaps.size()).toImmutable();

        WideAmount amountDistributed = WideAmount.ZERO;

        for (final IPowerPort port : powerTaps) {

            final IPowerPortHandler handler = port.getPowerPortHandler();

            if (handler.isActive() && handler.isConnected()) {
                amountDistributed = amountDistributed.add(handler.outputEnergy(energyAmount));
            }
        }

        return amountDistributed;
    }

    //endregion
    //region active-coolant system

    public abstract Optional<IFluidHandler> getLiquidHandler();

    public abstract Optional<IFluidHandler> getGasHandler();

    public abstract Optional<IFluidHandler> getFluidHandler(IoDirection portDirection);

    /**
     * Distribute the given fluid equally between the specified Active Coolant Ports
     *
     * @param availableFluid the gas to distribute
     * @param coolantPorts the Coolant Ports
     * @return the amount of gas distributed
     */
    protected static int distributeFluidEqually(final FluidStack availableFluid,
                                                final Collection<? extends IFluidPort> coolantPorts) {

        if (availableFluid.isEmpty() || coolantPorts.isEmpty()) {
            return 0;
        }

        final int fluidPerPort = availableFluid.getAmount() / coolantPorts.size();

        return coolantPorts.stream()
                .filter(p -> p.getIoDirection().isOutput())
                .map(IFluidPort::getFluidPortHandler)
                .filter(IIOPortHandler::isActive)
                .filter(IIOPortHandler::isConnected)
                .mapToInt(handler -> handler.outputFluid(new FluidStack(availableFluid, fluidPerPort)))
                .sum();
    }

    /**
     * Distribute the given fluid equally between the specified Active Coolant Ports
     */
    protected static int acquireFluidEqually(final IFluidHandler destination, final int maxAmount,
                                             final Collection<? extends IFluidPort> coolantPorts) {

        if (maxAmount <= 0 || coolantPorts.isEmpty()) {
            return 0;
        }

        final int fluidPerPort = maxAmount / coolantPorts.size();

        return coolantPorts.stream()
                .filter(p -> p.getIoDirection().isInput())
                .map(IFluidPort::getFluidPortHandler)
                .filter(IIOPortHandler::isActive)
                .filter(IIOPortHandler::isConnected)
                .mapToInt(handler -> handler.inputFluid(destination, fluidPerPort))
                .sum();
    }

    //endregion
    //region IWideEnergyStorage2

    @Override
    public WideAmount insertEnergy(EnergySystem system, WideAmount maxAmount, OperationMode mode) {
        return this.getEnergyBuffer().insertEnergy(system, maxAmount, mode);
    }

    @Override
    public WideAmount extractEnergy(EnergySystem system, WideAmount maxAmount, OperationMode mode) {
        return this.getEnergyBuffer().extractEnergy(system, maxAmount, mode);
    }

    @Override
    public WideAmount getEnergyStored(EnergySystem system) {
        return this.getEnergyBuffer().getEnergyStored(system);
    }

    @Override
    public WideAmount getCapacity(EnergySystem system) {
        return this.getEnergyBuffer().getCapacity(system);
    }

    @Override
    public EnergySystem getEnergySystem() {
        return this.getEnergyBuffer().getEnergySystem();
    }

    //endregion
    //region ISyncableEntity

    /**
     * Sync the entity data from the given NBT compound
     *
     * @param data the data
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public void syncDataFrom(final CompoundNBT data, final SyncReason syncReason) {

        this.syncChildDataEntityFrom(this.getEnergyBuffer(), "buffer", data, syncReason);

        if (syncReason.isNetworkUpdate()) {
            this.setOutputEnergySystem(EnergySystem.read(data, "energySystem", EnergySystem.REFERENCE));
        }

        super.syncDataFrom(data, syncReason);
    }

    /**
     * Sync the entity data to the given NBT compound
     *
     * @param data the data
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public CompoundNBT syncDataTo(final CompoundNBT data, final SyncReason syncReason) {

        this.syncChildDataEntityTo(this.getEnergyBuffer(), "buffer", data, syncReason);

        if (syncReason.isNetworkUpdate()) {
            EnergySystem.write(data, "energySystem", this.getOutputEnergySystem());
        }

        return data;
    }

    //endregion
    //region internals

    protected static final EnergySystem INTERNAL_ENERGY_SYSTEM = EnergySystem.ForgeEnergy;

    private final WideEnergyBuffer _energyBuffer;
    private EnergySystem _outputEnergySystem;

    //endregion
}
