/*
 *
 * IIOPortHandler.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common.part;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public interface IIOPortHandler {

    /**
     * @return true if this handler is connected to one of it's allowed consumers, false otherwise
     */
    boolean isConnected();

    /**
     * Check for connections
     *
     * @param world the handler world
     * @param position the handler position
     */
    void checkConnections(@Nullable IWorldReader world, BlockPos position);

    /**
     * Get the requested capability, if supported
     *
     * @param capability the capability
     * @param direction the direction the request is coming from
     * @param <T> the type of the capability
     * @return the capability (if supported) or null (if not)
     */
    @Nullable
    default <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction direction) {
        return null;
    }

    /**
     * @return true if this handler is in Active mode, false otherwise
     */
    boolean isActive();

    /**
     * @return true if this handler is in Passive mode, false otherwise
     */
    boolean isPassive();

    void invalidate();

    default void update() {
    }
}
