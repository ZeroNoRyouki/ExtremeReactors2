/*
 *
 * TurbineVariant.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import it.zerono.mods.extremereactors.config.Config;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.TurbinePartType;
import it.zerono.mods.zerocore.lib.CodeHelper;
import net.minecraft.block.Block;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

//TODO fix values
public enum TurbineVariant
    implements IMultiblockTurbineVariant {

    Basic(TurbineVariant.Builder.create(5)
            .setTranslationKey("variant.bigreactors.turbine.basic")
            .setBlockPropertiesFixer(bp -> bp.hardnessAndResistance(3.0F, 6.0F))
            .setPartEnergyCapacity(10000)
            .setEnergyGenerationEfficiency(0.8f)
            .setMaxEnergyExtractionRate(50000)
            .setRadiationAttenuation(0.85f)
            .setResidualRadiationAttenuation(0.1f)
            .setBaseFluidPerBlade(25) // mB
            .setRotorDragCoefficient(0.01f)
            .setMaxRotorSpeed(2000.0f)
            .setRotorBladeMass(10)
            .setRotorShaftMass(10)
            .setPartCompatibilityTest(TurbineVariant::isBasicPart)),

    Reinforced(TurbineVariant.Builder.create(1000) // using 1000 here so the config values will win
            .setTranslationKey("variant.bigreactors.turbine.reinforced")
            .setBlockPropertiesFixer(bp -> bp.hardnessAndResistance(6.0F, 6.0F))
            .setPartEnergyCapacity(30000) //TODO new value
            .setEnergyGenerationEfficiency(0.75f) //TODO new value
            .setMaxEnergyExtractionRate(5000000) //TODO new value
            .setRadiationAttenuation(0.75f)
            .setResidualRadiationAttenuation(0.15f)
            .setBaseFluidPerBlade(25) // mB //TODO new value
            .setRotorDragCoefficient(0.01f) //TODO new value
            .setMaxRotorSpeed(2000.0f) //TODO new value
            .setRotorBladeMass(10) //TODO new value
            .setRotorShaftMass(10) //TODO new value
            .setPartCoolantCapacity(4567) //TODO new value
            .setMaxCoolantCapacity(200000) //TODO new value
            .setVaporGenerationEfficiency(0.85f)),
    ;

    public boolean isPartCompatible(final TurbinePartType partType) {
        return this._partTest.test(partType);
    }

    //region IMultiblockGeneratorVariant

    @Override
    public int getId() {
        return this.ordinal();
    }

    @Override
    public String getName() {
        return CodeHelper.neutralLowercase(this.name());
    }

    @Override
    public String getTranslationKey() {
        return this._translationKey;
    }

    @Override
    public Block.Properties getBlockProperties() {
        return this._blockPropertiesFixer.apply(this.getDefaultBlockProperties());
    }

    @Override
    public int getMaximumXSize() {
        return Math.min(Config.COMMON.turbine.maxTurbineSize.get(), this._maxX);
    }

    @Override
    public int getMaximumZSize() {
        return Math.min(Config.COMMON.turbine.maxTurbineSize.get(), this._maxZ);
    }

    @Override
    public int getMaximumYSize() {
        return Math.min(Config.COMMON.turbine.maxTurbineHeight.get(), this._maxY);
    }

    @Override
    public int getMinimumPartsCount() {
        return this._minParts;
    }

    @Override
    public int getPartEnergyCapacity() {
        return this._partEnergyCapacity;
    }

    @Override
    public float getEnergyGenerationEfficiency() {
        return this._energyGenerationEfficiency;
    }

    @Override
    public double getMaxEnergyExtractionRate() {
        return this._maxEnergyExtractionRate;
    }

    @Override
    public float getRadiationAttenuation() {
        return this._radiationAttenuation;
    }

    @Override
    public float getResidualRadiationAttenuation() {
        return this._residualRadiationAttenuation;
    }

    @Override
    public int getRotorShaftMass() {
        return this._rotorShaftMass;
    }

    @Override
    public int getRotorBladeMass() {
        return this._rotorBladeMass;
    }

    @Override
    public float getMaxRotorSpeed() {
        return this._maxRotorSpeed;
    }

    @Override
    public float getRotorDragCoefficient() {
        return this._rotorDragCoefficient;
    }

    @Override
    public int getBaseFluidPerBlade() {
        return this._baseFluidPerBlade;
    }

    @Override
    public int getMaxPermittedFlow() {
        return this._maxPermittedFlow;
    }

    @Override
    public int getPartCoolantCapacity() {
        return this._partCoolantCapacity;
    }

    @Override
    public int getMaxCoolantCapacity() {
        return this._maxCoolantCapacity;
    }

    @Override
    public float getVaporGenerationEfficiency() {
        return this._vaporGenerationEfficiency;
    }

    //endregion
    //region internals

    TurbineVariant(final Builder builder) {

        this._translationKey = builder._translationKey;
        this._blockPropertiesFixer = null != builder._blockPropertiesFixer ? builder._blockPropertiesFixer : bp -> bp;
        this._maxX = builder._maxX;
        this._maxY = builder._maxY;
        this._maxZ = builder._maxZ;
        this._minParts = builder._minParts;
        this._partEnergyCapacity = builder._partEnergyCapacity;
        this._energyGenerationEfficiency = builder._energyGenerationEfficiency;
        this._maxEnergyExtractionRate = builder._maxEnergyExtractionRate;
        this._radiationAttenuation = builder._radiationAttenuation;
        this._residualRadiationAttenuation = builder._residualRadiationAttenuation;
        this._partCoolantCapacity = builder._partCoolantCapacity;
        this._maxCoolantCapacity = builder._maxCoolantCapacity;
        this._vaporGenerationEfficiency = builder._vaporGenerationEfficiency;
        this._rotorShaftMass = builder._rotorShaftMass;
        this._rotorBladeMass = builder._rotorBladeMass;
        this._maxRotorSpeed = builder._maxRotorSpeed;
        this._rotorDragCoefficient = builder._rotorDragCoefficient;
        this._baseFluidPerBlade = builder._baseFluidPerBlade;
        this._maxPermittedFlow = builder._maxPermittedFlow;
        this._partTest = builder._partTest;
    }

    private static boolean isBasicPart(final TurbinePartType partType) {
        return !BASIC_INVALID_PARTS.contains(partType);
    }

    private static final class Builder {

        public static Builder create(final int maxSize) {
            return new Builder(maxSize, maxSize, maxSize);
        }

        public static Builder create(final int maxWidth, final int maxHeight) {
            return new Builder(maxWidth, maxHeight, maxWidth);
        }

        public static Builder create(final int maxX, final int maxY, final int maxZ) {
            return new Builder(maxX, maxY, maxZ);
        }

        public Builder setTranslationKey(final String key) {

            this._translationKey = Objects.requireNonNull(key);
            return this;
        }

        public Builder setMinimumPartsCount(final int count) {

            Preconditions.checkArgument(count >= 26);
            this._minParts = count;
            return this;
        }

        public Builder setPartEnergyCapacity(final int capacity) {

            Preconditions.checkArgument(capacity > 0);
            this._partEnergyCapacity = capacity;
            return this;
        }

        public Builder setEnergyGenerationEfficiency(final float efficiency) {

            Preconditions.checkArgument(efficiency > 0.0f && efficiency < 1.0f);
            this._energyGenerationEfficiency = efficiency;
            return this;
        }

        public Builder setMaxEnergyExtractionRate(final double rate) {

            Preconditions.checkArgument(rate > 0.0);
            this._maxEnergyExtractionRate = rate;
            return this;
        }

        public Builder setRadiationAttenuation(final float attenuation) {

            Preconditions.checkArgument(attenuation > 0.0f && attenuation < 1.0f);
            this._radiationAttenuation = attenuation;
            return this;
        }

        public Builder setResidualRadiationAttenuation(final float attenuation) {

            Preconditions.checkArgument(attenuation > 0.0f && attenuation < 1.0f);
            this._residualRadiationAttenuation = attenuation;
            return this;
        }

        public Builder setPartCoolantCapacity(final int capacity) {

            Preconditions.checkArgument(capacity > 0);
            this._partCoolantCapacity = capacity;
            return this;
        }

        public Builder setMaxCoolantCapacity(final int capacity) {

            Preconditions.checkArgument(capacity > 0);
            this._maxCoolantCapacity = capacity;
            return this;
        }

        public Builder setVaporGenerationEfficiency(final float efficiency) {

            Preconditions.checkArgument(efficiency > 0.0f && efficiency < 1.0f);
            this._vaporGenerationEfficiency = efficiency;
            return this;
        }

        public Builder setBlockPropertiesFixer(final Function<Block.Properties, Block.Properties> fixer) {

            this._blockPropertiesFixer = fixer;
            return this;
        }

        public Builder setPartCompatibilityTest(final Predicate<TurbinePartType> test) {

            this._partTest = test;
            return this;
        }

        public Builder setRotorShaftMass(final int mass) {

            this._rotorShaftMass = mass;
            return this;
        }

        public Builder setRotorBladeMass(final int mass) {

            this._rotorBladeMass = mass;
            return this;
        }

        public Builder setMaxRotorSpeed(final float speed) {

            this._maxRotorSpeed = speed;
            return this;
        }

        public Builder setRotorDragCoefficient(final float coefficient) {

            this._rotorDragCoefficient = coefficient;
            return this;
        }

        public Builder setBaseFluidPerBlade(final int fluidPerBlade) {

            this._baseFluidPerBlade = fluidPerBlade;
            return this;
        }

        public Builder setMaxPermittedFlow(final int maxFlow) {

            this._maxPermittedFlow = maxFlow;
            return this;
        }

        //region internals

        private Builder(final int maxX, final int maxY, final int maxZ) {

            this._translationKey = "";
            this._maxX = maxX;
            this._maxY = maxY;
            this._maxZ = maxZ;
            this._minParts = 27;
            this._blockPropertiesFixer = null;
            this._partTest = turbinePartType -> true;
        }

        private String _translationKey;

        private final int _maxX;
        private final int _maxY;
        private final int _maxZ;
        private int _minParts;

        private int _partEnergyCapacity;
        private float _energyGenerationEfficiency;
        private double _maxEnergyExtractionRate;
        private int _partCoolantCapacity;
        private int _maxCoolantCapacity;
        private float _vaporGenerationEfficiency;

        private float _radiationAttenuation;
        private float _residualRadiationAttenuation;

        private int _rotorShaftMass;
        private int _rotorBladeMass;
        private float _maxRotorSpeed;
        private float _rotorDragCoefficient;
        private int _baseFluidPerBlade;
        private int _maxPermittedFlow;

        private Function<Block.Properties, Block.Properties> _blockPropertiesFixer;

        private Predicate<TurbinePartType> _partTest;

        //endregion
    }

    private static final Set<TurbinePartType> BASIC_INVALID_PARTS = Sets.immutableEnumSet(TurbinePartType.ComputerPort,
            TurbinePartType.ActiveFluidPortForge, TurbinePartType.PassiveFluidPortForge, TurbinePartType.CreativeFluidPort);

    private final String _translationKey;

    private final int _maxX;
    private final int _maxY;
    private final int _maxZ;
    private final int _minParts;

    private final int _partEnergyCapacity;
    private final float _energyGenerationEfficiency;
    private final double _maxEnergyExtractionRate;
    private final int _partCoolantCapacity;
    private final int _maxCoolantCapacity;
    private final float _vaporGenerationEfficiency;

    private final float _radiationAttenuation;
    private final float _residualRadiationAttenuation;

    private final int _rotorShaftMass;
    private final int _rotorBladeMass;
    private final float _maxRotorSpeed;
    private final float _rotorDragCoefficient;
    private final int _baseFluidPerBlade;
    private final int _maxPermittedFlow;

    private final Function<Block.Properties, Block.Properties> _blockPropertiesFixer;

    private final Predicate<TurbinePartType> _partTest;

    //endregion
}
