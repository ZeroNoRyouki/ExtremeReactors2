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
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.MultiblockReprocessor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.part.ReprocessorAccessPortEntity;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.item.inventory.container.ContainerFactory;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.BooleanData;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotTemplate;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.type.SlotType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class ReprocessorAccessPortContainer
        extends ModTileContainer<ReprocessorAccessPortEntity> {

    public final BooleanData ACTIVE;

    public ReprocessorAccessPortContainer(final int windowId, final Inventory playerInventory,
                                          final ReprocessorAccessPortEntity port) {

        super(5, factoryFor(port), Content.ContainerTypes.REPROCESSOR_ACCESSPORT.get(), windowId, playerInventory, port);

        final MultiblockReprocessor reprocessor = port.getMultiblockController().orElseThrow(IllegalStateException::new);

        this.ACTIVE = BooleanData.of(this, reprocessor.getWorld().isClientSide(), () -> reprocessor::isMachineActive);
        this._direction = port.getDirection();

        this.addInventory("inv", port.getItemInventory(this._direction));
        this.addInventory(ModContainer.INVENTORYNAME_PLAYER_INVENTORY, playerInventory);
        this.createSlots();
    }

    public ReprocessorAccessPortContainer(final int windowId, final Inventory playerInventory,
                                          final FriendlyByteBuf networkData) {
        this(windowId, playerInventory, AbstractModBlockEntity.getGuiClientBlockEntity(networkData));
    }

    public IoDirection getDirection() {
        return this._direction;
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
    //region internals

    private final IoDirection _direction;

    //endregion
}
