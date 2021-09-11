package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.model;
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

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.unimi.dsi.fastutil.shorts.Short2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CachedSprites;
import it.zerono.mods.zerocore.lib.block.BlockFacings;
import it.zerono.mods.zerocore.lib.client.model.AbstractDynamicBakedModel;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.SimpleModelTransform;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.StampedLock;

/**
 * An IBakedModel for a single variant/orientation of a Fuel Rod
 */
public class ReactorFuelRodModel
        extends AbstractDynamicBakedModel {

    public static int HORIZONTAL_MAX_STEPS = 10;
    public static int VERTICAL_MAX_STEPS = 12;

    protected ReactorFuelRodModel(final IBakedModel baseModel) {

        super(baseModel.useAmbientOcclusion(), baseModel.isGui3d());

        this._lock = new StampedLock();
        this._baseModel = baseModel;
        this._cachedQuads = new Short2ObjectArrayMap<>(208);
        this._faceBakery = new FaceBakery();
    }

    //region AbstractDynamicBakedModel

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand,
                                    @Nonnull IModelData modelData) {

        if (side != null) {
            return EMPTY_QUADS;
        }

        if (!(modelData instanceof ReactorFuelRodModelData)) {
            return this._baseModel.getQuads(state, null, rand, EmptyModelData.INSTANCE);
        }

        final ReactorFuelRodModelData fuelRodModelData = (ReactorFuelRodModelData)modelData;
        final short modelIndex = fuelRodModelData.getModelKey();

        if (fuelRodModelData.isOccluded()) {
            return EMPTY_QUADS;
        }

        if (0 == modelIndex) {
            return this._baseModel.getQuads(state, null, rand, EmptyModelData.INSTANCE);
        }

        long lockStamp = this._lock.readLock();
        List<BakedQuad> quads = this._cachedQuads.get(modelIndex);

        if (null != quads) {

            this._lock.unlockRead(lockStamp);
            return quads;
        }

        do {

            final long writeLockStamp = this._lock.tryConvertToWriteLock(lockStamp);

            if (0L != writeLockStamp) {

                final List<BakedQuad> tempQuads = Lists.newLinkedList(this._baseModel.getQuads(state, null, rand, EmptyModelData.INSTANCE));

                tempQuads.addAll(this.buildQuads(fuelRodModelData.getFuelLevel(), fuelRodModelData.getWasteLevel(), fuelRodModelData.isVertical() ? Direction.Axis.Y : Direction.Axis.X));
                this._cachedQuads.put(modelIndex, quads = new ObjectArrayList<>(tempQuads));

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
        return this._baseModel.getParticleTexture(EmptyModelData.INSTANCE);
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this._baseModel.getOverrides();
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemCameraTransforms getTransforms() {
        return this._baseModel.getTransforms();
    }

    @Override
    public boolean isCustomRenderer() {
        return this._baseModel.isCustomRenderer();
    }

    @Override
    public boolean usesBlockLight() {
        return this._baseModel.usesBlockLight();
    }

    @Override
    public boolean isGui3d() {
        return this._baseModel.isGui3d();
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

        return new ObjectArrayList<>(quads);
    }

    private BakedQuad createFace(final Direction direction, final Vector3f cubeFrom, final Vector3f cubeTo,
                                 final float[] flowingUV, final float[] stillUV, final int tintIndex,
                                 final TextureAtlasSprite flowingTexture, final TextureAtlasSprite stillTexture) {

        final boolean isVertical = direction.getAxis().isVertical();
        final float[] uv = isVertical ? stillUV : flowingUV;
        final TextureAtlasSprite sprite = isVertical ? stillTexture : flowingTexture;
        final BlockPartFace partFace = new BlockPartFace(null, tintIndex, "",  new BlockFaceUV(uv, 0));

        return this._faceBakery.bakeQuad(cubeFrom, cubeTo, partFace, sprite, direction, SimpleModelTransform.IDENTITY,
                null, true, FAKE_RESOURCELOCATION);
    }

    private static final List<BakedQuad> EMPTY_QUADS = ObjectLists.emptyList();
    private static final ResourceLocation FAKE_RESOURCELOCATION = new ResourceLocation("fake");

    private final StampedLock _lock;
    private final IBakedModel _baseModel;
    private final Short2ObjectMap<List<BakedQuad>> _cachedQuads;
    private final FaceBakery _faceBakery;

    //endregion
}
