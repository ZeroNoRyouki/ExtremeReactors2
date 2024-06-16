/*
 *
 * AbstractMultiblockModelsDataProvider.java
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

package it.zerono.mods.extremereactors.datagen.client;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.datagen.provider.client.state.multiblock.CuboidMultiblockStateDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class AbstractMultiblockModelsDataProvider
        extends CuboidMultiblockStateDataProvider {

    protected AbstractMultiblockModelsDataProvider(String name, PackOutput output,
                                                   CompletableFuture<HolderLookup.Provider> lookupProvider,
                                                   ResourceLocationBuilder modLocationRoot) {

        super(name, output, lookupProvider, modLocationRoot);
        this._transparentBlockModel = this.blocksRoot().buildWithSuffix("transparentblock");
    }

    protected ResourceLocationBuilder reactorRoot() {
        return this.blocksRoot().appendPath("reactor");
    }

    protected ResourceLocationBuilder turbineRoot() {
        return this.blocksRoot().appendPath("turbine");
    }

    protected ResourceLocation transparentBlockModel() {
        return this._transparentBlockModel;
    }

    protected ResourceLocation genericPartModel(String modelName, String textureName, String subFolder) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(modelName), "Model name must not be null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(textureName), "Texture name must not be null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(subFolder), "Sub folder name must not be null or empty");

        final var idBuilder = this.blocksRoot().appendPath(subFolder);

        return this.models()
                .model(idBuilder.buildWithSuffix(modelName))
                .cube(idBuilder.buildWithSuffix(textureName));
    }

    protected ResourceLocation genericPartModel(String name, String subFolder) {
        return this.genericPartModel(name, name, subFolder);
    }

    protected ResourceLocation assembledPlatingModel(String modelName, String textureName, String subFolder) {
        return this.genericPartModel(modelName, textureName, subFolder);
    }

    protected ResourceLocation assembledPlatingModel(String subFolder) {
        return this.assembledPlatingModel("assembledplating", "plating", subFolder);
    }

    protected <B extends Block> void genericPart(Supplier<B> block, String name, String subFolder,
                                                 String... subModelSuffixes) {

        final var mainModel = this.genericPartModel(name, subFolder);

        this.singleVariant(block).model(mainModel);
        this.models().item(block).parent(mainModel).build();

        for (final String suffix : subModelSuffixes) {
            this.genericPartModel(name + suffix, subFolder);
        }
    }

    protected <B extends Block> void controller(Supplier<B> block, String subFolder) {
        this.genericPart(block, "controller", subFolder, "_on", "_off");
    }

    protected <B extends Block> void redstonePort(Supplier<B> block, String subFolder) {
        this.genericPart(block, "redstoneport", subFolder, "_on");
    }

    protected <B extends Block> void ioPort(Supplier<B> block, String name, String subFolder,
                                            String inputSuffix, String outputSuffix) {

        this.genericPart(block, name + "_" + inputSuffix, subFolder);
        this.genericPartModel(name + "_assembled_" + inputSuffix, subFolder);
        this.genericPartModel(name + "_assembled_" + inputSuffix + "_connected", subFolder);
        this.genericPartModel(name + "_assembled_" + outputSuffix, subFolder);
        this.genericPartModel(name + "_assembled_" + outputSuffix + "_connected", subFolder);
    }

    protected <B extends Block> void ioPort(Supplier<B> block, String name, String subFolder) {
        this.ioPort(block, name, subFolder, "input", "output");
    }

    protected <B extends Block> void powerPort(Supplier<B> block, String name, String subFolder) {
        this.ioPort(block, name, subFolder);
    }

    protected <B extends Block> void fluidPort(Supplier<B> block, String name, String subFolder) {

        this.genericPart(block, name + "_cold", subFolder);
        this.genericPartModel(name + "_cold_connected", subFolder);
        this.genericPartModel(name + "_hot", subFolder);
        this.genericPartModel(name + "_hot_connected", subFolder);
    }

    protected <B extends Block> void computerPort(final Supplier<? extends Block> block, final String subFolder) {
        this.genericPart(block, "computerport", subFolder, "_connected");
    }

    //region internals

    private final ResourceLocation _transparentBlockModel;

    //endregion
}
