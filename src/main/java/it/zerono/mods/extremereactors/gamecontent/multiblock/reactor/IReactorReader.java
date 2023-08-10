/*
 *
 * IReactorReader.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor;

import it.zerono.mods.extremereactors.api.reactor.FuelProperties;
import it.zerono.mods.extremereactors.api.reactor.Reactant;
import it.zerono.mods.extremereactors.gamecontent.multiblock.IMachineReader;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.IMultiblockReactorVariant;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Optional;
import java.util.function.DoubleSupplier;

public interface IReactorReader
        extends IMachineReader {

    IMultiblockReactorVariant getVariant();

    OperationalMode getOperationalMode();

    /**
     * @return the amount of fuel contained in the Reactor
     */
    int getFuelAmount();

    /**
     * @return the amount of waste contained in the Reactor
     */
    int getWasteAmount();

    /**
     * @return an integer representing the maximum amount of fuel and waste, combined, the Reactor can contain
     */
    int getCapacity();

    Optional<Reactant> getFuel();

    FuelProperties getFuelProperties();

    Optional<Reactant> getWaste();

    float getFuelFertility();

    DoubleSupplier getFuelHeatValue();

    DoubleSupplier getReactorHeatValue();

    WasteEjectionSetting getWasteEjectionMode();

    FuelRodsLayout getFuelRodsLayout();

    /**
     * @return the number of Fuel Rods in the Reactor
     */
    int getFuelRodsCount();

    /**
     * @return the number of Control Rods in the Reactor
     */
    int getControlRodsCount();

    /**
     * @return the number of Power Taps in the Reactor
     */
    int getPowerTapsCount();

    /**
     * Returns the maximum amount of energy that can be stored expressed in the requested {@link EnergySystem}
     *
     * @param system the {@link EnergySystem} used by the request
     */
    WideAmount getCapacity(EnergySystem system);

    double getEnergyStoredPercentage();

    //TODO used???
    List<BlockPos> getControlRodLocations();

    Stats getUiStats();

    int getCoolantAmount();

    int getVaporAmount();
}
