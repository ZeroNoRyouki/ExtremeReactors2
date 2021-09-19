///*
// *
// * ReprocessorControllerContainer.java
// *
// * This file is part of Extreme Reactors 2 by ZeroNoRyouki, a Minecraft mod.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// * DEALINGS IN THE SOFTWARE.
// *
// * DO NOT REMOVE OR EDIT THIS HEADER
// *
// */
//
//package it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.container;
//
//import it.zerono.mods.extremereactors.gamecontent.Content;
//import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.part.ReprocessorControllerEntity;
//import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
//import it.zerono.mods.zerocore.lib.data.IoDirection;
//import it.zerono.mods.zerocore.lib.item.inventory.container.ContainerFactory;
//import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
//import it.zerono.mods.zerocore.lib.item.inventory.container.slot.SlotTemplate;
//import it.zerono.mods.zerocore.lib.item.inventory.container.slot.type.SlotType;
//import net.minecraft.entity.player.PlayerInventory;
//import net.minecraft.entity.player.ServerPlayerEntity;
//import net.minecraft.network.PacketBuffer;
//
//public class ReprocessorControllerContainer
//        extends ModTileContainer<ReprocessorControllerEntity> {
//
//    public ReprocessorControllerContainer(final int windowId, final PlayerInventory playerInventory,
//                                          final ReprocessorControllerEntity port, final ServerPlayerEntity player) {
//
//        this(windowId, playerInventory, port);
//        port.enlistForUpdates(player, true);
//    }
//
//    public ReprocessorControllerContainer(final int windowId, final PlayerInventory playerInventory,
//                                          final PacketBuffer networkData) {
//        this(windowId, playerInventory, AbstractModBlockEntity.getGuiClientBlockEntity(networkData));
//    }
//
//    //region internals
//
//    protected ReprocessorControllerContainer(final int windowId, final PlayerInventory playerInventory,
//                                             final ReprocessorControllerEntity port) {
//
//        super(FACTORY, Content.ContainerTypes.REPROCESSOR_CONTROLLER.get(), windowId, port);
//
//        this.addInventory("out", port.getItemInventory(IoDirection.Output));
//        this.addInventory("in", port.getItemInventory(IoDirection.Input));
//        this.createSlots();
//    }
//
//    private static final ContainerFactory FACTORY = new ContainerFactory()
//            .addSlot(0, "in", new SlotTemplate(SlotType.Static), 0, 0)
//            .addSlot(0, "out", new SlotTemplate(SlotType.Static), 0, 0);
//
//    //endregion
//}
