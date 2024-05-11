package it.zerono.mods.extremereactors.datagen;

import it.zerono.mods.extremereactors.datagen.loot.BlockSubProvider;
import it.zerono.mods.extremereactors.datagen.tag.BlockTagsDataProvider;
import it.zerono.mods.extremereactors.datagen.tag.FluidTagsDataProvider;
import it.zerono.mods.extremereactors.datagen.tag.ItemTagsDataProvider;
import it.zerono.mods.zerocore.lib.datagen.IModDataGenerator;
import net.neoforged.neoforge.common.util.NonNullConsumer;
import org.jetbrains.annotations.NotNull;

public class DataGenerationHandler
        implements NonNullConsumer<IModDataGenerator> {

    @Override
    public void accept(@NotNull IModDataGenerator generator) {

        // tags

        generator.addBlockTagsProvider(new BlockTagsDataProvider());
        generator.addItemTagsProvider(new ItemTagsDataProvider());
        generator.addFluidTagsProvider(new FluidTagsDataProvider());

        // loot

        generator.addLootProvider(builder -> builder.addBlockProvider(BlockSubProvider::new));

        // recipes

        generator.addProvider(it.zerono.mods.extremereactors.datagen.recipe.GenericRecipesDataProvider::new);
        generator.addProvider(it.zerono.mods.extremereactors.datagen.recipe.ReactorRecipesDataProvider::new);
        generator.addProvider(it.zerono.mods.extremereactors.datagen.recipe.TurbineRecipesDataProvider::new);
        generator.addProvider(it.zerono.mods.extremereactors.datagen.recipe.ReprocessorRecipesDataProvider::new);
        generator.addProvider(it.zerono.mods.extremereactors.datagen.recipe.FluidizerRecipesDataProvider::new);

        // atlas

        generator.addProvider(it.zerono.mods.extremereactors.datagen.client.BlockAtlasSpriteSourcesDataProvider::new);

        // block states and models

        generator.addProvider(it.zerono.mods.extremereactors.datagen.client.GenericModelsDataProvider::new);
        generator.addProvider(it.zerono.mods.extremereactors.datagen.client.ReactorModelsDataProvider::new);
        generator.addProvider(it.zerono.mods.extremereactors.datagen.client.TurbineModelsDataProvider::new);
        generator.addProvider(it.zerono.mods.extremereactors.datagen.client.ReprocessorModelsDataProvider::new);
        generator.addProvider(it.zerono.mods.extremereactors.datagen.client.FluidizerModelsDataProvider::new);
    }
}
