//package it.zerono.mods.extremereactors.gamecontent.compat.jei.reactor;
///*
// * ReactionsRecipeCategory
// *
// * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// * DEALINGS IN THE SOFTWARE.
// *
// * Do not remove or edit this header
// *
// */
//
//import it.unimi.dsi.fastutil.objects.ObjectLists;
//import it.zerono.mods.extremereactors.api.reactor.Reaction;
//import it.zerono.mods.extremereactors.gamecontent.Content;
//import it.zerono.mods.extremereactors.gamecontent.compat.jei.ExtremeReactorsJeiPlugin;
//import it.zerono.mods.zerocore.lib.compat.jei.AbstractModRecipeCategory;
//import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
//import mezz.jei.api.helpers.IGuiHelper;
//import mezz.jei.api.recipe.IFocusGroup;
//import mezz.jei.api.recipe.RecipeIngredientRole;
//import net.minecraft.network.chat.Component;
//import net.minecraft.world.item.ItemStack;
//
//public class ReactionsRecipeCategory
//        extends AbstractModRecipeCategory<Reaction> {
//
//    public ReactionsRecipeCategory(final IGuiHelper guiHelper) {
//        super(ExtremeReactorsJeiPlugin.REACTION_JEI_RECIPE_TYPE,
//                Component.translatable("compat.bigreactors.jei.reactantsreactions.recipecategory.title"),
//                new ItemStack(Content.Blocks.REACTOR_FUELROD_BASIC.get()), guiHelper,
//                ExtremeReactorsJeiPlugin.defaultMappingDrawable(guiHelper));
//    }
//
//    //region AbstractModRecipeCategory
//
//    @Override
//    public void setRecipe(final IRecipeLayoutBuilder builder, final Reaction recipe, final IFocusGroup focuses) {
//
//        builder.addSlot(RecipeIngredientRole.INPUT, 33, 20)
//                .addIngredients(ExtremeReactorsJeiPlugin.REACTANT_INGREDIENT_TYPE, ObjectLists.singleton(recipe.getSource()));
//
//        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 20)
//                .addIngredients(ExtremeReactorsJeiPlugin.REACTANT_INGREDIENT_TYPE, ObjectLists.singleton(recipe.getProduct()));
//    }
//
//    //endregion
//}
