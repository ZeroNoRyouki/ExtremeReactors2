package it.zerono.mods.extremereactors.api.reactor;
/*
 * Fuel
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

import net.minecraft.util.math.MathHelper;

/**
 * Describe the properties of a Fuel {@link Reactant}
 */
@SuppressWarnings({"WeakerAccess"})
public class FuelProperties {

    public static final FuelProperties DEFAULT = new FuelProperties(1.5f, 0.5f, 1.0f);
    public static final FuelProperties INVALID = new FuelProperties(1.0f, 0.0f, 1.0f);

    /**
     * Construct a new FuelProperties
     *
     * @param moderationFactor How well this fuel moderate, but not stop, radiation. Must be greater or equal to 1.
     *                         Anything under 1.5 is "poor", 2-2.5 is "good", above 4 is "excellent".
     * @param absorptionCoefficient How well this fuel absorbs radiation. Must be between 0 and 1.
     * @param hardnessDivisor How this fuel tolerates hard radiation. Must be greater or equal to 1.
     */
    FuelProperties(float moderationFactor, float absorptionCoefficient, float hardnessDivisor) {

        this.setModerationFactor(moderationFactor);
        this.setAbsorptionCoefficient(absorptionCoefficient);
        this.setHardnessDivisor(hardnessDivisor);
    }

    public float getModerationFactor() {
        return this._fuelModerationFactor;
    }

    public void setModerationFactor(float value) {
        this._fuelModerationFactor = Math.max(1.0f, value);
    }

    public float getAbsorptionCoefficient() {
        return this._fuelAbsorptionCoefficient;
    }

    public void setAbsorptionCoefficient(float value) {
        this._fuelAbsorptionCoefficient = MathHelper.clamp(value, 0.0f, 1.0f);
    }

    public float getHardnessDivisor() {
        return this._fuelHardnessDivisor;
    }

    public void setHardnessDivisor(float value) {
        this._fuelHardnessDivisor = Math.max(1.0f, value);
    }

    //region internals

    private float _fuelModerationFactor;
    private float _fuelAbsorptionCoefficient;
    private float _fuelHardnessDivisor;

    //endregion
}
