/*
 *
 * FluidizerPowerPortEntity.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part;

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.MultiblockFluidizer;
import it.zerono.mods.zerocore.base.multiblock.part.io.power.IPowerPort;
import it.zerono.mods.zerocore.base.multiblock.part.io.power.IPowerPortHandler;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FluidizerPowerPortEntity
        extends AbstractFluidizerEntity
        implements IPowerPort {

    public FluidizerPowerPortEntity(final BlockPos position, final BlockState blockState) {

        super(Content.TileEntityTypes.FLUIDIZER_POWERPORT.get(), position, blockState);
        this._handler = IPowerPortHandler.create(EnergySystem.ForgeEnergy, IoMode.Passive, this);
    }

    //region IPowerPort

    @Override
    public IPowerPortHandler getPowerPortHandler() {
        return this._handler;
    }

    @Override
    public IoDirection getIoDirection() {
        return IoDirection.Input;
    }

    @Override
    public void setIoDirection(IoDirection direction) {
    }

    //endregion
    //region AbstractCuboidMultiblockPart

    @Override
    public void onAttached(MultiblockFluidizer newController) {

        super.onAttached(newController);
        this.getPowerPortHandler().onPortChanged();
    }

    @Override
    public void onPostMachineAssembled(final MultiblockFluidizer controller) {

        super.onPostMachineAssembled(controller);
        this.getPowerPortHandler().onPortChanged();
    }

    @Override
    public void onPostMachineBroken() {

        super.onPostMachineBroken();
        this.getPowerPortHandler().onPortChanged();
    }

    @Override
    public boolean isGoodForPosition(final PartPosition position, final IMultiblockValidator validatorCallback) {
        return position.isFace() || super.isGoodForPosition(position, validatorCallback);
    }

    //endregion
    //region internals

    private final IPowerPortHandler _handler;

    //endregion
}
