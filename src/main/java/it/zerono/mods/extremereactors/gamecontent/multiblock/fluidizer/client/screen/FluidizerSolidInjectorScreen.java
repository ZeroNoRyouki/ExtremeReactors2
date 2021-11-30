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

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.MultiblockFluidizer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.container.FluidizerSolidInjectorContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part.FluidizerSolidInjectorEntity;
import it.zerono.mods.zerocore.base.multiblock.client.screen.AbstractMultiblockScreen;
import it.zerono.mods.zerocore.lib.client.gui.control.Panel;
import it.zerono.mods.zerocore.lib.client.gui.control.SlotsGroup;
import it.zerono.mods.zerocore.lib.client.gui.layout.FixedLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.layout.HorizontalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.SpriteTextureMap;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.util.NonNullSupplier;

public class FluidizerSolidInjectorScreen
        extends AbstractMultiblockScreen<MultiblockFluidizer, FluidizerSolidInjectorEntity, FluidizerSolidInjectorContainer> {

    public FluidizerSolidInjectorScreen(final FluidizerSolidInjectorContainer container,
                                        final Inventory inventory, final Component title) {
        super(container, inventory, PlayerInventoryUsage.Both, title,
                () -> new SpriteTextureMap(ExtremeReactors.newID("textures/gui/multiblock/basic_background.png"), 256, 256));
    }

    //region AbstractMultiblockScreen

    /**
     * Called when this screen is being created for the first time.
     * Override to handle this event
     */
    @Override
    protected void onScreenCreate() {

        //TODO add Reprocessor to Patchouli book
//        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, ExtremeReactors.newID("reactor/part-solidaccessport"), 1);

        super.onScreenCreate();

        final Panel panel = new Panel(this, "injector");
        SlotsGroup slotsGroup;

        panel.setLayoutEngineHint(FixedLayoutEngine.hint(0, 13));
        panel.setDesiredDimension(this.getGuiWidth(), 38);
        panel.setLayoutEngine(new VerticalLayoutEngine()
                .setHorizontalAlignment(HorizontalAlignment.Center)
                .setZeroMargins());
        panel.addControl(this.slotPanel("slot", "inv", 0, 0, CommonIcons.PortInputSlot));
        this.addControl(panel);

        // - player main inventory slots
        slotsGroup = this.createPlayerInventorySlotsGroupControl();
        slotsGroup.setLayoutEngineHint(FixedLayoutEngine.hint(31, 63));
        this.addControl(slotsGroup);

        // - player hotbar slots
        slotsGroup = this.createPlayerHotBarSlotsGroupControl();
        slotsGroup.setLayoutEngineHint(FixedLayoutEngine.hint(31, 121));
        this.addControl(slotsGroup);
    }

    //endregion
    //region  internals

    private Panel slotPanel(final String groupName, final String invName, final int x, final int y,
                            final NonNullSupplier<ISprite> slotBackground) {

        final SlotsGroup sg = this.createSingleSlotGroupControl(groupName, invName);
        final Panel p = new Panel(this);

        sg.setLayoutEngineHint(FixedLayoutEngine.hint(10, 10, 18, 18));

        p.setBackground(slotBackground.get());
        p.setDesiredDimension(38, 38);
        p.setLayoutEngine(new FixedLayoutEngine().setZeroMargins());
        p.addControl(sg);

        return p;
    }

    //endregion
}
