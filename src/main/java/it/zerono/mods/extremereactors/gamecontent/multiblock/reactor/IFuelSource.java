/*
 *
 * IFuelSource.java
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

import it.zerono.mods.extremereactors.api.reactor.Reactant;
import net.minecraft.util.math.BlockPos;

public interface IFuelSource<T> {

    /**
     * Return the fuel source items currently available in the fuel source
     *
     * @return the solid fuel source items
     */
    T getFuelStack();

    /**
     * Consume source items up to the amount indicated by the provided ItemStack
     *
     * @param sourceToConsume the source items to consume
     * @return the source items actually consumed
     */
    T consumeFuelSource(T sourceToConsume);

    /**
     * Try to emit a given amount of reactant as a solid item.
     * Will either match the item type already present, or will select whatever type allows the most reactant to be ejected right now
     *
     * @param reactant Type of reactant to emit.
     * @param amount
     * @return the amount of Reactant consumed in the operation
     */
    int emitReactant(Reactant reactant, int amount);

    /**
     * Return the position of the fuel source in the world
     * @return
     */
    BlockPos getWorldPosition();
}
