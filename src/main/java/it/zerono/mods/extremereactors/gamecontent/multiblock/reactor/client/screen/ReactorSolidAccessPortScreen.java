/*
 *
 * ReactorSolidAccessPortScreen.java
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
import it.zerono.mods.extremereactors.api.reactor.ReactantType;
import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.AbstractMultiblockScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.container.ReactorSolidAccessPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorSolidAccessPortEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
import it.zerono.mods.zerocore.lib.client.gui.ButtonState;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.control.Button;
import it.zerono.mods.zerocore.lib.client.gui.control.Panel;
import it.zerono.mods.zerocore.lib.client.gui.control.SlotsGroup;
import it.zerono.mods.zerocore.lib.client.gui.control.SwitchPictureButton;
import it.zerono.mods.zerocore.lib.client.gui.databind.BindingGroup;
import it.zerono.mods.zerocore.lib.client.gui.databind.MonoConsumerBinding;
import it.zerono.mods.zerocore.lib.client.gui.layout.FixedLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.data.geometry.Point;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.common.util.NonNullSupplier;

import java.util.function.Consumer;
import java.util.function.Function;

import static it.zerono.mods.zerocore.lib.CodeHelper.TEXT_EMPTY_LINE;

public class ReactorSolidAccessPortScreen
        extends AbstractMultiblockScreen<MultiblockReactor, ReactorSolidAccessPortEntity, ReactorSolidAccessPortContainer> {

    public ReactorSolidAccessPortScreen(final ReactorSolidAccessPortContainer container,
                                        final Inventory inventory, final Component title) {

        super(container, inventory, PlayerInventoryUsage.Both, title,
                mainTextureFromVariant(container.getTileEntity().getMultiblockVariant().orElse(ReactorVariant.Basic)));
        this._bindings = new BindingGroup();

        this._btnInputDirection = new SwitchPictureButton(this, "directionInput", false, "direction");
        this._btnOutputDirection = new SwitchPictureButton(this, "directionOutput", false, "direction");
        this._btnDumpFuel = new Button(this, "dumpFuel", "");
        this._btnDumpWaste = new Button(this, "dumpWaste", "");
    }

    //region AbstractMultiblockScreen

    /**
     * Called when this screen is being created for the first time.
     * Override to handle this event
     */
    @Override
    protected void onScreenCreate() {

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, CommonLocations.REACTOR.buildWithSuffix("part-solidaccessport"), 1);

        super.onScreenCreate();

        final Panel panel = new Panel(this, "solidaccessport");
        SlotsGroup slotsGroup;

        panel.setLayoutEngineHint(FixedLayoutEngine.hint(21, 13, 168, 38));
        this.addControl(panel);

        // - fuel input slot
        panel.addControl(this.slotPanel("fuelinput", ReactantType.Fuel, 79, 0, CommonIcons.PortInputSlot));

        // - waste output slot
        panel.addControl(this.slotPanel("wasteoutput", ReactantType.Waste, 129, 0, CommonIcons.PortOutputSlot));

        // - player main inventory slots
        slotsGroup = this.createPlayerInventorySlotsGroupControl();
        slotsGroup.setLayoutEngineHint(FixedLayoutEngine.hint(31, 63));
        this.addControl(slotsGroup);

        // - player hotbar slots
        slotsGroup = this.createPlayerHotBarSlotsGroupControl();
        slotsGroup.setLayoutEngineHint(FixedLayoutEngine.hint(31, 121));
        this.addControl(slotsGroup);

        // - input direction button

        this.setButtonSpritesAndOverlayForState(this._btnInputDirection, ButtonState.Default, CommonIcons.ButtonInputDirection);
        this.setButtonSpritesAndOverlayForState(this._btnInputDirection, ButtonState.Active, CommonIcons.ButtonInputDirectionActive);
        this._btnInputDirection.Activated.subscribe(this::onInputActivated);
        this._btnInputDirection.setTooltips(
                Component.translatable("gui.bigreactors.reactor.solidaccessport.directioninput.line1").setStyle(CommonConstants.STYLE_TOOLTIP_TITLE),
                TEXT_EMPTY_LINE,
                Component.translatable("gui.bigreactors.reactor.solidaccessport.directioninput.line2")
        );

        // - output direction button
        this.setButtonSpritesAndOverlayForState(this._btnOutputDirection, ButtonState.Default, CommonIcons.ButtonOutputDirection);
        this.setButtonSpritesAndOverlayForState(this._btnOutputDirection, ButtonState.Active, CommonIcons.ButtonOutputDirectionActive);
        this._btnOutputDirection.Activated.subscribe(this::onOutputActivated);
        this._btnOutputDirection.setTooltips(
                Component.translatable("gui.bigreactors.reactor.solidaccessport.directionoutput.line1").setStyle(CommonConstants.STYLE_TOOLTIP_TITLE),
                TEXT_EMPTY_LINE,
                Component.translatable("gui.bigreactors.reactor.solidaccessport.directionoutput.line2")
        );

        this.addBinding(ReactorSolidAccessPortContainer::getIoDirection, value -> {

            this._btnInputDirection.setActive(value.isInput());
            this._btnOutputDirection.setActive(value.isOutput());
        });

        // - dump fuel command button

        this._btnDumpFuel.setPadding(0);
        this._btnDumpFuel.setIconForState(CommonIcons.ButtonDumpFuel.get(), ButtonState.Default);
        this._btnDumpFuel.setIconForState(CommonIcons.ButtonDumpFuelActive.get(), ButtonState.Active, ButtonState.ActiveHighlighted, ButtonState.DefaultHighlighted);
        this._btnDumpFuel.Clicked.subscribe(this::onDumpFuel);
        this._btnDumpFuel.setTooltips(
                Component.translatable("gui.bigreactors.reactor.solidaccessport.dumpfuel.line1").setStyle(CommonConstants.STYLE_TOOLTIP_TITLE),
                TEXT_EMPTY_LINE,
                Component.translatable("gui.bigreactors.reactor.solidaccessport.dumpfuel.line2"),
                Component.translatable("gui.bigreactors.reactor.solidaccessport.dumpfuel.line3"),
                Component.translatable("gui.bigreactors.reactor.solidaccessport.dumpfuel.line4")
        );

        // - dump waste command button

        this._btnDumpWaste.setPadding(0);
        this._btnDumpWaste.setIconForState(CommonIcons.ButtonDumpWaste.get(), ButtonState.Default);
        this._btnDumpWaste.setIconForState(CommonIcons.ButtonDumpWasteActive.get(), ButtonState.Active, ButtonState.ActiveHighlighted, ButtonState.DefaultHighlighted);
        this._btnDumpWaste.Clicked.subscribe(this::onDumpWaste);
        this._btnDumpWaste.setTooltips(
                Component.translatable("gui.bigreactors.reactor.solidaccessport.dumpwaste.line1").setStyle(CommonConstants.STYLE_TOOLTIP_TITLE),
                TEXT_EMPTY_LINE,
                Component.translatable("gui.bigreactors.reactor.solidaccessport.dumpwaste.line2"),
                Component.translatable("gui.bigreactors.reactor.solidaccessport.dumpwaste.line3"),
                Component.translatable("gui.bigreactors.reactor.solidaccessport.dumpwaste.line4")
        );

        panel.addControl(this.buttonsPanel(this._btnInputDirection, this._btnOutputDirection, this._btnDumpFuel, this._btnDumpWaste));
    }

    /**
     * Called when this screen need to be updated after the TileEntity data changed.
     * Override to handle this event
     */
    @Override
    protected void onDataUpdated() {

        super.onDataUpdated();
        this._bindings.update();;
    }

    //endregion
    //region  internals

    private void onInputActivated(SwitchPictureButton button) {
        this.sendCommandToServer(CommonConstants.COMMAND_SET_INPUT);
    }

    private void onOutputActivated(SwitchPictureButton button) {
        this.sendCommandToServer(CommonConstants.COMMAND_SET_OUTPUT);
    }

    private void onDumpFuel(Button button, Integer mouseButton) {

        final CompoundTag options = new CompoundTag();

        options.putBoolean("void", Screen.hasShiftDown());
        this.sendCommandToServer(CommonConstants.COMMAND_DUMP_FUEL, options);
    }

    private void onDumpWaste(Button button, Integer integer) {

        final CompoundTag options = new CompoundTag();

        options.putBoolean("void", Screen.hasShiftDown());
        this.sendCommandToServer(CommonConstants.COMMAND_DUMP_WASTE, options);
    }

    private <Value> void addBinding(final Function<ReactorSolidAccessPortContainer, Value> supplier, final Consumer<Value> consumer) {
        this._bindings.addBinding(new MonoConsumerBinding<>(this.getMenu(), supplier, consumer));
    }

    private Panel buttonsPanel(final IControl setInput, final IControl setOutput,
                               final IControl dumpFuel, final IControl dumpWaste) {

        final Panel p = new Panel(this);

        p.setLayoutEngineHint(FixedLayoutEngine.hint(15, 0, 18 * 2 + 2, 18 * 2 + 2));
        p.setCustomBackgroundPainter((panel, matrix) -> {

            final Point xy = panel.controlToScreen(0, 0);
            final ISprite border = CommonIcons.ImageButtonBorder.get();
            final int z = (int)panel.getGui().getZLevel();

            ModRenderHelper.paintSprite(matrix, border, xy.X, xy.Y, z, 18, 18);
            ModRenderHelper.paintSprite(matrix, border, xy.X + 20, xy.Y, z, 18, 18);
            ModRenderHelper.paintSprite(matrix, border, xy.X, xy.Y + 20, z, 18, 18);
            ModRenderHelper.paintSprite(matrix, border, xy.X + 20, xy.Y + 20, z, 18, 18);
        });

        setInput.setLayoutEngineHint(FixedLayoutEngine.hint(1, 1, 16, 16));
        p.addControl(setInput);

        setOutput.setLayoutEngineHint(FixedLayoutEngine.hint(21, 1, 16, 16));
        p.addControl(setOutput);

        dumpFuel.setLayoutEngineHint(FixedLayoutEngine.hint(1, 21, 16, 16));
        p.addControl(dumpFuel);

        dumpWaste.setLayoutEngineHint(FixedLayoutEngine.hint(21, 21, 16, 16));
        p.addControl(dumpWaste);

        return p;
    }

    private Panel slotPanel(final String groupName, final ReactantType reactant, final int x, final int y,
                            final NonNullSupplier<ISprite> slotBackground) {

        final SlotsGroup sg = this.createSingleSlotGroupControl(groupName, reactant.name());
        final Panel p = new Panel(this);

        sg.setLayoutEngineHint(FixedLayoutEngine.hint(10, 10, 18, 18));

        p.setBackground(slotBackground.get());
        p.setLayoutEngineHint(FixedLayoutEngine.hint(x, y, 38, 38));
        p.addControl(sg);

        return p;
    }

    private final BindingGroup _bindings;

    private final SwitchPictureButton _btnInputDirection;
    private final SwitchPictureButton _btnOutputDirection;
    private final Button _btnDumpFuel;
    private final Button _btnDumpWaste;

    //endregion
}
