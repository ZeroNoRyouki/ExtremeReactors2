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

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.IIOPortHandler;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.fluidport.IFluidPort;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.powertap.IPowerTap;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockGeneratorVariant;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.energy.EnergyBuffer;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.energy.IWideEnergyProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity.SyncReason;

@SuppressWarnings({"WeakerAccess"})
public abstract class AbstractGeneratorMultiblockController<Controller extends AbstractGeneratorMultiblockController<Controller, V>,
            V extends IMultiblockGeneratorVariant>
        extends AbstractMultiblockController<Controller, V>
        implements IWideEnergyProvider {

    public AbstractGeneratorMultiblockController(Level world) {

        super(world);
        this._energyBuffer = new EnergyBuffer(INTERNAL_ENERGY_SYSTEM, 0); //TODO is starting from 0 a problem when loading NBT data?
        this._outputEnergySystem = INTERNAL_ENERGY_SYSTEM;

        this.setInteriorInvisible(false);
    }

    //region Energy production and storage

    protected EnergyBuffer getEnergyBuffer() {
        return this._energyBuffer;
    }

    public EnergySystem getOutputEnergySystem() {
        return this._outputEnergySystem;
    }

    protected void setOutputEnergySystem(final EnergySystem system) {
        this._outputEnergySystem = system;
    }

    public double getEnergyStoredPercentage() {
        return this.getEnergyStored(INTERNAL_ENERGY_SYSTEM, null) / this.getCapacity(INTERNAL_ENERGY_SYSTEM, null);
    }

    /**
     * Distribute the given amount of energy equally between the specified Active Power Taps
     *
     * @param energyAmount the amount of energy to distribute
     * @param powerTaps the Power Taps
     * @return the amount of energy distributed
     */
    protected static double distributeEnergyEqually(double energyAmount, final Collection<? extends IPowerTap> powerTaps) {

        if (energyAmount <= 0 || powerTaps.isEmpty()) {
            return 0;
        }

        final double energyPerTap = energyAmount / powerTaps.size();

        return powerTaps.stream()
                .map(IPowerTap::getPowerTapHandler)
                .filter(IIOPortHandler::isActive)
                .filter(IIOPortHandler::isConnected)
                .mapToDouble(handler -> handler.outputEnergy(energyPerTap))
                .sum();
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
    protected static <Controller extends AbstractGeneratorMultiblockController<Controller, V>, V extends IMultiblockGeneratorVariant>
            int distributeFluidEqually(final FluidStack availableFluid, final Collection<? extends IFluidPort<Controller, V>> coolantPorts) {

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
     *
     * @param availableFluid the gas to distribute
     * @param coolantPorts the Coolant Ports
     * @return the amount of gas distributed
     */
    protected static <Controller extends AbstractGeneratorMultiblockController<Controller, V>, V extends IMultiblockGeneratorVariant>
    int acquireFluidEqually(final IFluidHandler destination, final int maxAmount, final Collection<? extends IFluidPort<Controller, V>> coolantPorts) {

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
    //region IWideEnergyProvider

    /**
     * Remove energy, expressed in the specified {@EnergySystem}, from an IWideEnergyProvider.
     * Internal distribution is left entirely to the IWideEnergyProvider
     *
     * @param system    the {@link EnergySystem} used by the request
     * @param from      the direction the request is coming from
     * @param maxAmount maximum amount of energy to extract
     * @param simulate  if true, the extraction will only be simulated
     * @return amount of energy that was (or would have been, if simulated) extracted
     */
    @Override
    public double extractEnergy(EnergySystem system, @Nullable Direction from, double maxAmount, boolean simulate) {
        return this.getEnergyBuffer().extractEnergy(system, maxAmount, simulate);
    }

    /**
     * Returns the amount of energy currently stored expressed in the specified {@link EnergySystem}
     *
     * @param system the {@link EnergySystem} used by the request
     * @param from   the direction the request is coming from
     */
    @Override
    public double getEnergyStored(EnergySystem system, @Nullable Direction from) {
        return this.getEnergyBuffer().getEnergyStored(system);
    }

    /**
     * Returns the maximum amount of energy that can be stored expressed in the requested {@link EnergySystem}
     *
     * @param system the {@link EnergySystem} used by the request
     * @param from   the direction the request is coming from
     */
    @Override
    public double getCapacity(EnergySystem system, @Nullable Direction from) {
        return this.getEnergyBuffer().getCapacity(system);
    }

    /**
     * Get the {@EnergySystem} used by this entity
     *
     * @return the {@EnergySystem} in use
     */
    @Override
    public EnergySystem getEnergySystem() {
        return this.getEnergyBuffer().getEnergySystem();
    }

    /**
     * Returns true if the entity can connect on a given side and support with the provided {@Link EnergySystem}.
     *
     * @param system the {@link EnergySystem} used by the request
     * @param from   the direction the request is coming from
     */
    @Override
    public boolean canConnectEnergy(EnergySystem system, @Nullable Direction from) {
        return true;
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
    public void syncDataFrom(final CompoundTag data, final SyncReason syncReason) {

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
    public CompoundTag syncDataTo(final CompoundTag data, final SyncReason syncReason) {

        this.syncChildDataEntityTo(this.getEnergyBuffer(), "buffer", data, syncReason);

        if (syncReason.isNetworkUpdate()) {
            EnergySystem.write(data, "energySystem", this.getOutputEnergySystem());
        }

        return data;
    }

    //endregion
    //region internals

    protected static final EnergySystem INTERNAL_ENERGY_SYSTEM = EnergySystem.ForgeEnergy;

    private final EnergyBuffer _energyBuffer;
    private EnergySystem _outputEnergySystem;

    //endregion
}
