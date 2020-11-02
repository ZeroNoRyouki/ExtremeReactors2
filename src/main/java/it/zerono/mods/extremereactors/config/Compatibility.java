/*
 *
 * Compatibility.java
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

//package it.zerono.mods.extremereactors.config;
//
//import net.minecraftforge.common.ForgeConfigSpec;
//
//public class Compatibility {
//
//    public final ForgeConfigSpec.BooleanValue autoAddUranium;
//    public final ForgeConfigSpec.BooleanValue enableMetallurgyFantasyMetalsInTurbines;
//
//    Compatibility(final ForgeConfigSpec.Builder builder) {
//
//        builder.comment("Define how Extreme Reactors interact with other mods").push("compatibility");
//
//        this.autoAddUranium = builder
//                .comment("If true, automatically adds all unregistered uranium ingots found as clonesof standard yellorium fuel.")
//                .translation("config.bigreactors.compatibility.autoAddUranium")
//                .worldRestart()
//                .define("autoAddUranium", true);
//
//        this.enableMetallurgyFantasyMetalsInTurbines = builder
//                .comment("If true, allows Metallurgy's fantasy metals to be used as part of turbine coils.")
//                .translation("config.bigreactors.compatibility.enableMetallurgyFantasyMetalsInTurbines")
//                .worldRestart()
//                .define("enableMetallurgyFantasyMetalsInTurbines", true);
//
//        builder.pop();
//    }
//}
