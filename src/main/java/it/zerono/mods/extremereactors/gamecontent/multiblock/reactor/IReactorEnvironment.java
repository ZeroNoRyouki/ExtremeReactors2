/*
 *
 * IReactorEnvironment.java
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

import it.zerono.mods.extremereactors.api.reactor.radiation.IRadiationModerator;
import net.minecraft.core.BlockPos;

public interface IReactorEnvironment {

    boolean isSimulator();

    IHeat getReactorHeat();

    int getReactorVolume();

    float getFuelToReactorHeatTransferCoefficient();

    float getReactorToCoolantSystemHeatTransferCoefficient();

    float getReactorHeatLossCoefficient();

    IIrradiationSource getNextIrradiationSource();

    /**
     * Get a Moderator from the Reactor internal volume
     * @param position the position to look up. Must be inside the Reactor internal volume
     * @return the Moderator at the requested position, if the position is valid and a Moderator is found there
     */
    IRadiationModerator getModerator(BlockPos position);

    int getPartsCount(IReactorPartType type);




    ////////////////////

    /**
     * Perform a refueling cycle (eject waste, push new fuel in, etc)
     */
    void refuel();

    /**
     * Attempt to eject waste contained in the Reactor
     *
     * @param voidLeftover if true, any remaining waste will be voided
     */
    void ejectWaste(boolean voidLeftover);



}
