/*
 * EnergizerPowerPortScreen
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * Do not remove or edit this header
 *
 */

package it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.client.screen;

import it.zerono.mods.extremereactors.CommonLocations;
import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonMultiblockScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.MultiBlockEnergizer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.container.EnergizerPowerPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.part.EnergizerPowerPortEntity;
import it.zerono.mods.zerocore.base.client.screen.BaseScreenToolTipsBuilder;
import it.zerono.mods.zerocore.base.client.screen.ClientBaseHelper;
import it.zerono.mods.zerocore.base.client.screen.control.MachineStatusIndicator;
import it.zerono.mods.zerocore.lib.client.gui.ButtonState;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.control.Panel;
import it.zerono.mods.zerocore.lib.client.gui.control.SwitchPictureButton;
import it.zerono.mods.zerocore.lib.client.gui.layout.AnchoredLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.sprite.SpriteTextureMap;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import static it.zerono.mods.zerocore.base.client.screen.ClientBaseHelper.SQUARE_BUTTON_DIMENSION;

public class EnergizerPowerPortScreen
        extends CommonMultiblockScreen<MultiBlockEnergizer, EnergizerPowerPortEntity, EnergizerPowerPortContainer> {

    public EnergizerPowerPortScreen(EnergizerPowerPortContainer container, Inventory inventory, Component title) {

        super(container, inventory, PlayerInventoryUsage.None, title, 224, 98,
                () -> new SpriteTextureMap(CommonLocations.TEXTURES_GUI_MULTIBLOCK
                        .buildWithSuffix("basic_background_half.png"), 256, 98));

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, CommonLocations.ENERGIZER.buildWithSuffix("part-forgepowerport"), 1);

        final SwitchPictureButton inputDirection = new SwitchPictureButton(this, "directionInput", false, "direction");

        inputDirection.setDesiredDimension(SQUARE_BUTTON_DIMENSION, SQUARE_BUTTON_DIMENSION);
        ClientBaseHelper.setButtonSpritesAndOverlayForState(inputDirection, ButtonState.Default, CommonIcons.ButtonInputDirection);
        ClientBaseHelper.setButtonSpritesAndOverlayForState(inputDirection, ButtonState.Active, CommonIcons.ButtonInputDirectionActive);
        inputDirection.setBackground(CommonIcons.ImageButtonBackground.get());
        inputDirection.setPadding(1);
        inputDirection.Activated.subscribe(this::onInputActivated);
        inputDirection.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.energizer.powerport.directioninput.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.energizer.powerport.directioninput.tooltip.body")
        );

        final SwitchPictureButton outputDirection = new SwitchPictureButton(this, "directionOutput", false, "direction");

        outputDirection.setDesiredDimension(SQUARE_BUTTON_DIMENSION, SQUARE_BUTTON_DIMENSION);
        ClientBaseHelper.setButtonSpritesAndOverlayForState(outputDirection, ButtonState.Default, CommonIcons.ButtonOutputDirection);
        ClientBaseHelper.setButtonSpritesAndOverlayForState(outputDirection, ButtonState.Active, CommonIcons.ButtonOutputDirectionActive);
        outputDirection.setBackground(CommonIcons.ImageButtonBackground.get());
        outputDirection.setPadding(1);
        outputDirection.Activated.subscribe(this::onOutputActivated);
        outputDirection.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.energizer.powerport.directionoutput.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.energizer.powerport.directionoutput.tooltip.body")
        );

        container.DIRECTION.bind(direction -> {

            inputDirection.setActive(direction.isInput());
            outputDirection.setActive(direction.isOutput());
        });

        this._buttons = this.buttonsPanel(inputDirection, outputDirection);

        this.setContentLayoutEngine(new AnchoredLayoutEngine().setZeroMargins());
    }

    //region AbstractMultiblockScreen

    @Override
    protected MachineStatusIndicator createStatusIndicator(EnergizerPowerPortContainer container) {
        return this.createEnergizerStatusIndicator(container.ACTIVE);
    }

    @Override
    protected void onScreenCreate() {

        super.onScreenCreate();
        this.addControl(this._buttons);
    }

    //endregion
    //region internals

    private void onInputActivated(SwitchPictureButton button) {
        this.sendCommandToServer(CommonConstants.COMMAND_SET_INPUT);
    }

    private void onOutputActivated(SwitchPictureButton button) {
        this.sendCommandToServer(CommonConstants.COMMAND_SET_OUTPUT);
    }

    private Panel buttonsPanel(IControl input, IControl output) {

        final Panel p = new Panel(this);

        p.setDesiredDimension(SQUARE_BUTTON_DIMENSION * 5, SQUARE_BUTTON_DIMENSION);
        p.setLayoutEngineHint(AnchoredLayoutEngine.Anchor.Center);
        p.setLayoutEngine(new AnchoredLayoutEngine().setZeroMargins());
        p.addControl(input, output);

        input.setLayoutEngineHint(AnchoredLayoutEngine.Anchor.Left);
        output.setLayoutEngineHint(AnchoredLayoutEngine.Anchor.Right);

        return p;
    }

    private final IControl _buttons;

    //endregion
}
