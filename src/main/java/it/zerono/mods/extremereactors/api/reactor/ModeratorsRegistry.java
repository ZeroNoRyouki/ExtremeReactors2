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
import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.extremereactors.api.ExtremeReactorsAPI;
import it.zerono.mods.extremereactors.api.internal.InternalDispatcher;
import it.zerono.mods.extremereactors.api.internal.modpack.wrapper.AddRemoveSection;
import it.zerono.mods.extremereactors.api.internal.modpack.wrapper.ApiWrapper;
import it.zerono.mods.zerocore.lib.tag.CollectionProviders;
import it.zerono.mods.zerocore.lib.tag.TagList;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.Item;
import net.minecraft.tags.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.*;
import java.util.function.Consumer;

/**
 * Keep track of all the Moderators that could be used inside a Reactor
 */
@SuppressWarnings({"WeakerAccess"})
@Mod.EventBusSubscriber(modid = ExtremeReactorsAPI.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
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

        if (!fs.isEmpty() && s_moderatorFluidsData.containsKey(getFluidId(fs.getType()))) {
            return Optional.of(s_moderatorFluidsData.get(getFluidId(fs.getType())));
        }

        return getFromSolid(state.getBlock());
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

        if (!fs.isEmpty() && s_moderatorFluidsData.containsKey(getFluidId(fs.getType()))) {
            return true;
        }

        //noinspection rawtypes
        return s_moderatorBlocksTags
                .find(tag -> tag.contains(state.getBlock()))
                .filter(t -> t instanceof Tag.Named)
                .map(t -> (Tag.Named)t)
                .map(t -> s_moderatorBlocksData.containsKey(t.getName()))
                .orElse(false);
    }

    /**
     * Retrieve the radiation moderation data for the given block state
     *
     * @param state The block state
     * @return The Moderator or null if nothing could be found
     */
    @Deprecated // use getFrom()
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
                .filter(t -> t instanceof Tag.Named)
                .map(t -> (Tag.Named)t)
                .flatMap(ModeratorsRegistry::getFromSolid);
    }

    /**
     * Retrieve the radiation moderation data for the given block Tag
     *
     * @param tag The Block Tag
     * @return The Moderator or null if nothing could be found
     */
    public static Optional<Moderator> getFromSolid(final Tag.Named<Block> tag) {
        return Optional.ofNullable(s_moderatorBlocksData.get(tag.getName()));
    }

    /**
     * Retrieve the radiation moderation data for the given block Tag
     *
     * @param tag The Fluid Tag
     * @return The Moderator or null if nothing could be found
     */
    @Deprecated // use getFrom()
    public static Optional<Moderator> getFromFluid(final Tag.Named<Fluid> tag) {
        return Optional.empty();
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

        InternalDispatcher.dispatch("moderator-s-register", () -> {

            final ResourceLocation id = new ResourceLocation(tagId);

            if (s_moderatorBlocksData.containsKey(id)) {
                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Overriding existing radiation moderator for {}", id);
            }

            final Moderator m = new Moderator(absorption, heatEfficiency, moderation, heatConductivity);

            s_moderatorBlocksData.merge(id, m, (o, n) -> m);
        });
    }

    /**
     * Register a Fluid as a radiation moderator for the Reactor
     * <p>
     * If the Fluid is already registered, the provided values will replace the existing ones
     *
     * @param fluid            The Fluid
     * @param absorption       How much radiation this material absorbs and converts to heat. 0.0 = none, 1.0 = all.
     * @param heatEfficiency   How efficiently radiation is converted to heat. 0 = no heat, 1 = all heat.
     * @param moderation       How well this material moderates radiation. This is a divisor; should not be below 1.
     * @param heatConductivity How well this material conducts heat, in FE/t/m2.
     */
    public static void registerFluid(final Fluid fluid, final float absorption, final float heatEfficiency,
                                     final float moderation, final float heatConductivity) {
        registerFluid(getFluidIdString(fluid), absorption, heatEfficiency, moderation, heatConductivity);
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
     * @param tag The block Tag
     */
    public static void removeSolid(final Tag.Named<Block> tag) {

        Preconditions.checkNotNull(tag);
        removeSolid(tag.getName());
    }

    /**
     * Remove a previously registered radiation moderator block for the Reactor
     * If the moderator is not registered the operation will fail silently
     *
     * @param id The id of the block Tag
     */
    public static void removeSolid(final ResourceLocation id) {

        Preconditions.checkNotNull(id);
        InternalDispatcher.dispatch("moderator-s-remove", () -> s_moderatorBlocksData.remove(id));
    }

    /**
     * Remove a previously registered radiation moderator Fluid for the Reactor
     * If the moderator is not registered the operation will fail silently
     *
     * @param fluid The Fluid to remove
     */
    public static void removeFluid(final Fluid fluid) {

        Preconditions.checkNotNull(fluid);
        removeFluid(getFluidId(fluid));
    }

    /**
     * Remove a previously registered radiation moderator Fluid for the Reactor
     * If the moderator is not registered the operation will fail silently
     *
     * @param id The Id of the Fluid in the form modid:path (ie, it's registration ResourceLocation)
     */
    public static void removeFluid(final ResourceLocation id) {

        Preconditions.checkNotNull(id);
        InternalDispatcher.dispatch("moderator-f-remove", () -> s_moderatorFluidsData.remove(id));
    }

    public static void fillModeratorsTooltips(final Map<Item, Set<Component>> tooltipsMap,
                                              final NonNullSupplier<Set<Component>> setSupplier) {

        s_moderatorBlocksTags.tagStream()
                .flatMap(blockTag -> blockTag.getValues().stream())
                .map(Block::asItem)
                .forEach(item -> tooltipsMap.computeIfAbsent(item, k -> setSupplier.get()).add(TOOLTIP_MODERATOR));

        s_moderatorFluidsData.keySet().stream()
                .filter(ForgeRegistries.FLUIDS::containsKey)
                .map(ForgeRegistries.FLUIDS::getValue)
                .filter(Objects::nonNull)
                .map(Fluid::getBucket)
                .forEach(item -> tooltipsMap.computeIfAbsent(item, k -> setSupplier.get()).add(TOOLTIP_MODERATOR));
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onVanillaTagsUpdated(final TagsUpdatedEvent.VanillaTagTypes event) {
        updateTags(s_moderatorBlocksData.keySet(), s_moderatorBlocksTags, TagsHelper.BLOCKS);
    }

    public static void processWrapper(final ApiWrapper wrapper) {

        if (!wrapper.Enabled) {
            return;
        }

        processWrapper("solid", wrapper.ReactorSolidModerators, s_moderatorBlocksData,
                name -> removeSolid(new ResourceLocation(name)),
                (it.zerono.mods.extremereactors.api.internal.modpack.wrapper.Moderator w) ->
                        registerSolid(w.TagId, w.Absorption, w.HeatEfficiency, w.Moderation, w.HeatConductivity));

        processWrapper("fluid", wrapper.ReactorFluidModerators, s_moderatorFluidsData,
                name -> removeFluid(new ResourceLocation(name)),
                (it.zerono.mods.extremereactors.api.internal.modpack.wrapper.Moderator w) ->
                        registerFluid(w.TagId, w.Absorption, w.HeatEfficiency, w.Moderation, w.HeatConductivity));
    }

    //region internals

    private static final TagList<Block> s_moderatorBlocksTags;
    private static final Map<ResourceLocation, Moderator> s_moderatorBlocksData;
    private static final Map<ResourceLocation, Moderator> s_moderatorFluidsData;

    private static final Marker MARKER = MarkerManager.getMarker("API/ModeratorsRegistry").addParents(ExtremeReactorsAPI.MARKER);
    private static final Marker WRAPPER = MarkerManager.getMarker("ModPack API Wrapper").addParents(MARKER);

    private ModeratorsRegistry() {
    }

    private static <T> void updateTags(final Set<ResourceLocation> ids, final TagList<T> tagList, final TagsHelper<T> helper) {

        tagList.clear();
        ids.stream()
                .filter(helper::tagExist)
                .map(helper::createTag)
                .forEach(tagList::addTag);
    }

    private static <X> void processWrapper(final String objectName,
                                           final AddRemoveSection<it.zerono.mods.extremereactors.api.internal.modpack.wrapper.Moderator> wrapperSection,
                                           final Map<ResourceLocation, Moderator> registry, final Consumer<String> removeAction,
                                           final Consumer<it.zerono.mods.extremereactors.api.internal.modpack.wrapper.Moderator> addAction) {

        if (wrapperSection.WipeExistingValuesBeforeAdding) {

            // wipe all

            Log.LOGGER.info(WRAPPER, "Wiping all existing {} Reactor Moderators", objectName);
            registry.clear();

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

    private static ResourceLocation getFluidId(final Fluid fluid) {
        return Objects.requireNonNull(fluid.getRegistryName());
    }

    private static String getFluidIdString(final Fluid fluid) {
        return getFluidId(fluid).toString();
    }

    private static final Component TOOLTIP_MODERATOR = new TranslatableComponent("api.bigreactors.reactor.tooltip.moderator").setStyle(ExtremeReactorsAPI.STYLE_TOOLTIP);

    static {

        s_moderatorBlocksTags = new TagList<>(CollectionProviders.BLOCKS_PROVIDER);
        s_moderatorBlocksData = Maps.newHashMap();
        s_moderatorFluidsData = Maps.newHashMap();
    }

    //endregion
}
