/*
 *
 * ReprocessorRecipeCategory.java
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

package it.zerono.mods.extremereactors.gamecontent.compat.jei.reprocessor;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.extremereactors.CommonLocations;
import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.compat.jei.ExtremeReactorsJeiPlugin;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.MultiblockReprocessor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.recipe.ReprocessorRecipe;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.Orientation;
import it.zerono.mods.zerocore.lib.client.gui.Padding;
import it.zerono.mods.zerocore.lib.compat.jei.AbstractModRecipeCategory;
import it.zerono.mods.zerocore.lib.compat.jei.drawable.ProgressBarDrawable;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ReprocessorRecipeCategory
        extends AbstractModRecipeCategory<ReprocessorRecipe> {

    public ReprocessorRecipeCategory(final IGuiHelper guiHelper) {

        super(ExtremeReactorsJeiPlugin.REPROCESSOR_JEI_RECIPE_TYPE,
                Component.translatable("compat.bigreactors.jei.reprocessor.recipecategory.title"),
                Content.Blocks.REPROCESSOR_WASTEINJECTOR.get().createItemStack(), guiHelper,
                guiHelper.drawableBuilder(getBackgroundId(), 0, 0, 96, 96)
                        .setTextureSize(96, 96)
                        .addPadding(5, 5, 5, 78)
                        .build());

        this._powerBarArea = new Rectangle(6, 23, 16, 64);
        this._powerBar = new ProgressBarDrawable(CommonIcons.PowerBar, 0, Padding.ZERO,
                this._powerBarArea.Width, this._powerBarArea.Height, Orientation.BottomToTop);
    }

    //region AbstractModRecipeCategory

    @Override
    public void setRecipe(final IRecipeLayoutBuilder builder, final ReprocessorRecipe recipe, final IFocusGroup focuses) {

        // output slot

        builder.addSlot(RecipeIngredientRole.OUTPUT, 79 + 5, 66 + 5) // add the padding
                .addIngredients(VanillaTypes.ITEM_STACK, ObjectLists.singleton(recipe.getResult().getResult()));

        // input solid ingredient

        builder.addSlot(RecipeIngredientRole.INPUT, 79 + 5, 1 + 5) // add the padding
                .addIngredients(VanillaTypes.ITEM_STACK, recipe.getIngredient1().getMatchingElements());

        // input fluid ingredient

        builder.addSlot(RecipeIngredientRole.INPUT, 79 + 5, 1 + 5) // add the padding
                .addIngredients(VanillaTypes.ITEM_STACK, recipe.getIngredient1().getMatchingElements());

        builder.addSlot(RecipeIngredientRole.INPUT, 34, 23)
                .setFluidRenderer(MultiblockReprocessor.FLUID_CAPACITY, false, 16, 64)
                .addIngredients(NeoForgeTypes.FLUID_STACK, recipe.getIngredient2().getMatchingElements());

        // energy ingredient

        this._powerBar.setProgress(MultiblockReprocessor.ENERGY_CAPACITY, MultiblockReprocessor.TICKS * MultiblockReprocessor.TICK_ENERGY_COST);

        this._powerBarTooltips = new ObjectArrayList<>(3);
        this._powerBarTooltips.add(Component.translatable("compat.bigreactors.jei.common.recipecategory.energy.tooltip.title")
                .setStyle(CommonConstants.STYLE_TOOLTIP_TITLE));
        this._powerBarTooltips.add(CodeHelper.TEXT_EMPTY_LINE);
        this._powerBarTooltips.add(Component.literal(String.format("%d FE", MultiblockReprocessor.TICK_ENERGY_COST * MultiblockReprocessor.TICKS))
                .setStyle(CommonConstants.STYLE_TOOLTIP_VALUE));
    }

    @Override
    public void draw(final ReprocessorRecipe recipe, final IRecipeSlotsView recipeSlotsView, final GuiGraphics gfx,
                     final double mouseX, final double mouseY) {
        this._powerBar.draw(gfx, this._powerBarArea.getX1(), this._powerBarArea.getY1());
    }

    @Override
    public List<Component> getTooltipStrings(final ReprocessorRecipe recipe, final IRecipeSlotsView recipeSlotsView,
                                             final double mouseX, final double mouseY) {

        if (this._powerBarArea.contains(mouseX, mouseY)) {
            return this._powerBarTooltips;
        }

        return ObjectLists.emptyList();
    }

    //endregion
    //region internals

    private static ResourceLocation getBackgroundId() {
        return CommonLocations.TEXTURES_GUI_JEI.buildWithSuffix("reprocessor.png");
    }

    private final Rectangle _powerBarArea;
    private final ProgressBarDrawable _powerBar;
    private List<Component> _powerBarTooltips;

    //endregion
}
