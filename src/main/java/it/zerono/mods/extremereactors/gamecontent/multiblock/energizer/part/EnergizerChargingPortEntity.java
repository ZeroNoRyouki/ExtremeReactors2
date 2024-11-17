/*
 * EnergizerChargingPortEntity
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.part;

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.model.data.ModelTransformers;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.container.EnergizerChargingPortContainer;
import it.zerono.mods.zerocore.base.CommonConstants;
import it.zerono.mods.zerocore.base.multiblock.part.io.power.charging.IChargingPort;
import it.zerono.mods.zerocore.base.multiblock.part.io.power.charging.IChargingPortHandler;
import it.zerono.mods.zerocore.lib.block.TileCommandDispatcher;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class EnergizerChargingPortEntity
        extends AbstractEnergizerPowerPortEntity
        implements IChargingPort, MenuProvider {

    public EnergizerChargingPortEntity(EnergySystem system, BlockEntityType<?> entityType,
                                       BlockPos position, BlockState blockState) {

        super(entityType, position, blockState);
        this.setIoDirection(IoDirection.Output);
        this.setHandler(IChargingPortHandler.create(system, this, 1, 1));

        this.setCommandDispatcher(TileCommandDispatcher.<EnergizerChargingPortEntity>builder()
                .addServerHandler(CommonConstants.COMMAND_EJECT, tile -> tile.getChargingPortHandler().eject())
                .build(this));
    }

    //region client render support

    @Override
    protected int getUpdatedModelVariantIndex() {
        return (byte) (this.isMachineAssembled() ? ModelTransformers.MODEL_VARIANT_1 : ModelTransformers.MODEL_DEFAULT);
    }

    //endregion
    //region IChargingPort

    @Override
    public IChargingPortHandler getChargingPortHandler() {
        return (IChargingPortHandler) this.getPowerPortHandler();
    }

    @Override
    public IoDirection getIoDirection() {
        return IoDirection.Output;
    }

    //endregion
    //region ISyncableEntity

    @Override
    public void syncDataFrom(CompoundTag data, SyncReason syncReason) {

        super.syncDataFrom(data, syncReason);
        this.getChargingPortHandler().syncDataFrom(data, syncReason);
    }

    @Override
    public CompoundTag syncDataTo(CompoundTag data, SyncReason syncReason) {

        super.syncDataTo(data, syncReason);
        this.getChargingPortHandler().syncDataTo(data, syncReason);
        return data;
    }

    //endregion
    //region MenuProvider

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int windowId, final Inventory inventory, final Player player) {
        return new EnergizerChargingPortContainer(windowId, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return super.getPartDisplayName();
    }

    //endregion
    //region AbstractModBlockEntity

    @Override
    public boolean canOpenGui(Level world, BlockPos position, BlockState state) {
        return true;
    }

    //endregion
}
