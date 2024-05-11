/*
 *
 * ReactorFluidAccessPortScreen.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.screen;

import com.google.common.collect.ImmutableList;
import it.zerono.mods.extremereactors.CommonLocations;
import it.zerono.mods.extremereactors.api.reactor.ReactantType;
import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.AbstractMultiblockScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorFluidAccessPortEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
import it.zerono.mods.zerocore.lib.client.gui.ButtonState;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.control.*;
import it.zerono.mods.zerocore.lib.client.gui.databind.BindingGroup;
import it.zerono.mods.zerocore.lib.client.gui.databind.MonoConsumerBinding;
import it.zerono.mods.zerocore.lib.client.gui.databind.MultiConsumerBinding;
import it.zerono.mods.zerocore.lib.client.gui.layout.*;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.Sprite;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.client.text.BindableTextComponent;
import it.zerono.mods.zerocore.lib.data.geometry.Point;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.fluid.FluidHelper;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.common.util.NonNullSupplier;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.Consumer;
import java.util.function.Function;

import static it.zerono.mods.zerocore.lib.CodeHelper.TEXT_EMPTY_LINE;

public class ReactorFluidAccessPortScreen
        extends AbstractMultiblockScreen<MultiblockReactor, ReactorFluidAccessPortEntity, ModTileContainer<ReactorFluidAccessPortEntity>> {

    public ReactorFluidAccessPortScreen(final ModTileContainer<ReactorFluidAccessPortEntity> container,
                                        final Inventory inventory, final Component title) {

        super(container, inventory, PlayerInventoryUsage.None, title,
                mainTextureFromVariant(container.getTileEntity().getMultiblockVariant().orElse(ReactorVariant.Basic)));
        this._bindings = new BindingGroup();

        this._btnInputDirection = new SwitchPictureButton(this, "directionInput", false, "direction");
        this._btnOutputDirection = new SwitchPictureButton(this, "directionOutput", false, "direction");
        this._btnDumpFuel = new Button(this, "dumpFuel", "");
        this._btnDumpWaste = new Button(this, "dumpWaste", "");

        this._fuelTank = this.liquidBar("fuelTank", ReactorFluidAccessPortEntity.TANK_CAPACITY);
        this._wasteTank = this.liquidBar("wasteTank", ReactorFluidAccessPortEntity.TANK_CAPACITY);
    }

    //region AbstractMultiblockScreen

    /**
     * Called when this screen is being created for the first time.
     * Override to handle this event
     */
    @Override
    protected void onScreenCreate() {

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, CommonLocations.REACTOR.buildWithSuffix("part-fluidaccessport"), 1);

        super.onScreenCreate();

        final Panel panel = new Panel(this, "fluidaccessport");
        Panel p;

        panel.setLayoutEngineHint(FixedLayoutEngine.hint(0, 0));
        panel.setDesiredDimension(this.getGuiWidth(), this.getGuiHeight() - 21);
        panel.setLayoutEngine(new AnchoredLayoutEngine()
                .setHorizontalMargin(36)
                .setVerticalMargin(13));

        this.addControl(panel);

        // - buttons

        // -- input direction button

        this.setButtonSpritesAndOverlayForState(this._btnInputDirection, ButtonState.Default, CommonIcons.ButtonInputDirection);
        this.setButtonSpritesAndOverlayForState(this._btnInputDirection, ButtonState.Active, CommonIcons.ButtonInputDirectionActive);
        this._btnInputDirection.Activated.subscribe(this::onInputActivated);
        this._btnInputDirection.setTooltips(
                Component.translatable("gui.bigreactors.reactor.fluidaccessport.directioninput.line1").setStyle(CommonConstants.STYLE_TOOLTIP_TITLE),
                TEXT_EMPTY_LINE,
                Component.translatable("gui.bigreactors.reactor.fluidaccessport.directioninput.line2")
        );

        // -- output direction button

        this.setButtonSpritesAndOverlayForState(this._btnOutputDirection, ButtonState.Default, CommonIcons.ButtonOutputDirection);
        this.setButtonSpritesAndOverlayForState(this._btnOutputDirection, ButtonState.Active, CommonIcons.ButtonOutputDirectionActive);
        this._btnOutputDirection.Activated.subscribe(this::onOutputActivated);
        this._btnOutputDirection.setTooltips(
                Component.translatable("gui.bigreactors.reactor.fluidaccessport.directionoutput.line1").setStyle(CommonConstants.STYLE_TOOLTIP_TITLE),
                TEXT_EMPTY_LINE,
                Component.translatable("gui.bigreactors.reactor.fluidaccessport.directionoutput.line2")
        );

        this.addBinding(c -> c.getTileEntity().getIoDirection(), value -> {

            this._btnInputDirection.setActive(value.isInput());
            this._btnOutputDirection.setActive(value.isOutput());
        });

        // -- dump fuel command button

        this._btnDumpFuel.setPadding(0);
        this._btnDumpFuel.setIconForState(CommonIcons.ButtonDumpFuel.get(), ButtonState.Default);
        this._btnDumpFuel.setIconForState(CommonIcons.ButtonDumpFuelActive.get(), ButtonState.Active, ButtonState.ActiveHighlighted, ButtonState.DefaultHighlighted);
        this._btnDumpFuel.Clicked.subscribe(this::onDumpFuel);
        this._btnDumpFuel.setTooltips(
                Component.translatable("gui.bigreactors.reactor.fluidaccessport.dumpfuel.line1").setStyle(CommonConstants.STYLE_TOOLTIP_TITLE),
                TEXT_EMPTY_LINE,
                Component.translatable("gui.bigreactors.reactor.fluidaccessport.dumpfuel.line2"),
                Component.translatable("gui.bigreactors.reactor.fluidaccessport.dumpfuel.line3"),
                Component.translatable("gui.bigreactors.reactor.fluidaccessport.dumpfuel.line4")
        );

        // -- dump waste command button

        this._btnDumpWaste.setPadding(0);
        this._btnDumpWaste.setIconForState(CommonIcons.ButtonDumpWaste.get(), ButtonState.Default);
        this._btnDumpWaste.setIconForState(CommonIcons.ButtonDumpWasteActive.get(), ButtonState.Active, ButtonState.ActiveHighlighted, ButtonState.DefaultHighlighted);
        this._btnDumpWaste.Clicked.subscribe(this::onDumpWaste);
        this._btnDumpWaste.setTooltips(
                Component.translatable("gui.bigreactors.reactor.fluidaccessport.dumpwaste.line1").setStyle(CommonConstants.STYLE_TOOLTIP_TITLE),
                TEXT_EMPTY_LINE,
                Component.translatable("gui.bigreactors.reactor.fluidaccessport.dumpwaste.line2"),
                Component.translatable("gui.bigreactors.reactor.fluidaccessport.dumpwaste.line3"),
                Component.translatable("gui.bigreactors.reactor.fluidaccessport.dumpwaste.line4")
        );

        panel.addControl(this.buttonsPanel(this._btnInputDirection, this._btnOutputDirection, this._btnDumpFuel, this._btnDumpWaste));

        // - tanks

        final Component tankCapacity = Component.literal(String.format("%d mB", ReactorFluidAccessPortEntity.TANK_CAPACITY)).setStyle(CommonConstants.STYLE_TOOLTIP_VALUE);
        final Panel tanksPanel = new Panel(this);

        tanksPanel.setDesiredDimension(47, VBARPANEL_HEIGHT);
        tanksPanel.setLayoutEngineHint(AnchoredLayoutEngine.Anchor.TopRight);
        tanksPanel.setLayoutEngine(new HorizontalLayoutEngine()
                .setZeroMargins()
                .setVerticalAlignment(VerticalAlignment.Top)
                .setHorizontalAlignment(HorizontalAlignment.Left));
        panel.addControl(tanksPanel);

        // -- fuel

        final BindableTextComponent<Component> fuelFluidName = new BindableTextComponent<>((Component name) -> name);
        final BindableTextComponent<Integer> fuelAmount = new BindableTextComponent<>(
                amount -> Component.literal(String.format("%d mB", amount)).setStyle(CommonConstants.STYLE_TOOLTIP_VALUE));
        final BindableTextComponent<Double> fuelStoredPercentage = new BindableTextComponent<>(
                percentage -> Component.literal(String.format("%d", (int)(percentage * 100))).setStyle(CommonConstants.STYLE_TOOLTIP_VALUE));

        p = this.vBarPanel();
        this.addBarIcon(CommonIcons.FuelReactantIcon, p).useTooltipsFrom(this._fuelTank);

        this._fuelTank.setTooltips(ImmutableList.of(
                        Component.translatable("gui.bigreactors.reactor.fluidaccessport.fueltank.line1").setStyle(CommonConstants.STYLE_TOOLTIP_TITLE),
                        Component.translatable("gui.bigreactors.reactor.controller.coreheatbar.line2").setStyle(CommonConstants.STYLE_TOOLTIP_VALUE),
                        Component.translatable("gui.bigreactors.reactor.fluidaccessport.fueltank.line3a").setStyle(CommonConstants.STYLE_TOOLTIP_VALUE)
                                .append(Component.translatable("gui.bigreactors.reactor.fluidaccessport.fueltank.line3b")),
                        Component.translatable("gui.bigreactors.reactor.fluidaccessport.fueltank.line4a").setStyle(CommonConstants.STYLE_TOOLTIP_VALUE)
                                .append(Component.translatable("gui.bigreactors.reactor.fluidaccessport.fueltank.line4b")),
                        TEXT_EMPTY_LINE,
                        Component.translatable("gui.bigreactors.reactor.fluidaccessport.fueltank.line5"),
                        Component.translatable("gui.bigreactors.reactor.fluidaccessport.fueltank.line6")
                ),
                ImmutableList.of(
                        // @0
                        fuelFluidName,
                        // @1
                        fuelAmount,
                        // @2
                        tankCapacity,
                        // @3
                        fuelStoredPercentage
                )
        );
        this.addBinding(c -> FluidHelper.getFluidName(this.getFluidStack(ReactantType.Fuel)),
                v -> {

                    final FluidStack stack = this.getFluidStack(ReactantType.Fuel);

                    if (stack.isEmpty()) {

                        this._fuelTank.setBarSprite(Sprite.EMPTY);
                        this._fuelTank.setBarSpriteTint(Colour.WHITE);

                    } else {

                        this._fuelTank.setBarSprite(ModRenderHelper.getFlowingFluidSprite(stack));
                        this._fuelTank.setBarSpriteTint(ModRenderHelper.getFluidTintColour(stack));
                    }
                }, fuelFluidName);
        this.addBinding(c -> this.getFluidAmount(ReactantType.Fuel), (Consumer<Integer>)this._fuelTank::setValue, fuelAmount);
        this.addBinding(c -> this.getFluidStoredPercentage(ReactantType.Fuel), v -> {}, fuelStoredPercentage);

        p.addControl(this._fuelTank);
        tanksPanel.addControl(p);

        // -- separator

        tanksPanel.addControl(this.vSeparatorPanel());

        // -- waste

        final BindableTextComponent<Component> wasteFluidName = new BindableTextComponent<>((Component name) -> name);
        final BindableTextComponent<Integer> wasteAmount = new BindableTextComponent<>(
                amount -> Component.literal(String.format("%d mB", amount)).setStyle(CommonConstants.STYLE_TOOLTIP_VALUE));
        final BindableTextComponent<Double> wasteStoredPercentage = new BindableTextComponent<>(
                percentage -> Component.literal(String.format("%d", (int)(percentage * 100))).setStyle(CommonConstants.STYLE_TOOLTIP_VALUE));

        p = this.vBarPanel();
        this.addBarIcon(CommonIcons.WasteReactantIcon, p).useTooltipsFrom(this._wasteTank);

        this._wasteTank.setTooltips(ImmutableList.of(
                        Component.translatable("gui.bigreactors.reactor.fluidaccessport.wastetank.line1").setStyle(CommonConstants.STYLE_TOOLTIP_TITLE),
                        Component.translatable("gui.bigreactors.reactor.fluidaccessport.wastetank.line2").setStyle(CommonConstants.STYLE_TOOLTIP_VALUE),
                        Component.translatable("gui.bigreactors.reactor.fluidaccessport.wastetank.line3a").setStyle(CommonConstants.STYLE_TOOLTIP_VALUE)
                                .append(Component.translatable("gui.bigreactors.reactor.fluidaccessport.wastetank.line3b")),
                        Component.translatable("gui.bigreactors.reactor.fluidaccessport.wastetank.line4a").setStyle(CommonConstants.STYLE_TOOLTIP_VALUE)
                                .append(Component.translatable("gui.bigreactors.reactor.fluidaccessport.wastetank.line4b")),
                        TEXT_EMPTY_LINE,
                        Component.translatable("gui.bigreactors.reactor.fluidaccessport.wastetank.line5"),
                        Component.translatable("gui.bigreactors.reactor.fluidaccessport.wastetank.line6")
                ),
                ImmutableList.of(
                        // @0
                        wasteFluidName,
                        // @1
                        wasteAmount,
                        // @2
                        tankCapacity,
                        // @3
                        wasteStoredPercentage
                )
        );
        this.addBinding(c -> FluidHelper.getFluidName(this.getFluidStack(ReactantType.Waste)),
                v -> {

                    final FluidStack stack = this.getFluidStack(ReactantType.Waste);

                    if (stack.isEmpty()) {

                        this._wasteTank.setBarSprite(Sprite.EMPTY);
                        this._wasteTank.setBarSpriteTint(Colour.WHITE);

                    } else {

                        this._wasteTank.setBarSprite(ModRenderHelper.getFlowingFluidSprite(stack));
                        this._wasteTank.setBarSpriteTint(ModRenderHelper.getFluidTintColour(stack));
                    }
                }, wasteFluidName);
        this.addBinding(c -> this.getFluidAmount(ReactantType.Waste), (Consumer<Integer>)this._wasteTank::setValue, wasteAmount);
        this.addBinding(c -> this.getFluidStoredPercentage(ReactantType.Waste), v -> {}, wasteStoredPercentage);

        p.addControl(this._wasteTank);
        tanksPanel.addControl(p);
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

    private void onDumpFuel(Button button, Integer mouseButton) {

        final CompoundTag options = new CompoundTag();

        options.putBoolean("void", Screen.hasShiftDown());
        this.sendCommandToServer(CommonConstants.COMMAND_DUMP_FUEL, options);
    }

    private void onDumpWaste(Button button, Integer integer) {

        final CompoundTag options = new CompoundTag();

        options.putBoolean("void", Screen.hasShiftDown());
        this.sendCommandToServer(CommonConstants.COMMAND_DUMP_WASTE, options);
    }

    private <Value> void addBinding(final Function<ModTileContainer<ReactorFluidAccessPortEntity>, Value> supplier, final Consumer<Value> consumer) {
        this._bindings.addBinding(new MonoConsumerBinding<>(this.getMenu(), supplier, consumer));
    }

    @SafeVarargs
    private final <Value> void addBinding(final Function<ModTileContainer<ReactorFluidAccessPortEntity>, Value> supplier, final Consumer<Value>... consumers) {
        this._bindings.addBinding(new MultiConsumerBinding<>(this.getMenu(), supplier, consumers));
    }

    private FluidStack getFluidStack(final ReactantType type) {
        return this.getTileEntity().getFluidStackHandler(type).getFluidInTank(0);
    }

    private int getFluidAmount(final ReactantType type) {
        return this.getFluidStack(type).getAmount();
    }

    private double getFluidStoredPercentage(final ReactantType type) {
        return (double)this.getFluidAmount(type) / (double)ReactorFluidAccessPortEntity.TANK_CAPACITY;
    }

    private Panel buttonsPanel(final IControl setInput, final IControl setOutput,
                               final IControl dumpFuel, final IControl dumpWaste) {

        final Panel p = new Panel(this);

        p.setDesiredDimension(18 * 2 + 2, 18 * 2 + 2 + 18);
        p.setLayoutEngineHint(AnchoredLayoutEngine.Anchor.TopLeft);
        p.setCustomBackgroundPainter((panel, matrix) -> {

            final Point xy = panel.controlToScreen(0, 0+18);
            final ISprite border = CommonIcons.ImageButtonBorder.get();
            final int z = (int)panel.getGui().getZLevel();

            ModRenderHelper.paintSprite(matrix, border, xy.X, xy.Y, z, 18, 18);
            ModRenderHelper.paintSprite(matrix, border, xy.X + 20, xy.Y, z, 18, 18);
            ModRenderHelper.paintSprite(matrix, border, xy.X, xy.Y + 20, z, 18, 18);
            ModRenderHelper.paintSprite(matrix, border, xy.X + 20, xy.Y + 20, z, 18, 18);
        });

        setInput.setLayoutEngineHint(FixedLayoutEngine.hint(1, 1+18, 16, 16));
        p.addControl(setInput);

        setOutput.setLayoutEngineHint(FixedLayoutEngine.hint(21, 1+18, 16, 16));
        p.addControl(setOutput);

        dumpFuel.setLayoutEngineHint(FixedLayoutEngine.hint(1, 21+18, 16, 16));
        p.addControl(dumpFuel);

        dumpWaste.setLayoutEngineHint(FixedLayoutEngine.hint(21, 21+18, 16, 16));
        p.addControl(dumpWaste);

        return p;
    }

    //TODO move similar helpers to a common utility class when ZC.base is merged in
    private GaugeBar liquidBar(final String name, final double maxValue) {

        final GaugeBar bar = new GaugeBar(this, name, maxValue, CommonIcons.BarBackground.get());

        bar.setDesiredDimension(18, 66);
        bar.setBackground(CommonIcons.BarBackground.get());
        bar.setPadding(1);

        return bar;
    }

    private Panel vSeparatorPanel() {

        final Panel p = new Panel(this);
        final Static s = new Static(this, 1, VBARPANEL_HEIGHT);

        p.setDesiredDimension(11, VBARPANEL_HEIGHT);
        p.setLayoutEngine(new FixedLayoutEngine());

        s.setColor(Colour.BLACK);
        s.setLayoutEngineHint(FixedLayoutEngine.hint(5, 0, 1, VBARPANEL_HEIGHT));

        p.addControl(s);

        return p;
    }

    private Panel vBarPanel() {

        final Panel p = new Panel(this);

        p.setDesiredDimension(VBARPANEL_WIDTH, VBARPANEL_HEIGHT);
        p.setLayoutEngine(new VerticalLayoutEngine()
                .setZeroMargins()
                .setControlsSpacing(2));

        return p;
    }

    private IControl addBarIcon(final NonNullSupplier<ISprite> icon, final Panel parent) {
        return this.addBarIcon(icon, 16, 16, parent);
    }

    private IControl addBarIcon(final NonNullSupplier<ISprite> icon, final int width, final int height, final Panel parent) {

        final IControl c = new Picture(this, this.nextGenericName(), icon.get(), width, height);

        c.setDesiredDimension(width, height);
        parent.addControl(c);
        return c;
    }

    private static final Component TEXT_EMPTY = Component.translatable("gui.bigreactors.generic.empty").setStyle(CommonConstants.STYLE_TOOLTIP_VALUE);
    private final static int VBARPANEL_WIDTH = 18;
    private final static int VBARPANEL_HEIGHT = 84;

    private final BindingGroup _bindings;

    private final SwitchPictureButton _btnInputDirection;
    private final SwitchPictureButton _btnOutputDirection;
    private final Button _btnDumpFuel;
    private final Button _btnDumpWaste;

    private final GaugeBar _fuelTank;
    private final GaugeBar _wasteTank;

    //endregion
}
