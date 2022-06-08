/*
 *
 * FluidizerRecipeGenerator.java
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
import it.zerono.mods.extremereactors.api.reactor.ReactantMappingsRegistry;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.extremereactors.gamecontent.fluid.ReactantFluid;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerFluidMixingRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerSolidMixingRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerSolidRecipe;
import it.zerono.mods.zerocore.lib.recipe.ingredient.FluidStackRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.ingredient.ItemStackRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.FluidStackRecipeResult;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FluidizerRecipeGenerator
        extends AbstractRecipeGenerator {

    public FluidizerRecipeGenerator(final DataGenerator generatorIn) {
        super(generatorIn);
    }

    //region RecipeProvider

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return ExtremeReactors.MOD_NAME + "Fluidizer recipes";
    }

    /**
     * Registers all recipes to the given consumer.
     */
    @Override
    protected void buildCraftingRecipes(final Consumer<FinishedRecipe> c) {

        // machine recipes

        solid(c, "yellorium", Content.Items.YELLORIUM_INGOT, Content.Fluids.YELLORIUM_SOURCE);
        solid(c, "yellorium9", Content.Items.YELLORIUM_BLOCK, Content.Fluids.YELLORIUM_SOURCE, 9);
        solid(c, "cyanite", Content.Items.CYANITE_INGOT, Content.Fluids.CYANITE_SOURCE);
        solid(c, "cyanite9", Content.Items.CYANITE_BLOCK, Content.Fluids.CYANITE_SOURCE, 9);
        solid(c, "blutonium", Content.Items.BLUTONIUM_INGOT, Content.Fluids.BLUTONIUM_SOURCE);
        solid(c, "blutonium9", Content.Items.BLUTONIUM_BLOCK, Content.Fluids.BLUTONIUM_SOURCE, 9);
        solid(c, "magentite", Content.Items.MAGENTITE_INGOT, Content.Fluids.MAGENTITE_SOURCE);
        solid(c, "magentite9", Content.Items.MAGENTITE_BLOCK, Content.Fluids.MAGENTITE_SOURCE, 9);
        solidMixing(c, "verderium", Content.Items.YELLORIUM_INGOT, 2, Content.Items.BLUTONIUM_INGOT, 1, Content.Fluids.VERDERIUM_SOURCE, 2);
        solidMixing(c, "verderium9", Content.Items.YELLORIUM_BLOCK, 2, Content.Items.BLUTONIUM_BLOCK, 1, Content.Fluids.VERDERIUM_SOURCE, 18);
        fluidMixing(c, "verderium", Content.Fluids.YELLORIUM_SOURCE, 2000, Content.Fluids.BLUTONIUM_SOURCE, 1000, Content.Fluids.VERDERIUM_SOURCE, 2000);

        solidMixing(c, "cryomisi", Items.REDSTONE, 1, Items.SNOW_BLOCK, 1, Content.Fluids.CRYOMISI_SOURCE.get(), 500);
        solidMixing(c, "tangerium", Content.Items.ANGLESITE_CRYSTAL.get(), 1, Items.ENDER_PEARL, 4, Content.Fluids.TANGERIUM_SOURCE.get(), 500);
        fluidMixing(c, "redfrigium", Content.Fluids.CRYOMISI_SOURCE, 1000, Content.Fluids.TANGERIUM_SOURCE, 1000, Content.Fluids.REDFRIGIUM_SOURCE, 2000);

        // fluidizer blocks

        this.casing(c);
        this.glass(c);
        this.controller(c);
        this.port(c, "solidinjector", Content.Items.FLUIDIZER_SOLIDINJECTOR, Items.STICKY_PISTON,
                ContentTags.Items.INGOTS_YELLORIUM, Tags.Items.DUSTS_REDSTONE);
        this.port(c, "fluidinjector", Content.Items.FLUIDIZER_FLUIDINJECTOR, Items.PISTON,
                Tags.Items.INGOTS_NETHERITE, Tags.Items.GEMS_LAPIS);
        this.port(c, "outputport", Content.Items.FLUIDIZER_OUTPUTPORT, Items.DISPENSER,
                Tags.Items.STORAGE_BLOCKS_LAPIS, Tags.Items.CHESTS);
        this.port(c, "powerport", Content.Items.FLUIDIZER_POWERPORT, Items.REPEATER,
                Tags.Items.STORAGE_BLOCKS_REDSTONE, Tags.Items.GEMS_DIAMOND);
    }

    //endregion
    //region internals

    private static void solid(final Consumer<FinishedRecipe> c, final String name,
                              final Supplier<? extends Item> ingredient, final Supplier<ReactantFluid.Source> result) {
        solid(c, name, ingredient, result, 1);
    }

    private static void solid(final Consumer<FinishedRecipe> c, final String name,
                              final Supplier<? extends Item> ingredient, final Supplier<ReactantFluid.Source> result,
                              final int resultMultiplier) {
//
//        FluidizerSolidRecipe.builder(ItemStackRecipeIngredient.from(ingredient.get()),
//                        FluidStackRecipeResult.from(new FluidStack(result.get(), ReactantMappingsRegistry.STANDARD_SOLID_REACTANT_AMOUNT * resultMultiplier)))
//                .build(c, ExtremeReactors.newID("fluidizer/solid/" + name));

        solid(c, name, ingredient.get(), 1, result.get(), ReactantMappingsRegistry.STANDARD_SOLID_REACTANT_AMOUNT * resultMultiplier);
    }

    private static void solid(final Consumer<FinishedRecipe> c, final String name,
                              final Item ingredient, final int ingredientAmount,
                              final Fluid result, final int resultAmount) {

        FluidizerSolidRecipe.builder(ItemStackRecipeIngredient.from(ingredient, ingredientAmount),
                        FluidStackRecipeResult.from(new FluidStack(result, resultAmount)))
                .build(c, ExtremeReactors.newID("fluidizer/solid/" + name));
    }

    private static void solidMixing(final Consumer<FinishedRecipe> c, final String name,
                                    final Supplier<? extends Item> ingredient1, final int ingredient1Amount,
                                    final Supplier<? extends Item> ingredient2, final int ingredient2Amount,
                                    final Supplier<ReactantFluid.Source> result, final int resultMultiplier) {
        solidMixing(c, name, ingredient1.get(), ingredient1Amount, ingredient2.get(), ingredient2Amount, result.get(), ReactantMappingsRegistry.STANDARD_SOLID_REACTANT_AMOUNT * resultMultiplier);
//
//        FluidizerSolidMixingRecipe.builder(ItemStackRecipeIngredient.from(ingredient1.get(), ingredient1Amount),
//                        ItemStackRecipeIngredient.from(ingredient2.get(), ingredient2Amount),
//                        FluidStackRecipeResult.from(new FluidStack(result.get(), ReactantMappingsRegistry.STANDARD_SOLID_REACTANT_AMOUNT * resultMultiplier)))
//                .build(c, ExtremeReactors.newID("fluidizer/solidmixing/" + name + "_1"));
//
//        FluidizerSolidMixingRecipe.builder(ItemStackRecipeIngredient.from(ingredient2.get(), ingredient2Amount),
//                        ItemStackRecipeIngredient.from(ingredient1.get(), ingredient1Amount),
//                        FluidStackRecipeResult.from(new FluidStack(result.get(), ReactantMappingsRegistry.STANDARD_SOLID_REACTANT_AMOUNT * resultMultiplier)))
//                .build(c, ExtremeReactors.newID("fluidizer/solidmixing/" + name + "_2"));
    }

    private static void solidMixing(final Consumer<FinishedRecipe> c, final String name,
                                    final Item ingredient1, final int ingredient1Amount,
                                    final Item ingredient2, final int ingredient2Amount,
                                    final Fluid result, final int resultAmount) {

        FluidizerSolidMixingRecipe.builder(ItemStackRecipeIngredient.from(ingredient1, ingredient1Amount),
                        ItemStackRecipeIngredient.from(ingredient2, ingredient2Amount),
                        FluidStackRecipeResult.from(new FluidStack(result, resultAmount)))
                .build(c, ExtremeReactors.newID("fluidizer/solidmixing/" + name + "_1"));

        FluidizerSolidMixingRecipe.builder(ItemStackRecipeIngredient.from(ingredient2, ingredient2Amount),
                        ItemStackRecipeIngredient.from(ingredient1, ingredient1Amount),
                        FluidStackRecipeResult.from(new FluidStack(result, resultAmount)))
                .build(c, ExtremeReactors.newID("fluidizer/solidmixing/" + name + "_2"));
    }

    private static void fluidMixing(final Consumer<FinishedRecipe> c, final String name,
                                    final Supplier<? extends Fluid> ingredient1, final int ingredient1Amount,
                                    final Supplier<? extends Fluid> ingredient2, final int ingredient2Amount,
                                    final Supplier<? extends Fluid> result, final int resultAmount) {
        fluidMixing(c, name, ingredient1.get(), ingredient1Amount, ingredient2.get(), ingredient2Amount, result.get(), resultAmount);
    }

    private static void fluidMixing(final Consumer<FinishedRecipe> c, final String name,
                                    final Fluid ingredient1, final int ingredient1Amount,
                                    final Fluid ingredient2, final int ingredient2Amount,
                                    final Fluid result, final int resultAmount) {

        FluidizerFluidMixingRecipe.builder(FluidStackRecipeIngredient.from(ingredient1, ingredient1Amount),
                        FluidStackRecipeIngredient.from(ingredient2, ingredient2Amount),
                        FluidStackRecipeResult.from(new FluidStack(result, resultAmount)))
                .build(c, ExtremeReactors.newID("fluidizer/fluidmixing/" + name + "_1"));

        FluidizerFluidMixingRecipe.builder(FluidStackRecipeIngredient.from(ingredient2, ingredient2Amount),
                        FluidStackRecipeIngredient.from(ingredient1, ingredient1Amount),
                        FluidStackRecipeResult.from(new FluidStack(result, resultAmount)))
                .build(c, ExtremeReactors.newID("fluidizer/fluidmixing/" + name + "_2"));
    }

    private void casing(final Consumer<FinishedRecipe> c) {
        ShapedRecipeBuilder.shaped(Content.Items.FLUIDIZER_CASING.get())
                .define('I', Tags.Items.INGOTS_IRON)
                .define('W', Items.WATER_BUCKET)
                .define('C', ContentTags.Items.INGOTS_YELLORIUM)
                .pattern("ICI")
                .pattern("CWC")
                .pattern("ICI")
                .group(GROUP_GENERAL)
                .unlockedBy("has_item", has(ContentTags.Items.INGOTS_YELLORIUM))
                .save(c, fluidizerRecipeName("casing"));
    }

    private void glass(final Consumer<FinishedRecipe> c) {
        ShapedRecipeBuilder.shaped(Content.Items.FLUIDIZER_GLASS.get())
                .define('C', Content.Items.FLUIDIZER_CASING.get())
                .define('G', Tags.Items.GLASS)
                .pattern("GCG")
                .group(GROUP_GENERAL)
                .unlockedBy("has_item", has(Content.Items.FLUIDIZER_CASING.get()))
                .save(c, fluidizerRecipeName("glass"));
    }

    private void controller(final Consumer<FinishedRecipe> c) {
        ShapedRecipeBuilder.shaped(Content.Items.FLUIDIZER_CONTROLLER.get())
                .define('C', Content.Items.FLUIDIZER_CASING.get())
                .define('Y', ContentTags.Items.INGOTS_YELLORIUM)
                .define('P', Tags.Items.DUSTS_GLOWSTONE)
                .define('E', Tags.Items.GEMS_EMERALD)
                .define('X', Items.COMPARATOR)
                .pattern("CXC")
                .pattern("YEY")
                .pattern("CPC")
                .group(GROUP_GENERAL)
                .unlockedBy("has_item", has(Content.Items.FLUIDIZER_CASING.get()))
                .unlockedBy("has_item2", has(Tags.Items.DUSTS_GLOWSTONE))
                .save(c, fluidizerRecipeName("controller"));
    }

    private void port(final Consumer<FinishedRecipe> c, final String name, final Supplier<? extends ItemLike> result,
                      final ItemLike item1, final Tag<Item> tag2, final Tag<Item> tag3) {
        ShapedRecipeBuilder.shaped(result.get())
                .define('C', Content.Items.FLUIDIZER_CASING.get())
                .define('1', item1)
                .define('2', tag2)
                .define('3', tag3)
                .pattern("C2C")
                .pattern("313")
                .pattern("C2C")
                .group(GROUP_GENERAL)
                .unlockedBy("has_item", has(Content.Items.FLUIDIZER_CASING.get()))
                .unlockedBy("has_item2", has(item1))
                .save(c, fluidizerRecipeName(name));
    }

    private static ResourceLocation fluidizerRecipeName(final String name) {
        return ExtremeReactors.newID("fluidizer/" + name);
    }

    //endregion
}
