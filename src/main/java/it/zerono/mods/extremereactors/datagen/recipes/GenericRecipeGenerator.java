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
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.zerocore.lib.compat.Mods;
import it.zerono.mods.zerocore.lib.datagen.provider.recipe.NbtResultFinishedRecipeAdapter;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import vazkii.patchouli.api.PatchouliAPI;

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
        return ExtremeReactors.MOD_NAME + " Generic recipes";
    }

    /**
     * Registers all recipes to the given consumer.
     */
    @Override
    protected void buildCraftingRecipes(final Consumer<FinishedRecipe> c) {

        // ingots / (storage) blocks
        this.storageBlock3x3(c, "yellorium", Content.Items.YELLORIUM_INGOT, Content.Items.YELLORIUM_BLOCK);
        this.storageBlock3x3(c, "cyanite", Content.Items.CYANITE_INGOT, Content.Items.CYANITE_BLOCK);
        this.storageBlock3x3(c, "graphite", Content.Items.GRAPHITE_INGOT, Content.Items.GRAPHITE_BLOCK);
        this.storageBlock3x3(c, "blutonium", Content.Items.BLUTONIUM_INGOT, Content.Items.BLUTONIUM_BLOCK);
        this.storageBlock3x3(c, "magentite", Content.Items.MAGENTITE_INGOT, Content.Items.MAGENTITE_BLOCK);

        // smelting

        this.blastingAndSmelting(c, "yellorium_from_ore", Content.Items.YELLORIUM_INGOT, Content.Items.YELLORITE_ORE_BLOCK);
        this.blastingAndSmelting(c, "yellorium_from_dust", Content.Items.YELLORIUM_INGOT, Content.Items.YELLORIUM_DUST);
        this.blastingAndSmelting(c, "cyanite_from_dust", Content.Items.CYANITE_INGOT, Content.Items.CYANITE_DUST);
        this.blastingAndSmelting(c, "graphite_from_dust", Content.Items.GRAPHITE_INGOT, Content.Items.GRAPHITE_DUST);
        this.blastingAndSmelting(c, "blutonium_from_dust", Content.Items.BLUTONIUM_INGOT, Content.Items.BLUTONIUM_DUST);
        this.blastingAndSmelting(c, "magentite_from_dust", Content.Items.MAGENTITE_INGOT, Content.Items.MAGENTITE_DUST);
        this.blastingAndSmelting(c, "graphite_from_coal", Content.Items.GRAPHITE_INGOT, () -> Items.COAL, configCondition("registerCoalForSmelting"));
        this.blastingAndSmelting(c, "graphite_from_charcoal", Content.Items.GRAPHITE_INGOT, () -> Items.CHARCOAL, configCondition("registerCharcoalForSmelting"));
        this.blastingAndSmelting(c, "graphite_from_coalblock", Content.Items.GRAPHITE_BLOCK, () -> Items.COAL_BLOCK, 0.9f, 1800, configCondition("registerCoalBlockForSmelting"));

        // misc

        ShapedRecipeBuilder.shaped(Content.Items.WRENCH.get())
                .define('I', Tags.Items.INGOTS_IRON)
                .define('W', ItemTags.WOOL)
                .define('D', Tags.Items.DYES_GREEN)
                .pattern("DI ")
                .pattern("WII")
                .pattern("IW ")
                .group(GROUP_GENERAL)
                .unlockedBy("has_item", has(Content.Items.WRENCH.get()))
                .save(c, ExtremeReactors.newID("misc/wrench"));

        this.book(c, "erguide", PatchouliCompat.HANDBOOK_ID, Items.BOOK, ContentTags.Items.INGOTS_YELLORIUM);
        this.book(c, "erguide_alt", PatchouliCompat.HANDBOOK_ID, Items.BOOK, TAG_INGOTS_URANIUM);
    }

    //endregion
    //region internals

    private void book(final Consumer<FinishedRecipe> c, final String name, final ResourceLocation patchouliBookId,
                      final ItemLike ingredientBook,
                      final Tag<Item> ingredientItem) {

        Mods.PATCHOULI.map(() -> PatchouliAPI.get().getBookStack(patchouliBookId)).ifPresent(book -> {

                    ConditionalRecipe.builder()
                            .addCondition(modLoaded(Mods.PATCHOULI.id()))
                            .addRecipe(fr -> ShapedRecipeBuilder.shaped(book.getItem())
                                    .define('I', ingredientItem)
                                    .define('B', ingredientBook)
                                    .pattern("I")
                                    .pattern("B")
                                    .group(GROUP_GENERAL)
                                    .unlockedBy("has_item", has(ingredientItem))
                                    .save(NbtResultFinishedRecipeAdapter.from(fr, RecipeSerializer.SHAPED_RECIPE,
                                            nbt -> nbt.putString("patchouli:book", patchouliBookId.toString()))))
                            .build(c, ExtremeReactors.newID("misc/book/" + name));
                });
    }

    //endregion
}
