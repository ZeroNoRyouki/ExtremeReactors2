package it.zerono.mods.extremereactors.datagen.tag;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.ContentTags;
import it.zerono.mods.zerocore.lib.datagen.provider.tag.IIntrinsicTagDataProvider;
import it.zerono.mods.zerocore.lib.datagen.provider.tag.ModIntrinsicTagAppender;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ItemTagsDataProvider
        implements IIntrinsicTagDataProvider<Item> {

    @Override
    public String getName() {
        return ExtremeReactors.MOD_NAME + " items tags";
    }

    @Override
    public void build(HolderLookup.Provider registryLookup,
                      Function<@NotNull TagKey<Item>, @NotNull ModIntrinsicTagAppender<Item>> builder) {

        builder.apply(ContentTags.Items.ORE_YELLORITE)
                .add(Content.Items.YELLORITE_ORE_BLOCK)
                .add(Content.Items.DEEPSLATE_YELLORITE_ORE_BLOCK);
        builder.apply(ContentTags.Items.ORE_ANGLESITE)
                .add(Content.Items.ANGLESITE_ORE_BLOCK);
        builder.apply(ContentTags.Items.ORE_BENITOITE)
                .add(Content.Items.BENITOITE_ORE_BLOCK);
        builder.apply(Tags.Items.ORES)
                .add(Content.Items.YELLORITE_ORE_BLOCK)
                .add(Content.Items.DEEPSLATE_YELLORITE_ORE_BLOCK)
                .add(Content.Items.ANGLESITE_ORE_BLOCK)
                .add(Content.Items.BENITOITE_ORE_BLOCK);

        builder.apply(Tags.Items.ORES_IN_GROUND_STONE).add(Content.Items.YELLORITE_ORE_BLOCK);
        builder.apply(Tags.Items.ORES_IN_GROUND_DEEPSLATE).add(Content.Items.DEEPSLATE_YELLORITE_ORE_BLOCK);
        builder.apply(Tags.Items.ORES_IN_GROUND_NETHERRACK).add(Content.Items.BENITOITE_ORE_BLOCK);
        builder.apply(Tags.Items.RAW_MATERIALS).add(Content.Items.RAW_YELLORIUM);
        builder.apply(ContentTags.Items.RAW_MATERIALS_YELLORIUM).add(Content.Items.RAW_YELLORIUM);

        builder.apply(ContentTags.Items.INGOTS_YELLORIUM).add(Content.Items.YELLORIUM_INGOT);
        builder.apply(ContentTags.Items.INGOTS_CYANITE).add(Content.Items.CYANITE_INGOT);
        builder.apply(ContentTags.Items.INGOTS_GRAPHITE).add(Content.Items.GRAPHITE_INGOT);
        builder.apply(ContentTags.Items.INGOTS_BLUTONIUM).add(Content.Items.BLUTONIUM_INGOT);
        builder.apply(ContentTags.Items.INGOTS_MAGENTITE).add(Content.Items.MAGENTITE_INGOT);
        builder.apply(ContentTags.Items.INGOTS_LUDICRITE).add(Content.Items.LUDICRITE_INGOT);
        builder.apply(ContentTags.Items.INGOTS_RIDICULITE).add(Content.Items.RIDICULITE_INGOT);
        builder.apply(ContentTags.Items.INGOTS_INANITE).add(Content.Items.INANITE_INGOT);
        builder.apply(ContentTags.Items.INGOTS_INSANITE).add(Content.Items.INSANITE_INGOT);

        builder.apply(ContentTags.Items.BLOCKS_YELLORIUM).add(Content.Items.YELLORIUM_BLOCK);
        builder.apply(ContentTags.Items.BLOCKS_CYANITE).add(Content.Items.CYANITE_BLOCK);
        builder.apply(ContentTags.Items.BLOCKS_GRAPHITE).add(Content.Items.GRAPHITE_BLOCK);
        builder.apply(ContentTags.Items.BLOCKS_BLUTONIUM).add(Content.Items.BLUTONIUM_BLOCK);
        builder.apply(ContentTags.Items.BLOCKS_MAGENTITE).add(Content.Items.MAGENTITE_BLOCK);
        builder.apply(ContentTags.Items.BLOCKS_LUDICRITE).add(Content.Items.LUDICRITE_BLOCK);
        builder.apply(ContentTags.Items.BLOCKS_RIDICULITE).add(Content.Items.RIDICULITE_BLOCK);
        builder.apply(ContentTags.Items.BLOCKS_INANITE).add(Content.Items.INANITE_BLOCK);
        builder.apply(ContentTags.Items.BLOCKS_INSANITE).add(Content.Items.INSANITE_BLOCK);

        builder.apply(TagsHelper.TAG_WRENCH).add(Content.Items.WRENCH);

        builder.apply(Tags.Items.INGOTS).add(Content.Items.YELLORIUM_INGOT, Content.Items.CYANITE_INGOT,
                Content.Items.GRAPHITE_INGOT, Content.Items.BLUTONIUM_INGOT, Content.Items.MAGENTITE_INGOT,
                Content.Items.LUDICRITE_INGOT, Content.Items.RIDICULITE_INGOT, Content.Items.INANITE_INGOT,
                Content.Items.INSANITE_INGOT);

        builder.apply(ContentTags.Items.INGOTS_URANIUM).add(Content.Items.YELLORIUM_INGOT);
        builder.apply(ContentTags.Items.INGOTS_PLUTONIUM).add(Content.Items.BLUTONIUM_INGOT);
        builder.apply(TagsHelper.ITEMS.createCommonKey("ores/uranium")).add(Content.Items.YELLORITE_ORE_BLOCK);

        builder.apply(Tags.Items.STORAGE_BLOCKS)
                .add(Content.Items.YELLORIUM_BLOCK, Content.Items.CYANITE_BLOCK,
                        Content.Items.BLUTONIUM_BLOCK, Content.Items.MAGENTITE_BLOCK,
                        Content.Items.RAW_YELLORIUM_BLOCK, Content.Items.GRAPHITE_BLOCK);

        builder.apply(ContentTags.Items.USING_REACTOR_CASING_BASIC).add(Content.Items.REACTOR_CONTROLLER_BASIC,
                Content.Items.REACTOR_CONTROLROD_BASIC, Content.Items.REACTOR_SOLID_ACCESSPORT_BASIC,
                Content.Items.REACTOR_POWERTAP_FE_ACTIVE_BASIC, Content.Items.REACTOR_POWERTAP_FE_PASSIVE_BASIC,
                Content.Items.REACTOR_REDSTONEPORT_BASIC, Content.Items.REACTOR_CHARGINGPORT_FE_BASIC);

        builder.apply(ContentTags.Items.USING_REACTOR_CASING_REINFORCED).add(Content.Items.REACTOR_CONTROLLER_REINFORCED,
                Content.Items.REACTOR_CONTROLROD_REINFORCED, Content.Items.REACTOR_SOLID_ACCESSPORT_REINFORCED,
                Content.Items.REACTOR_POWERTAP_FE_ACTIVE_REINFORCED, Content.Items.REACTOR_POWERTAP_FE_PASSIVE_REINFORCED,
                Content.Items.REACTOR_FLUIDPORT_FORGE_ACTIVE_REINFORCED, Content.Items.REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED,
                Content.Items.REACTOR_FLUIDPORT_MEKANISM_PASSIVE_REINFORCED, Content.Items.REACTOR_REDSTONEPORT_REINFORCED,
                Content.Items.REACTOR_COMPUTERPORT_REINFORCED, Content.Items.REACTOR_CHARGINGPORT_FE_REINFORCED);

        builder.apply(ContentTags.Items.USING_TURBINE_CASING_BASIC).add(Content.Items.TURBINE_CONTROLLER_BASIC,
                Content.Items.TURBINE_POWERTAP_FE_ACTIVE_BASIC, Content.Items.TURBINE_POWERTAP_FE_PASSIVE_BASIC,
                Content.Items.TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC, Content.Items.TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC,
                Content.Items.TURBINE_CHARGINGPORT_FE_BASIC);

        builder.apply(ContentTags.Items.USING_TURBINE_CASING_REINFORCED).add(Content.Items.TURBINE_CONTROLLER_REINFORCED,
                Content.Items.TURBINE_POWERTAP_FE_ACTIVE_REINFORCED, Content.Items.TURBINE_POWERTAP_FE_PASSIVE_REINFORCED,
                Content.Items.TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED, Content.Items.TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED,
                Content.Items.TURBINE_COMPUTERPORT_REINFORCED, Content.Items.TURBINE_CHARGINGPORT_FE_REINFORCED);
    }
}
