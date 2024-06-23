/*
 * UpdateFluidizerFluidStatus
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.network;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part.AbstractFluidizerEntity;
import it.zerono.mods.zerocore.lib.network.AbstractBlockEntityPlayPacket;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;

public class UpdateFluidizerFluidStatus
        extends AbstractBlockEntityPlayPacket<UpdateFluidizerFluidStatus> {

    public static final Type<UpdateFluidizerFluidStatus> TYPE = createType(ExtremeReactors.ROOT_LOCATION, "update_fluidizer_fluid");

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateFluidizerFluidStatus> STREAM_CODEC = createStreamCodec(
            FluidStack.OPTIONAL_STREAM_CODEC, packet -> packet._stack,
            UpdateFluidizerFluidStatus::new);

    public UpdateFluidizerFluidStatus(AbstractFluidizerEntity referencePart, FluidStack stack) {

        super(TYPE, referencePart);
        this._stack = stack;
    }

    public UpdateFluidizerFluidStatus(GlobalPos position, FluidStack stack) {

        super(TYPE, position);
        this._stack = stack;
    }

    public FluidStack getStack() {
        return this._stack;
    }

    //region AbstractModTileMessage

    @Override
    protected void processBlockEntity(PacketFlow flow, BlockEntity tileEntity) {

        if (PacketFlow.CLIENTBOUND == flow && tileEntity instanceof AbstractFluidizerEntity fluidizerEntity) {
            fluidizerEntity.onUpdateFluidStatus(this);
        }
    }

    //endregion
    //region internals

    private final FluidStack _stack;

    //endregion
}
