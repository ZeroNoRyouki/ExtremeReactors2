/*
 *
 * TurbinePartType.java
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

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.*;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineRedstonePortBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineRotorBearingBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineRotorComponentBlock;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartType2;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartTypeProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.common.util.NonNullSupplier;

public enum TurbinePartType
        implements IMultiblockPartType2<MultiblockTurbine, TurbinePartType> {

    Casing(() -> Content.TileEntityTypes.TURBINE_CASING::get,
            MultiblockPartBlock::new,
            "part.bigreactors.turbine.casing"),

    Glass(() -> Content.TileEntityTypes.TURBINE_GLASS::get,
            GlassBlock::new, "part.bigreactors.turbine.glass",
            GlassBlock::addGlassProperties),

    Controller(() -> Content.TileEntityTypes.TURBINE_CONTROLLER::get,
            GenericDeviceBlock::new, "part.bigreactors.turbine.controller"),

    RotorBearing(() -> Content.TileEntityTypes.TURBINE_ROTORBEARING::get,
            TurbineRotorBearingBlock::new, "part.bigreactors.turbine.rotorbearing",
            bp -> bp.lightLevel(state -> 15)),

    RotorShaft(() -> Content.TileEntityTypes.TURBINE_ROTORSHAFT::get,
            TurbineRotorComponentBlock::shaft, "part.bigreactors.turbine.rotorshaft",
            TurbinePartType::rotorBlock),

    RotorBlade(() -> Content.TileEntityTypes.TURBINE_ROTORBLADE::get,
            TurbineRotorComponentBlock::blade, "part.bigreactors.turbine.rotorblade",
            TurbinePartType::rotorBlock),

    ActiveFluidPortForge(() -> Content.TileEntityTypes.TURBINE_FLUIDPORT_FORGE_ACTIVE::get,
            IOPortBlock::new, "part.bigreactors.turbine.fluidport_forge_active"),

    PassiveFluidPortForge(() -> Content.TileEntityTypes.TURBINE_FLUIDPORT_FORGE_PASSIVE::get,
            PassiveFluidPortBlock::new, "part.bigreactors.turbine.fluidport_forge_passive"),

//    ActiveFluidPortMekanism(() -> null,//Content.TileEntityTypes.TURBINE_FLUIDPORT_MEKANISM_ACTIVE::get, //TODO mekanism fluid port
//            IOPortBlock::new, "part.bigreactors.turbine.fluidport_mekanism_active"),
//
//    PassiveFluidPortMekanism(() -> null,//Content.TileEntityTypes.TURBINE_FLUIDPORT_MEKANISM_PASSIVE::get, //TODO mekanism fluid port
//            PassiveFluidPortBlock::new, "part.bigreactors.turbine.fluidport_mekanism_passive"),

    CreativeSteamGenerator(() -> Content.TileEntityTypes.TURBINE_CREATIVE_STEAM_GENERATOR::get,
            GenericDeviceBlock::new, "part.bigreactors.turbine.creativesteamgenerator"),

    ActivePowerTapFE(() -> Content.TileEntityTypes.TURBINE_POWERTAP_FE_ACTIVE::get,
            PowerTapBlock::new, "part.bigreactors.turbine.powertap_fe_active"),

    PassivePowerTapFE(() -> Content.TileEntityTypes.TURBINE_POWERTAP_FE_PASSIVE::get,
            PowerTapBlock::new, "part.bigreactors.turbine.powertap_fe_passive"),

    ComputerPort(() -> Content.TileEntityTypes.TURBINE_COMPUTERPORT::get,
            GenericDeviceBlock::new, "part.bigreactors.turbine.computerport"),

    ChargingPortFE(() -> Content.TileEntityTypes.TURBINE_CHARGINGPORT_FE::get,
            GenericDeviceBlock::new, "part.bigreactors.turbine.chargingport_fe"),

    RedstonePort(() -> Content.TileEntityTypes.TURBINE_REDSTONEPORT::get,
            TurbineRedstonePortBlock::new, "part.bigreactors.turbine.redstoneport"),
    ;

    TurbinePartType(final NonNullSupplier<NonNullSupplier<BlockEntityType<?>>> tileTypeSupplier,
                    final NonNullFunction<MultiblockPartBlock.MultiblockPartProperties<TurbinePartType>,
                                                MultiblockPartBlock<MultiblockTurbine, TurbinePartType>> blockFactory,
                    final String translationKey) {
        this(tileTypeSupplier, blockFactory, translationKey, bp -> bp);
    }

    TurbinePartType(final NonNullSupplier<NonNullSupplier<BlockEntityType<?>>> tileTypeSupplier,
                    final NonNullFunction<MultiblockPartBlock.MultiblockPartProperties<TurbinePartType>,
                            MultiblockPartBlock<MultiblockTurbine, TurbinePartType>> blockFactory,
                    final String translationKey,
                    final NonNullFunction<Block.Properties, Block.Properties> blockPropertiesFixer) {
        this._properties = new MultiblockPartTypeProperties<>(tileTypeSupplier, blockFactory, translationKey, blockPropertiesFixer);
    }

    //region IMultiblockPartType2

    @Override
    public MultiblockPartTypeProperties<MultiblockTurbine, TurbinePartType> getPartTypeProperties() {
        return this._properties;
    }

    @Override
    public String getSerializedName() {
        return this.name();
    }

    //endregion
    //region internals

    private static BlockBehaviour.Properties rotorBlock(final BlockBehaviour.Properties originals) {
        return originals
                .sound(SoundType.GLASS)
                .noOcclusion()
                .isRedstoneConductor((blockState, blockReader, pos) -> false)
                .isViewBlocking((blockState, blockReader, pos) -> false);
    }
//    private static BlockBehaviour.Properties rotorBlock(BlockBehaviour.Properties originals) {
//        return originals
//                .noOcclusion()
//                .isRedstoneConductor((blockState, blockReader, pos) -> false)
//                .isViewBlocking((blockState, blockReader, pos) -> false)
//                .lightLevel(state -> 15);
//    }

    private final MultiblockPartTypeProperties<MultiblockTurbine, TurbinePartType> _properties;

    //endregion
}
