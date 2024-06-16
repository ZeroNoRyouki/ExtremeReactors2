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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;

public class UpdateFluidizerFluidStatus
        extends AbstractBlockEntityPlayPacket {

    public static final ResourceLocation ID = ExtremeReactors.ROOT_LOCATION.buildWithSuffix("update_fluidizer_fluid");

    public UpdateFluidizerFluidStatus(AbstractFluidizerEntity referencePart, FluidStack stack) {

        super(ID, referencePart.getWorldPosition());
        this._stack = stack;
    }

    public UpdateFluidizerFluidStatus(FriendlyByteBuf buffer) {

        super(ID, buffer);
        this._stack = buffer.readFluidStack();
    }

    public FluidStack getStack() {
        return this._stack;
    }

    //region AbstractModTileMessage

    @Override
    public void write(FriendlyByteBuf buffer) {

        super.write(buffer);
        buffer.writeFluidStack(this._stack);
    }

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
