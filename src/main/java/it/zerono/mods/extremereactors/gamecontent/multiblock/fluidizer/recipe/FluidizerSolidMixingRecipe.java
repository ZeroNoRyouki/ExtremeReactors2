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

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.zerocore.lib.data.stack.OperationMode;
import it.zerono.mods.zerocore.lib.datagen.provider.recipe.TwoToOneRecipeBuilder;
import it.zerono.mods.zerocore.lib.recipe.AbstractTwoToOneRecipe;
import it.zerono.mods.zerocore.lib.recipe.ModRecipe;
import it.zerono.mods.zerocore.lib.recipe.holder.AbstractHeldRecipe;
import it.zerono.mods.zerocore.lib.recipe.holder.IRecipeHolder;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredientSource;
import it.zerono.mods.zerocore.lib.recipe.ingredient.ItemStackRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.FluidStackRecipeResult;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResult;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResultTarget;
import it.zerono.mods.zerocore.lib.recipe.serializer.TwoToOneRecipeSerializer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.IntFunction;

public class FluidizerSolidMixingRecipe
        extends AbstractTwoToOneRecipe<ItemStack, ItemStack, FluidStack, ItemStackRecipeIngredient, ItemStackRecipeIngredient, FluidStackRecipeResult>
        implements IFluidizerRecipe {

    protected FluidizerSolidMixingRecipe(final ResourceLocation id, final ItemStackRecipeIngredient ingredient1,
                                         final ItemStackRecipeIngredient ingredient2, final FluidStackRecipeResult result) {

        super(id, ingredient1, ingredient2, result, JSON_LABELS_SUPPLIER);
        s_maxResultAmount = Math.max(s_maxResultAmount, result.getAmount());
    }

    public static boolean lookup(final ModRecipe recipe, final IRecipeIngredientSource<ItemStack> source1,
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
        return this.getIngredient1().test(stack) || this.getIngredient2().test(stack);
    }

    /**
     * Check if the provided stacks match both the recipe ingredients
     */
    public boolean match(final ItemStack stack1, final ItemStack stack2) {

        final ItemStackRecipeIngredient ingredient1 = this.getIngredient1();
        final ItemStackRecipeIngredient ingredient2 = this.getIngredient2();

        return ingredient1.test(stack1) && ingredient2.test(stack2);
    }

    /**
     * Check if the provided stack match one of the recipe ingredients ignoring the stack size
     */
    public boolean matchIgnoreAmount(final ItemStack stack) {
        return this.getIngredient1().testIgnoreAmount(stack) || this.getIngredient2().testIgnoreAmount(stack);
    }

    public static IRecipeSerializer<FluidizerSolidMixingRecipe> serializer() {
        return new TwoToOneRecipeSerializer<>(FluidizerSolidMixingRecipe::new,
                ItemStackRecipeIngredient::from, ItemStackRecipeIngredient::from,
                ItemStackRecipeIngredient::from, ItemStackRecipeIngredient::from,
                FluidStackRecipeResult::from, FluidStackRecipeResult::from, JSON_LABELS_SUPPLIER);
    }

    public static TwoToOneRecipeBuilder<ItemStack, ItemStack, FluidStack> builder(final IRecipeIngredient<ItemStack> ingredient1,
                                                                                  final IRecipeIngredient<ItemStack> ingredient2,
                                                                                  final IRecipeResult<FluidStack> result) {
        return new TwoToOneRecipeBuilder<>(Type.SolidMixing.getRecipeId(), ingredient1, ingredient2, result, JSON_LABELS_SUPPLIER);
    }

    //region IFluidizerRecipe

    @Override
    public int getEnergyUsageMultiplier() {
        return this.getEnergyUsageMultiplier(this.getResult().getResult());
    }

    //endregion
    //region AbstractTwoToOneRecipe

    @Override
    public IRecipeSerializer<FluidizerSolidMixingRecipe> getSerializer() {
        return Content.Recipes.FLUIDIZER_SOLIDMIXING_RECIPE_SERIALIZER.get();
    }

    @Override
    public IRecipeType<?> getType() {
        return Content.Recipes.FLUIDIZER_RECIPE_TYPE;
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
            final ItemStack item1 = this._item1Source.getMatchFrom(recipe.getIngredient1());
            final ItemStack item2 = this._item2Source.getMatchFrom(recipe.getIngredient2());

            if (!item1.isEmpty() && !item2.isEmpty()) {

                this._item1Source.consumeIngredient(item1);
                this._item2Source.consumeIngredient(item2);
                this._outputTarget.setResult(recipe.getResult(), OperationMode.Execute);
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

    private static final String[] LABELS = {"ingredient1", "ingredient2"};
    private static final IntFunction<String> JSON_LABELS_SUPPLIER = n -> LABELS[n];
    private static long s_maxResultAmount = 0;

    //endregion
}
