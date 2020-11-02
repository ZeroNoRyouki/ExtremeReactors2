/*
 *
 * FuelRodsLayout.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor;

import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorFuelRodEntity;
import net.minecraft.util.Direction;

import java.util.Set;

public class FuelRodsLayout {

    public static final FuelRodsLayout DEFAULT = new FuelRodsLayout();

    public FuelRodsLayout(Direction direction, int length) {

        this._cachedOutwardFacing = direction;
        this._rodLength = Math.max(1, length);
    }

    public Direction.Axis getAxis() {
        return this._cachedOutwardFacing.getAxis();
    }

    public Direction.Plane getOrientation() {
        return this.getAxis().getPlane();
    }

    public Direction[] getRadiateDirections() {

        switch (this.getAxis()) {

            case X:
                return RADIATE_DIRECTIONS_X_AXIS;

            default:
            case Y:
                return RADIATE_DIRECTIONS_Y_AXIS;

            case Z:
                return RADIATE_DIRECTIONS_Z_AXIS;
        }
    }

    public int getRodLength() {
        return this._rodLength;
    }

    public void updateFuelData(IFuelContainer fuelData, final int fuelRodsInReactor) {
    }

    public void updateFuelRodsOcclusion(Set<ReactorFuelRodEntity> fuelRods) {
    }

    //region internals

    private FuelRodsLayout() {

        this._cachedOutwardFacing = Direction.UP;
        this._rodLength = 1;
    }

    private final Direction _cachedOutwardFacing;
    private final int _rodLength;

    private static final Direction[] RADIATE_DIRECTIONS_X_AXIS = {Direction.UP, Direction.NORTH, Direction.DOWN, Direction.SOUTH};
    private static final Direction[] RADIATE_DIRECTIONS_Y_AXIS = {Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST};
    private static final Direction[] RADIATE_DIRECTIONS_Z_AXIS = {Direction.EAST, Direction.DOWN, Direction.WEST, Direction.UP};

    //endregion
}
