/*
 *
 * AbstractChargingPortHandler.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.powertap.chargingport;

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.AbstractGeneratorMultiblockController;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.AbstractMultiblockEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.powertap.AbstractPowerTapHandler;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockGeneratorVariant;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockVariantProvider;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.item.inventory.handler.TileEntityItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public abstract class AbstractChargingPortHandler<Controller extends AbstractGeneratorMultiblockController<Controller, V>,
            V extends IMultiblockGeneratorVariant,
            T extends AbstractMultiblockEntity<Controller> & IMultiblockVariantProvider<? extends IMultiblockGeneratorVariant>>
        extends AbstractPowerTapHandler<Controller, V, T>
        implements IChargingPortHandler, ISyncableEntity {

    public static String TILE_COMMAND_EJECT = "eject";

    protected AbstractChargingPortHandler(final EnergySystem energySystem, final T part) {

        super(energySystem, part,IoMode.Active);
        this._input = new TileEntityItemStackHandler(part, 1);
        this._output = new TileEntityItemStackHandler(part, 1);
        this._chargingRate = 0.0f;
    }

    protected double getChargingRate() {

        if (0.0 == this._chargingRate) {
            this._chargingRate = this.getPart().getMultiblockVariant().map(IMultiblockGeneratorVariant::getChargerMaxRate).orElse(0.0);
        }

        return this._chargingRate;
    }

    protected <T> LazyOptional<T> getCapabilityFromInventory(final Capability<T> capability, final boolean ejectIfNotFound) {

        final ItemStack stack = this._input.getStackInSlot(0);

        if (!stack.isEmpty()) {

            final LazyOptional<T> cap = stack.getCapability(capability, null);

            if (ejectIfNotFound && !cap.isPresent()) {
                this.onChargeComplete();
            }

            return cap;
        }

        return LazyOptional.empty();
    }

    protected void onChargeComplete() {

        if (this._output.getStackInSlot(0).isEmpty()) {

            this._output.setStackInSlot(0, this._input.getStackInSlot(0));
            this._input.setStackInSlot(0, ItemStack.EMPTY);
        }
    }

    //region IChargingPortHandler

    @Override
    public IItemHandlerModifiable getItemStackHandler(final IoDirection direction) {
        return direction.isInput() ? this._input : this._output;
    }

    @Override
    public void eject() {
        this.onChargeComplete();
    }

    //endregion
    //region IIOPortHandler

    /**
     * @return true if this handler is connected to one of it's allowed consumers, false otherwise
     */
    @Override
    public boolean isConnected() {
        return !this._input.getStackInSlot(0).isEmpty();
    }

    /**
     * Check for connections
     *  @param world    the handler world
     * @param position the handler position
     */
    @Override
    public void checkConnections(@Nullable Level world, BlockPos position) {
        // nothing to do here
    }

    @Override
    public void invalidate() {
        // nothing to do here
    }

    //endregion
    //region ISyncableEntity

    /**
     * Sync the entity data from the given {@link CompoundTag}
     *
     * @param data       the {@link CompoundTag} to read from
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public void syncDataFrom(CompoundTag data, SyncReason syncReason) {

        if (syncReason.isFullSync()) {

            syncInvFrom(data, "in", this._input);
            syncInvFrom(data, "out", this._output);
        }
    }

    /**
     * Sync the entity data to the given {@link CompoundTag}
     *
     * @param data       the {@link CompoundTag} to write to
     * @param syncReason the reason why the synchronization is necessary
     * @return the {@link CompoundTag} the data was written to (usually {@code data})
     */
    @Override
    public CompoundTag syncDataTo(CompoundTag data, SyncReason syncReason) {

        if (syncReason.isFullSync()) {

            syncInvTo(data, "in", this._input);
            syncInvTo(data, "out", this._output);
        }

        return data;
    }

    //endregion
    //region internals

    private static void syncInvTo(final CompoundTag data, final String name, final ItemStackHandler inv) {

        if (!inv.getStackInSlot(0).isEmpty()) {
            data.put(name, inv.serializeNBT());
        }
    }

    private static void syncInvFrom(final CompoundTag data, final String name, final ItemStackHandler inv) {

        if (data.contains(name)) {
            inv.deserializeNBT(data.getCompound(name));
        }
    }

    private final ItemStackHandler _input;
    private final ItemStackHandler _output;
    private double _chargingRate;

    //endregion
}
