package it.zerono.mods.extremereactors.datagen.recipe;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.datagen.provider.recipe.ModRecipeProvider;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class AbstractRecipesDataProvider
        extends ModRecipeProvider {

    protected static final TagKey<Item> TAG_INGOTS_STEEL = TagsHelper.ITEMS.createKey("c:ingots/steel");
    protected static final Set<TagKey<Item>> TAGS_YELLORIUM_INGOTS = ImmutableSet.of(ContentTags.Items.INGOTS_YELLORIUM,
        ContentTags.Items.INGOTS_URANIUM);

    protected AbstractRecipesDataProvider(String modId, String name, PackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup,
                                          ResourceLocationBuilder modLocationRoot) {

        super(name, output, registryLookup, modLocationRoot);
        this._modId = modId;
    }

    protected String group(String name) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "Name must not be null or empty");

        return this._modId + ":" + name;
    }

    protected ResourceLocationBuilder reactorRoot() {
        return this.root().appendPath("reactor");
    }

    protected ResourceLocationBuilder reactorRoot(ReactorVariant variant) {
        return this.reactorRoot().appendPath(variant.getName());
    }

    protected ResourceLocationBuilder turbineRoot() {
        return this.root().appendPath("turbine");
    }

    protected ResourceLocationBuilder turbineRoot(TurbineVariant variant) {
        return this.turbineRoot().appendPath(variant.getName());
    }

    protected ResourceLocationBuilder reprocessorRoot() {
        return this.root().appendPath("reprocessor");
    }

    protected ResourceLocationBuilder fluidizerRoot() {
        return this.root().appendPath("fluidizer");
    }

    protected ResourceLocationBuilder fluidizerSolidRoot() {
        return this.fluidizerRoot().appendPath("solid");
    }

    protected ResourceLocationBuilder fluidizerSolidMixingRoot() {
        return this.fluidizerRoot().appendPath("solidmixing");
    }

    protected ResourceLocationBuilder fluidizerFluidMixingRoot() {
        return this.fluidizerRoot().appendPath("fluidmixing");
    }

    protected void chargingPort(RecipeOutput output, ResourceLocation name,
                                Supplier<? extends ItemLike> result, Supplier<? extends ItemLike> powerTap,
                                ItemLike item1, ItemLike item2) {

        this.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                .define('T', powerTap.get())
                .define('G', Tags.Items.GLASS_BLOCKS)
                .define('1', item1)
                .define('2', item2)
                .pattern("212")
                .pattern("GTG")
                .pattern("212")
                .unlockedBy("has_item", has(powerTap.get()))
                .save(output, name);
    }

    //region internals

    private final String _modId;

    //endregion
}
