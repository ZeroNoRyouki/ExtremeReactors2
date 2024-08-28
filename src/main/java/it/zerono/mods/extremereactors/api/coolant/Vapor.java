/*
 *
 * Vapor.java
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

public class Vapor
        extends AbstractNamedValue {

    public static final Vapor EMPTY = new Vapor("empty", Colour.WHITE, 0.0f, "gui.zerocore.base.generic.empty");

    public static final ModCodecs<Vapor, ByteBuf> CODECS = new ModCodecs<>(
            RecordCodecBuilder.create(instance -> instance.group(
                            Codec.STRING.fieldOf("name").forGetter(AbstractNamedValue::getName),
                            Colour.CODECS.field("colour", Vapor::getColour),
                            Codec.FLOAT.fieldOf("density").forGetter(Vapor::getFluidEnergyDensity),
                            Codec.STRING.fieldOf("translation").forGetter(AbstractNamedValue::getTranslationKey))
                    .apply(instance, Vapor::new)),
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8, AbstractNamedValue::getName,
                    Colour.CODECS.streamCodec(), Vapor::getColour,
                    ByteBufCodecs.FLOAT, Vapor::getFluidEnergyDensity,
                    ByteBufCodecs.STRING_UTF8, AbstractNamedValue::getTranslationKey,
                    Vapor::new)
    );

    /**
     * Construct a new Vapor
     *
     * @param name The name of this vapor. Must be unique.
     * @param fluidEnergyDensity the energy density of this vapor (in FE per mB)
     * @param translationKey The translation key for the name of the vapor
     */
    Vapor(final String name, final Colour colour, final float fluidEnergyDensity, final String translationKey) {

        super(name, translationKey);
        this._fluidEnergyDensity = Math.max(0.0f, fluidEnergyDensity);
        this._colour = colour;
    }

    public float getFluidEnergyDensity() {
        return this._fluidEnergyDensity;
    }

    public Vapor copy() {
        return new Vapor(this.getName(), this.getColour(), this.getFluidEnergyDensity(), this.getTranslationKey());
    }

    public Colour getColour() {
        return this._colour;
    }

    //region Object

    @Override
    public boolean equals(final Object obj) {

        return (this == obj) ||
                (obj instanceof Vapor &&
                        super.equals(obj) &&
                        this.getFluidEnergyDensity() == ((Vapor)obj).getFluidEnergyDensity());
    }

    //endregion
    //region internals

    private final float _fluidEnergyDensity;
    private final Colour _colour;

    //endregion
}
