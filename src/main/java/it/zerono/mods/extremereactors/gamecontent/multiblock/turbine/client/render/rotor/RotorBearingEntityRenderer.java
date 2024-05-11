/*
 *
 * RotorBearingEntityRenderer.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.render.rotor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineRotorBearingEntity;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.phys.AABB;

public class RotorBearingEntityRenderer
        implements BlockEntityRenderer<TurbineRotorBearingEntity> {

    public RotorBearingEntityRenderer(final BlockEntityRendererProvider.Context context) {
    }

    //region BlockEntityRenderer

    @Override
    public AABB getRenderBoundingBox(TurbineRotorBearingEntity bearing) {
        return bearing.getRenderBoundingBox();
    }

    @Override
    public boolean shouldRenderOffScreen(TurbineRotorBearingEntity te) {
        return true;
    }

    @Override
    public void render(final TurbineRotorBearingEntity bearing, float partialTicks, final PoseStack stack,
                       final MultiBufferSource buffer, final int combinedLight, final int combinedOverlay) {

        if (!bearing.isMachineAssembled() || bearing.isTurbineInteriorInvisible()) {
            return;
        }

        final RotorDescriptor descriptor = bearing.getRotorDescriptor();

        if (null == descriptor) {
            return;
        }

        final BakedModel shaft = descriptor.ShaftModel.get();
        final BakedModel blade = descriptor.BladeModel.get();
        final VertexConsumer builder = buffer.getBuffer(Sheets.solidBlockSheet());

        stack.pushPose();

        // translate the matrix stack to the center of the rotated shaft
        descriptor.InitMatrix.accept(stack, getRotorAngle(bearing));

        // render the rotor sections

        for (final ShaftSection section : descriptor.Sections) {

            // translate to the section position
            descriptor.accept(stack);

            // render the shaft
            ModRenderHelper.renderModel(shaft, section.ShaftModelData, stack, builder, combinedLight, combinedOverlay, null);

            // render the blades

            for (final BladeSpan bladeSpan : section.Blades) {

                stack.pushPose();

                for (int i = 0; i < bladeSpan.Length; ++i) {

                    bladeSpan.accept(stack);
                    ModRenderHelper.renderModel(blade, bladeSpan.BladeModelData, stack, builder, combinedLight, combinedOverlay, null);
                }

                stack.popPose();
            }
        }

        stack.popPose();
    }

    //endregion
    //region internals

    private static float getRotorAngle(final TurbineRotorBearingEntity bearing) {

        final long elapsedTime = System.currentTimeMillis() - ModRenderHelper.getLastRenderTime();
        final float speed = bearing.evalOnController(turbine -> turbine.getRotorSpeed() / 10.0f, 0.0f);
        float angle = bearing.getRotorAngle();

        if (speed > 0.001f) {

            angle += speed * ((float)elapsedTime / 60_000.0f) * 360.0f; // RPM * time in minutes * 360 degrees per rotation
            angle = angle % 360.0f;
            bearing.setRotorAngle(angle);
        }

        return angle;
    }

    //endregion
}
