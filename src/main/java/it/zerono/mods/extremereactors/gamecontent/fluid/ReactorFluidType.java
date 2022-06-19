package it.zerono.mods.extremereactors.gamecontent.fluid;

import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.Reactants;
import it.zerono.mods.zerocore.lib.fluid.SimpleFluidTypeRenderProperties;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.client.IFluidTypeRenderProperties;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;

import java.util.function.Consumer;

public class ReactorFluidType
        extends FluidType {

    public static ReactorFluidType of(final int tint, final int density, final int lightLevel, final Rarity rarity) {
        return new ReactorFluidType(0xff000000 | tint, density, lightLevel, rarity);
    }

    public static ReactorFluidType of(final Reactants reactant) {
        return of(reactant.getColour(), reactant.getFluidDensity(), reactant.getFluidLightLevel(), reactant.getRarity());
    }

    //region FluidType

    @Override
    public void initializeClient(final Consumer<IFluidTypeRenderProperties> consumer) {
        consumer.accept(new RenderProperties());
    }

    //endregion
    //region internals

    private ReactorFluidType(final int tint, final int density, final int lightLevel, final Rarity rarity) {

        super(FluidType.Properties.create()
                .density(density)
                .lightLevel(lightLevel)
                .canDrown(true)
                .canSwim(true)
                .canPushEntity(true)
                .fallDistanceModifier(0.4f)
                .canExtinguish(false)
                .canConvertToSource(false)
                .supportsBoating(true)
                .pathType(BlockPathTypes.WATER)
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
        public int getColorTint() {
            return _tintColour;
        }

        //endregion
    }

    private final int _tintColour;

    //endregion
}
