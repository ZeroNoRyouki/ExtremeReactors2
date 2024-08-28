/*
 *
 * ReactantType.java
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

import io.netty.buffer.ByteBuf;
import it.zerono.mods.zerocore.lib.data.ModCodecs;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.function.IntFunction;

/**
 * The type of Reactant. Fuel-Reactants are converted to Waste-Reactants inside a Reactor Fuel Rods
 */
public enum ReactantType
        implements StringRepresentable {

    Fuel(0xbcba50),
    Waste(0x4d92b5);

    public static final IntFunction<ReactantType> BY_ID = ByIdMap.continuous(Enum::ordinal, values(),
            ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final ModCodecs<ReactantType, ByteBuf> CODECS = new ModCodecs<>(
            StringRepresentable.fromEnum(ReactantType::values),
            ByteBufCodecs.idMapper(BY_ID, Enum::ordinal));

    public boolean isFuel() {
        return Fuel == this;
    }

    public boolean isWaste() {
        return Waste == this;
    }

    public int getDefaultColour() {
        return this._rgbDefaultColor;
    }

    //region StringRepresentable

    public String getSerializedName() {
        return this.name();
    }

    //endregion
    //region internals

    ReactantType(int defaultColour) {
        this._rgbDefaultColor = defaultColour;
    }

    private final int _rgbDefaultColor;

    //endregion
}
