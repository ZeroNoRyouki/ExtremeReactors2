package it.zerono.mods.extremereactors.gamecontent.fluid;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.Reactants;
import it.zerono.mods.zerocore.lib.fluid.SimpleFluidTypeRenderProperties;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;

public class ReactorFluidType
        extends FluidType {

    public static ReactorFluidType of(final String registrationName, final int tint, final int density, final int lightLevel, final Rarity rarity) {
        return new ReactorFluidType(registrationName, 0xff000000 | tint, density, lightLevel, rarity);
    }

    public static ReactorFluidType of(final Reactants reactant) {
        return of(reactant.getReactantName(), reactant.getColour(), reactant.getFluidDensity(), reactant.getFluidLightLevel(), reactant.getRarity());
    }

    //region internals

    private ReactorFluidType(final String registrationName, final int tint, final int density, final int lightLevel, final Rarity rarity) {

        super(FluidType.Properties.create()
                .descriptionId("fluid." + ExtremeReactors.MOD_ID + "." + registrationName)
                .density(density)
                .lightLevel(lightLevel)
                .canDrown(true)
                .canSwim(true)
                .canPushEntity(true)
                .fallDistanceModifier(0.4f)
                .canExtinguish(false)
                .canConvertToSource(false)
                .supportsBoating(true)
                .pathType(PathType.WATER)
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                .canHydrate(false)
                .rarity(rarity));

        this._tintColour = tint;
    }

    private class RenderProperties
            extends SimpleFluidTypeRenderProperties {

        public RenderProperties() {
            super(0, CommonConstants.FLUID_TEXTURE_SOURCE_WATER, CommonConstants.FLUID_TEXTURE_FLOWING_WATER,
                    CommonConstants.FLUID_TEXTURE_OVERLAY_WATER);
        }

        //region SimpleFluidTypeRenderProperties

        @Override
        public int getTintColor() {
            return _tintColour;
        }

        //endregion
    }

    private final int _tintColour;

    //endregion
}
