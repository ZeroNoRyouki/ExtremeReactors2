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
import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.zerocore.lib.data.stack.IStackAdapter;
import it.zerono.mods.zerocore.lib.data.stack.IndexedStackContainer;
import it.zerono.mods.zerocore.lib.data.stack.OperationMode;
import it.zerono.mods.zerocore.lib.data.stack.StackAdapters;
import it.zerono.mods.zerocore.lib.fluid.handler.IndexedFluidHandlerForwarder;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Map;
import java.util.Optional;

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

        final IStackAdapter<FluidStack, Fluid> adapter = this.getStackAdapter();

        return !adapter.isEmpty(stack) && adapter.getContent(stack)
                .map(fluid -> this.isContentValidForIndex(index, fluid)).orElse(false);
    }

    /**
     * Check if the provided content can be stored (in a stack) in the specified index
     *
     * @param index   the index used by the operation
     * @param content the content to evaluate
     * @return true if the content can be stored in the index, false otherwise
     */
    @Override //TODO use tags?
    public boolean isContentValidForIndex(FluidType index, Fluid content) {
        return FluidType.Liquid == index && (Fluids.WATER == content || Fluids.FLOWING_WATER == content);
    }

    public int getLiquidVaporizedLastTick() {
        return this._liquidVaporizedLastTick;
    }

    public double getLiquidTemperature(double reactorTemperature) {
        return this.getLiquid()
                .filter(fluid -> this.getLiquidAmount() > 0)
                .map(fluid -> Math.min(reactorTemperature, getBoilingPoint(fluid)))
                .orElse(reactorTemperature);
    }

    /**
     * Attempt to transfer some heat (in FE) into the coolant system.
     * This method assumes you've already checked the coolant liquid temperature, above,
     * and scaled the energy absorbed into the liquid based on surface area.
     *
     * @param energyAbsorbed amount of energy to transfer into the coolant system
     * @return amount of energy remaining after absorption
     */
    public double onAbsorbHeat(double energyAbsorbed) {
        return energyAbsorbed <= 0 ? energyAbsorbed : this.getLiquid()
                .map(liquid -> this.absorbHeat(energyAbsorbed, liquid))
                .orElse(energyAbsorbed);
    }

    //region internals
    //region heat absorption

    /**
     * Attempt to transfer some heat (in FE) into the coolant system.
     * This method assumes you've already checked the coolant liquid temperature, above,
     * and scaled the energy absorbed into the liquid based on surface area.
     *
     * @param energyAbsorbed FE to transfer into the coolant system.
     * @return FE remaining after absorption.
     */
    private double absorbHeat(double energyAbsorbed, Fluid liquid) {

        final double heatOfVaporization = getHeatOfVaporization(liquid);
        final int mbVaporizable = this.computeVaporizableAmount(heatOfVaporization, energyAbsorbed);

        // We don't do partial vaporization. Just return all the heat.
        if (mbVaporizable < 1) {
            return energyAbsorbed;
        }

        // Vaporize the liquid and return the amount of energy remaining after absorption, if vaporization is possible
        return this.getVaporizationTarget(liquid)
                .map(gas -> this.vaporize(liquid, gas, mbVaporizable, heatOfVaporization))
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
        final Optional<Fluid> resultingGas = getVaporizedLiquidFluid(liquid);

        if (!resultingGas.isPresent()) {

            // the source liquid can't be vaporized at all!
            Log.LOGGER.warn(Log.REACTOR, "Coolant fluid {} has no registered gas conversions!", liquid);
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

    /**
     * Returns the amount of heat (in FE) needed to convert 1mB of liquid into 1mB of vapor.
     * @return
     */
    private static double getHeatOfVaporization(Fluid fluid) {
        //TODO: Make a registry for this, do a lookup
        return 4f; // TE converts 1mB steam into 2 RF of work in a steam turbine, so we assume it's 50% efficient.
    }

    private static Optional<Fluid> getVaporizedLiquidFluid(Fluid fluid) {
        //TODO steam ref
        return Optional.empty();//BrFluids.fluidSteam;
    }

    //endregion

    private static double getBoilingPoint(Fluid fluid) {
        //TODO: Make a registry for this, do a lookup
        return 100f;
    }

    private Map<FluidType, IndexedFluidHandlerForwarder<FluidType>> _wrappers;
    private int _liquidVaporizedLastTick;

    //endregion
}
