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
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;

public enum RotorShaftState
    implements IStringSerializable {

    HIDDEN,
    // Shaft direction - blades direction
    Y_NOBLADES,
    Y_X,
    Y_Z,
    Y_XZ,
    X_NOBLADES,
    X_Y,
    X_Z,
    X_YZ,
    Z_NOBLADES,
    Z_Y,
    Z_X,
    Z_XY;

    RotorShaftState() {
        this._name = CodeHelper.neutralLowercase(this.name());
    }

//    public static RotorShaftState from(final Direction.Axis shaftAxis) {
//
//        switch (shaftAxis) {
//
//            default:
//            case Y:
//                return Y_NOBLADES;
//
//            case X:
//                return X_NOBLADES;
//
//            case Z:
//                return Z_NOBLADES;
//        }
//    }

//    public static RotorShaftState from(final Direction.Axis shaftAxis, final boolean... directionsWithBlades) {
//
//        RotorShaftState state;
//        final Direction[] directions = RotorShaftState.getBladesDirections(shaftAxis);
//        Direction.Axis[] bladesAxis = new Direction.Axis[2];
//
//        for (int i = 0; i < 2; ++i) {
//
//            if (directionsWithBlades[i] && directionsWithBlades[i + 2])
//                bladesAxis[i] = directions[i].getAxis();
//        }
//
//        switch (shaftAxis) {
//
//            default:
//            case Y:
//                state = Y_NOBLADES;
//                break;
//
//            case X:
//                state = X_NOBLADES;
//                break;
//
//            case Z:
//                state = Z_NOBLADES;
//                break;
//        }
//
//        return state;
//    }

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
    public String getString() {
        return this._name;
    }

    //endregion
    //region Object

    @Override
    public String toString() {
        return this.getString();
    }

    //endregion
    //region internals

    private static final Direction[] BLADES_DIRECTIONS_X = new Direction[] { Direction.UP, Direction.NORTH, Direction.DOWN, Direction.SOUTH };
    private static final Direction[] BLADES_DIRECTIONS_Y = new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
    private static final Direction[] BLADES_DIRECTIONS_Z = new Direction[] { Direction.UP, Direction.EAST, Direction.DOWN, Direction.WEST };

    private final String _name;

    //endregion
}
