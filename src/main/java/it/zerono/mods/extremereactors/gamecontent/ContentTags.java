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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class ContentTags {

    public static final class Blocks {

        public static final TagKey<Block> ORE_YELLORITE = forgeTag("ores/yellorite");

        public static final TagKey<Block> BLOCKS_YELLORIUM = forgeTag("storage_blocks/yellorium");
        public static final TagKey<Block> BLOCKS_CYANITE = forgeTag("storage_blocks/cyanite");
        public static final TagKey<Block> BLOCKS_GRAPHITE = forgeTag("storage_blocks/graphite");
        public static final TagKey<Block> BLOCKS_BLUTONIUM = forgeTag("storage_blocks/blutonium");
        public static final TagKey<Block> BLOCKS_MAGENTITE = forgeTag("storage_blocks/magentite");
        public static final TagKey<Block> BLOCKS_LUDICRITE = forgeTag("storage_blocks/ludicrite");
        public static final TagKey<Block> BLOCKS_RIDICULITE = forgeTag("storage_blocks/ridiculite");
        public static final TagKey<Block> BLOCKS_INANITE = forgeTag("storage_blocks/inanite");
        public static final TagKey<Block> BLOCKS_INSANITE = forgeTag("storage_blocks/insanite");

        //region internals

        private static TagKey<Block> forgeTag(final String name) {
            return TagsHelper.BLOCKS.createKey("forge:" + name);
        }

        //endregion
    }

    public static final class Items {

		public static final TagKey<Item> ORE_YELLORITE = forgeTag("ores/yellorite");

        public static final TagKey<Item> INGOTS_YELLORIUM = forgeTag("ingots/yellorium");
        public static final TagKey<Item> INGOTS_CYANITE = forgeTag("ingots/cyanite");
        public static final TagKey<Item> INGOTS_GRAPHITE = forgeTag("ingots/graphite");
        public static final TagKey<Item> INGOTS_BLUTONIUM = forgeTag("ingots/blutonium");
        public static final TagKey<Item> INGOTS_MAGENTITE = forgeTag("ingots/magentite");
        public static final TagKey<Item> INGOTS_LUDICRITE = forgeTag("ingots/ludicrite");
        public static final TagKey<Item> INGOTS_RIDICULITE = forgeTag("ingots/ridiculite");
        public static final TagKey<Item> INGOTS_INANITE = forgeTag("ingots/inanite");
        public static final TagKey<Item> INGOTS_INSANITE = forgeTag("ingots/insanite");

        public static final TagKey<Item> NUGGETS_YELLORIUM = forgeTag("nuggets/yellorium");
        public static final TagKey<Item> NUGGETS_BLUTONIUM = forgeTag("nuggets/blutonium");

        public static final TagKey<Item> DUSTS_YELLORIUM = forgeTag("dusts/yellorium");
        public static final TagKey<Item> DUSTS_CYANITE = forgeTag("dusts/cyanite");
        public static final TagKey<Item> DUSTS_GRAPHITE = forgeTag("dusts/graphite");
        public static final TagKey<Item> DUSTS_BLUTONIUM = forgeTag("dusts/blutonium");
        public static final TagKey<Item> DUSTS_MAGENTITE = forgeTag("dusts/magentite");
        public static final TagKey<Item> DUSTS_LUDICRITE = forgeTag("dusts/ludicrite");
        public static final TagKey<Item> DUSTS_RIDICULITE = forgeTag("dusts/ridiculite");
        public static final TagKey<Item> DUSTS_INANITE = forgeTag("dusts/inanite");
        public static final TagKey<Item> DUSTS_INSANITE = forgeTag("dusts/insanite");

        public static final TagKey<Item> BLOCKS_YELLORIUM = forgeTag("storage_blocks/yellorium");
        public static final TagKey<Item> BLOCKS_CYANITE = forgeTag("storage_blocks/cyanite");
        public static final TagKey<Item> BLOCKS_GRAPHITE = forgeTag("storage_blocks/graphite");
        public static final TagKey<Item> BLOCKS_BLUTONIUM = forgeTag("storage_blocks/blutonium");
        public static final TagKey<Item> BLOCKS_MAGENTITE = forgeTag("storage_blocks/magentite");
        public static final TagKey<Item> BLOCKS_LUDICRITE = forgeTag("storage_blocks/ludicrite");
        public static final TagKey<Item> BLOCKS_RIDICULITE = forgeTag("storage_blocks/ridiculite");
        public static final TagKey<Item> BLOCKS_INANITE = forgeTag("storage_blocks/inanite");
        public static final TagKey<Item> BLOCKS_INSANITE = forgeTag("storage_blocks/insanite");

        public static final TagKey<Item> USING_REACTOR_CASING_BASIC = tag("reactor/casing_user/basic");
        public static final TagKey<Item> USING_REACTOR_CASING_REINFORCED = tag("reactor/casing_user/reinforced");
        public static final TagKey<Item> USING_TURBINE_CASING_BASIC = tag("turbine/casing_user/basic");
        public static final TagKey<Item> USING_TURBINE_CASING_REINFORCED = tag("turbine/casing_user/reinforced");

        //region internals

        private static TagKey<Item> tag(final String name) {
            return TagsHelper.ITEMS.createKey(new ResourceLocation(ExtremeReactors.MOD_ID, name));
        }

        private static TagKey<Item> forgeTag(final String name) {
            return TagsHelper.ITEMS.createKey("forge:" + name);
        }

        //endregion
    }

    public static final class Fluids {

        public static final TagKey<Fluid> STEAM = forgeTag("steam");
        public static final TagKey<Fluid> WATER = FluidTags.WATER;

        public static final TagKey<Fluid> YELLORIUM = forgeTag(Reactants.Yellorium);
        public static final TagKey<Fluid> CYANITE = forgeTag(Reactants.Cyanite);
        public static final TagKey<Fluid> BLUTONIUM = forgeTag(Reactants.Blutonium);
        public static final TagKey<Fluid> MAGENTITE = forgeTag(Reactants.Magentite);
        public static final TagKey<Fluid> VERDERIUM = forgeTag(Reactants.Verderium);
        public static final TagKey<Fluid> ROSSINITE = forgeTag(Reactants.Rossinite);

        //region internals

        private static TagKey<Fluid> forgeTag(final String name) {
            return TagsHelper.FLUIDS.createKey("forge:" + name);
        }

        private static TagKey<Fluid> forgeTag(final Reactants reactant) {
            return forgeTag(reactant.getTagName());
        }

        //endregion
    }
}
