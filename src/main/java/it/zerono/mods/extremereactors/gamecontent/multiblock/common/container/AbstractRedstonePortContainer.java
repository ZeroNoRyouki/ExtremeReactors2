/*
 *
 * AbstractRedstonePortContainer.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common.container;

import it.zerono.mods.zerocore.base.multiblock.AbstractMultiblockMachineController;
import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockMachineContainer;
import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockMachineEntity;
import it.zerono.mods.zerocore.base.redstone.sensor.AbstractSensorSetting;
import it.zerono.mods.zerocore.base.redstone.sensor.ISensorSettingHolder;
import it.zerono.mods.zerocore.base.redstone.sensor.ISensorType;
import it.zerono.mods.zerocore.lib.IMachineReader;
import it.zerono.mods.zerocore.lib.item.inventory.container.ContainerFactory;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.IBindableData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.IBindableDataFactory;
import it.zerono.mods.zerocore.lib.multiblock.variant.IMultiblockDimensionVariant;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;

public abstract class AbstractRedstonePortContainer<Controller extends AbstractMultiblockMachineController<Controller, V>,
                                                    V extends IMultiblockDimensionVariant,
                                                    T extends AbstractMultiblockMachineEntity<Controller, V> & ISensorSettingHolder<Reader, Writer, SensorType, SensorSetting>,
                                                    Reader extends IMachineReader, Writer,
                                                    SensorType extends Enum<SensorType> & ISensorType<Reader>,
                                                    SensorSetting extends AbstractSensorSetting<Reader, Writer, SensorType, SensorSetting>>
        extends AbstractMultiblockMachineContainer<Controller, V, T> {

    public final IBindableData<SensorSetting> SETTING;

    @SuppressWarnings("Convert2MethodRef")
    protected AbstractRedstonePortContainer(int windowId, PlayerInventory playerInventory, T port,
                                            ContainerType<? extends AbstractMultiblockMachineContainer<Controller, V, T>> type,
                                            IBindableDataFactory<SensorSetting> dataFactory) {

        super(3, ContainerFactory.EMPTY, type, windowId, port);
        this.SETTING = dataFactory.create(this, port.getPartWorldOrFail().isClientSide(), () -> () -> port.getSensorSetting());
    }
}
