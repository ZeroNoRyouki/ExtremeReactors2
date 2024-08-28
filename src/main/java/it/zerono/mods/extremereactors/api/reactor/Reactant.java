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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.zerono.mods.extremereactors.api.IMapping;
import it.zerono.mods.extremereactors.api.internal.AbstractNamedValue;
import it.zerono.mods.zerocore.lib.data.ModCodecs;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Collections;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Describe the properties of a reactant, ie a material that can be used (Fuel) inside a
 * Reactor Fuel Rod to generate a controlled (or maybe not) reaction and that will eventually
 * produce a second reactant (Waste)
 */
public class Reactant
    extends AbstractNamedValue
    implements Predicate<ReactantType> {

    public static final ModCodecs<Reactant, ByteBuf> CODECS = new ModCodecs<>(
            RecordCodecBuilder.create(instance -> instance.group(
                            Codec.STRING.fieldOf("name").forGetter(AbstractNamedValue::getName),
                            ReactantType.CODECS.field("type", Reactant::getType),
                            Colour.CODECS.field("colour", Reactant::getColour),
                            Codec.STRING.fieldOf("translation").forGetter(AbstractNamedValue::getTranslationKey),
                            FuelProperties.CODECS.field("fuelprop", Reactant::getFuelData))
                    .apply(instance, Reactant::new)),
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, AbstractNamedValue::getName,
                    ReactantType.CODECS.streamCodec(), Reactant::getType,
                    Colour.CODECS.streamCodec(), Reactant::getColour,
                    ByteBufCodecs.STRING_UTF8, AbstractNamedValue::getTranslationKey,
                    FuelProperties.CODECS.streamCodec(), Reactant::getFuelData,
                    Reactant::new)
    );

    /**
     * Construct a new Reactant
     *
     * @param name The name of this reactant. Must be unique.
     * @param type The type of this reactant: Fuel or Waste.
     * @param rgbColour The color (in 0xRRGGBB form) to use when rendering fuel rods with this reactant in it.
     * @param fuelData The Fuel data associated to this Reactant (if it is a fuel)
     * @param translationKey The translation key for the name of the reactant.
     */
    Reactant(String name, ReactantType type, int rgbColour, String translationKey, FuelProperties fuelData) {
        this(name, type, Colour.fromRGB(rgbColour), translationKey, fuelData);
    }

    public ReactantType getType() {
        return this._type;
    }

    public Colour getColour() {
        return this._colour;
    }

    public FuelProperties getFuelData() {
        return this._fuelData;
    }

    /**
     * Compute the minimum amount of this Reactant that can be produced from a solid source.
     *
     * @return The smallest amount of reactant found in a given reactant<>solid mapping set or -1 if no mapping could be found.
     */
    public int getMinimumSolidSourceAmount() {
        return ReactantMappingsRegistry.getToSolid(this)
                .orElseGet(Collections::emptyList)
                .stream()
                .mapToInt(IMapping::getSourceAmount)
                .reduce(Integer::min)
                .orElse(-1);
    }

    /**
     * Compute the minimum amount of this Reactant that can be produced from a fluid source.
     *
     * @return The smallest amount of reactant found in a given reactant<>fluid mapping set or -1 if no mapping could be found.
     */
    public int getMinimumFluidSourceAmount() {
        return ReactantMappingsRegistry.getToFluid(this)
                .map($ -> ReactantMappingsRegistry.STANDARD_FLUID_REACTANT_AMOUNT)
                .orElse(-1);
    }

    public Reactant copy() {
        return new Reactant(this.getName(), this.getType(), this.getColour().toRGBA(), this.getTranslationKey(), this.getFuelData());
    }

    //region Predicate<ReactantType>

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param type the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    @Override
    public boolean test(final ReactantType type) {
        return this.getType() == type;
    }

    //endregion
    //region Object

    @Override
    public boolean equals(final Object other) {

        if (this == other) {
            return true;
        }

        if (!(other instanceof Reactant) || !super.equals(other)) {
            return false;
        }

        final Reactant reactant = (Reactant) other;

        return this._type == reactant._type && Objects.equals(this._colour, reactant._colour) &&
                Objects.equals(this._fuelData, reactant._fuelData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this._type, this._colour, this._fuelData);
    }

    //endregion
    //region internals

    private Reactant(String name, ReactantType type, Colour colour, String translationKey, FuelProperties fuelData) {

        super(name, translationKey);
        this._type = Objects.requireNonNull(type);
        this._colour = Objects.requireNonNull(colour);
        this._fuelData = type.isFuel() ? Objects.requireNonNull(fuelData) : FuelProperties.INVALID;
    }

    private final ReactantType _type;
    private final Colour _colour;
    private final FuelProperties _fuelData;

    //endregion
}
