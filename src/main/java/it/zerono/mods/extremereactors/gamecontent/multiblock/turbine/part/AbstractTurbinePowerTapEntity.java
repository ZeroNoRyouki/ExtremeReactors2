/*
 *
 * AbstractTurbinePowerTapEntity.java
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

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.powertap.IPowerTap;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.powertap.IPowerTapHandler;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import net.minecraft.tileentity.TileEntityType;

public class AbstractTurbinePowerTapEntity
        extends AbstractTurbineEntity
        implements IPowerTap {

    protected AbstractTurbinePowerTapEntity(final EnergySystem system, final TileEntityType<?> entityType) {
        super(entityType);
    }

    //region IPowerTap

    @Override
    public IPowerTapHandler getPowerTapHandler() {
        return this._handler;
    }

    //endregion
    //region internals

    protected final void setHandler(final IPowerTapHandler handler) {
        this._handler = handler;
    }

    private IPowerTapHandler _handler;

    //endregion
}
