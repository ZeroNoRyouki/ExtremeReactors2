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
import com.google.common.collect.Maps;
import it.zerono.mods.extremereactors.api.ExtremeReactorsAPI;
import it.zerono.mods.extremereactors.api.InternalDispatcher;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Map;
import java.util.Optional;

/**
 * Keep track of all the coolants that could be used in an active Reactor to produce fluids to drive a Turbine
 */
public final class CoolantsRegistry {

    /**
     * Check if a Coolant is registered
     *
     * @param name the name of the Coolant.
     * @return true if the Coolant is registered, false otherwise.
     */
    public static boolean contains(final String name) {
        return s_coolants.containsKey(name);
    }

    /**
     * Retrieve a registered Coolant by name.
     *
     * @param name the name of the Coolant.
     * @return the Coolant data if one is found, null otherwise
     */
    public static Optional<Coolant> get(final String name) {
        return Optional.ofNullable(s_coolants.get(name));
    }

    /**
     * Register a new Coolant.
     *
     * @param name The name of this coolant. Must be unique.
     * @param boilingPoint the temperature at which the coolant changes into a gas
     * @param enthalpyOfVaporization the amount of energy needed to transform the coolant into a gas
     * @param translationKey The translation key for the name of the coolant
     */
    public static void register(final String name, final float boilingPoint, final float enthalpyOfVaporization,
                                final String translationKey) {

        Preconditions.checkNotNull(name);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));

        InternalDispatcher.dispatch("coolant-register", () -> {

            if (s_coolants.containsKey(name)) {
                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Overwriting data for coolant {}", name);
            }

            s_coolants.put(name, new Coolant(name, boilingPoint, enthalpyOfVaporization, translationKey));
        });
    }

    //region internals

    private CoolantsRegistry() {
    }

    private static final Map<String, Coolant> s_coolants = Maps.newHashMap();

    private static final Marker MARKER = MarkerManager.getMarker("API/CoolantsRegistry").addParents(ExtremeReactorsAPI.MARKER);

    //endregion
}
