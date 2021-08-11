/*
 *
 * ReactorMekanismFluidPortEntity.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part;

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.fluidport.FluidPortType;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.IoMode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ReactorMekanismFluidPortEntity
    extends ReactorFluidPortEntity {

    public ReactorMekanismFluidPortEntity(final BlockPos position, final BlockState blockState) {
        super(FluidPortType.Mekanism, IoMode.Passive, Content.TileEntityTypes.REACTOR_FLUIDPORT_MEKANISM_PASSIVE.get(),
                position, blockState);
    }

    //region IIoEntity

    @Override
    public IoDirection getIoDirection() {
        return IoDirection.Output;
    }

    @Override
    public void setIoDirection(IoDirection direction) {
    }

    //endregion
}
