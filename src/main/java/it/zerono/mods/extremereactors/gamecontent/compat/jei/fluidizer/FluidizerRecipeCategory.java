/*
 *
 * FluidizerRecipeCategory.java
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

package it.zerono.mods.extremereactors.gamecontent.compat.jei.fluidizer;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.config.Config;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.compat.jei.ExtremeReactorsJeiPlugin;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.MultiblockFluidizer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerFluidMixingRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerSolidMixingRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerSolidRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.IFluidizerRecipe;
import it.zerono.mods.zerocore.base.BaseHelper;
import it.zerono.mods.zerocore.base.CommonConstants;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.Orientation;
import it.zerono.mods.zerocore.lib.client.gui.Padding;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.compat.jei.AbstractModRecipeCategory;
import it.zerono.mods.zerocore.lib.compat.jei.drawable.ProgressBarDrawable;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.recipe.ModRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Map;

public abstract class FluidizerRecipeCategory<T extends ModRecipe & IFluidizerRecipe>
        extends AbstractModRecipeCategory<T> {

    public static FluidizerRecipeCategory<FluidizerSolidRecipe> solid(final IGuiHelper guiHelper) {
        return new FluidizerRecipeCategory<>(ExtremeReactorsJeiPlugin.FLUIDIZER_SOLID_JEI_RECIPE_TYPE,
                IFluidizerRecipe.Type.Solid,
                "compat.bigreactors.jei.fluidizer.recipecategory.solid.title", guiHelper) {

            @Override
            public void setRecipe(final IRecipeLayoutBuilder builder, final FluidizerSolidRecipe recipe, final IFocusGroup focuses) {

                super.setRecipe(builder, recipe, focuses);

                // input solid ingredient

                builder.addSlot(RecipeIngredientRole.INPUT, 26, 47)
                        .addIngredients(VanillaTypes.ITEM_STACK, recipe.getIngredient().getMatchingElements());
            }
        };
    }

    public static FluidizerRecipeCategory<FluidizerSolidMixingRecipe> solidMixing(final IGuiHelper guiHelper) {
        return new FluidizerRecipeCategory<>(ExtremeReactorsJeiPlugin.FLUIDIZER_SOLIDMIXING_JEI_RECIPE_TYPE,
                IFluidizerRecipe.Type.SolidMixing,
                "compat.bigreactors.jei.fluidizer.recipecategory.solidmixing.title", guiHelper) {

            @Override
            public void setRecipe(final IRecipeLayoutBuilder builder, final FluidizerSolidMixingRecipe recipe, final IFocusGroup focuses) {

                super.setRecipe(builder, recipe, focuses);

                // input solid ingredients

                builder.addSlot(RecipeIngredientRole.INPUT, 26, 47)
                        .addIngredients(VanillaTypes.ITEM_STACK, recipe.getIngredient1().getMatchingElements());

                builder.addSlot(RecipeIngredientRole.INPUT, 116, 47)
                        .addIngredients(VanillaTypes.ITEM_STACK, recipe.getIngredient2().getMatchingElements());
            }
        };
    }

    public static FluidizerRecipeCategory<FluidizerFluidMixingRecipe> fluidMixing(final IGuiHelper guiHelper) {
        return new FluidizerRecipeCategory<>(ExtremeReactorsJeiPlugin.FLUIDIZER_FLUIDMIXING_JEI_RECIPE_TYPE,
                IFluidizerRecipe.Type.FluidMixing,
                "compat.bigreactors.jei.fluidizer.recipecategory.fluidmixing.title", guiHelper) {

            @Override
            public void setRecipe(final IRecipeLayoutBuilder builder, final FluidizerFluidMixingRecipe recipe, final IFocusGroup focuses) {

                super.setRecipe(builder, recipe, focuses);

                // input solid ingredients

                builder.addSlot(RecipeIngredientRole.INPUT, 26, 23)
                        .setFluidRenderer(FluidizerFluidMixingRecipe.getMaxResultAmount(), false, 16, 64)
                        .addIngredients(ForgeTypes.FLUID_STACK, recipe.getIngredient1().getMatchingElements());

                builder.addSlot(RecipeIngredientRole.INPUT, 116, 23)
                        .setFluidRenderer(FluidizerFluidMixingRecipe.getMaxResultAmount(), false, 16, 64)
                        .addIngredients(ForgeTypes.FLUID_STACK, recipe.getIngredient2().getMatchingElements());
            }
        };
    }

    //region AbstractModRecipeCategory

    @Override
    public void setRecipe(final IRecipeLayoutBuilder builder, final T recipe, final IFocusGroup focuses) {

        // energy ingredient

        final int energyCost = Config.COMMON.fluidizer.energyPerRecipeTick.get() *
                recipe.getEnergyUsageMultiplier() * recipe.getRecipeType().getTicks();

        this._powerBar.setProgress(MultiblockFluidizer.ENERGY_CAPACITY.doubleValue(), energyCost);
        this.addTooltips(this._powerBarArea,
                Component.translatable("compat.bigreactors.jei.common.recipecategory.energy.tooltip.title").setStyle(CommonConstants.STYLE_TOOLTIP_TITLE),
                CodeHelper.TEXT_EMPTY_LINE,
                Component.literal(String.format("%d FE", energyCost)).setStyle(CommonConstants.STYLE_TOOLTIP_VALUE));

        // recipe output

        final FluidStack output = recipe.getResult().getResult();
        final Fluid outputFluid = output.getFluid();

        this._recipeOutputSprite = ModRenderHelper.getFlowingFluidSprite(outputFluid);

        this._outputBar.setTint(ModRenderHelper.getFluidTintColour(outputFluid));
        this._outputBar.setProgress(20_000, output.getAmount());

        this.addTooltips(71, 47,
                BaseHelper.getFluidNameOrEmpty(output).setStyle(CommonConstants.STYLE_TOOLTIP_TITLE),
                CodeHelper.TEXT_EMPTY_LINE,
                Component.literal(String.format("%d mB", output.getAmount())).setStyle(CommonConstants.STYLE_TOOLTIP_VALUE));

        builder.addSlot(RecipeIngredientRole.OUTPUT, 71, 23)
                .setFluidRenderer(20_000, false, 16, 64)
                .addIngredients(ForgeTypes.FLUID_STACK, ObjectLists.singleton(recipe.getResult().getResult()));
    }

    @Override
    public void draw(final ModRecipe recipe, final IRecipeSlotsView recipeSlotsView, final PoseStack matrix,
                     final double mouseX, final double mouseY) {

        this._powerBar.draw(matrix, this._powerBarArea.getX1(), this._powerBarArea.getY1());

        if (null != this._leftProgressBar) {
            this._leftProgressBar.draw(matrix, 45, 47);
        }

        if (null != this._rightProgressBar) {
            this._rightProgressBar.draw(matrix, 84+5, 42+5);
        }
    }

    @Override
    public List<Component> getTooltipStrings(final ModRecipe recipe, final IRecipeSlotsView recipeSlotsView,
                                             final double mouseX, final double mouseY) {
        return this._tooltips.entrySet().stream()
                .filter(e -> e.getKey().contains(mouseX, mouseY))
                .map(Map.Entry::getValue)
                .findAny()
                .orElse(ObjectLists.emptyList());
    }

    //endregion
    //region internals

    protected FluidizerRecipeCategory(final RecipeType<T> jeiRecipeType, final IFluidizerRecipe.Type recipeType,
                                      final String titleKey, final IGuiHelper guiHelper) {

        super(jeiRecipeType, Component.translatable(titleKey),
                Content.Blocks.REPROCESSOR_WASTEINJECTOR.get().createItemStack(), guiHelper, background(recipeType, guiHelper));

        this._tooltips = new Object2ObjectArrayMap<>(4);

        this._powerBarArea = new Rectangle(6, 23, 16, 64);
        this._powerBar = new ProgressBarDrawable(CommonIcons.PowerBar, 0, Padding.ZERO,
                this._powerBarArea.Width, this._powerBarArea.Height, Orientation.BottomToTop);

        this._outputBar = new ProgressBarDrawable(this::getRecipeOutputSprite, 0, Padding.ZERO, 16, 64, Orientation.BottomToTop);

        // progress bars

        final ResourceLocation texture = ExtremeReactors.newID("textures/gui/jei/fluidizer.png");
        final IDrawableStatic leftBar = guiHelper.createDrawable(texture, 0, 176, 24, 16);
        final IDrawableStatic rightBar = guiHelper.createDrawable(texture, 0, 192, 24, 16);

        switch (recipeType) {

            case Solid:
                this._leftProgressBar = guiHelper.createAnimatedDrawable(leftBar, recipeType.getTicks(), IDrawableAnimated.StartDirection.LEFT, false);
                this._rightProgressBar = null;
                break;

            case SolidMixing:
            case FluidMixing:
                this._leftProgressBar = guiHelper.createAnimatedDrawable(leftBar, recipeType.getTicks(), IDrawableAnimated.StartDirection.LEFT, false);
                this._rightProgressBar = guiHelper.createAnimatedDrawable(rightBar, recipeType.getTicks(), IDrawableAnimated.StartDirection.RIGHT, false);
                break;

            default:
                this._leftProgressBar = this._rightProgressBar = null;
                break;
        }
    }

    protected void addTooltips(final int x, final int y, final Component... text) {
        this.addTooltips(new Rectangle(x, y, 18, 18), text);
    }

    protected void addTooltips(final Rectangle area, final Component... text) {
        this._tooltips.put(area, new ObjectArrayList<>(text));
    }

    private static IDrawableStatic background(final IFluidizerRecipe.Type recipeType, final IGuiHelper guiHelper) {

        final int ordinal = recipeType.ordinal() - 1;

        return guiHelper.drawableBuilder(ExtremeReactors.newID("textures/gui/jei/fluidizer.png"),
                        1 == ordinal ? 128 : 0, 2 == ordinal ? 88 : 0, 128, 88)
                .addPadding(5, 5, 5, 5)
                .build();
    }

    private ISprite getRecipeOutputSprite() {
        return this._recipeOutputSprite;
    }

    private final Rectangle _powerBarArea;
    private final ProgressBarDrawable _powerBar;
    private final ProgressBarDrawable _outputBar;
    private final IDrawableAnimated _leftProgressBar;
    private final IDrawableAnimated _rightProgressBar;
    private final Map<Rectangle, List<Component>> _tooltips;
    private ISprite _recipeOutputSprite;

    //endregion
}
