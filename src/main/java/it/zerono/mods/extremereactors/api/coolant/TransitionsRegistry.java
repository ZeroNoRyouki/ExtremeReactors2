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
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.extremereactors.api.ExtremeReactorsAPI;
import it.zerono.mods.extremereactors.api.IMapping;
import it.zerono.mods.extremereactors.api.internal.InternalDispatcher;
import it.zerono.mods.extremereactors.api.internal.modpack.wrapper.ApiWrapper;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Keep track of all the transitions between Coolants and Vapors and vice versa
 */
public final class TransitionsRegistry {

    public static final IMapping<Coolant, Vapor> EMPTY_VAPORIZATION = IMapping.of(Coolant.EMPTY, 0, Vapor.EMPTY, 0);
    public static final IMapping<Vapor, Coolant> EMPTY_CONDENSATION = IMapping.of(Vapor.EMPTY, 0, Coolant.EMPTY, 0);

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
                    ExtremeReactorsAPI.LOGGER.warn(MARKER, "Overwriting {} => {} vaporization transition", coolantName, vaporName);
                }

                if (s_condensations.containsKey(vapor)) {
                    ExtremeReactorsAPI.LOGGER.warn(MARKER, "Overwriting {} => {} condensation transition", vaporName, coolantName);
                }

                final IMapping<Coolant, Vapor> vaporization = IMapping.of(coolant, coolantAmount, vapor, vaporAmount);

                s_vaporizations.put(coolant, vaporization);
                s_condensations.put(vapor, vaporization.getReverse());
            }
        });
    }

    /**
     * Remove any transition that involve a Coolant or Vapor with the given name
     *
     * @param name the name of the Coolant or Vapor.
     */
    public static void remove(final String name) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));

        InternalDispatcher.dispatch("fluid-transition-remove", () -> {

            FluidsRegistry.getCoolant(name).ifPresent(coolant -> {

                final IMapping<Coolant, Vapor> vaporization = s_vaporizations.get(coolant);

                if (null != vaporization) {
                    s_condensations.remove(vaporization.getProduct());
                }

                s_vaporizations.remove(coolant);
            });

            FluidsRegistry.getVapor(name).ifPresent(vapor -> {

                final IMapping<Vapor, Coolant> condensation = s_condensations.get(vapor);

                if (null != condensation) {
                    s_vaporizations.remove(condensation.getProduct());
                }

                s_condensations.remove(vapor);
            });
        });
    }

    public static void processWrapper(final ApiWrapper wrapper) {

        if (!wrapper.Enabled) {
            return;
        }

        if (wrapper.FluidTransitions.WipeExistingValuesBeforeAdding) {

            // wipe all

            Log.LOGGER.info(WRAPPER, "Wiping all fluids transitions existing values");

            s_vaporizations.clear();
            s_condensations.clear();

        } else {

            // remove from list

            Arrays.stream(wrapper.FluidTransitions.Remove)
                    .filter(name -> !Strings.isNullOrEmpty(name))
                    .forEach(TransitionsRegistry::remove);
        }

        // add new

        Arrays.stream(wrapper.FluidTransitions.Add)
                .filter(Objects::nonNull)
                .forEach(mapping -> register(mapping.Source, mapping.SourceQuantity, mapping.Product, mapping.ProductQuantity));
    }

    public static Map<Coolant, IMapping<Coolant, Vapor>> getVaporizations() {
        return Object2ObjectMaps.unmodifiable(new Object2ObjectArrayMap<>(s_vaporizations));
    }

    public static Map<Vapor, IMapping<Vapor, Coolant>> getCondensations() {
        return Object2ObjectMaps.unmodifiable(new Object2ObjectArrayMap<>(s_condensations));
    }

    //region internals

    private TransitionsRegistry() {
    }

    private static final Map<Coolant, IMapping<Coolant, Vapor>> s_vaporizations = new Object2ObjectArrayMap<>(4);
    private static final Map<Vapor, IMapping<Vapor, Coolant>> s_condensations = new Object2ObjectArrayMap<>(4);

    private static final Marker MARKER = MarkerManager.getMarker("API/TransitionsRegistry").addParents(ExtremeReactorsAPI.MARKER);
    private static final Marker WRAPPER = MarkerManager.getMarker("ModPack API Wrapper").addParents(MARKER);

    //endregion
}
