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

import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.IReactorReader;
import it.zerono.mods.zerocore.base.redstone.sensor.ISensorType;
import it.zerono.mods.zerocore.base.redstone.sensor.SensorBehavior;
import it.zerono.mods.zerocore.base.redstone.sensor.SensorTypeData;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.IDebuggable;
import net.neoforged.fml.LogicalSide;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.ToIntFunction;

public enum ReactorSensorType
        implements ISensorType<IReactorReader>, IDebuggable {

    /**
     * No sensor active
     */
    Disabled(SensorBehavior.Disabled),

    /**
     * Turn the Reactor on or off.
     * Direction: input
     */
    inputActive(SensorBehavior.SetFromSignal, SensorBehavior.ToggleOnPulse),

    // Input: control rod insertion (0-100)
    inputSetControlRod(SensorBehavior.SetFromSignal, SensorBehavior.SetFromSignalLevel, SensorBehavior.SetOnPulse, SensorBehavior.AugmentOnPulse, SensorBehavior.ReduceOnPulse),

    // Input: eject waste from the reactor
    inputEjectWaste(SensorBehavior.PerformOnPulse),

    // Output: Temperature of the reactor fuel
    outputFuelTemperature(r -> (int)r.getFuelHeatValue().getAsDouble(), SensorBehavior.ActiveWhileAbove, SensorBehavior.ActiveWhileBelow, SensorBehavior.ActiveWhileBetween),

    // Output: Temperature of the reactor casing
    outputCasingTemperature(r -> (int)r.getReactorHeatValue().getAsDouble(), SensorBehavior.ActiveWhileAbove, SensorBehavior.ActiveWhileBelow, SensorBehavior.ActiveWhileBetween),

    // Output: Fuel richness, % of contents that is fuel (0-100, 100 = 100% fuel)
    outputFuelRichness(r -> (int)(r.getUiStats().getFuelRichness() * 100), SensorBehavior.ActiveWhileAbove, SensorBehavior.ActiveWhileBelow, SensorBehavior.ActiveWhileBetween),

    // Output: Fuel amount in a control rod, raw value, (0-4*height)
    outputFuelAmount(IReactorReader::getFuelAmount, SensorBehavior.ActiveWhileAbove, SensorBehavior.ActiveWhileBelow, SensorBehavior.ActiveWhileBetween),

    // Output: Waste amount in a control rod, raw value, (0-4*height)
    outputWasteAmount(IReactorReader::getWasteAmount, SensorBehavior.ActiveWhileAbove, SensorBehavior.ActiveWhileBelow, SensorBehavior.ActiveWhileBetween),

    // Output: Energy in the reactor's buffer, percentile (0-100, 100 = 100% full)
    outputEnergyAmount(r -> (int)(r.getEnergyStoredPercentage() * 100.0), SensorBehavior.ActiveWhileAbove, SensorBehavior.ActiveWhileBelow, SensorBehavior.ActiveWhileBetween),

    // Output: Coolant amount in an active reactor
    outputCoolantAmount(IReactorReader::getCoolantAmount, SensorBehavior.ActiveWhileAbove, SensorBehavior.ActiveWhileBelow, SensorBehavior.ActiveWhileBetween),

    // Output: Vapor amount in an active reactor
    outputVaporAmount(IReactorReader::getVaporAmount, SensorBehavior.ActiveWhileAbove, SensorBehavior.ActiveWhileBelow, SensorBehavior.ActiveWhileBetween);

    ReactorSensorType(final SensorBehavior... behaviors) {
        this(SensorTypeData.input(behaviors));
    }

    ReactorSensorType(final ToIntFunction<@NotNull IReactorReader> outputSupplier, final SensorBehavior... behaviors) {
        this(SensorTypeData.output(outputSupplier, behaviors));
    }

    //region ISensorType

    @Override
    public boolean isDisabled() {
        return Disabled == this;
    }

    @Override
    public boolean isInput() {
        return this._data.isInput();
    }

    @Override
    public boolean isOutput() {
        return this._data.isOutput();
    }

    @Override
    public List<SensorBehavior> getBehaviors() {
        return this._data.getBehaviors();
    }

    @Override
    public int applyAsInt(final IReactorReader reader) {
        return this._data.applyAsInt(reader);
    }

    @Override
    public String getTranslationBaseName() {
        return this._translationBaseName;
    }

    //endregion
    //region IDebuggable

    @Override
    public void getDebugMessages(final LogicalSide side, final IDebugMessages messages) {
        messages.addUnlocalized("Reactor Sensor Type: %1$s", this);
    }

    //endregion
    //region internals

    ReactorSensorType(final SensorTypeData<IReactorReader> _data) {

        this._data = _data;
        this._translationBaseName = "gui.bigreactors.reactor.redstoneport.sensortype." + CodeHelper.neutralLowercase(this.name());
    }

    private final SensorTypeData<IReactorReader> _data;
    private final String _translationBaseName;

    //endregion
}
