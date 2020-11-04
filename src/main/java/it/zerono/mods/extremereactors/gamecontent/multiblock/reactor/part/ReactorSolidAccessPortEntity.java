/*
 *
 * ReactorSolidAccessPortEntity.java
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
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.IFuelSource;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactantHelper;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.container.ReactorSolidAccessPortContainer;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.DebuggableHelper;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.block.INeighborChangeListener;
import it.zerono.mods.zerocore.lib.block.TileCommandDispatcher;
import it.zerono.mods.zerocore.lib.data.IIoEntity;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.item.ItemHelper;
import it.zerono.mods.zerocore.lib.item.inventory.handler.ItemHandlerModifiableForwarder;
import it.zerono.mods.zerocore.lib.item.inventory.handler.TileEntityItemStackHandler;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ReactorSolidAccessPortEntity
        extends AbstractReactorEntity
        implements IFuelSource<ItemStack>, IIoEntity, INeighborChangeListener, INamedContainerProvider {

    public static String COMMAND_SET_INPUT = "setinput";
    public static String COMMAND_SET_OUTPUT = "setoutput";
    public static String COMMAND_DUMP_FUEL = "dumpfuel";
    public static String COMMAND_DUMP_WASTE = "dumpwaste";

    public ReactorSolidAccessPortEntity() {

        super(Content.TileEntityTypes.REACTOR_SOLID_ACCESSPORT.get());
        this.setIoDirection(IoDirection.Input);
        this._fuelInventory = new TileEntityItemStackHandler(this, 1);
        this._wasteInventory = new TileEntityItemStackHandler(this, 1);
        this._fuelCapability = LazyOptional.of(this::createFuelCapability);
        this._wasteCapability = LazyOptional.of(this::createWasteCapability);

        this.setCommandDispatcher(TileCommandDispatcher.<ReactorSolidAccessPortEntity>builder()
                .addServerHandler(COMMAND_SET_INPUT, tile -> tile.setIoDirection(IoDirection.Input))
                .addServerHandler(COMMAND_SET_OUTPUT, tile -> tile.setIoDirection(IoDirection.Output))
                .addServerHandler(COMMAND_DUMP_FUEL, ReactorSolidAccessPortEntity::handleCommandEjectFuel)
                .addServerHandler(COMMAND_DUMP_WASTE, ReactorSolidAccessPortEntity::handleCommandEjectWaste)
                .build(this));
    }

    @Override // Keep modlauncher happy...
    public ITextComponent getDisplayName() {
        return super.getDisplayName();
    }

    public IItemHandlerModifiable getItemStackHandler(ReactantType type) {
        return type.isFuel() ? this._fuelInventory : this._wasteInventory;
    }

    /* *
     * Return the Reactant corresponding to the item in the input slot
     *//*
    public Optional<Reactant> getInputReactant() {
        return this.getStackMapping(ReactantType.Fuel).map(SourceProductMapping::getProduct);
    }*/

    /* *
     * Returns the potential amount of reactant which can be produced from this port.
     *//*
    public int getInputReactantAmount() {

        final ItemStack input = this.getStack(ReactantType.Fuel);

        if (ItemHelper.stackIsNotEmpty(input)) {
            return this.getStackMapping(ReactantType.Fuel).map(m -> m.getProductAmount(ItemHelper.stackGetSize(input))).orElse(0);
        }

        return 0;
    }*/



    /* *
     * Consume the source items (from the input slot) of a Reactant.
     * Returns the amount of Reactant that was created.
     *
     * @param reactantDesired The amount of reactant desired, in reactant units (mB)
     * @return The amount of reactant actually produced, in reactant units (mB)
     *//*
    public int consumeReactant(int reactantDesired) {

        // TODO consume partial amount of source fuel (say, a block) and put left over back in the inventory (say, ingots)

        final ItemStack input = this.getStack(ReactantType.Fuel);
        final SourceProductMapping<Tag<Item>, Reactant> mapping = this.getStackMapping(ReactantType.Fuel).orElse(null);

        if (ItemHelper.stackIsNotEmpty(input) && null != mapping) {

            final int amountToConsume = Math.min(ItemHelper.stackGetSize(input), mapping.getSourceAmount(reactantDesired));

            if (amountToConsume > 0) {

                this._fuelInventory.extractItem(0, amountToConsume, false);
                return mapping.getProductAmount(amountToConsume);
            }
        }

        return 0;
    }
*/

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

        if (this.isMachineAssembled()) {

            final int connectedOffset = this.getNeighborCapability().isPresent() ? 1 : 0;

            return this.getIoDirection().isInput() ? 2 + connectedOffset : 0 + connectedOffset;

        } else {

            return 0;
        }
    }

    //endregion
    //region ISolidFuelSource

    /**
     * Return the fuel source items currently available in the fuel source
     *
     * @return the solid fuel source items
     */
    public ItemStack getFuelStack() {
        return this.getStack(ReactantType.Fuel);
    }

    /**
     * Consume source items up to the amount indicated by the provided ItemStack
     *
     * @param sourceToConsume the source items to consume
     * @return the source items actually consumed
     */
    public ItemStack consumeFuelSource(final ItemStack sourceToConsume) {

        final ItemStack sourceStack = this.getStack(ReactantType.Fuel);

        if (!sourceStack.isEmpty() && !sourceToConsume.isEmpty()) {
            return this._fuelInventory.extractItem(0, Math.min(sourceStack.getCount(),
                    sourceToConsume.getCount()), false);
        }

        return ItemStack.EMPTY;
    }

    /**
     * Try to emit a given amount of reactant as a solid item.
     * Will either match the item type already present, or will select whatever type allows the most reactant to be ejected right now
     *
     * @param reactant Type of reactant to emit.
     * @param amount
     * @return the amount of Reactant consumed in the operation
     */
    public int emitReactant(Reactant reactant, int amount) {

        if (amount <= 0) {
            return 0;
        }

        final ItemStack outputStack = this.getStack(ReactantType.Waste);
        final int outputStackMaxSize = Math.min(this._wasteInventory.getSlotLimit(0), outputStack.getMaxStackSize());

        if (!outputStack.isEmpty() && outputStack.getCount() >= outputStackMaxSize) {
            // Already full?
            return 0;
        }

        // If we have an output item, try to produce more of it, given its mapping
        if (!outputStack.isEmpty()) {

            // Find matching mapping
            final IMapping<ITag.INamedTag<Item>, Reactant> mapping = ReactantMappingsRegistry.getFromSolid(outputStack).orElse(null);

            if (null == mapping || !reactant.equals(mapping.getProduct())) {
                // The items in the output slot are not compatible with the Reactant
                return 0;
            }

            // We're using the original source item >> reactant mapping here. This means that source == item, and product == reactant

            final int amountToProduce = Math.min(mapping.getSourceAmount(amount),
                    outputStackMaxSize - outputStack.getCount());

            if (amountToProduce <= 0) {
                return 0;
            }

            // Do we actually produce any reactant at this reduced amount?

            final int reactantToConsume = mapping.getProductAmount(amountToProduce);

            if (reactantToConsume <= 0) {
                return 0;
            }

            outputStack.grow(amountToProduce);
            this.onItemsReceived();

            return reactantToConsume;
        }

        /*
        We have no items in the output slot. We need to figure out candidate mappings.
        Below here, we're using the reactant >> source mappings. This means that source == reactant, and product == item.
        */
        IMapping<Reactant, ITag.INamedTag<Item>> bestMapping = null;
        final List<IMapping<Reactant, ITag.INamedTag<Item>>> mappings = ReactantMappingsRegistry.getToSolid(reactant).orElse(null);

        if (null != mappings) {

            int bestReactantAmount = 0;

            for (final IMapping<Reactant, ITag.INamedTag<Item>> mapping: mappings) {

                // How much product can we produce?
                final int potentialProducts = mapping.getProductAmount(amount);

                // And how much reactant will that consume?
                final int potentialReactant = mapping.getSourceAmount(potentialProducts);

                if (null == bestMapping || bestReactantAmount < potentialReactant) {

                    bestMapping = mapping;
                    bestReactantAmount = potentialReactant;
                }
            }
        }

        if (null == bestMapping) {

            Log.LOGGER.warn(Log.REACTOR, "There are no mapped item types for reactant {}. Nothing to emit here.", reactant);
            return 0;
        }

        int itemsToProduce = Math.min(bestMapping.getProductAmount(amount), outputStackMaxSize);

        if (itemsToProduce <= 0) {
            // Can't produce even one ingot?
            return 0;
        }

        // And clamp again in case we could produce more than 64 items
        final int reactantConsumed = bestMapping.getSourceAmount(itemsToProduce);

        itemsToProduce = bestMapping.getProductAmount(reactantConsumed);

        final ItemStack newItem = ItemHelper.stackFrom(TagsHelper.getTagFirstElement(bestMapping.getProduct()), 1);

        if (newItem.isEmpty()) {

            Log.LOGGER.warn(Log.REACTOR, "Can't create a stack from tag {}. Nothing to emit here.", bestMapping.getProduct());
            return 0;
        }

        ItemHelper.stackSetSize(newItem, itemsToProduce);
        this._wasteInventory.setStackInSlot(0, newItem);
        this.onItemsReceived();

        return reactantConsumed;
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
        return new ReactorSolidAccessPortContainer(windowId, inventory, this);
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
//
//        this.getPartWorld()
//                .filter(CodeHelper::calledByLogicalServer)
//                .ifPresent(world -> {
//
//                    this.notifyOutwardNeighborsOfStateChange();
//                    this.distributeItems();
//                    this.markDirty();
//                });

//        if (this.calledByLogicalServer()) {
//
//            this.notifyOutwardNeighborsOfStateChange();
//            this.distributeItems();
//            this.markDirty();
//
//        } else {
//
//            this.markForRenderUpdate();
//        }
//
        this.callOnLogicalSide(
                () -> {
                    this.notifyOutwardNeighborsOfStateChange();
                    this.distributeItems();
                    this.markDirty();
                },
                this::markForRenderUpdate
        );

        this.notifyNeighborsOfTileChange();
//        this.markForRenderUpdate();
    }

    @Override
    public void onBlockReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {

        ItemHelper.inventoryDropItems(this.getItemStackHandler(ReactantType.Fuel), world, pos);
        ItemHelper.inventoryDropItems(this.getItemStackHandler(ReactantType.Waste), world, pos);
    }

    //endregion
    //region INeighborChangeListener

    /**
     * Called when a neighboring Block on a side of this TileEntity changes
     *
     * @param state            the BlockState of this TileEntity block
     * @param neighborPosition position of neighbor
     * @param isMoving
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

            if (data.contains("invin")) {
                this._fuelInventory.deserializeNBT(data.getCompound("invin"));
            }

            if (data.contains("invout")) {
                this._wasteInventory.deserializeNBT(data.getCompound("invout"));
            }
        }
    }

    @Override
    public CompoundNBT syncDataTo(CompoundNBT data, SyncReason syncReason) {

        super.syncDataTo(data, syncReason);
        IoDirection.write(data, "iodir", this.getIoDirection());

        if (syncReason.isFullSync()) {

            data.put("invin", this._fuelInventory.serializeNBT());
            data.put("invout", this._wasteInventory.serializeNBT());
        }

        return data;
    }

    //endregion
    //region IDebuggable

    @Override
    public void getDebugMessages(final LogicalSide side, final IDebugMessages messages) {

        super.getDebugMessages(side, messages);
        this.getIoDirection().getDebugMessages(side, messages);
        messages.add(this.getItemStackHandler(ReactantType.Fuel), DebuggableHelper::getDebugMessagesFor, "Fuel");
        messages.add(this.getItemStackHandler(ReactantType.Waste), DebuggableHelper::getDebugMessagesFor, "Waste");
    }

    //endregion
    //region AbstractModBlockEntity

    /**
     * Check if the tile entity has a GUI or not
     * Override in derived classes to return true if your tile entity got a GUI
     *
     * @param world
     * @param position
     * @param state
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

        if (!this.isRemoved() && ITEM_HANDLER_CAPABILITY == capability) {

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
    public void remove() {

        super.remove();
        this._fuelCapability.invalidate();
        this._wasteCapability.invalidate();
    }

    //endregion
    //region internals

    private ItemStack getStack(ReactantType type) {
        return this.getItemStackHandler(type).getStackInSlot(0);
    }

    private void setStack(ReactantType type, ItemStack stack) {
        this.getItemStackHandler(type).setStackInSlot(0, stack);
    }
/*
    private Optional<SourceProductMapping<Tag<Item>, Reactant>> getStackMapping(ReactantType type) {

        final ItemStack input = this.getStack(ReactantType.Fuel);

        if (ItemHelper.stackIsNotEmpty(input)) {
            return ReactantMappingsRegistry.getFromSolid(input);
        }

        return Optional.empty();
    }*/

    private void distributeItems() {
/*
        final World world = this.getWorld();
        final Direction myDirection = this.getOutwardDirection().orElse(null);

        if (null == world || this.calledByLogicalClient() || null == myDirection || this.getIoDirection().isInput()) {
            return;
        }

        WorldHelper.getTile(world, this.getWorldPosition().offset(myDirection))
                .map(te -> te.getCapability(ITEM_HANDLER_CAPABILITY, myDirection.getOpposite()))
                .ifPresent(cap -> cap.ifPresent(itemHandler -> {

                    this.setStack(ReactantType.Waste, ItemHandlerHelper.insertItem(itemHandler, this.getStack(ReactantType.Waste), false));
                    this.markChunkDirty();
                }));
*/
        if (this.getIoDirection().isInput()) {
            return;
        }

        this.callOnLogicalServer(() -> this.getNeighborCapability().ifPresent(itemHandler -> {

                this.setStack(ReactantType.Waste, ItemHandlerHelper.insertItem(itemHandler, this.getStack(ReactantType.Waste), false));
                this.markChunkDirty();
        }));
    }

    private LazyOptional<IItemHandler> getNeighborCapability() {
        /*
        return this.callOnLogicalServer(
                () -> CodeHelper.optionalFlatMap(this.getPartWorld(), this.getOutwardDirection(),
                        (world, direction) -> WorldHelper.getTile(world, this.getWorldPosition().offset(direction))
                                .map(te -> te.getCapability(ITEM_HANDLER_CAPABILITY, direction.getOpposite())))
                        .orElse(LazyOptional.empty()),
                LazyOptional::empty
        );
        */
        return CodeHelper.optionalFlatMap(this.getPartWorld(), this.getOutwardDirection(),
                (world, direction) -> WorldHelper.getTile(world, this.getWorldPosition().offset(direction))
                        .map(te -> te.getCapability(ITEM_HANDLER_CAPABILITY, direction.getOpposite())))
                .orElse(LazyOptional.empty());
    }

    @Nonnull
    private IItemHandlerModifiable createFuelCapability() {
        return new ItemHandlerModifiableForwarder(this.getItemStackHandler(ReactantType.Fuel)) {

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                return this.isItemValid(slot, stack) ? super.insertItem(slot, stack, simulate) : stack;
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return ReactantHelper.isValidSource(ReactantType.Fuel, stack);
            }
        };
    }

    @Nonnull
    private IItemHandlerModifiable createWasteCapability() {
        return new ItemHandlerModifiableForwarder(this.getItemStackHandler(ReactantType.Waste)) {

            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                return stack;
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
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
    @CapabilityInject(IItemHandler.class)
    private static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = null;

    private final TileEntityItemStackHandler _fuelInventory;
    private final TileEntityItemStackHandler _wasteInventory;
    private final LazyOptional<IItemHandlerModifiable> _fuelCapability;
    private final LazyOptional<IItemHandlerModifiable> _wasteCapability;
    private IoDirection _direction;

    //endregion
}
