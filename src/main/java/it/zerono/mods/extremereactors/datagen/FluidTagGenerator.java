/*
 *
 * FluidTagGenerator.java
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
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class FluidTagGenerator
    extends FluidTagsProvider {

    public FluidTagGenerator(final DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, ExtremeReactors.MOD_ID, existingFileHelper);
    }

    //region IDataProvider

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return ExtremeReactors.MOD_NAME + " Fluids Tags";
    }

    //endregion
    //region FluidTagsProvider

    @Override
    protected void addTags() {

        this.tag(ContentTags.Fluids.STEAM, Content.Fluids.STEAM_SOURCE, Content.Fluids.STEAM_FLOWING);
        this.tag(ContentTags.Fluids.YELLORIUM, Content.Fluids.YELLORIUM_SOURCE, Content.Fluids.YELLORIUM_FLOWING);
        this.tag(ContentTags.Fluids.CYANITE, Content.Fluids.CYANITE_SOURCE, Content.Fluids.CYANITE_FLOWING);
        this.tag(ContentTags.Fluids.BLUTONIUM, Content.Fluids.BLUTONIUM_SOURCE, Content.Fluids.BLUTONIUM_FLOWING);
        this.tag(ContentTags.Fluids.MAGENTITE, Content.Fluids.MAGENTITE_SOURCE, Content.Fluids.MAGENTITE_FLOWING);
        this.tag(ContentTags.Fluids.VERDERIUM, Content.Fluids.VERDERIUM_SOURCE, Content.Fluids.VERDERIUM_FLOWING);
        this.tag(ContentTags.Fluids.ROSSINITE, Content.Fluids.ROSSINITE_SOURCE, Content.Fluids.ROSSINITE_FLOWING);
    }

    //endregion
    //region internals

    private void tag(final TagKey<Fluid> tag, final Supplier<? extends FlowingFluid> source,
                     final Supplier<? extends FlowingFluid> flowing) {
        this.tag(tag).add(source.get(), flowing.get());
    }

    //endregion
}
