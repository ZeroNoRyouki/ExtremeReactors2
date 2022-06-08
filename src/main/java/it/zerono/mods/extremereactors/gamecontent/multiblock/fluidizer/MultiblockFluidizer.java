/*
 *
 * MultiblockFluidizer.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.extremereactors.config.Config;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part.*;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerFluidMixingRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerSolidMixingRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerSolidRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.IFluidizerRecipe;
import it.zerono.mods.zerocore.lib.*;
import it.zerono.mods.zerocore.lib.block.ModBlock;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import it.zerono.mods.zerocore.lib.data.stack.OperationMode;
import it.zerono.mods.zerocore.lib.energy.EnergyHelper;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.energy.IWideEnergyStorage2;
import it.zerono.mods.zerocore.lib.energy.WideEnergyBuffer;
import it.zerono.mods.zerocore.lib.energy.handler.WideEnergyStoragePolicyWrapper;
import it.zerono.mods.zerocore.lib.fluid.FluidTank;
import it.zerono.mods.zerocore.lib.fluid.handler.FluidHandlerPolicyWrapper;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockMachine;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.storage.collection.IPartCollection;
import it.zerono.mods.zerocore.lib.multiblock.storage.collection.PartCollection;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.lib.recipe.holder.IHeldRecipe;
import it.zerono.mods.zerocore.lib.recipe.holder.IRecipeHolder;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredientSource;
import it.zerono.mods.zerocore.lib.recipe.result.FluidStackRecipeResult;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResultTarget;
import it.zerono.mods.zerocore.lib.recipe.result.RecipeResultTargetWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.List;

public class MultiblockFluidizer
        extends AbstractCuboidMultiblockController<MultiblockFluidizer>
        implements IMultiblockMachine, IActivableMachine, ISyncableEntity, IDebuggable, IFluidizerRecipeHolder.Callbacks {

    public static final WideAmount ENERGY_CAPACITY = WideAmount.asImmutable(50_000);

    public MultiblockFluidizer(final World world) {

        super(world);

        this._outputTank = new FluidTank(0);
        this._outputFluidHandler = FluidHandlerPolicyWrapper.outputOnly(this._outputTank);
        this._energyBuffer = new WideEnergyBuffer(EnergySystem.ForgeEnergy, ENERGY_CAPACITY, WideAmount.asImmutable(1000));
        this._energyInputHandler = WideEnergyStoragePolicyWrapper.inputOnly(this._energyBuffer);

        this._solidInjectors = new PartCollection<>(2, p -> p instanceof FluidizerSolidInjectorEntity);
        this._fluidInjectors = new PartCollection<>(2, p -> p instanceof FluidizerFluidInjectorEntity);

        this._solidSources = new ObjectArrayList<>(2);
        this._fluidSources = new ObjectArrayList<>(2);
        this._fluidTarget = RecipeResultTargetWrapper.wrap(this._outputTank);

        this._ticker = TickerListener.singleListener(10, this::sendUpdates);
        this._interiorInvisible = this._ingredientsChanged = this._active = false;
    }

    public boolean isValidIngredient(final ItemStack stack) {
        return null != this._recipeHolder && this._recipeHolder.isValidIngredient(stack);
    }

    public boolean isValidIngredient(final FluidStack stack) {
        return null != this._recipeHolder && this._recipeHolder.isValidIngredient(stack);
    }

    public IFluidHandler getFluidHandler() {
        return this._outputFluidHandler;
    }

    public IWideEnergyStorage2 getEnergyStorage() {
        return this._energyInputHandler;
    }

    public List<FluidizerSolidInjectorEntity> getSolidInjectors() {
        return this._solidInjectors.asList();
    }

    public List<FluidizerFluidInjectorEntity> getFluidInjectors() {
        return this._fluidInjectors.asList();
    }

    public IFluidizerRecipe.Type getRecipeType() {
        return null != this._recipeHolder ? this._recipeHolder.getRecipeType() : IFluidizerRecipe.Type.Invalid;
    }

    public double getRecipeProgress() {
        return null != this._recipeHolder ? this._recipeHolder.getProgress() : 0.0;
    }

    public void onIngredientsChanged() {

        if (this.calledByLogicalServer()) {
            this._ingredientsChanged = true;
        }
    }

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
    //region IFluidizerRecipeHolder.Callbacks

    @Override
    public boolean canProcessRecipe(final IFluidizerRecipe recipe) {
        return this.isMachineActive() &&
                this._energyBuffer.getEnergyStored().intValue() >= Config.COMMON.fluidizer.energyPerRecipeTick.get() &&
                this._fluidTarget.countStorableResults(recipe.getResult()) > 0;
    }

    @Override
    public boolean hasIngredientsChanged() {

        final boolean v = this._ingredientsChanged;

        this._ingredientsChanged = false;
        return v;
    }

    @Override
    public void onRecipeTickProcessed(final int currentTick) {
        this._energyBuffer.extractEnergy(EnergySystem.ForgeEnergy,
                WideAmount.from(Config.COMMON.fluidizer.energyPerRecipeTick.get() * (long)this._recipeHolder.getEnergyUsageMultiplier()),
                OperationMode.Execute);
    }

    @Override
    public void onRecipeChanged(final IFluidizerRecipe recipe) {
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
    public void syncDataFrom(CompoundNBT data, SyncReason syncReason) {

        super.syncDataFrom(data, syncReason);

        this.syncBooleanElementFrom("active", data, b -> this._active = b);
        this.syncChildDataEntityFrom(this._outputTank, "out", data, syncReason);
        this.syncChildDataEntityFrom(this._energyBuffer, "energy", data, syncReason);

        if (null != this._recipeHolder) {

            this._recipeHolder.refresh();
            this.syncChildDataEntityFrom(this._recipeHolder, "recipe", data, syncReason);
        }
    }

    /**
     * Sync the entity data to the given NBT compound
     *
     * @param data       the data
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public CompoundNBT syncDataTo(CompoundNBT data, SyncReason syncReason) {

        super.syncDataTo(data, syncReason);

        this.syncBooleanElementTo("active", data, this.isMachineActive());
        this.syncChildDataEntityTo(this._outputTank, "out", data, syncReason);
        this.syncChildDataEntityTo(this._energyBuffer, "energy", data, syncReason);

        if (null != this._recipeHolder) {
            this.syncChildDataEntityTo(this._recipeHolder, "recipe", data, syncReason);
        }

        return data;
    }

    //endregion
    //region IDebuggable

    @Override
    public void getDebugMessages(final LogicalSide side, final IDebugMessages messages) {

        if (!this.isAssembled()) {
            return;
        }

        messages.add(side, this._energyBuffer, "Energy buffer:");
        messages.add(side, this._outputTank, "Output tank:");
        messages.addUnlocalized("Current tick %d", this._recipeHolder.getCurrentTick());
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

        final IProfiler profiler = this.getWorld().getProfiler();
        boolean updated = false;

        profiler.push("Extreme Reactors|Fluidizer update"); // main section

        //////////////////////////////////////////////////////////////////////////////
        // PROCESSING
        //////////////////////////////////////////////////////////////////////////////

        profiler.push("Process");

        if (this.isMachineActive()) {
            updated = this._recipeHolder.processRecipe();
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

    @Override
    protected void updateClient() {
    }

    /**
     * @return True if the machine is "whole" and should be assembled. False otherwise.
     */
    @Override
    protected boolean isMachineWhole(final IMultiblockValidator validatorCallback) {

        if (!super.isMachineWhole(validatorCallback)) {
            return false;
        }

        if (1 != this.getPartsCount(p -> p instanceof FluidizerControllerEntity)) {

            validatorCallback.setLastError("multiblock.validation.fluidizer.missing_controller");
            return false;
        }

        final int solidInjectors = this.getPartsCount(p -> p instanceof FluidizerSolidInjectorEntity);
        final int fluidInjectors = this.getPartsCount(p -> p instanceof FluidizerFluidInjectorEntity);

        if ((0 == solidInjectors && fluidInjectors < 2) || (solidInjectors > 0 && fluidInjectors > 0) ||
                (solidInjectors > 2) || (fluidInjectors > 2)) {

            validatorCallback.setLastError("multiblock.validation.fluidizer.invalid_injectors");
            return false;
        }

        if (this.getPartsCount(p -> p instanceof FluidizerOutputPortEntity) < 1) {

            validatorCallback.setLastError("multiblock.validation.fluidizer.missing_outputport");
            return false;
        }

        if (this.getPartsCount(p -> p instanceof FluidizerPowerPortEntity) < 1) {

            validatorCallback.setLastError("multiblock.validation.fluidizer.missing_powerport");
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
    protected void onPartAdded(final IMultiblockPart<MultiblockFluidizer> newPart) {

        if (this._solidInjectors.test(newPart)) {
            this._solidInjectors.add(newPart);
        } else if (this._fluidInjectors.test(newPart)) {
            this._fluidInjectors.add(newPart);
        }
    }

    /**
     * Called when a part is removed from the machine. Good time to clean up lists.
     *
     * @param oldPart The part being removed.
     */
    @Override
    protected void onPartRemoved(final IMultiblockPart<MultiblockFluidizer> oldPart) {

        if (this._solidInjectors.test(oldPart)) {
            this._solidInjectors.remove(oldPart);
        } else if (this._fluidInjectors.test(oldPart)) {
            this._fluidInjectors.remove(oldPart);
        }
    }

    /**
     * Called when a machine is assembled from a disassembled state.
     */
    @Override
    protected void onMachineAssembled() {

        this._solidSources.clear();
        this._solidInjectors.stream()
                .map(FluidizerSolidInjectorEntity::asRecipeSource)
                .forEach(this._solidSources::add);

        this._fluidSources.clear();
        this._fluidInjectors.stream()
                .map(FluidizerFluidInjectorEntity::asRecipeSource)
                .forEach(this._fluidSources::add);

        // since we gattai up, we should have only 1 or 2 solid injectors or 2 fluid ones
        final int solidInjectors = this.getPartsCount(p -> p instanceof FluidizerSolidInjectorEntity);
        final int fluidInjectors = this.getPartsCount(p -> p instanceof FluidizerFluidInjectorEntity);

        if (1 == solidInjectors) {
            this._recipeHolder = FluidizerRecipeHolder.solid(this, this::solidRecipeFactory);
        } else if (2 == solidInjectors) {
            this._recipeHolder = FluidizerRecipeHolder.solidMixing(this, this::solidMixingRecipeFactory);
        } else if (2 == fluidInjectors) {
            this._recipeHolder = FluidizerRecipeHolder.fluidMixing(this, this::solidFluidMixingFactory);
        } else {
            throw new IllegalStateException("Invalid number of injectors");
        }

        // set output tank capacity
        this._outputTank.setCapacity(this.getBoundingBox().getInternalVolume() * 4000);

        // interior visible?
        this.setInteriorInvisible(!this.isAnyPartConnected(part -> part instanceof FluidizerGlassEntity));

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
    }

    /**
     * Called when a machine is disassembled from an assembled state.
     * This happens due to user or in-game actions (e.g. explosions)
     */
    @Override
    protected void onMachineDisassembled() {

        // do not call setMachineActive() here
        this._active = false;

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
        return 26;
    }

    /**
     * Returns the maximum X dimension size of the machine, or -1 (DIMENSION_UNBOUNDED) to disable
     * dimension checking in X. (This is not recommended.)
     *
     * @return The maximum X dimension size of the machine, or -1
     */
    @Override
    protected int getMaximumXSize() {
        return Config.COMMON.fluidizer.maxFluidizerSize.get();
    }

    /**
     * Returns the minimum X dimension size of the machine. Must be at least 1, because nothing else makes sense.
     *
     * @return The minimum X dimension size of the machine
     */
    @Override
    protected int getMinimumXSize() {
        return 3;
    }

    /**
     * Returns the maximum Z dimension size of the machine, or -1 (DIMENSION_UNBOUNDED) to disable
     * dimension checking in X. (This is not recommended.)
     *
     * @return The maximum Z dimension size of the machine, or -1
     */
    @Override
    protected int getMaximumZSize() {
        return Config.COMMON.fluidizer.maxFluidizerSize.get();
    }

    /**
     * Returns the minimum Z dimension size of the machine. Must be at least 1, because nothing else makes sense.
     *
     * @return The minimum Z dimension size of the machine
     */
    @Override
    protected int getMinimumZSize() {
        return 3;
    }

    /**
     * Returns the maximum Y dimension size of the machine, or -1 (DIMENSION_UNBOUNDED) to disable
     * dimension checking in X. (This is not recommended.)
     *
     * @return The maximum Y dimension size of the machine, or -1
     */
    @Override
    protected int getMaximumYSize() {
        return Config.COMMON.fluidizer.maxFluidizerHeight.get();
    }

    /**
     * Returns the minimum Y dimension size of the machine. Must be at least 1, because nothing else makes sense.
     *
     * @return The minimum Y dimension size of the machine
     */
    @Override
    protected int getMinimumYSize() {
        return 3;
    }

    /**
     * Callback. Called after this controller assimilates all the blocks
     * from another controller.
     * Use this to absorb that controller's game data.
     *
     * @param assimilated The controller whose uniqueness was added to our own.
     */
    @Override
    protected void onAssimilate(IMultiblockController<MultiblockFluidizer> assimilated) {

        if (!(assimilated instanceof MultiblockFluidizer)) {

            Log.LOGGER.warn(Log.FLUIDIZER, "[{}] Fluidizer @ {} is attempting to assimilate a non-Fluidizer machine! That machine's data will be lost!",
                    CodeHelper.getWorldSideName(this.getWorld()), this.getReferenceCoord());
            return;
        }

        final MultiblockFluidizer otherFluidizer = (MultiblockFluidizer)assimilated;

        FluidUtil.tryFluidTransfer(this._outputTank, otherFluidizer._outputTank, Integer.MAX_VALUE, true);
        EnergyHelper.transferEnergy(this._energyBuffer, otherFluidizer._energyBuffer, WideAmount.MAX_VALUE, OperationMode.Execute);
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
    protected void onAssimilated(IMultiblockController<MultiblockFluidizer> assimilator) {

        this._solidInjectors.clear();
        this._fluidInjectors.clear();
    }

    @Override
    protected boolean isBlockGoodForFrame(World world, int x, int y, int z, IMultiblockValidator validatorCallback) {
        return notifyInvalidBlock(world, x, y, z, validatorCallback);
    }

    @Override
    protected boolean isBlockGoodForTop(World world, int x, int y, int z, IMultiblockValidator validatorCallback) {
        return notifyInvalidBlock(world, x, y, z, validatorCallback);
    }

    @Override
    protected boolean isBlockGoodForBottom(World world, int x, int y, int z, IMultiblockValidator validatorCallback) {
        return notifyInvalidBlock(world, x, y, z, validatorCallback);
    }

    @Override
    protected boolean isBlockGoodForSides(World world, int x, int y, int z, IMultiblockValidator validatorCallback) {
        return notifyInvalidBlock(world, x, y, z, validatorCallback);
    }

    @Override
    protected boolean isBlockGoodForInterior(World world, int x, int y, int z, IMultiblockValidator validatorCallback) {

        if (world.getBlockState(new BlockPos(x, y, z)).isAir()) {
            return true;
        }

        notifyInvalidBlock(world, x, y, z, validatorCallback);
        return false;
    }

    //endregion
    //region internals

    private static boolean notifyInvalidBlock(final World world, final int x, final int y, final int z,
                                              final IMultiblockValidator validatorCallback) {

        final BlockPos position = new BlockPos(x, y, z);

        validatorCallback.setLastError(position, "multiblock.validation.fluidizer.invalid_block",
                ModBlock.getNameForTranslation(world.getBlockState(position).getBlock()));
        return false;
    }

    @Nullable
    private IHeldRecipe<FluidizerSolidRecipe> solidRecipeFactory(final IRecipeHolder<FluidizerSolidRecipe> holder) {

        this._ingredientsChanged = false;

        final IRecipeIngredientSource<ItemStack> source = this._solidSources.get(0);

        return Content.Recipes.FLUIDIZER_RECIPE_TYPE
                .findFirst(recipe -> FluidizerSolidRecipe.lookup(recipe, source))
                .map(recipe -> (FluidizerSolidRecipe)recipe)
                .map(recipe -> new FluidizerSolidRecipe.HeldRecipe(recipe, holder, source, this._fluidTarget))
                .orElse(null);
    }

    @Nullable
    private IHeldRecipe<FluidizerSolidMixingRecipe> solidMixingRecipeFactory(final IRecipeHolder<FluidizerSolidMixingRecipe> holder) {

        this._ingredientsChanged = false;

        final IRecipeIngredientSource<ItemStack> source1 = this._solidSources.get(0);
        final IRecipeIngredientSource<ItemStack> source2 = this._solidSources.get(1);

        return Content.Recipes.FLUIDIZER_RECIPE_TYPE
                .findFirst(recipe -> FluidizerSolidMixingRecipe.lookup(recipe, source1, source2))
                .map(recipe -> (FluidizerSolidMixingRecipe)recipe)
                .map(recipe -> new FluidizerSolidMixingRecipe.HeldRecipe(recipe, holder, source1, source2, this._fluidTarget))
                .orElse(null);
    }

    @Nullable
    private IHeldRecipe<FluidizerFluidMixingRecipe> solidFluidMixingFactory(final IRecipeHolder<FluidizerFluidMixingRecipe> holder) {

        this._ingredientsChanged = false;

        final IRecipeIngredientSource<FluidStack> source1 = this._fluidSources.get(0);
        final IRecipeIngredientSource<FluidStack> source2 = this._fluidSources.get(1);

        return Content.Recipes.FLUIDIZER_RECIPE_TYPE
                .findFirst(recipe -> FluidizerFluidMixingRecipe.lookup(recipe, source1, source2))
                .map(recipe -> (FluidizerFluidMixingRecipe)recipe)
                .map(recipe -> new FluidizerFluidMixingRecipe.HeldRecipe(recipe, holder, source1, source2, this._fluidTarget))
                .orElse(null);
    }

    private final FluidTank _outputTank;
    private final WideEnergyBuffer _energyBuffer;
    private final IFluidHandler _outputFluidHandler;
    private final IWideEnergyStorage2 _energyInputHandler;

    private final IPartCollection<MultiblockFluidizer, FluidizerSolidInjectorEntity> _solidInjectors;
    private final IPartCollection<MultiblockFluidizer, FluidizerFluidInjectorEntity> _fluidInjectors;

    private final ObjectList<IRecipeIngredientSource<ItemStack>> _solidSources;
    private final ObjectList<IRecipeIngredientSource<FluidStack>> _fluidSources;
    private final IRecipeResultTarget<FluidStackRecipeResult> _fluidTarget;
    private IFluidizerRecipeHolder _recipeHolder;
    private boolean _ingredientsChanged;

    private final TickerListener _ticker;
    private boolean _active;
    private boolean _interiorInvisible;

    //endregion
}
