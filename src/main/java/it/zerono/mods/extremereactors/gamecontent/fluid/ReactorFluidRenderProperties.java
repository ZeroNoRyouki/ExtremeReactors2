package it.zerono.mods.extremereactors.gamecontent.fluid;

import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.zerocore.lib.fluid.SimpleFluidTypeRenderProperties;

public class ReactorFluidRenderProperties
        extends SimpleFluidTypeRenderProperties {

    public ReactorFluidRenderProperties(ReactorFluidType type) {
        super(type.getTintColor(), CommonConstants.FLUID_TEXTURE_SOURCE_WATER, CommonConstants.FLUID_TEXTURE_FLOWING_WATER,
                CommonConstants.FLUID_TEXTURE_OVERLAY_WATER);
    }
}
