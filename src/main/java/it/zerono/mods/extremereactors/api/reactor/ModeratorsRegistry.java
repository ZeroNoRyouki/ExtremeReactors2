/*
 *
 * ModeratorsRegistry.java
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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import it.zerono.mods.extremereactors.api.ExtremeReactorsAPI;
import it.zerono.mods.extremereactors.api.InternalDispatcher;
import it.zerono.mods.zerocore.ZeroCore;
import it.zerono.mods.zerocore.lib.tag.CollectionProviders;
import it.zerono.mods.zerocore.lib.tag.TagList;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Map;
import java.util.Optional;

/**
 * Keep track of all the Moderators that could be used inside a Reactor
 */
@SuppressWarnings({"WeakerAccess"})
public final class ModeratorsRegistry {

    /**
     * Retrieve the radiation moderation data for the given block state
     *
     * @param state The block state
     * @return The Moderator or null if nothing could be found
     */
    public static Optional<Moderator> getFromSolid(final BlockState state) {
        return getFromSolid(state.getBlock());
    }

    /**
     * Retrieve the radiation moderation data for the given block
     *
     * @param block The block
     * @return The Moderator or null if nothing could be found
     */
    public static Optional<Moderator> getFromSolid(final Block block) {
        //noinspection rawtypes
        return s_moderatorBlocksTags
                .find(tag -> tag.contains(block))
                .filter(t -> t instanceof ITag.INamedTag)
                .map(t -> (ITag.INamedTag)t)
                .flatMap(ModeratorsRegistry::getFromSolid);
    }

    /**
     * Retrieve the radiation moderation data for the given block Tag
     *
     * @param tag The Block Tag
     * @return The Moderator or null if nothing could be found
     */
    public static Optional<Moderator> getFromSolid(final ITag.INamedTag<Block> tag) {
        return Optional.ofNullable(s_moderatorBlocksData.get(tag.getName()));
    }

    /**
     * Retrieve the radiation moderation data for the given block Tag
     *
     * @param tag The Fluid Tag
     * @return The Moderator or null if nothing could be found
     */
    public static Optional<Moderator> getFromFluid(final ITag.INamedTag<Fluid> tag) {
        return Optional.ofNullable(s_moderatorFluidsData.get(tag.getName()));
    }

    /**
     * Register a block Tag as a radiation moderator for the Reactor
     * All blocks that match this Tag will be permissible
     * <p>
     * If the Tag is already registered, the provided values will replace the existing ones
     *
     * @param tag              The block Tag
     * @param absorption       How much radiation this material absorbs and converts to heat. 0.0 = none, 1.0 = all.
     * @param heatEfficiency   How efficiently radiation is converted to heat. 0 = no heat, 1 = all heat.
     * @param moderation       How well this material moderates radiation. This is a divisor; should not be below 1.
     * @param heatConductivity How well this material conducts heat, in FE/t/m2.
     */
    public static void registerSolid(final ITag.INamedTag<Block> tag, final float absorption, final float heatEfficiency,
                                     final float moderation, final float heatConductivity) {

        Preconditions.checkNotNull(tag);

        InternalDispatcher.dispatch("moderator-s-register", () -> {

            final Optional<Moderator> entry = getFromSolid(tag);

            if (entry.isPresent()) {

                final Moderator moderator = entry.get();

                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Overriding existing radiation moderator for {}", tag.getName());

                moderator.setAbsorption(absorption);
                moderator.setHeatEfficiency(heatEfficiency);
                moderator.setModeration(moderation);
                moderator.setHeatConductivity(heatConductivity);

            } else {

                s_moderatorBlocksData.put(tag.getName(), new Moderator(absorption, heatEfficiency, moderation, heatConductivity));
                s_moderatorBlocksTags.addTag(tag);
            }
        });
    }

    /**
     * Register a block Tag (using it's Id) as a radiation moderator for the Reactor
     * All blocks that match this Tag will be permissible
     * <p>
     * If the Tag is already registered, the provided values will replace the existing ones
     *
     * @param id               The Id of the block Tag in the form modid:path
     * @param absorption       How much radiation this material absorbs and converts to heat. 0.0 = none, 1.0 = all.
     * @param heatEfficiency   How efficiently radiation is converted to heat. 0 = no heat, 1 = all heat.
     * @param moderation       How well this material moderates radiation. This is a divisor; should not be below 1.
     * @param heatConductivity How well this material conducts heat, in FE/t/m2.
     */
    public static void registerSolid(final String id, final float absorption,
                                     final float heatEfficiency, final float moderation, final float heatConductivity) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(id));
//        registerSolid(BlockTags.createOptional(new ResourceLocation(id)), absorption, heatEfficiency, moderation, heatConductivity);
        registerSolid(TagsHelper.BLOCKS.createOptionalTag(id), absorption, heatEfficiency, moderation, heatConductivity);
    }

