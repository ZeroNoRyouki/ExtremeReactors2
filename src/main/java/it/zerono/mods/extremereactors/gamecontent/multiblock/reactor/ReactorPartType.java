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
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.*;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorFuelRodBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorRedstonePortBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
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

public enum ReactorPartType
        implements IMultiblockPartType {

    Casing(() -> Content.TileEntityTypes.REACTOR_CASING::get,
            MultiblockPartBlock::new,
            "part.bigreactors.reactor.casing"),

    Glass(() -> Content.TileEntityTypes.REACTOR_GLASS::get,
            GlassBlock::new, "part.bigreactors.reactor.glass",
            ReactorPartType::notOpaqueBlock),

    Controller(() -> Content.TileEntityTypes.REACTOR_CONTROLLER::get,
            GenericDeviceBlock::new, "part.bigreactors.reactor.controller"),

    FuelRod(() -> Content.TileEntityTypes.REACTOR_FUELROD::get,
            ReactorFuelRodBlock::new, "part.bigreactors.reactor.fuelrod",
            bp -> ReactorPartType.notOpaqueBlock(bp)
                    .setLightLevel(state -> Config.COMMON.reactor.fuelRodLightValue.get())
                    .tickRandomly()),

    ControlRod(() -> Content.TileEntityTypes.REACTOR_CONTROLROD::get,
            GenericDeviceBlock::new, "part.bigreactors.reactor.controlrod"),

    SolidAccessPort(() -> Content.TileEntityTypes.REACTOR_SOLID_ACCESSPORT::get,
            IOPortBlock::new, "part.bigreactors.reactor.solidaccessport"),

    ActiveCoolantPortForge(() -> Content.TileEntityTypes.REACTOR_COOLANTPORT_FORGE_ACTIVE::get,
            IOPortBlock::new, "part.bigreactors.reactor.coolantport_forge_active"),

    PassiveCoolantPortForge(() -> Content.TileEntityTypes.REACTOR_COOLANTPORT_FORGE_PASSIVE::get,
            IOPortBlock::new, "part.bigreactors.reactor.coolantport_forge_passive"),

    ActiveCoolantPortMekanism(() -> null,//Content.TileEntityTypes.REACTOR_COOLANTPORT_MEKANISM_ACTIVE::get, //TODO mekanism coolant port
            IOPortBlock::new, "part.bigreactors.reactor.coolantport_mekanism_active"),

    PassiveCoolantPortMekanism(() -> null,//Content.TileEntityTypes.REACTOR_COOLANTPORT_MEKANISM_PASSIVE::get, //TODO mekanism coolant port
            IOPortBlock::new, "part.bigreactors.reactor.coolantport_mekanism_passive"),

    CreativeCoolantPort(
            () -> () -> null, //TODO fix!
            IOPortBlock::new, "part.bigreactors.reactor.creativecoolantport"),

    ActivePowerTapFE(() -> Content.TileEntityTypes.REACTOR_POWERTAP_FE_ACTIVE::get,
            PowerTapBlock::new, "part.bigreactors.reactor.powertap_fe_active"),

    PassivePowerTapFE(() -> Content.TileEntityTypes.REACTOR_POWERTAP_FE_PASSIVE::get,
            PowerTapBlock::new, "part.bigreactors.reactor.powertap_fe_passive"),

    ComputerPort(() -> Content.TileEntityTypes.REACTOR_COMPUTERPORT::get,
            ComputerPortBlock::new, "part.bigreactors.reactor.computerport"),

    RedstonePort(() -> Content.TileEntityTypes.REACTOR_REDSTONEPORT::get,
            ReactorRedstonePortBlock::new, "part.bigreactors.reactor.redstoneport"),
    ;

    ReactorPartType(final Supplier<Supplier<TileEntityType<?>>> tileTypeSupplier,
                    final Function<MultiblockPartBlock.MultiblockPartProperties<ReactorPartType>,
                            MultiblockPartBlock<MultiblockReactor, ReactorPartType>> blockFactory,
                    final String translationKey) {
        this(tileTypeSupplier, blockFactory, translationKey, bp -> bp);
    }

    ReactorPartType(final Supplier<Supplier<TileEntityType<?>>> tileTypeSupplier,
                    final Function<MultiblockPartBlock.MultiblockPartProperties<ReactorPartType>,
                            MultiblockPartBlock<MultiblockReactor, ReactorPartType>> blockFactory,
                    final String translationKey,
                    final Function<Block.Properties, Block.Properties> blockPropertiesFixer) {

        this._tileTypeSupplier = tileTypeSupplier;
        this._blockFactory = blockFactory;
        this._translationKey = translationKey;
        this._blockPropertiesFixer = blockPropertiesFixer;
    }

//    public boolean isTransparent() {
//        return this == Glass || this == FuelRod;
//    }

    public MultiblockPartBlock<MultiblockReactor, ReactorPartType> createBlock(ReactorVariant variant) {
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

//    private static String getBlockIdFor(final ReactorPartType partType, ReactorVariant variant) {
//        return ExtremeReactors.MOD_ID + ":" + variant.getName() + "_reactor" + partType.getNameForId();
//    }

    private final Supplier<Supplier<TileEntityType<?>>> _tileTypeSupplier;

    private final Function<MultiblockPartBlock.MultiblockPartProperties<ReactorPartType>,
            MultiblockPartBlock<MultiblockReactor, ReactorPartType>> _blockFactory;

    private final String _translationKey;

    private final Function<Block.Properties, Block.Properties> _blockPropertiesFixer;

    //endregion
}
