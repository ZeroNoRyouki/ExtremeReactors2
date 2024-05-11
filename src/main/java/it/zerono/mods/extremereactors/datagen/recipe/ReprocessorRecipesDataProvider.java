package it.zerono.mods.extremereactors.datagen.recipe;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.recipe.ReprocessorRecipe;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.datagen.provider.recipe.TwoToOneRecipeBuilder;
import it.zerono.mods.zerocore.lib.recipe.ingredient.FluidStackRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.ingredient.ItemStackRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.ItemStackRecipeResult;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ReprocessorRecipesDataProvider
        extends AbstractRecipesDataProvider {

    public ReprocessorRecipesDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup,
                                          ResourceLocationBuilder modLocationRoot) {
        super(ExtremeReactors.MOD_ID, "Reprocessor recipes", output, registryLookup, modLocationRoot);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {

        final var idBuilder = this.reprocessorRoot();

        // machine recipes

        this.reprocessor(ItemStackRecipeIngredient.from(Content.Items.CYANITE_INGOT, 2),
                        FluidStackRecipeIngredient.from(Fluids.WATER, 1000),
                        ItemStackRecipeResult.from(Content.Items.BLUTONIUM_INGOT.get()))
                .accept(output, idBuilder.buildWithSuffix("cyanite_to_blutonium"));

        this.reprocessor(ItemStackRecipeIngredient.from(Content.Items.BLUTONIUM_INGOT, 2),
                        FluidStackRecipeIngredient.from(Content.Fluids.CYANITE_SOURCE.get(), 1000),
                        ItemStackRecipeResult.from(Content.Items.LUDICRITE_INGOT.get()))
                .accept(output, idBuilder.buildWithSuffix("blutonium_to_ludicrite"));

        this.reprocessor(ItemStackRecipeIngredient.from(Content.Items.LUDICRITE_INGOT, 2),
                        FluidStackRecipeIngredient.from(Content.Fluids.MAGENTITE_SOURCE.get(), 1000),
                        ItemStackRecipeResult.from(Content.Items.RIDICULITE_INGOT.get()))
                .accept(output, idBuilder.buildWithSuffix("ludicrite_to_ridiculite"));

        this.reprocessor(ItemStackRecipeIngredient.from(Content.Items.RIDICULITE_INGOT, 2),
                        FluidStackRecipeIngredient.from(Content.Fluids.ROSSINITE_SOURCE.get(), 1000),
                        ItemStackRecipeResult.from(Content.Items.INANITE_INGOT.get()))
                .accept(output, idBuilder.buildWithSuffix("ridiculite_to_inanite"));

        this.reprocessor(ItemStackRecipeIngredient.from(Content.Items.BENITOITE_CRYSTAL.get(), 16),
                        FluidStackRecipeIngredient.from(Content.Fluids.ROSSINITE_SOURCE.get(), 2000),
                        ItemStackRecipeResult.from(Content.Items.INSANITE_INGOT.get()))
                .accept(output, idBuilder.buildWithSuffix("rossinite_to_insanite"));

        // reprocessor blocks

        this.casing(output);
        this.glass(output);
        this.controller(output);
        this.port(output, "wasteinjector", Content.Items.REPROCESSOR_WASTEINJECTOR, Items.STICKY_PISTON,
                ContentTags.Items.INGOTS_CYANITE, Tags.Items.DUSTS_REDSTONE);
        this.port(output, "fluidinjector", Content.Items.REPROCESSOR_FLUIDINJECTOR, Items.PISTON,
                Tags.Items.GEMS_PRISMARINE, Tags.Items.GEMS_LAPIS);
        this.port(output, "outputport", Content.Items.REPROCESSOR_OUTPUTPORT, Items.DISPENSER,
                Tags.Items.STORAGE_BLOCKS_LAPIS, Tags.Items.CHESTS);
        this.port(output, "powerport", Content.Items.REPROCESSOR_POWERPORT, Items.REPEATER,
                Tags.Items.STORAGE_BLOCKS_REDSTONE, Tags.Items.GEMS_DIAMOND);
        this.port(output, "collector", Content.Items.REPROCESSOR_COLLECTOR, Items.HOPPER,
                Tags.Items.INGOTS_IRON, Tags.Items.INGOTS_NETHERITE);
    }

    //region internals

    private void casing(RecipeOutput output) {
        this.shaped(RecipeCategory.BUILDING_BLOCKS, Content.Items.REPROCESSOR_CASING)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('W', Items.WATER_BUCKET)
                .define('C', ContentTags.Items.INGOTS_CYANITE)
                .pattern("ICI")
                .pattern("CWC")
                .pattern("ICI")
                .unlockedBy("has_item", has(Tags.Items.INGOTS_IRON))
                .save(output, this.reprocessorRoot().buildWithSuffix("casing"));
    }

    private void glass(RecipeOutput output) {
        this.shaped(RecipeCategory.BUILDING_BLOCKS, Content.Items.REPROCESSOR_GLASS)
                .define('C', Content.Items.REPROCESSOR_CASING.get())
                .define('G', Tags.Items.GLASS)
                .pattern("GCG")
                .unlockedBy("has_item", has(Content.Items.REPROCESSOR_CASING.get()))
                .save(output, this.reprocessorRoot().buildWithSuffix("glass"));
    }

    private void controller(RecipeOutput output) {
        this.shaped(RecipeCategory.BUILDING_BLOCKS, Content.Items.REPROCESSOR_CONTROLLER)
                .define('C', Content.Items.REPROCESSOR_CASING.get())
                .define('Y', ContentTags.Items.INGOTS_CYANITE)
                .define('P', Tags.Items.DUSTS_PRISMARINE)
                .define('E', Tags.Items.GEMS_EMERALD)
                .define('X', Items.COMPARATOR)
                .pattern("CXC")
                .pattern("YEY")
                .pattern("CPC")
                .unlockedBy("has_item", has(Content.Items.REPROCESSOR_CASING.get()))
                .unlockedBy("has_item2", has(Tags.Items.DUSTS_PRISMARINE))
                .save(output, this.reprocessorRoot().buildWithSuffix("controller"));
    }

    private void port(RecipeOutput output, String name, Supplier<? extends ItemLike> result,
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
                .save(output, this.reprocessorRoot().buildWithSuffix(name));
    }

    protected BiConsumer<RecipeOutput, ResourceLocation> reprocessor(ItemStackRecipeIngredient ingot,
                                                                     FluidStackRecipeIngredient fluid,
                                                                     ItemStackRecipeResult result) {
        return new TwoToOneRecipeBuilder<>(ingot, fluid, result, ReprocessorRecipe::new)::build;
    }

    //endregion
}
