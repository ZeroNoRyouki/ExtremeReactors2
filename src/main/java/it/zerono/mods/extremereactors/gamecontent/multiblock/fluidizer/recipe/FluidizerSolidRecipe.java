/*
 *
 * FluidizerSolidRecipe.java
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

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.zerocore.lib.data.stack.OperationMode;
import it.zerono.mods.zerocore.lib.recipe.AbstractOneToOneRecipe;
import it.zerono.mods.zerocore.lib.recipe.ModRecipe;
import it.zerono.mods.zerocore.lib.recipe.holder.AbstractHeldRecipe;
import it.zerono.mods.zerocore.lib.recipe.holder.IRecipeHolder;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredientSource;
import it.zerono.mods.zerocore.lib.recipe.ingredient.ItemStackRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.FluidStackRecipeResult;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResultTarget;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidizerSolidRecipe
        extends AbstractOneToOneRecipe<ItemStack, FluidStack, ItemStackRecipeIngredient, FluidStackRecipeResult>
        implements IFluidizerRecipe {

    public FluidizerSolidRecipe(ItemStackRecipeIngredient ingredient, FluidStackRecipeResult result) {

        super(ingredient, result);
        s_maxResultAmount = Math.max(s_maxResultAmount, result.getAmount());
    }

    public static boolean lookup(final ModRecipe recipe, final IRecipeIngredientSource<ItemStack> source) {
        return recipe instanceof FluidizerSolidRecipe && ((FluidizerSolidRecipe)recipe).test(source.getIngredient());
    }

    public static long getMaxResultAmount() {
        return s_maxResultAmount;
    }

    @Override
    public Type getRecipeType() {
        return Type.Solid;
    }

    public boolean match(final ItemStack stack) {
        return this.getIngredient().test(stack);
    }

    public boolean matchIgnoreAmount(final ItemStack stack) {
        return this.getIngredient().testIgnoreAmount(stack);
    }

    public static RecipeSerializer<FluidizerSolidRecipe> createSerializer() {
        return AbstractOneToOneRecipe.createSerializer(
                "ingredient", ItemStackRecipeIngredient.CODECS,
                "result", FluidStackRecipeResult.CODECS,
                FluidizerSolidRecipe::new);
    }

    //region IFluidizerRecipe

    @Override
    public int getEnergyUsageMultiplier() {
        return this.getEnergyUsageMultiplier(this.getResult().getResult());
    }

    //endregion
    //region AbstractOneToOneRecipe

    @Override
    public RecipeSerializer<FluidizerSolidRecipe> getSerializer() {
        return Content.Recipes.FLUIDIZER_SOLID_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Content.Recipes.FLUIDIZER_RECIPE_TYPE.get();
    }

    //endregion
    //region held recipe

    public static class HeldRecipe
            extends AbstractHeldRecipe<FluidizerSolidRecipe>
            implements IFluidizerRecipe.Held<FluidizerSolidRecipe> {

        public <Holder extends IRecipeHolder<FluidizerSolidRecipe>>
        HeldRecipe(final FluidizerSolidRecipe recipe, final Holder holder,
                   final IRecipeIngredientSource<ItemStack> itemSource,
                   final IRecipeResultTarget<FluidStackRecipeResult> outputTarget) {

            super(recipe, holder);
            this._itemSource = itemSource;
            this._outputTarget = outputTarget;
        }

        //region AbstractHeldRecipe

        /**
         * Called after the recipe was completely processed.
         */
        @Override
        public void onRecipeProcessed() {

            final FluidizerSolidRecipe recipe = this.getRecipe();
            final ItemStack item = this._itemSource.getMatchFrom(recipe.getIngredient());

            if (!item.isEmpty()) {

                this._itemSource.consumeIngredient(item);
                this._outputTarget.setResult(recipe.getResult(), OperationMode.Execute);
            }
        }

        //endregion
        //region internals

        private final IRecipeIngredientSource<ItemStack> _itemSource;
        private final IRecipeResultTarget<FluidStackRecipeResult> _outputTarget;

        //endregion
    }

    //endregion
    //region internals

    private static long s_maxResultAmount = 0;

    //endregion
}
