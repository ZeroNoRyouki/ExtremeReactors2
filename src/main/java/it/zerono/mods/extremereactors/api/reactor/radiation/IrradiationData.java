/*
 *
 * IrradiationData.java
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

package it.zerono.mods.extremereactors.api.reactor.radiation;

public class IrradiationData {

    /**
     * Amount of fuel used by an irradiation
     */
    public float fuelUsage;

    /**
     * Amount of energy absorbed by the environment
     */
    public double environmentEnergyAbsorption; //TODO was:environmentRfChange

    /**
     * Amount of energy absorbed by the fuel
     */
    public double fuelEnergyAbsorption; //TODO was:fuelRfChange

    public float fuelAbsorbedRadiation; // in rad-units

    public IrradiationData() {

        this.fuelUsage = 0;
        this.environmentEnergyAbsorption = 0;
        this.fuelEnergyAbsorption = 0;
        this.fuelAbsorbedRadiation = 0f;
    }

    public double getEnvironmentHeatChange(int environmentVolume) {
        return EnergyConversion.getTemperatureFromVolumeAndEnergy(environmentVolume, this.environmentEnergyAbsorption);
    }

    public double getFuelHeatChange(int fuelVolume) {
        return EnergyConversion.getTemperatureFromVolumeAndEnergy(fuelVolume, this.fuelEnergyAbsorption);
    }
}
