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
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.PassiveFluidPortBlock;
import it.zerono.mods.zerocore.base.multiblock.part.GenericDeviceBlock;
import it.zerono.mods.zerocore.base.multiblock.part.GlassBlock;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartTypeProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public enum ReprocessorPartType
        implements IReprocessorPartType {

    Casing(() -> Content.TileEntityTypes.REPROCESSOR_CASING::get,
            MultiblockPartBlock::new, "block.bigreactors.reprocessorcasing"),

    Glass(() -> Content.TileEntityTypes.REPROCESSOR_GLASS::get,
            GlassBlock::new, "block.bigreactors.reprocessorglass", GlassBlock::addGlassProperties),

    Controller(() -> Content.TileEntityTypes.REPROCESSOR_CONTROLLER::get,
            GenericDeviceBlock::new, "block.bigreactors.reprocessorcontroller"),

    WasteInjector(() -> Content.TileEntityTypes.REPROCESSOR_WASTEINJECTOR::get,
            GenericDeviceBlock::new, "block.bigreactors.reprocessorwasteinjector"),

    FluidInjector(() -> Content.TileEntityTypes.REPROCESSOR_FLUIDINJECTOR::get,
            PassiveFluidPortBlock::new, "block.bigreactors.reprocessorfluidinjector"),

    OutputPort(() -> Content.TileEntityTypes.REPROCESSOR_OUTPUTPORT::get,
            GenericDeviceBlock::new, "block.bigreactors.reprocessoroutputport"),

    PowerPort(() -> Content.TileEntityTypes.REPROCESSOR_POWERPORT::get,
            GenericDeviceBlock::new, "block.bigreactors.reprocessorpowerport"),

    Collector(() -> Content.TileEntityTypes.REPROCESSOR_COLLECTOR::get,
            GenericDeviceBlock::new, "block.bigreactors.reprocessorcollector",
            bp -> bp.lightLevel(bs -> 15)),
    ;

    ReprocessorPartType(final Supplier<@NotNull Supplier<@NotNull BlockEntityType<?>>> tileTypeSupplier,
                        final Function<MultiblockPartBlock.@NotNull MultiblockPartProperties<IReprocessorPartType>,
                                @NotNull MultiblockPartBlock<MultiblockReprocessor, IReprocessorPartType>> blockFactory,
                        final String translationKey) {
        this(tileTypeSupplier, blockFactory, translationKey, bp -> bp);
    }

    ReprocessorPartType(final Supplier<@NotNull Supplier<@NotNull BlockEntityType<?>>> tileTypeSupplier,
                        final Function<MultiblockPartBlock.@NotNull MultiblockPartProperties<IReprocessorPartType>,
                                @NotNull MultiblockPartBlock<MultiblockReprocessor, IReprocessorPartType>> blockFactory,
                        final String translationKey,
                        final Function<Block.@NotNull Properties, Block.@NotNull Properties> blockPropertiesFixer) {
        this._properties = new MultiblockPartTypeProperties<>(tileTypeSupplier, blockFactory, translationKey, blockPropertiesFixer);
    }

    //region IMultiblockPartType2

    @Override
    public MultiblockPartTypeProperties<MultiblockReprocessor, IReprocessorPartType> getPartTypeProperties() {
        return this._properties;
    }

    @Override
    public String getSerializedName() {
        return this.name();
    }

    //endregion
    //region internals

    private final MultiblockPartTypeProperties<MultiblockReprocessor, IReprocessorPartType> _properties;

    //endregion
}
