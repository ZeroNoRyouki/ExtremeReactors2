package it.zerono.mods.extremereactors.datagen.client;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CachedSprites;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.datagen.provider.client.atlas.AtlasSpriteSourcesDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class BlockAtlasSpriteSourcesDataProvider
        extends AtlasSpriteSourcesDataProvider {

    public BlockAtlasSpriteSourcesDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup,
                                               ResourceLocationBuilder modLocationRoot) {
        super("Block atlas sprites", output, registryLookup, modLocationRoot);
    }

    @Override
    public void provideData() {
        this.atlas(ATLAS_BLOCKS)
                .addFile(CachedSprites.REACTOR_FUEL_COLUMN_STILL_ID)
                .addFile(CachedSprites.REACTOR_FUEL_COLUMN_FLOWING_ID)
                .addFile(CachedSprites.GUI_CHARGINGPORT_SLOT_ID)
                .addFile(ExtremeReactors.ROOT_LOCATION
                        .appendPath("book", "erguide")
                        .buildWithSuffix("book"));
    }
}
