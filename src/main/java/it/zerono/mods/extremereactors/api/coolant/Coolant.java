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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.zerono.mods.extremereactors.api.internal.AbstractNamedValue;
import it.zerono.mods.zerocore.lib.data.ModCodecs;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Describe the properties of a coolant, ie a fluid that can be used inside a Reactor
 * to absorb heat and be converted to a gaseous fluid that can be used to drive a Turbine
 */
public class Coolant
    extends AbstractNamedValue {

    public static final Coolant EMPTY = new Coolant("empty", Colour.WHITE, Float.MAX_VALUE, 0.0f, "gui.zerocore.base.generic.empty");

    public static final ModCodecs<Coolant, ByteBuf> CODECS = new ModCodecs<>(
            RecordCodecBuilder.create(instance -> instance.group(
                            Codec.STRING.fieldOf("name").forGetter(AbstractNamedValue::getName),
                            Colour.CODECS.field("colour", Coolant::getColour),
                            Codec.FLOAT.fieldOf("boiling").forGetter(Coolant::getBoilingPoint),
                            Codec.FLOAT.fieldOf("enthalpy").forGetter(Coolant::getEnthalpyOfVaporization),
                            Codec.STRING.fieldOf("translation").forGetter(AbstractNamedValue::getTranslationKey))
                    .apply(instance, Coolant::new)),
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, AbstractNamedValue::getName,
                    Colour.CODECS.streamCodec(), Coolant::getColour,
                    ByteBufCodecs.FLOAT, Coolant::getBoilingPoint,
                    ByteBufCodecs.FLOAT, Coolant::getEnthalpyOfVaporization,
                    ByteBufCodecs.STRING_UTF8, AbstractNamedValue::getTranslationKey,
                    Coolant::new)
    );

    /**
     * Construct a new Coolant
     *
     * @param name The name of this coolant. Must be unique.
     * @param boilingPoint the temperature at which the coolant changes into a gas
     * @param enthalpyOfVaporization the amount of energy needed to transform the coolant into a gas
     * @param translationKey The translation key for the name of the coolant
     */
    Coolant(final String name, final Colour colour, final float boilingPoint, final float enthalpyOfVaporization, final String translationKey) {

        super(name, translationKey);
        this._boilingPoint = Math.max(0.0f, boilingPoint);
        this._enthalpyOfVaporization = Math.max(0.0f, enthalpyOfVaporization);
        this._colour = colour;
    }

    public float getBoilingPoint() {
        return this._boilingPoint;
    }

    public float getEnthalpyOfVaporization() {
        return this._enthalpyOfVaporization;
    }

    public Coolant copy() {
        return new Coolant(this.getName(), this.getColour(), this.getBoilingPoint(), this.getEnthalpyOfVaporization(), this.getTranslationKey());
    }

    public Colour getColour() {
        return this._colour;
    }

    //region Object

    @Override
    public boolean equals(final Object obj) {
        return (this == obj) ||
                (obj instanceof Coolant &&
                        super.equals(obj) &&
                        this.getBoilingPoint() == ((Coolant)obj).getBoilingPoint() &&
                        this.getEnthalpyOfVaporization() == ((Coolant)obj).getEnthalpyOfVaporization());
    }

    //endregion
    //region internals

    private final float _boilingPoint;
    private final float _enthalpyOfVaporization;
    private final Colour _colour;

    //endregion
}
