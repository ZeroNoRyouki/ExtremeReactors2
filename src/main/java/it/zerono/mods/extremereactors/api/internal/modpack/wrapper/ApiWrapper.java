/*
 *
 * ApiWrapper.java
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

package it.zerono.mods.extremereactors.api.internal.modpack.wrapper;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.extremereactors.api.ExtremeReactorsAPI;
import it.zerono.mods.extremereactors.api.coolant.FluidMappingsRegistry;
import it.zerono.mods.extremereactors.api.coolant.FluidsRegistry;
import it.zerono.mods.extremereactors.api.coolant.TransitionsRegistry;
import it.zerono.mods.extremereactors.api.reactor.ModeratorsRegistry;
import it.zerono.mods.extremereactors.api.reactor.ReactantMappingsRegistry;
import it.zerono.mods.extremereactors.api.reactor.ReactantsRegistry;
import it.zerono.mods.extremereactors.api.reactor.ReactionsRegistry;
import it.zerono.mods.extremereactors.api.turbine.CoilMaterialRegistry;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ApiWrapper {

    public boolean Enabled;

    //region api.coolant

    public AddRemoveSection<Coolant> Coolants;
    public AddRemoveSection<Vapor> Vapors;

    public AddRemoveSection<SourceTag> CoolantSources;
    public AddRemoveSection<SourceTag> VaporSources;
    public AddRemoveSection<Mapping> FluidTransitions;

    //endregion
    //region api.reactor

    public AddRemoveSection<Reactant> ReactorReactants;
    public AddRemoveSection<SourceTag> ReactorReactantSources;
    public AddRemoveSection<Reaction> ReactorReactantReaction;
    public AddRemoveSection<Moderator> ReactorSolidModerators;
    public AddRemoveSection<Moderator> ReactorFluidModerators;

    //endregion
    //region api.turbine

    public AddRemoveSection<CoilMaterial> TurbineCoils;

    //endregion

    public ApiWrapper() {

        this.Enabled = false;

        this.Coolants = new AddRemoveSection<>(Coolant[]::new);
        this.Vapors = new AddRemoveSection<>(Vapor[]::new);
        this.CoolantSources = new AddRemoveSection<>(SourceTag[]::new);
        this.VaporSources = new AddRemoveSection<>(SourceTag[]::new);
        this.FluidTransitions = new AddRemoveSection<>(Mapping[]::new);

        this.ReactorReactants = new AddRemoveSection<>(Reactant[]::new);
        this.ReactorReactantSources = new AddRemoveSection<>(SourceTag[]::new);
        this.ReactorReactantReaction = new AddRemoveSection<>(Reaction[]::new);
        this.ReactorSolidModerators = new AddRemoveSection<>(Moderator[]::new);
        this.ReactorFluidModerators = new AddRemoveSection<>(Moderator[]::new);

        this.TurbineCoils = new AddRemoveSection<>(CoilMaterial[]::new);
    }

    public static void generateSampleFile() {

        final ApiWrapper wrapper = new ApiWrapper();

        // sample coolant/vapor system values

        wrapper.Coolants.Add = ImmutableList.of(
                new Coolant("coolantA", "key.for.coolantA.name", 100.0f, 24.3f),
                new Coolant("coolantB", "key.for.coolantB.name.translation", 85.34f, 12.0f)
        ).toArray(new Coolant[0]);
        wrapper.Coolants.Remove = new String[] {"someCoolant", "water", "anotherCoolantName"};

        wrapper.Vapors.Add = ImmutableList.of(
                new Vapor("vapor1", "vapor1.name.translation.key", 2.0f),
                new Vapor("vapor2", "vapor2.the_name.translation.key.for_the_vapor", 1.25f)
        ).toArray(new Vapor[0]);
        wrapper.Vapors.Remove = new String[] {"vaporA", "theVapor4", "steam"};

        wrapper.CoolantSources.Add = ImmutableList.of(
                new SourceTag("minecraft:lava", "coolantA", 7),
                new SourceTag("modid:fluidid", "coolantB", 1)
        ).toArray(new SourceTag[0]);
        wrapper.CoolantSources.Remove = new String[] {"minecraft:water", "minecraft:lava", "anothermod:fluid"};

        wrapper.VaporSources.Add = ImmutableList.of(
                new SourceTag("forge:steam", "vapor2", 1),
                new SourceTag("modid:fluidid2", "vapor1", 2)
        ).toArray(new SourceTag[0]);
        wrapper.VaporSources.Remove = new String[] {"minecraft:water", "minecraft:lava", "anothermod:fluid"};

        wrapper.FluidTransitions.WipeExistingValuesBeforeAdding = true;
        wrapper.FluidTransitions.Add = ImmutableList.of(
                new Mapping("coolantA", 1, "vapor2", 1),
                new Mapping("coolantB", 1, "vapor1", 2)
        ).toArray(new Mapping[0]);
        //wrapper.FluidTransitions.Remove = new String[] {"coolantName", "vaporname", "vaporname2"};

        // sample reactor data values

        wrapper.ReactorReactants.Add = ImmutableList.of(
                new Reactant("newReactant2", "mod.reactant2.name.key", true, 0xc6ba54),
                new Reactant("newReactant3", "mod.reactant3.name.key", true, 0xc6ba54, new FuelProperties(2.5f, 0.9f, 1.3f)),
                new Reactant("mywaste", "mywaste.name.key", false, 0x00cd35)
        ).toArray(new Reactant[0]);
        wrapper.ReactorReactants.Remove = new String[] {"cyanite"};

        wrapper.ReactorReactantSources.Add = ImmutableList.of(
                new SourceTag("forge:ingots/yellorium", "yellorium", 1),
                new SourceTag("forge:ingots/iron", "yellorium", 2)
        ).toArray(new SourceTag[0]);
        wrapper.ReactorReactantSources.Remove = new String[] {"forge:ingots/cyanite"};

        wrapper.ReactorReactantReaction.Add = ImmutableList.of(
                new Reaction("yellorium", "cyanite", 1.05f, 0.01f),
                new Reaction("newReactant2", "mywaste", 1.5f, 0.03f)
        ).toArray(new Reaction[0]);
        wrapper.ReactorReactantReaction.Remove = new String[] {"yellorium", "newReactant2"};

        wrapper.ReactorSolidModerators.Add = ImmutableList.of(
                new Moderator("forge:storage_blocks/iron", 0.50f, 0.75f, 1.40f, 0.6f)
        ).toArray(new Moderator[0]);
        wrapper.ReactorSolidModerators.Remove = new String[] {"forge:storage_blocks/diamond", "forge:storage_blocks/copper"};

        wrapper.ReactorFluidModerators.Add = ImmutableList.of(
                new Moderator("minecraft:water", 0.50f, 0.75f, 1.40f, 0.6f)
        ).toArray(new Moderator[0]);
        wrapper.ReactorFluidModerators.Remove = new String[] {"minecraft:flowing_water"};

        // sample turbine data values

        wrapper.TurbineCoils.Add = ImmutableList.of(
                new CoilMaterial("forge:storage_blocks/iron", 1.0f, 1.0f, 1.0f)
        ).toArray(new CoilMaterial[0]);
        wrapper.TurbineCoils.Remove = new String[] {"forge:storage_blocks/gold", "forge:storage_blocks/copper"};

        // generate file

        saveToJSON(wrapper);
    }

    public static void processFile() {

        final ApiWrapper wrapper = createFromJSON();

        if (null == wrapper || !wrapper.Enabled) {
            return;
        }

        ExtremeReactorsAPI.LOGGER.info(WRAPPER, "Processing ModPack API Wrapper config");

        ReactantsRegistry.processWrapper(wrapper);
        ReactantMappingsRegistry.processWrapper(wrapper);
        ReactionsRegistry.processWrapper(wrapper);
        ModeratorsRegistry.processWrapper(wrapper);

        FluidsRegistry.processWrapper(wrapper);
        FluidMappingsRegistry.processWrapper(wrapper);
        TransitionsRegistry.processWrapper(wrapper);

        CoilMaterialRegistry.processWrapper(wrapper);
    }

    //region internals

    @Nullable
    private static ApiWrapper createFromJSON() {

        final Path filePath = getPath();

        if (Files.exists(filePath)) {

            try {
                return s_gson.fromJson(new JsonReader(new FileReader(filePath.toFile())), ApiWrapper.class);
            } catch (FileNotFoundException e) {
                Log.LOGGER.error(e);
            }
        }

        return null;
    }

    private static void saveToJSON(final ApiWrapper wrapper) {

        FileWriter file = null;

        try {

            file = new FileWriter(getPath().toFile());
            s_gson.toJson(wrapper, file);

        } catch (IOException e) {

            Log.LOGGER.error(e);

        } finally {

            if (null != file) {

                try {

                    file.flush();
                    file.close();

                } catch (IOException e) {
                    Log.LOGGER.error(e);
                }
            }
        }
    }

    private static Path getPath() {
        return FMLPaths.CONFIGDIR.get().resolve("extremereactors/modpack_api.json");
    }

    private static final Gson s_gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .disableHtmlEscaping()
            .create();

    private static final Marker WRAPPER = MarkerManager.getMarker("ModPack API Wrapper").addParents(ExtremeReactorsAPI.MARKER);

    //endregion
}
