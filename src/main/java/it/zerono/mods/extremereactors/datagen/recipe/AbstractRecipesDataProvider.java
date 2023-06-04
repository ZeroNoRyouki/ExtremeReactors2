package it.zerono.mods.extremereactors.datagen.recipe;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.recipe.ReprocessorRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.datagen.provider.ModRecipeProvider;
import it.zerono.mods.zerocore.lib.datagen.provider.recipe.TwoToOneRecipeBuilder;
import it.zerono.mods.zerocore.lib.recipe.ingredient.IRecipeIngredient;
import it.zerono.mods.zerocore.lib.recipe.result.IRecipeResult;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractRecipesDataProvider
        extends ModRecipeProvider {

    protected static final TagKey<Item> TAG_INGOTS_STEEL = TagsHelper.ITEMS.createKey("forge:ingots/steel");
    protected static final TagKey<Item> TAG_INGOTS_URANIUM = TagsHelper.ITEMS.createKey("forge:ingots/uranium");
    protected static final Set<TagKey<Item>> TAGS_YELLORIUM_INGOTS = ImmutableSet.of(ContentTags.Items.INGOTS_YELLORIUM, TAG_INGOTS_URANIUM);


    protected AbstractRecipesDataProvider(String name, PackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup,
                                          ResourceLocationBuilder modLocationRoot) {
        super(name, output, registryLookup, modLocationRoot);
    }

    protected String group(String name) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(name), "Name must not be null or empty");

        return ExtremeReactors.MOD_ID + ":" + name;
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

    protected TwoToOneRecipeBuilder<ItemStack, FluidStack, ItemStack> reprocessor(IRecipeIngredient<ItemStack> ingot,
                                                                                  IRecipeIngredient<FluidStack> fluid,
                                                                                  IRecipeResult<ItemStack> result) {
        return new TwoToOneRecipeBuilder<>(ReprocessorRecipe.ID, ingot, fluid, result,
                ReprocessorRecipe.JSON_LABELS_SUPPLIER);
    }

    protected void chargingPort(Consumer<FinishedRecipe> builder, ResourceLocation name,
                                Supplier<? extends ItemLike> result, Supplier<? extends ItemLike> powerTap,
                                ItemLike item1, ItemLike item2) {

        this.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                .define('T', powerTap.get())
                .define('G', Tags.Items.GLASS)
                .define('1', item1)
                .define('2', item2)
                .pattern("212")
                .pattern("GTG")
                .pattern("212")
                .unlockedBy("has_item", has(powerTap.get()))
                .save(builder, name);
    }
}
