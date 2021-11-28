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
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;

public class ContentTags {

    public static final class Blocks {

        public static final ITag.INamedTag<Block> ORE_YELLORITE = forgeTag("ores/yellorite");

        public static final ITag.INamedTag<Block> BLOCKS_YELLORIUM = forgeTag("storage_blocks/yellorium");
        public static final ITag.INamedTag<Block> BLOCKS_CYANITE = forgeTag("storage_blocks/cyanite");
        public static final ITag.INamedTag<Block> BLOCKS_GRAPHITE = forgeTag("storage_blocks/graphite");
        public static final ITag.INamedTag<Block> BLOCKS_BLUTONIUM = forgeTag("storage_blocks/blutonium");
        public static final ITag.INamedTag<Block> BLOCKS_MAGENTITE = forgeTag("storage_blocks/magentite");

        //region internals

        private static ITag.INamedTag<Block> forgeTag(final String name) {
            return TagsHelper.BLOCKS.createForgeTag(name);
        }

        //endregion
    }

    public static final class Items {

        public static final ITag.INamedTag<Item> ORE_YELLORITE = forgeTag("ores/yellorite");

        public static final ITag.INamedTag<Item> INGOTS_YELLORIUM = forgeTag("ingots/yellorium");
        public static final ITag.INamedTag<Item> INGOTS_CYANITE = forgeTag("ingots/cyanite");
        public static final ITag.INamedTag<Item> INGOTS_GRAPHITE = forgeTag("ingots/graphite");
        public static final ITag.INamedTag<Item> INGOTS_BLUTONIUM = forgeTag("ingots/blutonium");
        public static final ITag.INamedTag<Item> INGOTS_MAGENTITE = forgeTag("ingots/magentite");

        public static final ITag.INamedTag<Item> DUSTS_YELLORIUM = forgeTag("dusts/yellorium");
        public static final ITag.INamedTag<Item> DUSTS_CYANITE = forgeTag("dusts/cyanite");
        public static final ITag.INamedTag<Item> DUSTS_GRAPHITE = forgeTag("dusts/graphite");
        public static final ITag.INamedTag<Item> DUSTS_BLUTONIUM = forgeTag("dusts/blutonium");
        public static final ITag.INamedTag<Item> DUSTS_MAGENTITE = forgeTag("dusts/magentite");

        public static final ITag.INamedTag<Item> BLOCKS_YELLORIUM = forgeTag("storage_blocks/yellorium");
        public static final ITag.INamedTag<Item> BLOCKS_CYANITE = forgeTag("storage_blocks/cyanite");
        public static final ITag.INamedTag<Item> BLOCKS_GRAPHITE = forgeTag("storage_blocks/graphite");
        public static final ITag.INamedTag<Item> BLOCKS_BLUTONIUM = forgeTag("storage_blocks/blutonium");
        public static final ITag.INamedTag<Item> BLOCKS_MAGENTITE = forgeTag("storage_blocks/magentite");

        public static final ITag.INamedTag<Item> WRENCH = forgeTag("tools/wrench");

        public static final ITag.INamedTag<Item> USING_REACTOR_CASING_BASIC = tag("reactor/casing_user/basic");
        public static final ITag.INamedTag<Item> USING_REACTOR_CASING_REINFORCED = tag("reactor/casing_user/reinforced");
        public static final ITag.INamedTag<Item> USING_TURBINE_CASING_BASIC = tag("turbine/casing_user/basic");
        public static final ITag.INamedTag<Item> USING_TURBINE_CASING_REINFORCED = tag("turbine/casing_user/reinforced");

        //region internals

        private static ITag.INamedTag<Item> tag(final String name) {
            return TagsHelper.ITEMS.createModTag(ExtremeReactors.MOD_ID, name);
        }

        private static ITag.INamedTag<Item> forgeTag(final String name) {
            return TagsHelper.ITEMS.createForgeTag(name);
        }

        //endregion
    }

    public static final class Fluids {

        public static final ITag.INamedTag<Fluid> STEAM = forgeTag("steam");
        public static final ITag.INamedTag<Fluid> WATER = FluidTags.WATER;

        public static final ITag.INamedTag<Fluid> YELLORIUM = forgeTag(Reactants.Yellorium);
        public static final ITag.INamedTag<Fluid> CYANITE = forgeTag(Reactants.Cyanite);
        public static final ITag.INamedTag<Fluid> BLUTONIUM = forgeTag(Reactants.Blutonium);
        public static final ITag.INamedTag<Fluid> MAGENTITE = forgeTag(Reactants.Magentite);
        public static final ITag.INamedTag<Fluid> VERDERIUM = forgeTag(Reactants.Verderium);
        public static final ITag.INamedTag<Fluid> ROSSINITE = forgeTag(Reactants.Rossinite);

        //region internals

        private static ITag.INamedTag<Fluid> forgeTag(final String name) {
            return TagsHelper.FLUIDS.createForgeTag(name);
        }

        private static ITag.INamedTag<Fluid> forgeTag(final Reactants reactant) {
            return forgeTag(reactant.getTagName());
        }

        //endregion
    }
}
