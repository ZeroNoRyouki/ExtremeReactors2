/*
 *
 * AbstractMultiblockBlockStateGenerator.java
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
import it.zerono.mods.zerocore.lib.datagen.provider.multiblock.AbstractCuboidMultiblockBlockStateProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Supplier;

public abstract class AbstractMultiblockBlockStateGenerator
        extends AbstractCuboidMultiblockBlockStateProvider {

    public AbstractMultiblockBlockStateGenerator(final DataGenerator gen, final ExistingFileHelper exFileHelper) {
        super(gen, ExtremeReactors.MOD_ID, exFileHelper);
    }

    protected void genAssembledPlatingModel(final String subFolder) {

        final String fullPath = fullResourceName("", subFolder);

        this.models().cubeAll(fullPath + "assembledplating", this.modLoc(fullPath + /*"casing_single"*/"plating"));
    }

    protected void genController(final Supplier<? extends Block> block, final String subFolder) {
        this.genericPart(block, "controller", subFolder, true, "_on", "_off");
    }

    protected void genFluidPort(final Supplier<? extends Block> block, final String resourceName, final String subFolder) {

        final BlockModelProvider mbp = this.models();
        final String fullResourceName = fullResourceName(resourceName, subFolder);

        this.simpleBlock(block.get(), mbp.cubeAll(fullResourceName + "_cold", this.modLoc(fullResourceName + "_cold")), true);
        this.genericPartSubModels(fullResourceName, "_cold_connected", "_hot", "_hot_connected");
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
        this.genericPartSubModels(fullResourceName, variantSuffixes);
    }

    protected void genericPartSubModels(final String fullResourceName, final String... variantSuffixes) {

        final BlockModelProvider mbp = this.models();

        for (String suffix : variantSuffixes) {
            mbp.cubeAll(fullResourceName + suffix, this.modLoc(fullResourceName + suffix));
        }
    }

    protected <T extends Comparable<T>> void genPropertyVariant(Block block, Property<T> property, T propertyValue,
                                                                ModelFile model) {
        this.genPropertyVariant(block, property, propertyValue, model, 0, 0, false);
    }

    protected <T extends Comparable<T>> void genPropertyVariant(Block block, Property<T> property, T propertyValue,
                                                                ModelFile model, int rotationX, int rotationY) {
        this.genPropertyVariant(block, property, propertyValue, model, rotationX, rotationY, false);
    }

    protected <T extends Comparable<T>> void genPropertyVariant(Block block, Property<T> property, T propertyValue,
                                                                ModelFile model, int rotationX, int rotationY,
                                                                boolean uvLock) {
        this.getVariantBuilder(block)
                .partialState()
                .with(property, propertyValue)
                .modelForState()
                .modelFile(model)
                .rotationX(rotationX)
                .rotationY(rotationY)
                .uvLock(uvLock)
                .addModel();
    }
}
