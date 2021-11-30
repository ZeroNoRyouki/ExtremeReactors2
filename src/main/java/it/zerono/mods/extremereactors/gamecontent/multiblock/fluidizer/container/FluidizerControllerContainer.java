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
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.item.inventory.container.ContainerFactory;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.List;

public class FluidizerControllerContainer
        extends ModTileContainer<FluidizerControllerEntity> {

    public FluidizerControllerContainer(final int windowId, final Inventory playerInventory,
                                        final FluidizerControllerEntity controller) {

        super(ContainerFactory.EMPTY, Content.ContainerTypes.FLUIDIZER_CONTROLLER.get(), windowId, controller);

        final MultiblockFluidizer fluidizer = controller.getMultiblockController().orElseThrow(IllegalStateException::new);

        this._active = false;
        this._recipeType = IFluidizerRecipe.Type.Invalid;
        this._output = FluidStack.EMPTY;
        this._energyStored = WideAmount.ZERO;
        this._recipeProgress = 0.0;

        this._outputCapacity = fluidizer.getFluidHandler().getTankCapacity(0);
        this._solidInputs = new ItemStack[2];
        Arrays.fill(this._solidInputs, ItemStack.EMPTY);
        this._fluidInputs = new FluidStack[2];
        Arrays.fill(this._fluidInputs, FluidStack.EMPTY);

        this._solidInjectors = fluidizer.getSolidInjectors();
        this._fluidInjectors = fluidizer.getFluidInjectors();

        if (CodeHelper.calledByLogicalServer(fluidizer.getWorld())) {

            this.addContainerData(new BooleanData(fluidizer::isMachineActive, v -> this._active = v));
            this.addContainerData(new EnumData<>(IFluidizerRecipe.Type.class, fluidizer::getRecipeType, v -> this._recipeType = v));
            this.addContainerData(new FluidStackData(() -> fluidizer.getFluidHandler().getFluidInTank(0), v -> this._output = v));
            this.addContainerData(new WideAmountData(() -> fluidizer.getEnergyStorage().getEnergyStored(EnergySystem.ForgeEnergy), v -> this._energyStored = v.copy()));
            this.addContainerData(new DoubleData(fluidizer::getRecipeProgress, v -> this._recipeProgress = v));

            for (int i = 0; i < Math.min(2, this._solidInjectors.size()); ++i) {

                final int index = i;

                this.addContainerData(new ItemStackData(() -> this._solidInjectors.get(index).getStack(), v -> this._solidInputs[index] = v));
            }

            for (int i = 0; i < Math.min(2, this._fluidInjectors.size()); ++i) {

                final int index = i;

                this.addContainerData(new FluidStackData(() -> this._fluidInjectors.get(index).getStack(), v -> this._fluidInputs[index] = v));
            }

        } else {

            this.addContainerData(new BooleanData(this::isFluidizerActive, v -> this._active = v));
            this.addContainerData(new EnumData<>(IFluidizerRecipe.Type.class, this::getRecipeType, v -> this._recipeType = v));
            this.addContainerData(new FluidStackData(this::getOutput, v -> this._output = v));
            this.addContainerData(new WideAmountData(this::getEnergyStored, v -> this._energyStored = v.copy()));
            this.addContainerData(new DoubleData(this::getRecipeProgress, v -> this._recipeProgress = v));

            for (int i = 0; i < Math.min(2, this._solidInjectors.size()); ++i) {

                final int index = i;

                this.addContainerData(new ItemStackData(() -> this.getSolidInput(index), v -> this._solidInputs[index] = v));
            }

            for (int i = 0; i < Math.min(2, this._fluidInjectors.size()); ++i) {

                final int index = i;

                this.addContainerData(new FluidStackData(() -> this.getFluidInput(index), v -> this._fluidInputs[index] = v));
            }
        }
    }

    public FluidizerControllerContainer(final int windowId, final Inventory playerInventory,
                                        final FriendlyByteBuf networkData) {
        this(windowId, playerInventory, AbstractModBlockEntity.getGuiClientBlockEntity(networkData));
    }

    public IFluidizerRecipe.Type getRecipeType() {
        return this._recipeType;
    }

    public ItemStack getSolidInput(final int index) {
        return this._solidInputs[index];
    }

    public FluidStack getFluidInput(final int index) {
        return this._fluidInputs[index];
    }

    public int getFluidInputAmount(final int index) {
        return this._fluidInputs[index].getAmount();
    }

    public FluidStack getOutput() {
        return this._output;
    }

    public int getOutputAmount() {
        return this._output.getAmount();
    }

    public int getOutputCapacity() {
        return this._outputCapacity;
    }

    public WideAmount getEnergyStored() {
        return this._energyStored;
    }

    public double getRecipeProgress() {
        return this._recipeProgress;
    }

    public boolean isFluidizerActive() {
        return this._active;
    }

    //region internals

    private final ItemStack[] _solidInputs;
    private final FluidStack[] _fluidInputs;
    private final List<FluidizerSolidInjectorEntity> _solidInjectors;
    private final List<FluidizerFluidInjectorEntity> _fluidInjectors;
    private final int _outputCapacity;
    private boolean _active;
    private IFluidizerRecipe.Type _recipeType;
    private FluidStack _output;
    private WideAmount _energyStored;
    private double _recipeProgress;

    //endregion
}
