/*
 *
 * ReactorRedstonePortScreen.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.screen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.AbstractMultiblockScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorRedstonePortEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.sensor.SensorBehavior;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.sensor.SensorSetting;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.sensor.SensorType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.ButtonState;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.control.*;
import it.zerono.mods.zerocore.lib.client.gui.databind.BindingGroup;
import it.zerono.mods.zerocore.lib.client.gui.databind.MonoConsumerBinding;
import it.zerono.mods.zerocore.lib.client.gui.layout.FixedLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.layout.HorizontalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static it.zerono.mods.zerocore.lib.CodeHelper.TEXT_EMPTY_LINE;

public class ReactorRedstonePortScreen
        extends AbstractMultiblockScreen<MultiblockReactor, ReactorRedstonePortEntity, ModTileContainer<ReactorRedstonePortEntity>> {

    public ReactorRedstonePortScreen(final ModTileContainer<ReactorRedstonePortEntity> container,
                                   final PlayerInventory inventory, final ITextComponent title) {

        super(container, inventory, PlayerInventoryUsage.None, title,
                mainTextureFromVariant(container.getTileEntity().getMultiblockVariant().orElse(ReactorVariant.Basic)));
        this.ignoreCloseOnInventoryKey(true);
        this._bindings = new BindingGroup();

        this._disableButton = new Button(this, "disable", new TranslationTextComponent("gui.bigreactors.generic.disable"));
        this._saveButton = new Button(this, "save", new TranslationTextComponent("gui.bigreactors.generic.save"));
        this._resetButton = new Button(this, "reset", new TranslationTextComponent("gui.bigreactors.generic.reset"));

        this._activeSensorName = new Label(this, "activename", "");
        this._sensorsGroup = new PanelGroup<>(this, "behaviors", SensorType.values());
        this._guiMap = new SettingControlsMap(this._activeSensorName, this._sensorsGroup);

        this._inputActiveBehavior = new ChoiceText<>(this, "inputActiveBehavior", SensorType.inputActive.getBehaviors());

        this._inputEjectWasteBehavior = new ChoiceText<>(this, "inputEjectWasteBehavior", SensorType.inputEjectWaste.getBehaviors());

        this._inputSetControlRodBehavior = new ChoiceText<>(this, "inputSetControlRodBehavior", SensorType.inputSetControlRod.getBehaviors());
        this._inputSetControlRodWhileOn = this.inputTextPercentage("inputSetControlRodWhileOn");
        this._inputSetControlRodWhileOff = this.inputTextPercentage("inputSetControlRodWhileOff");
        this._inputSetControlRodInsertBy = this.inputTextPercentage("inputSetControlRodInsertBy");
        this._inputSetControlRodRetractBy = this.inputTextPercentage("inputSetControlRodRetractBy");
        this._inputSetControlRodSetTo = this.inputTextPercentage("inputSetControlRodSetTo");

        this._outputFuelTemperatureBehavior = new ChoiceText<>(this, "outputFuelTemperatureBehavior", SensorType.outputFuelTemperature.getBehaviors());
        this._outputFuelTemperatureAbove = this.inputTextNumber("outputFuelTemperatureAbove", " C");
        this._outputFuelTemperatureBelow = this.inputTextNumber("outputFuelTemperatureBelow", " C");

        this._outputCasingTemperatureBehavior = new ChoiceText<>(this, "outputCasingTemperatureBehavior", SensorType.outputCasingTemperature.getBehaviors());
        this._outputCasingTemperatureAbove = this.inputTextNumber("outputCasingTemperatureAbove", " C");
        this._outputCasingTemperatureBelow = this.inputTextNumber("outputCasingTemperatureBelow", " C");

        this._outputFuelRichnessBehavior = new ChoiceText<>(this, "outputFuelRichnessBehavior", SensorType.outputFuelRichness.getBehaviors());
        this._outputFuelRichnessAbove = this.inputTextPercentage("outputFuelRichnessAbove");
        this._outputFuelRichnessBelow = this.inputTextPercentage("outputFuelRichnessBelow");

        this._outputFuelAmountBehavior = new ChoiceText<>(this, "outputFuelAmountBehavior", SensorType.outputFuelAmount.getBehaviors());
        this._outputFuelAmountAbove = this.inputTextNumber("outputFuelAmountAbove", " mB");
        this._outputFuelAmountBelow = this.inputTextNumber("outputFuelAmountBelow", " mB");

        this._outputWasteAmountBehavior = new ChoiceText<>(this, "outputWasteAmountBehavior", SensorType.outputWasteAmount.getBehaviors());
        this._outputWasteAmountAbove = this.inputTextNumber("outputWasteAmountAbove", " mB");
        this._outputWasteAmountBelow = this.inputTextNumber("outputWasteAmountBelow", " mB");

        this._outputEnergyAmountBehavior = new ChoiceText<>(this, "outputEnergyAmountBehavior", SensorType.outputEnergyAmount.getBehaviors());
        this._outputEnergyAmountAbove = this.inputTextPercentage("outputEnergyAmountAbove");
        this._outputEnergyAmountBelow = this.inputTextPercentage("outputEnergyAmountBelow");
    }

    //region ContainerScreen

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    //endregion
    //region AbstractMultiblockScreen

    @Override
    protected void onScreenCreate() {

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, ExtremeReactors.newID("reactor/part-redstoneport"), 1);

        Panel p;
        PanelGroup<SensorBehavior> behaviorGroup;
        IControl c;

        super.onScreenCreate();

        // - sensor panel

        final Panel sensorsPanel = new Panel(this, "sensorsPanel");
        SwitchPictureButton sensorButton;
        int sensorButtonRowY = 0;

        c = new Label(this, "sensorlistlabel", new TranslationTextComponent("gui.bigreactors.reactor.redstoneport.sensortype.sensorlistlabel")./*getFormattedText*/getString());
        c.setPadding(0);
        c.setLayoutEngineHint(FixedLayoutEngine.hint(13, 4, this.getGuiWidth() - 36, 10));
        this.addControl(c);

        sensorsPanel.setPadding(0);
        sensorsPanel.setLayoutEngineHint(FixedLayoutEngine.hint(13, 18, 62, 88));
        this.addControl(sensorsPanel);

        // - sensors sub-behaviors controls

        final int behaviorPanelWidth = this.getGuiWidth() - 91;
        final int behaviorPanelHeight = 88;

        this._activeSensorName.setLayoutEngineHint(FixedLayoutEngine.hint(80, 4, behaviorPanelWidth, 10));
        this.addControl(this._activeSensorName);

        this._sensorsGroup.setLayoutEngineHint(FixedLayoutEngine.hint(78, 18, behaviorPanelWidth, behaviorPanelHeight));
        this.addControl(this._sensorsGroup);

        // -- inputActive

        sensorButton = this.sensorButton(0, sensorButtonRowY, SensorType.inputActive, CommonIcons.ButtonSensorInputActivate, CommonIcons.ButtonSensorInputActivateActive);
        sensorsPanel.addControl(sensorButton);

        p = this.sensorPanel(SensorType.inputActive, behaviorPanelWidth);
        this.setupBehaviorChoiceControl(SensorType.inputActive, this._inputActiveBehavior, null, behaviorPanelWidth);
        p.addControl(this._inputActiveBehavior);

        this._guiMap.add(SensorType.inputActive, new SettingControlsMap.SensorEntry(sensorButton, this._inputActiveBehavior));

        // -- inputEjectWaste

        sensorButton = this.sensorButton(22, sensorButtonRowY, SensorType.inputEjectWaste, CommonIcons.ButtonDumpWaste, CommonIcons.ButtonDumpWasteActive);
        sensorsPanel.addControl(sensorButton);

        p = this.sensorPanel(SensorType.inputEjectWaste, behaviorPanelWidth);
        this.setupBehaviorChoiceControl(SensorType.inputEjectWaste, this._inputEjectWasteBehavior, null, behaviorPanelWidth);
        this._inputEjectWasteBehavior.setEnabled(false);
        p.addControl(this._inputEjectWasteBehavior);

        this._guiMap.add(SensorType.inputEjectWaste, new SettingControlsMap.SensorEntry(sensorButton, this._inputEjectWasteBehavior));

        // -- inputSetControlRod

        sensorButton = this.sensorButton(44, sensorButtonRowY, SensorType.inputSetControlRod, CommonIcons.ButtonSensorInputSetControlRod, CommonIcons.ButtonSensorInputSetControlRodActive);
        sensorsPanel.addControl(sensorButton);
        sensorButtonRowY += 34;

        behaviorGroup = new PanelGroup<>(this, "inputSetControlRodSG", this._inputSetControlRodBehavior.getValidIndices());

        p = this.sensorPanel(SensorType.inputSetControlRod, behaviorPanelWidth);
        this.setupBehaviorChoiceControl(SensorType.inputSetControlRod, this._inputSetControlRodBehavior, behaviorGroup, behaviorPanelWidth);

        p.addControl(this._inputSetControlRodBehavior);
        p.addControl(behaviorGroup);

        p = this.behaviorPanel(SensorBehavior.SetFromSignal, behaviorPanelWidth, behaviorGroup);
        p.addControl(behaviorLabel("whileon", "gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.whileon", behaviorPanelWidth));
        p.addControl(this._inputSetControlRodWhileOn);
        p.addControl(behaviorLabel("whileoff", "gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.whileoff", behaviorPanelWidth));
        p.addControl(this._inputSetControlRodWhileOff);

        this.behaviorPanel(SensorBehavior.SetFromSignalLevel, behaviorPanelWidth, behaviorGroup);

        p = this.behaviorPanel(SensorBehavior.SetOnPulse, behaviorPanelWidth, behaviorGroup);
        p.addControl(behaviorLabel("setto", "gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.setto", behaviorPanelWidth));
        p.addControl(this._inputSetControlRodSetTo);

        p = this.behaviorPanel(SensorBehavior.InsertOnPulse, behaviorPanelWidth, behaviorGroup);
        p.addControl(behaviorLabel("insertby", "gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.insertby", behaviorPanelWidth));
        p.addControl(this._inputSetControlRodInsertBy);

        p = this.behaviorPanel(SensorBehavior.RetractOnPulse, behaviorPanelWidth, behaviorGroup);
        p.addControl(behaviorLabel("retractby", "gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.retractby", behaviorPanelWidth));
        p.addControl(this._inputSetControlRodRetractBy);
        
        behaviorGroup.setActivePanel(SensorBehavior.SetFromSignal);

        this._guiMap.add(SensorType.inputSetControlRod, new SettingControlsMap.SensorEntry(sensorButton, this._inputSetControlRodBehavior,
                new SettingControlsMap.BehaviorEntry(SensorBehavior.SetFromSignal, this._inputSetControlRodWhileOn, this._inputSetControlRodWhileOff),
                new SettingControlsMap.BehaviorEntry(SensorBehavior.SetOnPulse, this._inputSetControlRodSetTo),
                new SettingControlsMap.BehaviorEntry(SensorBehavior.InsertOnPulse, this._inputSetControlRodInsertBy),
                new SettingControlsMap.BehaviorEntry(SensorBehavior.RetractOnPulse, this._inputSetControlRodRetractBy)));

        // -- separator

        Static s = new Static(this, 62, 1);

        s.setColor(Colour.BLACK);
        s.setLayoutEngineHint(FixedLayoutEngine.hint(0, sensorButtonRowY - 8, 62, 1));
        sensorsPanel.addControl(s);

        // -- outputCasingTemperature

        sensorButton = this.sensorButton(0, sensorButtonRowY, SensorType.outputCasingTemperature, CommonIcons.ButtonSensorOutputCasingTemperature, CommonIcons.ButtonSensorOutputCasingTemperatureActive);
        sensorsPanel.addControl(sensorButton);

        behaviorGroup = new PanelGroup<>(this, "outputCasingTemperatureSG", this._outputCasingTemperatureBehavior.getValidIndices());

        p = this.sensorPanel(SensorType.outputCasingTemperature, behaviorPanelWidth);
        this.setupBehaviorChoiceControl(SensorType.outputCasingTemperature, this._outputCasingTemperatureBehavior, behaviorGroup, behaviorPanelWidth);

        p.addControl(this._outputCasingTemperatureBehavior);
        p.addControl(behaviorGroup);

        p = this.behaviorPanel(SensorBehavior.ActiveWhileAbove, behaviorPanelWidth, behaviorGroup);
        p.addControl(behaviorLabel("temp", "gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.temperature", behaviorPanelWidth));
        p.addControl(this._outputCasingTemperatureAbove);

        p = this.behaviorPanel(SensorBehavior.ActiveWhileBelow, behaviorPanelWidth, behaviorGroup);
        p.addControl(behaviorLabel("temp", "gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.temperature", behaviorPanelWidth));
        p.addControl(this._outputCasingTemperatureBelow);

        behaviorGroup.setActivePanel(SensorBehavior.ActiveWhileAbove);

        this._guiMap.add(SensorType.outputCasingTemperature, new SettingControlsMap.SensorEntry(sensorButton, this._outputCasingTemperatureBehavior,
                new SettingControlsMap.BehaviorEntry(SensorBehavior.ActiveWhileAbove, this._outputCasingTemperatureAbove),
                new SettingControlsMap.BehaviorEntry(SensorBehavior.ActiveWhileBelow, this._outputCasingTemperatureBelow)));

        // -- outputFuelTemperature

        sensorButton = this.sensorButton(22, sensorButtonRowY, SensorType.outputFuelTemperature, CommonIcons.ButtonSensorOutputFuelTemperature, CommonIcons.ButtonSensorOutputFuelTemperatureActive);
        sensorsPanel.addControl(sensorButton);

        behaviorGroup = new PanelGroup<>(this, "outputFuelTemperatureSG", this._outputFuelTemperatureBehavior.getValidIndices());

        p = this.sensorPanel(SensorType.outputFuelTemperature, behaviorPanelWidth);
        this.setupBehaviorChoiceControl(SensorType.outputFuelTemperature, this._outputFuelTemperatureBehavior, behaviorGroup, behaviorPanelWidth);

        p.addControl(this._outputFuelTemperatureBehavior);
        p.addControl(behaviorGroup);

        p = this.behaviorPanel(SensorBehavior.ActiveWhileAbove, behaviorPanelWidth, behaviorGroup);
        p.addControl(behaviorLabel("temp", "gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.temperature", behaviorPanelWidth));
        p.addControl(this._outputFuelTemperatureAbove);

        p = this.behaviorPanel(SensorBehavior.ActiveWhileBelow, behaviorPanelWidth, behaviorGroup);
        p.addControl(behaviorLabel("temp", "gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.temperature", behaviorPanelWidth));
        p.addControl(this._outputFuelTemperatureBelow);

        behaviorGroup.setActivePanel(SensorBehavior.ActiveWhileAbove);

        this._guiMap.add(SensorType.outputFuelTemperature, new SettingControlsMap.SensorEntry(sensorButton, this._outputFuelTemperatureBehavior,
                new SettingControlsMap.BehaviorEntry(SensorBehavior.ActiveWhileAbove, this._outputFuelTemperatureAbove),
                new SettingControlsMap.BehaviorEntry(SensorBehavior.ActiveWhileBelow, this._outputFuelTemperatureBelow)));

        // -- outputFuelRichness

        sensorButton = this.sensorButton(44, sensorButtonRowY, SensorType.outputFuelRichness, CommonIcons.ButtonSensorOutputFuelMix, CommonIcons.ButtonSensorOutputFuelMixActive);
        sensorsPanel.addControl(sensorButton);
        sensorButtonRowY += 22;

        behaviorGroup = new PanelGroup<>(this, "outputFuelRichnessSG", this._outputFuelRichnessBehavior.getValidIndices());

        p = this.sensorPanel(SensorType.outputFuelRichness, behaviorPanelWidth);
        this.setupBehaviorChoiceControl(SensorType.outputFuelRichness, this._outputFuelRichnessBehavior, behaviorGroup, behaviorPanelWidth);

        p.addControl(this._outputFuelRichnessBehavior);
        p.addControl(behaviorGroup);

        p = this.behaviorPanel(SensorBehavior.ActiveWhileAbove, behaviorPanelWidth, behaviorGroup);
        p.addControl(behaviorLabel("temp", "gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.richness", behaviorPanelWidth));
        p.addControl(this._outputFuelRichnessAbove);

        p = this.behaviorPanel(SensorBehavior.ActiveWhileBelow, behaviorPanelWidth, behaviorGroup);
        p.addControl(behaviorLabel("temp", "gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.richness", behaviorPanelWidth));
        p.addControl(this._outputFuelRichnessBelow);

        behaviorGroup.setActivePanel(SensorBehavior.ActiveWhileAbove);

        this._guiMap.add(SensorType.outputFuelRichness, new SettingControlsMap.SensorEntry(sensorButton, this._outputFuelRichnessBehavior,
                new SettingControlsMap.BehaviorEntry(SensorBehavior.ActiveWhileAbove, this._outputFuelRichnessAbove),
                new SettingControlsMap.BehaviorEntry(SensorBehavior.ActiveWhileBelow, this._outputFuelRichnessBelow)));

        // -- outputFuelAmount

        sensorButton = this.sensorButton(0, sensorButtonRowY, SensorType.outputFuelAmount, CommonIcons.ButtonSensorOutputFuelAmount, CommonIcons.ButtonSensorOutputFuelAmountActive);
        sensorsPanel.addControl(sensorButton);

        behaviorGroup = new PanelGroup<>(this, "outputFuelAmountSG", this._outputFuelAmountBehavior.getValidIndices());

        p = this.sensorPanel(SensorType.outputFuelAmount, behaviorPanelWidth);
        this.setupBehaviorChoiceControl(SensorType.outputFuelAmount, this._outputFuelAmountBehavior, behaviorGroup, behaviorPanelWidth);

        p.addControl(this._outputFuelAmountBehavior);
        p.addControl(behaviorGroup);

        p = this.behaviorPanel(SensorBehavior.ActiveWhileAbove, behaviorPanelWidth, behaviorGroup);
        p.addControl(behaviorLabel("temp", "gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.amount", behaviorPanelWidth));
        p.addControl(this._outputFuelAmountAbove);

        p = this.behaviorPanel(SensorBehavior.ActiveWhileBelow, behaviorPanelWidth, behaviorGroup);
        p.addControl(behaviorLabel("temp", "gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.amount", behaviorPanelWidth));
        p.addControl(this._outputFuelAmountBelow);

        behaviorGroup.setActivePanel(SensorBehavior.ActiveWhileAbove);

        this._guiMap.add(SensorType.outputFuelAmount, new SettingControlsMap.SensorEntry(sensorButton, this._outputFuelAmountBehavior,
                new SettingControlsMap.BehaviorEntry(SensorBehavior.ActiveWhileAbove, this._outputFuelAmountAbove),
                new SettingControlsMap.BehaviorEntry(SensorBehavior.ActiveWhileBelow, this._outputFuelAmountBelow)));

        // -- outputWasteAmount

        sensorButton = this.sensorButton(22, sensorButtonRowY, SensorType.outputWasteAmount, CommonIcons.ButtonSensorOutputWasteAmount, CommonIcons.ButtonSensorOutputWasteAmountActive);
        sensorsPanel.addControl(sensorButton);

        behaviorGroup = new PanelGroup<>(this, "outputWasteAmountSG", this._outputWasteAmountBehavior.getValidIndices());

        p = this.sensorPanel(SensorType.outputWasteAmount, behaviorPanelWidth);
        this.setupBehaviorChoiceControl(SensorType.outputWasteAmount, this._outputWasteAmountBehavior, behaviorGroup, behaviorPanelWidth);

        p.addControl(this._outputWasteAmountBehavior);
        p.addControl(behaviorGroup);

        p = this.behaviorPanel(SensorBehavior.ActiveWhileAbove, behaviorPanelWidth, behaviorGroup);
        p.addControl(behaviorLabel("temp", "gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.amount", behaviorPanelWidth));
        p.addControl(this._outputWasteAmountAbove);

        p = this.behaviorPanel(SensorBehavior.ActiveWhileBelow, behaviorPanelWidth, behaviorGroup);
        p.addControl(behaviorLabel("temp", "gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.amount", behaviorPanelWidth));
        p.addControl(this._outputWasteAmountBelow);

        behaviorGroup.setActivePanel(SensorBehavior.ActiveWhileAbove);

        this._guiMap.add(SensorType.outputWasteAmount, new SettingControlsMap.SensorEntry(sensorButton, this._outputWasteAmountBehavior,
                new SettingControlsMap.BehaviorEntry(SensorBehavior.ActiveWhileAbove, this._outputWasteAmountAbove),
                new SettingControlsMap.BehaviorEntry(SensorBehavior.ActiveWhileBelow, this._outputWasteAmountBelow)));

        // -- outputEnergyAmount

        sensorButton = this.sensorButton(44, sensorButtonRowY, SensorType.outputEnergyAmount, CommonIcons.ButtonSensorOutputEnergyAmount, CommonIcons.ButtonSensorOutputEnergyAmountActive);
        sensorsPanel.addControl(sensorButton);

        behaviorGroup = new PanelGroup<>(this, "outputEnergyAmountSG", this._outputEnergyAmountBehavior.getValidIndices());

        p = this.sensorPanel(SensorType.outputEnergyAmount, behaviorPanelWidth);
        this.setupBehaviorChoiceControl(SensorType.outputEnergyAmount, this._outputEnergyAmountBehavior, behaviorGroup, behaviorPanelWidth);

        p.addControl(this._outputEnergyAmountBehavior);
        p.addControl(behaviorGroup);

        p = this.behaviorPanel(SensorBehavior.ActiveWhileAbove, behaviorPanelWidth, behaviorGroup);
        p.addControl(behaviorLabel("energy", "gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.bufferfilling", behaviorPanelWidth));
        p.addControl(this._outputEnergyAmountAbove);

        p = this.behaviorPanel(SensorBehavior.ActiveWhileBelow, behaviorPanelWidth, behaviorGroup);
        p.addControl(behaviorLabel("energy", "gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.bufferfilling", behaviorPanelWidth));
        p.addControl(this._outputEnergyAmountBelow);

        behaviorGroup.setActivePanel(SensorBehavior.ActiveWhileAbove);

        this._guiMap.add(SensorType.outputEnergyAmount, new SettingControlsMap.SensorEntry(sensorButton, this._outputEnergyAmountBehavior,
                new SettingControlsMap.BehaviorEntry(SensorBehavior.ActiveWhileAbove, this._outputEnergyAmountAbove),
                new SettingControlsMap.BehaviorEntry(SensorBehavior.ActiveWhileBelow, this._outputEnergyAmountBelow)));

        // disable / save / reset

        sensorButtonRowY = 20 + behaviorPanelHeight;

        this._disableButton.setLayoutEngineHint(FixedLayoutEngine.hint(13, sensorButtonRowY, /*50*/62, 16));
        this._disableButton.Clicked.subscribe(this::onDisable);
        this.addControl(this._disableButton);

        this._saveButton.setLayoutEngineHint(FixedLayoutEngine.hint(109, sensorButtonRowY, 50, 16));
        this._saveButton.Clicked.subscribe(this::onSave);
        this.addControl(this._saveButton);

        this._resetButton.setLayoutEngineHint(FixedLayoutEngine.hint(161, sensorButtonRowY, 50, 16));
        this._resetButton.Clicked.subscribe(this::onReset);
        this.addControl(this._resetButton);

        this.setActionButtonsVisibility(false);

        this.addBinding(ReactorRedstonePortEntity::getSettings, this._guiMap::setSettings);
    }

    /**
     * Called when this screen need to be updated after the TileEntity data changed.
     * Override to handle this event
     */
    @Override
    protected void onDataUpdated() {

        super.onDataUpdated();
        this._bindings.update();
    }

    //endregion
    //region internals

    private void onDisable(final Button btn, final int mouseButton) {
        this.sendCommandToServer(ReactorRedstonePortEntity.COMMAND_DISABLE_SENSOR);
    }

    private void onSave(final Button btn, final int mouseButton) {
        this.sendCommandToServer(ReactorRedstonePortEntity.COMMAND_SET_SENSOR, this._guiMap.getSettings().syncDataTo(new CompoundNBT()));
    }

    private void onReset(final Button btn, final int mouseButton) {
        this._guiMap.setSettings(this.getTileEntity().getSettings());
    }

    private void onBehaviorChanged(final ChoiceText<SensorBehavior> choice, final SensorBehavior behavior) {
        choice.<PanelGroup<SensorBehavior>>getTag().ifPresent(group -> group.setActivePanel(behavior));
    }

    private void setActionButtonsVisibility(boolean visible) {

        this._saveButton.setVisible(visible);
        this._resetButton.setVisible(visible);
    }
    
    //region GUI controls helpers

    private SwitchPictureButton sensorButton(final int x, final int y, final SensorType sensor,
                                             final NonNullSupplier<ISprite> defaultSprite,
                                             final NonNullSupplier<ISprite> activeSprite) {

        final SwitchPictureButton button = new SwitchPictureButton(this, sensor.name(), false, "sensortype");
        final String tooltipBaseName = "gui.bigreactors.reactor.redstoneport.sensortype." + CodeHelper.neutralLowercase(sensor.name())  + ".line";

        button.setTag(sensor);
        button.setTooltips(new TranslationTextComponent(tooltipBaseName + "1").setStyle(STYLE_TOOLTIP_TITLE),
                sensor.isInput() ? TEXT_DIRECTION_INPUT : TEXT_DIRECTION_OUTPUT,
                TEXT_EMPTY_LINE, new TranslationTextComponent(tooltipBaseName + "2"), new TranslationTextComponent(tooltipBaseName + "3"));

        this.setButtonSpritesAndOverlayForState(button, ButtonState.Default, defaultSprite.get());
        this.setButtonSpritesAndOverlayForState(button, ButtonState.Active, activeSprite.get());

        button.setLayoutEngineHint(FixedLayoutEngine.hint(x, y, 18, 18));
        button.setBackground(CommonIcons.ImageButtonBackground.get());
        button.enablePaintBlending(true);
        button.setPadding(1);

        button.Activated.subscribe(spb -> spb.<SensorType>getTag().ifPresent(st -> {

            this._sensorsGroup.setActivePanel(st);
            this._activeSensorName.setText(new TranslationTextComponent("gui.bigreactors.reactor.redstoneport.sensortype." +
                    CodeHelper.neutralLowercase(st.name()) + ".line1"));
            this.setActionButtonsVisibility(true);
        }));

        button.Deactivated.subscribe(spb -> {

            this._sensorsGroup.clearActivePanel();
            this.setActionButtonsVisibility(false);
        });

        return button;
    }

    private TextInput inputText(final String name, final String suffix) {

        final TextInput t = new TextInput(this, name, "0");

        t.setDesiredDimension(70, 14);
        t.setFilter(TextConstraints.FILTER_NUMBERS);
        t.setDisplaySuffix(suffix);
        return t;
    }

    private TextInput inputTextPercentage(final String name) {

        final TextInput t = this.inputText(name, "%");

        t.setMaxLength(3);
        t.setDesiredDimension(40, 14);
        t.addConstraint(TextConstraints.CONSTRAINT_PERCENTAGE);
        return t;
    }

    private TextInput inputTextNumber(final String name, final String suffix) {

        final TextInput t = this.inputText(name, suffix);

        t.setMaxLength(10);
        t.addConstraint(TextConstraints.CONSTRAINT_POSITIVE_INTEGER_NUMBER);
        return t;
    }

    private Panel sensorPanel(final SensorType sensor, final int width) {

        final Panel p = new Panel(this, sensor.name());

        p.setLayoutEngineHint(FixedLayoutEngine.hint(0, 0, width, 88));
        p.setLayoutEngine(new VerticalLayoutEngine()
                .setVerticalMargin(0)
                .setHorizontalMargin(2)
                .setHorizontalAlignment(HorizontalAlignment.Left));
        this._sensorsGroup.setPanel(sensor, p);
        return p;
    }

    private Panel behaviorPanel(final SensorBehavior behavior, final int width,
                                final PanelGroup<SensorBehavior> group) {

        final Panel p = new Panel(this, behavior.name());

        p.setLayoutEngineHint(FixedLayoutEngine.hint(0, 0, width, 200));
        p.setLayoutEngine(new VerticalLayoutEngine()
                .setVerticalMargin(0)
                .setHorizontalMargin(4)
                .setControlsSpacing(4)
                .setHorizontalAlignment(HorizontalAlignment.Left));

        group.setPanel(behavior, p);
        return p;
    }

    private Label behaviorLabel(final String name, final String translationKey, final int width) {

        final Label l = new Label(this, name, new TranslationTextComponent(translationKey));

        l.setPadding(0);
        l.setDesiredDimension(width, 10);
        return l;
    }

    private void setupBehaviorChoiceControl(final SensorType sensor, final ChoiceText<SensorBehavior> choice,
                                            @Nullable final PanelGroup<SensorBehavior> behaviorGroup, final int width) {

        final List<SensorBehavior> behaviors = sensor.getBehaviors();

        behaviors.forEach(b -> choice.addText(b, new TranslationTextComponent("gui.bigreactors.reactor.redstoneport.sensorbehavior." +
                CodeHelper.neutralLowercase(b.name()) + ".line1")));

        choice.setSelectedIndex(behaviors.get(0));
        choice.setDesiredDimension(width, 16);

        if (null != behaviorGroup) {

            choice.setTag(behaviorGroup);
            choice.Changed.subscribe(this::onBehaviorChanged);
        }
    }

    //endregion

    private void addBinding(final Function<ReactorRedstonePortEntity, SensorSetting> supplier,
                            final Consumer<SensorSetting> consumer) {
        this._bindings.addBinding(new MonoConsumerBinding<>(this.getTileEntity(), supplier, consumer));
    }

    private static final ITextComponent TEXT_DIRECTION_INPUT = new TranslationTextComponent("gui.bigreactors.reactor.redstoneport.sensortype.input").setStyle(STYLE_TOOLTIP_INFO);
    private static final ITextComponent TEXT_DIRECTION_OUTPUT = new TranslationTextComponent("gui.bigreactors.reactor.redstoneport.sensortype.output").setStyle(STYLE_TOOLTIP_INFO);

    private final BindingGroup _bindings;

    private final Button _disableButton;
    private final Button _saveButton;
    private final Button _resetButton;

    private final Label _activeSensorName;
    private final PanelGroup<SensorType> _sensorsGroup;

    private final ChoiceText<SensorBehavior> _inputActiveBehavior;

    private final ChoiceText<SensorBehavior> _inputEjectWasteBehavior;

    private final ChoiceText<SensorBehavior> _inputSetControlRodBehavior;
    private final TextInput _inputSetControlRodWhileOn;
    private final TextInput _inputSetControlRodWhileOff;
    private final TextInput _inputSetControlRodInsertBy;
    private final TextInput _inputSetControlRodRetractBy;
    private final TextInput _inputSetControlRodSetTo;

    private final ChoiceText<SensorBehavior> _outputFuelTemperatureBehavior;
    private final TextInput _outputFuelTemperatureAbove;
    private final TextInput _outputFuelTemperatureBelow;

    private final ChoiceText<SensorBehavior> _outputCasingTemperatureBehavior;
    private final TextInput _outputCasingTemperatureAbove;
    private final TextInput _outputCasingTemperatureBelow;

    private final ChoiceText<SensorBehavior> _outputFuelRichnessBehavior;
    private final TextInput _outputFuelRichnessAbove;
    private final TextInput _outputFuelRichnessBelow;

    private final ChoiceText<SensorBehavior> _outputFuelAmountBehavior;
    private final TextInput _outputFuelAmountAbove;
    private final TextInput _outputFuelAmountBelow;

    private final ChoiceText<SensorBehavior> _outputWasteAmountBehavior;
    private final TextInput _outputWasteAmountAbove;
    private final TextInput _outputWasteAmountBelow;

    private final ChoiceText<SensorBehavior> _outputEnergyAmountBehavior;
    private final TextInput _outputEnergyAmountAbove;
    private final TextInput _outputEnergyAmountBelow;

    private final SettingControlsMap _guiMap;

    private static class SettingControlsMap {

        public final Label SensorName;
        public final PanelGroup<SensorType> SensorsGroup;
        public final Map<SensorType, SensorEntry> Sensors;

        static class SensorEntry {

            public final SwitchPictureButton SensorControl;
            public final ChoiceText<SensorBehavior> BehaviorControl;
            public final Map<SensorBehavior, BehaviorEntry> Behaviors;

            public SensorEntry(final SwitchPictureButton sensorControl,
                               final ChoiceText<SensorBehavior> behaviorControl, final BehaviorEntry... behaviors) {

                this.SensorControl = sensorControl;
                this.BehaviorControl = behaviorControl;
                //noinspection UnstableApiUsage
                this.Behaviors = Arrays.stream(behaviors).collect(ImmutableMap.toImmutableMap(be -> be.Behavior, be -> be));
            }

            public List<TextInput> inputs(final SensorBehavior behavior) {
                return this.Behaviors.containsKey(behavior) ? this.Behaviors.get(behavior).Inputs : Collections.emptyList();
            }
        }

        static class BehaviorEntry {

            public final SensorBehavior Behavior;
            public final List<TextInput> Inputs;

            public BehaviorEntry(final SensorBehavior behavior, final TextInput... inputs) {

                this.Behavior = behavior;
                this.Inputs = ImmutableList.copyOf(inputs);
            }
        }

        public SettingControlsMap(final Label name, final PanelGroup<SensorType> group) {

            this.SensorName = name;
            this.SensorsGroup = group;
            this.Sensors = Maps.newHashMapWithExpectedSize(SensorType.values().length - 1);
        }

        public void add(final SensorType sensor, final SensorEntry entry) {
            this.Sensors.put(sensor, entry);
        }

        public SensorSetting getSettings() {

            final SensorType sensor = this.SensorsGroup.getActivePanelIndex().orElse(SensorType.Disabled);

            if (SensorType.Disabled != sensor) {

                final SensorEntry se = this.Sensors.get(sensor);
                final SensorBehavior behavior = se.BehaviorControl.getSelectedIndex();
                final List<TextInput> inputs = se.inputs(behavior);
                final int[] values = new int[2];

                for (int idx = 0; idx < Math.min(values.length, inputs.size()); ++idx) {
                    values[idx] = Integer.parseInt(inputs.get(idx).getText());
                }

                return new SensorSetting(sensor, behavior, values[0], values[1]);
            }

            return SensorSetting.DISABLED;
        }

        public void setSettings(final SensorSetting setting) {

            this.resetControls();

            if (SensorType.Disabled != setting.Sensor) {

                final SensorEntry se = this.Sensors.get(setting.Sensor);
                final List<TextInput> inputs = se.inputs(setting.Behavior);
                final int[] values = {setting.Value1, setting.Value2};

                for (int idx = 0; idx < Math.min(values.length, inputs.size()); ++idx) {
                    inputs.get(idx).setText(Integer.toString(values[idx]));
                }

                se.SensorControl.setActive(true);
                se.BehaviorControl.setSelectedIndex(setting.Behavior);

                this.SensorName.setText(new TranslationTextComponent("gui.bigreactors.reactor.redstoneport.sensortype." +
                        CodeHelper.neutralLowercase(setting.Sensor.name()) + ".line1"));
                this.SensorsGroup.setActivePanel(setting.Sensor);
            }
        }

        private void resetControls() {

            this.SensorName.setText("");
            this.SensorsGroup.clearActivePanel();
            this.Sensors.values().forEach(se -> {

                se.SensorControl.setActive(false);
                se.BehaviorControl.setSelectedIndex(se.BehaviorControl.getValidIndices().get(0));
                se.Behaviors.values().forEach(be -> be.Inputs.forEach(i -> i.setText("0")));
            });
        }
    }

    //endregion
}
