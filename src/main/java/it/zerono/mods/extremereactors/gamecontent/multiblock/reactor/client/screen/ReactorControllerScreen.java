/*
 *
 * ReactorControllerScreen.java
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
import it.zerono.mods.extremereactors.api.reactor.ReactantType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.AbstractGeneratorMultiblockController;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.AbstractMultiblockScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.AbstractMultiblockEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.OperationalMode;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.ClientFuelRodsLayout;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorControllerEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
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
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.NonNullSupplier;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static it.zerono.mods.zerocore.lib.CodeHelper.TEXT_EMPTY_LINE;

@OnlyIn(Dist.CLIENT)
public class ReactorControllerScreen
        extends AbstractMultiblockScreen<MultiblockReactor, ReactorControllerEntity, ModTileContainer<ReactorControllerEntity>> {

    public ReactorControllerScreen(final ModTileContainer<ReactorControllerEntity> container,
                                   final PlayerInventory inventory, final ITextComponent title) {

        super(container, inventory, PlayerInventoryUsage.None, title,
                mainTextureFromVariant(container.getTileEntity().getMultiblockVariant().orElse(ReactorVariant.Basic)));

        this._reactor = this.getMultiblockController().orElseThrow(IllegalStateException::new);
        this._reactorMode = this._reactor.getOperationalMode();
        this._outputEnergySystem = this._reactor.getOutputEnergySystem();
        this._reactorCapacity = this._reactor.getCapacity(this._outputEnergySystem, null);

        this._bindings = new BindingGroup();

        this._fuelBar = new FuelGaugeBar(this, "fuelBar", this._reactor);
        this._coreHeatBar = this.heatBar("coreHeatBar", 2000.0);
        this._casingHeatBar = this.heatBar("casingHeatBar", 2000.0);
        this._lblTemperature = this.infoLabel("temperatureValue", "0 C");
        this._lblFuelUsage = this.infoLabel("fuelUsageValue", "0 mB/t");
        this._lblFuelRichness = this.infoLabel("fuelRichnessValue", "0%");

        if (this._reactorMode.isPassive()) {

            this._lblEnergyRatio = this.infoLabel("energyRatioValue", "");
            this._energyBar = new GaugeBar(this, "energyBar", this._reactorCapacity, CommonIcons.PowerBar.get());
            this._coolantBar = this._vaporBar = null;
            this._lblVaporRatio = null;

        } else {

            this._lblEnergyRatio = null;
            this._energyBar = null;
            this._coolantBar = this.liquidBar("coolantBar", this._reactor.getFluidContainer().getCapacity());
            this._vaporBar = this.liquidBar("vaporBar", this._reactor.getFluidContainer().getCapacity());
            this._lblVaporRatio = this.infoLabel("vaporRatioValue", "");
        }
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

        Static s;
        Panel p;

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
        s.setLayoutEngineHint(FixedLayoutEngine.hint(0, 0, this.getGuiWidth() - 29, 1));

        p.addControl(s);
        outerPanel.addControl(p);

        // INFOS

        infoPanel.setLayoutEngine(new HorizontalLayoutEngine()
                .setZeroMargins()
                .setVerticalAlignment(VerticalAlignment.Top)
                .setHorizontalAlignment(HorizontalAlignment.Left));
        infoPanel.addControl(infoPanelLeft, infoPanelRight);
        outerPanel.addControl(infoPanel);

        // - fuel bar

        p = this.vBarPanel();
        this.addBarIcon(CommonIcons.FuelIcon, p);

        this._fuelBar.setDesiredDimension(18, 66);
        this._fuelBar.setBackground(CommonIcons.BarBackground.get());
        this._fuelBar.setPadding(1);
        this.addBinding(MultiblockReactor::getFuelAmount, value -> this._fuelBar.setValue(ReactantType.Fuel, value));
        this.addBinding(MultiblockReactor::getWasteAmount, value -> this._fuelBar.setValue(ReactantType.Waste, value));
        this.addBinding((MultiblockReactor reactor) -> reactor.getFuelRodsLayout()
                        .filter(layout -> layout instanceof ClientFuelRodsLayout)
                        .map(layout -> (ClientFuelRodsLayout)layout)
                        .map(ClientFuelRodsLayout::getFuelColor)
                        .orElse(Colour.fromRGB(ReactantType.Fuel.getDefaultColour())),
                value -> this._fuelBar.setBarSpriteTint(ReactantType.Fuel, value));
        this.addBinding((MultiblockReactor reactor) -> reactor.getFuelRodsLayout()
                        .filter(layout -> layout instanceof ClientFuelRodsLayout)
                        .map(layout -> (ClientFuelRodsLayout)layout)
                        .map(ClientFuelRodsLayout::getWasteColor)
                        .orElse(Colour.fromRGB(ReactantType.Waste.getDefaultColour())),
                value -> this._fuelBar.setBarSpriteTint(ReactantType.Waste, value));
        p.addControl(this._fuelBar);
        barsPanel.addControl(p);

        // - separator
        barsPanel.addControl(this.vSeparatorPanel());

        // - core heat bar

        final BindableTextComponent<Double> coreHeatText = new BindableTextComponent<>(
                heat -> new StringTextComponent(String.format("%.0f C", heat)).setStyle(STYLE_TOOLTIP_VALUE));

        p = this.vBarPanel();
        this.addBarIcon(CommonIcons.ButtonSensorOutputFuelTemperature, p);

        this._coreHeatBar.setTooltips(ImmutableList.of(
                new TranslationTextComponent("gui.bigreactors.reactor.controller.coreheatbar.line1").setStyle(STYLE_TOOLTIP_TITLE),
                new TranslationTextComponent("gui.bigreactors.reactor.controller.coreheatbar.line2"),
                TEXT_EMPTY_LINE,
                new TranslationTextComponent("gui.bigreactors.reactor.controller.coreheatbar.line3"),
                new TranslationTextComponent("gui.bigreactors.reactor.controller.coreheatbar.line4"),
                TEXT_EMPTY_LINE,
                new TranslationTextComponent("gui.bigreactors.reactor.controller.coreheatbar.line5"),
                new TranslationTextComponent("gui.bigreactors.reactor.controller.coreheatbar.line6"),
                new TranslationTextComponent("gui.bigreactors.reactor.controller.coreheatbar.line7"),
                new TranslationTextComponent("gui.bigreactors.reactor.controller.coreheatbar.line8")),
                ImmutableList.of(
                        // @0
                        coreHeatText
                )
        );
        this.addBinding((MultiblockReactor reactor) -> reactor.getFuelHeat().get(),
                (value) -> {
                    this._coreHeatBar.setValue(value);
                    this._lblTemperature.setText("%d C", value.intValue());
                },
                coreHeatText);
        p.addControl(this._coreHeatBar);
        barsPanel.addControl(p);

        // - temperature scale
        barsPanel.addControl(this.vTempScalePanel());

        // - casing heat bar

        final BindableTextComponent<Double> reactorHeatText = new BindableTextComponent<>(
                heat -> new StringTextComponent(String.format("%.0f C", heat)).setStyle(STYLE_TOOLTIP_VALUE));

        p = this.vBarPanel();
        this.addBarIcon(CommonIcons.ButtonSensorOutputCasingTemperature, p);

        this._casingHeatBar.setTooltips(ImmutableList.of(
                new TranslationTextComponent("gui.bigreactors.reactor.controller.casingheatbar.line1").setStyle(STYLE_TOOLTIP_TITLE),
                new TranslationTextComponent("gui.bigreactors.reactor.controller.casingheatbar.line2"),
                TEXT_EMPTY_LINE,
                new TranslationTextComponent("gui.bigreactors.reactor.controller.casingheatbar.line3"),
                new TranslationTextComponent("gui.bigreactors.reactor.controller.casingheatbar.line4"),
                new TranslationTextComponent("gui.bigreactors.reactor.controller.casingheatbar.line5")),
                ImmutableList.of(
                        // @0
                        reactorHeatText
                )
        );
        this.addBinding((MultiblockReactor reactor) -> reactor.getReactorHeat().get(), this._casingHeatBar::setValue, reactorHeatText);
        p.addControl(this._casingHeatBar);
        barsPanel.addControl(p);

        // - separator
        barsPanel.addControl(this.vSeparatorPanel());

        if (this._reactorMode.isPassive()) {

            ////////////////////////////////////////////////////////////////////////////////////////////
            //
            // PASSIVE REACTOR GUI
            //
            ////////////////////////////////////////////////////////////////////////////////////////////

            p = new Panel(this);
            p.setDesiredDimension(VBARPANEL_WIDTH * 2 + 11, VBARPANEL_HEIGHT);
            p.setLayoutEngine(new VerticalLayoutEngine()
                    .setHorizontalAlignment(HorizontalAlignment.Left)
                    .setZeroMargins()
                    .setControlsSpacing(2));

            // - energy bar

            this.addBarIcon(CommonIcons.PowerBattery, 16, 16, p);

            final BindableTextComponent<Double> energyStoredText = new BindableTextComponent<>(
                    stored -> new StringTextComponent(CodeHelper.formatAsHumanReadableNumber(stored,
                            this._outputEnergySystem.getUnit())).setStyle(STYLE_TOOLTIP_VALUE));

            final BindableTextComponent<Double> energyStoredPercentageText = new BindableTextComponent<>(
                    percentage -> new StringTextComponent(String.format("%d", (int)(percentage * 100))).setStyle(STYLE_TOOLTIP_VALUE));

            this._energyBar.setDesiredDimension(18, 66);
            this._energyBar.setBackground(CommonIcons.BarBackground.get());
            this._energyBar.setPadding(1);
            this._energyBar.setTooltips(ImmutableList.of(
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.energybar.line1").setStyle(STYLE_TOOLTIP_TITLE),
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.energybar.line2a").setStyle(STYLE_TOOLTIP_VALUE)
                                .append(new TranslationTextComponent("gui.bigreactors.reactor.controller.energybar.line2b",
                                        CodeHelper.formatAsHumanReadableNumber(this._reactorCapacity, this._outputEnergySystem.getUnit()))),
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.energybar.line3a").setStyle(STYLE_TOOLTIP_VALUE)
                                .append(new TranslationTextComponent("gui.bigreactors.reactor.controller.energybar.line3b")),
                    TEXT_EMPTY_LINE,
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.energybar.line4"),
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.energybar.line5"),
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.energybar.line6"),
                    TEXT_EMPTY_LINE,
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.energybar.line7"),
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.energybar.line8")
                    ),
                    ImmutableList.of(
                            // @0
                            energyStoredText,
                            // @1
                            energyStoredPercentageText
                    )
            );
            this.addBinding((MultiblockReactor reactor) -> reactor.getEnergyStored(EnergySystem.REFERENCE, null),
                    this._energyBar::setValue, energyStoredText);
            this.addBinding(AbstractGeneratorMultiblockController::getEnergyStoredPercentage, energyStoredPercentageText);
            p.addControl(this._energyBar);
            barsPanel.addControl(p);

            // - energy generation ratio

            final BindableTextComponent<Double> energyGeneratedText = new BindableTextComponent<>(
                    generated -> new StringTextComponent(String.format("%.2f %s", generated,
                            this._outputEnergySystem.getUnit())).setStyle(STYLE_TOOLTIP_VALUE));

            p = this.hInfoPanelSlot();
            p.addControl(new Picture(this, "energyRatio", CommonIcons.EnergyRatioIcon.get(), 16, 16));

            this._lblEnergyRatio.setTooltips(ImmutableList.of(
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.energyratio.line1").setStyle(STYLE_TOOLTIP_TITLE),
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.energyratio.line2a").setStyle(STYLE_TOOLTIP_VALUE)
                        .append(new TranslationTextComponent("gui.bigreactors.reactor.controller.energyratio.line2b")),
                    TEXT_EMPTY_LINE,
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.energyratio.line3"),
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.energyratio.line4"),
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.energyratio.line5"),
                    TEXT_EMPTY_LINE,
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.energyratio.line6"),
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.energyratio.line7"),
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.energyratio.line8")),
                    ImmutableList.of(
                            // @0
                            energyGeneratedText
                    )
            );
            this.addBinding((MultiblockReactor reactor) -> reactor.getUiStats().getAmountGeneratedLastTick(),
                    value -> this._lblEnergyRatio.setText(CodeHelper.formatAsHumanReadableNumber(value, this._outputEnergySystem.getUnit() + "/t")),
                    energyGeneratedText);
            p.addControl(this._lblEnergyRatio);
            infoPanelRight.addControl(p);

        } else {

            ////////////////////////////////////////////////////////////////////////////////////////////
            //
            // ACTIVE REACTOR GUI
            //
            ////////////////////////////////////////////////////////////////////////////////////////////

            final BindableTextComponent<Integer> tankCapacity = new BindableTextComponent<>(
                    capacity -> new StringTextComponent(CodeHelper.formatAsHumanReadableNumber(capacity / 1000, "B")).setStyle(STYLE_TOOLTIP_VALUE));

            this.addBinding((MultiblockReactor reactor) -> this._reactor.getFluidContainer().getCapacity(),
                    v -> {

                        this._coolantBar.setMaxValue(v);
                        this._vaporBar.setMaxValue(v);

                    }, tankCapacity);

            // - coolant bar

            final BindableTextComponent<ITextComponent> coolantFluidName = new BindableTextComponent<>((ITextComponent name) -> name);
            final BindableTextComponent<Integer> coolantAmount = new BindableTextComponent<>(
                    amount -> new StringTextComponent(CodeHelper.formatAsHumanReadableNumber(amount / 1000, "B")).setStyle(STYLE_TOOLTIP_VALUE));
            final BindableTextComponent<Double> coolantStoredPercentage = new BindableTextComponent<>(
                    percentage -> new StringTextComponent(String.format("%d", (int)(percentage * 100))).setStyle(STYLE_TOOLTIP_VALUE));

            p = this.vBarPanel();
            this.addBarIcon(CommonIcons.CoolantIcon, p);

            this._coolantBar.setTooltips(ImmutableList.of(
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.coolantbar.line1").setStyle(STYLE_TOOLTIP_TITLE),
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.coreheatbar.line2").setStyle(STYLE_TOOLTIP_VALUE),
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.coolantbar.line3a").setStyle(STYLE_TOOLTIP_VALUE)
                            .append(new TranslationTextComponent("gui.bigreactors.reactor.controller.coolantbar.line3b")),
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.coolantbar.line4a").setStyle(STYLE_TOOLTIP_VALUE)
                            .append(new TranslationTextComponent("gui.bigreactors.reactor.controller.coolantbar.line4b")),
                    TEXT_EMPTY_LINE,
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.coolantbar.line5"),
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.coolantbar.line6")
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
            this.addBinding((MultiblockReactor reactor) -> getFluidName(reactor.getFluidContainer().getLiquid()),
                    v -> {
                        this._coolantBar.setBarSprite(Sprite.EMPTY);
                        this._coolantBar.setBarSpriteTint(Colour.WHITE);
                        this._reactor.getFluidContainer().getLiquid()
                                .ifPresent(fluid -> {

                                    this._coolantBar.setBarSprite(ModRenderHelper.getFlowingFluidSprite(fluid));
                                    this._coolantBar.setBarSpriteTint(Colour.fromARGB(fluid.getAttributes().getColor()));
                                });
                    }, coolantFluidName);
            this.addBinding((MultiblockReactor reactor) -> reactor.getFluidContainer().getLiquidAmount(), (Consumer<Integer>)this._coolantBar::setValue, coolantAmount);
            this.addBinding((MultiblockReactor reactor) -> reactor.getFluidContainer().getLiquidStoredPercentage(), v -> {}, coolantStoredPercentage);

            p.addControl(this._coolantBar);
            barsPanel.addControl(p);

            // - temperature scale
            barsPanel.addControl(this.vTempScalePanel());

            // - vapor bar

            final BindableTextComponent<ITextComponent> vaporFluidName = new BindableTextComponent<>((ITextComponent name) -> name);
            final BindableTextComponent<Integer> vaporAmount = new BindableTextComponent<>(
                    amount -> new StringTextComponent(CodeHelper.formatAsHumanReadableNumber(amount / 1000, "B")).setStyle(STYLE_TOOLTIP_VALUE));
            final BindableTextComponent<Double> vaporStoredPercentage = new BindableTextComponent<>(
                    percentage -> new StringTextComponent(String.format("%d", (int)(percentage * 100))).setStyle(STYLE_TOOLTIP_VALUE));

            p = this.vBarPanel();
            this.addBarIcon(CommonIcons.VaporIcon, p);

            this._vaporBar.setTooltips(ImmutableList.of(
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.vaporbar.line1").setStyle(STYLE_TOOLTIP_TITLE),
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.vaporbar.line2").setStyle(STYLE_TOOLTIP_VALUE),
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.vaporbar.line3a").setStyle(STYLE_TOOLTIP_VALUE)
                            .append(new TranslationTextComponent("gui.bigreactors.reactor.controller.vaporbar.line3b")),
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.vaporbar.line4a").setStyle(STYLE_TOOLTIP_VALUE)
                            .append(new TranslationTextComponent("gui.bigreactors.reactor.controller.vaporbar.line4b")),
                    TEXT_EMPTY_LINE,
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.vaporbar.line5"),
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.vaporbar.line6"),
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.vaporbar.line7")
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
            this.addBinding((MultiblockReactor reactor) -> getFluidName(reactor.getFluidContainer().getGas()),
                    v -> {
                        this._vaporBar.setBarSprite(Sprite.EMPTY);
                        this._vaporBar.setBarSpriteTint(Colour.WHITE);
                        this._reactor.getFluidContainer().getGas()
                                .ifPresent(fluid -> {

                                    this._vaporBar.setBarSprite(ModRenderHelper.getFlowingFluidSprite(fluid));
                                    this._vaporBar.setBarSpriteTint(Colour.fromARGB(fluid.getAttributes().getColor()));
                                });
                    }, vaporFluidName);
            this.addBinding((MultiblockReactor reactor) -> reactor.getFluidContainer().getGasAmount(), (Consumer<Integer>)this._vaporBar::setValue, vaporAmount);
            this.addBinding((MultiblockReactor reactor) -> reactor.getFluidContainer().getGasStoredPercentage(), v -> {}, vaporStoredPercentage);

            p.addControl(this._vaporBar);
            barsPanel.addControl(p);

            // - vapor generation ratio

            final BindableTextComponent<Double> vaporGeneratedText = new BindableTextComponent<>(
                    generated -> new StringTextComponent(String.format("%.2f %s", generated, "B")).setStyle(STYLE_TOOLTIP_VALUE));

            p = this.hInfoPanelSlot();
            p.addControl(new Picture(this, "vaporRatio", CommonIcons.VaporIcon.get(), 16, 16)); //TODO fix icon

            this._lblVaporRatio.setTooltips(ImmutableList.of(
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.vaporratio.line1").setStyle(STYLE_TOOLTIP_TITLE),
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.vaporratio.line2a").setStyle(STYLE_TOOLTIP_VALUE)
                            .append(new TranslationTextComponent("gui.bigreactors.reactor.controller.vaporratio.line2b")),
                    TEXT_EMPTY_LINE,
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.vaporratio.line3"),
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.vaporratio.line4"),
                    new TranslationTextComponent("gui.bigreactors.reactor.controller.vaporratio.line5")),
                    ImmutableList.of(
                            // @0
                            vaporGeneratedText
                    )
            );
            this.addBinding((MultiblockReactor reactor) -> reactor.getUiStats().getAmountGeneratedLastTick(),
                    value -> this._lblVaporRatio.setText(CodeHelper.formatAsHumanReadableNumber(value, "B" + "/t")),
                    vaporGeneratedText);
            p.addControl(this._lblVaporRatio);
            infoPanelRight.addControl(p);
        }

        // - separator
        barsPanel.addControl(this.vSeparatorPanel());

        ////////////////////////////////////////////////////////////////////////////////////////////////////
        //
        // TEXT INFO PANELS
        //
        ////////////////////////////////////////////////////////////////////////////////////////////////////

        // - temperature

        p = this.hInfoPanelSlot();
        p.addControl(new Picture(this, "temperature", CommonIcons.TemperatureIcon.get(), 16, 16));

        this._lblTemperature.setTooltips(this._coreHeatBar.getTooltips(), this._coreHeatBar.getTooltipsObjects());
        p.addControl(this._lblTemperature);
        infoPanelLeft.addControl(p);

        // - fuel usage

        p = this.hInfoPanelSlot();
        p.addControl(new Picture(this, "fuelusage", CommonIcons.FuelMix.get(), 16, 16));

        this._lblFuelUsage.setTooltips(
                new TranslationTextComponent("gui.bigreactors.reactor.controller.fuelusage.line1").setStyle(STYLE_TOOLTIP_TITLE),
                TEXT_EMPTY_LINE,
                new TranslationTextComponent("gui.bigreactors.reactor.controller.fuelusage.line2"),
                new TranslationTextComponent("gui.bigreactors.reactor.controller.fuelusage.line3"));
        this.addBinding((MultiblockReactor reactor) -> reactor.getUiStats().getFuelConsumedLastTick(),
                value -> this._lblFuelUsage.setText(CodeHelper.formatAsMillibuckets(value) + "/t"));
        p.addControl(this._lblFuelUsage);
        infoPanelLeft.addControl(p);

        // - fuel reactivity

        p = this.hInfoPanelSlot();
        p.addControl(new Picture(this, "fuelReactivity", CommonIcons.ReactivityIcon.get(), 16, 16));

        this._lblFuelRichness.setTooltips(
                new TranslationTextComponent("gui.bigreactors.reactor.controller.fuelrichness.line1").setStyle(STYLE_TOOLTIP_TITLE),
                TEXT_EMPTY_LINE,
                new TranslationTextComponent("gui.bigreactors.reactor.controller.fuelrichness.line2"),
                new TranslationTextComponent("gui.bigreactors.reactor.controller.fuelrichness.line3"),
                new TranslationTextComponent("gui.bigreactors.reactor.controller.fuelrichness.line4"));
        this.addBinding((MultiblockReactor reactor) -> reactor.getUiStats().getFuelRichness(),
                value -> this._lblFuelRichness.setText("%2.0f%%", value * 100f));
        p.addControl(this._lblFuelRichness);
        infoPanelLeft.addControl(p);
        
        // COMMANDS

        final Panel commandPanel = this.vCommandPanel();
        SwitchPictureButton swp;

        barsPanel.addControl(commandPanel);

        // - machine on/off

        int x = 0;
        int y = 0;
        int w = 25;

        SwitchButton on = new SwitchButton(this, "on", "ON", false, "onoff");
        SwitchButton off = new SwitchButton(this, "off", "OFF", true, "onoff");

        on.setLayoutEngineHint(FixedLayoutEngine.hint(x, y, w, 16));
        on.setTooltips(new TranslationTextComponent("gui.bigreactors.reactor.controller.on.line1"));
        on.Activated.subscribe(this::onActiveStateChanged);
        on.Deactivated.subscribe(this::onActiveStateChanged);
        this.addBinding(MultiblockReactor::isMachineActive, on::setActive);

        off.setLayoutEngineHint(FixedLayoutEngine.hint(x + w, y, w, 16));
        off.setTooltips(new TranslationTextComponent("gui.bigreactors.reactor.controller.off.line1"));
        this.addBinding(MultiblockReactor::isMachineActive, active -> off.setActive(!active));

        commandPanel.addControl(on, off);
        y += 28;

        // - waste ejection settings

        final BindableTextComponent<Boolean> wasteEjectionText = new BindableTextComponent<>(
                automatic -> automatic ? TEXT_AUTOMATIC_WASTE_EJECT : TEXT_MANUAL_WASTE_EJECT);

        swp = new SwitchPictureButton(this, "wasteeject", false);
        swp.Activated.subscribe(this::onWasteEjectionChanged);
        swp.Deactivated.subscribe(this::onWasteEjectionChanged);
        this.setButtonSpritesAndOverlayForState(swp, ButtonState.Default, CommonIcons.ButtonDumpWaste);
        this.setButtonSpritesAndOverlayForState(swp, ButtonState.Active, CommonIcons.ButtonDumpWasteActive);
        swp.setLayoutEngineHint(FixedLayoutEngine.hint(x + 17, y, 18, 18));
        swp.setBackground(CommonIcons.ImageButtonBackground.get());
        swp.enablePaintBlending(true);
        swp.setPadding(1);
        swp.setTooltips(ImmutableList.of(
                new TranslationTextComponent("gui.bigreactors.reactor.controller.wasteeject.line1").setStyle(STYLE_TOOLTIP_TITLE),
                new TranslationTextComponent("gui.bigreactors.reactor.controller.wasteeject.line2"),
                TEXT_EMPTY_LINE,
                new TranslationTextComponent("gui.bigreactors.reactor.controller.wasteeject.line3"),
                new TranslationTextComponent("gui.bigreactors.reactor.controller.wasteeject.line4"),
                TEXT_EMPTY_LINE,
                new TranslationTextComponent("gui.bigreactors.reactor.controller.wasteeject.line5"),
                new TranslationTextComponent("gui.bigreactors.reactor.controller.wasteeject.line6"),
                new TranslationTextComponent("gui.bigreactors.reactor.controller.wasteeject.line7")),
                ImmutableList.of(
                        // @0
                        wasteEjectionText
                )
        );

        this.addBinding(r -> r.getWasteEjectionMode().isAutomatic(), swp::setActive, wasteEjectionText);
        commandPanel.addControl(swp);
        y += 29;

        // - scram

        final Button scram = new Button(this, "scram", "SCRAM");

        scram.setLayoutEngineHint(FixedLayoutEngine.hint(x, y, 50, 25));
        scram.setTooltips(new TranslationTextComponent("gui.bigreactors.reactor.controller.scram.line1").setStyle(STYLE_TOOLTIP_TITLE),
                TEXT_EMPTY_LINE,
                new TranslationTextComponent("gui.bigreactors.reactor.controller.scram.line2"),
                new TranslationTextComponent("gui.bigreactors.reactor.controller.scram.line3"),
                new TranslationTextComponent("gui.bigreactors.reactor.controller.scram.line4").setStyle(Style.EMPTY.setItalic(true)));
        scram.Clicked.subscribe(this::onScram);
        commandPanel.addControl(scram);
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

    private GaugeBar heatBar(final String name, final double maxValue) {

        final GaugeBar bar = new GaugeBar(this, name, maxValue, CommonIcons.TemperatureBar.get());

        bar.setDesiredDimension(18, 66);
        bar.setBackground(CommonIcons.BarBackground.get());
        bar.setPadding(1);

        return bar;
    }

    private GaugeBar liquidBar(final String name, final double maxValue) {

        final GaugeBar bar = new GaugeBar(this, name, maxValue, CommonIcons.BarBackground.get());

        bar.setDesiredDimension(18, 66);
        bar.setBackground(CommonIcons.BarBackground.get());
        bar.setPadding(1);

        return bar;
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
    private static ITextComponent getFluidName(final Optional<Fluid> fluid) {
        return fluid.map(f -> (ITextComponent)new TranslationTextComponent(f.getAttributes().getTranslationKey()).setStyle(STYLE_TOOLTIP_VALUE))
                .orElse(TEXT_EMPTY);
    }

    //endregion

    private void onActiveStateChanged(final SwitchButton button) {
        this.sendCommandToServer(button.getActive() ?
                AbstractMultiblockEntity.COMMAND_ACTIVATE :
                AbstractMultiblockEntity.COMMAND_DEACTIVATE);
    }

    private void onWasteEjectionChanged(final SwitchPictureButton button) {
        this.sendCommandToServer(button.getActive() ?
                ReactorControllerEntity.COMMAND_WASTE_AUTOMATIC :
                ReactorControllerEntity.COMMAND_WASTE_MANUAL);
    }

    private void onScram(final Button button, final Integer mouseButton) {
        this.sendCommandToServer(ReactorControllerEntity.COMMAND_SCRAM);
    }

    private final <Value> void addBinding(final Function<MultiblockReactor, Value> supplier, final Consumer<Value> consumer) {
        this._bindings.addBinding(new MonoConsumerBinding<>(this._reactor, supplier, consumer));
    }

    @SafeVarargs
    private final <Value> void addBinding(final Function<MultiblockReactor, Value> supplier, final Consumer<Value>... consumers) {
        this._bindings.addBinding(new MultiConsumerBinding<>(this._reactor, supplier, consumers));
    }

    private static final ITextComponent TEXT_AUTOMATIC_WASTE_EJECT = new TranslationTextComponent("gui.bigreactors.reactor.controller.wasteeject.mode.automatic").setStyle(STYLE_TOOLTIP_VALUE);
    private static final ITextComponent TEXT_MANUAL_WASTE_EJECT = new TranslationTextComponent("gui.bigreactors.reactor.controller.wasteeject.mode.manual").setStyle(STYLE_TOOLTIP_VALUE);
    private static final ITextComponent TEXT_EMPTY = new TranslationTextComponent("gui.bigreactors.generic.empty").setStyle(STYLE_TOOLTIP_VALUE);

    private final MultiblockReactor _reactor;
    private final OperationalMode _reactorMode;
    private final EnergySystem _outputEnergySystem;
    private final double _reactorCapacity;

    private final BindingGroup _bindings;

    private final FuelGaugeBar _fuelBar;
    private final GaugeBar _coreHeatBar;
    private final GaugeBar _casingHeatBar;
    private final GaugeBar _energyBar;
    private final GaugeBar _coolantBar;
    private final GaugeBar _vaporBar;
    private final Label _lblTemperature;
    private final Label _lblFuelUsage;
    private final Label _lblFuelRichness;
    private final Label _lblEnergyRatio;
    private final Label _lblVaporRatio;

    //endregion
}
