/*
 *
 * ReactorRecipeGenerator.java
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
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
import it.zerono.mods.zerocore.lib.compat.Mods;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ReactorRecipeGenerator
    extends AbstractRecipeGenerator {

    public ReactorRecipeGenerator(final DataGenerator generatorIn) {
        super(generatorIn);
    }

    //region RecipeProvider

    /**
     * Gets a name for this provider, to use in logging.
     */
    @Override
    public String getName() {
        return ExtremeReactors.MOD_NAME + "Reactor recipes";
    }

    /**
     * Registers all recipes to the given consumer.
     */
    @Override
    protected void buildCraftingRecipes(final Consumer<FinishedRecipe> c) {

        Tag.Named<Item> core, metal, alternativeMetal;
        ReactorVariant variant;
        Supplier<? extends ItemLike> casing;

        // Basic parts

        variant = ReactorVariant.Basic;
        casing = Content.Items.REACTOR_CASING_BASIC;
        core = Tags.Items.SAND;
        metal = Tags.Items.INGOTS_IRON;
        alternativeMetal = null;

        this.reactorCasing(c, variant, Content.Items.REACTOR_CASING_BASIC, core, metal, alternativeMetal);
        this.reactorGlass(c, variant, Content.Items.REACTOR_GLASS_BASIC, casing, Tags.Items.GLASS);
        this.reactorController(c, variant, Content.Items.REACTOR_CONTROLLER_BASIC, casing, Tags.Items.GEMS_DIAMOND);
        this.reactorFuelRod(c, variant, Content.Items.REACTOR_FUELROD_BASIC, metal, alternativeMetal, Tags.Items.GLASS);
        this.reactorControlRod(c, variant, Content.Items.REACTOR_CONTROLROD_BASIC, casing, metal, alternativeMetal);
        this.reactorSolidAccessPort(c, variant, Content.Items.REACTOR_SOLID_ACCESSPORT_BASIC, casing, metal, alternativeMetal);
        this.reactorPowerTap(c, variant, "fe", Content.Items.REACTOR_POWERTAP_FE_PASSIVE_BASIC, Content.Items.REACTOR_POWERTAP_FE_ACTIVE_BASIC,
                casing, () -> net.minecraft.world.item.Items.REDSTONE_BLOCK, () -> net.minecraft.world.item.Items.REDSTONE);
        this.reactorRedstonePort(c, variant, Content.Items.REACTOR_REDSTONEPORT_BASIC, casing, metal, alternativeMetal, Tags.Items.INGOTS_GOLD);
        this.generatorChargingPort(c, variant, "chargingfe", GROUP_REACTOR, ReactorRecipeGenerator::reactorRecipeName,
                Content.Items.REACTOR_CHARGINGPORT_FE_BASIC, Content.Items.REACTOR_POWERTAP_FE_ACTIVE_BASIC,
                Items.LAPIS_LAZULI, Items.REDSTONE);

        this.reactorCasingRecycle(c, variant, Content.Items.REACTOR_CASING_BASIC, ContentTags.Items.USING_REACTOR_CASING_BASIC, Content.Items.REACTOR_GLASS_BASIC);

        // Reinforced parts

        variant = ReactorVariant.Reinforced;
        casing = Content.Items.REACTOR_CASING_REINFORCED;
        core = Tags.Items.STORAGE_BLOCKS_IRON;
        metal = TAG_INGOTS_STEEL;
        alternativeMetal = Tags.Items.STORAGE_BLOCKS_IRON;

        this.reactorCasing(c, variant, Content.Items.REACTOR_CASING_REINFORCED, core, metal, alternativeMetal);
        this.reactorCasingUpgrade(c, variant, Content.Items.REACTOR_CASING_REINFORCED, metal, alternativeMetal);
        this.reactorGlass(c, variant, Content.Items.REACTOR_GLASS_REINFORCED, casing, Tags.Items.GLASS);
        this.reactorController(c, variant, Content.Items.REACTOR_CONTROLLER_REINFORCED, casing, Tags.Items.STORAGE_BLOCKS_DIAMOND);
        this.reactorFuelRod(c, variant, Content.Items.REACTOR_FUELROD_REINFORCED, metal, alternativeMetal, Tags.Items.GLASS);
        this.reactorControlRod(c, variant, Content.Items.REACTOR_CONTROLROD_REINFORCED, casing, metal, alternativeMetal);
        this.reactorSolidAccessPort(c, variant, Content.Items.REACTOR_SOLID_ACCESSPORT_REINFORCED, casing, metal, alternativeMetal);
        this.reactorPowerTap(c, variant, "fe", Content.Items.REACTOR_POWERTAP_FE_PASSIVE_REINFORCED, Content.Items.REACTOR_POWERTAP_FE_ACTIVE_REINFORCED,
                casing, () -> net.minecraft.world.item.Items.REDSTONE_BLOCK, () -> net.minecraft.world.item.Items.REDSTONE);
        this.reactorRedstonePort(c, variant, Content.Items.REACTOR_REDSTONEPORT_REINFORCED, casing, metal, alternativeMetal, Tags.Items.STORAGE_BLOCKS_GOLD);
        this.reactorComputerPort(c, variant, Content.Items.REACTOR_COMPUTERPORT_REINFORCED, casing, metal, alternativeMetal);
        this.reactorFluidPort(c, variant, "forge", Content.Items.REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED, Content.Items.REACTOR_FLUIDPORT_FORGE_ACTIVE_REINFORCED,
                casing, () -> Items.LAVA_BUCKET,  () -> Items.WATER_BUCKET);
        this.reactorMekFluidPort(c, variant, Content.Items.REACTOR_FLUIDPORT_MEKANISM_PASSIVE_REINFORCED, casing, () -> Items.LAVA_BUCKET,  () -> Items.WATER_BUCKET);
        this.generatorChargingPort(c, variant, "chargingfe", GROUP_REACTOR, ReactorRecipeGenerator::reactorRecipeName,
                Content.Items.REACTOR_CHARGINGPORT_FE_REINFORCED, Content.Items.REACTOR_POWERTAP_FE_ACTIVE_REINFORCED,
                Items.LAPIS_BLOCK, Items.REDSTONE_BLOCK);

        this.reactorCasingRecycle(c, variant, Content.Items.REACTOR_CASING_REINFORCED, ContentTags.Items.USING_REACTOR_CASING_REINFORCED, Content.Items.REACTOR_GLASS_REINFORCED);
    }

    //endregion
    //region internals

    private void reactorCasing(final Consumer<FinishedRecipe> c, final ReactorVariant variant,
                               final Supplier<? extends ItemLike> result,
                               final Tag<Item> core, final Tag.Named<Item> metal,
                               @Nullable final Tag.Named<Item> alternativeMetal) {

        recipeWithAlternativeTag(c, reactorRecipeName(variant, "casing"), reactorRecipeName(variant, "casing_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shaped(result.get())
                                .define('I', metalTag)
                                .define('C', core)
                                .define('G', ContentTags.Items.INGOTS_GRAPHITE)
                                .pattern("IGI")
                                .pattern("GCG")
                                .pattern("IGI")
                                .group(GROUP_REACTOR)
                                .unlockedBy("has_item", has(ContentTags.Items.INGOTS_GRAPHITE)));
    }

    private void reactorCasingUpgrade(final Consumer<FinishedRecipe> c, final ReactorVariant variant,
                                      final Supplier<? extends ItemLike> result, final Tag.Named<Item> metal,
                                      @Nullable final Tag.Named<Item> alternativeMetal) {

        recipeWithAlternativeTag(c, reactorRecipeName(variant, "casing_upgrade"), reactorRecipeName(variant, "casing_upgrade_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shaped(result.get())
                                .define('I', metalTag)
                                .define('C', Content.Blocks.REACTOR_CASING_BASIC.get())
                                .define('G', ContentTags.Items.INGOTS_GRAPHITE)
                                .pattern("IGI")
                                .pattern("GCG")
                                .pattern("IGI")
                                .group(GROUP_REACTOR)
                                .unlockedBy("has_item", has(Content.Blocks.REACTOR_CASING_BASIC.get())));
    }

    private void reactorCasingRecycle(final Consumer<FinishedRecipe> c, final ReactorVariant variant,
                                      final Supplier<? extends ItemLike> casingResult,
                                      final Tag.Named<Item> casingSourceTag,
                                      final Supplier<? extends ItemLike> glassSourceItem) {

        ShapelessRecipeBuilder.shapeless(casingResult.get(), 1)
                .requires(glassSourceItem.get())
                .group(GROUP_REACTOR)
                .unlockedBy("has_item", has(glassSourceItem.get()))
                .save(c, reactorRecipeName(variant, "casing_recycle_glass"));

        ShapelessRecipeBuilder.shapeless(casingResult.get(), 4)
                .requires(casingSourceTag)
                .group(GROUP_REACTOR)
                .unlockedBy("has_item", has(casingSourceTag))
                .save(c, reactorRecipeName(variant, "casing_recycle"));
    }

    private void reactorGlass(final Consumer<FinishedRecipe> c, final ReactorVariant variant,
                              final Supplier<? extends ItemLike> result,
                              final Supplier<? extends ItemLike> casing, final Tag<Item> glass) {
        ShapedRecipeBuilder.shaped(result.get())
                .define('C', casing.get())
                .define('G', glass)
                .pattern("GCG")
                .group(GROUP_REACTOR)
                .unlockedBy("has_item", has(casing.get()))
                .save(c, reactorRecipeName(variant, "glass"));
    }

    private void reactorController(final Consumer<FinishedRecipe> c, final ReactorVariant variant,
                                   final Supplier<? extends ItemLike> result,
                                   final Supplier<? extends ItemLike> casing, final Tag<Item> diamond) {

        TAGS_YELLORIUM_INGOTS.forEach(yellorium -> ShapedRecipeBuilder.shaped(result.get())
                .define('C', casing.get())
                .define('Y', yellorium)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('D', diamond)
                .define('X', net.minecraft.world.item.Items.COMPARATOR)
                .pattern("CXC")
                .pattern("YDY")
                .pattern("CRC")
                .group(GROUP_REACTOR)
                .unlockedBy("has_item", has(casing.get()))
                .unlockedBy("has_item2", has(yellorium))
                .save(c, reactorRecipeName(variant, "controller", yellorium))
        );
    }

    private void reactorFuelRod(final Consumer<FinishedRecipe> c, final ReactorVariant variant,
                                final Supplier<? extends ItemLike> result,
                                final Tag.Named<Item> metal, @Nullable final Tag.Named<Item> alternativeMetal,
                                final Tag<Item> glass) {

        TAGS_YELLORIUM_INGOTS.forEach(yellorium ->
                recipeWithAlternativeTag(c, reactorRecipeName(variant, "fuelrod", yellorium),
                        reactorRecipeName(variant, "fuelrod_alt", yellorium),
                        metal, alternativeMetal, metalTag ->
                                ShapedRecipeBuilder.shaped(result.get())
                                        .define('M', metalTag)
                                        .define('Y', yellorium)
                                        .define('G', ContentTags.Items.INGOTS_GRAPHITE)
                                        .define('L', glass)
                                        .pattern("MGM")
                                        .pattern("LYL")
                                        .pattern("MGM")
                                        .group(GROUP_REACTOR)
                                        .unlockedBy("has_item", has(yellorium))));
    }

    private void reactorControlRod(final Consumer<FinishedRecipe> c, final ReactorVariant variant,
                                   final Supplier<? extends ItemLike> result, final Supplier<? extends ItemLike> casing,
                                   final Tag.Named<Item> metal, @Nullable final Tag.Named<Item> alternativeMetal) {

        recipeWithAlternativeTag(c, reactorRecipeName(variant, "controlrod"), reactorRecipeName(variant, "controlrod_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shaped(result.get())
                                .define('C', casing.get())
                                .define('M', metalTag)
                                .define('R', Tags.Items.DUSTS_REDSTONE)
                                .define('G', ContentTags.Items.INGOTS_GRAPHITE)
                                .define('X', net.minecraft.world.item.Items.PISTON)
                                .pattern("CRC")
                                .pattern("MXM")
                                .pattern("CGC")
                                .group(GROUP_REACTOR)
                                .unlockedBy("has_item", has(casing.get())));
    }

    private void reactorPowerTap(final Consumer<FinishedRecipe> c, final ReactorVariant variant, final String name,
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
                .group(GROUP_REACTOR)
                .unlockedBy("has_item", has(casing.get()))
                .unlockedBy("has_item2", has(energySmall.get()))
                .save(c, reactorRecipeName(variant, "passivetap_" + name));

        ShapedRecipeBuilder.shaped(activeResult.get())
                .define('C', casing.get())
                .define('B', energyBig.get())
                .define('S', energySmall.get())
                .pattern("CBC")
                .pattern("BSB")
                .pattern("CBC")
                .group(GROUP_REACTOR)
                .unlockedBy("has_item", has(casing.get()))
                .unlockedBy("has_item2", has(energyBig.get()))
                .save(c, reactorRecipeName(variant, "activetap_" + name));
    }

    private void reactorFluidPort(final Consumer<FinishedRecipe> c, final ReactorVariant variant, final String name,
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
                .group(GROUP_REACTOR)
                .unlockedBy("has_item", has(casing.get()))
                .unlockedBy("has_item2", has(water.get()))
                .save(c, reactorRecipeName(variant, "passivefluidport_" + name));

        ShapedRecipeBuilder.shaped(activeResult.get())
                .define('C', casing.get())
                .define('B', lava.get())
                .define('S', water.get())
                .pattern("CBC")
                .pattern("BSB")
                .pattern("CBC")
                .group(GROUP_REACTOR)
                .unlockedBy("has_item", has(casing.get()))
                .unlockedBy("has_item2", has(lava.get()))
                .save(c, reactorRecipeName(variant, "activefluidport_" + name));
    }


    private void reactorMekFluidPort(final Consumer<FinishedRecipe> c, final ReactorVariant variant,
                                     final Supplier<? extends ItemLike> passiveResult,
                                     final Supplier<? extends ItemLike> casing, final Supplier<? extends ItemLike> lava,
                                     final Supplier<? extends ItemLike> water) {

        ConditionalRecipe.builder()
                .addCondition(modLoaded(Mods.MEKANISM.id()))
                .addRecipe(ShapedRecipeBuilder.shaped(passiveResult.get())
                        .define('C', casing.get())
                        .define('B', lava.get())
                        .define('S', water.get())
                        .define('X', Items.EMERALD_BLOCK)
                        .pattern("CSC")
                        .pattern("BXB")
                        .pattern("CSC")
                        .group(GROUP_REACTOR)
                        .unlockedBy("has_item", has(casing.get()))
                        .unlockedBy("has_item2", has(water.get()))
                        ::save)
                .build(c, reactorRecipeName(variant, "passivefluidport_mekanism"));
    }

    private void reactorSolidAccessPort(final Consumer<FinishedRecipe> c, final ReactorVariant variant,
                                        final Supplier<? extends ItemLike> result, final Supplier<? extends ItemLike> casing,
                                        final Tag.Named<Item> metal, @Nullable final Tag.Named<Item> alternativeMetal) {
        recipeWithAlternativeTag(c, reactorRecipeName(variant, "solidaccessport"), reactorRecipeName(variant, "solidaccessport_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shaped(result.get())
                                .define('C', casing.get())
                                .define('M', metalTag)
                                .define('H', net.minecraft.world.item.Items.HOPPER)
                                .define('W', Tags.Items.CHESTS_WOODEN)
                                .define('X', net.minecraft.world.item.Items.PISTON)
                                .pattern("CHC")
                                .pattern("MWM")
                                .pattern("CXC")
                                .group(GROUP_REACTOR)
                                .unlockedBy("has_item", has(casing.get())));
    }

    private void reactorRedstonePort(final Consumer<FinishedRecipe> c, final ReactorVariant variant,
                                     final Supplier<? extends ItemLike> result, final Supplier<? extends ItemLike> casing,
                                     final Tag.Named<Item> metal, @Nullable final Tag.Named<Item> alternativeMetal,
                                     final Tag<Item> gold) {
        recipeWithAlternativeTag(c, reactorRecipeName(variant, "redstoneport"), reactorRecipeName(variant, "redstoneport_alt"),
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
                                .group(GROUP_REACTOR)
                                .unlockedBy("has_item", has(casing.get())));
    }

    private void reactorComputerPort(final Consumer<FinishedRecipe> c, final ReactorVariant variant,
                                     final Supplier<? extends ItemLike> result, final Supplier<? extends ItemLike> casing,
                                     final Tag.Named<Item> metal, @Nullable final Tag.Named<Item> alternativeMetal) {
        recipeWithAlternativeTag(c, reactorRecipeName(variant, "computerport"), reactorRecipeName(variant, "computerport_alt"),
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
                                .group(GROUP_REACTOR)
                                .unlockedBy("has_item", has(casing.get())));
    }

    private static ResourceLocation reactorRecipeName(final IMultiblockGeneratorVariant variant, final String name) {
        return ExtremeReactors.newID("reactor/" + variant.getName() + "/" + name);
    }

    private static ResourceLocation reactorRecipeName(final IMultiblockGeneratorVariant variant, final String name,
                                                      final Tag.Named<Item> tag) {
        return ExtremeReactors.newID("reactor/" + variant.getName() + "/" + name + "_" + tag.getName().getPath().replace('/', '_'));
    }

    //endregion
}
