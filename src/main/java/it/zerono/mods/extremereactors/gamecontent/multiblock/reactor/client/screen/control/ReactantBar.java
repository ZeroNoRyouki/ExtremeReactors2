/*
 *
 * ReactantBar.java
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

import it.zerono.mods.extremereactors.api.reactor.Reactant;
import it.zerono.mods.extremereactors.api.reactor.ReactantType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CachedSprites;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonMultiblockScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactantStack;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.container.data.ReactantStackData;
import it.zerono.mods.zerocore.base.client.screen.AbstractBaseScreen;
import it.zerono.mods.zerocore.base.client.screen.BaseScreenToolTipsBuilder;
import it.zerono.mods.zerocore.base.client.screen.control.AbstractVerticalIconMultiValueGaugeBar;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.IntData;
import it.zerono.mods.zerocore.lib.text.TextHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;

public class ReactantBar
        extends AbstractVerticalIconMultiValueGaugeBar<ReactantType> {

    public ReactantBar(ModContainerScreen<? extends ModContainer> gui, String name, IntData bindableCapacity,
                       ReactantStackData bindableFuelData, ReactantStackData bindableWasteData,
                       IntData bindableFuelRodsCount) {

        super(gui, name, 0, CommonIcons.FuelIcon, ReactantType.Waste, ReactantType.Fuel);
        this.setDesiredDimension(18, 84);

        this.bindMaxValue(bindableCapacity);
        this._bar.setBarSprite(ReactantType.Fuel, CachedSprites.REACTOR_FUEL_COLUMN_FLOWING);
        this._bar.setBarSprite(ReactantType.Waste, CachedSprites.REACTOR_FUEL_COLUMN_FLOWING);
        this._bar.bindValue(ReactantType.Fuel, bindableFuelData.amount());
        this._bar.bindValue(ReactantType.Waste, bindableWasteData.amount());

        bindableFuelData.bind(stack -> this.setSpriteTint(stack, ReactantType.Fuel));
        bindableWasteData.bind(stack -> this.setSpriteTint(stack, ReactantType.Waste));

        this.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.reactor.controller.fuelbar.tooltip.title")
                .addTranslatableAsValue("gui.bigreactors.reactor.controller.fuelbar.tooltip.value1")
                .addTranslatableAsValue("gui.bigreactors.reactor.controller.fuelbar.tooltip.value2")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.reactor.controller.fuelbar.tooltip.value3")
                .addTranslatable("gui.bigreactors.reactor.controller.fuelbar.tooltip.value4")
                .addTranslatable("gui.bigreactors.reactor.controller.fuelbar.tooltip.value5")
                .addTranslatable("gui.bigreactors.reactor.controller.fuelbar.tooltip.value6")
                .addTranslatable("gui.bigreactors.reactor.controller.fuelbar.tooltip.value7")
                .addBindableObjectAsValue(bindableFuelData, bindableWasteData, (fuel, waste) -> this.formatFillRatio()) // @0 Fill ratio
                .addBindableObjectAsValue(bindableFuelData, bindableWasteData, (fuel, waste) -> formatWastePercentage(fuel.getAmount(), waste.getAmount())) // @1 Depleted
                .addBindableObjectAsValue(bindableFuelData.amount(), ReactantBar::formatReactantAmount) // @2 Fuel amount
                .addBindableObjectAsValue(bindableWasteData.amount(), ReactantBar::formatReactantAmount) // @3 Waste amount
                .addBindableObjectAsValue(bindableFuelData, bindableWasteData, (fuel, waste) -> formatReactantAmount(fuel.getAmount() + waste.getAmount())) // @4 Total amount
                .addBindableObjectAsValue(bindableFuelRodsCount, ReactantBar::formatFuelRodsCount) // @5 Fuel Rods count
                .addBindableObjectAsValue(bindableCapacity, ReactantBar::formatMaxCapacity) // @6 Max capacity
        );
    }

    //region internals

    private static IFormattableTextComponent formatReactantAmount(int amount) {

        if (amount <= 0) {
            return CommonMultiblockScreen.EMPTY_VALUE;
        }

        return TextHelper.literal(CodeHelper.formatAsMillibuckets(amount), AbstractBaseScreen::formatAsValue);
    }

    private static IFormattableTextComponent formatFuelRodsCount(int count) {
        return TextHelper.literal("%d", text -> text.withStyle(TextFormatting.DARK_AQUA, TextFormatting.BOLD), count);
    }

    private static IFormattableTextComponent formatMaxCapacity(int capacity) {
        return TextHelper.literal(CodeHelper.formatAsMillibuckets(capacity),
                text -> text.withStyle(TextFormatting.DARK_AQUA, TextFormatting.BOLD), capacity);
    }

    private IFormattableTextComponent formatFillRatio() {
        return TextHelper.literal("%.2f%%", AbstractBaseScreen::formatAsValue, this._bar.getFillRatio() * 100f);
    }

    private static IFormattableTextComponent formatWastePercentage(int fuelAmount, int wasteAmount) {

        final int totalAmount = fuelAmount + wasteAmount;

        if (0 == totalAmount) {
            return TextHelper.literal("0%", AbstractBaseScreen::formatAsValue);
        } else {
            return TextHelper.literal("%.2f%%", AbstractBaseScreen::formatAsValue, (float) wasteAmount / (float) totalAmount * 100.0f);
        }
    }

    private void setSpriteTint(ReactantStack stack, ReactantType type) {
        this._bar.setBarSpriteTint(type, stack.getReactant().map(Reactant::getColour).orElse(Colour.WHITE));
    }

    //endregion
}
