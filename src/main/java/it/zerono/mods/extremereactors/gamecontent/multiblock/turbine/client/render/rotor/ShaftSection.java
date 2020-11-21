/*
 *
 * ShaftSection.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.render.rotor;

import com.google.common.collect.Lists;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorBladeState;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorShaftState;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ShaftSection {

    final RotorShaftState State;
    final IModelData ShaftModelData;
    final BladeSpan[] Blades;

    public static Builder builder(final RotorShaftState state) {
        return new Builder(state);
    }

    public static class Builder {

        public ShaftSection build() {
            return new ShaftSection(this._state, this._blades);
        }

        public Builder addBlade(final RotorBladeState state, final short length, final Direction direction) {

            this._blades.add(BladeSpan.from(state, length, direction));
            return this;
        }

        //region internals

        private Builder(final RotorShaftState state) {

            this._state = state;
            this._blades = Lists.newArrayListWithCapacity(4);
        }

        private final RotorShaftState _state;
        private final List<BladeSpan> _blades;

        //endregion
    }

    //region internals

    private ShaftSection(final RotorShaftState state, final List<BladeSpan> blades) {

        this.State = state;
        this.ShaftModelData = ComponentModelData.from(state);
        this.Blades = blades.toArray(new BladeSpan[0]);
    }

    //endregion
}
