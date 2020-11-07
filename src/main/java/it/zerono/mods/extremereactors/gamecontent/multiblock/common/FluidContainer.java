/*
 *
 * FluidContainer.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common;

import com.google.common.collect.Maps;
import it.zerono.mods.extremereactors.api.IMapping;
import it.zerono.mods.extremereactors.api.coolant.Coolant;
import it.zerono.mods.extremereactors.api.coolant.FluidMappingsRegistry;
import it.zerono.mods.extremereactors.api.coolant.TransitionsRegistry;
import it.zerono.mods.extremereactors.api.coolant.Vapor;
import it.zerono.mods.zerocore.lib.TestResult;
import it.zerono.mods.zerocore.lib.data.stack.IndexedStackContainer;
import it.zerono.mods.zerocore.lib.data.stack.OperationMode;
import it.zerono.mods.zerocore.lib.data.stack.StackAdapters;
import it.zerono.mods.zerocore.lib.fluid.handler.IndexedFluidHandlerForwarder;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Map;
import java.util.Optional;
import java.util.function.*;

public class FluidContainer
        extends IndexedStackContainer<FluidType, Fluid, FluidStack>
        implements IFluidContainer {

    public FluidContainer() {

        super(0, StackAdapters.FLUIDSTACK, FluidType.Gas, FluidType.Liquid);
        this._liquidVaporizedLastTick = 0;
    }

    /**
     * Reset the internal data
     * --- FOR TESTING PURPOSES ONLY ---
     */
    public void reset() {

        this._liquidVaporizedLastTick = 0;
        this.voidGas();
        this.voidLiquid();
    }

    public Optional<Fluid> getGas() {
        return this.getContent(FluidType.Gas);
    }

    public Optional<Fluid> getLiquid() {
        return this.getContent(FluidType.Liquid);
    }

    public <T> T mapLiquid(final Function<Fluid, T> mapper, final T defaultValue) {
        return this.map(FluidType.Liquid, mapper, defaultValue);
    }

    public <T> T mapLiquidAmount(final IntFunction<T> mapper, final T defaultValue) {
        return this.map(FluidType.Liquid, mapper, defaultValue);
    }

    public <T> T mapLiquid(final BiFunction<Fluid, Integer, T> mapper, final T defaultValue) {
        return this.map(FluidType.Liquid, mapper, defaultValue);
    }

    public <T> T mapGas(final Function<Fluid, T> mapper, final T defaultValue) {
        return this.map(FluidType.Gas, mapper, defaultValue);
    }

    public <T> T mapGasAmount(final IntFunction<T> mapper, final T defaultValue) {
        return this.map(FluidType.Gas, mapper, defaultValue);
    }

    public <T> T mapGas(final BiFunction<Fluid, Integer, T> mapper, final T defaultValue) {
        return this.map(FluidType.Gas, mapper, defaultValue);
    }

    public void forLiquid(final Consumer<Fluid> consumer) {
        this.accept(FluidType.Liquid, consumer);
    }

    public void forLiquid(final IntConsumer consumer) {
        this.accept(FluidType.Liquid, consumer);
    }

    public void forLiquid(final BiConsumer<Fluid, Integer> consumer) {
        this.accept(FluidType.Liquid, consumer);
    }

    public void forGas(final Consumer<Fluid> consumer) {
        this.accept(FluidType.Gas, consumer);
    }

    public void forGas(final IntConsumer consumer) {
        this.accept(FluidType.Gas, consumer);
    }

    public void forGas(final BiConsumer<Fluid, Integer> consumer) {
        this.accept(FluidType.Gas, consumer);
    }

    public int getGasAmount() {
        return this.getContentAmount(FluidType.Gas);
    }

    public int getLiquidAmount() {
        return this.getContentAmount(FluidType.Liquid);
    }

    /**
     * Add some liquid to the container, if possible
     *
     * @param liquid the liquid to add
     * @param amount the quantity of liquid to add
     * @param mode if Simulate, this will only simulate a fill and will not alter the fuel amount
     * @return the amount of Reactant actually added
     */
    public int insertLiquid(Fluid liquid, int amount, OperationMode mode) {
        return amount <= 0 ? 0 : this.insert(FluidType.Liquid, liquid, amount, mode);
    }

    public int voidGas() {
        return this.clear(FluidType.Gas).getAmount();
    }

    public int voidLiquid() {
        return this.clear(FluidType.Liquid).getAmount();
    }

    public IFluidHandler getWrapper(FluidType index) {

        if (null == this._wrappers) {
            this._wrappers = Maps.newHashMap();
        }

        return this._wrappers.computeIfAbsent(index,
                idx -> new IndexedFluidHandlerForwarder<>(this, idx, idx.getAllowedAction()));
    }

    /**
     * Check if the provided stack can be stored in the specified index
     *
     * @param index the index used by the operation
     * @param stack the stack to evaluate
     * @return true if the stack can be stored in the index, false otherwise
     */
    @Override
    public boolean isStackValidForIndex(FluidType index, FluidStack stack) {
        return !stack.isEmpty() && this.isContentValidForIndex(index, stack.getFluid());
    }

    /**
     * Check if the provided content can be stored (in a stack) in the specified index
     *
     * @param index   the index used by the operation
     * @param content the content to evaluate
     * @return true if the content can be stored in the index, false otherwise
     */
    @Override
    public boolean isContentValidForIndex(final FluidType index, final Fluid content) {

        // check if the the current content mach the provided one

        final TestResult result = TestResult.from(this.getContent(index)
                .map(fluid -> fluid.isEquivalentTo(content)));

        if (!result.wasSkipped()) {
            return result.getAsBoolean();
        }

        // check if the provided is a valid coolant or vapor

        final Predicate<Fluid> test = index.isGas() ? FluidMappingsRegistry::hasVaporFrom : FluidMappingsRegistry::hasCoolantFrom;

        return TestResult.from(test, content).getAsBoolean();
    }

    //region Reactor UPDATE logic

    public int getLiquidVaporizedLastTick() {
        return this._liquidVaporizedLastTick;
    }

    public double getLiquidTemperature(final double reactorTemperature) {
        return this.getLiquidAmount() > 0 ? Math.min(reactorTemperature, this.getCurrentCoolant().getBoilingPoint()) : reactorTemperature;
//        return this.getLiquid()
//                .filter(fluid -> this.getLiquidAmount() > 0)
//                .map(fluid -> Math.min(reactorTemperature, getBoilingPoint(fluid)))
//                .orElse(reactorTemperature);
    }

    /**
     * Attempt to transfer some heat (in FE) into the coolant system.
     * This method assumes you've already checked the coolant liquid temperature, in getLiquidTemperature(),
     * and scaled the energy absorbed into the liquid based on surface area.
     *
     * @param energyAbsorbed amount of energy to transfer into the coolant system
     * @return amount of energy remaining after absorption
     */
    public double onAbsorbHeat(final double energyAbsorbed) {

        if (energyAbsorbed <= 0 || this.getLiquidAmount() <= 0) {
            return energyAbsorbed;
        }

        return this.mapLiquidAmount(amount -> this.absorbHeat(energyAbsorbed, amount, this.getCurrentVaporization()), energyAbsorbed);
    }

    //endregion
    //region ISyncableEntity

    /**
     * Sync the entity data from the given {@link CompoundNBT}
     *
     * @param data       the {@link CompoundNBT} to read from
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public void syncDataFrom(CompoundNBT data, SyncReason syncReason) {

        super.syncDataFrom(data, syncReason);

        if (syncReason.isFullSync()) {

            this.rebuildCoolantCache();
            this.rebuildVaporCache();
        }
    }

    //endregion
    //region internals
    //region heat absorption

    /**
     * Attempt to transfer some heat (in FE) into the coolant system.
     * This method assumes you've already checked the coolant liquid temperature, in getLiquidTemperature(),
     * and scaled the energy absorbed into the liquid based on surface area.
     *
     * @param energyAbsorbed FE to transfer into the coolant system.
     * @param vaporization the vaporization mapping for the Coolant
     * @return FE remaining after absorption.
     */
    private double absorbHeat(final double energyAbsorbed, final int liquidAmount, final IMapping<Coolant, Vapor> vaporization) {

        // do we have some gas around already?

        if (this.getGasAmount() > 0) {

            // is the existing gas compatible with the requested vaporization?

            if (!this.getCurrentVapor().equals(vaporization.getProduct())) {

                // no, give up
                return energyAbsorbed;
            }

            // yes, vaporize using the current gas as the target fluid
            return this.mapGas(gas -> this.vaporize(energyAbsorbed, vaporization, liquidAmount, gas), energyAbsorbed);

        } else {

            // no, vaporize using the first fluid associated to the Vapor as the target fluid
            return FluidMappingsRegistry.getFluidFrom(vaporization.getProduct())
                    .filter(list -> !list.isEmpty())
                    .map(list -> list.get(0))
                    .map(IMapping::getProduct)
                    .map(TagsHelper::getTagFirstElement)
                    .map(gas -> this.vaporize(energyAbsorbed, vaporization, liquidAmount, gas))
                    .orElse(energyAbsorbed);
        }
    }

    private double vaporize(final double energyAbsorbed, final IMapping<Coolant, Vapor> vaporization,
                            final int liquidAmount, final Fluid targetGas) {


        if(getFluidAmount(COLD) <= 0 || rfAbsorbed <= 0) { return rfAbsorbed; }

        Fluid coolantType = getCoolantType();
        int coolantAmt = getFluidAmount(COLD);

        float heatOfVaporization = getHeatOfVaporization(coolantType);

        int mbVaporized = Math.min(coolantAmt, (int)(rfAbsorbed / heatOfVaporization));

        // Cap by the available space in the vapor chamber
        mbVaporized = Math.min(mbVaporized, getRemainingSpaceForFluid(HOT));

        // We don't do partial vaporization. Just return all the heat.
        if(mbVaporized < 1) { return rfAbsorbed; }

        // Make sure we either have an empty vapor chamber or the vapor types match
        Fluid newVaporType = getVaporizedCoolantFluid(coolantType);
        if(newVaporType == null) {
            BRLog.warning("Coolant in tank (%s) has no registered vapor type!", coolantType.getName());
            return rfAbsorbed;
        }

        Fluid existingVaporType = getVaporType();
        if(existingVaporType != null && !newVaporType.getName().equals(existingVaporType.getName())) {
            // Can't vaporize anything with incompatible vapor in the vapor tank
            return rfAbsorbed;
        }

        // Vaporize! -- POINT OF NO RETURN
        fluidVaporizedLastTick = mbVaporized;
        this.drainCoolant(mbVaporized);

        if(existingVaporType != null) {
            addFluidToStack(HOT, mbVaporized);
        }
        else {
            fill(HOT, new FluidStack(newVaporType, mbVaporized), true);
        }

        // Calculate how much we actually absorbed via vaporization
        float energyConsumed = (float)mbVaporized * heatOfVaporization;

        // And return energy remaining after absorption
        return Math.max(0f, rfAbsorbed - energyConsumed);

    }


    /**
     *
     * @param energyAbsorbed
     * @param liquidAmount
     * @param vaporization
     * @return
     */
    private int computeVaporizableAmount(final double energyAbsorbed, final int liquidAmount,
                                         final IMapping<Coolant, Vapor> vaporization) {

        if (liquidAmount > 0) {

            int mbVaporized = Math.min(vaporization.getProductAmount(liquidAmount),
                    (int)(energyAbsorbed / vaporization.getSource().getEnthalpyOfVaporization()));

            // Cap by the available space in the gas stack
            return Math.min(mbVaporized, this.getFreeSpace(FluidType.Gas));
        }

        return 0;
    }

    private double absorbHeat(double energyAbsorbed, Fluid liquid) {

        final double enthalpyOfVaporization = this.getCurrentCoolant().getEnthalpyOfVaporization(); //getHeatOfVaporization(liquid);
        final int mbVaporizable = this.computeVaporizableAmount(enthalpyOfVaporization, energyAbsorbed);

        // We don't do partial vaporization. Just return all the heat.
        if (mbVaporizable < 1) {
            return energyAbsorbed;
        }

        // Vaporize the liquid and return the amount of energy remaining after absorption, if vaporization is possible
        return this.getVaporizationTarget(liquid)
                .map(gas -> this.vaporize(liquid, gas, mbVaporizable, enthalpyOfVaporization))
                .map(energyConsumed -> Math.max(0d, energyAbsorbed - energyConsumed))
                .orElse(energyAbsorbed);
    }

    private int computeVaporizableAmount(final double heatOfVaporization, final double energyAbsorbed) {

        final int liquidAmount = this.getLiquidAmount();

        if (liquidAmount > 0) {

            int mbVaporized = Math.min(liquidAmount, (int)(energyAbsorbed / heatOfVaporization));

            // Cap by the available space in the gas stack
            return Math.min(mbVaporized, this.getFreeSpace(FluidType.Gas));
        }

        return 0;
    }

    /**
     * Return which the gas we should vaporize the liquid in to
     *
     * @param liquid the source liquid
     * @return an {@link Optional} containing the gas
     */
    private Optional<Fluid> getVaporizationTarget(Fluid liquid) {

        // Make sure we either have an empty gas stack or the gas types match
        final Optional<Fluid> resultingGas = Optional.empty(); //getVaporizationMappingFor(liquid); //TODO fix

        if (!resultingGas.isPresent()) {

            // the source liquid can't be vaporized at all!
            //TODO restore
//            Log.LOGGER.warn(Log.REACTOR, "Coolant fluid {} has no registered gas conversions!", liquid);
            return Optional.empty();
        }

        // is there any gas already in the container? does it mach with what the liquid vaporize in to?
        final Optional<Fluid> existingGas = this.getGas();

        if (existingGas.isPresent()) {
            return resultingGas.flatMap(resulting -> existingGas.filter(existing -> existing.isEquivalentTo(resulting)));
        } else {
            return resultingGas;
        }
    }

    private double vaporize(final Fluid liquid, final Fluid gas, final int amountToVaporize, final double heatOfVaporization) {

        this.extract(FluidType.Liquid, liquid, amountToVaporize, OperationMode.Execute);
        this.insert(FluidType.Gas, gas, amountToVaporize, OperationMode.Execute);

        this._liquidVaporizedLastTick = amountToVaporize;

        // return how much we actually absorbed via vaporization
        return (double)amountToVaporize * heatOfVaporization;
    }

    private static Optional<Coolant> getCoolantFrom(final Fluid fluid) {
        return FluidMappingsRegistry.getCoolantFrom(fluid)
                .map(IMapping::getProduct);
    }

    /**
     * Returns the amount of heat (in FE) needed to convert 1mB of liquid into 1mB of vapor.
     * @return
     */
    private static double getHeatOfVaporization(Fluid fluid) {
        //TODO: Make a registry for this, do a lookup
        return 4f; // TE converts 1mB steam into 2 RF of work in a steam turbine, so we assume it's 50% efficient.
    }

    private static Optional<IMapping<Coolant, Vapor>> getVaporizationMappingFor(final Fluid fluid) {
        return getCoolantFrom(fluid)
                .flatMap(TransitionsRegistry::get);
    }

    //endregion
    //region cached transitions

    protected Coolant getCurrentCoolant() {

        if (null == this._cachedCoolant || Coolant.EMPTY == this._cachedCoolant) {
            this._cachedCoolant = this.getLiquid()
                    .flatMap(FluidMappingsRegistry::getCoolantFrom)
                    .map(IMapping::getProduct)
                    .orElse(Coolant.EMPTY);
        }

        return this._cachedCoolant;
    }

    protected IMapping<Coolant, Vapor> getCurrentVaporization() {

        if (null == this._cachedVaporization || TransitionsRegistry.EMPTY_VAPORIZATION == this._cachedVaporization) {
            this._cachedVaporization = TransitionsRegistry.get(this.getCurrentCoolant())
                    .orElse(TransitionsRegistry.EMPTY_VAPORIZATION);
        }

        return this._cachedVaporization;
    }

    protected Vapor getCurrentVapor() {

        if (null == this._cachedVapor || Vapor.EMPTY == this._cachedVapor) {
            this._cachedVapor = this.getGas()
                    .flatMap(FluidMappingsRegistry::getVaporFrom)
                    .map(IMapping::getProduct)
                    .orElse(Vapor.EMPTY);
        }

        return this._cachedVapor;
    }

    protected IMapping<Vapor, Coolant> getCurrentCondensation() {

        if (null == this._cachedCondensation || TransitionsRegistry.EMPTY_CONDENSATION == this._cachedCondensation) {
            this._cachedCondensation = TransitionsRegistry.get(this.getCurrentVapor())
                    .orElse(TransitionsRegistry.EMPTY_CONDENSATION);
        }

        return this._cachedCondensation;
    }

    private void resetCoolantCache() {

        this._cachedCoolant = null;
        this._cachedCondensation = null;
    }

    private void rebuildCoolantCache() {

        this.getCurrentCoolant();
        this.getCurrentVaporization();
    }

    private void resetVaporCache() {

        this._cachedVapor = null;
        this._cachedCondensation = null;
    }

    private void rebuildVaporCache() {

        this.getCurrentVapor();
        this.getCurrentCondensation();
    }

    /**
     * Override this to get notified when an insert operation is completed on the given index
     *
     * @param fluidType the index target of the insert operation
     * @param wasEmpty  true if the index was empty before the insert operation
     */
    @Override
    protected void onInsert(final FluidType fluidType, final boolean wasEmpty) {

        super.onInsert(fluidType, wasEmpty);

        if (wasEmpty) {

            switch (fluidType) {

                case Liquid:
                    this.rebuildCoolantCache();
                    break;

                case Gas:
                    this.rebuildVaporCache();
                    break;
            }
        }
    }

    /**
     * Override this to get notified when an extract operation is completed on the given index
     *
     * @param fluidType  the index target of the extract operation
     * @param isEmptyNow true if the index has become empty after the extract operation
     */
    @Override
    protected void onExtract(final FluidType fluidType, final boolean isEmptyNow) {

        super.onExtract(fluidType, isEmptyNow);

        if (isEmptyNow) {

            switch (fluidType) {

                case Liquid:
                    this.resetCoolantCache();
                    break;

                case Gas:
                    this.resetVaporCache();
                    break;
            }
        }
    }

    //endregion

//    private static double getBoilingPoint(final Fluid fluid) {
//        return getCoolantFrom(fluid)
//                .map(Coolant::getBoilingPoint)
//                .orElse(Float.MAX_VALUE);
////        return 100f;
//    }


    private Map<FluidType, IndexedFluidHandlerForwarder<FluidType>> _wrappers;
    private int _liquidVaporizedLastTick;

    private Coolant _cachedCoolant;
    private Vapor _cachedVapor;
    private IMapping<Coolant, Vapor> _cachedVaporization;
    private IMapping<Vapor, Coolant> _cachedCondensation;

    //endregion
}
