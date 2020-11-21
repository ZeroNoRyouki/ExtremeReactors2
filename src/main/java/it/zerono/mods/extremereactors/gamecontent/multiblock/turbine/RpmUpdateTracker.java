/*
 *
 * RpmUpdateTracker.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine;

final class RpmUpdateTracker {

    public RpmUpdateTracker(final int minimumTicksBetweenUpdates, final int minimumTicksBetweenUrgentUpdates,
                            final float minimumSpreadForUpdate, final float maximumSpreadForUpdate) {

        this._ticksSinceLastUpdate = 0;
        this._value = 0f;
        this._ticksBetweenUpdates = minimumTicksBetweenUpdates;
        this._ticksBetweenUrgentUpdates = Math.min(this._ticksBetweenUpdates, minimumTicksBetweenUrgentUpdates);
        this._minimumDifference = minimumSpreadForUpdate;
        this._maximumDifference = Math.max(this._minimumDifference, maximumSpreadForUpdate);
    }

    public void setValue(final float value) {

        this._value = value;
        this._ticksSinceLastUpdate = 0;
    }

    public void reset() {
        this._ticksSinceLastUpdate = 0;
    }

    public boolean shouldUpdate(float currentValue) {

        ++this._ticksSinceLastUpdate;

        if ((0.0f == this._value && currentValue > 0.0f) || (0.0f == currentValue && this._value > 0)) {

            this._ticksSinceLastUpdate = 0;
            this._value = currentValue;
            return true;
        }

        if (this._ticksSinceLastUpdate < this._ticksBetweenUrgentUpdates) {
            return false;
        }

        final float spread = Math.abs(currentValue - this._value);

        if (spread >= this._maximumDifference) {

            this._ticksSinceLastUpdate = 0;
            this._value = currentValue;
            return true;
        }

        if (this._ticksSinceLastUpdate < this._ticksBetweenUpdates) {
            return false;
        }

        if (spread >= this._minimumDifference) {

            this._ticksSinceLastUpdate = 0;
            this._value = currentValue;
            return true;

        } else {

            this._ticksSinceLastUpdate = this._ticksBetweenUpdates;
            return false;
        }
    }

    //region internals

    private int _ticksSinceLastUpdate;
    private int _ticksBetweenUpdates;
    private int _ticksBetweenUrgentUpdates;
    private float _value;
    private float _minimumDifference;
    private float _maximumDifference;

    //endregion
}
