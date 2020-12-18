/*
 *
 * AbstractPowerTapHandler.java
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
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.AbstractIOPortHandler;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockGeneratorVariant;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.energy.IWideEnergyProvider;
import it.zerono.mods.zerocore.lib.energy.NullEnergyHandlers;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockPart;

public abstract class AbstractPowerTapHandler<Controller extends AbstractGeneratorMultiblockController<Controller, V>,
            V extends IMultiblockGeneratorVariant>
        extends AbstractIOPortHandler<Controller, V>
        implements IPowerTapHandler {

    protected AbstractPowerTapHandler(final EnergySystem energySystem, final AbstractCuboidMultiblockPart<Controller> part, final IoMode mode) {

        super(part, mode);
        this._system = energySystem;
    }

    protected IWideEnergyProvider getEnergyProvider() {
        return this.getPart().getMultiblockController()
                .filter(Controller::isAssembled)
                .map(controller -> (IWideEnergyProvider)controller)
                .orElse(NullEnergyHandlers.PROVIDER);
    }

    //region IPowerTapHandler

    /**
     * Get the {@link EnergySystem} supported by this IPowerTapHandler
     *
     * @return the supported {@link EnergySystem}
     */
    public EnergySystem getEnergySystem() {
        return this._system;
    }

    //endregion
    //region internals

    private final EnergySystem _system;

    //endregion
}
