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
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.function.Consumer;
import java.util.function.Supplier;

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
    protected void buildShapelessRecipes(final Consumer<IFinishedRecipe> c) {

        // ingots / (storage) blocks
        this.storageBlock3x3(c, "yellorium", Content.Items.YELLORIUM_INGOT, Content.Items.YELLORIUM_BLOCK);
        this.storageBlock3x3(c, "cyanite", Content.Items.CYANITE_INGOT, Content.Items.CYANITE_BLOCK);
        this.storageBlock3x3(c, "graphite", Content.Items.GRAPHITE_INGOT, Content.Items.GRAPHITE_BLOCK);
        this.storageBlock3x3(c, "blutonium", Content.Items.BLUTONIUM_INGOT, Content.Items.BLUTONIUM_BLOCK);
        this.storageBlock3x3(c, "magentite", Content.Items.MAGENTITE_INGOT, Content.Items.MAGENTITE_BLOCK);

        this.nugget(c, "yellorium", Content.Items.YELLORIUM_INGOT, Content.Items.YELLORIUM_NUGGET);
        this.nugget(c, "blutonium", Content.Items.BLUTONIUM_INGOT, Content.Items.BLUTONIUM_NUGGET);

        // nuggets

        this.coil(c, "ludicrite_block", Content.Items.LUDICRITE_BLOCK, Content.Items.LUDICRITE_INGOT, Items.END_CRYSTAL);
        this.coil(c, "ridiculite_block", Content.Items.RIDICULITE_BLOCK, Content.Items.RIDICULITE_INGOT, Items.NETHER_STAR);
        this.coil(c, "inanite_block", Content.Items.INANITE_BLOCK, Content.Items.INANITE_INGOT, Items.NETHERITE_BLOCK);

        // smelting

        this.blastingAndSmelting(c, "yellorium_from_ore", Content.Items.YELLORIUM_INGOT, Content.Items.YELLORITE_ORE_BLOCK);
        this.blastingAndSmelting(c, "yellorium_from_dust", Content.Items.YELLORIUM_INGOT, Content.Items.YELLORIUM_DUST);
        this.blastingAndSmelting(c, "cyanite_from_dust", Content.Items.CYANITE_INGOT, Content.Items.CYANITE_DUST);
        this.blastingAndSmelting(c, "graphite_from_dust", Content.Items.GRAPHITE_INGOT, Content.Items.GRAPHITE_DUST);
        this.blastingAndSmelting(c, "blutonium_from_dust", Content.Items.BLUTONIUM_INGOT, Content.Items.BLUTONIUM_DUST);
        this.blastingAndSmelting(c, "magentite_from_dust", Content.Items.MAGENTITE_INGOT, Content.Items.MAGENTITE_DUST);
        this.blastingAndSmelting(c, "graphite_from_coal", Content.Items.GRAPHITE_INGOT, () -> net.minecraft.item.Items.COAL, configCondition("registerCoalForSmelting"));
        this.blastingAndSmelting(c, "graphite_from_charcoal", Content.Items.GRAPHITE_INGOT, () -> net.minecraft.item.Items.CHARCOAL, configCondition("registerCharcoalForSmelting"));
        this.blastingAndSmelting(c, "graphite_from_coalblock", Content.Items.GRAPHITE_BLOCK, () -> net.minecraft.item.Items.COAL_BLOCK, 0.9f, 1800, configCondition("registerCoalBlockForSmelting"));

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

    private void coil(final Consumer<IFinishedRecipe> c, final String name, final Supplier<? extends IItemProvider> output,
                      final Supplier<? extends IItemProvider> ingot, final IItemProvider special) {
        ShapedRecipeBuilder.shaped(output.get())
                .define('I', ingot.get())
                .define('S', special)
                .pattern("III")
                .pattern("ISI")
                .pattern("III")
                .group(GROUP_GENERAL)
                .unlockedBy("has_item", has(ingot.get()))
                .save(c, ExtremeReactors.newID("turbine/" + name));
    }

    private void book(final Consumer<IFinishedRecipe> c, final String name, final ResourceLocation patchouliBookId,
                      final IItemProvider ingredientBook,
                      final ITag<Item> ingredientItem) {

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
                                    .save(NbtResultFinishedRecipeAdapter.from(fr, IRecipeSerializer.SHAPED_RECIPE,
                                            nbt -> nbt.putString("patchouli:book", patchouliBookId.toString()))))
                            .build(c, ExtremeReactors.newID("misc/book/" + name));
                });
    }

    //endregion
}
