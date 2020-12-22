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

import com.google.gson.JsonObject;
import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.zerocore.lib.compat.Mods;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import vazkii.patchouli.api.PatchouliAPI;

import javax.annotation.Nullable;
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

        this.book(c, "erguide", PatchouliCompat.HANDBOOK_ID, Items.BOOK, ContentTags.Items.INGOTS_YELLORIUM);
    }

    //endregion
    //region internals

    private void book(final Consumer<IFinishedRecipe> c, final String name, final ResourceLocation patchouliBookId,
                      final IItemProvider ingredientBook,
                      final ITag<Item> ingredientItem) {

        Mods.PATCHOULI.map(() -> PatchouliAPI.get().getBookStack(patchouliBookId)).ifPresent(book -> {

                    ConditionalRecipe.builder()
                            .addCondition(modLoaded(Mods.PATCHOULI.id()))
                            .addRecipe(fr -> ShapedRecipeBuilder.shapedRecipe(book.getItem())
                                    .key('I', ingredientItem)
                                    .key('B', ingredientBook)
                                    .patternLine("I")
                                    .patternLine("B")
                                    .setGroup(GROUP_GENERAL)
                                    .addCriterion("has_item", hasItem(ingredientItem))
                                    .build(NbtResultRecipeAdapter.from(fr, IRecipeSerializer.CRAFTING_SHAPED,
                                            nbt -> nbt.putString("patchouli:book", patchouliBookId.toString()))))
                            .build(c, ExtremeReactors.newID("misc/book/" + name));
                });
    }

    //TODO placed here temporarily, than move to ZC
    private static abstract class RecipeAdapter
            implements IFinishedRecipe {

        //region IFinishedRecipe

        @Override
        public void serialize(final JsonObject json) {
            this._originalRecipe.serialize(json);
        }

        /**
         * Gets the ID for the recipe.
         */
        @Override
        public ResourceLocation getID() {
            return this._originalRecipe.getID();
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return this._serializer;
        }

        /**
         * Gets the JSON for the advancement that unlocks this recipe. Null if there is no advancement.
         */
        @Nullable
        @Override
        public JsonObject getAdvancementJson() {
            return this._originalRecipe.getAdvancementJson();
        }

        /**
         * Gets the ID for the advancement associated with this recipe. Should not be null if {@link #getAdvancementJson} is
         * non-null.
         */
        @Nullable
        @Override
        public ResourceLocation getAdvancementID() {
            return this._originalRecipe.getAdvancementID();
        }

        //endregion
        //region internals

        protected RecipeAdapter(final IFinishedRecipe originalRecipe, final IRecipeSerializer<?> serializer) {

            this._originalRecipe = originalRecipe;
            this._serializer = serializer;
        }

        protected final IFinishedRecipe _originalRecipe;
        protected final IRecipeSerializer<?> _serializer;

        //endregion
    }

    //TODO placed here temporarily, than move to ZC
    private static final class NbtResultRecipeAdapter
        extends RecipeAdapter {

        public static Consumer<IFinishedRecipe> from(final Consumer<IFinishedRecipe> originalRecipe, final IRecipeSerializer<?> serializer,
                                                     final CompoundNBT data)  {
            return fr -> originalRecipe.accept(new NbtResultRecipeAdapter(fr, serializer, data));
        }

        public static Consumer<IFinishedRecipe> from(final Consumer<IFinishedRecipe> originalRecipe, final IRecipeSerializer<?> serializer,
                                                     final Consumer<CompoundNBT> data) {

            final CompoundNBT nbt = new CompoundNBT();

            data.accept(nbt);
            return from(originalRecipe, serializer, nbt);
        }

        //region IFinishedRecipe

        @Override
        public void serialize(final JsonObject json) {

            super.serialize(json);

            if (null != this._data) {
                JSONUtils.getJsonObject(json, "result").addProperty("nbt", this._data.toString());
            }
        }

        //endregion
        //region internals

        private NbtResultRecipeAdapter(final IFinishedRecipe originalRecipe, final IRecipeSerializer<?> serializer,
                                       final CompoundNBT resultData) {

            super(originalRecipe, serializer);
            this._data = resultData;
        }

        private final CompoundNBT _data;

        //endregion
    }

    //endregion
}
