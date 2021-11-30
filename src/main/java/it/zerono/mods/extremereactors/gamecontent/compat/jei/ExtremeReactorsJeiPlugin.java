/*
 *
 * JeiPlugin.java
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

package it.zerono.mods.extremereactors.gamecontent.compat.jei;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.compat.jei.fluidizer.FluidizerRecipeCategory;
import it.zerono.mods.extremereactors.gamecontent.compat.jei.reprocessor.ReprocessorRecipeCategory;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerFluidMixingRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerSolidMixingRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerSolidRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.IFluidizerRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.recipe.ReprocessorRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;


@JeiPlugin
public class ExtremeReactorsJeiPlugin
        implements IModPlugin {

    //region IModPlugin

    @Override
    public ResourceLocation getPluginUid() {
        return s_id;
    }

    @Override
    public void registerCategories(final IRecipeCategoryRegistration registration) {

        final IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(new ReprocessorRecipeCategory(guiHelper));
        registration.addRecipeCategories(FluidizerRecipeCategory.solid(guiHelper));
        registration.addRecipeCategories(FluidizerRecipeCategory.solidMixing(guiHelper));
        registration.addRecipeCategories(FluidizerRecipeCategory.fluidMixing(guiHelper));
    }

    @Override
    public void registerRecipes(final IRecipeRegistration registration) {

        registration.addRecipes(Content.Recipes.REPROCESSOR_RECIPE_TYPE.getRecipes(), ReprocessorRecipe.ID);
        registration.addRecipes(Content.Recipes.FLUIDIZER_RECIPE_TYPE.getRecipes(r -> r instanceof FluidizerSolidRecipe), IFluidizerRecipe.Type.Solid.getRecipeId());
        registration.addRecipes(Content.Recipes.FLUIDIZER_RECIPE_TYPE.getRecipes(r -> r instanceof FluidizerSolidMixingRecipe), IFluidizerRecipe.Type.SolidMixing.getRecipeId());
        registration.addRecipes(Content.Recipes.FLUIDIZER_RECIPE_TYPE.getRecipes(r -> r instanceof FluidizerFluidMixingRecipe), IFluidizerRecipe.Type.FluidMixing.getRecipeId());
    }

    //endregion
    //region internals

    private static final ResourceLocation s_id = ExtremeReactors.newID("jeiplugin");

    //endregion
}
