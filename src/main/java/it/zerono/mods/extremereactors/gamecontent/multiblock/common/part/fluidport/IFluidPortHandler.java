/*
 *
 * IFluidPortHandler.java
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
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.AbstractMultiblockEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.IIOPortHandler;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockGeneratorVariant;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockVariantProvider;
import it.zerono.mods.zerocore.lib.data.IIoEntity;
import it.zerono.mods.zerocore.lib.data.IoMode;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface IFluidPortHandler<Controller extends AbstractGeneratorMultiblockController<Controller, V>,
            V extends IMultiblockGeneratorVariant>
        extends IIOPortHandler {

    static <Controller extends AbstractGeneratorMultiblockController<Controller, V>, V extends IMultiblockGeneratorVariant,
            T extends AbstractMultiblockEntity<Controller> & IMultiblockVariantProvider<? extends IMultiblockGeneratorVariant> & IIoEntity>
    IFluidPortHandler<Controller, V> create(final FluidPortType type, final IoMode mode, final T part) {

        switch (type) {

            case Forge:
                return new FluidPortHandlerForge<>(part, mode);

//            case Mekanism:
//                return Mods.MEKANISM.map(() -> (IFluidPortHandler<Controller, V>)new FluidPortHandlerMekanism<>(part, mode))
//                        .orElseGet(EmptyFluidPortHandler::new);

            default:
                throw new IllegalArgumentException("Unsupported Fluid Port: " + type);
        }
    }

    /**
     * Get the {@link FluidPortType} supported by this IFluidPortHandler
     *
     * @return the supported {@link FluidPortType}
     */
    FluidPortType getFluidPortType();

    /**
     * If this is a Active Fluid Port in output mode, send fluid to the connected consumer (if there is one)
     *
     * @param stack FluidStack representing the Fluid and maximum amount of fluid to be sent out.
     * @return the amount of fluid accepted by the consumer
     */
    int outputFluid(FluidStack stack);


    /**
     * If this is a Active Fluid Port in input mode, try to get fluids from the connected consumer (if there is one)
     *
     * @param destination the destination IFluidHandler that will receive the fluid
     * @param maxAmount the maximum amount of fluid the acquire
     */
    int inputFluid(IFluidHandler destination, int maxAmount);
}
