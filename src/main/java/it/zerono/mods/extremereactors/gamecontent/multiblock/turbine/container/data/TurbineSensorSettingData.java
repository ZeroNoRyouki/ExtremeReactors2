/*
 *
 * TurbineSensorSettingData.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.container.data;

import com.google.common.base.Preconditions;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.ITurbineReader;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.ITurbineWriter;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.sensor.TurbineSensorSetting;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.sensor.TurbineSensorType;
import it.zerono.mods.zerocore.base.redstone.sensor.AbstractSensorSettingData;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TurbineSensorSettingData
        extends AbstractSensorSettingData<ITurbineReader, ITurbineWriter, TurbineSensorType, TurbineSensorSetting> {

    public static TurbineSensorSettingData of(ModContainer container, Supplier<TurbineSensorSetting> serverSideGetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");
        Preconditions.checkNotNull(serverSideGetter, "Server side getter must not be null.");

        final TurbineSensorSettingData data = container.isClientSide() ?
                new TurbineSensorSettingData(serverSideGetter, CodeHelper.emptyConsumer()) :
                new TurbineSensorSettingData(serverSideGetter);

        container.addBindableData(data);
        return data;
    }

    //region internals

    private TurbineSensorSettingData(Supplier<TurbineSensorSetting> getter, Consumer<TurbineSensorSetting> clientSideSetter) {
        super(TurbineSensorType.class, TurbineSensorSetting::new, getter, clientSideSetter);
    }

    private TurbineSensorSettingData(Supplier<TurbineSensorSetting> getter) {
        super(TurbineSensorType.class, TurbineSensorSetting::new, getter);
    }

    //endregion
}
