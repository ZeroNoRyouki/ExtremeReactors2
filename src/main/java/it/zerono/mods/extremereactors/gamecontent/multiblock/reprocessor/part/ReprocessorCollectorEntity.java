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
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import net.minecraft.util.math.AxisAlignedBB;

public class ReprocessorCollectorEntity
        extends AbstractReprocessorEntity {

    public ReprocessorCollectorEntity() {

        super(Content.TileEntityTypes.REPROCESSOR_COLLECTOR.get());
        this._renderBoundingBox = CodeHelper.EMPTY_AABB;
    }

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
        this._renderBoundingBox = this.evalOnController(c -> c.mapBoundingBoxCoordinates(AxisAlignedBB::new,
                CodeHelper.EMPTY_AABB), CodeHelper.EMPTY_AABB);
    }

    //endregion
    //region TileEntity

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return this._renderBoundingBox;
    }

    //endregion
    //region internals

    private AxisAlignedBB _renderBoundingBox;

    //endregion
}
