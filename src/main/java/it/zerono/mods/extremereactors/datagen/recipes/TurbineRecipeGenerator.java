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
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
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
        return ExtremeReactors.MOD_NAME + "Turbine recipes";
    }

    /**
     * Registers all recipes to the given consumer.
     */
    @Override
    protected void registerRecipes(final Consumer<IFinishedRecipe> c) {

        ITag.INamedTag<Item> core, metal, alternativeMetal;
        TurbineVariant variant;
        Supplier<? extends IItemProvider> casing;

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
                casing, () -> net.minecraft.item.Items.REDSTONE_BLOCK, () -> net.minecraft.item.Items.REDSTONE);
        this.turbineFluidPort(c, variant, "forge", Content.Items.TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC, Content.Items.TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC,
                casing, () -> Items.LAVA_BUCKET,  () -> Items.WATER_BUCKET);
        this.turbineBlade(c, variant, Content.Items.TURBINE_ROTORBLADE_BASIC, metal, alternativeMetal);
        this.turbineShaft(c, variant, Content.Items.TURBINE_ROTORSHAFT_BASIC, metal, alternativeMetal);
        this.turbineBearing(c, variant, Content.Items.TURBINE_ROTORBEARING_BASIC, metal, alternativeMetal);

        // Reinforced parts

        variant = TurbineVariant.Reinforced;
        casing = Content.Items.TURBINE_CASING_REINFORCED;
        core = Tags.Items.STORAGE_BLOCKS_IRON;
        metal = TAG_INGOTS_STEEL;
        alternativeMetal = Tags.Items.STORAGE_BLOCKS_IRON;

        this.turbineCasing(c, variant, Content.Items.TURBINE_CASING_REINFORCED, core, metal, alternativeMetal);
        this.turbineGlass(c, variant, Content.Items.TURBINE_GLASS_REINFORCED, casing, Tags.Items.GLASS);
        this.turbineController(c, variant, Content.Items.TURBINE_CONTROLLER_REINFORCED, casing, Tags.Items.STORAGE_BLOCKS_DIAMOND);
        this.turbinePowerTap(c, variant, "fe", Content.Items.TURBINE_POWERTAP_FE_PASSIVE_REINFORCED, Content.Items.TURBINE_POWERTAP_FE_ACTIVE_REINFORCED,
                casing, () -> net.minecraft.item.Items.REDSTONE_BLOCK, () -> net.minecraft.item.Items.REDSTONE);
        this.turbineFluidPort(c, variant, "forge", Content.Items.TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED, Content.Items.TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED,
                casing, () -> Items.LAVA_BUCKET,  () -> Items.WATER_BUCKET);
        this.turbineComputerPort(c, variant, Content.Items.TURBINE_COMPUTERPORT_REINFORCED, casing, metal, alternativeMetal);
        this.turbineBlade(c, variant, Content.Items.TURBINE_ROTORBLADE_REINFORCED, metal, alternativeMetal);
        this.turbineShaft(c, variant, Content.Items.TURBINE_ROTORSHAFT_REINFORCED, metal, alternativeMetal);
        this.turbineBearing(c, variant, Content.Items.TURBINE_ROTORBEARING_REINFORCED, metal, alternativeMetal);
    }

    //endregion
    //region internals

    private void turbineCasing(final Consumer<IFinishedRecipe> c, final TurbineVariant variant,
                               final Supplier<? extends IItemProvider> result,
                               final ITag<Item> core, final ITag.INamedTag<Item> metal,
                               @Nullable final ITag.INamedTag<Item> alternativeMetal) {

        recipeWithAlternativeTag(c, turbineRecipeName(variant, "casing"), turbineRecipeName(variant, "casing_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shapedRecipe(result.get())
                                .key('I', metalTag)
                                .key('C', core)
                                .key('G', ContentTags.Items.INGOTS_CYANITE)
                                .patternLine("IGI")
                                .patternLine("GCG")
                                .patternLine("IGI")
                                .setGroup(GROUP_TURBINE)
                                .addCriterion("has_item", hasItem(ContentTags.Items.INGOTS_CYANITE)));
    }

    private void turbineGlass(final Consumer<IFinishedRecipe> c, final TurbineVariant variant,
                              final Supplier<? extends IItemProvider> result,
                              final Supplier<? extends IItemProvider> casing, final ITag<Item> glass) {
        ShapedRecipeBuilder.shapedRecipe(result.get())
                .key('C', casing.get())
                .key('G', glass)
                .patternLine("GCG")
                .setGroup(GROUP_TURBINE)
                .addCriterion("has_item", hasItem(casing.get()))
                .build(c, turbineRecipeName(variant, "glass"));
    }

    private void turbineController(final Consumer<IFinishedRecipe> c, final TurbineVariant variant,
                                   final Supplier<? extends IItemProvider> result,
                                   final Supplier<? extends IItemProvider> casing, final ITag<Item> diamond) {
        ShapedRecipeBuilder.shapedRecipe(result.get())
                .key('C', casing.get())
                .key('Y', ContentTags.Items.BLOCKS_CYANITE)
                .key('R', Tags.Items.DUSTS_REDSTONE)
                .key('D', diamond)
                .key('X', net.minecraft.item.Items.COMPARATOR)
                .patternLine("CXC")
                .patternLine("YDY")
                .patternLine("CRC")
                .setGroup(GROUP_TURBINE)
                .addCriterion("has_item", hasItem(casing.get()))
                .addCriterion("has_item2", hasItem(ContentTags.Items.BLOCKS_CYANITE))
                .build(c, turbineRecipeName(variant, "controller"));
    }

    private void turbinePowerTap(final Consumer<IFinishedRecipe> c, final TurbineVariant variant, final String name,
                                 final Supplier<? extends IItemProvider> passiveResult, final Supplier<? extends IItemProvider> activeResult,
                                 final Supplier<? extends IItemProvider> casing, final Supplier<? extends IItemProvider> energyBig,
                                 final Supplier<? extends IItemProvider> energySmall) {

        ShapedRecipeBuilder.shapedRecipe(passiveResult.get())
                .key('C', casing.get())
                .key('B', energyBig.get())
                .key('S', energySmall.get())
                .patternLine("CSC")
                .patternLine("SBS")
                .patternLine("CSC")
                .setGroup(GROUP_TURBINE)
                .addCriterion("has_item", hasItem(casing.get()))
                .addCriterion("has_item2", hasItem(energySmall.get()))
                .build(c, turbineRecipeName(variant, "passivetap_" + name));

        ShapedRecipeBuilder.shapedRecipe(activeResult.get())
                .key('C', casing.get())
                .key('B', energyBig.get())
                .key('S', energySmall.get())
                .patternLine("CBC")
                .patternLine("BSB")
                .patternLine("CBC")
                .setGroup(GROUP_TURBINE)
                .addCriterion("has_item", hasItem(casing.get()))
                .addCriterion("has_item2", hasItem(energyBig.get()))
                .build(c, turbineRecipeName(variant, "activetap_" + name));
    }

    private void turbineComputerPort(final Consumer<IFinishedRecipe> c, final TurbineVariant variant,
                                     final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> casing,
                                     final ITag.INamedTag<Item> metal, @Nullable final ITag.INamedTag<Item> alternativeMetal) {
        recipeWithAlternativeTag(c, turbineRecipeName(variant, "computerport"), turbineRecipeName(variant, "computerport_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shapedRecipe(result.get())
                                .key('C', casing.get())
                                .key('M', metalTag)
                                .key('G', Tags.Items.STORAGE_BLOCKS_GOLD)
                                .key('Z', Tags.Items.GEMS_LAPIS)
                                .key('X', Tags.Items.DUSTS_GLOWSTONE)
                                .patternLine("CZC")
                                .patternLine("MGM")
                                .patternLine("CXC")
                                .setGroup(GROUP_TURBINE)
                                .addCriterion("has_item", hasItem(casing.get())));
    }

    private void turbineFluidPort(final Consumer<IFinishedRecipe> c, final TurbineVariant variant, final String name,
                                  final Supplier<? extends IItemProvider> passiveResult, final Supplier<? extends IItemProvider> activeResult,
                                  final Supplier<? extends IItemProvider> casing, final Supplier<? extends IItemProvider> lava,
                                  final Supplier<? extends IItemProvider> water) {

        ShapedRecipeBuilder.shapedRecipe(passiveResult.get())
                .key('C', casing.get())
                .key('B', lava.get())
                .key('S', water.get())
                .patternLine("CSC")
                .patternLine("SBS")
                .patternLine("CSC")
                .setGroup(GROUP_TURBINE)
                .addCriterion("has_item", hasItem(casing.get()))
                .addCriterion("has_item2", hasItem(water.get()))
                .build(c, turbineRecipeName(variant, "passivefluidport_" + name));

        ShapedRecipeBuilder.shapedRecipe(activeResult.get())
                .key('C', casing.get())
                .key('B', lava.get())
                .key('S', water.get())
                .patternLine("CBC")
                .patternLine("BSB")
                .patternLine("CBC")
                .setGroup(GROUP_TURBINE)
                .addCriterion("has_item", hasItem(casing.get()))
                .addCriterion("has_item2", hasItem(lava.get()))
                .build(c, turbineRecipeName(variant, "activefluidport_" + name));
    }

    private void turbineBlade(final Consumer<IFinishedRecipe> c, final TurbineVariant variant,
                              final Supplier<? extends IItemProvider> result,
                              final ITag.INamedTag<Item> metal, @Nullable final ITag.INamedTag<Item> alternativeMetal) {

        recipeWithAlternativeTag(c, turbineRecipeName(variant, "blade"), turbineRecipeName(variant, "blade_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shapedRecipe(result.get())
                                .key('I', metalTag)
                                .key('C', ContentTags.Items.INGOTS_CYANITE)
                                .patternLine("ICI")
                                .setGroup(GROUP_TURBINE)
                                .addCriterion("has_item", hasItem(ContentTags.Items.INGOTS_CYANITE)));
    }

    private void turbineShaft(final Consumer<IFinishedRecipe> c, final TurbineVariant variant,
                              final Supplier<? extends IItemProvider> result,
                              final ITag.INamedTag<Item> metal, @Nullable final ITag.INamedTag<Item> alternativeMetal) {

        recipeWithAlternativeTag(c, turbineRecipeName(variant, "shaft"), turbineRecipeName(variant, "shaft_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shapedRecipe(result.get())
                                .key('I', metalTag)
                                .key('C', ContentTags.Items.INGOTS_CYANITE)
                                .patternLine(" I ")
                                .patternLine("ICI")
                                .patternLine(" I ")
                                .setGroup(GROUP_TURBINE)
                                .addCriterion("has_item", hasItem(ContentTags.Items.INGOTS_CYANITE)));
    }

    private void turbineBearing(final Consumer<IFinishedRecipe> c, final TurbineVariant variant,
                                final Supplier<? extends IItemProvider> result,
                                final ITag.INamedTag<Item> metal, @Nullable final ITag.INamedTag<Item> alternativeMetal) {

        recipeWithAlternativeTag(c, turbineRecipeName(variant, "bearing"), turbineRecipeName(variant, "bearing_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shapedRecipe(result.get())
                                .key('I', metalTag)
                                .key('R', Blocks.REDSTONE_BLOCK)
                                .key('B', Tags.Items.STORAGE_BLOCKS_IRON)
                                .patternLine("IRI")
                                .patternLine("B B")
                                .patternLine("IRI")
                                .setGroup(GROUP_TURBINE)
                                .addCriterion("has_item", hasItem(Tags.Items.STORAGE_BLOCKS_IRON)));
    }





    private static ResourceLocation turbineRecipeName(final TurbineVariant variant, final String name) {
        return ExtremeReactors.newID("turbine/" + variant.getName() + "/" + name);
    }

    private static ResourceLocation turbineRecipeName(final TurbineVariant variant, final String name, final ITag.INamedTag<Item> tag) {
        return ExtremeReactors.newID("turbine/" + variant.getName() + "/" + name + "_" + tag.getName().getPath().replace('/', '_'));
    }

    //endregion
}
