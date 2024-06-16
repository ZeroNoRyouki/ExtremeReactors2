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

import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part.AbstractFluidizerEntity;
import it.zerono.mods.zerocore.lib.network.AbstractModTileMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.LogicalSide;

public class UpdateFluidizerFluidStatus
        extends AbstractModTileMessage {

    public UpdateFluidizerFluidStatus(AbstractFluidizerEntity referencePart, FluidStack stack) {

        super(referencePart.getWorldPosition());
        this._stack = stack;
    }

    public UpdateFluidizerFluidStatus(FriendlyByteBuf buffer) {

        super(buffer);
        this._stack = buffer.readFluidStack();
    }

    public FluidStack getStack() {
        return this._stack;
    }

    //region AbstractModTileMessage

    @Override
    public void encodeTo(FriendlyByteBuf buffer) {

        super.encodeTo(buffer);
        buffer.writeFluidStack(this._stack);
    }

    @Override
    protected void processTileEntityMessage(LogicalSide sourceSide, BlockEntity tileEntity) {

        if (LogicalSide.SERVER == sourceSide && tileEntity instanceof AbstractFluidizerEntity fluidizerEntity) {
            fluidizerEntity.onUpdateFluidStatus(this);
        }
    }

    //endregion
    //region internals

    private final FluidStack _stack;

    //endregion
}
