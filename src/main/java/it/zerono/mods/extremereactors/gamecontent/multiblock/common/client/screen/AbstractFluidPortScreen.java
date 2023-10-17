/*
 *
 * AbstractFluidPortScreen.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen;

import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.AbstractGeneratorMultiblockController;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.container.FluidPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockGeneratorVariant;
import it.zerono.mods.zerocore.base.client.screen.BaseScreenToolTipsBuilder;
import it.zerono.mods.zerocore.base.client.screen.ClientBaseHelper;
import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity;
import it.zerono.mods.zerocore.base.multiblock.part.io.fluid.IFluidPort;
import it.zerono.mods.zerocore.lib.IActivableMachine;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockVariantProvider;
import it.zerono.mods.zerocore.lib.client.gui.ButtonState;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.control.Panel;
import it.zerono.mods.zerocore.lib.client.gui.control.SwitchPictureButton;
import it.zerono.mods.zerocore.lib.client.gui.layout.AnchoredLayoutEngine;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockMachine;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import static it.zerono.mods.zerocore.base.client.screen.ClientBaseHelper.SQUARE_BUTTON_DIMENSION;

public abstract class AbstractFluidPortScreen<Controller extends AbstractGeneratorMultiblockController<Controller, V> & IMultiblockMachine & IActivableMachine,
        V extends IMultiblockGeneratorVariant,
        T extends AbstractMultiblockEntity<Controller> & IFluidPort & IMultiblockVariantProvider<V> & INamedContainerProvider>
        extends CommonMultiblockScreen<Controller, T, FluidPortContainer<Controller, V, T>> {

    protected AbstractFluidPortScreen(final FluidPortContainer<Controller, V, T> container, final PlayerInventory inventory,
                                      final ITextComponent title, final ResourceLocation bookEntryId) {

        super(container, inventory, PlayerInventoryUsage.Both, title, 224, 98,
                halfTextureFromVariant(container.getTileEntity().getMultiblockVariant().orElseThrow(IllegalStateException::new)));

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, bookEntryId, 1);

        final SwitchPictureButton inputDirection = new SwitchPictureButton(this, "directionInput", false, "direction");

        inputDirection.setDesiredDimension(SQUARE_BUTTON_DIMENSION, SQUARE_BUTTON_DIMENSION);
        ClientBaseHelper.setButtonSpritesAndOverlayForState(inputDirection, ButtonState.Default, CommonIcons.ButtonInputDirection);
        ClientBaseHelper.setButtonSpritesAndOverlayForState(inputDirection, ButtonState.Active, CommonIcons.ButtonInputDirectionActive);
        inputDirection.setBackground(CommonIcons.ImageButtonBackground.get());
        inputDirection.setPadding(1);
        inputDirection.Activated.subscribe(this::onInputActivated);
        inputDirection.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.generator.fluidport.directioninput.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.generator.fluidport.directioninput.tooltip.body")
        );

        final SwitchPictureButton outputDirection = new SwitchPictureButton(this, "directionOutput", false, "direction");

        outputDirection.setDesiredDimension(SQUARE_BUTTON_DIMENSION, SQUARE_BUTTON_DIMENSION);
        ClientBaseHelper.setButtonSpritesAndOverlayForState(outputDirection, ButtonState.Default, CommonIcons.ButtonOutputDirection);
        ClientBaseHelper.setButtonSpritesAndOverlayForState(outputDirection, ButtonState.Active, CommonIcons.ButtonOutputDirectionActive);
        outputDirection.setBackground(CommonIcons.ImageButtonBackground.get());
        outputDirection.setPadding(1);
        outputDirection.Activated.subscribe(this::onOutputActivated);
        outputDirection.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.generator.fluidport.directionoutput.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.generator.fluidport.directionoutput.tooltip.body")
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
    protected void onScreenCreate() {

        super.onScreenCreate();
        this.addControl(this._buttons);
    }

    //endregion
    //region  internals

    private void onInputActivated(SwitchPictureButton button) {
        this.sendCommandToServer(CommonConstants.COMMAND_SET_INPUT);
    }

    private void onOutputActivated(SwitchPictureButton button) {
        this.sendCommandToServer(CommonConstants.COMMAND_SET_OUTPUT);
    }

    private Panel buttonsPanel(final IControl input, final IControl output) {

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
