/*
 *
 * ReprocessorCollectorRender.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.client.render;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.zerono.mods.extremereactors.config.Config;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.part.ReprocessorCollectorEntity;
import it.zerono.mods.zerocore.lib.client.render.buffer.TintingRenderTypeBufferWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public class ReprocessorCollectorRender
        implements BlockEntityRenderer<ReprocessorCollectorEntity> {

    public ReprocessorCollectorRender(final BlockEntityRendererProvider.Context context) {
    }

    //region TileEntityRenderer

    @Override
    public AABB getRenderBoundingBox(ReprocessorCollectorEntity collector) {
        return collector.getRenderBoundingBox();
    }

    @Override
    public void render(final ReprocessorCollectorEntity collector, float partialTicks, final PoseStack stack,
                       final MultiBufferSource buffer, final int combinedLight, final int combinedOverlay) {

        final ItemStack startItem = collector.getRecipeSourceItem();
        final ItemStack endItem = collector.getRecipeProductItem();
        final double progress = collector.getProgress();

        if (!collector.isRecipeRunning() || startItem.isEmpty() || endItem.isEmpty()) {
            return;
        }

        stack.pushPose();

        stack.translate(0.5, 6.0 - (5.5 * progress), 0.5);
        stack.mulPose(Axis.YP.rotationDegrees((360 * collector.getPartWorldOrFail().getGameTime() / 50.0f) + (partialTicks / 50.0f)));
        stack.scale(1.5F, 1.5F, 1.5F);

        if (Config.CLIENT.disableReprocessorMorphingAnimation) {

            ITEM_RENDERER.renderStatic(startItem, ItemDisplayContext.GROUND, combinedLight, combinedOverlay,
                    stack, buffer, collector.getCurrentWorld(), 0);

        } else {

            final float startItemAlpha = (1.0f - (float)progress) + 0.25f;
            final float endItemAlpha = ((float)progress) - 0.10f;

            ITEM_RENDERER.renderStatic(startItem, ItemDisplayContext.GROUND, combinedLight, combinedOverlay,
                    stack, new TintingRenderTypeBufferWrapper(buffer, startItemAlpha, 1.0f, 1.0f, 1.0f),
                    collector.getCurrentWorld(), 0);

            ITEM_RENDERER.renderStatic(endItem, ItemDisplayContext.GROUND, combinedLight, combinedOverlay,
                    stack, new TintingRenderTypeBufferWrapper(buffer, endItemAlpha, 1.0f, 1.0f, 1.0f),
                    collector.getCurrentWorld(), 0);
        }

        stack.popPose();
    }

    //endregion
    //region internals

    private static final ItemRenderer ITEM_RENDERER = Minecraft.getInstance().getItemRenderer();

    //endregion
}
