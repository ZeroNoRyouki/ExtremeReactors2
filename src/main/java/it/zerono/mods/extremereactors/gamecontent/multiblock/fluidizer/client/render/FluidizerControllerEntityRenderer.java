/*
 * FluidizerControllerEntityRenderer
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.client.model.FluidizerClientTankData;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part.FluidizerControllerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;

public class FluidizerControllerEntityRenderer
        implements BlockEntityRenderer<FluidizerControllerEntity> {

    public FluidizerControllerEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    //region BlockEntityRenderer

    @Override
    public void render(FluidizerControllerEntity controller, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        if (controller.isMachineAssembled() && controller.getTankData() instanceof FluidizerClientTankData tank) {
            tank.render(poseStack, bufferSource, packedLight);
        }
    }

    //endregion
}
