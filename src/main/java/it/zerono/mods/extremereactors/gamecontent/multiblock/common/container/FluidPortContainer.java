/*
 *
 * FluidPortContainer.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common.container;

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.AbstractFluidGeneratorMultiblockController;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockFluidGeneratorVariant;
import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity;
import it.zerono.mods.zerocore.base.multiblock.part.io.fluid.IFluidPort;
import it.zerono.mods.zerocore.lib.IActivableMachine;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockVariantProvider;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.item.inventory.container.ContainerFactory;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.BooleanData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.EnumData;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockMachine;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class FluidPortContainer<Controller extends AbstractFluidGeneratorMultiblockController<Controller, V> & IMultiblockMachine & IActivableMachine,
        V extends IMultiblockFluidGeneratorVariant,
        T extends AbstractMultiblockEntity<Controller> & IFluidPort & IMultiblockVariantProvider<V> & MenuProvider>
        extends ModTileContainer<T> {

    public final BooleanData ACTIVE;
    public final EnumData<IoDirection> DIRECTION;

    public FluidPortContainer(final int windowId, final MenuType<? extends FluidPortContainer<Controller, V, T>> type,
                              final Inventory playerInventory, final T port) {

        super(5, ContainerFactory.EMPTY, type, windowId, playerInventory, port);

        final Controller controller = port.getMultiblockController().orElseThrow(IllegalStateException::new);
        final boolean isClientSide = controller.getWorld().isClientSide();

        // don't use method refs here to avoid LambdaConversionExceptions
        //noinspection Convert2MethodRef
        this.ACTIVE = BooleanData.of(this, isClientSide, () -> () -> controller.isMachineActive());
        //noinspection Convert2MethodRef
        this.DIRECTION = EnumData.of(this, isClientSide, IoDirection.class, () -> () -> port.getIoDirection());
    }

    public FluidPortContainer(final int windowId, final MenuType<? extends FluidPortContainer<Controller, V, T>> type,
                              final Inventory playerInventory, final FriendlyByteBuf networkData) {
        this(windowId, type, playerInventory, AbstractModBlockEntity.getGuiClientBlockEntity(networkData));
    }
}
