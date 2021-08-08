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
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactantHelper;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorSolidAccessPortEntity;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.item.inventory.container.ContainerFactory;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotTemplate;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.type.SlotType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.items.IItemHandler;

public class ReactorSolidAccessPortContainer extends ModTileContainer<ReactorSolidAccessPortEntity> {

    public static final ContainerFactory FACTORY = new ContainerFactory() {
        /**
         * Override in your derived class to add your slots to the factory
         * <p>
         * Keep in mind that this is called during object construction
         */
        @Override
        protected void onAddSlots() {

            this.addStandardPlayerInventorySlots(8*0, 83*0);
            this.addSlot(0, ReactantType.Fuel.name(), new SlotTemplate(SlotType.Input,
                    (index, stack) -> ReactantHelper.isValidSource(ReactantType.Fuel, stack)), 87*0, 43*0);
            this.addSlot(0, ReactantType.Waste.name(), new SlotTemplate(SlotType.Output), 137*0, 43*0);
        }
    };

    public ReactorSolidAccessPortContainer(final int windowId, final Inventory playerInventory,
                                           final ReactorSolidAccessPortEntity port) {

        super(FACTORY, Content.ContainerTypes.REACTOR_SOLID_ACCESSPORT.get(), windowId, port);

        this._fuelHandler = port.getItemStackHandler(ReactantType.Fuel);
        this._wasteHandler = port.getItemStackHandler(ReactantType.Waste);

        this.addInventory(ReactantType.Fuel.name(), this._fuelHandler);
        this.addInventory(ReactantType.Waste.name(), this._wasteHandler);
        this.addInventory(ModContainer.INVENTORYNAME_PLAYER_INVENTORY, playerInventory);
        this.createSlots();
    }

    public ReactorSolidAccessPortContainer(final int windowId, final Inventory playerInventory,
                                           final FriendlyByteBuf networkData) {
        this(windowId, playerInventory, AbstractModBlockEntity.<ReactorSolidAccessPortEntity>getGuiClientBlockEntity(networkData));
    }

    public IoDirection getIoDirection() {
        return this.getTileEntity().getIoDirection();
    }

    //region internals

    private final IItemHandler _fuelHandler;
    private final IItemHandler _wasteHandler;

    //endregion
}
