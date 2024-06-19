/*
 *
 * IFluidContainer.java
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

import it.zerono.mods.extremereactors.api.coolant.Coolant;
import it.zerono.mods.extremereactors.api.coolant.Vapor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockFluidGeneratorVariant;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.stack.OperationMode;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.FluidStackData;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.Optional;
import java.util.function.Function;

public interface IFluidContainer {

    Optional<Fluid> getGas();

    Optional<Fluid> getLiquid();

    int getGasAmount();

    int getLiquidAmount();

    default double getGasStoredPercentage() {
        return (double)this.getGasAmount() / (double)this.getCapacity();
    }

    default double getLiquidStoredPercentage() {
        return (double)this.getLiquidAmount() / (double)this.getCapacity();
    }

    int getCapacity();

    void setCapacity(int capacity);

    FluidStack extract(FluidType index, int amount, OperationMode mode);

    double getLiquidTemperature(double reactorTemperature);

    double onAbsorbHeat(double energyAbsorbed, IMultiblockFluidGeneratorVariant variant);

    int onCondensation(int vaporUsed, boolean ventAllCoolant, IMultiblockFluidGeneratorVariant variant);

    int getLiquidVaporizedLastTick();

    IFluidHandler getWrapper(IoDirection portDirection);

    Optional<Coolant> getCoolant();

    Optional<Vapor> getVapor();

    <T> T mapCoolant(final Function<Coolant, T> mapper, final T defaultValue);

    <T> T mapVapor(final Function<Vapor, T> mapper, final T defaultValue);

    FluidStackData getCoolantStackData(int sampleFrequency, ModContainer container);

    FluidStackData getVaporStackData(int sampleFrequency, ModContainer container);
}
