/*
 *
 * ICoolantPortHandler.java
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
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.IIOPortHandler;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockGeneratorVariant;
import it.zerono.mods.zerocore.lib.data.IIoEntity;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockPart;
import net.minecraftforge.fluids.FluidStack;

public interface ICoolantPortHandler<Controller extends AbstractGeneratorMultiblockController<Controller, V>,
            V extends IMultiblockGeneratorVariant>
        extends IIOPortHandler {

    static <Controller extends AbstractGeneratorMultiblockController<Controller, V>, V extends IMultiblockGeneratorVariant,
            P extends AbstractCuboidMultiblockPart<Controller> & IIoEntity>
        ICoolantPortHandler<Controller, V> create(final CoolantPortType type, final IoMode mode, final P part) {

        switch (type) {

            case Forge:
                return new CoolantPortHandlerForge<>(part, mode);

            case Mekanism:
                return new CoolantPortHandlerMekanism<>(part, mode);

            default:
                throw new IllegalArgumentException("Unsupported Coolant Port: " + type);
        }
    }

    /**
     * Get the {@link CoolantPortType} supported by this ICoolantPortHandler
     *
     * @return the supported {@link CoolantPortType}
     */
    CoolantPortType getCoolantPortType();

    /**
     * Send fluid to the connected consumer (if there is one)
     *
     * @param stack FluidStack representing the Fluid and maximum amount of fluid to be sent out.
     * @return the amount of fluid accepted by the consumer
     */
    int outputFluid(FluidStack stack);
}
