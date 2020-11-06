/*
 *
 * TransitionsRegistry.java
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
import it.zerono.mods.extremereactors.api.IMapping;
import it.zerono.mods.extremereactors.api.InternalDispatcher;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Map;
import java.util.Optional;

/**
 * Keep track of all the transitions between Coolants and Vapors and vice versa
 */
public final class TransitionsRegistry {

    /**
     * Check if a transition exists for the given Coolant
     *
     * @param coolant the Coolant
     * @return true if a transition is registered, false otherwise.
     */
    public static boolean contains(final Coolant coolant) {
        return s_vaporizations.containsKey(coolant);
    }

    /**
     * Check if a transition exists for the given Vapor
     *
     * @param vapor the Vapor
     * @return true if a transition is registered, false otherwise.
     */
    public static boolean contains(final Vapor vapor) {
        return s_condensations.containsKey(vapor);
    }

    /**
     * Retrieve a registered transition for the given Coolant
     *
     * @param coolant the Coolant
     * @return the transition data, if one is found
     */
    public static Optional<IMapping<Coolant, Vapor>> get(final Coolant coolant) {
        return Optional.ofNullable(s_vaporizations.get(coolant));
    }

    /**
     * Retrieve a registered transition for the given Vapor
     *
     * @param vapor the Vapor
     * @return the transition data, if one is found
     */
    public static Optional<IMapping<Vapor, Coolant>> get(final Vapor vapor) {
        return Optional.ofNullable(s_condensations.get(vapor));
    }

    /**
     * Register a new 1:1 transition from Coolant to Vapor and vice versa
     *
     * @param coolantName the name of the Coolant.
     * @param vaporName the name of the Vapor.
     */
    public static void register(final String coolantName, final String vaporName) {
        register(coolantName, 1, vaporName, 1);
    }

    /**
     * Register a new transition from Coolant to Vapor and vice versa
     *
     * @param coolantName the name of the Coolant.
     * @param coolantAmount the amount of Coolant that get converted to the given amount of Vapor.
     * @param vaporName the name of the Vapor.
     * @param vaporAmount the amount of Vapor that get produced from the given amount of Coolant.
     */
    public static void register(final String coolantName, final int coolantAmount,
                                final String vaporName, final int vaporAmount) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(coolantName));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(vaporName));

        InternalDispatcher.dispatch("fluid-transition-register", () -> {

            final Coolant coolant = FluidsRegistry.getCoolant(coolantName).orElse(null);
            final Vapor vapor = FluidsRegistry.getVapor(vaporName).orElse(null);

            if (null == coolant) {

                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Skipping registration of a transition for an unknown Coolant: {}", coolantName);

            } else if (null == vapor) {

                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Skipping registration of a transition for an unknown Vapor: {}", vaporName);

            } else {

                if (s_vaporizations.containsKey(coolant)) {
                    ExtremeReactorsAPI.LOGGER.warn(MARKER, "Overwriting {} => {} transition", coolantName, vaporName);
                }

                final IMapping<Coolant, Vapor> vaporization = IMapping.of(coolant, coolantAmount, vapor, vaporAmount);

                s_vaporizations.put(coolant, vaporization);
                s_condensations.put(vapor, vaporization.getReverse());
            }
        });
    }

    //region internals

    private TransitionsRegistry() {
    }

    private static final Map<Coolant, IMapping<Coolant, Vapor>> s_vaporizations = new Object2ObjectArrayMap<>(2);
    private static final Map<Vapor, IMapping<Vapor, Coolant>> s_condensations = new Object2ObjectArrayMap<>(2);

    private static final Marker MARKER = MarkerManager.getMarker("API/TransitionsRegistry").addParents(ExtremeReactorsAPI.MARKER);

    //endregion
}
