/*
 *
 * Config.java
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

import it.zerono.mods.zerocore.lib.CodeHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public final class Config {

    public static final Client CLIENT;
    public static final Common COMMON;

    public static void initialize() {
    }

    //region internals

    private static final ForgeConfigSpec s_clientSpec;
    private static final ForgeConfigSpec s_commonSpec;

    static {

//        Log.LOGGER.info("Init config");

        if (!CodeHelper.ioCreateModConfigDirectory("extremereactors")) {
            throw new RuntimeException("Unable to create a directory for the Extreme Reactors config files");
        }

        final Pair<Client, ForgeConfigSpec> pair1 = new ForgeConfigSpec.Builder().configure(Client::new);
        final Pair<Common, ForgeConfigSpec> pair2 = new ForgeConfigSpec.Builder().configure(Common::new);

        CLIENT = pair1.getLeft();
        s_clientSpec = pair1.getRight();

        COMMON = pair2.getLeft();
        s_commonSpec = pair2.getRight();

        register(ModConfig.Type.CLIENT, Config.s_clientSpec);
        register(ModConfig.Type.COMMON, Config.s_commonSpec);

//        Log.LOGGER.info("Init config DONE");
    }

    private static void register(final ModConfig.Type type, final ForgeConfigSpec spec) {
        ModLoadingContext.get().registerConfig(type, spec, String.format("extremereactors/%s.toml", type.extension()));
    }

    //endregion
}
