/*
 *
 * Stats.java
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

import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import net.minecraft.nbt.CompoundTag;

public class Stats implements ISyncableEntity {

    public Stats(final IFuelContainer fc) {

        this._fuelContainer = fc;
        this._amountGeneratedLastTick = 0d;
        this._fuelConsumedLastTick = 0;
    }

    public float getFuelConsumedLastTick() {
        return this._fuelConsumedLastTick;
    }

    /**
     * The amount of stuff generated in the last tick. This could be energy or gas depening on the Reactor operational mode
     *
     * @return the amount of stuff generated
     */
    public double getAmountGeneratedLastTick() {
        return this._amountGeneratedLastTick;
    }

    /**
     * @return Percentile fuel richness (fuel/fuel+waste), or 0 if all control rods are empty
     */
    public float getFuelRichness() {

        final int fuelAmount = this._fuelContainer.getFuelAmount();
        final int wasteAmount = this._fuelContainer.getWasteAmount();

        if (fuelAmount + wasteAmount <= 0f) {
            return 0f;
        } else {
            return (float)fuelAmount / (float)(fuelAmount + wasteAmount);
        }
    }

    void setFuelConsumedLastTick(float value) {
        this._fuelConsumedLastTick = value;
    }

    void changeFuelConsumedLastTick(float delta) {
        this._fuelConsumedLastTick += delta;
    }

    void setAmountGeneratedLastTick(double value) {
        this._amountGeneratedLastTick = value;
    }

    void changeAmountGeneratedLastTick(double delta) {
        this._amountGeneratedLastTick += delta;
    }

    //region ISyncableEntity

    /**
     * Sync the entity data from the given {@link CompoundTag}
     *
     * @param data       the {@link CompoundTag} to read from
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public void syncDataFrom(CompoundTag data, SyncReason syncReason) {

        if (data.contains("stuff")) {
            this.setAmountGeneratedLastTick(data.getDouble("stuff"));
        }

        if (data.contains("fuel")) {
            this.setFuelConsumedLastTick(data.getFloat("fuel"));
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

        data.putDouble("stuff", this.getAmountGeneratedLastTick());
        data.putFloat("fuel", this.getFuelConsumedLastTick());
        return data;
    }

    //endregion
    //region Object

    @Override
    public String toString() {
        return "Generated: " + this._amountGeneratedLastTick + ", Consumed: " + this._fuelConsumedLastTick;
    }

    //endregion
    //region internals

    private final IFuelContainer _fuelContainer;
    private double _amountGeneratedLastTick;
    private float _fuelConsumedLastTick;

    //endregion
}