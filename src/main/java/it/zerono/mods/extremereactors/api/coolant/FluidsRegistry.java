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
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.extremereactors.api.ExtremeReactorsAPI;
import it.zerono.mods.extremereactors.api.internal.InternalDispatcher;
import it.zerono.mods.extremereactors.api.internal.modpack.wrapper.AddRemoveSection;
import it.zerono.mods.extremereactors.api.internal.modpack.wrapper.ApiWrapper;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.*;
import java.util.function.Consumer;

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
    @Deprecated // use the color-aware version below
    public static void registerCoolant(final String name, final float boilingPoint, final float enthalpyOfVaporization,
                                       final String translationKey) {
        registerCoolant(name, boilingPoint, enthalpyOfVaporization, translationKey, 0xFFFFFFFF);
    }

    /**
     * Register a new Coolant.
     *
     * @param name The name of this Coolant. Must be unique.
     * @param boilingPoint the temperature at which the coolant changes into a gas
     * @param enthalpyOfVaporization the amount of energy needed to transform the coolant into a gas
     * @param translationKey The translation key for the name of the coolant
     * @param rgbColour The color (in 0xRRGGBB form) to use when rendering the coolant.
     */
    public static void registerCoolant(final String name, final float boilingPoint, final float enthalpyOfVaporization,
                                       final String translationKey, final int rgbColour) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));

        InternalDispatcher.dispatch("fluid-register", () -> {

            if (s_coolants.containsKey(name)) {
                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Overwriting data for Coolant {}", name);
            }

            s_coolants.put(name, new Coolant(name, Colour.fromRGB(rgbColour), boilingPoint, enthalpyOfVaporization, translationKey));
        });
    }

    /**
     * Remove a registered Coolant.
     *
     * @param name The name of this Coolant.
     */
    public static void removeCoolant(final String name) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));

        InternalDispatcher.dispatch("fluid-remove", () -> {
            s_coolants.remove(name);
        });
    }

    /**
     * Register a new Vapor.
     *
     * @param name The name of this Vapor. Must be unique.
     * @param fluidEnergyDensity the energy density of this vapor (in FE per mB)
     * @param translationKey The translation key for the name of the coolant
     */
    @Deprecated // use the color-aware version below
    public static void registerVapor(final String name, final float fluidEnergyDensity, final String translationKey) {
        registerVapor(name, fluidEnergyDensity, translationKey, 0xFFFFFFFF);
    }

    /**
     * Register a new Vapor.
     *
     * @param name The name of this Vapor. Must be unique.
     * @param fluidEnergyDensity the energy density of this vapor (in FE per mB)
     * @param translationKey The translation key for the name of the coolant
     * @param rgbColour The color (in 0xRRGGBB form) to use when rendering the vapor.
     */
    public static void registerVapor(final String name, final float fluidEnergyDensity, final String translationKey,
                                     final int rgbColour) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));

        InternalDispatcher.dispatch("fluid-register", () -> {

            if (s_vapors.containsKey(name)) {
                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Overwriting data for Vapor {}", name);
            }

            s_vapors.put(name, new Vapor(name, Colour.fromRGB(rgbColour), fluidEnergyDensity, translationKey));
        });
    }

    /**
     * Remove a registered Vapor.
     *
     * @param name The name of this Vapor.
     */
    public static void removeVapor(final String name) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
        InternalDispatcher.dispatch("fluid-remove", () -> s_vapors.remove(name));
    }

    public static void processWrapper(final ApiWrapper wrapper) {

        if (!wrapper.Enabled) {
            return;
        }

        processWrapper("Coolants", wrapper.Coolants, s_coolants, FluidsRegistry::removeCoolant,
                (it.zerono.mods.extremereactors.api.internal.modpack.wrapper.Coolant w) ->
                        registerCoolant(w.Name, w.BoilingPoint, w.EnthalpyOfVaporization, w.TranslationKey));

        processWrapper("Vapors", wrapper.Vapors, s_vapors, FluidsRegistry::removeVapor,
                (it.zerono.mods.extremereactors.api.internal.modpack.wrapper.Vapor w) ->
                        registerVapor(w.Name, w.FluidEnergyDensity, w.TranslationKey));
    }

    public static List<Coolant> getCoolants() {
        return ObjectLists.unmodifiable(new ObjectArrayList<>(s_coolants.values()));
    }

    public static List<Vapor> getVapors() {
        return ObjectLists.unmodifiable(new ObjectArrayList<>(s_vapors.values()));
    }

    //region internals

    private FluidsRegistry() {
    }

    private static <X, XWrapper> void processWrapper(final String objectName, final AddRemoveSection<XWrapper> wrapperSection,
                                                     final Map<String, X> registry, final Consumer<String> removeAction,
                                                     final Consumer<XWrapper> addAction) {

        if (wrapperSection.WipeExistingValuesBeforeAdding) {

            // wipe all

            Log.LOGGER.info(WRAPPER, "Wiping all existing {} sources", objectName);
            registry.clear();

        } else {

            // remove from list

            wrapperSection.removals()
                    .filter(name -> !Strings.isNullOrEmpty(name))
                    .forEach(removeAction);
        }

        // add new values

        wrapperSection.additions()
                .filter(Objects::nonNull)
                .forEach(addAction);
    }

    // - registered Coolants
    private static final Map<String, Coolant> s_coolants = new Object2ObjectArrayMap<>(2);
    // - registered Vapors
    private static final Map<String, Vapor> s_vapors = new Object2ObjectArrayMap<>(2);

    private static final Marker MARKER = MarkerManager.getMarker("API/FluidsRegistry").addParents(ExtremeReactorsAPI.MARKER);
    private static final Marker WRAPPER = MarkerManager.getMarker("ModPack API Wrapper").addParents(MARKER);

    //endregion
}
