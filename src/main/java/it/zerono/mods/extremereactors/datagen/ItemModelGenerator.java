/*
 *
 * ItemModelGenerator.java
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
import it.zerono.mods.zerocore.lib.datagen.DataGenHelper;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class ItemModelGenerator
        extends ItemModelProvider {

    public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, ExtremeReactors.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        generated(Content.Items.YELLORIUM_DUST, Content.Items.CYANITE_DUST, Content.Items.GRAPHITE_DUST,
                Content.Items.BLUTONIUM_DUST, Content.Items.MAGENTITE_DUST,
                Content.Items.LUDICRITE_DUST, Content.Items.RIDICULITE_DUST, Content.Items.INANITE_DUST,
                Content.Items.YELLORIUM_INGOT, Content.Items.CYANITE_INGOT, Content.Items.GRAPHITE_INGOT,
                Content.Items.BLUTONIUM_INGOT, Content.Items.MAGENTITE_INGOT,
                Content.Items.LUDICRITE_INGOT, Content.Items.RIDICULITE_INGOT, Content.Items.INANITE_INGOT,
                Content.Items.YELLORIUM_NUGGET, Content.Items.BLUTONIUM_NUGGET,
                Content.Items.ANGLESITE_CRYSTAL, Content.Items.BENITOITE_CRYSTAL);
    }

    @Nonnull
    @Override
    public String getName() {
        return ExtremeReactors.MOD_NAME + " Item models";
    }

    @SafeVarargs
    protected final void generated(final Supplier<? extends Item>... items) {

        for (Supplier<? extends Item> item : items) {
            generated(item);
        }
    }

    protected void generated(final Supplier<? extends Item> item) {
        DataGenHelper.generatedItem(this, item.get());
    }
}
