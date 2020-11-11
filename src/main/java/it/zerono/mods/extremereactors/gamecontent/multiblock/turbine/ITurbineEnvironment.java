/*
 *
 * ITurbineEnvironment.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine;

import it.zerono.mods.extremereactors.api.turbine.CoilMaterial;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorComponentType;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public interface ITurbineEnvironment {

    boolean isSimulator();

    /**
     * Get a CoilMaterial from the Turbine internal volume
     *
     * @param position the position to look up. Must be inside the Turbine internal volume
     * @return the CoilMaterial at the requested position, if the position is valid and a CoilMaterial is found there
     */
    Optional<CoilMaterial> getCoilBlock(BlockPos position);

    RotorComponentType getRotorComponentTypeAt(BlockPos position);
}
