/*
 *
 * SensorSetting.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.sensor;

import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.IReactorReader;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.IReactorWriter;
import it.zerono.mods.zerocore.base.redstone.sensor.AbstractSensorSetting;
import it.zerono.mods.zerocore.base.redstone.sensor.SensorBehavior;
import it.zerono.mods.zerocore.lib.data.nbt.NBTHelper;
import net.minecraft.nbt.CompoundTag;

public class ReactorSensorSetting
    extends AbstractSensorSetting<IReactorReader, IReactorWriter, ReactorSensorType, ReactorSensorSetting> {

    public static final ReactorSensorSetting DISABLED = new ReactorSensorSetting();

    public ReactorSensorSetting(final ReactorSensorType sensor, final SensorBehavior behavior, final int v1, final int v2) {
        super(sensor, behavior, v1, v2);
    }

    public static ReactorSensorSetting syncDataFrom(final CompoundTag data) {

        try {

            return new ReactorSensorSetting(data);

        } catch (IllegalArgumentException ex) {

            Log.LOGGER.error("Invalid NBT data while reading a ReactorSensorSetting");
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
     * @param reactor             the machine
     * @param isExternallyPowered true if the Redstone Port is receiving a signal, false otherwise
     * @param externalPowerLevel  the signal level (0 - 15)
     */
    @Override
    public void inputAction(final IReactorWriter reactor, final boolean isExternallyPowered, final int externalPowerLevel) {

        switch (this.Sensor) {

            case inputActive:
                this.acceptInputActive(reactor, isExternallyPowered);
                break;

            case inputSetControlRod:
                this.acceptInputSetControlRod(reactor, isExternallyPowered, externalPowerLevel);
                break;

            case inputEjectWaste:
                this.acceptInputEjectWaste(reactor, isExternallyPowered);
                break;
        }
    }

    //endregion
    //region AbstractSensorSetting

    @Override
    public ReactorSensorSetting copy() {
        return new ReactorSensorSetting(this.Sensor, this.Behavior, this.Value1, this.Value2);
    }

    //endregion
    //region Object

    @Override
    public boolean equals(final Object other) {

        if (other instanceof ReactorSensorSetting) {

            final ReactorSensorSetting s = (ReactorSensorSetting)other;

            return (this == s) || (this.Sensor == s.Sensor && this.Behavior == s.Behavior &&
                    this.Value1 == s.Value1 && this.Value2 == s.Value2);
        }

        return false;
    }

    //endregion
    //region internals

    private ReactorSensorSetting() {
        this(ReactorSensorType.Disabled, SensorBehavior.Disabled, 0 ,0);
    }

    protected ReactorSensorSetting(final CompoundTag data) throws IllegalArgumentException {
        super(data, ReactorSensorSetting::readSensorTypeFrom);
    }

    private static ReactorSensorType readSensorTypeFrom(final CompoundTag data) {

        if (!data.contains("sensor")) {
            throw new IllegalArgumentException("Invalid NBT data");
        }

        return NBTHelper.nbtGetEnum(data, "sensor", ReactorSensorType::valueOf, ReactorSensorType.Disabled);
    }

    private void acceptInputActive(final IReactorWriter reactor, final Boolean isExternallyPowered) {

        switch (this.Behavior) {

            case SetFromSignal:
                reactor.setMachineActive(isExternallyPowered);
                break;

            case ToggleOnPulse:

                if (isExternallyPowered) {
                    reactor.toggleMachineActive();
                }

                break;
        }
    }

    private void acceptInputSetControlRod(final IReactorWriter reactor, final Boolean isExternallyPowered,
                                          final int externalPowerLevel) {

        switch (this.Behavior) {

            case SetFromSignal:
                reactor.setControlRodsInsertionRatio(isExternallyPowered ? this.Value1 : this.Value2);
                break;

            case SetFromSignalLevel:
                reactor.setControlRodsInsertionRatio(isExternallyPowered ? (int)(externalPowerLevel / 15.0 * 100.0) : 0);
                break;

            case SetOnPulse:
                reactor.setControlRodsInsertionRatio(this.Value1);
                break;

            case AugmentOnPulse:
                reactor.changeControlRodsInsertionRatio(this.Value1);
                break;

            case ReduceOnPulse:
                reactor.changeControlRodsInsertionRatio(-this.Value1);
                break;
        }
    }

    private void acceptInputEjectWaste(final IReactorWriter reactor, final Boolean isExternallyPowered) {

        if (SensorBehavior.PerformOnPulse == this.Behavior && isExternallyPowered) {
            reactor.ejectWaste(false);
        }
    }

    //endregion
}
