/*
 *
 * CoolantRecipeCategory.java
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

package it.zerono.mods.extremereactors.gamecontent.compat.jei.reactor;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.api.IMapping;
import it.zerono.mods.extremereactors.api.coolant.Coolant;
import it.zerono.mods.extremereactors.api.coolant.TransitionsRegistry;
import it.zerono.mods.extremereactors.api.coolant.Vapor;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.compat.jei.ExtremeReactorsJeiPlugin;
import it.zerono.mods.zerocore.lib.compat.jei.AbstractModRecipeCategory;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TransitionsRecipeCategory<Input, Output, Recipe extends TransitionsRecipeCategory.AbstractTransition<Input, Output>>
        extends AbstractModRecipeCategory<Recipe> {

    public static TransitionsRecipeCategory<Coolant, Vapor, VaporizationTransition> vaporization(final IGuiHelper guiHelper) {
        return new TransitionsRecipeCategory<>(ExtremeReactors.newID("jei_vaporizations"), VaporizationTransition.class,
                "compat.bigreactors.jei.vaporization.recipecategory.title",
                Content.Blocks.REACTOR_FLUID_ACCESSPORT_REINFORCED.get().createItemStack(), guiHelper,
                TransitionsRegistry.getVaporizations().values().stream()
                        .map(VaporizationTransition::new)
                        .collect(Collectors.toList()));
    }

    public static TransitionsRecipeCategory<Vapor, Coolant, CondensationTransition> condensation(final IGuiHelper guiHelper) {
        return new TransitionsRecipeCategory<>(ExtremeReactors.newID("jei_condensations"), CondensationTransition.class,
                "compat.bigreactors.jei.condensation.recipecategory.title",
                Content.Blocks.REACTOR_FLUID_ACCESSPORT_REINFORCED.get().createItemStack(), guiHelper,
                TransitionsRegistry.getCondensations().values().stream()
                        .map(CondensationTransition::new)
                        .collect(Collectors.toList()));
    }

    public List<Recipe> getTransitions() {
        return this._transitions;
    }

    //region AbstractModRecipeCategory<T>

    @Override
    public Class<? extends Recipe> getRecipeClass() {
        return this._recipeClass;
    }

    @Override
    public void setIngredients(final Recipe recipe, final IIngredients ingredients) {
        recipe.setIngredients(ingredients);
    }

    @Override
    public void setRecipe(final IRecipeLayout recipeLayout, final Recipe recipe, final IIngredients ingredients) {
        recipe.setRecipe(recipeLayout, ingredients);
    }

    //endregion
    //region recipes

    public static abstract class AbstractTransition<Input, Output> {

        protected AbstractTransition(final IMapping<Input, Output> mapping) {
            this._mapping = Objects.requireNonNull(mapping);
        }

        public Input getInput() {
            return this._mapping.getSource();
        }

        public Output getOutput() {
            return this._mapping.getProduct();
        }

        protected abstract void setIngredients(IIngredients ingredients);
        protected abstract void setRecipe(IRecipeLayout recipeLayout, IIngredients ingredients);

        //region internals

        private final IMapping<Input, Output> _mapping;

        //endregion
    }

    public static class VaporizationTransition
            extends AbstractTransition<Coolant, Vapor> {

        public VaporizationTransition(final IMapping<Coolant, Vapor> mapping) {
            super(mapping);
        }

        @Override
        protected void setIngredients(final IIngredients ingredients) {

            ingredients.setInput(ExtremeReactorsJeiPlugin.COOLANT_INGREDIENT_TYPE, this.getInput());
            ingredients.setOutput(ExtremeReactorsJeiPlugin.VAPOR_INGREDIENT_TYPE, this.getOutput());
        }

        @Override
        protected void setRecipe(final IRecipeLayout recipeLayout, final IIngredients ingredients) {

            final IGuiIngredientGroup<Coolant> input = recipeLayout.getIngredientsGroup(ExtremeReactorsJeiPlugin.COOLANT_INGREDIENT_TYPE);
            final IGuiIngredientGroup<Vapor> output = recipeLayout.getIngredientsGroup(ExtremeReactorsJeiPlugin.VAPOR_INGREDIENT_TYPE);

            input.init(0, true, 33, 20);
            input.set(0, this.getInput());
            output.init(1, false, 95, 20);
            output.set(1, this.getOutput());
        }
    }

    public static class CondensationTransition
            extends AbstractTransition<Vapor, Coolant> {

        public CondensationTransition(final IMapping<Vapor, Coolant> mapping) {
            super(mapping);
        }

        @Override
        protected void setIngredients(final IIngredients ingredients) {

            ingredients.setInput(ExtremeReactorsJeiPlugin.VAPOR_INGREDIENT_TYPE, this.getInput());
            ingredients.setOutput(ExtremeReactorsJeiPlugin.COOLANT_INGREDIENT_TYPE, this.getOutput());
        }

        @Override
        protected void setRecipe(final IRecipeLayout recipeLayout, final IIngredients ingredients) {

            final IGuiIngredientGroup<Vapor> input = recipeLayout.getIngredientsGroup(ExtremeReactorsJeiPlugin.VAPOR_INGREDIENT_TYPE);
            final IGuiIngredientGroup<Coolant> output = recipeLayout.getIngredientsGroup(ExtremeReactorsJeiPlugin.COOLANT_INGREDIENT_TYPE);

            input.init(0, true, 33, 20);
            input.set(0, this.getInput());
            output.init(1, false, 95, 20);
            output.set(1, this.getOutput());
        }
    }

    //endregion
    //region internals

    protected TransitionsRecipeCategory(final ResourceLocation id, final Class<Recipe> recipeClass,
                                        final String titleTranslationKey, final ItemStack icon,
                                        final IGuiHelper guiHelper, final List<Recipe> transitions) {

        super(id, new TranslatableComponent(titleTranslationKey), icon, guiHelper,
                guiHelper.drawableBuilder(ExtremeReactors.newID("textures/gui/jei/mapping.png"), 0, 0, 144, 56)
                        .setTextureSize(144, 56)
                        .build());

        this._recipeClass = recipeClass;
        this._transitions = Objects.requireNonNull(transitions);
    }

    private final Class<Recipe> _recipeClass;
    private final List<Recipe> _transitions;

    //endregion
}
