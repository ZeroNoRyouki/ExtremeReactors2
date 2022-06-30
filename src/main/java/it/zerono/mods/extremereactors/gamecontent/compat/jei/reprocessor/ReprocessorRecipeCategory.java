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

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.compat.jei.ExtremeReactorsJeiPlugin;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.MultiblockReprocessor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.recipe.ReprocessorRecipe;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.Orientation;
import it.zerono.mods.zerocore.lib.client.gui.Padding;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.Sprite;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.compat.jei.AbstractModRecipeCategory;
import it.zerono.mods.zerocore.lib.compat.jei.drawable.ProgressBarDrawable;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.fluid.FluidHelper;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Objects;

public class ReprocessorRecipeCategory
        extends AbstractModRecipeCategory<ReprocessorRecipe> {

    public ReprocessorRecipeCategory(final IGuiHelper guiHelper) {

        super(ExtremeReactorsJeiPlugin.REPROCESSOR_JEI_RECIPE_TYPE,
                Component.translatable("compat.bigreactors.jei.reprocessor.recipecategory.title"),
                Content.Blocks.REPROCESSOR_WASTEINJECTOR.get().createItemStack(), guiHelper,
                guiHelper.drawableBuilder(ExtremeReactors.newID("textures/gui/jei/reprocessor.png"), 0, 0, 96, 96)
                        .setTextureSize(96, 96)
                        .addPadding(5, 5, 5, 78)
                        .build());

        this._powerBarArea = new Rectangle(6, 23, 16, 64);
        this._fluidBarrArea = new Rectangle(34, 23, 16, 64);
        this._fluidBarTooltips = this._powerBarTooltips = ObjectLists.emptyList();

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

        final FluidStack recipeFluid = recipe.getIngredient2().getMatchingElements().stream()
                .filter(Objects::nonNull)
                .filter(fs -> !fs.isEmpty())
                .findAny()
                .orElse(FluidStack.EMPTY);

        if (!recipeFluid.isEmpty()) {

            this._recipeFluidSprite = ModRenderHelper.getFlowingFluidSprite(recipeFluid.getFluid());

            this._fluidBar = new ProgressBarDrawable(this::getRecipeFluidSprite, 0, Padding.ZERO, 16, 64, Orientation.BottomToTop);
            this._fluidBar.setTint(ModRenderHelper.getFluidTintColour(recipeFluid.getFluid()));
            this._fluidBar.setProgress(MultiblockReprocessor.FLUID_CAPACITY, recipeFluid.getAmount());

            this._fluidBarTooltips = new ObjectArrayList<>(3);
            this._fluidBarTooltips.add(FluidHelper.getFluidName(recipeFluid.getFluid())
                    .setStyle(CommonConstants.STYLE_TOOLTIP_TITLE));
            this._fluidBarTooltips.add(CodeHelper.TEXT_EMPTY_LINE);
            this._fluidBarTooltips.add(Component.literal(String.format("%d mB", recipeFluid.getAmount()))
                    .setStyle(CommonConstants.STYLE_TOOLTIP_VALUE));

            this._progressBar = null;

        } else {

            this._recipeFluidSprite = Sprite.EMPTY;
            this._progressBar = null;
            this._fluidBar = ProgressBarDrawable.empty();
        }

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
    public void draw(final ReprocessorRecipe recipe, final IRecipeSlotsView recipeSlotsView, final PoseStack matrix,
                     final double mouseX, final double mouseY) {

        this._powerBar.draw(matrix, this._powerBarArea.getX1(), this._powerBarArea.getY1());
        this._fluidBar.draw(matrix, this._fluidBarrArea.getX1(), this._fluidBarrArea.getY1());

        if (null != this._progressBar) {
            this._progressBar.draw(matrix, 79 + 5, 26 + 5);
        }
    }

    @Override
    public List<Component> getTooltipStrings(final ReprocessorRecipe recipe, final IRecipeSlotsView recipeSlotsView,
                                             final double mouseX, final double mouseY) {

        if (this._powerBarArea.contains(mouseX, mouseY)) {
            return this._powerBarTooltips;
        }

        if (this._fluidBarrArea.contains(mouseX, mouseY)) {
            return this._fluidBarTooltips;
        }

        return ObjectLists.emptyList();
    }

    //endregion
    //region internals

    private ISprite getRecipeFluidSprite() {
        return this._recipeFluidSprite;
    }

    private final Rectangle _powerBarArea;
    private final Rectangle _fluidBarrArea;
    private final ProgressBarDrawable _powerBar;
    private List<Component> _powerBarTooltips;
    private ProgressBarDrawable _fluidBar;
    private List<Component> _fluidBarTooltips;
    private IDrawableAnimated _progressBar;

    private ISprite _recipeFluidSprite;

    //endregion
}
