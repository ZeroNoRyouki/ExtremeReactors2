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
import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.extremereactors.api.ExtremeReactorsAPI;
import it.zerono.mods.extremereactors.api.IMapping;
import it.zerono.mods.extremereactors.api.internal.InternalDispatcher;
import it.zerono.mods.extremereactors.api.internal.modpack.wrapper.AddRemoveSection;
import it.zerono.mods.extremereactors.api.internal.modpack.wrapper.ApiWrapper;
import it.zerono.mods.extremereactors.api.internal.modpack.wrapper.SourceTag;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.item.ItemHelper;
import it.zerono.mods.zerocore.lib.tag.CollectionProviders;
import it.zerono.mods.zerocore.lib.tag.TagList;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
    public static Optional<IMapping<ResourceLocation, Reactant>> getFromSolid(final ItemStack stack) {

        if (stack.isEmpty()) {
            return Optional.empty();
        }

        final Item item = stack.getItem();

        return s_solidTags
                .find(tag -> tag.contains(item))
                .filter(t -> t instanceof ITag.INamedTag)
                .map(t -> (ITag.INamedTag<Item>)t)
                .map(ITag.INamedTag::getName)
                .map(s_solidToReactant::get);
    }

    /**
     * Get a list of Source-Product solid mappings for the given Reactant.
     *
     * @param reactant The Reactant
     * @return A list of reactant => Item Tag mappings, if one is found. Note that reactant is the source and Item Tag is the product
     */
    public static Optional<List<IMapping<Reactant, ResourceLocation>>> getToSolid(final Reactant reactant) {
        return Optional.ofNullable(s_reactantToSolid.get(reactant));
    }

    /**
     * Get an ItemStack filled with the given amount of the first available Item from the Item Tag associated with the provided mapping
     *
     * @param mapping the mapping
     * @param amount the amount of items
     * @return an ItemStack that contains the requested item, if any is found, or an empty ItemStack
     */
    public static ItemStack getSolidStackFrom(final IMapping<Reactant, ResourceLocation> mapping, final int amount) {
        return s_solidTags.getTag(mapping.getProduct())
                .map(TagsHelper::getTagFirstElement)
                .map(item -> ItemHelper.stackFrom(item, amount))
                .orElse(ItemStack.EMPTY);
    }

    @Deprecated // use registerSolid, this method will be removed soon
    public static void register(final String reactantName, final int reactantQty, final ResourceLocation sourceItemTagId) {
        registerSolid(reactantName, reactantQty, sourceItemTagId);
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
    public static void registerSolid(final String reactantName, final int reactantQty, final ResourceLocation sourceItemTagId) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(reactantName));
        Preconditions.checkNotNull(sourceItemTagId);

        InternalDispatcher.dispatch("mapping-register", () -> {

            final int qty;

            if (reactantQty < 0) {

                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Using default quantity for reactant {} instead of the provided, invalid, one: {}", reactantName, reactantQty);
                qty = STANDARD_SOLID_REACTANT_AMOUNT;

            } else {

                qty = reactantQty;
            }

            CodeHelper.optionalIfPresentOrElse(ReactantsRegistry.get(reactantName),
                    reactant -> {

                        final IMapping<ResourceLocation, Reactant> mapping = IMapping.of(sourceItemTagId, 1, reactant, qty);

                        s_solidToReactant.put(mapping.getSource(), mapping);
                        s_reactantToSolid.computeIfAbsent(mapping.getProduct(), k -> Lists.newArrayList()).add(mapping.getReverse());

                    },
                    () -> ExtremeReactorsAPI.LOGGER.warn(MARKER, "Skipping registration for an unknown source reactant: {}", reactantName));
        });
    }

    public static void removeSolid(final String sourceItemTagId) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(sourceItemTagId));
        removeSolid(new ResourceLocation(sourceItemTagId));
    }

    public static void removeSolid(final ResourceLocation sourceItemTagId) {

        Preconditions.checkNotNull(sourceItemTagId);

        InternalDispatcher.dispatch("mapping-remove", () -> {

            final IMapping<ResourceLocation, Reactant> removedMapping = s_solidToReactant.remove(sourceItemTagId);

            if (null != removedMapping) {

                s_reactantToSolid.getOrDefault(removedMapping.getProduct(), Collections.emptyList())
                        .removeIf(reactantToTagMapping -> reactantToTagMapping.getProduct().equals(sourceItemTagId));

                s_reactantToSolid.entrySet().stream()
                        .filter(entry -> entry.getValue().isEmpty())
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet())
                        .forEach(s_reactantToSolid::remove);
            }
        });
    }

    public static void fillReactantsTooltips(final Map<Item, Set<ITextComponent>> tooltipsMap,
                                             final NonNullSupplier<Set<ITextComponent>> setSupplier) {

        s_solidToReactant.values().stream()
                .filter(mapping -> mapping.getProduct().getType().isFuel())
                .map(IMapping::getSource)
                .forEach(id -> s_solidTags.forTag(id,
                        tag -> tag.getAllElements()
                                .forEach(item -> tooltipsMap.computeIfAbsent(item, k -> setSupplier.get()).add(TOOLTIP_FUEL_SOURCE))));
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onVanillaTagsUpdated(final TagsUpdatedEvent.VanillaTagTypes event) {

        updateTags(s_solidToReactant.keySet(), s_solidTags, TagsHelper.ITEMS);
        //TODO fluids
    }

    public static void processWrapper(final ApiWrapper wrapper) {

        if (!wrapper.Enabled) {
            return;
        }

        processWrapper("solid", wrapper.ReactorReactantSources, s_solidToReactant, s_reactantToSolid,
                ReactantMappingsRegistry::removeSolid,
                (SourceTag w) -> registerSolid(w.ProductName, w.ProductQuantity, new ResourceLocation(w.SourceTagId)));

        //TODO fluids
    }

    //region internals

    private ReactantMappingsRegistry() {
    }

    private static <T> void updateTags(final Set<ResourceLocation> ids, final TagList<T> tagList, final TagsHelper<T> helper) {

        tagList.clear();
        ids.stream()
                .filter(helper::tagExist)
                .map(helper::createTag)
                .forEach(tagList::addTag);
    }

    private static <X> void processWrapper(final String objectName, final AddRemoveSection<SourceTag> wrapperSection,
                                           final Map<ResourceLocation, IMapping<ResourceLocation, Reactant>> sourceToReactant,
                                           final Map<Reactant, List<IMapping<Reactant, ResourceLocation>>> reactantToSources,
                                           final Consumer<String> removeAction, final Consumer<SourceTag> addAction) {

        if (wrapperSection.WipeExistingValuesBeforeAdding) {

            // wipe all

            Log.LOGGER.info(WRAPPER, "Wiping all existing {} Reactor Reactant source mappings", objectName);
            sourceToReactant.clear();
            reactantToSources.clear();

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
    // - solid source -> Item Tag : reactant name mapping
    private static final Map<ResourceLocation, IMapping<ResourceLocation, Reactant>> s_solidToReactant = Maps.newHashMap();
    //TODO fluids

    // 1:many mappings
    // - reactant name -> a list of reactant name : Item Tag mappings
    private static final Map<Reactant, List<IMapping<Reactant, ResourceLocation>>> s_reactantToSolid = Maps.newHashMap();
    //TODO fluids

    private static final TagList<Item> s_solidTags = new TagList<>(CollectionProviders.ITEMS_PROVIDER);
    //TODO fluids

    private static final Marker MARKER = MarkerManager.getMarker("API/ReactantMappingsRegistry").addParents(ExtremeReactorsAPI.MARKER);
    private static final Marker WRAPPER = MarkerManager.getMarker("ModPack API Wrapper").addParents(MARKER);

    private static final ITextComponent TOOLTIP_FUEL_SOURCE = new TranslationTextComponent("api.bigreactors.reactor.tooltip.reactant.fuel").setStyle(ExtremeReactorsAPI.STYLE_TOOLTIP);

    //endregion
}
