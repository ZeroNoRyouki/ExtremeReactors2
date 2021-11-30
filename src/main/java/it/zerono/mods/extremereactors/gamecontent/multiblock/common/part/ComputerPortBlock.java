/*
 *
 * ComputerPortBlock.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common.part;

import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartType;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;

import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock.MultiblockPartProperties;

public class ComputerPortBlock<Controller extends IMultiblockController<Controller>,
        PartType extends Enum<PartType> & IMultiblockPartType> extends GenericDeviceBlock<Controller, PartType> {

    public ComputerPortBlock(final MultiblockPartProperties<PartType> properties) {
        super(properties);
    }

    //TODO computers
    /*
    @Optional.Method(modid = IdReference.MODID_COMPUTERCRAFT)
    @Override
    public IPeripheral getPeripheral(Level world, BlockPos pos, EnumFacing side) {

        final TileEntity tileEntity = WorldHelper.getTile(world, pos);
        Connector computer = null;

        if (tileEntity instanceof TileEntityReactorComputerPort) {
            computer = ((TileEntityReactorComputerPort) tileEntity).getComputerCraftPeripheral();
        } else if (tileEntity instanceof TileEntityTurbineComputerPort) {
            computer = ((TileEntityTurbineComputerPort) tileEntity).getComputerCraftPeripheral();
        }

        return computer instanceof IPeripheral ? (IPeripheral)computer : null;
    }
    */
}
