/*
 *
 * PowerTapHandlerFE.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.powertap;

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.AbstractGeneratorMultiblockController;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockGeneratorVariant;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockPart;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class PowerTapHandlerFE<Controller extends AbstractGeneratorMultiblockController<Controller, V>,
            V extends IMultiblockGeneratorVariant>
        extends AbstractPowerTapHandler<Controller, V>
        implements IEnergyStorage {

    public PowerTapHandlerFE(final AbstractCuboidMultiblockPart<Controller> part, final IoMode mode) {

        super(part, mode);
        this._consumer = null;
        this._capability = LazyOptional.of(() -> this);
    }

    //region IPowerTapHandler

    /**
     * Get the {@link EnergySystem} supported by this IPowerTapHandler
     *
     * @return the supported {@link EnergySystem}
     */
    public EnergySystem getEnergySystem() {
        return EnergySystem.ForgeEnergy;
    }

    /**
     * Send energy to the connected consumer (if there is one and we are in active mode)
     *
     * @param amount amount of energy to send
     * @return the amount of energy accepted by the consumer
     */
    public double outputEnergy(double amount) {

        if (null == this._consumer || this.isPassive()) {
            return 0;
        }

        final int maxUnits = (int) Math.min(amount, Integer.MAX_VALUE);

        return this._consumer.receiveEnergy(maxUnits, false);
    }

    /**
     * @return true if there is a consumer connected, false otherwise
     */
    public boolean isConnected() {
        return null != this._consumer;
    }

    /**
     * Check for connections
     *
     * @param world    the PowerTap world
     * @param position the PowerTap position
     */
    public void checkConnections(@Nullable IBlockReader world, BlockPos position) {

        boolean wasConnected = null != this._consumer;

        if (null != world) {

            Direction approachDirection = this.getPart().getOutwardDirection().orElse(null);

            this._consumer = null;

            if (null == approachDirection) {

                wasConnected = false;

            } else {

                if (null != CAPAP_FORGE_ENERGYSTORAGE) {

                    final TileEntity te = world.getTileEntity(position.offset(approachDirection));

                    if (null != te && !(te instanceof IPowerTapHandler)) {

                        final LazyOptional<IEnergyStorage> capability = te.getCapability(CAPAP_FORGE_ENERGYSTORAGE, approachDirection.getOpposite());

                        if (capability.isPresent()) {
                            this._consumer = capability.orElseThrow(RuntimeException::new);
                        }
                    }
                }
            }
        }

        final boolean isConnected = this._consumer != null;
        final World partWorld = this.getPart().getWorld();

        if (wasConnected != isConnected && null != partWorld && CodeHelper.calledByLogicalClient(partWorld)) {
            WorldHelper.notifyBlockUpdate(partWorld, this.getPart().getWorldPosition(), null, null);
        }
    }

    /**
     * Get the requested capability if supported
     *
     * @param capability the capability
     * @param direction  the direction the request is coming from
     * @param <T>        the type of the capability
     * @return the capability (if supported) or null (if not)
     */
    @Override
    @Nullable
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction direction) {

        if (CAPAP_FORGE_ENERGYSTORAGE == capability) {
            return this._capability.cast();
        }

        return null;
    }

    //endregion
    //region IEnergyStorage

    /**
     * Adds energy to the storage. Returns quantity of energy that was accepted.
     *
     * @param maxReceive Maximum amount of energy to be inserted.
     * @param simulate   If TRUE, the insertion will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
     */
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        // we never accept energy
        return 0;
    }

    /**
     * Removes energy from the storage. Returns quantity of energy that was removed.
     *
     * @param maxExtract Maximum amount of energy to be extracted.
     * @param simulate   If TRUE, the extraction will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
     */
    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return this.canExtract() ?
                (int) this.getEnergyProvider().extractEnergy(this.getEnergySystem(), null, maxExtract, simulate) :
                0;
    }

    /**
     * Returns the amount of energy currently stored.
     */
    @Override
    public int getEnergyStored() {
        return (int) this.getEnergyProvider().getEnergyStored(this.getEnergySystem(), null);
    }

    /**
     * Returns the maximum amount of energy that can be stored.
     */
    @Override
    public int getMaxEnergyStored() {
        return (int) this.getEnergyProvider().getCapacity(this.getEnergySystem(), null);
    }

    /**
     * Returns if this storage can have energy extracted.
     * If this is false, then any calls to extractEnergy will return 0.
     */
    @Override
    public boolean canExtract() {
        return this.isPassive();
    }

    /**
     * Used to determine if this storage can receive energy.
     * If this is false, then any calls to receiveEnergy will return 0.
     */
    @Override
    public boolean canReceive() {
        // we never accept energy
        return false;
    }

    //endregion
    //region internals

    @SuppressWarnings("FieldMayBeFinal")
    @CapabilityInject(IEnergyStorage.class)
    private static Capability<IEnergyStorage> CAPAP_FORGE_ENERGYSTORAGE = null;

    private IEnergyStorage _consumer;
    private final LazyOptional<IEnergyStorage> _capability;

    //endregion
}
