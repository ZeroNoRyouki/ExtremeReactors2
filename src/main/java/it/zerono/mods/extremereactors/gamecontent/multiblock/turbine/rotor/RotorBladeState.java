/*
 *
 * RotorBladeState.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor;

import it.zerono.mods.zerocore.lib.CodeHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum RotorBladeState
        implements IStringSerializable {

    HIDDEN,
    // [shaft axis]_[blade direction axis]_[positive/negative]
    Y_X_POS,
    Y_X_NEG,
    Y_Z_POS,
    Y_Z_NEG,
    X_Y_POS,
    X_Y_NEG,
    X_Z_POS,
    X_Z_NEG,
    Z_Y_POS,
    Z_Y_NEG,
    Z_X_POS,
    Z_X_NEG;

    public static RotorBladeState[] VALUES = values();

    RotorBladeState() {
        this._name = CodeHelper.neutralLowercase(this.name());
    }

    public static RotorBladeState getDefault() {
        return RotorBladeState.Z_X_POS;
    }

    public static RotorBladeState from(String name) {
        return NAME_LOOKUP.getOrDefault(CodeHelper.neutralLowercase(name), HIDDEN);
    }

    public static RotorBladeState from(final RotorShaftState shaftState, final Direction bladeDirection) {

        final String name;

        switch (shaftState.getAxis()) {

            case X:
                name = "x_" + bladeDirection.getAxis().getSerializedName() + (bladeDirection.getAxisDirection() == Direction.AxisDirection.POSITIVE ? "_neg" : "_pos");
                break;

            case Y:
                name = "y_" + bladeDirection.getAxis().getSerializedName() + (bladeDirection.getAxisDirection() == Direction.AxisDirection.POSITIVE ? "_neg" : "_pos");
                break;

            case Z:
                name = "z_" + bladeDirection.getAxis().getSerializedName() + (bladeDirection.getAxisDirection() == Direction.AxisDirection.POSITIVE ? "_neg" : "_pos");
                break;

            default:
                name = shaftState.getSerializedName() + (bladeDirection.getOpposite().getAxisDirection() == Direction.AxisDirection.POSITIVE ? "_pos" : "_neg");
                break;
        }

        return RotorBladeState.from(name);
    }

    //region IStringSerializable

    @Override
    public String getSerializedName() {
        return this._name;
    }

    //endregion
    //region Object

    @Override
    public String toString() {
        return this.getSerializedName();
    }

    //endregion
    //region internals

    private static final Map<String, RotorBladeState> NAME_LOOKUP = Arrays.stream(values()).collect(Collectors.toMap(RotorBladeState::getSerializedName, state -> state));

    private final String _name;

    //region Object
}
