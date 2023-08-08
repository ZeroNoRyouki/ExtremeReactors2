/*
 *
 * ReprocessorAccessPortContainer.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.container;

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.part.ReprocessorAccessPortEntity;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.item.inventory.container.ContainerFactory;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotTemplate;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.type.SlotType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class ReprocessorAccessPortContainer
        extends ModTileContainer<ReprocessorAccessPortEntity> {

    public ReprocessorAccessPortContainer(final int windowId, final PlayerInventory playerInventory,
                                          final ReprocessorAccessPortEntity port) {

        super(5, factoryFor(port), Content.ContainerTypes.REPROCESSOR_ACCESSPORT.get(), windowId, port);

        this.addInventory("inv", port.getItemInventory(port.getDirection()));
        this.addInventory(ModContainer.INVENTORYNAME_PLAYER_INVENTORY, playerInventory);
        this.createSlots();
    }

    public ReprocessorAccessPortContainer(final int windowId, final PlayerInventory playerInventory,
                                          final PacketBuffer networkData) {
        this(windowId, playerInventory, AbstractModBlockEntity.getGuiClientBlockEntity(networkData));
    }

    //region internals

    private static ContainerFactory factoryFor(final ReprocessorAccessPortEntity port) {

        final ContainerFactory factory = new ContainerFactory();

        factory.addStandardPlayerInventorySlots(0, 0);

        if (port.getDirection().isInput()) {
            factory.addSlot(0, "inv", new SlotTemplate(SlotType.Input, (slotIndex, stack) -> port.isValidIngredient(stack)), 0, 0);
        } else {
            factory.addSlot(0, "inv", new SlotTemplate(SlotType.Output), 0, 0);
        }

        return factory;
    }

    //endregion
}
