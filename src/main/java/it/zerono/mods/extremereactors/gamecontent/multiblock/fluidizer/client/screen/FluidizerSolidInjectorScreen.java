/*
 *
 * FluidizerSolidInjectorScreen.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.client.screen;

import it.zerono.mods.extremereactors.CommonLocations;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonMultiblockScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.GuiTheme;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.MultiblockFluidizer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.container.FluidizerSolidInjectorContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part.FluidizerSolidInjectorEntity;
import it.zerono.mods.zerocore.base.client.screen.control.MachineStatusIndicator;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.sprite.SpriteTextureMap;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class FluidizerSolidInjectorScreen
        extends CommonMultiblockScreen<MultiblockFluidizer, FluidizerSolidInjectorEntity, FluidizerSolidInjectorContainer> {

    public FluidizerSolidInjectorScreen(final FluidizerSolidInjectorContainer container,
                                        final Inventory inventory, final Component title) {

        super(container, inventory, PlayerInventoryUsage.Both, title,
                () -> new SpriteTextureMap(CommonLocations.TEXTURES_GUI_MULTIBLOCK
                        .buildWithSuffix("basic_background.png"), 256, 256));

        this.setTheme(GuiTheme.ER.get());

        this._slotGroup = this.createSingleIoSlotPanel("slot", "inv", 0, 0, CommonIcons.PortInputSlot);
        this._playerInventoryGroup = this.createPlayerInventorySlotsGroupControl();
        this._playerHotBarGroup = this.createPlayerHotBarSlotsGroupControl();
    }

    //region AbstractMultiblockScreen

    @Override
    protected MachineStatusIndicator createStatusIndicator(FluidizerSolidInjectorContainer container) {
        return this.createFluidizerStatusIndicator(container.ACTIVE);
    }

    /**
     * Called when this screen is being created for the first time.
     * Override to handle this event
     */
    @Override
    protected void onScreenCreate() {

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, CommonLocations.FLUIDIZER.buildWithSuffix("part-solidinjector"), 1);

        super.onScreenCreate();

        this.setContentLayoutEngine(new VerticalLayoutEngine()
                .setZeroMargins()
                .setVerticalAlignment(VerticalAlignment.Bottom)
                .setControlsSpacing(4));

        this.addControl(this._slotGroup);
        this.addControl(this._playerInventoryGroup);
        this.addControl(this._playerHotBarGroup);
    }

    //endregion
    //region  internals

    private final IControl _slotGroup;
    private final IControl _playerInventoryGroup;
    private final IControl _playerHotBarGroup;

    //endregion
}
