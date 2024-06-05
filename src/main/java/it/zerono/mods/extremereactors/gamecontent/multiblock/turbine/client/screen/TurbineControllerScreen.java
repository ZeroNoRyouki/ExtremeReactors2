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
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.container.TurbineControllerContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineControllerEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.zerocore.base.client.screen.BaseScreenToolTipsBuilder;
import it.zerono.mods.zerocore.base.client.screen.ClientBaseHelper;
import it.zerono.mods.zerocore.base.client.screen.control.*;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.ButtonState;
import it.zerono.mods.zerocore.lib.client.gui.control.Panel;
import it.zerono.mods.zerocore.lib.client.gui.control.SwitchButton;
import it.zerono.mods.zerocore.lib.client.gui.control.SwitchPictureButton;
import it.zerono.mods.zerocore.lib.client.gui.layout.FixedLayoutEngine;
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

        this._infoDisplay = new InformationDisplay(this, "info", layout -> layout.columns(2, 88, 112).rows(3));
        this._infoDisplay.addCellContent(flowRate, builder -> builder.setRowsSpan(3));

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

        this._on = new SwitchButton(this, "on", "ON", false, "onoff");
        this._on.Activated.subscribe(this::onActiveStateChanged);
        this._on.Deactivated.subscribe(this::onActiveStateChanged);
        container.ACTIVE.bind(this._on::setActive);
        this._on.setTooltips(TextHelper.translatable("gui.bigreactors.turbine.controller.on.title"));

        this._off = new SwitchButton(this, "off", "OFF", true, "onoff");
        this._off.setTooltips(TextHelper.translatable("gui.bigreactors.turbine.controller.off.title"));

        this._inductorEngaged = new SwitchPictureButton(this, "inductorEngaged", false);
        this._inductorEngaged.bindActive(container.INDUCTOR_ENGAGED);
        this._inductorEngaged.Activated.subscribe(this::onInductorEngagedChanged);
        this._inductorEngaged.Deactivated.subscribe(this::onInductorEngagedChanged);
        ClientBaseHelper.setButtonSpritesAndOverlayForState(this._inductorEngaged, ButtonState.Default, CommonIcons.ButtonInductor);
        ClientBaseHelper.setButtonSpritesAndOverlayForState(this._inductorEngaged, ButtonState.Active, CommonIcons.ButtonInductorActive);
        this._inductorEngaged.setBackground(CommonIcons.ImageButtonBackground.get());
        this._inductorEngaged.enablePaintBlending(true);
        this._inductorEngaged.setPadding(1);
        this._inductorEngaged.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.turbine.controller.inductor.tooltip.title")
                .addTranslatableAsValue("gui.bigreactors.turbine.controller.inductor.tooltip.value")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.turbine.controller.inductor.tooltip.body")
                .addBindableObjectAsValue(container.INDUCTOR_ENGAGED, engaged -> engaged ? TEXT_INDUCTOR_ENGAGED : TEXT_INDUCTOR_DISENGAGED)
        );

        this._ventAll = this.ventButton(VentSetting.VentAll);
        this._ventAll.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.turbine.controller.vent.all.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.turbine.controller.vent.all.tooltip.body")
        );

        this._ventOverflow = this.ventButton(VentSetting.VentOverflow);
        this._ventOverflow.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.turbine.controller.vent.overflow.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.turbine.controller.vent.overflow.tooltip.body")
        );

        this._ventDoNotVent = this.ventButton(VentSetting.DoNotVent);
        this._ventDoNotVent.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.turbine.controller.vent.donotvent.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.turbine.controller.vent.donotvent.tooltip.body")
        );

        container.VENT_SETTINGS.bind(setting -> {

            this._ventAll.setActive(VentSetting.VentAll.test(setting));
            this._ventOverflow.setActive(VentSetting.VentOverflow.test(setting));
            this._ventDoNotVent.setActive(VentSetting.DoNotVent.test(setting));
        });

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
                .addTemperatureScale()
                .add(this._coolantBar)
                .addVerticalSeparator()
                .add(this._rpmBar)
                .addEmptyPanel(11)
                .add(this._energyBar)
                .addVerticalSeparator()
                .addEmptyPanel(4);

        this.addControl(barsPanel);
        this.addControl(CommonPanels.horizontalSeparator(this, this.getContentWidth()));
        this.addControl(this._infoDisplay);

        // COMMANDS

        final Panel commandPanel = CommonPanels.verticalCommandPanel(this, 50);

        barsPanel.add(commandPanel);

        // - machine on/off

        int x = 0;
        int y = 0;
        int w = 38;

        this._on.setLayoutEngineHint(FixedLayoutEngine.hint(x, y, w, 16));
        this._off.setLayoutEngineHint(FixedLayoutEngine.hint(x + w, y, w, 16));
        commandPanel.addControl(this._on, this._off);
        y += 28;

        // inductor

        int xButton = x;

        this._inductorEngaged.setLayoutEngineHint(FixedLayoutEngine.hint(xButton, y, 18, 18));
        commandPanel.addControl(this._inductorEngaged);
        xButton += 20;

        // - vent settings

        this._ventAll.setLayoutEngineHint(FixedLayoutEngine.hint(xButton, y, 18, 18));
        commandPanel.addControl(this._ventAll);
        xButton += 19;

        this._ventOverflow.setLayoutEngineHint(FixedLayoutEngine.hint(xButton, y, 18, 18));
        commandPanel.addControl(this._ventOverflow);
        xButton += 19;

        this._ventDoNotVent.setLayoutEngineHint(FixedLayoutEngine.hint(xButton, y, 18, 18));
        commandPanel.addControl(this._ventDoNotVent);
    }

    //region internals

    private SwitchPictureButton ventButton(final VentSetting setting) {

        final SwitchPictureButton swp = new SwitchPictureButton(this, setting.name(), false, "ventSetting");

        swp.setTag(setting);
        swp.Activated.subscribe(this::onVentSettingChanged);
        swp.enablePaintBlending(true);
        swp.setPadding(1);
        swp.setBackground(CommonIcons.ImageButtonBackground.get());

        switch (setting) {

            case VentAll:
                ClientBaseHelper.setButtonSpritesAndOverlayForState(swp, ButtonState.Default, CommonIcons.ButtonVentAll);
                ClientBaseHelper.setButtonSpritesAndOverlayForState(swp, ButtonState.Active, CommonIcons.ButtonVentAllActive);
                break;

            case VentOverflow:
                ClientBaseHelper.setButtonSpritesAndOverlayForState(swp, ButtonState.Default, CommonIcons.ButtonVentOverflow);
                ClientBaseHelper.setButtonSpritesAndOverlayForState(swp, ButtonState.Active, CommonIcons.ButtonVentOverflowActive);
                break;

            case DoNotVent:
                ClientBaseHelper.setButtonSpritesAndOverlayForState(swp, ButtonState.Default, CommonIcons.ButtonVentDoNot);
                ClientBaseHelper.setButtonSpritesAndOverlayForState(swp, ButtonState.Active, CommonIcons.ButtonVentDoNotActive);
                break;
        }

        return swp;
    }

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
    private final SwitchButton _on, _off;
    private final SwitchPictureButton _inductorEngaged;
    private final SwitchPictureButton _ventAll;
    private final SwitchPictureButton _ventOverflow;
    private final SwitchPictureButton _ventDoNotVent;
    private final InformationDisplay _infoDisplay;

    //endregion
}
