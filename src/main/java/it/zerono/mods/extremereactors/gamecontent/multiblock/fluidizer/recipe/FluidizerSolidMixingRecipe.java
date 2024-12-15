/*
 *
 * FluidizerSolidMixingRecipe.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe;

import com.google.common.base.Preconditions;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.zerocore.lib.data.stack.OperationMode;
import it.zerono.mods.zerocore.lib.recipe.IModRecipe;
import it.zerono.mods.zerocore.lib.recipe.IModRecipeType;
import it.zerono.mods.zerocore.lib.recipe.ITwoToOneModRecipe;
import it.zerono.mods.zerocore.lib.recipe.holder.AbstractHeldRecipe;
import it.zerono.mods.zerocore.lib.recipe.holder.IRecipeHolder;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredientSource;
import it.zerono.mods.zerocore.lib.recipe.ingredient.ItemRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.FluidStackRecipeResult;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResultTarget;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public record FluidizerSolidMixingRecipe(ItemRecipeIngredient ingredient1, ItemRecipeIngredient ingredient2,
                                         FluidStackRecipeResult result)
        implements IFluidizerRecipe,
            ITwoToOneModRecipe<ItemStack, ItemStack, FluidStack, ItemRecipeIngredient, ItemRecipeIngredient, FluidStackRecipeResult> {

    public FluidizerSolidMixingRecipe {

        Preconditions.checkArgument(!ingredient1.isEmpty(), "Ingredient 1 must not be empty");
        Preconditions.checkArgument(!ingredient2.isEmpty(), "Ingredient 2 must not be empty");
        Preconditions.checkArgument(!result.isEmpty(), "Result must not be empty");

        s_maxResultAmount = Math.max(s_maxResultAmount, result.getAmount());
    }

    public static boolean lookup(final IModRecipe recipe, final IRecipeIngredientSource<ItemStack> source1,
                                 final IRecipeIngredientSource<ItemStack> source2) {
        return recipe instanceof FluidizerSolidMixingRecipe &&
                ((FluidizerSolidMixingRecipe)recipe).test(source1.getIngredient(), source2.getIngredient());
    }

    public static long getMaxResultAmount() {
        return s_maxResultAmount;
    }

    @Override
    public Type getRecipeType() {
        return Type.SolidMixing;
    }

    /**
     * Check if the provided stack match one of the recipe ingredients
     */
    public boolean match(final ItemStack stack) {
        return this.ingredient1().test(stack) || this.ingredient2().test(stack);
    }

    /**
     * Check if the provided stacks match both the recipe ingredients
     */
    public boolean match(final ItemStack stack1, final ItemStack stack2) {

        final ItemRecipeIngredient ingredient1 = this.ingredient1();
        final ItemRecipeIngredient ingredient2 = this.ingredient2();

        return ingredient1.test(stack1) && ingredient2.test(stack2);
    }

    /**
     * Check if the provided stack match one of the recipe ingredients ignoring the stack size
     */
    public boolean matchIgnoreAmount(final ItemStack stack) {
        return this.ingredient1().testIgnoreAmount(stack) || this.ingredient2().testIgnoreAmount(stack);
    }

    public static RecipeSerializer<FluidizerSolidMixingRecipe> createSerializer() {
        return ITwoToOneModRecipe.createTwoToOneSerializer(
                "ingredient1", ItemRecipeIngredient.CODECS,
                "ingredient2", ItemRecipeIngredient.CODECS,
                "result", FluidStackRecipeResult.CODECS,
                FluidizerSolidMixingRecipe::new);
    }

    //region IFluidizerRecipe

    @Override
    public int getEnergyUsageMultiplier() {
        return this.getEnergyUsageMultiplier(this.result().getResult());
    }

    //endregion
    //region ITwoToOneModRecipe<ItemStack, ItemStack, FluidStack, ItemRecipeIngredient, ItemRecipeIngredient, FluidStackRecipeResult>

    @Override
    public RecipeSerializer<FluidizerSolidMixingRecipe> getSerializer() {
        return Content.Recipes.FLUIDIZER_SOLIDMIXING_RECIPE_SERIALIZER.get();
    }

    @Override
    public IModRecipeType<IModRecipe> getType() {
        return Content.Recipes.FLUIDIZER_RECIPE_TYPE.get();
    }

    @Override
    public Supplier<? extends @NotNull Item> getRecipeIcon() {
        return Content.Items.FLUIDIZER_FLUIDINJECTOR;
    }

    //endregion
    //region held recipe

    public static class HeldRecipe
            extends AbstractHeldRecipe<FluidizerSolidMixingRecipe>
            implements IFluidizerRecipe.Held<FluidizerSolidMixingRecipe> {

        public <Holder extends IRecipeHolder<FluidizerSolidMixingRecipe>>
        HeldRecipe(final FluidizerSolidMixingRecipe recipe, final Holder holder,
                   final IRecipeIngredientSource<ItemStack> item1Source, final IRecipeIngredientSource<ItemStack> item2Source,
                   final IRecipeResultTarget<FluidStackRecipeResult> outputTarget) {

            super(recipe, holder);
            this._item1Source = item1Source;
            this._item2Source = item2Source;
            this._outputTarget = outputTarget;
        }

        //region AbstractHeldRecipe

        /**
         * Called after the recipe was completely processed.
         */
        @Override
        public void onRecipeProcessed() {

            final FluidizerSolidMixingRecipe recipe = this.getRecipe();
            final ItemStack item1 = this._item1Source.getMatchFrom(recipe.ingredient1());
            final ItemStack item2 = this._item2Source.getMatchFrom(recipe.ingredient2());

            if (!item1.isEmpty() && !item2.isEmpty()) {

                this._item1Source.consumeIngredient(item1);
                this._item2Source.consumeIngredient(item2);
                this._outputTarget.setResult(recipe.result(), OperationMode.Execute);
            }
        }

        //endregion
        //region internals

        private final IRecipeIngredientSource<ItemStack> _item1Source;
        private final IRecipeIngredientSource<ItemStack> _item2Source;
        private final IRecipeResultTarget<FluidStackRecipeResult> _outputTarget;

        //endregion
    }

    //endregion
    //region internals

    private static long s_maxResultAmount = 0;

    //endregion
}
