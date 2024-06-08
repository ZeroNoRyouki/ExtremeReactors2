/*
 *
 * FlowRate.java
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

import it.zerono.mods.zerocore.base.client.screen.BaseScreenToolTipsBuilder;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.Padding;
import it.zerono.mods.zerocore.lib.client.gui.control.AbstractCompositeControl;
import it.zerono.mods.zerocore.lib.client.gui.control.Label;
import it.zerono.mods.zerocore.lib.client.gui.control.NumberInput;
import it.zerono.mods.zerocore.lib.client.gui.layout.HorizontalAlignment;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.text.TextHelper;

import java.util.function.IntConsumer;

public class FlowRate
        extends AbstractCompositeControl {

    public FlowRate(final ModContainerScreen<? extends ModContainer> gui, final String name,
                    final int maxRate, final int rate, final IntConsumer valueChangedHandler) {

        super(gui, name);
        this.setDesiredDimension(70, 14 + 14 + 2 + 2);

        this._label = new Label(gui, "label", TextHelper.translatable("gui.bigreactors.turbine.controller.flowrate.label"));

        this._rate = new NumberInput.IntNumberInput(gui, "rate", 0, maxRate, rate);
        this._rate.setStep(1, 10);
        this._rate.setDisplaySuffix(" mB/t");
        this._rate.setHorizontalAlignment(HorizontalAlignment.Right);
        this._rate.Changed.subscribe(($, value) -> valueChangedHandler.accept(value));

        this.addChildControl(this._label, this._rate);

        this.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.turbine.controller.flowrate.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.turbine.controller.flowrate.tooltip.body"));
    }

    //region AbstractCompositeControl

    @Override
    public void setBounds(final Rectangle bounds) {

        super.setBounds(bounds);

        final Padding padding = this.getPadding();
        final int width = bounds.Width - padding.getLeft() - padding.getRight();

        this._label.setBounds(new Rectangle(padding.getLeft(), padding.getTop(), width, 14));
        this._rate.setBounds(new Rectangle(padding.getLeft(), padding.getTop() + 14 + 2, width, 14));
    }

    //endregion
    //region internals

    private final Label _label;
    private final NumberInput.IntNumberInput _rate;

    //endregion
}
