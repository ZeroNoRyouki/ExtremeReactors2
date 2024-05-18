package it.zerono.mods.extremereactors.datagen.recipe;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class TurbineRecipesDataProvider
        extends AbstractRecipesDataProvider {

    public TurbineRecipesDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup,
                                      ResourceLocationBuilder modLocationRoot) {
        super(ExtremeReactors.MOD_ID, "Turbine recipes", output, registryLookup, modLocationRoot);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {

        TagKey<Item> core, metal, fallbackMetal;
        TurbineVariant variant;
        Supplier<? extends ItemLike> casing;

        // Basic parts

        variant = TurbineVariant.Basic;
        casing = Content.Items.TURBINE_CASING_BASIC;
        core = Tags.Items.STORAGE_BLOCKS_REDSTONE;
        metal = Tags.Items.INGOTS_IRON;
        fallbackMetal = null;

        this.casing(output, variant, Content.Items.TURBINE_CASING_BASIC, core, metal, fallbackMetal);
        this.casingRecycle(output, variant, Content.Items.TURBINE_CASING_BASIC,
                ContentTags.Items.USING_TURBINE_CASING_BASIC, Content.Items.TURBINE_GLASS_BASIC);
        this.glass(output, variant, Content.Items.TURBINE_GLASS_BASIC, casing, Tags.Items.GLASS_BLOCKS);
        this.controller(output, variant, Content.Items.TURBINE_CONTROLLER_BASIC, casing, Tags.Items.GEMS_DIAMOND);
        this.powerTap(output, variant, "fe", Content.Items.TURBINE_POWERTAP_FE_PASSIVE_BASIC, Content.Items.TURBINE_POWERTAP_FE_ACTIVE_BASIC,
                casing, () -> net.minecraft.world.item.Items.REDSTONE_BLOCK, () -> net.minecraft.world.item.Items.REDSTONE);
        this.fluidPort(output, variant, "forge", Content.Items.TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC,
                Content.Items.TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC, casing, () -> Items.LAVA_BUCKET, () -> Items.WATER_BUCKET);
        this.bearing(output, variant, Content.Items.TURBINE_ROTORBEARING_BASIC, metal, fallbackMetal);
        this.blade(output, variant, Content.Items.TURBINE_ROTORBLADE_BASIC, metal, fallbackMetal);
        this.shaft(output, variant, Content.Items.TURBINE_ROTORSHAFT_BASIC, metal, fallbackMetal);
        this.redstonePort(output, variant, Content.Items.TURBINE_REDSTONEPORT_BASIC, casing, metal, fallbackMetal,
                Tags.Items.INGOTS_GOLD);
        this.chargingPort(output, this.turbineRoot(variant).buildWithSuffix("chargingfe"),
                Content.Items.TURBINE_CHARGINGPORT_FE_BASIC, Content.Items.TURBINE_POWERTAP_FE_ACTIVE_BASIC,
                Items.GLOWSTONE_DUST, Items.REDSTONE);

        // Reinforced parts

        variant = TurbineVariant.Reinforced;
        casing = Content.Items.TURBINE_CASING_REINFORCED;
        core = Tags.Items.STORAGE_BLOCKS_IRON;
        metal = TAG_INGOTS_STEEL;
        fallbackMetal = Tags.Items.STORAGE_BLOCKS_IRON;

        this.casing(output, variant, Content.Items.TURBINE_CASING_REINFORCED, core, metal, fallbackMetal);
        this.casingUpgrade(output, variant, Content.Items.TURBINE_CASING_REINFORCED, metal, fallbackMetal);
        this.casingRecycle(output, variant, Content.Items.TURBINE_CASING_REINFORCED,
                ContentTags.Items.USING_TURBINE_CASING_REINFORCED, Content.Items.TURBINE_GLASS_REINFORCED);
        this.glass(output, variant, Content.Items.TURBINE_GLASS_REINFORCED, casing, Tags.Items.GLASS_BLOCKS);
        this.controller(output, variant, Content.Items.TURBINE_CONTROLLER_REINFORCED, casing, Tags.Items.STORAGE_BLOCKS_DIAMOND);
        this.powerTap(output, variant, "fe", Content.Items.TURBINE_POWERTAP_FE_PASSIVE_REINFORCED, Content.Items.TURBINE_POWERTAP_FE_ACTIVE_REINFORCED,
                casing, () -> net.minecraft.world.item.Items.REDSTONE_BLOCK, () -> net.minecraft.world.item.Items.REDSTONE);
        this.computerPort(output, variant, Content.Items.TURBINE_COMPUTERPORT_REINFORCED, casing, metal, fallbackMetal);
        this.fluidPort(output, variant, "forge", Content.Items.TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED,
                Content.Items.TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED, casing, () -> Items.LAVA_BUCKET, () -> Items.WATER_BUCKET);
        this.bearing(output, variant, Content.Items.TURBINE_ROTORBEARING_REINFORCED, metal, fallbackMetal);
        this.blade(output, variant, Content.Items.TURBINE_ROTORBLADE_REINFORCED, metal, fallbackMetal);
        this.shaft(output, variant, Content.Items.TURBINE_ROTORSHAFT_REINFORCED, metal, fallbackMetal);
        this.redstonePort(output, variant, Content.Items.TURBINE_REDSTONEPORT_REINFORCED, casing, metal, fallbackMetal,
                Tags.Items.INGOTS_GOLD);
        this.chargingPort(output, this.turbineRoot(variant).buildWithSuffix("chargingfe"),
                Content.Items.TURBINE_CHARGINGPORT_FE_REINFORCED, Content.Items.TURBINE_POWERTAP_FE_ACTIVE_REINFORCED,
                Items.GLOWSTONE, Items.REDSTONE_BLOCK);
    }

    //region internals

    private void casing(RecipeOutput output, TurbineVariant variant, Supplier<? extends ItemLike> result,
                        TagKey<Item> core, TagKey<Item> metal, @Nullable TagKey<Item> fallbackMetal) {

        final var idBuilder = this.turbineRoot(variant);

        this.withFallback(output, idBuilder.buildWithSuffix("casing"), metal, idBuilder.buildWithSuffix("casing_alt"),
                fallbackMetal, tag -> this.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                        .define('I', tag)
                        .define('C', core)
                        .define('G', ContentTags.Items.INGOTS_CYANITE)
                        .pattern("IGI")
                        .pattern("GCG")
                        .pattern("IGI")
                        .unlockedBy("has_item", has(ContentTags.Items.INGOTS_CYANITE)));
    }

    private void casingUpgrade(RecipeOutput output, TurbineVariant variant, Supplier<? extends ItemLike> result,
                               TagKey<Item> metal, @Nullable TagKey<Item> fallbackMetal) {

        final var idBuilder = this.turbineRoot(variant);

        this.withFallback(output, idBuilder.buildWithSuffix("casing_upgrade"), metal, idBuilder.buildWithSuffix("casing_upgrade_alt"),
                fallbackMetal, tag -> this.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                        .define('I', tag)
                        .define('C', Content.Blocks.TURBINE_CASING_BASIC.get())
                        .define('G', ContentTags.Items.INGOTS_CYANITE)
                        .pattern("IGI")
                        .pattern("GCG")
                        .pattern("IGI")
                        .unlockedBy("has_item", has(Content.Blocks.TURBINE_CASING_BASIC.get())));
    }

    private void casingRecycle(RecipeOutput output, TurbineVariant variant,
                               Supplier<? extends ItemLike> result, TagKey<Item> casingSourceTag,
                               Supplier<? extends ItemLike> glassSourceItem) {

        final var idBuilder = this.turbineRoot(variant);

        this.shapeless(RecipeCategory.BUILDING_BLOCKS, result, 1)
                .requires(glassSourceItem.get())
                .unlockedBy("has_item", has(glassSourceItem.get()))
                .save(output, idBuilder.buildWithSuffix("casing_recycle_glass"));

        this.shapeless(RecipeCategory.BUILDING_BLOCKS, result, 4)
                .requires(casingSourceTag)
                .unlockedBy("has_item", has(casingSourceTag))
                .save(output, idBuilder.buildWithSuffix("casing_recycle"));
    }

    private void glass(RecipeOutput output, TurbineVariant variant,
                       Supplier<? extends ItemLike> result, Supplier<? extends ItemLike> casing, TagKey<Item> glass) {
        this.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                .define('C', casing.get())
                .define('G', glass)
                .pattern("GCG")
                .unlockedBy("has_item", has(casing.get()))
                .save(output, this.turbineRoot(variant).buildWithSuffix("glass"));
    }

    private void controller(RecipeOutput output, TurbineVariant variant, Supplier<? extends ItemLike> result,
                            Supplier<? extends ItemLike> casing, TagKey<Item> diamond) {
        this.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                .define('C', casing.get())
                .define('Y', ContentTags.Items.BLOCKS_CYANITE)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('D', diamond)
                .define('X', net.minecraft.world.item.Items.COMPARATOR)
                .pattern("CXC")
                .pattern("YDY")
                .pattern("CRC")
                .unlockedBy("has_item", has(casing.get()))
                .unlockedBy("has_item2", has(ContentTags.Items.BLOCKS_CYANITE))
                .save(output, this.turbineRoot(variant).buildWithSuffix("controller"));
    }

    private void powerTap(RecipeOutput output, TurbineVariant variant, String name,
                          Supplier<? extends ItemLike> passiveResult, Supplier<? extends ItemLike> activeResult,
                          Supplier<? extends ItemLike> casing, Supplier<? extends ItemLike> energyBig,
                          Supplier<? extends ItemLike> energySmall) {

        final var idBuilder = this.turbineRoot(variant).append(name);

        this.shaped(RecipeCategory.BUILDING_BLOCKS, passiveResult)
                .define('C', casing.get())
                .define('B', energyBig.get())
                .define('S', energySmall.get())
                .pattern("CSC")
                .pattern("SBS")
                .pattern("CSC")
                .unlockedBy("has_item", has(casing.get()))
                .unlockedBy("has_item2", has(energySmall.get()))
                .save(output, idBuilder.buildWithPrefix("passivetap_"));

        this.shaped(RecipeCategory.BUILDING_BLOCKS, activeResult)
                .define('C', casing.get())
                .define('B', energyBig.get())
                .define('S', energySmall.get())
                .pattern("CBC")
                .pattern("BSB")
                .pattern("CBC")
                .unlockedBy("has_item", has(casing.get()))
                .unlockedBy("has_item2", has(energyBig.get()))
                .save(output, idBuilder.buildWithPrefix("activetap_"));
    }

    private void computerPort(RecipeOutput output, TurbineVariant variant, Supplier<? extends ItemLike> result,
                              Supplier<? extends ItemLike> casing, TagKey<Item> metal,
                              @Nullable final TagKey<Item> fallbackMetal) {

        final var idBuilder = this.turbineRoot(variant);

        this.withFallback(output, idBuilder.buildWithSuffix("computerport"), metal,
                idBuilder.buildWithSuffix("computerport_alt"), fallbackMetal,
                tag -> this.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                        .define('C', casing.get())
                        .define('M', tag)
                        .define('G', Tags.Items.STORAGE_BLOCKS_GOLD)
                        .define('Z', Tags.Items.GEMS_LAPIS)
                        .define('X', Tags.Items.DUSTS_GLOWSTONE)
                        .pattern("CZC")
                        .pattern("MGM")
                        .pattern("CXC")
                        .unlockedBy("has_item", has(casing.get())));
    }

    private void fluidPort(RecipeOutput output, TurbineVariant variant, String name,
                           Supplier<? extends ItemLike> passiveResult, Supplier<? extends ItemLike> activeResult,
                           Supplier<? extends ItemLike> casing, Supplier<? extends ItemLike> lava,
                           Supplier<? extends ItemLike> water) {

        final var idBuilder = this.turbineRoot(variant).append(name);

        this.shaped(RecipeCategory.BUILDING_BLOCKS, passiveResult)
                .define('C', casing.get())
                .define('B', lava.get())
                .define('S', water.get())
                .pattern("CSC")
                .pattern("SBS")
                .pattern("CSC")
                .unlockedBy("has_item", has(casing.get()))
                .unlockedBy("has_item2", has(water.get()))
                .save(output, idBuilder.buildWithPrefix("passivefluidport_"));

        this.shaped(RecipeCategory.BUILDING_BLOCKS, activeResult)
                .define('C', casing.get())
                .define('B', lava.get())
                .define('S', water.get())
                .pattern("CBC")
                .pattern("BSB")
                .pattern("CBC")
                .unlockedBy("has_item", has(casing.get()))
                .unlockedBy("has_item2", has(lava.get()))
                .save(output, idBuilder.buildWithPrefix("activefluidport_"));
    }

    private void blade(RecipeOutput output, TurbineVariant variant, Supplier<? extends ItemLike> result,
                       TagKey<Item> metal, @Nullable TagKey<Item> fallbackMetal) {

        final var idBuilder = this.turbineRoot(variant);

        this.withFallback(output, idBuilder.buildWithSuffix("blade"), metal, idBuilder.buildWithSuffix("blade_alt"),
                fallbackMetal, tag -> this.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                        .define('I', tag)
                        .define('C', ContentTags.Items.INGOTS_CYANITE)
                        .pattern("ICI")
                        .unlockedBy("has_item", has(ContentTags.Items.INGOTS_CYANITE)));
    }

    private void shaft(RecipeOutput output, TurbineVariant variant, Supplier<? extends ItemLike> result,
                       TagKey<Item> metal, @Nullable TagKey<Item> fallbackMetal) {

        final var idBuilder = this.turbineRoot(variant);

        this.withFallback(output, idBuilder.buildWithSuffix("shaft"), metal, idBuilder.buildWithSuffix("shaft_alt"),
                fallbackMetal, tag -> this.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                        .define('I', tag)
                        .define('C', ContentTags.Items.INGOTS_CYANITE)
                        .pattern(" I ")
                        .pattern("ICI")
                        .pattern(" I ")
                        .unlockedBy("has_item", has(ContentTags.Items.INGOTS_CYANITE)));
    }

    private void bearing(RecipeOutput output, TurbineVariant variant, Supplier<? extends ItemLike> result,
                         TagKey<Item> metal, @Nullable TagKey<Item> fallbackMetal) {

        final var idBuilder = this.turbineRoot(variant);

        this.withFallback(output, idBuilder.buildWithSuffix("bearing"), metal, idBuilder.buildWithSuffix("bearing_alt"),
                fallbackMetal, tag -> this.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                        .define('I', tag)
                        .define('R', Blocks.REDSTONE_BLOCK)
                        .define('B', Tags.Items.STORAGE_BLOCKS_IRON)
                        .pattern("IRI")
                        .pattern("B B")
                        .pattern("IRI")
                        .unlockedBy("has_item", has(Tags.Items.STORAGE_BLOCKS_IRON)));
    }

    private void redstonePort(RecipeOutput output, TurbineVariant variant, Supplier<? extends ItemLike> result,
                              Supplier<? extends ItemLike> casing, TagKey<Item> metal, @Nullable TagKey<Item> fallbackMetal,
                              TagKey<Item> gold) {

        final var idBuilder = this.turbineRoot(variant);

        this.withFallback(output, idBuilder.buildWithSuffix("redstoneport"), metal, idBuilder.buildWithSuffix("redstoneport_alt"),
                fallbackMetal, tag -> this.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                        .define('C', casing.get())
                        .define('M', tag)
                        .define('G', gold)
                        .define('Z', net.minecraft.world.item.Items.COMPARATOR)
                        .define('X', net.minecraft.world.item.Items.REPEATER)
                        .pattern("CZC")
                        .pattern("MGM")
                        .pattern("CXC")
                        .unlockedBy("has_item", has(casing.get())));
    }

    //endregion
}
