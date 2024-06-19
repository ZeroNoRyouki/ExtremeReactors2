/*
 * EnergizerPowerPortContainer
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
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.part.EnergizerPowerPortEntity;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.item.inventory.container.ContainerFactory;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.BooleanData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.EnumData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.WideAmountData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class EnergizerPowerPortContainer
        extends ModTileContainer<EnergizerPowerPortEntity> {

    public final BooleanData ACTIVE;
    public final EnumData<IoDirection> DIRECTION;
    public final WideAmountData TRANSFER_RATE;

    private EnergizerPowerPortContainer(boolean isClientSide, int windowId, Inventory playerInventory,
                                        EnergizerPowerPortEntity port) {

        super(isClientSide, 5, ContainerFactory.EMPTY, Content.ContainerTypes.ENERGIZER_POWERPORT.get(), windowId,
                playerInventory, port);

        final MultiBlockEnergizer energizer = port.getMultiblockController().orElseThrow(IllegalStateException::new);

        this.ACTIVE = BooleanData.of(this, energizer::isMachineActive, energizer::setMachineActive);
        this.DIRECTION = EnumData.of(this, IoDirection.class, port::getIoDirection, port::setIoDirection);
        this.TRANSFER_RATE = WideAmountData.of(this, port::getMaxTransferRate);
    }

    public EnergizerPowerPortContainer(int windowId, Inventory playerInventory, EnergizerPowerPortEntity port) {
        this(false, windowId, playerInventory, port);
    }

    public EnergizerPowerPortContainer(int windowId, Inventory playerInventory, FriendlyByteBuf networkData) {
        this(true, windowId, playerInventory, AbstractModBlockEntity.getGuiClientBlockEntity(networkData));
    }

    public EnergySystem getEnergySystem() {
        return this.getTileEntity().getPowerPortEnergySystem();
    }
}
