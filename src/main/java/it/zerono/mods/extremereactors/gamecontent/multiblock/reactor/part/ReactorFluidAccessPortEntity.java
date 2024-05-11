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

import it.zerono.mods.extremereactors.ExtremeReactors;
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
import it.zerono.mods.zerocore.lib.data.nbt.IConditionallySyncableEntity;
import it.zerono.mods.zerocore.lib.data.stack.IStackHolder;
import it.zerono.mods.zerocore.lib.fluid.FluidHelper;
import it.zerono.mods.zerocore.lib.fluid.FluidTank;
import it.zerono.mods.zerocore.lib.fluid.handler.FluidHandlerForwarder;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.NonNullConsumer;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ReactorFluidAccessPortEntity
        extends AbstractReactorEntity
        implements IFuelSource<FluidStack>, IIoEntity, INeighborChangeListener, MenuProvider, IConditionallySyncableEntity {

    public static int TANK_CAPACITY = 2 * ReactorFuelRodEntity.FUEL_CAPACITY_PER_FUEL_ROD;

    public ReactorFluidAccessPortEntity(final BlockPos position, final BlockState blockState) {

        super(Content.TileEntityTypes.REACTOR_FLUID_ACCESSPORT.get(), position, blockState);
        this.setIoDirection(IoDirection.Input);
        this._fuelTank = new FluidTank(TANK_CAPACITY).setOnLoadListener(this::onTankChanged).setOnContentsChangedListener(this::onTankChanged);
        this._wasteTank = new FluidTank(TANK_CAPACITY).setOnLoadListener(this::onTankChanged).setOnContentsChangedListener(this::onTankChanged);
        this._fuelCapability = this.createFuelCapability();
        this._wasteCapability = this.createWasteCapability();
        this._shouldSync = false;

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

    @Nullable
    public IFluidHandler getFluidHandler() {

        if (!this.isRemoved()) {
            return this.getIoDirection().isInput() ? this._fuelCapability : this._wasteCapability;
        }

        return null;
    }

    /**
     * Called when stuff has been placed in the access port
     */
    public void onItemsReceived() {
        this.markChunkDirty();
    }

    public static void itemTooltipBuilder(final ItemStack stack, final CompoundTag data, final @Nullable BlockGetter world,
                                          final NonNullConsumer<Component> appender, final boolean isAdvancedTooltip) {

        final FluidTank tank = new FluidTank(TANK_CAPACITY);

        if (data.contains("iodir")) {
            appender.accept(Component.translatable(IoDirection.read(data, "iodir", IoDirection.Input).isInput() ?
                    "gui.bigreactors.reactor.fluidaccessport.directioninput.line1" :
                    "gui.bigreactors.reactor.fluidaccessport.directionoutput.line1").setStyle(CommonConstants.STYLE_TOOLTIP_VALUE));
        }

        if (data.contains("invin")) {

            tank.syncDataFrom(data.getCompound("invin"), SyncReason.FullSync);
            appender.accept(getTankTooltip(tank, "gui.bigreactors.generic.fuel.label"));
        }

        if (data.contains("invout")) {

            tank.syncDataFrom(data.getCompound("invout"), SyncReason.FullSync);
            appender.accept(getTankTooltip(tank, "gui.bigreactors.generic.waste.label"));
        }
    }

    private static Component getTankTooltip(final FluidTank tank, final String labelKey) {

        final MutableComponent text;

        if (tank.isEmpty()) {
            text = Component.translatable("gui.bigreactors.generic.empty");
        } else {
            text = Component.translatable("gui.bigreactors.reactor.fluidaccessport.item.reactant",
                    FluidHelper.getFluidName(tank.getFluid()), tank.getFluidAmount(), TANK_CAPACITY);
        }

        return Component.translatable(labelKey).append(text.setStyle(CommonConstants.STYLE_TOOLTIP_VALUE));
    }

    //region client render support

    @Override
    protected int getUpdatedModelVariantIndex() {

        final int connectedOffset = this.isMachineAssembled() && null != this.getNeighborCapability() ? 1 : 0;

        return this.getIoDirection().isInput() ? 2 + connectedOffset : connectedOffset;
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
            final IMapping<TagKey<Fluid>, Reactant> mapping = ReactantMappingsRegistry.getFromFluid(outputStack).orElse(null);

            if (null == mapping || !reactant.equals(mapping.getProduct())) {
                // The fluid in the output tank is not compatible with the Reactant
                return 0;
            }

            final int amountToProduce = Math.min(amount, maxOutputAmount - outputStack.getAmount());

            if (amountToProduce <= 0) {
                return 0;
            }

            final int filled = this._wasteTank.fill(FluidHelper.stackFrom(outputStack, amountToProduce), IFluidHandler.FluidAction.EXECUTE);

            this.onItemsReceived();

            return filled;
        }

        /*
        We have no fluid in the output tank. We need to figure out candidate mappings.
        Below here, we're using the reactant >> source mappings. This means that source == reactant, and product == fluid.

        Since there is a 1:1 ratio between fluid fuels/wastes and reactants, there is really no "best" mapping.
        Pick the first one available.
        */
        final IMapping<Reactant, TagKey<Fluid>> bestMapping = ReactantMappingsRegistry.getToFluid(reactant)
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
        return ModTileContainer.empty(Content.ContainerTypes.REACTOR_FLUID_ACCESSPORT.get(), windowId, this, (ServerPlayer)player);
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
                    this.setChanged();
                },
                this::markForRenderUpdate
        );

        this.notifyNeighborsOfTileChange();
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
    public void syncDataFrom(CompoundTag data, SyncReason syncReason) {

        super.syncDataFrom(data, syncReason);
        this.setIoDirection(IoDirection.read(data, "iodir", IoDirection.Input));
        this.syncChildDataEntityFrom(this._fuelTank, "invin", data, syncReason);
        this.syncChildDataEntityFrom(this._wasteTank, "invout", data, syncReason);

        if (syncReason.isFullSync()) {
            this._shouldSync = true;
        }
    }

    @Override
    public CompoundTag syncDataTo(CompoundTag data, SyncReason syncReason) {

        super.syncDataTo(data, syncReason);
        IoDirection.write(data, "iodir", this.getIoDirection());
        this.syncChildDataEntityTo(this._fuelTank, "invin", data, syncReason);
        this.syncChildDataEntityTo(this._wasteTank, "invout", data, syncReason);
        return data;
    }

    //endregion
    //region IConditionallySyncableEntity

    /**
     * @return a unique identifier for this ISyncableEntity
     */
    @Override
    public ResourceLocation getSyncableEntityId() {
        return SYNC_DATA_ID;
    }

    /**
     * @return true if this ISyncableEntity should be synced, false otherwise
     */
    @Override
    public boolean shouldSyncEntity() {

        final boolean result = this._shouldSync;

        this._shouldSync = false;
        return result;
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

    private FluidStack getStack(ReactantType type) {
        return this.getFluidStackHandler(type).getFluidInTank(0);
    }

    @Nullable
    private IFluidHandler getNeighborCapability() {
        return CodeHelper.optionalMap(this.getPartWorld(), this.getOutwardDirection(),
                        (level, direction) -> level.getCapability(Capabilities.FluidHandler.BLOCK,
                                this.getWorldPosition().relative(direction), direction.getOpposite()))
                .orElse(null);
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

    private void onTankChanged(IStackHolder.ChangeType changeType, int slot) {
        this.onTankChanged();
    }

    private void onTankChanged() {

        this.setChanged();
        this._shouldSync = true;
    }

    private void handleCommandEjectFuel(CompoundTag options) {
        this.getMultiblockController().ifPresent(c -> c.ejectFuel(options.contains("void") && options.getBoolean("void")));
    }

    private void handleCommandEjectWaste(CompoundTag options) {
        this.getMultiblockController().ifPresent(c -> c.ejectWaste(options.contains("void") && options.getBoolean("void")));
    }

    private static final ResourceLocation SYNC_DATA_ID = ExtremeReactors.ROOT_LOCATION.buildWithSuffix("injector");

    private final FluidTank _fuelTank;
    private final FluidTank _wasteTank;

    private final IFluidHandler _fuelCapability;
    private final IFluidHandler _wasteCapability;
    private IoDirection _direction;
    private boolean _shouldSync;

    //endregion
}
