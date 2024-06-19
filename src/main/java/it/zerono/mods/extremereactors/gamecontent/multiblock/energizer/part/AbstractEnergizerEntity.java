/*
 * AbstractEnergizerEntity
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.part;

import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.IEnergizerPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.MultiBlockEnergizer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.variant.IMultiblockEnergizerVariant;
import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockMachineEntity;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartType;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartTypeProvider;
import it.zerono.mods.zerocore.lib.client.model.data.multiblock.CuboidPartVariantsModelDataCache;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class AbstractEnergizerEntity
        extends AbstractMultiblockMachineEntity<MultiBlockEnergizer, IMultiblockEnergizerVariant>
        implements IMultiblockPartTypeProvider<MultiBlockEnergizer, IEnergizerPartType> {

    public AbstractEnergizerEntity(BlockEntityType<?> type, BlockPos position, BlockState blockState) {
        super(type, position, blockState);
    }

    protected boolean isEnergizerActive() {
        return this.getMultiblockController()
                .filter(MultiBlockEnergizer::isAssembled)
                .map(MultiBlockEnergizer::isMachineActive)
                .orElse(false);
    }

    protected void setEnergizerActive(boolean active) {
        this.getMultiblockController()
                .filter(MultiBlockEnergizer::isAssembled)
                .ifPresent(c -> c.setMachineActive(active));
    }

    public Component getPartDisplayName() {
        return Component.translatable(this.getPartType().map(IEnergizerPartType::getTranslationKey).orElse("unknown"));
    }

    //region client render support

    @Override
    protected ModelData getUpdatedModelData() {
        return CodeHelper.optionalMap(this.getMultiblockVariant(), this.getPartType(), this::getUpdatedModelData)
                .orElse(ModelData.EMPTY);
    }

    protected int getUpdatedModelVariantIndex() {
        return 0;
    }

    //endregion
    //region AbstractMultiblockEntity

    @Override
    public boolean isGoodForPosition(PartPosition position, IMultiblockValidator validatorCallback) {

        // Most Energizer parts are not allowed on the Frame an inside the Energizer so reject those positions and allow all the other ones

        final BlockPos coordinates = this.getWorldPosition();

        if (position.isFrame()) {

            validatorCallback.setLastError(coordinates, "multiblock.validation.energizer.invalid_frame_block");
            return false;

        } else if (PartPosition.Interior == position) {

            validatorCallback.setLastError(coordinates, "multiblock.validation.energizer.invalid_part_for_interior");
            return false;
        }

        return true;
    }

    //endregion
    //region AbstractCuboidMultiblockPart

    @Override
    public MultiBlockEnergizer createController() {

        final Level myWorld = this.getLevel();

        if (null == myWorld) {
            throw new RuntimeException("Trying to create a Controller from a Part without a Level");
        }

        return new MultiBlockEnergizer(myWorld);
    }

    @Override
    public Class<MultiBlockEnergizer> getControllerType() {
        return MultiBlockEnergizer.class;
    }

    @Override
    public void onMachineActivated() {
    }

    @Override
    public void onMachineDeactivated() {
    }

    //endregion
    //region client render support

    protected ModelData getUpdatedModelData(IMultiblockEnergizerVariant variant, IMultiblockPartType partType) {
        return s_modelDataCache.computeIfAbsent(partType.getByteHashCode(), this.getUpdatedModelVariantIndex(),
                this.getOutwardFacings());
    }

    private static final CuboidPartVariantsModelDataCache s_modelDataCache = new CuboidPartVariantsModelDataCache();

    //endregion
}
