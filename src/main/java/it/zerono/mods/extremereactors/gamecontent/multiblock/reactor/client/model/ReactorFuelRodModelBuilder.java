/*
 *
 * ReactorFuelRodModelBuilder.java
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

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.IMultiblockReactorVariant;
import it.zerono.mods.zerocore.lib.client.model.ICustomModelBuilder;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;

import java.util.Map;

public class ReactorFuelRodModelBuilder
        implements ICustomModelBuilder {

    public ReactorFuelRodModelBuilder(final IMultiblockReactorVariant variant) {

        this._ids = new Object2ObjectArrayMap<>(3);

        for (final Direction.Axis axis : Direction.Axis.values()) {
            this._ids.put(axis, getBlockStateRL(variant, axis));
        }
    }

    //region ICustomModelBuilder

    @Override
    public void onRegisterModels() {
    }

    @Override
    public void onBakeModels(final ModelBakeEvent event) {

        final Map<ResourceLocation, IBakedModel> registry = event.getModelRegistry();

        final IBakedModel missing = ModRenderHelper.getMissingModel(registry);
        final Object2ObjectMap<Direction.Axis, IBakedModel> baseModels = new Object2ObjectArrayMap<>(this._ids.size());

        this._ids.forEach((axis, id) -> baseModels.put(axis, registry.getOrDefault(id, missing)));

        final ReactorFuelRodModel fuelRodModel = new ReactorFuelRodModel(baseModels);

        this._ids.forEach((axis, id) -> registry.put(id, fuelRodModel));
    }

    //endregion
    //region internals

    private static ResourceLocation getBlockStateRL(final IMultiblockReactorVariant variant,
                                                    final Direction.Axis axis) {
        return new ModelResourceLocation(ExtremeReactors.newID(variant.getName() + "_reactorfuelrod"),
                String.format("axis=%s", axis.getName()));
    }

    private final Object2ObjectMap<Direction.Axis, ResourceLocation> _ids;

    //endregion
}
