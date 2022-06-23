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

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.AbstractRedstonePortScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.sensor.SensorBehavior;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.IReactorReader;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.IReactorWriter;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorRedstonePortEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.sensor.ReactorSensorSetting;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.sensor.ReactorSensorType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ReactorRedstonePortScreen
        extends AbstractRedstonePortScreen<MultiblockReactor, ReactorRedstonePortEntity, ModTileContainer<ReactorRedstonePortEntity>,
                                            IReactorReader, IReactorWriter, ReactorSensorType, ReactorSensorSetting> {

    public ReactorRedstonePortScreen(final ModTileContainer<ReactorRedstonePortEntity> container,
                                     final Inventory inventory, final Component title) {
        super(container, inventory, PlayerInventoryUsage.None, title,
                mainTextureFromVariant(container.getTileEntity().getMultiblockVariant().orElse(ReactorVariant.Basic)),
                ReactorSensorSetting::new, ReactorSensorType.values());
    }

    //region AbstractRedstonePortScreen

    @Override
    protected ReactorSensorSetting getDefaultSettings() {
        return this.getTileEntity().getSettings();
    }

    @Override
    protected ReactorSensorSetting getDisabledSettings() {
        return ReactorSensorSetting.DISABLED;
    }

    @Override
    protected void onScreenCreate() {

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, ExtremeReactors.newID("reactor/part-redstoneport"), 1);

        super.onScreenCreate();

        // - sensors sub-behaviors controls

        int sensorButtonRowY = 0;

        // -- inputActive
        this.sensorPanelBuilder(ReactorSensorType.inputActive, 0, sensorButtonRowY, CommonIcons.ButtonSensorInputActivate, CommonIcons.ButtonSensorInputActivateActive).build();

        // -- inputEjectWaste
        this.sensorPanelBuilder(ReactorSensorType.inputEjectWaste, 22, sensorButtonRowY, CommonIcons.ButtonDumpWaste, CommonIcons.ButtonDumpWasteActive).build();

        // -- inputSetControlRod
        this.sensorPanelBuilder(ReactorSensorType.inputSetControlRod, 44, sensorButtonRowY, CommonIcons.ButtonSensorInputSetControlRod, CommonIcons.ButtonSensorInputSetControlRodActive)
                .addBehaviorDataInput(SensorBehavior.SetFromSignal,
                        this.inputTextPercentage("inputSetControlRodWhileOn"), "gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.whileon",
                        this.inputTextPercentage("inputSetControlRodWhileOff"), "gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.whileoff")
                .addBehaviorDataInput(SensorBehavior.SetFromSignalLevel)
                .addBehaviorDataInput(SensorBehavior.SetOnPulse, this.inputTextPercentage("inputSetControlRodSetTo"), "gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.setto")
                .addBehaviorDataInput(SensorBehavior.InsertOnPulse, this.inputTextPercentage("inputSetControlRodInsertBy"), "gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.insertby")
                .addBehaviorDataInput(SensorBehavior.RetractOnPulse, this.inputTextPercentage("inputSetControlRodRetractBy"), "gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.retractby")
                .build();

        sensorButtonRowY += 34;

        // -- separator
        this.addSensorButtonsSeparator(sensorButtonRowY);

        // -- outputCasingTemperature
        this.sensorPanelBuilder(ReactorSensorType.outputCasingTemperature, 0, sensorButtonRowY, CommonIcons.ButtonSensorOutputCasingTemperature, CommonIcons.ButtonSensorOutputCasingTemperatureActive)
                .addStandardOutputBehaviorPanel(
                        this.inputTextNumber("outputCasingTemperatureAbove", " C"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.temperature",
                        this.inputTextNumber("outputCasingTemperatureBelow", " C"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.temperature",
                        this.inputTextNumber("outputCasingTemperatureBetweenMin", " C"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.temperature.min",
                        this.inputTextNumber("outputCasingTemperatureBetweenMax", " C"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.temperature.max")
                .build();

        // -- outputFuelTemperature
        this.sensorPanelBuilder(ReactorSensorType.outputFuelTemperature, 22, sensorButtonRowY, CommonIcons.ButtonSensorOutputFuelTemperature, CommonIcons.ButtonSensorOutputFuelTemperatureActive)
                .addStandardOutputBehaviorPanel(
                        this.inputTextNumber("outputFuelTemperatureAbove", " C"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.temperature",
                        this.inputTextNumber("outputFuelTemperatureBelow", " C"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.temperature",
                        this.inputTextNumber("outputFuelTemperatureBetweenMin", " C"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.temperature.min",
                        this.inputTextNumber("outputFuelTemperatureBetweenMax", " C"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.temperature.max")
                .build();

        // -- outputFuelRichness
        this.sensorPanelBuilder(ReactorSensorType.outputFuelRichness, 44, sensorButtonRowY, CommonIcons.ButtonSensorOutputFuelMix, CommonIcons.ButtonSensorOutputFuelMixActive)
                .addStandardOutputBehaviorPanel(
                        this.inputTextPercentage("outputFuelRichnessAbove"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.richness",
                        this.inputTextPercentage("outputFuelRichnessBelow"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.richness",
                        this.inputTextPercentage("outputFuelRichnessBetweenMin"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.richness.min",
                        this.inputTextPercentage("outputFuelRichnessBetweenMax"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.richness.max")
                .build();

        sensorButtonRowY += 22;

        // -- outputFuelAmount
        this.sensorPanelBuilder(ReactorSensorType.outputFuelAmount, 0, sensorButtonRowY, CommonIcons.ButtonSensorOutputFuelAmount, CommonIcons.ButtonSensorOutputFuelAmountActive)
                .addStandardOutputBehaviorPanel(
                        this.inputTextNumber("outputFuelAmountAbove", " mB"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount",
                        this.inputTextNumber("outputFuelAmountBelow", " mB"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount",
                        this.inputTextNumber("outputFuelAmountBetweenMin", " mB"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount.min",
                        this.inputTextNumber("outputFuelAmountBetweenMax", " mB"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount.max")
                .build();

        // -- outputWasteAmount
        this.sensorPanelBuilder(ReactorSensorType.outputWasteAmount, 22, sensorButtonRowY, CommonIcons.ButtonSensorOutputWasteAmount, CommonIcons.ButtonSensorOutputWasteAmountActive)
                .addStandardOutputBehaviorPanel(
                        this.inputTextNumber("outputWasteAmountAbove", " mB"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount",
                        this.inputTextNumber("outputWasteAmountBelow", " mB"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount",
                        this.inputTextNumber("outputWasteAmountBetweenMin", " mB"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount.min",
                        this.inputTextNumber("outputWasteAmountBetweenMax", " mB"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount.max")
                .build();

        // -- outputEnergyAmount
        this.sensorPanelBuilder(ReactorSensorType.outputEnergyAmount, 44, sensorButtonRowY, CommonIcons.ButtonSensorOutputEnergyAmount, CommonIcons.ButtonSensorOutputEnergyAmountActive)
                .addStandardOutputBehaviorPanel(
                        this.inputTextPercentage("outputEnergyAmountAbove"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.bufferfilling",
                        this.inputTextPercentage("outputEnergyAmountBelow"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.bufferfilling",
                        this.inputTextPercentage("outputEnergyAmountBetweenMin"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.bufferfilling.min",
                        this.inputTextPercentage("outputEnergyAmountBetweenMax"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.bufferfilling.max")
                .build();

        if (this.getMultiblockController().orElseThrow(IllegalStateException::new).getOperationalMode().isActive()) {

            sensorButtonRowY += 22;

            // -- outputCoolantAmount
            this.sensorPanelBuilder(ReactorSensorType.outputCoolantAmount, 0, sensorButtonRowY, CommonIcons.ButtonSensorOutputCoolantAmount, CommonIcons.ButtonSensorOutputCoolantAmountActive)
                    .addStandardOutputBehaviorPanel(
                            this.inputTextNumber("outputCoolantAmountAbove", " mB"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount",
                            this.inputTextNumber("outputCoolantAmountBelow", " mB"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount",
                            this.inputTextNumber("outputCoolantAmountBetweenMin", " mB"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount.min",
                            this.inputTextNumber("outputCoolantAmountBetweenMax", " mB"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount.max")
                    .build();

            // -- outputVaporAmount
            this.sensorPanelBuilder(ReactorSensorType.outputVaporAmount, 22, sensorButtonRowY, CommonIcons.ButtonSensorOutputVaporAmount, CommonIcons.ButtonSensorOutputVaporAmountActive)
                    .addStandardOutputBehaviorPanel(
                            this.inputTextNumber("outputVaporAmountAbove", " mB"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount",
                            this.inputTextNumber("outputVaporAmountBelow", " mB"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount",
                            this.inputTextNumber("outputVaporAmountBetweenMin", " mB"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount.min",
                            this.inputTextNumber("outputVaporAmountBetweenMax", " mB"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount.max")
                    .build();
        }

        this.addBinding(ReactorRedstonePortEntity::getSettings, this::applySettings);
    }

    //endregion
}
