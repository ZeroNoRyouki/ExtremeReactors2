/*
 *
 * ReactorModelBuilder.java
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

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.model.AbstractMultiblockModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactorPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
import net.minecraft.util.ResourceLocation;

import java.util.function.Predicate;

public class ReactorModelBuilder
        extends AbstractMultiblockModelBuilder<ReactorPartType> {

    public ReactorModelBuilder(final ReactorVariant variant) {

        super(getModelRL(variant, "assembledplating"), true);

        final Predicate<ReactorPartType> isPartCompatible = variant::isPartCompatible;

        this.addBlockWithVariants(ReactorPartType.Casing, variant, isPartCompatible, "casing",
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

        this.addBlockWithVariants(ReactorPartType.ControlRod, variant, isPartCompatible, "controlrod");

        this.addBlockWithVariants(ReactorPartType.PassivePowerTapFE, variant, isPartCompatible, "powertapfe_passive");
        this.addBlockWithVariants(ReactorPartType.ActivePowerTapFE, variant, isPartCompatible, "powertapfe_active");

        this.addBlockWithVariants(ReactorPartType.SolidAccessPort, variant, isPartCompatible, "solidaccessport",
                "accessport_solid_in_connected",
                "accessport_solid_out",
                "accessport_solid_out_connected");

        this.addBlockWithVariants(ReactorPartType.ActiveFluidPortForge, variant, isPartCompatible, "fluidport_forge_active",
                "fluidport_forge_active_cold_connected",
                "fluidport_forge_active_hot",
                "fluidport_forge_active_hot_connected");

        this.addBlockWithVariants(ReactorPartType.PassiveFluidPortForge, variant, isPartCompatible, "fluidport_forge_passive",
                "fluidport_forge_passive_cold_connected",
                "fluidport_forge_passive_hot",
                "fluidport_forge_passive_hot_connected");

        this.addBlockWithVariants(ReactorPartType.CreativeWaterGenerator, variant, isPartCompatible, "creativewatergenerator");

        this.addBlockWithVariants(ReactorPartType.Controller, variant, isPartCompatible, "controller",
                "controller_on",
                "controller_off");

        this.addBlockWithVariants(ReactorPartType.ComputerPort, variant, isPartCompatible, "computerport",
                "computerport_connected");

        this.addBlockWithVariants(ReactorPartType.RedstonePort, variant, isPartCompatible, "redstoneport",
                "redstoneport_on");
    }

    //region AbstractMultiblockModelBuilder

    @Override
    protected String getMultiblockShortName() {
        return "reactor";
    }

    //endregion
    //region internals

    protected static ResourceLocation getModelRL(final ReactorVariant variant, final String modelName) {
        return ExtremeReactors.newID("block/reactor/" + variant.getName() + "/" + modelName);
    }

//    private void addBlockWithVariants(final ReactorPartType type, final ReactorVariant variant,
//                                      final String blockCommonName, final String... additionalVariantsModelNames) {
//
//        this.addBlock(type, variant, blockCommonName);
//        this.addBlockVariants(type, variant, blockCommonName, additionalVariantsModelNames);
//    }
//
//    private void addBlock(final ReactorPartType type, final ReactorVariant variant, final String blockCommonName) {
//
//        if (variant.isPartCompatible(type)) {
//            super.addBlock(type.ordinal(), getBlockStateRL(variant, blockCommonName), 0, false);
//        }
//    }
//
//    private void addBlockVariants(final ReactorPartType type, final ReactorVariant variant,
//                                  final String blockCommonName, final String... additionalVariantsModelNames) {
//
//        if (variant.isPartCompatible(type)) {
//
//            final List<ResourceLocation> variants = Lists.newArrayListWithCapacity(1 + additionalVariantsModelNames.length);
//
//            variants.add(getBlockStateRL(variant, blockCommonName));
//            Arrays.stream(additionalVariantsModelNames)
//                    .map(name -> getModelRL(variant, name))
//                    .collect(Collectors.toCollection(() -> variants));
//
//            this.addModels(type.ordinal(), variants);
//        }
//    }
//
//    private static ResourceLocation getBlockStateRL(final ReactorVariant variant, final String blockCommonName) {
//        return getBlockStateRL(variant, blockCommonName, "");
//    }
//
//    private static ResourceLocation getBlockStateRL(final ReactorVariant variant, final String blockCommonName,
//                                                    final String blockStateVariant) {
//        return new ModelResourceLocation(ExtremeReactors.newID(variant.getName() + "_reactor" + blockCommonName),
//                blockStateVariant);
//    }
//
//    private static ResourceLocation getModelRL(final ReactorVariant variant, final String modelName) {
//        return ExtremeReactors.newID("block/reactor/" + variant.getName() + "/" + modelName);
//    }

    //endregion
}
