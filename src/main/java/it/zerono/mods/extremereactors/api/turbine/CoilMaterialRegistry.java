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
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.extremereactors.api.ExtremeReactorsAPI;
import it.zerono.mods.extremereactors.api.internal.InternalDispatcher;
import it.zerono.mods.extremereactors.api.internal.modpack.wrapper.ApiWrapper;
import it.zerono.mods.zerocore.lib.tag.TagList;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.*;

/**
 * Keep track of all the CoilMaterials that could be used inside a Turbine
 */
@SuppressWarnings({"WeakerAccess"})
@Mod.EventBusSubscriber(modid = ExtremeReactorsAPI.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CoilMaterialRegistry {

    /**
     * Check if a CoilMaterial is registered for the given block Tag
     *
     * @param tag The block Tag
     * @return true if a CoilMaterial is registered for the given block Tag, false otherwise
     */
    public static boolean contains(final TagKey<Block> tag) {
        return s_materials.containsKey(tag);
    }

    /**
     * Retrieve the CoilMaterial for the given block state if one exist
     *
     * @param state The block state
     * @return The CoilMaterial or null if nothing could be found
     */
    public static Optional<CoilMaterial> get(final BlockState state) {

        final List<TagKey<Block>> tags = TagsHelper.BLOCKS.getTags(state.getBlock());

        return s_tags
                .findFirst(tags::contains)
                .map(s_materials::get);

        //TODO handle "waterlogged" block?
    }

    /**
     * Retrieve the CoilMaterial for the given block Tag if one exist
     *
     * @param tag The block Tag
     * @return The CoilMaterial or null if nothing could be found
     */
    public static Optional<CoilMaterial> get(final TagKey<Block> tag) {
        return Optional.ofNullable(s_materials.get(tag));
    }

    /**
     * Register a Block Tag as permissible in a Turbine's inductor coil.
     * All blocks that match this Tag will be permissible.
     *
     * @param tagId          The id of the Block Tag
     * @param efficiency     Efficiency of the block. 1.0 == iron, 2.0 == gold, etc.
     * @param bonus          Energy bonus of the block, if any. Normally 1.0. This is an exponential term and should only be used for EXTREMELY rare blocks!
     * @param extractionRate
     */
    public static void register(final String tagId, final float efficiency,
                                final float bonus, final float extractionRate) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(tagId));
        register(TagsHelper.BLOCKS.createKey(tagId), efficiency, bonus, extractionRate);
    }

    /**
     * Register a Block Tag as permissible in a Turbine's inductor coil.
     * All blocks that match this Tag will be permissible.
     *
     * @param tag            The Block Tag key
     * @param efficiency     Efficiency of the block. 1.0 == iron, 2.0 == gold, etc.
     * @param bonus          Energy bonus of the block, if any. Normally 1.0. This is an exponential term and should only be used for EXTREMELY rare blocks!
     * @param extractionRate
     */
    public static void register(final TagKey<Block> tag, final float efficiency,
                                final float bonus, final float extractionRate) {

        Preconditions.checkNotNull(tag);
        InternalDispatcher.dispatch("coilmaterial-register", () -> {

            if (s_materials.containsKey(tag)) {
                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Overriding existing coil data for Tag {}", tag);
            }

            final CoilMaterial c = new CoilMaterial(efficiency, bonus, extractionRate);

            s_materials.merge(tag, c, (o, n) -> c);
            s_tags.addTag(tag);
        });
    }

    /**
     * Remove a previously registered CoilMaterial.
     * If the CoilMaterial is not registered the operation will fail silently.
     *
     * @param tagId The id of the Block Tag to remove
     */
    public static void remove(final String tagId) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(tagId));
        remove(TagsHelper.BLOCKS.createKey(tagId));
    }

    /**
     * Remove a previously registered CoilMaterial.
     * If the CoilMaterial is not registered the operation will fail silently.
     *
     * @param tag The Block Tag key to remove
     */
    public static void remove(final TagKey<Block> tag) {

        Preconditions.checkNotNull(tag);
        InternalDispatcher.dispatch("coilmaterial-remove", () -> {

            s_materials.remove(tag);
            s_tags.removeTag(tag);
        });
    }

    public static void fillCoilsTooltips(final Map<Item, Set<Component>> tooltipsMap,
                                         final NonNullSupplier<Set<Component>> setSupplier) {

        s_tags.stream()
                .map(TagsHelper.BLOCKS::getObjects)
                .flatMap(List::stream)
                .map(Block::asItem)
                .forEach(item -> tooltipsMap.computeIfAbsent(item, k -> setSupplier.get()).add(TOOLTIP_COIL));
    }

    public static void processWrapper(final ApiWrapper wrapper) {

        if (!wrapper.Enabled) {
            return;
        }

        if (wrapper.TurbineCoils.WipeExistingValuesBeforeAdding) {

            // wipe all

            Log.LOGGER.info(WRAPPER, "Wiping all existing Reactor reactants reactions");
            s_materials.clear();

        } else {

            // remove from list

            Arrays.stream(wrapper.TurbineCoils.Remove)
                    .filter(name -> !Strings.isNullOrEmpty(name))
                    .forEach(CoilMaterialRegistry::remove);
        }

        // add new values

        Arrays.stream(wrapper.TurbineCoils.Add)
                .filter(Objects::nonNull)
                .forEach((it.zerono.mods.extremereactors.api.internal.modpack.wrapper.CoilMaterial w) ->
                        register(w.BlockTagId, w.Efficiency, w.Bonus, w.ExtractionRate));
    }

    //region internals

    private static final TagList<Block> s_tags = TagList.blocks();
    private static final Map<TagKey<Block>, CoilMaterial> s_materials = new Object2ObjectArrayMap<>(32);

    private static final Component TOOLTIP_COIL = new TranslatableComponent("api.bigreactors.reactor.tooltip.coil").setStyle(ExtremeReactorsAPI.STYLE_TOOLTIP);

    private static final Marker MARKER = MarkerManager.getMarker("API/CoilMaterialRegistry").addParents(ExtremeReactorsAPI.MARKER);
    private static final Marker WRAPPER = MarkerManager.getMarker("ModPack API Wrapper").addParents(MARKER);

    //endregion
}
