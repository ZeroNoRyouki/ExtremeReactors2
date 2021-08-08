/*
 *
 * ReactorControlRodEntity.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part;

import com.google.common.base.Strings;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.FuelRodsLayout;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.IReactorReader;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.block.TileCommandDispatcher;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockPart;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity.SyncReason;

public class ReactorControlRodEntity
        extends AbstractReactorEntity
        implements MenuProvider {

    public static String COMMAND_SET_NAME = "setname";
    public static String COMMAND_SET_INSERTION = "setinsertion";

    public ReactorControlRodEntity() {

        super(Content.TileEntityTypes.REACTOR_CONTROLROD.get());
        this._insertionRatio = 0;
        this._name = "";

        this.setCommandDispatcher(TileCommandDispatcher.<ReactorControlRodEntity>builder()
                .addServerHandler(COMMAND_SET_NAME, ReactorControlRodEntity::setNameFromGUI)
                .addServerHandler(COMMAND_SET_INSERTION, ReactorControlRodEntity::setInsertionFromGUI)
                .build(this));
    }

    public void linkToFuelRods(final int fuelRodsCount) {
        CodeHelper.optionalIfPresent(this.getPartWorld(), this.getOutwardDirection(),
                (world, direction) -> this.linkToFuelRods(world, direction.getOpposite(), fuelRodsCount));
    }

    private void linkToFuelRods(final Level world, final Direction direction,
                                final int fuelRodsCount) {

        BlockPos lookupPosition = this.getWorldPosition();

        for (int i = 0; i < fuelRodsCount; ++i) {

            final int rodIndex = fuelRodsCount - i - 1;

            lookupPosition = lookupPosition.relative(direction);

            CodeHelper.optionalIfPresentOrThrow(WorldHelper.getTile(world, lookupPosition)
                            .filter(te -> te instanceof ReactorFuelRodEntity)
                    .map(te -> (ReactorFuelRodEntity)te),
                    (rod) -> rod.linkToControlRod(this, rodIndex));
        }
    }

    public FuelRodsLayout getFuelRodsLayout() {
        return this.evalOnController(IReactorReader::getFuelRodsLayout, FuelRodsLayout.EMPTY);
    }

    //region getters and setters

    public String getName() {
        return this._name;
    }

    public byte getInsertionRatio() {
        return this._insertionRatio;
    }

    public float getInsertionPercentage() {
        return Math.min(1f, Math.max(0f, ((float) (this.getInsertionRatio()) / 100f)));
    }

    public static void setName(final ReactorControlRodEntity controlRod, final String newName) {

        if (controlRod.setName(newName)) {
            controlRod.notifyBlockUpdate();
        }
    }

    public static void setInsertionRatio(final ReactorControlRodEntity controlRod, final int newRatio) {

        if (controlRod.setInsertionRatio(newRatio)) {
            controlRod.notifyBlockUpdate();
        }
    }

    public static void changeInsertionRatio(final ReactorControlRodEntity controlRod, final int delta) {

        if (controlRod.setInsertionRatio(controlRod.getInsertionRatio() + delta)) {
            controlRod.notifyBlockUpdate();
        }
    }

    public static void setInsertionRatio(final Collection<ReactorControlRodEntity> controlRods, final int newRatio) {

        final Set<ReactorControlRodEntity> updated = controlRods.stream()
                .filter(IMultiblockPart::isConnected)
                .filter(rod -> rod.setInsertionRatio(newRatio))
                .collect(Collectors.toSet());

        if (!updated.isEmpty()) {
            AbstractModBlockEntity.notifyBlockUpdate(updated);
        }
    }

    public static void changeInsertionRatio(final Collection<ReactorControlRodEntity> controlRods, final int delta) {

        final Set<ReactorControlRodEntity> updated = controlRods.stream()
                .filter(IMultiblockPart::isConnected)
                .filter(rod -> rod.setInsertionRatio(rod.getInsertionRatio() + delta))
                .collect(Collectors.toSet());

        if (!updated.isEmpty()) {
            AbstractModBlockEntity.notifyBlockUpdate(updated);
        }
    }

    //endregion
    //region INamedContainerProvider

    /**
     * Create the SERVER-side container for this TileEntity
     * @param windowId  the window id
     * @param inventory the player inventory
     * @param player    the player
     * @return the container to use on the server
     */
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int windowId, final Inventory inventory, final Player player) {
        return ModTileContainer.empty(Content.ContainerTypes.REACTOR_CONTROLROD.get(), windowId, this, (ServerPlayer)player);
    }

    @Override
    public Component getDisplayName() {
        return super.getPartDisplayName();
    }

    //endregion
    //region ISyncableEntity

    @Override
    public void syncDataFrom(CompoundTag data, SyncReason syncReason) {

        super.syncDataFrom(data, syncReason);

        if (data.contains("rodInsertion")) {
            this.setInsertionRatio(data.getByte("rodInsertion"));
        }

        if (data.contains("rodName")) {
            this.setName(data.getString("rodName"));
        }
    }

    @Override
    public CompoundTag syncDataTo(CompoundTag data, SyncReason syncReason) {

        super.syncDataTo(data, syncReason);
        data.putByte("rodInsertion", this.getInsertionRatio());
        data.putString("rodName", this.getName());
        return data;
    }

    //endregion
    //region AbstractReactorEntity

    @Override
    public boolean isGoodForPosition(PartPosition position, IMultiblockValidator validatorCallback) {

        if (position.isFace()) {

            if (position.getDirection()
                    .map(direction -> this.checkForFuelRod(direction.getOpposite()))
                    .orElse(false)) {

                return true;

            } else {

                validatorCallback.setLastError(this.getWorldPosition(), "multiblock.validation.reactor.invalid_control_rods_position");
                return false;
            }
        }

        return super.isGoodForPosition(position, validatorCallback);
    }

    //endregion
    //region AbstractModBlockEntity

    /**
     * Check if the tile entity has a GUI or not
     * Override in derived classes to return true if your tile entity got a GUI
     *
     * @param world
     * @param position
     * @param state
     */
    @Override
    public boolean canOpenGui(Level world, BlockPos position, BlockState state) {
        return this.isMachineAssembled();
    }

    //endregion
    //region internals

    private void setInsertionFromGUI(final CompoundTag data) {

        if (data.contains("v")) {

            if (data.getBoolean("all")) {
                this.getMultiblockController().ifPresent(c -> c.setControlRodsInsertionRatio(data.getInt("v")));

            } else {
                this.setInsertionRatio(data.getInt("v"));
            }
        }
    }

    private void setNameFromGUI(final CompoundTag data) {

        if (data.contains("name")) {
            this.setName(data.getString("name"));
        }
    }

    //region setters and client-updates

    private boolean setInsertionRatio(final int newRatio) {

        if (this.getInsertionRatio() == newRatio || newRatio < 0 || newRatio > 100) {
            return false;
        }

        this._insertionRatio = (byte) newRatio;
        this.setChanged();
        this.notifyBlockUpdate();
        return true;
    }

    private boolean setName(final String newName) {

        if (Strings.isNullOrEmpty(newName) || this.getName().equals(newName)) {
            return false;
        }

        this._name = newName;
        this.setChanged();
        this.notifyBlockUpdate();
        return true;
    }

    private boolean checkForFuelRod(Direction rodDirection) {
        return this.mapPartWorld(world -> WorldHelper.getLoadedTile(world, this.getWorldPosition().relative(rodDirection)) instanceof ReactorFuelRodEntity, false);
    }

    //endregion

    private String _name;
    private byte _insertionRatio;

    //endregion
}
