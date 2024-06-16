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

import it.zerono.mods.extremereactors.CommonLocations;
import it.zerono.mods.extremereactors.gamecontent.compat.jei.IExtremeReactorsJeiService;
import it.zerono.mods.zerocore.base.client.screen.ClientBaseHelper;
import it.zerono.mods.zerocore.base.client.screen.control.MachineStatusIndicator;
import it.zerono.mods.zerocore.base.multiblock.client.screen.AbstractMultiblockScreen;
import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.control.Panel;
import it.zerono.mods.zerocore.lib.client.gui.control.SlotsGroup;
import it.zerono.mods.zerocore.lib.client.gui.layout.FixedLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.layout.HorizontalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalAlignment;
import it.zerono.mods.zerocore.lib.client.gui.layout.VerticalLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.SpriteTextureMap;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.BooleanData;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockMachine;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.variant.IMultiblockVariant;
import it.zerono.mods.zerocore.lib.text.TextHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.common.util.NonNullSupplier;
import org.jetbrains.annotations.Nullable;

public abstract class CommonMultiblockScreen<Controller extends AbstractCuboidMultiblockController<Controller> & IMultiblockMachine,
            T extends AbstractMultiblockEntity<Controller> & MenuProvider,
            C extends ModTileContainer<T>>
        extends AbstractMultiblockScreen<Controller, T, C> {

    public static final MutableComponent EMPTY_VALUE = TextHelper.translatable("gui.zerocore.base.generic.empty");

    protected CommonMultiblockScreen(C container, Inventory inventory, PlayerInventoryUsage inventoryUsage,
                                     Component title, NonNullSupplier<SpriteTextureMap> mainTextureSupplier) {

        super(container, inventory, inventoryUsage, title, mainTextureSupplier);
        this._indicator = createStatusIndicator(container);
        this.initialize();
    }

    protected CommonMultiblockScreen(C container, Inventory inventory, PlayerInventoryUsage inventoryUsage,
                                     Component title, int guiWidth, int guiHeight,
                                     NonNullSupplier<SpriteTextureMap> mainTextureSupplier) {

        super(container, inventory, inventoryUsage, title, guiWidth, guiHeight, mainTextureSupplier);
        this._indicator = createStatusIndicator(container);
        this.initialize();
    }

    protected CommonMultiblockScreen(C container, Inventory inventory, PlayerInventoryUsage inventoryUsage,
                                     Component title, int guiWidth, int guiHeight, SpriteTextureMap mainTexture) {

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

    protected MachineStatusIndicator createEnergizerStatusIndicator(BooleanData bindableStatus) {
        return this.createDefaultStatusIndicator(bindableStatus, "gui.bigreactors.energizer.active",
                "gui.bigreactors.energizer.inactive");
    }

    protected void displayFluidizerRecipes() {
        IExtremeReactorsJeiService.SERVICE.get().displayFluidizerRecipes();
    }

    protected void displayReprocessorRecipes() {
        IExtremeReactorsJeiService.SERVICE.get().displayReprocessorRecipes();
    }

    protected void displayReactorRecipes() {
        IExtremeReactorsJeiService.SERVICE.get().displayReactorRecipes();
    }

    protected static NonNullSupplier<SpriteTextureMap> mainTextureFromVariant(IMultiblockVariant variant) {
        return () -> new SpriteTextureMap(CommonLocations.TEXTURES_GUI_MULTIBLOCK
                .buildWithSuffix(variant.getName() + "_background.png"), 256, 256);
    }

    protected static NonNullSupplier<SpriteTextureMap> halfTextureFromVariant(IMultiblockVariant variant) {
        return () -> new SpriteTextureMap(CommonLocations.TEXTURES_GUI_MULTIBLOCK
                .buildWithSuffix(variant.getName() + "_background_half.png"), 256, 98);
    }

    protected Panel createSingleIoSlotPanel(String groupName, String invName, int x, int y,
                                            NonNullSupplier<ISprite> slotBackground) {

        final SlotsGroup sg = this.createSingleSlotGroupControl(groupName, invName);

        sg.setLayoutEngineHint(FixedLayoutEngine.hint(10, 10, ClientBaseHelper.SQUARE_BUTTON_DIMENSION,
                ClientBaseHelper.SQUARE_BUTTON_DIMENSION));

        final Panel background = new Panel(this, "background");

        background.setBackground(slotBackground.get());
        background.setDesiredDimension(ClientBaseHelper.SQUARE_BUTTON_DIMENSION + 20, ClientBaseHelper.SQUARE_BUTTON_DIMENSION + 20);
        background.setLayoutEngine(new FixedLayoutEngine().setZeroMargins());
        background.addControl(sg);

        final Panel outer = new Panel(this, "ioSlot");

        outer.setDesiredDimension(ClientBaseHelper.INVENTORY_SLOTS_ROW_WIDTH, ClientBaseHelper.SQUARE_BUTTON_DIMENSION + 30);
        outer.setLayoutEngine(new VerticalLayoutEngine()
                .setZeroMargins()
                .setHorizontalAlignment(HorizontalAlignment.Center)
                .setVerticalAlignment(VerticalAlignment.Center));
        outer.addControl(background);

        return outer;
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
