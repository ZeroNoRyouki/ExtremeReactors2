/*
 *
 * TurbineRotorBearingBlock.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part;

import it.zerono.mods.extremereactors.config.Config;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.GenericDeviceBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.MultiblockTurbine;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.TurbinePartType;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;
import java.util.Random;

import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock.MultiblockPartProperties;

public class TurbineRotorBearingBlock
        extends GenericDeviceBlock<MultiblockTurbine, TurbinePartType> {

    public TurbineRotorBearingBlock(final MultiblockPartProperties<TurbinePartType> properties) {
        super(properties);
    }

    //region Block

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos position, Random random) {

        if (Config.CLIENT.disableTurbineParticles.get()) {
            return;
        }

        getTile(world, position).ifPresent(bearing -> this.animateTick(bearing, world, position, random));
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(final TurbineRotorBearingEntity bearing, final Level world,
                            final BlockPos pos, final Random random) {

        bearing.getMultiblockController()
                .filter(turbine -> !turbine.isInteriorInvisible())
                .filter(MultiblockTurbine::isAssembledAndActive)
                .ifPresent(turbine -> turbine.forBoundingBoxCoordinates((min, max) -> {

                    // Spawn particles!

                    final int numParticles = Math.min(20, Math.max(1, turbine.getFluidConsumedLastTick() / 40));
                    final BlockPos minCoord = min.offset(1, 1, 1);
                    final BlockPos maxCoord = max.offset(-1, -1, -1);
                    final Direction inwardsDir = bearing.getOutwardDirection().map(Direction::getOpposite).orElse(Direction.UP);
                    final int offsetX = inwardsDir.getStepX();
                    final int offsetY = inwardsDir.getStepY();
                    final int offsetZ = inwardsDir.getStepZ();
                    final ParticleOptions particle = ParticleTypes.CLOUD; // TODO valentines HEART

                    int minX = minCoord.getX();
                    int minY = minCoord.getY();
                    int minZ = minCoord.getZ();
                    int maxX = maxCoord.getX();
                    int maxY = maxCoord.getY();
                    int maxZ = maxCoord.getZ();

                    if (offsetX != 0) {
                        minX = maxX = pos.getX() + offsetX;
                    } else if (offsetY != 0) {
                        minY = maxY = pos.getY() + offsetY;
                    } else {
                        minZ = maxZ = pos.getZ() + offsetZ;
                    }

                    int particleX, particleY, particleZ;

                    for (int i = 0; i < numParticles; i++) {

                        particleX = minX + (int)(random.nextFloat() * (maxX - minX + 1));
                        particleY = minY + (int)(random.nextFloat() * (maxY - minY + 1));
                        particleZ = minZ + (int)(random.nextFloat() * (maxZ - minZ + 1));

                        WorldHelper.spawnVanillaParticles(world, particle, 1, numParticles,
                                particleX, particleY, particleZ, 0, 0, 0);
                    }
                }));
    }

    //endregion
    //region internals

    private static Optional<TurbineRotorBearingEntity> getTile(final BlockGetter world, final BlockPos position) {
        return WorldHelper.getTile(world, position)
                .filter(te -> te instanceof TurbineRotorBearingEntity)
                .map(te -> (TurbineRotorBearingEntity)te);
    }

    //endregion
}
