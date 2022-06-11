package it.zerono.mods.extremereactors.gamecontent.fluid;
/*
 * ModeratorFluid
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * Do not remove or edit this header
 *
 */

import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.function.Supplier;

public abstract class ModeratorFluid
        extends ForgeFlowingFluid {

    public static class Flowing
            extends ModeratorFluid {

        public Flowing(final Supplier<ModeratorFluid.Source> sourceFluid, final Supplier<ModeratorFluid.Flowing> flowingFluid,
                       final Supplier<ModeratorFluid.Block> block, final Supplier<? extends Item> bucket, final int colour) {
            super(sourceFluid, flowingFluid, block, bucket, colour);
        }

        @Override
        protected void createFluidStateDefinition(final StateDefinition.Builder<Fluid, FluidState> builder) {

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
            extends ModeratorFluid {

        public Source(final Supplier<ModeratorFluid.Source> sourceFluid, final Supplier<ModeratorFluid.Flowing> flowingFluid,
                      final Supplier<ModeratorFluid.Block> block, final Supplier<? extends Item> bucket, final int colour) {
            super(sourceFluid, flowingFluid, block, bucket, colour);
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
            extends LiquidBlock {

        public Block(final Supplier<? extends ModeratorFluid> fluid) {
            super(fluid, ModeratorFluid.Block.Properties.of(Material.WATER)
                    .lightLevel(state -> 7)
                    .strength(70.0F)
                    .noLootTable());
        }
    }

    //region internals

    protected ModeratorFluid(final Supplier<ModeratorFluid.Source> source, final Supplier<ModeratorFluid.Flowing> flowing,
                            final Supplier<ModeratorFluid.Block> block, final Supplier<? extends Item> bucket, final int colour) {
        super(new ForgeFlowingFluid.Properties(source, flowing,
                FluidAttributes.builder(CommonConstants.FLUID_TEXTURE_SOURCE_WATER, CommonConstants.FLUID_TEXTURE_FLOWING_WATER)
                        .overlay(CommonConstants.FLUID_TEXTURE_OVERLAY_WATER)
                        .sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY)
                        .density(1000)
                        .luminosity(7)
                        .color(0xff000000 | colour))
                .bucket(bucket)
                .block(block));
    }

    //endregion
}
