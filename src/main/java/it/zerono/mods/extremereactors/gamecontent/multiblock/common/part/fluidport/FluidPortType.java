/*
 *
 * FluidPortType.java
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

import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity;
import it.zerono.mods.zerocore.base.multiblock.part.io.fluid.EmptyFluidPortHandler;
import it.zerono.mods.zerocore.base.multiblock.part.io.fluid.FluidPortHandlerForge;
import it.zerono.mods.zerocore.base.multiblock.part.io.fluid.IFluidPort;
import it.zerono.mods.zerocore.base.multiblock.part.io.fluid.IFluidPortHandler;
import it.zerono.mods.zerocore.lib.compat.Mods;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;

public enum FluidPortType {

    /**
     * Standard Forge IFluidHandler capability
     */
    Forge,

    /**
     * Mekanism IGasHandler capability
     */
    Mekanism
    ;

    public static <Controller extends AbstractCuboidMultiblockController<Controller>,
            T extends AbstractMultiblockEntity<Controller> & IFluidPort>
    IFluidPortHandler create(FluidPortType type, IoMode mode, T part) {

        switch (type) {

            case Forge:
                return new FluidPortHandlerForge<>(part, mode);

            case Mekanism:
//                return Mods.MEKANISM
//                        .map(() -> (IFluidPortHandler) new FluidPortHandlerMekanism<>(part, mode))
//                        .orElseGet(EmptyFluidPortHandler::new);
                return new EmptyFluidPortHandler();
        }

        throw new IllegalArgumentException("Unsupported Fluid Port: " + type);
    }
}
