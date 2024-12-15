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

import com.google.common.base.Preconditions;
import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.zerocore.lib.recipe.IModRecipeType;
import it.zerono.mods.zerocore.lib.recipe.ITwoToOneModRecipe;
import it.zerono.mods.zerocore.lib.recipe.ingredient.FluidRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.ingredient.ItemRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.ItemStackRecipeResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public record ReprocessorRecipe(ItemRecipeIngredient ingredient1, FluidRecipeIngredient ingredient2,
                                ItemStackRecipeResult result)
        implements ITwoToOneModRecipe<ItemStack, FluidStack, ItemStack,
                ItemRecipeIngredient, FluidRecipeIngredient, ItemStackRecipeResult> {

    public static final String NAME = "reprocessor";
    public static final ResourceLocation ID = ExtremeReactors.ROOT_LOCATION.buildWithSuffix(NAME);

    public ReprocessorRecipe {

        Preconditions.checkArgument(!ingredient1.isEmpty(), "Ingredient 1 must not be empty");
        Preconditions.checkArgument(!ingredient2.isEmpty(), "Ingredient 2 must not be empty");
        Preconditions.checkArgument(!result.isEmpty(), "Result must not be empty");
    }

    public boolean matchIgnoreAmount(final ItemStack stack) {
        return this.ingredient1().testIgnoreAmount(stack);
    }

    public boolean matchIgnoreAmount(final FluidStack stack) {
        return this.ingredient2().testIgnoreAmount(stack);
    }

    public static RecipeSerializer<ReprocessorRecipe> createSerializer() {
        return ITwoToOneModRecipe.createTwoToOneSerializer(
                "waste", ItemRecipeIngredient.CODECS,
                "fluid", FluidRecipeIngredient.CODECS,
                "result", ItemStackRecipeResult.CODECS,
                ReprocessorRecipe::new);
    }

    //region ITwoToOneModRecipe<ItemStack, FluidStack, ItemStack, ItemRecipeIngredient, FluidRecipeIngredient, ItemStackRecipeResult>

    @Override
    public RecipeSerializer<ReprocessorRecipe> getSerializer() {
        return Content.Recipes.REPROCESSOR_RECIPE_SERIALIZER.get();
    }

    @Override
    public IModRecipeType<ReprocessorRecipe> getType() {
        return Content.Recipes.REPROCESSOR_RECIPE_TYPE.get();
    }

    @Override
    public Supplier<? extends @NotNull Item> getRecipeIcon() {
        return Content.Items.REPROCESSOR_WASTEINJECTOR;
    }

    //endregion
}
