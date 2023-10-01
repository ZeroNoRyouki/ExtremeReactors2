/*
 *
 * TurbineRedstonePortContainer.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.container;

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.container.AbstractRedstonePortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.ITurbineReader;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.ITurbineWriter;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.MultiblockTurbine;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.container.data.TurbineSensorSettingData;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineRedstonePortEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.sensor.TurbineSensorSetting;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.sensor.TurbineSensorType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.IMultiblockTurbineVariant;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class TurbineRedstonePortContainer
        extends AbstractRedstonePortContainer<MultiblockTurbine, IMultiblockTurbineVariant, TurbineRedstonePortEntity,
                                              ITurbineReader, ITurbineWriter, TurbineSensorType, TurbineSensorSetting> {

    public TurbineRedstonePortContainer(int windowId, PlayerInventory playerInventory,
                                        TurbineRedstonePortEntity port) {
        super(windowId, playerInventory, port, Content.ContainerTypes.TURBINE_REDSTONEPORT.get(), TurbineSensorSettingData::of);
    }

    public TurbineRedstonePortContainer(int windowId, PlayerInventory playerInventory, PacketBuffer networkData) {
        this(windowId, playerInventory, AbstractModBlockEntity.getGuiClientBlockEntity(networkData));
    }
}
