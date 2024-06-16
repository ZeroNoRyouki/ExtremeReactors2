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
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.extremereactors.api.ExtremeReactorsAPI;
import it.zerono.mods.extremereactors.api.internal.InternalDispatcher;
import it.zerono.mods.extremereactors.api.internal.modpack.wrapper.AddRemoveSection;
import it.zerono.mods.extremereactors.api.internal.modpack.wrapper.ApiWrapper;
import it.zerono.mods.zerocore.lib.fluid.FluidHelper;
import it.zerono.mods.zerocore.lib.tag.TagList;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Keep track of all the Moderators that could be used inside a Reactor
 */
@SuppressWarnings({"WeakerAccess"})
public final class ModeratorsRegistry {

    /**
     * Retrieve the (solid or fluid) radiation moderation data for the given block state
     *
     * @param state The block state
     * @return The Moderator or null if nothing could be found
     */
    public static Optional<Moderator> getFrom(final BlockState state) {

        if (state.isAir()) {
            return Optional.of(Moderator.AIR);
        }

        final FluidState fs = state.getFluidState();

        if (!fs.isEmpty() && s_moderatorFluidsData.containsKey(FluidHelper.getFluidId(fs.getType()))) {
            return Optional.of(s_moderatorFluidsData.get(FluidHelper.getFluidId(fs.getType())));
        }

        final List<TagKey<Block>> tags = TagsHelper.BLOCKS.getTags(state.getBlock());

        return s_moderatorBlocksTags
                .findFirst(tags::contains)
                .map(s_moderatorBlocksData::get);
    }

    /**
     * Check if a valid (solid or fluid) radiation moderation exist for the given block state
     *
     * @param state The block state
     * @return True if a Moderator exists, false otherwise
     */
    public static boolean isValid(final BlockState state) {

        if (state.isAir()) {
            return true;
        }

        final FluidState fs = state.getFluidState();

        if (!fs.isEmpty() && s_moderatorFluidsData.containsKey(FluidHelper.getFluidId(fs.getType()))) {
            return true;
        }

        return s_moderatorBlocksTags.match(state.getBlock().builtInRegistryHolder()::is);
    }

