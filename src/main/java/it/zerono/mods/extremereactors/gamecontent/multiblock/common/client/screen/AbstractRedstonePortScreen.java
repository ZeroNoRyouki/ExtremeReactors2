/*
 *
 * AbstractRedstonePortScreen.java
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

import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.container.AbstractRedstonePortContainer;
import it.zerono.mods.zerocore.base.client.screen.control.redstone.sensor.ISensorBuilder;
import it.zerono.mods.zerocore.base.client.screen.control.redstone.sensor.SensorPanel;
import it.zerono.mods.zerocore.base.multiblock.AbstractMultiblockMachineController;
import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockMachineEntity;
import it.zerono.mods.zerocore.base.redstone.sensor.AbstractSensorSetting;
import it.zerono.mods.zerocore.base.redstone.sensor.ISensorSettingFactory;
import it.zerono.mods.zerocore.base.redstone.sensor.ISensorSettingHolder;
import it.zerono.mods.zerocore.base.redstone.sensor.ISensorType;
import it.zerono.mods.zerocore.lib.IMachineReader;
import it.zerono.mods.zerocore.lib.client.gui.layout.FixedLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.sprite.SpriteTextureMap;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockMachine;
import it.zerono.mods.zerocore.lib.multiblock.variant.IMultiblockDimensionVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public abstract class AbstractRedstonePortScreen<Controller extends AbstractMultiblockMachineController<Controller, V> & IMultiblockMachine,
                                                V extends IMultiblockDimensionVariant,
                                                T extends AbstractMultiblockMachineEntity<Controller, V> &
                                                        ISensorSettingHolder<Reader, Writer, SensorType, SensorSetting> & MenuProvider,
                                                C extends AbstractRedstonePortContainer<Controller, V, T, Reader, Writer, SensorType, SensorSetting>,
                                                Reader extends IMachineReader, Writer,
                                                SensorType extends Enum<SensorType> & ISensorType<Reader>,
                                                SensorSetting extends AbstractSensorSetting<Reader, Writer, SensorType, SensorSetting>>
        extends CommonMultiblockScreen<Controller, T, C> {

    @SafeVarargs
    protected AbstractRedstonePortScreen(final C container, final Inventory inventory,
                                         final PlayerInventoryUsage inventoryUsage, final Component title,
                                         final Supplier<@NotNull SpriteTextureMap> mainTextureSupplier,
                                         final ISensorSettingFactory<Reader, Writer, SensorType, SensorSetting> sensorSettingFactory,
                                         final SensorType... validSensors) {

        super(container, inventory, inventoryUsage, title, mainTextureSupplier);
        this.ignoreCloseOnInventoryKey(true);

        final int sensorPanelWidth = this.getContentWidth();
        final int sensorPanelHeight = this.getContentHeight();

        this._sensorPanel = new SensorPanel<>(this, container.SETTING, 3,
                "gui.bigreactors.generator.redstoneport.sensortype.sensorlistlabel",
                sensorPanelWidth, sensorPanelHeight, this::buildSettings, this::onSave, this::onDisable,
                this.getDefaultSettings(), sensorSettingFactory, validSensors);

        this._sensorPanel.setLayoutEngineHint(FixedLayoutEngine.hint(0, 0, sensorPanelWidth, sensorPanelHeight));
    }

    protected abstract SensorSetting getDefaultSettings();

    protected abstract SensorSetting getDisabledSettings();

    protected abstract void buildSettings(ISensorBuilder<Reader, SensorType> builder);

    //region AbstractMultiblockScreen

    @Override
    protected void onScreenCreate() {

        super.onScreenCreate();
        this.addControl(this._sensorPanel);
    }

    //endregion
    //region internals

    private void onDisable() {
        this.sendCommandToServer(CommonConstants.COMMAND_DISABLE_REDSTONE_SENSOR);
    }

    private void onSave() {

        if (this.isValid()) {
            this.sendCommandToServer(CommonConstants.COMMAND_SET_REDSTONE_SENSOR,
                    this._sensorPanel.getSettings(this.getDisabledSettings()).syncDataTo(new CompoundTag()));
        }
    }

    private final SensorPanel<Reader, Writer, SensorType, SensorSetting> _sensorPanel;

    //endregion
}
