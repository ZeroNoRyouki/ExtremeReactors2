/*
 *
 * BlockTagGenerator.java
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
import it.zerono.mods.extremereactors.gamecontent.Content.Blocks;
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.TagsProvider;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class BlockTagGenerator
        extends BlockTagsProvider {

    public BlockTagGenerator(final DataGenerator generator, final @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, ExtremeReactors.MOD_ID, existingFileHelper);
    }

    //region IDataProvider

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return ExtremeReactors.MOD_NAME + " Blocks Tags";
    }

    //endregion
    //region BlockTagsProvider

    @Override
    protected void addTags() {

        this.build(ContentTags.Blocks.BLOCKS_YELLORIUM, Blocks.YELLORIUM_BLOCK);
        this.build(ContentTags.Blocks.BLOCKS_CYANITE, Blocks.CYANITE_BLOCK);
        this.build(ContentTags.Blocks.BLOCKS_BLUTONIUM, Blocks.BLUTONIUM_BLOCK);
        this.build(ContentTags.Blocks.BLOCKS_MAGENTITE, Blocks.MAGENTITE_BLOCK);
        this.build(ContentTags.Blocks.BLOCKS_GRAPHITE, Blocks.GRAPHITE_BLOCK);
        this.build(ContentTags.Blocks.BLOCKS_LUDICRITE, Blocks.LUDICRITE_BLOCK);
        this.build(ContentTags.Blocks.BLOCKS_RIDICULITE, Blocks.RIDICULITE_BLOCK);
        this.build(ContentTags.Blocks.BLOCKS_INANITE, Blocks.INANITE_BLOCK);
        this.build(ContentTags.Blocks.BLOCKS_INSANITE, Blocks.INSANITE_BLOCK);

        this.build(ContentTags.Blocks.ORE_YELLORITE, Blocks.YELLORITE_ORE_BLOCK);

        this.build(Tags.Blocks.ORES, Blocks.YELLORITE_ORE_BLOCK, Blocks.ANGLESITE_ORE_BLOCK, Blocks.BENITOITE_ORE_BLOCK);
        this.build(TagsHelper.BLOCKS.createForgeOptionalTag("ores/uranium"), Blocks.YELLORITE_ORE_BLOCK);
    }

    //endregion
    //region internals

    @SafeVarargs
    private final void build(final ITag.INamedTag<Block> tag, final Supplier<? extends Block>... blocks) {

        final TagsProvider.Builder<Block> builder = this.tag(tag);

        for (final Supplier<? extends Block> block : blocks) {
            builder.add(block.get());
        }
    }

    private void buildOptional(final ITag.INamedTag<Block> tag, final ResourceLocation... blockIDs) {

        final TagsProvider.Builder<Block> builder = this.tag(tag);

        for (final ResourceLocation id : blockIDs) {
            builder.addOptional(id);
        }
    }

    //endregion
}
