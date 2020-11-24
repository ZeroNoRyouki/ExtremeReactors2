/*
 *
 * ApiWrapper.java
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

package it.zerono.mods.extremereactors.modpack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ApiWrapper {

    public boolean Enabled;
    public boolean EnableLogging;

    public static class FluidsRegistryWrapper {

        public Coolant[] Coolants;
        public Vapor[] Vapors;

        public static class Coolant
                extends AbstractNamedValue {

            public float BoilingPoint;
            public float EnthalpyOfVaporization;
        }

        public static class Vapor
                extends AbstractNamedValue {

            public float FluidEnergyDensity;
        }
    }

    protected static class AbstractNamedValue {

        public String Name;
        public String TranslationKey;
    }

    //region internals

    private static Gson s_gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .disableHtmlEscaping()
            .create();

    //endregion
}
