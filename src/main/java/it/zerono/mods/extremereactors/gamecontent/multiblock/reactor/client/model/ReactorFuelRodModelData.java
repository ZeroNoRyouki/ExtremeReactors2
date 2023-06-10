/*
 *
 * ReactorFuelRodModelData.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.model;

import it.unimi.dsi.fastutil.shorts.Short2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.ClientFuelRodsLayout;
import it.zerono.mods.zerocore.lib.client.model.data.NamedModelProperty;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import java.util.Objects;

public final class ReactorFuelRodModelData {

    public static final ModelData DEFAULT;

    public static final ModelProperty<Short> REACTANTS_MODEL_KEY = new NamedModelProperty<>("REACTANTS_MODEL_KEY", Objects::nonNull);
    public static final ModelProperty<Direction.Axis> ORIENTATION = new NamedModelProperty<>("ORIENTATION", Objects::nonNull);
    public static final ModelProperty<Boolean> OCCLUDED = new NamedModelProperty<>("OCCLUDED", Objects::nonNull);
    public static final ModelProperty<Byte> FUEL_LEVEL = new NamedModelProperty<>("FUEL_LEVEL", Objects::nonNull);
    public static final ModelProperty<Byte> WASTE_LEVEL = new NamedModelProperty<>("WASTE_LEVEL", Objects::nonNull);

    public static ModelData from(ClientFuelRodsLayout.FuelData fuelData, boolean rodIsOccluded) {
        return from(fuelData.getOrientation(), fuelData.getFuelLevel(), fuelData.getWasteLevel(), rodIsOccluded);
    }

    public static short getModelKey(final ModelData data) {

        final Short v = data.get(REACTANTS_MODEL_KEY);

        //noinspection AutoUnboxing
        return null != v ? v : (short)0;
    }

    public static Direction.Axis getOrientation(final ModelData data) {

        final Direction.Axis v = data.get(ORIENTATION);

        //noinspection AutoUnboxing
        return null == v ? Direction.Axis.X : v;
    }

    public static boolean isOccluded(final ModelData data) {

        final Boolean v = data.get(OCCLUDED);

        //noinspection AutoUnboxing
        return null != v && v;
    }

    public static byte getFuelLevel(final ModelData data) {

        final Byte v = data.get(FUEL_LEVEL);

        //noinspection AutoUnboxing
        return null != v ? v : (byte)0;
    }

    public static byte getWasteLevel(final ModelData data) {

        final Byte v = data.get(WASTE_LEVEL);

        //noinspection AutoUnboxing
        return null != v ? v : (byte)0;
    }

    //region internals

    private ReactorFuelRodModelData() {
    }

    private static short createKey(Direction.Axis axis, byte fuelLevel, byte wasteLevel, boolean rodIsOccluded) {

        final int orientation;

        switch (axis) {

            case X:
                orientation = 0x4000;
                break;

            case Z:
                orientation = 0xC000;
                break;

            default:
                orientation = 0;
                break;
        }

        return (short) (orientation | (rodIsOccluded ? 0x2000 : 0) | (fuelLevel << 4) | (wasteLevel & 0xf));
    }

    private static ModelData from(Direction.Axis axis, byte fuelLevel, byte wasteLevel, boolean rodIsOccluded) {
        return s_cache.computeIfAbsent(createKey(axis, fuelLevel, wasteLevel, rodIsOccluded),
                k -> ModelData.builder()
                        .with(REACTANTS_MODEL_KEY, (short)(k & 0xFF))
                        .with(ORIENTATION, axis)
                        .with(OCCLUDED, rodIsOccluded)
                        .with(FUEL_LEVEL, fuelLevel)
                        .with(WASTE_LEVEL, wasteLevel)
                        .build());
    }

    private static final Short2ObjectMap<ModelData> s_cache;

    static {

        s_cache = new Short2ObjectArrayMap<>(512);
        DEFAULT = from(Direction.Axis.Y, (byte) 0, (byte) 0, false);
    }

    //endregion
}
