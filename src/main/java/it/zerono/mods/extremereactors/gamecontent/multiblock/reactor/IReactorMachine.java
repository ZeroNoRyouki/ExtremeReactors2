/*
 *
 * IReactorMachine.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor;

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.IFluidContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.IMultiblockReactorVariant;
import it.zerono.mods.zerocore.lib.IActivableMachine;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockMachine;

public interface IReactorMachine
        extends IMultiblockMachine, IActivableMachine {

    IMultiblockReactorVariant getVariant();

    OperationalMode getOperationalMode();

    IReactorEnvironment getEnvironment();

    IHeat getFuelHeat();

    IFuelContainer getFuelContainer();

    IFluidContainer getFluidContainer();

    Stats getUiStats();

    /**
     * Perform a refueling cycle, ejecting waste and inserting new fuel into the Reactor
     */
    boolean performRefuelingCycle();

    /**
     * Output power/gas to active ports
     */
    void performOutputCycle();

    /**
     * Input fluid from active ports
     */
    boolean performInputCycle();
}
