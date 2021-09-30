/*
 *
 * ReactantsRegistry.java
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

package it.zerono.mods.extremereactors.api.reactor;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.extremereactors.api.ExtremeReactorsAPI;
import it.zerono.mods.extremereactors.api.internal.InternalDispatcher;
import it.zerono.mods.extremereactors.api.internal.modpack.wrapper.ApiWrapper;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Keep track of all the Reactants that could be used inside a Reactor Fuel Rod
 */
public final class ReactantsRegistry {

    /**
     * Check if a Reactant is registered
     *
     * @param name The name of the Reactant.
     * @return true if the Reactant is registered, false otherwise.
     */
    public static boolean contains(final String name) {
        return s_reactants.containsKey(name);
    }

    /**
     * Retrieve a registered Reactant by name.
     *
     * @param name The name of the Reactant.
     * @return the Reactant data if one is found, null otherwise
     */
    public static Optional<Reactant> get(final String name) {
        return Optional.ofNullable(s_reactants.get(name));
    }

    /**
     * Register a new Reactant.
     *
     * @param name The name of this reactant. Must be unique.
     * @param type The type of this reactant: Fuel or Waste.
     * @param rgbColour The color (in 0xRRGGBB form) to use when rendering fuel rods with this reactant in it.
     * @param translationKey The translation key for the name of the reactant.
     */
    public static void register(final String name, final ReactantType type, final int rgbColour, final String translationKey) {

        Preconditions.checkNotNull(type);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));

        InternalDispatcher.dispatch("reactant-register", () -> {

            if (s_reactants.containsKey(name)) {
                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Overwriting data for reactant {}", name);
            }

            s_reactants.put(name, new Reactant(name, type, rgbColour, translationKey, FuelProperties.DEFAULT));
        });
    }

    public static void registerFuel(final String name, final int rgbColour,
                                    float moderationFactor, float absorptionCoefficient, float hardnessDivisor,
                                    final String translationKey) {
        registerFuel(name, rgbColour, moderationFactor, absorptionCoefficient, hardnessDivisor,
                FuelProperties.DEFAULT.getFissionEventsPerFuelUnit(), FuelProperties.DEFAULT.getFuelUnitsPerFissionEvent(),
                translationKey);
    }

    /**
     * Register a new Fuel Reactant.
     *
     * @param name The name of this reactant. Must be unique.
     * @param rgbColour The color (in 0xRRGGBB form) to use when rendering fuel rods with this reactant in it.
     * @param moderationFactor How well this fuel moderate, but not stop, radiation. Must be greater or equal to 1.
     *                         Anything under 1.5 is "poor", 2-2.5 is "good", above 4 is "excellent".
     * @param absorptionCoefficient How well this fuel absorbs radiation. Must be between 0 and 1.
     * @param hardnessDivisor How this fuel tolerate hard radiation. Must be greater or equal to 1.
     * @param fissionEventsPerFuelUnit How many fission events occurs per unit of fuel.
     * @param fuelUnitsPerFissionEvent How many fuel units are used per fission event.
     * @param translationKey The translation key for the name of the reactant.
     */
    public static void registerFuel(final String name, final int rgbColour,
                                    float moderationFactor, float absorptionCoefficient, float hardnessDivisor,
                                    float fissionEventsPerFuelUnit, float fuelUnitsPerFissionEvent,
                                    final String translationKey) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));

        InternalDispatcher.dispatch("reactant-register", () -> {

            if (s_reactants.containsKey(name)) {
                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Overwriting data for fuel reactant {}", name);
            }

            s_reactants.put(name, new Reactant(name, ReactantType.Fuel, rgbColour, translationKey,
                    new FuelProperties(moderationFactor, absorptionCoefficient, hardnessDivisor,
                            fissionEventsPerFuelUnit, fuelUnitsPerFissionEvent)));
        });
    }

    /**
     * Remove a registered Reactant.
     *
     * @param name The name of the reactant to remove.
     */
    public static void remove(final String name) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
        InternalDispatcher.dispatch("reactant-remove", () -> s_reactants.remove(name));
    }

    public static void processWrapper(final ApiWrapper wrapper) {

        if (!wrapper.Enabled) {
            return;
        }

        if (wrapper.ReactorReactants.WipeExistingValuesBeforeAdding) {

            // wipe all

            Log.LOGGER.info(WRAPPER, "Wiping all existing Reactor reactants");
            s_reactants.clear();

        } else {

            // remove from list

            Arrays.stream(wrapper.ReactorReactants.Remove)
                    .filter(name -> !Strings.isNullOrEmpty(name))
                    .forEach(ReactantsRegistry::remove);
        }

        // add new values

        Arrays.stream(wrapper.ReactorReactants.Add)
                .filter(Objects::nonNull)
                .forEach((it.zerono.mods.extremereactors.api.internal.modpack.wrapper.Reactant w) -> {

                    if (w.IsFuel) {
                        registerFuel(w.Name, w.RgbColour, w.FuelProperties.ModerationFactor, w.FuelProperties.AbsorptionCoefficient,
                                w.FuelProperties.HardnessDivisor, w.TranslationKey);
                    } else {
                        register(w.Name, ReactantType.Waste, w.RgbColour, w.TranslationKey);
                    }
                });
    }

    //region internals

    private ReactantsRegistry() {
    }

    private static final Map<String, Reactant> s_reactants = Maps.newHashMap();

    private static final Marker MARKER = MarkerManager.getMarker("API/ReactantsRegistry").addParents(ExtremeReactorsAPI.MARKER);
    private static final Marker WRAPPER = MarkerManager.getMarker("ModPack API Wrapper").addParents(MARKER);

    //endregion
}
