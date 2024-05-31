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
import it.zerono.mods.zerocore.lib.client.gui.layout.FlowLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.data.geometry.Point;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;

import static it.zerono.mods.zerocore.base.client.screen.ClientBaseHelper.SQUARE_BUTTON_DIMENSION;

public class ReactorFluidAccessPortScreen
        extends CommonMultiblockScreen<MultiblockReactor, ReactorFluidAccessPortEntity, ReactorFluidAccessPortContainer> {

    public ReactorFluidAccessPortScreen(final ReactorFluidAccessPortContainer container,
                                        final PlayerInventory inventory, final ITextComponent title) {

        super(container, inventory, PlayerInventoryUsage.None, title,
                mainTextureFromVariant(container.getTileEntity().getMultiblockVariant().orElse(ReactorVariant.Basic)));

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, ExtremeReactors.newID("reactor/part-fluidaccessport"), 1);

        final SwitchPictureButton inputDirection = new SwitchPictureButton(this, "directionInput", false, "direction");

        ClientBaseHelper.setButtonSpritesAndOverlayForState(inputDirection, ButtonState.Default, CommonIcons.ButtonInputDirection);
        ClientBaseHelper.setButtonSpritesAndOverlayForState(inputDirection, ButtonState.Active, CommonIcons.ButtonInputDirectionActive);
        inputDirection.setDesiredDimension(16, 16);
        inputDirection.Activated.subscribe(this::onInputActivated);
        inputDirection.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.reactor.fluidaccessport.directioninput.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.reactor.fluidaccessport.directioninput.tooltip.body")
        );

        final SwitchPictureButton outputDirection = new SwitchPictureButton(this, "directionOutput", false, "direction");

        ClientBaseHelper.setButtonSpritesAndOverlayForState(outputDirection, ButtonState.Default, CommonIcons.ButtonOutputDirection);
        ClientBaseHelper.setButtonSpritesAndOverlayForState(outputDirection, ButtonState.Active, CommonIcons.ButtonOutputDirectionActive);
        outputDirection.setDesiredDimension(16, 16);
        outputDirection.Activated.subscribe(this::onOutputActivated);
        outputDirection.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.reactor.fluidaccessport.directionoutput.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.reactor.fluidaccessport.directionoutput.tooltip.body")
        );

        container.DIRECTION.bind(direction -> {

            inputDirection.setActive(direction.isInput());
            outputDirection.setActive(direction.isOutput());
        });

        final Button dumpFuel = new Button(this, "dumpFuel", "");

        dumpFuel.setPadding(0);
        dumpFuel.setIconForState(CommonIcons.ButtonDumpFuel.get(), ButtonState.Default);
        dumpFuel.setIconForState(CommonIcons.ButtonDumpFuelActive.get(), ButtonState.Active, ButtonState.ActiveHighlighted, ButtonState.DefaultHighlighted);
        dumpFuel.setDesiredDimension(16, 16);
        dumpFuel.Clicked.subscribe(this::onDumpFuel);
        dumpFuel.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.reactor.fluidaccessport.dumpfuel.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.reactor.fluidaccessport.dumpfuel.tooltip.body")
        );

        final Button dumpWaste = new Button(this, "dumpWaste", "");

        dumpWaste.setPadding(0);
        dumpWaste.setIconForState(CommonIcons.ButtonDumpWaste.get(), ButtonState.Default);
        dumpWaste.setIconForState(CommonIcons.ButtonDumpWasteActive.get(), ButtonState.Active, ButtonState.ActiveHighlighted, ButtonState.DefaultHighlighted);
        dumpWaste.setDesiredDimension(16, 16);
        dumpWaste.Clicked.subscribe(this::onDumpWaste);
        dumpWaste.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.reactor.fluidaccessport.dumpwaste.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.reactor.fluidaccessport.dumpwaste.tooltip.body")
        );

        this._buttonsPanel = this.buttonsPanel(inputDirection, outputDirection, dumpFuel, dumpWaste);

        final FluidBar fuelTank = new FluidBar(this, "fuelTank", ReactorFluidAccessPortEntity.TANK_CAPACITY,
                container.FUEL_STACK, CommonIcons.FuelReactantIcon, "gui.bigreactors.reactor.fluidaccessport.fueltank.tooltip.title",
                "gui.bigreactors.reactor.fluidaccessport.fueltank.tooltip.body");

        final FluidBar wasteTank = new FluidBar(this, "wasteTank", ReactorFluidAccessPortEntity.TANK_CAPACITY,
                container.WASTE_STACK, CommonIcons.WasteReactantIcon, "gui.bigreactors.reactor.fluidaccessport.wastetank.tooltip.title",
                "gui.bigreactors.reactor.fluidaccessport.wastetank.tooltip.body");

        this._barsPanel = new BarsPanel(this, "bars")
                .add(fuelTank)
                .addVerticalSeparator()
                .add(wasteTank);
        this._barsPanel.setLayoutEngineHint(AnchoredLayoutEngine.Anchor.TopRight);

        this.setContentLayoutEngine(new AnchoredLayoutEngine()
                .setHorizontalMargin(36)
                .setVerticalMargin(13));
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
        this.addControl(this._buttonsPanel);
        this.addControl(this._barsPanel);
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

        p.setDesiredDimension(SQUARE_BUTTON_DIMENSION * 2 + 2,
                SQUARE_BUTTON_DIMENSION * 2 + 2 + SQUARE_BUTTON_DIMENSION * 2);
        p.setLayoutEngineHint(AnchoredLayoutEngine.Anchor.TopLeft);
        p.setCustomBackgroundPainter((panel, matrix) -> {

            final Point xy = panel.controlToScreen(0, SQUARE_BUTTON_DIMENSION);
            final ISprite border = CommonIcons.ImageButtonBorder.get();
            final int z = (int)panel.getGui().getZLevel();

            ModRenderHelper.paintSprite(matrix, border, xy.X, xy.Y, z, SQUARE_BUTTON_DIMENSION, SQUARE_BUTTON_DIMENSION);
            ModRenderHelper.paintSprite(matrix, border, xy.X + 2 + SQUARE_BUTTON_DIMENSION, xy.Y, z, SQUARE_BUTTON_DIMENSION, SQUARE_BUTTON_DIMENSION);
            ModRenderHelper.paintSprite(matrix, border, xy.X, xy.Y + 2 + SQUARE_BUTTON_DIMENSION, z, SQUARE_BUTTON_DIMENSION, SQUARE_BUTTON_DIMENSION);
            ModRenderHelper.paintSprite(matrix, border, xy.X + 2 + SQUARE_BUTTON_DIMENSION, xy.Y + 2 + SQUARE_BUTTON_DIMENSION, z, SQUARE_BUTTON_DIMENSION, SQUARE_BUTTON_DIMENSION);
        });

        p.setLayoutEngine(new FlowLayoutEngine()
                .setZeroMargins()
                .setVerticalMargin(1 + SQUARE_BUTTON_DIMENSION)
                .setHorizontalMargin(1)
                .setControlsSpacing(4));

        p.addControl(setInput, setOutput, dumpFuel, dumpWaste);
        return p;
    }

    private final IControl _buttonsPanel, _barsPanel;

    //endregion
}
