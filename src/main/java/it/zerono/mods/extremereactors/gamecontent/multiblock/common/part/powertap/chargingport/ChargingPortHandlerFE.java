/*
 *
 * ChargingPortHandlerFE.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.powertap.chargingport;

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.AbstractGeneratorMultiblockController;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.AbstractMultiblockEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockGeneratorVariant;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockVariantProvider;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.energy.IEnergyStorage;

public class ChargingPortHandlerFE<Controller extends AbstractGeneratorMultiblockController<Controller, V>,
            V extends IMultiblockGeneratorVariant,
            T extends AbstractMultiblockEntity<Controller> & IMultiblockVariantProvider<? extends IMultiblockGeneratorVariant>>
        extends AbstractChargingPortHandler<Controller, V, T> {

    public ChargingPortHandlerFE(final T part) {
        super(EnergySystem.ForgeEnergy, part);
    }

    //region IPowerTapHandler

    /**
     * Send energy to the connected consumer (if there is one and we are in active mode)
     *
     * @param amount amount of energy to send
     * @return the amount of energy accepted by the consumer
     */
    public double outputEnergy(double amount) {

        final int transfer = (int)Math.min(amount, this.getChargingRate());
        final double used = this.getCapabilityFromInventory(CAPAP_FORGE_ENERGYSTORAGE, true)
                .map(cap -> (double)recharge(cap, transfer))
                .orElse(amount);

        if (used == 0) {
            this.onChargeComplete();
        }

        return used;
    }

    //endregion
    //region internals

    private static int recharge(final IEnergyStorage cap, int energyAmount) {
        return cap.canReceive() ? cap.receiveEnergy(energyAmount, false) : 0;
    }

    @SuppressWarnings("FieldMayBeFinal")
    @CapabilityInject(IEnergyStorage.class)
    private static Capability<IEnergyStorage> CAPAP_FORGE_ENERGYSTORAGE = null;

    //endregion
}
