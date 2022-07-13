/*
 *
 * ModelTransformers.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.model.data;

import com.google.common.collect.Lists;
import it.zerono.mods.zerocore.lib.block.BlockFacings;
import it.zerono.mods.zerocore.lib.block.property.BlockFacingsProperty;
import it.zerono.mods.zerocore.lib.multiblock.AbstractMultiblockPart;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import java.util.List;
import java.util.Optional;

public class ModelTransformers {

    public static final int MODEL_DEFAULT = 0;
    public static final int MODEL_VARIANT_1 = 1;
    public static final int MODEL_VARIANT_2 = 2;
    public static final int MODEL_VARIANT_3 = 3;
    public static final int MODEL_VARIANT_4 = 4;

    /*
    Generic side-mappings from our custom model blockstate file
     *//*
    public static final Direction BLOCKSTATE_CASING = Direction.DOWN;
    public static final Direction BLOCKSTATE_MACHINE = Direction.UP;
    public static final Direction BLOCKSTATE_CUSTOM_1 = Direction.NORTH;
    public static final Direction BLOCKSTATE_CUSTOM_2 = Direction.SOUTH;
    public static final Direction BLOCKSTATE_CUSTOM_3 = Direction.WEST;
    public static final Direction BLOCKSTATE_CUSTOM_4 = Direction.EAST;
*/

    public static int defaultModelTransformer(final ModelData data) {
        return MODEL_DEFAULT;
    }

    public static <C extends AbstractCuboidMultiblockController<C>> byte getGlassVariantIndexFrom(final AbstractMultiblockPart<C> entity) {

        if (!entity.isMachineAssembled()) {
            return 0;
        }

        final Level world = entity.getPartWorldOrFail();
        final BlockPos position = entity.getWorldPosition();
        final BlockState entityBlockState = world.getBlockState(position);
        final Block entityBlock = entityBlockState.getBlock();
        final Direction[] directions = Direction.values();
        final boolean[] actualFacings = new boolean[directions.length];

        for (int i = 0; i < directions.length; ++i) {
            actualFacings[i] = entityBlock == world.getBlockState(position.relative(directions[i])).getBlock();
        }

        final BlockFacingsProperty propertyValue = BlockFacings.from(actualFacings).toProperty();

        return (byte)(propertyValue.ordinal());
    }

    public static byte getCasingVariantIndexFrom(final BlockFacings facings) {

        if (facings.none()) {
            return 0;
        } else if (facings.one()) {
            return 1;
        }

        List<BlockFacings> casingFacings = s_casingFacings;

        if (null == casingFacings) {
            synchronized (s_lock) {

                casingFacings = s_casingFacings;

                if (null == casingFacings) {

                    s_casingFacings = casingFacings = Lists.newArrayListWithCapacity(20);

                    casingFacings.add(BlockFacings.from(Direction.DOWN, Direction.SOUTH)); // casing_02_frame_ds
                    casingFacings.add(BlockFacings.from(Direction.DOWN, Direction.EAST)); // casing_03_frame_de
                    casingFacings.add(BlockFacings.from(Direction.DOWN, Direction.NORTH)); // casing_04_frame_dn
                    casingFacings.add(BlockFacings.from(Direction.DOWN, Direction.WEST)); // casing_05_frame_dw
                    casingFacings.add(BlockFacings.from(Direction.UP, Direction.SOUTH)); // casing_06_frame_us
                    casingFacings.add(BlockFacings.from(Direction.UP, Direction.EAST)); // casing_07_frame_ue
                    casingFacings.add(BlockFacings.from(Direction.UP, Direction.NORTH)); // casing_08_frame_un
                    casingFacings.add(BlockFacings.from(Direction.UP, Direction.WEST)); // casing_09_frame_uw
                    casingFacings.add(BlockFacings.from(Direction.SOUTH, Direction.EAST)); // casing_10_frame_se
                    casingFacings.add(BlockFacings.from(Direction.NORTH, Direction.EAST)); // casing_11_frame_ne
                    casingFacings.add(BlockFacings.from(Direction.NORTH, Direction.WEST)); // casing_12_frame_nw
                    casingFacings.add(BlockFacings.from(Direction.SOUTH, Direction.WEST)); // casing_13_frame_sw
                    casingFacings.add(BlockFacings.from(Direction.DOWN, Direction.SOUTH, Direction.WEST)); // casing_14_corner_dsw
                    casingFacings.add(BlockFacings.from(Direction.DOWN, Direction.SOUTH, Direction.EAST)); // casing_15_corner_dse
                    casingFacings.add(BlockFacings.from(Direction.DOWN, Direction.NORTH, Direction.EAST)); // casing_16_corner_dne
                    casingFacings.add(BlockFacings.from(Direction.DOWN, Direction.NORTH, Direction.WEST)); // casing_17_corner_dnw
                    casingFacings.add(BlockFacings.from(Direction.UP, Direction.SOUTH, Direction.WEST)); // casing_18_corner_usw
                    casingFacings.add(BlockFacings.from(Direction.UP, Direction.SOUTH, Direction.EAST)); // casing_19_corner_use
                    casingFacings.add(BlockFacings.from(Direction.UP, Direction.NORTH, Direction.EAST)); // casing_20_corner_une
                    casingFacings.add(BlockFacings.from(Direction.UP, Direction.NORTH, Direction.WEST)); // casing_21_corner_unw
                }
            }
        }

        final int variantIndex = casingFacings.indexOf(facings);

        return (byte)(variantIndex >= 0 ? 2 + variantIndex : 0);
    }

    //region internals

    private static <T> Optional<T> getPropertyValue(final ModelData data, final ModelProperty<T> property) {
        return data.has(property) ? Optional.ofNullable(data.get(property)) : Optional.empty();
    }

    private static final Object s_lock = new Object();

    private static volatile List<BlockFacings> s_casingFacings;

    //endregion
}
