/*
 *
 * ReprocessorControllerScreen.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.client.screen;

import com.google.common.collect.ImmutableList;
import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.AbstractMultiblockScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.MultiblockReprocessor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.part.ReprocessorControllerEntity;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.DesiredDimension;
import it.zerono.mods.zerocore.lib.client.gui.IControl;
import it.zerono.mods.zerocore.lib.client.gui.control.*;
import it.zerono.mods.zerocore.lib.client.gui.databind.BindingGroup;
import it.zerono.mods.zerocore.lib.client.gui.databind.MonoConsumerBinding;
import it.zerono.mods.zerocore.lib.client.gui.databind.MultiConsumerBinding;
import it.zerono.mods.zerocore.lib.client.gui.layout.*;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISpriteTextureMap;
import it.zerono.mods.zerocore.lib.client.gui.sprite.Sprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.SpriteTextureMap;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.client.text.BindableTextComponent;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.item.inventory.PlayerInventoryUsage;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;
import java.util.function.Function;

public class ReprocessorControllerScreen
        extends AbstractMultiblockScreen<MultiblockReprocessor, ReprocessorControllerEntity, ModTileContainer<ReprocessorControllerEntity>> {

    public ReprocessorControllerScreen(final ModTileContainer<ReprocessorControllerEntity> container,
                                       final PlayerInventory inventory, final ITextComponent title) {

        super(container, inventory, PlayerInventoryUsage.None, title,
                () -> new SpriteTextureMap(ExtremeReactors.newID("textures/gui/multiblock/reprocessor_background.png"), 256, 256));

        this._reprocessor = this.getMultiblockController().orElseThrow(IllegalStateException::new);
        this._bindings = new BindingGroup();

        this._energyBar = new GaugeBar(this, "energyBar", MultiblockReprocessor.ENERGY_CAPACITY, CommonIcons.PowerBar.get());
        this._fluidBar = new GaugeBar(this, "fluidbar", MultiblockReprocessor.FLUID_CAPACITY, CommonIcons.BarBackground.get());
        this._progressBar = new GaugeBar(this, "progress", 1.0, Sprite.EMPTY);
        this._inputInventoryDisplay = new Static(this, 16, 16);
        this._outputInventoryDisplay = new Static(this, 16, 16);
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

        Panel p;

        this.addPatchouliHelpButton(PatchouliCompat.HANDBOOK_ID, ExtremeReactors.newID("reactor/part-controller"), 1); //TODO fix page

        super.onScreenCreate();

        this.setContentLayoutEngine(new VerticalLayoutEngine()
                .setVerticalMargin(1)
                .setHorizontalMargin(13)
                .setControlsSpacing(0));

        final Panel mainPanel = new Panel(this);

        mainPanel.setDesiredDimension(DesiredDimension.Height, VBARPANEL_HEIGHT);
        mainPanel.setLayoutEngine(new HorizontalLayoutEngine()
                .setZeroMargins()
                .setVerticalAlignment(VerticalAlignment.Top)
                .setHorizontalAlignment(HorizontalAlignment.Left));
        this.addControl(mainPanel);

        // bars

        // - energy bar

        p = this.vBarPanel();

        this.addBarIcon(CommonIcons.PowerBattery, 16, 16, p).useTooltipsFrom(this._energyBar);

        final BindableTextComponent<Double> energyStoredText = new BindableTextComponent<>(
                stored -> new StringTextComponent(CodeHelper.formatAsHumanReadableNumber(stored,
                        EnergySystem.ForgeEnergy.getUnit())).setStyle(STYLE_TOOLTIP_VALUE));

        final BindableTextComponent<Double> energyStoredPercentageText = new BindableTextComponent<>(
                percentage -> new StringTextComponent(String.format("%d", (int)(percentage * 100))).setStyle(STYLE_TOOLTIP_VALUE));

        this._energyBar.setDesiredDimension(18, 66);
        this._energyBar.setBackground(CommonIcons.BarBackground.get());
        this._energyBar.setPadding(1);
        this._energyBar.setTooltips(ImmutableList.of(
                new TranslationTextComponent("gui.bigreactors.reprocessor.controller.energybar.line1").setStyle(STYLE_TOOLTIP_TITLE),
                new TranslationTextComponent("gui.bigreactors.reprocessor.controller.energybar.line2a").setStyle(STYLE_TOOLTIP_VALUE)
                        .append(new TranslationTextComponent("gui.bigreactors.reprocessor.controller.energybar.line2b",
                                CodeHelper.formatAsHumanReadableNumber(MultiblockReprocessor.ENERGY_CAPACITY, EnergySystem.ForgeEnergy.getUnit()))),
                new TranslationTextComponent("gui.bigreactors.reprocessor.controller.energybar.line3a").setStyle(STYLE_TOOLTIP_VALUE)
                        .append(new TranslationTextComponent("gui.bigreactors.reprocessor.controller.energybar.line3b"))
                ),
                ImmutableList.of(
                        // @0
                        energyStoredText,
                        // @1
                        energyStoredPercentageText
                )
        );
        this.addBinding(MultiblockReprocessor::getEnergyStored, this._energyBar::setValue, energyStoredText);
        this.addBinding(MultiblockReprocessor::getEnergyStoredPercentage, energyStoredPercentageText);
        p.addControl(this._energyBar);
        mainPanel.addControl(p);

        // - separator
        mainPanel.addControl(this.vSeparatorPanel());

        // - fluid bar

        final BindableTextComponent<ITextComponent> fluidName = new BindableTextComponent<>((ITextComponent name) -> name);
        final BindableTextComponent<Integer> fluidAmount = new BindableTextComponent<>(
                amount -> new StringTextComponent(CodeHelper.formatAsMillibuckets(amount)).setStyle(STYLE_TOOLTIP_VALUE));
        final BindableTextComponent<Double> fluidStoredPercentage = new BindableTextComponent<>(
                percentage -> new StringTextComponent(String.format("%d", (int)(percentage * 100))).setStyle(STYLE_TOOLTIP_VALUE));

        p = this.vBarPanel();
        this.addBarIcon(CommonIcons.CoolantIcon, p).useTooltipsFrom(this._fluidBar); //TODO fix icon

        this._fluidBar.setMaxValue(MultiblockReprocessor.FLUID_CAPACITY);
        this._fluidBar.setDesiredDimension(18, 66);
        this._fluidBar.setBackground(CommonIcons.BarBackground.get());
        this._fluidBar.setPadding(1);
        this._fluidBar.setTooltips(ImmutableList.of(
                new TranslationTextComponent("gui.bigreactors.reprocessor.controller.coolantbar.line1").setStyle(STYLE_TOOLTIP_TITLE),
                new TranslationTextComponent("gui.bigreactors.reactor.controller.coreheatbar.line2").setStyle(STYLE_TOOLTIP_VALUE),
                new TranslationTextComponent("gui.bigreactors.reprocessor.controller.coolantbar.line3a").setStyle(STYLE_TOOLTIP_VALUE)
                        .append(new TranslationTextComponent("gui.bigreactors.reprocessor.controller.coolantbar.line3b",
                                CodeHelper.formatAsMillibuckets(MultiblockReprocessor.FLUID_CAPACITY))),
                new TranslationTextComponent("gui.bigreactors.reprocessor.controller.coolantbar.line4a").setStyle(STYLE_TOOLTIP_VALUE)
                        .append(new TranslationTextComponent("gui.bigreactors.reprocessor.controller.coolantbar.line4b"))
                ),
                ImmutableList.of(
                        // @0
                        fluidName,
                        // @1
                        fluidAmount,
                        // @2
                        fluidStoredPercentage
                )
        );
        this.addBinding((MultiblockReprocessor r) -> getFluidName(r.getFluidHandler().getFluidInTank(0)),
                v -> {
                    this._fluidBar.setBarSprite(Sprite.EMPTY);
                    this._fluidBar.setBarSpriteTint(Colour.WHITE);

                    final FluidStack fluidStack = this._reprocessor.getFluidHandler().getFluidInTank(0);

                    if (!fluidStack.isEmpty()) {

                        final Fluid fluid = fluidStack.getFluid();
                        final ISprite fluidSprite = ModRenderHelper.getFlowingFluidSprite(fluid.getFluid());
                        final Colour fluidTint = Colour.fromARGB(fluid.getAttributes().getColor());

                        this._fluidBar.setBarSprite(fluidSprite);
                        this._fluidBar.setBarSpriteTint(fluidTint);

                        this._progressBar.setBarSprite(fluidSprite);
                        this._progressBar.setBarSpriteTint(fluidTint);
                    }
                }, fluidName);
        this.addBinding(MultiblockReprocessor::getFluidStored, (Consumer<Integer>)this._fluidBar::setValue, fluidAmount);
        this.addBinding(MultiblockReprocessor::getFluidStoredPercentage, v -> {}, fluidStoredPercentage);

        p.addControl(this._fluidBar);
        mainPanel.addControl(p);

        // - separator
        mainPanel.addControl(this.vSeparatorPanel());

        // recipe

        mainPanel.addControl(this.recipePanel());

        // - separator
        mainPanel.addControl(this.vSeparatorPanel());

        // - inventory display
//
//        p = new Panel(this);
//        p.setLayoutEngine(new VerticalLayoutEngine());
//        p.setDesiredDimension(DesiredDimension.Width, 32);
//
//        p.addControl(this._inputInventoryDisplay);
//        this.addBinding(r -> r.getItemHandler(IoDirection.Input).getStackInSlot(0),
//                stack -> setInventoryDisplay(this._inputInventoryDisplay, stack));
//
//        ISpriteTextureMap map = new SpriteTextureMap(ExtremeReactors.newID("textures/gui/multiblock/vertical_progress_arrow.png"), 16, 32);
//        ISprite sp = ModRenderHelper.getStillFluidSprite(Fluids.WATER);
//        GaugeBar gb = new GaugeBar(this, "progress", 1.0, sp);
//        gb.setDesiredDimension(16, 32);
//        gb.setOverlay(map.sprite().ofSize(16, 32).build());
//        gb.setTopDown(true);
//        gb.setBarSpriteTint(Colour.fromARGB(Fluids.WATER.getAttributes().getColor()));
//        p.addControl(gb);
//        this.addBinding(MultiblockReprocessor::getRecipeProgress, gb::setValue);
//
//
//        p.addControl(this._outputInventoryDisplay);
//        this.addBinding(r -> r.getItemHandler(IoDirection.Output).getStackInSlot(0),
//                stack -> setInventoryDisplay(this._outputInventoryDisplay, stack));
//
//        mainPanel.addControl(p);

        // COMMANDS

        final Panel commandPanel = this.vCommandPanel();

        mainPanel.addControl(commandPanel);

        // - machine on/off

        int x = 0;
        int y = 0;
        int w = 25;

        SwitchButton on = new SwitchButton(this, "on", "ON", false, "onoff");
        SwitchButton off = new SwitchButton(this, "off", "OFF", true, "onoff");

        on.setLayoutEngineHint(FixedLayoutEngine.hint(x, y, w, 16));
        on.setTooltips(new TranslationTextComponent("gui.bigreactors.reactor.controller.on.line1"));
        on.Activated.subscribe(this::onActiveStateChanged);
        on.Deactivated.subscribe(this::onActiveStateChanged);
        this.addBinding(MultiblockReprocessor::isMachineActive, on::setActive);

        off.setLayoutEngineHint(FixedLayoutEngine.hint(x + w, y, w, 16));
        off.setTooltips(new TranslationTextComponent("gui.bigreactors.reactor.controller.off.line1"));
        this.addBinding(MultiblockReprocessor::isMachineActive, active -> off.setActive(!active));

        commandPanel.addControl(on, off);





    }

    /**
     * Called when this screen need to be updated after the TileEntity data changed.
     * Override to handle this event
     */
    @Override
    protected void onDataUpdated() {

        super.onDataUpdated();
        this._bindings.update();
    }

    //endregion
    //region internals

    private final <Value> void addBinding(final Function<MultiblockReprocessor, Value> supplier, final Consumer<Value> consumer) {
        this._bindings.addBinding(new MonoConsumerBinding<>(this._reprocessor, supplier, consumer));
    }

    @SafeVarargs
    private final <Value> void addBinding(final Function<MultiblockReprocessor, Value> supplier, final Consumer<Value>... consumers) {
        this._bindings.addBinding(new MultiConsumerBinding<>(this._reprocessor, supplier, consumers));
    }

    private void onActiveStateChanged(final SwitchButton button) {

        if (!this.isDataUpdateInProgress()) {
            this.sendCommandToServer(button.getActive() ?
                    CommonConstants.COMMAND_ACTIVATE :
                    CommonConstants.COMMAND_DEACTIVATE);
        }
    }

    private static ITextComponent getFluidName(final FluidStack fluidStack) {

        if (fluidStack.isEmpty()) {
            return TEXT_EMPTY;
        }

        return new TranslationTextComponent(fluidStack.getFluid().getAttributes().getTranslationKey())
                .setStyle(STYLE_TOOLTIP_VALUE);
    }

    private Panel vBarPanel() {

        final Panel p = new Panel(this);

        p.setDesiredDimension(VBARPANEL_WIDTH, VBARPANEL_HEIGHT);
        p.setLayoutEngine(new VerticalLayoutEngine()
                .setZeroMargins()
                .setControlsSpacing(2));

        return p;
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

    private Panel vCommandPanel() {

        final Panel p = new Panel(this);

        p.setDesiredDimension(DesiredDimension.Height, VBARPANEL_HEIGHT);
        p.setLayoutEngine(new FixedLayoutEngine());

        return p;
    }

    private Panel recipePanel() {

//        final Panel p = new Panel(this);
//        SlotsGroup sg;
//
//        p.setDesiredDimension(DesiredDimension.Width, 70);
//        p.setLayoutEngine(new VerticalLayoutEngine()
//                .setVerticalMargin(17)
//                .setHorizontalAlignment(HorizontalAlignment.Center));
//
//        sg = this.createSingleSlotGroupControl("recipeIn", "in");
//        p.addControl(sg);
//
//        sg = this.createSingleSlotGroupControl("recipeOut", "out");
//        p.addControl(sg);


        final Panel p = new Panel(this);

        p.setLayoutEngine(new VerticalLayoutEngine());
        p.setDesiredDimension(DesiredDimension.Width, 64);

        // recipe input

        p.addControl(this._inputInventoryDisplay);
        this.addBinding(r -> r.getItemHandler(IoDirection.Input).getStackInSlot(0),
                stack -> setInventoryDisplay(this._inputInventoryDisplay, stack));

        // recipe progress

        this._progressBar.setDesiredDimension(16, 32);
        this._progressBar.setOverlay(new SpriteTextureMap(ExtremeReactors.newID("textures/gui/multiblock/vertical_progress_arrow.png"), 16, 32)
                .sprite().ofSize(16, 32).build());
        this._progressBar.setTopDown(true);
        p.addControl(this._progressBar);

        this.addBinding(MultiblockReprocessor::getRecipeProgress, this._progressBar::setValue);

        // recipe output

        p.addControl(this._outputInventoryDisplay);
        this.addBinding(r -> r.getItemHandler(IoDirection.Output).getStackInSlot(0),
                stack -> setInventoryDisplay(this._outputInventoryDisplay, stack));

        return p;
    }

    private static void setInventoryDisplay(final Static display, final ItemStack stack) {

        if (stack.getCount() > 1) {
            display.setStackWithCount(stack);
        } else {
            display.setStack(stack);
        }
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

    private static final ITextComponent TEXT_EMPTY = new TranslationTextComponent("gui.bigreactors.generic.empty").setStyle(STYLE_TOOLTIP_VALUE);

    private final static int VBARPANEL_WIDTH = 18;
    private final static int VBARPANEL_HEIGHT = 84;


    private final MultiblockReprocessor _reprocessor;
    private final BindingGroup _bindings;

    private final GaugeBar _energyBar;
    private final GaugeBar _fluidBar;
    private final GaugeBar _progressBar;
    private final Static _inputInventoryDisplay;
    private final Static _outputInventoryDisplay;

    //endregion
}
