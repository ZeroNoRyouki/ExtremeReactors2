/*
 * EnergizerPartType
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * Do not remove or edit this header
 *
 */

package it.zerono.mods.extremereactors.gamecontent.multiblock.energizer;

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.PowerTapBlock;
import it.zerono.mods.zerocore.base.multiblock.part.GenericDeviceBlock;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartBlock;
import it.zerono.mods.zerocore.lib.block.multiblock.MultiblockPartTypeProperties;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.common.util.NonNullSupplier;

public enum EnergizerPartType
        implements IEnergizerPartType {

    Casing(() -> Content.TileEntityTypes.ENERGIZER_CASING::get, MultiblockPartBlock::new),

    Controller(() -> Content.TileEntityTypes.ENERGIZER_CONTROLLER::get,
            GenericDeviceBlock::new, "block.bigreactors.energizercontroller"),

    PowerPortFE(() -> Content.TileEntityTypes.ENERGIZER_POWERPORT_FE::get,
            PowerTapBlock::new, "block.bigreactors.energizerpowerportfe"),

    ChargingPortFE(() -> Content.TileEntityTypes.ENERGIZER_CHARGINGPORT_FE::get,
            PowerTapBlock::new, "block.bigreactors.energizerchargingportfe"),

    StatusDisplay(() -> Content.TileEntityTypes.ENERGIZER_STATUS_DISPLAY::get, GenericDeviceBlock::new),

    // TODO: Computer port
    ;

    EnergizerPartType(NonNullSupplier<NonNullSupplier<BlockEntityType<?>>> tileTypeSupplier,
                      NonNullFunction<MultiblockPartBlock.MultiblockPartProperties<IEnergizerPartType>,
                              MultiblockPartBlock<MultiBlockEnergizer, IEnergizerPartType>> blockFactory) {
        this._properties = new MultiblockPartTypeProperties<>(tileTypeSupplier, blockFactory, "", bp -> bp);
    }

    EnergizerPartType(NonNullSupplier<NonNullSupplier<BlockEntityType<?>>> tileTypeSupplier,
                      NonNullFunction<MultiblockPartBlock.MultiblockPartProperties<IEnergizerPartType>,
                              MultiblockPartBlock<MultiBlockEnergizer, IEnergizerPartType>> blockFactory,
                      String translationKey) {
        this._properties = new MultiblockPartTypeProperties<>(tileTypeSupplier, blockFactory, translationKey, bp -> bp);
    }

    //region IMultiblockPartType2

    @Override
    public MultiblockPartTypeProperties<MultiBlockEnergizer, IEnergizerPartType> getPartTypeProperties() {
        return this._properties;
    }

    @Override
    public String getSerializedName() {
        return this.name();
    }

    //endregion
    //region internals

    private final MultiblockPartTypeProperties<MultiBlockEnergizer, IEnergizerPartType> _properties;

    //endregion
}
