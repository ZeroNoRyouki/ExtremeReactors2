/*
 *
 * ReactantHelper.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import it.zerono.mods.extremereactors.api.IMapping;
import it.zerono.mods.extremereactors.api.reactor.*;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.IMultiblockReactorVariant;
import it.zerono.mods.zerocore.lib.data.stack.OperationMode;
import it.zerono.mods.zerocore.lib.fluid.FluidHelper;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.fluids.FluidStack;

import org.jetbrains.annotations.Nullable;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class ReactantHelper {

    /**
     * Check if the provided ItemStack contains a valid solid source for a Reactant of the specified type
     *
     * @param type  the type of the Reactant
     * @param stack the ItemStack
     * @return true if the ItemStack contains a valid solid source for the specified Reactant type
     */
    public static boolean isValidSource(final ReactantType type, final ItemStack stack) {

        return !stack.isEmpty() &&
                ReactantMappingsRegistry.getFromSolid(stack)
                        .map(IMapping::getProduct)
                        .map(reactant -> reactant.test(type))
                        .orElse(false);
    }

    /**
     * Check if the provided FluidStack contains a valid fluid source for a Reactant of the specified type
     *
     * @param type  the type of the Reactant
     * @param stack the FluidStack
     * @return true if the FluidStack contains a valid fluid source for the specified Reactant type
     */
    public static boolean isValidSource(final ReactantType type, final FluidStack stack) {

        return !stack.isEmpty() &&
                ReactantMappingsRegistry.getFromFluid(stack)
                        .map(IMapping::getProduct)
                        .map(reactant -> reactant.test(type))
                        .orElse(false);
    }

    /**
     * Eject the solid/fluid form of the given {@link ReactantType} to the provided Ports and, if desired, void the leftover Reactant
     *
     * @param type the {@link ReactantType} to eject, if available
     * @param fluid if true, eject fluids, solids otherwise
     * @param container the {@link FuelContainer} that is the source of the {@link ReactantType} to eject
     * @param voidLeftover if true, any remaining {@link ReactantType} will be voided
     * @param fuelSources the {@link IFuelSource Ports} that will receive the ejected {@link Reactant}
     * @return true if anything was ejected, false otherwise
     */
    static <T> boolean ejectReactant(final ReactantType type, final boolean fluid, final FuelContainer container,
                                     final boolean voidLeftover, final Stream<IFuelSource<T>> fuelSources) {

        final boolean ejected = container.getContent(type)
                .map(reactant -> ejectReactant(reactant, fluid, container, fuelSources))
                .orElse(false);

        if (ejected && voidLeftover) {
            container.clear(type);
        }

        return ejected;
    }

    /**
     * Eject the solid/fluid form of the given {@link ReactantType} to the provided Fuel Injector Ports and, if desired, void the leftover Reactant
     *
     * @param reactant the {@link Reactant} to eject, if available
     * @param container the {@link FuelContainer} that is the source of the {@link Reactant} to eject
     * @param fuelSources the {@link IFuelSource Ports} that will receive the ejected {@link Reactant}
     * @return true if anything was ejected, false otherwise
     */
    private static <T> boolean ejectReactant(final Reactant reactant, final boolean fluid, final FuelContainer container,
                                             final Stream<IFuelSource<T>> fuelSources) {

        final int minimumReactantAmount = fluid ? reactant.getMinimumFluidSourceAmount() : reactant.getMinimumSolidSourceAmount();

        return (minimumReactantAmount > 0) && (container.getContentAmount(reactant.getType()) >= minimumReactantAmount) &&
                ejectReactant(reactant, minimumReactantAmount, container, fuelSources) > 0;
    }

    /**
     * Eject the solid/fluid form of the given {@link Reactant} to the provided Fuel Injector Ports
     *
     * @param reactant the {@link Reactant} to eject, if available
     * @param minimumReactantAmount the minimum amount of {@link Reactant} to eject, if available
     * @param container the {@link FuelContainer} that is the source of the {@link Reactant} to eject
     * @param fuelSources the {@link IFuelSource Ports} that will receive the ejected {@link Reactant}
     * @return the total amount of {@link Reactant} that was ejected
     */
    private static <T> int ejectReactant(final Reactant reactant, int minimumReactantAmount, final FuelContainer container,
                                         final Stream<IFuelSource<T>> fuelSources) {
        return fuelSources
                .mapToInt(port -> ejectReactant(reactant, minimumReactantAmount, container, port))
                .sum();
    }

    /**
     * Eject the solid/fluid form of the given {@link Reactant} to the specified Port
     *
     * @param reactant the {@link Reactant} to eject, if available
     * @param minimumReactantAmount the minimum amount of {@link Reactant} to eject, if available
     * @param container the {@link FuelContainer} that is the source of the {@link Reactant} to eject
     * @param fuelSource the {@link IFuelSource Port} that will receive the ejected {@link Reactant}
     * @return the amount of {@link Reactant} that was ejected
     */
    private static <T> int ejectReactant(final Reactant reactant, int minimumReactantAmount, final FuelContainer container,
                                         final IFuelSource<T> fuelSource) {

        if (container.getContentAmount(reactant.getType()) >= minimumReactantAmount) {

            int reactantEjected = fuelSource.emitReactant(reactant, container.getContentAmount(reactant.getType()));

            return container.voidReactant(reactant.getType(), reactantEjected);
        }

        return 0;
    }

    static boolean refuelSolid(final FuelContainer container, final Stream<IFuelSource<ItemStack>> sources, final IMultiblockReactorVariant variant) {
        return sources.filter(source -> container.getFreeSpace(ReactantType.Fuel) >= ReactantMappingsRegistry.STANDARD_SOLID_REACTANT_AMOUNT)
                .mapToInt(source -> refuelSolid(container, source, variant))
                .sum() > 0;
    }

    /**
     * Refuel from a single port, return how much was added
      */
    private static int refuelSolid(final FuelContainer container, final IFuelSource<ItemStack> fuelSource,
                                   final IMultiblockReactorVariant variant) {

        // any fuel source items available in the port?

        final ItemStack fuelSourceStack = fuelSource.getFuelStack();

        return ReactantMappingsRegistry.getFromSolid(fuelSourceStack)
                .filter(mapping -> mapping.getProduct().getType().isFuel())
                .map(mapping -> refuelSolid(container, fuelSource, variant, fuelSourceStack, mapping))
                .orElse(0);
    }

    private static int refuelSolid(final FuelContainer container, final IFuelSource<ItemStack> fuelSource,
                                   final IMultiblockReactorVariant variant, final ItemStack fuelSourceStack,
                                   final IMapping<TagKey<Item>, Reactant> fuelMapping) {

        // convert the source items to the equivalent amount of Reactant

        final ReactantStack availableFuel = new ReactantStack(fuelMapping, fuelSourceStack.getCount());

        // any fuel there?

        if (availableFuel.isEmpty() || !availableFuel.getReactant().isPresent()) {
            // no, bail out
            return 0;
        }

        // how much of the available fuel can be stored in the FuelContainer?

        final int storableReactantAmount = container.insertFuel(availableFuel, OperationMode.Simulate);

        if (storableReactantAmount <= 0) {
            // no, bail out
            return 0;
        }

        // ask the port to consume as much source items as possible to fill the available space

        final ItemStack maxSourceToConsume = fuelSourceStack.copyWithCount(fuelMapping.getSourceAmount(storableReactantAmount));

        if (!maxSourceToConsume.isEmpty()) {

            final ItemStack fuelConsumedStack = fuelSource.consumeFuelSource(maxSourceToConsume);

            if (!fuelConsumedStack.isEmpty() && fuelConsumedStack.getCount() > 0) {

                // how much Reactant should we add to the FuelContainer given the consumed source items?

                final float conversionEfficiency = Mth.clamp(variant.getSolidFuelConversionEfficiency(), 0f, 1f);
                int amountToAdd = fuelMapping.getProductAmount(fuelConsumedStack.getCount());

                if (conversionEfficiency < 1.0f) {
                    // apply any variant-specific penalties
                    amountToAdd = (Mth.floor(amountToAdd * conversionEfficiency));
                }

                amountToAdd = Math.min(storableReactantAmount, amountToAdd);

                // how much Reactant was effectively added?

                return container.insertFuel(availableFuel.getReactant().get(), amountToAdd, OperationMode.Execute);
            }
        }

        return 0;
    }

    static boolean refuelFluid(final FuelContainer container, final Stream<IFuelSource<FluidStack>> sources, final IMultiblockReactorVariant variant) {
        return sources.filter(source -> container.getFreeSpace(ReactantType.Fuel) >= ReactantMappingsRegistry.STANDARD_SOLID_REACTANT_AMOUNT)
                .mapToInt(source -> refuelFluid(container, source, variant))
                .sum() > 0;
    }

    /**
     * Refuel from a single port, return how much was added
     */
    private static int refuelFluid(final FuelContainer container, final IFuelSource<FluidStack> fuelSource,
                                   final IMultiblockReactorVariant variant) {

        // any fuel source items available in the port?

        final FluidStack fuelSourceStack = fuelSource.getFuelStack();

        return ReactantMappingsRegistry.getFromFluid(fuelSourceStack)
                .filter(mapping -> mapping.getProduct().getType().isFuel())
                .map(mapping -> refuelFluid(container, fuelSource, variant, fuelSourceStack, mapping))
                .orElse(0);
    }

    private static int refuelFluid(final FuelContainer container, final IFuelSource<FluidStack> fuelSource,
                                   final IMultiblockReactorVariant variant, final FluidStack fuelSourceStack,
                                   final IMapping<TagKey<Fluid>, Reactant> fuelMapping) {

        // 1 mb of fluid fuel is 1 mb of fuel.

        final int availableReactantAmount = fuelSourceStack.getAmount();

        // any fuel there?

        if (availableReactantAmount <= 0) {
            // no, bail out
            return 0;
        }

        // how much of the available fuel can be stored in the FuelContainer?

        final Reactant reactant = fuelMapping.getProduct();
        final int storableReactantAmount = container.insertFuel(new ReactantStack(reactant, availableReactantAmount), OperationMode.Simulate);

        if (storableReactantAmount <= 0) {
            // no, bail out
            return 0;
        }

        // ask the port to consume as much source fuel as possible to fill the available space

        final FluidStack maxSourceToConsume = FluidHelper.stackFrom(fuelSourceStack, storableReactantAmount);

        if (!maxSourceToConsume.isEmpty()) {

            final FluidStack fuelConsumedStack = fuelSource.consumeFuelSource(maxSourceToConsume);

            if (!fuelConsumedStack.isEmpty() && fuelConsumedStack.getAmount() > 0) {
                return container.insertFuel(reactant, Math.min(storableReactantAmount, fuelConsumedStack.getAmount()), OperationMode.Execute);
            }
        }

        return 0;
    }

    @Nullable
    public static Moderator getModeratorFrom(final BlockState state) {
        return getCachedModeratorFrom(state);
    }

    public static Moderator getModeratorFrom(final BlockState state, final Moderator fallbackModerator) {

        final Moderator moderator = getCachedModeratorFrom(state);

        return null != moderator ? moderator : fallbackModerator;
    }

    public static boolean isValidModerator(final BlockState state) {
        return null != getCachedModeratorFrom(state);
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onVanillaTagsUpdated(final TagsUpdatedEvent event) {
        s_moderatorsCache.invalidateAll();
    }

    //region internals

    @Nullable
    private static Moderator getCachedModeratorFrom(final BlockState state) {

        Moderator moderator = s_moderatorsCache.getIfPresent(state);

        if (null == moderator) {

            final Optional<Moderator> m = ModeratorsRegistry.getFrom(state);

            if (m.isPresent()) {
                s_moderatorsCache.put(state, moderator = m.get());
            }
        }

        return moderator;
    }

    private static final Cache<BlockState, Moderator> s_moderatorsCache = CacheBuilder.newBuilder()
            .initialCapacity(4)
            .concurrencyLevel(2)
            .maximumSize(128)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build();

    //endregion
}
