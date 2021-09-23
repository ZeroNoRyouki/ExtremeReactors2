/*
 *
 * ReactorFluidAccessPortEntity.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part;

import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.extremereactors.api.IMapping;
import it.zerono.mods.extremereactors.api.reactor.Reactant;
import it.zerono.mods.extremereactors.api.reactor.ReactantMappingsRegistry;
import it.zerono.mods.extremereactors.api.reactor.ReactantType;
import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.IFuelSource;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactantHelper;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.DebuggableHelper;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.block.INeighborChangeListener;
import it.zerono.mods.zerocore.lib.block.TileCommandDispatcher;
import it.zerono.mods.zerocore.lib.data.IIoEntity;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.fluid.FluidTank;
import it.zerono.mods.zerocore.lib.fluid.handler.FluidHandlerForwarder;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ReactorFluidAccessPortEntity
        extends AbstractReactorEntity
        implements IFuelSource<FluidStack>, IIoEntity, INeighborChangeListener, INamedContainerProvider {

    public ReactorFluidAccessPortEntity() {

        super(Content.TileEntityTypes.REACTOR_FLUID_ACCESSPORT.get());
        this.setIoDirection(IoDirection.Input);
        this._fuelTank = new FluidTank(2 * ReactorFuelRodEntity.FUEL_CAPACITY_PER_FUEL_ROD)/*.setOnLoadListener(this::setIngredientsChanged).setOnContentsChangedListener(this::onInventoryChanged)*/;
        this._wasteTank = new FluidTank(2 * ReactorFuelRodEntity.FUEL_CAPACITY_PER_FUEL_ROD)/*.setOnLoadListener(this::setIngredientsChanged).setOnContentsChangedListener(this::onInventoryChanged)*/;
        this._fuelCapability = LazyOptional.of(this::createFuelCapability);
        this._wasteCapability = LazyOptional.of(this::createWasteCapability);

        this.setCommandDispatcher(TileCommandDispatcher.<ReactorFluidAccessPortEntity>builder()
                .addServerHandler(CommonConstants.COMMAND_SET_INPUT, tile -> tile.setIoDirection(IoDirection.Input))
                .addServerHandler(CommonConstants.COMMAND_SET_OUTPUT, tile -> tile.setIoDirection(IoDirection.Output))
                .addServerHandler(CommonConstants.COMMAND_DUMP_FUEL, ReactorFluidAccessPortEntity::handleCommandEjectFuel)
                .addServerHandler(CommonConstants.COMMAND_DUMP_WASTE, ReactorFluidAccessPortEntity::handleCommandEjectWaste)
                .build(this));
    }

    public IFluidHandler getFluidStackHandler(ReactantType type) {
        return type.isFuel() ? this._fuelTank : this._wasteTank;
    }

    /**
     * Called when stuff has been placed in the access port
     */
    public void onItemsReceived() {

        this.distributeItems();
        this.markChunkDirty();
    }

    //region client render support

    @Override
    protected int getUpdatedModelVariantIndex() {

        final int connectedOffset = this.isMachineAssembled() && this.getNeighborCapability().isPresent() ? 1 : 0;

        return this.getIoDirection().isInput() ? 2 + connectedOffset : 0 + connectedOffset;
    }

    //endregion
    //region IFuelSource<FluidStack>

    /**
     * Return the fuel source items currently available in the fuel source
     *
     * @return the solid fuel source items
     */
    @Override
    public FluidStack getFuelStack() {
        return this.getStack(ReactantType.Fuel);
    }

    /**
     * Consume source items up to the amount indicated by the provided ItemStack
     *
     * @param sourceToConsume the source items to consume
     * @return the source items actually consumed
     */
    @Override
    public FluidStack consumeFuelSource(final FluidStack sourceToConsume) {

        final FluidStack sourceStack = this.getStack(ReactantType.Fuel);

        if (sourceStack.isEmpty() || sourceToConsume.isEmpty()) {
            return FluidStack.EMPTY;
        }

        return this._fuelTank.drain(sourceToConsume, IFluidHandler.FluidAction.EXECUTE);
    }

    /**
     * Try to emit a given amount of reactant as a solid item.
     * Will either match the item type already present, or will select whatever type allows the most reactant to be ejected right now
     *
     * @param reactant Type of reactant to emit.
     * @param amount
     * @return the amount of Reactant consumed in the operation
     */
    @Override
    public int emitReactant(final Reactant reactant, final int amount) {

        // 1 mb of fluid fuel is 1 mb of fuel.

        final int maxOutputAmount = this._wasteTank.getCapacity();
        FluidStack outputStack;

        if (amount <= 0 || maxOutputAmount <= 0) {
            return 0;
        }

        outputStack = this.getStack(ReactantType.Waste);

        if (!outputStack.isEmpty() && outputStack.getAmount() >= maxOutputAmount) {
            // Already full?
            return 0;
        }

        // If we have an output fluid, try to produce more of it
        if (!outputStack.isEmpty()) {

            // Find matching mapping
            final IMapping<ResourceLocation, Reactant> mapping = ReactantMappingsRegistry.getFromFluid(outputStack).orElse(null);

            if (null == mapping || !reactant.equals(mapping.getProduct())) {
                // The fluid in the output tank is not compatible with the Reactant
                return 0;
            }

            final int amountToProduce = Math.min(amount, maxOutputAmount - outputStack.getAmount());

            if (amountToProduce <= 0) {
                return 0;
            }

            outputStack.grow(amountToProduce);
            this.onItemsReceived();

            return amountToProduce;
        }

        /*
        We have no fluid in the output tank. We need to figure out candidate mappings.
        Below here, we're using the reactant >> source mappings. This means that source == reactant, and product == fluid.

        Since there is a 1:1 ratio between fluid fuels/wastes and reactants, there is really no "best" mapping.
        Pick the first one available.
        */
        final IMapping<Reactant, ResourceLocation> bestMapping = ReactantMappingsRegistry.getToFluid(reactant)
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0))
                .orElse(null);

        if (null == bestMapping) {

            Log.LOGGER.warn(Log.REACTOR, "There are no mapped fluid types for reactant {}. Nothing to emit here.", reactant);
            return 0;
        }

        outputStack = ReactantMappingsRegistry.getFluidStackFrom(bestMapping, Math.min(amount, maxOutputAmount));

        if (outputStack.isEmpty()) {

            Log.LOGGER.warn(Log.REACTOR, "Can't create a stack from tag {}. Nothing to emit here.", bestMapping.getProduct());
            return 0;
        }

        final int filled = this._wasteTank.fill(outputStack, IFluidHandler.FluidAction.EXECUTE);

        this.onItemsReceived();

        return filled;
    }

    //endregion
    //region INamedContainerProvider

    /**
     * Create the SERVER-side container for this TileEntity
     * @param windowId  the window id
     * @param inventory the player inventory
     * @param player    the player
     * @return the container to use on the server
     */
    @Nullable
    @Override
    public Container createMenu(final int windowId, final PlayerInventory inventory, final PlayerEntity player) {
        return ModTileContainer.empty(Content.ContainerTypes.REACTOR_FLUID_ACCESSPORT.get(), windowId, this, (ServerPlayerEntity)player);
    }

    @Override
    public ITextComponent getDisplayName() {
        return super.getPartDisplayName();
    }

    //endregion
    //region IIoEntity

    @Override
    public IoDirection getIoDirection() {
        return this._direction;
    }

    @Override
    public void setIoDirection(IoDirection direction) {

        if (this.getIoDirection() == direction) {
            return;
        }

        this._direction = direction;
        this.notifyBlockUpdate();

        this.callOnLogicalSide(
                () -> {
                    this.notifyOutwardNeighborsOfStateChange();
                    this.distributeItems();
                    this.setChanged();
                },
                this::markForRenderUpdate
        );

        this.notifyNeighborsOfTileChange();
    }

    @Override
    public void onBlockReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        //TODO fluidizer
        /*
        ItemHelper.inventoryDropItems(this.getFluidStackHandler(ReactantType.Fuel), world, pos);
        ItemHelper.inventoryDropItems(this.getFluidStackHandler(ReactantType.Waste), world, pos);
        FluidUtil.tryPlaceFluid
                */
    }

    //endregion
    //region INeighborChangeListener

    /**
     * Called when a neighboring Block on a side of this TileEntity changes
     *
     * @param state            the BlockState of this TileEntity block
     * @param neighborPosition position of neighbor
     */
    @Override
    public void onNeighborBlockChanged(BlockState state, BlockPos neighborPosition, boolean isMoving) {
        this.requestClientRenderUpdate();
    }

    /**
     * Called when a neighboring TileEntity on a side of this TileEntity changes, is created or is destroyed
     *
     * @param state            the BlockState of this TileEntity block
     * @param neighborPosition position of neighbor
     */
    @Override
    public void onNeighborTileChanged(BlockState state, BlockPos neighborPosition) {
        this.requestClientRenderUpdate();
    }

    //endregion
    //region ISyncableEntity

    @Override
    public void syncDataFrom(CompoundNBT data, SyncReason syncReason) {

        super.syncDataFrom(data, syncReason);
        this.setIoDirection(IoDirection.read(data, "iodir", IoDirection.Input));

        if (syncReason.isFullSync()) {

            this.syncChildDataEntityFrom(this._fuelTank, "invin", data, syncReason);
            this.syncChildDataEntityFrom(this._wasteTank, "invout", data, syncReason);
        }
    }

    @Override
    public CompoundNBT syncDataTo(CompoundNBT data, SyncReason syncReason) {

        super.syncDataTo(data, syncReason);
        IoDirection.write(data, "iodir", this.getIoDirection());

        if (syncReason.isFullSync()) {

            this.syncChildDataEntityTo(this._fuelTank, "invin", data, syncReason);
            this.syncChildDataEntityTo(this._wasteTank, "invout", data, syncReason);
        }

        return data;
    }

    //endregion
    //region IDebuggable

    @Override
    public void getDebugMessages(final LogicalSide side, final IDebugMessages messages) {

        super.getDebugMessages(side, messages);
        this.getIoDirection().getDebugMessages(side, messages);
        messages.add(this.getFluidStackHandler(ReactantType.Fuel), DebuggableHelper::getDebugMessagesFor, "Fuel");
        messages.add(this.getFluidStackHandler(ReactantType.Waste), DebuggableHelper::getDebugMessagesFor, "Waste");
    }

    //endregion
    //region AbstractModBlockEntity

    /**
     * Check if the tile entity has a GUI or not
     * Override in derived classes to return true if your tile entity got a GUI
     */
    @Override
    public boolean canOpenGui(World world, BlockPos position, BlockState state) {
        return true;
    }

    //endregion
    //region IMultiblockPart

    @Override
    public void onPostMachineAssembled(MultiblockReactor controller) {

        super.onPostMachineAssembled(controller);
        this.listenForControllerDataUpdates();
    }

    //endregion
    //region TileEntity

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {

        if (!this.isRemoved() && FLUID_HANDLER_CAPABILITY == capability) {

            if (this.getIoDirection().isInput()) {
                return this._fuelCapability.cast();
            } else {
                return this._wasteCapability.cast();
            }
        }

        return super.getCapability(capability, side);
    }

    /**
     * invalidates a tile entity
     */
    @Override
    public void setRemoved() {

        super.setRemoved();
        this._fuelCapability.invalidate();
        this._wasteCapability.invalidate();
    }

    //endregion
    //region internals

    private FluidStack getStack(ReactantType type) {
        return this.getFluidStackHandler(type).getFluidInTank(0);
    }

    private void distributeItems() {

//        if (this.getIoDirection().isInput()) {
//            return;
//        }
//
//        this.callOnLogicalServer(() -> this.getNeighborCapability().ifPresent(itemHandler -> {
//
//            this.setStack(ReactantType.Waste, ItemHandlerHelper.insertItem(itemHandler, this.getStack(ReactantType.Waste), false));
//            this.markChunkDirty();
//        }));
    }

    private LazyOptional<IFluidHandler> getNeighborCapability() {
        return CodeHelper.optionalFlatMap(this.getPartWorld(), this.getOutwardDirection(),
                        (world, direction) -> WorldHelper.getTile(world, this.getWorldPosition().relative(direction))
                                .map(te -> te.getCapability(FLUID_HANDLER_CAPABILITY, direction.getOpposite())))
                .orElse(LazyOptional.empty());
    }

    @Nonnull
    private IFluidHandler createFuelCapability() {
        return new FluidHandlerForwarder(this.getFluidStackHandler(ReactantType.Fuel)) {

            @Override
            public int fill(FluidStack stack, FluidAction action) {
                return this.isFluidValid(0, stack) ? super.fill(stack, action) : 0;
            }

            @Override
            public boolean isFluidValid(int tank, FluidStack stack) {
                return ReactantHelper.isValidSource(ReactantType.Fuel, stack);
            }
        };
    }

    @Nonnull
    private IFluidHandler createWasteCapability() {
        return new FluidHandlerForwarder(this.getFluidStackHandler(ReactantType.Waste)) {

            @Override
            public int fill(FluidStack stack, FluidAction action) {
                return 0;
            }

            @Override
            public boolean isFluidValid(int tank, FluidStack stack) {
                return false;
            }
        };
    }

    private void handleCommandEjectFuel(CompoundNBT options) {
        this.getMultiblockController().ifPresent(c -> c.ejectFuel(options.contains("void") && options.getBoolean("void")));
    }

    private void handleCommandEjectWaste(CompoundNBT options) {
        this.getMultiblockController().ifPresent(c -> c.ejectWaste(options.contains("void") && options.getBoolean("void")));
    }

    @SuppressWarnings("FieldMayBeFinal")
    @CapabilityInject(IFluidHandler.class)
    private static Capability<IFluidHandler> FLUID_HANDLER_CAPABILITY = null;

    private final FluidTank _fuelTank;
    private final FluidTank _wasteTank;

    private final LazyOptional<IFluidHandler> _fuelCapability;
    private final LazyOptional<IFluidHandler> _wasteCapability;
    private IoDirection _direction;

    //endregion
}
