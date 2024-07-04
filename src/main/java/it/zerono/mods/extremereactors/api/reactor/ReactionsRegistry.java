/*
 *
 * ReactionsRegistry.java
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
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.extremereactors.api.ExtremeReactorsAPI;
import it.zerono.mods.extremereactors.api.internal.InternalDispatcher;
import it.zerono.mods.extremereactors.api.internal.modpack.wrapper.ApiWrapper;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Keep track of all the Reactions that could happen inside a Reactor Fuel Rod
 */
public final class ReactionsRegistry {

    /**
     * Check if a Reaction for the given source Reactant is registered
     *
     * @param sourceReactant The source Reactant.
     * @return true if the Reaction is registered, false otherwise.
     */
    public static boolean contains(final Reactant sourceReactant) {
        return s_reactions.containsKey(sourceReactant);
    }

    /**
     * Retrieve a registered Reaction for the given source Reactant.
     *
     * @param sourceReactant The source Reactant.
     * @return the Reaction data if one is found, null otherwise
     */
    public static Optional<Reaction> get(final Reactant sourceReactant) {
        return Optional.ofNullable(s_reactions.get(sourceReactant));
    }

    /**
     * Register a new Reaction.
     *
     * @param sourceReactantName  The name of the source reactant.
     * @param productReactantName The name of the product reactant.
     * @param reactivity          The reactivity value.
     * @param fissionRate         The fission rate value.
     */
    public static void register(final String sourceReactantName, final String productReactantName,
                                final float reactivity, final float fissionRate) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(sourceReactantName));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(productReactantName));

        InternalDispatcher.dispatch("reaction-register", () -> {

            final Reactant source = ReactantsRegistry.get(sourceReactantName).orElse(null);
            final Reactant product = ReactantsRegistry.get(productReactantName).orElse(null);

            if (null == source) {

                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Skipping registration for an unknown source reactant: {}", sourceReactantName);

            } else if (null == product) {

                ExtremeReactorsAPI.LOGGER.warn(MARKER, "Skipping registration for an unknown product reactant: {}", productReactantName);

            } else {

                if (s_reactions.containsKey(source)) {
                    ExtremeReactorsAPI.LOGGER.warn(MARKER, "Overwriting {} => {} reaction", sourceReactantName, productReactantName);
                }

                s_reactions.put(source, new Reaction(source, product, reactivity, fissionRate));
            }
        });
    }

    public static void remove(final String sourceReactantName) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(sourceReactantName));;

        InternalDispatcher.dispatch("reaction-remove", () -> {
            ReactantsRegistry.get(sourceReactantName).ifPresent(s_reactions::remove);
        });
    }

    public static void processWrapper(final ApiWrapper wrapper) {

        if (!wrapper.Enabled) {
            return;
        }

        if (wrapper.ReactorReactantReaction.WipeExistingValuesBeforeAdding) {

            // wipe all

            Log.LOGGER.info(WRAPPER, "Wiping all existing Reactor reactants reactions");
            s_reactions.clear();

        } else {

            // remove from list

            wrapper.ReactorReactantReaction.removals()
                    .filter(name -> !Strings.isNullOrEmpty(name))
                    .forEach(ReactionsRegistry::remove);
        }

        // add new values

        wrapper.ReactorReactantReaction.additions()
                .filter(Objects::nonNull)
                .forEach((it.zerono.mods.extremereactors.api.internal.modpack.wrapper.Reaction w) ->
                        register(w.SourceReactant, w.ProductReactant, w.Reactivity, w.FissionRate));
    }

    public static List<Reaction> getReactions() {
        return ObjectLists.unmodifiable(new ObjectArrayList<>(s_reactions.values()));
    }

    //region /er support

    public static List<String> getReactionsNames() {
        return s_reactions.keySet().stream()
                .map(Reactant::getName)
                .sorted(String::compareTo)
                .toList();
    }

    //endregion
    //region internals

    private ReactionsRegistry() {
    }

    private static final Map<Reactant, Reaction> s_reactions = Maps.newHashMap();

    private static final Marker MARKER = MarkerManager.getMarker("API/ReactionsRegistry").addParents(ExtremeReactorsAPI.MARKER);
    private static final Marker WRAPPER = MarkerManager.getMarker("ModPack API Wrapper").addParents(MARKER);

    //endregion
}
