/*
 *
 * RedstonePortSettingControlsMap.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import it.zerono.mods.zerocore.base.redstone.sensor.AbstractSensorSetting;
import it.zerono.mods.zerocore.base.redstone.sensor.ISensorSettingFactory;
import it.zerono.mods.zerocore.base.redstone.sensor.ISensorType;
import it.zerono.mods.zerocore.base.redstone.sensor.SensorBehavior;
import it.zerono.mods.zerocore.lib.IMachineReader;
import it.zerono.mods.zerocore.lib.client.gui.control.*;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RedstonePortSettingControlsMap<Reader extends IMachineReader, Writer,
                                            SensorType extends Enum<SensorType> & ISensorType<Reader>,
                                            SensorSetting extends AbstractSensorSetting<Reader, Writer, SensorType>> {

    public final Label SensorName;
    public final PanelGroup<SensorType> SensorsGroup;
    public final Map<SensorType, SensorEntry> Sensors;

    public static class SensorEntry {

        public final SwitchPictureButton SensorControl;
        public final ChoiceText<SensorBehavior> BehaviorControl;
        public final Map<SensorBehavior, BehaviorEntry> Behaviors;

        public SensorEntry(final SwitchPictureButton sensorControl,
                           final ChoiceText<SensorBehavior> behaviorControl, final BehaviorEntry... behaviors) {

            this.SensorControl = sensorControl;
            this.BehaviorControl = behaviorControl;
            //noinspection UnstableApiUsage
            this.Behaviors = Arrays.stream(behaviors).collect(ImmutableMap.toImmutableMap(be -> be.Behavior, be -> be));
        }

        public SensorEntry(final SwitchPictureButton sensorControl,
                           final ChoiceText<SensorBehavior> behaviorControl, final List<BehaviorEntry> behaviors) {

            this.SensorControl = sensorControl;
            this.BehaviorControl = behaviorControl;
            //noinspection UnstableApiUsage
            this.Behaviors = behaviors.stream().collect(ImmutableMap.toImmutableMap(be -> be.Behavior, be -> be));
        }

        public List<TextInput> inputs(final SensorBehavior behavior) {
            return this.Behaviors.containsKey(behavior) ? this.Behaviors.get(behavior).Inputs : Collections.emptyList();
        }
    }

    public static class BehaviorEntry {

        public final SensorBehavior Behavior;
        public final List<TextInput> Inputs;

        public BehaviorEntry(final SensorBehavior behavior, final TextInput... inputs) {

            this.Behavior = behavior;
            this.Inputs = ImmutableList.copyOf(inputs);
        }
    }

    public RedstonePortSettingControlsMap(final Label name, final PanelGroup<SensorType> group,
                                          final ISensorSettingFactory<Reader, Writer, SensorType, SensorSetting> factory) {

        this.SensorName = name;
        this.SensorsGroup = group;
        this.Sensors = Maps.newHashMap();
        this._factory = factory;
    }

    public void add(final SensorType sensor, final SensorEntry entry) {
        this.Sensors.put(sensor, entry);
    }

    public SensorSetting getSettings(final SensorSetting defaultValue) {
        return this.SensorsGroup.getActivePanelIndex()
                .filter(sensor -> !sensor.isDisabled())
                .map(this::createSetting)
                .orElse(defaultValue);
    }

    public void setSettings(final SensorSetting setting) {

        this.resetControls();

        if (!setting.Sensor.isDisabled()) {

            final SensorEntry se = this.Sensors.get(setting.Sensor);
            final List<TextInput> inputs = se.inputs(setting.Behavior);
            final int[] values = {setting.Value1, setting.Value2};

            for (int idx = 0; idx < Math.min(values.length, inputs.size()); ++idx) {
                inputs.get(idx).setText(Integer.toString(values[idx]));
            }

            se.SensorControl.setActive(true);
            se.BehaviorControl.setSelectedIndex(setting.Behavior);

            this.SensorName.setText(new TranslationTextComponent(setting.Sensor.getTranslationBaseName() + "1"));
            this.SensorsGroup.setActivePanel(setting.Sensor);
        }
    }

    //region internals

    private void resetControls() {

        this.SensorName.setText("");
        this.SensorsGroup.clearActivePanel();
        this.Sensors.values().forEach(se -> {

            se.SensorControl.setActive(false);
            se.BehaviorControl.setSelectedIndex(se.BehaviorControl.getValidIndices().get(0));
            se.Behaviors.values().forEach(be -> be.Inputs.forEach(i -> i.setText("0")));
        });
    }

    private SensorSetting createSetting(final SensorType sensor) {

        final SensorEntry se = this.Sensors.get(sensor);
        final SensorBehavior behavior = se.BehaviorControl.getSelectedIndex();
        final List<TextInput> inputs = se.inputs(behavior);
        final int[] values = new int[2];

        for (int idx = 0; idx < Math.min(values.length, inputs.size()); ++idx) {
            values[idx] = inputs.get(idx).intValue();
        }

        return this._factory.create(sensor, behavior, values[0], values[1]);
    }

    private final ISensorSettingFactory<Reader, Writer, SensorType, SensorSetting> _factory;

    //endregion
}
