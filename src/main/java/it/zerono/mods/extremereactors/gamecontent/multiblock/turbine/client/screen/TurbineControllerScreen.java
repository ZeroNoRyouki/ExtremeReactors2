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

import com.google.common.collect.ImmutableList;
import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.AbstractGeneratorMultiblockController;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.AbstractMultiblockScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorControllerEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.MultiblockTurbine;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.VentSetting;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineControllerEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.ButtonState;
import it.zerono.mods.zerocore.lib.client.gui.DesiredDimension;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.control.*;
import it.zerono.mods.zerocore.lib.client.gui.databind.BindingGroup;
import it.zerono.mods.zerocore.lib.client.gui.databind.MonoConsumerBinding;
import it.zerono.mods.zerocore.lib.client.gui.databind.MultiConsumerBinding;
import it.zerono.mods.zerocore.lib.client.gui.layout.*;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.Sprite;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.client.text.BindableTextComponent;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.data.nbt.NBTHelper;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.NonNullSupplier;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static it.zerono.mods.zerocore.lib.CodeHelper.TEXT_EMPTY_LINE;

public class TurbineControllerScreen
        extends AbstractMultiblockScreen<MultiblockTurbine, TurbineControllerEntity, ModTileContainer<TurbineControllerEntity>> {

    public TurbineControllerScreen(final ModTileContainer<TurbineControllerEntity> container,
                                   final Inventory inventory, final Component title) {

        super(container, inventory, PlayerInventoryUsage.None, title,
                mainTextureFromVariant(container.getTileEntity().getMultiblockVariant().orElse(TurbineVariant.Basic)));

        this._turbine = this.getMultiblockController().orElseThrow(IllegalStateException::new);
        this._outputEnergySystem = this._turbine.getOutputEnergySystem();
        this._turbineEnergyCapacity = this._turbine.getCapacity(this._outputEnergySystem, null);

        this._bindings = new BindingGroup();

        this._coolantBar = this.liquidBar("coolantBar", this._turbine.getFluidContainer().getCapacity());
        this._vaporBar = this.liquidBar("vaporBar", this._turbine.getFluidContainer().getCapacity());
        this._energyBar = new GaugeBar(this, "energyBar", this._turbineEnergyCapacity, CommonIcons.PowerBar.get());
        this._lblEnergyRatio = this.infoLabel("energyRatioValue", "");
        this._rpmBar = new GaugeBar(this, "rpmBar", this._turbine.getVariant().getMaxRotorSpeed(), CommonIcons.RpmBar.get());
        this._lblRpm = this.infoLabel("rpmInfo", "");
        this._lblRotorStatus = this.infoLabel("rotorBlades", "");
        this._maxIntakeRate = new NumberInput.IntNumberInput(this, "maxIntakeRate", 0, this._turbine.getMaxIntakeRateHardLimit(), this._turbine.getMaxIntakeRate());

        this._inductorEngaged = new SwitchPictureButton(this, "inductorEngaged", false);
        this._ventAll = this.ventButton(VentSetting.VentAll);
        this._ventOverflow = this.ventButton(VentSetting.VentOverflow);
        this._ventDoNotVent = this.ventButton(VentSetting.DoNotVent);
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

        //this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, ExtremeReactors.newID("turbine/part-controller"), 1);

        Static s;
        Panel p;
        IControl c;

        final Panel outerPanel = new Panel(this);
        final Panel barsPanel = new Panel(this);
        final Panel infoPanel = new Panel(this);
        final Panel infoPanelLeft = this.hInfoPanel();
        final Panel infoPanelRight = this.hInfoPanel();

        super.onScreenCreate();

        this.setContentLayoutEngine(new VerticalLayoutEngine()
                .setVerticalMargin(1)
                .setHorizontalMargin(13)
                .setControlsSpacing(0));

        // OUTER PANEL

        outerPanel.setDesiredDimension(this.getGuiWidth() - 26, VBARPANEL_HEIGHT + 57);
        outerPanel.setLayoutEngine(new VerticalLayoutEngine()
                .setZeroMargins()
                .setControlsSpacing(2)
                .setVerticalAlignment(VerticalAlignment.Top)
                .setHorizontalAlignment(HorizontalAlignment.Left));
        this.addControl(outerPanel);

        // BARS

        barsPanel.setDesiredDimension(DesiredDimension.Height, VBARPANEL_HEIGHT);
        barsPanel.setLayoutEngine(new HorizontalLayoutEngine()
                .setZeroMargins()
                .setVerticalAlignment(VerticalAlignment.Top)
                .setHorizontalAlignment(HorizontalAlignment.Left));
        outerPanel.addControl(barsPanel);

        // h-separator

        p = new Panel(this);
        p.setDesiredDimension(this.getGuiWidth() - 29, 1);
        p.setLayoutEngine(new FixedLayoutEngine());

        s = new Static(this, 0, 0).setColor(Colour.BLACK);
        s.setLayoutEngineHint(FixedLayoutEngine.hint(0, 0, this.getGuiWidth() - 29 - 3, 1));

        p.addControl(s);
        outerPanel.addControl(p);

        // INFOS

        infoPanel.setLayoutEngine(new HorizontalLayoutEngine()
                .setZeroMargins()
                .setVerticalAlignment(VerticalAlignment.Top)
                .setHorizontalAlignment(HorizontalAlignment.Left));
        infoPanel.addControl(infoPanelLeft, infoPanelRight);
        outerPanel.addControl(infoPanel);

        // fluids bars

        final BindableTextComponent<Integer> tankCapacity = new BindableTextComponent<>(
                capacity -> new TextComponent(CodeHelper.formatAsHumanReadableNumber(capacity / 1000, "B")).setStyle(STYLE_TOOLTIP_VALUE));

        this.addBinding((MultiblockTurbine turbine) -> turbine.getFluidContainer().getCapacity(),
                v -> {

                    this._coolantBar.setMaxValue(v);
                    this._vaporBar.setMaxValue(v);

                }, tankCapacity);

        // - vapor bar

        final BindableTextComponent<Component> vaporFluidName = new BindableTextComponent<>((Component name) -> name);
        final BindableTextComponent<Integer> vaporAmount = new BindableTextComponent<>(
                amount -> new TextComponent(CodeHelper.formatAsHumanReadableNumber(amount / 1000, "B")).setStyle(STYLE_TOOLTIP_VALUE));
        final BindableTextComponent<Double> vaporStoredPercentage = new BindableTextComponent<>(
                percentage -> new TextComponent(String.format("%d", (int)(percentage * 100))).setStyle(STYLE_TOOLTIP_VALUE));

        p = this.vBarPanel();
        this.addBarIcon(CommonIcons.VaporIcon, p).useTooltipsFrom(this._vaporBar);

        this._vaporBar.setTooltips(ImmutableList.of(
                new TranslatableComponent("gui.bigreactors.turbine.controller.vaporbar.line1").setStyle(STYLE_TOOLTIP_TITLE),
                new TranslatableComponent("gui.bigreactors.turbine.controller.vaporbar.line2").setStyle(STYLE_TOOLTIP_VALUE),
                new TranslatableComponent("gui.bigreactors.turbine.controller.vaporbar.line3a").setStyle(STYLE_TOOLTIP_VALUE)
                        .append(new TranslatableComponent("gui.bigreactors.turbine.controller.vaporbar.line3b")),
                new TranslatableComponent("gui.bigreactors.turbine.controller.vaporbar.line4a").setStyle(STYLE_TOOLTIP_VALUE)
                        .append(new TranslatableComponent("gui.bigreactors.turbine.controller.vaporbar.line4b")),
                TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.bigreactors.turbine.controller.vaporbar.line5"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.vaporbar.line6"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.vaporbar.line7")
                ),
                ImmutableList.of(
                        // @0
                        vaporFluidName,
                        // @1
                        vaporAmount,
                        // @2
                        tankCapacity,
                        // @3
                        vaporStoredPercentage
                )
        );
        this.addBinding((MultiblockTurbine turbine) -> getFluidName(turbine.getFluidContainer().getGas()),
                v -> {
                    this._vaporBar.setBarSprite(Sprite.EMPTY);
                    this._vaporBar.setBarSpriteTint(Colour.WHITE);
                    this._turbine.getFluidContainer().getGas()
                            .ifPresent(fluid -> {

                                this._vaporBar.setBarSprite(ModRenderHelper.getFlowingFluidSprite(fluid));
                                this._vaporBar.setBarSpriteTint(Colour.fromARGB(fluid.getAttributes().getColor()));
                            });
                }, vaporFluidName);
        this.addBinding((MultiblockTurbine turbine) -> turbine.getFluidContainer().getGasAmount(), (Consumer<Integer>)this._vaporBar::setValue, vaporAmount);
        this.addBinding((MultiblockTurbine turbine) -> turbine.getFluidContainer().getGasStoredPercentage(), v -> {}, vaporStoredPercentage);

        p.addControl(this._vaporBar);
        barsPanel.addControl(p);

        // - temperature scale
        barsPanel.addControl(this.vTempScalePanel());

        // - coolant bar

        final BindableTextComponent<Component> coolantFluidName = new BindableTextComponent<>((Component name) -> name);
        final BindableTextComponent<Integer> coolantAmount = new BindableTextComponent<>(
                amount -> new TextComponent(CodeHelper.formatAsHumanReadableNumber(amount / 1000, "B")).setStyle(STYLE_TOOLTIP_VALUE));
        final BindableTextComponent<Double> coolantStoredPercentage = new BindableTextComponent<>(
                percentage -> new TextComponent(String.format("%d", (int)(percentage * 100))).setStyle(STYLE_TOOLTIP_VALUE));

        p = this.vBarPanel();
        this.addBarIcon(CommonIcons.CoolantIcon, p).useTooltipsFrom(this._coolantBar);

        this._coolantBar.setTooltips(ImmutableList.of(
                new TranslatableComponent("gui.bigreactors.turbine.controller.coolantbar.line1").setStyle(STYLE_TOOLTIP_TITLE),
                new TranslatableComponent("gui.bigreactors.turbine.controller.coolantbar.line2").setStyle(STYLE_TOOLTIP_VALUE),
                new TranslatableComponent("gui.bigreactors.turbine.controller.coolantbar.line3a").setStyle(STYLE_TOOLTIP_VALUE)
                        .append(new TranslatableComponent("gui.bigreactors.turbine.controller.coolantbar.line3b")),
                new TranslatableComponent("gui.bigreactors.turbine.controller.coolantbar.line4a").setStyle(STYLE_TOOLTIP_VALUE)
                        .append(new TranslatableComponent("gui.bigreactors.turbine.controller.coolantbar.line4b")),
                TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.bigreactors.turbine.controller.coolantbar.line5"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.coolantbar.line6"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.coolantbar.line7"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.coolantbar.line8")
                ),
                ImmutableList.of(
                        // @0
                        coolantFluidName,
                        // @1
                        coolantAmount,
                        // @2
                        tankCapacity,
                        // @3
                        coolantStoredPercentage
                )
        );
        this.addBinding((MultiblockTurbine turbine) -> getFluidName(turbine.getFluidContainer().getLiquid()),
                v -> {
                    this._coolantBar.setBarSprite(Sprite.EMPTY);
                    this._coolantBar.setBarSpriteTint(Colour.WHITE);
                    this._turbine.getFluidContainer().getLiquid()
                            .ifPresent(fluid -> {

                                this._coolantBar.setBarSprite(ModRenderHelper.getFlowingFluidSprite(fluid));
                                this._coolantBar.setBarSpriteTint(Colour.fromARGB(fluid.getAttributes().getColor()));
                            });
                }, coolantFluidName);
        this.addBinding((MultiblockTurbine turbine) -> turbine.getFluidContainer().getLiquidAmount(), (Consumer<Integer>)this._coolantBar::setValue, coolantAmount);
        this.addBinding((MultiblockTurbine turbine) -> turbine.getFluidContainer().getLiquidStoredPercentage(), v -> {}, coolantStoredPercentage);

        p.addControl(this._coolantBar);
        barsPanel.addControl(p);

        // max intake rate info and selector

        p = this.hInfoPanelSlot();
        p.addControl(new Label(this, "maxIntakeLabel", new TranslatableComponent("gui.bigreactors.turbine.controller.maxintake.label")));
        infoPanelLeft.addControl(p);

        p = this.hInfoPanelSlot();

        this._maxIntakeRate.setStep(1, 10);
        this._maxIntakeRate.setDisplaySuffix(" mB/t");
        this._maxIntakeRate.setHorizontalAlignment(HorizontalAlignment.Right);
        this._maxIntakeRate.setDesiredDimension(INFOPANEL_WIDTH - 10, 14);
        this._maxIntakeRate.Changed.subscribe(this::onMaxIntakeRateChanged);
        this._maxIntakeRate.setTooltips(ImmutableList.of(
                new TranslatableComponent("gui.bigreactors.turbine.controlrod.maxintake.input.tooltip.line1").setStyle(STYLE_TOOLTIP_TITLE),
                TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.bigreactors.turbine.controlrod.maxintake.input.tooltip.line2"),
                new TranslatableComponent("gui.bigreactors.turbine.controlrod.maxintake.input.tooltip.line3"),
                new TranslatableComponent("gui.bigreactors.turbine.controlrod.maxintake.input.tooltip.line4"),
                new TranslatableComponent("gui.bigreactors.turbine.controlrod.maxintake.input.tooltip.line5"),
                new TranslatableComponent("gui.bigreactors.turbine.controlrod.maxintake.input.tooltip.line6"),
                TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.bigreactors.turbine.controlrod.maxintake.input.tooltip.line7"),
                new TranslatableComponent("gui.bigreactors.turbine.controlrod.maxintake.input.tooltip.line8"),
                new TranslatableComponent("gui.bigreactors.turbine.controlrod.maxintake.input.tooltip.line9"),
                new TranslatableComponent("gui.bigreactors.turbine.controlrod.maxintake.input.tooltip.line10"),
                new TranslatableComponent("gui.bigreactors.turbine.controlrod.maxintake.input.tooltip.line11"),
                new TranslatableComponent("gui.bigreactors.turbine.controlrod.maxintake.input.tooltip.line12"),
                new TranslatableComponent("gui.bigreactors.turbine.controlrod.maxintake.input.tooltip.line13"),
                new TranslatableComponent("gui.bigreactors.turbine.controlrod.maxintake.input.tooltip.line14"),
                new TranslatableComponent("gui.bigreactors.turbine.controlrod.maxintake.input.tooltip.line15")
                )
        );

        p.addControl(this._maxIntakeRate);
        infoPanelLeft.addControl(p);

        // - separator
        barsPanel.addControl(this.vSeparatorPanel());

        // - rpm bar

        final BindableTextComponent<Float> rpmText = new BindableTextComponent<>(rpm -> new TextComponent(String.format("%.2f RPM", rpm))
                .setStyle(STYLE_TOOLTIP_VALUE));

        p = this.vBarPanel();
        this.addBarIcon(CommonIcons.RotorRPM, 16, 16, p).useTooltipsFrom(this._rpmBar);

        this._rpmBar.setDesiredDimension(18, 66);
        this._rpmBar.setBackground(CommonIcons.BarBackground.get());
        this._rpmBar.setPadding(1);

        this._rpmBar.setTooltips(ImmutableList.of(
                new TranslatableComponent("gui.bigreactors.turbine.controller.rpmbar.line1").setStyle(STYLE_TOOLTIP_TITLE),
                new TranslatableComponent("gui.bigreactors.turbine.controller.rpmbar.line2").setStyle(STYLE_TOOLTIP_VALUE),
                TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.bigreactors.turbine.controller.rpmbar.line3"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.rpmbar.line4"),
                TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.bigreactors.turbine.controller.rpmbar.line5"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.rpmbar.line6"),
                TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.bigreactors.turbine.controller.rpmbar.line7"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.rpmbar.line8")
                ),
                ImmutableList.of(
                        // @0
                        rpmText
                )
        );
        this.addBinding(MultiblockTurbine::getMaxIntakeRate, this._maxIntakeRate::setValue);

        p.addControl(this._rpmBar);
        barsPanel.addControl(p);

        // - rpm info

        p = this.hInfoPanelSlot();
        c = new Picture(this, "rpmInfoIcon", CommonIcons.RotorRPM.get(), 16, 16);
        c.useTooltipsFrom(this._lblRpm);
        p.addControl(c);

        this._lblRpm.setTooltips(this._rpmBar.getTooltips(), this._rpmBar.getTooltipsObjects());

        this.addBinding(MultiblockTurbine::getRotorSpeed,
                rpm -> {

                    this._lblRpm.setText(String.format("%.2f RPM", rpm));
                    this._rpmBar.setValue(rpm);
                },
                rpmText);
        p.addControl(this._lblRpm);
        infoPanelRight.addControl(p);

        // - empty separator
        p = new Panel(this);

        p.setDesiredDimension(11, VBARPANEL_HEIGHT);
        barsPanel.addControl(p);

        // - energy bar

        p = this.vBarPanel();
        this.addBarIcon(CommonIcons.PowerBattery, 16, 16, p).useTooltipsFrom(this._energyBar);

        final BindableTextComponent<Double> energyStoredText = new BindableTextComponent<>(
                stored -> new TextComponent(CodeHelper.formatAsHumanReadableNumber(stored,
                        this._outputEnergySystem.getUnit())).setStyle(STYLE_TOOLTIP_VALUE));

        final BindableTextComponent<Double> energyStoredPercentageText = new BindableTextComponent<>(
                percentage -> new TextComponent(String.format("%d", (int)(percentage * 100))).setStyle(STYLE_TOOLTIP_VALUE));

        this._energyBar.setDesiredDimension(18, 66);
        this._energyBar.setBackground(CommonIcons.BarBackground.get());
        this._energyBar.setPadding(1);
        this._energyBar.setTooltips(ImmutableList.of(
                new TranslatableComponent("gui.bigreactors.reactor.controller.energybar.line1").setStyle(STYLE_TOOLTIP_TITLE),
                new TranslatableComponent("gui.bigreactors.reactor.controller.energybar.line2a").setStyle(STYLE_TOOLTIP_VALUE)
                        .append(new TranslatableComponent("gui.bigreactors.reactor.controller.energybar.line2b",
                                CodeHelper.formatAsHumanReadableNumber(this._turbineEnergyCapacity, this._outputEnergySystem.getUnit()))),
                new TranslatableComponent("gui.bigreactors.reactor.controller.energybar.line3a").setStyle(STYLE_TOOLTIP_VALUE)
                        .append(new TranslatableComponent("gui.bigreactors.reactor.controller.energybar.line3b")),
                TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.bigreactors.reactor.controller.energybar.line4"),
                new TranslatableComponent("gui.bigreactors.reactor.controller.energybar.line5"),
                new TranslatableComponent("gui.bigreactors.reactor.controller.energybar.line6"),
                TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.bigreactors.reactor.controller.energybar.line7"),
                new TranslatableComponent("gui.bigreactors.reactor.controller.energybar.line8")
                ),
                ImmutableList.of(
                        // @0
                        energyStoredText,
                        // @1
                        energyStoredPercentageText
                )
        );
        this.addBinding((MultiblockTurbine turbine) -> turbine.getEnergyStored(EnergySystem.REFERENCE, null),
                this._energyBar::setValue, energyStoredText);
        this.addBinding(AbstractGeneratorMultiblockController::getEnergyStoredPercentage, energyStoredPercentageText);
        p.addControl(this._energyBar);
        barsPanel.addControl(p);

        // - energy generation ratio

        final BindableTextComponent<Double> energyGeneratedText = new BindableTextComponent<>(
                generated -> new TextComponent(String.format("%.2f %s", generated,
                        this._outputEnergySystem.getUnit())).setStyle(STYLE_TOOLTIP_VALUE));

        p = this.hInfoPanelSlot();
        c = new Picture(this, "energyRatio", CommonIcons.EnergyRatioIcon.get(), 16, 16);
        c.useTooltipsFrom(this._lblEnergyRatio);
        p.addControl(c);

        this._lblEnergyRatio.setTooltips(ImmutableList.of(
                new TranslatableComponent("gui.bigreactors.turbine.controller.energyratio.line1").setStyle(STYLE_TOOLTIP_TITLE),
                new TranslatableComponent("gui.bigreactors.turbine.controller.energyratio.line2a").setStyle(STYLE_TOOLTIP_VALUE)
                        .append(new TranslatableComponent("gui.bigreactors.turbine.controller.energyratio.line2b")),
                TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.bigreactors.turbine.controller.energyratio.line3"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.energyratio.line4"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.energyratio.line5"),
                TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.bigreactors.turbine.controller.energyratio.line6"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.energyratio.line7")),
                ImmutableList.of(
                        // @0
                        energyGeneratedText
                )
        );
        this.addBinding(MultiblockTurbine::getEnergyGeneratedLastTick,
                value -> this._lblEnergyRatio.setText(CodeHelper.formatAsHumanReadableNumber(value, this._outputEnergySystem.getUnit() + "/t")),
                energyGeneratedText);
        p.addControl(this._lblEnergyRatio);
        infoPanelRight.addControl(p);

        // rotor status info

        final BindableTextComponent<String> rotorEfficiencyText = new BindableTextComponent<>(text -> new TextComponent(text).setStyle(STYLE_TOOLTIP_VALUE));
        final BindableTextComponent<String> rotorBlades = new BindableTextComponent<>(text -> new TextComponent(text).setStyle(STYLE_TOOLTIP_VALUE));

        p = this.hInfoPanelSlot();
        c = new Picture(this, "rotorStatusIcon", CommonIcons.RotorStatus.get(), 16, 16);
        c.useTooltipsFrom(this._lblRotorStatus);
        p.addControl(c);

        this._lblRotorStatus.setTooltips(ImmutableList.of(
                new TranslatableComponent("gui.bigreactors.turbine.controller.rotorstatus.line1").setStyle(STYLE_TOOLTIP_TITLE),
                new TranslatableComponent("gui.bigreactors.turbine.controller.rotorstatus.line2"),
                CodeHelper.TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.bigreactors.turbine.controller.rotorstatus.line3", String.format(ChatFormatting.DARK_AQUA + "" + ChatFormatting.BOLD + "%d", this._turbine.getVariant().getBaseFluidPerBlade())),
                new TranslatableComponent("gui.bigreactors.turbine.controller.rotorstatus.line4"),
                CodeHelper.TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.bigreactors.turbine.controller.rotorstatus.line5"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.rotorstatus.line6"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.rotorstatus.line7"),
                CodeHelper.TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.bigreactors.turbine.controller.rotorstatus.line8"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.rotorstatus.line9")),
                ImmutableList.of(
                        // @0
                        rotorEfficiencyText,
                        // @1
                        rotorBlades
                )
        );
        this.addBinding(TurbineControllerScreen::getRotorEfficiencyText, this._lblRotorStatus::setText, rotorEfficiencyText);
        this.addBinding(TurbineControllerScreen::getRotorBladesText, rotorBlades);

        p.addControl(this._lblRotorStatus);
        infoPanelRight.addControl(p);

        // - separator
        barsPanel.addControl(this.vSeparatorPanel());

        // COMMANDS

        final Panel commandPanel = this.vCommandPanel();

        barsPanel.addControl(commandPanel);

        // - machine on/off

        int x = 0;
        int y = 0;
        int w = 38;

        SwitchButton on = new SwitchButton(this, "on", "ON", false, "onoff");
        SwitchButton off = new SwitchButton(this, "off", "OFF", true, "onoff");

        on.setLayoutEngineHint(FixedLayoutEngine.hint(x, y, w, 16));
        on.setTooltips(new TranslatableComponent("gui.bigreactors.turbine.controller.on.line1"));
        on.Activated.subscribe(this::onActiveStateChanged);
        on.Deactivated.subscribe(this::onActiveStateChanged);
        this.addBinding(MultiblockTurbine::isMachineActive, on::setActive);

        off.setLayoutEngineHint(FixedLayoutEngine.hint(x + w, y, w, 16));
        off.setTooltips(new TranslatableComponent("gui.bigreactors.turbine.controller.off.line1"));
        this.addBinding(MultiblockTurbine::isMachineActive, active -> off.setActive(!active));

        commandPanel.addControl(on, off);
        y += 28;

        // inductor

        int xButton = x;

        final BindableTextComponent<Boolean> inductorEngagedText = new BindableTextComponent<>(
                engaged -> engaged ? TEXT_INDUCTOR_ENGAGED : TEXT_INDUCTOR_DISENGAGED);

        this._inductorEngaged.setLayoutEngineHint(FixedLayoutEngine.hint(xButton, y, 18, 18));
        this._inductorEngaged.Activated.subscribe(this::onInductorEngagedChanged);
        this._inductorEngaged.Deactivated.subscribe(this::onInductorEngagedChanged);
        this.setButtonSpritesAndOverlayForState(this._inductorEngaged, ButtonState.Default, CommonIcons.ButtonInductor);
        this.setButtonSpritesAndOverlayForState(this._inductorEngaged, ButtonState.Active, CommonIcons.ButtonInductorActive);
        this._inductorEngaged.setBackground(CommonIcons.ImageButtonBackground.get());
        this._inductorEngaged.enablePaintBlending(true);
        this._inductorEngaged.setPadding(1);
        this._inductorEngaged.setTooltips(ImmutableList.of(
                new TranslatableComponent("gui.bigreactors.turbine.controller.inductor.line1").setStyle(STYLE_TOOLTIP_TITLE),
                new TranslatableComponent("gui.bigreactors.turbine.controller.inductor.line2").setStyle(STYLE_TOOLTIP_VALUE),
                TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.bigreactors.turbine.controller.inductor.line3"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.inductor.line4"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.inductor.line5"),
                TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.bigreactors.turbine.controller.inductor.line6"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.inductor.line7"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.inductor.line8"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.inductor.line9"),
                TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.bigreactors.turbine.controller.inductor.line10"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.inductor.line11"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.inductor.line12")),
                ImmutableList.of(
                        // @0
                        inductorEngagedText
                )
        );
        this.addBinding(MultiblockTurbine::isInductorEngaged, this._inductorEngaged::setActive, inductorEngagedText);

        commandPanel.addControl(this._inductorEngaged);
        xButton += 22+1-3;

        // - vent settings

        this._ventAll.setLayoutEngineHint(FixedLayoutEngine.hint(xButton, y, 18, 18));
        this._ventAll.setTooltips(ImmutableList.of(
                new TranslatableComponent("gui.bigreactors.turbine.controller.vent.all.line1").setStyle(STYLE_TOOLTIP_TITLE),
                TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.bigreactors.turbine.controller.vent.all.line2"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.vent.all.line3"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.vent.all.line4")));
        commandPanel.addControl(this._ventAll);
        xButton += 18+1;

        this._ventOverflow.setLayoutEngineHint(FixedLayoutEngine.hint(xButton, y, 18, 18));
        this._ventOverflow.setTooltips(ImmutableList.of(
                new TranslatableComponent("gui.bigreactors.turbine.controller.vent.overflow.line1").setStyle(STYLE_TOOLTIP_TITLE),
                TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.bigreactors.turbine.controller.vent.overflow.line2"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.vent.overflow.line3"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.vent.overflow.line4")));
        commandPanel.addControl(this._ventOverflow);
        xButton += 18+1;

        this._ventDoNotVent.setLayoutEngineHint(FixedLayoutEngine.hint(xButton, y, 18, 18));
        this._ventDoNotVent.setTooltips(ImmutableList.of(
                new TranslatableComponent("gui.bigreactors.turbine.controller.vent.donotvent.line1").setStyle(STYLE_TOOLTIP_TITLE),
                TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.bigreactors.turbine.controller.vent.donotvent.line2"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.vent.donotvent.line3"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.vent.donotvent.line4"),
                new TranslatableComponent("gui.bigreactors.turbine.controller.vent.donotvent.line5")));
        commandPanel.addControl(this._ventDoNotVent);

        this.addBinding(MultiblockTurbine::getVentSetting, setting -> {

            this._ventAll.setActive(VentSetting.VentAll.test(setting));
            this._ventOverflow.setActive(VentSetting.VentOverflow.test(setting));
            this._ventDoNotVent.setActive(VentSetting.DoNotVent.test(setting));
        });

//        y += 29;

        // - scram

//        final Button scram = new Button(this, "scram", "SCRAM");
//
//        scram.setLayoutEngineHint(FixedLayoutEngine.hint(x, y, w * 2, 25));
//        scram.setTooltips(new TranslationTextComponent("gui.bigreactors.reactor.controller.scram.line1").setStyle(STYLE_TOOLTIP_TITLE),
//                TEXT_EMPTY_LINE,
//                new TranslationTextComponent("gui.bigreactors.reactor.controller.scram.line2"),
//                new TranslationTextComponent("gui.bigreactors.reactor.controller.scram.line3"),
//                new TranslationTextComponent("gui.bigreactors.reactor.controller.scram.line4").setStyle(Style.EMPTY.setItalic(true)));
//        scram.Clicked.subscribe(this::onScram);
//        commandPanel.addControl(scram);
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

    //region internals
    //region GUI controls helpers

    private final static int VBARPANEL_WIDTH = 18;
    private final static int VBARPANEL_HEIGHT = 84;
    private final static int INFOPANEL_WIDTH = 88;

    private Panel vCommandPanel() {

        final Panel p = new Panel(this);

        p.setDesiredDimension(DesiredDimension.Height, VBARPANEL_HEIGHT);
        p.setLayoutEngine(new FixedLayoutEngine());

        return p;
    }

    private Panel vBarPanel() {

        final Panel p = new Panel(this);

        p.setDesiredDimension(VBARPANEL_WIDTH, VBARPANEL_HEIGHT);
        p.setLayoutEngine(new VerticalLayoutEngine()
                .setZeroMargins()
                .setControlsSpacing(2));

        return p;
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

    private Panel vTempScalePanel() {

        final Panel p = new Panel(this);
        final Picture pic = new Picture(this, this.nextGenericName(), CommonIcons.TemperatureScale.get(), 5, 59);

        p.setDesiredDimension(11, VBARPANEL_HEIGHT);
        p.setLayoutEngine(new FixedLayoutEngine());

        pic.setLayoutEngineHint(FixedLayoutEngine.hint(3, 23, 5, 59));

        p.addControl(pic);

        return p;
    }

    private Panel hInfoPanel() {

        final Panel p = new Panel(this);

        p.setDesiredDimension(DesiredDimension.Width, INFOPANEL_WIDTH);
        p.setLayoutEngine(new VerticalLayoutEngine()
                .setZeroMargins()
                .setControlsSpacing(1));

        return p;
    }

    private Panel hInfoPanelSlot() {

        final Panel p = new Panel(this);

        p.setDesiredDimension(INFOPANEL_WIDTH, 16);
        p.setLayoutEngine(new HorizontalLayoutEngine()
                .setZeroMargins()
                .setControlsSpacing(3));

        return p;
    }

    private Label infoLabel(final String name, final String value) {

        final Label l = new Label(this, name, value);

        l.setAutoSize(false);
        l.setDesiredDimension(INFOPANEL_WIDTH - 20, 10);

        return l;
    }

    private GaugeBar liquidBar(final String name, final double maxValue) {

        final GaugeBar bar = new GaugeBar(this, name, maxValue, CommonIcons.BarBackground.get());

        bar.setDesiredDimension(18, 66);
        bar.setBackground(CommonIcons.BarBackground.get());
        bar.setPadding(1);

        return bar;
    }

    private SwitchPictureButton ventButton(final VentSetting setting) {

        final SwitchPictureButton swp = new SwitchPictureButton(this, setting.name(), false, "ventSetting");

        swp.setTag(setting);
        swp.Activated.subscribe(this::onVentSettingChanged);
        swp.enablePaintBlending(true);
        swp.setPadding(1);
        swp.setBackground(CommonIcons.ImageButtonBackground.get());

        switch (setting) {

            case VentAll:
                this.setButtonSpritesAndOverlayForState(swp, ButtonState.Default, CommonIcons.ButtonVentAll);
                this.setButtonSpritesAndOverlayForState(swp, ButtonState.Active, CommonIcons.ButtonVentAllActive);
                break;

            case VentOverflow:
                this.setButtonSpritesAndOverlayForState(swp, ButtonState.Default, CommonIcons.ButtonVentOverflow);
                this.setButtonSpritesAndOverlayForState(swp, ButtonState.Active, CommonIcons.ButtonVentOverflowActive);
                break;

            case DoNotVent:
                this.setButtonSpritesAndOverlayForState(swp, ButtonState.Default, CommonIcons.ButtonVentDoNot);
                this.setButtonSpritesAndOverlayForState(swp, ButtonState.Active, CommonIcons.ButtonVentDoNotActive);
                break;
        }

        return swp;
    }

    private IControl addBarIcon(final NonNullSupplier<ISprite> icon, final Panel parent) {
        return this.addBarIcon(icon, 16, 16, parent);
    }

    private IControl addBarIcon(final NonNullSupplier<ISprite> icon, final int width, final int height, final Panel parent) {

        final IControl c = new Picture(this, this.nextGenericName(), icon.get(), width, height);

        c.setDesiredDimension(width, height);
        parent.addControl(c);
        return c;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static Component getFluidName(final Optional<Fluid> fluid) {
        return fluid.map(f -> (Component)new TranslatableComponent(f.getAttributes().getTranslationKey()).setStyle(STYLE_TOOLTIP_VALUE))
                .orElse(TEXT_EMPTY);
    }

    private static String getRotorEfficiencyText(final MultiblockTurbine turbine) {
        return String.format("%.1f%%", Mth.clamp(turbine.getRotorEfficiencyLastTick(), 0.0f, 1.0f) * 100);
    }

    private static String getRotorBladesText(final MultiblockTurbine turbine) {

        final int numBlades = turbine.getRotorBladesCount();
        final int neededBlades = turbine.getFluidConsumedLastTick() / turbine.getVariant().getBaseFluidPerBlade();

        return String.format("%d / %d", numBlades, neededBlades);
    }

    //endregion

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

    private void onMaxIntakeRateChanged(final NumberInput<Integer> inputControl, int newRate) {

        final CompoundTag data = new CompoundTag();

        data.putInt("rate", newRate);
        this.sendCommandToServer(TurbineControllerEntity.COMMAND_SET_INTAKERATE, data);
    }

    private void onScram(final Button button, final Integer mouseButton) {
        //TODO imp
        this.sendCommandToServer(ReactorControllerEntity.COMMAND_SCRAM);
    }

    private final <Value> void addBinding(final Function<MultiblockTurbine, Value> supplier, final Consumer<Value> consumer) {
        this._bindings.addBinding(new MonoConsumerBinding<>(this._turbine, supplier, consumer));
    }

    @SafeVarargs
    private final <Value> void addBinding(final Function<MultiblockTurbine, Value> supplier, final Consumer<Value>... consumers) {
        this._bindings.addBinding(new MultiConsumerBinding<>(this._turbine, supplier, consumers));
    }

    private static final Component TEXT_EMPTY = new TranslatableComponent("gui.bigreactors.generic.empty").setStyle(STYLE_TOOLTIP_VALUE);

    private static final Component TEXT_INDUCTOR_ENGAGED = new TranslatableComponent("gui.bigreactors.turbine.controller.inductor.mode.engaged").setStyle(STYLE_TOOLTIP_VALUE);
    private static final Component TEXT_INDUCTOR_DISENGAGED = new TranslatableComponent("gui.bigreactors.turbine.controller.inductor.mode.disengaged").setStyle(STYLE_TOOLTIP_VALUE);

    private static final Component TEXT_ROTOR_EFFICIENCY_100 = new TranslatableComponent("gui.bigreactors.turbine.controller.rotorstatus.100").setStyle(STYLE_TOOLTIP_VALUE);


    private final MultiblockTurbine _turbine;
    private final EnergySystem _outputEnergySystem;
    private final double _turbineEnergyCapacity;

    private final BindingGroup _bindings;

    private final GaugeBar _coolantBar;
    private final GaugeBar _vaporBar;
    private final GaugeBar _rpmBar;
    private final GaugeBar _energyBar;
    private final NumberInput.IntNumberInput _maxIntakeRate;

    private final SwitchPictureButton _inductorEngaged;
    private final SwitchPictureButton _ventAll;
    private final SwitchPictureButton _ventOverflow;
    private final SwitchPictureButton _ventDoNotVent;

    private final Label _lblEnergyRatio;
    private final Label _lblRpm;
    private final Label _lblRotorStatus;

    //endregion
}
