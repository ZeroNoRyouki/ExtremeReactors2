/*
 *
 * ReactorRedstonePortBlock.java
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

import it.zerono.mods.extremereactors.config.Config;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactorPartType;
import it.zerono.mods.zerocore.base.multiblock.part.GenericDeviceBlock;
import it.zerono.mods.zerocore.lib.block.INeighborChangeListener;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockPart;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class ReactorRedstonePortBlock
        extends GenericDeviceBlock<MultiblockReactor, ReactorPartType>
        implements INeighborChangeListener.Notifier {

    public ReactorRedstonePortBlock(final MultiblockPartProperties<ReactorPartType> properties) {
        super(properties);
    }

    //region Block

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state..
     */
    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getDirectSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return this.getSignal(blockState, blockAccess, pos, side);
    }

    @Override
    public int getSignal(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return WorldHelper.getTile(blockAccess, pos)
                .filter(te -> te instanceof ReactorRedstonePortEntity)
                .map(te -> (ReactorRedstonePortEntity)te)
                .map(ReactorRedstonePortEntity::getOutputSignalPower)
                .orElse(0);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {

        if (Config.CLIENT.disableReactorParticles.get()) {
            return;
        }

        // 1.15 bug MC-161917: Particles do not render underwater, behind water or behind other transparent blocks
        // https://bugs.mojang.com/browse/MC-161917
        // fixed in 20w22a (last pre-1.16 snapshot)
        WorldHelper.getTile(world, pos)
                .filter(te -> te instanceof ReactorRedstonePortEntity)
                .map(te -> (ReactorRedstonePortEntity)te)
                //.filter(port -> port.isLit())
                .flatMap(AbstractCuboidMultiblockPart::getOutwardDirection)
                .ifPresent(direction -> {

                    WorldHelper.spawnVanillaParticles(world, RedstoneParticleData.REDSTONE, 1, 1,
                            pos.getX(), pos.getY(), pos.getZ(), 0, 1, 0);
                });
    }

    //endregion
}
