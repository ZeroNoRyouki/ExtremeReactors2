package it.zerono.mods.extremereactors;

/*
 * CommonLocations
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * Do not remove or edit this header
 *
 */

import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;

public final class CommonLocations {

    public static final ResourceLocationBuilder ROOT = ExtremeReactors.ROOT_LOCATION;
    public static final ResourceLocationBuilder REACTOR = ROOT.appendPath("reactor");
    public static final ResourceLocationBuilder TURBINE = ROOT.appendPath("turbine");
    public static final ResourceLocationBuilder REPROCESSOR = ROOT.appendPath("reprocessor");
    public static final ResourceLocationBuilder FLUIDIZER = ROOT.appendPath("fluidizer");

    public static final ResourceLocationBuilder TEXTURES_GUI_MULTIBLOCK = ROOT
            .appendPath("textures", "gui", "multiblock");

    public static final ResourceLocationBuilder TEXTURES_GUI_JEI = ROOT
            .appendPath("textures", "gui", "jei");

    //region internals

    private CommonLocations() {
    }

    //endregion
}
