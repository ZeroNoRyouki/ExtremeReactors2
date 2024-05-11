/*
 *
 * ReprocessorRecipe.java
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

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.zerocore.lib.recipe.AbstractTwoToOneRecipe;
import it.zerono.mods.zerocore.lib.recipe.ingredient.FluidStackRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.ingredient.ItemStackRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.ItemStackRecipeResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.fluids.FluidStack;

public class ReprocessorRecipe
        extends AbstractTwoToOneRecipe<ItemStack, FluidStack, ItemStack,
                    ItemStackRecipeIngredient, FluidStackRecipeIngredient, ItemStackRecipeResult> {

    public static final String NAME = "reprocessor";
    public static final ResourceLocation ID = ExtremeReactors.ROOT_LOCATION.buildWithSuffix(NAME);

    public ReprocessorRecipe(ItemStackRecipeIngredient ingot, FluidStackRecipeIngredient fluid,
                             ItemStackRecipeResult result) {
        super(ingot, fluid, result);
    }

    public boolean match(final ItemStack stack) {
        return this.getIngredient1().test(stack);
    }

    public boolean matchIgnoreAmount(final ItemStack stack) {
        return this.getIngredient1().testIgnoreAmount(stack);
    }

    public boolean matchIgnoreAmount(final FluidStack stack) {
        return this.getIngredient2().testIgnoreAmount(stack);
    }

    public static RecipeSerializer<ReprocessorRecipe> createSerializer() {
        return AbstractTwoToOneRecipe.createSerializer(
                "waste", ItemStackRecipeIngredient.CODEC, ItemStackRecipeIngredient::from,
                "fluid", FluidStackRecipeIngredient.CODEC, FluidStackRecipeIngredient::from,
                "result", ItemStackRecipeResult.CODEC, ItemStackRecipeResult::from,
                ReprocessorRecipe::new);
    }

    //region AbstractTwoToOneRecipe

    @Override
    public RecipeSerializer<ReprocessorRecipe> getSerializer() {
        return Content.Recipes.REPROCESSOR_RECIPE_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return Content.Recipes.REPROCESSOR_RECIPE_TYPE.get();
    }

    //endregion
}
