/*
 *
 * TurbineSensorSetting.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.sensor;

import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.sensor.AbstractSensorSetting;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.sensor.SensorBehavior;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.ITurbineReader;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.ITurbineWriter;
import it.zerono.mods.zerocore.lib.data.nbt.NBTHelper;
import net.minecraft.nbt.CompoundTag;

public class TurbineSensorSetting
        extends AbstractSensorSetting<ITurbineReader, ITurbineWriter, TurbineSensorType> {

    public static final TurbineSensorSetting DISABLED = new TurbineSensorSetting();

    public TurbineSensorSetting(final TurbineSensorType sensor, final SensorBehavior behavior, final int v1, final int v2) {
        super(sensor, behavior, v1, v2);
    }

    public static TurbineSensorSetting syncDataFrom(final CompoundTag data) {

        try {

            return new TurbineSensorSetting(data);

        } catch (IllegalArgumentException ex) {

            Log.LOGGER.error("Invalid NBT data while reading a TurbineSensorSetting");
            return DISABLED;
        }
    }

    public CompoundTag syncDataTo(CompoundTag data) {
        return super.syncDataTo(NBTHelper.nbtSetEnum(data, "sensor", this.Sensor));
    }

    //region InputSensorAction

    /**
     * Performs the current (input) sensor action on the machine
     *
     * @param turbine             the machine
     * @param isExternallyPowered true if the Redstone Port is receiving a signal, false otherwise
     * @param externalPowerLevel  the signal level (0 - 15)
     */
    @Override
    public void inputAction(final ITurbineWriter turbine, final Boolean isExternallyPowered, final int externalPowerLevel) {

        switch (this.Sensor) {

            case inputActive:
                this.acceptInputActive(turbine, isExternallyPowered);
                break;

            case inputEngageCoils:
                this.acceptInputEngageCoils(turbine, isExternallyPowered);
                break;

            case inputFlowRegulator:
                this.acceptInputFlowRegulator(turbine, isExternallyPowered, externalPowerLevel);
                break;
        }
    }

    //endregion
    //region Object

    @Override
    public boolean equals(final Object other) {

        if (other instanceof TurbineSensorSetting) {

            final TurbineSensorSetting s = (TurbineSensorSetting)other;

            return (this == s) || (this.Sensor == s.Sensor && this.Behavior == s.Behavior &&
                    this.Value1 == s.Value1 && this.Value2 == s.Value2);
        }

        return false;
    }

    //endregion
    //region internals

    private TurbineSensorSetting() {
        this(TurbineSensorType.Disabled, SensorBehavior.Disabled, 0 ,0);
    }

    protected TurbineSensorSetting(final CompoundTag data) throws IllegalArgumentException {
        super(data, TurbineSensorSetting::readSensorTypeFrom);
    }

    private static TurbineSensorType readSensorTypeFrom(final CompoundTag data) {

        if (!data.contains("sensor")) {
            throw new IllegalArgumentException("Invalid NBT data");
        }

        return NBTHelper.nbtGetEnum(data, "sensor", TurbineSensorType::valueOf, TurbineSensorType.Disabled);
    }

    private void acceptInputActive(final ITurbineWriter turbine, final Boolean isExternallyPowered) {

        switch (this.Behavior) {

            case SetFromSignal:
                turbine.setMachineActive(isExternallyPowered);
                break;

            case ToggleOnPulse:

                if (isExternallyPowered) {
                    turbine.toggleMachineActive();
                }

                break;
        }
    }

    private void acceptInputEngageCoils(final ITurbineWriter turbine, final Boolean isExternallyPowered) {

        switch (this.Behavior) {

            case SetFromSignal:
                turbine.setInductorEngaged(isExternallyPowered);
                break;

            case ToggleOnPulse:

                if (isExternallyPowered) {
                    turbine.setInductorEngaged(!turbine.isInductorEngaged());
                }

                break;
        }
    }

    private void acceptInputFlowRegulator(final ITurbineWriter turbine, final Boolean isExternallyPowered,
                                          final int externalPowerLevel) {

        switch (this.Behavior) {

            case SetFromSignal:
                turbine.setMaxIntakeRate(isExternallyPowered ? this.Value1 : this.Value2);
                break;

            case SetFromSignalLevel:
                turbine.setMaxIntakeRatePercentage(isExternallyPowered ? (int)(externalPowerLevel / 15.0 * 100.0) : 0);
                break;

            case SetOnPulse:
                turbine.setMaxIntakeRate(this.Value1);
                break;

            case InsertOnPulse:
                turbine.changeMaxIntakeRate(this.Value1);
                break;

            case RetractOnPulse:
                turbine.changeMaxIntakeRate(-this.Value1);
                break;
        }
    }

    //endregion
}
