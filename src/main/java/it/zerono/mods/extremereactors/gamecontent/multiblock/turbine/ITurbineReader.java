/*
 *
 * ITurbineReader.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine;

import it.zerono.mods.extremereactors.gamecontent.multiblock.IMachineReader;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.IFluidContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.IMultiblockTurbineVariant;

public interface ITurbineReader
        extends IMachineReader {

    IMultiblockTurbineVariant getVariant();

    IFluidContainer getFluidContainer();

    boolean isAssembledAndActive();

    /**
     * @return the amount of coolant contained in the Turbine
     */
    int getCoolantAmount();

    /**
     * @return the amount of vapor contained in the Turbine
     */
    int getVaporAmount();

    /**
     * @return an integer representing the maximum amount of coolant and vapor, combined, the Turbine can contain
     */
    int getCapacity();

    float getRotorSpeed();

    float getMaxRotorSpeed();

    int getRotorBladesCount();

    int getMaxIntakeRate();

    int getMaxIntakeRateHardLimit();

    double getEnergyGeneratedLastTick();

    int getFluidConsumedLastTick();

    float getRotorEfficiencyLastTick();

    int getRotorMass();

    VentSetting getVentSetting();

    boolean isInductorEngaged();

    double getEnergyStoredPercentage();
}
