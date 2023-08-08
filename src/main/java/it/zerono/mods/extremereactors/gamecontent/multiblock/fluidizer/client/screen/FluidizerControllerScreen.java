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

import it.zerono.mods.extremereactors.ExtremeReactors;
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
import it.zerono.mods.zerocore.lib.client.gui.layout.FixedLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.layout.TabularLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.Sprite;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.NonNullSupplier;

import java.util.Arrays;

public class FluidizerControllerScreen
        extends CommonMultiblockScreen<MultiblockFluidizer, FluidizerControllerEntity, FluidizerControllerContainer> {

    public FluidizerControllerScreen(final FluidizerControllerContainer container, final PlayerInventory inventory,
                                     final ITextComponent title) {

        super(container, inventory, PlayerInventoryUsage.None, title, FluidizerIcons::getMap);

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, ExtremeReactors.newID("fluidizer/part-controller"), 1);

        this._energyBar = new EnergyBar(this, "energyBar", EnergySystem.ForgeEnergy, container.getEnergyCapacity(),
                container.ENERGY_STORED, null);
        this._recipePanel = new RecipePanel();

        this._onOff = new OnOff(this, this.getMenu().ACTIVE, this::onActiveStateChanged,
                new TranslationTextComponent("gui.bigreactors.fluidizer.controller.on.line1"),
                new TranslationTextComponent("gui.bigreactors.fluidizer.controller.off.line1"));
    }

    //region CommonMultiblockScreen

    @Override
    protected MachineStatusIndicator createStatusIndicator(FluidizerControllerContainer container) {
        return this.createDefaultStatusIndicator(container.ACTIVE, "gui.bigreactors.fluidizer.active",
                "gui.bigreactors.fluidizer.inactive");
    }

    @Override
    protected void onScreenCreate() {

        super.onScreenCreate();

        this.setContentLayoutEngine(new VerticalLayoutEngine()
                .setVerticalMargin(1)
                .setHorizontalMargin(13)
                .setControlsSpacing(0));

        final Panel mainPanel = new Panel(this);

        mainPanel.setDesiredDimension(DesiredDimension.Height, CommonPanels.STANDARD_PANEL_HEIGHT);
        mainPanel.setLayoutEngine(TabularLayoutEngine.builder()
                .columns(5, 18, 11, 108, 11, 50)
                .rows(1)
                .build());
        this.addControl(mainPanel);

        // BARS

        // - energy bar
        mainPanel.addControl(this._energyBar);
        // - separator
        mainPanel.addControl(CommonPanels.verticalSeparator(this));
        // - recipe panel
        mainPanel.addControl(this._recipePanel);
        // - separator
        mainPanel.addControl(CommonPanels.verticalSeparator(this));

        // COMMANDS

        final Panel commandPanel = CommonPanels.verticalCommandPanel(this);

        mainPanel.addControl(commandPanel);

        // - machine on/off

        this._onOff.setLayoutEngineHint(FixedLayoutEngine.hint(0, 0, 50, 16));
        commandPanel.addControl(this._onOff);
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
                    container.FLUID_OUTPUT_STACK, BaseIcons.Bucket, "gui.bigreactors.fluidizer.controller.fluidbar.line1", null);
            this._leftProgressBar = this.progressBar(true, FluidizerIcons.ProgressLeft, FluidizerIcons.ProgressLeftFilled);
            this._rightProgressBar = this.progressBar(false, FluidizerIcons.ProgressRight, FluidizerIcons.ProgressRightFilled);

            this._solidIngredients = new IControl[2];
            Arrays.setAll(this._solidIngredients, this::solidInputControl);

            this._fluidIngredients = new IControl[2];
            Arrays.setAll(this._fluidIngredients,this::fluidInputControl);

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

        private GaugeBar progressBar(final boolean left, final NonNullSupplier<ISprite> background,
                                     final NonNullSupplier<ISprite> filler) {

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
