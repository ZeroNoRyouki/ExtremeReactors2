package it.zerono.mods.extremereactors.datagen.tag;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.zerocore.lib.datagen.provider.tag.IIntrinsicTagDataProvider;
import it.zerono.mods.zerocore.lib.datagen.provider.tag.ModIntrinsicTagAppender;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class BlockTagsDataProvider
        implements IIntrinsicTagDataProvider<Block> {

    @Override
    public String getName() {
        return ExtremeReactors.MOD_NAME + " blocks tags";
    }

    @Override
    public void build(HolderLookup.Provider registryLookup,
                      Function<@NotNull TagKey<Block>, @NotNull ModIntrinsicTagAppender<Block>> builder) {

        builder.apply(ContentTags.Blocks.BLOCKS_YELLORIUM).add(Content.Blocks.YELLORIUM_BLOCK);
        builder.apply(ContentTags.Blocks.BLOCKS_RAW_YELLORIUM).add(Content.Blocks.RAW_YELLORIUM_BLOCK);
        builder.apply(ContentTags.Blocks.BLOCKS_CYANITE).add(Content.Blocks.CYANITE_BLOCK);
        builder.apply(ContentTags.Blocks.BLOCKS_BLUTONIUM).add(Content.Blocks.BLUTONIUM_BLOCK);
        builder.apply(ContentTags.Blocks.BLOCKS_MAGENTITE).add(Content.Blocks.MAGENTITE_BLOCK);
        builder.apply(ContentTags.Blocks.BLOCKS_GRAPHITE).add(Content.Blocks.GRAPHITE_BLOCK);
        builder.apply(ContentTags.Blocks.BLOCKS_LUDICRITE).add(Content.Blocks.LUDICRITE_BLOCK);
        builder.apply(ContentTags.Blocks.BLOCKS_RIDICULITE).add(Content.Blocks.RIDICULITE_BLOCK);
        builder.apply(ContentTags.Blocks.BLOCKS_INANITE).add(Content.Blocks.INANITE_BLOCK);
        builder.apply(ContentTags.Blocks.BLOCKS_INSANITE).add(Content.Blocks.INSANITE_BLOCK);

        builder.apply(ContentTags.Blocks.ORE_YELLORITE)
                .add(Content.Blocks.YELLORITE_ORE_BLOCK, Content.Blocks.DEEPSLATE_YELLORITE_ORE_BLOCK);
        builder.apply(ContentTags.Blocks.ORE_ANGLESITE)
                .add(Content.Blocks.ANGLESITE_ORE_BLOCK);
        builder.apply(ContentTags.Blocks.ORE_BENITOITE)
                .add(Content.Blocks.BENITOITE_ORE_BLOCK);
        builder.apply(Tags.Blocks.ORES).add(Content.Blocks.YELLORITE_ORE_BLOCK, Content.Blocks.DEEPSLATE_YELLORITE_ORE_BLOCK,
                Content.Blocks.ANGLESITE_ORE_BLOCK, Content.Blocks.BENITOITE_ORE_BLOCK);
        builder.apply(TagsHelper.BLOCKS.createCommonKey("ores/uranium")).add(Content.Blocks.YELLORITE_ORE_BLOCK);

        builder.apply(TagsHelper.BLOCKS.createCommonKey("storage_blocks/enderium"))
                .addOptional(ResourceLocation.parse("ftbic:enderium_block"));

        builder.apply(ContentTags.Blocks.BLOCKS_YELLORIUM).add(Content.Blocks.YELLORIUM_BLOCK);

        builder.apply(Tags.Blocks.STORAGE_BLOCKS)
                .add(Content.Blocks.YELLORIUM_BLOCK, Content.Blocks.CYANITE_BLOCK,
                        Content.Blocks.BLUTONIUM_BLOCK, Content.Blocks.MAGENTITE_BLOCK,
                        Content.Blocks.RAW_YELLORIUM_BLOCK, Content.Blocks.GRAPHITE_BLOCK);

        Content.Blocks.getAll().forEach(s -> {

            if (!(s.get() instanceof LiquidBlock)) {

                builder.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(s);
                builder.apply(BlockTags.NEEDS_IRON_TOOL).add(s);
            }
        });
    }
}
