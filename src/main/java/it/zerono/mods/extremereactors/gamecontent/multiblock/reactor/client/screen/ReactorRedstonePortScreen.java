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
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.IReactorReader;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.IReactorWriter;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.container.ReactorRedstonePortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorRedstonePortEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.sensor.ReactorSensorSetting;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.sensor.ReactorSensorType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.IMultiblockReactorVariant;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
import it.zerono.mods.zerocore.base.client.screen.control.MachineStatusIndicator;
import it.zerono.mods.zerocore.base.client.screen.control.redstone.sensor.ISensorBuilder;
import it.zerono.mods.zerocore.base.redstone.sensor.SensorBehavior;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ReactorRedstonePortScreen
        extends AbstractRedstonePortScreen<MultiblockReactor, IMultiblockReactorVariant, ReactorRedstonePortEntity,
                                            ReactorRedstonePortContainer, IReactorReader, IReactorWriter,
                                            ReactorSensorType, ReactorSensorSetting> {

    public ReactorRedstonePortScreen(final ReactorRedstonePortContainer container,
                                     final PlayerInventory inventory, final ITextComponent title) {

        super(container, inventory, PlayerInventoryUsage.None, title,
                mainTextureFromVariant(container.getTileEntity().getMultiblockVariant().orElse(ReactorVariant.Basic)),
                ReactorSensorSetting::new, ReactorSensorType.values());

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, ExtremeReactors.newID("reactor/part-redstoneport"), 1);
    }

    //region AbstractRedstonePortScreen

    @Override
    protected ReactorSensorSetting getDefaultSettings() {
        return this.getTileEntity().getSensorSetting();
    }

    @Override
    protected ReactorSensorSetting getDisabledSettings() {
        return ReactorSensorSetting.DISABLED;
    }

    @Override
    protected void buildSettings(ISensorBuilder<IReactorReader, ReactorSensorType> builder) {

        builder.addSensor(ReactorSensorType.inputActive, CommonIcons.ButtonSensorInputActivate, CommonIcons.ButtonSensorInputActivateActive)
                    .addInputLessBehavior(SensorBehavior.SetFromSignal)
                    .addInputLessBehavior(SensorBehavior.ToggleOnPulse)
                    .build()
                .addSensor(ReactorSensorType.inputEjectWaste, CommonIcons.ButtonDumpWaste, CommonIcons.ButtonDumpWasteActive)
                    .addInputLessBehavior(SensorBehavior.PerformOnPulse)
                    .build()
                .addSensor(ReactorSensorType.inputSetControlRod, CommonIcons.ButtonSensorInputSetControlRod, CommonIcons.ButtonSensorInputSetControlRodActive)
                    .addBehavior(SensorBehavior.SetFromSignal)
                        .addPercentageField("gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.whileon.label", "inputSetControlRodWhileOn")
                        .addPercentageField("gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.whileoff.label", "inputSetControlRodWhileOff")
                        .build()
                    .addInputLessBehavior(SensorBehavior.SetFromSignalLevel)
                    .addBehavior(SensorBehavior.SetOnPulse)
                        .addPercentageField("gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.setto.label", "inputSetControlRodSetTo")
                        .build()
                    .addBehavior(SensorBehavior.AugmentOnPulse)
                        .addPercentageField("gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.augment.label", "inputSetControlRodInsertBy")
                        .build()
                    .addBehavior(SensorBehavior.ReduceOnPulse)
                        .addPercentageField("gui.bigreactors.reactor.redstoneport.sensortype.inputsetcontrolrod.reduce.label", "inputSetControlRodRetractBy")
                        .build()
                    .build()
                .addSeparator()
                .addSensor(ReactorSensorType.outputCasingTemperature, CommonIcons.ButtonSensorOutputCasingTemperature, CommonIcons.ButtonSensorOutputCasingTemperatureActive)
                    .addStandardOutputBehaviorsNumbers(" C", "gui.bigreactors.reactor.redstoneport.sensortype.temperature.label",
                            "gui.bigreactors.reactor.redstoneport.sensortype.temperature.label",
                            "gui.bigreactors.reactor.redstoneport.sensortype.temperature.min.label",
                            "gui.bigreactors.reactor.redstoneport.sensortype.temperature.max.label")
                .addSensor(ReactorSensorType.outputFuelTemperature, CommonIcons.ButtonSensorOutputFuelTemperature, CommonIcons.ButtonSensorOutputFuelTemperatureActive)
                    .addStandardOutputBehaviorsNumbers(" C", "gui.bigreactors.reactor.redstoneport.sensortype.temperature.label",
                            "gui.bigreactors.reactor.redstoneport.sensortype.temperature.label",
                            "gui.bigreactors.reactor.redstoneport.sensortype.temperature.min.label",
                            "gui.bigreactors.reactor.redstoneport.sensortype.temperature.max.label")
                .addSensor(ReactorSensorType.outputFuelRichness, CommonIcons.ButtonSensorOutputFuelMix, CommonIcons.ButtonSensorOutputFuelMixActive)
                    .addStandardOutputBehaviorsPercentages("gui.bigreactors.reactor.redstoneport.sensortype.richness.label",
                            "gui.bigreactors.reactor.redstoneport.sensortype.richness.label",
                            "gui.bigreactors.reactor.redstoneport.sensortype.richness.min.label",
                            "gui.bigreactors.reactor.redstoneport.sensortype.richness.max.label")
                .addSensor(ReactorSensorType.outputFuelAmount, CommonIcons.ButtonSensorOutputFuelAmount, CommonIcons.ButtonSensorOutputFuelAmountActive)
                    .addStandardOutputBehaviorsNumbers(" mB", "gui.bigreactors.generator.redstoneport.sensortype.amount.label",
                            "gui.bigreactors.generator.redstoneport.sensortype.amount.label",
                            "gui.bigreactors.generator.redstoneport.sensortype.amount.min.label",
                            "gui.bigreactors.generator.redstoneport.sensortype.amount.max.label")
                .addSensor(ReactorSensorType.outputWasteAmount, CommonIcons.ButtonSensorOutputWasteAmount, CommonIcons.ButtonSensorOutputWasteAmountActive)
                    .addStandardOutputBehaviorsNumbers(" mB", "gui.bigreactors.generator.redstoneport.sensortype.amount.label",
                        "gui.bigreactors.generator.redstoneport.sensortype.amount.label",
                        "gui.bigreactors.generator.redstoneport.sensortype.amount.min.label",
                        "gui.bigreactors.generator.redstoneport.sensortype.amount.max.label");

        if (this.getMenu().isReactorInPassiveMode()) {

            builder.addSensor(ReactorSensorType.outputEnergyAmount, CommonIcons.ButtonSensorOutputEnergyAmount, CommonIcons.ButtonSensorOutputEnergyAmountActive)
                    .addStandardOutputBehaviorsPercentages("gui.bigreactors.generator.redstoneport.sensortype.bufferfilling.label",
                            "gui.bigreactors.generator.redstoneport.sensortype.bufferfilling.label",
                            "gui.bigreactors.generator.redstoneport.sensortype.bufferfilling.min.label",
                            "gui.bigreactors.generator.redstoneport.sensortype.bufferfilling.max.label");

        } else {

            builder.addSensor(ReactorSensorType.outputCoolantAmount, CommonIcons.ButtonSensorOutputCoolantAmount, CommonIcons.ButtonSensorOutputCoolantAmountActive)
                        .addStandardOutputBehaviorsNumbers(" mB", "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount",
                            "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount",
                            "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount.min",
                            "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount.max")
                    .addSensor(ReactorSensorType.outputVaporAmount, CommonIcons.ButtonSensorOutputVaporAmount, CommonIcons.ButtonSensorOutputVaporAmountActive)
                        .addStandardOutputBehaviorsNumbers(" mB", "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount",
                            "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount",
                            "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount.min",
                            "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount.max");
        }

        builder.build();
    }

    @Override
    protected MachineStatusIndicator createStatusIndicator(ReactorRedstonePortContainer container) {
        return this.createReactorStatusIndicator(container.ACTIVE);
    }

    //endregion
}
