/*
 *
 * Energizer.java
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

import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.variant.EnergizerVariant;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Energizer {

    public final ModConfigSpec.IntValue maxEnergizerHeight;
    public final ModConfigSpec.IntValue maxEnergizerSize;

    Energizer(ModConfigSpec.Builder builder) {

        builder.comment("Define how Energizer works").push("energizer");

        this.maxEnergizerHeight = builder
                .comment("The maximum valid size of a Energizer in the Y dimension, in blocks.",
                        "Lower this if your server's players are building ginormous Energizer.")
                .translation("config.bigreactors.energizer.maxenergizerheight")
                .worldRestart()
                .defineInRange("maxEnergizerHeight", EnergizerVariant.MULTIBLOCK_MAX_SIZE, 3,
                        EnergizerVariant.MULTIBLOCK_MAX_SIZE);

        this.maxEnergizerSize = builder
                .comment("The maximum valid size of a Energizer in the X/Z plane, in blocks.",
                        "Lower this if your server's players are building ginormous Energizer.")
                .translation("config.bigreactors.energizer.maxenergizersize")
                .worldRestart()
                .defineInRange("maxEnergizerSize", EnergizerVariant.MULTIBLOCK_MAX_SIZE, 3,
                        EnergizerVariant.MULTIBLOCK_MAX_SIZE);

        builder.pop();
    }
}
