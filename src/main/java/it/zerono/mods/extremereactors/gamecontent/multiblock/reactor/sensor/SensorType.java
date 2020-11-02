/*
 *
 * SensorType.java
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

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.IReactorReader;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.IDebuggable;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.fml.LogicalSide;

import java.util.List;

public enum SensorType
        implements IDebuggable, NonNullFunction<IReactorReader, Integer> {

    /**
     * No sensor active
     */
    Disabled(IoDirection.Input, r -> 0, SensorBehavior.Disabled),

    /**
     * Turn the Reactor on or off.
     * Direction: input
     */
    inputActive(IoDirection.Input, r -> 0, SensorBehavior.SetFromSignal, SensorBehavior.ToggleOnPulse),

    // Input: control rod insertion (0-100)
    inputSetControlRod(IoDirection.Input, r -> 0, SensorBehavior.SetFromSignal, SensorBehavior.SetOnPulse,
            SensorBehavior.InsertOnPulse, SensorBehavior.RetractOnPulse),

    // Input: eject waste from the reactor
    inputEjectWaste(IoDirection.Input, r -> 0, SensorBehavior.EjectOnPulse),

    // Output: Temperature of the reactor fuel
    outputFuelTemperature(IoDirection.Output, r -> r.getFuelHeatValue().get().intValue(),
            SensorBehavior.ActiveWhileAbove, SensorBehavior.ActiveWhileBelow),

    // Output: Temperature of the reactor casing
    outputCasingTemperature(IoDirection.Output, r -> r.getReactorHeatValue().get().intValue(),
            SensorBehavior.ActiveWhileAbove, SensorBehavior.ActiveWhileBelow),

    // Output: Fuel richness, % of contents that is fuel (0-100, 100 = 100% fuel)
    outputFuelRichness(IoDirection.Output, r -> (int)r.getUiStats().getFuelRichness() * 100,
            SensorBehavior.ActiveWhileAbove, SensorBehavior.ActiveWhileBelow),

    // Output: Fuel amount in a control rod, raw value, (0-4*height)
    outputFuelAmount(IoDirection.Output, IReactorReader::getFuelAmount,
            SensorBehavior.ActiveWhileAbove, SensorBehavior.ActiveWhileBelow),

    // Output: Waste amount in a control rod, raw value, (0-4*height)
    outputWasteAmount(IoDirection.Output, IReactorReader::getWasteAmount,
            SensorBehavior.ActiveWhileAbove, SensorBehavior.ActiveWhileBelow),

    // Output: Energy in the reactor's buffer, percentile (0-100, 100 = 100% full)
    outputEnergyAmount(IoDirection.Output, r -> (int)r.getEnergyStoredPercentage(),
            SensorBehavior.ActiveWhileAbove, SensorBehavior.ActiveWhileBelow);

//    public static final Predicate<SensorType> INPUT = SensorType::isInput;
//    public static final Predicate<SensorType> OUTPUT = SensorType::isOutput;

    SensorType(final IoDirection direction, final NonNullFunction<IReactorReader, Integer> outputSupplier,
               final SensorBehavior... behaviors) {

        this._direction = direction;
        this._outputTestSupplier = outputSupplier;
        this._validBehaviors = ImmutableList.copyOf(behaviors);
    }

    public List<SensorBehavior> getBehaviors() {
        return this._validBehaviors;
    }

    public boolean isInput() {
        return this._direction.isInput();
    }

    public boolean isOutput() {
        return this._direction.isOutput();
    }

    //TODO check "data" owner; for write too
    public static SensorType read(final CompoundNBT data, final String key, final SensorType defaultValue) {

        if (data.contains(key)) {

            final String value = data.getString(key);

            if (!Strings.isNullOrEmpty(value)) {
                return SensorType.valueOf(value);
            }
        }

        return defaultValue;
    }

    public static void write(final CompoundNBT data, final String key, final SensorType value) {
        data.putString(key, value.name());
    }

    //region IDebuggable

    @Override
    public void getDebugMessages(final LogicalSide side, final IDebugMessages messages) {
        messages.addUnlocalized("Sensor Type: %1$s", this);
    }

    //endregion
    //region NonNullFunction<IReactorReader, Integer>

    @Override
    public Integer apply(final IReactorReader reader) {
        return this._outputTestSupplier.apply(reader);
    }

    //endregion
    //region internals

    private final IoDirection _direction;
    private final List<SensorBehavior> _validBehaviors;
    private final NonNullFunction<IReactorReader, Integer> _outputTestSupplier;

    //endregion
}
