/*
 *
 * Coolant.java
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

package it.zerono.mods.extremereactors.api.coolant;

import it.zerono.mods.extremereactors.api.internal.AbstractNamedValue;

/**
 * Describe the properties of a coolant, ie a fluid that can be used inside a Reactor
 * to absorb heat and be converted to a gaseous fluid that can be used to drive a Turbine
 */
public class Coolant
    extends AbstractNamedValue {

    public static final Coolant EMPTY = new Coolant("empty", Float.MAX_VALUE, 0.0f, "gui.bigreactors.generic.empty");

    /**
     * Construct a new Coolant
     *
     * @param name The name of this coolant. Must be unique.
     * @param boilingPoint the temperature at which the coolant changes into a gas
     * @param enthalpyOfVaporization the amount of energy needed to transform the coolant into a gas
     * @param translationKey The translation key for the name of the coolant
     */
    Coolant(final String name, final float boilingPoint, final float enthalpyOfVaporization, final String translationKey) {

        super(name, translationKey);
        this._boilingPoint = boilingPoint;
        this._enthalpyOfVaporization = enthalpyOfVaporization;
    }

    public float getBoilingPoint() {
        return this._boilingPoint;
    }

    public float getEnthalpyOfVaporization() {
        return this._enthalpyOfVaporization;
    }

    //region Object

    @Override
    public boolean equals(final Object obj) {

        return (this == obj) ||
                (obj instanceof Coolant &&
                        super.equals(obj) &&
                        this.getBoilingPoint() == ((Coolant)obj).getBoilingPoint() &&
                        this.getEnthalpyOfVaporization() == ((Coolant)obj).getEnthalpyOfVaporization());

//        if ((obj instanceof Coolant) && super.equals(obj)) {
//
//            final Coolant other = (Coolant)obj;
//
//            return this.getBoilingPoint() == other.getBoilingPoint() &&
//                    this.getEnthalpyOfVaporization() == other.getEnthalpyOfVaporization();
//        }
//
//        return false;
    }

    //endregion
    //region internals

    private final float _boilingPoint;
    private final float _enthalpyOfVaporization;

    //endregion
}
