/*
 *
 * AbstractMultiblockController.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common;

import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.variant.IMultiblockDimensionVariant;
import net.minecraft.world.level.Level;

@SuppressWarnings({"WeakerAccess"})
public abstract class AbstractMultiblockController<Controller extends AbstractCuboidMultiblockController<Controller>,
        V extends IMultiblockDimensionVariant>
        extends AbstractCuboidMultiblockController<Controller> {

    protected AbstractMultiblockController(Level world) {

        super(world);
        this._ticksSinceLastUpdate = 0;
        this._interiorInvisible = false;
    }

    public abstract V getVariant();

    //region client updates

    protected abstract void sendClientUpdates();

    protected void checkAndSendClientUpdates() {

        ++this._ticksSinceLastUpdate;

        if (this._ticksSinceLastUpdate >= TICKS_BETWEEN_UPDATES) {

            this._ticksSinceLastUpdate = 0;
            this.sendClientUpdates();
        }
    }

    //endregion
    //region Rendering

    public boolean isInteriorInvisible() {
        return this._interiorInvisible;
    }

    public boolean isInteriorVisible() {
        return !this._interiorInvisible;
    }

    protected void setInteriorInvisible(final boolean visible) {
        this._interiorInvisible = visible;
    }

    //endregion
    //region AbstractCuboidMultiblockController

//    /**
//     * Call when a block with cached save-delegate data is added to the multiblock.
//     * The part will be notified that the data has been used after this call completes.
//     *
//     * @param part The NBT tag containing this controller's data.
//     * @param data
//     */
//    @Override
//    protected void onAttachedPartWithMultiblockData(final IMultiblockPart part, final CompoundTag data) {
//        this.syncDataFrom(data, SyncReason.FullSync);
//    }

    /**
     * Helper method so we don't check for a whole machine until we have enough blocks
     * to actually assemble it. This isn't as simple as xmax*ymax*zmax for non-cubic machines
     * or for machines with hollow/complex interiors.
     *
     * @return The minimum number of blocks connected to the machine for it to be assembled.
     */
    @Override
    protected int getMinimumNumberOfPartsForAssembledMachine() {
        return this.getVariant().getMinimumPartsCount();
    }

    /**
     * Returns the maximum X dimension size of the machine, or -1 (DIMENSION_UNBOUNDED) to disable
     * dimension checking in X. (This is not recommended.)
     *
     * @return The maximum X dimension size of the machine, or -1
     */
    @Override
    protected int getMaximumXSize() {
        return this.getVariant().getMaximumXSize();
    }

    /**
     * Returns the maximum Z dimension size of the machine, or -1 (DIMENSION_UNBOUNDED) to disable
     * dimension checking in X. (This is not recommended.)
     *
     * @return The maximum Z dimension size of the machine, or -1
     */
    @Override
    protected int getMaximumZSize() {
        return this.getVariant().getMaximumZSize();
    }

    /**
     * Returns the maximum Y dimension size of the machine, or -1 (DIMENSION_UNBOUNDED) to disable
     * dimension checking in X. (This is not recommended.)
     *
     * @return The maximum Y dimension size of the machine, or -1
     */
    @Override
    protected int getMaximumYSize() {
        return this.getVariant().getMaximumYSize();
    }

    /**
     * Client-side update loop. Generally, this shouldn't do anything, but if you want
     * to do some interpolation or something, do it here.
     */
    @Override
    protected void updateClient() {
    }

    /**
     * Called when a machine is restored to the assembled state from a paused state.
     */
    @Override
    protected void onMachineRestored() {
        this.onMachineAssembled();
    }

    /**
     * Called when a machine is paused from an assembled state
     * This generally only happens due to chunk-loads and other "system" events.
     */
    @Override
    protected void onMachinePaused() {
        //TODO ???
        //this.markMultiblockForRenderUpdate();
    }

    //endregion
    //region internals

    //TODO config?
    private static final int TICKS_BETWEEN_UPDATES = 3;

    private int _ticksSinceLastUpdate;
    private boolean _interiorInvisible;

    //endregion
}
