/*
 *
 * TurbineControllerScreen.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.screen;

import it.zerono.mods.extremereactors.CommonLocations;
import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonMultiblockScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.MultiblockTurbine;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.VentSetting;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.screen.control.FlowRate;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.screen.control.RpmBar;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.screen.control.VentSettings;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.container.TurbineControllerContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineControllerEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.zerocore.base.client.screen.BaseScreenToolTipsBuilder;
import it.zerono.mods.zerocore.base.client.screen.ClientBaseHelper;
import it.zerono.mods.zerocore.base.client.screen.control.*;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.ButtonState;
import it.zerono.mods.zerocore.lib.client.gui.DesiredDimension;
import it.zerono.mods.zerocore.lib.client.gui.control.Panel;
import it.zerono.mods.zerocore.lib.client.gui.control.SwitchButton;
import it.zerono.mods.zerocore.lib.client.gui.control.SwitchPictureButton;
import it.zerono.mods.zerocore.lib.client.gui.layout.AnchoredLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.layout.HorizontalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalLayoutEngine;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.data.nbt.NBTHelper;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.text.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class TurbineControllerScreen
        extends CommonMultiblockScreen<MultiblockTurbine, TurbineControllerEntity, TurbineControllerContainer> {

    public TurbineControllerScreen(final TurbineControllerContainer container,
                                   final Inventory inventory, final Component title) {

        super(container, inventory, PlayerInventoryUsage.None, title,
                mainTextureFromVariant(container.getTileEntity().getMultiblockVariant().orElse(TurbineVariant.Basic)));

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, CommonLocations.TURBINE.buildWithSuffix("part-controller"), 1);

        this._coolantBar = new FluidBar(this, "coolantBar", container.getFluidCapacity(), container.COOLANT_STACK,
                CommonIcons.CoolantIcon, "gui.bigreactors.turbine.controller.coolantbar.title",
                "gui.bigreactors.turbine.controller.coolantbar.footer");
        this._vaporBar = new FluidBar(this, "vaporBar", container.getFluidCapacity(), container.VAPOR_STACK,
                CommonIcons.VaporIcon, "gui.bigreactors.turbine.controller.vaporbar.title",
                "gui.bigreactors.turbine.controller.vaporbar.footer");

        this._rpmBar = new RpmBar(this, "rpmBar", container.getMaxRotorSpeed(), container.RPM);

        this._energyBar = new EnergyBar(this, "energyBar", container.getOutputEnergySystem(),
                container.getEnergyCapacity(), container.ENERGY_STORED, "gui.bigreactors.reactor_turbine.controller.energybar.tooltip.body");

        final FlowRate flowRate = new FlowRate(this, "maxIntakeRate", container.getMaxIntakeRateHardLimit(),
                container.getMaxIntakeRate(), this::onMaxIntakeRateChanged);

        this._infoDisplay = new InformationDisplay(this, "info",
                layout -> layout.columns(2, 79, 112).rows(3));

        this._infoDisplay.addCellContent(flowRate, builder -> builder
                .setRowsSpan(3)
                .setHorizontalAlignment(HorizontalAlignment.Left)
                .setVerticalAlignment(VerticalAlignment.Top));

        this._infoDisplay.addInformationCell(builder -> builder
                .name("rpm")
                .bindText(container.RPM, rpm -> String.format("%.2f RPM", rpm))
                .icon(CommonIcons.RotorRPM)
                .useTooltipsFrom(this._rpmBar));

        this._infoDisplay.addInformationCell(builder -> builder
                .name("energyRatio")
                .bindText(container.ENERGY_GENERATED_LAST_TICK, this::formatEnergyRatio)
                .icon(CommonIcons.EnergyRatioIcon)
                .tooltips(new BaseScreenToolTipsBuilder()
                        .addTranslatableAsTitle("gui.bigreactors.turbine.controller.energyratio.tooltip.title")
                        .addTextAsValue(TextHelper.translatable("gui.bigreactors.turbine.controller.energyratio.tooltip.value1a"),
                                TextHelper.translatable("gui.bigreactors.turbine.controller.energyratio.tooltip.value1b"))
                        .addEmptyLine()
                        .addTranslatable("gui.bigreactors.turbine.controller.energyratio.tooltip.body")
                        .addBindableObjectAsValue(container.ENERGY_GENERATED_LAST_TICK, generated ->
                                TextHelper.literal(this.formatEnergyRatio(generated)))
                ));

        this._infoDisplay.addInformationCell(builder -> builder
                .name("rotorStatus")
                .bindText(container.ROTOR_EFFICIENCY_LAST_TICK, this::formatRotorEfficiency)
                .icon(CommonIcons.RotorStatus)
                .tooltips(new BaseScreenToolTipsBuilder()
                        .addTranslatableAsTitle("gui.bigreactors.turbine.controller.rotorstatus.tooltip.title")
                        .addTranslatableAsValue("gui.bigreactors.turbine.controller.rotorstatus.tooltip.value1")
                        .addEmptyLine()
                        .addTranslatable("gui.bigreactors.turbine.controller.rotorstatus.tooltip.value2",
                                String.format(ChatFormatting.DARK_AQUA + String.valueOf(ChatFormatting.BOLD) + "%d",
                                        container.getBaseFluidPerBlade()))
                        .addTranslatable("gui.bigreactors.turbine.controller.rotorstatus.tooltip.value3")
                        .addEmptyLine()
                        .addTranslatable("gui.bigreactors.turbine.controller.rotorstatus.tooltip.body")
                        .addBindableObjectAsValue(container.ROTOR_EFFICIENCY_LAST_TICK, efficiency ->
                                TextHelper.literal(this.formatRotorEfficiency(efficiency)))
                        .addBindableObjectAsValue(container.FLUID_CONSUMED_LAST_TICK, consumed ->
                                TextHelper.literal("%d / %d", container.getRotorBladesCount(),
                                        consumed / container.getBaseFluidPerBlade()))
                ));

        // commands...

        this._onOff = new OnOff(this, 30, 16, container.ACTIVE, this::onActiveStateChanged,
                TextHelper.translatable("gui.bigreactors.turbine.controller.on.title"),
                TextHelper.translatable("gui.bigreactors.turbine.controller.off.title"));

        this._inductorEngaged = new SwitchPictureButton(this, "inductorEngaged", false);
        this._inductorEngaged.bindActive(container.INDUCTOR_ENGAGED);
        this._inductorEngaged.Activated.subscribe(this::onInductorEngagedChanged);
        this._inductorEngaged.Deactivated.subscribe(this::onInductorEngagedChanged);
        ClientBaseHelper.setButtonSpritesAndOverlayForState(this._inductorEngaged, ButtonState.Default, CommonIcons.ButtonInductor);
        ClientBaseHelper.setButtonSpritesAndOverlayForState(this._inductorEngaged, ButtonState.Active, CommonIcons.ButtonInductorActive);
        this._inductorEngaged.setBackground(CommonIcons.ImageButtonBackground.get());
        this._inductorEngaged.enablePaintBlending(true);
        this._inductorEngaged.setPadding(1);
        this._inductorEngaged.setDesiredDimension(ClientBaseHelper.SQUARE_BUTTON_DIMENSION, ClientBaseHelper.SQUARE_BUTTON_DIMENSION);
        this._inductorEngaged.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.turbine.controller.inductor.tooltip.title")
                .addTranslatableAsValue("gui.bigreactors.turbine.controller.inductor.tooltip.value")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.turbine.controller.inductor.tooltip.body")
                .addBindableObjectAsValue(container.INDUCTOR_ENGAGED, engaged -> engaged ? TEXT_INDUCTOR_ENGAGED : TEXT_INDUCTOR_DISENGAGED)
        );

        this._ventSettings = new VentSettings(this, container.VENT_SETTINGS, this::onVentSettingChanged);

        this.setContentBounds(14, 0);
    }

    //region CommonMultiblockScreen

    @Override
    protected MachineStatusIndicator createStatusIndicator(TurbineControllerContainer container) {
        return this.createTurbineStatusIndicator(container.ACTIVE);
    }

    @Override
    protected void onScreenCreate() {

        super.onScreenCreate();

        this.setContentLayoutEngine(new VerticalLayoutEngine()
                .setZeroMargins()
                .setControlsSpacing(2)
                .setVerticalAlignment(VerticalAlignment.Top)
                .setHorizontalAlignment(HorizontalAlignment.Left));

        // BARS

        final BarsPanel barsPanel = new BarsPanel(this, "bars")
                .add(this._vaporBar)
                .addEmptyPanel(5)
                .addTemperatureScale()
                .addEmptyPanel(5)
                .add(this._coolantBar)
                .addEmptyPanel(21)
                .add(this._rpmBar)
                .addVerticalSeparator()
                .add(this._energyBar)
                .addVerticalSeparator();

        this.addControl(barsPanel);
        this.addControl(CommonPanels.horizontalSeparator(this, this.getContentWidth()));
        this.addControl(this._infoDisplay);

        // COMMANDS

        final Panel commandPanel = CommonPanels.verticalCommandPanel(this,
                this._ventSettings.getDesiredDimension(DesiredDimension.Width));

        commandPanel.setLayoutEngine(new AnchoredLayoutEngine().setZeroMargins());
        barsPanel.add(commandPanel);

        // - machine on/off
        this._onOff.setLayoutEngineHint(AnchoredLayoutEngine.Anchor.Top);
        commandPanel.addControl(this._onOff);

        // - inductor
        this._inductorEngaged.setLayoutEngineHint(AnchoredLayoutEngine.Anchor.Center);
        commandPanel.addControl(this._inductorEngaged);

        // - vent settings
        this._ventSettings.setLayoutEngineHint(AnchoredLayoutEngine.Anchor.Bottom);
        commandPanel.addControl(this._ventSettings);
    }

    //region internals

    private String formatEnergyRatio(final WideAmount generated) {
        return CodeHelper.formatAsHumanReadableNumber(generated.doubleValue(), this.getMenu().getOutputEnergySystem().getUnit());
    }

    private String formatRotorEfficiency(final float efficiency) {
        return String.format("%.1f%%", Mth.clamp(efficiency, 0.0f, 1.0f) * 100);
    }

    private void onActiveStateChanged(final SwitchButton button) {
        this.sendCommandToServer(button.getActive() ?
                CommonConstants.COMMAND_ACTIVATE :
                CommonConstants.COMMAND_DEACTIVATE);
    }

    private void onVentSettingChanged(final SwitchPictureButton button) {
        this.sendCommandToServer(TurbineControllerEntity.COMMAND_SET_VENT,
                NBTHelper.nbtSetEnum(new CompoundTag(), "vent", (VentSetting)button.getTag().orElse(VentSetting.VentAll)));
    }

    private void onInductorEngagedChanged(final SwitchPictureButton button) {
        this.sendCommandToServer(button.getActive() ?
                TurbineControllerEntity.COMMAND_ENGAGE_COILS :
                TurbineControllerEntity.COMMAND_DISENGAGE_COILS);
    }

    private void onMaxIntakeRateChanged(final int newRate) {

        final CompoundTag data = new CompoundTag();

        data.putInt("rate", newRate);
        this.sendCommandToServer(TurbineControllerEntity.COMMAND_SET_INTAKERATE, data);
    }

    private static final MutableComponent TEXT_INDUCTOR_ENGAGED = TextHelper.translatable("gui.bigreactors.turbine.controller.inductor.mode.engaged", ClientBaseHelper::formatAsValue);
    private static final MutableComponent TEXT_INDUCTOR_DISENGAGED = TextHelper.translatable("gui.bigreactors.turbine.controller.inductor.mode.disengaged", ClientBaseHelper::formatAsValue);

    private final FluidBar _coolantBar;
    private final FluidBar _vaporBar;
    private final RpmBar _rpmBar;
    private final EnergyBar _energyBar;
    private final OnOff _onOff;
    private final SwitchPictureButton _inductorEngaged;
    private final VentSettings _ventSettings;
    private final InformationDisplay _infoDisplay;

    //endregion
}
