/*
 *
 * TurbineComputerPeripheral.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.computer;

import com.google.common.collect.Maps;
import it.zerono.mods.extremereactors.gamecontent.multiblock.IMachineReader;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.MultiblockTurbine;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.VentSetting;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineComputerPortEntity;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.compat.computer.ComputerMethod;
import it.zerono.mods.zerocore.lib.compat.computer.LuaHelper;
import it.zerono.mods.zerocore.lib.compat.computer.MultiblockComputerPeripheral;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.NonNullConsumer;

import java.util.Map;
import java.util.Optional;

public class TurbineComputerPeripheral
        extends MultiblockComputerPeripheral<MultiblockTurbine, TurbineComputerPortEntity> {

    public TurbineComputerPeripheral(final TurbineComputerPortEntity computerPort) {
        super(computerPort);
    }

    //region MultiblockComputerPeripheral

    /**
     * Get the name of this ComputerPeripheral
     *
     * @return the name
     */
    @Override
    public String getPeripheralStaticName() {
        return "extremereactor-turbineComputerPort";
    }

    /**
     * Collect the methods provided by this ComputerPeripheral
     *
     * @param methodConsumer pass your methods to this Consumer
     */
    @Override
    public void populateMethods(final NonNullConsumer<ComputerMethod<MultiblockComputerPeripheral<MultiblockTurbine, TurbineComputerPortEntity>>> methodConsumer) {

        super.populateMethods(methodConsumer);

        methodConsumer.accept(new ComputerMethod<>("getVariant", wrapControllerValue(c -> c.getVariant().getName()))); //NEW

        methodConsumer.accept(new ComputerMethod<>("getActive", wrapControllerValue(IMachineReader::isMachineActive)));

        methodConsumer.accept(new ComputerMethod<>("getEnergyProducedLastTick", wrapControllerValue(MultiblockTurbine::getEnergyGeneratedLastTick)));

        methodConsumer.accept(new ComputerMethod<>("getEnergyStored", wrapControllerValue(c -> c.getEnergyStored(c.getOutputEnergySystem(), null))));

        methodConsumer.accept(new ComputerMethod<>("getEnergyStoredAsText", wrapControllerValue(c -> { //NEW

            final EnergySystem sys = c.getOutputEnergySystem();

            return CodeHelper.formatAsHumanReadableNumber(c.getEnergyStored(sys, null), sys.getUnit());
        })));

        methodConsumer.accept(new ComputerMethod<>("getFluidAmountMax", wrapControllerValue(c -> c.getCapacity())));

        methodConsumer.accept(new ComputerMethod<>("getFluidFlowRate", wrapControllerValue(MultiblockTurbine::getFluidConsumedLastTick)));

        methodConsumer.accept(new ComputerMethod<>("getFluidFlowRateMax", wrapControllerValue(MultiblockTurbine::getMaxIntakeRate)));

        methodConsumer.accept(new ComputerMethod<>("getFluidFlowRateMaxMax", wrapControllerValue(MultiblockTurbine::getMaxIntakeRateHardLimit)));

        methodConsumer.accept(new ComputerMethod<>("getInputAmount", wrapControllerValue(c -> c.getFluidContainer().getGasAmount())));

        methodConsumer.accept(new ComputerMethod<>("getInputType", wrapControllerValue(c -> getFluidName(c.getFluidContainer().getGas()))));

        methodConsumer.accept(new ComputerMethod<>("getOutputAmount", wrapControllerValue(c -> c.getFluidContainer().getLiquidAmount())));

        methodConsumer.accept(new ComputerMethod<>("getOutputType", wrapControllerValue(c -> getFluidName(c.getFluidContainer().getLiquid()))));

        methodConsumer.accept(new ComputerMethod<>("getRotorSpeed", wrapControllerValue(MultiblockTurbine::getRotorSpeed)));

        methodConsumer.accept(new ComputerMethod<>("getNumberOfBlades", wrapControllerValue(MultiblockTurbine::getRotorBladesCount)));

        methodConsumer.accept(new ComputerMethod<>("getBladeEfficiency", wrapControllerValue(c -> c.getRotorEfficiencyLastTick() * 100.0f)));

        methodConsumer.accept(new ComputerMethod<>("getRotorMass", wrapControllerValue(MultiblockTurbine::getRotorMass)));

        methodConsumer.accept(new ComputerMethod<>("getInductorEngaged", wrapControllerValue(MultiblockTurbine::isInductorEngaged)));

        methodConsumer.accept(new ComputerMethod<>("getEnergyCapacity", wrapControllerValue(c -> c.getCapacity(c.getOutputEnergySystem(), null))));

        methodConsumer.accept(new ComputerMethod<>("getEnergyStats", wrapControllerValue(c -> {

            final Map<String, Object> stats = Maps.newHashMap();
            final EnergySystem sys = c.getOutputEnergySystem();

            stats.put("energyStored", c.getEnergyStored(sys, null));
            stats.put("energyCapacity", c.getCapacity(sys, null));
            stats.put("energyProducedLastTick", c.getEnergyGeneratedLastTick());
            stats.put("energySystem", sys.getUnit()); //NEW

            return stats;

        })));

        methodConsumer.accept(new ComputerMethod<>("setActive", wrapControllerAction((c, arguments) ->
                c.setMachineActive(LuaHelper.getBooleanFromArgs(arguments, 0))), 1, true));

        methodConsumer.accept(new ComputerMethod<>("setFluidFlowRateMax", wrapControllerAction((c, arguments) ->
                c.setMaxIntakeRate(LuaHelper.getIntFromArgs(arguments, 0))), 1, true));

        methodConsumer.accept(new ComputerMethod<>("setVentNone", wrapControllerAction(c -> c.setVentSetting(VentSetting.DoNotVent)), 0, true));

        methodConsumer.accept(new ComputerMethod<>("setVentOverflow", wrapControllerAction(c -> c.setVentSetting(VentSetting.VentOverflow)), 0, true));

        methodConsumer.accept(new ComputerMethod<>("setVentAll", wrapControllerAction(c -> c.setVentSetting(VentSetting.VentAll)), 0, true));

        methodConsumer.accept(new ComputerMethod<>("setInductorEngaged", wrapControllerAction((c, arguments) ->
                c.setInductorEngaged(LuaHelper.getBooleanFromArgs(arguments, 0))), 1, true));
    }

    //endregion
    //region Methods

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static String getFluidName(final Optional<Fluid> fluid) {
        return fluid.map(f -> new TranslationTextComponent(f.getAttributes().getTranslationKey()).getString()).orElse("");
    }

    //endregion
}

