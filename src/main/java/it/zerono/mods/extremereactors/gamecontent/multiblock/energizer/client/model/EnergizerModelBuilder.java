/*
 * EnergizerModelBuilder
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.client.model;

import com.google.common.collect.Lists;
import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.EnergizerPartType;
import it.zerono.mods.zerocore.lib.client.model.multiblock.CuboidPartVariantsModelBuilder;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EnergizerModelBuilder
        extends CuboidPartVariantsModelBuilder {

    public EnergizerModelBuilder() {
        this("assembledplating");
    }

    protected EnergizerModelBuilder(final String templateName) {

        super(getModelRL(templateName), true);
        this.build();
    }

    protected void build() {

        this.addBlockWithVariants(EnergizerPartType.Casing, "casing",
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

        this.addBlockWithVariants(EnergizerPartType.Controller, "controller",
                "controller_on", "controller_off");

        this.addBlockWithVariants(EnergizerPartType.PowerPortFE, "powerportfe", "powerportfe_input",
                "powerportfe_assembled_input", "powerportfe_assembled_input_connected",
                "powerportfe_assembled_output", "powerportfe_assembled_output_connected");

        this.addBlockWithVariants(EnergizerPartType.ChargingPortFE, "chargingportfe", "charging_assembled");
    }

    //region internals

    protected void addBlockWithVariants(EnergizerPartType partType, String blockCommonName,
                                        String... additionalVariantsModelNames) {

        this.addBlock(partType.ordinal(), getBlockStateRL(blockCommonName), 0, false);
        this.addBlockVariants(partType, blockCommonName, additionalVariantsModelNames);
    }

    protected void addBlockVariants(EnergizerPartType partType, String blockCommonName,
                                    String... additionalVariantsModelNames) {

        final List<ResourceLocation> variants = Lists.newArrayListWithCapacity(1 + additionalVariantsModelNames.length);

        variants.add(getBlockStateRL(blockCommonName));
        Arrays.stream(additionalVariantsModelNames)
                .map(EnergizerModelBuilder::getModelRL)
                .collect(Collectors.toCollection(() -> variants));

        this.addModels(partType.ordinal(), variants);
    }

    protected ResourceLocation getBlockStateRL(String blockCommonName) {
        return getBlockStateRL(blockCommonName, "");
    }

    protected ResourceLocation getBlockStateRL(String blockCommonName, String blockStateVariant) {
        return new ModelResourceLocation(ExtremeReactors.ROOT_LOCATION.buildWithSuffix("energizer" + blockCommonName), blockStateVariant);
    }

    protected static ResourceLocation getModelRL(String modelName) {
        return ExtremeReactors.ROOT_LOCATION.appendPath("block", "energizer").buildWithSuffix(modelName);
    }

    //endregion
}
