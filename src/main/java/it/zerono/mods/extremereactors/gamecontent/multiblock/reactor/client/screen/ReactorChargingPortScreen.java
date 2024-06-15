/*
 *
 * ReactorChargingPortScreen.java
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

import it.zerono.mods.extremereactors.CommonLocations;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.AbstractChargingPortScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.container.ChargingPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorChargingPortEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.IMultiblockReactorVariant;
import it.zerono.mods.zerocore.base.client.screen.control.MachineStatusIndicator;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ReactorChargingPortScreen
        extends AbstractChargingPortScreen<MultiblockReactor, IMultiblockReactorVariant, ReactorChargingPortEntity,
                    ChargingPortContainer<MultiblockReactor, IMultiblockReactorVariant, ReactorChargingPortEntity>> {

    public ReactorChargingPortScreen(ChargingPortContainer<MultiblockReactor, IMultiblockReactorVariant, ReactorChargingPortEntity> container,
                                     Inventory inventory, Component title) {
        super(container, inventory, title, CommonLocations.REACTOR.buildWithSuffix("part-forgechargingport"));
    }

    //region AbstractMultiblockScreen

    @Override
    protected MachineStatusIndicator createStatusIndicator(ChargingPortContainer<MultiblockReactor, IMultiblockReactorVariant, ReactorChargingPortEntity> container) {
        return this.createReactorStatusIndicator(container.ACTIVE);
    }

    //endregion
}
