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

import it.zerono.mods.extremereactors.CommonLocations;
import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.api.coolant.Coolant;
import it.zerono.mods.extremereactors.api.coolant.FluidsRegistry;
import it.zerono.mods.extremereactors.api.coolant.Vapor;
import it.zerono.mods.extremereactors.api.reactor.Reactant;
import it.zerono.mods.extremereactors.api.reactor.ReactantsRegistry;
import it.zerono.mods.extremereactors.api.reactor.Reaction;
import it.zerono.mods.extremereactors.api.reactor.ReactionsRegistry;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.compat.jei.fluidizer.FluidizerRecipeCategory;
import it.zerono.mods.extremereactors.gamecontent.compat.jei.reactor.*;
import it.zerono.mods.extremereactors.gamecontent.compat.jei.reprocessor.ReprocessorRecipeCategory;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerFluidMixingRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerSolidMixingRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerSolidRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.recipe.ReprocessorRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("unused")
@JeiPlugin
public class ExtremeReactorsJeiPlugin
        implements IModPlugin {

    public static final RecipeType<ReprocessorRecipe> REPROCESSOR_JEI_RECIPE_TYPE = RecipeType.create(ExtremeReactors.MOD_ID, ReprocessorRecipe.NAME, ReprocessorRecipe.class);
    public static final RecipeType<FluidizerSolidRecipe> FLUIDIZER_SOLID_JEI_RECIPE_TYPE = RecipeType.create(ExtremeReactors.MOD_ID, "fluidizer_solid", FluidizerSolidRecipe.class);
    public static final RecipeType<FluidizerSolidMixingRecipe> FLUIDIZER_SOLIDMIXING_JEI_RECIPE_TYPE = RecipeType.create(ExtremeReactors.MOD_ID, "fluidizer_solidmixing", FluidizerSolidMixingRecipe.class);
    public static final RecipeType<FluidizerFluidMixingRecipe> FLUIDIZER_FLUIDMIXING_JEI_RECIPE_TYPE = RecipeType.create(ExtremeReactors.MOD_ID, "fluidizer_fluidmixing", FluidizerFluidMixingRecipe.class);
    public static final RecipeType<Reaction> REACTION_JEI_RECIPE_TYPE = RecipeType.create(ExtremeReactors.MOD_ID, "reactantsreactions", Reaction.class);

    public static final IIngredientType<Reactant> REACTANT_INGREDIENT_TYPE = () -> Reactant.class;
    public static final IIngredientType<Coolant> COOLANT_INGREDIENT_TYPE = () -> Coolant.class;
    public static final IIngredientType<Vapor> VAPOR_INGREDIENT_TYPE = () -> Vapor.class;

    public static IDrawableStatic defaultMappingDrawable(IGuiHelper guiHelper) {

        final var id = CommonLocations.TEXTURES_GUI_JEI.buildWithSuffix("mapping.png");

        return guiHelper.drawableBuilder(id, 0, 0, 144, 56)
                .setTextureSize(144, 56)
                .build();
    }

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

        registration.addRecipes(REPROCESSOR_JEI_RECIPE_TYPE, Content.Recipes.REPROCESSOR_RECIPE_TYPE.get().getRecipes());
        registration.addRecipes(FLUIDIZER_SOLID_JEI_RECIPE_TYPE, Content.Recipes.FLUIDIZER_RECIPE_TYPE.get().getRecipes(r -> r instanceof FluidizerSolidRecipe, r -> (FluidizerSolidRecipe)r));
        registration.addRecipes(FLUIDIZER_SOLIDMIXING_JEI_RECIPE_TYPE, Content.Recipes.FLUIDIZER_RECIPE_TYPE.get().getRecipes(r -> r instanceof FluidizerSolidMixingRecipe, r -> (FluidizerSolidMixingRecipe)r));
        registration.addRecipes(FLUIDIZER_FLUIDMIXING_JEI_RECIPE_TYPE, Content.Recipes.FLUIDIZER_RECIPE_TYPE.get().getRecipes(r -> r instanceof FluidizerFluidMixingRecipe, r -> (FluidizerFluidMixingRecipe)r));
        registration.addRecipes(REACTION_JEI_RECIPE_TYPE, ReactionsRegistry.getReactions());
        registration.addRecipes(this._reactantsSolidMappings.getRecipeType(), this._reactantsSolidMappings.getReactants());
        registration.addRecipes(this._reactantsFluidMappings.getRecipeType(), this._reactantsFluidMappings.getReactants());
        registration.addRecipes(this._vaporizations.getRecipeType(), this._vaporizations.getTransitions());
        registration.addRecipes(this._condensations.getRecipeType(), this._condensations.getTransitions());
        registration.addRecipes(this._coolantsMappings.getRecipeType(), this._coolantsMappings.getCoolants());
        registration.addRecipes(this._vaporsMappings.getRecipeType(), this._vaporsMappings.getVapors());
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

        ingredient = new ItemStack(Content.Blocks.REPROCESSOR_CONTROLLER.get());
        registration.addRecipeCatalyst(ingredient, REPROCESSOR_JEI_RECIPE_TYPE);

        ingredient = new ItemStack(Content.Blocks.FLUIDIZER_CONTROLLER.get());
        registration.addRecipeCatalyst(ingredient, FLUIDIZER_SOLID_JEI_RECIPE_TYPE);
        registration.addRecipeCatalyst(ingredient, FLUIDIZER_SOLIDMIXING_JEI_RECIPE_TYPE);
        registration.addRecipeCatalyst(ingredient, FLUIDIZER_FLUIDMIXING_JEI_RECIPE_TYPE);

        ingredient = new ItemStack(Content.Blocks.REACTOR_CONTROLLER_BASIC.get());
        registration.addRecipeCatalyst(ingredient, REACTION_JEI_RECIPE_TYPE);

        ingredient = new ItemStack(Content.Blocks.REACTOR_CONTROLLER_REINFORCED.get());
        registration.addRecipeCatalyst(ingredient, REACTION_JEI_RECIPE_TYPE);
        registration.addRecipeCatalyst(ingredient, this._vaporizations.getRecipeType());
        registration.addRecipeCatalyst(ingredient, this._condensations.getRecipeType());

        ingredient = new ItemStack(Content.Blocks.REACTOR_SOLID_ACCESSPORT_BASIC.get());
        registration.addRecipeCatalyst(ingredient, this._reactantsSolidMappings.getRecipeType());

        ingredient = new ItemStack(Content.Blocks.REACTOR_SOLID_ACCESSPORT_REINFORCED.get());
        registration.addRecipeCatalyst(ingredient, this._reactantsSolidMappings.getRecipeType());

        ingredient = new ItemStack(Content.Blocks.REACTOR_FLUID_ACCESSPORT_REINFORCED.get());
        registration.addRecipeCatalyst(ingredient, this._reactantsFluidMappings.getRecipeType());

        ingredient = new ItemStack(Content.Blocks.REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED.get());
        registration.addRecipeCatalyst(ingredient, this._vaporsMappings.getRecipeType());
        registration.addRecipeCatalyst(ingredient, this._coolantsMappings.getRecipeType());

        ingredient = new ItemStack(Content.Blocks.REACTOR_FLUIDTPORT_FORGE_ACTIVE_REINFORCED.get());
        registration.addRecipeCatalyst(ingredient, this._vaporsMappings.getRecipeType());
        registration.addRecipeCatalyst(ingredient, this._coolantsMappings.getRecipeType());

        ingredient = new ItemStack(Content.Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC.get());
        registration.addRecipeCatalyst(ingredient, this._vaporsMappings.getRecipeType());
        registration.addRecipeCatalyst(ingredient, this._coolantsMappings.getRecipeType());

        ingredient = new ItemStack(Content.Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC.get());
        registration.addRecipeCatalyst(ingredient, this._vaporsMappings.getRecipeType());
        registration.addRecipeCatalyst(ingredient, this._coolantsMappings.getRecipeType());

        ingredient = new ItemStack(Content.Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED.get());
        registration.addRecipeCatalyst(ingredient, this._vaporsMappings.getRecipeType());
        registration.addRecipeCatalyst(ingredient, this._coolantsMappings.getRecipeType());

        ingredient = new ItemStack(Content.Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED.get());
        registration.addRecipeCatalyst(ingredient, this._vaporsMappings.getRecipeType());
        registration.addRecipeCatalyst(ingredient, this._coolantsMappings.getRecipeType());
    }

    //endregion
    //region internals

    private static final ResourceLocation s_id = ExtremeReactors.ROOT_LOCATION.buildWithSuffix("jeiplugin");

    private ReactantFromSolidRecipeCategory _reactantsSolidMappings;
    private ReactantFromFluidRecipeCategory _reactantsFluidMappings;
    private TransitionsRecipeCategory<Coolant, Vapor, TransitionsRecipeCategory.VaporizationTransition> _vaporizations;
    private TransitionsRecipeCategory<Vapor, Coolant, TransitionsRecipeCategory.CondensationTransition> _condensations;
    private CoolantFromFluidRecipeCategory _coolantsMappings;
    private VaporFromFluidRecipeCategory _vaporsMappings;

    //endregion
}
