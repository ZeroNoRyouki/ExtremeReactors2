/*
 *
 * Heat.java
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

import it.zerono.mods.zerocore.lib.data.nbt.IMergeableEntity;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import net.minecraft.nbt.CompoundNBT;

class Heat implements IHeat, ISyncableEntity, IMergeableEntity {

    public Heat() {
        this._heat = 0;
    }

    //region IHeat

    public void set(double value) {
        this._heat = Double.isNaN(value) ? 0 : value;
    }

    public void add(double amount) {

        if (!Double.isNaN(amount)) {

            this._heat += amount;

            // Clamp to zero to prevent floating point issues
            if (-0.00001 < this._heat && this._heat < 0.00001) {
                this._heat = 0;
            }
        }
    }

    public void resetIfNegative() {

        if (this.getAsDouble() < 0) {
            this.set(0);
        }
    }

    //endregion
    //region DoubleSupplier

    @Override
    public double getAsDouble() {
        return this._heat;
    }

    //endregion
    //region ISyncableEntity

    /**
     * Sync the entity data from the given {@link CompoundNBT}
     *
     * @param data       the {@link CompoundNBT} to read from
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public void syncDataFrom(CompoundNBT data, SyncReason syncReason) {

        if (data.contains("heat")) {
            this.set(data.getDouble("heat"));
        }
    }

    /**
     * Sync the entity data to the given {@link CompoundNBT}
     *
     * @param data       the {@link CompoundNBT} to write to
     * @param syncReason the reason why the synchronization is necessary
     * @return the {@link CompoundNBT} the data was written to (usually {@code data})
     */
    @Override
    public CompoundNBT syncDataTo(CompoundNBT data, SyncReason syncReason) {

        data.putDouble("heat", this.getAsDouble());
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

        if (other instanceof Heat) {

            final Heat heat = (Heat)other;

            if (heat.getAsDouble() > this.getAsDouble()) {
                this.set(heat.getAsDouble());
            }
        }
    }

    //endregion
    //region Object

    @Override
    public String toString() {
        return Double.toString(this._heat);
    }

    //endregion
    //region internals

    private double _heat;

    //endregion
}
