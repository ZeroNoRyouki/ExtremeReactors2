/*
 *
 * DataGenerationHandler.java
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

package it.zerono.mods.extremereactors.datagen;

import it.zerono.mods.extremereactors.datagen.loot.BlockSubProvider;
import it.zerono.mods.extremereactors.datagen.recipe.*;
import it.zerono.mods.extremereactors.datagen.tag.BlockTagsDataProvider;
import it.zerono.mods.extremereactors.datagen.tag.FluidTagsDataProvider;
import it.zerono.mods.extremereactors.datagen.tag.ItemTagsDataProvider;
import it.zerono.mods.zerocore.lib.datagen.IModDataGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class DataGenerationHandler
        implements Consumer<@NotNull IModDataGenerator> {

    @Override
    public void accept(@NotNull IModDataGenerator generator) {

        // tags

        generator.addBlockTagsProvider(new BlockTagsDataProvider());
        generator.addItemTagsProvider(new ItemTagsDataProvider());
        generator.addFluidTagsProvider(new FluidTagsDataProvider());

        // loot

        generator.addLootProvider(builder -> builder.addBlockProvider(BlockSubProvider::new));

        // recipes

        generator.addRecipeProvider("Generic recipes", GenericRecipesDataProvider::new);
        generator.addRecipeProvider("Reactor recipes", ReactorRecipesDataProvider::new);
        generator.addRecipeProvider("Turbine recipes", TurbineRecipesDataProvider::new);
        generator.addRecipeProvider("Reprocessor recipes", ReprocessorRecipesDataProvider::new);
        generator.addRecipeProvider("Fluidizer recipes", FluidizerRecipesDataProvider::new);
        generator.addRecipeProvider("Energizer recipes", EnergizerRecipesDataProvider::new);

        // atlas

        generator.addProvider(it.zerono.mods.extremereactors.datagen.client.BlockAtlasSpriteSourcesDataProvider::new);

        // block states and models

        generator.addProvider(it.zerono.mods.extremereactors.datagen.client.GenericModelsDataProvider::new);
        generator.addProvider(it.zerono.mods.extremereactors.datagen.client.ReactorModelsDataProvider::new);
        generator.addProvider(it.zerono.mods.extremereactors.datagen.client.TurbineModelsDataProvider::new);
        generator.addProvider(it.zerono.mods.extremereactors.datagen.client.ReprocessorModelsDataProvider::new);
        generator.addProvider(it.zerono.mods.extremereactors.datagen.client.FluidizerModelsDataProvider::new);
        generator.addProvider(it.zerono.mods.extremereactors.datagen.client.EnergizerModelsDataProvider::new);
    }
}
