/*
 *
 * ReprocessorModelBuilder.java
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

import com.google.common.collect.Lists;
import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.ReprocessorPartType;
import it.zerono.mods.zerocore.lib.client.model.multiblock.CuboidPartVariantsModelBuilder;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReprocessorModelBuilder
    extends CuboidPartVariantsModelBuilder {

    public ReprocessorModelBuilder() {
        this("assembledplating");
    }

    protected ReprocessorModelBuilder(final String templateName) {

        super(getModelRL(templateName), true);
        this.build();
    }

    protected void build() {

        this.addBlockWithVariants(ReprocessorPartType.Casing, "casing",
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

        this.addBlockWithVariants(ReprocessorPartType.Controller, "controller", "controller_on", "controller_off");
        this.addBlockWithVariants(ReprocessorPartType.PowerPort, "powerport");
        //this.addBlockWithVariants(ReprocessorPartType.WasteInjector, "wasteinjector", "wasteinjector_connected");
        this.addBlockWithVariants(ReprocessorPartType.FluidInjector, "fluidinjector", "fluidinjector_connected");
        this.addBlockWithVariants(ReprocessorPartType.OutputPort, "outputport", "outputport_connected");
    }

    //region internals

    protected void addBlockWithVariants(final ReprocessorPartType partType, final String blockCommonName,
                                        final String... additionalVariantsModelNames) {

        this.addBlock(partType.ordinal(), getBlockStateRL(blockCommonName), 0, false);
        this.addBlockVariants(partType, blockCommonName, additionalVariantsModelNames);
    }

    protected void addBlockVariants(final ReprocessorPartType partType, final String blockCommonName,
                                    final String... additionalVariantsModelNames) {

        final List<ResourceLocation> variants = Lists.newArrayListWithCapacity(1 + additionalVariantsModelNames.length);

        variants.add(getBlockStateRL(blockCommonName));
        Arrays.stream(additionalVariantsModelNames)
                .map(ReprocessorModelBuilder::getModelRL)
                .collect(Collectors.toCollection(() -> variants));

        this.addModels(partType.ordinal(), variants);
    }

    protected ResourceLocation getBlockStateRL(final String blockCommonName) {
        return getBlockStateRL(blockCommonName, "");
    }

    protected ResourceLocation getBlockStateRL(final String blockCommonName, final String blockStateVariant) {
        return new ModelResourceLocation(ExtremeReactors.ROOT_LOCATION.buildWithSuffix("reprocessor" + blockCommonName), blockStateVariant);
    }

    protected static ResourceLocation getModelRL(final String modelName) {
        return ExtremeReactors.ROOT_LOCATION.appendPath("block", "reprocessor").buildWithSuffix(modelName);
    }

    //endregion
}
