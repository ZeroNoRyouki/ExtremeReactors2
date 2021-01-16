/*
 *
 * EmptyFluidPortHandler.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.fluidport;

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.AbstractGeneratorMultiblockController;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.EmptyIOPortHandler;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockGeneratorVariant;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class EmptyFluidPortHandler<Controller extends AbstractGeneratorMultiblockController<Controller, V>,
                                    V extends IMultiblockGeneratorVariant>
    extends EmptyIOPortHandler
    implements IFluidPortHandler<Controller, V> {

    //region IFluidPortHandler

    /**
     * Get the {@link FluidPortType} supported by this IFluidPortHandler
     *
     * @return the supported {@link FluidPortType}
     */
    @Override
    public FluidPortType getFluidPortType() {
        return FluidPortType.Forge;
    }

    /**
     * If this is a Active Fluid Port in output mode, send fluid to the connected consumer (if there is one)
     *
     * @param stack FluidStack representing the Fluid and maximum amount of fluid to be sent out.
     * @return the amount of fluid accepted by the consumer
     */
    @Override
    public int outputFluid(FluidStack stack) {
        return 0;
    }

    /**
     * If this is a Active Fluid Port in input mode, try to get fluids from the connected consumer (if there is one)
     *
     * @param destination the destination IFluidHandler that will receive the fluid
     * @param maxAmount   the maximum amount of fluid the acquire
     */
    @Override
    public int inputFluid(IFluidHandler destination, int maxAmount) {
        return 0;
    }

    //endregion
}
