/*
 *
 * ReactorControllerContainer.java
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

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.OperationalMode;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.WasteEjectionSetting;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.container.data.ReactantStackData;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorControllerEntity;
import it.zerono.mods.zerocore.lib.block.AbstractModBlockEntity;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import it.zerono.mods.zerocore.lib.energy.EnergySystem;
import it.zerono.mods.zerocore.lib.item.inventory.container.ContainerFactory;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModTileContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class ReactorControllerContainer
        extends ModTileContainer<ReactorControllerEntity>
        implements IReactorControllerContainer {

    public ReactorControllerContainer(final int windowId, final PlayerInventory playerInventory,
                                      final ReactorControllerEntity controller) {

        super(3, ContainerFactory.EMPTY, Content.ContainerTypes.REACTOR_CONTROLLER.get(), windowId, controller);

        final MultiblockReactor reactor = controller.getMultiblockController().orElseThrow(IllegalStateException::new);
        final boolean isClientSide = reactor.getWorld().isClientSide();

        this._reactorMode = reactor.getOperationalMode();
        this._outputEnergySystem = reactor.getOutputEnergySystem();

        this._reactantCapacity = IntData.immutable(this, isClientSide, reactor.getCapacity());
        this._fluidCapacity = IntData.immutable(this, isClientSide, reactor.getFluidContainer().getCapacity());
        this._energyCapacity = WideAmountData.immutable(this, isClientSide,
                WideAmount.from(reactor.getCapacity(this._outputEnergySystem, null)));
        this._fuelRodsCount = IntData.immutable(this, isClientSide, reactor.getFuelRodsCount());

        this._active = BooleanData.of(this, isClientSide, () -> reactor::isMachineActive);
        this._wasteEjectionSetting = EnumData.of(this, isClientSide, WasteEjectionSetting.class, () -> reactor::getWasteEjectionMode);

        this._coreHeat = DoubleData.sampled(3, this, isClientSide, () -> () -> reactor.getFuelHeat().getAsDouble());
        this._casingHeat = DoubleData.sampled(3, this, isClientSide, () -> () -> reactor.getReactorHeat().getAsDouble());
        this._fuelConsumedLastTick = FloatData.of(this, isClientSide, () -> () -> reactor.getUiStats().getFuelConsumedLastTick());
        this._fuelRichness = FloatData.sampled(3, this, isClientSide, () -> () -> reactor.getUiStats().getFuelRichness());
        this._generatedLastTick = DoubleData.of(this, isClientSide, () -> () -> reactor.getUiStats().getAmountGeneratedLastTick());
        this._energyStored = WideAmountData.sampled(3, this, isClientSide,
                () -> () -> WideAmount.from(reactor.getEnergyStored(this.getOutputEnergySystem(), null)));
        this._fuelStack = reactor.getFuelContainer().getFuelStackData(2, this, isClientSide);
        this._wasteStack = reactor.getFuelContainer().getWasteStackData(2, this, isClientSide);
        this._coolantStack = reactor.getFluidContainer().getCoolantStackData(2, this, isClientSide);
        this._vaporStack = reactor.getFluidContainer().getVaporStackData(2, this, isClientSide);
    }

    public ReactorControllerContainer(final int windowId, final PlayerInventory playerInventory,
                                      final PacketBuffer networkData) {
        this(windowId, playerInventory, AbstractModBlockEntity.getGuiClientBlockEntity(networkData));
    }

    //region IReactorControllerContainer

    public OperationalMode getReactorMode() {
        return this._reactorMode;
    }

    public EnergySystem getOutputEnergySystem() {
        return this._outputEnergySystem;
    }

    @Override
    public BooleanData active() {
        return this._active;
    }

    @Override
    public WideAmountData energyCapacity() {
        return this._energyCapacity;
    }

    @Override
    public WideAmountData energyStored() {
        return this._energyStored;
    }

    @Override
    public DoubleData generatedLastTick() {
        return this._generatedLastTick;
    }

    @Override
    public FluidStackData coolantStack() {
        return this._coolantStack;
    }

    @Override
    public FluidStackData vaporStack() {
        return this._vaporStack;
    }

    @Override
    public DoubleData coreHeat() {
        return this._coreHeat;
    }

    @Override
    public DoubleData casingHeat() {
        return this._casingHeat;
    }

    @Override
    public FloatData fuelConsumedLastTick() {
        return this._fuelConsumedLastTick;
    }

    @Override
    public FloatData fuelRichness() {
        return this._fuelRichness;
    }

    @Override
    public IntData reactantCapacity() {
        return this._reactantCapacity;
    }

    @Override
    public ReactantStackData fuelStack() {
        return this._fuelStack;
    }

    @Override
    public ReactantStackData wasteStack() {
        return this._wasteStack;
    }

    @Override
    public EnumData<WasteEjectionSetting> wasteEjectionSetting() {
        return this._wasteEjectionSetting;
    }

    @Override
    public IntData fluidCapacity() {
        return this._fluidCapacity;
    }

    @Override
    public IntData fuelRodsCount() {
        return this._fuelRodsCount;
    }

    //endregion
    //region internals

    private final IntData _reactantCapacity;
    private final IntData _fluidCapacity;
    private final WideAmountData _energyCapacity;
    private final IntData _fuelRodsCount;
    private final OperationalMode _reactorMode;
    private final EnergySystem _outputEnergySystem;

    private final BooleanData _active;
    private final DoubleData _coreHeat;
    private final DoubleData _casingHeat;
    private final FloatData _fuelConsumedLastTick;
    private final FloatData _fuelRichness;
    private final DoubleData _generatedLastTick;
    private final WideAmountData _energyStored;
    private final ReactantStackData _fuelStack;
    private final ReactantStackData _wasteStack;
    private final FluidStackData _coolantStack;
    private final FluidStackData _vaporStack;
    private final EnumData<WasteEjectionSetting> _wasteEjectionSetting;

    //endregion
}
