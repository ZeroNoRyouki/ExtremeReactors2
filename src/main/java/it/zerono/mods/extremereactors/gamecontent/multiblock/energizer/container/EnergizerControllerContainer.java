/*
 * EnergizerControllerContainer
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.container;

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.MultiBlockEnergizer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.part.EnergizerControllerEntity;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.item.inventory.container.ContainerFactory;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.BooleanData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.WideAmountData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class EnergizerControllerContainer
        extends ModTileContainer<EnergizerControllerEntity> {

    public final BooleanData ACTIVE;
    public final WideAmountData ENERGY_STORED;
    public final WideAmountData ENERGY_IO;
    public final WideAmountData ENERGY_LAST_INPUT;
    public final WideAmountData ENERGY_LAST_OUTPUT;

    private EnergizerControllerContainer(boolean isClientSide, int windowId, Inventory playerInventory,
                                        EnergizerControllerEntity controller) {

        super(isClientSide, 5, ContainerFactory.EMPTY, Content.ContainerTypes.ENERGIZER_CONTROLLER.get(), windowId,
                playerInventory, controller);

        final MultiBlockEnergizer energizer = controller.getMultiblockController().orElseThrow(IllegalStateException::new);

        this._energyCapacity = energizer.getCapacity(EnergySystem.REFERENCE);

        this.ACTIVE = BooleanData.of(this, energizer::isMachineActive, energizer::setMachineActive);
        this.ENERGY_STORED = WideAmountData.of(this, () -> energizer.getEnergyStored(EnergySystem.REFERENCE),
                value -> energizer.setEnergyStored(value, EnergySystem.REFERENCE));
        this.ENERGY_IO = WideAmountData.of(this, () -> energizer.getEnergyIoRate(EnergySystem.REFERENCE),
                CodeHelper.emptyConsumer());
        this.ENERGY_LAST_INPUT = WideAmountData.of(this, () -> energizer.getEnergyInsertedLastTick(EnergySystem.REFERENCE),
                CodeHelper.emptyConsumer());
        this.ENERGY_LAST_OUTPUT = WideAmountData.of(this, () -> energizer.getEnergyExtractedLastTick(EnergySystem.REFERENCE),
                CodeHelper.emptyConsumer());
    }

    public EnergizerControllerContainer(int windowId, Inventory playerInventory, EnergizerControllerEntity controller) {
        this(false, windowId, playerInventory, controller);
    }

    public EnergizerControllerContainer(int windowId, Inventory playerInventory, FriendlyByteBuf networkData) {
        this(true, windowId, playerInventory, AbstractModBlockEntity.getGuiClientBlockEntity(networkData));
    }

    public WideAmount getEnergyCapacity() {
        return this._energyCapacity;
    }

    //region internals

    private final WideAmount _energyCapacity;

    //endregion
}
