/*
 *
 * ReactantFluid.java
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

package it.zerono.mods.extremereactors.gamecontent.fluid;

import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.function.Supplier;

public abstract class ReactantFluid
        extends ForgeFlowingFluid {

    public static class Flowing
            extends ReactantFluid {

        public Flowing(final Supplier<ReactantFluid.Source> sourceFluid, final Supplier<ReactantFluid.Flowing> flowingFluid,
                       final Supplier<ReactantFluid.Block> block, final Supplier<? extends Item> bucket,
                       final int colour, final NonNullConsumer<LivingEntity> effectProvider) {
            super(sourceFluid, flowingFluid, block, bucket, colour, effectProvider);
        }

        @Override
        protected void createFluidStateDefinition(final StateContainer.Builder<Fluid, FluidState> builder) {

            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(final FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(final FluidState state) {
            return false;
        }
    }

    public static class Source
            extends ReactantFluid {

        public Source(final Supplier<ReactantFluid.Source> sourceFluid, final Supplier<ReactantFluid.Flowing> flowingFluid,
                      final Supplier<ReactantFluid.Block> block, final Supplier<? extends Item> bucket,
                      final int colour, final NonNullConsumer<LivingEntity> effectProvider) {
            super(sourceFluid, flowingFluid, block, bucket, colour, effectProvider);
        }

        @Override
        public int getAmount(final FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(final FluidState state) {
            return true;
        }
    }

    public static class Block
            extends FlowingFluidBlock {

        public Block(final Supplier<? extends ReactantFluid> fluid) {

            super(fluid, Block.Properties.of(Material.WATER)
                    .lightLevel(state -> 6)
                    .strength(100.0F)
                    .noDrops());

            this._fluidSupplier = fluid;
        }

        @Override
        public void entityInside(final BlockState state, final World world, final BlockPos position, final Entity entity) {

            super.entityInside(state, world, position, entity);

            if (entity instanceof LivingEntity && state.getValue(LEVEL) > 0) {
                this._fluidSupplier.get().applyEffect((LivingEntity)entity);
            }
        }

        //region internals

        final Supplier<? extends ReactantFluid> _fluidSupplier;

        //endregion
    }

    //region internals

    protected ReactantFluid(final Supplier<ReactantFluid.Source> source, final Supplier<ReactantFluid.Flowing> flowing,
                            final Supplier<ReactantFluid.Block> block, final Supplier<? extends Item> bucket,
                            final int colour, final NonNullConsumer<LivingEntity> effectProvider) {
        super(new ForgeFlowingFluid.Properties(source, flowing,
                FluidAttributes.builder(CommonConstants.FLUID_TEXTURE_SOURCE_WATER, CommonConstants.FLUID_TEXTURE_FLOWING_WATER)
                        .overlay(CommonConstants.FLUID_TEXTURE_OVERLAY_WATER)
                        .sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY)
                        .density(2000)
                        .luminosity(5)
                        .color(0xff000000 | colour))
                .bucket(bucket)
                .block(block));

        this._effectProvider = effectProvider;
    }

    protected void applyEffect(final LivingEntity entity) {
        this._effectProvider.accept(entity);
    }

    private final NonNullConsumer<LivingEntity> _effectProvider;

    //endregion
}
