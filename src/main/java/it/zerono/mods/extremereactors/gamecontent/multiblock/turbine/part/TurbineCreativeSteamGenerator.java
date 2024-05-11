/*
 *
 * TurbineCreativeSteamGenerator.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part;

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.multiblock.ITickableMultiblockPart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class TurbineCreativeSteamGenerator
        extends AbstractTurbineEntity
        implements ITickableMultiblockPart {

    public TurbineCreativeSteamGenerator(final BlockPos position, final BlockState blockState) {
        super(Content.TileEntityTypes.TURBINE_CREATIVE_STEAM_GENERATOR.get(), position, blockState);
    }

    //region ITickableMultiblockPart

    /**
     * Called once every tick from the multiblock server-side tick loop.
     */
    @Override
    public void onMultiblockServerTick() {

        this.executeOnController(turbine -> {

            //TODO only fill if there is space

            //if (turbine.isMachineActive()) {
                turbine.getFluidHandler(IoDirection.Input)
                        .ifPresent(handler -> handler.fill(new FluidStack(Content.Fluids.STEAM_SOURCE.get(), turbine.getMaxIntakeRateHardLimit()),
                                IFluidHandler.FluidAction.EXECUTE));
//            }
        });
    }

    //endregion
}
