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

import it.zerono.mods.extremereactors.config.Config;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.PassiveFluidPortBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.PowerTapBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorFluidAccessPortEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorFuelRodBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorRedstonePortBlock;
import it.zerono.mods.zerocore.base.multiblock.part.GenericDeviceBlock;
import it.zerono.mods.zerocore.base.multiblock.part.GlassBlock;
import it.zerono.mods.zerocore.base.multiblock.part.io.IOPortBlock;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartType2;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartTypeProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.common.util.NonNullSupplier;

public enum ReactorPartType
        implements IMultiblockPartType2<MultiblockReactor, ReactorPartType> {

    Casing(() -> Content.TileEntityTypes.REACTOR_CASING::get,
            MultiblockPartBlock::new,
            "part.bigreactors.reactor.casing"),

    Glass(() -> Content.TileEntityTypes.REACTOR_GLASS::get,
            GlassBlock::new, "part.bigreactors.reactor.glass",
            GlassBlock::addGlassProperties),

    Controller(() -> Content.TileEntityTypes.REACTOR_CONTROLLER::get,
            GenericDeviceBlock::new, "part.bigreactors.reactor.controller"),

    FuelRod(() -> Content.TileEntityTypes.REACTOR_FUELROD::get,
            ReactorFuelRodBlock::new, "part.bigreactors.reactor.fuelrod",
            bp -> GlassBlock.addGlassProperties(bp).lightLevel(state -> Config.COMMON.reactor.fuelRodLightValue.get())),

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
            PassiveFluidPortBlock::new, "part.bigreactors.reactor.fluidport_mekanism_passive"),

    CreativeWaterGenerator(() -> Content.TileEntityTypes.REACTOR_CREATIVE_WATER_GENERATOR::get,
            GenericDeviceBlock::new, "part.bigreactors.reactor.creativewatergenerator"),

    ActivePowerTapFE(() -> Content.TileEntityTypes.REACTOR_POWERTAP_FE_ACTIVE::get,
            PowerTapBlock::new, "part.bigreactors.reactor.powertap_fe_active"),

    PassivePowerTapFE(() -> Content.TileEntityTypes.REACTOR_POWERTAP_FE_PASSIVE::get,
            PowerTapBlock::new, "part.bigreactors.reactor.powertap_fe_passive"),

    ComputerPort(() -> Content.TileEntityTypes.REACTOR_COMPUTERPORT::get,
            GenericDeviceBlock::new, "part.bigreactors.reactor.computerport"),

    RedstonePort(() -> Content.TileEntityTypes.REACTOR_REDSTONEPORT::get,
            ReactorRedstonePortBlock::new, "part.bigreactors.reactor.redstoneport"),

    ChargingPortFE(() -> Content.TileEntityTypes.REACTOR_CHARGINGPORT_FE::get,
            GenericDeviceBlock::new, "part.bigreactors.reactor.chargingport_fe"),
    ;

    ReactorPartType(final NonNullSupplier<NonNullSupplier<BlockEntityType<?>>> tileTypeSupplier,
                    final NonNullFunction<MultiblockPartBlock.MultiblockPartProperties<ReactorPartType>,
                            MultiblockPartBlock<MultiblockReactor, ReactorPartType>> blockFactory,
                    final String translationKey) {
        this(tileTypeSupplier, blockFactory, translationKey, bp -> bp);
    }

    ReactorPartType(final NonNullSupplier<NonNullSupplier<BlockEntityType<?>>> tileTypeSupplier,
                    final NonNullFunction<MultiblockPartBlock.MultiblockPartProperties<ReactorPartType>,
                                                MultiblockPartBlock<MultiblockReactor, ReactorPartType>> blockFactory,
                    final String translationKey,
                    final NonNullFunction<Block.Properties, Block.Properties> blockPropertiesFixer) {
        this(tileTypeSupplier, blockFactory, translationKey, blockPropertiesFixer, ep -> ep);
    }

    ReactorPartType(final NonNullSupplier<NonNullSupplier<BlockEntityType<?>>> tileTypeSupplier,
                    final NonNullFunction<MultiblockPartBlock.MultiblockPartProperties<ReactorPartType>,
                            MultiblockPartBlock<MultiblockReactor, ReactorPartType>> blockFactory,
                    final String translationKey,
                    final NonNullFunction<Block.Properties, Block.Properties> blockPropertiesFixer,
                    final NonNullFunction<MultiblockPartBlock.MultiblockPartProperties<ReactorPartType>, MultiblockPartBlock.MultiblockPartProperties<ReactorPartType>> partPropertiesFixer) {
        this._properties = new MultiblockPartTypeProperties<>(tileTypeSupplier, blockFactory, translationKey, blockPropertiesFixer, partPropertiesFixer);
    }

    //region IMultiblockPartType2

    @Override
    public MultiblockPartTypeProperties<MultiblockReactor, ReactorPartType> getPartTypeProperties() {
        return this._properties;
    }

    @Override
    public String getSerializedName() {
        return this.name();
    }

    //endregion
    //region internals

    private final MultiblockPartTypeProperties<MultiblockReactor, ReactorPartType> _properties;

    //endregion
}
