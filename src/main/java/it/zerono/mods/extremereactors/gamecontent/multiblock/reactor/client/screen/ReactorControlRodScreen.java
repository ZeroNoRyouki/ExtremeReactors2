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

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.AbstractMultiblockScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorControlRodEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.DesiredDimension;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.control.*;
import it.zerono.mods.zerocore.lib.client.gui.databind.BindingGroup;
import it.zerono.mods.zerocore.lib.client.gui.databind.MonoConsumerBinding;
import it.zerono.mods.zerocore.lib.client.gui.layout.HorizontalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.HorizontalLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalLayoutEngine;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.function.Consumer;
import java.util.function.Function;

public class ReactorControlRodScreen
        extends AbstractMultiblockScreen<MultiblockReactor, ReactorControlRodEntity, ModTileContainer<ReactorControlRodEntity>> {

    public ReactorControlRodScreen(final ModTileContainer<ReactorControlRodEntity> container,
                                   final PlayerInventory inventory, final ITextComponent title) {

        super(container, inventory, PlayerInventoryUsage.None, title,
                mainTextureFromVariant(container.getTileEntity().getMultiblockVariant().orElse(ReactorVariant.Basic)));
        this.ignoreCloseOnInventoryKey(true);
        this._bindings = new BindingGroup();

        this._nameInput = new TextInput(this, "name");
        this._insertionInput = new NumberInput.IntNumberInput(this, "insertion", 0, 100, 0);
    }

    //region AbstractMultiblockScreen

    @Override
    protected void onScreenCreate() {

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, ExtremeReactors.newID("reactor/part-controlrod"), 1);

        IControl c;
        Label l;
        Button b;
        Panel p;
        final int panelWidth = this.getGuiWidth() - (13 * 2);

        super.onScreenCreate();
        this.setContentLayoutEngine(new VerticalLayoutEngine()
                .setHorizontalAlignment(HorizontalAlignment.Left)
                .setControlsSpacing(7)
                .setHorizontalMargin(13)
                .setVerticalMargin(8));

        // name

        l = new Label(this, "nameLabel", new TranslationTextComponent("gui.bigreactors.reactor.controlrod.name.label"));
        l.setHorizontalAlignment(HorizontalAlignment.Left);
        l.setDesiredDimension(DesiredDimension.Width, panelWidth);
        this.addControl(l);

        p = new Panel(this, "namePanel");
        p.setLayoutEngine(new HorizontalLayoutEngine()
                .setHorizontalAlignment(HorizontalAlignment.Left)
                .setVerticalAlignment(VerticalAlignment.Center)
                .setControlsSpacing(2)
                .setHorizontalMargin(0));
        p.setDesiredDimension(panelWidth, 18);

        c = new Picture(this, "p1", CommonIcons.LabelEdit.get());
        c.setDesiredDimension(16, 16);
        p.addControl(c);

        this._nameInput.setDesiredDimension(DesiredDimension.Height, 16);
        this.addBinding(ReactorControlRodEntity::getName, this._nameInput::setText);
        p.addControl(this._nameInput);

        b = new Button(this, "nameSet", new TranslationTextComponent("gui.bigreactors.reactor.controlrod.name.set"));
        b.setDesiredDimension(40, 16);
        b.setTooltips(new TranslationTextComponent("gui.bigreactors.reactor.controlrod.name.set.tooltip.line1"));
        b.Clicked.subscribe((button, mousebutton) -> this.sendSetName(this._nameInput.getText()));
        p.addControl(b);

        this.addControl(p);

        this.addControl(new Static(this, panelWidth, 1).setColor(Colour.BLACK));

        // insertion

        l = new Label(this, "insertionLabel", new TranslationTextComponent("gui.bigreactors.reactor.controlrod.insertion.label"));
        l.setHorizontalAlignment(HorizontalAlignment.Left);
        l.setDesiredDimension(DesiredDimension.Width, panelWidth);
        this.addControl(l);

        p = new Panel(this, "insertionPanel");
        p.setLayoutEngine(new HorizontalLayoutEngine()
                .setHorizontalAlignment(HorizontalAlignment.Left)
                .setVerticalAlignment(VerticalAlignment.Center)
                .setControlsSpacing(2)
                .setHorizontalMargin(0));
        p.setDesiredDimension(panelWidth, 18);

        c = new Picture(this, "p2", CommonIcons.ButtonSensorInputSetControlRod.get());
        c.setDesiredDimension(16, 16);
        p.addControl(c);

        this._insertionInput.setStep(1, 10);
        this._insertionInput.setDisplaySuffix("%");
        this._insertionInput.setHorizontalAlignment(HorizontalAlignment.Right);
        this._insertionInput.setDesiredDimension(42, 14);
        this._insertionInput.setTooltips(
                new TranslationTextComponent("gui.bigreactors.reactor.controlrod.insertion.input.tooltip.line1").setStyle(CommonConstants.STYLE_TOOLTIP_TITLE),
                CodeHelper.TEXT_EMPTY_LINE,
                new TranslationTextComponent("gui.bigreactors.reactor.controlrod.insertion.input.tooltip.line2"),
                CodeHelper.TEXT_EMPTY_LINE,
                new TranslationTextComponent("gui.bigreactors.reactor.controlrod.insertion.input.tooltip.line3"),
                new TranslationTextComponent("gui.bigreactors.reactor.controlrod.insertion.input.tooltip.line4"),
                CodeHelper.TEXT_EMPTY_LINE,
                new TranslationTextComponent("gui.bigreactors.reactor.controlrod.insertion.input.tooltip.line5"),
                new TranslationTextComponent("gui.bigreactors.reactor.controlrod.insertion.input.tooltip.line6")
        );
        this.addBinding(ReactorControlRodEntity::getInsertionRatio, (Consumer<Byte>) this._insertionInput::setValue);
        p.addControl(this._insertionInput);

        b = new Button(this, "rodSet", new TranslationTextComponent("gui.bigreactors.reactor.controlrod.insertion.set"));
        b.setDesiredDimension(67, 16);
        b.setTooltips(new TranslationTextComponent("gui.bigreactors.reactor.controlrod.insertion.set.tooltip.line1"),
                new TranslationTextComponent("gui.bigreactors.reactor.controlrod.insertion.set.tooltip.line2"));
        b.Clicked.subscribe((button, mousebutton) -> this.sendSetInsertion(this._insertionInput.getAsInt(), false));
        p.addControl(b);

        b = new Button(this, "rodSetAll", new TranslationTextComponent("gui.bigreactors.reactor.controlrod.insertion.setall"));
        b.setDesiredDimension(67, 16);
        b.setTooltips(new TranslationTextComponent("gui.bigreactors.reactor.controlrod.insertion.setall.tooltip.line1"),
                new TranslationTextComponent("gui.bigreactors.reactor.controlrod.insertion.setall.tooltip.line2"));
        b.Clicked.subscribe((button, mousebutton) -> this.sendSetInsertion(this._insertionInput.getAsInt(), true));
        p.addControl(b);

        this.addControl(p);
    }

    /**
     * Called when this screen need to be updated after the TileEntity data changed.
     * Override to handle this event
     */
    @Override
    protected void onDataUpdated() {

        super.onDataUpdated();
        this._bindings.update();
    }

    //region ContainerScreen

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    //endregion
    //region internals

    private <Value> void addBinding(final Function<ReactorControlRodEntity, Value> supplier, final Consumer<Value> consumer) {
        this._bindings.addBinding(new MonoConsumerBinding<>(this.getTileEntity(), supplier, consumer));
    }

    private void sendSetInsertion(final int insertion, final boolean setAll) {

        final CompoundNBT data = new CompoundNBT();

        data.putInt("v", MathHelper.clamp(insertion, 0, 100));
        data.putBoolean("all", setAll);

        this.sendCommandToServer(ReactorControlRodEntity.COMMAND_SET_INSERTION, data);
    }

    private void sendSetName(final String name) {

        final CompoundNBT data = new CompoundNBT();

        data.putString("name", name);

        this.sendCommandToServer(ReactorControlRodEntity.COMMAND_SET_NAME, data);
    }

    private final BindingGroup _bindings;

    private final TextInput _nameInput;
    private final NumberInput.IntNumberInput _insertionInput;

    //endregion
}
