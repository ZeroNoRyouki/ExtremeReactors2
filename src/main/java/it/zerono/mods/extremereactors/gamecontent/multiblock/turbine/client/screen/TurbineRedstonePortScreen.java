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

import it.zerono.mods.extremereactors.CommonLocations;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.AbstractRedstonePortScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.ITurbineReader;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.ITurbineWriter;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.MultiblockTurbine;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.container.TurbineRedstonePortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineRedstonePortEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.sensor.TurbineSensorSetting;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.sensor.TurbineSensorType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.IMultiblockTurbineVariant;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.zerocore.base.client.screen.control.MachineStatusIndicator;
import it.zerono.mods.zerocore.base.client.screen.control.redstone.sensor.ISensorBuilder;
import it.zerono.mods.zerocore.base.redstone.sensor.SensorBehavior;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.control.TextInput;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

public class TurbineRedstonePortScreen
        extends AbstractRedstonePortScreen<MultiblockTurbine, IMultiblockTurbineVariant, TurbineRedstonePortEntity,
                                            TurbineRedstonePortContainer, ITurbineReader, ITurbineWriter,
                                            TurbineSensorType, TurbineSensorSetting> {

    public TurbineRedstonePortScreen(final TurbineRedstonePortContainer container,
                                     final Inventory inventory, final Component title) {

        super(container, inventory, PlayerInventoryUsage.None, title,
                mainTextureFromVariant(container.getTileEntity().getMultiblockVariant().orElse(TurbineVariant.Basic)),
                TurbineSensorSetting::new, TurbineSensorType.values());

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, CommonLocations.TURBINE.buildWithSuffix("part-redstoneport"), 1);
    }

    //region AbstractRedstonePortScreen

    @Override
    protected TurbineSensorSetting getDefaultSettings() {
        return this.getTileEntity().getSensorSetting();
    }

    @Override
    protected TurbineSensorSetting getDisabledSettings() {
        return TurbineSensorSetting.DISABLED;
    }

    @Override
    protected void buildSettings(ISensorBuilder<ITurbineReader, TurbineSensorType> builder) {

        builder.addSensor(TurbineSensorType.inputActive, CommonIcons.ButtonSensorInputActivate, CommonIcons.ButtonSensorInputActivateActive)
                    .addInputLessBehavior(SensorBehavior.SetFromSignal)
                    .addInputLessBehavior(SensorBehavior.ToggleOnPulse)
                    .build()
                .addSensor(TurbineSensorType.inputEngageCoils, CommonIcons.ButtonInductor, CommonIcons.ButtonInductorActive)
                    .addInputLessBehavior(SensorBehavior.SetFromSignal)
                    .addInputLessBehavior(SensorBehavior.ToggleOnPulse)
                    .build()
                .addSensor(TurbineSensorType.inputFlowRegulator, CommonIcons.ButtonSensorInputFlowRegulator, CommonIcons.ButtonSensorInputFlowRegulatorActive)
                    .addBehavior(SensorBehavior.SetFromSignal)
                        .addNumberField("gui.bigreactors.turbine.redstoneport.sensortype.inputflowregulator.whileon.label",
                        "inputFlowRegulatorWhileOn", " mB/t", this::configureFlowRegulatorInput)
                        .addNumberField("gui.bigreactors.turbine.redstoneport.sensortype.inputflowregulator.whileoff.label",
                        "inputFlowRegulatorWhileOff", " mB/t", this::configureFlowRegulatorInput)
                        .build()
                    .addInputLessBehavior(SensorBehavior.SetFromSignalLevel)
                    .addBehavior(SensorBehavior.SetOnPulse)
                        .addNumberField("gui.bigreactors.turbine.redstoneport.sensortype.inputflowregulator.setto.label",
                        "inputFlowRegulatorSetTo", " mB/t", this::configureFlowRegulatorInput)
                        .build()
                    .addBehavior(SensorBehavior.AugmentOnPulse)
                        .addNumberField("gui.bigreactors.turbine.redstoneport.sensortype.inputflowregulator.insertby.label",
                                "inputFlowRegulatorInsertBy", " mB/t", this::configureFlowRegulatorInput)
                        .build()
                    .addBehavior(SensorBehavior.ReduceOnPulse)
                        .addNumberField("gui.bigreactors.turbine.redstoneport.sensortype.inputflowregulator.retractby.label",
                                "inputFlowRegulatorRetractBy", " mB/t", this::configureFlowRegulatorInput)
                        .build()
                .build()
                .addSeparator()
                .addSensor(TurbineSensorType.outputRotorSpeed, CommonIcons.ButtonSensorOutputRotorSpeed, CommonIcons.ButtonSensorOutputRotorSpeedActive)
                    .addStandardOutputBehaviorsNumbers(" RPM", "gui.bigreactors.turbine.redstoneport.sensortype.outputrotorspeed.speed.label",
                            "gui.bigreactors.turbine.redstoneport.sensortype.outputrotorspeed.speed.label",
                            "gui.bigreactors.turbine.redstoneport.sensortype.outputrotorspeed.speed.min.label",
                            "gui.bigreactors.turbine.redstoneport.sensortype.outputrotorspeed.speed.max.label")
                .addSensor(TurbineSensorType.outputCoolantAmount, CommonIcons.ButtonSensorOutputCoolantAmount, CommonIcons.ButtonSensorOutputCoolantAmountActive)
                    .addStandardOutputBehaviorsNumbers(" mB", "gui.bigreactors.generator.redstoneport.sensortype.amount.label",
                            "gui.bigreactors.generator.redstoneport.sensortype.amount.label",
                            "gui.bigreactors.generator.redstoneport.sensortype.amount.min.label",
                            "gui.bigreactors.generator.redstoneport.sensortype.amount.max.label")
                .addSensor(TurbineSensorType.outputVaporAmount, CommonIcons.ButtonSensorOutputVaporAmount, CommonIcons.ButtonSensorOutputVaporAmountActive)
                    .addStandardOutputBehaviorsNumbers(" mB", "gui.bigreactors.generator.redstoneport.sensortype.amount.label",
                            "gui.bigreactors.generator.redstoneport.sensortype.amount.label",
                            "gui.bigreactors.generator.redstoneport.sensortype.amount.min.label",
                            "gui.bigreactors.generator.redstoneport.sensortype.amount.max.label")
                .addSensor(TurbineSensorType.outputEnergyAmount, CommonIcons.ButtonSensorOutputEnergyAmount, CommonIcons.ButtonSensorOutputEnergyAmountActive)
                    .addStandardOutputBehaviorsPercentages("gui.bigreactors.generator.redstoneport.sensortype.bufferfilling.label",
                            "gui.bigreactors.generator.redstoneport.sensortype.bufferfilling.label",
                            "gui.bigreactors.generator.redstoneport.sensortype.bufferfilling.min.label",
                            "gui.bigreactors.generator.redstoneport.sensortype.bufferfilling.max.label")
                .build();
    }

    @Override
    protected MachineStatusIndicator createStatusIndicator(TurbineRedstonePortContainer container) {
        return this.createTurbineStatusIndicator(container.ACTIVE);
    }

    //endregion
    //region internals

    private void configureFlowRegulatorInput(SensorBehavior behavior, TextInput input) {

        input.addConstraint(text -> {

            if (text.isEmpty()) {

                return Optional.of("0");

            } else {

                final long originalValue = Long.parseLong(text);
                final long value = CodeHelper.mathClamp(originalValue, 0, this.getTileEntity().getMultiblockVariant().map(v -> (int)v.getMaxRotorSpeed()).orElse(0));

                if (originalValue != value || '0' == text.charAt(0)) {
                    return Optional.of(Long.toString(value));
                }
            }

            return Optional.empty();
        });
    }

    //endregion
}
