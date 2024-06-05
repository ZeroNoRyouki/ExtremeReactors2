/*
 *
 * ITurbinePartType.java
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
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.PassiveFluidPortBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.PowerTapBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineRedstonePortBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineRotorBearingBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineRotorComponentBlock;
import it.zerono.mods.zerocore.base.BaseHelper;
import it.zerono.mods.zerocore.base.multiblock.part.GenericDeviceBlock;
import it.zerono.mods.zerocore.base.multiblock.part.GlassBlock;
import it.zerono.mods.zerocore.base.multiblock.part.io.IOPortBlock;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartTypeProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.common.util.NonNullSupplier;

public enum TurbinePartType
        implements ITurbinePartType {

    Casing(() -> Content.TileEntityTypes.TURBINE_CASING::get,
            MultiblockPartBlock::new),

    Glass(() -> Content.TileEntityTypes.TURBINE_GLASS::get,
            GlassBlock::new, GlassBlock::addGlassProperties),

    Controller(() -> Content.TileEntityTypes.TURBINE_CONTROLLER::get,
            GenericDeviceBlock::new, "part.bigreactors.turbine.controller"),

    RotorBearing(() -> Content.TileEntityTypes.TURBINE_ROTORBEARING::get,
            TurbineRotorBearingBlock::new, bp -> bp.lightLevel(state -> 15)),

    RotorShaft(() -> Content.TileEntityTypes.TURBINE_ROTORSHAFT::get,
            TurbineRotorComponentBlock::shaft, TurbinePartType::rotorBlock),

    RotorBlade(() -> Content.TileEntityTypes.TURBINE_ROTORBLADE::get,
            TurbineRotorComponentBlock::blade, TurbinePartType::rotorBlock),

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
            GenericDeviceBlock::new),

    ActivePowerTapFE(() -> Content.TileEntityTypes.TURBINE_POWERTAP_FE_ACTIVE::get,
            PowerTapBlock::new),

    PassivePowerTapFE(() -> Content.TileEntityTypes.TURBINE_POWERTAP_FE_PASSIVE::get,
            PowerTapBlock::new),

    ComputerPort(() -> Content.TileEntityTypes.TURBINE_COMPUTERPORT::get,
            GenericDeviceBlock::new),

    ChargingPortFE(() -> Content.TileEntityTypes.TURBINE_CHARGINGPORT_FE::get,
            GenericDeviceBlock::new, "part.bigreactors.turbine.chargingport_fe"),

    RedstonePort(() -> Content.TileEntityTypes.TURBINE_REDSTONEPORT::get,
            TurbineRedstonePortBlock::new, "part.bigreactors.turbine.redstoneport"),
    ;


    TurbinePartType(final NonNullSupplier<NonNullSupplier<BlockEntityType<?>>> tileTypeSupplier,
                    final NonNullFunction<MultiblockPartBlock.MultiblockPartProperties<ITurbinePartType>,
                            MultiblockPartBlock<MultiblockTurbine, ITurbinePartType>> blockFactory) {
        this(tileTypeSupplier, blockFactory, BaseHelper.EMPTY_TRANSLATION_KEY, bp -> bp);
    }

    TurbinePartType(final NonNullSupplier<NonNullSupplier<BlockEntityType<?>>> tileTypeSupplier,
                    final NonNullFunction<MultiblockPartBlock.MultiblockPartProperties<ITurbinePartType>,
                            MultiblockPartBlock<MultiblockTurbine, ITurbinePartType>> blockFactory,
                    final String translationKey) {
        this(tileTypeSupplier, blockFactory, translationKey, bp -> bp);
    }

    TurbinePartType(final NonNullSupplier<NonNullSupplier<BlockEntityType<?>>> tileTypeSupplier,
                    final NonNullFunction<MultiblockPartBlock.MultiblockPartProperties<ITurbinePartType>,
                            MultiblockPartBlock<MultiblockTurbine, ITurbinePartType>> blockFactory,
                    final NonNullFunction<Block.Properties, Block.Properties> blockPropertiesFixer) {
        this(tileTypeSupplier, blockFactory, BaseHelper.EMPTY_TRANSLATION_KEY, blockPropertiesFixer, ep -> ep);
    }

    TurbinePartType(final NonNullSupplier<NonNullSupplier<BlockEntityType<?>>> tileTypeSupplier,
                    final NonNullFunction<MultiblockPartBlock.MultiblockPartProperties<ITurbinePartType>,
                            MultiblockPartBlock<MultiblockTurbine, ITurbinePartType>> blockFactory,
                    final String translationKey,
                    final NonNullFunction<Block.Properties, Block.Properties> blockPropertiesFixer) {
        this(tileTypeSupplier, blockFactory, translationKey, blockPropertiesFixer, ep -> ep);
    }

    TurbinePartType(final NonNullSupplier<NonNullSupplier<BlockEntityType<?>>> tileTypeSupplier,
                    final NonNullFunction<MultiblockPartBlock.MultiblockPartProperties<ITurbinePartType>,
                            MultiblockPartBlock<MultiblockTurbine, ITurbinePartType>> blockFactory,
                    final String translationKey,
                    final NonNullFunction<Block.Properties, Block.Properties> blockPropertiesFixer,
                    final NonNullFunction<MultiblockPartBlock.MultiblockPartProperties<ITurbinePartType>,
                            MultiblockPartBlock.MultiblockPartProperties<ITurbinePartType>> partPropertiesFixer) {
        this._properties = new MultiblockPartTypeProperties<>(tileTypeSupplier, blockFactory, translationKey,
                blockPropertiesFixer, partPropertiesFixer);
    }

    //region IMultiblockPartType2

    @Override
    public MultiblockPartTypeProperties<MultiblockTurbine, ITurbinePartType> getPartTypeProperties() {
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

    private final MultiblockPartTypeProperties<MultiblockTurbine, ITurbinePartType> _properties;

    //endregion
}
