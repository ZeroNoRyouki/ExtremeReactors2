package it.zerono.mods.extremereactors.datagen.recipe;

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.recipe.ReprocessorRecipe;
import it.zerono.mods.zerocore.lib.datagen.provider.recipe.ModRecipeProviderRunner;
import it.zerono.mods.zerocore.lib.recipe.ingredient.FluidRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.ingredient.ItemRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.ItemStackRecipeResult;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;

import java.util.function.Supplier;

public class ReprocessorRecipesDataProvider
        extends AbstractRecipesDataProvider {

    public ReprocessorRecipesDataProvider(ModRecipeProviderRunner<ReprocessorRecipesDataProvider> mainProvider,
                                          HolderLookup.Provider registryLookupProvider, RecipeOutput output) {
        super(mainProvider, registryLookupProvider, output);
    }

    @Override
    protected void buildRecipes() {

        // machine recipes

        this.reprocessor(Content.Items.CYANITE_INGOT, 2, Fluids.WATER, 1000,
                Content.Items.BLUTONIUM_INGOT, "cyanite_to_blutonium");

        this.reprocessor(Content.Items.BLUTONIUM_INGOT, 2, Content.Fluids.CYANITE_SOURCE, 1000,
                Content.Items.LUDICRITE_INGOT, "blutonium_to_ludicrite");

        this.reprocessor(Content.Items.LUDICRITE_INGOT, 2, Content.Fluids.MAGENTITE_SOURCE, 1000,
                Content.Items.RIDICULITE_INGOT, "ludicrite_to_ridiculite");

        this.reprocessor(Content.Items.RIDICULITE_INGOT, 2, Content.Fluids.ROSSINITE_SOURCE, 1000,
                Content.Items.INANITE_INGOT, "ridiculite_to_inanite");

        this.reprocessor(Content.Items.BENITOITE_CRYSTAL, 16, Content.Fluids.ROSSINITE_SOURCE, 2000,
                Content.Items.INSANITE_INGOT, "rossinite_to_insanite");

        // reprocessor blocks

        this.casing();
        this.glass();
        this.controller();
        this.port("wasteinjector", Content.Items.REPROCESSOR_WASTEINJECTOR, Items.STICKY_PISTON,
                ContentTags.Items.INGOTS_CYANITE, Tags.Items.DUSTS_REDSTONE);
        this.port("fluidinjector", Content.Items.REPROCESSOR_FLUIDINJECTOR, Items.PISTON,
                Tags.Items.GEMS_PRISMARINE, Tags.Items.GEMS_LAPIS);
        this.port("outputport", Content.Items.REPROCESSOR_OUTPUTPORT, Items.DISPENSER,
                Tags.Items.STORAGE_BLOCKS_LAPIS, Tags.Items.CHESTS);
        this.port("powerport", Content.Items.REPROCESSOR_POWERPORT, Items.REPEATER,
                Tags.Items.STORAGE_BLOCKS_REDSTONE, Tags.Items.GEMS_DIAMOND);
        this.port("collector", Content.Items.REPROCESSOR_COLLECTOR, Items.HOPPER,
                Tags.Items.INGOTS_IRON, Tags.Items.INGOTS_NETHERITE);
    }

    //region internals

    private void casing() {
        this.shaped(RecipeCategory.BUILDING_BLOCKS, Content.Items.REPROCESSOR_CASING)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('W', Items.WATER_BUCKET)
                .define('C', ContentTags.Items.INGOTS_CYANITE)
                .pattern("ICI")
                .pattern("CWC")
                .pattern("ICI")
                .unlockedBy("has_item", has(Tags.Items.INGOTS_IRON))
                .save(this.output, recipeKeyFrom(this.reprocessorRoot().buildWithSuffix("casing")));
    }

    private void glass() {
        this.shaped(RecipeCategory.BUILDING_BLOCKS, Content.Items.REPROCESSOR_GLASS)
                .define('C', Content.Items.REPROCESSOR_CASING.get())
                .define('G', Tags.Items.GLASS_BLOCKS)
                .pattern("GCG")
                .unlockedBy("has_item", has(Content.Items.REPROCESSOR_CASING.get()))
                .save(this.output, recipeKeyFrom(this.reprocessorRoot().buildWithSuffix("glass")));
    }

    private void controller() {
        this.shaped(RecipeCategory.BUILDING_BLOCKS, Content.Items.REPROCESSOR_CONTROLLER)
                .define('C', Content.Items.REPROCESSOR_CASING.get())
                .define('Y', ContentTags.Items.INGOTS_CYANITE)
                .define('P', Tags.Items.GEMS_PRISMARINE)
                .define('E', Tags.Items.GEMS_EMERALD)
                .define('X', Items.COMPARATOR)
                .pattern("CXC")
                .pattern("YEY")
                .pattern("CPC")
                .unlockedBy("has_item", has(Content.Items.REPROCESSOR_CASING.get()))
                .unlockedBy("has_item2", has(Tags.Items.GEMS_PRISMARINE))
                .save(this.output, recipeKeyFrom(this.reprocessorRoot().buildWithSuffix("controller")));
    }

    private void port(String name, Supplier<? extends ItemLike> result,
                      ItemLike item1, TagKey<Item> tag2, TagKey<Item> tag3) {
        this.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                .define('C', Content.Items.REPROCESSOR_CASING.get())
                .define('1', item1)
                .define('2', tag2)
                .define('3', tag3)
                .pattern("C2C")
                .pattern("313")
                .pattern("C2C")
                .unlockedBy("has_item", has(Content.Items.REPROCESSOR_CASING.get()))
                .unlockedBy("has_item2", has(item1))
                .save(this.output, recipeKeyFrom(this.reprocessorRoot().buildWithSuffix(name)));
    }

    protected void reprocessor(Supplier<? extends ItemLike> ingot, int ingotCount, Supplier<? extends Fluid> fluid,
                               int fluidAmount, Supplier<? extends ItemLike> result, String name) {
        reprocessor(ingot, ingotCount, fluid.get(), fluidAmount, result, name);
    }

    protected void reprocessor(Supplier<? extends ItemLike> ingot, int ingotCount, Fluid fluid,
                               int fluidAmount, Supplier<? extends ItemLike> result, String name) {
        this.twoToOne(ItemRecipeIngredient.of(ingotCount, ingot),
                        FluidRecipeIngredient.of(fluidAmount, fluid),
                        ItemStackRecipeResult.from(result),
                        ReprocessorRecipe::new)
                .build(this.output, recipeKeyFrom(this.reprocessorRoot().buildWithSuffix(name)));
    }

    //endregion
}
