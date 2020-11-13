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
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.MultiblockTurbine;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TurbineRotorBearingEntity
        extends AbstractTurbineEntity {

    public TurbineRotorBearingEntity() {

        super(Content.TileEntityTypes.TURBINE_ROTORBEARING.get());
        this._rotorAngle = 0.0f;
    }

    public Direction getRotorDirection() {
        return this.getOutwardFacings().firstIf(true)
                .orElse(this.getOutwardFacingFromWorldPosition()
                        .orElseThrow(IllegalStateException::new))
                .getOpposite();
    }

    @OnlyIn(Dist.CLIENT)
    public float getRotorAngle() {
        return _rotorAngle;
    }

    @OnlyIn(Dist.CLIENT)
    public void setRotorAngle(float angle) {
        this._rotorAngle = angle;
    }

    //region AbstractReactorEntity

    @Override
    public boolean isGoodForPosition(PartPosition position, IMultiblockValidator validatorCallback) {

        if (position.isFace()) {
            return true;
        }

        final BlockPos partPosition = this.getWorldPosition();

        validatorCallback.setLastError("multiblock.validation.turbine.invalid_bearing_position",
                partPosition.getX(), partPosition.getY(), partPosition.getZ());

        return false;
    }

    //endregion
    //region AbstractMultiblockEntity

    @Override
    public void onPostMachineAssembled(final MultiblockTurbine controller) {

        super.onPostMachineAssembled(controller);
        //this.calculateRotorInfo();
    }

    //endregion
    //region internals

    private float _rotorAngle;
    private AxisAlignedBB _renderBoundingBox;

    //endregion
}
