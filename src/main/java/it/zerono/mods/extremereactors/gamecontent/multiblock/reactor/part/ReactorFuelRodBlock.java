/*
 *
 * ReactorFuelRodBlock.java
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

import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactorPartType;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ReactorFuelRodBlock extends MultiblockPartBlock<MultiblockReactor, ReactorPartType> {

    public ReactorFuelRodBlock(final MultiblockPartProperties<ReactorPartType> properties) {
        super(properties);
    }

    //region Block

    @Override
    public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
        return this == adjacentBlockState.getBlock();
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
        return true;
    }

    public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 1.0f;
    }

//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
//
//        if (Config.CLIENT.disableReactorParticles.get()) {
//            return;
//        }
//
//        // 1.15 bug MC-161917: Particles do not render underwater, behind water or behind other transparent blocks
//        // https://bugs.mojang.com/browse/MC-161917
//        // fixed in 20w22a (last pre-1.16 snapshot)
//        WorldHelper.getTile(world, pos)
//                .filter(te -> te instanceof ReactorFuelRodEntity)
//                .map(te -> (ReactorFuelRodEntity)te)
////                .filter(rod -> !rod.isOccluded())
//                .flatMap(ReactorFuelRodEntity::getMultiblockController)
//                .ifPresent(reactor -> {
//
//                    if (!reactor.isInteriorInvisible() && reactor.isMachineActive() && reactor.getUiStats().getFuelConsumedLastTick() > 0) {
//                        WorldHelper.spawnVanillaParticles(world, Config.CLIENT.isValentinesDay ? ParticleTypes.HEART : ParticleTypes.CRIT,
//                                1, random.nextInt(4) + 1, pos.getX(), pos.getY(), pos.getZ(), 1, 1, 1);
//                    }
//                });
//    }

    //endregion
    //region ModBlock

    @Override
    protected void buildBlockState(final StateContainer.Builder<Block, BlockState> builder) {

        super.buildBlockState(builder);
        builder.add(BlockStateProperties.AXIS);
    }

    @Override
    protected BlockState buildDefaultState(final BlockState state) {
        return super.buildDefaultState(state).with(BlockStateProperties.AXIS, Direction.Axis.Y);
    }

    //endregion
}
