/*
 *
 * TurbineData.java
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

import it.zerono.mods.extremereactors.api.turbine.CoilMaterial;
import it.zerono.mods.extremereactors.config.Config;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.IMultiblockTurbineVariant;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.IDebuggable;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import it.zerono.mods.zerocore.lib.data.nbt.NBTHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.LogicalSide;

import java.util.function.Consumer;

public class TurbineData
    implements ISyncableEntity, IDebuggable {

    public TurbineData(final IMultiblockTurbineVariant variant) {

        this._variant = variant;
        this._ventSetting = VentSetting.getDefault();
        this._inductorEngaged = false;

        this._rotorEnergy = 0f;
        this._maxIntakeRate = variant.getMaxPermittedFlow();

        this._bladeSurfaceArea = 0;
        this._rotorMass = 0;
        this._coilSize = 0;
        this._energyGeneratedLastTick = 0f;
        this._fluidConsumedLastTick = 0;
        this._rotorEfficiencyLastTick = 1f;

        this._inductorDragCoefficient = INDUCTOR_BASE_DRAG_COEFFICIENT;
        this._inductionEfficiency = 0.5f;
        this._inductionEnergyExponentBonus = 1.0f;

        this._rotorDragCoefficient = variant.getRotorDragCoefficient();
        this._bladeDrag = 0.00025f;

        this._inputFluidPerBlade = (int)Math.floor(variant.getBaseFluidPerBlade() * Config.COMMON.turbine.turbineFluidPerBladeMultiplier.get());
    }

    /**
     * Reset the internal data
     * --- FOR TESTING PURPOSES ONLY ---
     */
    public void reset() {

        this._ventSetting = VentSetting.getDefault();
        this._inductorEngaged = false;

        this._rotorEnergy = 0f;
        this._maxIntakeRate = this._variant.getMaxPermittedFlow();

        this._bladeSurfaceArea = 0;
        this._rotorMass = 0;
        this._coilSize = 0;
        this._energyGeneratedLastTick = 0f;
        this._fluidConsumedLastTick = 0;
        this._rotorEfficiencyLastTick = 1f;

        this._inductorDragCoefficient = INDUCTOR_BASE_DRAG_COEFFICIENT;
        this._inductionEfficiency = 0.5f;
        this._inductionEnergyExponentBonus = 1.0f;

        this._bladeDrag = 0.00025f;
    }

    public void onTurbineAssembled() {

    }

    public void onTurbineDisassembled() {

        this._bladeSurfaceArea = 0;
        this._rotorMass = 0;
        this._coilSize = 0;
        this._rotorEnergy = 0.0f; // Kill energy when machines get broken by players/explosions
    }

    public void onAssimilate(final TurbineData other) {
        this.setRotorEnergy(Math.max(this.getRotorEnergy(), other.getRotorEnergy()));
    }

    public void update(final ITurbineEnvironment environment, final BlockPos minInterior, final BlockPos maxInterior,
                       final IMultiblockTurbineVariant variant) {

        int rotorMass = 0;
        int bladeSurfaceArea = 0;

        // Loop over interior space. Calculate mass and blade area of rotor and size of coils

        int maxX = maxInterior.getX();
        int maxY = maxInterior.getY();
        int maxZ = maxInterior.getZ();

        final CoilStats coilStats = new CoilStats();

        for (int x = minInterior.getX(); x <= maxX; ++x) {

            for (int y = minInterior.getY(); y <= maxY; ++y) {

                for (int z = minInterior.getZ(); z <= maxZ; ++z) {

                    final BlockPos position = new BlockPos(x, y, z);

                    switch (environment.getRotorComponentTypeAt(position)) {

                        case Blade:

                            rotorMass += variant.getRotorBladeMass();
                            bladeSurfaceArea += 1;
                            break;

                        case Shaft:

                            rotorMass += variant.getRotorShaftMass();
                            break;

                        case CandidateCoil:

                            environment.getCoilBlock(position).ifPresent(coilStats);
                            break;
                    }
                } // end z
            } // end y
        } // end x loop - looping over interior

        // Precalculate some stuff now that we know how big the rotor and blades are

        this._frictionalDrag = rotorMass * this._rotorDragCoefficient * Config.COMMON.turbine.turbineMassDragMultiplier.get().floatValue();
        this._bladeDrag = BASE_BLADE_DRAG_COEFFICIENT * bladeSurfaceArea * Config.COMMON.turbine.turbineAeroDragMultiplier.get().floatValue();

        if (coilStats.Size <= 0) {

            // Uh. No coil? Fine.

            this._inductionEfficiency = 0.0f;
            this._inductionEnergyExponentBonus = 1.0f;
            this._inductorDragCoefficient = 0.0f;

        } else {

            this._inductionEfficiency = (coilStats.Efficiency * 0.33f) / coilStats.Size;
            this._inductionEnergyExponentBonus = Math.max(1.0f, (coilStats.Bonus / coilStats.Size));
            this._inductorDragCoefficient = (coilStats.DragCoefficient / coilStats.Size) * this.getInductorBaseDragCoefficient();
        }

        this._coilSize = coilStats.Size;
        this._rotorMass = rotorMass;
        this._bladeSurfaceArea = bladeSurfaceArea;
    }

    //region getters

    public float getInductorBaseDragCoefficient() {
        return INDUCTOR_BASE_DRAG_COEFFICIENT;
    }

    public float getRotorEnergy() {
        return this._rotorEnergy;
    }

    public boolean isInductorEngaged() {
        return this._inductorEngaged;
    }

    public VentSetting getVentSetting() {
        return this._ventSetting;
    }

    public int getMaxIntakeRate() {
        return this._maxIntakeRate;
    }

    public int getBladeSurfaceArea() {
        return this._bladeSurfaceArea;
    }

    public int getRotorMass() {
        return this._rotorMass;
    }

    public int getCoilSize() {
        return this._coilSize;
    }

    public float getInductorDragCoefficient() {
        return this._inductorDragCoefficient;
    }

    public float getInductionEfficiency() {
        return this._inductionEfficiency;
    }

    public float getInductionEnergyExponentBonus() {
        return this._inductionEnergyExponentBonus;
    }

    public float getBladeDrag() {
        return this._bladeDrag;
    }

    public float getFrictionalDrag() {
        return this._frictionalDrag;
    }

    public int getInputFluidPerBlade() {
        return this._inputFluidPerBlade;
    }

    public double getEnergyGeneratedLastTick() {
        return this._energyGeneratedLastTick;
    }

    public int getFluidConsumedLastTick() {
        return this._fluidConsumedLastTick;
    }

    public float getRotorEfficiencyLastTick() {
        return this._rotorEfficiencyLastTick;
    }

    //endregion
    //region setters

    public void setRotorEnergy(final float energy) {

        if (!Float.isNaN(energy) && !Float.isInfinite(energy)) {
            this._rotorEnergy = Math.max(0f, energy);
        }
    }

    public void changeRotorEnergy(final float delta) {
        this.setRotorEnergy(this.getRotorEnergy() + delta);
    }

    public void setInductorEngaged(final boolean engaged) {
        this._inductorEngaged = engaged;
    }

    public void setVentSetting(final VentSetting status) {
        this._ventSetting = status;
    }

    public void setMaxIntakeRate(final int rate) {
        this._maxIntakeRate = Math.min(this._variant.getMaxPermittedFlow(), Math.max(0, rate));
    }

    public void setEnergyGeneratedLastTick(final double energy) {
        this._energyGeneratedLastTick = energy;
    }

    void changeEnergyGeneratedLastTick(final double delta) {
        this._energyGeneratedLastTick += delta;
    }

    public void setFluidConsumedLastTick(final int fluid) {
        this._fluidConsumedLastTick = fluid;
    }

    public void setRotorEfficiencyLastTick(final float efficiency) {
        this._rotorEfficiencyLastTick = efficiency;
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

        this.setVentSetting(NBTHelper.nbtGetEnum(data, "vent", VentSetting::valueOf, VentSetting.getDefault()));

        if (data.contains("rotorenergy")) {
            this.setRotorEnergy(data.getFloat("rotorenergy"));
        }

        if (data.contains("maxintake")) {
            this.setMaxIntakeRate(data.getInt("maxintake"));
        }

        if (data.contains("inductorengaged")) {
            this.setInductorEngaged(data.getBoolean("inductorengaged"));
        }

        if (data.contains("energylast")) {
            this.setEnergyGeneratedLastTick(data.getDouble("energylast"));
        }

        if (data.contains("fluidlast")) {
            this.setFluidConsumedLastTick(data.getInt("fluidlast"));
        }

        if (data.contains("rotorlast")) {
            this.setRotorEfficiencyLastTick(data.getFloat("rotorlast"));
        }
    }

    /**
     * Sync the entity data to the given {@link CompoundNBT}
     *
     * @param data       the {@link CompoundNBT} to write to
     * @param syncReason the reason why the synchronization is necessary
     * @return the {@link CompoundNBT} the data was written to (usually {@code data})
     */
    @Override
    public CompoundNBT syncDataTo(CompoundNBT data, SyncReason syncReason) {

        NBTHelper.nbtSetEnum(data, "vent", this.getVentSetting());

        data.putFloat("rotorenergy", this.getRotorEnergy());
        data.putInt("maxintake", this.getMaxIntakeRate());
        data.putBoolean("inductorengaged", this.isInductorEngaged());
        data.putDouble("energylast", this.getEnergyGeneratedLastTick());
        data.putInt("fluidlast", this.getFluidConsumedLastTick());
        data.putFloat("rotorlast", this.getRotorEfficiencyLastTick());

        return data;
    }

    //endregion
    //region IDebuggable

    /**
     * @param side the LogicalSide of the caller
     * @param messages add your debug messages here
     */
    @Override
    public void getDebugMessages(final LogicalSide side, final IDebugMessages messages) {

        messages.addUnlocalized("Last Tick: EnergyGenerated:%.5f; FluidConsumed:%.5f; RotorEfficiency:%.5f",
                this.getEnergyGeneratedLastTick(), this.getFluidConsumedLastTick(), this.getRotorEfficiencyLastTick());

        messages.addUnlocalized("Inductor: %s; DragCoefficient:%.5f; Efficiency:%.5f; ExponentBonus:%.5f; CoilSize:%d",
                this.isInductorEngaged() ? "on" : "off",  this.getInductorDragCoefficient(),
                this.getInductionEfficiency(), this.getInductionEnergyExponentBonus(), this.getCoilSize());

        messages.addUnlocalized("Rotor: Energy:%.5f; Mass:%d; DragCoefficient:%.5f; BladeSurfaceArea:%d; BladeDrag:%.5f; FrictionalDrag:%.5f",
                this.getRotorEnergy(), this.getRotorMass(), this._rotorDragCoefficient, this.getBladeSurfaceArea(),
                this.getBladeDrag(), this.getFrictionalDrag());

        messages.addUnlocalized("Fluids: MaxIntakeRate:%d; FluidPerBlade:%d; VentStatus:%s",
                this.getMaxIntakeRate(), this.getInputFluidPerBlade(), this.getVentSetting());
    }

    //endregion
    //region internals

    // FE/t extracted per coil block, multiplied by rotor speed squared.
    private static final float INDUCTOR_BASE_DRAG_COEFFICIENT = 0.1f * Config.COMMON.turbine.turbineCoilDragMultiplier.get().floatValue();

    // FE/t base lost to aero drag per blade block. Includes a 50% reduction to factor in constant parts of the drag equation
    private static final float BASE_BLADE_DRAG_COEFFICIENT = 0.00025f;

    private final IMultiblockTurbineVariant _variant;

    private boolean _inductorEngaged;
    private float _rotorEnergy;
    private VentSetting _ventSetting;
    private int _maxIntakeRate;

    // Rotor and coils
    private int _bladeSurfaceArea; // # of blocks that are blades
    private int _rotorMass; // 10 = 1 standard block-weight
    private int _coilSize;  // number of blocks in the coils

    // Inductor dynamic constants - get from a table on assembly
    private float _inductorDragCoefficient;
    private float _inductionEfficiency; // Final energy rectification efficiency. Averaged based on coil material and shape. 0.25-0.5 = iron, 0.75-0.9 = diamond, 1 = perfect.
    private float _inductionEnergyExponentBonus; // Exponential bonus to energy generation. Use this for very rare materials or special constructs.

    // Rotor dynamic constants
    private final float _rotorDragCoefficient; // FE/t lost to friction per unit of mass in the rotor.
    private float _bladeDrag = 0.00025f; // FE/t lost to friction, multiplied by rotor speed squared.
    private float _frictionalDrag;

    // Penalize suboptimal shapes with worse drag (i.e. increased drag without increasing lift)
    // Suboptimal is defined as "not a christmas-tree shape". At worst, drag is increased 4x.

    private final int _inputFluidPerBlade;

    // Stats
    private double _energyGeneratedLastTick;
    private int _fluidConsumedLastTick;
    private float _rotorEfficiencyLastTick;

    private static class CoilStats
            implements Consumer<CoilMaterial> {

        public float Efficiency = 0f;
        public float Bonus = 0f;
        public float DragCoefficient = 0f;
        public int Size;

        public CoilStats() {

            this.Efficiency = 0.0f;
            this.Bonus = 0.0f;
            this.DragCoefficient = 0.0f;
            this.Size = 0;
        }

        /**
         * Performs this operation on the given argument.
         *
         * @param material the input argument
         */
        @Override
        public void accept(final CoilMaterial material) {

            this.Efficiency += material.getEfficiency();
            this.Bonus += material.getBonus();
            this.DragCoefficient += material.getEnergyExtractionRate();
            this.Size += 1;
        }
    }

    //endregion
}
