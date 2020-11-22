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
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineRotorBearingBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineRotorComponentBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartType;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public enum TurbinePartType
        implements IMultiblockPartType {

    Casing(() -> Content.TileEntityTypes.TURBINE_CASING::get,
            MultiblockPartBlock::new,
            "part.bigreactors.turbine.casing"),

    Glass(() -> Content.TileEntityTypes.TURBINE_GLASS::get,
            GlassBlock::new, "part.bigreactors.turbine.glass",
            TurbinePartType::notOpaqueBlock),

    Controller(() -> Content.TileEntityTypes.TURBINE_CONTROLLER::get,
            GenericDeviceBlock::new, "part.bigreactors.turbine.controller"),

    RotorBearing(() -> Content.TileEntityTypes.TURBINE_ROTORBEARING::get,
            TurbineRotorBearingBlock::new, "part.bigreactors.turbine.rotorbearing",
            bp -> bp.setLightLevel(state -> 15)),

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

    ActiveFluidPortMekanism(() -> null,//Content.TileEntityTypes.TURBINE_FLUIDPORT_MEKANISM_ACTIVE::get, //TODO mekanism fluid port
            IOPortBlock::new, "part.bigreactors.turbine.fluidport_mekanism_active"),

    PassiveFluidPortMekanism(() -> null,//Content.TileEntityTypes.TURBINE_FLUIDPORT_MEKANISM_PASSIVE::get, //TODO mekanism fluid port
            PassiveFluidPortBlock::new, "part.bigreactors.turbine.fluidport_mekanism_passive"),

    CreativeSteamGenerator(() -> Content.TileEntityTypes.TURBINE_CREATIVE_STEAM_GENERATOR::get,
            GenericDeviceBlock::new, "part.bigreactors.turbine.creativesteamgenerator"),

    ActivePowerTapFE(() -> Content.TileEntityTypes.TURBINE_POWERTAP_FE_ACTIVE::get,
            PowerTapBlock::new, "part.bigreactors.turbine.powertap_fe_active"),

    PassivePowerTapFE(() -> Content.TileEntityTypes.TURBINE_POWERTAP_FE_PASSIVE::get,
            PowerTapBlock::new, "part.bigreactors.turbine.powertap_fe_passive"),

    ComputerPort(() -> Content.TileEntityTypes.TURBINE_COMPUTERPORT::get,
            ComputerPortBlock::new, "part.bigreactors.turbine.computerport"),

//    //TODO new? implement?
//    RedstonePort(() -> Content.TileEntityTypes.TURBINE_REDSTONEPORT::get,
//            TurbineRedstonePortBlock::new, "part.bigreactors.turbine.redstoneport"),
    ;

    TurbinePartType(final Supplier<Supplier<TileEntityType<?>>> tileTypeSupplier,
                    final Function<MultiblockPartBlock.MultiblockPartProperties<TurbinePartType>,
                            MultiblockPartBlock<MultiblockTurbine, TurbinePartType>> blockFactory,
                    final String translationKey) {
        this(tileTypeSupplier, blockFactory, translationKey, bp -> bp);
    }

    TurbinePartType(final Supplier<Supplier<TileEntityType<?>>> tileTypeSupplier,
                    final Function<MultiblockPartBlock.MultiblockPartProperties<TurbinePartType>,
                            MultiblockPartBlock<MultiblockTurbine, TurbinePartType>> blockFactory,
                    final String translationKey,
                    final Function<Block.Properties, Block.Properties> blockPropertiesFixer) {

        this._tileTypeSupplier = tileTypeSupplier;
        this._blockFactory = blockFactory;
        this._translationKey = translationKey;
        this._blockPropertiesFixer = blockPropertiesFixer;
    }

    public MultiblockPartBlock<MultiblockTurbine, TurbinePartType> createBlock(TurbineVariant variant) {
        return this._blockFactory.apply(MultiblockPartBlock.MultiblockPartProperties.create(this,
                this._blockPropertiesFixer.apply(variant.getDefaultBlockProperties()))
                .variant(variant));
    }

    //region IMultiblockPartType

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return this._tileTypeSupplier.get().get().create();
    }

    @Override
    public String getTranslationKey() {
        return this._translationKey;
    }

    @Override
    public String getString() {
        return this.name();
    }

    //endregion
    //region internals

    private static AbstractBlock.Properties notOpaqueBlock(AbstractBlock.Properties originals) {
        return originals
                .sound(SoundType.GLASS)
                .notSolid()
                .setOpaque((blockState, blockReader, pos) -> false)
                .setBlocksVision((blockState, blockReader, pos) -> false);
    }

    private static AbstractBlock.Properties rotorBlock(AbstractBlock.Properties originals) {
        return originals
                .notSolid()
                .setOpaque((blockState, blockReader, pos) -> false)
                .setBlocksVision((blockState, blockReader, pos) -> false)
                .setLightLevel(state -> 15);
    }

    private final Supplier<Supplier<TileEntityType<?>>> _tileTypeSupplier;

    private final Function<MultiblockPartBlock.MultiblockPartProperties<TurbinePartType>,
            MultiblockPartBlock<MultiblockTurbine, TurbinePartType>> _blockFactory;

    private final String _translationKey;

    private final Function<Block.Properties, Block.Properties> _blockPropertiesFixer;

    //endregion
}
