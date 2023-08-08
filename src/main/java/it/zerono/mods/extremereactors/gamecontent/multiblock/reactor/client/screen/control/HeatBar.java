/*
 *
 * HeatBar.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.screen.control;

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.zerocore.base.client.screen.BaseScreenToolTipsBuilder;
import it.zerono.mods.zerocore.base.client.screen.control.AbstractVerticalIconSingleValueGaugeBar;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.DoubleData;
import it.zerono.mods.zerocore.lib.text.TextHelper;
import net.minecraftforge.common.util.NonNullSupplier;

public class HeatBar
        extends AbstractVerticalIconSingleValueGaugeBar {

    public HeatBar(ModContainerScreen<? extends ModContainer> gui, String name, double maxValue,
                   DoubleData bindableValue, NonNullSupplier<ISprite> icon, String tooltipsTitleKey,
                   String tooltipsValueKey, String tooltipsBodyKey) {

        super(gui, name, maxValue, bindableValue, CommonIcons.TemperatureBar, icon);
        this.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle(tooltipsTitleKey)
                .addTranslatableAsValue(tooltipsValueKey)
                .addEmptyLine()
                .addTranslatable(tooltipsBodyKey)
                .addBindableObjectAsValue(bindableValue, heat -> TextHelper.literal("%.0f C", heat))
        );
    }
}
