/*
 *
 * ReactorComputerPortEntity.java
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

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.computer.ReactorComputerPeripheral;
import it.zerono.mods.zerocore.lib.compat.computer.ComputerPeripheral;
import it.zerono.mods.zerocore.lib.compat.computer.Connector;
import it.zerono.mods.zerocore.lib.compat.computer.IComputerCraftService;
import it.zerono.mods.zerocore.lib.compat.computer.IComputerPort;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ReactorComputerPortEntity
        extends AbstractReactorEntity
        implements IComputerPort {

    public ReactorComputerPortEntity(final BlockPos position, final BlockState blockState) {

        super(Content.TileEntityTypes.REACTOR_COMPUTERPORT.get(), position, blockState);
        this._ccConnector = IComputerCraftService.SERVICE.get().createConnector("BigReactors-Reactor", this.getPeripheral());
        // TODO OC
    }

    //region IComputerPort

    @Nullable
    public final Connector<? extends ComputerPeripheral<?>> getConnector(Direction direction) {
        return this._ccConnector;
    }

    //endregion
    //region ISyncableEntity

    /**
     * Sync the entity data from the given {@link CompoundTag}
     *
     * @param data       the {@link CompoundTag} to read from
     * @param syncReason the reason why the synchronization is necessary
     */
    public void syncDataFrom(final CompoundTag data, final SyncReason syncReason) {

        super.syncDataFrom(data, syncReason);

        if (null != this._ccConnector) {
            this._ccConnector.syncDataFrom(data, syncReason);
        }
    }

    /**
     * Sync the entity data to the given {@link CompoundTag}
     *
     * @param data       the {@link CompoundTag} to write to
     * @param syncReason the reason why the synchronization is necessary
     * @return the {@link CompoundTag} the data was written to (usually {@code data})
     */
    public CompoundTag syncDataTo(final CompoundTag data, final SyncReason syncReason) {

        super.syncDataTo(data, syncReason);

        if (null != this._ccConnector) {
            this._ccConnector.syncDataTo(data, syncReason);
        }

        return data;
    }

    //endregion
    //region AbstractReactorEntity

    @Override
    public void onAttached(MultiblockReactor newController) {

        super.onAttached(newController);

        if (null != this._ccConnector) {
            this._ccConnector.onAttachedToController();
        }
    }

    @Override
    public void onDetached(MultiblockReactor oldController) {

        super.onDetached(oldController);

        if (null != this._ccConnector) {
            this._ccConnector.onDetachedFromController();
        }
    }

    //endregion
    //region client render support

    @Override
    protected int getUpdatedModelVariantIndex() {
        return this.isMachineAssembled() ? 1 : 0;
    }

    //endregion
    //region internals

    private ReactorComputerPeripheral getPeripheral() {

        if (null == this._peripheral) {
            this._peripheral = new ReactorComputerPeripheral(this);
        }

        return this._peripheral;
    }

    private final Connector<? extends ComputerPeripheral<?>> _ccConnector;
    private ReactorComputerPeripheral _peripheral;

    //endregion
}
