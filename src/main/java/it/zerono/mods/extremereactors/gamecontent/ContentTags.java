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

        //region internals

        private static ITag<Block> tag(final String name) {
            return TagsHelper.BLOCKS.createModTag(ExtremeReactors.MOD_ID, name);
        }

        private static ITag.INamedTag<Block> forgeTag(final String name) {
            return TagsHelper.BLOCKS.createForgeTag(name);
        }

        //endregion
    }

    public static final class Items {

        public static final ITag.INamedTag<Item> INGOTS_YELLORIUM = forgeTag("ingots/yellorium");
        public static final ITag.INamedTag<Item> INGOTS_CYANITE = forgeTag("ingots/cyanite");
        public static final ITag.INamedTag<Item> INGOTS_GRAPHITE = forgeTag("ingots/graphite");

        public static final ITag.INamedTag<Item> DUSTS_YELLORIUM = forgeTag("dusts/yellorium");
        public static final ITag.INamedTag<Item> DUSTS_CYANITE = forgeTag("dusts/cyanite");
        public static final ITag.INamedTag<Item> DUSTS_GRAPHITE = forgeTag("dusts/graphite");

        public static final ITag.INamedTag<Item> BLOCKS_YELLORIUM = forgeTag("storage_blocks/yellorium");
        public static final ITag.INamedTag<Item> BLOCKS_CYANITE = forgeTag("storage_blocks/cyanite");
        public static final ITag.INamedTag<Item> BLOCKS_GRAPHITE = forgeTag("storage_blocks/graphite");

        public static final ITag.INamedTag<Item> WRENCH = forgeTag("tools/wrench");

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

        //region internals

        private static ITag.INamedTag<Fluid> forgeTag(final String name) {
            return TagsHelper.FLUIDS.createForgeTag(name);
        }

        //endregion
    }
}
