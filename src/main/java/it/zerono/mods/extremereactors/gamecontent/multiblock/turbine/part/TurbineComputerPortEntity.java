/*
 *
 * TurbineComputerPortEntity.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part;

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.MultiblockTurbine;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.computer.TurbineComputerPeripheral;
import it.zerono.mods.zerocore.lib.compat.computer.ComputerPeripheral;
import it.zerono.mods.zerocore.lib.compat.computer.Connector;
import it.zerono.mods.zerocore.lib.compat.computer.IComputerCraftService;
import it.zerono.mods.zerocore.lib.compat.computer.IComputerPort;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class TurbineComputerPortEntity
        extends AbstractTurbineEntity
        implements IComputerPort {

    public TurbineComputerPortEntity(final BlockPos position, final BlockState blockState) {

        super(Content.TileEntityTypes.TURBINE_COMPUTERPORT.get(), position, blockState);
        this._ccConnector = IComputerCraftService.SERVICE.get().createConnector("BigReactors-Turbine", this.getPeripheral());
        // TODO OC
    }

    //region IComputerPort

    @org.jetbrains.annotations.Nullable
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
    public void syncDataFrom(CompoundTag data, HolderLookup.Provider registries, SyncReason syncReason) {

        super.syncDataFrom(data, registries, syncReason);

        if (null != this._ccConnector) {
            this._ccConnector.syncDataFrom(data, registries, syncReason);
        }
    }

    /**
     * Sync the entity data to the given {@link CompoundTag}
     *
     * @param data       the {@link CompoundTag} to write to
     * @param syncReason the reason why the synchronization is necessary
     * @return the {@link CompoundTag} the data was written to (usually {@code data})
     */
    public CompoundTag syncDataTo(CompoundTag data, HolderLookup.Provider registries, SyncReason syncReason) {

        super.syncDataTo(data, registries, syncReason);

        if (null != this._ccConnector) {
            this._ccConnector.syncDataTo(data, registries, syncReason);
        }

        return data;
    }

    //endregion
    //region AbstractReactorEntity

    @Override
    public void onAttached(MultiblockTurbine newController) {

        super.onAttached(newController);

        if (null != this._ccConnector) {
            this._ccConnector.onAttachedToController();
        }
    }

    @Override
    public void onDetached(MultiblockTurbine oldController) {

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

    private TurbineComputerPeripheral getPeripheral() {

        if (null == this._peripheral) {
            this._peripheral = new TurbineComputerPeripheral(this);
        }

        return this._peripheral;
    }

    private final Connector<? extends ComputerPeripheral<?>> _ccConnector;
    private TurbineComputerPeripheral _peripheral;

    //endregion
}
