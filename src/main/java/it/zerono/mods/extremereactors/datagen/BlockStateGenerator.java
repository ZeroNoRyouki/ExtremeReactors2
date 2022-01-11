/*
 *
 * BlockStateGenerator.java
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
import it.zerono.mods.extremereactors.gamecontent.Content;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class BlockStateGenerator
        extends BlockStateProvider {

    public BlockStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, ExtremeReactors.MOD_ID, exFileHelper);
    }

    //region BlockStateProvider

    @Override
    protected void registerStatesAndModels() {

        this.simpleBlocksAndItems(Content.Blocks.YELLORIUM_BLOCK, Content.Blocks.CYANITE_BLOCK,
                Content.Blocks.GRAPHITE_BLOCK, Content.Blocks.BLUTONIUM_BLOCK, Content.Blocks.MAGENTITE_BLOCK,
                Content.Blocks.LUDICRITE_BLOCK, Content.Blocks.RIDICULITE_BLOCK, Content.Blocks.INANITE_BLOCK,
                Content.Blocks.YELLORITE_ORE_BLOCK, Content.Blocks.ANGLESITE_ORE_BLOCK, Content.Blocks.BENITOITE_ORE_BLOCK);
    }

    @Nonnull
    @Override
    public String getName() {
        return ExtremeReactors.MOD_NAME + " blockstates and models";
    }

    //endregion

    @SafeVarargs
    protected final void simpleBlocksAndItems(final Supplier<? extends Block>... blocks) {

        for (final Supplier<? extends Block> block : blocks) {

            final Block b = block.get();
            final ModelFile model = this.cubeAll(b);

            this.simpleBlock(b, model);
            this.simpleBlockItem(b, model);
        }
    }
}
