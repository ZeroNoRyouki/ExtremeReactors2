package it.zerono.mods.extremereactors.datagen.client;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class ReprocessorModelsDataProvider
        extends AbstractMultiblockModelsDataProvider {

    public ReprocessorModelsDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                         ResourceLocationBuilder modLocationRoot) {
        super(ExtremeReactors.MOD_NAME + " Reprocessor block states and models", output, lookupProvider, modLocationRoot);
    }

    //region BlockStateDataProvider

    @Override
    public void provideData() {


        final String folder = "reprocessor";

        this.assembledPlatingModel(folder);
        this.assembledPlatingModel("assembledplatingio", "platingio", folder);
        this.multiblockFrame(Content.Blocks.REPROCESSOR_CASING, "casing", folder);
        this.multiblockGlass(Content.Blocks.REPROCESSOR_GLASS, "glass", folder);
        this.controller(Content.Blocks.REPROCESSOR_CONTROLLER, folder);
        this.genericPart(Content.Blocks.REPROCESSOR_WASTEINJECTOR, "wasteinjector", folder, "_connected");
        this.genericPart(Content.Blocks.REPROCESSOR_FLUIDINJECTOR, "fluidinjector", folder, "_connected");
        this.genericPart(Content.Blocks.REPROCESSOR_OUTPUTPORT, "outputport", folder, "_connected");
        this.genericPart(Content.Blocks.REPROCESSOR_POWERPORT, "powerport", folder);
        this.genericPart(Content.Blocks.REPROCESSOR_COLLECTOR, "collector", folder);
    }
}
