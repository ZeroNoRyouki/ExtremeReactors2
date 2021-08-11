/*
 *
 * TurbineRedstonePortScreen.java
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

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.AbstractRedstonePortScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.sensor.SensorBehavior;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.ITurbineReader;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.ITurbineWriter;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.MultiblockTurbine;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineRedstonePortEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.sensor.TurbineSensorSetting;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.sensor.TurbineSensorType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.zerocore.lib.client.gui.control.TextInput;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

public class TurbineRedstonePortScreen
        extends AbstractRedstonePortScreen<MultiblockTurbine, TurbineRedstonePortEntity, ModTileContainer<TurbineRedstonePortEntity>,
        ITurbineReader, ITurbineWriter, TurbineSensorType, TurbineSensorSetting> {

    public TurbineRedstonePortScreen(final ModTileContainer<TurbineRedstonePortEntity> container,
                                     final Inventory inventory, final Component title) {
        super(container, inventory, PlayerInventoryUsage.None, title,
                mainTextureFromVariant(container.getTileEntity().getMultiblockVariant().orElse(TurbineVariant.Basic)),
                TurbineSensorSetting::new, TurbineSensorType.values());
    }

    //region AbstractRedstonePortScreen

    @Override
    protected TurbineSensorSetting getDefaultSettings() {
        return this.getTileEntity().getSettings();
    }

    @Override
    protected TurbineSensorSetting getDisabledSettings() {
        return TurbineSensorSetting.DISABLED;
    }

    @Override
    protected void onScreenCreate() {

        //this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, ExtremeReactors.newID("turbine/part-redstoneport"), 1);

        super.onScreenCreate();

        // - sensors sub-behaviors controls

        int sensorButtonRowY = 0;

        // -- inputActive
        this.sensorPanelBuilder(TurbineSensorType.inputActive, 0, sensorButtonRowY, CommonIcons.ButtonSensorInputActivate, CommonIcons.ButtonSensorInputActivateActive).build();

        // -- inputEngageCoils
        this.sensorPanelBuilder(TurbineSensorType.inputEngageCoils, 22, sensorButtonRowY, CommonIcons.ButtonInductor, CommonIcons.ButtonInductorActive).build();

        // -- inputFlowRegulator
        this.sensorPanelBuilder(TurbineSensorType.inputFlowRegulator, 44, sensorButtonRowY, CommonIcons.ButtonSensorInputFlowRegulator, CommonIcons.ButtonSensorInputFlowRegulatorActive)
                .addBehaviorDataInput(SensorBehavior.SetFromSignal,
                        this.flowRegulatorInput("inputFlowRegulatorWhileOn"), "gui.bigreactors.turbine.redstoneport.sensortype.inputflowregulator.whileon",
                        this.flowRegulatorInput("inputFlowRegulatorWhileOff"), "gui.bigreactors.turbine.redstoneport.sensortype.inputflowregulator.whileoff")
                .addBehaviorDataInput(SensorBehavior.SetFromSignalLevel)
                .addBehaviorDataInput(SensorBehavior.SetOnPulse, this.flowRegulatorInput("inputFlowRegulatorSetTo"), "gui.bigreactors.turbine.redstoneport.sensortype.inputflowregulator.setto")
                .addBehaviorDataInput(SensorBehavior.InsertOnPulse, this.flowRegulatorInput("inputFlowRegulatorInsertBy"), "gui.bigreactors.turbine.redstoneport.sensortype.inputflowregulator.insertby")
                .addBehaviorDataInput(SensorBehavior.RetractOnPulse, this.flowRegulatorInput("inputFlowRegulatorRetractBy"), "gui.bigreactors.turbine.redstoneport.sensortype.inputflowregulator.retractby")
                .build();

        sensorButtonRowY += 34;

        // -- separator
        this.addSensorButtonsSeparator(sensorButtonRowY);

        // -- outputRotorSpeed
        this.sensorPanelBuilder(TurbineSensorType.outputRotorSpeed, 0, sensorButtonRowY, CommonIcons.ButtonSensorOutputRotorSpeed, CommonIcons.ButtonSensorOutputRotorSpeedActive)
                .addStandardOutputBehaviorPanel(
                        this.inputTextNumber("outputRotorSpeedAbove", " RPM"), "gui.bigreactors.turbine.redstoneport.sensortype.outputrotorspeed.speed",
                        this.inputTextNumber("outputRotorSpeedBelow", " RPM"), "gui.bigreactors.turbine.redstoneport.sensortype.outputrotorspeed.speed",
                        this.inputTextNumber("outputRotorSpeedBetweenMin", " RPM"), "gui.bigreactors.turbine.redstoneport.sensortype.outputrotorspeed.speed.min",
                        this.inputTextNumber("outputRotorSpeedBetweenMax", " RPM"), "gui.bigreactors.turbine.redstoneport.sensortype.outputrotorspeed.speed.max")
                .build();

        // -- outputCoolantAmount
        this.sensorPanelBuilder(TurbineSensorType.outputCoolantAmount, 22, sensorButtonRowY, CommonIcons.ButtonSensorOutputCoolantAmount, CommonIcons.ButtonSensorOutputCoolantAmountActive)
                .addStandardOutputBehaviorPanel(
                        this.inputTextNumber("outputCoolantAmountAbove", " mB"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount",
                        this.inputTextNumber("outputCoolantAmountBelow", " mB"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount",
                        this.inputTextNumber("outputCoolantAmountBetweenMin", " mB"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount.min",
                        this.inputTextNumber("outputCoolantAmountBetweenMax", " mB"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount.max")
                .build();

        // -- outputVaporAmount
        this.sensorPanelBuilder(TurbineSensorType.outputVaporAmount, 44, sensorButtonRowY, CommonIcons.ButtonSensorOutputVaporAmount, CommonIcons.ButtonSensorOutputVaporAmountActive)
                .addStandardOutputBehaviorPanel(
                        this.inputTextNumber("outputVaporAmountAbove", " mB"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount",
                        this.inputTextNumber("outputVaporAmountBelow", " mB"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount",
                        this.inputTextNumber("outputVaporAmountBetweenMin", " mB"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount.min",
                        this.inputTextNumber("outputVaporAmountBetweenMax", " mB"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.amount.max")
                .build();

        sensorButtonRowY += 22;

        // -- outputEnergyAmount
        this.sensorPanelBuilder(TurbineSensorType.outputEnergyAmount, 0, sensorButtonRowY, CommonIcons.ButtonSensorOutputEnergyAmount, CommonIcons.ButtonSensorOutputEnergyAmountActive)
                .addStandardOutputBehaviorPanel(
                        this.inputTextPercentage("outputEnergyAmountAbove"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.bufferfilling",
                        this.inputTextPercentage("outputEnergyAmountBelow"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.bufferfilling",
                        this.inputTextPercentage("outputEnergyAmountBetweenMin"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.bufferfilling.min",
                        this.inputTextPercentage("outputEnergyAmountBetweenMax"), "gui.bigreactors.generator.redstoneport.sensortype.datalabel.bufferfilling.max")
                .build();

        this.addBinding(TurbineRedstonePortEntity::getSettings, this::applySettings);
    }

    //endregion
    //region internals

    protected TextInput flowRegulatorInput(final String name) {
        return this.inputTextNumber(name, " mB/t", text -> {

            if (text.isEmpty()) {

                return Optional.of("0");

            } else {

                final long originalValue = Long.parseLong(text);
                final long value = Mth.clamp(originalValue, 0, this.getTileEntity().getMultiblockVariant().map(v -> (int)v.getMaxRotorSpeed()).orElse(0));

                if (originalValue != value || '0' == text.charAt(0)) {
                    return Optional.of(Long.toString(value));
                }
            }

            return Optional.empty();
        });
    }

    //endregion
}
