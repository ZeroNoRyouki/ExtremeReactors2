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
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockGeneratorVariant;
import it.zerono.mods.zerocore.lib.TestResult;
import it.zerono.mods.zerocore.lib.data.IoDirection;
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

    public FluidContainer(final IFluidContainerAccess accessGovernor) {

        super(0, StackAdapters.FLUIDSTACK, FluidType.Gas, FluidType.Liquid);
        this._accessGovernor = accessGovernor;
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

    @Override
    public Optional<Fluid> getGas() {
        return this.getContent(FluidType.Gas);
    }

    @Override
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

    @Override
    public int getGasAmount() {
        return this.getContentAmount(FluidType.Gas);
    }

    @Override
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

    @Override
    public IFluidHandler getWrapper(final IoDirection portDirection) {

        if (null == this._wrappers) {
            this._wrappers = Maps.newHashMap();
        }

        return this._wrappers.computeIfAbsent(this._accessGovernor.getFluidTypeFrom(portDirection),
                idx -> new IndexedFluidHandlerForwarder<>(this, idx, this._accessGovernor.getAllowedActionFor(idx)));
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

    @Override
    public Optional<Coolant> getCoolant() {
        return (null == this._cachedCoolant || Coolant.EMPTY == this._cachedCoolant) ? Optional.empty() : Optional.of(this._cachedCoolant);
    }

    @Override
    public Optional<Vapor> getVapor() {
        return (null == this._cachedVapor || Vapor.EMPTY == this._cachedVapor) ? Optional.empty() : Optional.of(this._cachedVapor);
    }

    @Override
    public <T> T mapCoolant(Function<Coolant, T> mapper, T defaultValue) {
        return (null == this._cachedCoolant || Coolant.EMPTY == this._cachedCoolant) ? defaultValue : mapper.apply(this._cachedCoolant);
    }

    @Override
    public <T> T mapVapor(Function<Vapor, T> mapper, T defaultValue) {
        return (null == this._cachedVapor || Vapor.EMPTY == this._cachedVapor) ? defaultValue : mapper.apply(this._cachedVapor);
    }

    //region Reactor UPDATE logic

    @Override
    public int getLiquidVaporizedLastTick() {
        return this._liquidVaporizedLastTick;
    }

    @Override
    public double getLiquidTemperature(final double reactorTemperature) {
        return this.getLiquidAmount() > 0 ? Math.min(reactorTemperature, this.getCurrentCoolant().getBoilingPoint()) : reactorTemperature;
    }

    /**
     * Attempt to transfer some heat (in FE) into the coolant system.
     * This method assumes you've already checked the coolant liquid temperature, in getLiquidTemperature(),
     * and scaled the energy absorbed into the liquid based on surface area.
     *
     * @param energyAbsorbed amount of energy to transfer into the coolant system
     * @return amount of energy remaining after absorption
     */
    @Override
    public double onAbsorbHeat(final double energyAbsorbed, final IMultiblockGeneratorVariant variant) {

        if (energyAbsorbed <= 0 || this.getLiquidAmount() <= 0) {
            return energyAbsorbed;
        }

        return this.mapLiquidAmount(amount ->
                this.absorbHeat(energyAbsorbed, variant, amount, this.getCurrentVaporization()), energyAbsorbed);
    }

    @Override
    public int onCondensation(final int vaporUsed, final boolean ventAllCoolant, final IMultiblockGeneratorVariant variant) {

        if (vaporUsed <= 0 || this.getGasAmount() <= 0) {
            return vaporUsed;
        }

        this.extract(FluidType.Gas, vaporUsed, OperationMode.Execute);

        if (ventAllCoolant) {
            return 0;
        } else {
            return this.mapGasAmount(amount -> this.condensate(vaporUsed, this.getCurrentCondensation()), vaporUsed);
        }
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
    private double absorbHeat(final double energyAbsorbed, final IMultiblockGeneratorVariant variant,
                              final int liquidAmount, final IMapping<Coolant, Vapor> vaporization) {

        // do we have some gas around already?

        if (this.getGasAmount() > 0) {

            // is the existing gas compatible with the requested vaporization?

            if (!this.getCurrentVapor().equals(vaporization.getProduct())) {

                // no, give up
                return energyAbsorbed;
            }

            // yes, vaporize using the current gas as the target fluid
            return this.mapGas(gas -> this.vaporize(energyAbsorbed, variant, vaporization, liquidAmount, gas), energyAbsorbed);

        } else {

            // no, vaporize using the first fluid associated to the Vapor as the target fluid
            return FluidMappingsRegistry.getFluidFrom(vaporization.getProduct())
                    .filter(list -> !list.isEmpty())
                    .map(list -> list.get(0))
                    .map(IMapping::getProduct)
                    .map(TagsHelper::getTagFirstElement)
                    .map(gas -> this.vaporize(energyAbsorbed, variant, vaporization, liquidAmount, gas))
                    .orElse(energyAbsorbed);
        }
    }

    private double vaporize(final double energyAbsorbed, final IMultiblockGeneratorVariant variant,
                            final IMapping<Coolant, Vapor> vaporization, final int availableLiquidAmount,
                            final Fluid targetGas) {

        final IMapping<Vapor, Coolant> condensation = vaporization.getReverse();
        final Coolant coolant = vaporization.getSource();
        final double enthalpyOfVaporization = coolant.getEnthalpyOfVaporization();

        // how much vapor can we generate and how much coolant should we consume?

        int maxVaporGenerable = this.getFreeSpace(FluidType.Gas);
        int maxLiquidVaporizable = condensation.getSourceAmount(maxVaporGenerable);

        int liquidVaporized = Math.min(maxLiquidVaporizable, availableLiquidAmount);
        int vaporGenerated = Math.min(vaporization.getProductAmount(liquidVaporized), (int)(energyAbsorbed / enthalpyOfVaporization));

        liquidVaporized = condensation.getSourceAmount(vaporGenerated);
        vaporGenerated *= variant.getVaporGenerationEfficiency();

        if (liquidVaporized < 1) {
            return energyAbsorbed;
        }

        // vaporize!

        this._liquidVaporizedLastTick = liquidVaporized;
        this.extract(FluidType.Liquid, liquidVaporized, OperationMode.Execute);
        this.insert(FluidType.Gas, targetGas, vaporGenerated, OperationMode.Execute);

        // return the energy not absorbed via vaporization

        return Math.max(0.0, energyAbsorbed - ((double)liquidVaporized * enthalpyOfVaporization));
    }

    //endregion
    //region condensation

    public int condensate(final int vaporUsed, final IMapping<Vapor, Coolant> condensation) {

        // do we have some liquid around already?

        if (this.getLiquidAmount() > 0) {

            // is the existing liquid compatible with the requested condensation?

            if (!this.getCurrentCoolant().equals(condensation.getProduct())) {

                // no, give up
                return vaporUsed;
            }

            // yes, condensate using the current gas as the target fluid
            return this.mapLiquid(liquid -> this.condensate(vaporUsed, condensation, liquid), vaporUsed);

        } else {

            // no, condensate using the first fluid associated to the Coolant as the target fluid
            return FluidMappingsRegistry.getFluidFrom(condensation.getProduct())
                    .filter(list -> !list.isEmpty())
                    .map(list -> list.get(0))
                    .map(IMapping::getProduct)
                    .map(TagsHelper::getTagFirstElement)
                    .map(liquid -> this.condensate(vaporUsed, condensation, liquid))
                    .orElse(vaporUsed);
        }
    }

    public int condensate(final int vaporUsed, final IMapping<Vapor, Coolant> condensation, final Fluid targetLiquid) {
        return this.insert(FluidType.Liquid, new FluidStack(targetLiquid, condensation.getProductAmount(vaporUsed)), OperationMode.Execute);
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

    private final IFluidContainerAccess _accessGovernor;
    private Map<FluidType, IndexedFluidHandlerForwarder<FluidType>> _wrappers;
    private int _liquidVaporizedLastTick;

    private Coolant _cachedCoolant;
    private Vapor _cachedVapor;
    private IMapping<Coolant, Vapor> _cachedVaporization;
    private IMapping<Vapor, Coolant> _cachedCondensation;

    //endregion
}
