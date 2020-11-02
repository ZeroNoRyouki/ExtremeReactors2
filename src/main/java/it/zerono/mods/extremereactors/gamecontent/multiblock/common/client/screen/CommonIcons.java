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

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISpriteBuilder;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISpriteTextureMap;
import it.zerono.mods.zerocore.lib.client.gui.sprite.SpriteTextureMap;
import net.minecraftforge.common.util.NonNullSupplier;

public enum CommonIcons implements NonNullSupplier<ISprite> {

    MachineStatusOff(builder().from(32, 176).ofSize(10, 10).build()),
    MachineStatusOn(builder().from(48, 176).ofSize(10, 10).build()),

    TemperatureIcon(builder().from(128, 0).ofSize(32, 32).build()),
    EnergyRatioIcon(builder().from(160, 0).ofSize(32, 32).build()),
    FuelIcon(builder().from(64, 0).ofSize(32, 32).build()),
    ReactivityIcon(builder().from(96, 0).ofSize(32, 32).build()),

    BarBackground(builder().from(0, 112).ofSize(16, 64).build()),
    PowerBar(builder().from(17, 113).ofSize(14, 62).build()),
    TemperatureBar(builder().from(33, 113).ofSize(14, 62).build()),
    TemperatureScale(builder().from(54, 114).ofSize(5, 59).build()),
    PowerBattery(builder().from(128, 96).build()),
    FuelMix(builder().from(144, 96).build()),
    LabelEdit(builder().from(160, 96).build()),

    ButtonInputDirection(builder().from(0, 96).build()),
    ButtonInputDirectionActive(builder().from(16, 96).build()),
    ButtonOutputDirection(builder().from(32, 96).build()),
    ButtonOutputDirectionActive(builder().from(48, 96).build()),
    ButtonDumpFuel(builder().from(64, 96).build()),
    ButtonDumpFuelActive(builder().from(80, 96).build()),
    ButtonDumpWaste(builder().from(96, 96).build()),
    ButtonDumpWasteActive(builder().from(112, 96).build()),

    ButtonSensorInputActivate(builder().from(0, 0).ofSize(32, 32).build()),
    ButtonSensorOutputCasingTemperature(builder().from(0, 80).build()),
    ButtonSensorOutputFuelTemperature(builder().from(16, 80).build()),
    ButtonSensorInputSetControlRod(builder().from(32, 80).build()),
    ButtonSensorOutputFuelMix(builder().from(48, 80).build()),
    ButtonSensorOutputFuelAmount(builder().from(64, 80).build()),
    ButtonSensorOutputWasteAmount(builder().from(80, 80).build()),
    ButtonSensorOutputEnergyAmount(builder().from(96, 80).build()),

    ButtonSensorInputActivateActive(builder().from(32, 0).ofSize(32, 32).build()),
    ButtonSensorOutputCasingTemperatureActive(builder().from(0, 64).build()),
    ButtonSensorOutputFuelTemperatureActive(builder().from(16, 64).build()),
    ButtonSensorInputSetControlRodActive(builder().from(32, 64).build()),
    ButtonSensorOutputFuelMixActive(builder().from(48, 64).build()),
    ButtonSensorOutputFuelAmountActive(builder().from(64, 64).build()),
    ButtonSensorOutputWasteAmountActive(builder().from(80, 64).build()),
    ButtonSensorOutputEnergyAmountActive(builder().from(96, 64).build()),

    ImageButtonBorder(builder().from(174, 156).ofSize(18, 18).build()),
    ImageButtonBackground(builder().from(174, 174).ofSize(18, 18).build()),
    Button16x16HightlightOverlay(builder().from(0, 176).build()),
    Button16x16DisabledOverlay(builder().from(16, 176).build()),

    PortInputSlot(builder().from(136, 116).ofSize(38, 38).build()),
    PortOutputSlot(builder().from(136, 154).ofSize(38, 38).build()),
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
            s_map = new SpriteTextureMap(ExtremeReactors.newID("textures/gui/multiblock/common_icons.png"), 192, 192);
        }

        return s_map;
    }

    private static ISpriteTextureMap s_map;
    private final ISprite _sprite;

    //endregion
}
