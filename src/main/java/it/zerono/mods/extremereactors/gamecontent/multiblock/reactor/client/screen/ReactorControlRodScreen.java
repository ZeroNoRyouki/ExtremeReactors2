/*
 *
 * ReactorControlRodScreen.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.screen;

import it.zerono.mods.extremereactors.CommonLocations;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonMultiblockScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.container.ReactorControlRodContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorControlRodEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
import it.zerono.mods.zerocore.base.client.screen.BaseScreenToolTipsBuilder;
import it.zerono.mods.zerocore.base.client.screen.control.CommonPanels;
import it.zerono.mods.zerocore.base.client.screen.control.MachineStatusIndicator;
import it.zerono.mods.zerocore.lib.client.gui.DesiredDimension;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.control.*;
import it.zerono.mods.zerocore.lib.client.gui.layout.HorizontalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.HorizontalLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.text.TextHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class ReactorControlRodScreen
        extends CommonMultiblockScreen<MultiblockReactor, ReactorControlRodEntity, ReactorControlRodContainer> {

    public ReactorControlRodScreen(final ReactorControlRodContainer container,
                                   final Inventory inventory, final Component title) {

        super(container, inventory, PlayerInventoryUsage.None, title,
                mainTextureFromVariant(container.getTileEntity().getMultiblockVariant().orElse(ReactorVariant.Basic)));
        this.ignoreCloseOnInventoryKey(true);

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, CommonLocations.REACTOR.buildWithSuffix("part-controlrod"), 1);

        final TextInput name;
        final Button nameSet;

        name = new TextInput(this, "name");
        name.bindText(container.NAME, $ -> $);
        name.setDesiredDimension(DesiredDimension.Height, 16);

        nameSet = new Button(this, "nameSet", TextHelper.translatable("gui.bigreactors.reactor.controlrod.name.set"));
        nameSet.setDesiredDimension(40, 16);
        nameSet.setTooltips(TextHelper.translatable("gui.bigreactors.reactor.controlrod.name.set.tooltip.line1"));
        nameSet.Clicked.subscribe((button, mousebutton) -> this.sendSetName(name.getText()));

        this._nameSection = this.labeledControlSection("name", this.getContentWidth(), "gui.bigreactors.reactor.controlrod.name.label",
                CommonIcons.LabelEdit, name, nameSet);

        final NumberInput.IntNumberInput insertion = new NumberInput.IntNumberInput(this, "insertion", 0, 100, 0);
        final Button rodSet, rodSetAll;

        container.INSERTION_RATIO.bind(insertion::setValue);
        insertion.setStep(1, 10);
        insertion.setDisplaySuffix("%");
        insertion.setHorizontalAlignment(HorizontalAlignment.Right);
        insertion.setDesiredDimension(42, 14);
        insertion.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.reactor.controlrod.insertion.input.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.reactor.controlrod.insertion.input.tooltip.body")
        );

        rodSet = new Button(this, "rodSet", TextHelper.translatable("gui.bigreactors.reactor.controlrod.insertion.set"));
        rodSet.setDesiredDimension(67, 16);
        rodSet.setTooltips(TextHelper.translatable("gui.bigreactors.reactor.controlrod.insertion.set.tooltip.body"));
        rodSet.Clicked.subscribe((button, mousebutton) -> this.sendSetInsertion(insertion.getAsInt(), false));

        rodSetAll = new Button(this, "rodSetAll", Component.translatable("gui.bigreactors.reactor.controlrod.insertion.setall"));
        rodSetAll.setDesiredDimension(67, 16);
        rodSetAll.setTooltips(TextHelper.translatable("gui.bigreactors.reactor.controlrod.insertion.setall.tooltip.body"));
        rodSetAll.Clicked.subscribe((button, mousebutton) -> this.sendSetInsertion(insertion.getAsInt(), true));

        this._insertionSection = this.labeledControlSection("insertion", this.getContentWidth(), "gui.bigreactors.reactor.controlrod.insertion.label",
                CommonIcons.ButtonSensorInputSetControlRod, insertion, rodSet, rodSetAll);
    }

    //region AbstractMultiblockScreen

    @Override
    protected MachineStatusIndicator createStatusIndicator(ReactorControlRodContainer container) {
        return this.createReactorStatusIndicator(container.ACTIVE);
    }

    @Override
    protected void onScreenCreate() {

        super.onScreenCreate();
        this.setContentLayoutEngine(new VerticalLayoutEngine()
                .setHorizontalAlignment(HorizontalAlignment.Left)
                .setControlsSpacing(7)
                .setHorizontalMargin(3)
                .setVerticalAlignment(VerticalAlignment.Center));

        this.addControl(this._nameSection);
        this.addControl(CommonPanels.horizontalSeparator(this, this.getContentWidth()));
        this.addControl(this._insertionSection);
    }

    //endregion
    //region internals

    private void sendSetInsertion(final int insertion, final boolean setAll) {

        final CompoundTag data = new CompoundTag();

        data.putInt("v", Mth.clamp(insertion, 0, 100));
        data.putBoolean("all", setAll);

        this.sendCommandToServer(ReactorControlRodEntity.COMMAND_SET_INSERTION, data);
    }

    private void sendSetName(final String name) {

        final CompoundTag data = new CompoundTag();

        data.putString("name", name);

        this.sendCommandToServer(ReactorControlRodEntity.COMMAND_SET_NAME, data);
    }

    private IControl labeledControlSection(String name, int width, String labelKey, Supplier<@NotNull ISprite> icon,
                                           IControl... controls) {

        final Panel panel = new Panel(this, name);

        panel.setDesiredDimension(width, 40);
        panel.setLayoutEngine(new VerticalLayoutEngine()
                .setHorizontalAlignment(HorizontalAlignment.Left)
                .setControlsSpacing(7)
                .setHorizontalMargin(0)
                .setVerticalMargin(0));

        final Label label = new Label(this, "lbl", TextHelper.translatable(labelKey));

        label.setHorizontalAlignment(HorizontalAlignment.Left);
        label.setDesiredDimension(DesiredDimension.Width, width);
        panel.addControl(label);

        final Panel innerPanel = new Panel(this, "pnl");

        innerPanel.setLayoutEngine(new HorizontalLayoutEngine()
                .setHorizontalAlignment(HorizontalAlignment.Left)
                .setVerticalAlignment(VerticalAlignment.Center)
                .setControlsSpacing(2)
                .setHorizontalMargin(0));
        innerPanel.setDesiredDimension(width, 18);

        innerPanel.addControl(CommonPanels.icon(this, icon));
        innerPanel.addControl(controls);

        panel.addControl(innerPanel);
        return panel;
    }

    private final IControl _nameSection, _insertionSection;

    //endregion
}
