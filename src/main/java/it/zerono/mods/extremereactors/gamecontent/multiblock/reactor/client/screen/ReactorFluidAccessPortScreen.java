/*
 *
 * ReactorFluidAccessPortScreen.java
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
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonMultiblockScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.container.ReactorFluidAccessPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorFluidAccessPortEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
import it.zerono.mods.zerocore.base.client.screen.BaseScreenToolTipsBuilder;
import it.zerono.mods.zerocore.base.client.screen.ClientBaseHelper;
import it.zerono.mods.zerocore.base.client.screen.control.BarsPanel;
import it.zerono.mods.zerocore.base.client.screen.control.FluidBar;
import it.zerono.mods.zerocore.base.client.screen.control.MachineStatusIndicator;
import it.zerono.mods.zerocore.lib.client.gui.ButtonState;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.control.Button;
import it.zerono.mods.zerocore.lib.client.gui.control.Panel;
import it.zerono.mods.zerocore.lib.client.gui.control.SwitchPictureButton;
import it.zerono.mods.zerocore.lib.client.gui.layout.AnchoredLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.layout.FixedLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.data.geometry.Point;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;

public class ReactorFluidAccessPortScreen
        extends CommonMultiblockScreen<MultiblockReactor, ReactorFluidAccessPortEntity, ReactorFluidAccessPortContainer> {

    public ReactorFluidAccessPortScreen(final ReactorFluidAccessPortContainer container,
                                        final PlayerInventory inventory, final ITextComponent title) {

        super(container, inventory, PlayerInventoryUsage.None, title,
                mainTextureFromVariant(container.getTileEntity().getMultiblockVariant().orElse(ReactorVariant.Basic)));

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, ExtremeReactors.newID("reactor/part-fluidaccessport"), 1);

        this._btnInputDirection = new SwitchPictureButton(this, "directionInput", false, "direction");
        ClientBaseHelper.setButtonSpritesAndOverlayForState(this._btnInputDirection, ButtonState.Default, CommonIcons.ButtonInputDirection);
        ClientBaseHelper.setButtonSpritesAndOverlayForState(this._btnInputDirection, ButtonState.Active, CommonIcons.ButtonInputDirectionActive);
        this._btnInputDirection.Activated.subscribe(this::onInputActivated);
        this._btnInputDirection.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.reactor.fluidaccessport.directioninput.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.reactor.fluidaccessport.directioninput.tooltip.body")
        );

        this._btnOutputDirection = new SwitchPictureButton(this, "directionOutput", false, "direction");
        ClientBaseHelper.setButtonSpritesAndOverlayForState(this._btnOutputDirection, ButtonState.Default, CommonIcons.ButtonOutputDirection);
        ClientBaseHelper.setButtonSpritesAndOverlayForState(this._btnOutputDirection, ButtonState.Active, CommonIcons.ButtonOutputDirectionActive);
        this._btnOutputDirection.Activated.subscribe(this::onOutputActivated);
        this._btnOutputDirection.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.reactor.fluidaccessport.directionoutput.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.reactor.fluidaccessport.directionoutput.tooltip.body")
        );

        container.DIRECTION.bind(direction -> {

            this._btnInputDirection.setActive(direction.isInput());
            this._btnOutputDirection.setActive(direction.isOutput());
        });

        this._btnDumpFuel = new Button(this, "dumpFuel", "");
        this._btnDumpFuel.setPadding(0);
        this._btnDumpFuel.setIconForState(CommonIcons.ButtonDumpFuel.get(), ButtonState.Default);
        this._btnDumpFuel.setIconForState(CommonIcons.ButtonDumpFuelActive.get(), ButtonState.Active, ButtonState.ActiveHighlighted, ButtonState.DefaultHighlighted);
        this._btnDumpFuel.Clicked.subscribe(this::onDumpFuel);
        this._btnDumpFuel.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.reactor.fluidaccessport.dumpfuel.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.reactor.fluidaccessport.dumpfuel.tooltip.body")
        );

        this._btnDumpWaste = new Button(this, "dumpWaste", "");
        this._btnDumpWaste.setPadding(0);
        this._btnDumpWaste.setIconForState(CommonIcons.ButtonDumpWaste.get(), ButtonState.Default);
        this._btnDumpWaste.setIconForState(CommonIcons.ButtonDumpWasteActive.get(), ButtonState.Active, ButtonState.ActiveHighlighted, ButtonState.DefaultHighlighted);
        this._btnDumpWaste.Clicked.subscribe(this::onDumpWaste);
        this._btnDumpWaste.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.reactor.fluidaccessport.dumpwaste.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.reactor.fluidaccessport.dumpwaste.tooltip.body")
        );

        this._fuelTank = new FluidBar(this, "fuelTank", ReactorFluidAccessPortEntity.TANK_CAPACITY,
                container.FUEL_STACK, CommonIcons.FuelReactantIcon, "gui.bigreactors.reactor.fluidaccessport.fueltank.tooltip.title",
                "gui.bigreactors.reactor.fluidaccessport.fueltank.tooltip.body");

        this._wasteTank = new FluidBar(this, "wasteTank", ReactorFluidAccessPortEntity.TANK_CAPACITY,
                container.WASTE_STACK, CommonIcons.WasteReactantIcon, "gui.bigreactors.reactor.fluidaccessport.wastetank.tooltip.title",
                "gui.bigreactors.reactor.fluidaccessport.wastetank.tooltip.body");
    }

    //region CommonMultiblockScreen

    @Override
    protected MachineStatusIndicator createStatusIndicator(ReactorFluidAccessPortContainer container) {
        return this.createReactorStatusIndicator(container.ACTIVE);
    }

    /**
     * Called when this screen is being created for the first time.
     * Override to handle this event
     */
    @Override
    protected void onScreenCreate() {

        super.onScreenCreate();

        final Panel panel = new Panel(this, "fluidaccessport");

        panel.setLayoutEngineHint(FixedLayoutEngine.hint(0, 0));
        panel.setDesiredDimension(this.getGuiWidth(), this.getGuiHeight() - 21);
        panel.setLayoutEngine(new AnchoredLayoutEngine()
                .setHorizontalMargin(36)
                .setVerticalMargin(13));

        panel.addControl(this.buttonsPanel(this._btnInputDirection, this._btnOutputDirection, this._btnDumpFuel, this._btnDumpWaste));

        final BarsPanel barsPanel = new BarsPanel(this, "bars")
                .add(this._fuelTank)
                .addVerticalSeparator()
                .add(this._wasteTank);

        barsPanel.setLayoutEngineHint(AnchoredLayoutEngine.Anchor.TopRight);
        panel.addControl(barsPanel);
        this.addControl(panel);
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

        final CompoundNBT options = new CompoundNBT();

        options.putBoolean("void", Screen.hasShiftDown());
        this.sendCommandToServer(CommonConstants.COMMAND_DUMP_FUEL, options);
    }

    private void onDumpWaste(Button button, Integer integer) {

        final CompoundNBT options = new CompoundNBT();

        options.putBoolean("void", Screen.hasShiftDown());
        this.sendCommandToServer(CommonConstants.COMMAND_DUMP_WASTE, options);
    }

    private Panel buttonsPanel(final IControl setInput, final IControl setOutput,
                               final IControl dumpFuel, final IControl dumpWaste) {

        final Panel p = new Panel(this);

        p.setDesiredDimension(18 * 2 + 2, 18 * 2 + 2 + 18);
        p.setLayoutEngineHint(AnchoredLayoutEngine.Anchor.TopLeft);
        p.setCustomBackgroundPainter((panel, matrix) -> {

            final Point xy = panel.controlToScreen(0, 18);
            final ISprite border = CommonIcons.ImageButtonBorder.get();
            final int z = (int)panel.getGui().getZLevel();

            ModRenderHelper.paintSprite(matrix, border, xy.X, xy.Y, z, 18, 18);
            ModRenderHelper.paintSprite(matrix, border, xy.X + 20, xy.Y, z, 18, 18);
            ModRenderHelper.paintSprite(matrix, border, xy.X, xy.Y + 20, z, 18, 18);
            ModRenderHelper.paintSprite(matrix, border, xy.X + 20, xy.Y + 20, z, 18, 18);
        });

        setInput.setLayoutEngineHint(FixedLayoutEngine.hint(1, 1+18, 16, 16));
        p.addControl(setInput);

        setOutput.setLayoutEngineHint(FixedLayoutEngine.hint(21, 1+18, 16, 16));
        p.addControl(setOutput);

        dumpFuel.setLayoutEngineHint(FixedLayoutEngine.hint(1, 21+18, 16, 16));
        p.addControl(dumpFuel);

        dumpWaste.setLayoutEngineHint(FixedLayoutEngine.hint(21, 21+18, 16, 16));
        p.addControl(dumpWaste);

        return p;
    }

    private final SwitchPictureButton _btnInputDirection;
    private final SwitchPictureButton _btnOutputDirection;
    private final Button _btnDumpFuel;
    private final Button _btnDumpWaste;
    private final FluidBar _fuelTank;
    private final FluidBar _wasteTank;

    //endregion
}
