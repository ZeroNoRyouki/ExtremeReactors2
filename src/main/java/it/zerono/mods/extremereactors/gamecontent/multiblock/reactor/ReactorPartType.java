/*
 *
 * ReactorPartType.java
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

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.PassiveFluidPortBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.PowerTapBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorFluidAccessPortEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorFuelRodBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorRedstonePortBlock;
import it.zerono.mods.zerocore.base.BaseHelper;
import it.zerono.mods.zerocore.base.multiblock.part.GenericDeviceBlock;
import it.zerono.mods.zerocore.base.multiblock.part.GlassBlock;
import it.zerono.mods.zerocore.base.multiblock.part.io.IOPortBlock;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartTypeProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public enum ReactorPartType
        implements IReactorPartType {

    Casing(() -> Content.TileEntityTypes.REACTOR_CASING::get,
            MultiblockPartBlock::new),

    Glass(() -> Content.TileEntityTypes.REACTOR_GLASS::get,
            GlassBlock::new, GlassBlock::addGlassProperties),

    Controller(() -> Content.TileEntityTypes.REACTOR_CONTROLLER::get,
            GenericDeviceBlock::new, "part.bigreactors.reactor.controller"),

    FuelRod(() -> Content.TileEntityTypes.REACTOR_FUELROD::get,
            ReactorFuelRodBlock::new,
            bp -> GlassBlock.addGlassProperties(bp).lightLevel(state -> 15)),

    ControlRod(() -> Content.TileEntityTypes.REACTOR_CONTROLROD::get,
            GenericDeviceBlock::new, "part.bigreactors.reactor.controlrod"),

    SolidAccessPort(() -> Content.TileEntityTypes.REACTOR_SOLID_ACCESSPORT::get,
            IOPortBlock::new, "part.bigreactors.reactor.solidaccessport"),

    FluidAccessPort(() -> Content.TileEntityTypes.REACTOR_FLUID_ACCESSPORT::get,
            IOPortBlock::new, "part.bigreactors.reactor.fluidaccessport", bp -> bp,
            partProperties -> partProperties.setAsStackStorable(ReactorFluidAccessPortEntity::itemTooltipBuilder)),

    ActiveFluidPortForge(() -> Content.TileEntityTypes.REACTOR_FLUIDPORT_FORGE_ACTIVE::get,
            IOPortBlock::new, "part.bigreactors.reactor.fluidport_forge_active"),

    PassiveFluidPortForge(() -> Content.TileEntityTypes.REACTOR_FLUIDPORT_FORGE_PASSIVE::get,
            PassiveFluidPortBlock::new, "part.bigreactors.reactor.fluidport_forge_passive"),

//    ActiveFluidPortMekanism(() -> null,//Content.TileEntityTypes.REACTOR_FLUIDPORT_MEKANISM_ACTIVE::get, //TODO mekanism Fluid port
//            IOPortBlock::new, "part.bigreactors.reactor.fluidport_mekanism_active"),

    PassiveFluidPortMekanism(() -> Content.TileEntityTypes.REACTOR_FLUIDPORT_MEKANISM_PASSIVE::get,
            PassiveFluidPortBlock::new),

    CreativeWaterGenerator(() -> Content.TileEntityTypes.REACTOR_CREATIVE_WATER_GENERATOR::get,
            GenericDeviceBlock::new),

    ActivePowerTapFE(() -> Content.TileEntityTypes.REACTOR_POWERTAP_FE_ACTIVE::get,
            PowerTapBlock::new),

    PassivePowerTapFE(() -> Content.TileEntityTypes.REACTOR_POWERTAP_FE_PASSIVE::get,
            PowerTapBlock::new),

    ComputerPort(() -> Content.TileEntityTypes.REACTOR_COMPUTERPORT::get,
            GenericDeviceBlock::new),

    RedstonePort(() -> Content.TileEntityTypes.REACTOR_REDSTONEPORT::get,
            ReactorRedstonePortBlock::new, "part.bigreactors.reactor.redstoneport"),

    ChargingPortFE(() -> Content.TileEntityTypes.REACTOR_CHARGINGPORT_FE::get,
            GenericDeviceBlock::new, "part.bigreactors.reactor.chargingport_fe"),
    ;

    ReactorPartType(final Supplier<@NotNull Supplier<@NotNull BlockEntityType<?>>> tileTypeSupplier,
                    final Function<MultiblockPartBlock.@NotNull MultiblockPartProperties<IReactorPartType>,
                            @NotNull MultiblockPartBlock<MultiblockReactor, IReactorPartType>> blockFactory) {
        this(tileTypeSupplier, blockFactory, BaseHelper.EMPTY_TRANSLATION_KEY, bp -> bp);
    }

    ReactorPartType(final Supplier<@NotNull Supplier<@NotNull BlockEntityType<?>>> tileTypeSupplier,
                    final Function<MultiblockPartBlock.@NotNull MultiblockPartProperties<IReactorPartType>,
                            @NotNull MultiblockPartBlock<MultiblockReactor, IReactorPartType>> blockFactory,
                    final String translationKey) {
        this(tileTypeSupplier, blockFactory, translationKey, bp -> bp);
    }

    ReactorPartType(final Supplier<@NotNull Supplier<@NotNull BlockEntityType<?>>> tileTypeSupplier,
                    final Function<MultiblockPartBlock.@NotNull MultiblockPartProperties<IReactorPartType>,
                            @NotNull MultiblockPartBlock<MultiblockReactor, IReactorPartType>> blockFactory,
                    final Function<Block.@NotNull Properties, Block.@NotNull Properties> blockPropertiesFixer) {
        this(tileTypeSupplier, blockFactory, BaseHelper.EMPTY_TRANSLATION_KEY, blockPropertiesFixer, ep -> ep);
    }

    ReactorPartType(final Supplier<@NotNull Supplier<@NotNull BlockEntityType<?>>> tileTypeSupplier,
                    final Function<MultiblockPartBlock.@NotNull MultiblockPartProperties<IReactorPartType>,
                            @NotNull MultiblockPartBlock<MultiblockReactor, IReactorPartType>> blockFactory,
                    final String translationKey,
                    final Function<Block.@NotNull Properties, Block.@NotNull Properties> blockPropertiesFixer) {
        this(tileTypeSupplier, blockFactory, translationKey, blockPropertiesFixer, ep -> ep);
    }

    ReactorPartType(final Supplier<@NotNull Supplier<@NotNull BlockEntityType<?>>> tileTypeSupplier,
                    final Function<MultiblockPartBlock.@NotNull MultiblockPartProperties<IReactorPartType>,
                            @NotNull MultiblockPartBlock<MultiblockReactor, IReactorPartType>> blockFactory,
                    final String translationKey,
                    final Function<Block.@NotNull Properties, Block.@NotNull Properties> blockPropertiesFixer,
                    final Function<MultiblockPartBlock.@NotNull MultiblockPartProperties<IReactorPartType>,
                            MultiblockPartBlock.@NotNull MultiblockPartProperties<IReactorPartType>> partPropertiesFixer) {
        this._properties = new MultiblockPartTypeProperties<>(tileTypeSupplier, blockFactory, translationKey,
                blockPropertiesFixer, partPropertiesFixer);
    }

    //region IMultiblockPartType2

    @Override
    public MultiblockPartTypeProperties<MultiblockReactor, IReactorPartType> getPartTypeProperties() {
        return this._properties;
    }

    @Override
    public String getSerializedName() {
        return this.name();
    }

    //endregion
    //region internals

    private final MultiblockPartTypeProperties<MultiblockReactor, IReactorPartType> _properties;

    //endregion
}
