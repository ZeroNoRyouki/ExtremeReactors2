/*
 *
 * Common.java
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

public class Common {

    public final General general;
    public final Reactor reactor;
    public final Turbine turbine;
    public final Fluidizer fluidizer;
    public final Recipes recipes;
    public final Worldgen worldgen;

    Common(final ForgeConfigSpec.Builder builder) {

        builder.comment("Common configuration settings").push("common");

        this.general = new General(builder);
        this.reactor = new Reactor(builder);
        this.turbine = new Turbine(builder);
        this.fluidizer = new Fluidizer(builder);
        this.recipes = new Recipes(builder);
        this.worldgen = new Worldgen(builder);

        builder.pop();
    }
}
