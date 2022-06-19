/*
 *
 * Reactor.java
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

public class Reactor {

    public final ForgeConfigSpec.IntValue maxReactorHeight;
    public final ForgeConfigSpec.IntValue maxReactorSize;
    public final ForgeConfigSpec.DoubleValue reactorPowerProductionMultiplier;

    Reactor(final ForgeConfigSpec.Builder builder) {

        builder.comment("Define how Reactors works").push("reactor");

        this.maxReactorHeight = builder
                .comment("The maximum valid size of a Reactor in the Y dimension, in blocks.",
                        "Lower this if your server's players are building ginormous Reactors.",
                        "Bigger Y sizes have far less performance impact than X/Z sizes.")
                .translation("config.bigreactors.reactor.maxreactorheight")
                .worldRestart()
                .defineInRange("maxReactorHeight", 48, 3, 256);

        this.maxReactorSize = builder
                .comment("The maximum valid size of a Reactor in the X/Z plane, in blocks.",
                        "Lower this if your server's players are building ginormous Reactors.")
                .translation("config.bigreactors.reactor.maxreactorsize")
                .worldRestart()
                .defineInRange("maxReactorSize", 32, 3, 256);

        this.reactorPowerProductionMultiplier = builder
                .comment("A multiplier for balancing Reactor power production. Stacks with powerProductionMultiplier.")
                .translation("config.bigreactors.reactor.reactorpowerproductionmultiplier")
                .worldRestart()
                .defineInRange("reactorPowerProductionMultiplier", 1.0, 0.5, 100.0);

        builder.pop();
    }
}
