package it.zerono.mods.extremereactors.datagen.recipe;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.api.reactor.ReactantMappingsRegistry;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerFluidMixingRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerSolidMixingRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerSolidRecipe;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.datagen.provider.recipe.OneToOneRecipeBuilder;
import it.zerono.mods.zerocore.lib.datagen.provider.recipe.TwoToOneRecipeBuilder;
import it.zerono.mods.zerocore.lib.recipe.ingredient.FluidStackRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.ingredient.ItemStackRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.FluidStackRecipeResult;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class FluidizerRecipesDataProvider
        extends AbstractRecipesDataProvider {

    public FluidizerRecipesDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup,
                                        ResourceLocationBuilder modLocationRoot) {
        super(ExtremeReactors.MOD_ID, "Fluidizer recipes", output, registryLookup, modLocationRoot);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {

        // machine recipes

        this.solid(output, "yellorium", Content.Items.YELLORIUM_INGOT, Content.Fluids.YELLORIUM_SOURCE);
        this.solid(output, "yellorium9", Content.Items.YELLORIUM_BLOCK, Content.Fluids.YELLORIUM_SOURCE, 9);
        this.solid(output, "cyanite", Content.Items.CYANITE_INGOT, Content.Fluids.CYANITE_SOURCE);
        this.solid(output, "cyanite9", Content.Items.CYANITE_BLOCK, Content.Fluids.CYANITE_SOURCE, 9);
        this.solid(output, "blutonium", Content.Items.BLUTONIUM_INGOT, Content.Fluids.BLUTONIUM_SOURCE);
        this.solid(output, "blutonium9", Content.Items.BLUTONIUM_BLOCK, Content.Fluids.BLUTONIUM_SOURCE, 9);
        this.solid(output, "magentite", Content.Items.MAGENTITE_INGOT, Content.Fluids.MAGENTITE_SOURCE);
        this.solid(output, "magentite9", Content.Items.MAGENTITE_BLOCK, Content.Fluids.MAGENTITE_SOURCE, 9);

        this.solidMixing(output, "verderium", Content.Items.YELLORIUM_INGOT, 2,
                Content.Items.BLUTONIUM_INGOT, 1, Content.Fluids.VERDERIUM_SOURCE, 2);
        this.solidMixing(output, "verderium9", Content.Items.YELLORIUM_BLOCK, 2,
                Content.Items.BLUTONIUM_BLOCK, 1, Content.Fluids.VERDERIUM_SOURCE, 18);
        this.fluidMixing(output, "verderium", Content.Fluids.YELLORIUM_SOURCE, 2000,
                Content.Fluids.BLUTONIUM_SOURCE, 1000, Content.Fluids.VERDERIUM_SOURCE, 2000);

        this.solidMixing(output, "cryomisi", Items.REDSTONE, 1, Items.SNOW_BLOCK, 1,
                Content.Fluids.CRYOMISI_SOURCE.get(), 500);
        this.solidMixing(output, "tangerium", Content.Items.ANGLESITE_CRYSTAL.get(), 1,
                Items.ENDER_PEARL, 4, Content.Fluids.TANGERIUM_SOURCE.get(), 500);
        this.fluidMixing(output, "redfrigium", Content.Fluids.CRYOMISI_SOURCE, 1000,
                Content.Fluids.TANGERIUM_SOURCE, 1000, Content.Fluids.REDFRIGIUM_SOURCE, 2000);

        // fluidizer blocks

        this.casing(output);
        this.glass(output);
        this.controller(output);
        this.port(output, "solidinjector", Content.Items.FLUIDIZER_SOLIDINJECTOR, Items.STICKY_PISTON,
                ContentTags.Items.INGOTS_YELLORIUM, Tags.Items.DUSTS_REDSTONE);
        this.port(output, "fluidinjector", Content.Items.FLUIDIZER_FLUIDINJECTOR, Items.PISTON,
                Tags.Items.INGOTS_NETHERITE, Tags.Items.GEMS_LAPIS);
        this.port(output, "outputport", Content.Items.FLUIDIZER_OUTPUTPORT, Items.DISPENSER,
                Tags.Items.STORAGE_BLOCKS_LAPIS, Tags.Items.CHESTS);
        this.port(output, "powerport", Content.Items.FLUIDIZER_POWERPORT, Items.REPEATER,
                Tags.Items.STORAGE_BLOCKS_REDSTONE, Tags.Items.GEMS_DIAMOND);
    }

    //endregion
    //region internals

    private void solid(RecipeOutput output, String name,
                       Supplier<? extends Item> ingredient, Supplier<FlowingFluid> result) {
        this.solid(output, name, ingredient, result, 1);
    }

    private void solid(RecipeOutput output, String name,
                       Supplier<? extends Item> ingredient, Supplier<FlowingFluid> result,
                       int resultMultiplier) {
        this.solid(output, name, ingredient.get(), 1, result.get(),
                ReactantMappingsRegistry.STANDARD_SOLID_REACTANT_AMOUNT * resultMultiplier);
    }

    private void solid(RecipeOutput output, String name, Item ingredient, int ingredientAmount,
                       Fluid result, int resultAmount) {
        new OneToOneRecipeBuilder<>(ItemStackRecipeIngredient.from(ingredient, ingredientAmount),
                FluidStackRecipeResult.from(new FluidStack(result, resultAmount)), FluidizerSolidRecipe::new)
                .build(output, this.fluidizerSolidRoot().buildWithSuffix(name));
    }

    private void solidMixing(RecipeOutput output, String name,
                             Supplier<? extends Item> ingredient1, int ingredient1Amount,
                             Supplier<? extends Item> ingredient2, int ingredient2Amount,
                             Supplier<FlowingFluid> result, int resultMultiplier) {
        this.solidMixing(output, name, ingredient1.get(), ingredient1Amount, ingredient2.get(), ingredient2Amount,
                result.get(), ReactantMappingsRegistry.STANDARD_SOLID_REACTANT_AMOUNT * resultMultiplier);
    }

    private void solidMixing(RecipeOutput output, String name,
                             Item ingredient1, int ingredient1Amount,
                             Item ingredient2, int ingredient2Amount,
                             Fluid result, int resultAmount) {

        final var idBuilder = this.fluidizerSolidMixingRoot().append(name);

        new TwoToOneRecipeBuilder<>(ItemStackRecipeIngredient.from(ingredient1, ingredient1Amount),
                ItemStackRecipeIngredient.from(ingredient2, ingredient2Amount),
                FluidStackRecipeResult.from(new FluidStack(result, resultAmount)),
                FluidizerSolidMixingRecipe::new)
                .build(output, idBuilder.buildWithSuffix("_1"));

        new TwoToOneRecipeBuilder<>(ItemStackRecipeIngredient.from(ingredient2, ingredient2Amount),
                ItemStackRecipeIngredient.from(ingredient1, ingredient1Amount),
                FluidStackRecipeResult.from(new FluidStack(result, resultAmount)),
                FluidizerSolidMixingRecipe::new)
                .build(output, idBuilder.buildWithSuffix("_2"));
    }

    private void fluidMixing(RecipeOutput output, String name,
                             Supplier<? extends Fluid> ingredient1, int ingredient1Amount,
                             Supplier<? extends Fluid> ingredient2, int ingredient2Amount,
                             Supplier<? extends Fluid> result, int resultAmount) {
        this.fluidMixing(output, name, ingredient1.get(), ingredient1Amount, ingredient2.get(), ingredient2Amount,
                result.get(), resultAmount);
    }

    private void fluidMixing(RecipeOutput output, String name,
                             Fluid ingredient1, int ingredient1Amount,
                             Fluid ingredient2, int ingredient2Amount,
                             Fluid result, int resultAmount) {

        final var idBuilder = this.fluidizerFluidMixingRoot().append(name);

        new TwoToOneRecipeBuilder<>(FluidStackRecipeIngredient.from(ingredient1, ingredient1Amount),
                FluidStackRecipeIngredient.from(ingredient2, ingredient2Amount),
                FluidStackRecipeResult.from(new FluidStack(result, resultAmount)),
                FluidizerFluidMixingRecipe::new)
                .build(output, idBuilder.buildWithSuffix("_1"));

        new TwoToOneRecipeBuilder<>(FluidStackRecipeIngredient.from(ingredient2, ingredient2Amount),
                FluidStackRecipeIngredient.from(ingredient1, ingredient1Amount),
                FluidStackRecipeResult.from(new FluidStack(result, resultAmount)),
                FluidizerFluidMixingRecipe::new)
                .build(output, idBuilder.buildWithSuffix("_2"));
    }

    private void casing(RecipeOutput output) {
        this.shaped(RecipeCategory.BUILDING_BLOCKS, Content.Items.FLUIDIZER_CASING)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('W', Items.WATER_BUCKET)
                .define('C', ContentTags.Items.INGOTS_YELLORIUM)
                .pattern("ICI")
                .pattern("CWC")
                .pattern("ICI")
                .unlockedBy("has_item", has(ContentTags.Items.INGOTS_YELLORIUM))
                .save(output, this.fluidizerRoot().buildWithSuffix("casing"));
    }

    private void glass(RecipeOutput output) {
        this.shaped(RecipeCategory.BUILDING_BLOCKS, Content.Items.FLUIDIZER_GLASS)
                .define('C', Content.Items.FLUIDIZER_CASING.get())
                .define('G', Tags.Items.GLASS)
                .pattern("GCG")
                .unlockedBy("has_item", has(Content.Items.FLUIDIZER_CASING.get()))
                .save(output, this.fluidizerRoot().buildWithSuffix("glass"));
    }

    private void controller(RecipeOutput output) {
        this.shaped(RecipeCategory.BUILDING_BLOCKS, Content.Items.FLUIDIZER_CONTROLLER)
                .define('C', Content.Items.FLUIDIZER_CASING.get())
                .define('Y', ContentTags.Items.INGOTS_YELLORIUM)
                .define('P', Tags.Items.DUSTS_GLOWSTONE)
                .define('E', Tags.Items.GEMS_EMERALD)
                .define('X', Items.COMPARATOR)
                .pattern("CXC")
                .pattern("YEY")
                .pattern("CPC")
                .unlockedBy("has_item", has(Content.Items.FLUIDIZER_CASING.get()))
                .unlockedBy("has_item2", has(Tags.Items.DUSTS_GLOWSTONE))
                .save(output, this.fluidizerRoot().buildWithSuffix("controller"));
    }

    private void port(RecipeOutput output, String name, Supplier<? extends ItemLike> result,
                      ItemLike item1, TagKey<Item> tag2, TagKey<Item> tag3) {
        this.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                .define('C', Content.Items.FLUIDIZER_CASING.get())
                .define('1', item1)
                .define('2', tag2)
                .define('3', tag3)
                .pattern("C2C")
                .pattern("313")
                .pattern("C2C")
                .unlockedBy("has_item", has(Content.Items.FLUIDIZER_CASING.get()))
                .unlockedBy("has_item2", has(item1))
                .save(output, this.fluidizerRoot().buildWithSuffix(name));
    }

    //endregion
}
