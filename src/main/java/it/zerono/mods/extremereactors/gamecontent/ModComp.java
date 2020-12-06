/*
 *
 * ModComp.java
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

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.zerocore.lib.compat.Mods;
import vazkii.patchouli.api.PatchouliAPI;

public final class ModComp {

    public static void initialize() {
        // Patchouli multiblock rendering do not support IModelData-based models
        Mods.PATCHOULI.ifPresent(ModComp::initPatchouli);
    }

    //region internals

    private static void initPatchouli() {

        PatchouliAPI.get().registerMultiblock(ExtremeReactors.newID("bookfirstbasicreactor"),
                PatchouliAPI.get().makeMultiblock(
                        new String[][] {
                            {
                                "CCC",
                                "CRC",
                                "CCC",
                            },
                            {
                                "CGC",
                                "EFA",
                                "CXC",
                            },
                            {
                                "CCC",
                                "C0C",
                                "CCC",
                            }
                    },
                    '0', Content.Blocks.REACTOR_CASING_BASIC.get(),
                    'C', Content.Blocks.REACTOR_CASING_BASIC.get(),
                    'G', Content.Blocks.REACTOR_GLASS_BASIC.get(),
                    'F', Content.Blocks.REACTOR_FUELROD_BASIC.get(),
                    'R', Content.Blocks.REACTOR_CONTROLROD_BASIC.get(),
                    'A', Content.Blocks.REACTOR_SOLID_ACCESSPORT_BASIC.get(),
                    'E', Content.Blocks.REACTOR_POWERTAP_FE_ACTIVE_BASIC.get(),
                    'X', Content.Blocks.REACTOR_CONTROLLER_BASIC.get()));

    }

    //endregion
}
