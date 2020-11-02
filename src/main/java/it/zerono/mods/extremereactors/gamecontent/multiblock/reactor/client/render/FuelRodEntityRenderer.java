/*
 *
 * FuelRodEntityRenderer.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.ClientFuelRodsLayout;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.ClientFuelRodsLayout.FuelData;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorFuelRodEntity;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import it.zerono.mods.zerocore.lib.multiblock.AbstractMultiblockController;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.EmptyModelData;

public class FuelRodEntityRenderer extends TileEntityRenderer<ReactorFuelRodEntity> {

    public FuelRodEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    //region TileEntityRenderer

    @Override
    public void render(ReactorFuelRodEntity rod, float partialTicks, MatrixStack matrix,
                       IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {

        if (rod.isOccluded()) {
            return;
        }

        rod.getMultiblockController()
                .filter(AbstractMultiblockController::isAssembled)
                .filter(it.zerono.mods.extremereactors.gamecontent.multiblock.common.AbstractMultiblockController::isInteriorVisible)
                .ifPresent(controller -> controller.getFuelRodsLayout()
                        .filter(layout -> layout instanceof ClientFuelRodsLayout)
                        .map(layout -> (ClientFuelRodsLayout)layout)
                        .ifPresent(clientLayout -> {

                            final FuelData rodData;

                            if (Direction.Plane.VERTICAL == clientLayout.getOrientation()) {

                                rodData = clientLayout.getFuelData(controller.getMinimumCoord()
                                        .map(minPos -> rod.getWorldPosition().getY() - minPos.getY() - 1)
                                        .orElse(0));

                            } else {

                                rodData = clientLayout.getFuelData(0);
                            }

                            final IVertexBuilder builder = buffer.getBuffer(Atlases.getSolidBlockType());

                            switch (rodData.getFluidStatus()) {

                                case FullFuelOnly:
                                case FullWasteOnly:
                                case FuelOnly:
                                case WasteOnly:
                                case Mixed:
                                    ModRenderHelper.renderModel(clientLayout.getModelFor(rodData), EmptyModelData.INSTANCE, matrix, builder,
                                            combinedLight, combinedOverlay, clientLayout::getModelTint);
                                    break;
                            }
                        })
                );
    }

    //endregion
}