//    /**
//     * Register a fluid Tag as a radiation moderator for the Reactor.
//     * All fluids that match this Tag will be permissible
//     * <p>
//     * If the Tag is already registered, the provided values will replace the existing ones
//     *
//     * @param tag              The fluid Tag
//     * @param absorption       How much radiation this material absorbs and converts to heat. 0.0 = none, 1.0 = all.
//     * @param heatEfficiency   How efficiently radiation is converted to heat. 0 = no heat, 1 = all heat.
//     * @param moderation       How well this material moderates radiation. This is a divisor; should not be below 1.
//     * @param heatConductivity How well this material conducts heat, in FE/t/m2.
//     */
//    public static void registerFluid(final ITag.INamedTag<Fluid> tag, final float absorption, final float heatEfficiency,
//                                     final float moderation, final float heatConductivity) {
//
//        Preconditions.checkNotNull(tag);
//
//        InternalDispatcher.dispatch("moderator-f-register", () -> {
//
//            final Optional<Moderator> entry = getFromFluid(tag);
//
//            if (entry.isPresent()) {
//
//                final Moderator moderator = entry.get();
//
//                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Overriding existing radiation moderator for {}", tag.getName());
//
//                moderator.setAbsorption(absorption);
//                moderator.setHeatEfficiency(heatEfficiency);
//                moderator.setModeration(moderation);
//                moderator.setHeatConductivity(heatConductivity);
//
//            } else {
//
//                s_moderatorFluidsData.put(tag.getName(), new Moderator(absorption, heatEfficiency, moderation, heatConductivity));
//                s_moderatorFluidssTags.addTag(tag);
//            }
//        });
//    }

    /**
     * Remove a previously registered radiation moderator block for the Reactor
     * If the moderator is not registered the operation will fail silently
     *
     * @param tag The block Tag
     */
    public static void removeSolid(final ITag.INamedTag<Block> tag) {

        Preconditions.checkNotNull(tag);
        removeSolid(tag.getName());
    }

//    /**
//     * Remove a previously registered radiation moderator block for the Reactor
//     * If the moderator is not registered the operation will fail silently
//     *
//     * @param tag The fluid Tag
//     */
//    public static void removeFluid(final ITag.INamedTag<Fluid> tag) {
//
//        Preconditions.checkNotNull(tag);
//        removeFluid(tag.getName());
//    }

    /**
     * Remove a previously registered radiation moderator block for the Reactor
     * If the moderator is not registered the operation will fail silently
     *
     * @param id The id of the block Tag
     */
    public static void removeSolid(final ResourceLocation id) {

        Preconditions.checkNotNull(id);

        InternalDispatcher.dispatch("moderator-s-remove", () -> {

            s_moderatorBlocksTags.removeTag(id);
            s_moderatorBlocksData.remove(id);
        });
    }

//    /**
//     * Remove a previously registered radiation moderator block for the Reactor
//     * If the moderator is not registered the operation will fail silently
//     *
//     * @param id The id of the block Tag
//     */
//    public static void removeFluid(final ResourceLocation id) {
//
//        Preconditions.checkNotNull(id);
//
//        InternalDispatcher.dispatch("moderator-f-remove", () -> {
//
//            s_moderatorFluidssTags.removeTag(id);
//            s_moderatorFluidsData.remove(id);
//        });
//    }

    //region internals

    private static TagList<Block> s_moderatorBlocksTags;
    private static Map<ResourceLocation, Moderator> s_moderatorBlocksData;
    private static TagList<Fluid> s_moderatorFluidssTags;
    private static Map<ResourceLocation, Moderator> s_moderatorFluidsData;

    private static final Marker MARKER = MarkerManager.getMarker("API/ModeratorsRegistry").addParents(ExtremeReactorsAPI.MARKER);

    private ModeratorsRegistry() {
    }

    static {

        s_moderatorBlocksTags = new TagList<>(CollectionProviders.BLOCKS_PROVIDER);
        s_moderatorBlocksData = Maps.newHashMap();

        s_moderatorFluidssTags = new TagList<>(CollectionProviders.FLUIDS_PROVIDER);
        s_moderatorFluidsData = Maps.newHashMap();

        // register Air and Water moderators

        //TODO re-valutate
        //TODO fluids
        s_moderatorBlocksData.put(ZeroCore.newID("air"), Moderator.AIR);
        s_moderatorBlocksData.put(ZeroCore.newID("water"), Moderator.WATER);

//        ITag<Block> tag;
//
//        tag = TagsHelper.BLOCKS.createTag(id)//
//        tag = Tags.Blocks.WA
//
//        s_moderatorBlocksData.put(tag.getName(), new Moderator(absorption, heatEfficiency, moderation, heatConductivity));
//        s_moderatorBlocksTags.addTag(tag);
    }

    //endregion
}
