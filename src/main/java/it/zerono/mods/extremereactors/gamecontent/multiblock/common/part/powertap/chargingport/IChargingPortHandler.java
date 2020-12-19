/*
 *
 * IChargingPortHandler.java
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
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.powertap.IPowerTapHandler;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockGeneratorVariant;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockVariantProvider;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import net.minecraftforge.items.IItemHandlerModifiable;

public interface IChargingPortHandler
        extends IPowerTapHandler, ISyncableEntity {

    static <Controller extends AbstractGeneratorMultiblockController<Controller, V>,
            V extends IMultiblockGeneratorVariant,
            T extends AbstractMultiblockEntity<Controller> & IMultiblockVariantProvider<? extends IMultiblockGeneratorVariant>>
    IChargingPortHandler create(final EnergySystem system, final T part) {

        switch (system) {

            case ForgeEnergy:
                return new ChargingPortHandlerFE<>(part);

            default:
                throw new IllegalArgumentException("Unsupported energy system: " + system);
        }
    }

    IItemHandlerModifiable getItemStackHandler(IoDirection direction);

    void eject();
}
