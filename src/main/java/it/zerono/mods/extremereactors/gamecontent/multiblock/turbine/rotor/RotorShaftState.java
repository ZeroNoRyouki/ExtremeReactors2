/*
 *
 * RotorShaftState.java
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
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

public enum RotorShaftState
    implements StringRepresentable {

    HIDDEN(Direction.Axis.Y),
    // Shaft direction - blades direction
    Y_NOBLADES(Direction.Axis.Y),
    Y_X(Direction.Axis.Y),
    Y_Z(Direction.Axis.Y),
    Y_XZ(Direction.Axis.Y),
    X_NOBLADES(Direction.Axis.X),
    X_Y(Direction.Axis.X),
    X_Z(Direction.Axis.X),
    X_YZ(Direction.Axis.X),
    Z_NOBLADES(Direction.Axis.Z),
    Z_Y(Direction.Axis.Z),
    Z_X(Direction.Axis.Z),
    Z_XY(Direction.Axis.Z);

    public static RotorShaftState[] VALUES = values();

    RotorShaftState(final Direction.Axis axis) {

        this._name = CodeHelper.neutralLowercase(this.name());
        this._axis = axis;
    }

    public Direction.Axis getAxis() {
        return this._axis;
    }

    public static RotorShaftState getDefault() {
        return RotorShaftState.Y_NOBLADES;
    }
    
    public static Direction[] getBladesDirections(final Direction.Axis axis) {

        switch (axis) {

            case X:
                return BLADES_DIRECTIONS_X;

            default:
            case Y:
                return BLADES_DIRECTIONS_Y;

            case Z:
                return BLADES_DIRECTIONS_Z;
        }
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

    private static final Direction[] BLADES_DIRECTIONS_X = new Direction[] { Direction.UP, Direction.NORTH, Direction.DOWN, Direction.SOUTH };
    private static final Direction[] BLADES_DIRECTIONS_Y = new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
    private static final Direction[] BLADES_DIRECTIONS_Z = new Direction[] { Direction.UP, Direction.EAST, Direction.DOWN, Direction.WEST };

    private final String _name;
    private final Direction.Axis _axis;

    //endregion
}
