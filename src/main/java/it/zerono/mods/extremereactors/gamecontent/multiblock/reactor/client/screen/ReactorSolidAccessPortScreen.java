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
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonMultiblockScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.container.ReactorSolidAccessPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorSolidAccessPortEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
import it.zerono.mods.zerocore.base.client.screen.BaseScreenToolTipsBuilder;
import it.zerono.mods.zerocore.base.client.screen.ClientBaseHelper;
import it.zerono.mods.zerocore.base.client.screen.control.MachineStatusIndicator;
import it.zerono.mods.zerocore.lib.client.gui.ButtonState;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.control.*;
import it.zerono.mods.zerocore.lib.client.gui.layout.*;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.data.geometry.Point;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static it.zerono.mods.zerocore.base.client.screen.ClientBaseHelper.SQUARE_BUTTON_DIMENSION;

public class ReactorSolidAccessPortScreen
        extends CommonMultiblockScreen<MultiblockReactor, ReactorSolidAccessPortEntity, ReactorSolidAccessPortContainer> {

    public ReactorSolidAccessPortScreen(final ReactorSolidAccessPortContainer container,
                                        final Inventory inventory, final Component title) {

        super(container, inventory, PlayerInventoryUsage.Both, title,
                mainTextureFromVariant(container.getTileEntity().getMultiblockVariant().orElse(ReactorVariant.Basic)));

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, CommonLocations.REACTOR.buildWithSuffix("part-solidaccessport"), 1);

        final SwitchPictureButton inputDirection, outputDirection;
        final Button dumpFuel, dumpWaste;

        inputDirection = new SwitchPictureButton(this, "directionInput", false, "direction");
        inputDirection.setDesiredDimension(16, 16);
        ClientBaseHelper.setButtonSpritesAndOverlayForState(inputDirection, ButtonState.Default, CommonIcons.ButtonInputDirection);
        ClientBaseHelper.setButtonSpritesAndOverlayForState(inputDirection, ButtonState.Active, CommonIcons.ButtonInputDirectionActive);
        inputDirection.Activated.subscribe(this::onInputActivated);
        inputDirection.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.reactor.solidaccessport.directioninput.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.reactor.solidaccessport.directioninput.tooltip.body")
        );

        outputDirection = new SwitchPictureButton(this, "directionOutput", false, "direction");
        outputDirection.setDesiredDimension(16, 16);
        ClientBaseHelper.setButtonSpritesAndOverlayForState(outputDirection, ButtonState.Default, CommonIcons.ButtonOutputDirection);
        ClientBaseHelper.setButtonSpritesAndOverlayForState(outputDirection, ButtonState.Active, CommonIcons.ButtonOutputDirectionActive);
        outputDirection.Activated.subscribe(this::onOutputActivated);
        outputDirection.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.reactor.solidaccessport.directionoutput.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.reactor.solidaccessport.directionoutput.tooltip.body")
        );

        container.DIRECTION.bind(direction -> {

            inputDirection.setActive(direction.isInput());
            outputDirection.setActive(direction.isOutput());
        });

        dumpFuel = new Button(this, "dumpFuel", "");
        dumpFuel.setDesiredDimension(16, 16);
        dumpFuel.setPadding(0);
        dumpFuel.setIconForState(CommonIcons.ButtonDumpFuel.get(), ButtonState.Default);
        dumpFuel.setIconForState(CommonIcons.ButtonDumpFuelActive.get(), ButtonState.Active, ButtonState.ActiveHighlighted, ButtonState.DefaultHighlighted);
        dumpFuel.Clicked.subscribe(this::onDumpFuel);
        dumpFuel.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.reactor.solidaccessport.dumpfuel.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.reactor.solidaccessport.dumpfuel.tooltip.body")
        );

        dumpWaste = new Button(this, "dumpWaste", "");
        dumpWaste.setDesiredDimension(16, 16);
        dumpWaste.setPadding(0);
        dumpWaste.setIconForState(CommonIcons.ButtonDumpWaste.get(), ButtonState.Default);
        dumpWaste.setIconForState(CommonIcons.ButtonDumpWasteActive.get(), ButtonState.Active, ButtonState.ActiveHighlighted, ButtonState.DefaultHighlighted);
        dumpWaste.Clicked.subscribe(this::onDumpWaste);
        dumpWaste.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.reactor.solidaccessport.dumpwaste.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.reactor.solidaccessport.dumpwaste.tooltip.body")
        );

        this._buttonsPanel = this.buttonsPanel(inputDirection, outputDirection, dumpFuel, dumpWaste);

        this._fuelInputGroup = this.slotPanel("fuelinput", ReactantType.Fuel, CommonIcons.PortInputSlot);
        this._wasteOutputGroup = this.slotPanel("wasteoutput", ReactantType.Waste, CommonIcons.PortOutputSlot);
        this._playerInventoryGroup = this.createPlayerInventorySlotsGroupControl();
        this._playerHotBarGroup = this.createPlayerHotBarSlotsGroupControl();
    }

    //region AbstractMultiblockScreen

    @Override
    protected MachineStatusIndicator createStatusIndicator(ReactorSolidAccessPortContainer container) {
        return this.createReactorStatusIndicator(container.ACTIVE);
    }

    /**
     * Called when this screen is being created for the first time.
     * Override to handle this event
     */
    @Override
    protected void onScreenCreate() {

        super.onScreenCreate();

        this.setContentLayoutEngine(new VerticalLayoutEngine()
                .setZeroMargins()
                .setVerticalAlignment(VerticalAlignment.Bottom)
                .setControlsSpacing(4));

        final Table ioPane = new Table(this, "ioPane", layout -> layout
                .columns(3, SQUARE_BUTTON_DIMENSION * 3, SQUARE_BUTTON_DIMENSION * 3, SQUARE_BUTTON_DIMENSION * 3)
                .rows(1));

        ioPane.setDesiredDimension(ClientBaseHelper.INVENTORY_SLOTS_ROW_WIDTH, ClientBaseHelper.SQUARE_BUTTON_DIMENSION + 20 + 10);

        ioPane.addCellContent(this._buttonsPanel, hint -> hint
                .setHorizontalAlignment(HorizontalAlignment.Left)
                .setVerticalAlignment(VerticalAlignment.Center));

        ioPane.addCellContent(this._fuelInputGroup, hint -> hint
                .setHorizontalAlignment(HorizontalAlignment.Center)
                .setVerticalAlignment(VerticalAlignment.Center));

        ioPane.addCellContent(this._wasteOutputGroup, hint -> hint
                .setHorizontalAlignment(HorizontalAlignment.Center)
                .setVerticalAlignment(VerticalAlignment.Center));

        this.addControl(ioPane);
        this.addControl(this._playerInventoryGroup);
        this.addControl(this._playerHotBarGroup);
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

    private Panel buttonsPanel(final IControl setInput, final IControl setOutput,
                               final IControl dumpFuel, final IControl dumpWaste) {

        final Panel p = new Panel(this, "buttons");

        p.setDesiredDimension(SQUARE_BUTTON_DIMENSION * 2 + 2, SQUARE_BUTTON_DIMENSION * 2 + 2);
        p.setLayoutEngineHint(FixedLayoutEngine.hint(0, 5));

        p.setCustomBackgroundPainter((panel, matrix) -> {

            final Point xy = panel.controlToScreen(0, 0);
            final ISprite border = CommonIcons.ImageButtonBorder.get();
            final int z = (int)panel.getGui().getZLevel();

            ModRenderHelper.paintSprite(matrix, border, xy.X, xy.Y, z, SQUARE_BUTTON_DIMENSION, SQUARE_BUTTON_DIMENSION);
            ModRenderHelper.paintSprite(matrix, border, xy.X + 2 + SQUARE_BUTTON_DIMENSION, xy.Y, z, SQUARE_BUTTON_DIMENSION, SQUARE_BUTTON_DIMENSION);
            ModRenderHelper.paintSprite(matrix, border, xy.X, xy.Y + 2 + SQUARE_BUTTON_DIMENSION, z, SQUARE_BUTTON_DIMENSION, SQUARE_BUTTON_DIMENSION);
            ModRenderHelper.paintSprite(matrix, border, xy.X + 2 + SQUARE_BUTTON_DIMENSION, xy.Y + 2 + SQUARE_BUTTON_DIMENSION, z, SQUARE_BUTTON_DIMENSION, SQUARE_BUTTON_DIMENSION);
        });

        p.setLayoutEngine(new FlowLayoutEngine()
                .setZeroMargins()
                .setVerticalMargin(1)
                .setHorizontalMargin(1)
                .setControlsSpacing(4));

        p.addControl(setInput, setOutput, dumpFuel, dumpWaste);
        return p;
    }

    private Panel slotPanel(final String groupName, final ReactantType reactant,
                            final Supplier<@NotNull ISprite> slotBackground) {

        final ISprite sprite = slotBackground.get();
        final SlotsGroup sg = this.createSingleSlotGroupControl(groupName, reactant.name());
        final Panel p = new Panel(this);

        sg.setLayoutEngineHint(FixedLayoutEngine.hint(10, 10, SQUARE_BUTTON_DIMENSION, SQUARE_BUTTON_DIMENSION));

        p.setBackground(sprite);
        p.setDesiredDimension(sprite.getWidth(), sprite.getHeight());
        p.addControl(sg);

        return p;
    }

    private final IControl _buttonsPanel;
    private final IControl _fuelInputGroup;
    private final IControl _wasteOutputGroup;
    private final IControl _playerInventoryGroup;
    private final IControl _playerHotBarGroup;

    //endregion
}
