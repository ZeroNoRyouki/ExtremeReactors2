/*
 *
 * Turbine.java
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

import net.neoforged.neoforge.common.ModConfigSpec;

public class Turbine {

    public final ModConfigSpec.IntValue maxTurbineHeight;
    public final ModConfigSpec.IntValue maxTurbineSize;
    public final ModConfigSpec.DoubleValue turbineAeroDragMultiplier;
    public final ModConfigSpec.DoubleValue turbineCoilDragMultiplier;
    public final ModConfigSpec.DoubleValue turbineFluidPerBladeMultiplier;
    public final ModConfigSpec.DoubleValue turbineMassDragMultiplier;
    public final ModConfigSpec.DoubleValue turbinePowerProductionMultiplier;

    Turbine(ModConfigSpec.Builder builder) {

        builder.comment("Define how Turbines works").push("turbine");

        this.maxTurbineHeight = builder
                .comment("The maximum valid height of a Turbine (Y axis), in blocks.")
                .translation("config.bigreactors.turbine.maxturbineheight")
                .worldRestart()
                .defineInRange("maxTurbineHeight", 32, 5, 256);

        this.maxTurbineSize = builder
                .comment("The maximum valid size of a Turbine in the X/Z plane, in blocks.")
                .translation("config.bigreactors.turbine.maxturbinesize")
                .worldRestart()
                .defineInRange("maxTurbineSize", 32, 5, 256);

        this.turbineAeroDragMultiplier = builder
                .comment("A multiplier for balancing rotor sizes.",
                        "Multiplies the amount of energy lost to aerodynamic drag per tick.")
                .translation("config.bigreactors.turbine.turbineaerodragmultiplier")
                .worldRestart()
                .defineInRange("turbineAeroDragMultiplier", 1.0, 0.5, 10.0);

        this.turbineCoilDragMultiplier = builder
                .comment("A multiplier for balancing coil size.",
                        "Multiplies the amount of energy drawn per coil block per tick.")
                .translation("config.bigreactors.turbine.turbinecoildragmultiplier")
                .worldRestart()
                .defineInRange("turbineCoilDragMultiplier", 1.0, 0.5, 10.0);

        this.turbineFluidPerBladeMultiplier = builder
                .comment("A multiplier for balancing coil size.",
                        "Multiplies the amount of fluid each blade block can process (base of 25 will be multiplied,",
                        "then rounded down to the nearest integer).")
                .translation("config.bigreactors.turbine.turbinefluidperblademultiplier")
                .worldRestart()
                .defineInRange("turbineFluidPerBladeMultiplier", 1.0, 0.5, 10.0);

        this.turbineMassDragMultiplier = builder
                .comment("A multiplier for balancing rotor sizes.",
                        "Multiplies the amount of energy lost to friction per tick.")
                .translation("config.bigreactors.turbine.turbinemassdragmultiplier")
                .worldRestart()
                .defineInRange("turbineMassDragMultiplier", 1.0, 0.5, 10.0);

        this.turbinePowerProductionMultiplier = builder
                .comment("A multiplier for balancing turbine power production.",
                        "Stacks with powerProductionMultiplier.")
                .translation("config.bigreactors.turbine.turbinepowerproductionmultiplier")
                .worldRestart()
                .defineInRange("turbinePowerProductionMultiplier", 1.0, 0.5, 10.0);

        builder.pop();
    }
}
