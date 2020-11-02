/*
 *
 * EnergyConversion.java
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

public final class EnergyConversion {

    // Energy generated per fission event
    public static final float ENERGY_PER_RADIATION_UNIT = 10f;

    //TODO was:getRFFromVolumeAndTemp
    public static double getEnergyFromVolumeAndTemperature(int volume, double temperature) {
        return temperature * (double)volume * ENERGY_PER_CENTIGRADE_PER_UNIT_VOLUME;
    }

    //TODO was:getTempFromVolumeAndRF
    public static double getTemperatureFromVolumeAndEnergy(int volume, double energy) {
        return energy / ((double)volume * ENERGY_PER_CENTIGRADE_PER_UNIT_VOLUME);
    }

    //region internals

    private static final float ENERGY_PER_CENTIGRADE_PER_UNIT_VOLUME = 10f;
}
