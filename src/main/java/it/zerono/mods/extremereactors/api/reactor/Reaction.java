/*
 *
 * Reaction.java
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

import it.zerono.mods.extremereactors.api.IMapping;

/**
 * This is the reaction of 1 unit of source Reactant to 1 unit of product Reactant.
 * Floating-point quantities will be used, so this registration
 * is for a Unit Reaction of 1 unit of reactant to 1 unit of product.
 *
 * Reactivity:
 *  An exponent applied to the number of fission events to determine the base amount of radiation created.
 *  Raising this produces more radiation per fuel unit.
 *
 * Fission rate:
 *  How fast this reaction occurs. Each tick, the reactor will multiply this number by the amount of source
 *  reactant to determine how much source reactant actually reacts.
 *  Raising this will cause the reactor to burn this reactant faster.
 */
public class Reaction
        extends IMapping.OneToOne<Reactant, Reactant> {

    public static final float STANDARD_REACTIVITY = 1.05f;
    public static final float STANDARD_FISSIONRATE = 0.01f;

    /**
     * Construct a new Reaction
     *
     * @param sourceReactant The source Reactant.
     * @param productReactant The product Reactant.
     * @param reactivity The reactivity value.
     * @param fissionRate The fission rate value.
     */
    Reaction(final Reactant sourceReactant, final Reactant productReactant,
             final float reactivity, final float fissionRate) {

        super(sourceReactant, 1, productReactant, 1);
        this._reactivity = reactivity;
        this._fissionRate = fissionRate;
    }

    public float getReactivity() {
        return this._reactivity;
    }

    public float getFissionRate() {
        return this._fissionRate;
    }

    //region internals

    private final float _reactivity;
    private final float _fissionRate;

    //endregion
}
