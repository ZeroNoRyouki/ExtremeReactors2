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
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class FluidMappingsRegistry {

    /**
     * @return true if there is a Coolant for the given FluidStack
     */
    public static boolean hasCoolantFrom(final FluidStack stack) {
        return getCoolantFrom(stack).isPresent();
    }

    /**
     * @return true if there is a Coolant for the given Fluid
     */
    public static boolean hasCoolantFrom(final Fluid stack) {
        return getCoolantFrom(stack).isPresent();
    }

    /**
     * @return true if there is a Vapor for the given FluidStack
     */
    public static boolean hasVaporFrom(final FluidStack stack) {
        return getVaporFrom(stack).isPresent();
    }

    /**
     * @return true if there is a Vapor for the given Fluid
     */
    public static boolean hasVaporFrom(final Fluid stack) {
        return getVaporFrom(stack).isPresent();
    }

    /**
     * Get the ITag<Fluid> to Coolant mapping for the given FluidStack (if one exists)
     *
     * @param stack The FluidStack
     * @return The mapping, if one is found and the provided stack is not empty
     */
    public static Optional<IMapping<ITag.INamedTag<Fluid>, Coolant>> getCoolantFrom(final FluidStack stack) {
        return getFrom(s_fluidToCoolant, stack);
    }

    /**
     * Get the ITag<Fluid> to Coolant mapping for the given Fluid (if one exists)
     *
     * @param fluid The Fluid
     * @return The mapping, if one is found
     */
    public static Optional<IMapping<ITag.INamedTag<Fluid>, Coolant>> getCoolantFrom(final Fluid fluid) {
        return getFrom(s_fluidToCoolant, fluid);
    }

    /**
     * Get the ITag<Fluid> to Vapor mapping for the given FluidStack (if one exists)
     *
     * @param stack The FluidStack
     * @return The mapping, if one is found and the provided stack is not empty
     */
    public static Optional<IMapping<ITag.INamedTag<Fluid>, Vapor>> getVaporFrom(final FluidStack stack) {
        return getFrom(s_fluidToVapor, stack);
    }

    /**
     * Get the ITag<Fluid> to Vapor mapping for the given Fluid (if one exists)
     *
     * @param fluid The Fluid
     * @return The mapping, if one is found
     */
    public static Optional<IMapping<ITag.INamedTag<Fluid>, Vapor>> getVaporFrom(final Fluid fluid) {
        return getFrom(s_fluidToVapor, fluid);
    }

    /**
     * Get a list of Coolant to ITag<Fluid> mappings for the given Coolant.
     *
     * @param coolant The Coolant
     * @return A list of Coolant to ITag<Fluid> mappings, if one is found. Note that Coolant is the source and the ITag<Fluid> is the product of the mapping
     */
    public static Optional<List<IMapping<Coolant, ITag.INamedTag<Fluid>>>> getFluidFrom(final Coolant coolant) {
        return Optional.ofNullable(s_coolantToFluid.get(coolant));
    }

    /**
     * Get a list of Vapor to ITag<Fluid> mappings for the given Vapor.
     *
     * @param vapor The Vapor
     * @return A list of Vapor to ITag<Fluid> mappings, if one is found. Note that Vapor is the source and the ITag<Fluid> is the product of the mapping
     */
    public static Optional<List<IMapping<Vapor, ITag.INamedTag<Fluid>>>> getFluidFrom(final Vapor vapor) {
        return Optional.ofNullable(s_vaporToFluid.get(vapor));
    }

    /**
     * Register an Fluid Tag as a valid Coolant source.
     *
     * @param name The name of the Coolant produced by the source.
     * @param quantity The quantity of the Coolant produced for every unit of source (must be >= 0).
     * @param source The source for the Coolant.
     */
    public static void registerCoolantMapping(final String name, final int quantity, final ITag.INamedTag<Fluid> source) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));

        InternalDispatcher.dispatch("fluid-mapping-register", () -> {

            int qty = quantity;

            if (qty < 0) {

                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Using default quantity for Coolant {} instead of the provided, invalid, one: {}", name, qty);
                qty = 1;
            }

            final Optional<Coolant> coolant = FluidsRegistry.getCoolant(name);

            if (coolant.isPresent()) {

                final IMapping<ITag.INamedTag<Fluid>, Coolant> mapping = IMapping.of(source, 1, coolant.get(), qty);

                s_fluidToCoolant.put(mapping.getSource(), mapping);
                s_coolantToFluid.computeIfAbsent(mapping.getProduct(), k -> Lists.newArrayList()).add(mapping.getReverse());

            } else {

                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Skipping registration for an unknown source Coolant: {}", name);
            }
        });
    }

    /**
     * Remove a Fluid Tag as a valid Coolant source
     *
     * @param sourceFluidTag the Fluid Tag to remove
     */
    public static void removeCoolantMapping(final ITag.INamedTag<Fluid> sourceFluidTag) {
        removeSourceMapping(sourceFluidTag.getName(), s_fluidToCoolant, s_coolantToFluid);
    }

    /**
     * Remove a Fluid Tag as a valid Coolant source
     *
     * @param sourceFluidTagId the id of the Fluid Tag to remove
     */
    public static void removeCoolantMapping(final String sourceFluidTagId) {
        removeSourceMapping(new ResourceLocation(sourceFluidTagId), s_fluidToCoolant, s_coolantToFluid);
    }

    /**
     * Register an Fluid Tag as a valid Vapor source.
     *
     * @param name The name of the Vapor produced by the source.
     * @param quantity The quantity of the Vapor produced for every unit of source (must be >= 0).
     * @param source The source for the Vapor.
     */
    public static void registerVaporMapping(final String name, final int quantity, final ITag.INamedTag<Fluid> source) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));

        InternalDispatcher.dispatch("fluid-mapping-register", () -> {

            int qty = quantity;

            if (qty < 0) {

                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Using default quantity for Vapor {} instead of the provided, invalid, one: {}", name, qty);
                qty = 1;
            }

            final Optional<Vapor> entry = FluidsRegistry.getVapor(name);

            if (entry.isPresent()) {

                final IMapping<ITag.INamedTag<Fluid>, Vapor> mapping = IMapping.of(source, 1, entry.get(), qty);

                s_fluidToVapor.put(mapping.getSource(), mapping);
                s_vaporToFluid.computeIfAbsent(mapping.getProduct(), k -> Lists.newArrayList()).add(mapping.getReverse());

            } else {

                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Skipping registration for an unknown source Vapor: {}", name);
            }
        });
    }

    /**
     * Remove a Fluid Tag as a valid Vapor source
     *
     * @param sourceFluidTag the Fluid Tag to remove
     */
    public static void removeVaporMapping(final ITag.INamedTag<Fluid> sourceFluidTag) {
        removeSourceMapping(sourceFluidTag.getName(), s_fluidToVapor, s_vaporToFluid);
    }

    /**
     * Remove a Fluid Tag as a valid Vapor source
     *
     * @param sourceFluidTagId the id of the Fluid Tag to remove
     */
    public static void removeVaporMapping(final String sourceFluidTagId) {
        removeSourceMapping(new ResourceLocation(sourceFluidTagId), s_fluidToVapor, s_vaporToFluid);
    }

    public static void processWrapper(final ApiWrapper wrapper) {

        if (!wrapper.Enabled) {
            return;
        }

        processWrapper("Coolants", wrapper.CoolantSources, s_fluidToCoolant, s_coolantToFluid,
                FluidMappingsRegistry::removeCoolantMapping,
                (source -> registerCoolantMapping(source.ProductName, source.ProductQuantity, TagsHelper.FLUIDS.createOptionalTag(source.SourceTagId))));

        processWrapper("Vapors", wrapper.VaporSources, s_fluidToVapor, s_vaporToFluid,
                FluidMappingsRegistry::removeVaporMapping,
                (source -> registerVaporMapping(source.ProductName, source.ProductQuantity, TagsHelper.FLUIDS.createOptionalTag(source.SourceTagId))));
    }

    //region internals

    private FluidMappingsRegistry() {
    }

    private static <T> Optional<IMapping<ITag.INamedTag<Fluid>, T>> getFrom(final Map<ITag.INamedTag<Fluid>, IMapping<ITag.INamedTag<Fluid>, T>> map,
                                                                            final FluidStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }

        return getFrom(map, stack.getFluid());
    }

    private static <T> Optional<IMapping<ITag.INamedTag<Fluid>, T>> getFrom(final Map<ITag.INamedTag<Fluid>, IMapping<ITag.INamedTag<Fluid>, T>> map,
                                                                            final Fluid fluid) {
        return map.entrySet().stream()
                .filter(entry -> entry.getKey().contains(fluid))
                .map(Map.Entry::getValue)
                .findAny();
    }

    private static <X> void removeSourceMapping(final ResourceLocation sourceFluidTagId,
                                                final Map<ITag.INamedTag<Fluid>, IMapping<ITag.INamedTag<Fluid>, X>> fluidToX,
                                                final Map<X, List<IMapping<X, ITag.INamedTag<Fluid>>>> xToFluid) {

        Preconditions.checkNotNull(sourceFluidTagId);
        Preconditions.checkNotNull(fluidToX);
        Preconditions.checkNotNull(xToFluid);

        InternalDispatcher.dispatch("fluid-mapping-remove", () -> {

            final Optional<ITag.INamedTag<Fluid>> fluidTag = fluidToX.keySet().stream()
                    .filter(tag -> tag.getName().equals(sourceFluidTagId))
                    .findFirst();

            fluidTag.ifPresent(tag -> {

                final IMapping<ITag.INamedTag<Fluid>, X> removedMapping = fluidToX.remove(tag);

                if (null != removedMapping) {

                    xToFluid.getOrDefault(removedMapping.getProduct(), Collections.emptyList())
                            .removeIf(xToTagMapping -> xToTagMapping.getProduct().equals(tag));

                    xToFluid.entrySet().stream()
                            .filter(entry -> entry.getValue().isEmpty())
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toSet())
                            .forEach(xToFluid::remove);
                }
            });
        });
    }

    private static <X> void processWrapper(final String objectName, final AddRemoveSection<SourceTag> wrapperSection,
                                           final Map<ITag.INamedTag<Fluid>, IMapping<ITag.INamedTag<Fluid>, X>> fluidToX,
                                           final Map<X, List<IMapping<X, ITag.INamedTag<Fluid>>>> xToFluid,
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
    private static final Map<ITag.INamedTag<Fluid>, IMapping<ITag.INamedTag<Fluid>, Coolant>> s_fluidToCoolant = new Object2ObjectArrayMap<>(2);
    // - fluid source -> Fluid Tag to Vapor mapping
    private static final Map<ITag.INamedTag<Fluid>, IMapping<ITag.INamedTag<Fluid>, Vapor>> s_fluidToVapor = new Object2ObjectArrayMap<>(2);

    // 1:many mappings

    // - Coolant -> a list of Coolant to Fluid Tag mappings
    private static final Map<Coolant, List<IMapping<Coolant, ITag.INamedTag<Fluid>>>> s_coolantToFluid = new Object2ObjectArrayMap<>(2);
    // - Vapor -> a list of Vapor to Fluid Tag mappings
    private static final Map<Vapor, List<IMapping<Vapor, ITag.INamedTag<Fluid>>>> s_vaporToFluid = new Object2ObjectArrayMap<>(2);

    private static final Marker MARKER = MarkerManager.getMarker("API/FluidMappingsRegistry").addParents(ExtremeReactorsAPI.MARKER);
    private static final Marker WRAPPER = MarkerManager.getMarker("ModPack API Wrapper").addParents(MARKER);

    //endregion
}
