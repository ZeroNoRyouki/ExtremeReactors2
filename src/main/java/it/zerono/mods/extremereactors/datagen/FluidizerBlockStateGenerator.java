/*
 *
 * FluidizerBlockStateGenerator.java
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

public class FluidizerBlockStateGenerator
        extends AbstractMultiblockBlockStateGenerator {

    public FluidizerBlockStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, exFileHelper);
    }

    //region IDataProvider

    @Override
    protected void registerStatesAndModels() {

        final String folder = "fluidizer";

        this.genAssembledPlatingModel(folder);
        this.genAssembledPlatingModel("assembledplatingio", "plating", folder);
        this.genFrame(Content.Blocks.FLUIDIZER_CASING, "casing", folder);
        this.genGlass(Content.Blocks.FLUIDIZER_GLASS, "glass", folder);
        this.genController(Content.Blocks.FLUIDIZER_CONTROLLER, folder);
        this.genericPart(Content.Blocks.FLUIDIZER_SOLIDINJECTOR, "solidinjector", folder, true, "_connected");
        this.genericPart(Content.Blocks.FLUIDIZER_FLUIDINJECTOR, "fluidinjector", folder, true, "_connected");
        this.genericPart(Content.Blocks.FLUIDIZER_OUTPUTPORT, "outputport", folder, true, "_connected");
        this.genericPart(Content.Blocks.FLUIDIZER_POWERPORT, "powerport", folder, true);
    }

    @Override
    public String getName() {
        return ExtremeReactors.MOD_NAME + " Fluidizer blockstates and models";
    }

    //endregion
}
