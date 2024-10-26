/*
 *
 * TurbineModelBuilder.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.model;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.model.AbstractMultiblockModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.TurbinePartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.zerocore.lib.block.property.BlockFacingsProperty;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;

import java.util.function.Predicate;

public class TurbineModelBuilder
        extends AbstractMultiblockModelBuilder<TurbinePartType> {

    public TurbineModelBuilder(final TurbineVariant variant) {
        this(variant, ExtremeReactors.ROOT_LOCATION, ExtremeReactors.ROOT_LOCATION
                .appendPath("block", "turbine"));
    }

    private TurbineModelBuilder(TurbineVariant variant, ResourceLocationBuilder root, ResourceLocationBuilder blockRoot) {

        super("turbine", root, blockRoot, blockRoot
                .appendPath(variant.getName())
                .buildWithSuffix("assembledplating"), true);

        final Predicate<TurbinePartType> isPartCompatible = variant::isPartCompatible;

        this.addBlockWithVariants(TurbinePartType.Casing, variant, isPartCompatible, "casing",
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

        this.addBlockWithVariants(TurbinePartType.PassivePowerTapFE, variant, isPartCompatible, "powertapfe_passive");
        this.addBlockWithVariants(TurbinePartType.ActivePowerTapFE, variant, isPartCompatible, "powertapfe_active");

        this.addBlockWithVariants(TurbinePartType.RotorBearing, variant, isPartCompatible, "rotorbearing");

        this.addBlockWithVariants(TurbinePartType.ActiveFluidPortForge, variant, isPartCompatible, "fluidport_forge_active",
                "fluidport_forge_active_cold_connected",
                "fluidport_forge_active_hot",
                "fluidport_forge_active_hot_connected");

        this.addBlockWithVariants(TurbinePartType.PassiveFluidPortForge, variant, isPartCompatible, "fluidport_forge_passive",
                "fluidport_forge_passive_cold_connected",
                "fluidport_forge_passive_hot",
                "fluidport_forge_passive_hot_connected");

        this.addBlockWithVariants(TurbinePartType.CreativeSteamGenerator, variant, isPartCompatible, "creativesteamgenerator");

        this.addBlockWithVariants(TurbinePartType.Controller, variant, isPartCompatible, "controller",
                "controller_on",
                "controller_off");

        this.addBlockWithVariants(TurbinePartType.ComputerPort, variant, isPartCompatible, "computerport",
                "computerport_connected");

        this.addBlockWithVariants(TurbinePartType.RedstonePort, variant, isPartCompatible, "redstoneport",
                "redstoneport_on");

        this.addBlockWithVariants(TurbinePartType.ChargingPortFE, variant, isPartCompatible, "chargingportfe");

        this.setFallbackModelData(TurbinePartType.Casing.ordinal(), BlockFacingsProperty.None.ordinal());
    }
}
