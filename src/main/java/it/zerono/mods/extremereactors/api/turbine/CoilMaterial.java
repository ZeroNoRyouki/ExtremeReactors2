/*
 *
 * CoilMaterial.java
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

package it.zerono.mods.extremereactors.api.turbine;

@SuppressWarnings({"WeakerAccess"})
public class CoilMaterial {

    public static final float STANDARD_EXTRACTIONRATE = 1.0f;

    CoilMaterial(final float efficiency, final float bonus, final float extractionRate) {

        this.setEfficiency(efficiency);
        this.setBonus(bonus);
        this.setEnergyExtractionRate(extractionRate);
    }

    public float getEfficiency() {
        return this._efficiency;
    }

    public void setEfficiency(float value) {
        this._efficiency = value;
    }

    public float getBonus() {
        return this._bonus;
    }

    public void setBonus(float value) {
        this._bonus = value;
    }

    public float getEnergyExtractionRate() {
        return this._energyExtractionRate;
    }

    public void setEnergyExtractionRate(float value) {
        this._energyExtractionRate = value;
    }

    //region internals

    private float _efficiency;
    private float _bonus;
    private float _energyExtractionRate;

    //endregion
}
