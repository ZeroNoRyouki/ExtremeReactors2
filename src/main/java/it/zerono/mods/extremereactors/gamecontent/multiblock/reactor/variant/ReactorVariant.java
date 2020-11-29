/*
 *
 * ReactorVariant.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import it.zerono.mods.extremereactors.config.Config;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactorPartType;
import it.zerono.mods.zerocore.lib.CodeHelper;
import net.minecraft.block.Block;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public enum ReactorVariant
        implements IMultiblockReactorVariant {

    Basic(Builder.create(5)
            .setTranslationKey("variant.bigreactors.reactor.basic")
            .setBlockPropertiesFixer(bp -> bp.hardnessAndResistance(3.0F, 6.0F))
            .setPartEnergyCapacity(10000)
            .setEnergyGenerationEfficiency(0.8f)
            .setMaxEnergyExtractionRate(50000)
            .setRadiationAttenuation(0.85f)
            .setResidualRadiationAttenuation(0.1f)
            .setSolidFuelConversionEfficiency(0.5f)
            .setPartCompatibilityTest(ReactorVariant::isBasicPart)),

    Reinforced(Builder.create(1000) // using 1000 here so the config values will win
            .setTranslationKey("variant.bigreactors.reactor.reinforced")
            .setBlockPropertiesFixer(bp -> bp.hardnessAndResistance(6.0F, 6.0F))
            .setPartEnergyCapacity(30000)
            .setEnergyGenerationEfficiency(0.75f)
            .setMaxEnergyExtractionRate(5000000)
            .setRadiationAttenuation(0.75f)
            .setResidualRadiationAttenuation(0.15f)
            .setPartFluidCapacity(4567)
            .setMaxFluidCapacity(200000)
            .setVaporGenerationEfficiency(0.85f)
            .setSolidFuelConversionEfficiency(0.75f)
            .setFluidFuelConversionEfficiency(0.8f)),
    ;

    public boolean isPartCompatible(final ReactorPartType partType) {
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
        return Math.min(Config.COMMON.reactor.maxReactorSize.get(), this._maxX);
    }

    @Override
    public int getMaximumZSize() {
        return Math.min(Config.COMMON.reactor.maxReactorSize.get(), this._maxZ);
    }

    @Override
    public int getMaximumYSize() {
        return Math.min(Config.COMMON.reactor.maxReactorHeight.get(), this._maxY);
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
    public float getSolidFuelConversionEfficiency() {
        return this._solidFuelConversionEfficiency;
    }

    @Override
    public float getFluidFuelConversionEfficiency() {
        return this._fluidFuelConversionEfficiency;
    }

    @Override
    public int getPartFluidCapacity() {
        return this._partFluidCapacity;
    }

    @Override
    public int getMaxFluidCapacity() {
        return this._maxFluidCapacity;
    }

    @Override
    public float getVaporGenerationEfficiency() {
        return this._vaporGenerationEfficiency;
    }

    //endregion
    //region internals

    ReactorVariant(final Builder builder) {

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
        this._solidFuelConversionEfficiency = builder._solidFuelConversionEfficiency;
        this._fluidFuelConversionEfficiency = builder._fluidFuelConversionEfficiency;
        this._partFluidCapacity = builder._partFluidCapacity;
        this._maxFluidCapacity = builder._maxFluidCapacity;
        this._vaporGenerationEfficiency = builder._vaporGenerationEfficiency;
        this._partTest = builder._partTest;
    }

    private static boolean isBasicPart(final ReactorPartType partType) {
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

        public Builder setSolidFuelConversionEfficiency(final float efficiency) {

            Preconditions.checkArgument(efficiency > 0.0f && efficiency < 1.0f);
            this._solidFuelConversionEfficiency = efficiency;
            return this;
        }

        public Builder setFluidFuelConversionEfficiency(final float efficiency) {

            Preconditions.checkArgument(efficiency > 0.0f && efficiency < 1.0f);
            this._fluidFuelConversionEfficiency = efficiency;
            return this;
        }

        public Builder setPartFluidCapacity(final int capacity) {

            Preconditions.checkArgument(capacity > 0);
            this._partFluidCapacity = capacity;
            return this;
        }

        public Builder setMaxFluidCapacity(final int capacity) {

            Preconditions.checkArgument(capacity > 0);
            this._maxFluidCapacity = capacity;
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

        public Builder setPartCompatibilityTest(final Predicate<ReactorPartType> test) {

            this._partTest = test;
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
            this._partTest = reactorPartType -> true;
        }

        private String _translationKey;

        private final int _maxX;
        private final int _maxY;
        private final int _maxZ;
        private int _minParts;

        private int _partEnergyCapacity;
        private float _energyGenerationEfficiency;
        private double _maxEnergyExtractionRate;
        private int _partFluidCapacity;
        private int _maxFluidCapacity;
        private float _vaporGenerationEfficiency;

        private float _radiationAttenuation;
        private float _residualRadiationAttenuation;
        private float _solidFuelConversionEfficiency;
        private float _fluidFuelConversionEfficiency;

        private Function<Block.Properties, Block.Properties> _blockPropertiesFixer;

        private Predicate<ReactorPartType> _partTest;

        //endregion
    }

    private static final Set<ReactorPartType> BASIC_INVALID_PARTS = Sets.immutableEnumSet(ReactorPartType.ComputerPort,
            ReactorPartType.ActiveFluidPortForge, ReactorPartType.PassiveFluidPortForge,
            ReactorPartType.ActiveFluidPortMekanism, ReactorPartType.PassiveFluidPortMekanism,
            ReactorPartType.CreativeWaterGenerator);

    private final String _translationKey;

    private final int _maxX;
    private final int _maxY;
    private final int _maxZ;
    private final int _minParts;

    private final int _partEnergyCapacity;
    private final float _energyGenerationEfficiency;
    private final double _maxEnergyExtractionRate;
    private final int _partFluidCapacity;
    private final int _maxFluidCapacity;
    private final float _vaporGenerationEfficiency;

    private final float _radiationAttenuation;
    private final float _residualRadiationAttenuation;
    private final float _solidFuelConversionEfficiency;
    private final float _fluidFuelConversionEfficiency;

    private final Function<Block.Properties, Block.Properties> _blockPropertiesFixer;

    private final Predicate<ReactorPartType> _partTest;

    //endregion
}
