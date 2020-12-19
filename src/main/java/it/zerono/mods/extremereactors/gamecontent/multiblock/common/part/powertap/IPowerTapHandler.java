/*
 *
 * IPowerTapHandler.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.powertap;

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.AbstractGeneratorMultiblockController;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.AbstractMultiblockEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.IIOPortHandler;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockGeneratorVariant;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockVariantProvider;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;

public interface IPowerTapHandler
        extends IIOPortHandler {

    static <Controller extends AbstractGeneratorMultiblockController<Controller, V>,
                V extends IMultiblockGeneratorVariant,
                T extends AbstractMultiblockEntity<Controller> & IMultiblockVariantProvider<? extends IMultiblockGeneratorVariant>>
    IPowerTapHandler create(final EnergySystem system, final IoMode mode, final T part) {

        switch (system) {

            case ForgeEnergy:
                return new PowerTapHandlerFE<>(part, mode);

            default:
                throw new IllegalArgumentException("Unsupported energy system: " + system);
        }
    }

    /**
     * Get the {@link EnergySystem} supported by this IPowerTapHandler
     *
     * @return the supported {@link EnergySystem}
     */
    EnergySystem getEnergySystem();

    /**
     * Send energy to the connected consumer (if there is one)
     *
     * @param amount amount of energy to send
     * @return the amount of energy accepted by the consumer
     */
    double outputEnergy(double amount);
}
