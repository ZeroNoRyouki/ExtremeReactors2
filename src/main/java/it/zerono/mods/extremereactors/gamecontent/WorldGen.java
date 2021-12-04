/*
 *
 * WorldGen.java
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

package it.zerono.mods.extremereactors.gamecontent;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.config.Config;
import it.zerono.mods.zerocore.lib.world.OreGenRegisteredFeature;
import it.zerono.mods.zerocore.lib.world.WorldGenManager;
import it.zerono.mods.zerocore.lib.world.WorldReGenHandler;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Predicate;

public final class WorldGen {

    public static void initialize() {

        s_regen = new WorldReGenHandler("ergen",
                Config.COMMON.worldgen.userWorldGenVersion::get,
                () -> Config.COMMON.worldgen.enableWorldGen.get() && Config.COMMON.worldgen.enableWorldRegeneration.get());

        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Feature.class, EventPriority.LOW, WorldGen::register);
    }

    public static void register(final RegistryEvent.Register<Feature<?>> event) {

        final Pair<OreGenRegisteredFeature, OreGenRegisteredFeature> yelloriteGenerators = WorldReGenHandler
                .oreVeinWithRegen("yellorite", ExtremeReactors::newID, Content.Blocks.YELLORITE_ORE_BLOCK,
                        WorldGenManager.oreMatch(Tags.Blocks.STONE),
                        Config.COMMON.worldgen.yelloriteOrePerCluster.get(),
                        Config.COMMON.worldgen.yelloriteOreMaxClustersPerChunk.get(),
                        15, Config.COMMON.worldgen.yelloriteOreMaxY.get());

        final Pair<OreGenRegisteredFeature, OreGenRegisteredFeature> anglesiteGenerators = WorldReGenHandler
                .oreVeinWithRegen("anglesite", ExtremeReactors::newID, Content.Blocks.ANGLESITE_ORE_BLOCK,
                        WorldGenManager.oreMatch(Tags.Blocks.END_STONES),
                        Config.COMMON.worldgen.anglesiteOrePerCluster.get(),
                        Config.COMMON.worldgen.anglesiteOreMaxClustersPerChunk.get(),
                        5, 200);

        final Pair<OreGenRegisteredFeature, OreGenRegisteredFeature> benitoiteGenerators = WorldReGenHandler
                .oreVeinWithRegen("benitoite", ExtremeReactors::newID, Content.Blocks.BENITOITE_ORE_BLOCK,
                        WorldGenManager.oreMatch(Tags.Blocks.NETHERRACK),
                        Config.COMMON.worldgen.benitoiteOrePerCluster.get(),
                        Config.COMMON.worldgen.benitoiteOreMaxClustersPerChunk.get(),
                        5, 256);

        registerToVanilla(yelloriteGenerators);
        registerToVanilla(anglesiteGenerators);
        registerToVanilla(benitoiteGenerators);

        final Predicate<BiomeLoadingEvent> yelloriteGenEnabled = e -> Config.COMMON.worldgen.enableWorldGen.get() && Config.COMMON.worldgen.yelloriteOreEnableWorldGen.get();
        final Predicate<BiomeLoadingEvent> anglesiteGenEnabled = e -> Config.COMMON.worldgen.enableWorldGen.get() && Config.COMMON.worldgen.anglesiteOreEnableWorldGen.get();
        final Predicate<BiomeLoadingEvent> benitoiteGenEnabled = e -> Config.COMMON.worldgen.enableWorldGen.get() && Config.COMMON.worldgen.benitoiteOreEnableWorldGen.get();

        final Predicate<Biome> yelloriteReGenEnabled = e -> Config.COMMON.worldgen.yelloriteOreEnableWorldGen.get();
        final Predicate<Biome> anglesiteReGenEnabled = e -> Config.COMMON.worldgen.anglesiteOreEnableWorldGen.get();
        final Predicate<Biome> benitoiteReGenEnabled = e -> Config.COMMON.worldgen.benitoiteOreEnableWorldGen.get();

        s_regen.addOreVein(yelloriteGenerators, yelloriteGenEnabled, yelloriteReGenEnabled);
        s_regen.addOreVein(anglesiteGenerators, anglesiteGenEnabled, anglesiteReGenEnabled);
        s_regen.addOreVein(benitoiteGenerators, benitoiteGenEnabled, benitoiteReGenEnabled);
    }

    //region internals

    private static void registerToVanilla(final Pair<OreGenRegisteredFeature, OreGenRegisteredFeature> features) {

        features.getLeft().register();
        features.getRight().register();
    }

    private static WorldReGenHandler s_regen;

    //endregion
}
