package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.network;
/*
 * UpdateClientsFuelRodsLayout
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
 * Do not remove or edit this header
 *
 */

import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.AbstractReactorEntity;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import it.zerono.mods.zerocore.lib.network.AbstractModTileMessage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.LogicalSide;

public class UpdateClientsFuelRodsLayout
    extends AbstractModTileMessage {

    public UpdateClientsFuelRodsLayout(final AbstractReactorEntity referencePart, final ISyncableEntity fuelContainer) {

        super(referencePart.getWorldPosition());
        this._fuelContainerData = fuelContainer.syncDataTo(new CompoundNBT(), ISyncableEntity.SyncReason.NetworkUpdate);
    }

    public UpdateClientsFuelRodsLayout(final PacketBuffer buffer) {

        super(buffer);
        this._fuelContainerData = buffer.readNbt();
    }

    public CompoundNBT getFuelContainerData() {
        return this._fuelContainerData;
    }

    //region AbstractModTileMessage

    @Override
    public void encodeTo(final PacketBuffer buffer) {

        super.encodeTo(buffer);
        buffer.writeNbt(this._fuelContainerData);
    }

    @Override
    protected void processTileEntityMessage(final LogicalSide sourceSide, final TileEntity tileEntity) {

        if (LogicalSide.SERVER == sourceSide && tileEntity instanceof AbstractReactorEntity) {
            ((AbstractReactorEntity)tileEntity).onUpdateClientsFuelRodsLayout(this);
        }
    }

    //endregion
    //region internals

    private final CompoundNBT _fuelContainerData;

    //endregion
}
