/*
 *
 * CommonConstants.java
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

package it.zerono.mods.extremereactors.gamecontent;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public final class CommonConstants {

    //region TE commands

    public static String COMMAND_ACTIVATE = "activate";
    public static String COMMAND_DEACTIVATE = "deactivate";
    public static String COMMAND_SET_INPUT = "setinput";
    public static String COMMAND_SET_OUTPUT = "setoutput";
    public static String COMMAND_SET_REDSTONE_SENSOR = "setsensor";
    public static String COMMAND_DISABLE_REDSTONE_SENSOR = "nosensor";
    public static String COMMAND_DUMP_FUEL = "dumpfuel";
    public static String COMMAND_DUMP_WASTE = "dumpwaste";

    //endregion
    //region UI styles

    public static final Style STYLE_TOOLTIP_TITLE = Style.EMPTY
            .withColor(TextFormatting.YELLOW)
            .withBold(true);

    public static final Style STYLE_TOOLTIP_VALUE = Style.EMPTY
            .withColor(TextFormatting.DARK_AQUA)
            .withBold(true);

    public static final Style STYLE_TOOLTIP_INFO = Style.EMPTY
            .withColor(TextFormatting.DARK_PURPLE)
            .withItalic(true);

    //endregion
    //region internals

    private CommonConstants() {
    }

    //endregion
}
