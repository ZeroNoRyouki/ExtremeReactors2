/*
 *
 * AbstractMultiblockEntity.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common.part;

import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockPart;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;

public abstract class AbstractMultiblockEntity<Controller extends AbstractCuboidMultiblockController<Controller>>
        extends AbstractCuboidMultiblockPart<Controller> {

    public AbstractMultiblockEntity(final BlockEntityType<?> type) {
        super(type);
    }

    //region client render support

    protected abstract IModelData getUpdatedModelData();

    //endregion
    //region AbstractCuboidMultiblockPart

    /**
     * validates a tile entity
     */
    @Override
    public void clearRemoved() {

        super.clearRemoved();
        //this.updateClientRenderState();
    }

    @Override
    public void onPostMachineAssembled(final Controller controller) {

        super.onPostMachineAssembled(controller);
        this.updateClientRenderState();
    }

    @Override
    public void onPostMachineBroken() {

        super.onPostMachineBroken();
        this.updateClientRenderState();
    }

    //endregion
    //region AbstractModBlockEntity

    @Override
    public void markForRenderUpdate() {

        this.updateClientRenderState();
        super.markForRenderUpdate();
    }

    //endregion
    //region TileEntity

    /**
     * Allows you to return additional model data.
     * This data can be used to provide additional functionality in your {@link IBakedModel}
     * You need to schedule a refresh of you model data via {@link #requestModelDataUpdate()} if the result of this function changes.
     * <b>Note that this method may be called on a chunk render thread instead of the main client thread</b>
     *
     * @return Your model data
     */
    @Nonnull
    @Override
    public IModelData getModelData() {

        if (null == this._clientModelData) {
            this._clientModelData = this.getUpdatedModelData();
        }

        return this._clientModelData;
    }

    //endregion
    //region internal

    private void updateClientRenderState() {

        this.callOnLogicalClient(() -> {

            this._clientModelData = null;
            this.requestModelDataUpdate();
            this.notifyBlockUpdate();
        });
    }

    private IModelData _clientModelData;

    //endregion
}
