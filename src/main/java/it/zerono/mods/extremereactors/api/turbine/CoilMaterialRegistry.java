/*
 *
 * CoilMaterialRegistry.java
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

package it.zerono.mods.extremereactors.api.turbine;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import it.zerono.mods.extremereactors.api.ExtremeReactorsAPI;
import it.zerono.mods.extremereactors.api.InternalDispatcher;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.tag.CollectionProviders;
import it.zerono.mods.zerocore.lib.tag.TagList;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.Optional;

/**
 * Keep track of all the CoilMaterials that could be used inside a Turbine
 */
@SuppressWarnings({"WeakerAccess"})
public class CoilMaterialRegistry {

    /**
     * Retrieve the CoilMaterial for the given block state if one exist
     *
     * @param state The block state
     * @return The CoilMaterial or null if nothing could be found
     */
    public static Optional<CoilMaterial> get(final BlockState state) {
        //TODO handle "waterlogged" block?
        return get(state.getBlock());
    }

    /**
     * Retrieve the CoilMaterial for the given block Tag if one exist
     *
     * @param tag The block Tag
     * @return The CoilMaterial or null if nothing could be found
     */
    public static Optional<CoilMaterial> get(final ITag.INamedTag<Block> tag) {
        return get(tag.getName());
    }

    /**
     * Retrieve the CoilMaterial for the given block Tag id if one exist
     *
     * @param id The id of the Block Tag
     * @return The CoilMaterial or null if nothing could be found
     */
    public static Optional<CoilMaterial> get(final ResourceLocation id) {
        return Optional.ofNullable(s_materials.get(id));
    }

    /**
     * Retrieve the CoilMaterial for the given block
     *
     * @param block The block
     * @return The CoilMaterial or null if nothing could be found
     */
    public static Optional<CoilMaterial> get(final Block block) {
        //noinspection rawtypes
        return s_tags
                .find(tag -> tag.contains(block))
                .filter(t -> t instanceof ITag.INamedTag)
                .map(t -> (ITag.INamedTag)t)
                .flatMap(CoilMaterialRegistry::get);
    }

    /**
     * Register a Block Tag as permissible in a Turbine's inductor coil.
     * All blocks that match this Tag will be permissible.
     *
     * @param tag            The Block Tag
     * @param efficiency     Efficiency of the block. 1.0 == iron, 2.0 == gold, etc.
     * @param bonus          Energy bonus of the block, if any. Normally 1.0. This is an exponential term and should only be used for EXTREMELY rare blocks!
     * @param extractionRate
     */
    public static void register(final ITag.INamedTag<Block> tag, final float efficiency,
                                final float bonus, final float extractionRate) {

        Preconditions.checkNotNull(tag);
        InternalDispatcher.dispatch("coilmaterial-register", () -> CodeHelper.optionalIfPresentOrElse(get(tag),
                material -> {

                    ExtremeReactorsAPI.LOGGER.warn("Overriding existing coil part data for Tag <{}}>", tag);

                    material.setEfficiency(efficiency);
                    material.setBonus(bonus);
                    material.setEnergyExtractionRate(extractionRate);
                },
                () -> {

                    s_materials.put(tag.getName(), new CoilMaterial(efficiency, bonus, extractionRate));
                    s_tags.addTag(tag);
                }));
    }

    /**
     * Register a Block Tag Id as permissible in a turbine's inductor coil.
     * All blocks that match this Tag will be permissible.
     * <p>
     * If the block Tag could not be found, the registration will be ignored
     *
     * @param tagId             The id of the Block Tag
     * @param efficiency     Efficiency of the block. 1.0 == iron, 2.0 == gold, etc.
     * @param bonus          Energy bonus of the block, if any. Normally 1.0. This is an exponential term and should only be used for EXTREMELY rare blocks!
     * @param extractionRate
     */
    public static void register(final String tagId, final float efficiency,
                                final float bonus, final float extractionRate) {

        Preconditions.checkNotNull(tagId);
        register(TagsHelper.BLOCKS.createTag(tagId), efficiency, bonus, extractionRate);
    }

    /**
     * Remove a previously registered CoilMaterial.
     * If the CoilMaterial is not registered the operation will fail silently.
     *
     * @param tag The Block Tag to remove
     */
    public static void remove(final ITag.INamedTag<Block> tag) {

        Preconditions.checkNotNull(tag);
        remove(tag.getName());
    }

    /**
     * Remove a previously registered CoilMaterial.
     * If the CoilMaterial is not registered the operation will fail silently.
     *
     * @param id The id of the Block Tag to remove
     */
    public static void remove(final ResourceLocation id) {

        Preconditions.checkNotNull(id);
        InternalDispatcher.dispatch("coilmaterial-remove", () -> {

            s_tags.removeTag(id);
            s_materials.remove(id);
        });
    }

    //region internals

    private static TagList<Block> s_tags = new TagList<>(CollectionProviders.BLOCKS_PROVIDER);
    private static Map<ResourceLocation, CoilMaterial> s_materials = Maps.newHashMap();

    //endregion
}
