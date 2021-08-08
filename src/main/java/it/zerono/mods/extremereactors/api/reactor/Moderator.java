/*
 *
 * Moderator.java
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

package it.zerono.mods.extremereactors.api.reactor;

import net.minecraft.util.Mth;

/**
 * Describe the properties of a material that can be used to moderate the reaction inside a Reactor
 */
@SuppressWarnings({"WeakerAccess"})
public class Moderator {

    public static final Moderator AIR = new Moderator(0.1f, 0.25f, 1.1f, IHeatEntity.CONDUCTIVITY_AIR);
    public static final Moderator WATER = new Moderator(0.33f, 0.5f, 1.33f, IHeatEntity.CONDUCTIVITY_WATER);

    /**
     * Construct a new Moderator
     *
     * @param absorption How much radiation this material absorbs and converts to heat. 0.0 = none, 1.0 = all.
     * @param heatEfficiency How efficiently radiation is converted to heat. 0 = no heat, 1 = all heat.
     * @param moderation How well this material moderates radiation. This is a divisor; should not be below 1.
     * @param heatConductivity How well this material conducts heat, in FE/t/m2.
     */
    Moderator(float absorption, float heatEfficiency, float moderation, float heatConductivity) {

        this.setAbsorption(absorption);
        this.setHeatEfficiency(heatEfficiency);
        this.setModeration(moderation);
        this.setHeatConductivity(heatConductivity);
    }

    public float getAbsorption() {
        return this._absorption;
    }

    public void setAbsorption(float value) {
        this._absorption = Mth.clamp(value, 0.0f, 1.0f);
    }

    public float getHeatEfficiency() {
        return this._heatEfficiency;
    }

    public void setHeatEfficiency(float value) {
        this._heatEfficiency = Mth.clamp(value, 0.0f, 1.0f);
    }

    public float getModeration() {
        return this._moderation;
    }

    public void setModeration(float value) {
        this._moderation = Math.max(1.0f, value);
    }

    public float getHeatConductivity() {
        return this._heatConductivity;
    }

    public void setHeatConductivity(float value) {
        this._heatConductivity = Math.max(0.0f, value);
    }

    //region internals

    private float _absorption;
    private float _heatEfficiency;
    private float _moderation;
    private float _heatConductivity;

    //endregion
}
