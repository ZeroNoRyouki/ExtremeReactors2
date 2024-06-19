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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class ReprocessorControllerContainer
        extends ModTileContainer<ReprocessorControllerEntity> {

    public final BooleanData ACTIVE;
    public final FluidStackData FLUID_INPUT_STACK;
    public final ItemStackData ITEM_INPUT_STACK;
    public final ItemStackData ITEM_OUTPUT_STACK;
    public final WideAmountData ENERGY_STORED;
    public final DoubleData RECIPE_PROGRESS;

    private ReprocessorControllerContainer(boolean isClientSide, final int windowId, final Inventory playerInventory,
                                           final ReprocessorControllerEntity controller) {

        super(isClientSide, 5, ContainerFactory.EMPTY, Content.ContainerTypes.REPROCESSOR_CONTROLLER.get(),
                windowId, playerInventory, controller);

        final MultiblockReprocessor reprocessor = controller.getMultiblockController().orElseThrow(IllegalStateException::new);

        this.ACTIVE = BooleanData.of(this, reprocessor::isMachineActive, reprocessor::setMachineActive);
        this.FLUID_INPUT_STACK = FluidStackData.sampled(2, this,
                () -> reprocessor.getFluidHandler().getFluidInTank(0));
        this.ITEM_INPUT_STACK = ItemStackData.sampled(2, this,
                () -> reprocessor.getItemHandler(IoDirection.Input).getStackInSlot(0));
        this.ITEM_OUTPUT_STACK = ItemStackData.sampled(2, this,
                () -> reprocessor.getItemHandler(IoDirection.Output).getStackInSlot(0));
        this.ENERGY_STORED = WideAmountData.sampled(3, this,
                () -> WideAmount.from(reprocessor.getEnergyStorage().getEnergyStored(EnergySystem.ForgeEnergy)));
        this.RECIPE_PROGRESS = DoubleData.of(this, reprocessor::getRecipeProgress);
    }

    public ReprocessorControllerContainer(final int windowId, final Inventory playerInventory,
                                          final ReprocessorControllerEntity controller) {
        this(false, windowId, playerInventory, controller);
    }

    public ReprocessorControllerContainer(final int windowId, final Inventory playerInventory,
                                          final FriendlyByteBuf networkData) {
        this(true, windowId, playerInventory, AbstractModBlockEntity.getGuiClientBlockEntity(networkData));
    }

    public int getOutputCapacity() {
        return MultiblockReprocessor.FLUID_CAPACITY;
    }

    public WideAmount getEnergyCapacity() {
        return WideAmount.asImmutable(MultiblockReprocessor.ENERGY_CAPACITY);
    }
}
