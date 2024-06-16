/*
 *
 * ReactorSolidAccessPortContainer.java
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

import it.zerono.mods.extremereactors.api.reactor.ReactantType;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactantHelper;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorSolidAccessPortEntity;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.item.inventory.container.ContainerFactory;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.BooleanData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.EnumData;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotTemplate;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.type.SlotType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class ReactorSolidAccessPortContainer
        extends ModTileContainer<ReactorSolidAccessPortEntity> {

    public final BooleanData ACTIVE;
    public final EnumData<IoDirection> DIRECTION;

    public ReactorSolidAccessPortContainer(final int windowId, final Inventory playerInventory,
                                           final ReactorSolidAccessPortEntity port) {

        super(5, new ContainerFactory()
                        .addStandardPlayerInventorySlots(0, 0)
                        .addSlot(0, ReactantType.Fuel.name(), new SlotTemplate(SlotType.Input,
                                ($, stack) -> ReactantHelper.isValidSource(ReactantType.Fuel, stack)), 0, 0)
                        .addSlot(0, ReactantType.Waste.name(), new SlotTemplate(SlotType.Output), 0, 0),
                Content.ContainerTypes.REACTOR_SOLID_ACCESSPORT.get(), windowId, playerInventory, port);

        final MultiblockReactor reactor = port.getMultiblockController().orElseThrow(IllegalStateException::new);
        final boolean isClientSide = reactor.getWorld().isClientSide();

        this.ACTIVE = BooleanData.of(this, isClientSide, () -> reactor::isMachineActive);
        this.DIRECTION = EnumData.of(this, isClientSide, IoDirection.class, () -> port::getIoDirection);

        this.addInventory(ReactantType.Fuel.name(), port.getItemStackHandler(ReactantType.Fuel));
        this.addInventory(ReactantType.Waste.name(), port.getItemStackHandler(ReactantType.Waste));
        this.addInventory(ModContainer.INVENTORYNAME_PLAYER_INVENTORY, playerInventory);
        this.createSlots();
    }

    public ReactorSolidAccessPortContainer(final int windowId, final Inventory playerInventory,
                                           final FriendlyByteBuf networkData) {
        this(windowId, playerInventory, AbstractModBlockEntity.getGuiClientBlockEntity(networkData));
    }
}
