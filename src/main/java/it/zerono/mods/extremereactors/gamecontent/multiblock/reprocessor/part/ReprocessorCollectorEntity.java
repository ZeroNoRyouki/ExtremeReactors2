/*
 *
 * ReprocessorCollectorEntity.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.part;

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.MultiblockReprocessor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.recipe.ReprocessorRecipe;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.Ticker;
import it.zerono.mods.zerocore.lib.item.ItemHelper;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

public class ReprocessorCollectorEntity
        extends AbstractReprocessorEntity {

    public ReprocessorCollectorEntity(final BlockPos position, final BlockState blockState) {

        super(Content.TileEntityTypes.REPROCESSOR_COLLECTOR.get(), position, blockState);
        this._renderBoundingBox = CodeHelper.EMPTY_AABB;
        this._recipeSourceItem = this._recipeProductItem = ItemStack.EMPTY;
    }

    public void onRecipeChanged(@Nullable ReprocessorRecipe currentRecipe) {

        if (null != currentRecipe) {

            final List<ItemStack> ingredients = currentRecipe.getIngredient1().getMatchingElements();

            this._recipeSourceItem = ingredients.isEmpty() ? ItemStack.EMPTY : ingredients.get(0);
            this._recipeProductItem = currentRecipe.getResult().getResult();

        } else {

            this._recipeSourceItem = this._recipeProductItem = ItemStack.EMPTY;
        }

        this.callOnLogicalServer(this::notifyTileEntityUpdate);
    }

    //region TESR support

    public boolean isRecipeRunning() {
        return this.isReprocessorActive();
    }

    public ItemStack getRecipeSourceItem() {
        return this._recipeSourceItem;
    }

    public ItemStack getRecipeProductItem() {
        return this._recipeProductItem;
    }

    public double getProgress() {
        return this._tersTicker.getTicks() / ((double)MultiblockReprocessor.TICKS);
    }

    public void onClientTick() {
        this.callOnLogicalClient(this._tersTicker::tick);
    }

    //endregion
    //region ISyncableEntity

    @Override
    public void syncDataFrom(final CompoundTag data, final SyncReason syncReason) {

        super.syncDataFrom(data, syncReason);

        if (syncReason.isNetworkUpdate()) {

            this._recipeSourceItem = syncItem(data, "source");
            this._recipeProductItem = syncItem(data, "product");
        }
    }

    @Override
    public CompoundTag syncDataTo(final CompoundTag data, final SyncReason syncReason) {

        super.syncDataTo(data, syncReason);

        if (syncReason.isNetworkUpdate()) {

            syncItem(data, "source", this._recipeSourceItem);
            syncItem(data, "product", this._recipeProductItem);
        }

        return data;
    }

    //endregion
    //region AbstractReprocessorEntity

    @Override
    public boolean isGoodForPosition(PartPosition position, IMultiblockValidator validatorCallback) {

        if (PartPosition.BottomFace == position) {
            return true;
        }

        validatorCallback.setLastError(this.getWorldPosition(), "multiblock.validation.reprocessor.invalid_collector_position");
        return false;
    }

    //endregion
    //region client render support

    @Override
    protected int getUpdatedModelVariantIndex() {
        return 0;
    }

    //endregion
    //region AbstractMultiblockEntity

    @Override
    public void onPostMachineAssembled(final MultiblockReprocessor controller) {

        super.onPostMachineAssembled(controller);
        this._renderBoundingBox = this.evalOnController(c -> c.mapBoundingBoxCoordinates(AABB::new,
                CodeHelper.EMPTY_AABB), CodeHelper.EMPTY_AABB);
    }

    //endregion
    //region TileEntity

    @Override
    public AABB getRenderBoundingBox() {
        return this._renderBoundingBox;
    }

    @Override
    public void setLevel(Level world) {

        super.setLevel(world);
        this._tersTicker = this.callOnLogicalClient(() -> new Ticker(MultiblockReprocessor.TICKS), () -> null);
    }

    //endregion
    //region internals

    private static void syncItem(final CompoundTag data, final String name, final ItemStack stack) {
        if (!stack.isEmpty()) {
            data.putString(name, ItemHelper.getItemId(stack).toString());
        }
    }

    @Nullable
    private static ItemStack syncItem(final CompoundTag data, final String name) {

        Item item;

        if (data.contains(name) && null != (item = ItemHelper.getItemFrom(data.getString(name)))) {
            return ItemHelper.stackFrom(item);
        }

        return ItemStack.EMPTY;
    }

    private AABB _renderBoundingBox;

    private Ticker _tersTicker;
    private ItemStack _recipeSourceItem;
    private ItemStack _recipeProductItem;

    //endregion
}
