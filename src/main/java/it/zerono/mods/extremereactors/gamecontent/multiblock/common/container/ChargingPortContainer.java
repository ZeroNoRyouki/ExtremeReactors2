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

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.AbstractGeneratorMultiblockController;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockGeneratorVariant;
import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity;
import it.zerono.mods.zerocore.base.multiblock.part.io.power.charging.IChargingPort;
import it.zerono.mods.zerocore.lib.IActivableMachine;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockVariantProvider;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.item.inventory.container.ContainerFactory;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.BooleanData;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotTemplate;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.type.SlotType;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockMachine;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;

public class ChargingPortContainer<Controller extends AbstractGeneratorMultiblockController<Controller, V> & IMultiblockMachine & IActivableMachine,
        V extends IMultiblockGeneratorVariant,
        T extends AbstractMultiblockEntity<Controller> & IChargingPort & IMultiblockVariantProvider<V> & INamedContainerProvider>
        extends ModTileContainer<T> {

    public final BooleanData ACTIVE;

    public ChargingPortContainer(final int windowId, final ContainerType<? extends ChargingPortContainer<Controller, V, T>> type,
                                 final PlayerInventory playerInventory, final T port) {

        super(5, new ContainerFactory()
                .addStandardPlayerInventorySlots(0, 0)
                .addSlot(0, IoDirection.Input.name(), new SlotTemplate(SlotType.Input, (index, stack) -> true), 0, 0)
                .addSlot(0, IoDirection.Output.name(), new SlotTemplate(SlotType.Output), 0, 0), type, windowId, port);

        final Controller controller = port.getMultiblockController().orElseThrow(IllegalStateException::new);

        // don't use method refs here to avoid LambdaConversionExceptions
        //noinspection Convert2MethodRef
        this.ACTIVE = BooleanData.of(this, controller.getWorld().isClientSide(), () -> () -> controller.isMachineActive());

        this.addInventory(IoDirection.Input.name(), port.getChargingPortHandler().getItemStackHandler(IoDirection.Input));
        this.addInventory(IoDirection.Output.name(), port.getChargingPortHandler().getItemStackHandler(IoDirection.Output));
        this.addInventory(ModContainer.INVENTORYNAME_PLAYER_INVENTORY, playerInventory);
        this.createSlots();
    }

    public ChargingPortContainer(final int windowId, final ContainerType<? extends ChargingPortContainer<Controller, V, T>> type,
                                 final PlayerInventory playerInventory, final PacketBuffer networkData) {
        this(windowId, type, playerInventory, AbstractModBlockEntity.getGuiClientBlockEntity(networkData));
    }
}
