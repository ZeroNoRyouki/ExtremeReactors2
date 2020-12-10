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
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.NonNullPredicate;

public class SensorSetting
    implements NonNullPredicate<IReactorReader>, InputSensorAction<IReactorWriter> {

    public static final SensorSetting DISABLED = new SensorSetting();

    public final SensorType Sensor;
    public final SensorBehavior Behavior;
    public final int Value1;
    public final int Value2;

    public SensorSetting(final SensorType sensor, final SensorBehavior behavior, final int v1, final int v2) {

        this.Sensor = sensor;
        this.Behavior = behavior;
        this.Value1 = v1;
        this.Value2 = v2;
    }

    public SensorSetting(final SensorSetting other) {

        this.Sensor = other.Sensor;
        this.Behavior = other.Behavior;
        this.Value1 = other.Value1;
        this.Value2 = other.Value2;
    }

    public static SensorSetting syncDataFrom(final CompoundNBT data) {

        if (data.contains("sensor") && data.contains("behavior") && data.contains("v1") && data.contains("v2")) {

            return new SensorSetting(
                    SensorType.read(data, "sensor", SensorType.Disabled),
                    SensorBehavior.read(data, "behavior", SensorBehavior.Disabled),
                    data.getInt("v1"), data.getInt("v2"));
        }

        Log.LOGGER.error("Invalid NBT data while reading a SensorSetting");
        return DISABLED;
    }

    public CompoundNBT syncDataTo(CompoundNBT data) {

        SensorType.write(data, "sensor", this.Sensor);
        SensorBehavior.write(data, "behavior", this.Behavior);
        data.putInt("v1", this.Value1);
        data.putInt("v2", this.Value2);

        return data;
    }

    //region NonNullPredicate<IReactorReader>

    /**
     * Check if the current (output) setting match the Reactor state,
     *
     * @param reactor the Reactor
     * @return {@code true} if the current sensor is an output sensor that match the Reactor state, otherwise {@code false}
     */
    @Override
    public boolean test(final IReactorReader reactor) {
        return this.Sensor.isOutput() && this.Behavior.outputTest(this.Sensor.apply(reactor), this.Value1);
    }

    //endregion
    //region InputSensorAction

    /**
     * Performs the current (input) sensor action on the machine
     *
     * @param reactor             the machine
     * @param isExternallyPowered true if the Redstone Port is receiving a signal, false otherwise
     * @param externalPowerLevel  the signal level (0 - 15)
     */
    @Override
    public void inputAction(final IReactorWriter reactor, final Boolean isExternallyPowered, final int externalPowerLevel) {

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
    //region Object

    @Override
    public boolean equals(final Object other) {

        if (other instanceof SensorSetting) {

            final SensorSetting s = (SensorSetting)other;

            return (this == s) || (this.Sensor == s.Sensor && this.Behavior == s.Behavior &&
                    this.Value1 == s.Value1 && this.Value2 == s.Value2);
        }

        return false;
    }

    //endregion
    //region internals

    private SensorSetting() {
        this(SensorType.Disabled, SensorBehavior.Disabled, 0 ,0);
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

            case InsertOnPulse:
                reactor.changeControlRodsInsertionRatio(this.Value1);
                break;

            case RetractOnPulse:
                reactor.changeControlRodsInsertionRatio(-this.Value1);
                break;
        }
    }

    private void acceptInputEjectWaste(final IReactorWriter reactor, final Boolean isExternallyPowered) {

        if (SensorBehavior.EjectOnPulse == this.Behavior && isExternallyPowered) {
            reactor.ejectWaste(false);
        }
    }

    //endregion
}
