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
import com.google.common.collect.Maps;
import it.zerono.mods.extremereactors.api.ExtremeReactorsAPI;
import it.zerono.mods.extremereactors.api.InternalDispatcher;
import it.zerono.mods.zerocore.lib.tag.CollectionProviders;
import it.zerono.mods.zerocore.lib.tag.TagList;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Keep track of all the CoilMaterials that could be used inside a Turbine
 */
@SuppressWarnings({"WeakerAccess"})
@Mod.EventBusSubscriber(modid = ExtremeReactorsAPI.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CoilMaterialRegistry {

    /**
     * Check if a CoilMaterial is registered for the given BlockState
     *
     * @param state The block state
     * @return true if a CoilMaterial is registered for the given BlockState, false otherwise
     */
    public static boolean contains(final BlockState state) {
        return contains(state.getBlock());
    }

    /**
     * Check if a CoilMaterial is registered for the given block Tag
     *
     * @param tag The block Tag
     * @return true if a CoilMaterial is registered for the given block Tag, false otherwise
     */
    public static boolean contains(final ITag.INamedTag<Block> tag) {
        return contains(tag.getName());
    }

    /**
     * Check if a CoilMaterial is registered for the given block Tag id
     *
     * @param id The block Tag id
     * @return true if a CoilMaterial is registered for the given block Tag id, false otherwise
     */
    public static boolean contains(final ResourceLocation id) {
        return s_materials.containsKey(id);
    }

    /**
     * Check if a CoilMaterial is registered for the given block
     *
     * @param block The block
     * @return true if a CoilMaterial is registered for the given block, false otherwise
     */
    public static boolean contains(final Block block) {
        return s_tags
                .find(tag -> tag.contains(block))
                .isPresent();
    }

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
     * @param tagId          The id of the Block Tag
     * @param efficiency     Efficiency of the block. 1.0 == iron, 2.0 == gold, etc.
     * @param bonus          Energy bonus of the block, if any. Normally 1.0. This is an exponential term and should only be used for EXTREMELY rare blocks!
     * @param extractionRate
     */
    public static void register(final String tagId, final float efficiency,
                                final float bonus, final float extractionRate) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(tagId));

        InternalDispatcher.dispatch("coilmaterial-register", () -> {

            final ResourceLocation id = new ResourceLocation(tagId);

            if (s_materials.containsKey(id)) { ;
                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Overriding existing coil data for Tag {}", tagId);
            }

            final CoilMaterial c = new CoilMaterial(efficiency, bonus, extractionRate);

            s_materials.merge(id, c, (o, n) -> c);
        });
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
        InternalDispatcher.dispatch("coilmaterial-remove", () -> s_materials.remove(id));
    }

    public static void fillModeratorsTooltips(final Map<Item, Set<ITextComponent>> tooltipsMap,
                                              final NonNullSupplier<Set<ITextComponent>> setSupplier) {

        s_tags.tagStream()
                .flatMap(blockTag -> blockTag.getAllElements().stream())
                .map(Block::asItem)
                .forEach(item -> tooltipsMap.computeIfAbsent(item, k -> setSupplier.get()).add(TOOLTIP_COIL));
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onVanillaTagsUpdated(final TagsUpdatedEvent.VanillaTagTypes event) {

        s_tags.clear();
        s_materials.keySet().stream()
                .filter(TagsHelper.BLOCKS::tagExist)
                .map(TagsHelper.BLOCKS::createTag)
                .forEach(s_tags::addTag);
    }

    //region internals

    private static TagList<Block> s_tags = new TagList<>(CollectionProviders.BLOCKS_PROVIDER);
    private static Map<ResourceLocation, CoilMaterial> s_materials = Maps.newHashMap();

    private static final ITextComponent TOOLTIP_COIL = new TranslationTextComponent("api.bigreactors.reactor.tooltip.coil").setStyle(ExtremeReactorsAPI.STYLE_TOOLTIP);

    private static final Marker MARKER = MarkerManager.getMarker("API/CoilMaterialRegistry").addParents(ExtremeReactorsAPI.MARKER);

    //endregion
}
