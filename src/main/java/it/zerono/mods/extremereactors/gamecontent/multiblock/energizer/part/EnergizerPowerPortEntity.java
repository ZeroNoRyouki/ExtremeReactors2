/*
 * EnergizerPowerPortEntity
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

import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.MultiBlockEnergizer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.container.EnergizerPowerPortContainer;
import it.zerono.mods.zerocore.base.multiblock.part.io.power.IPowerPortHandler;
import it.zerono.mods.zerocore.lib.block.INeighborChangeListener;
import it.zerono.mods.zerocore.lib.block.TileCommandDispatcher;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class EnergizerPowerPortEntity
        extends AbstractEnergizerPowerPortEntity
        implements MenuProvider, INeighborChangeListener {

    public EnergizerPowerPortEntity(EnergySystem system, BlockEntityType<?> entityType,
                                    BlockPos position, BlockState blockState) {

        super(entityType, position, blockState);
        this.setHandler(IPowerPortHandler.create(system, IoMode.Passive, this));

        this.setCommandDispatcher(TileCommandDispatcher.<EnergizerPowerPortEntity>builder()
                .addServerHandler(CommonConstants.COMMAND_SET_INPUT, tile -> tile.setIoDirection(IoDirection.Input))
                .addServerHandler(CommonConstants.COMMAND_SET_OUTPUT, tile -> tile.setIoDirection(IoDirection.Output))
                .build(this));
    }

    //region client render support

    @Override
    protected int getUpdatedModelVariantIndex() {

        if (!this.isMachineAssembled()) {
            return 0;
        }

        int index = this.getIoDirection().isInput() ? 1 : 3;

        if (this.getPowerPortHandler().isConnected()) {
            ++index;
        }

        return index;
    }

    //endregion
    //region ISyncableEntity

    @Override
    public void syncDataFrom(final CompoundTag data, final SyncReason syncReason) {

        super.syncDataFrom(data, syncReason);
        this.setIoDirection(IoDirection.read(data, "iodir", IoDirection.Input));
    }

    @Override
    public CompoundTag syncDataTo(final CompoundTag data, final SyncReason syncReason) {

        super.syncDataTo(data, syncReason);
        IoDirection.write(data, "iodir", this.getIoDirection());
        return data;
    }

    //endregion
    //region INeighborChangeListener

    @Override
    public void onNeighborBlockChanged(BlockState state, BlockPos neighborPosition, boolean isMoving) {

        if (this.isConnected()) {
            this.getPowerPortHandler().checkConnections(this.getLevel(), this.getWorldPosition());
        }

        this.requestClientRenderUpdate();
    }

    @Override
    public void onNeighborTileChanged(BlockState state, BlockPos neighborPosition) {

        if (this.isConnected()) {
            this.getPowerPortHandler().checkConnections(this.getLevel(), this.getWorldPosition());
        }

        this.requestClientRenderUpdate();
    }

    //endregion
    //region AbstractModBlockEntity

    @Override
    public boolean canOpenGui(Level world, BlockPos position, BlockState state) {
        return this.isMachineAssembled();
    }

    //endregion
    //region MenuProvider

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int windowId, final Inventory inventory, final Player player) {
        return new EnergizerPowerPortContainer(windowId, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return super.getPartDisplayName();
    }

    //endregion
    //region AbstractCuboidMultiblockPart

    @Override
    public void onAttached(MultiBlockEnergizer newController) {

        super.onAttached(newController);
        this.getPowerPortHandler().checkConnections(this.getLevel(), this.getWorldPosition());
    }

    @Override
    public void onPostMachineAssembled(MultiBlockEnergizer controller) {

        super.onPostMachineAssembled(controller);
        this.getPowerPortHandler().checkConnections(this.getLevel(), this.getWorldPosition());
    }

    //endregion
    //region BlockEntity

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {

        final LazyOptional<T> cap = this.getPowerPortHandler().getCapability(capability, side);

        return null != cap ? cap : super.getCapability(capability, side);
    }

    //endregion
}
