/*
 *
 * AbstractFluidizerEntity.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part;

import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.FluidizerPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.MultiblockFluidizer;
import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartTypeProvider;
import it.zerono.mods.zerocore.lib.client.model.data.multiblock.CuboidPartVariantsModelData;
import it.zerono.mods.zerocore.lib.client.model.data.multiblock.CuboidPartVariantsModelDataCache;
import it.zerono.mods.zerocore.lib.energy.IWideEnergyStorage2;
import it.zerono.mods.zerocore.lib.energy.NullEnergyHandlers;
import it.zerono.mods.zerocore.lib.fluid.FluidHelper;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Objects;

public class AbstractFluidizerEntity
        extends AbstractMultiblockEntity<MultiblockFluidizer>
        implements IMultiblockPartTypeProvider<MultiblockFluidizer, FluidizerPartType> {

    public AbstractFluidizerEntity(final BlockEntityType<?> type, final BlockPos position, final BlockState blockState) {
        super(type, position, blockState);
    }

    protected boolean isFluidizerActive() {
        return this.getMultiblockController()
                .filter(MultiblockFluidizer::isAssembled)
                .map(MultiblockFluidizer::isMachineActive)
                .orElse(false);
    }

    protected void setFluidizerActive(boolean active) {
        this.getMultiblockController()
                .filter(MultiblockFluidizer::isAssembled)
                .ifPresent(c -> c.setMachineActive(active));
    }

    protected void onIngredientsChanged() {
        this.executeOnController(MultiblockFluidizer::onIngredientsChanged);
    }

    public IFluidHandler getFluidOutput() {
        return this.evalOnController(MultiblockFluidizer::getFluidHandler, FluidHelper.EMPTY_FLUID_HANDLER);
    }

    public IWideEnergyStorage2 getEnergyStorage() {
        return this.evalOnController(MultiblockFluidizer::getEnergyStorage, NullEnergyHandlers.WIDE_STORAGE);
    }

    public Component getPartDisplayName() {
        return new TranslatableComponent(this.getPartType().map(FluidizerPartType::getTranslationKey).orElse("unknown"));
    }

    //region client render support

    @Override
    protected IModelData getUpdatedModelData() {
        return this.getPartType().map(this::getUpdatedModelData).orElse(EmptyModelData.INSTANCE);
    }

    protected int getUpdatedModelVariantIndex() {
        return 0;
    }

    //endregion
    //region AbstractMultiblockEntity

    @Override
    public boolean isGoodForPosition(PartPosition position, IMultiblockValidator validatorCallback) {

        // Most Fluidizer parts are not allowed on the frame or inside the multiblock
        // so reject those positions and allow all the other ones

        if (position.isFrame()) {

            validatorCallback.setLastError(this.getWorldPosition(), "multiblock.validation.fluidizer.invalid_frame_block");
            return false;

        } else if (PartPosition.Interior == position) {

            validatorCallback.setLastError(this.getWorldPosition(), "multiblock.validation.fluidizer.invalid_part_for_interior");
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
    public MultiblockFluidizer createController() {
        return new MultiblockFluidizer(Objects.requireNonNull(this.getLevel(), "Trying to create a Controller from a Part without a Level"));
    }

    /**
     * Retrieve the type of multiblock controller which governs this part.
     * Used to ensure that incompatible multiblocks are not merged.
     *
     * @return The class/type of the multiblock controller which governs this type of part.
     */
    @Override
    public Class<MultiblockFluidizer> getControllerType() {
        return MultiblockFluidizer.class;
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

    protected IModelData getUpdatedModelData(final FluidizerPartType partType) {
        return s_modelDataCaches.computeIfAbsent(partType.ordinal(), this.getUpdatedModelVariantIndex(), this.getOutwardFacings(),
                () -> new CuboidPartVariantsModelData(partType.ordinal(), this.getUpdatedModelVariantIndex(), this.getOutwardFacings()));
    }

    private static final CuboidPartVariantsModelDataCache s_modelDataCaches = new CuboidPartVariantsModelDataCache();

    //endregion
}
