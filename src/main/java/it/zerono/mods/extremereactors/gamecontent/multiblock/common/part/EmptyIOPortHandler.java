/*
 *
 * EmptyIOPortHandler.java
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

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

import javax.annotation.Nullable;

public class EmptyIOPortHandler
        implements IIOPortHandler {

    //region IIOPortHandler

    /**
     * @return true if this handler is connected to one of it's allowed consumers, false otherwise
     */
    @Override
    public boolean isConnected() {
        return false;
    }

    /**
     * Check for connections
     *
     * @param world    the handler world
     * @param position the handler position
     */
    @Override
    public void checkConnections(@Nullable LevelReader world, BlockPos position) {
    }

    /**
     * @return true if this handler is in Active mode, false otherwise
     */
    @Override
    public boolean isActive() {
        return false;
    }

    /**
     * @return true if this handler is in Passive mode, false otherwise
     */
    @Override
    public boolean isPassive() {
        return false;
    }

    @Override
    public void invalidate() {
    }

    //endregion
}
