/*
 *
 * TurbineRotorBearingEntity.java
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

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.AbstractMultiblockController;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.MultiblockTurbine;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.render.rotor.RotorDescriptor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.render.rotor.ShaftSection;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorBladeState;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorShaftState;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class TurbineRotorBearingEntity
        extends AbstractTurbineEntity {

    public TurbineRotorBearingEntity() {

        super(Content.TileEntityTypes.TURBINE_ROTORBEARING.get());
        this._rotorAngle = 0.0f;
        this._rotorDescriptor = null;
        this._renderBoundingBox = INFINITE_EXTENT_AABB;
    }

    public Direction getRotorDirection() {

        final PartPosition partPosition = this.getPartPosition();

        if (partPosition.isFace()) {
            return partPosition.getDirection().map(Direction::getOpposite).orElse(Direction.UP);
        }

        return this.getOutwardFacingFromWorldPosition(Direction.DOWN).getOpposite();
    }

    @OnlyIn(Dist.CLIENT)
    public float getRotorAngle() {
        return this._rotorAngle;
    }

    @OnlyIn(Dist.CLIENT)
    public void setRotorAngle(float angle) {
        this._rotorAngle = angle;
    }

    @OnlyIn(Dist.CLIENT)
    @Nullable
    public RotorDescriptor getRotorDescriptor() {

        if (null == this._rotorDescriptor) {
            this._rotorDescriptor = this.buildRotorDescriptor();
        }

        return this._rotorDescriptor;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean isTurbineInteriorInvisible() {
        return this.evalOnController(AbstractMultiblockController::isInteriorInvisible, false);
    }

    //region AbstractReactorEntity

    @Override
    public boolean isGoodForPosition(PartPosition position, IMultiblockValidator validatorCallback) {

        if (position.isFace()) {
            return true;
        }

        validatorCallback.setLastError(this.getWorldPosition(), "multiblock.validation.turbine.invalid_bearing_position");
        return false;
    }

    //endregion
    //region client render support

    @Override
    protected int getUpdatedModelVariantIndex() {
        return 0;
    }

    //endregion
    //region AbstractMultiblockEntity

    @Override
    public void onPostMachineAssembled(final MultiblockTurbine controller) {

        super.onPostMachineAssembled(controller);
        this.resetRotorDescriptor();
    }

    @Override
    public void onPostMachineBroken() {

        super.onPostMachineBroken();
        this.resetRotorDescriptor();
    }

    //endregion
    //region TileEntity

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return this._renderBoundingBox;
    }

    //endregion
    //region internals
    //region build rotor

    private void resetRotorDescriptor() {
        this._rotorDescriptor = null;
    }

    @SuppressWarnings("ConstantConditions")
    @OnlyIn(Dist.CLIENT)
    @Nullable
    private RotorDescriptor buildRotorDescriptor() {
        return this.evalOnController(turbine -> turbine.mapBoundingBoxCoordinates((min, max) ->
                this.buildRotorDescriptor(turbine, min, max), null), null);
    }

    @OnlyIn(Dist.CLIENT)
    private RotorDescriptor buildRotorDescriptor(final MultiblockTurbine turbine, final BlockPos turbineMin, final BlockPos turbineMax) {

        // build our new render bounding box

        this._renderBoundingBox = new AxisAlignedBB(turbineMin, turbineMax);

        // build the rotor

        final Direction rotorDirection = this.getRotorDirection();
        final int rotorLength;

        switch (rotorDirection.getAxis()) {

            case X:
                rotorLength = Math.abs(turbineMax.getX() - turbineMin.getX()) - 1;
                break;

            default:
            case Y:
                rotorLength = Math.abs(turbineMax.getY() - turbineMin.getY()) - 1;
                break;

            case Z:
                rotorLength = Math.abs(turbineMax.getZ() - turbineMin.getZ()) - 1;
                break;
        }

        final List<Direction> bladesDirections = CodeHelper.perpendicularDirections(rotorDirection);
        final World world = this.getPartWorldOrFail();

        final RotorDescriptor.Builder rotorBuilder = RotorDescriptor.builder(TurbineVariant.from(turbine.getVariant()),
                rotorDirection, rotorLength);

        BlockPos shaftPosition = this.getWorldPosition().relative(rotorDirection);
        int checkedSections = 0;

        do {

            // the shaft

            final RotorShaftState shaftState = WorldHelper.getTile(world, shaftPosition)
                    .filter(te -> te instanceof TurbineRotorComponentEntity)
                    .map(te -> TurbineRotorComponentEntity.computeShaftState((TurbineRotorComponentEntity)te))
                    .orElse(RotorShaftState.HIDDEN);

            // the blades

            final BlockPos bladesPosition = shaftPosition;

            rotorBuilder.section(shaftState, sectionBuilder -> this.buildShaftSection(world, bladesPosition,
                    bladesDirections, sectionBuilder));

            // next section

            shaftPosition = shaftPosition.relative(rotorDirection);

        } while (++checkedSections < rotorLength);

        return rotorBuilder.build();
    }

    private void buildShaftSection(final World world, final BlockPos shaftPosition, List<Direction> bladesDirections,
                                   final ShaftSection.Builder sectionBuilder) {

        for (final Direction direction : bladesDirections) {

            BlockPos bladePosition = shaftPosition.relative(direction);

            final RotorBladeState state = WorldHelper.getTile(world, bladePosition)
                    .filter(te -> te instanceof TurbineRotorComponentEntity)
                    .map(te -> (TurbineRotorComponentEntity)te)
                    .filter(TurbineRotorComponentEntity::isBlade)
                    .map(TurbineRotorComponentEntity::computeBladeState)
                    .orElse(RotorBladeState.HIDDEN);

            if (RotorBladeState.HIDDEN == state) {

                // no blade here
                continue;
            }

            // found a blade, check how big it is

            short bladeLength = 1;

            while (true) {

                bladePosition = bladePosition.relative(direction);

                if (WorldHelper.getTile(world, bladePosition)
                        .filter(te -> te instanceof TurbineRotorComponentEntity)
                        .map(te -> (TurbineRotorComponentEntity)te)
                        .map(TurbineRotorComponentEntity::isBlade)
                        .orElse(false)) {

                    ++bladeLength;

                } else {

                    break;
                }
            }

            // create blade

            sectionBuilder.addBlade(state, bladeLength, direction);
        }
    }

    //endregion

    private float _rotorAngle;
    private AxisAlignedBB _renderBoundingBox;
    private RotorDescriptor _rotorDescriptor;

    //endregion
}
