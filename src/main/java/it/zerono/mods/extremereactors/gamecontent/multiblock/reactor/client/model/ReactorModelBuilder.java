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
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.IReactorPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactorPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
import it.zerono.mods.zerocore.lib.block.property.BlockFacingsProperty;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;

import java.util.Set;
import java.util.function.Predicate;

public class ReactorModelBuilder
        extends AbstractMultiblockModelBuilder<IReactorPartType> {

    public ReactorModelBuilder(final ReactorVariant variant) {
        this(variant, ExtremeReactors.ROOT_LOCATION, ExtremeReactors.ROOT_LOCATION
                .appendPath("block", "reactor"));
    }

    private ReactorModelBuilder(ReactorVariant variant, ResourceLocationBuilder root, ResourceLocationBuilder blockRoot) {

        super("reactor", root, blockRoot, blockRoot
                .appendPath(variant.getName())
                .buildWithSuffix("assembledplating"), true);

        final Set<IReactorPartType> invalidPartsForBasicVariant = Set.of(ReactorPartType.ComputerPort,
                ReactorPartType.ActiveFluidPortForge, ReactorPartType.PassiveFluidPortForge, ReactorPartType.FluidAccessPort,
                ReactorPartType.PassiveFluidPortMekanism, ReactorPartType.CreativeWaterGenerator);

        final Predicate<IReactorPartType> isPartCompatible = partType ->
                ReactorVariant.Reinforced == variant || !invalidPartsForBasicVariant.contains(partType);

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

        this.addBlockWithVariants(ReactorPartType.FluidAccessPort, variant, isPartCompatible, "fluidaccessport",
                "accessport_fluid_in_connected",
                "accessport_fluid_out",
                "accessport_fluid_out_connected");

        this.addBlockWithVariants(ReactorPartType.ActiveFluidPortForge, variant, isPartCompatible, "fluidport_forge_active",
                "fluidport_forge_active_cold_connected",
                "fluidport_forge_active_hot",
                "fluidport_forge_active_hot_connected");

        this.addBlockWithVariants(ReactorPartType.PassiveFluidPortForge, variant, isPartCompatible, "fluidport_forge_passive",
                "fluidport_forge_passive_cold_connected",
                "fluidport_forge_passive_hot",
                "fluidport_forge_passive_hot_connected");

        this.addBlockWithVariants(ReactorPartType.PassiveFluidPortMekanism, variant, isPartCompatible, "fluidport_mekanism_passive",
                "fluidport_mekanism_passive_cold_connected",
                "fluidport_mekanism_passive_hot",
                "fluidport_mekanism_passive_hot_connected");

        this.addBlockWithVariants(ReactorPartType.CreativeWaterGenerator, variant, isPartCompatible, "creativewatergenerator");

        this.addBlockWithVariants(ReactorPartType.Controller, variant, isPartCompatible, "controller",
                "controller_on",
                "controller_off");

        this.addBlockWithVariants(ReactorPartType.ComputerPort, variant, isPartCompatible, "computerport",
                "computerport_connected");

        this.addBlockWithVariants(ReactorPartType.RedstonePort, variant, isPartCompatible, "redstoneport",
                "redstoneport_on");

        this.addBlockWithVariants(ReactorPartType.ChargingPortFE, variant, isPartCompatible, "chargingportfe");

        this.setFallbackModelData(ReactorPartType.Casing.ordinal(), BlockFacingsProperty.None.ordinal());
    }
}
