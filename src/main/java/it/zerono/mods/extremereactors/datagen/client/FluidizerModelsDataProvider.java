package it.zerono.mods.extremereactors.datagen.client;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class FluidizerModelsDataProvider
        extends AbstractMultiblockModelsDataProvider {

    public FluidizerModelsDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                       ResourceLocationBuilder modLocationRoot) {
        super(ExtremeReactors.MOD_NAME + " Fluidizer block states and models", output, lookupProvider, modLocationRoot);
    }

    //region BlockStateDataProvider

    @Override
    public void provideData() {

        final String folder = "fluidizer";

        this.assembledPlatingModel(folder);
        this.assembledPlatingModel("assembledplatingio", "platingio", folder);
        this.multiblockFrame(Content.Blocks.FLUIDIZER_CASING, "casing", folder);
        this.multiblockGlass(Content.Blocks.FLUIDIZER_GLASS, "glass", folder);
        this.controller(Content.Blocks.FLUIDIZER_CONTROLLER, folder);
        this.genericPart(Content.Blocks.FLUIDIZER_SOLIDINJECTOR, "solidinjector", folder, "_connected");
        this.genericPart(Content.Blocks.FLUIDIZER_FLUIDINJECTOR, "fluidinjector", folder, "_connected");
        this.genericPart(Content.Blocks.FLUIDIZER_OUTPUTPORT, "outputport", folder, "_connected");
        this.genericPart(Content.Blocks.FLUIDIZER_POWERPORT, "powerport", folder);
    }

    //endregion
}
