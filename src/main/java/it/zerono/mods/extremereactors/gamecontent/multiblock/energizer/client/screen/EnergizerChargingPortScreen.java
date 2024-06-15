/*
 * EnergizerChargingPortScreen
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * Do not remove or edit this header
 *
 */

package it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.client.screen;

import it.zerono.mods.extremereactors.CommonLocations;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.AbstractChargingPortScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.MultiBlockEnergizer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.container.EnergizerChargingPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.part.EnergizerChargingPortEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.variant.IMultiblockEnergizerVariant;
import it.zerono.mods.zerocore.base.client.screen.control.MachineStatusIndicator;
import it.zerono.mods.zerocore.lib.client.gui.sprite.SpriteTextureMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class EnergizerChargingPortScreen
        extends AbstractChargingPortScreen<MultiBlockEnergizer, IMultiblockEnergizerVariant,
                    EnergizerChargingPortEntity, EnergizerChargingPortContainer> {

    public EnergizerChargingPortScreen(EnergizerChargingPortContainer container, Inventory inventory, Component title) {
        super(container, inventory, title, CommonLocations.ENERGIZER.buildWithSuffix("part-forgechargingport"),
                () -> new SpriteTextureMap(CommonLocations.TEXTURES_GUI_MULTIBLOCK
                        .buildWithSuffix("basic_background.png"), 256, 256));
    }

    //region AbstractMultiblockScreen

    @Override
    protected MachineStatusIndicator createStatusIndicator(EnergizerChargingPortContainer container) {
        return this.createEnergizerStatusIndicator(container.ACTIVE);
    }

    //endregion
}
