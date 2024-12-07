package it.zerono.mods.extremereactors.datagen.recipe;

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.extremereactors.gamecontent.mekanism.IMekanismService;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
import it.zerono.mods.zerocore.lib.datagen.provider.recipe.ModRecipeProviderRunner;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ReactorRecipesDataProvider
        extends AbstractRecipesDataProvider {

    public ReactorRecipesDataProvider(ModRecipeProviderRunner<ReactorRecipesDataProvider> mainProvider,
                                      HolderLookup.Provider registryLookupProvider, RecipeOutput output) {
        super(mainProvider, registryLookupProvider, output);
    }

    @Override
    protected void buildRecipes() {

        TagKey<Item> core, metal, fallbackMetal;
        ReactorVariant variant;
        Supplier<? extends ItemLike> casing;

        // Basic parts

        variant = ReactorVariant.Basic;
        casing = Content.Items.REACTOR_CASING_BASIC;
        core = Tags.Items.SANDS_COLORLESS;
        metal = Tags.Items.INGOTS_IRON;
        fallbackMetal = null;

        this.casing(variant, Content.Items.REACTOR_CASING_BASIC, core, metal, fallbackMetal);
        this.casingRecycle(variant, Content.Items.REACTOR_CASING_BASIC,
                ContentTags.Items.USING_REACTOR_CASING_BASIC, Content.Items.REACTOR_GLASS_BASIC);
        this.glass(variant, Content.Items.REACTOR_GLASS_BASIC, casing, Tags.Items.GLASS_BLOCKS);
        this.controller(variant, Content.Items.REACTOR_CONTROLLER_BASIC, casing, Tags.Items.GEMS_DIAMOND);
        this.fuelRod(variant, Content.Items.REACTOR_FUELROD_BASIC, metal, fallbackMetal, Tags.Items.GLASS_BLOCKS);
        this.controlRod(variant, Content.Items.REACTOR_CONTROLROD_BASIC, casing, metal, fallbackMetal);
        this.solidAccessPort(variant, Content.Items.REACTOR_SOLID_ACCESSPORT_BASIC, casing, metal, fallbackMetal);
        this.powerTap(variant, "fe", Content.Items.REACTOR_POWERTAP_FE_PASSIVE_BASIC,
                Content.Items.REACTOR_POWERTAP_FE_ACTIVE_BASIC, casing, () -> net.minecraft.world.item.Items.REDSTONE_BLOCK,
                () -> net.minecraft.world.item.Items.REDSTONE);
        this.redstonePort(variant, Content.Items.REACTOR_REDSTONEPORT_BASIC, casing, metal, fallbackMetal,
                Tags.Items.INGOTS_GOLD);
        this.chargingPort(this.reactorRoot(variant).buildWithSuffix("chargingfe"),
                Content.Items.REACTOR_CHARGINGPORT_FE_BASIC, Content.Items.REACTOR_POWERTAP_FE_ACTIVE_BASIC,
                Items.LAPIS_LAZULI, Items.REDSTONE);

        // Reinforced parts

        variant = ReactorVariant.Reinforced;
        casing = Content.Items.REACTOR_CASING_REINFORCED;
        core = Tags.Items.STORAGE_BLOCKS_IRON;
        metal = TAG_INGOTS_STEEL;
        fallbackMetal = Tags.Items.STORAGE_BLOCKS_IRON;

        this.casing(variant, Content.Items.REACTOR_CASING_REINFORCED, core, metal, fallbackMetal);
        this.casingUpgrade(variant, Content.Items.REACTOR_CASING_REINFORCED, metal, fallbackMetal);
        this.casingRecycle(variant, Content.Items.REACTOR_CASING_REINFORCED,
                ContentTags.Items.USING_REACTOR_CASING_REINFORCED, Content.Items.REACTOR_GLASS_REINFORCED);
        this.glass(variant, Content.Items.REACTOR_GLASS_REINFORCED, casing, Tags.Items.GLASS_BLOCKS);
        this.controller(variant, Content.Items.REACTOR_CONTROLLER_REINFORCED, casing, Tags.Items.STORAGE_BLOCKS_DIAMOND);
        this.fuelRod(variant, Content.Items.REACTOR_FUELROD_REINFORCED, metal, fallbackMetal, Tags.Items.GLASS_BLOCKS);
        this.controlRod(variant, Content.Items.REACTOR_CONTROLROD_REINFORCED, casing, metal, fallbackMetal);
        this.solidAccessPort(variant, Content.Items.REACTOR_SOLID_ACCESSPORT_REINFORCED, casing, metal, fallbackMetal);
        this.fluidAccessPort(variant, Content.Items.REACTOR_FLUID_ACCESSPORT_REINFORCED, casing, metal, fallbackMetal);
        this.powerTap(variant, "fe", Content.Items.REACTOR_POWERTAP_FE_PASSIVE_REINFORCED,
                Content.Items.REACTOR_POWERTAP_FE_ACTIVE_REINFORCED, casing, () -> net.minecraft.world.item.Items.REDSTONE_BLOCK,
                () -> net.minecraft.world.item.Items.REDSTONE);
        this.redstonePort(variant, Content.Items.REACTOR_REDSTONEPORT_REINFORCED, casing, metal, fallbackMetal,
                Tags.Items.STORAGE_BLOCKS_GOLD);
        this.computerPort(variant, Content.Items.REACTOR_COMPUTERPORT_REINFORCED, casing, metal, fallbackMetal);
        this.fluidPort(variant, "forge", Content.Items.REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED,
                Content.Items.REACTOR_FLUIDPORT_FORGE_ACTIVE_REINFORCED, casing, () -> Items.LAVA_BUCKET,
                () -> Items.WATER_BUCKET);
        this.mekFluidPort(variant, Content.Items.REACTOR_FLUIDPORT_MEKANISM_PASSIVE_REINFORCED, casing,
                () -> Items.LAVA_BUCKET, () -> Items.WATER_BUCKET);
        this.chargingPort(this.reactorRoot(variant).buildWithSuffix("chargingfe"),
                Content.Items.REACTOR_CHARGINGPORT_FE_REINFORCED, Content.Items.REACTOR_POWERTAP_FE_ACTIVE_REINFORCED,
                Items.LAPIS_BLOCK, Items.REDSTONE_BLOCK);
    }

    //region internals

    private void casing(ReactorVariant variant, Supplier<? extends ItemLike> result,
                        TagKey<Item> core, TagKey<Item> metal, @Nullable TagKey<Item> fallbackMetal) {

        final var idBuilder = this.reactorRoot(variant);

        this.withFallback(this.output, idBuilder.buildWithSuffix("casing"), metal, idBuilder.buildWithSuffix("casing_alt"),
                fallbackMetal, tag -> this.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                        .define('I', tag)
                        .define('C', core)
                        .define('G', ContentTags.Items.INGOTS_GRAPHITE)
                        .pattern("IGI")
                        .pattern("GCG")
                        .pattern("IGI")
                        .unlockedBy("has_item", has(ContentTags.Items.INGOTS_GRAPHITE)));
    }

    private void casingUpgrade(ReactorVariant variant, Supplier<? extends ItemLike> result,
                               TagKey<Item> metal, @Nullable final TagKey<Item> fallbackMetal) {

        final var idBuilder = this.reactorRoot(variant);

        this.withFallback(this.output, idBuilder.buildWithSuffix("casing_upgrade"), metal, idBuilder.buildWithSuffix("casing_upgrade_alt"),
                fallbackMetal, tag -> this.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                        .define('I', tag)
                        .define('C', Content.Blocks.REACTOR_CASING_BASIC.get())
                        .define('G', ContentTags.Items.INGOTS_GRAPHITE)
                        .pattern("IGI")
                        .pattern("GCG")
                        .pattern("IGI")
                        .unlockedBy("has_item", has(Content.Blocks.REACTOR_CASING_BASIC.get())));
    }

    private void casingRecycle(ReactorVariant variant,
                               Supplier<? extends ItemLike> casingResult, TagKey<Item> casingSourceTag,
                               Supplier<? extends ItemLike> glassSourceItem) {

        final var idBuilder = this.reactorRoot(variant);

        this.shapeless(RecipeCategory.BUILDING_BLOCKS, casingResult)
                .requires(glassSourceItem.get())
                .unlockedBy("has_item", has(glassSourceItem.get()))
                .save(this.output, recipeKeyFrom(idBuilder.buildWithSuffix("casing_recycle_glass")));

        this.shapeless(RecipeCategory.BUILDING_BLOCKS, casingResult, 4)
                .requires(casingSourceTag)
                .unlockedBy("has_item", has(casingSourceTag))
                .save(this.output, recipeKeyFrom(idBuilder.buildWithSuffix("casing_recycle")));
    }

    private void glass(ReactorVariant variant, Supplier<? extends ItemLike> result,
                       Supplier<? extends ItemLike> casing, TagKey<Item> glass) {
        this.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                .define('C', casing.get())
                .define('G', glass)
                .pattern("GCG")
                .unlockedBy("has_item", has(casing.get()))
                .save(this.output, recipeKeyFrom(this.reactorRoot(variant).buildWithSuffix("glass")));
    }

    private void controller(ReactorVariant variant, Supplier<? extends ItemLike> result,
                            Supplier<? extends ItemLike> casing, TagKey<Item> diamond) {
        TAGS_YELLORIUM_INGOTS.forEach(tag -> this.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                .define('C', casing.get())
                .define('Y', tag)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('D', diamond)
                .define('X', net.minecraft.world.item.Items.COMPARATOR)
                .pattern("CXC")
                .pattern("YDY")
                .pattern("CRC")
                .unlockedBy("has_item", has(casing.get()))
                .unlockedBy("has_item2", has(tag))
                .save(this.output, recipeKeyFrom(this.nameForTaggedSubtype("controller", variant, tag))));
    }

    private void fuelRod(ReactorVariant variant, Supplier<? extends ItemLike> result,
                         TagKey<Item> metal, @Nullable TagKey<Item> fallbackMetal, TagKey<Item> glass) {
        TAGS_YELLORIUM_INGOTS.forEach(tag -> this.withFallback(this.output,
                this.nameForTaggedSubtype("fuelrod", variant, tag), metal,
                this.nameForTaggedSubtype("fuelrod_alt", variant, tag), fallbackMetal,
                metalTag -> this.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                        .define('M', metalTag)
                        .define('Y', tag)
                        .define('G', ContentTags.Items.INGOTS_GRAPHITE)
                        .define('L', glass)
                        .pattern("MGM")
                        .pattern("LYL")
                        .pattern("MGM")
                        .unlockedBy("has_item", has(tag))));
    }

    private void controlRod(ReactorVariant variant, Supplier<? extends ItemLike> result,
                            Supplier<? extends ItemLike> casing, TagKey<Item> metal, @Nullable TagKey<Item> fallbackMetal) {

        final var idBuilder = this.reactorRoot(variant);

        this.withFallback(this.output, idBuilder.buildWithSuffix("controlrod"), metal, idBuilder.buildWithSuffix("controlrod_alt"),
                fallbackMetal, tag -> this.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                        .define('C', casing.get())
                        .define('M', tag)
                        .define('R', Tags.Items.DUSTS_REDSTONE)
                        .define('G', ContentTags.Items.INGOTS_GRAPHITE)
                        .define('X', net.minecraft.world.item.Items.PISTON)
                        .pattern("CRC")
                        .pattern("MXM")
                        .pattern("CGC")
                        .unlockedBy("has_item", has(casing.get())));
    }

    private void powerTap(ReactorVariant variant, String name,
                          Supplier<? extends ItemLike> passiveResult, Supplier<? extends ItemLike> activeResult,
                          Supplier<? extends ItemLike> casing, Supplier<? extends ItemLike> energyBig,
                          Supplier<? extends ItemLike> energySmall) {

        final var idBuilder = this.reactorRoot(variant).append(name);

        this.shaped(RecipeCategory.BUILDING_BLOCKS, passiveResult)
                .define('C', casing.get())
                .define('B', energyBig.get())
                .define('S', energySmall.get())
                .pattern("CSC")
                .pattern("SBS")
                .pattern("CSC")
                .unlockedBy("has_item", has(casing.get()))
                .unlockedBy("has_item2", has(energySmall.get()))
                .save(this.output, recipeKeyFrom(idBuilder.buildWithPrefix("passivetap_")));

        this.shaped(RecipeCategory.BUILDING_BLOCKS, activeResult)
                .define('C', casing.get())
                .define('B', energyBig.get())
                .define('S', energySmall.get())
                .pattern("CBC")
                .pattern("BSB")
                .pattern("CBC")
                .unlockedBy("has_item", has(casing.get()))
                .unlockedBy("has_item2", has(energyBig.get()))
                .save(this.output, recipeKeyFrom(idBuilder.buildWithPrefix("activetap_")));
    }

    private void fluidPort(final ReactorVariant variant, final String name,
                           Supplier<? extends ItemLike> passiveResult, final Supplier<? extends ItemLike> activeResult,
                           Supplier<? extends ItemLike> casing, final Supplier<? extends ItemLike> lava,
                           Supplier<? extends ItemLike> water) {

        final var idBuilder = this.reactorRoot(variant).append(name);

        this.shaped(RecipeCategory.BUILDING_BLOCKS, passiveResult)
                .define('C', casing.get())
                .define('B', lava.get())
                .define('S', water.get())
                .pattern("CSC")
                .pattern("SBS")
                .pattern("CSC")
                .unlockedBy("has_item", has(casing.get()))
                .unlockedBy("has_item2", has(water.get()))
                .save(this.output, recipeKeyFrom(idBuilder.buildWithPrefix("passivefluidport_")));

        this.shaped(RecipeCategory.BUILDING_BLOCKS, activeResult)
                .define('C', casing.get())
                .define('B', lava.get())
                .define('S', water.get())
                .pattern("CBC")
                .pattern("BSB")
                .pattern("CBC")
                .unlockedBy("has_item", has(casing.get()))
                .unlockedBy("has_item2", has(lava.get()))
                .save(this.output, recipeKeyFrom(idBuilder.buildWithPrefix("activefluidport_")));
    }

    private void mekFluidPort(ReactorVariant variant,
                              Supplier<? extends ItemLike> passiveResult, Supplier<? extends ItemLike> casing,
                              Supplier<? extends ItemLike> lava, Supplier<? extends ItemLike> water) {

        final var output = this.output.withConditions(modLoaded(IMekanismService.SERVICE.getId()));

        this.shaped(RecipeCategory.BUILDING_BLOCKS, passiveResult)
                .define('C', casing.get())
                .define('B', lava.get())
                .define('S', water.get())
                .define('X', Items.EMERALD_BLOCK)
                .pattern("CSC")
                .pattern("BXB")
                .pattern("CSC")
                .unlockedBy("has_item", has(casing.get()))
                .unlockedBy("has_item2", has(water.get()))
                .save(output, recipeKeyFrom(this.reactorRoot(variant).buildWithSuffix("passivefluidport_mekanism")));
    }

    private void solidAccessPort(ReactorVariant variant,
                                 Supplier<? extends ItemLike> result, Supplier<? extends ItemLike> casing,
                                 TagKey<Item> metal, @Nullable TagKey<Item> fallbackMetal) {

        final var idBuilder = this.reactorRoot(variant);

        this.withFallback(this.output, idBuilder.buildWithSuffix("solidaccessport"), metal,
                idBuilder.buildWithSuffix("solidaccessport_alt"), fallbackMetal,
                tag -> this.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                        .define('C', casing.get())
                        .define('M', tag)
                        .define('H', net.minecraft.world.item.Items.HOPPER)
                        .define('W', Tags.Items.CHESTS_WOODEN)
                        .define('X', net.minecraft.world.item.Items.PISTON)
                        .pattern("CHC")
                        .pattern("MWM")
                        .pattern("CXC")
                        .unlockedBy("has_item", has(casing.get())));
    }

    private void fluidAccessPort(ReactorVariant variant,
                                 Supplier<? extends ItemLike> result, Supplier<? extends ItemLike> casing,
                                 TagKey<Item> metal, @Nullable TagKey<Item> fallbackMetal) {

        final var idBuilder = this.reactorRoot(variant);

        this.withFallback(this.output, idBuilder.buildWithSuffix("fluidaccessport"), metal,
                idBuilder.buildWithSuffix("fluidaccessport_alt"), fallbackMetal,
                tag -> this.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                        .define('C', casing.get())
                        .define('M', tag)
                        .define('H', net.minecraft.world.item.Items.HOPPER)
                        .define('W', net.minecraft.world.item.Items.BUCKET)
                        .define('X', net.minecraft.world.item.Items.PISTON)
                        .pattern("CHC")
                        .pattern("MWM")
                        .pattern("CXC")
                        .unlockedBy("has_item", has(casing.get())));
    }

    private void redstonePort(ReactorVariant variant,
                              Supplier<? extends ItemLike> result, Supplier<? extends ItemLike> casing,
                              TagKey<Item> metal, @Nullable TagKey<Item> fallbackMetal, TagKey<Item> gold) {

        final var idBuilder = this.reactorRoot(variant);

        this.withFallback(this.output, idBuilder.buildWithSuffix("redstoneport"), metal,
                idBuilder.buildWithSuffix("redstoneport_alt"), fallbackMetal,
                tag -> this.shaped(RecipeCategory.BUILDING_BLOCKS, result)
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

    private void computerPort(ReactorVariant variant,
                              Supplier<? extends ItemLike> result, Supplier<? extends ItemLike> casing,
                              TagKey<Item> metal, @Nullable TagKey<Item> fallbackMetal) {

        final var idBuilder = this.reactorRoot(variant);

        this.withFallback(this.output, idBuilder.buildWithSuffix("computerport"), metal,
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

    private ResourceLocation nameForTaggedSubtype(String baseName, ReactorVariant variant, TagKey<Item> tag) {
        return this.reactorRoot(variant)
                .buildWithSuffix(baseName + "_" + tag.location().getPath().replace('/', '_'));
    }

    //endregion
}
