/*
 *
 * Reactant.java
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

import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collections;

/**
 * Describe the properties of a reactant, ie a material that can be used (Fuel) inside a
 * Reactor Fuel Rod to generate a controlled (or maybe not) reaction and that will eventually
 * produce a second reactant (Waste)
 */
public class Reactant {

    /**
     * Construct a new Reactant
     *
     * @param name The name of this reactant. Must be unique.
     * @param type The type of this reactant: Fuel or Waste.
     * @param rgbColour The color (in 0xRRGGBB form) to use when rendering fuel rods with this reactant in it.
     * @param translationKey The translation key for the name of the reactant.
     */
    Reactant(String name, ReactantType type, int rgbColour, String translationKey) {

        this._name = name;
        this._type = type;
        this._colour = Colour.fromRGB(rgbColour);
        this._translationKey = translationKey;
    }

    public String getName() {
        return this._name;
    }

    public ReactantType getType() {
        return this._type;
    }

    public Colour getColour() {
        return this._colour;
    }

    public String getTranslationKey() {
        return this._translationKey;
    }

    public ITextComponent getTranslatedName() {
        return new TranslationTextComponent(this.getTranslationKey());
    }

    /**
     * Compute the minimum amount of this Reactant that can be produced from a solid source.
     *
     * @return The smallest amount of reactant found in a given reactant<>solid mapping set
     * @throws IllegalArgumentException if no reactants were mapped.
     */
    public int getMinimumSolidSourceAmount() {
        return ReactantMappingsRegistry.getToSolid(this)
                .orElseGet(Collections::emptyList)
                .stream()
                .mapToInt(SourceProductMapping::getSourceAmount)
                .reduce(Integer::min)
                .orElseThrow(() -> new IllegalArgumentException("No solid products mapped for reactant " + this.getName()));
    }

    //region Object

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof  Reactant) {

            final Reactant other = (Reactant)obj;

            return (this == other) || (this.getName().equals(other.getName()));
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

    @Override
    public String toString() {
        return this.getName();
    }

    //endregion
    //region internals

    private final String _name;
    private final ReactantType _type;
    private final Colour _colour;
    private final String _translationKey;

    //endregion
}
