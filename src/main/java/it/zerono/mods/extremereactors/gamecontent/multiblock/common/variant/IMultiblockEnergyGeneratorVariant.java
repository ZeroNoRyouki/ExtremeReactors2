package it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant;

import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.multiblock.variant.IMultiblockDimensionVariant;

public interface IMultiblockEnergyGeneratorVariant
        extends IMultiblockDimensionVariant {

    int getPartEnergyCapacity();

    float getEnergyGenerationEfficiency();

    WideAmount getMaxEnergyExtractionRate();

    double getChargerMaxRate();
}
