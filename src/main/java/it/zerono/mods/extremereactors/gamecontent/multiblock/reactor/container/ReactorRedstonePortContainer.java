/*
 *
 * ReactorRedstonePortContainer.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.container;

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.container.AbstractRedstonePortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.IReactorReader;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.IReactorWriter;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.container.data.ReactorSensorSettingData;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorRedstonePortEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.sensor.ReactorSensorSetting;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.sensor.ReactorSensorType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.IMultiblockReactorVariant;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class ReactorRedstonePortContainer
        extends AbstractRedstonePortContainer<MultiblockReactor, IMultiblockReactorVariant, ReactorRedstonePortEntity,
        IReactorReader, IReactorWriter, ReactorSensorType, ReactorSensorSetting> {

    public ReactorRedstonePortContainer(int windowId, Inventory playerInventory,
                                        ReactorRedstonePortEntity port) {

        super(windowId, playerInventory, port, Content.ContainerTypes.REACTOR_REDSTONEPORT.get(), ReactorSensorSettingData::of);
        this._isPassive = port.evalOnController(reactor -> reactor.getOperationalMode().isPassive(), false);
    }

    public ReactorRedstonePortContainer(int windowId, Inventory playerInventory, FriendlyByteBuf networkData) {
        this(windowId, playerInventory, AbstractModBlockEntity.getGuiClientBlockEntity(networkData));
    }

    public boolean isReactorInPassiveMode() {
        return this._isPassive;
    }

    //region internals

    private final boolean _isPassive;

    //endregion
}
