package it.zerono.mods.extremereactors.gamecontent.fluid;

import it.zerono.mods.extremereactors.api.reactor.Reactant;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.Reactants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.util.NonNullConsumer;

import java.util.function.Supplier;

public class ReactantFluidBlock
        extends LiquidBlock {

    public ReactantFluidBlock(final Reactants reactant, final Supplier<? extends FlowingFluid> fluid) {

        super(fluid, Properties.of(Material.WATER)
                .lightLevel(state -> 6)
                .strength(100.0F)
                .noLootTable());

        this._effect = reactant;
    }

    //region LiquidBlock

    @Override
    public void entityInside(final BlockState state, final Level world, final BlockPos position, final Entity entity) {

        super.entityInside(state, world, position, entity);

        if (entity instanceof LivingEntity e && state.getValue(LEVEL) > 0) {
            this._effect.accept(e);
        }
    }

    //endregion
    //region internals

    final NonNullConsumer<LivingEntity> _effect;

    //endregion
}
