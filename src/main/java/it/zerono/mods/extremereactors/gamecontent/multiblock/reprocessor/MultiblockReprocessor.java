/*
 *
 * MultiblockReprocessor.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor;

import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.part.*;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.recipe.ReprocessorHeldRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.recipe.ReprocessorRecipe;
import it.zerono.mods.zerocore.lib.*;
import it.zerono.mods.zerocore.lib.block.ModBlock;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import it.zerono.mods.zerocore.lib.data.stack.IStackHolder;
import it.zerono.mods.zerocore.lib.data.stack.OperationMode;
import it.zerono.mods.zerocore.lib.energy.EnergyBuffer;
import it.zerono.mods.zerocore.lib.energy.EnergyHelper;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.energy.IWideEnergyStorage;
import it.zerono.mods.zerocore.lib.energy.handler.WideEnergyStoragePolicyWrapper;
import it.zerono.mods.zerocore.lib.fluid.FluidTank;
import it.zerono.mods.zerocore.lib.fluid.handler.FluidHandlerPolicyWrapper;
import it.zerono.mods.zerocore.lib.item.inventory.ItemStackHolder;
import it.zerono.mods.zerocore.lib.item.inventory.handler.ItemHandlerPolicyWrapper;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockMachine;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.lib.recipe.holder.IHeldRecipe;
import it.zerono.mods.zerocore.lib.recipe.holder.IRecipeHolder;
import it.zerono.mods.zerocore.lib.recipe.holder.RecipeHolder;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredientSource;
import it.zerono.mods.zerocore.lib.recipe.ingredient.RecipeIngredientSourceWrapper;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResultTarget;
import it.zerono.mods.zerocore.lib.recipe.result.ItemStackRecipeResult;
import it.zerono.mods.zerocore.lib.recipe.result.RecipeResultTargetWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class MultiblockReprocessor
        extends AbstractCuboidMultiblockController<MultiblockReprocessor>
        implements IMultiblockMachine, IActivableMachine, ISyncableEntity, IDebuggable {

    public static final int TICKS = 40;
    public static final int TICK_ENERGY_COST = 25; // 25 FE per processed tick
    public static final int INTERNAL_HEIGHT = 5;

    public static final int FLUID_CAPACITY = INTERNAL_HEIGHT * FluidAttributes.BUCKET_VOLUME;
    public static final int ENERGY_CAPACITY = INTERNAL_HEIGHT * 1000;

    public MultiblockReprocessor(final Level world) {

        super(world);

        this._outputInventory = new ItemStackHandler(1);
        this._wasteInventory = new ItemStackHolder(1).setOnLoadListener(this::setIngredientsChanged).setOnContentsChangedListener(this::onInventoryChanged);
        this._fluidTank = new FluidTank(FLUID_CAPACITY).setOnLoadListener(this::setIngredientsChanged).setOnContentsChangedListener(this::onInventoryChanged);
        this._energyBuffer = new EnergyBuffer(EnergySystem.ForgeEnergy, ENERGY_CAPACITY, 1000);

        this._outputItemHandler = ItemHandlerPolicyWrapper.outputOnly(this._outputInventory);
        this._inputItemHandler = ItemHandlerPolicyWrapper.twoWay(this._wasteInventory, (slot, stack) -> this.isValidIngredient(stack));
        this._inputFluidHandler = FluidHandlerPolicyWrapper.inputOnly(this._fluidTank, this::isValidIngredient);
        this._energyInputHandler = WideEnergyStoragePolicyWrapper.inputOnly(this._energyBuffer);

        this._outputTarget = RecipeResultTargetWrapper.wrap(this._outputInventory, 0);
        this._wasteIngredientSource = RecipeIngredientSourceWrapper.wrap(this._wasteInventory, 0);
        this._fluidIngredientSource = RecipeIngredientSourceWrapper.wrap(this._fluidTank, 0);

        this._recipeHolder = RecipeHolder.builder(this::heldRecipeFactory, recipe -> TICKS)
                .onCanProcess(this::canProcess)
                .onHasIngredientsChanged(this::hasIngredientsChanged)
                .onRecipeTickProcessed(tick -> this._energyBuffer.extractEnergy(EnergySystem.ForgeEnergy, TICK_ENERGY_COST, false))
//                .onRecipeProcessed(this::sendUpdates)
                .onRecipeChanged(this::onRecipeChanged)
                .build();

        this._ticker = TickerListener.singleListener(5, this::sendUpdates);
        this._interiorInvisible = false;
        this._ingredientsChanged = false;
    }

    public boolean isValidIngredient(final ItemStack stack) {
        return Content.Recipes.REPROCESSOR_RECIPE_TYPE.findFirst(recipe -> recipe.matchIgnoreAmount(stack)).isPresent();
    }

    public boolean isValidIngredient(final FluidStack stack) {
        return Content.Recipes.REPROCESSOR_RECIPE_TYPE.findFirst(recipe -> recipe.matchIgnoreAmount(stack)).isPresent();
    }

    public IItemHandlerModifiable getItemHandler(final IoDirection direction) {
        return direction.isInput() ? this._inputItemHandler : this._outputItemHandler;
    }

    public IFluidHandler getFluidHandler() {
        return this._inputFluidHandler;
    }

    public IWideEnergyStorage getEnergyStorage() {
        return this._energyInputHandler;
    }

    //region GUI helpers

    public double getEnergyStored() {
        return this._energyBuffer.getEnergyStored();
    }

    public double getEnergyStoredPercentage() {
        return this._energyBuffer.getEnergyStoredPercentage();
    }

    public int getFluidStored() {
        return this._fluidTank.getFluidAmount();
    }

    public void voidFluid() {
        this._fluidTank.setContent(FluidStack.EMPTY);
    }

    public double getFluidStoredPercentage() {
        return this._fluidTank.getFluidAmountPercentage();
    }

    public double getRecipeProgress() {
        return this._recipeHolder.getHeldRecipe().map(IHeldRecipe::getProgress).orElse(0.0d);
    }

    //endregion
    //region Rendering

    public boolean isInteriorInvisible() {
        return this._interiorInvisible;
    }

    public boolean isInteriorVisible() {
        return !this._interiorInvisible;
    }

    protected void setInteriorInvisible(final boolean visible) {
        this._interiorInvisible = visible;
    }

    //endregion
    //region IActivableMachine

    /**
     * @return true if the machine is active, false otherwise
     */
    @Override
    public boolean isMachineActive() {
        return this._active;
    }

    /**
     * Change the state of the machine
     *
     * @param active if true, activate the machine; if false, deactivate it
     */
    @Override
    public void setMachineActive(boolean active) {

        if (this.isMachineActive() == active) {
            return;
        }

        this._active = active;

        if (active) {
            this.getConnectedParts().forEach(IMultiblockPart::onMachineActivated);
        } else {
            this.getConnectedParts().forEach(IMultiblockPart::onMachineDeactivated);
        }

        this.callOnLogicalServer(this::markReferenceCoordForUpdate);
    }

    //endregion
    //region ISyncableEntity

    /**
     * Sync the entity data from the given NBT compound
     *
     * @param data       the data
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public void syncDataFrom(CompoundTag data, SyncReason syncReason) {

        super.syncDataFrom(data, syncReason);

        this.syncBooleanElementFrom("active", data, b -> this._active = b);
        this.syncDataElementFrom("out", data, this._outputInventory);
        this.syncDataElementFrom("waste", data, this._wasteInventory);
        this.syncChildDataEntityFrom(this._fluidTank, "fluid", data, syncReason);
        this.syncChildDataEntityFrom(this._energyBuffer, "energy", data, syncReason);
        this._recipeHolder.refresh();
        this.syncChildDataEntityFrom(this._recipeHolder, "recipe", data, syncReason);
    }

    /**
     * Sync the entity data to the given NBT compound
     *
     * @param data       the data
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public CompoundTag syncDataTo(CompoundTag data, SyncReason syncReason) {

        super.syncDataTo(data, syncReason);

        this.syncBooleanElementTo("active", data, this.isMachineActive());
        this.syncDataElementTo("out", data, this._outputInventory);
        this.syncDataElementTo("waste", data, this._wasteInventory);
        this.syncChildDataEntityTo(this._fluidTank, "fluid", data, syncReason);
        this.syncChildDataEntityTo(this._energyBuffer, "energy", data, syncReason);
        this.syncChildDataEntityTo(this._recipeHolder, "recipe", data, syncReason);

        return data;
    }

    //endregion
    //region IDebuggable

    @Override
    public void getDebugMessages(final LogicalSide side, final IDebugMessages messages) {

        if (!this.isAssembled()) {
            return;
        }

        messages.addUnlocalized("Input waste: %s", this._wasteInventory.getStackInSlot(0).toString());
        messages.add(side, this._fluidTank, "Input Fluid:");
        messages.addUnlocalized("Output: %s", this._outputInventory.getStackInSlot(0));
        messages.add(side, this._energyBuffer, "Energy buffer:");
        messages.addUnlocalized("Current tick %d", this._recipeHolder.getHeldRecipe().map(IHeldRecipe::getCurrentTick).orElse(-1));
    }

    //endregion
    //region AbstractCuboidMultiblockController

    /**
     * The server-side update loop! Use this similarly to a TileEntity's update loop.
     * You do not need to call your superclass' update() if you're directly
     * derived from AbstractMultiblockController. This is a callback.
     * Note that this will only be called when the machine is assembled.
     *
     * @return True if the multiblock should save data, i.e. its internal game state has changed. False otherwise.
     */
    @Override
    protected boolean updateServer() {

        final ProfilerFiller profiler = this.getWorld().getProfiler();
        boolean updated = false;

        profiler.push("Extreme Reactors|Reprocessor update"); // main section

        //////////////////////////////////////////////////////////////////////////////
        // PROCESSING
        //////////////////////////////////////////////////////////////////////////////

        profiler.push("Process");

        if (this.isMachineActive()) {
            updated = this._recipeHolder.getCurrentRecipe().map(IHeldRecipe::processRecipe).orElse(false);
        }

        //////////////////////////////////////////////////////////////////////////////
        // SEND CLIENT UPDATES
        //////////////////////////////////////////////////////////////////////////////

        profiler.popPush("Updates");
        this._ticker.tick();

        profiler.pop();
        profiler.pop(); // main section

        return updated;
    }

    /**
     * Client-side update loop. Generally, this shouldn't do anything, but if you want
     * to do some interpolation or something, do it here.
     */
    @Override
    protected void updateClient() {
        if (null != this._collector) {
            this._collector.onClientTick();
        }
    }

    /**
     * @return True if the machine is "whole" and should be assembled. False otherwise.
     */
    @Override
    protected boolean isMachineWhole(IMultiblockValidator validatorCallback) {

        if (!super.isMachineWhole(validatorCallback)) {
            return false;
        }

        if (1 != this.getPartsCount(p -> p instanceof ReprocessorControllerEntity)) {

            validatorCallback.setLastError("multiblock.validation.reprocessor.missing_controller");
            return false;
        }

        if (1 != this.getPartsCount(p -> p instanceof ReprocessorFluidPortEntity)) {

            validatorCallback.setLastError("multiblock.validation.reprocessor.missing_fluidinjector");
            return false;
        }

        if (1 != this.getPartsCount(p -> p instanceof ReprocessorAccessPortEntity &&
                ((ReprocessorAccessPortEntity)p).getDirection().isInput())) {

            validatorCallback.setLastError("multiblock.validation.reprocessor.missing_wasteinjector");
            return false;
        }

        if (1 != this.getPartsCount(p -> p instanceof ReprocessorAccessPortEntity &&
                ((ReprocessorAccessPortEntity)p).getDirection().isOutput())) {

            validatorCallback.setLastError("multiblock.validation.reprocessor.missing_outputport");
            return false;
        }

        if (1 != this.getPartsCount(p -> p instanceof ReprocessorPowerPortEntity)) {

            validatorCallback.setLastError("multiblock.validation.reprocessor.missing_powerport");
            return false;
        }

        final List<IMultiblockPart<MultiblockReprocessor>> collectors =
                this.getConnectedParts(p -> p instanceof ReprocessorCollectorEntity).collect(Collectors.toList());

        if (1 != collectors.size()) {

            validatorCallback.setLastError("multiblock.validation.reprocessor.missing_collector");
            return false;
        }

        return true;
    }

    /**
     * Called when a new part is added to the machine. Good time to register things into lists.
     *
     * @param newPart The part being added.
     */
    @Override
    protected void onPartAdded(IMultiblockPart<MultiblockReprocessor> newPart) {
    }

    /**
     * Called when a part is removed from the machine. Good time to clean up lists.
     *
     * @param oldPart The part being removed.
     */
    @Override
    protected void onPartRemoved(IMultiblockPart<MultiblockReprocessor> oldPart) {
    }

    /**
     * Called when a machine is assembled from a disassembled state.
     */
    @Override
    protected void onMachineAssembled() {

        // get the Collector
        this._collector = this.getConnectedParts(p -> p instanceof ReprocessorCollectorEntity)
                .map(p -> (ReprocessorCollectorEntity)p)
                .findAny()
                .orElse(null);

        // interior visible?
        this.setInteriorInvisible(!this.isAnyPartConnected(part -> part instanceof ReprocessorGlassEntity));

        // Re-render the multiblock
        this.callOnLogicalSide(
                this::markReferenceCoordForUpdate,
                this::markMultiblockForRenderUpdate
        );

        super.onMachineAssembled();
    }

    /**
     * Called when a machine is restored to the assembled state from a paused state.
     */
    @Override
    protected void onMachineRestored() {
        this.onMachineAssembled();
    }

    /**
     * Called when a machine is paused from an assembled state
     * This generally only happens due to chunk-loads and other "system" events.
     */
    @Override
    protected void onMachinePaused() {
        this._collector = null;
    }

    /**
     * Called when a machine is disassembled from an assembled state.
     * This happens due to user or in-game actions (e.g. explosions)
     */
    @Override
    protected void onMachineDisassembled() {

        // do not call setMachineActive() here
        this._active = false;

        this._collector = null;

        this.markMultiblockForRenderUpdate();
    }

    /**
     * Helper method so we don't check for a whole machine until we have enough parts
     * to actually assemble it. This isn't as simple as xmax*ymax*zmax for non-cubic machines
     * or for machines with hollow/complex interiors.
     *
     * @return The minimum number of parts connected to the machine for it to be assembled.
     */
    @Override
    protected int getMinimumNumberOfPartsForAssembledMachine() {
        return 18 + 8 * INTERNAL_HEIGHT;
    }

    /**
     * Returns the maximum X dimension size of the machine, or -1 (DIMENSION_UNBOUNDED) to disable
     * dimension checking in X. (This is not recommended.)
     *
     * @return The maximum X dimension size of the machine, or -1
     */
    @Override
    protected int getMaximumXSize() {
        return 3;
    }

    /**
     * Returns the minimum X dimension size of the machine. Must be at least 1, because nothing else makes sense.
     *
     * @return The minimum X dimension size of the machine
     */
    @Override
    protected int getMinimumXSize() {
        return this.getMaximumXSize();
    }

    /**
     * Returns the maximum Z dimension size of the machine, or -1 (DIMENSION_UNBOUNDED) to disable
     * dimension checking in X. (This is not recommended.)
     *
     * @return The maximum Z dimension size of the machine, or -1
     */
    @Override
    protected int getMaximumZSize() {
        return 3;
    }

    /**
     * Returns the minimum Z dimension size of the machine. Must be at least 1, because nothing else makes sense.
     *
     * @return The minimum Z dimension size of the machine
     */
    @Override
    protected int getMinimumZSize() {
        return this.getMaximumZSize();
    }

    /**
     * Returns the maximum Y dimension size of the machine, or -1 (DIMENSION_UNBOUNDED) to disable
     * dimension checking in X. (This is not recommended.)
     *
     * @return The maximum Y dimension size of the machine, or -1
     */
    @Override
    protected int getMaximumYSize() {
        return 2 + INTERNAL_HEIGHT;
    }

    /**
     * Returns the minimum Y dimension size of the machine. Must be at least 1, because nothing else makes sense.
     *
     * @return The minimum Y dimension size of the machine
     */
    @Override
    protected int getMinimumYSize() {
        return this.getMaximumYSize();
    }

    /**
     * Callback. Called after this controller assimilates all the blocks
     * from another controller.
     * Use this to absorb that controller's game data.
     *
     * @param assimilated The controller whose uniqueness was added to our own.
     */
    @Override
    protected void onAssimilate(IMultiblockController<MultiblockReprocessor> assimilated) {

        if (!(assimilated instanceof MultiblockReprocessor)) {

            Log.LOGGER.warn(Log.REPROCESSOR, "[{}] Reprocessor @ {} is attempting to assimilate a non-Reprocessor machine! That machine's data will be lost!",
                    CodeHelper.getWorldSideName(this.getWorld()), this.getReferenceCoord());
            return;
        }

        final MultiblockReprocessor otherReprocessor = (MultiblockReprocessor)assimilated;

        ItemHandlerHelper.insertItem(this._outputInventory, otherReprocessor._outputInventory.getStackInSlot(0), false);
        ItemHandlerHelper.insertItem(this._wasteInventory, otherReprocessor._wasteInventory.getStackInSlot(0), false);
        FluidUtil.tryFluidTransfer(this._fluidTank, otherReprocessor._fluidTank, Integer.MAX_VALUE, true);
        EnergyHelper.transferEnergy(this._energyBuffer, otherReprocessor._energyBuffer, Double.MAX_VALUE, OperationMode.Execute);
    }

    /**
     * Callback. Called after this controller is assimilated into another controller.
     * All blocks have been stripped out of this object and handed over to the
     * other controller.
     * This is intended primarily for cleanup.
     *
     * @param assimilator The controller which has assimilated this controller.
     */
    @Override
    protected void onAssimilated(IMultiblockController<MultiblockReprocessor> assimilator) {
    }

    @Override
    protected boolean isBlockGoodForFrame(Level world, int x, int y, int z, IMultiblockValidator validatorCallback) {
        return notifyInvalidBlock(world, x, y, z, validatorCallback);
    }

    @Override
    protected boolean isBlockGoodForTop(Level world, int x, int y, int z, IMultiblockValidator validatorCallback) {
        return notifyInvalidBlock(world, x, y, z, validatorCallback);
    }

    @Override
    protected boolean isBlockGoodForBottom(Level world, int x, int y, int z, IMultiblockValidator validatorCallback) {
        return notifyInvalidBlock(world, x, y, z, validatorCallback);
    }

    @Override
    protected boolean isBlockGoodForSides(Level world, int x, int y, int z, IMultiblockValidator validatorCallback) {
        return notifyInvalidBlock(world, x, y, z, validatorCallback);
    }

    @Override
    protected boolean isBlockGoodForInterior(Level world, int x, int y, int z, IMultiblockValidator validatorCallback) {

        if (world.getBlockState(new BlockPos(x, y, z)).isAir()) {
            return true;
        }

        notifyInvalidBlock(world, x, y, z, validatorCallback);
        return false;
    }

    //endregion
    //region internals
    //region recipe helpers

    @Nullable
    private ReprocessorHeldRecipe heldRecipeFactory(final IRecipeHolder<ReprocessorRecipe> holder) {

        this._ingredientsChanged = false;

        return Content.Recipes.REPROCESSOR_RECIPE_TYPE
                .findFirst(recipe -> recipe.test(this._wasteInventory.getStackInSlot(0), this._fluidTank.getFluidInTank(0)))
                .map(recipe -> new ReprocessorHeldRecipe(recipe, holder, this._wasteIngredientSource,
                        this._fluidIngredientSource, this._outputTarget))
                .orElse(null);
    }

    private boolean canProcess(final ReprocessorRecipe recipe) {
        return this.isMachineActive() &&
                this._energyBuffer.getEnergyStored() >= TICK_ENERGY_COST &&
                this._outputTarget.countStorableResults(recipe.getResult()) > 0;
    }

    private boolean hasIngredientsChanged() {

        final boolean v = this._ingredientsChanged;

        this._ingredientsChanged = false;
        return v;
    }

    private void setIngredientsChanged() {

        if (this.calledByLogicalServer()) {
            this._ingredientsChanged = true;
        }
    }

    private void onInventoryChanged(final IStackHolder.ChangeType changeType, final int slot) {

        if (changeType.fullChange()) {
            this.setIngredientsChanged();
        }
    }

    //endregion

    private static boolean notifyInvalidBlock(final Level world, final int x, final int y, final int z,
                                              final IMultiblockValidator validatorCallback) {

        final BlockPos position = new BlockPos(x, y, z);

        validatorCallback.setLastError(position, "multiblock.validation.reprocessor.invalid_block",
                ModBlock.getNameForTranslation(world.getBlockState(position).getBlock()));
        return false;
    }

    private void onRecipeChanged(ReprocessorRecipe heldRecipe) {

        if (null != this._collector) {
//            this._collector.onRecipeChanged(this._recipeHolder.getHeldRecipe().map(IHeldRecipe::getRecipe).orElse(null));
            this._collector.onRecipeChanged(heldRecipe);
        }
    }

    private final ItemStackHandler _outputInventory;
    private final ItemStackHolder _wasteInventory;
    private final FluidTank _fluidTank;
    private final EnergyBuffer _energyBuffer;

    private final IRecipeIngredientSource<ItemStack> _wasteIngredientSource;
    private final IRecipeIngredientSource<FluidStack> _fluidIngredientSource;
    private final IRecipeResultTarget<ItemStackRecipeResult> _outputTarget;
    private final RecipeHolder<ReprocessorRecipe> _recipeHolder;
    private boolean _ingredientsChanged;

    private ReprocessorCollectorEntity _collector;

    private final IItemHandlerModifiable _outputItemHandler;
    private final IItemHandlerModifiable _inputItemHandler;
    private final IFluidHandler _inputFluidHandler;
    private final IWideEnergyStorage _energyInputHandler;

    private final TickerListener _ticker;
    private boolean _active;

    private boolean _interiorInvisible;

    //endregion
}
