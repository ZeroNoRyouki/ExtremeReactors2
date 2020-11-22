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
import com.google.common.collect.Sets;
import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content.Items;
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
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
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class RecipeGenerator
    extends RecipeProvider {

    public RecipeGenerator(final DataGenerator generatorIn) {
        super(generatorIn);
    }

    //region RecipeProvider

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return ExtremeReactors.MOD_NAME + " Recipes";
    }

    /**
     * Registers all recipes to the given consumer.
     */
    @Override
    protected void registerRecipes(final Consumer<IFinishedRecipe> c) {

        // ingots / (storage) blocks
        this.storageBlock3x3(c, "yellorium", Items.YELLORIUM_INGOT, Items.YELLORIUM_BLOCK);
        this.storageBlock3x3(c, "cyanite", Items.CYANITE_INGOT, Items.CYANITE_BLOCK);
        this.storageBlock3x3(c, "graphite", Items.GRAPHITE_INGOT, Items.GRAPHITE_BLOCK);

        // smelting

        this.blastingAndSmelting(c, "yellorium_from_ore", Items.YELLORIUM_INGOT, Items.YELLORITE_ORE_BLOCK);
        this.blastingAndSmelting(c, "yellorium_from_dust", Items.YELLORIUM_INGOT, Items.YELLORIUM_DUST);
        this.blastingAndSmelting(c, "cyanite_from_dust", Items.CYANITE_INGOT, Items.CYANITE_DUST);
        this.blastingAndSmelting(c, "graphite_from_dust", Items.GRAPHITE_INGOT, Items.GRAPHITE_DUST);
        this.blastingAndSmelting(c, "graphite_from_coal", Items.GRAPHITE_INGOT, () -> net.minecraft.item.Items.COAL);
        this.blastingAndSmelting(c, "graphite_from_charcoal", Items.GRAPHITE_INGOT, () -> net.minecraft.item.Items.CHARCOAL);
        this.blastingAndSmelting(c, "graphite_from_coalblock", Items.GRAPHITE_BLOCK, () -> net.minecraft.item.Items.COAL_BLOCK, 0.9f, 1800);

        // misc

        ShapedRecipeBuilder.shapedRecipe(Items.WRENCH.get())
                .key('I', Tags.Items.INGOTS_IRON)
                .key('W', ItemTags.WOOL)
                .key('D', Tags.Items.DYES_GREEN)
                .patternLine("DI ")
                .patternLine("WII")
                .patternLine("IW ")
                .setGroup(GROUP_GENERAL)
                .addCriterion("has_item", hasItem(Items.WRENCH.get()))
                .build(c, ExtremeReactors.newID("misc/wrench"));

        // reactor

        this.reactor(c);

        // turbine
    }

    //endregion
    //region internals

    //region reactor

    private void reactor(final Consumer<IFinishedRecipe> c) {

        ITag.INamedTag<Item> core, metal, alternativeMetal;
        ReactorVariant variant;
        Supplier<? extends IItemProvider> casing;

        // Basic parts

        variant = ReactorVariant.Basic;
        casing = Items.REACTOR_CASING_BASIC;
        core = Tags.Items.SAND;
        metal = Tags.Items.INGOTS_IRON;
        alternativeMetal = null;

        this.reactorCasing(c, variant, Items.REACTOR_CASING_BASIC, core, metal, alternativeMetal);
        this.reactorGlass(c, variant, Items.REACTOR_GLASS_BASIC, casing, Tags.Items.GLASS);
        this.reactorController(c, variant, Items.REACTOR_CONTROLLER_BASIC, casing, Tags.Items.GEMS_DIAMOND);
        this.reactorFuelRod(c, variant, Items.REACTOR_FUELROD_BASIC, metal, alternativeMetal, Tags.Items.GLASS);
        this.reactorControlRod(c, variant, Items.REACTOR_CONTROLROD_BASIC, casing, metal, alternativeMetal);
        this.reactorSolidAccessPort(c, variant, Items.REACTOR_SOLID_ACCESSPORT_BASIC, casing, metal, alternativeMetal);
        this.reactorPowerTap(c, variant, "fe", Items.REACTOR_POWERTAP_FE_PASSIVE_BASIC, Items.REACTOR_POWERTAP_FE_ACTIVE_BASIC,
                casing, () -> net.minecraft.item.Items.REDSTONE, () -> net.minecraft.item.Items.REDSTONE_BLOCK);
        this.reactorRedstonePort(c, variant, Items.REACTOR_REDSTONEPORT_BASIC, casing, metal, alternativeMetal, Tags.Items.INGOTS_GOLD);

        // Reinforced parts

        variant = ReactorVariant.Reinforced;
        casing = Items.REACTOR_CASING_REINFORCED;
        core = Tags.Items.STORAGE_BLOCKS_IRON;
        metal = TAG_INGOTS_STEEL;
        alternativeMetal = Tags.Items.STORAGE_BLOCKS_IRON;

        this.reactorCasing(c, variant, Items.REACTOR_CASING_REINFORCED, core, metal, alternativeMetal);
        this.reactorGlass(c, variant, Items.REACTOR_GLASS_REINFORCED, casing, Tags.Items.GLASS);
        this.reactorController(c, variant, Items.REACTOR_CONTROLLER_REINFORCED, casing, Tags.Items.STORAGE_BLOCKS_DIAMOND);
        this.reactorFuelRod(c, variant, Items.REACTOR_FUELROD_REINFORCED, metal, alternativeMetal, Tags.Items.GLASS);
        this.reactorControlRod(c, variant, Items.REACTOR_CONTROLROD_REINFORCED, casing, metal, alternativeMetal);
        this.reactorSolidAccessPort(c, variant, Items.REACTOR_SOLID_ACCESSPORT_REINFORCED, casing, metal, alternativeMetal);
        this.reactorPowerTap(c, variant, "fe", Items.REACTOR_POWERTAP_FE_PASSIVE_REINFORCED, Items.REACTOR_POWERTAP_FE_ACTIVE_REINFORCED,
                casing, () -> net.minecraft.item.Items.REDSTONE, () -> net.minecraft.item.Items.REDSTONE_BLOCK);
        this.reactorRedstonePort(c, variant, Items.REACTOR_REDSTONEPORT_REINFORCED, casing, metal, alternativeMetal, Tags.Items.STORAGE_BLOCKS_GOLD);
        this.reactorComputerPort(c, variant, Items.REACTOR_COMPUTERPORT_REINFORCED, casing, metal, alternativeMetal);

        // computer port

    }

    private static ICondition not(final ICondition condition) {
        return new NotCondition(condition);
    }

    private static void recipeWithAlternativeTag(final Consumer<IFinishedRecipe> c,
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

    private void reactorCasing(final Consumer<IFinishedRecipe> c, final ReactorVariant variant,
                               final Supplier<? extends IItemProvider> result,
                               final ITag<Item> core, final ITag.INamedTag<Item> metal,
                               @Nullable final ITag.INamedTag<Item> alternativeMetal) {

        recipeWithAlternativeTag(c, reactorRecipeName(variant, "casing"), reactorRecipeName(variant, "casing_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shapedRecipe(result.get())
                                .key('I', metalTag)
                                .key('C', core)
                                .key('G', ContentTags.Items.INGOTS_GRAPHITE)
                                .patternLine("IGI")
                                .patternLine("GCG")
                                .patternLine("IGI")
                                .setGroup(GROUP_REACTOR)
                                .addCriterion("has_item", hasItem(ContentTags.Items.INGOTS_GRAPHITE)));
    }

    private void reactorGlass(final Consumer<IFinishedRecipe> c, final ReactorVariant variant,
                              final Supplier<? extends IItemProvider> result,
                              final Supplier<? extends IItemProvider> casing, final ITag<Item> glass) {
        ShapedRecipeBuilder.shapedRecipe(result.get())
                .key('C', casing.get())
                .key('G', glass)
                .patternLine("GCG")
                .setGroup(GROUP_REACTOR)
                .addCriterion("has_item", hasItem(casing.get()))
                .build(c, reactorRecipeName(variant, "glass"));
    }

    private void reactorController(final Consumer<IFinishedRecipe> c, final ReactorVariant variant,
                                   final Supplier<? extends IItemProvider> result,
                                   final Supplier<? extends IItemProvider> casing, final ITag<Item> diamond) {

        TAGS_YELLORIUM_INGOTS.forEach(yellorium -> ShapedRecipeBuilder.shapedRecipe(result.get())
                    .key('C', casing.get())
                    .key('Y', yellorium)
                    .key('R', Tags.Items.DUSTS_REDSTONE)
                    .key('D', diamond)
                    .key('X', net.minecraft.item.Items.COMPARATOR)
                    .patternLine("CXC")
                    .patternLine("YDY")
                    .patternLine("CRC")
                    .setGroup(GROUP_REACTOR)
                    .addCriterion("has_item", hasItem(casing.get()))
                    .addCriterion("has_item2", hasItem(yellorium))
                    .build(c, reactorRecipeName(variant, "controller", yellorium))
        );
    }

    private void reactorFuelRod(final Consumer<IFinishedRecipe> c, final ReactorVariant variant,
                                final Supplier<? extends IItemProvider> result,
                                final ITag.INamedTag<Item> metal, @Nullable final ITag.INamedTag<Item> alternativeMetal,
                                final ITag<Item> glass) {

        TAGS_YELLORIUM_INGOTS.forEach(yellorium ->
                recipeWithAlternativeTag(c, reactorRecipeName(variant, "fuelrod", yellorium),
                        reactorRecipeName(variant, "fuelrod_alt", yellorium),
                        metal, alternativeMetal, metalTag ->
                                ShapedRecipeBuilder.shapedRecipe(result.get())
                                        .key('M', metalTag)
                                        .key('Y', yellorium)
                                        .key('G', ContentTags.Items.INGOTS_GRAPHITE)
                                        .key('L', glass)
                                        .patternLine("MGM")
                                        .patternLine("LYL")
                                        .patternLine("MGM")
                                        .setGroup(GROUP_REACTOR)
                                        .addCriterion("has_item", hasItem(yellorium))));
    }

    private void reactorControlRod(final Consumer<IFinishedRecipe> c, final ReactorVariant variant,
                                   final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> casing,
                                   final ITag.INamedTag<Item> metal, @Nullable final ITag.INamedTag<Item> alternativeMetal) {

        recipeWithAlternativeTag(c, reactorRecipeName(variant, "controlrod"), reactorRecipeName(variant, "controlrod_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shapedRecipe(result.get())
                                .key('C', casing.get())
                                .key('M', metalTag)
                                .key('R', Tags.Items.DUSTS_REDSTONE)
                                .key('G', ContentTags.Items.INGOTS_GRAPHITE)
                                .key('X', net.minecraft.item.Items.PISTON)
                                .patternLine("CRC")
                                .patternLine("MXM")
                                .patternLine("CGC")
                                .setGroup(GROUP_REACTOR)
                                .addCriterion("has_item", hasItem(casing.get())));
    }

    private void reactorPowerTap(final Consumer<IFinishedRecipe> c, final ReactorVariant variant, final String name,
                                 final Supplier<? extends IItemProvider> passiveResult, final Supplier<? extends IItemProvider> activeResult,
                                 final Supplier<? extends IItemProvider> casing, final Supplier<? extends IItemProvider> energyBig,
                                 final Supplier<? extends IItemProvider> energySmall) {

        ShapedRecipeBuilder.shapedRecipe(passiveResult.get())
                .key('C', casing.get())
                .key('B', energyBig.get())
                .key('S', energySmall.get())
                .patternLine("CSC")
                .patternLine("SBS")
                .patternLine("CSC")
                .setGroup(GROUP_REACTOR)
                .addCriterion("has_item", hasItem(casing.get()))
                .addCriterion("has_item2", hasItem(energySmall.get()))
                .build(c, reactorRecipeName(variant, "passivetap_" + name));

        ShapedRecipeBuilder.shapedRecipe(activeResult.get())
                .key('C', casing.get())
                .key('B', energyBig.get())
                .key('S', energySmall.get())
                .patternLine("CBC")
                .patternLine("BSB")
                .patternLine("CBC")
                .setGroup(GROUP_REACTOR)
                .addCriterion("has_item", hasItem(casing.get()))
                .addCriterion("has_item2", hasItem(energyBig.get()))
                .build(c, reactorRecipeName(variant, "activetap_" + name));
    }

    private void reactorSolidAccessPort(final Consumer<IFinishedRecipe> c, final ReactorVariant variant,
                                        final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> casing,
                                        final ITag.INamedTag<Item> metal, @Nullable final ITag.INamedTag<Item> alternativeMetal) {
        recipeWithAlternativeTag(c, reactorRecipeName(variant, "solidaccessport"), reactorRecipeName(variant, "solidaccessport_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shapedRecipe(result.get())
                                .key('C', casing.get())
                                .key('M', metalTag)
                                .key('H', net.minecraft.item.Items.HOPPER)
                                .key('W', Tags.Items.CHESTS_WOODEN)
                                .key('X', net.minecraft.item.Items.PISTON)
                                .patternLine("CHC")
                                .patternLine("MWM")
                                .patternLine("CXC")
                                .setGroup(GROUP_REACTOR)
                                .addCriterion("has_item", hasItem(casing.get())));
    }

    private void reactorRedstonePort(final Consumer<IFinishedRecipe> c, final ReactorVariant variant,
                                     final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> casing,
                                     final ITag.INamedTag<Item> metal, @Nullable final ITag.INamedTag<Item> alternativeMetal,
                                     final ITag<Item> gold) {
        recipeWithAlternativeTag(c, reactorRecipeName(variant, "redstoneport"), reactorRecipeName(variant, "redstoneport_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shapedRecipe(result.get())
                                .key('C', casing.get())
                                .key('M', metalTag)
                                .key('G', gold)
                                .key('Z', net.minecraft.item.Items.COMPARATOR)
                                .key('X', net.minecraft.item.Items.REPEATER)
                                .patternLine("CZC")
                                .patternLine("MGM")
                                .patternLine("CXC")
                                .setGroup(GROUP_REACTOR)
                                .addCriterion("has_item", hasItem(casing.get())));
    }

    private void reactorComputerPort(final Consumer<IFinishedRecipe> c, final ReactorVariant variant,
                                     final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> casing,
                                     final ITag.INamedTag<Item> metal, @Nullable final ITag.INamedTag<Item> alternativeMetal) {
        recipeWithAlternativeTag(c, reactorRecipeName(variant, "computerport"), reactorRecipeName(variant, "computerport_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shapedRecipe(result.get())
                                .key('C', casing.get())
                                .key('M', metalTag)
                                .key('G', Tags.Items.STORAGE_BLOCKS_GOLD)
                                .key('Z', Tags.Items.GEMS_LAPIS)
                                .key('X', Tags.Items.DUSTS_GLOWSTONE)
                                .patternLine("CZC")
                                .patternLine("MGM")
                                .patternLine("CXC")
                                .setGroup(GROUP_REACTOR)
                                .addCriterion("has_item", hasItem(casing.get())));
    }

    private static ResourceLocation reactorRecipeName(final ReactorVariant variant, final String name) {
        return ExtremeReactors.newID("reactor/" + variant.getName() + "/" + name);
    }

    private static ResourceLocation reactorRecipeName(final ReactorVariant variant, final String name, final ITag.INamedTag<Item> tag) {
        return ExtremeReactors.newID("reactor/" + variant.getName() + "/" + name + "_" + tag.getName().getPath().replace('/', '_'));
    }

    //endregion

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

    private static final String GROUP_GENERAL = ExtremeReactors.MOD_ID + ":general";
    private static final String GROUP_REACTOR = ExtremeReactors.MOD_ID + ":reactor";
    private static final String GROUP_TURBINE = ExtremeReactors.MOD_ID + ":turbine";

    private static final Tags.IOptionalNamedTag<Item> TAG_INGOTS_STEEL = ItemTags.createOptional(new ResourceLocation("forge", "ingots/steel"));
    private static final Tags.IOptionalNamedTag<Item> TAG_INGOTS_URANIUM = ItemTags.createOptional(new ResourceLocation("forge", "ingots/uranium"));

    private static final Set<ITag.INamedTag<Item>> TAGS_YELLORIUM_INGOTS = ImmutableSet.of(ContentTags.Items.INGOTS_YELLORIUM, TAG_INGOTS_URANIUM);

    //endregion
}
