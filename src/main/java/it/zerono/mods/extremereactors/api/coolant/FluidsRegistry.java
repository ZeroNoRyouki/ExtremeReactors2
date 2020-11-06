/*
 *
 * CoolantsRegistry.java
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

package it.zerono.mods.extremereactors.api.coolant;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.zerono.mods.extremereactors.api.ExtremeReactorsAPI;
import it.zerono.mods.extremereactors.api.InternalDispatcher;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Map;
import java.util.Optional;

/**
 * Keep track of all the coolants and vapors that could be used in an active Reactor / Turbine setup
 */
public final class FluidsRegistry {

    /**
     * Check if a Coolant is registered.
     *
     * @param name the name of the Coolant.
     * @return true if the Coolant is registered, false otherwise.
     */
    public static boolean containsCoolant(final String name) {
        return s_coolants.containsKey(name);
    }

    /**
     * Check if a Vapor is registered.
     *
     * @param name the name of the Vapor.
     * @return true if the Vapor is registered, false otherwise.
     */
    public static boolean containsVapor(final String name) {
        return s_vapors.containsKey(name);
    }

    /**
     * Retrieve a registered Coolant by name.
     *
     * @param name the name of the Coolant.
     * @return the Coolant data if one is found, null otherwise
     */
    public static Optional<Coolant> getCoolant(final String name) {
        return Optional.ofNullable(s_coolants.get(name));
    }

    /**
     * Retrieve a registered Vapor by name.
     *
     * @param name the name of the Vapor.
     * @return the Vapor data if one is found, null otherwise
     */
    public static Optional<Vapor> getVapor(final String name) {
        return Optional.ofNullable(s_vapors.get(name));
    }

    /**
     * Register a new Coolant.
     *
     * @param name The name of this Coolant. Must be unique.
     * @param boilingPoint the temperature at which the coolant changes into a gas
     * @param enthalpyOfVaporization the amount of energy needed to transform the coolant into a gas
     * @param translationKey The translation key for the name of the coolant
     */
    public static void registerCoolant(final String name, final float boilingPoint, final float enthalpyOfVaporization,
                                       final String translationKey) {

        Preconditions.checkNotNull(name);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));

        InternalDispatcher.dispatch("fluid-register", () -> {

            if (s_coolants.containsKey(name)) {
                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Overwriting data for Coolant {}", name);
            }

            s_coolants.put(name, new Coolant(name, boilingPoint, enthalpyOfVaporization, translationKey));
        });
    }

    /**
     * Register a new Vapor.
     *
     * @param name The name of this Vapor. Must be unique.
     * @param fluidEnergyDensity the energy density of this vapor (in FE per mB)
     * @param translationKey The translation key for the name of the coolant
     */
    public static void registerVapor(final String name, final float fluidEnergyDensity, final String translationKey) {

        Preconditions.checkNotNull(name);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));

        InternalDispatcher.dispatch("fluid-register", () -> {

            if (s_vapors.containsKey(name)) {
                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Overwriting data for Vapor {}", name);
            }

            s_vapors.put(name, new Vapor(name, fluidEnergyDensity, translationKey));
        });
    }

    //region internals

    private FluidsRegistry() {
    }

    // - registered Coolants
    private static final Map<String, Coolant> s_coolants = new Object2ObjectArrayMap<>(2);
    // - registered Vapors
    private static final Map<String, Vapor> s_vapors = new Object2ObjectArrayMap<>(2);

    private static final Marker MARKER = MarkerManager.getMarker("API/FluidsRegistry").addParents(ExtremeReactorsAPI.MARKER);

    //endregion
}
