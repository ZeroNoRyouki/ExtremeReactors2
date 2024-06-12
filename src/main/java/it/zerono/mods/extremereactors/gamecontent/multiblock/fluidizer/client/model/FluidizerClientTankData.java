/*
 *
 * FluidizerClientTankData.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.FluidizerTankData;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.MultiblockFluidizer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.part.FluidizerControllerEntity;
import it.zerono.mods.zerocore.lib.client.render.FluidTankRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.fluids.FluidStack;

public class FluidizerClientTankData
        extends FluidizerTankData {

    public FluidizerClientTankData(FluidizerControllerEntity controllerEntity) {

        final MultiblockFluidizer controller = controllerEntity.getMultiblockController().orElseThrow();
        final var box = controller.getBoundingBox();
        final var min = box.getMin().offset(1, 1, 1);
        final var offset = controllerEntity.getWorldPosition().subtract(min);

        this._renderer = new FluidTankRenderer.Single(controller.getFluidHandler().getTankCapacity(0),
                min.getX(), min.getY(), min.getZ(), box.getMaxX() - 1, box.getMaxY() - 1, box.getMaxZ() - 1
        );

        this._controller = controller;
        this._translationX = -offset.getX();
        this._translationY = -offset.getY();
        this._translationZ = -offset.getZ();
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {

        final FluidStack stack = this._controller.getFluidHandler().getFluidInTank(0);

        poseStack.translate(this._translationX, this._translationY, this._translationZ);
        this._renderer.render(poseStack, bufferSource, packedLight, stack);
        poseStack.translate(-this._translationX, -this._translationY, -this._translationZ);
    }

    //region internals

    private final FluidTankRenderer.Single _renderer;
    private final MultiblockFluidizer _controller;
    private final int _translationX, _translationY, _translationZ;

    //endregion
}
