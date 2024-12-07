/*
 * ReactorFuelRodModel
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
 * Do not remove or edit this header
 *
 */

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mojang.math.Transformation;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.unimi.dsi.fastutil.shorts.Short2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CachedSprites;
import it.zerono.mods.zerocore.lib.block.BlockFacings;
import it.zerono.mods.zerocore.lib.client.model.AbstractDynamicBakedModel;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.SimpleModelState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;

/**
 * An IBakedModel for a single variant/orientation of a Fuel Rod
 */
public class ReactorFuelRodModel
        extends AbstractDynamicBakedModel {

    public static int HORIZONTAL_MAX_STEPS = 10;
    public static int VERTICAL_MAX_STEPS = 12;

    protected ReactorFuelRodModel(Map<Direction.Axis, BakedModel> baseModels) {

        super(true, false);

        this._lock = new StampedLock();
        this._faceBakery = new FaceBakery();

        this._subTypes = new ModelSubType[baseModels.size()];
        baseModels.forEach((axis, model) -> this._subTypes[axis.ordinal()] = new ModelSubType(axis, model));
    }

    //region AbstractDynamicBakedModel

    @NotNull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand,
                                    @NotNull ModelData modelData, @Nullable RenderType renderType) {

        if (side != null || ReactorFuelRodModelData.isOccluded(modelData)) {
            return EMPTY_QUADS;
        }

        final ModelSubType modelSubType = this._subTypes[ReactorFuelRodModelData.getOrientation(modelData).ordinal()];
        final short modelIndex = ReactorFuelRodModelData.getModelKey(modelData);

        if (0 == modelIndex) {
            return modelSubType._baseModel.getQuads(state, null, rand, ModelData.EMPTY, renderType);
        }

        long lockStamp = this._lock.readLock();
        List<BakedQuad> quads = modelSubType._cachedQuads.get(modelIndex);

        if (null != quads) {

            this._lock.unlockRead(lockStamp);
            return quads;
        }

        do {

            final long writeLockStamp = this._lock.tryConvertToWriteLock(lockStamp);

            if (0L != writeLockStamp) {

                final List<BakedQuad> tempQuads = Lists.newLinkedList(modelSubType._baseModel.getQuads(state, null, rand, ModelData.EMPTY, renderType));

                tempQuads.addAll(this.buildQuads(ReactorFuelRodModelData.getFuelLevel(modelData), ReactorFuelRodModelData.getWasteLevel(modelData), modelSubType._axis));
                modelSubType._cachedQuads.put(modelIndex, quads = new ObjectArrayList<>(tempQuads));

                lockStamp = writeLockStamp;
                break;

            } else {

                this._lock.unlockRead(lockStamp);
                lockStamp = this._lock.writeLock();
            }

        } while (true);

        this._lock.unlock(lockStamp);
        return quads;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this._subTypes[0]._baseModel.getParticleIcon(ModelData.EMPTY);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(@NotNull ModelData modelData) {
        return this._subTypes[ReactorFuelRodModelData.getOrientation(modelData).ordinal()]._baseModel.getParticleIcon(modelData);
    }

    @Override
    public BakedOverrides overrides() {
        return this._subTypes[0]._baseModel.overrides();
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemTransforms getTransforms() {
        return this._subTypes[0]._baseModel.getTransforms();
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    //endregion
    //region internals

    private List<BakedQuad> buildQuads(byte fuelLevel, byte wasteLevel, final Direction.Axis axis) {

        final float fromX, fromY, fromZ, baseWidth;
        final float[] fuelFlowingUV, wasteFlowingUV, stillUV;
        final TextureAtlasSprite fuelColumnStill = CachedSprites.REACTOR_FUEL_COLUMN_STILL.get().getAtlasSprite().orElse(ModRenderHelper.getMissingTexture());
        final TextureAtlasSprite fuelColumnFlowing = CachedSprites.REACTOR_FUEL_COLUMN_FLOWING.get().getAtlasSprite().orElse(ModRenderHelper.getMissingTexture());
        final BlockFacings fuelVisibleFaces, wasteVisibleFaces;
        BlockFacings visibleFaces;

        switch (axis.getPlane()) {

            default:
            case VERTICAL:

                fuelLevel = (byte)Math.min(fuelLevel, 12);
                wasteLevel = (byte)Math.min(wasteLevel, 12);
                fromX = 3.0f;
                fromY = 2.0f;
                fromZ = 3.0f;
                baseWidth = 10.0f;
                fuelFlowingUV = new float[]{3, 2, 13, 2 + fuelLevel};
                wasteFlowingUV = new float[]{3, 2, 13, 2 + wasteLevel};
                stillUV = new float[]{3, 3, 13, 13};
                visibleFaces = BlockFacings.ALL.set(Direction.UP, fuelLevel + wasteLevel < 12).set(Direction.DOWN, false);
                fuelVisibleFaces = visibleFaces;
                wasteVisibleFaces = visibleFaces.set(Direction.UP, 0 == fuelLevel && wasteLevel < 12);
                break;

            case HORIZONTAL:

                fuelLevel = (byte)Math.min(fuelLevel, 10);
                wasteLevel = (byte)Math.min(wasteLevel, 10);
                fromX = 2.0f;
                fromY = 3.0f;
                fromZ = 2.0f;
                baseWidth = 12.0f;
                fuelFlowingUV = new float[]{2, 3, 14, 2 + fuelLevel};
                wasteFlowingUV = new float[]{2, 3, 14, 2 + wasteLevel};
                stillUV = new float[]{2, 2, 14, 14};
                visibleFaces = BlockFacings.ALL;

                for (Direction direction : Direction.Plane.HORIZONTAL) {

                    if (direction.getAxis() == axis) {
                        visibleFaces = visibleFaces.set(direction, false);
                    }
                }

                fuelVisibleFaces = visibleFaces.set(Direction.DOWN, 0 == wasteLevel);
                wasteVisibleFaces = visibleFaces.set(Direction.UP, 0 == fuelLevel);
                break;
        }

        final List<BakedQuad> quads = Lists.newLinkedList();

        // waste

        if (wasteLevel > 0) {

            final Vector3f from = new Vector3f(fromX, fromY, fromZ);
            final Vector3f to = new Vector3f(fromX + baseWidth, fromY + wasteLevel, fromZ + baseWidth);

            wasteVisibleFaces.stream().forEach(direction -> quads.add(this.createFace(direction, from, to, wasteFlowingUV, stillUV, 1, fuelColumnFlowing, fuelColumnStill)));
        }

        // fuel

        if (fuelLevel > 0) {

            final Vector3f from = new Vector3f(fromX, fromY + wasteLevel, fromZ);
            final Vector3f to = new Vector3f(fromX + baseWidth, fromY + wasteLevel + fuelLevel, fromZ + baseWidth);

            fuelVisibleFaces.stream().forEach(direction -> quads.add(this.createFace(direction, from, to, fuelFlowingUV, stillUV, 0, fuelColumnFlowing, fuelColumnStill)));
        }

        return quads;
    }

    private BakedQuad createFace(final Direction direction, final Vector3f cubeFrom, final Vector3f cubeTo,
                                 final float[] flowingUV, final float[] stillUV, final int tintIndex,
                                 final TextureAtlasSprite flowingTexture, final TextureAtlasSprite stillTexture) {

        final boolean isVertical = direction.getAxis().isVertical();
        final float[] uv = isVertical ? stillUV : flowingUV;
        final TextureAtlasSprite sprite = isVertical ? stillTexture : flowingTexture;
        final BlockElementFace partFace = new BlockElementFace(null, tintIndex, "",  new BlockFaceUV(uv, 0));

        return this._faceBakery.bakeQuad(cubeFrom, cubeTo, partFace, sprite, direction, IDENTITY_MODEL_STATE,
                null, true, -1);
    }

    private static class ModelSubType {

        public ModelSubType(Direction.Axis axis, BakedModel baseModel) {

            Preconditions.checkNotNull(axis, "Axis cannot be null");
            Preconditions.checkNotNull(baseModel, "Base model cannot be null");

            this._axis = axis;
            this._baseModel = baseModel;
            this._cachedQuads = new Short2ObjectArrayMap<>(208);
        }

        //region internals

        private final Direction.Axis _axis;
        private final BakedModel _baseModel;
        private final Short2ObjectMap<List<BakedQuad>> _cachedQuads;

        //endregion
    }

    private static final List<BakedQuad> EMPTY_QUADS = ObjectLists.emptyList();
    private static final ModelState IDENTITY_MODEL_STATE = new SimpleModelState(Transformation.identity(), false);

    private final StampedLock _lock;
    private final ModelSubType[] _subTypes;
    private final FaceBakery _faceBakery;

    //endregion
}
