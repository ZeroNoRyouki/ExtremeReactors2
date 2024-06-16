/*
 * EnergizerControllerScreen
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * Do not remove or edit this header
 *
 */

package it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.client.screen;

import it.zerono.mods.extremereactors.CommonLocations;
import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonMultiblockScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.MultiBlockEnergizer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.container.EnergizerControllerContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.part.EnergizerControllerEntity;
import it.zerono.mods.zerocore.base.client.screen.BaseScreenToolTipsBuilder;
import it.zerono.mods.zerocore.base.client.screen.control.*;
import it.zerono.mods.zerocore.lib.client.gui.DesiredDimension;
import it.zerono.mods.zerocore.lib.client.gui.control.Panel;
import it.zerono.mods.zerocore.lib.client.gui.control.SwitchButton;
import it.zerono.mods.zerocore.lib.client.gui.layout.AnchoredLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.layout.HorizontalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.sprite.SpriteTextureMap;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.text.TextHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class EnergizerControllerScreen
        extends CommonMultiblockScreen<MultiBlockEnergizer, EnergizerControllerEntity, EnergizerControllerContainer> {

    public EnergizerControllerScreen(EnergizerControllerContainer container, Inventory inventory, Component title) {

        super(container, inventory, PlayerInventoryUsage.None, title,
                () -> new SpriteTextureMap(CommonLocations.TEXTURES_GUI_MULTIBLOCK
                        .buildWithSuffix("basic_background.png"), 256, 256));

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, CommonLocations.ENERGIZER.buildWithSuffix("part-controller"), 1);

        this._displaySystem = EnergySystem.ForgeEnergy;

        this._energyBar = new EnergyBar(this, "energyBar", EnergySystem.REFERENCE,
                container.getEnergyCapacity(), container.ENERGY_STORED, "gui.bigreactors.energizer.controller.energybar.tooltip.body");

        this._infoDisplay = new InformationDisplay(this, "info",
                layout -> layout.columns(1).rows(3, 20, 20, 20));

        this._infoDisplay.setDesiredDimension(DesiredDimension.Width, 106);

        this._infoDisplay.addInformationCell(builder -> builder
                .name("stored")
                .bindText(container.ENERGY_STORED, value -> this._displaySystem.asHumanReadableNumber(value))
                .icon(CommonIcons.PowerBattery)
                .useTooltipsFrom(this._energyBar));

        this._infoDisplay.addInformationCell(builder -> builder
                .name("io")
                .bindText(container.ENERGY_IO, value -> this._displaySystem.asHumanReadableNumber(value))
                .icon(CommonIcons.EnergyRatioIcon)
                .tooltips(new BaseScreenToolTipsBuilder()
                        .addTranslatableAsTitle("gui.bigreactors.energizer.controller.io.tooltip.title")
                        .addTranslatableAsValue("gui.bigreactors.energizer.controller.io.tooltip.value")
                        .addEmptyLine()
                        .addTranslatable("gui.bigreactors.energizer.controller.io.tooltip.body")
                        .addBindableObjectAsValue(container.ENERGY_IO, value ->
                                TextHelper.literal(this._displaySystem.asHumanReadableNumber(value) + "/t"))));

        final String capacity = this._displaySystem.asHumanReadableNumber(container.getEnergyCapacity());

        this._infoDisplay.addInformationCell(builder -> builder
                .name("capacity")
                .text(capacity)
                .icon(CommonIcons.ButtonSensorOutputEnergyAmount)
                .tooltips(new BaseScreenToolTipsBuilder()
                    .addTranslatableAsTitle("gui.bigreactors.energizer.controller.capacity.tooltip.title")
                    .addTranslatableAsValue("gui.bigreactors.energizer.controller.capacity.tooltip.value", capacity)
                    .addEmptyLine()
                    .addTranslatable("gui.bigreactors.energizer.controller.capacity.tooltip.body")));

        this._detailsDisplay = new InformationDisplay(this, "details",
                layout -> layout.columns(2).rows(2, 20, 20));

        this._detailsDisplay.addInformationCell(builder -> builder
                .name("input")
                .bindText(container.ENERGY_LAST_INPUT, value -> this._displaySystem.asHumanReadableNumber(value))
                .icon(CommonIcons.DirectionInput)
                .tooltips(new BaseScreenToolTipsBuilder()
                    .addTranslatableAsTitle("gui.bigreactors.energizer.controller.input.tooltip.title")
                    .addTranslatableAsValue("gui.bigreactors.energizer.controller.input.tooltip.value")
                    .addEmptyLine()
                    .addTranslatable("gui.bigreactors.energizer.controller.input.tooltip.body")
                    .addBindableObjectAsValue(container.ENERGY_LAST_INPUT, value ->
                            TextHelper.literal(this._displaySystem.asHumanReadableNumber(value) + "/t"))));

        this._detailsDisplay.addInformationCell(builder -> builder
                .name("output")
                .bindText(container.ENERGY_LAST_OUTPUT, value -> this._displaySystem.asHumanReadableNumber(value))
                .icon(CommonIcons.DirectionOutput)
                .tooltips(new BaseScreenToolTipsBuilder()
                    .addTranslatableAsTitle("gui.bigreactors.energizer.controller.output.tooltip.title")
                    .addTranslatableAsValue("gui.bigreactors.energizer.controller.output.tooltip.value")
                    .addEmptyLine()
                    .addTranslatable("gui.bigreactors.energizer.controller.output.tooltip.body")
                    .addBindableObjectAsValue(container.ENERGY_LAST_OUTPUT, value ->
                            TextHelper.literal(this._displaySystem.asHumanReadableNumber(value) + "/t"))));

        // commands...

        this._onOff = new OnOff(this, 25, 16, container.ACTIVE, this::onActiveStateChanged,
                TextHelper.translatable("gui.bigreactors.energizer.controller.on.title"),
                TextHelper.translatable("gui.bigreactors.energizer.controller.off.title"));

        this.setContentBounds(14, 0);
    }

    //region AbstractMultiblockScreen

    @Override
    protected MachineStatusIndicator createStatusIndicator(EnergizerControllerContainer container) {
        return this.createEnergizerStatusIndicator(container.ACTIVE);
    }

    @Override
    protected void onScreenCreate() {

        super.onScreenCreate();

        this.setContentLayoutEngine(new VerticalLayoutEngine()
                .setZeroMargins()
                .setControlsSpacing(2)
                .setVerticalAlignment(VerticalAlignment.Top)
                .setHorizontalAlignment(HorizontalAlignment.Left));

        // COMMANDS

        final Panel commandPanel = CommonPanels.verticalCommandPanel(this, 50);

        commandPanel.setLayoutEngine(new AnchoredLayoutEngine().setZeroMargins());

        // - machine on/off
        this._onOff.setLayoutEngineHint(AnchoredLayoutEngine.Anchor.Top);
        commandPanel.addControl(this._onOff);

        // BARS

        final BarsPanel barsPanel = new BarsPanel(this, "bars")
                .add(this._energyBar)
                .addVerticalSeparator()
                .add(this._infoDisplay)
                .addVerticalSeparator()
                .add(commandPanel);

        this.addControl(barsPanel);
        this.addControl(CommonPanels.horizontalSeparator(this, this.getContentWidth()));
        this.addControl(this._detailsDisplay);
    }

    //endregion
    //region internals

    private void onActiveStateChanged(SwitchButton button) {
        this.sendCommandToServer(button.getActive() ?
                CommonConstants.COMMAND_ACTIVATE :
                CommonConstants.COMMAND_DEACTIVATE);
    }

    private final EnergyBar _energyBar;
    private final OnOff _onOff;
    private final InformationDisplay _infoDisplay;
    private final InformationDisplay _detailsDisplay;

    private EnergySystem _displaySystem;

    //endregion
}
