/*
 *
 * AbstractRedstonePortScreen.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen;

import com.google.common.collect.Lists;
import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.multiblock.IMachineReader;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.AbstractMultiblockEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.sensor.AbstractSensorSetting;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.sensor.ISensorSettingFactory;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.sensor.ISensorType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.sensor.SensorBehavior;
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
import it.zerono.mods.zerocore.lib.client.gui.sprite.SpriteTextureMap;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockMachine;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static it.zerono.mods.zerocore.lib.CodeHelper.TEXT_EMPTY_LINE;

public abstract class AbstractRedstonePortScreen<Controller extends AbstractCuboidMultiblockController<Controller> & IMultiblockMachine,
                                                T extends AbstractMultiblockEntity<Controller> & MenuProvider,
                                                C extends ModTileContainer<T>,
                                                Reader extends IMachineReader, Writer,
                                                SensorType extends Enum<SensorType> & ISensorType<Reader>,
                                                SensorSetting extends AbstractSensorSetting<Reader, Writer, SensorType>>
        extends AbstractMultiblockScreen<Controller, T, C> {

    @FunctionalInterface
    public interface BehaviorSingleDataValidator {

        void validate(TextInput input, Consumer<Component> errorReport);
    }

    @FunctionalInterface
    public interface BehaviorDoubleDataValidator {

        void validate(TextInput inputA, TextInput inputB, Consumer<Component> errorReport);
    }

    @SafeVarargs
    protected AbstractRedstonePortScreen(final C container, final Inventory inventory,
                                         final PlayerInventoryUsage inventoryUsage, final Component title,
                                         final NonNullSupplier<SpriteTextureMap> mainTextureSupplier,
                                         final ISensorSettingFactory<Reader, Writer, SensorType, SensorSetting> sensorSettingFactory,
                                         final SensorType... validSensors) {

        super(container, inventory, inventoryUsage, title, mainTextureSupplier);
        this.ignoreCloseOnInventoryKey(true);

        this._bindings = new BindingGroup();

        this._sensorButtonsPanel = new Panel(this, "sensorButtonsPanel");
        this._activeSensorName = new Label(this, "activename", "");
        this._sensorsGroup = new PanelGroup<>(this, "behaviors", validSensors);
        this._guiMap = new RedstonePortSettingControlsMap<>(this._activeSensorName, this._sensorsGroup, sensorSettingFactory);

        this._disableButton = new Button(this, "disable", Component.translatable("gui.bigreactors.generic.disable"));
        this._saveButton = new Button(this, "save", Component.translatable("gui.bigreactors.generic.save"));
        this._resetButton = new Button(this, "reset", Component.translatable("gui.bigreactors.generic.reset"));
    }

    protected abstract SensorSetting getDefaultSettings();

    protected abstract SensorSetting getDisabledSettings();

    //region AbstractMultiblockScreen

    @Override
    protected void onScreenCreate() {

        super.onScreenCreate();

        // - sensor panel

        final IControl c = new Label(this, "sensorlistlabel", Component.translatable("gui.bigreactors.generator.redstoneport.sensortype.sensorlistlabel"));

        c.setPadding(0);
        c.setLayoutEngineHint(FixedLayoutEngine.hint(13, 4, this.getGuiWidth() - 36, 10));
        this.addControl(c);

        this._sensorButtonsPanel.setPadding(0);
        this._sensorButtonsPanel.setLayoutEngineHint(FixedLayoutEngine.hint(13, 18, 62, 96));
        this.addControl(this._sensorButtonsPanel);

        // - sensors sub-behaviors controls

        final int behaviorPanelWidth = this.getBehaviorPanelWidth();
        final int behaviorPanelHeight = this.getBehaviorPanelHeight();

        this._activeSensorName.setLayoutEngineHint(FixedLayoutEngine.hint(80, 4, behaviorPanelWidth, 10));
        this.addControl(this._activeSensorName);

        this._sensorsGroup.setLayoutEngineHint(FixedLayoutEngine.hint(78, 18, behaviorPanelWidth, behaviorPanelHeight));
        this.addControl(this._sensorsGroup);

        // disable / save / reset

        int sensorButtonRowY = this.getGuiHeight() - 46;

        this._disableButton.setLayoutEngineHint(FixedLayoutEngine.hint(13, sensorButtonRowY, 62, 16));
        this._disableButton.Clicked.subscribe(this::onDisable);
        this.addControl(this._disableButton);

        this._saveButton.setLayoutEngineHint(FixedLayoutEngine.hint(109, sensorButtonRowY, 50, 16));
        this._saveButton.Clicked.subscribe(this::onSave);
        this.addControl(this._saveButton);

        this._resetButton.setLayoutEngineHint(FixedLayoutEngine.hint(161, sensorButtonRowY, 50, 16));
        this._resetButton.Clicked.subscribe(this::onReset);
        this.addControl(this._resetButton);

        this.setActionButtonsVisibility(false);
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
    //region ContainerScreen

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    //endregion
    //region internals
    //region GUI controls helpers

    protected int getBehaviorPanelWidth() {
        return this.getGuiWidth() - 91;
    }

    protected int getBehaviorPanelHeight() {
        return 88;
    }

    protected TextInput inputText(final String name, final String suffix) {

        final TextInput t = new TextInput(this, name, "0");

        t.setDesiredDimension(70, 14);
        t.setFilter(TextConstraints.FILTER_NUMBERS);
        t.setDisplaySuffix(suffix);
        return t;
    }

    protected TextInput inputTextPercentage(final String name) {

        final TextInput t = this.inputText(name, "%");

        t.setMaxLength(3);
        t.setDesiredDimension(40, 14);
        t.addConstraint(TextConstraints.CONSTRAINT_PERCENTAGE);
        return t;
    }

    protected TextInput inputTextNumber(final String name, final String suffix) {

        final TextInput t = this.inputText(name, suffix);

        t.setMaxLength(10);
        t.addConstraint(TextConstraints.CONSTRAINT_POSITIVE_INTEGER_NUMBER);
        return t;
    }

    protected TextInput inputTextNumber(final String name, final String suffix, final Function<String, Optional<String>> constraint) {

        final TextInput t = this.inputTextNumber(name, suffix);

        t.addConstraint(constraint);
        return t;
    }

    protected void addSensorButtonsSeparator(final int sensorButtonRowY) {

        final Static s = new Static(this, 62, 1);

        s.setColor(Colour.BLACK);
        s.setLayoutEngineHint(FixedLayoutEngine.hint(0, sensorButtonRowY - 8, 62, 1));
        this._sensorButtonsPanel.addControl(s);
    }

    protected void applySettings(final SensorSetting setting) {
        this._guiMap.setSettings(setting);
    }

    //endregion

    private void onDisable(final Button btn, final int mouseButton) {
        this.sendCommandToServer(CommonConstants.COMMAND_DISABLE_REDSTONE_SENSOR);
    }

    private void onSave(final Button btn, final int mouseButton) {

        if (this.isValid()) {
            this.sendCommandToServer(CommonConstants.COMMAND_SET_REDSTONE_SENSOR,
                    this._guiMap.getSettings(this.getDisabledSettings()).syncDataTo(new CompoundTag()));
        }
    }

    private void onReset(final Button btn, final int mouseButton) {
        this._guiMap.setSettings(this.getDefaultSettings());
    }

    private static void onBehaviorChanged(final ChoiceText<SensorBehavior> choice, final SensorBehavior behavior) {
        choice.<PanelGroup<SensorBehavior>>getTag().ifPresent(group -> group.setActivePanel(behavior));
    }

    protected void addBinding(final Function<T, SensorSetting> supplier, final Consumer<SensorSetting> consumer) {
        this._bindings.addBinding(new MonoConsumerBinding<>(this.getTileEntity(), supplier, consumer));
    }

    private void setActionButtonsVisibility(boolean visible) {

        this._saveButton.setVisible(visible);
        this._resetButton.setVisible(visible);
    }

    private SwitchPictureButton createSensorButton(final int x, final int y, final SensorType sensor,
                                                   final NonNullSupplier<ISprite> defaultSprite,
                                                   final NonNullSupplier<ISprite> activeSprite) {

        final SwitchPictureButton button = new SwitchPictureButton(this, sensor.name(), false, "sensortype");
        final String tooltipBaseName = sensor.getTranslationBaseName();

        button.setTag(sensor);
        button.setTooltips(Component.translatable(tooltipBaseName + "1").setStyle(CommonConstants.STYLE_TOOLTIP_TITLE),
                sensor.isInput() ? TEXT_DIRECTION_INPUT : TEXT_DIRECTION_OUTPUT,
                TEXT_EMPTY_LINE, Component.translatable(tooltipBaseName + "2"), Component.translatable(tooltipBaseName + "3"));

        this.setButtonSpritesAndOverlayForState(button, ButtonState.Default, defaultSprite.get());
        this.setButtonSpritesAndOverlayForState(button, ButtonState.Active, activeSprite.get());

        button.setLayoutEngineHint(FixedLayoutEngine.hint(x, y, 18, 18));
        button.setBackground(CommonIcons.ImageButtonBackground.get());
        button.enablePaintBlending(true);
        button.setPadding(1);

        button.Activated.subscribe(spb -> spb.<SensorType>getTag().ifPresent(st -> {

            this._sensorsGroup.setActivePanel(st);
            this._activeSensorName.setText(Component.translatable(st.getTranslationBaseName() + "1"));
            this.setActionButtonsVisibility(true);
        }));

        button.Deactivated.subscribe(spb -> {

            this._sensorsGroup.clearActivePanel();
            this.setActionButtonsVisibility(false);
        });

        return button;
    }

    private static final Component TEXT_DIRECTION_INPUT = Component.translatable("gui.bigreactors.generator.redstoneport.sensortype.input").setStyle(CommonConstants.STYLE_TOOLTIP_INFO);
    private static final Component TEXT_DIRECTION_OUTPUT = Component.translatable("gui.bigreactors.generator.redstoneport.sensortype.output").setStyle(CommonConstants.STYLE_TOOLTIP_INFO);

    private final BindingGroup _bindings;
    private final RedstonePortSettingControlsMap<Reader, Writer, SensorType, SensorSetting> _guiMap;

    private final Panel _sensorButtonsPanel;
    private final PanelGroup<SensorType> _sensorsGroup;
    private final Label _activeSensorName;

    private final Button _disableButton;
    private final Button _saveButton;
    private final Button _resetButton;

    //region sensor panel builder

    protected SensorPanelBuilder<Controller, T, C, Reader, Writer, SensorType, SensorSetting> sensorPanelBuilder(
                                    final SensorType sensorType, final int buttonX, final int buttonY,
                                    final NonNullSupplier<ISprite> buttonDefaultIcon,
                                    final NonNullSupplier<ISprite> buttonActiveIcon) {
        return new SensorPanelBuilder<>(this, sensorType, buttonX, buttonY, buttonDefaultIcon, buttonActiveIcon);
    }

    private void addBuiltSensorPanel(final SensorType sensor, final Panel panel, final SwitchPictureButton button,
                                     final ChoiceText<SensorBehavior> behaviorChoice,
                                     final List<RedstonePortSettingControlsMap.BehaviorEntry> behaviorEntries) {

        this._sensorsGroup.setPanel(sensor, panel);
        this._sensorButtonsPanel.addControl(button);
        this._guiMap.add(sensor, new RedstonePortSettingControlsMap.SensorEntry(button, behaviorChoice, behaviorEntries));
    }

    @SuppressWarnings({"UnusedReturnValue", "unused"})
    protected static class SensorPanelBuilder<Controller extends AbstractCuboidMultiblockController<Controller> & IMultiblockMachine,
                                                T extends AbstractMultiblockEntity<Controller> & MenuProvider,
                                                C extends ModTileContainer<T>,
                                                Reader extends IMachineReader, Writer,
                                                SensorType extends Enum<SensorType> & ISensorType<Reader>,
                                                SensorSetting extends AbstractSensorSetting<Reader, Writer, SensorType>> {

        public void build() {

            this.setupBehaviorChoiceControl(this._sensor, this._behaviorChoice, this._behaviorDataGroup, this._behaviorPanelWidth);

            if (null != this._behaviorDataGroup) {
                this._behaviorDataGroup.setActivePanel(this._behaviorChoice.getValidIndices().get(0));
            }

            this._gui.addBuiltSensorPanel(this._sensor, this._sensorPanel, this._sensorButton, this._behaviorChoice,
                    null != this._mapBehaviorDataEntries ? this._mapBehaviorDataEntries : Collections.emptyList());
        }

        public SensorPanelBuilder<Controller, T, C, Reader, Writer, SensorType, SensorSetting> addBehaviorDataInput(
                                    final SensorBehavior behavior) {

            this.createBehaviorDataGroup();
            this.createBehaviorDataPanel(behavior);
            return this;
        }

        public SensorPanelBuilder<Controller, T, C, Reader, Writer, SensorType, SensorSetting> addBehaviorDataInput(
                                    final SensorBehavior behavior,
                                    final TextInput inputA, final String inputALabelKey) {
            return this.addBehaviorSingleDataInput(behavior, inputA, inputALabelKey, null);
        }

        public SensorPanelBuilder<Controller, T, C, Reader, Writer, SensorType, SensorSetting> addBehaviorDataInput(
                                    final SensorBehavior behavior,
                                    final TextInput inputA, final String inputALabelKey,
                                    final BehaviorSingleDataValidator validator) {
            return this.addBehaviorSingleDataInput(behavior, inputA, inputALabelKey, validator);
        }

        public SensorPanelBuilder<Controller, T, C, Reader, Writer, SensorType, SensorSetting> addBehaviorDataInput(
                                    final SensorBehavior behavior,
                                    final TextInput inputA, final String inputALabelKey,
                                    final TextInput inputB, final String inputBLabelKey) {
            return this.addBehaviorDoubleDataInput(behavior, inputA, inputALabelKey, inputB, inputBLabelKey, null);
        }

        public SensorPanelBuilder<Controller, T, C, Reader, Writer, SensorType, SensorSetting> addBehaviorDataInput(
                                    final SensorBehavior behavior,
                                    final TextInput inputA, final String inputALabelKey,
                                    final TextInput inputB, final String inputBLabelKey,
                                    final BehaviorDoubleDataValidator validator) {
            return this.addBehaviorDoubleDataInput(behavior, inputA, inputALabelKey, inputB, inputBLabelKey, validator);
        }

        public SensorPanelBuilder<Controller, T, C, Reader, Writer, SensorType, SensorSetting> addStandardOutputBehaviorPanel(
                                    final TextInput inputAbove, final String inputAboveLabelKey,
                                    final TextInput inputBelow, final String inputBelowLabelKey,
                                    final TextInput inputBetweenMin, final String inputBetweenMinLabelKey,
                                    final TextInput inputBetweenMax, final String inputBetweenMaxLabelKey) {

            this.addBehaviorDataInput(SensorBehavior.ActiveWhileAbove, inputAbove, inputAboveLabelKey);
            this.addBehaviorDataInput(SensorBehavior.ActiveWhileBelow, inputBelow, inputBelowLabelKey);
            this.addBehaviorDataInput(SensorBehavior.ActiveWhileBetween, inputBetweenMin, inputBetweenMinLabelKey,
                    inputBetweenMax, inputBetweenMaxLabelKey, (min, max, errorReport) -> {
                        if (min.intValue() >= max.intValue()) {
                            errorReport.accept(Component.translatable("gui.bigreactors.generator.validation.redstoneport.invalidminmax.line1"));
                        }
                    });

            return this;
        }

        //region internals

        protected SensorPanelBuilder(final AbstractRedstonePortScreen<Controller, T, C, Reader, Writer, SensorType, SensorSetting> gui,
                                     final SensorType sensorType, final int buttonX, final int buttonY,
                                     final NonNullSupplier<ISprite> buttonDefaultIcon,
                                     final NonNullSupplier<ISprite> buttonActiveIcon) {

            this._gui = gui;
            this._behaviorPanelWidth = gui.getBehaviorPanelWidth();

            this._sensor = sensorType;
            this._sensorButton = this._gui.createSensorButton(buttonX, buttonY, sensorType, buttonDefaultIcon, buttonActiveIcon);
            this._sensorPanel = createSensorPanel(sensorType, this._behaviorPanelWidth);
            this._behaviorChoice = new ChoiceText<>(this._gui, sensorType.name() + "Behavior", sensorType.getBehaviors());
            this._sensorPanel.addControl(this._behaviorChoice);
        }

        private void createBehaviorDataGroup() {

            if (null == this._behaviorDataGroup) {

                this._behaviorDataGroup = new PanelGroup<>(this._gui, "dg", this._behaviorChoice.getValidIndices());
                this._sensorPanel.addControl(this._behaviorDataGroup);
            }

            if (null == this._mapBehaviorDataEntries) {
                this._mapBehaviorDataEntries = Lists.newLinkedList();
            }
        }

        private SensorPanelBuilder<Controller, T, C, Reader, Writer, SensorType, SensorSetting> addBehaviorSingleDataInput(
                final SensorBehavior behavior, final TextInput inputA, final String inputALabelKey,
                @Nullable final BehaviorSingleDataValidator validator) {

            this.createBehaviorDataGroup();

            final Panel p = this.createBehaviorDataPanel(behavior);

            p.addControl(behaviorLabel(behavior.name() + "Label", inputALabelKey, this._behaviorPanelWidth));
            p.addControl(inputA);

            if (null != validator) {
                p.setValidator((cnt, report) -> validator.validate(inputA, report));
            }

            this._mapBehaviorDataEntries.add(new RedstonePortSettingControlsMap.BehaviorEntry(behavior, inputA));
            return this;
        }

        private SensorPanelBuilder<Controller, T, C, Reader, Writer, SensorType, SensorSetting>  addBehaviorDoubleDataInput(
                final SensorBehavior behavior, final TextInput inputA, final String inputALabelKey,
                final TextInput inputB, final String inputBLabelKey, @Nullable final BehaviorDoubleDataValidator validator) {

            this.createBehaviorDataGroup();

            final Panel p = this.createBehaviorDataPanel(behavior);

            p.addControl(behaviorLabel(behavior.name() + "LabelA", inputALabelKey, this._behaviorPanelWidth));
            p.addControl(inputA);
            p.addControl(behaviorLabel(behavior.name() + "LabelB", inputBLabelKey, this._behaviorPanelWidth));
            p.addControl(inputB);

            if (null != validator) {
                p.setValidator((cnt, report) -> validator.validate(inputA, inputB, report));
            }

            this._mapBehaviorDataEntries.add(new RedstonePortSettingControlsMap.BehaviorEntry(behavior, inputA, inputB));
            return this;
        }

        //region GUI controls helpers

        private Panel createSensorPanel(final SensorType sensor, final int width) {

            final Panel p = new Panel(this._gui, sensor.name());

            p.setLayoutEngineHint(FixedLayoutEngine.hint(0, 0, width, 88));
            p.setLayoutEngine(new VerticalLayoutEngine()
                    .setVerticalMargin(0)
                    .setHorizontalMargin(2)
                    .setHorizontalAlignment(HorizontalAlignment.Left));
            return p;
        }

        private Panel createBehaviorDataPanel(final SensorBehavior behavior) {

            final Panel p = new Panel(this._gui, behavior.name());

            p.setLayoutEngineHint(FixedLayoutEngine.hint(0, 0, this._behaviorPanelWidth, 200));
            p.setLayoutEngine(new VerticalLayoutEngine()
                    .setVerticalMargin(0)
                    .setHorizontalMargin(4)
                    .setControlsSpacing(4)
                    .setHorizontalAlignment(HorizontalAlignment.Left));

            this._behaviorDataGroup.setPanel(behavior, p);
            return p;
        }

        private Label behaviorLabel(final String name, final String translationKey, final int width) {

            final Label l = new Label(this._gui, name, Component.translatable(translationKey));

            l.setPadding(0);
            l.setDesiredDimension(width, 10);
            return l;
        }

        private void setupBehaviorChoiceControl(final SensorType sensor, final ChoiceText<SensorBehavior> choice,
                                                @Nullable final PanelGroup<SensorBehavior> behaviorGroup, final int width) {

            final List<SensorBehavior> behaviors = sensor.getBehaviors();

            behaviors.forEach(b -> choice.addText(b, Component.translatable("gui.bigreactors.generator.redstoneport.sensorbehavior." +
                    CodeHelper.neutralLowercase(b.name()) + ".line1")));

            choice.setSelectedIndex(behaviors.get(0));
            choice.setDesiredDimension(width, 16);

            if (behaviors.size() == 1) {
                choice.setEnabled(false);
            }

            if (null != behaviorGroup) {

                choice.setTag(behaviorGroup);
                choice.Changed.subscribe(AbstractRedstonePortScreen::onBehaviorChanged);
            }
        }

        //endregion

        private final AbstractRedstonePortScreen<Controller, T, C, Reader, Writer, SensorType, SensorSetting> _gui;
        private final SensorType _sensor;
        private final SwitchPictureButton _sensorButton;
        private final Panel _sensorPanel;
        private final ChoiceText<SensorBehavior> _behaviorChoice;
        private final int _behaviorPanelWidth;

        private PanelGroup<SensorBehavior> _behaviorDataGroup;
        private List<RedstonePortSettingControlsMap.BehaviorEntry> _mapBehaviorDataEntries;

        //endregion
    }

    //endregion
    //endregion
}
