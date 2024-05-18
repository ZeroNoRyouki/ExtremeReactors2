/*
 *
 * CommonIcons.java
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
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISpriteBuilder;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISpriteTextureMap;
import it.zerono.mods.zerocore.lib.client.gui.sprite.SpriteTextureMap;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public enum CommonIcons implements Supplier<@NotNull ISprite> {

    MachineStatusOff(builder().from(32, 176).ofSize(10, 10).build()),
    MachineStatusOn(builder().from(48, 176).ofSize(10, 10).build()),

    TemperatureIcon(builder().from(128, 0).ofSize(32, 32).build()),
    EnergyRatioIcon(builder().from(160, 0).ofSize(32, 32).build()),
    FuelIcon(builder().from(64, 0).ofSize(32, 32).build()),
    ReactivityIcon(builder().from(96, 0).ofSize(32, 32).build()),
    CoolantIcon(builder().from(80, 176).build()),
    VaporIcon(builder().from(96, 176).build()),
    FuelReactantIcon(builder().from(64, 32).build()),
    WasteReactantIcon(builder().from(64, 48).build()),

    BarBackground(builder().from(174, 126).ofSize(18, 66).build()),
    PowerBar(builder().from(0, 112).ofSize(16, 64).build()),
    RpmBar(builder().from(48, 112).ofSize(16, 64).build()),
    TemperatureBar(builder().from(16, 112).ofSize(16, 64).build()),
    TemperatureScale(builder().from(38, 114).ofSize(5, 59).build()),
    PowerBattery(builder().from(128, 96).build()),
    FuelMix(builder().from(144, 96).build()),
    RotorStatus(builder().from(160, 96).build()),
    RotorRPM(builder().from(176, 96).build()),
    LabelEdit(builder().from(64, 176).build()),
    TrashCan(builder().from(80, 48).build()),

    ButtonInputDirection(builder().from(0, 96).build()),
    ButtonInputDirectionActive(builder().from(16, 96).build()),
    ButtonOutputDirection(builder().from(32, 96).build()),
    ButtonOutputDirectionActive(builder().from(48, 96).build()),
    ButtonDumpFuel(builder().from(64, 96).build()),
    ButtonDumpFuelActive(builder().from(80, 96).build()),
    ButtonDumpWaste(builder().from(96, 96).build()),
    ButtonDumpWasteActive(builder().from(112, 96).build()),
    ButtonInductor(builder().from(112, 80).build()),
    ButtonInductorActive(builder().from(112, 64).build()),
    ButtonVentAll(builder().from(128, 80).build()),
    ButtonVentAllActive(builder().from(128, 64).build()),
    ButtonVentOverflow(builder().from(144, 80).build()),
    ButtonVentOverflowActive(builder().from(144, 64).build()),
    ButtonVentDoNot(builder().from(160, 80).build()),
    ButtonVentDoNotActive(builder().from(160, 64).build()),
    ButtonManualEject(builder().from(176, 80).build()),
    ButtonManualEjectActive(builder().from(176, 64).build()),

    ButtonSensorInputActivate(builder().from(0, 0).ofSize(32, 32).build()),
    ButtonSensorOutputCasingTemperature(builder().from(0, 80).build()),
    ButtonSensorOutputFuelTemperature(builder().from(16, 80).build()),
    ButtonSensorInputSetControlRod(builder().from(32, 80).build()),
    ButtonSensorOutputFuelMix(builder().from(48, 80).build()),
    ButtonSensorOutputFuelAmount(builder().from(64, 80).build()),
    ButtonSensorOutputWasteAmount(builder().from(80, 80).build()),
    ButtonSensorOutputEnergyAmount(builder().from(96, 80).build()),
    ButtonSensorInputFlowRegulator(builder().from(0, 48).build()),
    ButtonSensorOutputRotorSpeed(builder().from(48, 48).build()),
    ButtonSensorOutputCoolantAmount(builder().from(16, 48).build()),
    ButtonSensorOutputVaporAmount(builder().from(32, 48).build()),

    ButtonSensorInputActivateActive(builder().from(32, 0).ofSize(32, 32).build()),
    ButtonSensorOutputCasingTemperatureActive(builder().from(0, 64).build()),
    ButtonSensorOutputFuelTemperatureActive(builder().from(16, 64).build()),
    ButtonSensorInputSetControlRodActive(builder().from(32, 64).build()),
    ButtonSensorOutputFuelMixActive(builder().from(48, 64).build()),
    ButtonSensorOutputFuelAmountActive(builder().from(64, 64).build()),
    ButtonSensorOutputWasteAmountActive(builder().from(80, 64).build()),
    ButtonSensorOutputEnergyAmountActive(builder().from(96, 64).build()),
    ButtonSensorInputFlowRegulatorActive(builder().from(0, 32).build()),
    ButtonSensorOutputRotorSpeedActive(builder().from(48, 32).build()),
    ButtonSensorOutputCoolantAmountActive(builder().from(16, 32).build()),
    ButtonSensorOutputVaporAmountActive(builder().from(32, 32).build()),

    ImageButtonBorder(builder().from(118, 156).ofSize(18, 18).build()),
    ImageButtonBackground(builder().from(118, 174).ofSize(18, 18).build()),
    Button16x16HightlightOverlay(builder().from(0, 176).build()),
    Button16x16DisabledOverlay(builder().from(16, 176).build()),

    PortInputSlot(builder().from(136, 116).ofSize(38, 38).build()),
    PortOutputSlot(builder().from(136, 154).ofSize(38, 38).build()),

    ReprocessorProgressBarMask(new SpriteTextureMap(CommonLocations.TEXTURES_GUI_MULTIBLOCK
            .buildWithSuffix("reprocessor_controller_arrow.png"), 16, 32)
            .sprite().ofSize(16, 32).build()),

    ;

    CommonIcons(final ISprite sprite) {
        this._sprite = sprite;
    }

    @Override
    public ISprite get() {
        return this._sprite;
    }

    //region internals

    private static ISpriteBuilder builder() {
        return getMap().sprite();
    }

    private static ISpriteTextureMap getMap() {

        if (null == s_map) {
            s_map = new SpriteTextureMap(CommonLocations.TEXTURES_GUI_MULTIBLOCK
                    .buildWithSuffix("common_icons.png"), 192, 192);
        }

        return s_map;
    }

    private static ISpriteTextureMap s_map;
    private final ISprite _sprite;

    //endregion
}
