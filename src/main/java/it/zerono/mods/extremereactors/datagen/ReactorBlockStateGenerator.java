/*
 *
 * ReactorBlockStateGenerator.java
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
import it.zerono.mods.zerocore.lib.datagen.provider.multiblock.AbstractCuboidMultiblockBlockStateProvider;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

@SuppressWarnings("SpellCheckingInspection")
public class ReactorBlockStateGenerator
        extends AbstractCuboidMultiblockBlockStateProvider {

    public ReactorBlockStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, ExtremeReactors.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        String variant;

        //region basic
        variant = "reactor/basic";
        this.genAssembledPlatingModel(variant);
        this.genFrame(Content.Blocks.REACTOR_CASING_BASIC, "casing", variant);
        this.genGlass(Content.Blocks.REACTOR_GLASS_BASIC, "glass", variant);
        this.genController(Content.Blocks.REACTOR_CONTROLLER_BASIC, variant);
        this.genControlRod(Content.Blocks.REACTOR_CONTROLROD_BASIC, variant);
        this.genFuelRod(Content.Blocks.REACTOR_FUELROD_BASIC, variant);
        this.genSolidAccessPort(Content.Blocks.REACTOR_SOLID_ACCESSPORT_BASIC, variant);
        this.genRedstonePort(Content.Blocks.REACTOR_REDSTONEPORT_BASIC, variant);
        this.genericPart(Content.Blocks.REACTOR_POWERTAP_FE_ACTIVE_BASIC, "powertap_fe_active", variant, true);
        this.genericPart(Content.Blocks.REACTOR_POWERTAP_FE_PASSIVE_BASIC, "powertap_fe_passive", variant, true);
        //endregion

        //region reinforced
        variant = "reactor/reinforced";
        this.genAssembledPlatingModel(variant);
        this.genFrame(Content.Blocks.REACTOR_CASING_REINFORCED, "casing", variant);
        this.genGlass(Content.Blocks.REACTOR_GLASS_REINFORCED, "glass", variant);
        this.genController(Content.Blocks.REACTOR_CONTROLLER_REINFORCED, variant);
        this.genControlRod(Content.Blocks.REACTOR_CONTROLROD_REINFORCED, variant);
        this.genFuelRod(Content.Blocks.REACTOR_FUELROD_REINFORCED, variant);
        this.genSolidAccessPort(Content.Blocks.REACTOR_SOLID_ACCESSPORT_REINFORCED, variant);
        this.genRedstonePort(Content.Blocks.REACTOR_REDSTONEPORT_REINFORCED, variant);
        this.genericPart(Content.Blocks.REACTOR_POWERTAP_FE_ACTIVE_REINFORCED, "powertap_fe_active", variant, true);
        this.genericPart(Content.Blocks.REACTOR_POWERTAP_FE_PASSIVE_REINFORCED, "powertap_fe_passive", variant, true);
        this.genComputerPort(Content.Blocks.REACTOR_COMPUTERPORT_REINFORCED, variant);
        //endregion

    }

    @Nonnull
    @Override
    public String getName() {
        return ExtremeReactors.MOD_NAME + " Reactor blockstates and models";
    }

    protected void genAssembledPlatingModel(final String subFolder) {

        final String fullPath = fullResourceName("", subFolder);

        this.models().cubeAll(fullPath + "assembledplating", this.modLoc(fullPath + "casing_single"));
    }

    protected void genController(final Supplier<? extends Block> block, final String subFolder) {
        this.genericPart(block, "controller", subFolder, true, "_on", "_off");
    }

    protected void genControlRod(final Supplier<? extends Block> block, final String subFolder) {
        this.genericPart(block, "controlrod", subFolder, true);
    }

    protected void genFuelRod(final Supplier<? extends Block> block, final String subFolder) {

        final BlockModelProvider mbp = this.models();
        final String fullResourceName = fullResourceName("fuelrod", subFolder);
        final ResourceLocation vertical = this.modLoc(fullResourceName + "_vertical");
        final ResourceLocation cap = this.modLoc(fullResourceName + "_cap");

        // fuel rod variant model
        final ModelFile model = mbp.withExistingParent(fullResourceName, this.modLoc("block/reactor/fuel_rod"))
                .texture("cap", cap)
                .texture("side", vertical)
                .texture("particle", vertical);

        // fuel rod variant blockstate
        this.getVariantBuilder(block.get())
                .partialState().with(BlockStateProperties.AXIS, Direction.Axis.X)
                    .modelForState()
                        .modelFile(model)
                        .rotationX(90)
                        .rotationY(90)
                        .uvLock(false)
                        .addModel()
                .partialState().with(BlockStateProperties.AXIS, Direction.Axis.Y)
                    .modelForState()
                        .modelFile(model)
                        .rotationX(0)
                        .rotationY(0)
                        .uvLock(false)
                        .addModel()
                .partialState().with(BlockStateProperties.AXIS, Direction.Axis.Z)
                    .modelForState()
                        .modelFile(model)
                        .rotationX(90)
                        .rotationY(0)
                        .uvLock(false)
                        .addModel();

        this.simpleBlockItem(block.get(), model);
    }

    protected void genSolidAccessPort(final Supplier<? extends Block> block, final String subFolder) {

        final BlockModelProvider mbp = this.models();
        final String fullResourceName = fullResourceName("accessport_solid", subFolder);

        this.simpleBlock(block.get(), mbp.cubeAll(fullResourceName + "_in", this.modLoc(fullResourceName + "_in")), true);
        this.genericPartSubModels(fullResourceName, "_in_connected", "_out", "_out_connected");
    }

    protected void genRedstonePort(final Supplier<? extends Block> block, final String subFolder) {
        this.genericPart(block, "redstoneport", subFolder, true, "_on");
    }

    protected void genComputerPort(final Supplier<? extends Block> block, final String subFolder) {
        this.genericPart(block, "computerport", subFolder, true, "_connected");
    }

    protected void genericPart(final Supplier<? extends Block> block, final String resourceName,
                               final String subFolder, final String... variantSuffixes) {
        this.genericPart(block, resourceName, subFolder, false, variantSuffixes);
    }

    protected void genericPart(final Supplier<? extends Block> block, final String resourceName,
                               final String subFolder, final boolean genStandardItem,
                               final String... variantSuffixes) {

        final BlockModelProvider mbp = this.models();
        final String fullResourceName = fullResourceName(resourceName, subFolder);

        this.simpleBlock(block.get(), mbp.cubeAll(fullResourceName, this.modLoc(fullResourceName)), genStandardItem);
        /*
        for (String suffix : variantSuffixes) {
            mbp.cubeAll(fullResourceName + suffix, this.modLoc(fullResourceName + suffix));
        }*/
        this.genericPartSubModels(fullResourceName, variantSuffixes);
    }

    protected void genericPartSubModels(final String fullResourceName, final String... variantSuffixes) {

        final BlockModelProvider mbp = this.models();

        for (String suffix : variantSuffixes) {
            mbp.cubeAll(fullResourceName + suffix, this.modLoc(fullResourceName + suffix));
        }
    }
}
