/*
 *
 * CoolantPortHandlerMekanism.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.coolantport;

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.AbstractGeneratorMultiblockController;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockGeneratorVariant;
import it.zerono.mods.zerocore.lib.data.IIoEntity;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockPart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class CoolantPortHandlerMekanism<Controller extends AbstractGeneratorMultiblockController<Controller, V>,
        V extends IMultiblockGeneratorVariant, P extends AbstractCuboidMultiblockPart<Controller> & IIoEntity>
        extends AbstractCoolantPortHandler<Controller, V, P> {

    public CoolantPortHandlerMekanism(final P part, final IoMode mode) {

        super(CoolantPortType.Mekanism, part, mode);
    }

    //region ICoolantPortHandler

    /**
     * Send fluid to the connected consumer (if there is one)
     *
     * @param stack FluidStack representing the Fluid and maximum amount of fluid to be sent out.
     * @return the amount of fluid accepted by the consumer
     */
    @Override
    public int outputFluid(final FluidStack stack) {
        // look up a Mekanism compatible gas and output it
        return 0; // TODO imp
    }

    /**
     * If this is a Active Coolant Port in input mode, try to get fluids from the connected consumer (if there is one)
     */
    @Override
    public void inputFluid() {

    }

    //endregion
    //region IIOPortHandler

    /**
     * @return true if this handler is connected to one of it's allowed consumers, false otherwise
     */
    @Override
    public boolean isConnected() {
        return false; // TODO imp
    }

    /**
     * Check for connections
     *
     * @param world    the handler world
     * @param position the handler position
     */
    @Override
    public void checkConnections(@Nullable final IBlockReader world, final BlockPos position) {
        // TODO imp
    }

    @Override
    public void invalidate() {
        // TODO imp
    }

    //endregion
}
