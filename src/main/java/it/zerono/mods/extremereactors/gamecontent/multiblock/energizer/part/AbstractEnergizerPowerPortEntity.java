/*
 * AbstractEnergizerPowerPortEntity
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * Do not remove or edit this header
 *
 */

package it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.part;

import it.zerono.mods.zerocore.base.multiblock.part.io.power.IPowerPort;
import it.zerono.mods.zerocore.base.multiblock.part.io.power.IPowerPortHandler;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.LogicalSide;

abstract class AbstractEnergizerPowerPortEntity
        extends AbstractEnergizerEntity
        implements IPowerPort {

    protected AbstractEnergizerPowerPortEntity(BlockEntityType<?> type, BlockPos position, BlockState blockState) {

        super(type, position, blockState);
        this._transferRate = WideAmount.MAX_VALUE;
        this.setIoDirection(IoDirection.Input);
    }

    //region IPowerPort

    @Override
    public IPowerPortHandler getPowerPortHandler() {
        return this._handler;
    }

    @Override
    public WideAmount getMaxTransferRate() {
        return this._transferRate;
    }

    @Override
    public IoDirection getIoDirection() {
        return this._ioDirection;
    }

    @Override
    public void setIoDirection(IoDirection direction) {

        if (this.getIoDirection() == direction) {
            return;
        }

        this._ioDirection = direction;

        this.notifyBlockUpdate();
        this.callOnLogicalSide(
                () -> {
                    this.notifyOutwardNeighborsOfStateChange();
                    this.setChanged();
                },
                this::markForRenderUpdate
        );
    }

    //endregion
    //region ISyncableEntity

    @Override
    public void syncDataFrom(final CompoundTag data, final SyncReason syncReason) {

        super.syncDataFrom(data, syncReason);
        this._transferRate = WideAmount.from(data.getCompound("rate"));
    }

    @Override
    public CompoundTag syncDataTo(final CompoundTag data, final SyncReason syncReason) {

        super.syncDataTo(data, syncReason);
        data.put("rate", this._transferRate.serializeTo(new CompoundTag()));
        return data;
    }

    //endregion
    //region IDebuggable

    @Override
    public void getDebugMessages(LogicalSide side, IDebugMessages messages) {

        super.getDebugMessages(side, messages);

        messages.add("Max transfer rate: %1$s",
                CodeHelper.formatAsHumanReadableNumber(this.getMaxTransferRate(), "/t"));
        messages.add("Direction: %1$s", this._ioDirection);
    }

    //endregion
    //region internals

    protected final void setHandler(final IPowerPortHandler handler) {
        this._handler = handler;
    }

    private IPowerPortHandler _handler;
    private WideAmount _transferRate;
    private IoDirection _ioDirection;

    //endregion
}
