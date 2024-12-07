/*
 * GenericRecipesDataProvider
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * Do not remove or edit this header
 *
 */

package it.zerono.mods.extremereactors.datagen.recipe;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.zerocore.lib.compat.patchouli.IPatchouliService;
import it.zerono.mods.zerocore.lib.datagen.provider.recipe.ModRecipeProviderRunner;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

import java.util.function.Supplier;

public class GenericRecipesDataProvider
        extends AbstractRecipesDataProvider {

    public GenericRecipesDataProvider(ModRecipeProviderRunner<GenericRecipesDataProvider> mainProvider,
                                      HolderLookup.Provider registryLookupProvider, RecipeOutput output) {
        super(mainProvider, registryLookupProvider, output);
    }

    @Override
    protected void buildRecipes() {

        // ingots / (storage) blocks

        this.reactantsStorage("yellorium", Content.Items.YELLORIUM_BLOCK, Content.Items.YELLORIUM_INGOT);
        this.reactantsStorage("cyanite", Content.Items.CYANITE_BLOCK, Content.Items.CYANITE_INGOT);
        this.reactantsStorage("graphite", Content.Items.GRAPHITE_BLOCK, Content.Items.GRAPHITE_INGOT);
        this.reactantsStorage("blutonium", Content.Items.BLUTONIUM_BLOCK, Content.Items.BLUTONIUM_INGOT);
        this.reactantsStorage("magentite", Content.Items.MAGENTITE_BLOCK, Content.Items.MAGENTITE_INGOT);
        this.storageBlock3x3(this.output, "raw_yellorium", this.group("raw_yellorium"),
                RecipeCategory.MISC, Content.Blocks.RAW_YELLORIUM_BLOCK, RecipeCategory.MISC, Content.Items.RAW_YELLORIUM);

        // coils

        this.coil("ludicrite_block", Content.Items.LUDICRITE_BLOCK, Content.Items.LUDICRITE_INGOT, Items.END_CRYSTAL);
        this.coil("ridiculite_block", Content.Items.RIDICULITE_BLOCK, Content.Items.RIDICULITE_INGOT, Items.NETHER_STAR);
        this.coil("inanite_block", Content.Items.INANITE_BLOCK, Content.Items.INANITE_INGOT, Items.NETHERITE_BLOCK);
        this.coil("insanite_block", Content.Items.INSANITE_BLOCK, Content.Items.INSANITE_INGOT, Content.Items.INANITE_BLOCK.get());

        // smelting

        this.blastingAndSmelting("yellorium_from_ore", "yellorium", Content.Items.YELLORIUM_INGOT, Content.Items.YELLORITE_ORE_BLOCK);
        this.blastingAndSmelting("yellorium_from_raw", "yellorium", Content.Items.YELLORIUM_INGOT, Content.Items.RAW_YELLORIUM);
        this.blastingAndSmelting("graphite_from_coal", "graphite", Content.Items.GRAPHITE_INGOT, () -> Items.COAL);
        this.blastingAndSmelting("graphite_from_charcoal", "graphite", Content.Items.GRAPHITE_INGOT, () -> Items.CHARCOAL);
        this.blastingAndSmelting("graphite_from_coalblock", "graphite", Content.Items.GRAPHITE_BLOCK, () -> Items.COAL_BLOCK, 0.9f, 1800);

        // misc

        this.shaped(RecipeCategory.TOOLS, Content.Items.WRENCH)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('W', ItemTags.WOOL)
                .define('D', Tags.Items.DYES_GREEN)
                .pattern("DI ")
                .pattern("WII")
                .pattern("IW ")
                .group(this.group("wrench"))
                .unlockedBy("has_item", has(Content.Items.WRENCH.get()))
                .save(this.output, recipeKeyFrom(this.miscRoot().buildWithSuffix("wrench")));

        this.book("erguide", PatchouliCompat.HANDBOOK_ID, Items.BOOK, ContentTags.Items.INGOTS_YELLORIUM);
        this.book("erguide_alt", PatchouliCompat.HANDBOOK_ID, Items.BOOK, ContentTags.Items.INGOTS_URANIUM);
    }

    //region internals

    private void reactantsStorage(String name, Supplier<? extends ItemLike> storage,
                                  Supplier<? extends ItemLike> component) {
        this.storageBlock3x3(this.output, name, this.group("reactants"),
                RecipeCategory.MISC, storage, RecipeCategory.MISC, component);
    }

    private void coil(String name, Supplier<? extends ItemLike> coil,
                      Supplier<? extends ItemLike> ingot, ItemLike special) {
        this.shaped(RecipeCategory.BUILDING_BLOCKS, coil)
                .define('I', ingot.get())
                .define('S', special)
                .pattern("III")
                .pattern("ISI")
                .pattern("III")
                .group(this.group("coil"))
                .unlockedBy("has_item", has(ingot.get()))
                .save(this.output, recipeKeyFrom(this.turbineRoot().buildWithSuffix(name)));
    }

    private void blastingAndSmelting(String name, String group,
                                     Supplier<? extends ItemLike> result, Supplier<? extends ItemLike> ingredient,
                                     float experience, int cookingTime) {

        this.blasting(RecipeCategory.MISC, result, Ingredient.of(ingredient.get()), experience, cookingTime)
                .group(this.group(group))
                .unlockedBy("has_item", has(ingredient.get()))
                .save(this.output, recipeKeyFrom(this.blastingRoot().buildWithSuffix(name)));

        this.smelting(RecipeCategory.MISC, result, Ingredient.of(ingredient.get()), experience, cookingTime)
                .group(this.group(group))
                .unlockedBy("has_item", has(ingredient.get()))
                .save(this.output, recipeKeyFrom(this.smeltingRoot().buildWithSuffix(name)));
    }

    private void blastingAndSmelting(String name, String group,
                                     Supplier<? extends ItemLike> result, Supplier<? extends ItemLike> ingredient) {
        this.blastingAndSmelting(name, group, result, ingredient, 1.0f, 100);
    }

    private void book(String name, ResourceLocation patchouliBookId,
                      ItemLike book, TagKey<Item> item) {

        IPatchouliService.SERVICE.get().consumeBookStack(patchouliBookId, stack -> {

            final var conditional = this.output.withConditions(modLoaded(IPatchouliService.SERVICE.getId()));

            this.shaped(RecipeCategory.MISC, stack)
                    .define('I', item)
                    .define('B', book)
                    .pattern("I")
                    .pattern("B")
                    .unlockedBy("has_item", has(item))
                    .save(conditional, recipeKeyFrom(ExtremeReactors.ROOT_LOCATION.appendPath("misc", "book").buildWithSuffix(name)));
        });
    }

    //endregion
}
