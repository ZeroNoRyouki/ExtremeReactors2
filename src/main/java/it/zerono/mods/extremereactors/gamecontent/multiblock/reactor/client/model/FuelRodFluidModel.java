/*
 *
 * FuelRodFluidModel.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CachedSprites;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.ClientFuelRodsLayout;
import it.zerono.mods.zerocore.lib.block.BlockFacings;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.model.AbstractDynamicBakedModel;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.data.geometry.Vector3d;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static it.zerono.mods.zerocore.lib.client.render.ModRenderHelper.ONE_PIXEL;

@OnlyIn(Dist.CLIENT)
public class FuelRodFluidModel
        extends AbstractDynamicBakedModel {


    ///////////////////////////////// stupid gradle...
    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.getParticleTexture(EmptyModelData.INSTANCE);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }
    /////////////////////////////////


    public FuelRodFluidModel(final ClientFuelRodsLayout.FuelData rodData, final Direction.Axis axis) {
        this(rodData.getFuelHeight(), rodData.getWasteHeight(), axis);
    }

    public FuelRodFluidModel(float fuelHeight, float wasteHeight, final Direction.Axis axis) {

        super(true, false);
        this._quads = Maps.newHashMap();

        final float heightRatio;
        final float width;

        Vector3d offset;
        BlockFacings visibleFaces = BlockFacings.ALL.set(Direction.UP, 1.0f > fuelHeight + wasteHeight);

        if (Direction.Axis.Y == axis) {

            heightRatio = 12.f;
            width = 10.0f / 16.0f;
            offset = new Vector3d(0, -0.5 + 2 * ONE_PIXEL, 0);
            visibleFaces = visibleFaces.set(Direction.DOWN, false);

        } else {

            heightRatio = 10.f;
            width = 12.0f / 16.0f;
            offset = new Vector3d(0, -0.5 + 3 * ONE_PIXEL, 0);

            for (Direction direction : Direction.Plane.HORIZONTAL) {

                if (direction.getAxis() == axis) {
                    visibleFaces = visibleFaces.set(direction, false);
                }
            }
        }

        wasteHeight = wasteHeight / 16.f * heightRatio;
        fuelHeight = fuelHeight / 16.f * heightRatio;

        if (wasteHeight > 0) {

            offset = offset.add(0, wasteHeight / 2.0, 0);
            this.addFluid(1, width, wasteHeight, visibleFaces, offset);
        }

        if (fuelHeight > 0) {

            offset = offset.add(0, (wasteHeight + fuelHeight) / 2.0, 0);
            this.addFluid(0, width, fuelHeight, visibleFaces, offset);
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void addFluid(final int tintIndex, final float width, final float height, final BlockFacings visibleFaces,
                          final Vector3d offset) {

        Direction.Plane.VERTICAL.forEach(direction ->
                visibleFaces.ifSet(direction, () -> this.addFace(direction, width, width, height, tintIndex, offset)));

        Direction.Plane.HORIZONTAL.forEach(direction ->
                visibleFaces.ifSet(direction, () -> this.addFace(direction, width, height, width, tintIndex, offset)));
    }

    //region IDynamicBakedModel

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        return this._quads.getOrDefault(side, Collections.emptyList());
    }

    @Override
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) {
        return CachedSprites.REACTOR_FUEL_COLUMN_FLOWING.get().getAtlasSprite().orElse(ModRenderHelper.getMissingTexture());
    }

    //endregion
    //region internals

    protected void addFace(final Direction direction, final float width, final float height, final float depth,
                           final int tintIndex, final Vector3d offset) {
        this.addQuad(direction, this.createFace(direction, width, height, depth, sprite(direction), tintIndex, offset));
    }

    private void addQuad(final Direction direction, final BakedQuad quad) {
        this._quads.computeIfAbsent(direction, d -> Lists.newArrayListWithExpectedSize(2)).add(quad);
    }

    private static ISprite sprite(final Direction direction) {
        return direction.getAxis().isVertical() ? CachedSprites.REACTOR_FUEL_COLUMN_STILL.get() :
                CachedSprites.REACTOR_FUEL_COLUMN_FLOWING.get();
    }

    private final Map<Direction, List<BakedQuad>> _quads;

    //endregion
}