    /**
     * Register a block Tag as a radiation moderator for the Reactor
     * All blocks that match this Tag will be permissible
     * <p>
     * If the block Tag is already registered, the provided values will replace the existing ones
     *
     * @param tagId            The Id of the block Tag in the form modid:path
     * @param absorption       How much radiation this material absorbs and converts to heat. 0.0 = none, 1.0 = all.
     * @param heatEfficiency   How efficiently radiation is converted to heat. 0 = no heat, 1 = all heat.
     * @param moderation       How well this material moderates radiation. This is a divisor; should not be below 1.
     * @param heatConductivity How well this material conducts heat, in FE/t/m2.
     */
    public static void registerSolid(final String tagId, final float absorption, final float heatEfficiency,
                                     final float moderation, final float heatConductivity) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(tagId));
        registerSolid(TagsHelper.BLOCKS.createKey(tagId), absorption, heatEfficiency, moderation, heatConductivity);
    }

    /**
     * Register a block Tag as a radiation moderator for the Reactor
     * All blocks that match this Tag will be permissible
     * <p>
     * If the block Tag is already registered, the provided values will replace the existing ones
     *
     * @param tag              The block Tag key
     * @param absorption       How much radiation this material absorbs and converts to heat. 0.0 = none, 1.0 = all.
     * @param heatEfficiency   How efficiently radiation is converted to heat. 0 = no heat, 1 = all heat.
     * @param moderation       How well this material moderates radiation. This is a divisor; should not be below 1.
     * @param heatConductivity How well this material conducts heat, in FE/t/m2.
     */
    public static void registerSolid(final TagKey<Block> tag, final float absorption, final float heatEfficiency,
                                     final float moderation, final float heatConductivity) {

        Preconditions.checkNotNull(tag);
        InternalDispatcher.dispatch("moderator-s-register", () -> {

            if (s_moderatorBlocksData.containsKey(tag)) {
                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Overriding existing radiation moderator for {}", tag);
            }

            final Moderator m = new Moderator(absorption, heatEfficiency, moderation, heatConductivity);

            s_moderatorBlocksData.merge(tag, m, (o, n) -> m);
            s_moderatorBlocksTags.addTag(tag);
        });
    }

    /**
     * Register a Fluid as a radiation moderator for the Reactor
     * <p>
     * If the Fluid is already registered, the provided values will replace the existing ones
     *
     * @param fluidId          The Id of the Fluid in the form modid:path (ie, it's registration ResourceLocation)
     * @param absorption       How much radiation this material absorbs and converts to heat. 0.0 = none, 1.0 = all.
     * @param heatEfficiency   How efficiently radiation is converted to heat. 0 = no heat, 1 = all heat.
     * @param moderation       How well this material moderates radiation. This is a divisor; should not be below 1.
     * @param heatConductivity How well this material conducts heat, in FE/t/m2.
     */
    public static void registerFluid(final String fluidId, final float absorption, final float heatEfficiency,
                                     final float moderation, final float heatConductivity) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(fluidId));
        InternalDispatcher.dispatch("moderator-f-register", () -> {

            final ResourceLocation id = new ResourceLocation(fluidId);

            if (s_moderatorFluidsData.containsKey(id)) {
                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Overriding existing radiation moderator for {}", id);
            }

            final Moderator m = new Moderator(absorption, heatEfficiency, moderation, heatConductivity);

            s_moderatorFluidsData.merge(id, m, (o, n) -> m);
        });
    }

    /**
     * Remove a previously registered radiation moderator block for the Reactor
     * If the moderator is not registered the operation will fail silently
     *
     * @param tag The block Tag key
     */
    public static void removeSolid(final TagKey<Block> tag) {

        Preconditions.checkNotNull(tag);
        InternalDispatcher.dispatch("moderator-s-remove", () -> {

            s_moderatorBlocksData.remove(tag);
            s_moderatorBlocksTags.removeTag(tag);
        });
    }

    /**
     * Remove a previously registered radiation moderator block for the Reactor
     * If the moderator is not registered the operation will fail silently
     *
     * @param tagId The Id of the block Tag in the form modid:path
     */
    public static void removeSolid(final String tagId) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(tagId));
        removeSolid(TagsHelper.BLOCKS.createKey(tagId));
    }

    /**
     * Remove a previously registered radiation moderator Fluid for the Reactor
     * If the moderator is not registered the operation will fail silently
     *
     * @param fluidId The Id of the Fluid in the form modid:path (ie, it's registration ResourceLocation)
     */
    public static void removeFluid(final String fluidId) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(fluidId));
        InternalDispatcher.dispatch("moderator-f-remove", () -> s_moderatorFluidsData.remove(new ResourceLocation(fluidId)));
    }

    public static void fillModeratorsTooltips(final Map<Item, Set<Component>> tooltipsMap,
                                              final Supplier<@NotNull Set<Component>> setSupplier) {

        s_moderatorBlocksTags.stream()
                .map(TagsHelper.BLOCKS::getObjects)
                .flatMap(List::stream)
                .map(Block::asItem)
                .forEach(item -> tooltipsMap.computeIfAbsent(item, k -> setSupplier.get()).add(TOOLTIP_MODERATOR));

        s_moderatorFluidsData.keySet().stream()
                .filter(BuiltInRegistries.FLUID::containsKey)
                .map(BuiltInRegistries.FLUID::get)
                .filter(Objects::nonNull)
                .map(Fluid::getBucket)
                .forEach(item -> tooltipsMap.computeIfAbsent(item, k -> setSupplier.get()).add(TOOLTIP_MODERATOR));
    }

    public static void processWrapper(final ApiWrapper wrapper) {

        if (!wrapper.Enabled) {
            return;
        }

        processWrapper("solid", wrapper.ReactorSolidModerators, s_moderatorBlocksData::clear,
                ModeratorsRegistry::removeSolid,
                (it.zerono.mods.extremereactors.api.internal.modpack.wrapper.Moderator w) ->
                        registerSolid(w.TagId, w.Absorption, w.HeatEfficiency, w.Moderation, w.HeatConductivity));

        processWrapper("fluid", wrapper.ReactorFluidModerators, s_moderatorFluidsData::clear,
                ModeratorsRegistry::removeFluid,
                (it.zerono.mods.extremereactors.api.internal.modpack.wrapper.Moderator w) ->
                        registerFluid(w.TagId, w.Absorption, w.HeatEfficiency, w.Moderation, w.HeatConductivity));
    }

    //region internals

    private static final TagList<Block> s_moderatorBlocksTags;
    private static final Map<TagKey<Block>, Moderator> s_moderatorBlocksData;
    private static final Map<ResourceLocation, Moderator> s_moderatorFluidsData;

    private static final Marker MARKER = MarkerManager.getMarker("API/ModeratorsRegistry").addParents(ExtremeReactorsAPI.MARKER);
    private static final Marker WRAPPER = MarkerManager.getMarker("ModPack API Wrapper").addParents(MARKER);

    private ModeratorsRegistry() {
    }

    private static void processWrapper(final String objectName,
                                       final AddRemoveSection<it.zerono.mods.extremereactors.api.internal.modpack.wrapper.Moderator> wrapperSection,
                                       final Runnable clearAction, final Consumer<String> removeAction,
                                       final Consumer<it.zerono.mods.extremereactors.api.internal.modpack.wrapper.Moderator> addAction) {

        if (wrapperSection.WipeExistingValuesBeforeAdding) {

            // wipe all

            Log.LOGGER.info(WRAPPER, "Wiping all existing {} Reactor Moderators", objectName);
            clearAction.run();

        } else {

            // remove from list

            Arrays.stream(wrapperSection.Remove)
                    .filter(name -> !Strings.isNullOrEmpty(name))
                    .forEach(removeAction);
        }

        // add new values

        Arrays.stream(wrapperSection.Add)
                .filter(Objects::nonNull)
                .forEach(addAction);
    }

    private static final Component TOOLTIP_MODERATOR = Component.translatable("api.bigreactors.reactor.tooltip.moderator").setStyle(ExtremeReactorsAPI.STYLE_TOOLTIP);

    static {

        s_moderatorBlocksTags = TagList.blocks();
        s_moderatorBlocksData = new Object2ObjectArrayMap<>(32);
        s_moderatorFluidsData = new Object2ObjectArrayMap<>(32);
    }

    //endregion
}
