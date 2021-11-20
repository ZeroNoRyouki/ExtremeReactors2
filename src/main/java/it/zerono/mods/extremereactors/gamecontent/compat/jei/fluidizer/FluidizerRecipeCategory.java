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

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.config.Config;
import it.zerono.mods.extremereactors.gamecontent.Content;
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
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.recipe.ModRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Map;

public abstract class FluidizerRecipeCategory
        extends AbstractModRecipeCategory<ModRecipe> {

    public static FluidizerRecipeCategory solid(final IGuiHelper guiHelper) {
        return new FluidizerRecipeCategory(IFluidizerRecipe.Type.Solid, "compat.bigreactors.jei.fluidizer.recipecategory.solid.title", guiHelper) {

            @Override
            public void setIngredients(final ModRecipe recipe, final IIngredients ingredients) {

                super.setIngredients(recipe, ingredients);

                if (recipe instanceof FluidizerSolidRecipe) {

                    final FluidizerSolidRecipe solidRecipe = (FluidizerSolidRecipe)recipe;

                    ingredients.setInputs(VanillaTypes.ITEM, solidRecipe.getIngredient().getMatchingElements());
                    ingredients.setOutput(VanillaTypes.FLUID, solidRecipe.getResult().getResult());
                }
            }

            @Override
            public void setRecipe(final IRecipeLayout layout, final ModRecipe recipe, final IIngredients ingredients) {

                final IGuiItemStackGroup items = layout.getItemStacks();
                final IGuiFluidStackGroup fluids = layout.getFluidStacks();

                items.init(0, true, 25, 46);
                items.set(ingredients);

                fluids.init(0, false, 71, 23, 16, 64, (int)FluidizerSolidRecipe.getMaxResultAmount(), true, null);
                fluids.set(ingredients);
            }

            @Override
            public boolean isHandled(final ModRecipe recipe) {
                return recipe instanceof FluidizerSolidRecipe;
            }
        };
    }

    public static FluidizerRecipeCategory solidMixing(final IGuiHelper guiHelper) {
        return new FluidizerRecipeCategory(IFluidizerRecipe.Type.SolidMixing, "compat.bigreactors.jei.fluidizer.recipecategory.solidmixing.title", guiHelper) {

            @Override
            public void setIngredients(final ModRecipe recipe, final IIngredients ingredients) {

                super.setIngredients(recipe, ingredients);

                if (recipe instanceof FluidizerSolidMixingRecipe) {

                    final FluidizerSolidMixingRecipe solidRecipe = (FluidizerSolidMixingRecipe)recipe;

                    ingredients.setInputLists(VanillaTypes.ITEM,
                            Lists.newArrayList(solidRecipe.getIngredient1().getMatchingElements(),
                                    solidRecipe.getIngredient2().getMatchingElements()));

                    ingredients.setOutput(VanillaTypes.FLUID, solidRecipe.getResult().getResult());
                }
            }

            @Override
            public void setRecipe(final IRecipeLayout layout, final ModRecipe recipe, final IIngredients ingredients) {

                final IGuiItemStackGroup items = layout.getItemStacks();
                final IGuiFluidStackGroup fluids = layout.getFluidStacks();

                items.init(0, true, 25, 46);
                items.set(ingredients);

                items.init(1, true, 115, 46);
                items.set(ingredients);

                fluids.init(0, false, 71, 23, 16, 64, (int)FluidizerSolidMixingRecipe.getMaxResultAmount(), true, null);
                fluids.set(ingredients);
            }

            @Override
            public boolean isHandled(final ModRecipe recipe) {
                return recipe instanceof FluidizerSolidMixingRecipe;
            }
        };
    }

    public static FluidizerRecipeCategory fluidMixing(final IGuiHelper guiHelper) {
        return new FluidizerRecipeCategory(IFluidizerRecipe.Type.FluidMixing, "compat.bigreactors.jei.fluidizer.recipecategory.fluidmixing.title", guiHelper) {

            @Override
            public void setIngredients(final ModRecipe recipe, final IIngredients ingredients) {

                super.setIngredients(recipe, ingredients);

                if (recipe instanceof FluidizerFluidMixingRecipe) {

                    final FluidizerFluidMixingRecipe fluidRecipe = (FluidizerFluidMixingRecipe)recipe;

                    ingredients.setInputLists(VanillaTypes.FLUID,
                            Lists.newArrayList(fluidRecipe.getIngredient1().getMatchingElements(),
                                    fluidRecipe.getIngredient2().getMatchingElements()));

                    ingredients.setOutput(VanillaTypes.FLUID, fluidRecipe.getResult().getResult());
                }
            }

            @Override
            public void setRecipe(final IRecipeLayout layout, final ModRecipe recipe, final IIngredients ingredients) {

                final IGuiFluidStackGroup stacks = layout.getFluidStacks();

                stacks.init(0, true, 26, 23, 16, 64, (int)FluidizerFluidMixingRecipe.getMaxResultAmount(), true, null);
                stacks.set(ingredients);

                stacks.init(1, true, 116, 23, 16, 64, (int)FluidizerFluidMixingRecipe.getMaxResultAmount(), true, null);
                stacks.set(ingredients);

                stacks.init(2, false, 71, 23, 16, 64, (int)FluidizerFluidMixingRecipe.getMaxResultAmount(), true, null);
                stacks.set(ingredients);
            }

            @Override
            public boolean isHandled(final ModRecipe recipe) {
                return recipe instanceof FluidizerFluidMixingRecipe;
            }
        };
    }

    //region AbstractModRecipeCategory

    @Override
    public Class<? extends ModRecipe> getRecipeClass() {
        return ModRecipe.class;
    }

    @Override
    public void setIngredients(final ModRecipe recipe, final IIngredients ingredients) {

        if (!(recipe instanceof IFluidizerRecipe)) {
            return;
        }

        final IFluidizerRecipe fluidizerRecipe = (IFluidizerRecipe)recipe;

        // energy ingredient

        final int energyCost = Config.COMMON.fluidizer.energyPerRecipeTick.get() *
                fluidizerRecipe.getEnergyUsageMultiplier() * fluidizerRecipe.getRecipeType().getTicks();

        this._powerBar.setProgress(MultiblockFluidizer.ENERGY_CAPACITY.doubleValue(), energyCost);
        this.addTooltips(this._powerBarArea,
                new TranslationTextComponent("compat.bigreactors.jei.common.recipecategory.energy.tooltip.title").setStyle(CommonConstants.STYLE_TOOLTIP_TITLE),
                CodeHelper.TEXT_EMPTY_LINE,
                new StringTextComponent(String.format("%d FE", energyCost)).setStyle(CommonConstants.STYLE_TOOLTIP_VALUE));

        // recipe output

        final FluidStack output = fluidizerRecipe.getResult().getResult();
        final Fluid outputFluid = output.getFluid();

        ingredients.setOutput(VanillaTypes.FLUID, output);
        this._recipeOutputSprite = ModRenderHelper.getFlowingFluidSprite(outputFluid);

        this._outputBar.setTint(Colour.fromARGB(outputFluid.getAttributes().getColor()));
        this._outputBar.setProgress(20_000, output.getAmount());

        this.addTooltips(71, 47,
                BaseHelper.getFluidNameOrEmpty(output).setStyle(CommonConstants.STYLE_TOOLTIP_TITLE),
                CodeHelper.TEXT_EMPTY_LINE,
                new StringTextComponent(String.format("%d mB", output.getAmount())).setStyle(CommonConstants.STYLE_TOOLTIP_VALUE));
    }

    @Override
    public void draw(final ModRecipe recipe, final MatrixStack matrix, final double mouseX, final double mouseY) {

        this._powerBar.draw(matrix, this._powerBarArea.getX1(), this._powerBarArea.getY1());

        if (null != this._leftProgressBar) {
            this._leftProgressBar.draw(matrix, 45, 47);
        }

        if (null != this._rightProgressBar) {
            this._rightProgressBar.draw(matrix, 84+5, 42+5);
        }
    }

    @Override
    public List<ITextComponent> getTooltipStrings(final ModRecipe recipe, final double mouseX, final double mouseY) {
        return this._tooltips.entrySet().stream()
                .filter(e -> e.getKey().contains(mouseX, mouseY))
                .map(Map.Entry::getValue)
                .findAny()
                .orElse(ObjectLists.emptyList());
    }

    //endregion
    //region internals

    protected FluidizerRecipeCategory(final IFluidizerRecipe.Type recipeType, final String titleKey,
                                      final IGuiHelper guiHelper) {

        super(recipeType.getRecipeId(), new TranslationTextComponent(titleKey),
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

    protected void addTooltips(final int x, final int y, final ITextComponent... text) {
        this.addTooltips(new Rectangle(x, y, 18, 18), text);
    }

    protected void addTooltips(final Rectangle area, final ITextComponent... text) {
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
    private final Map<Rectangle, List<ITextComponent>> _tooltips;
    private ISprite _recipeOutputSprite;

    //endregion
}
