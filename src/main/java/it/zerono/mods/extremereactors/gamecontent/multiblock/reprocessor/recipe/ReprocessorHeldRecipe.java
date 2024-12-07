/*
 *
 * ReprocessorHeldRecipe.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.recipe;

import it.zerono.mods.zerocore.lib.data.stack.OperationMode;
import it.zerono.mods.zerocore.lib.recipe.holder.AbstractHeldRecipe;
import it.zerono.mods.zerocore.lib.recipe.holder.IRecipeHolder;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredientSource;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResultTarget;
import it.zerono.mods.zerocore.lib.recipe.result.ItemStackRecipeResult;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class ReprocessorHeldRecipe
        extends AbstractHeldRecipe<ReprocessorRecipe> {

    public <Holder extends IRecipeHolder<ReprocessorRecipe>>
    ReprocessorHeldRecipe(final ReprocessorRecipe recipe, final Holder holder,
                          final IRecipeIngredientSource<ItemStack> wasteSource,
                          final IRecipeIngredientSource<FluidStack> fluidSource,
                          final IRecipeResultTarget<ItemStackRecipeResult> outputTarget) {

        super(recipe, holder);
        this._outputTarget = outputTarget;
        this._wasteIngredientSource = wasteSource;
        this._fluidIngredientSource = fluidSource;
    }

    //region AbstractHeldRecipe

    /**
     * Called after the recipe was completely processed.
     */
    @Override
    public void onRecipeProcessed() {

        final ReprocessorRecipe recipe = this.getRecipe();
        final ItemStack waste = this._wasteIngredientSource.getMatchFrom(recipe.ingredient1());
        final FluidStack fluid = this._fluidIngredientSource.getMatchFrom(recipe.ingredient2());

        if (!waste.isEmpty() && !fluid.isEmpty()) {

            this._wasteIngredientSource.consumeIngredient(waste);
            this._fluidIngredientSource.consumeIngredient(fluid);
            this._outputTarget.setResult(recipe.result(), OperationMode.Execute);
        }
    }

    //endregion
    //region internals

    private final IRecipeIngredientSource<ItemStack> _wasteIngredientSource;
    private final IRecipeIngredientSource<FluidStack> _fluidIngredientSource;
    private final IRecipeResultTarget<ItemStackRecipeResult> _outputTarget;

    //endregion
}
