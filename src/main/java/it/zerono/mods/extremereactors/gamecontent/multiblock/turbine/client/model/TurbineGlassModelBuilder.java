/*
 *
 * TurbineGlassModelBuilder.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.model;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.zerocore.base.multiblock.client.model.AbstractMultiblockModelBuilder;

public abstract class TurbineGlassModelBuilder
        extends AbstractMultiblockModelBuilder {

    public static class Basic
            extends TurbineGlassModelBuilder {

        public Basic() {
            super(TurbineVariant.Basic);
        }

        @Override
        public void build() {

            this.addGlass(Content.Blocks.TURBINE_GLASS_BASIC.get());
            this.setFallbackModelData(Content.Blocks.TURBINE_GLASS_BASIC.get());
        }
    }

    public static class Reinforced
            extends TurbineGlassModelBuilder {

        public Reinforced() {
            super(TurbineVariant.Reinforced);
        }

        @Override
        public void build() {

            this.addGlass(Content.Blocks.TURBINE_GLASS_REINFORCED.get());
            this.setFallbackModelData(Content.Blocks.TURBINE_GLASS_REINFORCED.get());
        }
    }

    protected TurbineGlassModelBuilder(final TurbineVariant variant) {
        super(ExtremeReactors.ROOT_LOCATION.appendPath("block", "turbine", variant.getName()));
    }

    //endregion
}
