/*
 *
 * ReactorControlRodContainer.java
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

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorControlRodEntity;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.item.inventory.container.ContainerFactory;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.BooleanData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.ByteData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.StringData;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class ReactorControlRodContainer
        extends ModTileContainer<ReactorControlRodEntity> {

    public final BooleanData ACTIVE;
    public final StringData NAME;
    public final ByteData INSERTION_RATIO;

    public ReactorControlRodContainer(final int windowId, final PlayerInventory playerInventory,
                                      final ReactorControlRodEntity rod) {

        super(3, ContainerFactory.EMPTY, Content.ContainerTypes.REACTOR_CONTROLROD.get(), windowId, rod);

        final MultiblockReactor reactor = rod.getMultiblockController().orElseThrow(IllegalStateException::new);
        final boolean isClientSide = reactor.getWorld().isClientSide();

        this.ACTIVE = BooleanData.of(this, isClientSide, () -> reactor::isMachineActive);
        this.NAME = StringData.of(this, isClientSide, 1024, () -> rod::getName);
        this.INSERTION_RATIO = ByteData.of(this, isClientSide, () -> rod::getInsertionRatio);
    }

    public ReactorControlRodContainer(final int windowId, final PlayerInventory playerInventory,
                                      final PacketBuffer networkData) {
        this(windowId, playerInventory, AbstractModBlockEntity.getGuiClientBlockEntity(networkData));
    }


}
