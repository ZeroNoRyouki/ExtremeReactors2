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
import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReactorSolidAccessPortEntity
        extends AbstractReactorEntity
        implements IFuelSource<ItemStack>, IIoEntity, INeighborChangeListener, MenuProvider {

    public ReactorSolidAccessPortEntity(final BlockPos position, final BlockState blockState) {

        super(Content.TileEntityTypes.REACTOR_SOLID_ACCESSPORT.get(), position, blockState);
        this.setIoDirection(IoDirection.Input);
        this._fuelInventory = new TileEntityItemStackHandler(this, 1);
        this._wasteInventory = new TileEntityItemStackHandler(this, 1);
        this._fuelCapability = this.createFuelCapability();
        this._wasteCapability = this.createWasteCapability();

        this.setCommandDispatcher(TileCommandDispatcher.<ReactorSolidAccessPortEntity>builder()
                .addServerHandler(CommonConstants.COMMAND_SET_INPUT, tile -> tile.setIoDirection(IoDirection.Input))
                .addServerHandler(CommonConstants.COMMAND_SET_OUTPUT, tile -> tile.setIoDirection(IoDirection.Output))
                .addServerHandler(CommonConstants.COMMAND_DUMP_FUEL, ReactorSolidAccessPortEntity::handleCommandEjectFuel)
                .addServerHandler(CommonConstants.COMMAND_DUMP_WASTE, ReactorSolidAccessPortEntity::handleCommandEjectWaste)
                .build(this));
    }

    public IItemHandlerModifiable getItemStackHandler(ReactantType type) {
        return type.isFuel() ? this._fuelInventory : this._wasteInventory;
    }

    @Nullable
    public IItemHandler getItemHandler() {

        if (!this.isRemoved()) {
            return this.getIoDirection().isInput() ? this._fuelCapability : this._wasteCapability;
        }

        return null;
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

        final int connectedOffset = this.isMachineAssembled() && null != this.getNeighborCapability() ? 1 : 0;

        return this.getIoDirection().isInput() ? 2 + connectedOffset : connectedOffset;
    }

    //endregion
    //region IFuelSource<ItemStack>

    /**
     * Return the fuel source items currently available in the fuel source
     *
     * @return the solid fuel source items
     */
    @Override
    public ItemStack getFuelStack() {
        return this.getStack(ReactantType.Fuel);
    }

    /**
     * Consume source items up to the amount indicated by the provided ItemStack
     *
     * @param sourceToConsume the source items to consume
     * @return the source items actually consumed
     */
    @Override
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
    @Override
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
            final IMapping<TagKey<Item>, Reactant> mapping = ReactantMappingsRegistry.getFromSolid(outputStack).orElse(null);

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
        IMapping<Reactant, TagKey<Item>> bestMapping = null;
        final List<IMapping<Reactant, TagKey<Item>>> mappings = ReactantMappingsRegistry.getToSolid(reactant).orElse(null);

        if (null != mappings) {

            int bestReactantAmount = 0;

            for (final IMapping<Reactant, TagKey<Item>> mapping: mappings) {

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

        final ItemStack newItem = ReactantMappingsRegistry.getSolidStackFrom(bestMapping, 1);

        if (newItem.isEmpty()) {

            Log.LOGGER.warn(Log.REACTOR, "Can't create a stack from tag {}. Nothing to emit here.", bestMapping.getProduct());
            return 0;
        }

        newItem.setCount(itemsToProduce);
        this._wasteInventory.setStackInSlot(0, newItem);
        this.onItemsReceived();

        return reactantConsumed;
    }

    //endregion
    //region MenuProvider

    /**
     * Create the SERVER-side container for this TileEntity
     * @param windowId  the window id
     * @param inventory the player inventory
     * @param player    the player
     * @return the container to use on the server
     */
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int windowId, final Inventory inventory, final Player player) {
        return new ReactorSolidAccessPortContainer(windowId, inventory, this);
    }

    @Override
    public Component getDisplayName() {
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
//        this.markForRenderUpdate();
    }

    @Override
    public void onBlockReplaced(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {

        ItemHelper.inventoryDropItems(this.getItemStackHandler(ReactantType.Fuel), world, pos);
        ItemHelper.inventoryDropItems(this.getItemStackHandler(ReactantType.Waste), world, pos);
    }

    //endregion
    //region INeighborChangeListener

    @Override
    public void onNeighborBlockChanged(BlockState state, @Nullable Orientation orientation, boolean isMoving) {
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
    public void syncDataFrom(CompoundTag data, HolderLookup.Provider registries, SyncReason syncReason) {

        super.syncDataFrom(data, registries, syncReason);
        this.setIoDirection(IoDirection.read(data, "iodir", IoDirection.Input));

        if (syncReason.isFullSync()) {

            if (data.contains("invin")) {
                this._fuelInventory.deserializeNBT(registries, data.getCompound("invin"));
            }

            if (data.contains("invout")) {
                this._wasteInventory.deserializeNBT(registries, data.getCompound("invout"));
            }
        }
    }

    @Override
    public CompoundTag syncDataTo(CompoundTag data, HolderLookup.Provider registries, SyncReason syncReason) {

        super.syncDataTo(data, registries, syncReason);
        IoDirection.write(data, "iodir", this.getIoDirection());

        if (syncReason.isFullSync()) {

            data.put("invin", this._fuelInventory.serializeNBT(registries));
            data.put("invout", this._wasteInventory.serializeNBT(registries));
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
    public boolean canOpenGui(Level world, BlockPos position, BlockState state) {
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
    //region internals

    private ItemStack getStack(ReactantType type) {
        return this.getItemStackHandler(type).getStackInSlot(0);
    }

    private void setStack(ReactantType type, ItemStack stack) {
        this.getItemStackHandler(type).setStackInSlot(0, stack);
    }

    private void distributeItems() {

        if (this.getIoDirection().isInput()) {
            return;
        }

        this.callOnLogicalServer(() -> {

            final IItemHandler handler = this.getNeighborCapability();

            if (null != handler) {

                this.setStack(ReactantType.Waste, ItemHandlerHelper.insertItem(handler, this.getStack(ReactantType.Waste), false));
                this.markChunkDirty();
            }
        });
    }

    @Nullable
    private IItemHandler getNeighborCapability() {
        return CodeHelper.optionalMap(this.getPartWorld(), this.getOutwardDirection(),
                        (level, direction) -> level.getCapability(Capabilities.ItemHandler.BLOCK,
                                this.getWorldPosition().relative(direction), direction.getOpposite()))
                        .orElse(null);
    }

    @NotNull
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

    @NotNull
    private IItemHandlerModifiable createWasteCapability() {
        return new ItemHandlerModifiableForwarder(this.getItemStackHandler(ReactantType.Waste)) {

            @Override
            public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                return stack;
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return false;
            }
        };
    }

    private void handleCommandEjectFuel(CompoundTag options) {
        this.getMultiblockController().ifPresent(c -> c.ejectFuel(options.contains("void") && options.getBoolean("void")));
    }

    private void handleCommandEjectWaste(CompoundTag options) {
        this.getMultiblockController().ifPresent(c -> c.ejectWaste(options.contains("void") && options.getBoolean("void")));
    }

    private final TileEntityItemStackHandler _fuelInventory;
    private final TileEntityItemStackHandler _wasteInventory;
    private final IItemHandlerModifiable _fuelCapability;
    private final IItemHandlerModifiable _wasteCapability;
    private IoDirection _direction;

    //endregion
}
