/*
 *
 * IPowerTapHandler.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.powertap;

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.AbstractGeneratorMultiblockController;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockGeneratorVariant;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockPart;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public interface IPowerTapHandler<Controller extends AbstractGeneratorMultiblockController<Controller, V>,
            V extends IMultiblockGeneratorVariant> {

    static <Controller extends AbstractGeneratorMultiblockController<Controller, V>, V extends IMultiblockGeneratorVariant>
    IPowerTapHandler<Controller, V> create(final EnergySystem system,
                                           final IoMode mode,
                                           final AbstractCuboidMultiblockPart<Controller> part) {

        switch (system) {

            case ForgeEnergy:
                return new PowerTapHandlerFE<>(part, mode);

            default:
                throw new IllegalArgumentException("Unsupported energy system: " + system);
        }
    }

    /**
     * Get the {@link EnergySystem} supported by this IPowerTapHandler
     *
     * @return the supported {@link EnergySystem}
     */
    EnergySystem getEnergySystem();

    /**
     * Send energy to the connected consumer (if there is one)
     *
     * @param amount amount of energy to send
     * @return the amount of energy accepted by the consumer
     */
    double outputEnergy(double amount);

    /**
     * @return true if this handler is connected to one of it's allowed consumers, false otherwise
     */
    boolean isConnected();

    /**
     * Check for connections
     *
     * @param world the PowerTap world
     * @param position the PowerTap position
     */
    void checkConnections(@Nullable IBlockReader world, BlockPos position);

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

    boolean isActive();

    boolean isPassive();
}
