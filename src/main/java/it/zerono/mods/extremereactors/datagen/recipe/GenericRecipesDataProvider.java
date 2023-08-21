package it.zerono.mods.extremereactors.datagen.recipe;

import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericRecipesDataProvider
        extends AbstractRecipesDataProvider {

    public GenericRecipesDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup,
                                      ResourceLocationBuilder modLocationRoot) {
        super("Generic recipes", output, registryLookup, modLocationRoot);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> builder) {

        // ingots / (storage) blocks

        this.reactantsStorage(builder, "yellorium", Content.Items.YELLORIUM_BLOCK, Content.Items.YELLORIUM_INGOT);
        this.reactantsStorage(builder, "cyanite", Content.Items.CYANITE_BLOCK, Content.Items.CYANITE_INGOT);
        this.reactantsStorage(builder, "graphite", Content.Items.GRAPHITE_BLOCK, Content.Items.GRAPHITE_INGOT);
        this.reactantsStorage(builder, "blutonium", Content.Items.BLUTONIUM_BLOCK, Content.Items.BLUTONIUM_INGOT);
        this.reactantsStorage(builder, "magentite", Content.Items.MAGENTITE_BLOCK, Content.Items.MAGENTITE_INGOT);

        // nuggets

        this.reactantsNugget(builder, "yellorium", Content.Items.YELLORIUM_INGOT, Content.Items.YELLORIUM_NUGGET);
        this.reactantsNugget(builder, "blutonium", Content.Items.BLUTONIUM_INGOT, Content.Items.BLUTONIUM_NUGGET);

        // coils

        this.coil(builder, "ludicrite_block", Content.Items.LUDICRITE_BLOCK, Content.Items.LUDICRITE_INGOT, Items.END_CRYSTAL);
        this.coil(builder, "ridiculite_block", Content.Items.RIDICULITE_BLOCK, Content.Items.RIDICULITE_INGOT, Items.NETHER_STAR);
        this.coil(builder, "inanite_block", Content.Items.INANITE_BLOCK, Content.Items.INANITE_INGOT, Items.NETHERITE_BLOCK);
        this.coil(builder, "insanite_block", Content.Items.INSANITE_BLOCK, Content.Items.INSANITE_INGOT, Content.Items.INANITE_BLOCK.get());

        // smelting

        this.blastingAndSmelting(builder, "yellorium_from_ore", "yellorium", Content.Items.YELLORIUM_INGOT, Content.Items.YELLORITE_ORE_BLOCK);
        this.blastingAndSmelting(builder, "yellorium_from_dust", "yellorium", Content.Items.YELLORIUM_INGOT, Content.Items.YELLORIUM_DUST);
        this.blastingAndSmelting(builder, "cyanite_from_dust", "cyanite", Content.Items.CYANITE_INGOT, Content.Items.CYANITE_DUST);
        this.blastingAndSmelting(builder, "graphite_from_dust", "graphite", Content.Items.GRAPHITE_INGOT, Content.Items.GRAPHITE_DUST);
        this.blastingAndSmelting(builder, "blutonium_from_dust", "blutonium", Content.Items.BLUTONIUM_INGOT, Content.Items.BLUTONIUM_DUST);
        this.blastingAndSmelting(builder, "magentite_from_dust", "magentite", Content.Items.MAGENTITE_INGOT, Content.Items.MAGENTITE_DUST);
        this.blastingAndSmelting(builder, "graphite_from_coal", "graphite", Content.Items.GRAPHITE_INGOT, () -> Items.COAL);
        this.blastingAndSmelting(builder, "graphite_from_charcoal", "graphite", Content.Items.GRAPHITE_INGOT, () -> Items.CHARCOAL);
        this.blastingAndSmelting(builder, "graphite_from_coalblock", "graphite", Content.Items.GRAPHITE_BLOCK, () -> Items.COAL_BLOCK, 0.9f, 1800);

        // misc

        this.shaped(RecipeCategory.TOOLS, Content.Items.WRENCH)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('W', ItemTags.WOOL)
                .define('D', Tags.Items.DYES_GREEN)
                .pattern("DI ")
                .pattern("WII")
                .pattern("IW ")
                .group(this.group("wrench"))
                .unlockedBy("has_item", has(Content.Items.WRENCH.get()))
                .save(builder, this.miscRoot().buildWithSuffix("wrench"));

//        this.book(builder, "erguide", PatchouliCompat.HANDBOOK_ID, Items.BOOK, ContentTags.Items.INGOTS_YELLORIUM);
//        this.book(builder, "erguide_alt", PatchouliCompat.HANDBOOK_ID, Items.BOOK, TAG_INGOTS_URANIUM);
        Log.LOGGER.info(Log.DATAGEN, "Skipping Patchouli book recipe generation");
    }

    //region internals

    private void reactantsStorage(Consumer<FinishedRecipe> builder, String name, Supplier<? extends ItemLike> storage,
                                  Supplier<? extends ItemLike> component) {
        this.storageBlock3x3(builder, name, this.group("reactants"),
                RecipeCategory.MISC, storage, RecipeCategory.MISC, component);
    }

    private void reactantsNugget(Consumer<FinishedRecipe> builder, String name, Supplier<? extends ItemLike> ingot,
                                 Supplier<? extends ItemLike> nugget) {
        this.nugget(builder, name, this.group("reactantNuggets"),
                RecipeCategory.MISC, ingot, RecipeCategory.MISC, nugget);
    }

    private void coil(Consumer<FinishedRecipe> builder, String name, Supplier<? extends ItemLike> coil,
                      Supplier<? extends ItemLike> ingot, ItemLike special) {
        this.shaped(RecipeCategory.BUILDING_BLOCKS, coil)
                .define('I', ingot.get())
                .define('S', special)
                .pattern("III")
                .pattern("ISI")
                .pattern("III")
                .group(this.group("coil"))
                .unlockedBy("has_item", has(ingot.get()))
                .save(builder, this.turbineRoot().buildWithSuffix(name));
    }

    private void blastingAndSmelting(Consumer<FinishedRecipe> builder, String name, String group,
                                     Supplier<? extends ItemLike> result, Supplier<? extends ItemLike> ingredient,
                                     float experience, int cookingTime) {

        this.blasting(RecipeCategory.MISC, result, Ingredient.of(ingredient.get()), experience, cookingTime)
                .group(this.group(group))
                .unlockedBy("has_item", has(ingredient.get()))
                .save(builder, this.blastingRoot().buildWithSuffix(name));

        this.smelting(RecipeCategory.MISC, result, Ingredient.of(ingredient.get()), experience, cookingTime)
                .group(this.group(group))
                .unlockedBy("has_item", has(ingredient.get()))
                .save(builder, this.smeltingRoot().buildWithSuffix(name));
    }

    private void blastingAndSmelting(Consumer<FinishedRecipe> builder, String name, String group,
                                     Supplier<? extends ItemLike> result, Supplier<? extends ItemLike> ingredient) {
        this.blastingAndSmelting(builder, name, group, result, ingredient, 1.0f, 100);
    }

    private void book(Consumer<FinishedRecipe> builder, String name, ResourceLocation patchouliBookId,
                      ItemLike book, TagKey<Item> item) {

/*
        Mods.PATCHOULI.map(() -> PatchouliAPI.get().getBookStack(patchouliBookId)).ifPresent(book -> {

            ConditionalRecipe.builder()
                    .addCondition(modLoaded(Mods.PATCHOULI.id()))
                    .addRecipe(fr -> ShapedRecipeBuilder.shaped(book.getItem())
                            .define('I', item)
                            .define('B', book)
                            .pattern("I")
                            .pattern("B")
                            .unlockedBy("has_item", has(item))
                            .save(NbtResultFinishedRecipeAdapter.from(fr, RecipeSerializer.SHAPED_RECIPE,
                                    nbt -> nbt.putString("patchouli:book", patchouliBookId.toString()))))
                    .build(builder, ExtremeReactors.newID("misc/book/" + name));
        });
 */
    }

    //endregion
}
