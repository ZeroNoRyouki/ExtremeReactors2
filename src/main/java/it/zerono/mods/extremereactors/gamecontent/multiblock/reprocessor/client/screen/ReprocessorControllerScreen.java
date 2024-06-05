/*
 *
 * ReprocessorControllerScreen.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.client.screen;

import it.zerono.mods.extremereactors.CommonLocations;
import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonMultiblockScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.GuiTheme;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.MultiblockReprocessor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.container.ReprocessorControllerContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.part.ReprocessorControllerEntity;
import it.zerono.mods.zerocore.base.client.screen.BaseIcons;
import it.zerono.mods.zerocore.base.client.screen.BaseScreenToolTipsBuilder;
import it.zerono.mods.zerocore.base.client.screen.control.*;
import it.zerono.mods.zerocore.lib.client.gui.ButtonState;
import it.zerono.mods.zerocore.lib.client.gui.DesiredDimension;
import it.zerono.mods.zerocore.lib.client.gui.Orientation;
import it.zerono.mods.zerocore.lib.client.gui.control.*;
import it.zerono.mods.zerocore.lib.client.gui.layout.*;
import it.zerono.mods.zerocore.lib.client.gui.sprite.Sprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.SpriteTextureMap;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ReprocessorControllerScreen
        extends CommonMultiblockScreen<MultiblockReprocessor, ReprocessorControllerEntity, ReprocessorControllerContainer> {

    public ReprocessorControllerScreen(final ReprocessorControllerContainer container, final Inventory inventory,
                                       final Component title) {

        super(container, inventory, PlayerInventoryUsage.None, title,
                () -> new SpriteTextureMap(CommonLocations.TEXTURES_GUI_MULTIBLOCK.buildWithSuffix("basic_background.png"), 256, 256));

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, CommonLocations.REPROCESSOR.buildWithSuffix("part-controller"), 1);

        this._inputStack = new ItemStackDisplay(this, "input");
        this._inputStack.bindStack(container.ITEM_INPUT_STACK);
        this._outputStack = new ItemStackDisplay(this, "output");
        this._outputStack.bindStack(container.ITEM_OUTPUT_STACK);

        this._energyBar = new EnergyBar(this, "energyBar", EnergySystem.ForgeEnergy, container.getEnergyCapacity(),
                container.ENERGY_STORED, null);

        this._fluidBar = new FluidBar(this, "fluidBar", container.getOutputCapacity(), container.FLUID_INPUT_STACK,
                BaseIcons.Bucket, "gui.bigreactors.reprocessor.controller.coolantbar.title", null);

        this._progressBar = new GaugeBar(this, "progress", 1.0, container.RECIPE_PROGRESS, Sprite.EMPTY);
        this._progressBar.setDesiredDimension(16, 32);
        this._progressBar.setBarSprite(new SpriteTextureMap(CommonLocations.TEXTURES_GUI_MULTIBLOCK.buildWithSuffix("reprocessor_controller_arrow_filled.png"), 16, 32)
                .sprite().ofSize(16, 32).build());
        this._progressBar.setOverlay(new SpriteTextureMap(CommonLocations.TEXTURES_GUI_MULTIBLOCK.buildWithSuffix("reprocessor_controller_arrow.png"), 16, 32)
                .sprite().ofSize(16, 32).build());
        this._progressBar.setOrientation(Orientation.TopToBottom);

        this._onOff = new OnOff(this, this.getMenu().ACTIVE, this::onActiveStateChanged,
                Component.translatable("gui.bigreactors.reprocessor.controller.on.title"),
                Component.translatable("gui.bigreactors.reprocessor.controller.off.title"));

        this._voidFluid = new Button(this, "voidfluid", "");
        this._voidFluid.Clicked.subscribe(this::onVoidFluid);
        this._voidFluid.setIconForState(CommonIcons.TrashCan.get(), ButtonState.Default);
        this._voidFluid.enablePaintBlending(true);

        this._voidFluid.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.reprocessor.controller.voidfluid.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.reprocessor.controller.voidfluid.body"));

        this.setTheme(GuiTheme.ER.get());
    }

    //region CommonMultiblockScreen

    @Override
    protected MachineStatusIndicator createStatusIndicator(ReprocessorControllerContainer container) {
        return this.createReprocessorStatusIndicator(container.ACTIVE);
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
        mainPanel.setLayoutEngine(new HorizontalLayoutEngine()
                .setZeroMargins()
                .setVerticalAlignment(VerticalAlignment.Top)
                .setHorizontalAlignment(HorizontalAlignment.Left));
        this.addControl(mainPanel);

        // BARS

        // - energy bar
        mainPanel.addControl(this._energyBar);
        // - separator
        mainPanel.addControl(CommonPanels.verticalSeparator(this));
        // - fluid bar
        mainPanel.addControl(this._fluidBar);
        // - separator
        mainPanel.addControl(CommonPanels.verticalSeparator(this));

        // RECIPE PANEL

        final Panel recipePanel = new Panel(this);

        mainPanel.addControl(recipePanel);

        recipePanel.setLayoutEngine(new VerticalLayoutEngine());
        recipePanel.setDesiredDimension(DesiredDimension.Width, 64);
        // - recipe input
        recipePanel.addControl(this._inputStack);
        // - recipe progress
        recipePanel.addControl(this._progressBar);
        // - recipe output
        recipePanel.addControl(this._outputStack);

        // - separator
        mainPanel.addControl(CommonPanels.verticalSeparator(this));

        // COMMANDS

        final Panel commandPanel = CommonPanels.verticalCommandPanel(this);

        mainPanel.addControl(commandPanel);

        // - machine on/off

        this._onOff.setLayoutEngineHint(FixedLayoutEngine.hint(0, 0, 50, 16));
        commandPanel.addControl(this._onOff);

        // - void fluid

        this._voidFluid.setLayoutEngineHint(FixedLayoutEngine.hint(17, 28, 18, 18));
        this._voidFluid.setPadding(1);

        commandPanel.addControl(this._voidFluid);
    }

    //endregion
    //region internals

    private void onActiveStateChanged(final SwitchButton button) {

        if (!this.isDataUpdateInProgress()) {
            this.sendCommandToServer(button.getActive() ?
                    CommonConstants.COMMAND_ACTIVATE :
                    CommonConstants.COMMAND_DEACTIVATE);
        }
    }

    private void onVoidFluid(final Button button, final Integer mouseButton) {
        this.sendCommandToServer(CommonConstants.COMMAND_VOID_FLUID);
    }

    private final ItemStackDisplay _inputStack, _outputStack;
    private final EnergyBar _energyBar;
    private final FluidBar _fluidBar;
    private final GaugeBar _progressBar;
    private final OnOff _onOff;
    private final Button _voidFluid;

    //endregion
}
