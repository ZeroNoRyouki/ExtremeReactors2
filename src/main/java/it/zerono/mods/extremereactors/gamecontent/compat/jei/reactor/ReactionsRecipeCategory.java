package it.zerono.mods.extremereactors.gamecontent.compat.jei.reactor;
/*
 * ReactionsRecipeCategory
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * Do not remove or edit this header
 *
 */

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.api.reactor.Reactant;
import it.zerono.mods.extremereactors.api.reactor.Reaction;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.compat.jei.ExtremeReactorsJeiPlugin;
import it.zerono.mods.zerocore.lib.compat.jei.AbstractModRecipeCategory;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class ReactionsRecipeCategory
        extends AbstractModRecipeCategory<Reaction> {

    public static ResourceLocation ID = ExtremeReactors.newID("jei_reactantsreactions");

    public ReactionsRecipeCategory(final IGuiHelper guiHelper) {
        super(ID, new TranslationTextComponent("compat.bigreactors.jei.reactantsreactions.recipecategory.title"),
                Content.Blocks.REACTOR_FUELROD_BASIC.get().createItemStack(), guiHelper,
                guiHelper.drawableBuilder(ExtremeReactors.newID("textures/gui/jei/mapping.png"), 0, 0, 144, 56)
                        .setTextureSize(144, 56)
                        .build());
    }

    //region AbstractModRecipeCategory

    @Override
    public Class<? extends Reaction> getRecipeClass() {
        return Reaction.class;
    }

    @Override
    public void setIngredients(final Reaction recipe, final IIngredients ingredients) {

        ingredients.setInput(ExtremeReactorsJeiPlugin.REACTANT_INGREDIENT_TYPE, recipe.getSource());
        ingredients.setOutput(ExtremeReactorsJeiPlugin.REACTANT_INGREDIENT_TYPE, recipe.getProduct());
    }

    @Override
    public void setRecipe(final IRecipeLayout layout, final Reaction recipe, final IIngredients ingredients) {

        final IGuiIngredientGroup<Reactant> group = layout.getIngredientsGroup(ExtremeReactorsJeiPlugin.REACTANT_INGREDIENT_TYPE);

        // input
        group.init(0, true, 33, 20);
        group.set(0, recipe.getSource());

        // output
        group.init(1, false, 95, 20);
        group.set(1, recipe.getProduct());
    }

    //endregion
}
