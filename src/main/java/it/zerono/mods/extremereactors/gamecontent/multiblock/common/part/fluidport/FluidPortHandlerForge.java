/*
 *
 * FluidPortHandlerForge.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.fluidport;

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.AbstractGeneratorMultiblockController;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.AbstractMultiblockEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockGeneratorVariant;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockVariantProvider;
import it.zerono.mods.zerocore.lib.data.IIoEntity;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.fluid.handler.FluidHandlerForwarder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;

import javax.annotation.Nullable;

public class FluidPortHandlerForge<Controller extends AbstractGeneratorMultiblockController<Controller, V>,
            V extends IMultiblockGeneratorVariant,
            T extends AbstractMultiblockEntity<Controller> & IMultiblockVariantProvider<? extends IMultiblockGeneratorVariant> & IIoEntity>
        extends AbstractFluidPortHandler<Controller, V, T>
        implements IFluidHandler {

    public FluidPortHandlerForge(final T part, final IoMode mode) {

        super(FluidPortType.Forge, part, mode);
        this._capability = LazyOptional.of(() -> this);
        this._capabilityForwarder = new FluidHandlerForwarder(EmptyFluidHandler.INSTANCE);
        this._consumer = null;
    }

    //region IFluidPortHandler

    /**
     * If this is a Active Fluid Port in output mode, send fluid to the connected consumer (if there is one)
     *
     * @param stack FluidStack representing the Fluid and maximum amount of fluid to be sent out.
     * @return the amount of fluid accepted by the consumer
     */
    @Override
    public int outputFluid(final FluidStack stack) {

        if (null == this._consumer || this.isPassive() || this.getIoEntity().getIoDirection().isInput()) {
            return 0;
        }

        return this._consumer.fill(stack, IFluidHandler.FluidAction.EXECUTE);
    }

    /**
     * If this is a Active Fluid Port in input mode, try to get fluids from the connected consumer (if there is one)
     */
    @Override
    public int inputFluid(final IFluidHandler destination, final int maxAmount) {

        if (null == this._consumer || this.isPassive() || this.getIoEntity().getIoDirection().isOutput()) {
            return 0;
        }

        final FluidStack transferred = FluidUtil.tryFluidTransfer(destination, this._consumer, maxAmount, true);

        return transferred.isEmpty() ? 0 : transferred.getAmount();
    }

    //endregion
    //region IIOPortHandler

    /**
     * @return true if this handler is connected to one of it's allowed consumers, false otherwise
     */
    @Override
    public boolean isConnected() {
        return null != this._consumer;
    }

    /**
     * Check for connections
     *  @param world    the handler world
     * @param position the handler position
     */
    @Override
    public void checkConnections(@Nullable final Level world, final BlockPos position) {
        this._consumer = this.lookupConsumer(world, position, CAPAP_FORGE_FLUIDHANDLER,
                te -> te instanceof IFluidPortHandler, this._consumer);
    }

    /**
     * Get the requested capability, if supported
     *
     * @param capability the capability
     * @param direction  the direction the request is coming from
     * @return the capability (if supported) or null (if not)
     */
    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(final Capability<T> capability, final @Nullable Direction direction) {

        if (CAPAP_FORGE_FLUIDHANDLER == capability) {
            return this._capability.cast();
        }

        return null;
    }

    @Override
    public void invalidate() {
        this._capability.invalidate();
    }

    @Override
    public void update() {
        this.updateCapabilityForwarder();
    }

    //endregion
    //region IFluidHandler

    /**
     * Returns the number of fluid storage units ("tanks") available
     *
     * @return The number of tanks available
     */
    @Override
    public int getTanks() {
        return this._capabilityForwarder.getTanks();
    }

    /**
     * Returns the FluidStack in a given tank.
     *
     * <p>
     * <strong>IMPORTANT:</strong> This FluidStack <em>MUST NOT</em> be modified. This method is not for
     * altering internal contents. Any implementers who are able to detect modification via this method
     * should throw an exception. It is ENTIRELY reasonable and likely that the stack returned here will be a copy.
     * </p>
     *
     * <p>
     * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED FLUIDSTACK</em></strong>
     * </p>
     *
     * @param tank Tank to query.
     * @return FluidStack in a given tank. FluidStack.EMPTY if the tank is empty.
     */
    @Override
    public FluidStack getFluidInTank(int tank) {
        return this._capabilityForwarder.getFluidInTank(tank);
    }

    /**
     * Retrieves the maximum fluid amount for a given tank.
     *
     * @param tank Tank to query.
     * @return The maximum fluid amount held by the tank.
     */
    @Override
    public int getTankCapacity(int tank) {
        return this._capabilityForwarder.getTankCapacity(tank);
    }

    /**
     * This function is a way to determine which fluids can exist inside a given handler. General purpose tanks will
     * basically always return TRUE for this.
     *
     * @param tank  Tank to query for validity
     * @param stack Stack to test with for validity
     * @return TRUE if the tank can hold the FluidStack, not considering current state.
     * (Basically, is a given fluid EVER allowed in this tank?) Return FALSE if the answer to that question is 'no.'
     */
    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return this._capabilityForwarder.isFluidValid(tank, stack);
    }

    /**
     * Fills fluid into internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be filled.
     * @param action   If SIMULATE, fill will only be simulated.
     * @return Amount of resource that was (or would have been, if simulated) filled.
     */
    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return this.isPassive() ? this._capabilityForwarder.fill(resource, action) : 0;
    }

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be drained.
     * @param action   If SIMULATE, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return this.isPassive() ? this._capabilityForwarder.drain(resource, action) : FluidStack.EMPTY;
    }

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     * <p/>
     * This method is not Fluid-sensitive.
     *
     * @param maxDrain Maximum amount of fluid to drain.
     * @param action   If SIMULATE, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return this.isPassive() ? this._capabilityForwarder.drain(maxDrain, action) : FluidStack.EMPTY;
    }

    //endregion
    //region internals

    private void updateCapabilityForwarder() {
        this._capabilityForwarder.setHandler(this.getPart().evalOnController(
                        c -> c.getFluidHandler(this.getIoEntity().getIoDirection()).orElse(EmptyFluidHandler.INSTANCE),
                        EmptyFluidHandler.INSTANCE));
    }

    @SuppressWarnings("FieldMayBeFinal")
    public static Capability<IFluidHandler> CAPAP_FORGE_FLUIDHANDLER = CapabilityManager.get(new CapabilityToken<>(){});

    private IFluidHandler _consumer;
    private final FluidHandlerForwarder _capabilityForwarder;
    private final LazyOptional<IFluidHandler> _capability;

    //endregion
}
