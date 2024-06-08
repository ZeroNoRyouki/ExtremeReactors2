/*
 *
 * AbstractChargingPortScreen.java
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

import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.AbstractGeneratorMultiblockController;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.container.ChargingPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockGeneratorVariant;
import it.zerono.mods.zerocore.base.CommonConstants;
import it.zerono.mods.zerocore.base.client.screen.BaseScreenToolTipsBuilder;
import it.zerono.mods.zerocore.base.client.screen.ClientBaseHelper;
import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity;
import it.zerono.mods.zerocore.base.multiblock.part.io.power.charging.IChargingPort;
import it.zerono.mods.zerocore.lib.IActivableMachine;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockVariantProvider;
import it.zerono.mods.zerocore.lib.client.gui.ButtonState;
import it.zerono.mods.zerocore.lib.client.gui.DesiredDimension;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.control.Button;
import it.zerono.mods.zerocore.lib.client.gui.control.Panel;
import it.zerono.mods.zerocore.lib.client.gui.control.Picture;
import it.zerono.mods.zerocore.lib.client.gui.layout.FixedLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalLayoutEngine;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockMachine;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;

public abstract class AbstractChargingPortScreen<Controller extends AbstractGeneratorMultiblockController<Controller, V> & IMultiblockMachine & IActivableMachine,
            V extends IMultiblockGeneratorVariant,
            T extends AbstractMultiblockEntity<Controller> & IChargingPort & IMultiblockVariantProvider<V> & MenuProvider>
        extends CommonMultiblockScreen<Controller, T, ChargingPortContainer<Controller, V, T>> {

    protected AbstractChargingPortScreen(final ChargingPortContainer<Controller, V, T> container, final Inventory inventory,
                                         final Component title, final ResourceLocation bookEntryId) {

        super(container, inventory, PlayerInventoryUsage.Both, title,
                mainTextureFromVariant(container.getTileEntity().getMultiblockVariant().orElseThrow(IllegalStateException::new)));

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, bookEntryId, 1);

        this._btnEject = new Button(this, "eject", "");
        this._btnEject.setPadding(1);
        this._btnEject.setLayoutEngineHint(FixedLayoutEngine.hint(144, 2, 18, 18));
        this._btnEject.setIconForState(CommonIcons.ButtonManualEject.get(), ButtonState.Default);
        this._btnEject.setIconForState(CommonIcons.ButtonManualEjectActive.get(), ButtonState.Active, ButtonState.ActiveHighlighted, ButtonState.DefaultHighlighted);
        this._btnEject.Clicked.subscribe(this::onManualEject);
        this._btnEject.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.generator.chargingport.eject.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.generator.chargingport.eject.tooltip.body")
        );

        this._playerInventoryGroup = this.createPlayerInventorySlotsGroupControl();
        this._playerHotBarGroup = this.createPlayerHotBarSlotsGroupControl();
        this._inputGroup = this.createSingleSlotGroupControl("input", IoDirection.Input.name());
        this._outputGroup = this.createSingleSlotGroupControl("output", IoDirection.Output.name());
    }

    //region AbstractMultiblockScreen

    /**
     * Called when this screen is being created for the first time.
     * Override to handle this event
     */
    @Override
    protected void onScreenCreate() {

        super.onScreenCreate();

        this.setContentLayoutEngine(new VerticalLayoutEngine()
                .setZeroMargins()
                .setControlsSpacing(4));

        final Panel panel = new Panel(this);

        panel.setDesiredDimension(DesiredDimension.Width, ClientBaseHelper.INVENTORY_SLOTS_ROW_WIDTH);
        this.addControl(panel);

        // I/O slots & commands

        Picture p = new Picture(this, "animation", CachedSprites.GUI_CHARGINGPORT_SLOT.get());

        p.setLayoutEngineHint(FixedLayoutEngine.hint(49, 0, 64, 64));
        panel.addControl(p);

        this._inputGroup.setLayoutEngineHint(FixedLayoutEngine.hint(72, 20, 18, 18));
        panel.addControl(this._inputGroup);

        this._outputGroup.setLayoutEngineHint(FixedLayoutEngine.hint(144, 39, 18, 18));
        panel.addControl(this._outputGroup);

        this._btnEject.setLayoutEngineHint(FixedLayoutEngine.hint(144, 2, 18, 18));
        panel.addControl(this._btnEject);

        // player inventory

        this.addControl(this._playerInventoryGroup);
        this.addControl(this._playerHotBarGroup);
    }

    //endregion
    //region  internals

    private void onManualEject(Button button, Integer mouseButton) {
        this.sendCommandToServer(CommonConstants.COMMAND_EJECT);
    }

    private final Button _btnEject;
    private final IControl _playerInventoryGroup;
    private final IControl _playerHotBarGroup;
    private final IControl _inputGroup;
    private final IControl _outputGroup;

    //endregion
}
