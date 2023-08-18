/*
 *
 * ReactorFluidPortScreen.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.screen;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.AbstractFluidPortScreen;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.container.FluidPortContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorFluidPortEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.IMultiblockReactorVariant;
import it.zerono.mods.zerocore.base.client.screen.control.MachineStatusIndicator;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ReactorFluidPortScreen
        extends AbstractFluidPortScreen<MultiblockReactor, IMultiblockReactorVariant, ReactorFluidPortEntity> {

    public ReactorFluidPortScreen(FluidPortContainer<MultiblockReactor, IMultiblockReactorVariant, ReactorFluidPortEntity> container,
                                  PlayerInventory inventory, ITextComponent title) {
        super(container, inventory, title, ExtremeReactors.newID("reactor/part-forgefluidport"));
    }

    //region AbstractMultiblockScreen

    @Override
    protected MachineStatusIndicator createStatusIndicator(FluidPortContainer<MultiblockReactor, IMultiblockReactorVariant, ReactorFluidPortEntity> container) {
        return this.createReactorStatusIndicator(container.ACTIVE);
    }

    //endregion
}
