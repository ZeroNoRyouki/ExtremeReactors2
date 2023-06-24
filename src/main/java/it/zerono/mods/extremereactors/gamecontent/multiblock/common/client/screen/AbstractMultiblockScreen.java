/*
 *
 * AbstractMultiblockScreen.java
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

import com.google.common.collect.ImmutableList;
import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.AbstractReactorEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.part.AbstractReprocessorEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.AbstractTurbineEntity;
import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity;
import it.zerono.mods.zerocore.lib.IActivableMachine;
import it.zerono.mods.zerocore.lib.client.gui.*;
import it.zerono.mods.zerocore.lib.client.gui.control.*;
import it.zerono.mods.zerocore.lib.client.gui.layout.FixedLayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.layout.ILayoutEngine;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISpriteTextureMap;
import it.zerono.mods.zerocore.lib.client.gui.sprite.Sprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.SpriteTextureMap;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockMachine;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;
import it.zerono.mods.zerocore.lib.multiblock.variant.IMultiblockVariant;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.NonNullSupplier;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractMultiblockScreen<Controller extends AbstractCuboidMultiblockController<Controller> & IMultiblockMachine,
                                                T extends AbstractMultiblockEntity<Controller> & MenuProvider,
                                                C extends ModTileContainer<T>>
        extends ModTileContainerScreen<T, C> {

    protected AbstractMultiblockScreen(final C container, final Inventory inventory,
                                       final PlayerInventoryUsage inventoryUsage, final Component title) {
        this(container, inventory, inventoryUsage, title, DEFAULT_GUI_WIDTH, DEFAULT_GUI_HEIGHT);
    }

    protected AbstractMultiblockScreen(final C container, final Inventory inventory,
                                       final PlayerInventoryUsage inventoryUsage, final Component title,
                                       final NonNullSupplier<SpriteTextureMap> mainTextureSupplier) {
        this(container, inventory, inventoryUsage, title, DEFAULT_GUI_WIDTH, DEFAULT_GUI_HEIGHT, mainTextureSupplier.get());
    }

    protected AbstractMultiblockScreen(final C container, final Inventory inventory,
                                       final PlayerInventoryUsage inventoryUsage, final Component title,
                                       final int guiWidth, final int guiHeight,
                                       final NonNullSupplier<SpriteTextureMap> mainTextureSupplier) {
        this(container, inventory, inventoryUsage, title, guiWidth, guiHeight, mainTextureSupplier.get());
    }

    protected static NonNullSupplier<SpriteTextureMap> mainTextureFromVariant(final IMultiblockVariant variant) {
        return () -> new SpriteTextureMap(ExtremeReactors.newID("textures/gui/multiblock/" + variant.getName() + "_background.png"), 256, 256);
    }

    protected static NonNullSupplier<SpriteTextureMap> halfTextureFromVariant(final IMultiblockVariant variant) {
        return () -> new SpriteTextureMap(ExtremeReactors.newID("textures/gui/multiblock/" + variant.getName() + "_background_half.png"), 256, 98);
    }

    protected AbstractMultiblockScreen(final C container, final Inventory inventory,
                                       final PlayerInventoryUsage inventoryUsage, final Component title,
                                       final int guiWidth, final int guiHeight) {
        this(container, inventory, inventoryUsage, title, guiWidth, guiHeight,
                new SpriteTextureMap(ExtremeReactors.newID("textures/gui/multiblock/generic_background.png"), 256, 256));
    }

    protected AbstractMultiblockScreen(final C container, final Inventory inventory,
                                       final PlayerInventoryUsage inventoryUsage, final Component title,
                                       final int guiWidth, final int guiHeight, final SpriteTextureMap mainTexture) {

        super(container, inventory, title, guiWidth, guiHeight);
        this._mainTextMap = mainTexture;
        this.setTheme(GuiTheme.ER.get());

        switch (inventoryUsage) {

            default:
            case None:
                this._invMainSprite = this._invHotBarSprite = this._invSingleSprite = Sprite.EMPTY;
                break;

            case HotBar:
                this._invHotBarSprite = this.createInventoryHotBarSprite();
                this._invMainSprite = Sprite.EMPTY;
                this._invSingleSprite = this.createInventorySingleSprite();
                break;

            case MainInventory:
                this._invHotBarSprite = Sprite.EMPTY;
                this._invMainSprite = this.createInventoryMainSprite();
                this._invSingleSprite = this.createInventorySingleSprite();
                break;

            case Both:
                this._invHotBarSprite = this.createInventoryHotBarSprite();
                this._invMainSprite = this.createInventoryMainSprite();
                this._invSingleSprite = this.createInventorySingleSprite();
                break;
        }

        this._contentPanel = new Panel(this, "content");
        this._helpButton = null;

        // indicators

        this._indicatorOn = new Picture(this, "on", CommonIcons.MachineStatusOn);
        this._indicatorOff = new Picture(this, "off", CommonIcons.MachineStatusOff);

        final T tile = container.getTileEntity();

        if (tile instanceof AbstractReactorEntity) {

            this.setIndicatorToolTip(true, INDICATOR_ACTIVE_REACTOR);
            this.setIndicatorToolTip(false, INDICATOR_INACTIVE_REACTOR);

        } else if (tile instanceof AbstractTurbineEntity) {

            this.setIndicatorToolTip(true, INDICATOR_ACTIVE_TURBINE);
            this.setIndicatorToolTip(false, INDICATOR_INACTIVE_TURBINE);

        } else if (tile instanceof AbstractReprocessorEntity) {

            this.setIndicatorToolTip(true, INDICATOR_ACTIVE_REPROCESSOR);
            this.setIndicatorToolTip(false, INDICATOR_INACTIVE_REPROCESSOR);
        }
    }

    protected void addControl(final IControl control) {
        this._contentPanel.addControl(control);
    }

    protected void setContentPanelBackground(final ISprite sprite) {
        this._contentPanel.setBackground(sprite);
    }

    protected void setContentPanelBackground(final Colour colour) {
        this._contentPanel.setBackground(colour);
    }

    protected void setContentLayoutEngine(final ILayoutEngine engine) {
        this._contentPanel.setLayoutEngine(engine);
    }

    protected void setIndicatorToolTip(final boolean active, final Component... lines) {
        this.setIndicatorToolTip(active, ImmutableList.copyOf(lines), Collections.emptyList());
    }

    protected void setIndicatorToolTip(final boolean active, final List<Component> lines, final List<Object> objects) {

        if (active) {
            this._indicatorOn.setTooltips(lines, objects);
        } else {
            this._indicatorOff.setTooltips(lines, objects);
        }
    }

    protected void addPatchouliHelpButton(final ResourceLocation bookId, final ResourceLocation entryId, final int pageNum) {
        this._helpButton = this.createPatchouliHelpButton(bookId, entryId, pageNum);
    }

    protected void setButtonSpritesAndOverlayForState(final AbstractButtonControl button,
                                                      final ButtonState standardState,
                                                      final NonNullSupplier<ISprite> standardSprite) {
        this.setButtonSpritesAndOverlayForState(button, standardState,standardSprite.get());
    }

    protected void setButtonSpritesAndOverlayForState(final AbstractButtonControl button,
                                                      final ButtonState standardState,
                                                      final ISprite standardSprite) {

        button.setIconForState(standardSprite, standardState);

        ISprite withOverlay;

        withOverlay = standardSprite.copyWith(CommonIcons.Button16x16HightlightOverlay.get());
        button.setIconForState(withOverlay, standardState.getHighlighted());

        withOverlay = standardSprite.copyWith(CommonIcons.Button16x16DisabledOverlay.get());
        button.setIconForState(withOverlay, standardState.getDisabled());
    }

    protected Optional<Controller> getMultiblockController() {
        return this.getMenu().getTileEntity().getMultiblockController();
    }

    protected boolean isMultiblockActive() {
        //noinspection Convert2MethodRef
        return this.getMultiblockController()
                .filter(controller -> controller instanceof IActivableMachine)
                .map(controller -> ((IActivableMachine)controller).isMachineActive())
                .orElse(false);
    }

    protected boolean isMultiblockAssembled() {
        return this.getMultiblockController()
                .map(IMultiblockController::isAssembled)
                .orElse(false);
    }

    //region slot groups

    protected SlotsGroup createSingleSlotGroupControl(final String controlName, final String inventorySlotsGroupName) {
        return this.createMonoSlotGroupControl(controlName, inventorySlotsGroupName, this._invSingleSprite, 1);
    }

    protected SlotsGroup createPlayerHotBarSlotsGroupControl() {
        return this.createPlayerHotBarSlotsGroupControl(this._invHotBarSprite, 1);
    }

    protected SlotsGroup createPlayerInventorySlotsGroupControl() {
        return this.createPlayerInventorySlotsGroupControl(this._invMainSprite, 1);
    }

    //endregion
    //region internals

    /**
     * Called when this screen need to be updated after the TileEntity data changed.
     * Override to handle this event
     */
    @Override
    protected void onDataUpdated() {

        super.onDataUpdated();

        if (!this.isMultiblockAssembled()) {
            this.close();
        }

        final boolean active = this.isMultiblockActive();

        this._indicatorOn.setVisible(active);
        this._indicatorOff.setVisible(!active);
    }

    /**
     * Called when this screen is being created for the first time.
     * Override to handle this event
     */
    @Override
    protected void onScreenCreate() {

        super.onScreenCreate();

        final int guiWidth = this.getGuiWidth();
        final int guiHeight = this.getGuiHeight();

        final Panel mainPanel = new Panel(this, "mainPanel");
        final Panel titlePanel = new Panel(this, "titlePanel");
        final int contentHeight = guiHeight - TITLE_PANEL_HEIGHT;

        // - main panel

        mainPanel.setDesiredDimension(DesiredDimension.Height, guiHeight);
        mainPanel.setDesiredDimension(DesiredDimension.Width, guiWidth);
        mainPanel.setLayoutEngineHint(FixedLayoutEngine.hint(0, 0, guiWidth, guiHeight));
        mainPanel.setBackground(this._mainTextMap.sprite().ofSize(guiWidth, guiHeight).build());
        mainPanel.setLayoutEngine(new FixedLayoutEngine().setZeroMargins());

        // - title panel

        titlePanel.setDesiredDimension(DesiredDimension.Height, TITLE_PANEL_HEIGHT);
        titlePanel.setDesiredDimension(DesiredDimension.Width, guiWidth);
        titlePanel.setLayoutEngineHint(FixedLayoutEngine.hint(0, 0, guiWidth, TITLE_PANEL_HEIGHT));
        titlePanel.setLayoutEngine(new FixedLayoutEngine().setZeroMargins());

        final Label title = new Label(this, "title", this.getTitle()./*getFormattedText*/getString());

        title.setPadding(2);
        title.setColor(this.getTheme().GUI_TITLE);
        title.setAutoSize(false);
        title.setLayoutEngineHint(FixedLayoutEngine.hint(17, 7, guiWidth - 47, 12));

        this._indicatorOn.setVisible(false);
        this._indicatorOn.setLayoutEngineHint(FixedLayoutEngine.hint(7, 7, 10, 10));

        this._indicatorOff.setVisible(false);
        this._indicatorOff.setLayoutEngineHint(FixedLayoutEngine.hint(7, 7, 10, 10));

        titlePanel.addControl(title, this._indicatorOn, this._indicatorOff);

        if (null != this._helpButton) {

            this._helpButton.setLayoutEngineHint(FixedLayoutEngine.hint(guiWidth - 18, 6, 12, 12));
            titlePanel.addControl(this._helpButton);
        }

        // - content panel

        // MC call the init() method (witch rise onScreenCreated()) also when the main windows is resized: clear
        // the controls in the content panel to avoid duplications
        this._contentPanel.removeControls();

        this._contentPanel.setLayoutEngine(new FixedLayoutEngine());
        this._contentPanel.setDesiredDimension(DesiredDimension.Height, contentHeight);
        this._contentPanel.setDesiredDimension(DesiredDimension.Width, guiWidth);
        this._contentPanel.setLayoutEngineHint(FixedLayoutEngine.hint(0, TITLE_PANEL_HEIGHT, guiWidth, contentHeight));

        // create main window

        mainPanel.addControl(titlePanel, this._contentPanel);
        this.createWindow(mainPanel, true);
    }

    private ISprite createInventoryHotBarSprite() {
        return this._mainTextMap.sprite().from(0, 202).ofSize(162, 18).build();
    }

    private ISprite createInventoryMainSprite() {
        return this._mainTextMap.sprite().from(0, 202).ofSize(162, 54).build();
    }

    private ISprite createInventorySingleSprite() {
        return this._mainTextMap.sprite().from(0, 202).ofSize(18, 18).build();
    }

    protected static final Component INDICATOR_ACTIVE_REACTOR = new TranslatableComponent("gui.bigreactors.reactor.active");
    protected static final Component INDICATOR_INACTIVE_REACTOR = new TranslatableComponent("gui.bigreactors.reactor.inactive");
    protected static final Component INDICATOR_ACTIVE_TURBINE = new TranslatableComponent("gui.bigreactors.turbine.active");
    protected static final Component INDICATOR_INACTIVE_TURBINE = new TranslatableComponent("gui.bigreactors.turbine.inactive");
    protected static final Component INDICATOR_ACTIVE_REPROCESSOR = new TranslatableComponent("gui.bigreactors.reprocessor.active");
    protected static final Component INDICATOR_INACTIVE_REPROCESSOR = new TranslatableComponent("gui.bigreactors.reprocessor.inactive");

    private static final int DEFAULT_GUI_WIDTH = 224;
    private static final int DEFAULT_GUI_HEIGHT = 166;
    private static final int TITLE_PANEL_HEIGHT = 21;

    private final ISpriteTextureMap _mainTextMap;
    private final ISprite _invMainSprite;
    private final ISprite _invHotBarSprite;
    private final ISprite _invSingleSprite;

    private final IControlContainer _contentPanel;
    private final Picture _indicatorOn;
    private final Picture _indicatorOff;
    private IControl _helpButton;

    //endregion
}
