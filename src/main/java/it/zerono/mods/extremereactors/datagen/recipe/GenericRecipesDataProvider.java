package it.zerono.mods.extremereactors.datagen.recipe;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.extremereactors.gamecontent.compat.patchouli.PatchouliCompat;
import it.zerono.mods.zerocore.lib.compat.patchouli.IPatchouliService;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class GenericRecipesDataProvider
        extends AbstractRecipesDataProvider {

    public GenericRecipesDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup,
                                      ResourceLocationBuilder modLocationRoot) {
        super(ExtremeReactors.MOD_ID, "Generic recipes", output, registryLookup, modLocationRoot);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {

        // ingots / (storage) blocks

        this.reactantsStorage(output, "yellorium", Content.Items.YELLORIUM_BLOCK, Content.Items.YELLORIUM_INGOT);
        this.reactantsStorage(output, "cyanite", Content.Items.CYANITE_BLOCK, Content.Items.CYANITE_INGOT);
        this.reactantsStorage(output, "graphite", Content.Items.GRAPHITE_BLOCK, Content.Items.GRAPHITE_INGOT);
        this.reactantsStorage(output, "blutonium", Content.Items.BLUTONIUM_BLOCK, Content.Items.BLUTONIUM_INGOT);
        this.reactantsStorage(output, "magentite", Content.Items.MAGENTITE_BLOCK, Content.Items.MAGENTITE_INGOT);
        this.storageBlock3x3(output, "raw_yellorium", this.group("raw_yellorium"),
                RecipeCategory.MISC, Content.Blocks.RAW_YELLORIUM_BLOCK, RecipeCategory.MISC, Content.Items.RAW_YELLORIUM);

        // coils

        this.coil(output, "ludicrite_block", Content.Items.LUDICRITE_BLOCK, Content.Items.LUDICRITE_INGOT, Items.END_CRYSTAL);
        this.coil(output, "ridiculite_block", Content.Items.RIDICULITE_BLOCK, Content.Items.RIDICULITE_INGOT, Items.NETHER_STAR);
        this.coil(output, "inanite_block", Content.Items.INANITE_BLOCK, Content.Items.INANITE_INGOT, Items.NETHERITE_BLOCK);
        this.coil(output, "insanite_block", Content.Items.INSANITE_BLOCK, Content.Items.INSANITE_INGOT, Content.Items.INANITE_BLOCK.get());

        // smelting

        this.blastingAndSmelting(output, "yellorium_from_ore", "yellorium", Content.Items.YELLORIUM_INGOT, Content.Items.YELLORITE_ORE_BLOCK);
        this.blastingAndSmelting(output, "yellorium_from_raw", "yellorium", Content.Items.YELLORIUM_INGOT, Content.Items.RAW_YELLORIUM);
        this.blastingAndSmelting(output, "graphite_from_coal", "graphite", Content.Items.GRAPHITE_INGOT, () -> Items.COAL);
        this.blastingAndSmelting(output, "graphite_from_charcoal", "graphite", Content.Items.GRAPHITE_INGOT, () -> Items.CHARCOAL);
        this.blastingAndSmelting(output, "graphite_from_coalblock", "graphite", Content.Items.GRAPHITE_BLOCK, () -> Items.COAL_BLOCK, 0.9f, 1800);

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
                .save(output, this.miscRoot().buildWithSuffix("wrench"));

        this.book(output, "erguide", PatchouliCompat.HANDBOOK_ID, Items.BOOK, ContentTags.Items.INGOTS_YELLORIUM);
        this.book(output, "erguide_alt", PatchouliCompat.HANDBOOK_ID, Items.BOOK, ContentTags.Items.INGOTS_URANIUM);
    }

    //region internals

    private void reactantsStorage(RecipeOutput output, String name, Supplier<? extends ItemLike> storage,
                                  Supplier<? extends ItemLike> component) {
        this.storageBlock3x3(output, name, this.group("reactants"),
                RecipeCategory.MISC, storage, RecipeCategory.MISC, component);
    }

    private void coil(RecipeOutput output, String name, Supplier<? extends ItemLike> coil,
                      Supplier<? extends ItemLike> ingot, ItemLike special) {
        this.shaped(RecipeCategory.BUILDING_BLOCKS, coil)
                .define('I', ingot.get())
                .define('S', special)
                .pattern("III")
                .pattern("ISI")
                .pattern("III")
                .group(this.group("coil"))
                .unlockedBy("has_item", has(ingot.get()))
                .save(output, this.turbineRoot().buildWithSuffix(name));
    }

    private void blastingAndSmelting(RecipeOutput output, String name, String group,
                                     Supplier<? extends ItemLike> result, Supplier<? extends ItemLike> ingredient,
                                     float experience, int cookingTime) {

        this.blasting(RecipeCategory.MISC, result, Ingredient.of(ingredient.get()), experience, cookingTime)
                .group(this.group(group))
                .unlockedBy("has_item", has(ingredient.get()))
                .save(output, this.blastingRoot().buildWithSuffix(name));

        this.smelting(RecipeCategory.MISC, result, Ingredient.of(ingredient.get()), experience, cookingTime)
                .group(this.group(group))
                .unlockedBy("has_item", has(ingredient.get()))
                .save(output, this.smeltingRoot().buildWithSuffix(name));
    }

    private void blastingAndSmelting(RecipeOutput output, String name, String group,
                                     Supplier<? extends ItemLike> result, Supplier<? extends ItemLike> ingredient) {
        this.blastingAndSmelting(output, name, group, result, ingredient, 1.0f, 100);
    }

    private void book(RecipeOutput output, String name, ResourceLocation patchouliBookId,
                      ItemLike book, TagKey<Item> item) {

        IPatchouliService.SERVICE.get().consumeBookStack(patchouliBookId, stack -> {

            final var conditional = output.withConditions(modLoaded(IPatchouliService.SERVICE.getId()));

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, stack)
                    .define('I', item)
                    .define('B', book)
                    .pattern("I")
                    .pattern("B")
                    .unlockedBy("has_item", has(item))
                    .save(conditional, ExtremeReactors.ROOT_LOCATION.appendPath("misc", "book").buildWithSuffix(name));
        });
    }

    //endregion
}
