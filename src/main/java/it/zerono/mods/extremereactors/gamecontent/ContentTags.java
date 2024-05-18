/*
 *
 * ContentTags.java
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

package it.zerono.mods.extremereactors.gamecontent;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.Reactants;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class ContentTags {

    public static final class Blocks {

        public static final TagKey<Block> ORE_YELLORITE = common("ores/yellorite");

        public static final TagKey<Block> BLOCKS_YELLORIUM = common("storage_blocks/yellorium");
        public static final TagKey<Block> BLOCKS_CYANITE = common("storage_blocks/cyanite");
        public static final TagKey<Block> BLOCKS_GRAPHITE = common("storage_blocks/graphite");
        public static final TagKey<Block> BLOCKS_BLUTONIUM = common("storage_blocks/blutonium");
        public static final TagKey<Block> BLOCKS_MAGENTITE = common("storage_blocks/magentite");
        public static final TagKey<Block> BLOCKS_LUDICRITE = common("storage_blocks/ludicrite");
        public static final TagKey<Block> BLOCKS_RIDICULITE = common("storage_blocks/ridiculite");
        public static final TagKey<Block> BLOCKS_INANITE = common("storage_blocks/inanite");
        public static final TagKey<Block> BLOCKS_INSANITE = common("storage_blocks/insanite");

        //region internals

        private static TagKey<Block> common(final String name) {
            return TagsHelper.BLOCKS.createCommonKey(name);
        }

        //endregion
    }

    public static final class Items {

		public static final TagKey<Item> ORE_YELLORITE = common("ores/yellorite");

        public static final TagKey<Item> INGOTS_YELLORIUM = common("ingots/yellorium");
        public static final TagKey<Item> INGOTS_CYANITE = common("ingots/cyanite");
        public static final TagKey<Item> INGOTS_GRAPHITE = common("ingots/graphite");
        public static final TagKey<Item> INGOTS_BLUTONIUM = common("ingots/blutonium");
        public static final TagKey<Item> INGOTS_MAGENTITE = common("ingots/magentite");
        public static final TagKey<Item> INGOTS_LUDICRITE = common("ingots/ludicrite");
        public static final TagKey<Item> INGOTS_RIDICULITE = common("ingots/ridiculite");
        public static final TagKey<Item> INGOTS_INANITE = common("ingots/inanite");
        public static final TagKey<Item> INGOTS_INSANITE = common("ingots/insanite");

        public static final TagKey<Item> INGOTS_URANIUM = common("ingots/uranium");
        public static final TagKey<Item> INGOTS_PLUTONIUM = common("ingots/plutonium");

        public static final TagKey<Item> NUGGETS_YELLORIUM = common("nuggets/yellorium");
        public static final TagKey<Item> NUGGETS_BLUTONIUM = common("nuggets/blutonium");

        public static final TagKey<Item> DUSTS_YELLORIUM = common("dusts/yellorium");
        public static final TagKey<Item> DUSTS_CYANITE = common("dusts/cyanite");
        public static final TagKey<Item> DUSTS_GRAPHITE = common("dusts/graphite");
        public static final TagKey<Item> DUSTS_BLUTONIUM = common("dusts/blutonium");
        public static final TagKey<Item> DUSTS_MAGENTITE = common("dusts/magentite");
        public static final TagKey<Item> DUSTS_LUDICRITE = common("dusts/ludicrite");
        public static final TagKey<Item> DUSTS_RIDICULITE = common("dusts/ridiculite");
        public static final TagKey<Item> DUSTS_INANITE = common("dusts/inanite");
        public static final TagKey<Item> DUSTS_INSANITE = common("dusts/insanite");

        public static final TagKey<Item> BLOCKS_YELLORIUM = common("storage_blocks/yellorium");
        public static final TagKey<Item> BLOCKS_CYANITE = common("storage_blocks/cyanite");
        public static final TagKey<Item> BLOCKS_GRAPHITE = common("storage_blocks/graphite");
        public static final TagKey<Item> BLOCKS_BLUTONIUM = common("storage_blocks/blutonium");
        public static final TagKey<Item> BLOCKS_MAGENTITE = common("storage_blocks/magentite");
        public static final TagKey<Item> BLOCKS_LUDICRITE = common("storage_blocks/ludicrite");
        public static final TagKey<Item> BLOCKS_RIDICULITE = common("storage_blocks/ridiculite");
        public static final TagKey<Item> BLOCKS_INANITE = common("storage_blocks/inanite");
        public static final TagKey<Item> BLOCKS_INSANITE = common("storage_blocks/insanite");

        public static final TagKey<Item> USING_REACTOR_CASING_BASIC = tag("reactor/casing_user/basic");
        public static final TagKey<Item> USING_REACTOR_CASING_REINFORCED = tag("reactor/casing_user/reinforced");
        public static final TagKey<Item> USING_TURBINE_CASING_BASIC = tag("turbine/casing_user/basic");
        public static final TagKey<Item> USING_TURBINE_CASING_REINFORCED = tag("turbine/casing_user/reinforced");

        //region internals

        private static TagKey<Item> tag(final String name) {
            return TagsHelper.ITEMS.createKey(ExtremeReactors.MOD_ID, name);
        }

        private static TagKey<Item> common(final String name) {
            return TagsHelper.ITEMS.createCommonKey(name);
        }

        //endregion
    }

    public static final class Fluids {

        public static final TagKey<Fluid> STEAM = common("steam");
        public static final TagKey<Fluid> WATER = FluidTags.WATER;

        public static final TagKey<Fluid> YELLORIUM = common(Reactants.Yellorium);
        public static final TagKey<Fluid> CYANITE = common(Reactants.Cyanite);
        public static final TagKey<Fluid> BLUTONIUM = common(Reactants.Blutonium);
        public static final TagKey<Fluid> MAGENTITE = common(Reactants.Magentite);
        public static final TagKey<Fluid> VERDERIUM = common(Reactants.Verderium);
        public static final TagKey<Fluid> ROSSINITE = common(Reactants.Rossinite);

        //region internals

        private static TagKey<Fluid> common(final String name) {
            return TagsHelper.FLUIDS.createCommonKey(name);
        }

        private static TagKey<Fluid> common(final Reactants reactant) {
            return common(reactant.getTagName());
        }

        //endregion
    }
}
