/*
 *
 * ReactorFluidAccessPortContainer.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.container;

import it.zerono.mods.extremereactors.api.reactor.ReactantType;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorFluidAccessPortEntity;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.item.inventory.container.ContainerFactory;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.BooleanData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.EnumData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.FluidStackData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class ReactorFluidAccessPortContainer
        extends ModTileContainer<ReactorFluidAccessPortEntity> {

    public final BooleanData ACTIVE;
    public final EnumData<IoDirection> DIRECTION;
    public final FluidStackData FUEL_STACK;
    public final FluidStackData WASTE_STACK;

    public ReactorFluidAccessPortContainer(final int windowId, final Inventory playerInventory,
                                           final ReactorFluidAccessPortEntity port) {

        super(5, ContainerFactory.EMPTY, Content.ContainerTypes.REACTOR_FLUID_ACCESSPORT.get(), windowId, playerInventory, port);

        final MultiblockReactor reactor = port.getMultiblockController().orElseThrow(IllegalStateException::new);
        final boolean isClientSide = reactor.getWorld().isClientSide();

        this.ACTIVE = BooleanData.of(this, isClientSide, () -> reactor::isMachineActive);
        this.DIRECTION = EnumData.of(this, isClientSide, IoDirection.class, () -> port::getIoDirection);

        this.FUEL_STACK = FluidStackData.sampled(2, this, isClientSide,
                () -> () -> port.getFluidStackHandler(ReactantType.Fuel).getFluidInTank(0));
        this.WASTE_STACK = FluidStackData.sampled(2, this, isClientSide,
                () -> () -> port.getFluidStackHandler(ReactantType.Waste).getFluidInTank(0));
    }

    public ReactorFluidAccessPortContainer(final int windowId, final Inventory playerInventory,
                                           final FriendlyByteBuf networkData) {
        this(windowId, playerInventory, AbstractModBlockEntity.getGuiClientBlockEntity(networkData));
    }
}
