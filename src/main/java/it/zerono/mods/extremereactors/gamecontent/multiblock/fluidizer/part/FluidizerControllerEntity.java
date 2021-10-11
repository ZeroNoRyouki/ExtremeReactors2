/*
 *
 * FluidizerControllerEntity.java
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

import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.model.data.ModelTransformers;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.MultiblockFluidizer;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.block.TileCommandDispatcher;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.lib.network.INetworkTileEntitySyncProvider;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;

public class FluidizerControllerEntity
        extends AbstractFluidizerEntity
        implements INamedContainerProvider, INetworkTileEntitySyncProvider {

    public FluidizerControllerEntity() {

        super(Content.TileEntityTypes.FLUIDIZER_CONTROLLER.get());
        this.setCommandDispatcher(TileCommandDispatcher.<FluidizerControllerEntity>builder()
                .addServerHandler(CommonConstants.COMMAND_ACTIVATE, rce -> rce.setFluidizerActive(true))
                .addServerHandler(CommonConstants.COMMAND_DEACTIVATE, rce -> rce.setFluidizerActive(false))
                .build(this)
        );
    }

    //region client render support

    @Override
    protected int getUpdatedModelVariantIndex() {
        return (byte) (this.isMachineAssembled() ?
                (this.isFluidizerActive() ? ModelTransformers.MODEL_VARIANT_1 : ModelTransformers.MODEL_VARIANT_2) :
                ModelTransformers.MODEL_DEFAULT);
    }

    //endregion
    //region IMultiblockPart

    @Override
    public void onPostMachineAssembled(MultiblockFluidizer controller) {

        super.onPostMachineAssembled(controller);
        this.listenForControllerDataUpdates();
    }

    /**
     * Called when the user activates the machine. This is not called by default, but is included
     * as most machines have this game-logical concept.
     */
    @Override
    public void onMachineActivated() {

        super.onMachineActivated();
        this.requestClientRenderUpdate();
    }

    /**
     * Called when the user deactivates the machine. This is not called by default, but is included
     * as most machines have this game-logical concept.
     */
    @Override
    public void onMachineDeactivated() {

        super.onMachineDeactivated();
        this.requestClientRenderUpdate();
    }

    @Override
    public boolean isGoodForPosition(final PartPosition position, final IMultiblockValidator validatorCallback) {
        return position.isFace() || super.isGoodForPosition(position, validatorCallback);
    }

    //endregion
    //region IDebuggable

    @Override
    public void getDebugMessages(final LogicalSide side, final IDebugMessages messages) {

        super.getDebugMessages(side, messages);
        this.executeOnController(c -> c.getDebugMessages(side, messages));
    }

    //endregion
    //region AbstractModBlockEntity

    /**
     * Check if the tile entity has a GUI or not
     * Override in derived classes to return true if your tile entity got a GUI
     */
    @Override
    public boolean canOpenGui(World world, BlockPos position, BlockState state) {
        return this.isMachineAssembled();
    }

    //endregion
    //region INetworkTileEntitySyncProvider

    /**
     * Add the player to the update queue.
     *
     * @param player    the player to send updates to.
     * @param updateNow if true, send an update to the player immediately.
     */
    @Override
    public void enlistForUpdates(ServerPlayerEntity player, boolean updateNow) {
        this.executeOnController(c -> c.enlistForUpdates(player, updateNow));
    }

    /**
     * Remove the player for the update queue.
     *
     * @param player the player to be removed from the update queue.
     */
    @Override
    public void delistFromUpdates(ServerPlayerEntity player) {
        this.executeOnController(c -> c.delistFromUpdates(player));
    }

    /**
     * Send an update to all enlisted players
     */
    @Override
    public void sendUpdates() {
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
        return null;
    }

    @Override
    public ITextComponent getDisplayName() {
        return super.getPartDisplayName();
    }

    //endregion
}
