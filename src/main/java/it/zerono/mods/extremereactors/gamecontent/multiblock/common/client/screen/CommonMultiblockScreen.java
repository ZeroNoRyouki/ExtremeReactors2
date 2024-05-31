/*
 *
 * CommonMultiblockScreen.java
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

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.zerocore.base.client.screen.control.MachineStatusIndicator;
import it.zerono.mods.zerocore.base.multiblock.client.screen.AbstractMultiblockScreen;
import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.sprite.SpriteTextureMap;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.BooleanData;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockMachine;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.variant.IMultiblockVariant;
import it.zerono.mods.zerocore.lib.text.TextHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nullable;

public abstract class CommonMultiblockScreen<Controller extends AbstractCuboidMultiblockController<Controller> & IMultiblockMachine,
        T extends AbstractMultiblockEntity<Controller> & INamedContainerProvider,
        C extends ModTileContainer<T>>
        extends AbstractMultiblockScreen<Controller, T, C> {

    public static final IFormattableTextComponent EMPTY_VALUE = TextHelper.translatable("gui.zerocore.base.generic.empty");

    protected CommonMultiblockScreen(C container, PlayerInventory inventory, PlayerInventoryUsage inventoryUsage,
                                     ITextComponent title, NonNullSupplier<SpriteTextureMap> mainTextureSupplier) {

        super(container, inventory, inventoryUsage, title, mainTextureSupplier);
        this._indicator = createStatusIndicator(container);
        this.initialize();
    }

    protected CommonMultiblockScreen(C container, PlayerInventory inventory, PlayerInventoryUsage inventoryUsage,
                                     ITextComponent title, int guiWidth, int guiHeight,
                                     NonNullSupplier<SpriteTextureMap> mainTextureSupplier) {

        super(container, inventory, inventoryUsage, title, guiWidth, guiHeight, mainTextureSupplier);
        this._indicator = createStatusIndicator(container);
        this.initialize();
    }

    protected CommonMultiblockScreen(C container, PlayerInventory inventory, PlayerInventoryUsage inventoryUsage,
                                     ITextComponent title, int guiWidth, int guiHeight, SpriteTextureMap mainTexture) {

        super(container, inventory, inventoryUsage, title, guiWidth, guiHeight, mainTexture);
        this._indicator = createStatusIndicator(container);
        this.initialize();
    }

    protected abstract MachineStatusIndicator createStatusIndicator(C container);

    protected MachineStatusIndicator createDefaultStatusIndicator(BooleanData bindableStatus, String activeTooltipKey,
                                                                  String inactiveTooltipKey) {

        final MachineStatusIndicator indicator = new MachineStatusIndicator(this, "indicator", bindableStatus);

        indicator.setTooltips(true, activeTooltipKey);
        indicator.setTooltips(false, inactiveTooltipKey);
        return indicator;
    }

    protected MachineStatusIndicator createReactorStatusIndicator(BooleanData bindableStatus) {
        return this.createDefaultStatusIndicator(bindableStatus, "gui.bigreactors.reactor.active",
                "gui.bigreactors.reactor.inactive");
    }

    protected MachineStatusIndicator createTurbineStatusIndicator(BooleanData bindableStatus) {
        return this.createDefaultStatusIndicator(bindableStatus, "gui.bigreactors.turbine.active",
                "gui.bigreactors.turbine.inactive");
    }

    protected MachineStatusIndicator createReprocessorStatusIndicator(BooleanData bindableStatus) {
        return this.createDefaultStatusIndicator(bindableStatus, "gui.bigreactors.reprocessor.active",
                "gui.bigreactors.reprocessor.inactive");
    }

    protected MachineStatusIndicator createFluidizerStatusIndicator(BooleanData bindableStatus) {
        return this.createDefaultStatusIndicator(bindableStatus, "gui.bigreactors.fluidizer.active",
                "gui.bigreactors.fluidizer.inactive");
    }

    protected static NonNullSupplier<SpriteTextureMap> mainTextureFromVariant(IMultiblockVariant variant) {
        return () -> new SpriteTextureMap(ExtremeReactors.newID("textures/gui/multiblock/" + variant.getName() + "_background.png"), 256, 256);
    }

    protected static NonNullSupplier<SpriteTextureMap> halfTextureFromVariant(IMultiblockVariant variant) {
        return () -> new SpriteTextureMap(ExtremeReactors.newID("textures/gui/multiblock/" + variant.getName() + "_background_half.png"), 256, 98);
    }

    //region AbstractMultiblockScreen

    @Nullable
    @Override
    protected IControl getTitleBarWidget() {
        return this._indicator;
    }

    @Override
    public int getTooltipsPopupMaxWidth() {
        return parseTooltipsPopupMaxWidthFromLang("gui.bigreactors.tooltips.popup_max_width", 220);
    }

    //endregion
    //region internals

    private void initialize() {

        this.setContentBounds(10, 0, this.getGuiWidth(), this.getGuiHeight() - 6 /* stop at the last "bolt" on the GUI background */);
        this.setTheme(GuiTheme.ER.get());
    }

    private final MachineStatusIndicator _indicator;

    //endregion
}
