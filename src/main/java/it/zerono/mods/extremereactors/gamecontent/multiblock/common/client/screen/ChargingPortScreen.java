/*
 *
 * ChargingPortScreen.java
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
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.container.ChargingPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.AbstractMultiblockEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.powertap.chargingport.AbstractChargingPortHandler;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.powertap.chargingport.IChargingPort;
import it.zerono.mods.zerocore.base.CommonConstants;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockVariantProvider;
import it.zerono.mods.zerocore.lib.client.gui.ButtonState;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.control.Button;
import it.zerono.mods.zerocore.lib.client.gui.control.Panel;
import it.zerono.mods.zerocore.lib.client.gui.control.Picture;
import it.zerono.mods.zerocore.lib.client.gui.control.SlotsGroup;
import it.zerono.mods.zerocore.lib.client.gui.layout.FixedLayoutEngine;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockMachine;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.variant.IMultiblockVariant;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;

import static it.zerono.mods.zerocore.lib.CodeHelper.TEXT_EMPTY_LINE;

public class ChargingPortScreen<Controller extends AbstractCuboidMultiblockController<Controller> & IMultiblockMachine,
                                V extends IMultiblockVariant,
                                T extends AbstractMultiblockEntity<Controller> & IChargingPort & IMultiblockVariantProvider<V> & MenuProvider>
        extends AbstractMultiblockScreen<Controller, T, ChargingPortContainer<T>> {

    public ChargingPortScreen(final ChargingPortContainer<T> container, final Inventory inventory,
                              final Component title, final ResourceLocation bookEntryId) {
        super(container, inventory, PlayerInventoryUsage.Both, title,
                mainTextureFromVariant(container.getTileEntity().getMultiblockVariant().orElseThrow(IllegalStateException::new)));

        this._btnEject = new Button(this, "eject", "");
        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, bookEntryId, 1);
    }

    //region AbstractMultiblockScreen

    /**
     * Called when this screen is being created for the first time.
     * Override to handle this event
     */
    @Override
    protected void onScreenCreate() {

        super.onScreenCreate();

        final Panel panel = new Panel(this);
        SlotsGroup slotsGroup;

        panel.setLayoutEngineHint(FixedLayoutEngine.hint(31, 0, 162, 64));
        this.addControl(panel);

        // - input slot

        Picture p = new Picture(this, "animation", CachedSprites.GUI_CHARGINGPORT_SLOT.get());

        p.setLayoutEngineHint(FixedLayoutEngine.hint(49, 0, 64, 64));
        panel.addControl(p);

        panel.addControl(this.slot("input", IoDirection.Input, 72, 20));

        // - output slot
        panel.addControl(this.slot("output", IoDirection.Output, 144, 39));

        // - player main inventory slots
        slotsGroup = this.createPlayerInventorySlotsGroupControl();
        slotsGroup.setLayoutEngineHint(FixedLayoutEngine.hint(31, 63));
        this.addControl(slotsGroup);

        // - player hotbar slots
        slotsGroup = this.createPlayerHotBarSlotsGroupControl();
        slotsGroup.setLayoutEngineHint(FixedLayoutEngine.hint(31, 121));
        this.addControl(slotsGroup);

        // - manual eject button

        this._btnEject.setPadding(1);
        this._btnEject.setLayoutEngineHint(FixedLayoutEngine.hint(144, 2, 18, 18));
        this._btnEject.setIconForState(CommonIcons.ButtonManualEject.get(), ButtonState.Default);
        this._btnEject.setIconForState(CommonIcons.ButtonManualEjectActive.get(), ButtonState.Active, ButtonState.ActiveHighlighted, ButtonState.DefaultHighlighted);
        this._btnEject.Clicked.subscribe(this::onManualEject);
        this._btnEject.setTooltips(
                new TranslatableComponent("gui.bigreactors.generator.chargingport.dumpfuel.line1").setStyle(CommonConstants.STYLE_TOOLTIP_TITLE),
                TEXT_EMPTY_LINE,
                new TranslatableComponent("gui.bigreactors.generator.chargingport.dumpfuel.line2"),
                new TranslatableComponent("gui.bigreactors.generator.chargingport.dumpfuel.line3"),
                new TranslatableComponent("gui.bigreactors.generator.chargingport.dumpfuel.line4")
        );

        panel.addControl(this._btnEject);
    }

    //endregion
    //region  internals

    private IControl slot(final String groupName, final IoDirection direction, final int x, final int y) {

        final SlotsGroup sg = this.createSingleSlotGroupControl(groupName, direction.name());

        sg.setLayoutEngineHint(FixedLayoutEngine.hint(x, y, 18, 18));
        return sg;
    }

    private void onManualEject(Button button, Integer mouseButton) {
        this.sendCommandToServer(AbstractChargingPortHandler.TILE_COMMAND_EJECT);
    }

    private final Button _btnEject;

    //endregion
}
