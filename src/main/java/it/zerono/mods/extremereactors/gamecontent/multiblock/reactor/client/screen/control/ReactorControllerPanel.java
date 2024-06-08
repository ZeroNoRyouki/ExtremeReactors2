/*
 *
 * ReactorControllerPanel.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.screen.control;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.container.IReactorControllerContainer;
import it.zerono.mods.zerocore.base.client.screen.BaseScreenToolTipsBuilder;
import it.zerono.mods.zerocore.base.client.screen.ClientBaseHelper;
import it.zerono.mods.zerocore.base.client.screen.control.*;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.ButtonState;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.control.AbstractButtonControl;
import it.zerono.mods.zerocore.lib.client.gui.control.Button;
import it.zerono.mods.zerocore.lib.client.gui.control.Panel;
import it.zerono.mods.zerocore.lib.client.gui.control.SwitchPictureButton;
import it.zerono.mods.zerocore.lib.client.gui.layout.HorizontalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.text.TextHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.common.util.NonNullSupplier;
import org.apache.logging.log4j.util.TriConsumer;

import javax.annotation.Nullable;

public class ReactorControllerPanel
        extends Panel {

    public ReactorControllerPanel(ModContainerScreen<? extends ModContainer> gui, int width, int height,
                                  IReactorControllerContainer container,
                                  BooleanConsumer onActiveStateChanged, BooleanConsumer onWasteEjectionChanged,
                                  @Nullable Runnable onVoidReactants, @Nullable Runnable onScram,
                                  TriConsumer<AbstractButtonControl, ButtonState, NonNullSupplier<ISprite>> setButtonSpritesAndOverlayForState) {

        super(gui);

        // bars...

        final HeatBar coreHeatBar = coreHeatBar(gui, container);
        final BarsPanel barsPanel = new BarsPanel(gui, "bars")
                .add(fuelBar(gui, container))
                .addVerticalSeparator()
                .add(coreHeatBar)
                .addTemperatureScale()
                .add(casingHeatBar(gui, container))
                .addVerticalSeparator();

        if (container.getReactorMode().isPassive()) {

            barsPanel.add(energyBar(gui, container))
                    .addEmptyPanel(29);

        } else {

            final NonNullSupplier<Component> fluidCapacityText = container.fluidCapacity().asBindableText(capacity ->
                    TextHelper.literal(CodeHelper.formatAsHumanReadableNumber(capacity / 1000, "B"),
                            ClientBaseHelper::formatAsValue));

            barsPanel.add(coolantBar(gui, container, fluidCapacityText))
                    .addTemperatureScale()
                    .add(vaporBar(gui, container, fluidCapacityText));
        }

        barsPanel.addVerticalSeparator();
        barsPanel.add(commandPanel(gui, container, onActiveStateChanged, onWasteEjectionChanged, onVoidReactants,
                onScram, setButtonSpritesAndOverlayForState));

        // main layout...

        this.setDesiredDimension(width, height);
        this.setLayoutEngine(new VerticalLayoutEngine()
                .setZeroMargins()
                .setControlsSpacing(2)
                .setVerticalAlignment(VerticalAlignment.Top)
                .setHorizontalAlignment(HorizontalAlignment.Left));

        this.addControl(barsPanel);
        this.addControl(CommonPanels.horizontalSeparator(gui, width - 1));
        this.addControl(infoDisplay(gui, container, coreHeatBar));
    }

    //region internals

    private static ReactantBar fuelBar(ModContainerScreen<? extends ModContainer> gui, IReactorControllerContainer container) {
        return new ReactantBar(gui, "fuelBar", container.reactantCapacity(), container.fuelStack(),
                container.wasteStack(), container.fuelRodsCount());
    }

    private static HeatBar coreHeatBar(ModContainerScreen<? extends ModContainer> gui, IReactorControllerContainer container) {
        return new HeatBar(gui, "coreHeatBar", 2000.0, container.coreHeat(),
                CommonIcons.ButtonSensorOutputFuelTemperature,
                "gui.bigreactors.reactor.controller.coreheatbar.tooltip.title",
                "gui.bigreactors.reactor.controller.coreheatbar.tooltip.value",
                "gui.bigreactors.reactor.controller.coreheatbar.tooltip.body");
    }

    private static HeatBar casingHeatBar(ModContainerScreen<? extends ModContainer> gui, IReactorControllerContainer container) {
        return new HeatBar(gui, "casingHeatBar", 2000.0, container.casingHeat(),
                CommonIcons.ButtonSensorOutputCasingTemperature,
                "gui.bigreactors.reactor.controller.casingheatbar.tooltip.title",
                "gui.bigreactors.reactor.controller.casingheatbar.tooltip.value",
                "gui.bigreactors.reactor.controller.casingheatbar.tooltip.body");
    }

    private static EnergyBar energyBar(ModContainerScreen<? extends ModContainer> gui, IReactorControllerContainer container) {

        final EnergyBar bar = new EnergyBar(gui, "energyBar", container.getOutputEnergySystem(),
                WideAmount.ZERO, container.energyStored(), "gui.bigreactors.reactor_turbine.controller.energybar.tooltip.body");

        bar.bindMaxValue(container.energyCapacity());
        return bar;
    }

    private static FluidBar coolantBar(ModContainerScreen<? extends ModContainer> gui, IReactorControllerContainer container,
                                       NonNullSupplier<Component> fluidCapacityText) {

        final FluidBar coolantBar = new FluidBar(gui, "coolantBar", 0, container.coolantStack(),
                CommonIcons.CoolantIcon);

        coolantBar.bindMaxValue(container.fluidCapacity());
        coolantBar.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.reactor.controller.coolantbar.tooltip.title")
                .addTranslatableAsValue("gui.bigreactors.reactor.controller.coolantbar.tooltip.value1")
                .addTextAsValue(TextHelper.translatable("gui.bigreactors.reactor.controller.coolantbar.tooltip.value2a"),
                        TextHelper.translatable("gui.bigreactors.reactor.controller.coolantbar.tooltip.value2b"))
                .addTextAsValue(TextHelper.translatable("gui.bigreactors.reactor.controller.coolantbar.tooltip.value3a"),
                        TextHelper.translatable("gui.bigreactors.reactor.controller.coolantbar.tooltip.value3b"))
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.reactor.controller.coolantbar.tooltip.body")
                .addBindableObjectAsValue(container.coolantStack(), coolantBar::formatFluidText)
                .addBindableObjectAsValue(container.coolantStack().amount(), coolantBar::formatAmountText)
                .addObject(fluidCapacityText)
                .addBindableObjectAsValue(container.coolantStack().amount(), coolantBar::formatPercentageText)
        );

        return coolantBar;
    }

    private static FluidBar vaporBar(ModContainerScreen<? extends ModContainer> gui, IReactorControllerContainer container,
                                     NonNullSupplier<Component> fluidCapacityText) {

        final FluidBar vaporBar = new FluidBar(gui, "vaporBar", 0, container.vaporStack(),
                CommonIcons.VaporIcon);

        vaporBar.bindMaxValue(container.fluidCapacity());
        vaporBar.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.reactor.controller.vaporbar.tooltip.title")
                .addTranslatableAsValue("gui.bigreactors.reactor.controller.vaporbar.tooltip.value1")
                .addTextAsValue(TextHelper.translatable("gui.bigreactors.reactor.controller.vaporbar.tooltip.value2a"),
                        TextHelper.translatable("gui.bigreactors.reactor.controller.vaporbar.tooltip.value2b"))
                .addTextAsValue(TextHelper.translatable("gui.bigreactors.reactor.controller.vaporbar.tooltip.value3a"),
                        TextHelper.translatable("gui.bigreactors.reactor.controller.vaporbar.tooltip.value3b"))
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.reactor.controller.vaporbar.tooltip.body")
                .addBindableObjectAsValue(container.vaporStack(), vaporBar::formatFluidText)
                .addBindableObjectAsValue(container.vaporStack().amount(), vaporBar::formatAmountText)
                .addObject(fluidCapacityText)
                .addBindableObjectAsValue(container.vaporStack().amount(), vaporBar::formatPercentageText)
        );

        return vaporBar;
    }

    private static Panel commandPanel(ModContainerScreen<? extends ModContainer> gui, IReactorControllerContainer container,
                                      BooleanConsumer onActiveStateChanged, BooleanConsumer onWasteEjectionChanged,
                                      @Nullable Runnable onVoidReactants, @Nullable Runnable onScram,
                                      TriConsumer<AbstractButtonControl, ButtonState, NonNullSupplier<ISprite>> setButtonSpritesAndOverlayForState) {

        final Panel commandPanel = CommonPanels.verticalCommandPanel(gui, 50);
        Button button;

        commandPanel.setLayoutEngine(new VerticalLayoutEngine()
                .setZeroMargins()
                .setControlsSpacing(3)
                .setVerticalAlignment(VerticalAlignment.Top)
                .setHorizontalAlignment(HorizontalAlignment.Center));

        // - machine on/off

        final OnOff onOff = new OnOff(gui, container.active(), btn -> onActiveStateChanged.accept(btn.getActive()),
                TextHelper.translatable("gui.bigreactors.reactor.controller.on.title"),
                TextHelper.translatable("gui.bigreactors.reactor.controller.off.title"));

        commandPanel.addControl(onOff);

        // - void reactants

        if (null != onVoidReactants) {

            button = new Button(gui, "voidreactants", "");
            button.Clicked.subscribe((btn, $) -> onVoidReactants.run());
            button.setIconForState(CommonIcons.TrashCan.get(), ButtonState.Default);
            button.enablePaintBlending(true);
            button.setPadding(1);
            button.setDesiredDimension(ClientBaseHelper.SQUARE_BUTTON_DIMENSION, ClientBaseHelper.SQUARE_BUTTON_DIMENSION);
            button.setTooltips(new BaseScreenToolTipsBuilder()
                    .addTranslatableAsTitle("gui.bigreactors.reactor.controller.voidreactants.tooltip.title")
                    .addEmptyLine()
                    .addTranslatable("gui.bigreactors.reactor.controller.voidreactants.tooltip.body")
            );
            commandPanel.addControl(button);
        }

        // - waste ejection settings

        final SwitchPictureButton wasteEjectionSettings = new SwitchPictureButton(gui, "wasteeject", false);

        wasteEjectionSettings.Activated.subscribe(btn -> onWasteEjectionChanged.accept(btn.getActive()));
        wasteEjectionSettings.Deactivated.subscribe(btn -> onWasteEjectionChanged.accept(btn.getActive()));
        setButtonSpritesAndOverlayForState.accept(wasteEjectionSettings, ButtonState.Default, CommonIcons.ButtonDumpWaste);
        setButtonSpritesAndOverlayForState.accept(wasteEjectionSettings, ButtonState.Active, CommonIcons.ButtonDumpWasteActive);
        wasteEjectionSettings.setBackground(CommonIcons.ImageButtonBackground.get());
        wasteEjectionSettings.enablePaintBlending(true);
        wasteEjectionSettings.setPadding(1);
        wasteEjectionSettings.setDesiredDimension(ClientBaseHelper.SQUARE_BUTTON_DIMENSION, ClientBaseHelper.SQUARE_BUTTON_DIMENSION);
        wasteEjectionSettings.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.reactor.controller.wasteeject.tooltip.title")
                .addTranslatableAsValue("gui.bigreactors.reactor.controller.wasteeject.tooltip.value")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.reactor.controller.wasteeject.tooltip.body")
                .addBindableObjectAsValue(container.wasteEjectionSetting(), settings ->
                        settings.isAutomatic() ? TEXT_AUTOMATIC_WASTE_EJECT : TEXT_MANUAL_WASTE_EJECT)
        );
        container.wasteEjectionSetting().bind(settings -> wasteEjectionSettings.setActive(settings.isAutomatic()));
        commandPanel.addControl(wasteEjectionSettings);

        // - scram

        if (null != onScram) {

            button = new Button(gui, "scram", "SCRAM");
            button.Clicked.subscribe((btn, $) -> onScram.run());
            button.setTooltips(new BaseScreenToolTipsBuilder()
                    .addTranslatableAsTitle("gui.bigreactors.reactor.controller.scram.tooltip.title")
                    .addEmptyLine()
                    .addTranslatable("gui.bigreactors.reactor.controller.scram.tooltip.body")
            );
            button.setDesiredDimension(50, 25);
            commandPanel.addControl(button);
        }

        return commandPanel;
    }

    private static InformationDisplay infoDisplay(ModContainerScreen<? extends ModContainer> gui,
                                                  IReactorControllerContainer container, HeatBar coreHeatBar) {

        final InformationDisplay infoDisplay = new InformationDisplay(gui, "info", layout -> layout
                .columns(2, 88, 107)
                .rows(3));

        infoDisplay.addInformationCell(builder -> builder
                .name("coreHeat")
                .bindText(container.coreHeat(), heat -> String.format("%.0f C", heat))
                .icon(CommonIcons.TemperatureIcon)
                .useTooltipsFrom(coreHeatBar));

        if (container.getReactorMode().isPassive()) {

            infoDisplay.addInformationCell(builder -> builder
                    .name("energyRatio")
                    .bindText(container.generatedLastTick(), generated ->
                            CodeHelper.formatAsHumanReadableNumber(generated, container.getOutputEnergySystem().getUnit() + "/t"))
                    .icon(CommonIcons.EnergyRatioIcon)
                    .tooltips(new BaseScreenToolTipsBuilder()
                            .addTranslatableAsTitle("gui.bigreactors.reactor.controller.energyratio.tooltip.title")
                            .addTranslatableAsValue("gui.bigreactors.reactor.controller.energyratio.tooltip.value")
                            .addEmptyLine()
                            .addTranslatable("gui.bigreactors.reactor.controller.energyratio.tooltip.body")
                            .addBindableObjectAsValue(container.generatedLastTick(), generated ->
                                    TextHelper.literal(CodeHelper.formatAsHumanReadableNumber(generated, container.getOutputEnergySystem().getUnit() + "/t"))))
            );

        } else {

            infoDisplay.addInformationCell(builder -> builder
                    .name("vaporRatio")
                    .bindText(container.generatedLastTick(), generated -> CodeHelper.formatAsHumanReadableNumber(generated / 1000.0, "B/t"))
                    .icon(CommonIcons.VaporIcon)
                    .tooltips(new BaseScreenToolTipsBuilder()
                            .addTranslatableAsTitle("gui.bigreactors.reactor.controller.vaporratio.tooltip.title")
                            .addTranslatableAsValue("gui.bigreactors.reactor.controller.vaporratio.tooltip.value")
                            .addEmptyLine()
                            .addTranslatable("gui.bigreactors.reactor.controller.vaporratio.tooltip.body")
                            .addBindableObjectAsValue(container.generatedLastTick(), generated ->
                                    TextHelper.literal(CodeHelper.formatAsHumanReadableNumber(generated / 1000.0, "B/t"))))
            );
        }

        infoDisplay.addInformationCell(builder -> builder
                .name("fuelusage")
                .bindText(container.fuelConsumedLastTick(), consumed -> CodeHelper.formatAsMillibuckets(consumed) + "/t")
                .icon(CommonIcons.FuelMix)
                .tooltips(new BaseScreenToolTipsBuilder()
                        .addTranslatableAsTitle("gui.bigreactors.reactor.controller.fuelusage.tooltip.title")
                        .addEmptyLine()
                        .addTranslatable("gui.bigreactors.reactor.controller.fuelusage.tooltip.body"))
        );

        infoDisplay.addEmptyCell();

        infoDisplay.addInformationCell(builder -> builder
                .name("fuelRichness")
                .bindText(container.fuelRichness(), richness -> String.format("%2.0f%%", richness * 100f))
                .icon(CommonIcons.ReactivityIcon)
                .tooltips(new BaseScreenToolTipsBuilder()
                        .addTranslatableAsTitle("gui.bigreactors.reactor.controller.fuelrichness.tooltip.title")
                        .addEmptyLine()
                        .addTranslatable("gui.bigreactors.reactor.controller.fuelrichness.tooltip.body"))
        );

        return infoDisplay;
    }

    private static final MutableComponent TEXT_AUTOMATIC_WASTE_EJECT = TextHelper.translatable("gui.bigreactors.reactor.controller.wasteeject.mode.automatic", ClientBaseHelper::formatAsValue);
    private static final MutableComponent TEXT_MANUAL_WASTE_EJECT = TextHelper.translatable("gui.bigreactors.reactor.controller.wasteeject.mode.manual", ClientBaseHelper::formatAsValue);

    //endregion
}
