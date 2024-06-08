package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.screen.control;

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CommonIcons;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.VentSetting;
import it.zerono.mods.zerocore.base.client.screen.BaseScreenToolTipsBuilder;
import it.zerono.mods.zerocore.base.client.screen.ClientBaseHelper;
import it.zerono.mods.zerocore.lib.client.gui.ButtonState;
import it.zerono.mods.zerocore.lib.client.gui.ModContainerScreen;
import it.zerono.mods.zerocore.lib.client.gui.control.AbstractCompositeControl;
import it.zerono.mods.zerocore.lib.client.gui.control.SwitchPictureButton;
import it.zerono.mods.zerocore.lib.data.geometry.Rectangle;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.EnumData;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class VentSettings
        extends AbstractCompositeControl {

    public VentSettings(ModContainerScreen<? extends ModContainer> gui, EnumData<VentSetting> bindable,
                        Consumer<@NotNull SwitchPictureButton> onActivated) {

        super(gui, "ventSettings");
        this.setPadding(0);

        this._ventAll = button(gui, VentSetting.VentAll, onActivated);
        this._ventAll.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.turbine.controller.vent.all.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.turbine.controller.vent.all.tooltip.body")
        );

        this._ventOverflow = button(gui, VentSetting.VentOverflow, onActivated);
        this._ventOverflow.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.turbine.controller.vent.overflow.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.turbine.controller.vent.overflow.tooltip.body")
        );

        this._ventDoNotVent = button(gui, VentSetting.DoNotVent, onActivated);
        this._ventDoNotVent.setTooltips(new BaseScreenToolTipsBuilder()
                .addTranslatableAsTitle("gui.bigreactors.turbine.controller.vent.donotvent.tooltip.title")
                .addEmptyLine()
                .addTranslatable("gui.bigreactors.turbine.controller.vent.donotvent.tooltip.body")
        );

        bindable.bind(setting -> {

            this._ventAll.setActive(VentSetting.VentAll.test(setting));
            this._ventOverflow.setActive(VentSetting.VentOverflow.test(setting));
            this._ventDoNotVent.setActive(VentSetting.DoNotVent.test(setting));
        });

        this.setDesiredDimension(3 * ClientBaseHelper.SQUARE_BUTTON_DIMENSION + 2+2+2, ClientBaseHelper.SQUARE_BUTTON_DIMENSION);
        this.addChildControl(this._ventAll, this._ventOverflow, this._ventDoNotVent);
    }

    //region AbstractCompositeControl

    @Override
    public void setBounds(final Rectangle bounds) {

        super.setBounds(bounds);

        final int size = ClientBaseHelper.SQUARE_BUTTON_DIMENSION;

        this._ventAll.setBounds(new Rectangle(0, 0, size, size));
        this._ventOverflow.setBounds(new Rectangle(size + 3, 0, size, size));
        this._ventDoNotVent.setBounds(new Rectangle(2 * size + 6, 0, size, size));
    }

    //endregion
    //region internals

    private static SwitchPictureButton button(ModContainerScreen<? extends ModContainer> gui, VentSetting setting,
                                              Consumer<@NotNull SwitchPictureButton> onActivated) {

        final SwitchPictureButton swp = new SwitchPictureButton(gui, setting.name(), false, "ventSetting");

        swp.setTag(setting);
        swp.Activated.subscribe(onActivated);
        swp.enablePaintBlending(true);
        swp.setPadding(1);
        swp.setBackground(CommonIcons.ImageButtonBackground.get());

        switch (setting) {

            case VentAll:
                ClientBaseHelper.setButtonSpritesAndOverlayForState(swp, ButtonState.Default, CommonIcons.ButtonVentAll);
                ClientBaseHelper.setButtonSpritesAndOverlayForState(swp, ButtonState.Active, CommonIcons.ButtonVentAllActive);
                break;

            case VentOverflow:
                ClientBaseHelper.setButtonSpritesAndOverlayForState(swp, ButtonState.Default, CommonIcons.ButtonVentOverflow);
                ClientBaseHelper.setButtonSpritesAndOverlayForState(swp, ButtonState.Active, CommonIcons.ButtonVentOverflowActive);
                break;

            case DoNotVent:
                ClientBaseHelper.setButtonSpritesAndOverlayForState(swp, ButtonState.Default, CommonIcons.ButtonVentDoNot);
                ClientBaseHelper.setButtonSpritesAndOverlayForState(swp, ButtonState.Active, CommonIcons.ButtonVentDoNotActive);
                break;
        }

        return swp;
    }

    final SwitchPictureButton _ventAll, _ventOverflow, _ventDoNotVent;

    //endregion
}
