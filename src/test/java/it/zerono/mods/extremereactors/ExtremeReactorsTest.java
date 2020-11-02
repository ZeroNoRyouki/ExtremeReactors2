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

package it.zerono.mods.extremereactors;

import it.zerono.mods.zerocore.internal.gamecontent.debugtool.DebugToolItem;
import it.zerono.mods.zerocore.lib.debug.DebugHelper;
import it.zerono.mods.zerocore.lib.init.IModInitializationHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(value = "bigreactorstest")
public class ExtremeReactorsTest implements IModInitializationHandler {

    public ExtremeReactorsTest() {
        Mod.EventBusSubscriber.Bus.MOD.bus().get().register(this);
    }

    /**
     * Called on both the physical client and the physical server to perform common initialization tasks
     * @param event the event
     */
    @Override
    @SubscribeEvent
    public void onCommonInit(final FMLCommonSetupEvent event) {
        DebugToolItem.setTestCallback(DebugTests::runTest);
    }

    /**
     * Called on the physical client to perform client-specific initialization tasks
     *
     * @param event
     */
    @Override
    @SubscribeEvent
    public void onClientInit(final FMLClientSetupEvent event) {
        DebugHelper.initVoxelShapeHighlighter();
    }
}
