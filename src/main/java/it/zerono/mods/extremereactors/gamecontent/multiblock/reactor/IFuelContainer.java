/*
 *
 * IFuelContainer.java
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

import java.util.Optional;

public interface IFuelContainer {

    Optional<Reactant> getFuel();

    Optional<Reactant> getWaste();

    int getFuelAmount();

    int getWasteAmount();

    void setCapacity(int capacity);

    float getFuelReactivity();

    void onIrradiation(float fuelUsed);

    default boolean isEmpty() {
        return 0 == this.getFuelAmount() + this.getWasteAmount();
    }
}
