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
import it.zerono.mods.extremereactors.api.ExtremeReactorsAPI;
import it.zerono.mods.extremereactors.api.IMapping;
import it.zerono.mods.extremereactors.api.InternalDispatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.NonNullSupplier;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ReactantMappingsRegistry {

    public static final int STANDARD_SOLID_REACTANT_AMOUNT = 1000; // 1 item = 1000 mB, standard
    public static final int STANDARD_FLUID_REACTANT_AMOUNT = 1; // 1 mB = 1 mB

    /**
     * Get the Source-Product solid mapping for the given ItemStack (if one exists)
     *
     * @param stack The ItemStack
     * @return The Source-Product solid mapping, if one is found and the provided stack is not empty
     */
    public static Optional<IMapping<ITag.INamedTag<Item>, Reactant>> getFromSolid(final ItemStack stack) {

        if (stack.isEmpty()) {
            return Optional.empty();
        }

        final Item item = stack.getItem();

        return s_solidToReactant.entrySet().stream()
                .filter(entry -> entry.getKey().contains(item))
                .map(Map.Entry::getValue)
                .findAny();
    }

    /**
     * Get a list of Source-Product solid mappings for the given Reactant.
     *
     * @param reactant The Reactant
     * @return A list of reactant => Item Tag mappings, if one is found. Note that reactant is the source and Item Tag is the product
     */
    public static Optional<List<IMapping<Reactant, ITag.INamedTag<Item>>>> getToSolid(final Reactant reactant) {
        return Optional.ofNullable(s_reactantToSolid.get(reactant));
    }

    /**
     * Register an Item Tag as a valid Reactant source.
     *
     * For fuels, it will allow access ports to accept Items in the inlet slot.
     * For wastes, it will allow access ports to eject Items into the outlet slot.
     *
     * @param reactantName The name of the Reactant produced by the source.
     * @param reactantQty The quantity of the Reactant produced for every unit of source (must be >= 0).
     * @param source The source for the reactant.
     */
    public static void register(final String reactantName, final int reactantQty, final ITag.INamedTag<Item> source) {

        Preconditions.checkNotNull(reactantName);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(reactantName));

        InternalDispatcher.dispatch("mapping-register", () -> {

            int qty = reactantQty;

            if (qty < 0) {

                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Using default quantity for reactant {} instead of the provided, invalid, one: {}", reactantName, qty);
                qty = STANDARD_SOLID_REACTANT_AMOUNT;
            }
            
            final Optional<Reactant> entry = ReactantsRegistry.get(reactantName);

            if (entry.isPresent()) {

                final IMapping<ITag.INamedTag<Item>, Reactant> mapping = IMapping.of(source, 1, entry.get(), qty);

                s_solidToReactant.put(mapping.getSource(), mapping);
                s_reactantToSolid.computeIfAbsent(mapping.getProduct(), k -> Lists.newArrayList()).add(mapping.getReverse());

            } else {

                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Skipping registration for an unknown source reactant: {}", reactantName);
            }
        });
    }

    public static void fillReactantsTooltips(final Map<Item, Set<ITextComponent>> tooltipsMap,
                                             final NonNullSupplier<Set<ITextComponent>> setSupplier) {

        s_solidToReactant.entrySet().stream()
                .filter(entry -> entry.getValue().getProduct().getType().isFuel())
                .map(Map.Entry::getKey)
                .flatMap(itemTag -> itemTag.getAllElements().stream())
                .forEach(item -> tooltipsMap.computeIfAbsent(item, k -> setSupplier.get()).add(TOOLTIP_FUEL_SOURCE));
    }

    //region internals

    private ReactantMappingsRegistry() {
    }

    // 1:1 mappings
    // - solid source -> Item Tag : reactant name mapping
    private static final Map<ITag.INamedTag<Item>, IMapping<ITag.INamedTag<Item>, Reactant>> s_solidToReactant = Maps.newHashMap();
    //TODO fluids

    // 1:many mappings
    // - reactant name -> a list of reactant name : Item Tag mappings
    private static final Map<Reactant, List<IMapping<Reactant, ITag.INamedTag<Item>>>> s_reactantToSolid = Maps.newHashMap();
    //TODO fluids

    private static final Marker MARKER = MarkerManager.getMarker("API/ReactantMappingsRegistry").addParents(ExtremeReactorsAPI.MARKER);

    private static final ITextComponent TOOLTIP_FUEL_SOURCE = new TranslationTextComponent("api.bigreactors.reactor.tooltip.reactant.fuel").setStyle(ExtremeReactorsAPI.STYLE_TOOLTIP);

    //endregion
}
