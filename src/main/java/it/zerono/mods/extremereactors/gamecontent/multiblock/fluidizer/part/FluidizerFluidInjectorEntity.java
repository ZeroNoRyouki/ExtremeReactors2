/*
 *
 * FluidizerFluidInjectorEntity.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.zerocore.base.BaseHelper;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.DebuggableHelper;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.block.INeighborChangeListener;
import it.zerono.mods.zerocore.lib.data.nbt.IConditionallySyncableEntity;
import it.zerono.mods.zerocore.lib.data.stack.IStackHolder;
import it.zerono.mods.zerocore.lib.fluid.FluidHelper;
import it.zerono.mods.zerocore.lib.fluid.FluidStackHolder;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredientSource;
import it.zerono.mods.zerocore.lib.recipe.ingredient.RecipeIngredientSourceWrapper;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidizerFluidInjectorEntity
        extends AbstractFluidizerEntity
        implements MenuProvider, INeighborChangeListener, IConditionallySyncableEntity {

    public static int MAX_CAPACITY = 8 * 1000;

    public FluidizerFluidInjectorEntity(final BlockPos position, final BlockState blockState) {

        super(Content.TileEntityTypes.FLUIDIZER_FLUIDINJECTOR.get(), position, blockState);
        this._fluids = new FluidStackHolder(1, ($, stack) -> this.isValidIngredient(stack)).setOnLoadListener(this::onFluidsChanged).setOnContentsChangedListener(this::onFluidsChanged);
        this._fluids.setMaxCapacity(MAX_CAPACITY);
        this._capability = LazyOptional.of(() -> this._fluids);
    }

    public IRecipeIngredientSource<FluidStack> asRecipeSource() {
        return RecipeIngredientSourceWrapper.wrap(this._fluids, 0);
    }

    public IFluidHandler getFluidHandler() {
        return this._fluids;
    }

    public FluidStack getStack() {
        return this._fluids.getStackAt(0);
    }

    public boolean isValidIngredient(final FluidStack stack) {
        return this.evalOnController(c -> c.isValidIngredient(stack), false);
    }

    public static void itemTooltipBuilder(final ItemStack stack, final CompoundTag data, final @Nullable BlockGetter world,
                                          final NonNullConsumer<Component> appender, final boolean isAdvancedTooltip) {

        if (data.contains("inv")) {

            final FluidStackHolder holder = new FluidStackHolder(1);
            MutableComponent text;

            holder.syncDataFrom(data.getCompound("inv"), SyncReason.FullSync);

            if (holder.isEmpty(0)) {
                text = BaseHelper.emptyValue();
            } else {
                text = Component.translatable("gui.bigreactors.reactor.fluidaccessport.item.reactant",
                        FluidHelper.getFluidName(holder.getFluidInTank(0)), holder.getAmount(0));
            }

            appender.accept(Component.translatable("gui.bigreactors.generic.fuel.label")
                    .append(text.setStyle(CommonConstants.STYLE_TOOLTIP_VALUE)));
        }
    }

    //region client render support

    @Override
    protected int getUpdatedModelVariantIndex() {
        return this.isMachineAssembled() && this.getNeighborCapability().isPresent() ? 1 : 0;
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
        this.syncChildDataEntityFrom(this._fluids, "inv", data, syncReason);

        if (syncReason.isFullSync()) {
            this._shouldSync = true;
        }
    }

    @Override
    public CompoundTag syncDataTo(CompoundTag data, SyncReason syncReason) {

        super.syncDataTo(data, syncReason);
        this.syncChildDataEntityTo(this._fluids, "inv", data, syncReason);
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
        messages.add(this._fluids, DebuggableHelper::getDebugMessagesFor, "Fluids");
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
        return null;
    }

    @Override
    public Component getDisplayName() {
        return super.getPartDisplayName();
    }

    //endregion
    //region AbstractModBlockEntity

    /**
     * Check if the tile entity has a GUI or not
     * Override in derived classes to return true if your tile entity got a GUI
     */
    @Override
    public boolean canOpenGui(Level world, BlockPos position, BlockState state) {
        return false;
    }

    //endregion
    //region TileEntity

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return FLUID_HANDLER_CAPABILITY == cap ? this._capability.cast() : super.getCapability(cap, side);
    }

    /**
     * invalidates a tile entity
     */
    @Override
    public void setRemoved() {

        super.setRemoved();
        this._capability.invalidate();
    }

    //endregion
    //region internals

    private LazyOptional<IFluidHandler> getNeighborCapability() {
        return CodeHelper.optionalFlatMap(this.getPartWorld(), this.getOutwardDirection(),
                        (world, direction) -> WorldHelper.getTile(world, this.getWorldPosition().relative(direction))
                                .map(te -> te.getCapability(FLUID_HANDLER_CAPABILITY, direction.getOpposite())))
                .orElse(LazyOptional.empty());
    }

    private void onFluidsChanged(IStackHolder.ChangeType changeType, int slot) {
        this.onFluidsChanged();
    }

    private void onFluidsChanged() {

        this.setChanged();
        this.onIngredientsChanged();
        this._shouldSync = true;
    }

    @SuppressWarnings("FieldMayBeFinal")
    private static Capability<IFluidHandler> FLUID_HANDLER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});

    private static final ResourceLocation SYNC_DATA_ID = ExtremeReactors.ROOT_LOCATION.buildWithSuffix("fluidinjector");

    private final FluidStackHolder _fluids;
    private final LazyOptional<IFluidHandler> _capability;
    private boolean _shouldSync;

    //endregion
}
