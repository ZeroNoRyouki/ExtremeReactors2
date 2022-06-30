/*
 *
 * ReactantMappingsRegistry.java
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
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.extremereactors.api.ExtremeReactorsAPI;
import it.zerono.mods.extremereactors.api.IMapping;
import it.zerono.mods.extremereactors.api.internal.InternalDispatcher;
import it.zerono.mods.extremereactors.api.internal.modpack.wrapper.AddRemoveSection;
import it.zerono.mods.extremereactors.api.internal.modpack.wrapper.ApiWrapper;
import it.zerono.mods.extremereactors.api.internal.modpack.wrapper.SourceTag;
import it.zerono.mods.zerocore.lib.item.ItemHelper;
import it.zerono.mods.zerocore.lib.tag.TagList;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = ExtremeReactorsAPI.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ReactantMappingsRegistry {

    public static final int STANDARD_SOLID_REACTANT_AMOUNT = 1000; // 1 item = 1000 mB, standard
    public static final int STANDARD_FLUID_REACTANT_AMOUNT = 1; // 1 mB = 1 mB

    /**
     * Get the Source-Product solid mapping for the given ItemStack (if one exists)
     *
     * @param stack The ItemStack
     * @return The Source-Product solid mapping, if one is found and the provided stack is not empty
     */
    public static Optional<IMapping<TagKey<Item>, Reactant>> getFromSolid(final ItemStack stack) {

        if (stack.isEmpty()) {
            return Optional.empty();
        }

        final Item item = stack.getItem();
        final List<TagKey<Item>> tags = TagsHelper.ITEMS.getTags(item);

        return s_solidTags
                .findFirst(tags::contains)
                .map(s_solidToReactant::get);
    }

    /**
     * Get the Source-Product fluid mapping for the given FluidStack (if one exists)
     *
     * @param stack The FluidStack
     * @return The Source-Product fluid mapping, if one is found and the provided stack is not empty
     */
    public static Optional<IMapping<TagKey<Fluid>, Reactant>> getFromFluid(final FluidStack stack) {

        if (stack.isEmpty()) {
            return Optional.empty();
        }

        final Fluid fluid = stack.getFluid();
        final List<TagKey<Fluid>> tags = TagsHelper.FLUIDS.getTags(fluid);

        return s_fluidTags
                .findFirst(tags::contains)
                .map(s_fluidToReactant::get);
    }

    /**
     * Get a list of Source-Product solid mappings for the given Reactant.
     *
     * @param reactant The Reactant
     * @return A list of reactant => Item Tag mappings, if one is found. Note that reactant is the source and Item Tag is the product
     */
    public static Optional<List<IMapping<Reactant, TagKey<Item>>>> getToSolid(final Reactant reactant) {
        return Optional.ofNullable(s_reactantToSolid.get(reactant));
    }

    /**
     * Get a list of Source-Product fluid mappings for the given Reactant.
     *
     * @param reactant The Reactant
     * @return A list of reactant => Fluid Tag mappings, if one is found. Note that reactant is the source and Fluid Tag is the product
     */
    public static Optional<List<IMapping<Reactant, TagKey<Fluid>>>> getToFluid(final Reactant reactant) {
        return Optional.ofNullable(s_reactantToFluid.get(reactant));
    }

    /**
     * Get an ItemStack filled with the given amount of the first available Item from the Item Tag associated with the provided mapping
     *
     * @param mapping the mapping
     * @param amount the amount of items
     * @return an ItemStack that contains the requested item, if any is found, or an empty ItemStack
     */
    public static ItemStack getSolidStackFrom(final IMapping<Reactant, TagKey<Item>> mapping, final int amount) {
        return s_solidTags
                .first(mapping.getProduct())
                .map(item -> ItemHelper.stackFrom(item, amount))
                .orElse(ItemStack.EMPTY);
    }

    /**
     * Get a FluidStack filled with the given amount of the first available Fluid from the Fluid Tag associated with the provided mapping
     *
     * @param mapping the mapping
     * @param amount the amount of fluid
     * @return an FluidStack that contains the requested fluid, if any is found, or an empty FluidStack
     */
    public static FluidStack getFluidStackFrom(final IMapping<Reactant, TagKey<Fluid>> mapping, final int amount) {
        return s_fluidTags
                .first(mapping.getProduct())
                .map(fluid -> new FluidStack(fluid, amount))
                .orElse(FluidStack.EMPTY);
    }

    /**
     * Register an Item Tag id as a valid Reactant source.
     *
     * For fuels, it will allow access ports to accept Items in the inlet slot.
     * For wastes, it will allow access ports to eject Items into the outlet slot.
     *
     * @param reactantName The name of the Reactant produced by the source.
     * @param reactantQty The quantity of the Reactant produced for every unit of source (must be >= 0).
     * @param sourceItemTagId The Item Tag id of the source for the reactant.
     */
    public static void registerSolid(final String reactantName, final int reactantQty, final String sourceItemTagId) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(sourceItemTagId));
        registerSolid(reactantName, reactantQty, TagsHelper.ITEMS.createKey(sourceItemTagId));
    }

    /**
     * Register an Item Tag id as a valid Reactant source.
     *
     * For fuels, it will allow access ports to accept Items in the inlet slot.
     * For wastes, it will allow access ports to eject Items into the outlet slot.
     *
     * @param reactantName The name of the Reactant produced by the source.
     * @param reactantQty The quantity of the Reactant produced for every unit of source (must be >= 0).
     * @param sourceItemTag The Item Tag key of the source for the reactant.
     */
    public static void registerSolid(final String reactantName, final int reactantQty, final TagKey<Item> sourceItemTag) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(reactantName));
        Preconditions.checkNotNull(sourceItemTag);
        InternalDispatcher.dispatch("mapping-register", () -> {

            final int qty;

            if (reactantQty < 0) {

                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Using default quantity for reactant {} instead of the provided, invalid, one: {}", reactantName, reactantQty);
                qty = STANDARD_SOLID_REACTANT_AMOUNT;

            } else {

                qty = reactantQty;
            }

            ReactantsRegistry.get(reactantName).ifPresentOrElse(
                    reactant -> {

                        final IMapping<TagKey<Item>, Reactant> mapping = IMapping.of(sourceItemTag, 1, reactant, qty);

                        s_solidToReactant.put(mapping.getSource(), mapping);
                        s_reactantToSolid.computeIfAbsent(mapping.getProduct(), k -> Lists.newArrayList()).add(mapping.getReverse());
                        s_solidTags.addTag(sourceItemTag);

                    },
                    () -> ExtremeReactorsAPI.LOGGER.warn(MARKER, "Skipping registration for an unknown source reactant: {}", reactantName));
        });
    }

    /**
     * Register a Fluid Tag id as a valid Reactant source.
     *
     * For fuels, it will allow injection ports to accept Fluids in the inlet tank.
     * For wastes, it will allow injection ports to eject Fluids into the outlet tank.
     *
     * @param reactantName The name of the Reactant produced by the source.
     * @param sourceFluidTagId The Fluid Tag id of the source for the reactant.
     */
    public static void registerFluid(final String reactantName, final String sourceFluidTagId) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(sourceFluidTagId));
        registerFluid(reactantName, TagsHelper.FLUIDS.createKey(sourceFluidTagId));
    }

    /**
     * Register a Fluid Tag id as a valid Reactant source.
     *
     * For fuels, it will allow injection ports to accept Fluids in the inlet tank.
     * For wastes, it will allow injection ports to eject Fluids into the outlet tank.
     *
     * @param reactantName The name of the Reactant produced by the source.
     * @param sourceFluidTag The Fluid Tag key of the source for the reactant.
     */
    public static void registerFluid(final String reactantName, final TagKey<Fluid> sourceFluidTag) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(reactantName));
        Preconditions.checkNotNull(sourceFluidTag);
        InternalDispatcher.dispatch("mapping-register", () -> {

            ReactantsRegistry.get(reactantName).ifPresentOrElse(
                    reactant -> {

                        final IMapping<TagKey<Fluid>, Reactant> mapping = IMapping.of(sourceFluidTag, 1, reactant, STANDARD_FLUID_REACTANT_AMOUNT);

                        s_fluidToReactant.put(mapping.getSource(), mapping);
                        s_reactantToFluid.computeIfAbsent(mapping.getProduct(), k -> Lists.newArrayList()).add(mapping.getReverse());
                        s_fluidTags.addTag(sourceFluidTag);

                    },
                    () -> ExtremeReactorsAPI.LOGGER.warn(MARKER, "Skipping registration for an unknown source reactant: {}", reactantName));
        });
    }

    public static void removeSolid(final String sourceItemTagId) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(sourceItemTagId));
        removeSolid(TagsHelper.ITEMS.createKey(sourceItemTagId));
    }

    public static void removeSolid(final TagKey<Item> sourceItemTag) {

        Preconditions.checkNotNull(sourceItemTag);
        InternalDispatcher.dispatch("mapping-remove", () -> {

            final IMapping<TagKey<Item>, Reactant> removedMapping = s_solidToReactant.remove(sourceItemTag);

            if (null != removedMapping) {

                s_reactantToSolid.getOrDefault(removedMapping.getProduct(), Collections.emptyList())
                        .removeIf(reactantToTagMapping -> reactantToTagMapping.getProduct().equals(sourceItemTag));

                s_reactantToSolid.entrySet().stream()
                        .filter(entry -> entry.getValue().isEmpty())
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet())
                        .forEach(s_reactantToSolid::remove);

                s_solidTags.removeTag(sourceItemTag);
            }
        });
    }

    public static void removeFluid(final String sourceFluidTagId) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(sourceFluidTagId));
        removeFluid(TagsHelper.FLUIDS.createKey(sourceFluidTagId));
    }

    public static void removeFluid(final TagKey<Fluid> sourceFluidTag) {

        Preconditions.checkNotNull(sourceFluidTag);
        InternalDispatcher.dispatch("mapping-remove", () -> {

            final IMapping<TagKey<Fluid>, Reactant> removedMapping = s_fluidToReactant.remove(sourceFluidTag);

            if (null != removedMapping) {

                s_reactantToFluid.getOrDefault(removedMapping.getProduct(), Collections.emptyList())
                        .removeIf(reactantToTagMapping -> reactantToTagMapping.getProduct().equals(sourceFluidTag));

                s_reactantToFluid.entrySet().stream()
                        .filter(entry -> entry.getValue().isEmpty())
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet())
                        .forEach(s_reactantToFluid::remove);

                s_fluidTags.removeTag(sourceFluidTag);
            }
        });
    }

    public static void fillReactantsTooltips(final Map<Item, Set<Component>> tooltipsMap,
                                             final NonNullSupplier<Set<Component>> setSupplier) {

        s_solidToReactant.values().stream()
                .filter(mapping -> mapping.getProduct().getType().isFuel())
                .map(IMapping::getSource)
                .forEach(key -> s_solidTags.forEach(key, item -> tooltipsMap.computeIfAbsent(item, k -> setSupplier.get()).add(TOOLTIP_FUEL_SOURCE)));

        s_fluidToReactant.values().stream()
                .filter(mapping -> mapping.getProduct().getType().isFuel())
                .map(IMapping::getSource)
                .forEach(key -> s_fluidTags.forEach(key, fluid -> tooltipsMap.computeIfAbsent(fluid.getBucket(), k -> setSupplier.get()).add(TOOLTIP_FUEL_SOURCE)));
    }

    public static void processWrapper(final ApiWrapper wrapper) {

        if (!wrapper.Enabled) {
            return;
        }

        processWrapper("solid", wrapper.ReactorReactantSources, () -> {
                    s_solidToReactant.clear();
                    s_reactantToSolid.clear();
                },
                ReactantMappingsRegistry::removeSolid,
                (SourceTag w) -> registerSolid(w.ProductName, w.ProductQuantity, w.SourceTagId));

        //TODO fluids
    }

    public static Map<Reactant, List<IMapping<Reactant, TagKey<Item>>>> getToSolidMap() {
        return unmodifiableInverseMap(s_reactantToSolid);
    }

    public static Map<Reactant, List<IMapping<Reactant, TagKey<Fluid>>>> getToFluidMap() {
        return unmodifiableInverseMap(s_reactantToFluid);
    }

    //region internals

    private ReactantMappingsRegistry() {
    }

    private static <X> void processWrapper(final String objectName, final AddRemoveSection<SourceTag> wrapperSection,
                                           final Runnable clearAction,
                                           final Consumer<String> removeAction, final Consumer<SourceTag> addAction) {

        if (wrapperSection.WipeExistingValuesBeforeAdding) {

            // wipe all

            Log.LOGGER.info(WRAPPER, "Wiping all existing {} Reactor Reactant source mappings", objectName);
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

    private static <T> Map<Reactant, List<IMapping<Reactant, TagKey<T>>>> unmodifiableInverseMap(final Map<Reactant, List<IMapping<Reactant, TagKey<T>>>> map) {

        final Object2ObjectMap<Reactant, List<IMapping<Reactant, TagKey<T>>>> copy = new Object2ObjectArrayMap<>(map.size());

        for (final Map.Entry<Reactant, List<IMapping<Reactant, TagKey<T>>>> entry : map.entrySet()) {
            copy.put(entry.getKey(), new ObjectArrayList<>(entry.getValue()));
        }

        return Object2ObjectMaps.unmodifiable(copy);
    }

    // 1:1 mappings
    // - solid source -> Item Tag : reactant name mapping
    private static final Map<TagKey<Item>, IMapping<TagKey<Item>, Reactant>> s_solidToReactant = Maps.newHashMap();
    // - fluid source -> Fluid Tag : reactant name mapping
    private static final Map<TagKey<Fluid>, IMapping<TagKey<Fluid>, Reactant>> s_fluidToReactant = Maps.newHashMap();

    // 1:many mappings
    // - reactant name -> a list of reactant name : Item Tag mappings
    private static final Map<Reactant, List<IMapping<Reactant, TagKey<Item>>>> s_reactantToSolid = Maps.newHashMap();
    // - reactant name -> a list of reactant name : Fluid Tag mappings
    private static final Map<Reactant, List<IMapping<Reactant, TagKey<Fluid>>>> s_reactantToFluid = Maps.newHashMap();

    private static final TagList<Item> s_solidTags = TagList.items();
    private static final TagList<Fluid> s_fluidTags = TagList.fluids();

    private static final Marker MARKER = MarkerManager.getMarker("API/ReactantMappingsRegistry").addParents(ExtremeReactorsAPI.MARKER);
    private static final Marker WRAPPER = MarkerManager.getMarker("ModPack API Wrapper").addParents(MARKER);

    private static final Component TOOLTIP_FUEL_SOURCE = Component.translatable("api.bigreactors.reactor.tooltip.reactant.fuel").setStyle(ExtremeReactorsAPI.STYLE_TOOLTIP);

    //endregion
}
