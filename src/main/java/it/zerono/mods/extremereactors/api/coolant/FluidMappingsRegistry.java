/*
 *
 * CoolantsMappingsRegistry.java
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

package it.zerono.mods.extremereactors.api.coolant;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.extremereactors.api.ExtremeReactorsAPI;
import it.zerono.mods.extremereactors.api.IMapping;
import it.zerono.mods.extremereactors.api.internal.InternalDispatcher;
import it.zerono.mods.extremereactors.api.internal.modpack.wrapper.AddRemoveSection;
import it.zerono.mods.extremereactors.api.internal.modpack.wrapper.ApiWrapper;
import it.zerono.mods.extremereactors.api.internal.modpack.wrapper.SourceTag;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class FluidMappingsRegistry {

    /**
     * @return true if there is a Coolant for the given Fluid
     */
    public static boolean hasCoolantFrom(final Fluid stack) {
        return getCoolantFrom(stack).isPresent();
    }

    /**
     * @return true if there is a Vapor for the given Fluid
     */
    public static boolean hasVaporFrom(final Fluid stack) {
        return getVaporFrom(stack).isPresent();
    }

    /**
     * Get the Tag<Fluid> to Coolant mapping for the given Fluid (if one exists)
     *
     * @param fluid The Fluid
     * @return The mapping, if one is found
     */
    public static Optional<IMapping<TagKey<Fluid>, Coolant>> getCoolantFrom(final Fluid fluid) {
        return getFrom(s_fluidToCoolant, fluid);
    }

    /**
     * Get the Tag<Fluid> to Vapor mapping for the given Fluid (if one exists)
     *
     * @param fluid The Fluid
     * @return The mapping, if one is found
     */
    public static Optional<IMapping<TagKey<Fluid>, Vapor>> getVaporFrom(final Fluid fluid) {
        return getFrom(s_fluidToVapor, fluid);
    }

    /**
     * Get a list of Coolant to Tag<Fluid> mappings for the given Coolant.
     *
     * @param coolant The Coolant
     * @return A list of Coolant to Tag<Fluid> mappings, if one is found. Note that Coolant is the source and the Tag<Fluid> is the product of the mapping
     */
    public static Optional<List<IMapping<Coolant, TagKey<Fluid>>>> getFluidFrom(final Coolant coolant) {
        return Optional.ofNullable(s_coolantToFluid.get(coolant));
    }

    /**
     * Get a list of Vapor to Tag<Fluid> mappings for the given Vapor.
     *
     * @param vapor The Vapor
     * @return A list of Vapor to Tag<Fluid> mappings, if one is found. Note that Vapor is the source and the Tag<Fluid> is the product of the mapping
     */
    public static Optional<List<IMapping<Vapor, TagKey<Fluid>>>> getFluidFrom(final Vapor vapor) {
        return Optional.ofNullable(s_vaporToFluid.get(vapor));
    }

    /**
     * Register an Fluid Tag as a valid Coolant source.
     *
     * @param name The name of the Coolant produced by the source.
     * @param quantity The quantity of the Coolant produced for every unit of source (must be >= 0).
     * @param sourceFluidTagId The Fuid Tag id of the source for the Coolant.
     */
    public static void registerCoolantMapping(final String name, final int quantity, final String sourceFluidTagId) {
        registerCoolantMapping(name, quantity, TagsHelper.FLUIDS.createKey(sourceFluidTagId));
    }

    /**
     * Register an Fluid Tag as a valid Coolant source.
     *
     * @param name The name of the Coolant produced by the source.
     * @param quantity The quantity of the Coolant produced for every unit of source (must be >= 0).
     * @param sourceFluidTag The Fuid Tag key of the source for the Coolant.
     */
    public static void registerCoolantMapping(final String name, final int quantity, final TagKey<Fluid> sourceFluidTag) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
        Preconditions.checkNotNull(sourceFluidTag);
        InternalDispatcher.dispatch("fluid-mapping-register", () -> {

            final int qty;

            if (quantity < 0) {

                qty = 1;
                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Using default quantity for Coolant {} instead of the provided, invalid, one: {}", name, qty);

            } else {

                qty = quantity;
            }

            FluidsRegistry.getCoolant(name).ifPresentOrElse(coolant -> {

                final IMapping<TagKey<Fluid>, Coolant> mapping = IMapping.of(sourceFluidTag, 1, coolant, qty);

                s_fluidToCoolant.put(mapping.getSource(), mapping);
                s_coolantToFluid.computeIfAbsent(mapping.getProduct(), k -> Lists.newArrayList()).add(mapping.getReverse());

            }, () -> ExtremeReactorsAPI.LOGGER.warn(MARKER, "Skipping registration for an unknown source Coolant: {}", name));
        });
    }

    /**
     * Remove a Fluid Tag as a valid Coolant source
     *
     * @param sourceFluidTag the Fluid Tag to remove
     */
    public static void removeCoolantMapping(final TagKey<Fluid> sourceFluidTag) {
        removeSourceMapping(sourceFluidTag, s_fluidToCoolant, s_coolantToFluid);
    }

    /**
     * Remove a Fluid Tag as a valid Coolant source
     *
     * @param sourceFluidTagId the id of the Fluid Tag to remove
     */
    public static void removeCoolantMapping(final String sourceFluidTagId) {
        removeSourceMapping(sourceFluidTagId, s_fluidToCoolant, s_coolantToFluid);
    }

    /**
     * Register an Fluid Tag as a valid Vapor source.
     *
     * @param name The name of the Vapor produced by the source.
     * @param quantity The quantity of the Vapor produced for every unit of source (must be >= 0).
     * @param sourceFluidTagId The Fuid Tag id of the source for the Vapor.
     */
    public static void registerVaporMapping(final String name, final int quantity, final String sourceFluidTagId) {
        registerVaporMapping(name, quantity, TagsHelper.FLUIDS.createKey(sourceFluidTagId));
    }

    /**
     * Register an Fluid Tag as a valid Vapor source.
     *
     * @param name The name of the Vapor produced by the source.
     * @param quantity The quantity of the Vapor produced for every unit of source (must be >= 0).
     * @param sourceFluidTag The Fuid Tag key of the source for the Vapor.
     */
    public static void registerVaporMapping(final String name, final int quantity, final TagKey<Fluid> sourceFluidTag) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
        Preconditions.checkNotNull(sourceFluidTag);
        InternalDispatcher.dispatch("fluid-mapping-register", () -> {

            final int qty;

            if (quantity < 0) {

                qty = 1;
                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Using default quantity for Vapor {} instead of the provided, invalid, one: {}", name, qty);

            } else {

                qty = quantity;
            }

            FluidsRegistry.getVapor(name).ifPresentOrElse(vapor -> {

                final IMapping<TagKey<Fluid>, Vapor> mapping = IMapping.of(sourceFluidTag, 1, vapor, qty);

                s_fluidToVapor.put(mapping.getSource(), mapping);
                s_vaporToFluid.computeIfAbsent(mapping.getProduct(), k -> Lists.newArrayList()).add(mapping.getReverse());

            }, () -> ExtremeReactorsAPI.LOGGER.warn(MARKER, "Skipping registration for an unknown source Vapor: {}", name));
        });
    }

    /**
     * Remove a Fluid Tag as a valid Vapor source
     *
     * @param sourceFluidTag the Fluid Tag to remove
     */
    public static void removeVaporMapping(final TagKey<Fluid> sourceFluidTag) {
        removeSourceMapping(sourceFluidTag, s_fluidToVapor, s_vaporToFluid);
    }

    /**
     * Remove a Fluid Tag as a valid Vapor source
     *
     * @param sourceFluidTagId the id of the Fluid Tag to remove
     */
    public static void removeVaporMapping(final String sourceFluidTagId) {
        removeSourceMapping(sourceFluidTagId, s_fluidToVapor, s_vaporToFluid);
    }

    public static void processWrapper(final ApiWrapper wrapper) {

        if (!wrapper.Enabled) {
            return;
        }

        processWrapper("Coolants", wrapper.CoolantSources, s_fluidToCoolant, s_coolantToFluid,
                FluidMappingsRegistry::removeCoolantMapping,
                (source -> registerCoolantMapping(source.ProductName, source.ProductQuantity, source.SourceTagId)));

        processWrapper("Vapors", wrapper.VaporSources, s_fluidToVapor, s_vaporToFluid,
                FluidMappingsRegistry::removeVaporMapping,
                (source -> registerVaporMapping(source.ProductName, source.ProductQuantity, source.SourceTagId)));
    }

    //region internals

    private FluidMappingsRegistry() {
    }

    private static <T> Optional<IMapping<TagKey<Fluid>, T>> getFrom(final Map<TagKey<Fluid>, IMapping<TagKey<Fluid>, T>> map,
                                                                    final Fluid fluid) {

        final List<TagKey<Fluid>> tags = TagsHelper.FLUIDS.getTags(fluid);

        return map.entrySet().stream()
                .filter(entry -> tags.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findAny();
    }

    private static <X> void removeSourceMapping(final String sourceFluidTagId,
                                                final Map<TagKey<Fluid>, IMapping<TagKey<Fluid>, X>> fluidToX,
                                                final Map<X, List<IMapping<X, TagKey<Fluid>>>> xToFluid) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(sourceFluidTagId));
        removeSourceMapping(TagsHelper.FLUIDS.createKey(sourceFluidTagId), fluidToX, xToFluid);

    }
    private static <X> void removeSourceMapping(final TagKey<Fluid> sourceFluidTag,
                                                final Map<TagKey<Fluid>, IMapping<TagKey<Fluid>, X>> fluidToX,
                                                final Map<X, List<IMapping<X, TagKey<Fluid>>>> xToFluid) {

        Preconditions.checkNotNull(sourceFluidTag);
        Preconditions.checkNotNull(fluidToX);
        Preconditions.checkNotNull(xToFluid);
        InternalDispatcher.dispatch("fluid-mapping-remove", () -> {

            final IMapping<TagKey<Fluid>, X> removedMapping = fluidToX.remove(sourceFluidTag);

            if (null != removedMapping) {

                xToFluid.getOrDefault(removedMapping.getProduct(), Collections.emptyList())
                        .removeIf(xToTagMapping -> xToTagMapping.getProduct().equals(sourceFluidTag));

                xToFluid.entrySet().stream()
                        .filter(entry -> entry.getValue().isEmpty())
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet())
                        .forEach(xToFluid::remove);
            }
        });
    }

    private static <X> void processWrapper(final String objectName, final AddRemoveSection<SourceTag> wrapperSection,
                                           final Map<TagKey<Fluid>, IMapping<TagKey<Fluid>, X>> fluidToX,
                                           final Map<X, List<IMapping<X, TagKey<Fluid>>>> xToFluid,
                                           final Consumer<String> removeAction, final Consumer<SourceTag> addAction) {

        if (wrapperSection.WipeExistingValuesBeforeAdding) {

            // wipe all

            Log.LOGGER.info(WRAPPER, "Wiping all existing {} sources", objectName);
            fluidToX.clear();
            xToFluid.clear();

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

    // 1:1 mappings

    // - fluid source -> Fluid Tag to Coolant mapping
    private static final Map<TagKey<Fluid>, IMapping<TagKey<Fluid>, Coolant>> s_fluidToCoolant = new Object2ObjectArrayMap<>(4);
    // - fluid source -> Fluid Tag to Vapor mapping
    private static final Map<TagKey<Fluid>, IMapping<TagKey<Fluid>, Vapor>> s_fluidToVapor = new Object2ObjectArrayMap<>(4);

    // 1:many mappings

    // - Coolant -> a list of Coolant to Fluid Tag mappings
    private static final Map<Coolant, List<IMapping<Coolant, TagKey<Fluid>>>> s_coolantToFluid = new Object2ObjectArrayMap<>(4);
    // - Vapor -> a list of Vapor to Fluid Tag mappings
    private static final Map<Vapor, List<IMapping<Vapor, TagKey<Fluid>>>> s_vaporToFluid = new Object2ObjectArrayMap<>(4);

    private static final Marker MARKER = MarkerManager.getMarker("API/FluidMappingsRegistry").addParents(ExtremeReactorsAPI.MARKER);
    private static final Marker WRAPPER = MarkerManager.getMarker("ModPack API Wrapper").addParents(MARKER);

    //endregion
}
