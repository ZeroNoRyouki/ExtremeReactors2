/*
 *
 * ReactorLogic.java
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

import it.zerono.mods.extremereactors.api.radiation.RadiationPacket;
import it.zerono.mods.extremereactors.api.reactor.IHeatEntity;
import it.zerono.mods.extremereactors.api.reactor.radiation.EnergyConversion;
import it.zerono.mods.extremereactors.api.reactor.radiation.IrradiationData;
import it.zerono.mods.extremereactors.config.Config;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.IFluidContainer;
import it.zerono.mods.zerocore.lib.data.nbt.IMergeableEntity;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import it.zerono.mods.zerocore.lib.energy.EnergyBuffer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import java.util.Optional;

public class ReactorLogic
        implements ISyncableEntity, IMergeableEntity {

    ReactorLogic(final IReactorMachine reactor, final EnergyBuffer energyBuffer) {

        this._reactor = reactor;
        this._energyBuffer = energyBuffer;
        this.setFertility(1f);
    }

    public float getFertility() {
        return this._fertility;
    }
    
    /**
     * Main update logic
     */
    public boolean update() {

        final ProfilerFiller profiler = this._reactor.getWorld().getProfiler();
        final IHeat reactorHeat = this.getReactorHeat();

        //TODO variants

        if (Double.isNaN(reactorHeat.getAsDouble())) {
            reactorHeat.set(0);
        }

        final double startingReactorHeat = reactorHeat.getAsDouble();
        final double startingEnergy = this._energyBuffer.getEnergyStored();

        this.getUiStats().setAmountGeneratedLastTick(0);
        this.getUiStats().setFuelConsumedLastTick(0);

        //////////////////////////////////////////////////////////////////////////////
        // IRRADIATION
        //////////////////////////////////////////////////////////////////////////////

        // - Irradiate from the next Fuel Rod
        profiler.push("Irradiate");
        this.performIrradiation();
        // - Allow radiation to decay even when reactor is off.
        profiler.popPush("Decay");
        this.performRadiationDecay(this._reactor.isMachineActive());

        //////////////////////////////////////////////////////////////////////////////
        // REFUELING
        //////////////////////////////////////////////////////////////////////////////

        profiler.popPush("Refueling");

        boolean reactantsChanged;

        reactantsChanged = this._reactor.performRefuelingCycle();
        reactantsChanged |= this._reactor.performInputCycle();

        //////////////////////////////////////////////////////////////////////////////
        // HEAT TRANSFERS
        //////////////////////////////////////////////////////////////////////////////

        profiler.popPush("Heat");
        // - Fuel Pool <> Reactor Environment
        this.transferHeatBetweenFuelAndReactor();
        // - If we have a temperature differential between environment and coolant system, move heat between them
        this.transferHeatBetweenReactorAndCoolant();
        // - Do passive heat loss - this is always versus external environment
        this.performPassiveHeatLoss();
        // - Prevent cryogenics
        reactorHeat.resetIfNegative();
        this.getFuelHeat().resetIfNegative();

        //////////////////////////////////////////////////////////////////////////////
        // SEND POWER/GAS OUT
        //////////////////////////////////////////////////////////////////////////////

        profiler.popPush("Distribute"); // close "Generate"
        this._reactor.performOutputCycle();
        profiler.pop();

        //////////////////////////////////////////////////////////////////////////////
        //TODO: Overload/overheat
        //////////////////////////////////////////////////////////////////////////////

        return reactantsChanged || startingReactorHeat != reactorHeat.getAsDouble() ||
                startingEnergy != this._energyBuffer.getEnergyStored();
    }

    void reset() {
        this.setFertility(1f);
    }

    //region ISyncableEntity

    /**
     * Sync the entity data from the given NBT compound
     *
     * @param data       the data
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public void syncDataFrom(CompoundTag data, ISyncableEntity.SyncReason syncReason) {
        this.setFertility(data.getFloat("fertility"));
    }

    /**
     * Sync the entity data to the given NBT compound
     *
     * @param data       the data
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public CompoundTag syncDataTo(CompoundTag data, ISyncableEntity.SyncReason syncReason) {

        data.putFloat("fertility", this._fertility);
        return data;
    }

    //endregion
    //region IMergeableEntity

    /**
     * Sync the entity data from another IMergeableEntity
     *
     * @param other the IMergeableEntity to sync from
     */
    @Override
    public void syncDataFrom(final IMergeableEntity other) {

        if (other instanceof ReactorLogic) {
            this._fertility = Math.max(this._fertility, ((ReactorLogic)other)._fertility);
        }
    }

    //endregion
    //region internals
    //region reactor data accessors

    private IHeat getFuelHeat() {
        return this._reactor.getFuelHeat();
    }

    private IHeat getReactorHeat() {
        return this._reactor.getEnvironment().getReactorHeat();
    }

    private IFluidContainer getFluidContainer() {
        return this._reactor.getFluidContainer();
    }

    private IFuelContainer getFuelContainer() {
        return this._reactor.getFuelContainer();
    }

    private int getReactorVolume() {
        return this._reactor.getEnvironment().getReactorVolume();
    }

    private Stats getUiStats() {
        return this._reactor.getUiStats();
    }

    private int getFuelRodsCount() {
        return this._reactor.getEnvironment().getPartsCount(ReactorPartType.FuelRod);
    }

    private int getControlRodsCount() {
        return this._reactor.getEnvironment().getPartsCount(ReactorPartType.ControlRod);
    }

    //endregion
    //region reactor UPDATE

    /**
     * Reactor UPDATE
     * Select the next Fuel Dod to irradiate from and irradiate from it
     */
    private void performIrradiation() {

        if (!this._reactor.isMachineActive()) {
            return;
        }

        this._reactor.getEnvironment().getNextIrradiationSource()
                .filter(IIrradiationSource::isLinked)
                .ifPresent(this::performIrradiationFrom);
    }

    /**
     * Reactor UPDATE
     * Irradiate from a Fuel Rod
     */
    private void performIrradiationFrom(IIrradiationSource source) {

        this.radiate(/*this._reactor.getWorld(),*/ this.getFuelContainer(), source,
                this.getFuelHeat().getAsDouble(), this.getReactorHeat().getAsDouble(),
                this.getControlRodsCount()).ifPresent(data -> {

            // Assimilate results of radiation
            this.getFuelHeat().add(data.getFuelHeatChange(this.getFuelRodsCount()));
            this.getReactorHeat().add(data.getEnvironmentHeatChange(this.getReactorVolume()));
            this.getUiStats().changeFuelConsumedLastTick(data.fuelUsage);
        });
    }

    /**
     * Reactor UPDATE
     * Transfer heat between the Fuel Rods and the Reactor Environment
     */
    private void transferHeatBetweenFuelAndReactor() {

        final double temperatureDifferential = this._reactor.getFuelHeat().getAsDouble() - this.getReactorHeat().getAsDouble();

        if (temperatureDifferential > 0.01) {

            final double energyTransferred = temperatureDifferential * this._reactor.getEnvironment().getFuelToReactorHeatTransferCoefficient();
            final double fuelVolEnergy = EnergyConversion.getEnergyFromVolumeAndTemperature(this.getFuelRodsCount(),
                    this.getFuelHeat().getAsDouble()) - energyTransferred;
            final double reactorEnergy = EnergyConversion.getEnergyFromVolumeAndTemperature(this.getReactorVolume(),
                    this.getReactorHeat().getAsDouble()) + energyTransferred;

            this.getFuelHeat().set(EnergyConversion.getTemperatureFromVolumeAndEnergy(this.getFuelRodsCount(), fuelVolEnergy));
            this.getReactorHeat().set(EnergyConversion.getTemperatureFromVolumeAndEnergy(this.getReactorVolume(), reactorEnergy));
        }
    }

    /**
     * Reactor UPDATE
     * Transfer heat between the Reactor Environment and the coolant system
     */
    private void transferHeatBetweenReactorAndCoolant() {

        final double temperatureDifferential = this.getReactorHeat().getAsDouble() - this.getCoolantTemperature();

        if (temperatureDifferential > 0.01f) {

            double energyTransferred = temperatureDifferential * this._reactor.getEnvironment().getReactorToCoolantSystemHeatTransferCoefficient();
            double reactorEnergy = EnergyConversion.getEnergyFromVolumeAndTemperature(this.getReactorVolume(),
                    this.getReactorHeat().getAsDouble());

            if (this._reactor.getOperationalMode().isPassive()) {

                energyTransferred *= PASSIVE_COOLING_TRANSFER_EFFICIENCY;
                this.generateEnergy(energyTransferred * PASSIVE_COOLING_POWER_EFFICIENCY);

            } else {

                energyTransferred -= this.getFluidContainer().onAbsorbHeat(energyTransferred, this._reactor.getVariant());
                // Piggyback so we don't have useless stuff in the update packet
                this.getUiStats().setAmountGeneratedLastTick(this.getFluidContainer().getLiquidVaporizedLastTick());
            }

            reactorEnergy -= energyTransferred;
            this.getReactorHeat().set(EnergyConversion.getTemperatureFromVolumeAndEnergy(this.getReactorVolume(), reactorEnergy));
        }
    }

    /**
     * Reactor UPDATE
     * Do passive heat loss to external environment
     */
    private void performPassiveHeatLoss() {

        final double temperatureDifferential = this.getReactorHeat().getAsDouble() - this.getPassiveCoolantTemperature();

        if (temperatureDifferential > 0.000001f) {

            // Lose at least 1FE/t
            final double energyLost = Math.max(1d, temperatureDifferential * this._reactor.getEnvironment().getReactorHeatLossCoefficient());

            final double reactorNewEnergy = Math.max(0d,
                    EnergyConversion.getEnergyFromVolumeAndTemperature(this.getReactorVolume(),
                            this.getReactorHeat().getAsDouble()) - energyLost);

            this.getReactorHeat().set(EnergyConversion.getTemperatureFromVolumeAndEnergy(this.getReactorVolume(), reactorNewEnergy));
        }
    }

    /**
     * Reactor UPDATE
     * Generate energy, internally. Will be multiplied by the config settings "powerProductionMultiplier" and "reactorPowerProductionMultiplier"
     *
     * @param rawEnergy energy to generate, before multipliers
     */
    private void generateEnergy(double rawEnergy) {

        rawEnergy = rawEnergy * Config.COMMON.general.powerProductionMultiplier.get() *
                Config.COMMON.reactor.reactorPowerProductionMultiplier.get() *
                this._reactor.getVariant().getEnergyGenerationEfficiency();

        this._energyBuffer.modifyEnergyStored(rawEnergy);
        this.getUiStats().changeAmountGeneratedLastTick(rawEnergy);
    }

    //endregion
    //region irradiation

    private Optional<IrradiationData> radiate(final IFuelContainer fuelContainer, final IIrradiationSource source,
                                              final double fuelHeat, final double environmentHeat, final int numControlRods) {
        // No fuel? No radiation!
        if (fuelContainer.getFuelAmount() <= 0) {
            return Optional.empty();
        }

        // Determine radiation amount & intensity, heat amount, determine fuel usage

        // Base value for radiation production penalties. 0-1, caps at about 3000C;
        double radiationPenaltyBase = Math.exp(-15 * Math.exp(-0.0025 * fuelHeat));

        // Raw amount - what's actually in the tanks
        // Effective amount - how
        int baseFuelAmount = fuelContainer.getFuelAmount() + (fuelContainer.getWasteAmount() / 100);
        float fuelReactivity = fuelContainer.getFuelReactivity();

        // Intensity = how strong the radiation is, hardness = how energetic the radiation is (penetration)
        float rawRadIntensity = (float)baseFuelAmount * FISSION_EVENTS_PER_FUEL_UNIT;

        // Scale up the "effective" intensity of radiation, to provide an incentive for bigger reactors in general.
        float scaledRadIntensity = (float)Math.pow(rawRadIntensity, fuelReactivity);

        // Scale up a second time based on scaled amount in each fuel rod. Provides an incentive for making reactors that aren't just pancakes.
        scaledRadIntensity = (float)Math.pow(scaledRadIntensity / numControlRods, fuelReactivity) * numControlRods;

        // Apply control rod moderation of radiation to the quantity of produced radiation. 100% insertion = 100% reduction.
        float controlRodModifier = (float)(100 - source.getControlRodInsertionRatio()) / 100f;

        scaledRadIntensity = scaledRadIntensity * controlRodModifier;
        rawRadIntensity = rawRadIntensity * controlRodModifier;

        // Now nerf actual radiation production based on heat.
        float effectiveRadIntensity = scaledRadIntensity * (1f + (float)(-0.95f * Math.exp(-10f * Math.exp(-0.0012f * fuelHeat))));

        // Radiation hardness starts at 20% and asymptotically approaches 100% as heat rises.
        // This will make radiation harder and harder to capture.
        float radHardness = 0.2f + (float)(0.8 * radiationPenaltyBase);

        // Calculate based on propagation-to-self

        final float rawFuelUsage = (FUEL_PER_RADIATION_UNIT * rawRadIntensity / getFertilityModifier()) *
                Config.COMMON.general.fuelUsageMultiplier.get().floatValue(); // Not a typo. Fuel usage is thus penalized at high heats.
        final IrradiationData data = new IrradiationData();

        data.environmentEnergyAbsorption = 0f;
        data.fuelAbsorbedRadiation = 0f;
        data.fuelEnergyAbsorption = EnergyConversion.ENERGY_PER_RADIATION_UNIT * effectiveRadIntensity;

        // Propagate radiation to others

        BlockPos originCoord = source.getWorldPosition();
        BlockPos currentCoord;
        final RadiationPacket radPacket = new RadiationPacket();

        effectiveRadIntensity *= 0.25f; // We're going to do this four times, no need to repeat

        for (final Direction dir : source.getIrradiationDirections()) {

            radPacket.hardness = radHardness;
            radPacket.intensity = effectiveRadIntensity;

            int ttl = 4; //TODO variants? radPacket.intensity will be > 0 if ttl > 4? check effectiveRadIntensity too

            currentCoord = originCoord;

            while (ttl > 0 && radPacket.intensity > 0.0001f) {

                ttl--;
                currentCoord = currentCoord.relative(dir);

                this._reactor.getEnvironment().getModerator(currentCoord).moderateRadiation(data, radPacket);
            }
        }

        // Apply changes
        this._fertility += data.fuelAbsorbedRadiation;
        data.fuelAbsorbedRadiation = 0f;

        // Inform fuelContainer
        fuelContainer.onIrradiation(rawFuelUsage);
        data.fuelUsage = rawFuelUsage;

        return Optional.of(data);
    }

    private void performRadiationDecay(final boolean isReactorActive) {

        float denominator = 20f;

        if (!isReactorActive) {
            // Much slower decay when off
            denominator *= 200f;
        }

        // Fertility decay, at least 0.1 rad/t, otherwise halve it every 10 ticks
        this._fertility = Math.max(0f, this._fertility - Math.max(0.1f, this._fertility / denominator));
    }

    private float getFertilityModifier() {

        if (this._fertility <= 1f) {
            return 1f;
        } else {
            return (float)(Math.log10(this._fertility) + 1);
        }
    }

    private void setFertility(float newFertility) {

        if (Float.isNaN(newFertility) || Float.isInfinite(newFertility)) {
            this._fertility = 1f;
        } else {
            this._fertility = Math.max(newFertility, 0f);
        }
    }

    //endregion

    private double getPassiveCoolantTemperature() {
        return IHeatEntity.AMBIENT_HEAT;
    }

    private double getCoolantTemperature() {

        if (this._reactor.getOperationalMode().isPassive()) {
            return this.getPassiveCoolantTemperature();
        } else {
            return this.getFluidContainer().getLiquidTemperature(this.getReactorHeat().getAsDouble());
        }
    }

    // 50% power penalty, so this comes out as about 1/3 a basic water-cooled reactor
    private static final float PASSIVE_COOLING_POWER_EFFICIENCY = 0.5f;

    // 20% of available heat transferred per tick when passively cooled
    private static final float PASSIVE_COOLING_TRANSFER_EFFICIENCY = 0.2f;

    // fuel units used per fission event
    private static final float FUEL_PER_RADIATION_UNIT = 0.0007f;

    // 1 fission event per 100 mB
    private static final float FISSION_EVENTS_PER_FUEL_UNIT = 0.01f;

    private final IReactorMachine _reactor;
    private final EnergyBuffer _energyBuffer;

    private float _fertility;

    //endregion
}
