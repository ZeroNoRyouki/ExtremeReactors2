/*
 *
 * AbstractFluidGeneratorMultiblockController.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common;

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockFluidGeneratorVariant;
import it.zerono.mods.zerocore.base.multiblock.part.io.IIOPortHandler;
import it.zerono.mods.zerocore.base.multiblock.part.io.fluid.IFluidPort;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Collection;
import java.util.Optional;

@SuppressWarnings({"WeakerAccess"})
public abstract class AbstractFluidGeneratorMultiblockController<Controller extends AbstractFluidGeneratorMultiblockController<Controller, V>,
            V extends IMultiblockFluidGeneratorVariant>
        extends AbstractEnergyGeneratorMultiblockController<Controller, V> {

    public AbstractFluidGeneratorMultiblockController(Level world) {
        super(world);
    }

    //region active-coolant system

    public abstract Optional<IFluidHandler> getLiquidHandler();

    public abstract Optional<IFluidHandler> getGasHandler();

    public abstract Optional<IFluidHandler> getFluidHandler(IoDirection portDirection);

    /**
     * Distribute the given fluid equally between the specified Active Coolant Ports
     *
     * @param availableFluid the gas to distribute
     * @param coolantPorts the Coolant Ports
     * @return the amount of gas distributed
     */
    protected static int distributeFluidEqually(final FluidStack availableFluid,
                                                final Collection<? extends IFluidPort> coolantPorts) {

        if (availableFluid.isEmpty() || coolantPorts.isEmpty()) {
            return 0;
        }

        final int fluidPerPort = availableFluid.getAmount() / coolantPorts.size();

        return coolantPorts.stream()
                .filter(p -> p.getIoDirection().isOutput())
                .map(IFluidPort::getFluidPortHandler)
                .filter(IIOPortHandler::isActive)
                .filter(IIOPortHandler::isConnected)
                .mapToInt(handler -> handler.outputFluid(new FluidStack(availableFluid, fluidPerPort)))
                .sum();
    }

    /**
     * Distribute the given fluid equally between the specified Active Coolant Ports
     */
    protected static int acquireFluidEqually(final IFluidHandler destination, final int maxAmount,
                                             final Collection<? extends IFluidPort> coolantPorts) {

        if (maxAmount <= 0 || coolantPorts.isEmpty()) {
            return 0;
        }

        final int fluidPerPort = maxAmount / coolantPorts.size();

        return coolantPorts.stream()
                .filter(p -> p.getIoDirection().isInput())
                .map(IFluidPort::getFluidPortHandler)
                .filter(IIOPortHandler::isActive)
                .filter(IIOPortHandler::isConnected)
                .mapToInt(handler -> handler.inputFluid(destination, fluidPerPort))
                .sum();
    }

    //endregion
}
