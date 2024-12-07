/*
 *
 * FluidizerFluidMixingRecipe.java
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
import it.zerono.mods.zerocore.lib.recipe.ITwoToOneModRecipe;
import it.zerono.mods.zerocore.lib.recipe.holder.AbstractHeldRecipe;
import it.zerono.mods.zerocore.lib.recipe.holder.IRecipeHolder;
import it.zerono.mods.zerocore.lib.recipe.ingredient.FluidRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredientSource;
import it.zerono.mods.zerocore.lib.recipe.result.FluidStackRecipeResult;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResultTarget;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public record FluidizerFluidMixingRecipe(FluidRecipeIngredient ingredient1, FluidRecipeIngredient ingredient2,
                                         FluidStackRecipeResult result)
        implements IFluidizerRecipe, 
            ITwoToOneModRecipe<FluidStack, FluidStack, FluidStack, FluidRecipeIngredient, FluidRecipeIngredient, FluidStackRecipeResult> {

    public FluidizerFluidMixingRecipe {

        Preconditions.checkArgument(!ingredient1.isEmpty(), "Ingredient 1 must not be empty");
        Preconditions.checkArgument(!ingredient2.isEmpty(), "Ingredient 2 must not be empty");
        Preconditions.checkArgument(!result.isEmpty(), "Result must not be empty");

        s_maxResultAmount = Math.max(s_maxResultAmount, result.getAmount());
    }

    public static boolean lookup(final IModRecipe recipe, final IRecipeIngredientSource<FluidStack> source1,
                                 final IRecipeIngredientSource<FluidStack> source2) {
        return recipe instanceof FluidizerFluidMixingRecipe &&
                ((FluidizerFluidMixingRecipe)recipe).test(source1.getIngredient(), source2.getIngredient());
    }

    public static long getMaxResultAmount() {
        return s_maxResultAmount;
    }

    @Override
    public Type getRecipeType() {
        return Type.FluidMixing;
    }

    /**
     * Check if the provided stack match one of the recipe ingredients
     */
    public boolean match(final FluidStack stack) {
        return this.ingredient1().test(stack) || this.ingredient2().test(stack);
    }

    /**
     * Check if the provided stacks match both the recipe ingredients
     */
    public boolean match(final FluidStack stack1, final FluidStack stack2) {

        final FluidRecipeIngredient ingredient1 = this.ingredient1();
        final FluidRecipeIngredient ingredient2 = this.ingredient2();

        return ingredient1.test(stack1) && ingredient2.test(stack2);
    }

    /**
     * Check if the provided stack match one of the recipe ingredients ignoring the stack size
     */
    public boolean matchIgnoreAmount(final FluidStack stack) {
        return this.ingredient1().testIgnoreAmount(stack) || this.ingredient2().testIgnoreAmount(stack);
    }

    public static RecipeSerializer<FluidizerFluidMixingRecipe> createSerializer() {
        return ITwoToOneModRecipe.createTwoToOneSerializer(
                "ingredient1", FluidRecipeIngredient.CODECS,
                "ingredient2", FluidRecipeIngredient.CODECS,
                "result", FluidStackRecipeResult.CODECS,
                FluidizerFluidMixingRecipe::new);
    }

    //region IFluidizerRecipe

    @Override
    public int getEnergyUsageMultiplier() {
        return this.getEnergyUsageMultiplier(this.result().getResult());
    }

    //endregion
    //region ITwoToOneModRecipe<FluidStack, FluidStack, FluidStack, FluidRecipeIngredient, FluidRecipeIngredient, FluidStackRecipeResult>

    @Override
    public RecipeSerializer<FluidizerFluidMixingRecipe> getSerializer() {
        return Content.Recipes.FLUIDIZER_FLUIDMIXING_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<IModRecipe> getType() {
        return Content.Recipes.FLUIDIZER_RECIPE_TYPE.get();
    }

    @Override
    public Supplier<? extends @NotNull Item> getRecipeIcon() {
        return Content.Items.FLUIDIZER_SOLIDINJECTOR;
    }

    //endregion
    //region held recipe

    public static class HeldRecipe
            extends AbstractHeldRecipe<FluidizerFluidMixingRecipe>
            implements IFluidizerRecipe.Held<FluidizerFluidMixingRecipe> {

        public <Holder extends IRecipeHolder<FluidizerFluidMixingRecipe>>
        HeldRecipe(final FluidizerFluidMixingRecipe recipe, final Holder holder,
                   final IRecipeIngredientSource<FluidStack> fluid1Source, final IRecipeIngredientSource<FluidStack> fluid2Source,
                   final IRecipeResultTarget<FluidStackRecipeResult> outputTarget) {

            super(recipe, holder);
            this._fluid1Source = fluid1Source;
            this._fluid2Source = fluid2Source;
            this._outputTarget = outputTarget;
        }

        //region AbstractHeldRecipe

        /**
         * Called after the recipe was completely processed.
         */
        @Override
        public void onRecipeProcessed() {

            final FluidizerFluidMixingRecipe recipe = this.getRecipe();
            final FluidStack fluid1 = this._fluid1Source.getMatchFrom(recipe.ingredient1());
            final FluidStack fluid2 = this._fluid2Source.getMatchFrom(recipe.ingredient2());

            if (!fluid1.isEmpty() && !fluid2.isEmpty()) {

                this._fluid1Source.consumeIngredient(fluid1);
                this._fluid2Source.consumeIngredient(fluid2);
                this._outputTarget.setResult(recipe.result(), OperationMode.Execute);
            }
        }

        //endregion
        //region internals

        private final IRecipeIngredientSource<FluidStack> _fluid1Source;
        private final IRecipeIngredientSource<FluidStack> _fluid2Source;
        private final IRecipeResultTarget<FluidStackRecipeResult> _outputTarget;

        //endregion
    }

    //endregion
    //region internals

    private static long s_maxResultAmount = 0;

    //endregion
}
