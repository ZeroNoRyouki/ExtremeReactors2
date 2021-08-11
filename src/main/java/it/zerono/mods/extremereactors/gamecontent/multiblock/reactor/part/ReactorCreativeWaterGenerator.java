/*
 *
 * ReactorCreativeWaterGenerator.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part;

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.multiblock.ITickableMultiblockPart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class ReactorCreativeWaterGenerator
        extends AbstractReactorEntity
        implements ITickableMultiblockPart {

    public ReactorCreativeWaterGenerator(final BlockPos position, final BlockState blockState) {
        super(Content.TileEntityTypes.REACTOR_CREATIVE_WATER_GENERATOR.get(), position, blockState);
    }

    //region ITickableMultiblockPart

    /**
     * Called once every tick from the multiblock server-side tick loop.
     */
    @Override
    public void onMultiblockServerTick() {

        this.executeOnController(reactor -> {

            if (reactor.isMachineActive()) {
                reactor.getFluidHandler(IoDirection.Input)
                        .ifPresent(handler -> handler.fill(new FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE));
            }
        });
    }

    //endregion
}
