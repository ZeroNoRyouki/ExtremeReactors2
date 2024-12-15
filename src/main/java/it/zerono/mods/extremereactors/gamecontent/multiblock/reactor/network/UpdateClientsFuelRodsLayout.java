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

import io.netty.buffer.ByteBuf;
import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.AbstractReactorEntity;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import it.zerono.mods.zerocore.lib.network.payload.AbstractBlockEntityPlayPacket;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.world.level.block.entity.BlockEntity;

public class UpdateClientsFuelRodsLayout
    extends AbstractBlockEntityPlayPacket<UpdateClientsFuelRodsLayout> {

    public static final Type<UpdateClientsFuelRodsLayout> TYPE = createType(ExtremeReactors.ROOT_LOCATION, "update_client_fuelrods");

    public static final StreamCodec<ByteBuf, UpdateClientsFuelRodsLayout> STREAM_CODEC = createStreamCodec(
            ByteBufCodecs.COMPOUND_TAG, packet -> packet._fuelContainerData,
            UpdateClientsFuelRodsLayout::new);

    public UpdateClientsFuelRodsLayout(final AbstractReactorEntity referencePart, final ISyncableEntity fuelContainer) {

        super(TYPE, referencePart);
        this._fuelContainerData = fuelContainer.syncDataTo(new CompoundTag(),
                referencePart.getPartWorldOrFail().registryAccess(), ISyncableEntity.SyncReason.NetworkUpdate);
    }

    public UpdateClientsFuelRodsLayout(GlobalPos position, CompoundTag data) {

        super(TYPE, position);
        this._fuelContainerData = data;
    }

    public CompoundTag getFuelContainerData() {
        return this._fuelContainerData;
    }

    //region AbstractModTileMessage

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
