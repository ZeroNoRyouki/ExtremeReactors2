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
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part.FluidizerControllerEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part.FluidizerFluidInjectorEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part.FluidizerSolidInjectorEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.IFluidizerRecipe;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.item.inventory.container.ContainerFactory;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

import java.util.List;

public class FluidizerControllerContainer
        extends ModTileContainer<FluidizerControllerEntity> {

    public final BooleanData ACTIVE;
    public final EnumData<IFluidizerRecipe.Type> RECIPE_TYPE;
    public final DoubleData RECIPE_PROGRESS;
    public final FluidStackData FLUID_OUTPUT_STACK;
    public final WideAmountData ENERGY_STORED;

    public FluidizerControllerContainer(final int windowId, final PlayerInventory playerInventory,
                                        final FluidizerControllerEntity controller) {

        super(5, ContainerFactory.EMPTY, Content.ContainerTypes.FLUIDIZER_CONTROLLER.get(), windowId, controller);

        final MultiblockFluidizer fluidizer = controller.getMultiblockController().orElseThrow(IllegalStateException::new);

        this._outputCapacity = fluidizer.getFluidHandler().getTankCapacity(0);

        final List<FluidizerSolidInjectorEntity> solidInjectors = fluidizer.getSolidInjectors();
        final List<FluidizerFluidInjectorEntity> fluidInjectors = fluidizer.getFluidInjectors();
        final boolean isClientSide = fluidizer.getWorld().isClientSide();

        this.ACTIVE = BooleanData.of(this, isClientSide, () -> fluidizer::isMachineActive);
        this.RECIPE_TYPE = EnumData.of(this, isClientSide, IFluidizerRecipe.Type.class,
                () -> fluidizer::getRecipeType);
        this.RECIPE_PROGRESS = DoubleData.of(this, isClientSide, () -> fluidizer::getRecipeProgress);
        this.FLUID_OUTPUT_STACK = FluidStackData.sampled(3, this, isClientSide,
                () -> () -> fluidizer.getFluidHandler().getFluidInTank(0));
        this.ENERGY_STORED = WideAmountData.sampled(3, this, isClientSide,
                () -> () -> fluidizer.getEnergyStorage().getEnergyStored(EnergySystem.ForgeEnergy));

        this._solidInputBindings = new ItemStackData[2];
        this._fluidInputBindings = new FluidStackData[2];

        for (int i = 0; i < 2; ++i) {

            final ItemStackData itemData;
            final FluidStackData fluidData;

            if (i < solidInjectors.size()) {

                final FluidizerSolidInjectorEntity entity = solidInjectors.get(i);

                itemData = ItemStackData.of(this, isClientSide, () -> entity::getStack);

            } else {

                itemData = ItemStackData.empty(isClientSide);
            }

            if (i < fluidInjectors.size()) {

                final FluidizerFluidInjectorEntity entity = fluidInjectors.get(i);

                fluidData = FluidStackData.of(this, isClientSide, () -> entity::getStack);

            } else {

                fluidData = FluidStackData.empty(isClientSide);
            }

            this._solidInputBindings[i] = itemData;
            this._fluidInputBindings[i] = fluidData;
        }
    }

    public FluidizerControllerContainer(final int windowId, final PlayerInventory playerInventory,
                                        final PacketBuffer networkData) {
        this(windowId, playerInventory, AbstractModBlockEntity.getGuiClientBlockEntity(networkData));
    }

    public ItemStackData getSolidInput(int index) {
        return this._solidInputBindings[index];
    }

    public FluidStackData getFluidInput(int index) {
        return this._fluidInputBindings[index];
    }

    public int getOutputCapacity() {
        return this._outputCapacity;
    }

    public WideAmount getEnergyCapacity() {
        return MultiblockFluidizer.ENERGY_CAPACITY;
    }

    //region internals

    private final ItemStackData[] _solidInputBindings;
    private final FluidStackData[] _fluidInputBindings;
    private final int _outputCapacity;

    //endregion
}
