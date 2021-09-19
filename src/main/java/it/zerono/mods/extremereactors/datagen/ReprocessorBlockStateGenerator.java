/*
 *
 * ReprocessorBlockStateGenerator.java
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
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ReprocessorBlockStateGenerator
        extends AbstractMultiblockBlockStateGenerator {

    public ReprocessorBlockStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, exFileHelper);
    }

    //region IDataProvider

    @Override
    protected void registerStatesAndModels() {

        final String folder = "reprocessor";

        this.genAssembledPlatingModel(folder);
        this.genAssembledPlatingModel("assembledplatingio", "platingio", folder);
        this.genFrame(Content.Blocks.REPROCESSOR_CASING, "casing", folder);
        this.genGlass(Content.Blocks.REPROCESSOR_GLASS, "glass", folder);
        this.genController(Content.Blocks.REPROCESSOR_CONTROLLER, folder);
        this.genericPart(Content.Blocks.REPROCESSOR_WASTEINJECTOR, "wasteinjector", folder, true, "_connected");
        this.genericPart(Content.Blocks.REPROCESSOR_FLUIDINJECTOR, "fluidinjector", folder, true, "_connected");
        this.genericPart(Content.Blocks.REPROCESSOR_OUTPUTPORT, "outputport", folder, true, "_connected");
        this.genericPart(Content.Blocks.REPROCESSOR_POWERPORT, "powerport", folder, true);
        this.genericPart(Content.Blocks.REPROCESSOR_COLLECTOR, "collector", folder, true);
    }

    @Override
    public String getName() {
        return ExtremeReactors.MOD_NAME + " Reprocessor blockstates and models";
    }

    //endregion
}
