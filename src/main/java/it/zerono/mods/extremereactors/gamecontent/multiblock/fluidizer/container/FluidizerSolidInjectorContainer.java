/*
 *
 * FluidizerSolidInjectorContainer.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.container;

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.MultiblockFluidizer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part.FluidizerSolidInjectorEntity;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.item.inventory.container.ContainerFactory;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.BooleanData;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotTemplate;
import it.zerono.mods.zerocore.lib.item.inventory.container.slot.type.SlotType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class FluidizerSolidInjectorContainer
        extends ModTileContainer<FluidizerSolidInjectorEntity> {

    public final BooleanData ACTIVE;

    private FluidizerSolidInjectorContainer(boolean isClientSide, final int windowId, final Inventory playerInventory,
                                            final FluidizerSolidInjectorEntity injector) {
        super(isClientSide, 5, new ContainerFactory()
                        .addStandardPlayerInventorySlots(0, 0)
                        .addSlot(0, "inv", new SlotTemplate(SlotType.Input, ($, stack) -> injector.isValidIngredient(stack)), 0, 0),
                Content.ContainerTypes.FLUIDIZER_SOLID_INJECTOR.get(), windowId, playerInventory, injector);

        final MultiblockFluidizer fluidizer = injector.getMultiblockController().orElseThrow(IllegalStateException::new);

        this.ACTIVE = BooleanData.of(this, fluidizer::isMachineActive, fluidizer::setMachineActive);

        this.addInventory("inv", injector.getItemHandler());
        this.addInventory(ModContainer.INVENTORYNAME_PLAYER_INVENTORY, playerInventory);
        this.createSlots();
    }

    public FluidizerSolidInjectorContainer(final int windowId, final Inventory playerInventory,
                                           final FluidizerSolidInjectorEntity injector) {
        this(false, windowId, playerInventory, injector);
    }

    public FluidizerSolidInjectorContainer(final int windowId, final Inventory playerInventory,
                                           final FriendlyByteBuf networkData) {
        this(true, windowId, playerInventory, AbstractModBlockEntity.getGuiClientBlockEntity(networkData));
    }
}
