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

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.AbstractReactorEntity;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import it.zerono.mods.zerocore.lib.network.AbstractBlockEntityPlayPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public class UpdateClientsFuelRodsLayout
    extends AbstractBlockEntityPlayPacket {

    public static final ResourceLocation ID = ExtremeReactors.ROOT_LOCATION.buildWithSuffix("update_client_fuelrods");

    public UpdateClientsFuelRodsLayout(final AbstractReactorEntity referencePart, final ISyncableEntity fuelContainer) {

        super(ID, referencePart.getWorldPosition());
        this._fuelContainerData = fuelContainer.syncDataTo(new CompoundTag(), ISyncableEntity.SyncReason.NetworkUpdate);
    }

    public UpdateClientsFuelRodsLayout(final FriendlyByteBuf buffer) {

        super(ID, buffer);
        this._fuelContainerData = buffer.readNbt();
    }

    public CompoundTag getFuelContainerData() {
        return this._fuelContainerData;
    }

    //region AbstractModTileMessage

    @Override
    public void write(FriendlyByteBuf buffer) {

        super.write(buffer);
        buffer.writeNbt(this._fuelContainerData);
    }

    @Override
    protected void processBlockEntity(PacketFlow flow, BlockEntity blockEntity) {

        if (PacketFlow.CLIENTBOUND == flow && blockEntity instanceof AbstractReactorEntity re) {
            re.onUpdateClientsFuelRodsLayout(this);
        }
    }

    //endregion
    //region internals

    private final CompoundTag _fuelContainerData;

    //endregion
}
