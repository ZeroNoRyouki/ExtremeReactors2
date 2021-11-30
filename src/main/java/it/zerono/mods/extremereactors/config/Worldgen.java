/*
 *
 * Worldgen.java
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

package it.zerono.mods.extremereactors.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Worldgen {

    public final ForgeConfigSpec.BooleanValue enableWorldGen;
    public final ForgeConfigSpec.BooleanValue enableWorldRegeneration;
//    public final ForgeConfigSpec.ConfigValue<List<Integer>> dimensionWhitelist;
//    public final ForgeConfigSpec.BooleanValue useBlacklist;
    public final ForgeConfigSpec.IntValue userWorldGenVersion;
    public final ForgeConfigSpec.BooleanValue yelloriteOreEnableWorldGen;
    public final ForgeConfigSpec.IntValue yelloriteOreMaxClustersPerChunk;
    public final ForgeConfigSpec.IntValue yelloriteOrePerCluster;
    public final ForgeConfigSpec.IntValue yelloriteOreMaxY;
    public final ForgeConfigSpec.BooleanValue anglesiteOreEnableWorldGen;
    public final ForgeConfigSpec.IntValue anglesiteOreMaxClustersPerChunk;
    public final ForgeConfigSpec.IntValue anglesiteOrePerCluster;
    public final ForgeConfigSpec.BooleanValue benitoiteOreEnableWorldGen;
    public final ForgeConfigSpec.IntValue benitoiteOreMaxClustersPerChunk;
    public final ForgeConfigSpec.IntValue benitoiteOrePerCluster;

    Worldgen(final ForgeConfigSpec.Builder builder) {

        builder.comment("Define how ores generates in the world").push("worldgen");

        this.enableWorldGen = builder
                .comment("If false, disables all world gen from Extreme Reactors;",
                        "all other worldgen settings are automatically ignored.")
                .translation("config.bigreactors.worldgen.enableworldgen")
                .worldRestart()
                .define("enableWorldGen", true);

        this.enableWorldRegeneration = builder
                .comment("Re-run world gen in chunks that have already been generated (once they have been loaded), ",
                        "but have not been modified by Extreme Reactors before.")
                .translation("config.bigreactors.worldgen.enableworldregeneration")
                .worldRestart()
                .define("enableWorldRegeneration", false);

        this.userWorldGenVersion = builder
                .comment("User-set world generation version.",
                        "Increase this by one if you want Extreme Reactors to re-run world generation in already modified chunks.")
                .translation("config.bigreactors.worldgen.userworldgenversion")
                .worldRestart()
                .defineInRange("userWorldGenVersion", 1, 0, Integer.MAX_VALUE);
//
//        this.dimensionWhitelist = builder
//                .comment("Level gen will be performed only in the dimensions listed here")
//                .translation("config.bigreactors.worldgen.dimensionWhitelist")
//                .define("dimensionWhitelist", Collections.singletonList(0), object -> object instanceof Integer);
//
//        this.useBlacklist = builder
//                .comment("If true, dimensionWhitelist will be used as a black list.")
//                .translation("config.bigreactors.worldgen.useBlacklist")
//                .worldRestart()
//                .define("useBlacklist", false);
//

        this.yelloriteOreEnableWorldGen = builder
                .comment("Enable generation of Yellorite Ore.")
                .translation("config.bigreactors.worldgen.yelloriteoreenableworldgen")
                .worldRestart()
                .define("yelloriteOreEnableWorldGen", true);

        this.yelloriteOreMaxClustersPerChunk = builder
                .comment("Maximum number of Yellorite Ore clusters per chunk.")
                .translation("config.bigreactors.worldgen.yelloriteoremaxclustersperchunk")
                .worldRestart()
                .defineInRange("yelloriteOreMaxClustersPerChunk", 3, 1, 25);

        this.yelloriteOrePerCluster = builder
                .comment("Maximum number of Yellorite Ores to generate in each cluster.")
                .translation("config.bigreactors.worldgen.yelloriteorepercluster")
                .worldRestart()
                .defineInRange("yelloriteOrePerCluster", 5, 1, 16);

        this.yelloriteOreMaxY = builder
                .comment("Maximum height (Y coordinate) in the world to generate Yellorite Ore.")
                .translation("config.bigreactors.worldgen.yelloriteoremaxy")
                .worldRestart()
                .defineInRange("yelloriteOreMaxY", 32, 1, 256);

        this.anglesiteOreEnableWorldGen = builder
                .comment("Enable generation of Anglesite Ore.")
                .translation("config.bigreactors.worldgen.anglesiteoreenableworldgen")
                .worldRestart()
                .define("anglesiteOreEnableWorldGen", true);

        this.anglesiteOreMaxClustersPerChunk = builder
                .comment("Maximum number of Anglesite Ore clusters per chunk.")
                .translation("config.bigreactors.worldgen.anglesiteoremaxclustersperchunk")
                .worldRestart()
                .defineInRange("anglesiteOreMaxClustersPerChunk", 2, 1, 16);

        this.anglesiteOrePerCluster = builder
                .comment("Maximum number of Anglesite Ores to generate in each cluster.")
                .translation("config.bigreactors.worldgen.anglesiteorepercluster")
                .worldRestart()
                .defineInRange("anglesiteOrePerCluster", 5, 1, 16);

        this.benitoiteOreEnableWorldGen = builder
                .comment("Enable generation of Benitoite Ore.")
                .translation("config.bigreactors.worldgen.benitoiteoreenableworldgen")
                .worldRestart()
                .define("benitoiteOreEnableWorldGen", true);

        this.benitoiteOreMaxClustersPerChunk = builder
                .comment("Maximum number of Benitoite Ore clusters per chunk.")
                .translation("config.bigreactors.worldgen.benitoiteoremaxclustersperchunk")
                .worldRestart()
                .defineInRange("benitoiteOreMaxClustersPerChunk", 2, 1, 16);

        this.benitoiteOrePerCluster = builder
                .comment("Maximum number of Benitoite Ores to generate in each cluster.")
                .translation("config.bigreactors.worldgen.benitoiteorepercluster")
                .worldRestart()
                .defineInRange("benitoiteOrePerCluster", 5, 1, 16);

        builder.pop();
    }
}
