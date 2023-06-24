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

import it.zerono.mods.zerocore.base.multiblock.part.io.power.IPowerPort;
import it.zerono.mods.zerocore.base.multiblock.part.io.power.IPowerPortHandler;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AbstractTurbinePowerTapEntity
        extends AbstractTurbineEntity
        implements IPowerPort {

    protected AbstractTurbinePowerTapEntity(final EnergySystem system, final BlockEntityType<?> entityType,
                                            final BlockPos position, final BlockState blockState) {
        super(entityType, position, blockState);
    }

    //region IPowerTap

    @Override
    public IPowerPortHandler getPowerPortHandler() {
        return this._handler;
    }

    @Override
    public IoDirection getIoDirection() {
        return IoDirection.Output;
    }

    @Override
    public void setIoDirection(IoDirection direction) {
        throw new IllegalStateException("This port can only works in output mode");
    }

    //endregion
    //region internals

    protected final void setHandler(final IPowerPortHandler handler) {
        this._handler = handler;
    }

    private IPowerPortHandler _handler;

    //endregion
}
