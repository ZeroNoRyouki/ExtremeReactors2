/*
 *
 * TurbineControllerContainer.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.container;

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.MultiblockTurbine;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.VentSetting;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineControllerEntity;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.item.inventory.container.ContainerFactory;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class TurbineControllerContainer
        extends ModTileContainer<TurbineControllerEntity> {

    public final BooleanData ACTIVE;
    public final FluidStackData COOLANT_STACK;
    public final FluidStackData VAPOR_STACK;
    public final FloatData RPM;
    public final WideAmountData ENERGY_STORED;
    public final WideAmountData ENERGY_GENERATED_LAST_TICK;
    public final IntData FLUID_CONSUMED_LAST_TICK;
    public final FloatData ROTOR_EFFICIENCY_LAST_TICK;
    public final BooleanData INDUCTOR_ENGAGED;
    public final EnumData<VentSetting> VENT_SETTINGS;

    public TurbineControllerContainer(final int windowId, final PlayerInventory playerInventory,
                                      final TurbineControllerEntity controller) {

        super(5, ContainerFactory.EMPTY, Content.ContainerTypes.TURBINE_CONTROLLER.get(), windowId, controller);

        final MultiblockTurbine turbine = controller.getMultiblockController().orElseThrow(IllegalStateException::new);
        final boolean isClientSide = turbine.getWorld().isClientSide();

        this._outputEnergySystem = turbine.getOutputEnergySystem();
        this._energyCapacity = WideAmount.from(turbine.getCapacity(this._outputEnergySystem, null));
        this._fluidCapacity = turbine.getFluidContainer().getCapacity();
        this._rotorBladesCount = turbine.getRotorBladesCount();
        this._baseFluidPerBlade = turbine.getVariant().getBaseFluidPerBlade();

        this.ACTIVE = BooleanData.of(this, isClientSide, () -> turbine::isMachineActive);
        this.COOLANT_STACK = turbine.getFluidContainer().getCoolantStackData(2, this, isClientSide);
        this.VAPOR_STACK = turbine.getFluidContainer().getVaporStackData(2, this, isClientSide);
        this.RPM = FloatData.of(this, isClientSide, () -> turbine::getRotorSpeed);
        this.ENERGY_STORED = WideAmountData.sampled(3, this, isClientSide,
                () -> () -> WideAmount.from(turbine.getEnergyStored(this.getOutputEnergySystem(), null)));
        this.ENERGY_GENERATED_LAST_TICK = WideAmountData.of(this, isClientSide,
                () -> () -> WideAmount.from(turbine.getEnergyGeneratedLastTick()));
        this.FLUID_CONSUMED_LAST_TICK = IntData.of(this, isClientSide, () -> turbine::getFluidConsumedLastTick);
        this.ROTOR_EFFICIENCY_LAST_TICK = FloatData.of(this, isClientSide, () -> turbine::getRotorEfficiencyLastTick);
        this.INDUCTOR_ENGAGED = BooleanData.of(this, isClientSide, () -> turbine::isInductorEngaged);
        this.VENT_SETTINGS = EnumData.of(this, isClientSide, VentSetting.class, () -> turbine::getVentSetting);
    }

    public TurbineControllerContainer(final int windowId, final PlayerInventory playerInventory,
                                        final PacketBuffer networkData) {
        this(windowId, playerInventory, AbstractModBlockEntity.getGuiClientBlockEntity(networkData));
    }

    public EnergySystem getOutputEnergySystem() {
        return this._outputEnergySystem;
    }

    public WideAmount getEnergyCapacity() {
        return this._energyCapacity;
    }

    public int getFluidCapacity() {
        return this._fluidCapacity;
    }

    public int getRotorBladesCount() {
        return this._rotorBladesCount;
    }

    public int getBaseFluidPerBlade() {
        return this._baseFluidPerBlade;
    }

    //region internals

    private final EnergySystem _outputEnergySystem;
    private final WideAmount _energyCapacity;
    private final int _fluidCapacity;
    private final int _rotorBladesCount;
    private final int _baseFluidPerBlade;

    //endregion
}
