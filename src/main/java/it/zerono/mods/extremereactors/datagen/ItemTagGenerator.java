/*
 *
 * ItemTagGenerator.java
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
import it.zerono.mods.extremereactors.gamecontent.Content.Items;
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ItemTagGenerator
        extends ItemTagsProvider {

    public ItemTagGenerator(final DataGenerator generator, final BlockTagsProvider blockTagProvider,
                            @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, blockTagProvider, ExtremeReactors.MOD_ID, existingFileHelper);
    }

    //region IDataProvider

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return ExtremeReactors.MOD_NAME + " Items Tags";
    }

    //endregion
    //region BlockTagsProvider

    @Override
    protected void addTags() {

        this.build(ContentTags.Items.ORE_YELLORITE, Items.YELLORITE_ORE_BLOCK);

        this.build(ContentTags.Items.INGOTS_YELLORIUM, Items.YELLORIUM_INGOT);
        this.build(ContentTags.Items.INGOTS_CYANITE, Items.CYANITE_INGOT);
        this.build(ContentTags.Items.INGOTS_GRAPHITE, Items.GRAPHITE_INGOT);
        this.build(ContentTags.Items.INGOTS_BLUTONIUM, Items.BLUTONIUM_INGOT);
        this.build(ContentTags.Items.INGOTS_MAGENTITE, Items.MAGENTITE_INGOT);
        this.build(ContentTags.Items.INGOTS_LUDICRITE, Items.LUDICRITE_INGOT);
        this.build(ContentTags.Items.INGOTS_RIDICULITE, Items.RIDICULITE_INGOT);
        this.build(ContentTags.Items.INGOTS_INANITE, Items.INANITE_INGOT);
        this.build(ContentTags.Items.INGOTS_INSANITE, Items.INSANITE_INGOT);

        this.build(Tags.Items.NUGGETS, Items.YELLORIUM_NUGGET, Items.BLUTONIUM_NUGGET);
        this.build(ContentTags.Items.NUGGETS_YELLORIUM, Items.YELLORIUM_NUGGET);
        this.build(ContentTags.Items.NUGGETS_BLUTONIUM, Items.BLUTONIUM_NUGGET);

        this.build(ContentTags.Items.DUSTS_YELLORIUM, Items.YELLORIUM_DUST);
        this.build(ContentTags.Items.DUSTS_CYANITE, Items.CYANITE_DUST);
        this.build(ContentTags.Items.DUSTS_GRAPHITE, Items.GRAPHITE_DUST);
        this.build(ContentTags.Items.DUSTS_BLUTONIUM, Items.BLUTONIUM_DUST);
        this.build(ContentTags.Items.DUSTS_MAGENTITE, Items.MAGENTITE_DUST);
        this.build(ContentTags.Items.DUSTS_LUDICRITE, Items.LUDICRITE_DUST);
        this.build(ContentTags.Items.DUSTS_RIDICULITE, Items.RIDICULITE_DUST);
        this.build(ContentTags.Items.DUSTS_INANITE, Items.INANITE_DUST);
        this.build(ContentTags.Items.DUSTS_INSANITE, Items.INSANITE_DUST);

        this.build(ContentTags.Items.BLOCKS_YELLORIUM, Items.YELLORIUM_BLOCK);
        this.build(ContentTags.Items.BLOCKS_CYANITE, Items.CYANITE_BLOCK);
        this.build(ContentTags.Items.BLOCKS_GRAPHITE, Items.GRAPHITE_BLOCK);
        this.build(ContentTags.Items.BLOCKS_BLUTONIUM, Items.BLUTONIUM_BLOCK);
        this.build(ContentTags.Items.BLOCKS_MAGENTITE, Items.MAGENTITE_BLOCK);
        this.build(ContentTags.Items.BLOCKS_LUDICRITE, Items.LUDICRITE_BLOCK);
        this.build(ContentTags.Items.BLOCKS_RIDICULITE, Items.RIDICULITE_BLOCK);
        this.build(ContentTags.Items.BLOCKS_INANITE, Items.INANITE_BLOCK);
        this.build(ContentTags.Items.BLOCKS_INSANITE, Items.INSANITE_BLOCK);

        this.build(TagsHelper.TAG_WRENCH, Items.WRENCH);

        this.build(Tags.Items.INGOTS, Items.YELLORIUM_INGOT, Items.CYANITE_INGOT, Items.GRAPHITE_INGOT, Items.BLUTONIUM_INGOT,
                Items.MAGENTITE_INGOT);
        this.build(Tags.Items.DUSTS, Items.YELLORIUM_DUST, Items.CYANITE_DUST, Items.GRAPHITE_DUST, Items.BLUTONIUM_DUST,
                Items.MAGENTITE_DUST);

        this.build(TagsHelper.ITEMS.createKey("forge:dusts/uranium"), Items.YELLORIUM_DUST);
        this.build(TagsHelper.ITEMS.createKey("forge:ingots/uranium"), Items.YELLORIUM_INGOT);
        this.build(TagsHelper.ITEMS.createKey("forge:dusts/plutonium"), Items.BLUTONIUM_DUST);
        this.build(TagsHelper.ITEMS.createKey("forge:ingots/plutonium"), Items.BLUTONIUM_INGOT);
        this.build(TagsHelper.ITEMS.createKey("forge:ores/uranium"), Items.YELLORITE_ORE_BLOCK);

        this.build(ContentTags.Items.USING_REACTOR_CASING_BASIC,
                Content.Items.REACTOR_CONTROLLER_BASIC,
                Content.Items.REACTOR_CONTROLROD_BASIC,
                Content.Items.REACTOR_SOLID_ACCESSPORT_BASIC,
                Content.Items.REACTOR_POWERTAP_FE_ACTIVE_BASIC,
                Content.Items.REACTOR_POWERTAP_FE_PASSIVE_BASIC,
                Content.Items.REACTOR_REDSTONEPORT_BASIC,
                Content.Items.REACTOR_CHARGINGPORT_FE_BASIC);

        this.build(ContentTags.Items.USING_REACTOR_CASING_REINFORCED,
                Content.Items.REACTOR_CONTROLLER_REINFORCED,
                Content.Items.REACTOR_CONTROLROD_REINFORCED,
                Content.Items.REACTOR_SOLID_ACCESSPORT_REINFORCED,
                Content.Items.REACTOR_POWERTAP_FE_ACTIVE_REINFORCED,
                Content.Items.REACTOR_POWERTAP_FE_PASSIVE_REINFORCED,
                Content.Items.REACTOR_FLUIDPORT_FORGE_ACTIVE_REINFORCED,
                Content.Items.REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED,
                Content.Items.REACTOR_FLUIDPORT_MEKANISM_PASSIVE_REINFORCED,
                Content.Items.REACTOR_REDSTONEPORT_REINFORCED,
                Content.Items.REACTOR_COMPUTERPORT_REINFORCED,
                Content.Items.REACTOR_CHARGINGPORT_FE_REINFORCED);

        this.build(ContentTags.Items.USING_TURBINE_CASING_BASIC,
                Content.Items.TURBINE_CONTROLLER_BASIC,
                Content.Items.TURBINE_POWERTAP_FE_ACTIVE_BASIC,
                Content.Items.TURBINE_POWERTAP_FE_PASSIVE_BASIC,
                Content.Items.TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC,
                Content.Items.TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC,
                Content.Items.TURBINE_CHARGINGPORT_FE_BASIC);

        this.build(ContentTags.Items.USING_TURBINE_CASING_REINFORCED,
                Content.Items.TURBINE_CONTROLLER_REINFORCED,
                Content.Items.TURBINE_POWERTAP_FE_ACTIVE_REINFORCED,
                Content.Items.TURBINE_POWERTAP_FE_PASSIVE_REINFORCED,
                Content.Items.TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED,
                Content.Items.TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED,
                Content.Items.TURBINE_COMPUTERPORT_REINFORCED,
                Content.Items.TURBINE_CHARGINGPORT_FE_REINFORCED);
    }

    //endregion
    //region internals

    @SafeVarargs
    private final void build(final TagKey<Item> tag, final Supplier<? extends Item>... items) {

        final TagsProvider.TagAppender<Item> builder = this.tag(tag);

        for (Supplier<? extends Item> item : items) {
            builder.add(item.get());
        }
    }

    //endregion
}
