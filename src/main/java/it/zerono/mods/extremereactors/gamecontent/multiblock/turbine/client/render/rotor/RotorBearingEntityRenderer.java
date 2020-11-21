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

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineRotorBearingEntity;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

public class RotorBearingEntityRenderer
        extends TileEntityRenderer<TurbineRotorBearingEntity> {

    public RotorBearingEntityRenderer(final TileEntityRendererDispatcher rendererDispatcher) {
        super(rendererDispatcher);
    }

    //region TileEntityRenderer

    @Override
    public boolean isGlobalRenderer(TurbineRotorBearingEntity te) {
        return true;
    }

    @Override
    public void render(final TurbineRotorBearingEntity bearing, float partialTicks, final MatrixStack stack,
                       final IRenderTypeBuffer buffer, final int combinedLight, final int combinedOverlay) {

        if (!bearing.isMachineAssembled()) {
            return;
        }

        final RotorDescriptor descriptor = bearing.getRotorDescriptor();

        if (null == descriptor) {
            return;
        }

        final IBakedModel shaft = descriptor.ShaftModel.get();
        final IBakedModel blade = descriptor.BladeModel.get();
        final IVertexBuilder builder = buffer.getBuffer(Atlases.getSolidBlockType());

        stack.push();

        // translate the matrix stack to the center of the rotated shaft
        descriptor.InitMatrix.accept(stack, getRotorAngle(bearing));

        // render the rotor sections

        for (final ShaftSection section : descriptor.Sections) {

            // translate to the section position
            descriptor.accept(stack);

            // render the shaft
            ModRenderHelper.renderModel(shaft, section.ShaftModelData, stack, builder, combinedLight, combinedOverlay);

            // render the blades

            for (final BladeSpan bladeSpan : section.Blades) {

                stack.push();

                for (int i = 0; i < bladeSpan.Length; ++i) {

                    bladeSpan.accept(stack);
                    ModRenderHelper.renderModel(blade, bladeSpan.BladeModelData, stack, builder, combinedLight, combinedOverlay);
                }

                stack.pop();
            }
        }

        stack.pop();
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
