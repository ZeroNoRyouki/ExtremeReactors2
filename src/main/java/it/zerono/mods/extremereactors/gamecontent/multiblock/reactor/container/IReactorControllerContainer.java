/*
 *
 * IReactorControllerContainer.java
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

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.container.IGeneratorControllerContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.OperationalMode;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.WasteEjectionSetting;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.container.data.ReactantStackData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.DoubleData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.EnumData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.FloatData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.IntData;

public interface IReactorControllerContainer
        extends IGeneratorControllerContainer {

    OperationalMode getReactorMode();

    DoubleData coreHeat();

    DoubleData casingHeat();

    FloatData fuelConsumedLastTick();

    FloatData fuelRichness();

    IntData reactantCapacity();

    ReactantStackData fuelStack();

    ReactantStackData wasteStack();

    EnumData<WasteEjectionSetting> wasteEjectionSetting();

    IntData fluidCapacity();

    IntData fuelRodsCount();
}