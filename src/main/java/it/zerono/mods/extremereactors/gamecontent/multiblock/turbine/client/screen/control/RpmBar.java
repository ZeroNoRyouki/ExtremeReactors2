/*
 *
 * RpmBar.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.screen.control;

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.zerocore.base.client.screen.BaseScreenToolTipsBuilder;
import it.zerono.mods.zerocore.base.client.screen.control.AbstractVerticalIconSingleValueGaugeBar;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.FloatData;
import it.zerono.mods.zerocore.lib.text.TextHelper;

public class RpmBar
        extends AbstractVerticalIconSingleValueGaugeBar {

    public RpmBar(final ModContainerScreen<? extends ModContainer> gui, final String name, final float maxValue,
                  final FloatData bindableValue) {

        super(gui, name, maxValue, bindableValue, CommonIcons.RpmBar, CommonIcons.RotorRPM);
        this.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.turbine.controller.rpmbar.tooltip.title")
                .addTranslatableAsValue("gui.bigreactors.turbine.controller.rpmbar.tooltip.value")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.turbine.controller.rpmbar.tooltip.body")
                .addBindableObjectAsValue(bindableValue, rpm -> TextHelper.literal("%.2f RPM", rpm))
        );
    }
}
