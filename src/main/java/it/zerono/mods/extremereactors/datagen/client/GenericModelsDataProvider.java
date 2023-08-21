package it.zerono.mods.extremereactors.datagen.client;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.datagen.provider.client.state.BlockStateDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class GenericModelsDataProvider
        extends BlockStateDataProvider {

    public GenericModelsDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                     ResourceLocationBuilder modLocationRoot) {
        super(ExtremeReactors.MOD_NAME + " block states and models", output, lookupProvider, modLocationRoot);
    }

    //region BlockStateDataProvider

    @Override
    public void provideData() {

        // blocks

        this.simpleBlock(Content.Blocks.YELLORIUM_BLOCK);
        this.simpleBlock(Content.Blocks.CYANITE_BLOCK);
        this.simpleBlock(Content.Blocks.GRAPHITE_BLOCK);
        this.simpleBlock(Content.Blocks.BLUTONIUM_BLOCK);
        this.simpleBlock(Content.Blocks.MAGENTITE_BLOCK);
        this.simpleBlock(Content.Blocks.LUDICRITE_BLOCK);
        this.simpleBlock(Content.Blocks.RIDICULITE_BLOCK);
        this.simpleBlock(Content.Blocks.INANITE_BLOCK);
        this.simpleBlock(Content.Blocks.INSANITE_BLOCK);
        this.simpleBlock(Content.Blocks.YELLORITE_ORE_BLOCK);
        this.simpleBlock(Content.Blocks.ANGLESITE_ORE_BLOCK);
        this.simpleBlock(Content.Blocks.BENITOITE_ORE_BLOCK);

        // items

        this.simpleItem(Content.Items.YELLORIUM_DUST);
        this.simpleItem(Content.Items.CYANITE_DUST);
        this.simpleItem(Content.Items.GRAPHITE_DUST);
        this.simpleItem(Content.Items.BLUTONIUM_DUST);
        this.simpleItem(Content.Items.MAGENTITE_DUST);
        this.simpleItem(Content.Items.LUDICRITE_DUST);
        this.simpleItem(Content.Items.RIDICULITE_DUST);
        this.simpleItem(Content.Items.INANITE_DUST);
        this.simpleItem(Content.Items.INSANITE_DUST);
        this.simpleItem(Content.Items.YELLORIUM_INGOT);
        this.simpleItem(Content.Items.CYANITE_INGOT);
        this.simpleItem(Content.Items.GRAPHITE_INGOT);
        this.simpleItem(Content.Items.BLUTONIUM_INGOT);
        this.simpleItem(Content.Items.MAGENTITE_INGOT);
        this.simpleItem(Content.Items.LUDICRITE_INGOT);
        this.simpleItem(Content.Items.RIDICULITE_INGOT);
        this.simpleItem(Content.Items.INANITE_INGOT);
        this.simpleItem(Content.Items.INSANITE_INGOT);
        this.simpleItem(Content.Items.YELLORIUM_NUGGET);
        this.simpleItem(Content.Items.BLUTONIUM_NUGGET);
        this.simpleItem(Content.Items.ANGLESITE_CRYSTAL);
        this.simpleItem(Content.Items.BENITOITE_CRYSTAL);
    }

    //endregion
}
