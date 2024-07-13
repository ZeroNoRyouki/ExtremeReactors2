/*
 * EnergizerComputerPeripheral
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * Do not remove or edit this header
 *
 */

package it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.computer;

import com.google.common.collect.Maps;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.MultiBlockEnergizer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.part.EnergizerComputerPortEntity;
import it.zerono.mods.zerocore.lib.IActivableMachine;
import it.zerono.mods.zerocore.lib.compat.computer.ComputerMethod;
import it.zerono.mods.zerocore.lib.compat.computer.IComputerMethodHandler;
import it.zerono.mods.zerocore.lib.compat.computer.LuaHelper;
import it.zerono.mods.zerocore.lib.compat.computer.MultiblockComputerPeripheral;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import net.minecraftforge.common.util.NonNullConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.BiFunction;

public class EnergizerComputerPeripheral
        extends MultiblockComputerPeripheral<MultiBlockEnergizer, EnergizerComputerPortEntity> {

    public EnergizerComputerPeripheral(final EnergizerComputerPortEntity port) {
        super(port);
    }

    //region MultiblockComputerPeripheral

    /**
     * Get the name of this ComputerPeripheral
     *
     * @return the name
     */
    @Override
    public String getPeripheralStaticName() {
        return "extremereactor-energizerComputerPort";
    }

    /**
     * Collect the methods provided by this ComputerPeripheral
     *
     * @param methodConsumer pass your methods to this Consumer
     */
    @Override
    public void populateMethods(
            final NonNullConsumer<ComputerMethod<MultiblockComputerPeripheral<MultiBlockEnergizer, EnergizerComputerPortEntity>>> methodConsumer) {

        super.populateMethods(methodConsumer);

        methodConsumer.accept(new ComputerMethod<>("getActive",
                wrapControllerValue(IActivableMachine::isMachineActive)));

        methodConsumer.accept(new ComputerMethod<>("setActive", wrapControllerAction((c, arguments) ->
                c.setMachineActive(LuaHelper.getBooleanFromArgs(arguments, 0))), 1, true));

        this.addEnergyMethod(methodConsumer, "getEnergyStored", MultiBlockEnergizer::getEnergyStored);
        this.addEnergyMethod(methodConsumer, "getEnergyCapacity", MultiBlockEnergizer::getCapacity);
        this.addEnergyMethod(methodConsumer, "getEnergyIoLastTick", MultiBlockEnergizer::getEnergyIoRate);
        this.addEnergyMethod(methodConsumer, "getEnergyInsertedLastTick", MultiBlockEnergizer::getEnergyInsertedLastTick);
        this.addEnergyMethod(methodConsumer, "getEnergyExtractedLastTick", MultiBlockEnergizer::getEnergyExtractedLastTick);

        methodConsumer.accept(new ComputerMethod<>("getEnergyStats", wrapControllerValue(c -> {

            final Map<String, Object> stats = Maps.newHashMap();
            final EnergySystem sys = c.getOutputEnergySystem();

            stats.put("energyStored", c.getEnergyStored(sys).doubleValue());
            stats.put("energyCapacity", c.getCapacity(sys).doubleValue());
            stats.put("energyIoLastTick", c.getEnergyIoRate(sys).doubleValue());
            stats.put("energyInsertedLastTick", c.getEnergyInsertedLastTick(sys).doubleValue());
            stats.put("energyExtractedLastTick", c.getEnergyExtractedLastTick(sys).doubleValue());
            stats.put("energySystem", sys.getUnit());

            return stats;

        })));

        methodConsumer.accept(new ComputerMethod<>("getEnergyStatsAsText", wrapControllerValue(c -> {

            final Map<String, String> stats = Maps.newHashMap();
            final EnergySystem sys = c.getOutputEnergySystem();

            stats.put("energyStored", sys.asHumanReadableNumber(c.getEnergyStored(sys)));
            stats.put("energyCapacity", sys.asHumanReadableNumber(c.getCapacity(sys)));
            stats.put("energyIoLastTick", sys.asHumanReadableNumber(c.getEnergyIoRate(sys)));
            stats.put("energyInsertedLastTick", sys.asHumanReadableNumber(c.getEnergyInsertedLastTick(sys)));
            stats.put("energyExtractedLastTick", sys.asHumanReadableNumber(c.getEnergyExtractedLastTick(sys)));
            stats.put("energySystem", sys.getUnit());

            return stats;

        })));
    }

    //endregion
    //region method wrappers and helpers

    private void addEnergyMethod(NonNullConsumer<ComputerMethod<MultiblockComputerPeripheral<MultiBlockEnergizer, EnergizerComputerPortEntity>>> methodConsumer,
                                 String methodBaseName,
                                 BiFunction<@NotNull MultiBlockEnergizer, @NotNull EnergySystem, @NotNull WideAmount> getter) {

        methodConsumer.accept(new ComputerMethod<>(methodBaseName, this.energy(getter)));
        methodConsumer.accept(new ComputerMethod<>(methodBaseName + "AsText", this.energyAsText(getter)));
    }

    private IComputerMethodHandler<MultiblockComputerPeripheral<MultiBlockEnergizer, EnergizerComputerPortEntity>>
    energy(BiFunction<@NotNull MultiBlockEnergizer, @NotNull EnergySystem, @NotNull WideAmount> getter) {
        return wrapControllerValue(c -> getter.apply(c, c.getOutputEnergySystem()).doubleValue());
    }

    private IComputerMethodHandler<MultiblockComputerPeripheral<MultiBlockEnergizer, EnergizerComputerPortEntity>>
    energyAsText(BiFunction<@NotNull MultiBlockEnergizer, @NotNull EnergySystem, @NotNull WideAmount> getter) {
        return wrapControllerValue(c -> {

            final EnergySystem sys = c.getOutputEnergySystem();

            return sys.asHumanReadableNumber(getter.apply(c, sys));
        });
    }

    //endregion
}
