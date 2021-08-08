/*
 *
 * ReactorFuelRodBlockColor.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.model;

import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.FuelRodsLayout;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.ClientFuelRodsLayout;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorFuelRodEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;

import javax.annotation.Nullable;

public class ReactorFuelRodBlockColor
    implements BlockColor {

    //region IBlockColor

    @Override
    public int getColor(BlockState state, @Nullable BlockAndTintGetter world, @Nullable BlockPos position, int tintIndex) {

        if (null != world && null != position) {

            final BlockEntity te = world.getBlockEntity(position);

            if (te instanceof ReactorFuelRodEntity) {

                final FuelRodsLayout layout = ((ReactorFuelRodEntity)te).getFuelRodsLayoutForRendering();

                if (layout instanceof ClientFuelRodsLayout) {
                    return ((ClientFuelRodsLayout)layout).getModelTint(tintIndex).toARGB();
                }
            }
        }

        return 0xFFFFFFFF;
    }

    //endregion
}
