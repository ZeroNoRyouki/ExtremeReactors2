/*
 *
 * AbstractReactorEntity.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part;

import it.zerono.mods.extremereactors.api.radiation.RadiationPacket;
import it.zerono.mods.extremereactors.api.reactor.IHeatEntity;
import it.zerono.mods.extremereactors.api.reactor.radiation.IRadiationModerator;
import it.zerono.mods.extremereactors.api.reactor.radiation.IrradiationData;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.MultiblockReactor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactorPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.network.UpdateClientsFuelRodsLayout;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.IMultiblockReactorVariant;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartTypeProvider;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockVariantProvider;
import it.zerono.mods.zerocore.lib.client.model.data.multiblock.CuboidPartVariantsModelData;
import it.zerono.mods.zerocore.lib.client.model.data.multiblock.CuboidPartVariantsModelDataCache;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.lib.multiblock.variant.IMultiblockVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

public abstract class AbstractReactorEntity
        extends AbstractMultiblockEntity<MultiblockReactor>
        implements IHeatEntity, IRadiationModerator, IMultiblockPartTypeProvider<MultiblockReactor, ReactorPartType>,
                    IMultiblockVariantProvider<IMultiblockReactorVariant> {

    public AbstractReactorEntity(final BlockEntityType<?> type, final BlockPos position, final BlockState blockState) {
        super(type, position, blockState);
    }

    protected boolean isReactorActive() {
        return this.getMultiblockController()
                .filter(MultiblockReactor::isAssembled)
                .map(MultiblockReactor::isMachineActive)
                .orElse(false);
    }

    protected void setReactorActive(boolean active) {
        this.getMultiblockController()
                .filter(MultiblockReactor::isAssembled)
                .ifPresent(c -> c.setMachineActive(active));
    }

    public Component getPartDisplayName() {
        return new TranslatableComponent("gui.bigreactors.multiblock_variant_part_format.title",
                new TranslatableComponent(this.getMultiblockVariant().map(IMultiblockVariant::getTranslationKey).orElse("unknown")),
                new TranslatableComponent(this.getPartType().map(ReactorPartType::getTranslationKey).orElse("unknown")));
    }

    //region client render support

    @Override
    protected IModelData getUpdatedModelData() {
        return CodeHelper.optionalMap(this.getMultiblockVariant(), this.getPartType(), this::getUpdatedModelData)
                .orElse(EmptyModelData.INSTANCE);
    }

    protected int getUpdatedModelVariantIndex() {
        return 0;
    }

    public void onUpdateClientsFuelRodsLayout(final UpdateClientsFuelRodsLayout message) {
        this.executeOnController(c -> c.onUpdateClientsFuelRodsLayout(message));
    }

    //endregion
    //region IHeatEntity

    /**
     * @return The amount of heat in the entity, in Celsius.
     */
    @Override
    public double getHeat() {
        return this.evalOnController(c -> c.getFuelHeatValue().getAsDouble(), 0.0);
    }

    /**
     * The thermal conductivity of the entity.
     * This is the amount of heat (in C) that this entity transfers
     * over a unit area (1x1 square) in one tick, per degree-C difference.
     * (Yes, I know centigrade != joules, it's an abstraction)
     *
     * @return Thermal conductivity constant, see above.
     */
    @Override
    public double getThermalConductivity() {
        return IHeatEntity.CONDUCTIVITY_IRON;
    }

    //endregion
    //region IRadiationModerator

    @Override
    public void moderateRadiation(IrradiationData irradiationData, RadiationPacket radiation) {
        //TODO leak radiation !!!
        // Discard all remaining radiation, sorry bucko
        radiation.intensity = 0f;
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
    public MultiblockReactor createController() {

        final Level myWorld = this.getLevel();

        if (null == myWorld) {
            throw new RuntimeException("Trying to create a Controller from a Part without a Level");
        }

        return new MultiblockReactor(this.getLevel(), this.getMultiblockVariant().orElse(ReactorVariant.Basic));
    }

    /**
     * Retrieve the type of multiblock controller which governs this part.
     * Used to ensure that incompatible multiblocks are not merged.
     *
     * @return The class/type of the multiblock controller which governs this type of part.
     */
    @Override
    public Class<MultiblockReactor> getControllerType() {
        return MultiblockReactor.class;
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

    protected IModelData getUpdatedModelData(final IMultiblockReactorVariant variant, final ReactorPartType partType) {
        return getVariantModelDataCache(variant).computeIfAbsent(partType.ordinal(), this.getUpdatedModelVariantIndex(),
                this.getOutwardFacings(),
                () -> new CuboidPartVariantsModelData(partType.ordinal(), this.getUpdatedModelVariantIndex(),
                        this.getOutwardFacings()));
    }

    private static CuboidPartVariantsModelDataCache getVariantModelDataCache(final IMultiblockReactorVariant variant) {

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
