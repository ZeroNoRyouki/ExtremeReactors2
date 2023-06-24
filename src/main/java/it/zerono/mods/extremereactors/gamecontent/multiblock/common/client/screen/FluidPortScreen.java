/*
 *
 * FluidPortScreen.java
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
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockGeneratorVariant;
import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity;
import it.zerono.mods.zerocore.base.multiblock.part.io.fluid.IFluidPort;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockVariantProvider;
import it.zerono.mods.zerocore.lib.client.gui.ButtonState;
import it.zerono.mods.zerocore.lib.client.gui.control.SwitchPictureButton;
import it.zerono.mods.zerocore.lib.client.gui.databind.BindingGroup;
import it.zerono.mods.zerocore.lib.client.gui.databind.MonoConsumerBinding;
import it.zerono.mods.zerocore.lib.client.gui.layout.FixedLayoutEngine;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockMachine;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;

import java.util.function.Consumer;
import java.util.function.Function;

import static it.zerono.mods.zerocore.lib.CodeHelper.TEXT_EMPTY_LINE;

public class FluidPortScreen<Controller extends AbstractGeneratorMultiblockController<Controller, V> & IMultiblockMachine,
        V extends IMultiblockGeneratorVariant,
        T extends AbstractMultiblockEntity<Controller> & IFluidPort & IMultiblockVariantProvider<V> & MenuProvider>
        extends AbstractMultiblockScreen<Controller, T, ModTileContainer<T>> {

    public FluidPortScreen(final ModTileContainer<T> container, final Inventory inventory,
                           final Component title, final ResourceLocation bookEntryId) {

        super(container, inventory, PlayerInventoryUsage.Both, title, 224, 98,
                halfTextureFromVariant(container.getTileEntity().getMultiblockVariant().orElseThrow(IllegalStateException::new)));

        this._bindings = new BindingGroup();

        this._btnInputDirection = new SwitchPictureButton(this, "directionInput", false, "direction");
        this._btnOutputDirection = new SwitchPictureButton(this, "directionOutput", false, "direction");

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, bookEntryId, 1);
    }

    //region ContainerScreen

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    //endregion
    //region AbstractMultiblockScreen

    @Override
    protected void onScreenCreate() {

        super.onScreenCreate();

        // - input direction button
        this.setButtonSpritesAndOverlayForState(this._btnInputDirection, ButtonState.Default, CommonIcons.ButtonInputDirection);
        this.setButtonSpritesAndOverlayForState(this._btnInputDirection, ButtonState.Active, CommonIcons.ButtonInputDirectionActive);
        this._btnInputDirection.setLayoutEngineHint(FixedLayoutEngine.hint(83, 26, 18, 18));
        this._btnInputDirection.setBackground(CommonIcons.ImageButtonBackground.get());
        this._btnInputDirection.setPadding(1);

        this._btnInputDirection.Activated.subscribe(this::onInputActivated);
        this._btnInputDirection.setTooltips(
                new TranslatableComponent("gui.bigreactors.generator.fluidport.directioninput.line1").setStyle(CommonConstants.STYLE_TOOLTIP_TITLE),
                TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.bigreactors.generator.fluidport.directioninput.line2")
        );

        // - output direction button
        this.setButtonSpritesAndOverlayForState(this._btnOutputDirection, ButtonState.Default, CommonIcons.ButtonOutputDirection);
        this.setButtonSpritesAndOverlayForState(this._btnOutputDirection, ButtonState.Active, CommonIcons.ButtonOutputDirectionActive);
        this._btnOutputDirection.setLayoutEngineHint(FixedLayoutEngine.hint(123, 26, 18, 18));
        this._btnOutputDirection.setBackground(CommonIcons.ImageButtonBackground.get());
        this._btnOutputDirection.setPadding(1);
        this._btnOutputDirection.Activated.subscribe(this::onOutputActivated);
        this._btnOutputDirection.setTooltips(
                new TranslatableComponent("gui.bigreactors.generator.fluidport.directionoutput.line1").setStyle(CommonConstants.STYLE_TOOLTIP_TITLE),
                TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.bigreactors.generator.fluidport.directionoutput.line2")
        );

        //noinspection Convert2MethodRef
        this.addBinding(t -> t.getIoDirection(), value -> {

            this._btnInputDirection.setActive(value.isInput());
            this._btnOutputDirection.setActive(value.isOutput());
        });

        this.addControl(this._btnInputDirection);
        this.addControl(this._btnOutputDirection);
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

    private <Value> void addBinding(final Function<T, Value> supplier, final Consumer<Value> consumer) {
        this._bindings.addBinding(new MonoConsumerBinding<>(this.getTileEntity(), supplier, consumer));
    }

    private final BindingGroup _bindings;

    private final SwitchPictureButton _btnInputDirection;
    private final SwitchPictureButton _btnOutputDirection;

    //endregion
}
