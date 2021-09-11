/*
 *
 * AbstractReprocessorEntity.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.part;

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.AbstractMultiblockEntity;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.MultiblockReprocessor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.ReprocessorPartType;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartTypeProvider;
import it.zerono.mods.zerocore.lib.client.model.data.multiblock.CuboidPartVariantsModelData;
import it.zerono.mods.zerocore.lib.client.model.data.multiblock.CuboidPartVariantsModelDataCache;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.energy.IWideEnergyStorage;
import it.zerono.mods.zerocore.lib.energy.NullEnergyHandlers;
import it.zerono.mods.zerocore.lib.fluid.FluidHelper;
import it.zerono.mods.zerocore.lib.item.ItemHelper;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.EmptyHandler;

public abstract class AbstractReprocessorEntity
        extends AbstractMultiblockEntity<MultiblockReprocessor>
        implements IMultiblockPartTypeProvider<MultiblockReprocessor, ReprocessorPartType> {

    public AbstractReprocessorEntity(final TileEntityType<?> type) {
        super(type);
    }

    protected boolean isReprocessorActive() {
        return this.getMultiblockController()
                .filter(MultiblockReprocessor::isAssembled)
                .map(MultiblockReprocessor::isMachineActive)
                .orElse(false);
    }

    protected void setReprocessorActive(boolean active) {
        this.getMultiblockController()
                .filter(MultiblockReprocessor::isAssembled)
                .ifPresent(c -> c.setMachineActive(active));
    }

    public IItemHandlerModifiable getItemInventory(final IoDirection direction) {
        return this.evalOnController(r -> r.getItemHandler(direction), ItemHelper.EMPTY_ITEM_HANDLER);
    }

    public IFluidHandler getFluidInventory() {
        return this.evalOnController(MultiblockReprocessor::getFluidHandler, FluidHelper.EMPTY_FLUID_HANDLER);
    }

    public IWideEnergyStorage getEnergyStorage() {
        return this.evalOnController(MultiblockReprocessor::getEnergyStorage, NullEnergyHandlers.STORAGE);
    }

    public ITextComponent getPartDisplayName() {
        return new TranslationTextComponent(this.getPartType().map(ReprocessorPartType::getTranslationKey).orElse("unknown"));
    }

    public boolean isValidIngredient(final ItemStack stack) {
        return this.evalOnController(r -> r.isValidIngredient(stack), false);
    }

    public boolean isValidIngredient(final FluidStack stack) {
        return this.evalOnController(r -> r.isValidIngredient(stack), false);
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

        // Most Reprocessor parts are not allowed on the frame or inside the multiblock
        // so reject those positions and allow all the other ones

        if (position.isFrame()) {

            validatorCallback.setLastError(this.getWorldPosition(), "multiblock.validation.reprocessor.invalid_frame_block");
            return false;

        } else if (PartPosition.Interior == position) {

            validatorCallback.setLastError(this.getWorldPosition(), "multiblock.validation.reprocessor.invalid_part_for_interior");
            return false;

        } else if (position.isFace()) {

            validatorCallback.setLastError(this.getWorldPosition(), "multiblock.validation.reprocessor.invalid_part_for_face");
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
    public MultiblockReprocessor createController() {

        final World myWorld = this.getWorld();

        if (null == myWorld) {
            throw new RuntimeException("Trying to create a Controller from a Part without a World");
        }

        return new MultiblockReprocessor(this.getWorld());
    }

    /**
     * Retrieve the type of multiblock controller which governs this part.
     * Used to ensure that incompatible multiblocks are not merged.
     *
     * @return The class/type of the multiblock controller which governs this type of part.
     */
    @Override
    public Class<MultiblockReprocessor> getControllerType() {
        return MultiblockReprocessor.class;
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

    protected IModelData getUpdatedModelData(final ReprocessorPartType partType) {
        return s_modelDataCaches.computeIfAbsent(partType.ordinal(), this.getUpdatedModelVariantIndex(), this.getOutwardFacings(),
                () -> new CuboidPartVariantsModelData(partType.ordinal(), this.getUpdatedModelVariantIndex(), this.getOutwardFacings()));
    }

    private static CuboidPartVariantsModelDataCache s_modelDataCaches = new CuboidPartVariantsModelDataCache();

    //endregion
}
