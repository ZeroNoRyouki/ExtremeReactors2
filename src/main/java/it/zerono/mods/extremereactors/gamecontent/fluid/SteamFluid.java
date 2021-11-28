/*
 *
 * SteamFluid.java
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
import it.zerono.mods.extremereactors.gamecontent.Content;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.StateContainer;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class SteamFluid
        extends ForgeFlowingFluid {

    public static class Flowing
            extends SteamFluid {

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
            extends SteamFluid {

        @Override
        public int getAmount(final FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(final FluidState state) {
            return true;
        }
    }

    //region internals

    protected SteamFluid() {
        super(new ForgeFlowingFluid.Properties(Content.Fluids.STEAM_SOURCE, Content.Fluids.STEAM_FLOWING,
                FluidAttributes.builder(CommonConstants.FLUID_TEXTURE_SOURCE_WATER, CommonConstants.FLUID_TEXTURE_FLOWING_WATER)
                        .overlay(CommonConstants.FLUID_TEXTURE_OVERLAY_WATER)
                        .sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY)
                        .density(1)
                        .gaseous()
                        .luminosity(6)
                        .color(0xffffffff/*0x86bdfbff*/))
                .bucket(Content.Items.STEAM_BUCKET)
                .block(Content.Blocks.STEAM));
    }

    //endregion
}
