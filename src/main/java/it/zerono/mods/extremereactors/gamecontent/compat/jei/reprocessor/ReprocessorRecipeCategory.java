///*
// *
// * ReprocessorRecipeCategory.java
// *
// * This file is part of Extreme Reactors 2 by ZeroNoRyouki, a Minecraft mod.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// * DEALINGS IN THE SOFTWARE.
// *
// * DO NOT REMOVE OR EDIT THIS HEADER
// *
// */
//
//package it.zerono.mods.extremereactors.gamecontent.compat.jei.reprocessor;
//
//import com.mojang.blaze3d.vertex.PoseStack;
//import it.unimi.dsi.fastutil.objects.ObjectArrayList;
//import it.unimi.dsi.fastutil.objects.ObjectLists;
//import it.zerono.mods.extremereactors.ExtremeReactors;
//import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
//import it.zerono.mods.extremereactors.gamecontent.Content;
//import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
//import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.MultiblockReprocessor;
//import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.recipe.ReprocessorRecipe;
//import it.zerono.mods.zerocore.lib.CodeHelper;
//import it.zerono.mods.zerocore.lib.client.gui.Orientation;
//import it.zerono.mods.zerocore.lib.client.gui.Padding;
//import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
//import it.zerono.mods.zerocore.lib.client.gui.sprite.Sprite;
//import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
//import it.zerono.mods.zerocore.lib.compat.jei.AbstractModRecipeCategory;
//import it.zerono.mods.zerocore.lib.compat.jei.drawable.ProgressBarDrawable;
//import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
//import it.zerono.mods.zerocore.lib.data.gfx.Colour;
//import mezz.jei.api.constants.VanillaTypes;
//import mezz.jei.api.gui.IRecipeLayout;
//import mezz.jei.api.gui.drawable.IDrawableAnimated;
//import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
//import mezz.jei.api.helpers.IGuiHelper;
//import mezz.jei.api.ingredients.IIngredients;
//import net.minecraft.network.chat.BaseComponent;
//import net.minecraft.network.chat.Component;
//import net.minecraft.network.chat.TextComponent;
//import net.minecraft.network.chat.TranslatableComponent;
//import net.minecraft.world.level.material.Fluid;
//import net.minecraftforge.fluids.FluidStack;
//
//import java.util.List;
//import java.util.Objects;
//
//public class ReprocessorRecipeCategory
//    extends AbstractModRecipeCategory<ReprocessorRecipe> {
//
//    public ReprocessorRecipeCategory(final IGuiHelper guiHelper) {
//
//        super(ReprocessorRecipe.ID, new TranslatableComponent("compat.bigreactors.jei.common.recipecategory.title"),
//                Content.Blocks.REPROCESSOR_WASTEINJECTOR.get().createItemStack(), guiHelper,
//                guiHelper.drawableBuilder(ExtremeReactors.newID("textures/gui/jei/reprocessor.png"), 0, 0, 96, 96)
//                        .setTextureSize(96, 96)
//                        .addPadding(5, 5, 5, 78)
//                        .build());
//
//        this._powerBarArea = new Rectangle(6, 23, 16, 64);
//        this._fluidBarrArea = new Rectangle(34, 23, 16, 64);
//        this._fluidBarTooltips = this._powerBarTooltips = ObjectLists.emptyList();
//
//        this._guiHelper = guiHelper;//remove
//
//        this._powerBar = new ProgressBarDrawable(CommonIcons.PowerBar, 0, Padding.ZERO,
//                this._powerBarArea.Width, this._powerBarArea.Height, Orientation.BottomToTop);
//    }
//
//    //region AbstractModRecipeCategory
//
//    @Override
//    public Class<? extends ReprocessorRecipe> getRecipeClass() {
//        return ReprocessorRecipe.class;
//    }
//
//    @Override
//    public void setIngredients(final ReprocessorRecipe recipe, final IIngredients ingredients) {
//
//        ingredients.setInputIngredients(recipe.getIngredients());
//        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResult().getResult());
//
//        // fluid ingredient
//
//        this._recipeFluid = recipe.getIngredient2().getMatchingElements().stream()
//                .filter(Objects::nonNull)
//                .filter(fs -> !fs.isEmpty())
//                .findAny()
//                .orElse(FluidStack.EMPTY);
//
//        if (!this._recipeFluid.isEmpty()) {
//
//            this._recipeFluidSprite = ModRenderHelper.getFlowingFluidSprite(this._recipeFluid.getFluid());
//            this._recipeProgressSprite = this._recipeFluidSprite.copyWith(CommonIcons.ReprocessorProgressBarMask.get());
//
//            this._fluidBar = new ProgressBarDrawable(this::getRecipeFluidSprite, 0, Padding.ZERO, 16, 64, Orientation.BottomToTop);
//            this._fluidBar.setTint(Colour.fromARGB(this._recipeFluid.getFluid().getAttributes().getColor()));
//            this._fluidBar.setProgress(MultiblockReprocessor.FLUID_CAPACITY, this._recipeFluid.getAmount());
//
//            this._fluidBarTooltips = new ObjectArrayList<>(3);
//            this._fluidBarTooltips.add(getFluidName(this._recipeFluid.getFluid())
//                    .setStyle(CommonConstants.STYLE_TOOLTIP_TITLE));
//            this._fluidBarTooltips.add(CodeHelper.TEXT_EMPTY_LINE);
//            this._fluidBarTooltips.add(new TextComponent(String.format("%d mB", this._recipeFluid.getAmount()))
//                    .setStyle(CommonConstants.STYLE_TOOLTIP_VALUE));
//
//            this._progressBar = null;
//
//        } else {
//
//            this._recipeFluidSprite = this._recipeProgressSprite = Sprite.EMPTY;
//            this._progressBar = null;
//            this._fluidBar = ProgressBarDrawable.empty();
//        }
//
//        // energy ingredient
//
//        this._powerBar.setProgress(MultiblockReprocessor.ENERGY_CAPACITY, MultiblockReprocessor.TICKS * MultiblockReprocessor.TICK_ENERGY_COST);
//
//        this._powerBarTooltips = new ObjectArrayList<>(3);
//        this._powerBarTooltips.add(new TranslatableComponent("compat.bigreactors.jei.reprocessor.recipecategory.energy.tooltip.title")
//                .setStyle(CommonConstants.STYLE_TOOLTIP_TITLE));
//        this._powerBarTooltips.add(CodeHelper.TEXT_EMPTY_LINE);
//        this._powerBarTooltips.add(new TextComponent(String.format("%d FE", MultiblockReprocessor.TICK_ENERGY_COST * MultiblockReprocessor.TICKS))
//                .setStyle(CommonConstants.STYLE_TOOLTIP_VALUE));
//    }
//
//    @Override
//    public void setRecipe(final IRecipeLayout layout, final ReprocessorRecipe recipe, final IIngredients ingredients) {
//
//        final IGuiItemStackGroup stacks = layout.getItemStacks();
//
//        // input
//        stacks.init(0, true, 78 + 5, 0 + 5); // add the padding
//        stacks.set(ingredients);
//
//        // output
//        stacks.init(1, false, 78 + 5, 65 + 5); // add the padding
//        stacks.set(1, ingredients.getOutputs(VanillaTypes.ITEM).get(0));
//    }
//
//    @Override
//    public void draw(final ReprocessorRecipe recipe, final PoseStack matrix, final double mouseX, final double mouseY) {
//
//        this._powerBar.draw(matrix, this._powerBarArea.getX1(), this._powerBarArea.getY1());
//        this._fluidBar.draw(matrix, this._fluidBarrArea.getX1(), this._fluidBarrArea.getY1());
//
//        if (null != this._progressBar) {
//            this._progressBar.draw(matrix, 79 + 5, 26 + 5);
//        }
//    }
//
//    @Override
//    public List<Component> getTooltipStrings(final ReprocessorRecipe recipe, final double mouseX, final double mouseY) {
//
//        if (this._powerBarArea.contains(mouseX, mouseY)) {
//            return this._powerBarTooltips;
//        }
//
//        if (this._fluidBarrArea.contains(mouseX, mouseY)) {
//            return this._fluidBarTooltips;
//        }
//
//        return ObjectLists.emptyList();
//    }
//
//    //endregion
//    //region internals
//
//    private ISprite getRecipeFluidSprite() {
//        return this._recipeFluidSprite;
//    }
//
//    private static BaseComponent getFluidName(final Fluid fluid) {
//        return new TranslatableComponent(fluid.getAttributes().getTranslationKey());
//    }
//
//    private final Rectangle _powerBarArea;
//    private final Rectangle _fluidBarrArea;
//
//    private final IGuiHelper _guiHelper;
//    private final ProgressBarDrawable _powerBar;
//    private List<Component> _powerBarTooltips;
//    private ProgressBarDrawable _fluidBar;
//    private List<Component> _fluidBarTooltips;
//    private IDrawableAnimated _progressBar;
//
//    private FluidStack _recipeFluid;
//    private ISprite _recipeFluidSprite;
//    private ISprite _recipeProgressSprite;
//
//    //endregion
//}
