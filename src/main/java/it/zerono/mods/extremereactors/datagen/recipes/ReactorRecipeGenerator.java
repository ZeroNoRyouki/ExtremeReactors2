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
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
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
    protected void registerRecipes(final Consumer<IFinishedRecipe> c) {

        ITag.INamedTag<Item> core, metal, alternativeMetal;
        ReactorVariant variant;
        Supplier<? extends IItemProvider> casing;

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
                casing, () -> net.minecraft.item.Items.REDSTONE_BLOCK, () -> net.minecraft.item.Items.REDSTONE);
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
                casing, () -> net.minecraft.item.Items.REDSTONE_BLOCK, () -> net.minecraft.item.Items.REDSTONE);
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

    private void reactorCasing(final Consumer<IFinishedRecipe> c, final ReactorVariant variant,
                               final Supplier<? extends IItemProvider> result,
                               final ITag<Item> core, final ITag.INamedTag<Item> metal,
                               @Nullable final ITag.INamedTag<Item> alternativeMetal) {

        recipeWithAlternativeTag(c, reactorRecipeName(variant, "casing"), reactorRecipeName(variant, "casing_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shapedRecipe(result.get())
                                .key('I', metalTag)
                                .key('C', core)
                                .key('G', ContentTags.Items.INGOTS_GRAPHITE)
                                .patternLine("IGI")
                                .patternLine("GCG")
                                .patternLine("IGI")
                                .setGroup(GROUP_REACTOR)
                                .addCriterion("has_item", hasItem(ContentTags.Items.INGOTS_GRAPHITE)));
    }

    private void reactorCasingUpgrade(final Consumer<IFinishedRecipe> c, final ReactorVariant variant,
                                      final Supplier<? extends IItemProvider> result, final ITag.INamedTag<Item> metal,
                                      @Nullable final ITag.INamedTag<Item> alternativeMetal) {

        recipeWithAlternativeTag(c, reactorRecipeName(variant, "casing_upgrade"), reactorRecipeName(variant, "casing_upgrade_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shapedRecipe(result.get())
                                .key('I', metalTag)
                                .key('C', Content.Blocks.REACTOR_CASING_BASIC.get())
                                .key('G', ContentTags.Items.INGOTS_GRAPHITE)
                                .patternLine("IGI")
                                .patternLine("GCG")
                                .patternLine("IGI")
                                .setGroup(GROUP_REACTOR)
                                .addCriterion("has_item", hasItem(Content.Blocks.REACTOR_CASING_BASIC.get())));
    }

    private void reactorCasingRecycle(final Consumer<IFinishedRecipe> c, final ReactorVariant variant,
                                      final Supplier<? extends IItemProvider> casingResult,
                                      final ITag.INamedTag<Item> casingSourceTag,
                                      final Supplier<? extends IItemProvider> glassSourceItem) {

        ShapelessRecipeBuilder.shapelessRecipe(casingResult.get(), 1)
                .addIngredient(glassSourceItem.get())
                .setGroup(GROUP_REACTOR)
                .addCriterion("has_item", hasItem(glassSourceItem.get()))
                .build(c, reactorRecipeName(variant, "casing_recycle_glass"));

        ShapelessRecipeBuilder.shapelessRecipe(casingResult.get(), 4)
                .addIngredient(casingSourceTag)
                .setGroup(GROUP_REACTOR)
                .addCriterion("has_item", hasItem(casingSourceTag))
                .build(c, reactorRecipeName(variant, "casing_recycle"));
    }

    private void reactorGlass(final Consumer<IFinishedRecipe> c, final ReactorVariant variant,
                              final Supplier<? extends IItemProvider> result,
                              final Supplier<? extends IItemProvider> casing, final ITag<Item> glass) {
        ShapedRecipeBuilder.shapedRecipe(result.get())
                .key('C', casing.get())
                .key('G', glass)
                .patternLine("GCG")
                .setGroup(GROUP_REACTOR)
                .addCriterion("has_item", hasItem(casing.get()))
                .build(c, reactorRecipeName(variant, "glass"));
    }

    private void reactorController(final Consumer<IFinishedRecipe> c, final ReactorVariant variant,
                                   final Supplier<? extends IItemProvider> result,
                                   final Supplier<? extends IItemProvider> casing, final ITag<Item> diamond) {

        TAGS_YELLORIUM_INGOTS.forEach(yellorium -> ShapedRecipeBuilder.shapedRecipe(result.get())
                .key('C', casing.get())
                .key('Y', yellorium)
                .key('R', Tags.Items.DUSTS_REDSTONE)
                .key('D', diamond)
                .key('X', net.minecraft.item.Items.COMPARATOR)
                .patternLine("CXC")
                .patternLine("YDY")
                .patternLine("CRC")
                .setGroup(GROUP_REACTOR)
                .addCriterion("has_item", hasItem(casing.get()))
                .addCriterion("has_item2", hasItem(yellorium))
                .build(c, reactorRecipeName(variant, "controller", yellorium))
        );
    }

    private void reactorFuelRod(final Consumer<IFinishedRecipe> c, final ReactorVariant variant,
                                final Supplier<? extends IItemProvider> result,
                                final ITag.INamedTag<Item> metal, @Nullable final ITag.INamedTag<Item> alternativeMetal,
                                final ITag<Item> glass) {

        TAGS_YELLORIUM_INGOTS.forEach(yellorium ->
                recipeWithAlternativeTag(c, reactorRecipeName(variant, "fuelrod", yellorium),
                        reactorRecipeName(variant, "fuelrod_alt", yellorium),
                        metal, alternativeMetal, metalTag ->
                                ShapedRecipeBuilder.shapedRecipe(result.get())
                                        .key('M', metalTag)
                                        .key('Y', yellorium)
                                        .key('G', ContentTags.Items.INGOTS_GRAPHITE)
                                        .key('L', glass)
                                        .patternLine("MGM")
                                        .patternLine("LYL")
                                        .patternLine("MGM")
                                        .setGroup(GROUP_REACTOR)
                                        .addCriterion("has_item", hasItem(yellorium))));
    }

    private void reactorControlRod(final Consumer<IFinishedRecipe> c, final ReactorVariant variant,
                                   final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> casing,
                                   final ITag.INamedTag<Item> metal, @Nullable final ITag.INamedTag<Item> alternativeMetal) {

        recipeWithAlternativeTag(c, reactorRecipeName(variant, "controlrod"), reactorRecipeName(variant, "controlrod_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shapedRecipe(result.get())
                                .key('C', casing.get())
                                .key('M', metalTag)
                                .key('R', Tags.Items.DUSTS_REDSTONE)
                                .key('G', ContentTags.Items.INGOTS_GRAPHITE)
                                .key('X', net.minecraft.item.Items.PISTON)
                                .patternLine("CRC")
                                .patternLine("MXM")
                                .patternLine("CGC")
                                .setGroup(GROUP_REACTOR)
                                .addCriterion("has_item", hasItem(casing.get())));
    }

    private void reactorPowerTap(final Consumer<IFinishedRecipe> c, final ReactorVariant variant, final String name,
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
                .setGroup(GROUP_REACTOR)
                .addCriterion("has_item", hasItem(casing.get()))
                .addCriterion("has_item2", hasItem(energySmall.get()))
                .build(c, reactorRecipeName(variant, "passivetap_" + name));

        ShapedRecipeBuilder.shapedRecipe(activeResult.get())
                .key('C', casing.get())
                .key('B', energyBig.get())
                .key('S', energySmall.get())
                .patternLine("CBC")
                .patternLine("BSB")
                .patternLine("CBC")
                .setGroup(GROUP_REACTOR)
                .addCriterion("has_item", hasItem(casing.get()))
                .addCriterion("has_item2", hasItem(energyBig.get()))
                .build(c, reactorRecipeName(variant, "activetap_" + name));
    }

    private void reactorFluidPort(final Consumer<IFinishedRecipe> c, final ReactorVariant variant, final String name,
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
                .setGroup(GROUP_REACTOR)
                .addCriterion("has_item", hasItem(casing.get()))
                .addCriterion("has_item2", hasItem(water.get()))
                .build(c, reactorRecipeName(variant, "passivefluidport_" + name));

        ShapedRecipeBuilder.shapedRecipe(activeResult.get())
                .key('C', casing.get())
                .key('B', lava.get())
                .key('S', water.get())
                .patternLine("CBC")
                .patternLine("BSB")
                .patternLine("CBC")
                .setGroup(GROUP_REACTOR)
                .addCriterion("has_item", hasItem(casing.get()))
                .addCriterion("has_item2", hasItem(lava.get()))
                .build(c, reactorRecipeName(variant, "activefluidport_" + name));
    }


    private void reactorMekFluidPort(final Consumer<IFinishedRecipe> c, final ReactorVariant variant,
                                     final Supplier<? extends IItemProvider> passiveResult,
                                     final Supplier<? extends IItemProvider> casing, final Supplier<? extends IItemProvider> lava,
                                     final Supplier<? extends IItemProvider> water) {

        ConditionalRecipe.builder()
                .addCondition(modLoaded(Mods.MEKANISM.id()))
                .addRecipe(ShapedRecipeBuilder.shapedRecipe(passiveResult.get())
                        .key('C', casing.get())
                        .key('B', lava.get())
                        .key('S', water.get())
                        .key('X', Items.EMERALD_BLOCK)
                        .patternLine("CSC")
                        .patternLine("BXB")
                        .patternLine("CSC")
                        .setGroup(GROUP_REACTOR)
                        .addCriterion("has_item", hasItem(casing.get()))
                        .addCriterion("has_item2", hasItem(water.get()))
                        ::build)
                .build(c, reactorRecipeName(variant, "passivefluidport_mekanism"));
    }

    private void reactorSolidAccessPort(final Consumer<IFinishedRecipe> c, final ReactorVariant variant,
                                        final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> casing,
                                        final ITag.INamedTag<Item> metal, @Nullable final ITag.INamedTag<Item> alternativeMetal) {
        recipeWithAlternativeTag(c, reactorRecipeName(variant, "solidaccessport"), reactorRecipeName(variant, "solidaccessport_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shapedRecipe(result.get())
                                .key('C', casing.get())
                                .key('M', metalTag)
                                .key('H', net.minecraft.item.Items.HOPPER)
                                .key('W', Tags.Items.CHESTS_WOODEN)
                                .key('X', net.minecraft.item.Items.PISTON)
                                .patternLine("CHC")
                                .patternLine("MWM")
                                .patternLine("CXC")
                                .setGroup(GROUP_REACTOR)
                                .addCriterion("has_item", hasItem(casing.get())));
    }

    private void reactorRedstonePort(final Consumer<IFinishedRecipe> c, final ReactorVariant variant,
                                     final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> casing,
                                     final ITag.INamedTag<Item> metal, @Nullable final ITag.INamedTag<Item> alternativeMetal,
                                     final ITag<Item> gold) {
        recipeWithAlternativeTag(c, reactorRecipeName(variant, "redstoneport"), reactorRecipeName(variant, "redstoneport_alt"),
                metal, alternativeMetal, metalTag ->
                        ShapedRecipeBuilder.shapedRecipe(result.get())
                                .key('C', casing.get())
                                .key('M', metalTag)
                                .key('G', gold)
                                .key('Z', net.minecraft.item.Items.COMPARATOR)
                                .key('X', net.minecraft.item.Items.REPEATER)
                                .patternLine("CZC")
                                .patternLine("MGM")
                                .patternLine("CXC")
                                .setGroup(GROUP_REACTOR)
                                .addCriterion("has_item", hasItem(casing.get())));
    }

    private void reactorComputerPort(final Consumer<IFinishedRecipe> c, final ReactorVariant variant,
                                     final Supplier<? extends IItemProvider> result, final Supplier<? extends IItemProvider> casing,
                                     final ITag.INamedTag<Item> metal, @Nullable final ITag.INamedTag<Item> alternativeMetal) {
        recipeWithAlternativeTag(c, reactorRecipeName(variant, "computerport"), reactorRecipeName(variant, "computerport_alt"),
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
                                .setGroup(GROUP_REACTOR)
                                .addCriterion("has_item", hasItem(casing.get())));
    }

    private static ResourceLocation reactorRecipeName(final IMultiblockGeneratorVariant variant, final String name) {
        return ExtremeReactors.newID("reactor/" + variant.getName() + "/" + name);
    }

    private static ResourceLocation reactorRecipeName(final IMultiblockGeneratorVariant variant, final String name,
                                                      final ITag.INamedTag<Item> tag) {
        return ExtremeReactors.newID("reactor/" + variant.getName() + "/" + name + "_" + tag.getName().getPath().replace('/', '_'));
    }

    //endregion
}
