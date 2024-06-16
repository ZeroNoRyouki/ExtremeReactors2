/*
 *
 * AbstractTurbineEntity.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part;

import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.ITurbinePartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.MultiblockTurbine;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.IMultiblockTurbineVariant;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockMachineEntity;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartTypeProvider;
import it.zerono.mods.zerocore.lib.client.model.data.multiblock.CuboidPartVariantsModelDataCache;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.lib.multiblock.variant.IMultiblockVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class AbstractTurbineEntity
        extends AbstractMultiblockMachineEntity<MultiblockTurbine, IMultiblockTurbineVariant>
        implements IMultiblockPartTypeProvider<MultiblockTurbine, ITurbinePartType> {

    public AbstractTurbineEntity(final BlockEntityType<?> type, final BlockPos position, final BlockState blockState) {
        super(type, position, blockState);
    }

    protected boolean isTurbineActive() {
        return this.getMultiblockController()
                .filter(MultiblockTurbine::isAssembled)
                .map(MultiblockTurbine::isMachineActive)
                .orElse(false);
    }

    protected void setTurbineActive(boolean active) {
        this.getMultiblockController()
                .filter(MultiblockTurbine::isAssembled)
                .ifPresent(c -> c.setMachineActive(active));
    }

    public Component getPartDisplayName() {
        return Component.translatable("gui.bigreactors.multiblock_variant_part_format.title",
                Component.translatable(this.getMultiblockVariant().map(IMultiblockVariant::getTranslationKey).orElse("unknown")),
                Component.translatable(this.getPartType().map(ITurbinePartType::getTranslationKey).orElse("unknown")));
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

        // Most Reactor parts are not allowed on the Frame an inside the Reactor so reject those positions and allow all the other ones

        final BlockPos coordinates = this.getWorldPosition();

        if (position.isFrame()) {

            validatorCallback.setLastError(coordinates, "multiblock.validation.reactor.invalid_frame_block");
            return false;

        } else if (PartPosition.Interior == position) {

            validatorCallback.setLastError(coordinates, "multiblock.validation.reactor.invalid_part_for_interior");
            return false;
        }

        return true;
    }

    //endregion
    //region AbstractCuboidMultiblockPart

    /**
     * Factory method. Creates a new multiblock controller and returns it.
     * Does not attach this tile entity to it.
     * Override this in your game code!
     *
     * @return A new Multiblock Controller
     */
    @Override
    public MultiblockTurbine createController() {

        final Level myWorld = this.getLevel();

        if (null == myWorld) {
            throw new RuntimeException("Trying to create a Controller from a Part without a Level");
        }

        return new MultiblockTurbine(this.getLevel(), this.getMultiblockVariant().orElse(TurbineVariant.Basic));
    }

    /**
     * Retrieve the type of multiblock controller which governs this part.
     * Used to ensure that incompatible multiblocks are not merged.
     *
     * @return The class/type of the multiblock controller which governs this type of part.
     */
    @Override
    public Class<MultiblockTurbine> getControllerType() {
        return MultiblockTurbine.class;
    }

    /**
     * Called when the user activates the machine. This is not called by default, but is included
     * as most machines have this game-logical concept.
     */
    @Override
    public void onMachineActivated() {
    }

    /**
     * Called when the user deactivates the machine. This is not called by default, but is included
     * as most machines have this game-logical concept.
     */
    @Override
    public void onMachineDeactivated() {
    }

    //endregion
    //region client render support

    protected ModelData getUpdatedModelData(final IMultiblockTurbineVariant variant, final ITurbinePartType partType) {
        return getVariantModelDataCache(variant).computeIfAbsent(partType.getByteHashCode(), this.getUpdatedModelVariantIndex(),
                this.getOutwardFacings());
    }

    private static CuboidPartVariantsModelDataCache getVariantModelDataCache(final IMultiblockTurbineVariant variant) {

        if (null == s_modelDataCaches) {
            s_modelDataCaches = new CuboidPartVariantsModelDataCache[ReactorVariant.values().length];
        }

        if (null == s_modelDataCaches[variant.getId()]) {
            s_modelDataCaches[variant.getId()] = new CuboidPartVariantsModelDataCache();
        }

        return s_modelDataCaches[variant.getId()];
    }

    private static CuboidPartVariantsModelDataCache[] s_modelDataCaches;

    //endregion
}
