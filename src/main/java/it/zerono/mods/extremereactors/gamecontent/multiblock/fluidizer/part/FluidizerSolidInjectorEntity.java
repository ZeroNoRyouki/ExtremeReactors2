/*
 *
 * FluidizerAccessPortEntity.java
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
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.container.FluidizerSolidInjectorContainer;
import it.zerono.mods.zerocore.base.BaseHelper;
import it.zerono.mods.zerocore.base.CommonConstants;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.DebuggableHelper;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.block.INeighborChangeListener;
import it.zerono.mods.zerocore.lib.data.component.IComponentProvider;
import it.zerono.mods.zerocore.lib.data.component.ItemStackListComponent;
import it.zerono.mods.zerocore.lib.data.nbt.IConditionallySyncableEntity;
import it.zerono.mods.zerocore.lib.data.stack.IStackHolder;
import it.zerono.mods.zerocore.lib.item.ItemHelper;
import it.zerono.mods.zerocore.lib.item.inventory.ItemStackHolder;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredientSource;
import it.zerono.mods.zerocore.lib.recipe.ingredient.RecipeIngredientSourceWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class FluidizerSolidInjectorEntity
        extends AbstractFluidizerEntity
        implements MenuProvider, INeighborChangeListener, IConditionallySyncableEntity,
                    IComponentProvider<ItemStackListComponent> {

    public FluidizerSolidInjectorEntity(final BlockPos position, final BlockState blockState) {

        super(Content.TileEntityTypes.FLUIDIZER_SOLIDINJECTOR.get(), position, blockState);

        this._solidItems = new ItemStackHolder(1, ($, stack) -> this.isValidIngredient(stack))
                .setOnLoadListener(this::onItemsChanged)
                .setOnContentsChangedListener(this::onItemsChanged);
    }

    public IRecipeIngredientSource<ItemStack> asRecipeSource() {
        return RecipeIngredientSourceWrapper.wrap(this._solidItems, 0);
    }

    public IItemHandlerModifiable getItemHandler() {
        return this._solidItems;
    }

    public ItemStack getStack() {
        return this._solidItems.getStackAt(0);
    }

    public boolean isValidIngredient(final ItemStack stack) {
        return this.evalOnController(c -> c.isValidIngredient(stack), false);
    }

    public static void itemTooltipBuilder(ItemStack stack, Item.TooltipContext context,
                                          Consumer<@NotNull Component> appender, TooltipFlag flag) {

        final var component = stack.get(ItemStackListComponent.getComponentType());

        if (null != component) {

            final MutableComponent text;

            if (component.isEmpty(0)) {
                text = BaseHelper.emptyValue();
            } else {
                text = Component.translatable("gui.bigreactors.reactor.fluidaccessport.item.reactant",
                        ItemHelper.getItemName(component.getStack(0)), component.getAmount(0));
            }

            appender.accept(Component.translatable("gui.bigreactors.generic.fuel.label")
                    .append(text.setStyle(CommonConstants.STYLE_TOOLTIP_VALUE)));
        }
    }

    //region client render support

    @Override
    protected int getUpdatedModelVariantIndex() {
        return this.isMachineAssembled() && null != this.getNeighborCapability() ? 1 : 0;
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
    public void syncDataFrom(CompoundTag data, HolderLookup.Provider registries, SyncReason syncReason) {

        super.syncDataFrom(data, registries, syncReason);
        this.syncChildDataEntityFrom(this._solidItems, "inv", data, registries, syncReason);

        if (syncReason.isFullSync()) {
            this._shouldSync = true;
        }
    }

    @Override
    public CompoundTag syncDataTo(CompoundTag data, HolderLookup.Provider registries, SyncReason syncReason) {

        super.syncDataTo(data, registries, syncReason);
        this.syncChildDataEntityTo(this._solidItems, "inv", data, registries, syncReason);
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
    //region IComponentProvider<ItemStackListComponent>

    @Override
    public ItemStackListComponent createDataComponent() {
        return this._solidItems.createDataComponent();
    }

    @Override
    public void mergeComponent(ItemStackListComponent component) {
        this._solidItems.mergeComponent(component);
    }

    //endregion
    //region IDebuggable

    @Override
    public void getDebugMessages(final LogicalSide side, final IDebugMessages messages) {

        super.getDebugMessages(side, messages);
        messages.add(this._solidItems, DebuggableHelper::getDebugMessagesFor, "Items");
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
        return new FluidizerSolidInjectorContainer(windowId, inventory, this);
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
        return true;
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {

        final var component = input.get(ItemStackListComponent.getComponentType());

        if (null != component) {
            this.mergeComponent(component);
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder collector) {
        collector.set(ItemStackListComponent.getComponentType(), this.createDataComponent());
    }

    @Override
    public ItemStack asStorableStack() {

        final var stack = new ItemStack(this.getBlockType());

        stack.set(ItemStackListComponent.getComponentType(), this.createDataComponent());
        return stack;
    }

    //endregion
    //region internals

    @Nullable
    private IItemHandler getNeighborCapability() {
        return CodeHelper.optionalMap(this.getPartWorld(), this.getOutwardDirection(),
                        (level, direction) -> level.getCapability(Capabilities.ItemHandler.BLOCK,
                                this.getWorldPosition().relative(direction), direction.getOpposite()))
                .orElse(null);
    }

    private void onItemsChanged() {
        this.onItemsChanged(IStackHolder.ChangeType.Replaced, 0);
    }

    private void onItemsChanged(IStackHolder.ChangeType changeType, int slot) {

        this.setChanged();
        this.onIngredientsChanged(changeType);
        this._shouldSync = true;
    }

    private static final ResourceLocation SYNC_DATA_ID = ExtremeReactors.ROOT_LOCATION.buildWithSuffix("solidinjector");

    private final ItemStackHolder _solidItems;
    private boolean _shouldSync;

    //endregion
}
