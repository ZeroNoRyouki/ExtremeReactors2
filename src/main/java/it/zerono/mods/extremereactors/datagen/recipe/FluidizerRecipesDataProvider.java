package it.zerono.mods.extremereactors.datagen.recipe;

import it.zerono.mods.extremereactors.api.reactor.ReactantMappingsRegistry;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerFluidMixingRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerSolidMixingRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerSolidRecipe;
import it.zerono.mods.zerocore.lib.datagen.provider.recipe.ModRecipeProviderRunner;
import it.zerono.mods.zerocore.lib.datagen.provider.recipe.OneToOneRecipeBuilder;
import it.zerono.mods.zerocore.lib.recipe.ingredient.FluidRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.ingredient.ItemRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.FluidStackRecipeResult;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.Tags;

import java.util.function.Supplier;

public class FluidizerRecipesDataProvider
        extends AbstractRecipesDataProvider {

    public FluidizerRecipesDataProvider(ModRecipeProviderRunner<FluidizerRecipesDataProvider> mainProvider,
                                        HolderLookup.Provider registryLookupProvider, RecipeOutput output) {
        super(mainProvider, registryLookupProvider, output);
    }

    @Override
    protected void buildRecipes() {

        // machine recipes

        this.solid("yellorium", Content.Items.YELLORIUM_INGOT, Content.Fluids.YELLORIUM_SOURCE);
        this.solid("yellorium9", Content.Items.YELLORIUM_BLOCK, Content.Fluids.YELLORIUM_SOURCE, 9);
        this.solid("cyanite", Content.Items.CYANITE_INGOT, Content.Fluids.CYANITE_SOURCE);
        this.solid("cyanite9", Content.Items.CYANITE_BLOCK, Content.Fluids.CYANITE_SOURCE, 9);
        this.solid("blutonium", Content.Items.BLUTONIUM_INGOT, Content.Fluids.BLUTONIUM_SOURCE);
        this.solid("blutonium9", Content.Items.BLUTONIUM_BLOCK, Content.Fluids.BLUTONIUM_SOURCE, 9);
        this.solid("magentite", Content.Items.MAGENTITE_INGOT, Content.Fluids.MAGENTITE_SOURCE);
        this.solid("magentite9", Content.Items.MAGENTITE_BLOCK, Content.Fluids.MAGENTITE_SOURCE, 9);

        this.solidMixing("verderium", Content.Items.YELLORIUM_INGOT, 2,
                Content.Items.BLUTONIUM_INGOT, 1, Content.Fluids.VERDERIUM_SOURCE, 2);
        this.solidMixing("verderium9", Content.Items.YELLORIUM_BLOCK, 2,
                Content.Items.BLUTONIUM_BLOCK, 1, Content.Fluids.VERDERIUM_SOURCE, 18);
        this.fluidMixing("verderium", Content.Fluids.YELLORIUM_SOURCE, 2000,
                Content.Fluids.BLUTONIUM_SOURCE, 1000, Content.Fluids.VERDERIUM_SOURCE, 2000);

        this.solidMixing("cryomisi", Items.REDSTONE, 1, Items.SNOW_BLOCK, 1,
                Content.Fluids.CRYOMISI_SOURCE.get(), 500);
        this.solidMixing("tangerium", Content.Items.ANGLESITE_CRYSTAL.get(), 1,
                Items.ENDER_PEARL, 4, Content.Fluids.TANGERIUM_SOURCE.get(), 500);
        this.fluidMixing("redfrigium", Content.Fluids.CRYOMISI_SOURCE, 1000,
                Content.Fluids.TANGERIUM_SOURCE, 1000, Content.Fluids.REDFRIGIUM_SOURCE, 2000);

        // fluidizer blocks

        this.casing();
        this.glass();
        this.controller();
        this.port("solidinjector", Content.Items.FLUIDIZER_SOLIDINJECTOR, Items.STICKY_PISTON,
                ContentTags.Items.INGOTS_YELLORIUM, Tags.Items.DUSTS_REDSTONE);
        this.port("fluidinjector", Content.Items.FLUIDIZER_FLUIDINJECTOR, Items.PISTON,
                Tags.Items.INGOTS_NETHERITE, Tags.Items.GEMS_LAPIS);
        this.port("outputport", Content.Items.FLUIDIZER_OUTPUTPORT, Items.DISPENSER,
                Tags.Items.STORAGE_BLOCKS_LAPIS, Tags.Items.CHESTS);
        this.port("powerport", Content.Items.FLUIDIZER_POWERPORT, Items.REPEATER,
                Tags.Items.STORAGE_BLOCKS_REDSTONE, Tags.Items.GEMS_DIAMOND);
    }

    //endregion
    //region internals

    private void solid(String name, Supplier<? extends Item> ingredient, Supplier<FlowingFluid> result) {
        this.solid(name, ingredient, result, 1);
    }

    private void solid(String name, Supplier<? extends Item> ingredient, Supplier<FlowingFluid> result,
                       int resultMultiplier) {
        this.solid(name, ingredient.get(), 1, result.get(),
                ReactantMappingsRegistry.STANDARD_SOLID_REACTANT_AMOUNT * resultMultiplier);
    }

    private void solid(String name, Item ingredient, int ingredientAmount, Fluid result, int resultAmount) {
        new OneToOneRecipeBuilder<>(this::holderGetterOf, ItemRecipeIngredient.of(ingredientAmount, ingredient),
                FluidStackRecipeResult.from(result, resultAmount), FluidizerSolidRecipe::new)
                .build(this.output, this.fluidizerSolidRoot().buildWithSuffix(name));
    }

    private void solidMixing(String name,
                             Supplier<? extends Item> ingredient1, int ingredient1Amount,
                             Supplier<? extends Item> ingredient2, int ingredient2Amount,
                             Supplier<FlowingFluid> result, int resultMultiplier) {
        this.solidMixing(name, ingredient1.get(), ingredient1Amount, ingredient2.get(), ingredient2Amount,
                result.get(), ReactantMappingsRegistry.STANDARD_SOLID_REACTANT_AMOUNT * resultMultiplier);
    }

    private void solidMixing(String name,
                             Item ingredient1, int ingredient1Amount,
                             Item ingredient2, int ingredient2Amount,
                             Fluid result, int resultAmount) {

        final var idBuilder = this.fluidizerSolidMixingRoot().append(name);

        this.twoToOne(ItemRecipeIngredient.of(ingredient1Amount, ingredient1),
                        ItemRecipeIngredient.of(ingredient2Amount, ingredient2),
                        FluidStackRecipeResult.from(result, resultAmount),
                        FluidizerSolidMixingRecipe::new)
                .build(this.output, idBuilder.buildWithSuffix("_1"));

        this.twoToOne(ItemRecipeIngredient.of(ingredient2Amount, ingredient2),
                        ItemRecipeIngredient.of(ingredient1Amount, ingredient1),
                        FluidStackRecipeResult.from(result, resultAmount),
                        FluidizerSolidMixingRecipe::new)
                .build(this.output, idBuilder.buildWithSuffix("_2"));
    }

    private void fluidMixing(String name,
                             Supplier<? extends Fluid> ingredient1, int ingredient1Amount,
                             Supplier<? extends Fluid> ingredient2, int ingredient2Amount,
                             Supplier<? extends Fluid> result, int resultAmount) {
        this.fluidMixing(name, ingredient1.get(), ingredient1Amount, ingredient2.get(), ingredient2Amount,
                result.get(), resultAmount);
    }

    private void fluidMixing(String name,
                             Fluid ingredient1, int ingredient1Amount,
                             Fluid ingredient2, int ingredient2Amount,
                             Fluid result, int resultAmount) {

        final var idBuilder = this.fluidizerFluidMixingRoot().append(name);

        this.twoToOne(FluidRecipeIngredient.of(ingredient1Amount, ingredient1),
                        FluidRecipeIngredient.of(ingredient2Amount, ingredient2),
                        FluidStackRecipeResult.from(result, resultAmount),
                        FluidizerFluidMixingRecipe::new)
                .build(this.output, idBuilder.buildWithSuffix("_1"));

        this.twoToOne(FluidRecipeIngredient.of(ingredient2Amount, ingredient2),
                        FluidRecipeIngredient.of(ingredient1Amount, ingredient1),
                        FluidStackRecipeResult.from(result, resultAmount),
                        FluidizerFluidMixingRecipe::new)
                .build(this.output, idBuilder.buildWithSuffix("_2"));
    }

    private void casing() {
        this.shaped(RecipeCategory.BUILDING_BLOCKS, Content.Items.FLUIDIZER_CASING)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('W', Items.WATER_BUCKET)
                .define('C', ContentTags.Items.INGOTS_YELLORIUM)
                .pattern("ICI")
                .pattern("CWC")
                .pattern("ICI")
                .unlockedBy("has_item", has(ContentTags.Items.INGOTS_YELLORIUM))
                .save(this.output, recipeKeyFrom(this.fluidizerRoot().buildWithSuffix("casing")));
    }

    private void glass() {
        this.shaped(RecipeCategory.BUILDING_BLOCKS, Content.Items.FLUIDIZER_GLASS)
                .define('C', Content.Items.FLUIDIZER_CASING.get())
                .define('G', Tags.Items.GLASS_BLOCKS)
                .pattern("GCG")
                .unlockedBy("has_item", has(Content.Items.FLUIDIZER_CASING.get()))
                .save(this.output, recipeKeyFrom(this.fluidizerRoot().buildWithSuffix("glass")));
    }

    private void controller() {
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
                .save(this.output, recipeKeyFrom(this.fluidizerRoot().buildWithSuffix("controller")));
    }

    private void port(String name, Supplier<? extends ItemLike> result,
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
                .save(this.output, recipeKeyFrom(this.fluidizerRoot().buildWithSuffix(name)));
    }

    //endregion
}
