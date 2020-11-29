/*
 *
 * FluidPortHandlerMekanism.java
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

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.zerono.mods.extremereactors.api.IMapping;
import it.zerono.mods.extremereactors.api.coolant.FluidMappingsRegistry;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.AbstractGeneratorMultiblockController;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockGeneratorVariant;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.compat.Mods;
import it.zerono.mods.zerocore.lib.data.IIoEntity;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.fluid.handler.FluidHandlerForwarder;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockPart;
import mekanism.api.Action;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.recipes.RotaryRecipe;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

public class FluidPortHandlerMekanism<Controller extends AbstractGeneratorMultiblockController<Controller, V>,
        V extends IMultiblockGeneratorVariant, P extends AbstractCuboidMultiblockPart<Controller> & IIoEntity>
        extends AbstractFluidPortHandler<Controller, V, P>
        implements IGasHandler {

    public FluidPortHandlerMekanism(final P part, final IoMode mode) {

        super(FluidPortType.Mekanism, part, IoMode.Passive);
        this._capability = LazyOptional.of(() -> this);
        this._capabilityForwarder = new FluidHandlerForwarder(EmptyFluidHandler.INSTANCE);
        this._consumer = null;
    }

    //region IFluidPortHandler

    /**
     * Send fluid to the connected consumer (if there is one)
     *
     * @param stack FluidStack representing the Fluid and maximum amount of fluid to be sent out.
     * @return the amount of fluid accepted by the consumer
     */
    @Override
    public int outputFluid(final FluidStack stack) {

        if (null == this._consumer || this.isPassive()) {
            return 0;
        }

        return (int)(this._consumer.insertChemical(getGasStack(stack), Action.EXECUTE).getAmount());
    }

    /**
     * If this is a Active Fluid Port in input mode, try to get fluids from the connected consumer (if there is one)
     */
    @Override
    public void inputFluid() {
        // no insertions
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
     *
     * @param world    the handler world
     * @param position the handler position
     */
    @Override
    public void checkConnections(@Nullable final IBlockReader world, final BlockPos position) {
        this._consumer = this.lookupConsumer(world, position, CAPAP_MEKANISM_GASHANDLER,
                te -> te instanceof IFluidPortHandler, this._consumer);
    }

    @Override
    public void invalidate() {
        this._capability.invalidate();
    }

    @Override
    public void update() {
        this.updateCapabilityForwarder();
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
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction direction) {

        if (CAPAP_MEKANISM_GASHANDLER == capability) {
            return this._capability.cast();
        }

        return null;
    }

    //endregion
    //region IGasHandler

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public GasStack getChemicalInTank(int idx) {
        return 0 != idx ? GasStack.EMPTY : getGasStack(this._capabilityForwarder.getFluidInTank(0));
    }

    @Override
    public void setChemicalInTank(int idx, GasStack stack) {
        // no insertions
    }

    @Override
    public long getTankCapacity(int idx) {
        return 0 == idx ? this._capabilityForwarder.getTankCapacity(0) : 0;
    }

    @Override
    public boolean isValid(int idx, GasStack stack) {
        // no insertions
        return false;
    }

    @Override
    public GasStack insertChemical(int idx, GasStack stack, Action action) {
        // no insertions
        return stack;
    }

    @Override
    public GasStack extractChemical(int idx, long amount, Action action) {

        final FluidStack currentStack = this._capabilityForwarder.getFluidInTank(0);

        if (0 != idx || currentStack.isEmpty()) {
            return GasStack.EMPTY;
        }

        final IMapping<Fluid, Gas> fluidMapping = getFluidMapping(currentStack.getFluid());

        if (null == fluidMapping) {
            return GasStack.EMPTY;
        }

        final IMapping<Gas, Fluid> gasMapping = getGasMapping(fluidMapping.getProduct());

        if (null == gasMapping) {
            return GasStack.EMPTY;
        }

        final int amountToRemove = gasMapping.getProductAmount((int)Math.max(amount, Integer.MAX_VALUE));
        final FluidStack removed = this._capabilityForwarder.drain(amountToRemove, action.toFluidAction());

        return getGasStack(removed);
    }

    //endregion
    //region internals

    private void updateCapabilityForwarder() {
        this._capabilityForwarder.setHandler(this.getPart().evalOnController(
                c -> c.getFluidHandler(IoDirection.Output).orElse(EmptyFluidHandler.INSTANCE),
                EmptyFluidHandler.INSTANCE));
    }

    private static GasStack getGasStack(final FluidStack fluidStack) {
        return fluidStack.isEmpty() ? GasStack.EMPTY : getGasStack(fluidStack.getFluid(), fluidStack.getAmount());
    }

    private static GasStack getGasStack(final Fluid fluid, final int amount) {

        final IMapping<Fluid, Gas> mapping = getFluidMapping(fluid);

        return null != mapping ? new GasStack(mapping.getProduct(), mapping.getProductAmount(amount)) : GasStack.EMPTY;
    }

    @Nullable
    private static IMapping<Fluid, Gas> getFluidMapping(final Fluid fluid) {

        if (null == s_fluidToGas) {
            buildMappings();
        }

        return s_fluidToGas.get(fluid);
    }

    @Nullable
    private static IMapping<Gas, Fluid> getGasMapping(final Gas gas) {

        if (null == s_gasToFluid) {
            buildMappings();
        }

        return s_gasToFluid.get(gas);
    }

    private static void buildMappings() {

        s_fluidToGas = new Object2ObjectOpenHashMap<>(8);
        s_gasToFluid = new Object2ObjectOpenHashMap<>(8);

        CodeHelper.getMinecraftServer().ifPresent(server -> {

            final ResourceLocation typeId = new ResourceLocation(Mods.MEKANISM.id(), "rotary");

            if (Registry.RECIPE_TYPE.containsKey(typeId)) {

                //noinspection unchecked
                server.getRecipeManager()
                        .getRecipesForType((IRecipeType<RotaryRecipe>)Objects.requireNonNull(Registry.RECIPE_TYPE.getOrDefault(typeId))).stream()
                        .filter(RotaryRecipe::hasFluidToGas)
                        .forEach(rotaryRecipe -> {

                            final GasStack gasStack = rotaryRecipe.getGasOutputRepresentation();

                            rotaryRecipe.getFluidInput().getRepresentations().stream()
                                    .filter(FluidMappingsRegistry::hasVaporFrom)
                                    .forEach(fluidStack -> {

                                        final IMapping<Fluid, Gas> mapping = IMapping.of(fluidStack.getFluid(), fluidStack.getAmount(),
                                                gasStack.getRaw(), (int) gasStack.getAmount());

                                        s_fluidToGas.put(fluidStack.getFluid(), mapping);
                                        s_gasToFluid.put(mapping.getProduct(), mapping.getReverse());
                                    });
                        });
            }
        });
    }

    @SuppressWarnings("FieldMayBeFinal")
    @CapabilityInject(IGasHandler.class)
    public static Capability<IGasHandler> CAPAP_MEKANISM_GASHANDLER = null;

    private static Map<Fluid, IMapping<Fluid, Gas>> s_fluidToGas;
    private static Map<Gas, IMapping<Gas, Fluid>> s_gasToFluid;

    private IGasHandler _consumer;
    private final FluidHandlerForwarder _capabilityForwarder;
    private final LazyOptional<IGasHandler> _capability;

    //endregion
}
