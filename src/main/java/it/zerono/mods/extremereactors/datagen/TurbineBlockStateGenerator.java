/*
 *
 * TurbineBlockStateGenerator.java
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

package it.zerono.mods.extremereactors.datagen;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineRotorComponentBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorShaftState;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class TurbineBlockStateGenerator
        extends AbstractMultiblockBlockStateGenerator {

    public TurbineBlockStateGenerator(final DataGenerator gen, final ExistingFileHelper exFileHelper) {
        super(gen, exFileHelper);
    }

    //region IDataProvider

    @Nonnull
    @Override
    public String getName() {
        return ExtremeReactors.MOD_NAME + " Turbine blockstates and models";
    }

    @Override
    protected void registerStatesAndModels() {

        String variant;

        //region basic

        variant = "turbine/basic";
        this.genAssembledPlatingModel(variant);
        this.genFrame(Content.Blocks.TURBINE_CASING_BASIC, "casing", variant);
        this.genGlass(Content.Blocks.TURBINE_GLASS_BASIC, "glass", variant);
        this.genController(Content.Blocks.TURBINE_CONTROLLER_BASIC, variant);

        //TURBINE_ROTORBEARING_BASIC
        //TURBINE_ROTORSHAFT_BASIC
        //TURBINE_ROTORBLADE_BASIC

        this.genRedstonePort(Content.Blocks.TURBINE_REDSTONEPORT_BASIC, variant);
        this.genericPart(Content.Blocks.TURBINE_POWERTAP_FE_ACTIVE_BASIC, "powertap_fe_active", variant, true);
        this.genericPart(Content.Blocks.TURBINE_POWERTAP_FE_PASSIVE_BASIC, "powertap_fe_passive", variant, true);
        this.genCoolantPort(Content.Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC, "coolantport_forge_active", variant);
        this.genCoolantPort(Content.Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC, "coolantport_forge_passive", variant);

        //endregion
        //region reinforced

        variant = "turbine/reinforced";
        this.genAssembledPlatingModel(variant);
        this.genFrame(Content.Blocks.TURBINE_CASING_REINFORCED, "casing", variant);
        this.genGlass(Content.Blocks.TURBINE_GLASS_REINFORCED, "glass", variant);
        this.genController(Content.Blocks.TURBINE_CONTROLLER_REINFORCED, variant);

        //TURBINE_ROTORBEARING_REINFORCED
        //TURBINE_ROTORSHAFT_REINFORCED
        //TURBINE_ROTORBLADE_REINFORCED

        this.genRedstonePort(Content.Blocks.TURBINE_REDSTONEPORT_REINFORCED, variant);
        this.genericPart(Content.Blocks.TURBINE_POWERTAP_FE_ACTIVE_REINFORCED, "powertap_fe_active", variant, true);
        this.genericPart(Content.Blocks.TURBINE_POWERTAP_FE_PASSIVE_REINFORCED, "powertap_fe_passive", variant, true);
        this.genCoolantPort(Content.Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED, "coolantport_forge_active", variant);
        this.genCoolantPort(Content.Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED, "coolantport_forge_passive", variant);
        this.genComputerPort(Content.Blocks.TURBINE_COMPUTERPORT_REINFORCED, variant);

        //endregion
    }

    //endregion
    //region internals
//
//    protected void genShaft(final Supplier<? extends Block> block, final String resourceName, final String subFolder) {
//        this.genShaft(block.get(), resourceName, subFolder);
//    }
//
//    protected void genShaft(final Block block, final String resourceName, final String subFolder) {
//
//        final BlockModelProvider mbp = this.models();
//        final String fullResourceName = fullResourceName(resourceName, subFolder);
//
//        final ResourceLocation hidden = this.modLoc(ModelProvider.BLOCK_FOLDER + "/transparentblock");
//        final ResourceLocation rotor_shaft_z_0c = this.modLoc(fullResourceName("rotor_shaft_z_0c"));
//        final ResourceLocation rotor_shaft_z_2c = this.modLoc(fullResourceName("rotor_shaft_z_2c"));
//        final ResourceLocation rotor_shaft_z_2cy = this.modLoc(fullResourceName("rotor_shaft_z_2cy"));
//        final ResourceLocation rotor_shaft_z_4c = this.modLoc(fullResourceName("rotor_shaft_z_4c"));
//
//        ModelFile model;
//
//        // RotorShaftState.HIDDEN
//
//        model = mbp.cubeAll(hidden, glass0);
//        this.genShaftVariant(block, RotorShaftState.HIDDEN, model);
//        this.simpleBlockItem(block, model);
//
//        // BlockFacingsProperty.Face_* / glass_c1
//
//        model = mbp.cube(fullResourceName + "_c1", glass0, glass15, glass1, glass1, glass1, glass1).texture("particle", glass0);
//        this.addGlassVariant(block, BlockFacingsProperty.Face_U, model);
//        this.addGlassVariant(block, BlockFacingsProperty.Face_D, model, 180, 0);
//        this.addGlassVariant(block, BlockFacingsProperty.Face_N, model, 90, 0);
//        this.addGlassVariant(block, BlockFacingsProperty.Face_S, model, -90, 0);
//        this.addGlassVariant(block, BlockFacingsProperty.Face_W, model, 90, -90);
//        this.addGlassVariant(block, BlockFacingsProperty.Face_E, model, 90, 90);
//
//        // BlockFacingsProperty.Angle_* / glass_c2angle
//
//        model = mbp.cube(fullResourceName + "_c2angle", glass8, glass15, glass5, glass9, glass15, glass1).texture("particle", glass0);
//        this.addGlassVariant(block, BlockFacingsProperty.Angle_EU, model);
//        this.addGlassVariant(block, BlockFacingsProperty.Angle_UW, model, 0, 180);
//        this.addGlassVariant(block, BlockFacingsProperty.Angle_DE, model, 180, 0);
//        this.addGlassVariant(block, BlockFacingsProperty.Angle_DW, model, 180, 180);
//        this.addGlassVariant(block, BlockFacingsProperty.Angle_NU, model, 0, -90);
//        this.addGlassVariant(block, BlockFacingsProperty.Angle_SU, model, 0, 90);
//        this.addGlassVariant(block, BlockFacingsProperty.Angle_DN, model, 180, -90);
//        this.addGlassVariant(block, BlockFacingsProperty.Angle_DS, model, 180, 90);
//        this.addGlassVariant(block, BlockFacingsProperty.Angle_EN, model, 90, 0);
//        this.addGlassVariant(block, BlockFacingsProperty.Angle_ES, model, -90, 0);
//        this.addGlassVariant(block, BlockFacingsProperty.Angle_NW, model, 90, -90);
//        this.addGlassVariant(block, BlockFacingsProperty.Angle_SW, model, -90, 90);
//
//        // BlockFacingsProperty.Opposite_* / glass_c2
//
//        model = mbp.cube(fullResourceName + "_c2", glass15, glass15, glass3, glass3, glass3, glass3).texture("particle", glass0);
//        this.addGlassVariant(block, BlockFacingsProperty.Opposite_DU, model);
//        this.addGlassVariant(block, BlockFacingsProperty.Opposite_EW, model, -90, 90);
//        this.addGlassVariant(block, BlockFacingsProperty.Opposite_NS, model, -90, 0);
//
//        // BlockFacingsProperty.CShape_* / glass_c3t1/glass_c3t2
//
//        model = mbp.cube(fullResourceName + "_c3t1", glass15, glass15, glass11, glass7, glass3, glass15).texture("particle", glass0);
//        this.addGlassVariant(block, BlockFacingsProperty.CShape_DUW, model);
//        this.addGlassVariant(block, BlockFacingsProperty.CShape_DSU, model, 0, -90);
//        this.addGlassVariant(block, BlockFacingsProperty.CShape_DEU, model, 0, -180);
//        this.addGlassVariant(block, BlockFacingsProperty.CShape_DNU, model, 0, 90);
//        this.addGlassVariant(block, BlockFacingsProperty.CShape_NSW, model, -90, 0);
//        this.addGlassVariant(block, BlockFacingsProperty.CShape_ENS, model, -90, 180);
//
//        model = mbp.cube(fullResourceName + "_c3t2", glass12, glass15, glass13, glass13, glass15, glass15).texture("particle", glass0);
//        this.addGlassVariant(block, BlockFacingsProperty.CShape_EUW, model);
//        this.addGlassVariant(block, BlockFacingsProperty.CShape_ESW, model, -90, 0);
//        this.addGlassVariant(block, BlockFacingsProperty.CShape_DEW, model, 180, 0);
//        this.addGlassVariant(block, BlockFacingsProperty.CShape_ENW, model, 90, 0);
//        this.addGlassVariant(block, BlockFacingsProperty.CShape_NSU, model, 0, 90);
//        this.addGlassVariant(block, BlockFacingsProperty.CShape_DNS, model, 180, 90);
//
//        // BlockFacingsProperty.Corner_* / glass_c3angle
//
//        model = mbp.cube(fullResourceName + "_c3angle", glass9, glass15, glass5, glass15, glass15, glass9).texture("particle", glass0);
//        this.addGlassVariant(block, BlockFacingsProperty.Corner_ESU, model);
//        this.addGlassVariant(block, BlockFacingsProperty.Corner_SUW, model, 0, 90);
//        this.addGlassVariant(block, BlockFacingsProperty.Corner_ENU, model, 90, 0);
//        this.addGlassVariant(block, BlockFacingsProperty.Corner_DEN, model, 180, 0);
//        this.addGlassVariant(block, BlockFacingsProperty.Corner_DES, model, 270, 0);
//        this.addGlassVariant(block, BlockFacingsProperty.Corner_DSW, model, 180, -180);
//        this.addGlassVariant(block, BlockFacingsProperty.Corner_DNW, model, -90, -180);
//        this.addGlassVariant(block, BlockFacingsProperty.Corner_NUW, model, 90, -90);
//
//        // BlockFacingsProperty.Pipe_* / glass_c4x
//
//        model = mbp.cube(fullResourceName + "_c4x", glass15, glass15, glass15, glass15, glass15, glass15).texture("particle", glass0);
//        this.addGlassVariant(block, BlockFacingsProperty.Pipe_DEUW, model);
//        this.addGlassVariant(block, BlockFacingsProperty.Pipe_ENSW, model, 90, 0);
//        this.addGlassVariant(block, BlockFacingsProperty.Pipe_DNSU, model, 0, 90);
//
//        // BlockFacingsProperty.Misc_* / glass_c4angle
//
//        model = mbp.cube(fullResourceName + "_c4angle", glass15, glass15, glass15, glass7, glass11, glass15).texture("particle", glass0);
//        this.addGlassVariant(block, BlockFacingsProperty.Misc_DNUW, model);
//        this.addGlassVariant(block, BlockFacingsProperty.Misc_DNSW, model, 90, 0);
//        this.addGlassVariant(block, BlockFacingsProperty.Misc_NSUW, model, -90, 0);
//        this.addGlassVariant(block, BlockFacingsProperty.Misc_DSUW, model, 0, -90);
//        this.addGlassVariant(block, BlockFacingsProperty.Misc_DENU, model, 0, 90);
//        this.addGlassVariant(block, BlockFacingsProperty.Misc_DESU, model, 0, 180);
//        this.addGlassVariant(block, BlockFacingsProperty.Misc_ENSU, model, -90, 180);
//        this.addGlassVariant(block, BlockFacingsProperty.Misc_DENS, model, 90, 180);
//        this.addGlassVariant(block, BlockFacingsProperty.Misc_ESUW, model, -90, -90);
//        this.addGlassVariant(block, BlockFacingsProperty.Misc_DESW, model, -270, -90);
//        this.addGlassVariant(block, BlockFacingsProperty.Misc_ENUW, model, -90, 90);
//        this.addGlassVariant(block, BlockFacingsProperty.Misc_DENW, model, -270, -270);
//
//        // BlockFacingsProperty.PipeEnd_*/All / glass_c5-6
//
//        model = mbp.cubeAll(fullResourceName + "_c5-6", glass15);
//        this.addGlassVariant(block, BlockFacingsProperty.PipeEnd_DENSW, model);
//        this.addGlassVariant(block, BlockFacingsProperty.PipeEnd_DESUW, model);
//        this.addGlassVariant(block, BlockFacingsProperty.PipeEnd_DNSUW, model);
//        this.addGlassVariant(block, BlockFacingsProperty.PipeEnd_DENSU, model);
//        this.addGlassVariant(block, BlockFacingsProperty.PipeEnd_DENUW, model);
//        this.addGlassVariant(block, BlockFacingsProperty.PipeEnd_ENSUW, model);
//        this.addGlassVariant(block, BlockFacingsProperty.All, model);
//    }

    private void genShaftVariant(Block block, RotorShaftState propertyValue, ModelFile model) {
        this.genPropertyVariant(block, TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, propertyValue, model, 0, 0, false);
    }

    private void genShaftVariant(Block block, RotorShaftState propertyValue, ModelFile model,
                                 int rotationX, int rotationY) {
        this.genPropertyVariant(block, TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, propertyValue, model, rotationX, rotationY, false);
    }

    private void genShaftVariant(Block block, RotorShaftState propertyValue, ModelFile model,
                                 int rotationX, int rotationY, boolean uvLock) {
        this.genPropertyVariant(block, TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, propertyValue, model, rotationX, rotationY, uvLock);
    }

    protected static String fullResourceName(final String resourceName) {
        return ModelProvider.BLOCK_FOLDER + "/turbine/" + resourceName;
    }

    //endregion
}
