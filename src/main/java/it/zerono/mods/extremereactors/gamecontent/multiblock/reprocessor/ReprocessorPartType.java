/*
 *
 * ReprocessorPartType.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor;

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.GenericDeviceBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.GlassBlock;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartType;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock;
import it.zerono.mods.zerocore.lib.multiblock.IMultiblockPart;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public enum ReprocessorPartType
        implements IMultiblockPartType {

    Casing(() -> Content.TileEntityTypes.REPROCESSOR_CASING::get,
            MultiblockPartBlock::new, "block.bigreactors.reprocessorcasing"),

    Glass(() -> Content.TileEntityTypes.REPROCESSOR_GLASS::get,
            GlassBlock::new, "block.bigreactors.reprocessorglass", GlassBlock::addGlassProperties),

    Controller(() -> Content.TileEntityTypes.REPROCESSOR_CONTROLLER::get,
            GenericDeviceBlock::new, "block.bigreactors.reprocessorcontroller"),

    WasteInjector(() -> Content.TileEntityTypes.REPROCESSOR_WASTEINJECTOR::get,
            GenericDeviceBlock::new, "block.bigreactors.reprocessorwasteinjector"),

    FluidInjector(() -> Content.TileEntityTypes.REPROCESSOR_FLUIDINJECTOR::get,
            GenericDeviceBlock::new, "block.bigreactors.reprocessorfluidinjector"),

    OutputPort(() -> Content.TileEntityTypes.REPROCESSOR_OUTPUTPORT::get,
            GenericDeviceBlock::new, "block.bigreactors.reprocessoroutputport"),

    PowerPort(() -> Content.TileEntityTypes.REPROCESSOR_POWERPORT::get,
            GenericDeviceBlock::new, "block.bigreactors.reprocessorpowerport"),

    Collector(() -> Content.TileEntityTypes.REPROCESSOR_COLLECTOR::get,
            GenericDeviceBlock::new, "block.bigreactors.reprocessorcollector"),
    ;

    ReprocessorPartType(final Supplier<Supplier<TileEntityType<?>>> tileTypeSupplier,
                        final Function<MultiblockPartBlock.MultiblockPartProperties<ReprocessorPartType>,
                                MultiblockPartBlock<MultiblockReprocessor, ReprocessorPartType>> blockFactory,
                        final String translationKey) {
        this(tileTypeSupplier, blockFactory, translationKey, bp -> bp);
    }

    ReprocessorPartType(final Supplier<Supplier<TileEntityType<?>>> tileTypeSupplier,
                        final Function<MultiblockPartBlock.MultiblockPartProperties<ReprocessorPartType>,
                                MultiblockPartBlock<MultiblockReprocessor, ReprocessorPartType>> blockFactory,
                        final String translationKey,
                        final Function<Block.Properties, Block.Properties> blockPropertiesFixer) {

        this._tileTypeSupplier = tileTypeSupplier;
        this._blockFactory = blockFactory;
        this._translationKey = translationKey;
        this._blockPropertiesFixer = blockPropertiesFixer;
    }

    public MultiblockPartBlock<MultiblockReprocessor, ReprocessorPartType> createBlock() {
        return this._blockFactory.apply(MultiblockPartBlock.MultiblockPartProperties.create(this,
                this._blockPropertiesFixer.apply(IMultiblockPart.getDefaultBlockProperties())));
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

    private final Supplier<Supplier<TileEntityType<?>>> _tileTypeSupplier;

    private final Function<MultiblockPartBlock.MultiblockPartProperties<ReprocessorPartType>,
            MultiblockPartBlock<MultiblockReprocessor, ReprocessorPartType>> _blockFactory;

    private final String _translationKey;

    private final Function<Block.Properties, Block.Properties> _blockPropertiesFixer;

    //endregion
}
