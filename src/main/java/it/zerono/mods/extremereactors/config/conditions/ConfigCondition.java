/*
 *
 * ConfigRecipeCondition.java
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

package it.zerono.mods.extremereactors.config.conditions;

import com.google.gson.JsonObject;
import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.config.Config;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class ConfigCondition
        implements ICondition {

    public ConfigCondition(final String configName) {
        this._configName = configName;
    }

    //region ICondition

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public boolean test(final IContext context) {

        switch (this._configName) {

            default:
                return false;

            // recipes

            case "registerCharcoalForSmelting":
                return Config.COMMON.recipes.registerCharcoalForSmelting.get();

            case "registerCoalForSmelting":
                return Config.COMMON.recipes.registerCoalForSmelting.get();

            case "registerCoalBlockForSmelting":
                return Config.COMMON.recipes.registerCoalBlockForSmelting.get();
        }
    }

    //endregion
    //region serializer

    public static class Serializer
            implements IConditionSerializer<ConfigCondition> {

        public static final Serializer INSTANCE = new Serializer();

        //region IConditionSerializer<ConfigCondition>

        @Override
        public void write(JsonObject json, ConfigCondition value) {
            json.addProperty("config_name", value._configName);
        }

        @Override
        public ConfigCondition read(JsonObject json) {
            return new ConfigCondition(GsonHelper.getAsString(json, "config_name"));
        }

        @Override
        public ResourceLocation getID() {
            return ConfigCondition.ID;
        }

        //endregion
    }

    //endregion
    //region internal

    private final static ResourceLocation ID = ExtremeReactors.newID("config_condition");
    private final String _configName;

    //endregion
}
