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
import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity;
import it.zerono.mods.zerocore.base.multiblock.part.io.fluid.IFluidPort;
import it.zerono.mods.zerocore.lib.IActivableMachine;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockVariantProvider;
import it.zerono.mods.zerocore.lib.client.gui.ButtonState;
import it.zerono.mods.zerocore.lib.client.gui.control.SwitchPictureButton;
import it.zerono.mods.zerocore.lib.client.gui.layout.FixedLayoutEngine;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockMachine;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public abstract class AbstractFluidPortScreen<Controller extends AbstractGeneratorMultiblockController<Controller, V> & IMultiblockMachine & IActivableMachine,
        V extends IMultiblockGeneratorVariant,
        T extends AbstractMultiblockEntity<Controller> & IFluidPort & IMultiblockVariantProvider<V> & INamedContainerProvider>
        extends CommonMultiblockScreen<Controller, T, FluidPortContainer<Controller, V, T>> {

    protected AbstractFluidPortScreen(final FluidPortContainer<Controller, V, T> container, final PlayerInventory inventory,
                                      final ITextComponent title, final ResourceLocation bookEntryId) {

        super(container, inventory, PlayerInventoryUsage.Both, title, 224, 98,
                halfTextureFromVariant(container.getTileEntity().getMultiblockVariant().orElseThrow(IllegalStateException::new)));

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, bookEntryId, 1);

        this._btnInputDirection = new SwitchPictureButton(this, "directionInput", false, "direction");
        this.setButtonSpritesAndOverlayForState(this._btnInputDirection, ButtonState.Default, CommonIcons.ButtonInputDirection);
        this.setButtonSpritesAndOverlayForState(this._btnInputDirection, ButtonState.Active, CommonIcons.ButtonInputDirectionActive);
        this._btnInputDirection.setLayoutEngineHint(FixedLayoutEngine.hint(83, 26, 18, 18));
        this._btnInputDirection.setBackground(CommonIcons.ImageButtonBackground.get());
        this._btnInputDirection.setPadding(1);
        this._btnInputDirection.Activated.subscribe(this::onInputActivated);
        this._btnInputDirection.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.generator.fluidport.directioninput.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.generator.fluidport.directioninput.tooltip.body")
        );

        this._btnOutputDirection = new SwitchPictureButton(this, "directionOutput", false, "direction");
        this.setButtonSpritesAndOverlayForState(this._btnOutputDirection, ButtonState.Default, CommonIcons.ButtonOutputDirection);
        this.setButtonSpritesAndOverlayForState(this._btnOutputDirection, ButtonState.Active, CommonIcons.ButtonOutputDirectionActive);
        this._btnOutputDirection.setLayoutEngineHint(FixedLayoutEngine.hint(123, 26, 18, 18));
        this._btnOutputDirection.setBackground(CommonIcons.ImageButtonBackground.get());
        this._btnOutputDirection.setPadding(1);
        this._btnOutputDirection.Activated.subscribe(this::onOutputActivated);
        this._btnOutputDirection.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.generator.fluidport.directionoutput.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.generator.fluidport.directionoutput.tooltip.body")
        );

        container.DIRECTION.bind(direction -> {

            this._btnInputDirection.setActive(direction.isInput());
            this._btnOutputDirection.setActive(direction.isOutput());
        });
    }

    //region AbstractMultiblockScreen

    @Override
    protected void onScreenCreate() {

        super.onScreenCreate();
        this.addControl(this._btnInputDirection);
        this.addControl(this._btnOutputDirection);
    }

    //endregion
    //region  internals

    private void onInputActivated(SwitchPictureButton button) {
        this.sendCommandToServer(CommonConstants.COMMAND_SET_INPUT);
    }

    private void onOutputActivated(SwitchPictureButton button) {
        this.sendCommandToServer(CommonConstants.COMMAND_SET_OUTPUT);
    }

    private final SwitchPictureButton _btnInputDirection;
    private final SwitchPictureButton _btnOutputDirection;

    //endregion
}
