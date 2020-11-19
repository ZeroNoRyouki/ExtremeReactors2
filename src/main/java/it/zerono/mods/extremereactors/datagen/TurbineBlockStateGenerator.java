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
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorBladeState;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorShaftState;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
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

        final BlockModelProvider mbp = this.models();

        this.TRANSPARENT_BLOCK_MODEL = mbp.getExistingFile(this.modLoc(ModelProvider.BLOCK_FOLDER + "/transparentblock"));

        this.ROTOR_SHAFT_Z_0C_ID = this.modLoc(fullResourceName("rotor_shaft_z_0c"));
        this.ROTOR_SHAFT_Z_2C_ID = this.modLoc(fullResourceName("rotor_shaft_z_2c"));
        this.ROTOR_SHAFT_Z_2CY_ID = this.modLoc(fullResourceName("rotor_shaft_z_2cy"));
        this.ROTOR_SHAFT_Z_4C_ID = this.modLoc(fullResourceName("rotor_shaft_z_4c"));

        this.ROTOR_BLADE_Z_ID = this.modLoc(fullResourceName("rotor_blade_z"));
        this.ROTOR_BLADE_ZY_ID = this.modLoc(fullResourceName("rotor_blade_zy"));
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

        this.genericPart(Content.Blocks.TURBINE_ROTORBEARING_BASIC, "bearing", variant, true);
        this.genShaft(Content.Blocks.TURBINE_ROTORSHAFT_BASIC, "rotorshaft", variant);
        this.genBlade(Content.Blocks.TURBINE_ROTORBLADE_BASIC, "rotorblade", variant);

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

        this.genericPart(Content.Blocks.TURBINE_ROTORBEARING_REINFORCED, "bearing", variant, true);
        this.genShaft(Content.Blocks.TURBINE_ROTORSHAFT_REINFORCED, "rotorshaft", variant);
        this.genBlade(Content.Blocks.TURBINE_ROTORBLADE_REINFORCED, "rotorblade", variant);

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

    protected void genShaft(final Supplier<? extends Block> block, final String resourceName, final String subFolder) {
        this.genShaft(block.get(), resourceName, subFolder);
    }

    protected void genShaft(final Block block, final String resourceName, final String subFolder) {

        final ResourceLocation texture0 = this.modLoc(fullResourceName(resourceName, subFolder));
        final ResourceLocation texture1 = this.modLoc(fullResourceName(resourceName, subFolder));

        final BlockModelProvider mbp = this.models();
        final ModelFile z0c = mbp.withExistingParent(fullResourceName(resourceName + "_z_0c", subFolder), this.ROTOR_SHAFT_Z_0C_ID).texture("0", texture0).texture("1", texture1);
        final ModelFile z2c = mbp.withExistingParent(fullResourceName(resourceName + "_z_2c", subFolder), this.ROTOR_SHAFT_Z_2C_ID).texture("0", texture0).texture("1", texture1);
        final ModelFile z2cy = mbp.withExistingParent(fullResourceName(resourceName + "_z_2cy", subFolder), this.ROTOR_SHAFT_Z_2CY_ID).texture("0", texture0).texture("1", texture1);
        final ModelFile z4c = mbp.withExistingParent(fullResourceName(resourceName + "_z_4c", subFolder), this.ROTOR_SHAFT_Z_4C_ID).texture("0", texture0).texture("1", texture1);

        this.simpleBlockItem(block, z0c);

        this.genShaftVariant(block, RotorShaftState.HIDDEN, this.TRANSPARENT_BLOCK_MODEL, 0, 0);

        this.genShaftVariant(block, RotorShaftState.Y_NOBLADES, z0c, -90, 0);
        this.genShaftVariant(block, RotorShaftState.Y_X, z2c, -90, 0);
        this.genShaftVariant(block, RotorShaftState.Y_Z, z2c, -90, 90);
        this.genShaftVariant(block, RotorShaftState.Y_XZ, z4c, -90, 0);

        this.genShaftVariant(block, RotorShaftState.X_NOBLADES, z0c, 0, -90);
        this.genShaftVariant(block, RotorShaftState.X_Y, z2cy, 0, 90);
        this.genShaftVariant(block, RotorShaftState.X_Z, z2c, 0, -90);
        this.genShaftVariant(block, RotorShaftState.X_YZ, z4c, 0, -90);

        this.genShaftVariant(block, RotorShaftState.Z_NOBLADES, z0c, 0, 0);
        this.genShaftVariant(block, RotorShaftState.Z_Y, z2cy, 0, 0);
        this.genShaftVariant(block, RotorShaftState.Z_X, z2c, 0, 0);
        this.genShaftVariant(block, RotorShaftState.Z_XY, z4c, 0, 0);
    }

    protected void genBlade(final Supplier<? extends Block> block, final String resourceName, final String subFolder) {
        this.genBlade(block.get(), resourceName, subFolder);
    }

    protected void genBlade(final Block block, final String resourceName, final String subFolder) {

        final ResourceLocation textureId0 = this.modLoc(fullResourceName(resourceName, subFolder));
        final ResourceLocation textureId1 = this.modLoc(fullResourceName(resourceName, subFolder));

        final BlockModelProvider mbp = this.models();
        final ModelFile z = mbp.withExistingParent(fullResourceName(resourceName + "_z", subFolder), this.ROTOR_BLADE_Z_ID).texture("0", textureId0).texture("1", textureId1);
        final ModelFile zy = mbp.withExistingParent(fullResourceName(resourceName + "_zy", subFolder), this.ROTOR_BLADE_ZY_ID).texture("0", textureId0).texture("1", textureId1);

        this.simpleBlockItem(block, z);

        this.genBladeVariant(block, RotorBladeState.HIDDEN, this.TRANSPARENT_BLOCK_MODEL, 0, 0);

        this.genBladeVariant(block, RotorBladeState.Y_X_POS, z, 90, 0);
        this.genBladeVariant(block, RotorBladeState.Y_X_NEG, z, -90, 180);
        this.genBladeVariant(block, RotorBladeState.Y_Z_POS, z, 90, 90);
        this.genBladeVariant(block, RotorBladeState.Y_Z_NEG, z, -90, -90);
        this.genBladeVariant(block, RotorBladeState.X_Y_POS, zy, 0, 90);
        this.genBladeVariant(block, RotorBladeState.X_Y_NEG, zy, -180, -90);
        this.genBladeVariant(block, RotorBladeState.X_Z_POS, z, 0, 90);
        this.genBladeVariant(block, RotorBladeState.X_Z_NEG, z, 0, -90);
        this.genBladeVariant(block, RotorBladeState.Z_Y_POS, zy, 0, 180);
        this.genBladeVariant(block, RotorBladeState.Z_Y_NEG, zy, 180, 0);
        this.genBladeVariant(block, RotorBladeState.Z_X_POS, z, 180, 0);
        this.genBladeVariant(block, RotorBladeState.Z_X_NEG, z, 180, 180);
    }

    private void genShaftVariant(final Block block, final RotorShaftState state, final ModelFile model,
                                 final int rotateX, final int rotateY) {
        this.genPropertyVariant(block, TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, state, model, rotateX, rotateY, false);
    }

    private void genBladeVariant(final Block block, final RotorBladeState state, final ModelFile model,
                                 final int rotateX, final int rotateY) {
        this.genPropertyVariant(block, TurbineRotorComponentBlock.ROTOR_BLADE_STATE, state, model, rotateX, rotateY, false);
    }

    protected static String fullResourceName(final String resourceName) {
        return ModelProvider.BLOCK_FOLDER + "/turbine/" + resourceName;
    }

    private final ModelFile TRANSPARENT_BLOCK_MODEL;

    private final ResourceLocation ROTOR_SHAFT_Z_0C_ID;
    private final ResourceLocation ROTOR_SHAFT_Z_2C_ID;
    private final ResourceLocation ROTOR_SHAFT_Z_2CY_ID;
    private final ResourceLocation ROTOR_SHAFT_Z_4C_ID;

    private final ResourceLocation ROTOR_BLADE_Z_ID;
    private final ResourceLocation ROTOR_BLADE_ZY_ID;

    //endregion
}
