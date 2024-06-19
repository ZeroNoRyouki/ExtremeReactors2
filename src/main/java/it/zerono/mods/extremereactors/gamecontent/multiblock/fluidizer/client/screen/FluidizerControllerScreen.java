/*
 *
 * FluidizerControllerScreen.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.client.screen;

import it.zerono.mods.extremereactors.CommonLocations;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonMultiblockScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.MultiblockFluidizer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.container.FluidizerControllerContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part.FluidizerControllerEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part.FluidizerFluidInjectorEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.IFluidizerRecipe;
import it.zerono.mods.zerocore.base.CommonConstants;
import it.zerono.mods.zerocore.base.client.screen.BaseIcons;
import it.zerono.mods.zerocore.base.client.screen.control.*;
import it.zerono.mods.zerocore.lib.client.gui.DesiredDimension;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.Orientation;
import it.zerono.mods.zerocore.lib.client.gui.Padding;
import it.zerono.mods.zerocore.lib.client.gui.control.*;
import it.zerono.mods.zerocore.lib.client.gui.layout.*;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.Sprite;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Supplier;

public class FluidizerControllerScreen
        extends CommonMultiblockScreen<MultiblockFluidizer, FluidizerControllerEntity, FluidizerControllerContainer> {

    public FluidizerControllerScreen(final FluidizerControllerContainer container, final Inventory inventory,
                                     final Component title) {

        super(container, inventory, PlayerInventoryUsage.None, title, FluidizerIcons::getMap);

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, CommonLocations.FLUIDIZER.buildWithSuffix("part-controller"), 1);
        this.addRecipesButton(this::displayFluidizerRecipes, "gui.bigreactors.show_recipes.tooltip.title");

        this._energyBar = new EnergyBar(this, "energyBar", EnergySystem.ForgeEnergy, container.getEnergyCapacity(),
                container.ENERGY_STORED, null);
        this._recipePanel = new RecipePanel();

        this._onOff = new OnOff(this, this.getMenu().ACTIVE, this::onActiveStateChanged,
                Component.translatable("gui.bigreactors.fluidizer.controller.on.title"),
                Component.translatable("gui.bigreactors.fluidizer.controller.off.title"));
    }

    //region CommonMultiblockScreen

    @Override
    protected MachineStatusIndicator createStatusIndicator(FluidizerControllerContainer container) {
        return this.createFluidizerStatusIndicator(container.ACTIVE);
    }

    @Override
    protected void onScreenCreate() {

        super.onScreenCreate();

        this.setContentLayoutEngine(new VerticalLayoutEngine().setZeroMargins());

        IControl verticalSeparator;
        final Panel table = new Panel(this, "table");
        final ILayoutEngine.ILayoutEngineHint hint = TabularLayoutEngine.hintBuilder()
                .setVerticalAlignment(VerticalAlignment.Top)
                .setPadding(Padding.get(0, 0, 1, 0))
                .build();

        table.setDesiredDimension(DesiredDimension.Width, 18 + 11 + 108 + 11 + 50);
        table.setLayoutEngine(TabularLayoutEngine.builder()
                .columns(5, 18, 11, 108, 11, 50)
                .rows(1)
                .build());

        // BARS

        this._energyBar.setLayoutEngineHint(hint);
        table.addControl(this._energyBar);

        // - separator
        verticalSeparator = CommonPanels.verticalSeparator(this);
        verticalSeparator.setLayoutEngineHint(hint);
        table.addControl(verticalSeparator);

        // - recipe panel
        this._recipePanel.setLayoutEngineHint(hint);
        table.addControl(this._recipePanel);

        // - separator
        verticalSeparator = CommonPanels.verticalSeparator(this);
        verticalSeparator.setLayoutEngineHint(hint);
        table.addControl(verticalSeparator);

        // COMMANDS

        final Panel commandPanel = CommonPanels.verticalCommandPanel(this);

        commandPanel.setLayoutEngineHint(hint);
        table.addControl(commandPanel);

        // - machine on/off

        this._onOff.setLayoutEngineHint(FixedLayoutEngine.hint(0, 0, 50, 16));
        commandPanel.addControl(this._onOff);

        this.addControl(table);
    }

    //endregion
    //region internals

    private void onActiveStateChanged(final SwitchButton button) {

        if (!this.isDataUpdateInProgress()) {
            this.sendCommandToServer(button.getActive() ?
                    CommonConstants.COMMAND_ACTIVATE : CommonConstants.COMMAND_DEACTIVATE);
        }
    }

    private class RecipePanel
            extends PanelGroup<IFluidizerRecipe.Type> {

        public RecipePanel() {

            super(FluidizerControllerScreen.this, "recipes", IFluidizerRecipe.Type.values());
            this.setDesiredDimension(108, CommonPanels.STANDARD_PANEL_HEIGHT);

            final FluidizerControllerContainer container = FluidizerControllerScreen.this.getMenu();

            this.bindActivePanel(container.RECIPE_TYPE);
            container.RECIPE_PROGRESS.bind(this::setProgress);

            this._outputFluidBar = new FluidBar(FluidizerControllerScreen.this, "outbar", container.getOutputCapacity(),
                    container.FLUID_OUTPUT_STACK, BaseIcons.Bucket, "gui.bigreactors.fluidizer.controller.fluidbar.title", null);
            this._leftProgressBar = this.progressBar(true, FluidizerIcons.ProgressLeft, FluidizerIcons.ProgressLeftFilled);
            this._rightProgressBar = this.progressBar(false, FluidizerIcons.ProgressRight, FluidizerIcons.ProgressRightFilled);

            this._solidIngredients = new IControl[2];
            Arrays.setAll(this._solidIngredients, this::solidInputControl);

            this._fluidIngredients = new IControl[2];
            Arrays.setAll(this._fluidIngredients, this::fluidInputControl);

            // sub panels

            this.setPanel(IFluidizerRecipe.Type.Invalid, this.noRecipePanel());
            this.setPanel(IFluidizerRecipe.Type.Solid, this.solidRecipePanel());
            this.setPanel(IFluidizerRecipe.Type.SolidMixing, this.solidMixingRecipePanel());
            this.setPanel(IFluidizerRecipe.Type.FluidMixing, this.fluidMixingRecipePanel());
            this.setActivePanel(IFluidizerRecipe.Type.Invalid);
        }

        //region internals

        private void setProgress(final double progress) {

            this._leftProgressBar.setValue(progress);
            this._rightProgressBar.setValue(progress);
        }

        //region internals

        private GaugeBar progressBar(final boolean left, final Supplier<@NotNull ISprite> background,
                                     final Supplier<@NotNull ISprite> filler) {

            final GaugeBar bar = new GaugeBar(FluidizerControllerScreen.this, left ? "sxprogress" : "dxprogress",
                    1.0, filler.get());

            bar.setDesiredDimension(24, 16);
            bar.setLayoutEngineHint(TabularLayoutEngine.hintBuilder().setPadding(Padding.get(0, 0, 18, 0)).build());
            bar.setBackground(background.get());
            bar.setOrientation(left ? Orientation.LeftToRight : Orientation.RightToLeft);
            return bar;
        }

        private IControl solidInputControl(int index) {

            final ItemStackDisplay display = new ItemStackDisplay(FluidizerControllerScreen.this, "solidInput" + index);

            display.setLayoutEngineHint(TabularLayoutEngine.hintBuilder().setPadding(Padding.get(0, 0, 18, 0)).build());
            display.bindStack(FluidizerControllerScreen.this.getMenu().getSolidInput(index));

            return display;
        }

        private IControl fluidInputControl(int index) {
            return new FluidBar(FluidizerControllerScreen.this, "inputfluid" + index,
                    FluidizerFluidInjectorEntity.MAX_CAPACITY, FluidizerControllerScreen.this.getMenu().getFluidInput(index),
                    Sprite.EMPTY_SUPPLIER, "gui.bigreactors.fluidizer.controller.inputfuel.title", null);
        }

        private Panel panel() {

            final Panel p = new Panel(FluidizerControllerScreen.this);

            p.setDesiredDimension(108, CommonPanels.STANDARD_PANEL_HEIGHT);
            p.setLayoutEngine(TabularLayoutEngine.builder()
                    .columns(5, 18, 27, 18, 27, 18)
                    .rows(1)
                    .build());
            return p;
        }

        private Panel noRecipePanel() {

            final Panel p = this.panel();
            final Static dummy = new Static(FluidizerControllerScreen.this, 1, 1);

            p.addControl(dummy, dummy, this._outputFluidBar);
            return p;
        }

        private Panel solidRecipePanel() {

            final Panel p = this.panel();

            p.addControl(this._solidIngredients[0], this._leftProgressBar, this._outputFluidBar);
            return p;
        }

        private Panel solidMixingRecipePanel() {

            final Panel p = this.panel();

            p.addControl(this._solidIngredients[0], this._leftProgressBar, this._outputFluidBar, this._rightProgressBar, this._solidIngredients[1]);
            return p;
        }

        private Panel fluidMixingRecipePanel() {

            final Panel p = this.panel();

            p.addControl(this._fluidIngredients[0], this._leftProgressBar, this._outputFluidBar, this._rightProgressBar, this._fluidIngredients[1]);
            return p;
        }

        private final FluidBar _outputFluidBar;
        private final IControl[] _solidIngredients;
        private final IControl[] _fluidIngredients;
        private final GaugeBar _leftProgressBar;
        private final GaugeBar _rightProgressBar;

        //endregion
    }

    private final IControl _energyBar;
    private final RecipePanel _recipePanel;
    private final OnOff _onOff;

    //endregion
}
