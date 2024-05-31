/*
 *
 * ReprocessorControllerContainer.java
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
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.part.ReprocessorControllerEntity;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.item.inventory.container.ContainerFactory;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class ReprocessorControllerContainer
        extends ModTileContainer<ReprocessorControllerEntity> {

    public final BooleanData ACTIVE;
    public final FluidStackData FLUID_INPUT_STACK;
    public final ItemStackData ITEM_INPUT_STACK;
    public final ItemStackData ITEM_OUTPUT_STACK;
    public final WideAmountData ENERGY_STORED;
    public final DoubleData RECIPE_PROGRESS;

    public ReprocessorControllerContainer(final int windowId, final PlayerInventory playerInventory,
                                          final ReprocessorControllerEntity controller) {

        super(5, ContainerFactory.EMPTY, Content.ContainerTypes.REPROCESSOR_CONTROLLER.get(), windowId, controller);

        final MultiblockReprocessor reprocessor = controller.getMultiblockController().orElseThrow(IllegalStateException::new);
        final boolean isClientSide = reprocessor.getWorld().isClientSide();

        this.ACTIVE = BooleanData.of(this, isClientSide, () -> reprocessor::isMachineActive);
        this.FLUID_INPUT_STACK = FluidStackData.sampled(2, this, isClientSide,
                () -> () -> reprocessor.getFluidHandler().getFluidInTank(0));
        this.ITEM_INPUT_STACK = ItemStackData.sampled(2, this, isClientSide,
                () -> () -> reprocessor.getItemHandler(IoDirection.Input).getStackInSlot(0));
        this.ITEM_OUTPUT_STACK = ItemStackData.sampled(2, this, isClientSide,
                () -> () -> reprocessor.getItemHandler(IoDirection.Output).getStackInSlot(0));
        this.ENERGY_STORED = WideAmountData.sampled(3, this, isClientSide,
                () -> () -> WideAmount.from(reprocessor.getEnergyStorage().getEnergyStored(EnergySystem.ForgeEnergy)));
        this.RECIPE_PROGRESS = DoubleData.of(this, isClientSide, () -> reprocessor::getRecipeProgress);
    }

    public ReprocessorControllerContainer(final int windowId, final PlayerInventory playerInventory,
                                          final PacketBuffer networkData) {
        this(windowId, playerInventory, AbstractModBlockEntity.getGuiClientBlockEntity(networkData));
    }

    public int getOutputCapacity() {
        return MultiblockReprocessor.FLUID_CAPACITY;
    }

    public WideAmount getEnergyCapacity() {
        return WideAmount.asImmutable(MultiblockReprocessor.ENERGY_CAPACITY);
    }
}
