/*
 *
 * ChargingPortContainer.java
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

import it.zerono.mods.zerocore.base.multiblock.part.io.power.charging.IChargingPort;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.item.inventory.container.ContainerFactory;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotTemplate;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.type.SlotType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;

public class ChargingPortContainer<T extends AbstractModBlockEntity & IChargingPort>
        extends ModTileContainer<T> {

    public static final ContainerFactory FACTORY = new ContainerFactory() {
        /**
         * Override in your derived class to add your slots to the factory
         * <p>
         * Keep in mind that this is called during object construction
         */
        @Override
        protected void onAddSlots() {

            this.addStandardPlayerInventorySlots(0, 0);
            this.addSlot(0, IoDirection.Input.name(), new SlotTemplate(SlotType.Input, (index, stack) -> true), 0, 0);
            this.addSlot(0, IoDirection.Output.name(), new SlotTemplate(SlotType.Output), 0, 0);
        }
    };

    public ChargingPortContainer(final int windowId, final ContainerType<? extends ModTileContainer<T>> type,
                                 final PlayerInventory playerInventory, final T port) {

        super(FACTORY, type, windowId, port);

        this.addInventory(IoDirection.Input.name(), port.getChargingPortHandler().getItemStackHandler(IoDirection.Input));
        this.addInventory(IoDirection.Output.name(), port.getChargingPortHandler().getItemStackHandler(IoDirection.Output));
        this.addInventory(ModContainer.INVENTORYNAME_PLAYER_INVENTORY, playerInventory);
        this.createSlots();
    }

    public ChargingPortContainer(final int windowId, final ContainerType<? extends ModTileContainer<T>> type,
                                 final PlayerInventory playerInventory, final PacketBuffer networkData) {
        this(windowId, type, playerInventory, AbstractModBlockEntity.getGuiClientBlockEntity(networkData));
    }
}
