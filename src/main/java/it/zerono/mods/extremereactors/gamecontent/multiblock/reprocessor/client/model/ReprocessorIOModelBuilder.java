/*
 *
 * ReprocessorIOModelBuilder.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.client.model;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.ReprocessorPartType;
import it.zerono.mods.zerocore.lib.block.property.BlockFacingsProperty;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class ReprocessorIOModelBuilder
        extends ReprocessorModelBuilder {

    public ReprocessorIOModelBuilder() {
        super("assembledplatingio");
    }

    @Override
    protected void build() {

        final Function<String, ResourceLocation> modelToReplaceIdGetter = blockName ->
                new ModelResourceLocation(ExtremeReactors.ROOT_LOCATION.buildWithSuffix("reprocessor" + blockName), "");

        final Function<String, ResourceLocation> variantModelIdGetter = ReprocessorModelBuilder::getModelRL;

        this.addBlockWithVariants(modelToReplaceIdGetter, variantModelIdGetter, ReprocessorPartType.WasteInjector,
                "wasteinjector", "wasteinjector_connected");
        this.addBlockWithVariants(modelToReplaceIdGetter, variantModelIdGetter, ReprocessorPartType.Collector,
                "collector");

        this.setFallbackModelData(ReprocessorPartType.WasteInjector.ordinal(), BlockFacingsProperty.None.ordinal());
    }
}
