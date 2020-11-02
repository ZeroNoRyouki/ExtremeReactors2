/*
 *
 * General.java
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

public class General {

    public final ForgeConfigSpec.DoubleValue fuelUsageMultiplier;
    public final ForgeConfigSpec.DoubleValue powerProductionMultiplier;
    public final ForgeConfigSpec.IntValue ticksPerRedstoneUpdate;

    General(final ForgeConfigSpec.Builder builder) {

        builder.comment("General options").push("general");

        this.fuelUsageMultiplier = builder
                .comment("A multiplier for balancing fuel consumption.")
                .translation("config.bigreactors.general.fuelusagemultiplier")
                .worldRestart()
                .defineInRange("fuelUsageMultiplier", 1.0, 0.5, 100.0);

        this.powerProductionMultiplier = builder
                .comment("A multiplier for balancing overall power production from Extreme Reactors generators.")
                .translation("config.bigreactors.general.powerproductionmultiplier")
                .worldRestart()
                .defineInRange("powerProductionMultiplier", 1.0, 0.5, 100.0);

        this.ticksPerRedstoneUpdate = builder
                .comment("Number of ticks between updates for the Redstone Port.")
                .translation("config.bigreactors.general.ticksperredstoneupdate")
                .worldRestart()
                .defineInRange("ticksPerRedstoneUpdate", 20, 10, 100);

        builder.pop();
    }
}
