/*
 *
 * GenericRecipeGenerator.java
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

package it.zerono.mods.extremereactors.datagen.recipes;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class GenericRecipeGenerator
        extends AbstractRecipeGenerator {

    public GenericRecipeGenerator(final DataGenerator generatorIn) {
        super(generatorIn);
    }

    //region RecipeProvider

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return ExtremeReactors.MOD_NAME + "Generic recipes";
    }

    /**
     * Registers all recipes to the given consumer.
     */
    @Override
    protected void registerRecipes(final Consumer<IFinishedRecipe> c) {

        // ingots / (storage) blocks
        this.storageBlock3x3(c, "yellorium", Content.Items.YELLORIUM_INGOT, Content.Items.YELLORIUM_BLOCK);
        this.storageBlock3x3(c, "cyanite", Content.Items.CYANITE_INGOT, Content.Items.CYANITE_BLOCK);
        this.storageBlock3x3(c, "graphite", Content.Items.GRAPHITE_INGOT, Content.Items.GRAPHITE_BLOCK);

        // smelting

        this.blastingAndSmelting(c, "yellorium_from_ore", Content.Items.YELLORIUM_INGOT, Content.Items.YELLORITE_ORE_BLOCK);
        this.blastingAndSmelting(c, "yellorium_from_dust", Content.Items.YELLORIUM_INGOT, Content.Items.YELLORIUM_DUST);
        this.blastingAndSmelting(c, "cyanite_from_dust", Content.Items.CYANITE_INGOT, Content.Items.CYANITE_DUST);
        this.blastingAndSmelting(c, "graphite_from_dust", Content.Items.GRAPHITE_INGOT, Content.Items.GRAPHITE_DUST);
        this.blastingAndSmelting(c, "graphite_from_coal", Content.Items.GRAPHITE_INGOT, () -> net.minecraft.item.Items.COAL);
        this.blastingAndSmelting(c, "graphite_from_charcoal", Content.Items.GRAPHITE_INGOT, () -> net.minecraft.item.Items.CHARCOAL);
        this.blastingAndSmelting(c, "graphite_from_coalblock", Content.Items.GRAPHITE_BLOCK, () -> net.minecraft.item.Items.COAL_BLOCK, 0.9f, 1800);

        // misc

        ShapedRecipeBuilder.shapedRecipe(Content.Items.WRENCH.get())
                .key('I', Tags.Items.INGOTS_IRON)
                .key('W', ItemTags.WOOL)
                .key('D', Tags.Items.DYES_GREEN)
                .patternLine("DI ")
                .patternLine("WII")
                .patternLine("IW ")
                .setGroup(GROUP_GENERAL)
                .addCriterion("has_item", hasItem(Content.Items.WRENCH.get()))
                .build(c, ExtremeReactors.newID("misc/wrench"));
    }

    //endregion
}
