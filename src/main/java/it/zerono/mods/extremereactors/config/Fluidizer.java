/*
 * Fluidizer
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
 * Do not remove or edit this header
 *
 */

package it.zerono.mods.extremereactors.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Fluidizer {

    public final ForgeConfigSpec.IntValue maxFluidizerHeight;
    public final ForgeConfigSpec.IntValue maxFluidizerSize;
    public final ForgeConfigSpec.IntValue energyPerRecipeTick;

    Fluidizer(final ForgeConfigSpec.Builder builder) {

        builder.comment("Define how Fluidizer works").push("fluidizer");

        this.maxFluidizerHeight = builder
                .comment("The maximum valid size of a Fluidizer in the Y dimension, in blocks.",
                        "Lower this if your server's players are building ginormous Fluidizer.")
                .translation("config.bigreactors.fluidizer.maxfluidizerheight")
                .worldRestart()
                .defineInRange("maxFluidizerHeight", 16, 3, 64);

        this.maxFluidizerSize = builder
                .comment("The maximum valid size of a Fluidizer in the X/Z plane, in blocks.",
                        "Lower this if your server's players are building ginormous Fluidizer.")
                .translation("config.bigreactors.fluidizer.maxfluidizersize")
                .worldRestart()
                .defineInRange("maxFluidizerSize", 16, 3, 64);

        this.energyPerRecipeTick = builder
                .comment("The amount of energy need to process a single tick of a recipe.")
                .translation("config.bigreactors.fluidizer.energyperrecipetick")
                .worldRestart()
                .defineInRange("energyPerRecipeTick", 25, 20, 1000);

        builder.pop();
    }
}
