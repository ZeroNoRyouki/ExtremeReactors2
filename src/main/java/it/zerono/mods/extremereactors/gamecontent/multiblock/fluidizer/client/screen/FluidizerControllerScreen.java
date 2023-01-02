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
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.GuiTheme;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.MultiblockFluidizer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.container.FluidizerControllerContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part.FluidizerControllerEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part.FluidizerFluidInjectorEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.IFluidizerRecipe;
import it.zerono.mods.zerocore.base.CommonConstants;
import it.zerono.mods.zerocore.base.client.screen.BaseIcons;
import it.zerono.mods.zerocore.base.client.screen.control.EnergyBar;
import it.zerono.mods.zerocore.base.client.screen.control.FluidBar;
import it.zerono.mods.zerocore.base.client.screen.control.MachineStatusIndicator;
import it.zerono.mods.zerocore.base.client.screen.control.OnOff;
import it.zerono.mods.zerocore.base.multiblock.client.screen.AbstractMultiblockScreen;
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
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Supplier;

public class FluidizerControllerScreen
        extends AbstractMultiblockScreen<MultiblockFluidizer, FluidizerControllerEntity, FluidizerControllerContainer> {

    public FluidizerControllerScreen(final FluidizerControllerContainer container, final Inventory inventory,
                                     final Component title) {

        super(container, inventory, PlayerInventoryUsage.None, title, FluidizerIcons::getMap);

        this._energyBar = new EnergyBar(this, "energyBar", EnergySystem.ForgeEnergy, MultiblockFluidizer.ENERGY_CAPACITY,
                container::getEnergyStored, null);
        this._recipePanel = new RecipePanel();

        this._indicator = new MachineStatusIndicator(this, "indicator");
        this._indicator.setTooltips(true, "gui.bigreactors.fluidizer.active");
        this._indicator.setTooltips(false, "gui.bigreactors.fluidizer.inactive");
        this.setTheme(GuiTheme.ER.get());
    }

    //region ContainerScreen

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    //endregion
    //region AbstractMultiblockScreen

    @Nullable
    @Override
    protected IControl getTitleBarWidget() {
        return this._indicator;
    }

    @Override
    public int getTooltipsPopupMaxWidth() {
        return parseTooltipsPopupMaxWidthFromLang("gui.bigreactors.fluidizer.controller.tooltips_popup_max_width", 150);
    }

    @Override
    protected void onScreenCreate() {

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, ExtremeReactors.newID("fluidizer/part-controller"), 1);

        super.onScreenCreate();

        this.setContentLayoutEngine(new VerticalLayoutEngine()
                .setVerticalMargin(1)
                .setHorizontalMargin(13)
                .setControlsSpacing(0));

        final Panel mainPanel = new Panel(this);

        mainPanel.setDesiredDimension(DesiredDimension.Height, VBARPANEL_HEIGHT);
        mainPanel.setLayoutEngine(TabularLayoutEngine.builder()
                .columns(5, 18, 11, 108, 11, 50)
                .rows(1)
                .build());
        this.addControl(mainPanel);

        // bars

        // - energy bar
        mainPanel.addControl(this._energyBar);
        // - separator
        mainPanel.addControl(this.vSeparatorPanel());
        // - recipe panel
        mainPanel.addControl(this._recipePanel);
        // - separator
        mainPanel.addControl(this.vSeparatorPanel());

        // COMMANDS

        final Panel commandPanel = this.vCommandPanel();

        mainPanel.addControl(commandPanel);

        // - machine on/off

        final Supplier<Boolean> isActive = this.getMenu()::isFluidizerActive;
        final OnOff onOff = new OnOff(this, isActive, this::onActiveStateChanged,
                new TranslatableComponent("gui.bigreactors.fluidizer.controller.on.line1"),
                new TranslatableComponent("gui.bigreactors.fluidizer.controller.off.line1"));

        onOff.setLayoutEngineHint(FixedLayoutEngine.hint(0, 0, 50, 16));
        commandPanel.addControl(onOff);

        this.addDataBinding(isActive, this._indicator);
    }

    //endregion
    //region internals

    private void onActiveStateChanged(final SwitchButton button) {

        if (!this.isDataUpdateInProgress()) {
            this.sendCommandToServer(button.getActive() ?
                    CommonConstants.COMMAND_ACTIVATE : CommonConstants.COMMAND_DEACTIVATE);
        }
    }

    private Panel vSeparatorPanel() {

        final Panel p = new Panel(this);
        final Static s = new Static(this, 1, VBARPANEL_HEIGHT);

        p.setDesiredDimension(11, VBARPANEL_HEIGHT);
        p.setLayoutEngine(new FixedLayoutEngine());

        s.setColor(Colour.BLACK);
        s.setLayoutEngineHint(FixedLayoutEngine.hint(5, 0, 1, VBARPANEL_HEIGHT));

        p.addControl(s);

        return p;
    }

    private Panel vCommandPanel() {

        final Panel p = new Panel(this);

        p.setDesiredDimension(DesiredDimension.Height, VBARPANEL_HEIGHT);
        p.setLayoutEngine(new FixedLayoutEngine());

        return p;
    }

    private class RecipePanel
            extends PanelGroup<IFluidizerRecipe.Type> {

        public RecipePanel() {

            super(FluidizerControllerScreen.this, "recipes", IFluidizerRecipe.Type.values());
            this.setDesiredDimension(108, VBARPANEL_HEIGHT);

            final FluidizerControllerContainer container = getMenu();

            addDataBinding(container::getRecipeType, this::setActivePanel);
            addDataBinding(container::getRecipeProgress, this::setProgress);

            this._outputFluidBar = new FluidBar(FluidizerControllerScreen.this, "outbar", container.getOutputCapacity(),
                    () -> (double)container.getOutputAmount(), container::getOutput, BaseIcons.Bucket, "gui.bigreactors.fluidizer.controller.fluidbar.line1", null);
            this._leftProgressBar = this.progressBar(true, FluidizerIcons.ProgressLeft, FluidizerIcons.ProgressLeftFilled);
            this._rightProgressBar = this.progressBar(false, FluidizerIcons.ProgressRight, FluidizerIcons.ProgressRightFilled);

            this._solidIngredients = new Static[2];
            Arrays.setAll(this._solidIngredients, i -> this.solidSlot());

            for (int i = 0; i < 2; ++i) {

                final int index = i;

                this._solidIngredients[index] = this.solidSlot();
                addDataBinding(() -> container.getSolidInput(index), stack -> this.setInput(index, stack));
            }

            this._fluidIngredients = new FluidBar[2];
            Arrays.setAll(this._fluidIngredients, i -> new FluidBar(FluidizerControllerScreen.this, "inputfluid" + i,
                    FluidizerFluidInjectorEntity.MAX_CAPACITY, () -> (double)(container.getFluidInputAmount(i)),
                    () -> container.getFluidInput(i), Sprite.EMPTY_SUPPLIER, "gui.bigreactors.fluidizer.controller.inputfuel.title", null));

            // sub panels

            this.setPanel(IFluidizerRecipe.Type.Invalid, this.noRecipePanel());
            this.setPanel(IFluidizerRecipe.Type.Solid, this.solidRecipePanel());
            this.setPanel(IFluidizerRecipe.Type.SolidMixing, this.solidMixingRecipePanel());
            this.setPanel(IFluidizerRecipe.Type.FluidMixing, this.fluidMixingRecipePanel());
            this.setActivePanel(IFluidizerRecipe.Type.Invalid);
        }

        public void setProgress(final double progress) {

            this._leftProgressBar.setValue(progress);
            this._rightProgressBar.setValue(progress);
        }

        public void setInput(final int index, final ItemStack stack) {
            this._solidIngredients[index].setStackAsInventory(stack);
        }

        //region internals

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

        private Static solidSlot() {

            final Static s = new Static(FluidizerControllerScreen.this, 18, 18);

            s.setLayoutEngineHint(TabularLayoutEngine.hintBuilder().setPadding(Padding.get(0, 0, 18, 0)).build());
            s.setPadding(1);
            return s;
        }

        private Panel panel() {

            final Panel p = new Panel(FluidizerControllerScreen.this);

            p.setDesiredDimension(108, VBARPANEL_HEIGHT);
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
        private final Static[] _solidIngredients;
        private final FluidBar[] _fluidIngredients;
        private final GaugeBar _leftProgressBar;
        private final GaugeBar _rightProgressBar;

        //endregion
    }

    private final static int VBARPANEL_HEIGHT = 84;

    private final EnergyBar _energyBar;
    private final RecipePanel _recipePanel;
    private final MachineStatusIndicator _indicator;

    //endregion
}
