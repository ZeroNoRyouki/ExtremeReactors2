/*
 *
 * ItemTagGenerator.java
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

package it.zerono.mods.extremereactors.datagen;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content.Items;
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.data.TagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraftforge.common.Tags;

import java.util.function.Supplier;

public class ItemTagGenerator
        extends ItemTagsProvider {

    public ItemTagGenerator(final DataGenerator generator, final BlockTagsProvider blockTagProvider) {
        super(generator, blockTagProvider, ExtremeReactors.MOD_ID, null);
    }

    //region IDataProvider

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return ExtremeReactors.MOD_NAME + " Items Tags";
    }

    //endregion
    //region BlockTagsProvider

    @Override
    protected void registerTags() {

        this.build(ContentTags.Items.INGOTS_YELLORIUM, Items.YELLORIUM_INGOT);
        this.build(ContentTags.Items.INGOTS_CYANITE, Items.CYANITE_INGOT);
        this.build(ContentTags.Items.INGOTS_GRAPHITE, Items.GRAPHITE_INGOT);

        this.build(ContentTags.Items.DUSTS_YELLORIUM, Items.YELLORIUM_DUST);
        this.build(ContentTags.Items.DUSTS_CYANITE, Items.CYANITE_DUST);
        this.build(ContentTags.Items.DUSTS_GRAPHITE, Items.GRAPHITE_DUST);

        this.build(ContentTags.Items.BLOCKS_YELLORIUM, Items.YELLORIUM_BLOCK);
        this.build(ContentTags.Items.BLOCKS_CYANITE, Items.CYANITE_BLOCK);
        this.build(ContentTags.Items.BLOCKS_GRAPHITE, Items.GRAPHITE_BLOCK);

        this.build(ContentTags.Items.WRENCH, Items.WRENCH);

        this.build(Tags.Items.INGOTS, Items.YELLORIUM_INGOT, Items.CYANITE_INGOT, Items.GRAPHITE_INGOT);
        this.build(Tags.Items.DUSTS, Items.YELLORIUM_DUST, Items.CYANITE_DUST, Items.GRAPHITE_DUST);

        this.build(TagsHelper.ITEMS.createOptionalForgeTag("dusts/uranium"), Items.YELLORIUM_DUST);
        this.build(TagsHelper.ITEMS.createOptionalForgeTag("ingots/uranium"), Items.YELLORIUM_INGOT);
    }

    //endregion
    //region internals

    @SafeVarargs
    private final void build(final ITag.INamedTag<Item> tag, final Supplier<? extends Item>... items) {

        final TagsProvider.Builder<Item> builder = this.getOrCreateBuilder(tag);

        for (Supplier<? extends Item> item : items) {
            builder.add(item.get());
        }
    }

    //endregion
}
