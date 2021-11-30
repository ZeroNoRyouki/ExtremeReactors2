/*
 *
 * FuelContainer.java
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

import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.extremereactors.api.reactor.*;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.data.nbt.IMergeableEntity;
import it.zerono.mods.zerocore.lib.data.stack.IndexedStackContainer;
import it.zerono.mods.zerocore.lib.data.stack.OperationMode;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fml.LogicalSide;

import java.util.Optional;

public class FuelContainer
        extends IndexedStackContainer<ReactantType, Reactant, ReactantStack>
        implements IFuelContainer {

    public FuelContainer() {

        super(0, true, 60, ReactantStack.ADAPTER, ReactantType.Fuel, ReactantType.Waste);
        this._radiationFuelUsage = 0f;
    }

    /**
     * Reset the internal data
     * --- FOR TESTING PURPOSES ONLY ---
     */
    void reset() {

        this._radiationFuelUsage = 0f;
        this.voidFuel();
        this.voidWaste();
    }

    public Optional<Reactant> getFuel() {
        return this.getContent(ReactantType.Fuel);
    }

    public FuelProperties getFuelProperties() {
        return this.map(ReactantType.Fuel, Reactant::getFuelData, FuelProperties.INVALID);
    }

    public Optional<Reactant> getWaste() {
        return this.getContent(ReactantType.Waste);
    }

    public int getFuelAmount() {
        return this.getContentAmount(ReactantType.Fuel);
    }

    public int getWasteAmount() {
        return this.getContentAmount(ReactantType.Waste);
    }

    /**
     * Add some fuel to the current pile, if possible
     *
     * @param reactant the Reactant to add
     * @param amount   the quantity of Reactant to add
     * @param mode if Simulate, this will only simulate a fill and will not alter the fuel amount
     * @return the amount of Reactant actually added
     */
    public int insertFuel(Reactant reactant, int amount, OperationMode mode) {
        return amount <= 0 ? 0 : this.insert(ReactantType.Fuel, reactant, amount, mode);
    }

    /**
     * Add some fuel to the current pile, if possible
     *
     * @param stack the Reactant to add
     * @param mode if Simulate, this will only simulate a fill and will not alter the fuel amount
     * @return the amount of Reactant actually added
     */
    public int insertFuel(ReactantStack stack, OperationMode mode) {
        return stack.isEmpty() ? 0 : this.insert(ReactantType.Fuel, stack, mode);
    }

    /**
     * Add some waste to the current pile, if possible
     *
     * @param reactant the Reactant to add
     * @param amount   the quantity of Reactant to add
     * @param mode if Simulate, this will only simulate a fill and will not alter the fuel amount
     * @return the amount of Reactant actually added
     */
    public int insertWaste(Reactant reactant, int amount, OperationMode mode) {
        return amount <= 0 ? 0 : this.insert(ReactantType.Waste, reactant, amount, mode);
    }
/*
    private int addWaste(int amount) {

        if (this.getWasteType() == null) {

            BigReactors.getLogger().warn("System is using addWaste(int) when there's no waste present, defaulting to cyanite");
            return this.fill(ReactantType.Waste, GameBalanceData.REACTANT_NAME_CYANITE, amount, true);

        } else {

            return addToStack(ReactantType.Waste, amount);
        }
    }*/

    public int voidFuel() {
        return this.clear(ReactantType.Fuel).getAmount();
    }

    public int voidFuel(int amount) {
        return this.voidReactant(ReactantType.Fuel, amount);
    }

    public int voidWaste() {
        return this.clear(ReactantType.Waste).getAmount();
    }

    public int voidWaste(int amount) {
        return this.voidReactant(ReactantType.Waste, amount);
    }

    public int voidReactant(ReactantType index, int amount) {
        return this.getContent(index).map(r -> this.extract(index, r, amount, OperationMode.Execute).getAmount()).orElse(0);
    }

    public void onIrradiation(float fuelUsed) {

        if (Float.isInfinite(fuelUsed) || Float.isNaN(fuelUsed)) {
            return;
        }

        this._radiationFuelUsage += fuelUsed;

        if (this._radiationFuelUsage < 1f) {
            return;
        }

        final int fuelToConvert = Math.min(this.getFuelAmount(), (int) this._radiationFuelUsage);

        if (fuelToConvert <= 0) {
            return;
        }

        this._radiationFuelUsage = Math.max(0f, this._radiationFuelUsage - fuelToConvert);

        final Optional<Reactant> fuelReactant = this.getFuel();

        if (fuelReactant.isPresent()) {

            this.voidFuel(fuelToConvert);

            Optional<Reactant> wasteReactant = this.getWaste();

            if (wasteReactant.isPresent()) {

                // If there's already waste, just keep on producing the same type.
                this.insertWaste(wasteReactant.get(), fuelToConvert, OperationMode.Execute);

            } else {

                // Create waste type from registry

                wasteReactant = ReactionsRegistry.get(fuelReactant.get()).map(Reaction::getProduct);

                if (wasteReactant.isPresent()) {
                    this.insertWaste(wasteReactant.get(), fuelToConvert, OperationMode.Execute);
                } else {
                    Log.LOGGER.warn(Log.REACTOR, "Could not locate waste for reaction of fuel type {}: no waste will be produced", fuelReactant);
                }
            }
        } else {

            Log.LOGGER.warn(Log.REACTOR, "Attempting to use {} fuel and there's no fuel in the tank", fuelToConvert);
        }
    }

    public float getFuelReactivity() {

        final Optional<Reactant> reactant = this.getFuel();
        final Optional<Float> reactivity = reactant.flatMap(ReactionsRegistry::get).map(Reaction::getReactivity);

        if (!reactivity.isPresent()) {
            Log.LOGGER.warn(Log.REACTOR, "Could not locate reaction data for reactant type {}; using default value for reactivity",
                    reactant.isPresent() ? reactant.get() : "<UNKNOWN>");
        }

        return reactivity.orElse(Reaction.STANDARD_REACTIVITY);
    }

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

        if (data.contains("radiationFuelUsage")) {
            this._radiationFuelUsage = data.getFloat("radiationFuelUsage");
        }
    }

    /**
     * Sync the entity data to the given {@link CompoundTag}
     *
     * @param data       the {@link CompoundTag} to write to
     * @param syncReason the reason why the synchronization is necessary
     * @return the {@link CompoundTag} the data was written to (usually {@code data})
     */
    @Override
    public CompoundTag syncDataTo(CompoundTag data, SyncReason syncReason) {

        super.syncDataTo(data, syncReason);
        data.putFloat("radiationFuelUsage", this._radiationFuelUsage);
        return data;
    }

    //endregion
    //region IMergeableEntity

    /**
     * Sync the entity data from another IMergeableEntity
     *
     * @param other the IMergeableEntity to sync from
     */
    @Override
    public void syncDataFrom(IMergeableEntity other) {

        if (other instanceof FuelContainer) {
            this._radiationFuelUsage = Math.max(this._radiationFuelUsage, ((FuelContainer) other)._radiationFuelUsage);
        }

        super.syncDataFrom(other);
    }

    //endregion
    //region IDebuggable

    @Override
    public void getDebugMessages(final LogicalSide side, final IDebugMessages messages) {

        super.getDebugMessages(side, messages);
        messages.addUnlocalized("Radiation fuel usage: %1$.4f", this._radiationFuelUsage);
    }

    //endregion
    //region Object

    @Override
    public String toString() {
        return "Radiation Fuel Usage: " + this._radiationFuelUsage + "\n" + super.toString();
    }

    //endregion
    //region internals

    private float _radiationFuelUsage;

    //endregion
}
