/*
 *
 * TurbineLogic.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine;

import it.zerono.mods.extremereactors.api.coolant.Vapor;
import it.zerono.mods.extremereactors.config.Config;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.IFluidContainer;
import it.zerono.mods.zerocore.lib.energy.EnergyBuffer;
import net.minecraft.util.profiling.ProfilerFiller;

public class TurbineLogic {

    TurbineLogic(final ITurbineReader turbine, final TurbineData data, final EnergyBuffer energyBuffer) {

        this._turbine = turbine;
        this._data = data;
        this._energyBuffer = energyBuffer;
    }

    /**
     * Main update logic
     */
    public void update() {

        final ProfilerFiller profiler = this._turbine.getWorld().getProfiler();
        final IFluidContainer fc = this._turbine.getFluidContainer();
        final VentSetting ventSetting = this._data.getVentSetting();

        this.resetStats();

        // Generate energy based on vapor

        int vaporAmount = 0; // mB. Based on water, actually. Probably higher for steam. Measure it.

        if (this._turbine.isMachineActive()) {

            // Spin up via steam inputs, convert some steam back into water.
            // Use at most the user-configured max, or the amount in the tank, whichever is less.

            vaporAmount = Math.min(this._data.getMaxIntakeRate(), this._turbine.getVaporAmount());

            if (ventSetting.isDoNotVent()) {

                // Cap steam used to available space, if not venting
                final int availableSpace = this._turbine.getCapacity() - this._turbine.getCoolantAmount();

                vaporAmount = Math.min(vaporAmount, availableSpace);
            }
        }

        if (vaporAmount > 0 || this._data.getRotorEnergy() > 0) {

            final float rotorSpeed = this._turbine.getRotorSpeed();

            // FEs lost to aerodynamic drag.
            float aerodynamicDragTorque = rotorSpeed * this._data.getBladeDrag();
            float liftTorque = 0f;

            profiler.push("Energy");

            if (vaporAmount > 0) {

                float fluidEnergyDensity = fc.mapVapor(Vapor::getFluidEnergyDensity, 0f);

                // Cap amount of steam we can fully extract energy from based on blade size
                int steamToProcess = this._data.getBladeSurfaceArea() * this._data.getInputFluidPerBlade();

                steamToProcess = Math.min(steamToProcess, vaporAmount);
                liftTorque = steamToProcess * fluidEnergyDensity;

                // Did we have excess steam for our blade size?

                if (steamToProcess < vaporAmount) {

                    // Extract some percentage of the remaining steam's energy, based on how many blades are missing
                    steamToProcess = vaporAmount - steamToProcess;

                    final float bladeEfficiency;
                    final int neededBlades = vaporAmount / this._data.getInputFluidPerBlade(); // round in the player's favor
                    final int missingBlades = neededBlades - this._data.getBladeSurfaceArea();

                    bladeEfficiency = 1f - (float)missingBlades / (float)neededBlades;
                    liftTorque += steamToProcess * fluidEnergyDensity * bladeEfficiency;

                    this._data.setRotorEfficiencyLastTick(liftTorque / (vaporAmount * fluidEnergyDensity));
                }
            }

            // Yay for derivation. We're assuming delta-Time is always 1, as we're always calculating for 1 tick.
            // FEs available to coils

            final float inductionTorque = this._data.isInductorEngaged() ?
                    rotorSpeed * this._data.getInductorDragCoefficient() * this._data.getCoilSize() : 0.0f;

            final float energyToGenerate = (float)Math.pow(inductionTorque, this._data.getInductionEnergyExponentBonus()) *
                    this._data.getInductionEfficiency();

            if (energyToGenerate > 0.0f) {

                // Efficiency curve. Rotors are 50% less efficient when not near 900/1800 RPMs.

                float efficiency = (float)(0.25 * Math.cos(rotorSpeed / (45.5 * Math.PI))) + 0.75f;

                if (rotorSpeed < 500) {
                    efficiency = Math.min(0.5f, efficiency);
                }

                this.generateEnergy(energyToGenerate * efficiency);
            }

            this._data.changeRotorEnergy(liftTorque + -1.0f * inductionTorque + -1.0f * aerodynamicDragTorque +
                    -1.0f * this._data.getFrictionalDrag());

            // And create some coolant

            profiler.popPush("Condensate");

            if (vaporAmount > 0) {

                fc.onCondensation(vaporAmount, ventSetting.isVentAll(), this._turbine.getVariant());
                this._data.setFluidConsumedLastTick(vaporAmount);
            }

            profiler.pop();
        }
    }

    //region internals
    //region turbine update

    /**
     * Turbine UPDATE
     * Generate energy, internally. Will be multiplied by the config settings "powerProductionMultiplier" and "turbinePowerProductionMultiplier"
     *
     * @param rawEnergy energy to generate, before multipliers
     */
    private void generateEnergy(double rawEnergy) {

        rawEnergy = rawEnergy * Config.COMMON.general.powerProductionMultiplier.get() *
                Config.COMMON.turbine.turbinePowerProductionMultiplier.get();

        this._energyBuffer.modifyEnergyStored(rawEnergy);
        this._data.changeEnergyGeneratedLastTick(rawEnergy);
    }

    //endregion

    private void resetStats() {

        this._data.setEnergyGeneratedLastTick(0f);
        this._data.setFluidConsumedLastTick(0);
        this._data.setRotorEfficiencyLastTick(1.0f);
    }

    private final ITurbineReader _turbine;
    private final TurbineData _data;
    private final EnergyBuffer _energyBuffer;

    //endregion
}
