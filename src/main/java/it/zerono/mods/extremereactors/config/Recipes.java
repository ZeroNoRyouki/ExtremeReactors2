/*
 *
 * Recipes.java
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

public class Recipes {

    public final ForgeConfigSpec.BooleanValue registerCharcoalForSmelting;
    public final ForgeConfigSpec.BooleanValue registerCoalForSmelting;
    public final ForgeConfigSpec.BooleanValue registerCoalBlockForSmelting;

    Recipes(final ForgeConfigSpec.Builder builder) {

        builder.comment("Recipes options").push("recipes");

        this.registerCharcoalForSmelting = builder
                .comment("If set, charcoal will be smeltable into graphite bars.",
                        "Disable this if other mods need to smelt charcoal into their own products.")
                .translation("config.bigreactors.recipes.registercharcoalforsmelting")
                .worldRestart()
                .define("registerCharcoalForSmelting", true);

        this.registerCoalForSmelting = builder
                .comment("If set, coal will be smeltable into graphite bars.",
                        "Disable this if other mods need to smelt coal into their own products.")
                .translation("config.bigreactors.recipes.registerCoalForSmelting")
                .worldRestart()
                .define("registerCoalForSmelting", true);

        this.registerCoalBlockForSmelting = builder
                .comment("If set, coal blocks will be smeltable into graphite blocks.",
                        "Disable this if other mods need to smelt coal blocks into their own products.")
                .translation("config.bigreactors.recipes.registerCoalBlockForSmelting")
                .worldRestart()
                .define("registerCoalBlockForSmelting", true);

        builder.pop();
    }
}
