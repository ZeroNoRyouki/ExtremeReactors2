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
import it.zerono.mods.extremereactors.gamecontent.mekanism.IMekanismService;
import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity;
import it.zerono.mods.zerocore.base.multiblock.part.io.IOPortBlockCapabilitySource;
import it.zerono.mods.zerocore.base.multiblock.part.io.fluid.AbstractFluidPortHandler;
import it.zerono.mods.zerocore.base.multiblock.part.io.fluid.IFluidPort;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.fluid.handler.FluidHandlerForwarder;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;
import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.api.recipes.RotaryRecipe;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;

public class FluidPortHandlerMekanism<Controller extends AbstractCuboidMultiblockController<Controller>,
        Port extends AbstractMultiblockEntity<Controller> & IFluidPort>
    extends AbstractFluidPortHandler<Controller, Port>
    implements IChemicalHandler {

    public FluidPortHandlerMekanism(Port port) {

        super(port, IoMode.Passive);
        this._capabilityForwarder = new FluidHandlerForwarder(EmptyFluidHandler.INSTANCE);
        this._remoteCapabilitySource = new IOPortBlockCapabilitySource<>(port, Capabilities.CHEMICAL.block());
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

        final var consumer = this._remoteCapabilitySource.getCapability();

        if (null == consumer || this.isPassive()) {
            return 0;
        }

        return (int)(consumer.insertChemical(getGasStack(stack), Action.EXECUTE).getAmount());
    }

    /**
     * If this is a Active Fluid Port in input mode, try to get fluids from the connected consumer (if there is one)
     *
     * @param destination the destination IFluidHandler that will receive the fluid
     * @param maxAmount   the maximum amount of fluid to acquire
     */
    @Override
    public int inputFluid(IFluidHandler destination, int maxAmount) {
        return 0;
    }

    //endregion
    //region IIOPortHandler

    @Override
    public boolean isConnected() {
        return null != this._remoteCapabilitySource.getCapability();
    }

    @Override
    public void onPortChanged() {
        this._remoteCapabilitySource.onPortChanged();
    }

    @Override
    public void update(Function<@NotNull IoDirection, @NotNull IFluidHandler> handlerProvider) {
        this._capabilityForwarder.setHandler(handlerProvider.apply(IoDirection.Output));
    }

    //endregion
    //region IChemicalHandler

    @Override
    public int getChemicalTanks() {
        return 1;
    }

    @Override
    public ChemicalStack getChemicalInTank(int idx) {
        return 0 != idx ? ChemicalStack.EMPTY : getGasStack(this._capabilityForwarder.getFluidInTank(0));
    }

    @Override
    public void setChemicalInTank(int idx, ChemicalStack stack) {
        // no insertions
    }

    @Override
    public long getChemicalTankCapacity(int idx) {
        return 0 == idx ? this._capabilityForwarder.getTankCapacity(0) : 0;
    }

    @Override
    public boolean isValid(int idx, ChemicalStack stack) {
        // no insertions
        return false;
    }

    @Override
    public ChemicalStack insertChemical(int idx, ChemicalStack stack, Action action) {
        // no insertions
        return stack;
    }

    @Override
    public ChemicalStack extractChemical(int idx, long amount, Action action) {

        final FluidStack currentStack = this._capabilityForwarder.getFluidInTank(0);

        if (0 != idx || currentStack.isEmpty()) {
            return ChemicalStack.EMPTY;
        }

        final IMapping<Fluid, Chemical> fluidMapping = getFluidMapping(currentStack.getFluid());

        if (null == fluidMapping) {
            return ChemicalStack.EMPTY;
        }

        final IMapping<Chemical, Fluid> gasMapping = getGasMapping(fluidMapping.getProduct());

        if (null == gasMapping) {
            return ChemicalStack.EMPTY;
        }

        final int amountToRemove = gasMapping.getProductAmount((int)Math.min(amount, Integer.MAX_VALUE));
        final FluidStack removed = this._capabilityForwarder.drain(amountToRemove, action.toFluidAction());

        return getGasStack(removed);
    }

    //endregion
    //region internals

    private static ChemicalStack getGasStack(final FluidStack fluidStack) {
        return fluidStack.isEmpty() ? ChemicalStack.EMPTY : getGasStack(fluidStack.getFluid(), fluidStack.getAmount());
    }

    private static ChemicalStack getGasStack(final Fluid fluid, final int amount) {

        final IMapping<Fluid, Chemical> mapping = getFluidMapping(fluid);

        return null != mapping ? new ChemicalStack(mapping.getProduct(), mapping.getProductAmount(amount)) : ChemicalStack.EMPTY;
    }

    @Nullable
    private static IMapping<Fluid, Chemical> getFluidMapping(final Fluid fluid) {

        if (null == s_fluidToGas) {
            buildMappings();
        }

        return s_fluidToGas.get(fluid);
    }

    @Nullable
    private static IMapping<Chemical, Fluid> getGasMapping(final Chemical gas) {

        if (null == s_gasToFluid) {
            buildMappings();
        }

        return s_gasToFluid.get(gas);
    }

    private static void buildMappings() {

        s_fluidToGas = new Object2ObjectOpenHashMap<>(8);
        s_gasToFluid = new Object2ObjectOpenHashMap<>(8);

        CodeHelper.getMinecraftServer().ifPresent(server -> {

            final ResourceLocation typeId = ResourceLocation.fromNamespaceAndPath(IMekanismService.SERVICE.getId(), "rotary");
            @SuppressWarnings("unchecked")
            final RecipeType<RotaryRecipe> type = (RecipeType<RotaryRecipe>) BuiltInRegistries.RECIPE_TYPE.get(typeId);

            if (null != type) {

                server.getRecipeManager()
                        .getAllRecipesFor(type).stream()
                        .map(RecipeHolder::value)
                        .filter(RotaryRecipe::hasFluidToChemical)
                        .forEach(rotaryRecipe -> {

                            final ChemicalStack gasStack = rotaryRecipe.getChemicalOutputDefinition().get(0);

                            rotaryRecipe.getFluidInput().getRepresentations().stream()
                                    .filter(fluidStack -> FluidMappingsRegistry.hasVaporFrom(fluidStack.getFluid()))
                                    .forEach(fluidStack -> {

                                        final IMapping<Fluid, Chemical> mapping = IMapping.of(fluidStack.getFluid(), fluidStack.getAmount(),
                                                gasStack.getChemical(), (int) gasStack.getAmount());

                                        s_fluidToGas.put(fluidStack.getFluid(), mapping);
                                        s_gasToFluid.put(mapping.getProduct(), mapping.getReverse());
                                    });
                        });
            }
        });
    }

    private static Map<Fluid, IMapping<Fluid, Chemical>> s_fluidToGas;
    private static Map<Chemical, IMapping<Chemical, Fluid>> s_gasToFluid;

    private final FluidHandlerForwarder _capabilityForwarder;
    private final IOPortBlockCapabilitySource<Controller, Port, IChemicalHandler> _remoteCapabilitySource;

    //endregion
}
