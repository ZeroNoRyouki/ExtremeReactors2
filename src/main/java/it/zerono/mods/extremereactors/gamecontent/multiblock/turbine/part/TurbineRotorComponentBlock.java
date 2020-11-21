/*
 *
 * TurbineRotorComponentBlock.java
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

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.GenericDeviceBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.MultiblockTurbine;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.TurbinePartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorBladeState;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorShaftState;
import it.zerono.mods.zerocore.lib.block.INeighborChangeListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;

public abstract class TurbineRotorComponentBlock
        extends GenericDeviceBlock<MultiblockTurbine, TurbinePartType>
        implements INeighborChangeListener.Notifier {

    public static final EnumProperty<RotorShaftState> ROTOR_SHAFT_STATE = EnumProperty.create("state", RotorShaftState.class);
    public static final EnumProperty<RotorBladeState> ROTOR_BLADE_STATE = EnumProperty.create("state", RotorBladeState.class);

    public static TurbineRotorComponentBlock shaft(final MultiblockPartProperties<TurbinePartType> properties) {
        return new TurbineRotorComponentBlock(properties) {

            @Override
            public boolean isShaft() {
                return true;
            }

            @Override
            public boolean isBlade() {
                return false;
            }

            @Override
            protected void buildBlockState(final StateContainer.Builder<Block, BlockState> builder) {

                super.buildBlockState(builder);
                builder.add(ROTOR_SHAFT_STATE);
            }

            @Override
            protected BlockState buildDefaultState(BlockState state) {
                return super.buildDefaultState(state).with(ROTOR_SHAFT_STATE, RotorShaftState.getDefault());
            }
        };
    }

    public static TurbineRotorComponentBlock blade(final MultiblockPartProperties<TurbinePartType> properties) {
        return new TurbineRotorComponentBlock(properties) {

            @Override
            public boolean isShaft() {
                return false;
            }

            @Override
            public boolean isBlade() {
                return true;
            }

            @Override
            protected void buildBlockState(final StateContainer.Builder<Block, BlockState> builder) {

                super.buildBlockState(builder);
                builder.add(ROTOR_BLADE_STATE);
            }

            @Override
            protected BlockState buildDefaultState(BlockState state) {
                return super.buildDefaultState(state).with(ROTOR_BLADE_STATE, RotorBladeState.getDefault());
            }
        };
    }

    public abstract boolean isShaft();

    public abstract boolean isBlade();

    //region Block

    @Override
    public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
        return this == adjacentBlockState.getBlock();
    }

    //endregion
    //region internals

    protected TurbineRotorComponentBlock(final MultiblockPartProperties<TurbinePartType> properties) {
        super(properties);
    }

    //endregion
}
