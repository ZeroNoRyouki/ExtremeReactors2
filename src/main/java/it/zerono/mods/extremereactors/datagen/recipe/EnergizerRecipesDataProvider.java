/*
 * EnergizerRecipesDataProvider
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * Do not remove or edit this header
 *
 */

package it.zerono.mods.extremereactors.datagen.recipe;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnergizerRecipesDataProvider
        extends AbstractRecipesDataProvider {

    public EnergizerRecipesDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup,
                                        ResourceLocationBuilder modLocationRoot) {
        super(ExtremeReactors.MOD_ID, "Energizer recipes", output, registryLookup, modLocationRoot);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> builder) {

        this.casing(builder);
        this.controller(builder);
        this.port(builder, "powerport_fe", Content.Items.ENERGIZER_POWERPORT_FE, () -> Items.REDSTONE_BLOCK,
                Tags.Items.INGOTS_IRON, Tags.Items.DUSTS_GLOWSTONE);
        this.port(builder, "chargingport_fe", Content.Items.ENERGIZER_CHARGINGPORT_FE, () -> Items.LAPIS_BLOCK,
                Tags.Items.INGOTS_IRON, Tags.Items.STORAGE_BLOCKS_REDSTONE);

        // energy core
        this.shaped(RecipeCategory.MISC, Content.Items.ENERGY_CORE)
                .define('A', Content.Items.ANGLESITE_CRYSTAL.get())
                .define('R', Tags.Items.STORAGE_BLOCKS_REDSTONE)
                .define('E', Items.ENDER_EYE)
                .define('M', ContentTags.Items.BLOCKS_MAGENTITE)
                .pattern("ARA")
                .pattern("EME")
                .pattern("ARA")
                .unlockedBy("has_item", has(ContentTags.Items.BLOCKS_MAGENTITE))
                .unlockedBy("has_item2", has(Content.Items.ANGLESITE_CRYSTAL.get()))
                .save(builder, this.energizerRoot().buildWithSuffix("energycore"));

        // energy cell
        this.shaped(RecipeCategory.MISC, Content.Items.ENERGY_CELL)
                .define('B', Content.Items.BENITOITE_CRYSTAL.get())
                .define('G', Tags.Items.STORAGE_BLOCKS_GOLD)
                .define('I', Tags.Items.STORAGE_BLOCKS_IRON)
                .define('C', Content.Items.ENERGY_CORE.get())
                .pattern("BGB")
                .pattern("ICI")
                .pattern("BGB")
                .unlockedBy("has_item", has(Content.Items.ENERGY_CORE.get()))
                .unlockedBy("has_item2", has(Content.Items.ANGLESITE_CRYSTAL.get()))
                .save(builder, this.energizerRoot().buildWithSuffix("energizercell"));
    }

    //region internals

    private void casing(Consumer<FinishedRecipe> builder) {
        this.shaped(RecipeCategory.BUILDING_BLOCKS, Content.Items.ENERGIZER_CASING)
                .define('I', Tags.Items.INGOTS_IRON)
                .define('G', Tags.Items.STORAGE_BLOCKS_GOLD)
                .define('R', Items.REDSTONE_BLOCK)
                .pattern("IRI")
                .pattern("IGI")
                .pattern("IRI")
                .unlockedBy("has_item", has(Tags.Items.STORAGE_BLOCKS_GOLD))
                .unlockedBy("has_item2", has(Items.REDSTONE_BLOCK))
                .save(builder, this.energizerRoot().buildWithSuffix("casing"));
    }

    private void controller(Consumer<FinishedRecipe> builder) {
        this.shaped(RecipeCategory.BUILDING_BLOCKS, Content.Items.ENERGIZER_CONTROLLER)
                .define('C', Content.Items.ENERGIZER_CASING.get())
                .define('G', Tags.Items.INGOTS_GOLD)
                .define('R', Items.REDSTONE_TORCH)
                .define('D', Items.GLOWSTONE)
                .define('X', Items.COMPARATOR)
                .pattern("CXC")
                .pattern("DRD")
                .pattern("CGC")
                .unlockedBy("has_item", has(Content.Items.ENERGIZER_CASING.get()))
                .unlockedBy("has_item2", has(Tags.Items.DUSTS_GLOWSTONE))
                .save(builder, this.energizerRoot().buildWithSuffix("controller"));
    }

    private void port(Consumer<FinishedRecipe> builder, String name, Supplier<? extends ItemLike> result,
                      ItemLike item1, TagKey<Item> tag2, TagKey<Item> tag3) {
        this.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                .define('C', Content.Items.ENERGIZER_CASING.get())
                .define('1', item1)
                .define('2', tag2)
                .define('3', tag3)
                .pattern("C2C")
                .pattern("313")
                .pattern("C2C")
                .unlockedBy("has_item", has(Content.Items.ENERGIZER_CASING.get()))
                .unlockedBy("has_item2", has(item1))
                .save(builder, this.energizerRoot().buildWithSuffix(name));
    }

    //endregion
}
