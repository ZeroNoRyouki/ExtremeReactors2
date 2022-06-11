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
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.GenericDeviceBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactorPartType;
import it.zerono.mods.zerocore.lib.block.INeighborChangeListener;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockPart;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
    public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return this.getSignal(blockState, blockAccess, pos, side);
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return WorldHelper.getTile(blockAccess, pos)
                .filter(te -> te instanceof ReactorRedstonePortEntity)
                .map(te -> (ReactorRedstonePortEntity)te)
                .map(ReactorRedstonePortEntity::getOutputSignalPower)
                .orElse(0);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {

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

                    WorldHelper.spawnVanillaParticles(world, DustParticleOptions.REDSTONE, 1, 1,
                            pos.getX(), pos.getY(), pos.getZ(), 0, 1, 0);
                });
    }

    //endregion
}
