/*
 *
 * AbstractIOPortHandler.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common.part;

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.AbstractGeneratorMultiblockController;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockGeneratorVariant;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockVariantProvider;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullPredicate;

import javax.annotation.Nullable;

public abstract class AbstractIOPortHandler<Controller extends AbstractGeneratorMultiblockController<Controller, V>,
        V extends IMultiblockGeneratorVariant,
        T extends AbstractMultiblockEntity<Controller> & IMultiblockVariantProvider<? extends IMultiblockGeneratorVariant>> {

    public T getPart() {
        return this._part;
    }

    public boolean isActive() {
        return this._mode.isActive();
    }

    public boolean isPassive() {
        return this._mode.isPassive();
    }

    protected AbstractIOPortHandler(final T part, final IoMode mode) {

        this._part = part;
        this._mode = mode;
    }

    @Nullable
    protected <T> T lookupConsumer(@Nullable final Level world, final BlockPos position,
                                   @Nullable final Capability<T> requestedCapability,
                                   final NonNullPredicate<BlockEntity> isSameHandler,
                                   @Nullable T currentConsumer) {

        boolean wasConnected = null != currentConsumer;
        T foundConsumer = null;

        if (null != world) {

            final Direction approachDirection = this.getPart().getOutwardDirection().orElse(null);

            if (null == approachDirection) {

                wasConnected = false;

            } else {

                if (null != requestedCapability) {

                    final BlockEntity te = WorldHelper.getLoadedTile(world, position.relative(approachDirection));

                    if (null != te && !isSameHandler.test(te)) {

                        final LazyOptional<T> capability = te.getCapability(requestedCapability, approachDirection.getOpposite());

                        if (capability.isPresent()) {
                            foundConsumer = capability.orElseThrow(RuntimeException::new);
                        }
                    }
                }
            }
        }

        final boolean isConnectedNow = null != foundConsumer;
        final Level partWorld = this.getPart().getLevel();

        if (wasConnected != isConnectedNow && null != partWorld && CodeHelper.calledByLogicalClient(partWorld)) {
            WorldHelper.notifyBlockUpdate(partWorld, this.getPart().getWorldPosition(), null, null);
        }

        return foundConsumer;
    }

    //region internals

    private final T _part;
    private final IoMode _mode;

    //endregion
}
