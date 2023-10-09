package it.zerono.mods.extremereactors.datagen.tag;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.zerocore.lib.datagen.provider.tag.IIntrinsicTagDataProvider;
import it.zerono.mods.zerocore.lib.datagen.provider.tag.ModIntrinsicTagAppender;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.NonNullFunction;

public class FluidTagsDataProvider
        implements IIntrinsicTagDataProvider<Fluid> {

    @Override
    public String getName() {
        return ExtremeReactors.MOD_NAME + " fluids tags";
    }

    @Override
    public void build(HolderLookup.Provider registryLookup,
                      NonNullFunction<TagKey<Fluid>, ModIntrinsicTagAppender<Fluid>> builder) {

        builder.apply(ContentTags.Fluids.STEAM).add(Content.Fluids.STEAM_SOURCE, Content.Fluids.STEAM_FLOWING);
        builder.apply(ContentTags.Fluids.YELLORIUM).add(Content.Fluids.YELLORIUM_SOURCE, Content.Fluids.YELLORIUM_FLOWING);
        builder.apply(ContentTags.Fluids.CYANITE).add(Content.Fluids.CYANITE_SOURCE, Content.Fluids.CYANITE_FLOWING);
        builder.apply(ContentTags.Fluids.BLUTONIUM).add(Content.Fluids.BLUTONIUM_SOURCE, Content.Fluids.BLUTONIUM_FLOWING);
        builder.apply(ContentTags.Fluids.MAGENTITE).add(Content.Fluids.MAGENTITE_SOURCE, Content.Fluids.MAGENTITE_FLOWING);
        builder.apply(ContentTags.Fluids.VERDERIUM).add(Content.Fluids.VERDERIUM_SOURCE, Content.Fluids.VERDERIUM_FLOWING);
        builder.apply(ContentTags.Fluids.ROSSINITE).add(Content.Fluids.ROSSINITE_SOURCE, Content.Fluids.ROSSINITE_FLOWING);
    }
}
