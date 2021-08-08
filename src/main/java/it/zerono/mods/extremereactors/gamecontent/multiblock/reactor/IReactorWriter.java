/*
 *
 * IReactorWriter.java
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

import it.zerono.mods.zerocore.lib.IActivableMachine;
import net.minecraft.core.BlockPos;

public interface IReactorWriter
        extends IReactorReader, IActivableMachine {

    void setWasteEjectionMode(WasteEjectionSetting newSetting);

    void setControlRodsInsertionRatio(int newRatio);

    void changeControlRodsInsertionRatio(int delta);

    /**
     * Attempt to eject fuel contained in the Reactor
     */
    void ejectFuel();

    /**
     * Attempt to eject fuel contained in the Reactor
     *
     * @param voidLeftover if true, any remaining fuel will be voided
     */
    void ejectFuel(boolean voidLeftover);

    /**
     * Attempt to eject fuel contained in the Reactor
     *
     * @param voidLeftover if true, any remaining fuel will be voided
     * @param portPosition coordinates of the Access Port to witch distribute fuel
     */
    void ejectFuel(boolean voidLeftover, BlockPos portPosition);

    /**
     * Attempt to eject waste contained in the Reactor
     */
    void ejectWaste();

    /**
     * Attempt to eject waste contained in the Reactor
     *
     * @param voidLeftover if true, any remaining waste will be voided
     */
    void ejectWaste(boolean voidLeftover);

    /**
     * Attempt to eject waste contained in the Reactor
     *
     * @param voidLeftover if true, any remaining waste will be voided
     * @param portPosition coordinates of the Access Port to witch distribute waste
     */
    void ejectWaste(boolean voidLeftover, BlockPos portPosition);
}
