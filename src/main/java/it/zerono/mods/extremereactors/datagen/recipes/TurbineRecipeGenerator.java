/*
 *
 * TurbineRecipeGenerator.java
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

package it.zerono.mods.extremereactors.datagen.recipes;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockGeneratorVariant;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TurbineRecipeGenerator
        extends AbstractRecipeGenerator {

    public TurbineRecipeGenerator(final DataGenerator generatorIn) {
        super(generatorIn);
    }

    //region RecipeProvider

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return ExtremeReactors.MOD_NAME + " Turbine recipes";
    }

    /**
     * Registers all recipes to the given consumer.
     */
    @Override
    protected void buildCraftingRecipes(final Consumer<FinishedRecipe> c) {

        TagKey<Item> core, metal, alternativeMetal;
        TurbineVariant variant;
        Supplier<? extends ItemLike> casing;

        // Basic parts

        variant = TurbineVariant.Basic;
        casing = Content.Items.TURBINE_CASING_BASIC;
        core = Tags.Items.STORAGE_BLOCKS_REDSTONE;
        metal = Tags.Items.INGOTS_IRON;
        alternativeMetal = null;

        this.turbineCasing(c, variant, Content.Items.TURBINE_CASING_BASIC, core, metal, alternativeMetal);
        this.turbineGlass(c, variant, Content.Items.TURBINE_GLASS_BASIC, casing, Tags.Items.GLASS);
        this.turbineController(c, variant, Content.Items.TURBINE_CONTROLLER_BASIC, casing, Tags.Items.GEMS_DIAMOND);
        this.turbinePowerTap(c, variant, "fe", Content.Items.TURBINE_POWERTAP_FE_PASSIVE_BASIC, Content.Items.TURBINE_POWERTAP_FE_ACTIVE_BASIC,
                casing, () -> net.minecraft.world.item.Items.REDSTONE_BLOCK, () -> net.minecraft.world.item.Items.REDSTONE);
        this.turbineFluidPort(c, variant, "forge", Content.Items.TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC, Content.Items.TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC,
                casing, () -> Items.LAVA_BUCKET,  () -> Items.WATER_BUCKET);
        this.turbineBlade(c, variant, Content.Items.TURBINE_ROTORBLADE_BASIC, metal, alternativeMetal);
        this.turbineShaft(c, variant, Content.Items.TURBINE_ROTORSHAFT_BASIC, metal, alternativeMetal);
        this.turbineBearing(c, variant, Content.Items.TURBINE_ROTORBEARING_BASIC, metal, alternativeMetal);
        this.turbineRedstonePort(c, variant, Content.Items.TURBINE_REDSTONEPORT_BASIC, casing, metal, alternativeMetal, Tags.Items.INGOTS_GOLD);
        this.generatorChargingPort(c, variant, "chargingfe", GROUP_TURBINE, TurbineRecipeGenerator::turbineRecipeName,
                Content.Items.TURBINE_CHARGINGPORT_FE_BASIC, Content.Items.TURBINE_POWERTAP_FE_ACTIVE_BASIC,
                Items.GLOWSTONE_DUST, Items.REDSTONE);

        this.turbineCasingRecycle(c, variant, Content.Items.TURBINE_CASING_BASIC, ContentTags.Items.USING_TURBINE_CASING_BASIC, Content.Items.TURBINE_GLASS_BASIC);

        // Reinforced parts

        variant = TurbineVariant.Reinforced;
        casing = Content.Items.TURBINE_CASING_REINFORCED;
        core = Tags.Items.STORAGE_BLOCKS_IRON;
        metal = TAG_INGOTS_STEEL;
        alternativeMetal = Tags.Items.STORAGE_BLOCKS_IRON;

        this.turbineCasing(c, variant, Content.Items.TURBINE_CASING_REINFORCED, core, metal, alternativeMetal);
        this.turbineCasingUpgrade(c, variant, Content.Items.TURBINE_CASING_REINFORCED, metal, alternativeMetal);
        this.turbineGlass(c, variant, Content.Items.TURBINE_GLASS_REINFORCED, casing, Tags.Items.GLASS);
        this.turbineController(c, variant, Content.Items.TURBINE_CONTROLLER_REINFORCED, casing, Tags.Items.STORAGE_BLOCKS_DIAMOND);
        this.turbinePowerTap(c, variant, "fe", Content.Items.TURBINE_POWERTAP_FE_PASSIVE_REINFORCED, Content.Items.TURBINE_POWERTAP_FE_ACTIVE_REINFORCED,
                casing, () -> net.minecraft.world.item.Items.REDSTONE_BLOCK, () -> net.minecraft.world.item.Items.REDSTONE);
        this.turbineFluidPort(c, variant, "forge", Content.Items.TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED, Content.Items.TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED,
                casing, () -> Items.LAVA_BUCKET,  () -> Items.WATER_BUCKET);
        this.turbineComputerPort(c, variant, Content.Items.TURBINE_COMPUTERPORT_REINFORCED, casing, metal, alternativeMetal);
        this.turbineBlade(c, variant, Content.Items.TURBINE_ROTORBLADE_REINFORCED, metal, alternativeMetal);
        this.turbineShaft(c, variant, Content.Items.TURBINE_ROTORSHAFT_REINFORCED, metal, alternativeMetal);
        this.turbineBearing(c, variant, Content.Items.TURBINE_ROTORBEARING_REINFORCED, metal, alternativeMetal);
        this.turbineRedstonePort(c, variant, Content.Items.TURBINE_REDSTONEPORT_REINFORCED, casing, metal, alternativeMetal, Tags.Items.INGOTS_GOLD);
        this.generatorChargingPort(c, variant, "chargingfe", GROUP_TURBINE, TurbineRecipeGenerator::turbineRecipeName,
                Content.Items.TURBINE_CHARGINGPORT_FE_REINFORCED, Content.Items.TURBINE_POWERTAP_FE_ACTIVE_REINFORCED,
                Items.GLOWSTONE, Items.REDSTONE_BLOCK);

        this.turbineCasingRecycle(c, variant, Content.Items.TURBINE_CASING_REINFORCED, ContentTags.Items.USING_TURBINE_CASING_REINFORCED, Content.Items.TURBINE_GLASS_REINFORCED);
    }

    //endregion
    //region internals

    private void turbineCasing(final Consumer<FinishedRecipe> c, final TurbineVariant variant,
                               final Supplier<? extends ItemLike> result,
                               final TagKey<Item> core, final TagKey<Item> metal,
                               @Nullable final TagKey<Item> alternativeMetal) {

        recipeWithAlternativeTag(c, turbineRecipeName(variant, "casing"), turbineRecipeName(variant, "casing_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shaped(result.get())
                                .define('I', metalTag)
                                .define('C', core)
                                .define('G', ContentTags.Items.INGOTS_CYANITE)
                                .pattern("IGI")
                                .pattern("GCG")
                                .pattern("IGI")
                                .group(GROUP_TURBINE)
                                .unlockedBy("has_item", has(ContentTags.Items.INGOTS_CYANITE)));
    }

    private void turbineCasingUpgrade(final Consumer<FinishedRecipe> c, final TurbineVariant variant,
                                      final Supplier<? extends ItemLike> result, final TagKey<Item> metal,
                                      @Nullable final TagKey<Item> alternativeMetal) {

        recipeWithAlternativeTag(c, turbineRecipeName(variant, "casing_upgrade"), turbineRecipeName(variant, "casing_upgrade_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shaped(result.get())
                                .define('I', metalTag)
                                .define('C', Content.Blocks.TURBINE_CASING_BASIC.get())
                                .define('G', ContentTags.Items.INGOTS_CYANITE)
                                .pattern("IGI")
                                .pattern("GCG")
                                .pattern("IGI")
                                .group(GROUP_TURBINE)
                                .unlockedBy("has_item", has(Content.Blocks.TURBINE_CASING_BASIC.get())));
    }

    private void turbineCasingRecycle(final Consumer<FinishedRecipe> c, final TurbineVariant variant,
                                      final Supplier<? extends ItemLike> casingResult,
                                      final TagKey<Item> casingSourceTag,
                                      final Supplier<? extends ItemLike> glassSourceItem) {

        ShapelessRecipeBuilder.shapeless(casingResult.get(), 1)
                .requires(glassSourceItem.get())
                .group(GROUP_TURBINE)
                .unlockedBy("has_item", has(glassSourceItem.get()))
                .save(c, turbineRecipeName(variant, "casing_recycle_glass"));

        ShapelessRecipeBuilder.shapeless(casingResult.get(), 4)
                .requires(casingSourceTag)
                .group(GROUP_TURBINE)
                .unlockedBy("has_item", has(casingSourceTag))
                .save(c, turbineRecipeName(variant, "casing_recycle"));
    }

    private void turbineGlass(final Consumer<FinishedRecipe> c, final TurbineVariant variant,
                              final Supplier<? extends ItemLike> result,
                              final Supplier<? extends ItemLike> casing, final TagKey<Item> glass) {
        ShapedRecipeBuilder.shaped(result.get())
                .define('C', casing.get())
                .define('G', glass)
                .pattern("GCG")
                .group(GROUP_TURBINE)
                .unlockedBy("has_item", has(casing.get()))
                .save(c, turbineRecipeName(variant, "glass"));
    }

    private void turbineController(final Consumer<FinishedRecipe> c, final TurbineVariant variant,
                                   final Supplier<? extends ItemLike> result,
                                   final Supplier<? extends ItemLike> casing, final TagKey<Item> diamond) {
        ShapedRecipeBuilder.shaped(result.get())
                .define('C', casing.get())
                .define('Y', ContentTags.Items.BLOCKS_CYANITE)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('D', diamond)
                .define('X', net.minecraft.world.item.Items.COMPARATOR)
                .pattern("CXC")
                .pattern("YDY")
                .pattern("CRC")
                .group(GROUP_TURBINE)
                .unlockedBy("has_item", has(casing.get()))
                .unlockedBy("has_item2", has(ContentTags.Items.BLOCKS_CYANITE))
                .save(c, turbineRecipeName(variant, "controller"));
    }

    private void turbinePowerTap(final Consumer<FinishedRecipe> c, final TurbineVariant variant, final String name,
                                 final Supplier<? extends ItemLike> passiveResult, final Supplier<? extends ItemLike> activeResult,
                                 final Supplier<? extends ItemLike> casing, final Supplier<? extends ItemLike> energyBig,
                                 final Supplier<? extends ItemLike> energySmall) {

        ShapedRecipeBuilder.shaped(passiveResult.get())
                .define('C', casing.get())
                .define('B', energyBig.get())
                .define('S', energySmall.get())
                .pattern("CSC")
                .pattern("SBS")
                .pattern("CSC")
                .group(GROUP_TURBINE)
                .unlockedBy("has_item", has(casing.get()))
                .unlockedBy("has_item2", has(energySmall.get()))
                .save(c, turbineRecipeName(variant, "passivetap_" + name));

        ShapedRecipeBuilder.shaped(activeResult.get())
                .define('C', casing.get())
                .define('B', energyBig.get())
                .define('S', energySmall.get())
                .pattern("CBC")
                .pattern("BSB")
                .pattern("CBC")
                .group(GROUP_TURBINE)
                .unlockedBy("has_item", has(casing.get()))
                .unlockedBy("has_item2", has(energyBig.get()))
                .save(c, turbineRecipeName(variant, "activetap_" + name));
    }

    private void turbineComputerPort(final Consumer<FinishedRecipe> c, final TurbineVariant variant,
                                     final Supplier<? extends ItemLike> result, final Supplier<? extends ItemLike> casing,
                                     final TagKey<Item> metal, @Nullable final TagKey<Item> alternativeMetal) {
        recipeWithAlternativeTag(c, turbineRecipeName(variant, "computerport"), turbineRecipeName(variant, "computerport_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shaped(result.get())
                                .define('C', casing.get())
                                .define('M', metalTag)
                                .define('G', Tags.Items.STORAGE_BLOCKS_GOLD)
                                .define('Z', Tags.Items.GEMS_LAPIS)
                                .define('X', Tags.Items.DUSTS_GLOWSTONE)
                                .pattern("CZC")
                                .pattern("MGM")
                                .pattern("CXC")
                                .group(GROUP_TURBINE)
                                .unlockedBy("has_item", has(casing.get())));
    }

    private void turbineFluidPort(final Consumer<FinishedRecipe> c, final TurbineVariant variant, final String name,
                                  final Supplier<? extends ItemLike> passiveResult, final Supplier<? extends ItemLike> activeResult,
                                  final Supplier<? extends ItemLike> casing, final Supplier<? extends ItemLike> lava,
                                  final Supplier<? extends ItemLike> water) {

        ShapedRecipeBuilder.shaped(passiveResult.get())
                .define('C', casing.get())
                .define('B', lava.get())
                .define('S', water.get())
                .pattern("CSC")
                .pattern("SBS")
                .pattern("CSC")
                .group(GROUP_TURBINE)
                .unlockedBy("has_item", has(casing.get()))
                .unlockedBy("has_item2", has(water.get()))
                .save(c, turbineRecipeName(variant, "passivefluidport_" + name));

        ShapedRecipeBuilder.shaped(activeResult.get())
                .define('C', casing.get())
                .define('B', lava.get())
                .define('S', water.get())
                .pattern("CBC")
                .pattern("BSB")
                .pattern("CBC")
                .group(GROUP_TURBINE)
                .unlockedBy("has_item", has(casing.get()))
                .unlockedBy("has_item2", has(lava.get()))
                .save(c, turbineRecipeName(variant, "activefluidport_" + name));
    }

    private void turbineBlade(final Consumer<FinishedRecipe> c, final TurbineVariant variant,
                              final Supplier<? extends ItemLike> result,
                              final TagKey<Item> metal, @Nullable final TagKey<Item> alternativeMetal) {

        recipeWithAlternativeTag(c, turbineRecipeName(variant, "blade"), turbineRecipeName(variant, "blade_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shaped(result.get())
                                .define('I', metalTag)
                                .define('C', ContentTags.Items.INGOTS_CYANITE)
                                .pattern("ICI")
                                .group(GROUP_TURBINE)
                                .unlockedBy("has_item", has(ContentTags.Items.INGOTS_CYANITE)));
    }

    private void turbineShaft(final Consumer<FinishedRecipe> c, final TurbineVariant variant,
                              final Supplier<? extends ItemLike> result,
                              final TagKey<Item> metal, @Nullable final TagKey<Item> alternativeMetal) {

        recipeWithAlternativeTag(c, turbineRecipeName(variant, "shaft"), turbineRecipeName(variant, "shaft_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shaped(result.get())
                                .define('I', metalTag)
                                .define('C', ContentTags.Items.INGOTS_CYANITE)
                                .pattern(" I ")
                                .pattern("ICI")
                                .pattern(" I ")
                                .group(GROUP_TURBINE)
                                .unlockedBy("has_item", has(ContentTags.Items.INGOTS_CYANITE)));
    }

    private void turbineBearing(final Consumer<FinishedRecipe> c, final TurbineVariant variant,
                                final Supplier<? extends ItemLike> result,
                                final TagKey<Item> metal, @Nullable final TagKey<Item> alternativeMetal) {

        recipeWithAlternativeTag(c, turbineRecipeName(variant, "bearing"), turbineRecipeName(variant, "bearing_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shaped(result.get())
                                .define('I', metalTag)
                                .define('R', Blocks.REDSTONE_BLOCK)
                                .define('B', Tags.Items.STORAGE_BLOCKS_IRON)
                                .pattern("IRI")
                                .pattern("B B")
                                .pattern("IRI")
                                .group(GROUP_TURBINE)
                                .unlockedBy("has_item", has(Tags.Items.STORAGE_BLOCKS_IRON)));
    }

    private void turbineRedstonePort(final Consumer<FinishedRecipe> c, final TurbineVariant variant,
                                     final Supplier<? extends ItemLike> result, final Supplier<? extends ItemLike> casing,
                                     final TagKey<Item> metal, @Nullable final TagKey<Item> alternativeMetal,
                                     final TagKey<Item> gold) {
        recipeWithAlternativeTag(c, turbineRecipeName(variant, "redstoneport"), turbineRecipeName(variant, "redstoneport_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shaped(result.get())
                                .define('C', casing.get())
                                .define('M', metalTag)
                                .define('G', gold)
                                .define('Z', net.minecraft.world.item.Items.COMPARATOR)
                                .define('X', net.minecraft.world.item.Items.REPEATER)
                                .pattern("CZC")
                                .pattern("MGM")
                                .pattern("CXC")
                                .group(GROUP_TURBINE)
                                .unlockedBy("has_item", has(casing.get())));
    }

    private static ResourceLocation turbineRecipeName(final IMultiblockGeneratorVariant variant, final String name) {
        return ExtremeReactors.newID("turbine/" + variant.getName() + "/" + name);
    }

    private static ResourceLocation turbineRecipeName(final IMultiblockGeneratorVariant variant, final String name, final TagKey<Item> tag) {
        return ExtremeReactors.newID("turbine/" + variant.getName() + "/" + name + "_" + tag.location().getPath().replace('/', '_'));
    }

    //endregion
}
