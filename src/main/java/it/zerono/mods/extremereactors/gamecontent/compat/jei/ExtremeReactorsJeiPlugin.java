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
import it.zerono.mods.extremereactors.api.coolant.Coolant;
import it.zerono.mods.extremereactors.api.coolant.FluidsRegistry;
import it.zerono.mods.extremereactors.api.coolant.Vapor;
import it.zerono.mods.extremereactors.api.reactor.Reactant;
import it.zerono.mods.extremereactors.api.reactor.ReactantsRegistry;
import it.zerono.mods.extremereactors.api.reactor.ReactionsRegistry;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.compat.jei.fluidizer.FluidizerRecipeCategory;
import it.zerono.mods.extremereactors.gamecontent.compat.jei.reactor.*;
import it.zerono.mods.extremereactors.gamecontent.compat.jei.reprocessor.ReprocessorRecipeCategory;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerFluidMixingRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerSolidMixingRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerSolidRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.IFluidizerRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.recipe.ReprocessorRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@SuppressWarnings("unused")
@JeiPlugin
public class ExtremeReactorsJeiPlugin
        implements IModPlugin {

    public static final IIngredientType<Reactant> REACTANT_INGREDIENT_TYPE = () -> Reactant.class;
    public static final IIngredientType<Coolant> COOLANT_INGREDIENT_TYPE = () -> Coolant.class;
    public static final IIngredientType<Vapor> VAPOR_INGREDIENT_TYPE = () -> Vapor.class;

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
        registration.addRecipeCategories(new ReactionsRecipeCategory(guiHelper));
        registration.addRecipeCategories(this._reactantsSolidMappings = new ReactantFromSolidRecipeCategory(guiHelper));
        registration.addRecipeCategories(this._reactantsFluidMappings = new ReactantFromFluidRecipeCategory(guiHelper));
        registration.addRecipeCategories(this._vaporizations = TransitionsRecipeCategory.vaporization(guiHelper));
        registration.addRecipeCategories(this._condensations = TransitionsRecipeCategory.condensation(guiHelper));
        registration.addRecipeCategories(this._coolantsMappings = new CoolantFromFluidRecipeCategory(guiHelper));
        registration.addRecipeCategories(this._vaporsMappings = new VaporFromFluidRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(final IRecipeRegistration registration) {

        registration.addRecipes(Content.Recipes.REPROCESSOR_RECIPE_TYPE.getRecipes(), ReprocessorRecipe.ID);
        registration.addRecipes(Content.Recipes.FLUIDIZER_RECIPE_TYPE.getRecipes(r -> r instanceof FluidizerSolidRecipe), IFluidizerRecipe.Type.Solid.getRecipeId());
        registration.addRecipes(Content.Recipes.FLUIDIZER_RECIPE_TYPE.getRecipes(r -> r instanceof FluidizerSolidMixingRecipe), IFluidizerRecipe.Type.SolidMixing.getRecipeId());
        registration.addRecipes(Content.Recipes.FLUIDIZER_RECIPE_TYPE.getRecipes(r -> r instanceof FluidizerFluidMixingRecipe), IFluidizerRecipe.Type.FluidMixing.getRecipeId());
        registration.addRecipes(ReactionsRegistry.getReactions(), ReactionsRecipeCategory.ID);
        registration.addRecipes(this._reactantsSolidMappings.getReactants(), this._reactantsSolidMappings.getUid());
        registration.addRecipes(this._reactantsFluidMappings.getReactants(), this._reactantsFluidMappings.getUid());
        registration.addRecipes(this._vaporizations.getTransitions(), this._vaporizations.getUid());
        registration.addRecipes(this._condensations.getTransitions(), this._condensations.getUid());
        registration.addRecipes(this._coolantsMappings.getCoolants(), this._coolantsMappings.getUid());
        registration.addRecipes(this._vaporsMappings.getVapors(), this._vaporsMappings.getUid());
    }

    @Override
    public void registerIngredients(final IModIngredientRegistration registration) {

        registration.register(REACTANT_INGREDIENT_TYPE, ReactantsRegistry.getReactants(),
                new ReactantIngredientHelper(), new ReactantIngredientRenderer());

        registration.register(COOLANT_INGREDIENT_TYPE, FluidsRegistry.getCoolants(),
                new CoolantIngredientHelper(), new CoolantIngredientRenderer());

        registration.register(VAPOR_INGREDIENT_TYPE, FluidsRegistry.getVapors(),
                new VaporIngredientHelper(), new VaporIngredientRenderer());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {

        ItemStack ingredient;

        ingredient = Content.Blocks.REPROCESSOR_CONTROLLER.get().createItemStack();
        registration.addRecipeCatalyst(ingredient, ReprocessorRecipe.ID);

        ingredient = Content.Blocks.FLUIDIZER_CONTROLLER.get().createItemStack();
        registration.addRecipeCatalyst(ingredient, IFluidizerRecipe.Type.Solid.getRecipeId());
        registration.addRecipeCatalyst(ingredient, IFluidizerRecipe.Type.SolidMixing.getRecipeId());
        registration.addRecipeCatalyst(ingredient, IFluidizerRecipe.Type.FluidMixing.getRecipeId());

        ingredient = Content.Blocks.REACTOR_CONTROLLER_BASIC.get().createItemStack();
        registration.addRecipeCatalyst(ingredient, ReactionsRecipeCategory.ID);

        ingredient = Content.Blocks.REACTOR_CONTROLLER_REINFORCED.get().createItemStack();
        registration.addRecipeCatalyst(ingredient, ReactionsRecipeCategory.ID);
        registration.addRecipeCatalyst(ingredient, this._vaporizations.getUid());
        registration.addRecipeCatalyst(ingredient, this._condensations.getUid());

        ingredient = Content.Blocks.REACTOR_SOLID_ACCESSPORT_BASIC.get().createItemStack();
        registration.addRecipeCatalyst(ingredient, this._reactantsSolidMappings.getUid());

        ingredient = Content.Blocks.REACTOR_SOLID_ACCESSPORT_REINFORCED.get().createItemStack();
        registration.addRecipeCatalyst(ingredient, this._reactantsSolidMappings.getUid());

        ingredient = Content.Blocks.REACTOR_FLUID_ACCESSPORT_REINFORCED.get().createItemStack();
        registration.addRecipeCatalyst(ingredient, this._reactantsFluidMappings.getUid());

        ingredient = Content.Blocks.REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED.get().createItemStack();
        registration.addRecipeCatalyst(ingredient, this._vaporsMappings.getUid());
        registration.addRecipeCatalyst(ingredient, this._coolantsMappings.getUid());

        ingredient = Content.Blocks.REACTOR_FLUIDTPORT_FORGE_ACTIVE_REINFORCED.get().createItemStack();
        registration.addRecipeCatalyst(ingredient, this._vaporsMappings.getUid());
        registration.addRecipeCatalyst(ingredient, this._coolantsMappings.getUid());

        ingredient = Content.Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC.get().createItemStack();
        registration.addRecipeCatalyst(ingredient, this._vaporsMappings.getUid());
        registration.addRecipeCatalyst(ingredient, this._coolantsMappings.getUid());

        ingredient = Content.Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC.get().createItemStack();
        registration.addRecipeCatalyst(ingredient, this._vaporsMappings.getUid());
        registration.addRecipeCatalyst(ingredient, this._coolantsMappings.getUid());

        ingredient = Content.Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED.get().createItemStack();
        registration.addRecipeCatalyst(ingredient, this._vaporsMappings.getUid());
        registration.addRecipeCatalyst(ingredient, this._coolantsMappings.getUid());

        ingredient = Content.Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED.get().createItemStack();
        registration.addRecipeCatalyst(ingredient, this._vaporsMappings.getUid());
        registration.addRecipeCatalyst(ingredient, this._coolantsMappings.getUid());
    }

    //endregion
    //region internals

    private static final ResourceLocation s_id = ExtremeReactors.newID("jeiplugin");

    private ReactantFromSolidRecipeCategory _reactantsSolidMappings;
    private ReactantFromFluidRecipeCategory _reactantsFluidMappings;
    private TransitionsRecipeCategory<Coolant, Vapor, TransitionsRecipeCategory.VaporizationTransition> _vaporizations;
    private TransitionsRecipeCategory<Vapor, Coolant, TransitionsRecipeCategory.CondensationTransition> _condensations;
    private CoolantFromFluidRecipeCategory _coolantsMappings;
    private VaporFromFluidRecipeCategory _vaporsMappings;

    //endregion
}
