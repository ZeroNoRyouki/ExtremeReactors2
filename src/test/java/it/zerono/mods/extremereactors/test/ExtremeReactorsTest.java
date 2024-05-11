/*
 *
 * ExtremeReactorsTest.java
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

package it.zerono.mods.extremereactors.test;

import it.zerono.mods.zerocore.internal.gamecontent.debugtool.DebugToolItem;
import it.zerono.mods.zerocore.lib.debug.DebugHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(value = "bigreactorstest")
public class ExtremeReactorsTest {

    public ExtremeReactorsTest(IEventBus modBus, ModContainer container, Dist distribution) {

        modBus.addListener(ExtremeReactorsTest::onCommonInit);
        modBus.addListener(ExtremeReactorsTest::onClientInit);
    }

    private static void onCommonInit(FMLCommonSetupEvent event) {
        DebugToolItem.setTestCallback(DebugTests::runTest);
    }

    private static void onClientInit(FMLClientSetupEvent event) {
        DebugHelper.initVoxelShapeHighlighter();
    }
}
