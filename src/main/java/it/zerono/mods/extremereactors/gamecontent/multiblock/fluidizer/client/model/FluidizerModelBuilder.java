/*
 *
 * FluidizerModelBuilder.java
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

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.FluidizerPartType;
import it.zerono.mods.zerocore.lib.block.property.BlockFacingsProperty;
import it.zerono.mods.zerocore.lib.client.model.multiblock.CuboidPartVariantsModelBuilder;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class FluidizerModelBuilder
        extends CuboidPartVariantsModelBuilder {

    public FluidizerModelBuilder() {
        this("assembledplating");
    }

    protected FluidizerModelBuilder(final String templateName) {

        super(getModelRL(templateName), true);
        this.build();
    }

    protected void build() {

        final Function<String, ResourceLocation> modelToReplaceIdGetter = blockName ->
                new ModelResourceLocation(ExtremeReactors.ROOT_LOCATION.buildWithSuffix("fluidizer" + blockName), "");

        final Function<String, ResourceLocation> variantModelIdGetter = FluidizerModelBuilder::getModelRL;

        this.addBlockWithVariants(modelToReplaceIdGetter, variantModelIdGetter, FluidizerPartType.Casing, "casing",
                "casing_01_face",
                "casing_02_frame_ds",
                "casing_03_frame_de",
                "casing_04_frame_dn",
                "casing_05_frame_dw",
                "casing_06_frame_us",
                "casing_07_frame_ue",
                "casing_08_frame_un",
                "casing_09_frame_uw",
                "casing_10_frame_se",
                "casing_11_frame_ne",
                "casing_12_frame_nw",
                "casing_13_frame_sw",
                "casing_14_corner_dsw",
                "casing_15_corner_dse",
                "casing_16_corner_dne",
                "casing_17_corner_dnw",
                "casing_18_corner_usw",
                "casing_19_corner_use",
                "casing_20_corner_une",
                "casing_21_corner_unw");

        this.addBlockWithVariants(modelToReplaceIdGetter, variantModelIdGetter, FluidizerPartType.Controller,
                "controller", "controller_on", "controller_off");
        this.addBlockWithVariants(modelToReplaceIdGetter, variantModelIdGetter, FluidizerPartType.PowerPort,
                "powerport");
        this.addBlockWithVariants(modelToReplaceIdGetter, variantModelIdGetter, FluidizerPartType.SolidInjector,
                "solidinjector", "solidinjector_connected");
        this.addBlockWithVariants(modelToReplaceIdGetter, variantModelIdGetter, FluidizerPartType.FluidInjector,
                "fluidinjector", "fluidinjector_connected");
        this.addBlockWithVariants(modelToReplaceIdGetter, variantModelIdGetter, FluidizerPartType.OutputPort,
                "outputport", "outputport_connected");

        this.setFallbackModelData(FluidizerPartType.Casing.ordinal(), BlockFacingsProperty.None.ordinal());
    }

    //region internals

    protected static ResourceLocation getModelRL(final String modelName) {
        return ExtremeReactors.ROOT_LOCATION.appendPath("block", "fluidizer").buildWithSuffix(modelName);
    }

    //endregion
}
