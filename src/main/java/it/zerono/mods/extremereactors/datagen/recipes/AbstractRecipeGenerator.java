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
import it.zerono.mods.extremereactors.config.conditions.ConfigCondition;
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockGeneratorVariant;
import it.zerono.mods.zerocore.lib.datagen.provider.recipe.BaseRecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ICondition;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractRecipeGenerator
    extends BaseRecipeProvider {

    protected AbstractRecipeGenerator(final DataGenerator generator) {
        super(generator);
    }

    //region internals
    protected void blastingAndSmelting(final Consumer<IFinishedRecipe> consumer, final String name,
                                       final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source,
                                       final float xp, final int smeltingTime) {

        this.blasting(consumer, name, ExtremeReactors::newID, result, source, xp, smeltingTime / 2);
        this.smelting(consumer, name, ExtremeReactors::newID, result, source, xp, smeltingTime);
    }

    protected void blastingAndSmelting(final Consumer<IFinishedRecipe> consumer, final String name,
                                       final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source,
                                       final float xp, final int smeltingTime, final ICondition... conditions) {

        this.blasting(consumer, name, ExtremeReactors::newID, result, source, xp, smeltingTime / 2, conditions);
        this.smelting(consumer, name, ExtremeReactors::newID, result, source, xp, smeltingTime, conditions);
    }

    protected void blastingAndSmelting(final Consumer<IFinishedRecipe> consumer, final String name,
                                       final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source) {

        this.blasting(consumer, name, ExtremeReactors::newID, result, source);
        this.smelting(consumer, name, ExtremeReactors::newID, result, source);
    }

    protected void blastingAndSmelting(final Consumer<IFinishedRecipe> consumer, final String name,
                                       final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source,
                                       final ICondition... conditions) {

        this.blasting(consumer, name, ExtremeReactors::newID, result, source, conditions);
        this.smelting(consumer, name, ExtremeReactors::newID, result, source, conditions);
    }

//    protected void blastingAndSmelting(final Consumer<IFinishedRecipe> consumer, final String name,
//                                       final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source) {
//        this.blastingAndSmelting(consumer, name, result, source, 1f, 200);
//    }
//
//    protected void blastingAndSmelting(final Consumer<IFinishedRecipe> consumer, final String name,
//                                       final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source,
//                                       final float xp, final int smeltingTime) {
//
//        this.blasting(consumer, name, result, source, xp, smeltingTime / 2);
//        this.smelting(consumer, name, result, source, xp, smeltingTime);
//    }
//
//    protected void blasting(final Consumer<IFinishedRecipe> consumer, final String name,
//                            final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source) {
//        this.blasting(consumer, name, result, source, 1f, 100);
//    }
//
//    protected void blasting(final Consumer<IFinishedRecipe> consumer, final String name,
//                            final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source,
//                            final float xp, final int time) {
//        CookingRecipeBuilder.blasting(Ingredient.of(source.get()), result.get(), xp, time)
//                .unlockedBy("has_item", has(source.get()))
//                .save(consumer, ExtremeReactors.newID("blasting/" + name));
//    }
//
//    protected void blasting(final Consumer<IFinishedRecipe> consumer, final String name,
//                            final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source,
//                            final float xp, final int time, final ICondition... conditions) {
//        conditionalBuilder(conditions)
//                .addRecipe(fr -> CookingRecipeBuilder.blasting(Ingredient.of(source.get()), result.get(), xp, time)
//                        .unlockedBy("has_item", has(source.get()))
//                        .save(consumer))
//                .build(consumer, ExtremeReactors.newID("blasting/" + name));
//    }
//
//    protected void smelting(final Consumer<IFinishedRecipe> consumer, final String name,
//                            final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source) {
//        this.smelting(consumer, name, result, source, 1f, 200);
//    }
//
//    protected void smelting(final Consumer<IFinishedRecipe> consumer, final String name,
//                            final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source,
//                            final float xp, final int time) {
//        CookingRecipeBuilder.smelting(Ingredient.of(source.get()), result.get(), xp, time)
//                .unlockedBy("has_item", has(source.get()))
//                .save(consumer, ExtremeReactors.newID("smelting/" + name));
//    }
//
//    protected void smelting(final Consumer<IFinishedRecipe> consumer, final String name,
//                            final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> source,
//                            final float xp, final int time, final ICondition... conditions) {
//        conditionalBuilder(conditions)
//                .addRecipe(fr -> CookingRecipeBuilder.smelting(Ingredient.of(source.get()), result.get(), xp, time)
//                        .unlockedBy("has_item", has(source.get()))
//                        .save(consumer))
//                .build(consumer, ExtremeReactors.newID("smelting/" + name));
//    }
//
//    protected void storageBlock3x3(final Consumer<IFinishedRecipe> consumer, final String name,
//                                   final Supplier<? extends IItemProvider> component, final Supplier<? extends IItemProvider> storage) {
//
//        // 3x3 components -> 1 storage
//        ShapelessRecipeBuilder.shapeless(storage.get())
//                .requires(component.get(), 9)
//                .group(GROUP_GENERAL)
//                .unlockedBy(name + "_has_storage", has(storage.get()))
//                .save(consumer, ExtremeReactors.newID(name + "_component_to_storage"));
//
//        // 1 storage -> 9 components
//        ShapelessRecipeBuilder.shapeless(component.get(), 9)
//                .requires(storage.get())
//                .group(GROUP_GENERAL)
//                .unlockedBy("has_item", has(storage.get()))
//                .save(consumer, ExtremeReactors.newID("crafting/" + name + "_storage_to_component"));
//    }
    protected void storageBlock3x3(final Consumer<IFinishedRecipe> consumer, final String name,
                                   final Supplier<? extends IItemProvider> component,
                                   final Supplier<? extends IItemProvider> storage) {
        this.storageBlock3x3(consumer, name, ExtremeReactors::newID, GROUP_GENERAL, component, storage);
    }

//    protected static void recipeWithAlternativeTag(final Consumer<IFinishedRecipe> c,
//                                                   final ResourceLocation name, @Nullable final ResourceLocation alternativeName,
//                                                   final ITag.INamedTag<Item> tag, @Nullable final ITag.INamedTag<Item> alternativeTag,
//                                                   final Function<ITag.INamedTag<Item>, ShapedRecipeBuilder> recipe) {
//
//        if (null == alternativeTag || null == alternativeName) {
//
//            recipe.apply(tag).save(c, name);
//
//        } else {
//
//            // normal metal recipe (if metal exists)
//            conditionalBuilder(not(new TagEmptyCondition(tag.getName())))
//                    .addRecipe(recipe.apply(tag)::save)
//                    .build(c, name);
//
//            // alternative metal recipe (if metal DO NOT exists)
//            conditionalBuilder(new TagEmptyCondition(tag.getName()))
//                    .addRecipe(recipe.apply(alternativeTag)::save)
//                    .build(c, alternativeName);
//        }
//    }

    protected <V extends IMultiblockGeneratorVariant>
    void generatorChargingPort(final Consumer<IFinishedRecipe> c, final V variant,
                               final String name, final String group,
                               final BiFunction<V, String, ResourceLocation> nameProvider,
                               final Supplier<? extends IItemProvider> result,
                               final Supplier<? extends IItemProvider> powerTap,
                               final IItemProvider item1,
                               final IItemProvider item2) {

        ShapedRecipeBuilder.shaped(result.get())
                .define('T', powerTap.get())
                .define('G', Tags.Items.GLASS)
                .define('1', item1)
                .define('2', item2)
                .pattern("212")
                .pattern("GTG")
                .pattern("212")
                .group(group)
                .unlockedBy("has_item", has(powerTap.get()))
                .save(c, nameProvider.apply(variant, name));
    }

//    protected static ICondition not(final ICondition condition) {
//        return new NotCondition(condition);
//    }
//
//    protected static ICondition modLoaded(final String modId) {
//        return new ModLoadedCondition(modId);
//    }
//
//    protected static ConditionalRecipe.Builder conditionalBuilder(final ICondition... conditions) {
//
//        if (0 == conditions.length) {
//            throw new IllegalArgumentException("No conditions were provided");
//        }
//
//        final ConditionalRecipe.Builder builder = ConditionalRecipe.builder();
//
//        for (final ICondition condition : conditions) {
//            builder.addCondition(condition);
//        }
//
//        return builder;
//    }

    protected ICondition configCondition(final String configName) {
        return new ConfigCondition(configName);
    }

    protected static final String GROUP_GENERAL = ExtremeReactors.MOD_ID + ":general";
    protected static final String GROUP_REACTOR = ExtremeReactors.MOD_ID + ":reactor";
    protected static final String GROUP_TURBINE = ExtremeReactors.MOD_ID + ":turbine";

    protected static final Tags.IOptionalNamedTag<Item> TAG_INGOTS_STEEL = ItemTags.createOptional(new ResourceLocation("forge", "ingots/steel"));
    protected static final Tags.IOptionalNamedTag<Item> TAG_INGOTS_URANIUM = ItemTags.createOptional(new ResourceLocation("forge", "ingots/uranium"));

    protected static final Set<ITag.INamedTag<Item>> TAGS_YELLORIUM_INGOTS = ImmutableSet.of(ContentTags.Items.INGOTS_YELLORIUM, TAG_INGOTS_URANIUM);

    //endregion
}
