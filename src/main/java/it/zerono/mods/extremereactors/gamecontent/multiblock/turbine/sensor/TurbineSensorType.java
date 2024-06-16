/*
 *
 * TurbineSensorType.java
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

import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.ITurbineReader;
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

public enum TurbineSensorType
        implements ISensorType<ITurbineReader>, IDebuggable {

    /**
     * No sensor active
     */
    Disabled(SensorBehavior.Disabled),

    /**
     * Turn the Turbine on or off.
     * Direction: input
     */
    inputActive(SensorBehavior.SetFromSignal, SensorBehavior.ToggleOnPulse),

    // Input: engage induction coils
    inputEngageCoils(SensorBehavior.SetFromSignal, SensorBehavior.ToggleOnPulse),

    // Input: flow regulator (0-100%)
    inputFlowRegulator(SensorBehavior.SetFromSignal, SensorBehavior.SetFromSignalLevel, SensorBehavior.SetOnPulse, SensorBehavior.AugmentOnPulse, SensorBehavior.ReduceOnPulse),

    // Output: rotor speed
    outputRotorSpeed(r -> (int)r.getRotorSpeed(), SensorBehavior.ActiveWhileAbove, SensorBehavior.ActiveWhileBelow, SensorBehavior.ActiveWhileBetween),

    // Output: Energy in the Turbine's buffer, percentile (0-100, 100 = 100% full)
    outputEnergyAmount(r -> (int)(r.getEnergyStoredPercentage() * 100.0), SensorBehavior.ActiveWhileAbove, SensorBehavior.ActiveWhileBelow, SensorBehavior.ActiveWhileBetween),

    // Output: Coolant amount in the Turbine
    outputCoolantAmount(ITurbineReader::getCoolantAmount, SensorBehavior.ActiveWhileAbove, SensorBehavior.ActiveWhileBelow, SensorBehavior.ActiveWhileBetween),

    // Output: Vapor amount in the Turbine
    outputVaporAmount(ITurbineReader::getVaporAmount, SensorBehavior.ActiveWhileAbove, SensorBehavior.ActiveWhileBelow, SensorBehavior.ActiveWhileBetween);

    TurbineSensorType(final SensorBehavior... behaviors) {
        this(SensorTypeData.input(behaviors));
    }

    TurbineSensorType(final ToIntFunction<@NotNull ITurbineReader> outputSupplier, final SensorBehavior... behaviors) {
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
    public int applyAsInt(final ITurbineReader reader) {
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
        messages.addUnlocalized("Turbine Sensor Type: %1$s", this);
    }

    //endregion
    //region internals

    TurbineSensorType(final SensorTypeData<ITurbineReader> _data) {

        this._data = _data;
        this._translationBaseName = "gui.bigreactors.turbine.redstoneport.sensortype." + CodeHelper.neutralLowercase(this.name());
    }

    private final SensorTypeData<ITurbineReader> _data;
    private final String _translationBaseName;

    //endregion
}
