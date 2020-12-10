/*
 *
 * RecipeGenerator.java
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

import com.google.common.collect.ImmutableSet;
import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractRecipeGenerator
    extends RecipeProvider {

    public AbstractRecipeGenerator(final DataGenerator generatorIn) {
        super(generatorIn);
    }

    //region internals

    protected void blastingAndSmelting(final Consumer<IFinishedRecipe> consumer, final String name,
                                       final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source) {
        this.blastingAndSmelting(consumer, name, result, source, 1f, 200);
    }

    protected void blastingAndSmelting(final Consumer<IFinishedRecipe> consumer, final String name,
                                       final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source,
                                       final float xp, final int smeltingTime) {

        this.blasting(consumer, name, result, source, xp, smeltingTime / 2);
        this.smelting(consumer, name, result, source, xp, smeltingTime);
    }

    protected void blasting(final Consumer<IFinishedRecipe> consumer, final String name,
                            final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source) {
        this.blasting(consumer, name, result, source, 1f, 100);
    }

    protected void blasting(final Consumer<IFinishedRecipe> consumer, final String name,
                            final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source,
                            final float xp, final int time) {
        CookingRecipeBuilder.blastingRecipe(Ingredient.fromItems(source.get()), result.get(), xp, time)
                .addCriterion("has_item", hasItem(source.get()))
                .build(consumer, ExtremeReactors.newID("blasting/" + name));
    }

    protected void smelting(final Consumer<IFinishedRecipe> consumer, final String name,
                            final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source) {
        this.smelting(consumer, name, result, source, 1f, 200);
    }

    protected void smelting(final Consumer<IFinishedRecipe> consumer, final String name,
                            final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source,
                            final float xp, final int time) {
        CookingRecipeBuilder.smeltingRecipe(Ingredient.fromItems(source.get()), result.get(), xp, time)
                .addCriterion("has_item", hasItem(source.get()))
                .build(consumer, ExtremeReactors.newID("smelting/" + name));
    }

    protected void storageBlock3x3(final Consumer<IFinishedRecipe> consumer, final String name,
                                   final Supplier<? extends IItemProvider> component, final Supplier<? extends IItemProvider> storage) {

        // 3x3 components -> 1 storage
        ShapelessRecipeBuilder.shapelessRecipe(storage.get())
                .addIngredient(component.get(), 9)
                .setGroup(GROUP_GENERAL)
                .addCriterion(name + "_has_storage", hasItem(storage.get()))
                .build(consumer, ExtremeReactors.newID(name + "_component_to_storage"));

        // 1 storage -> 9 components
        ShapelessRecipeBuilder.shapelessRecipe(component.get(), 9)
                .addIngredient(storage.get())
                .setGroup(GROUP_GENERAL)
                .addCriterion("has_item", hasItem(storage.get()))
                .build(consumer, ExtremeReactors.newID("crafting/" + name + "_storage_to_component"));
    }

    protected static void recipeWithAlternativeTag(final Consumer<IFinishedRecipe> c,
                                                   final ResourceLocation name, @Nullable final ResourceLocation alternativeName,
                                                   final ITag.INamedTag<Item> tag, @Nullable final ITag.INamedTag<Item> alternativeTag,
                                                   final Function<ITag.INamedTag<Item>, ShapedRecipeBuilder> recipe) {

        if (null == alternativeTag || null == alternativeName) {

            recipe.apply(tag).build(c, name);

        } else {

            // normal metal recipe (if metal exists)
            ConditionalRecipe.builder()
                    .addCondition(not(new TagEmptyCondition(tag.getName())))
                    .addRecipe(recipe.apply(tag)::build)
                    .build(c, name);

            // alternative metal recipe (if metal DO NOT exists)
            ConditionalRecipe.builder()
                    .addCondition(new TagEmptyCondition(tag.getName()))
                    .addRecipe(recipe.apply(alternativeTag)::build)
                    .build(c, alternativeName);
        }
    }

    protected static ICondition not(final ICondition condition) {
        return new NotCondition(condition);
    }

    protected static ICondition modLoaded(final String modId) {
        return new ModLoadedCondition(modId);
    }

    protected static final String GROUP_GENERAL = ExtremeReactors.MOD_ID + ":general";
    protected static final String GROUP_REACTOR = ExtremeReactors.MOD_ID + ":reactor";
    protected static final String GROUP_TURBINE = ExtremeReactors.MOD_ID + ":turbine";

    protected static final Tags.IOptionalNamedTag<Item> TAG_INGOTS_STEEL = ItemTags.createOptional(new ResourceLocation("forge", "ingots/steel"));
    protected static final Tags.IOptionalNamedTag<Item> TAG_INGOTS_URANIUM = ItemTags.createOptional(new ResourceLocation("forge", "ingots/uranium"));

    protected static final Set<ITag.INamedTag<Item>> TAGS_YELLORIUM_INGOTS = ImmutableSet.of(ContentTags.Items.INGOTS_YELLORIUM, TAG_INGOTS_URANIUM);

    //endregion
}
