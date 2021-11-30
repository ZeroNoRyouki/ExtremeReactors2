/*
 *
 * FluidizerPartType.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer;

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.GenericDeviceBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.GlassBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part.FluidizerFluidInjectorEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part.FluidizerSolidInjectorEntity;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartType2;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartTypeProperties;
import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.common.util.NonNullSupplier;

public enum FluidizerPartType
        implements IMultiblockPartType2<MultiblockFluidizer, FluidizerPartType> {

    Casing(() -> Content.TileEntityTypes.FLUIDIZER_CASING::get,
            MultiblockPartBlock::new, "block.bigreactors.fluidizercasing"),

    Glass(() -> Content.TileEntityTypes.FLUIDIZER_GLASS::get,
            GlassBlock::new, "block.bigreactors.fluidizerglass", GlassBlock::addGlassProperties),

    Controller(() -> Content.TileEntityTypes.FLUIDIZER_CONTROLLER::get,
            GenericDeviceBlock::new, "block.bigreactors.fluidizercontroller"),

    SolidInjector(() -> Content.TileEntityTypes.FLUIDIZER_SOLIDINJECTOR::get,
            GenericDeviceBlock::new, "block.bigreactors.fluidizersolidinjector", bp -> bp,
            partProperties -> partProperties.setAsStackStorable(FluidizerSolidInjectorEntity::itemTooltipBuilder)),

    FluidInjector(() -> Content.TileEntityTypes.FLUIDIZER_FLUIDINJECTOR::get,
            GenericDeviceBlock::new, "block.bigreactors.fluidizerfluidinjector", bp -> bp,
            partProperties -> partProperties.setAsStackStorable(FluidizerFluidInjectorEntity::itemTooltipBuilder)),

    OutputPort(() -> Content.TileEntityTypes.FLUIDIZER_OUTPUTPORT::get,
            GenericDeviceBlock::new, "block.bigreactors.fluidizeroutputport"),

    PowerPort(() -> Content.TileEntityTypes.FLUIDIZER_POWERPORT::get,
            GenericDeviceBlock::new, "block.bigreactors.fluidizerpowerport"),

    ;

    FluidizerPartType(final NonNullSupplier<NonNullSupplier<BlockEntityType<?>>> tileTypeSupplier,
                      final NonNullFunction<MultiblockPartBlock.MultiblockPartProperties<FluidizerPartType>,
                              MultiblockPartBlock<MultiblockFluidizer, FluidizerPartType>> blockFactory,
                      final String translationKey) {
        this(tileTypeSupplier, blockFactory, translationKey, bp -> bp);
    }

    FluidizerPartType(final NonNullSupplier<NonNullSupplier<BlockEntityType<?>>> tileTypeSupplier,
                      final NonNullFunction<MultiblockPartBlock.MultiblockPartProperties<FluidizerPartType>,
                              MultiblockPartBlock<MultiblockFluidizer, FluidizerPartType>> blockFactory,
                      final String translationKey,
                      final NonNullFunction<Block.Properties, Block.Properties> blockPropertiesFixer) {
        this(tileTypeSupplier, blockFactory, translationKey, blockPropertiesFixer, ep -> ep);
    }

    FluidizerPartType(final NonNullSupplier<NonNullSupplier<BlockEntityType<?>>> tileTypeSupplier,
                      final NonNullFunction<MultiblockPartBlock.MultiblockPartProperties<FluidizerPartType>,
                              MultiblockPartBlock<MultiblockFluidizer, FluidizerPartType>> blockFactory,
                      final String translationKey,
                      final NonNullFunction<Block.Properties, Block.Properties> blockPropertiesFixer,
                      final NonNullFunction<MultiblockPartBlock.MultiblockPartProperties<FluidizerPartType>, MultiblockPartBlock.MultiblockPartProperties<FluidizerPartType>> partPropertiesFixer) {
        this._properties = new MultiblockPartTypeProperties<>(tileTypeSupplier, blockFactory, translationKey, blockPropertiesFixer, partPropertiesFixer);
    }

    //region IMultiblockPartType2

    @Override
    public MultiblockPartTypeProperties<MultiblockFluidizer, FluidizerPartType> getPartTypeProperties() {
        return this._properties;
    }

    @Override
    public String getSerializedName() {
        return this.name();
    }

    //endregion
    //region internals

    private final MultiblockPartTypeProperties<MultiblockFluidizer, FluidizerPartType> _properties;

    //endregion
}
