package it.zerono.mods.extremereactors.loader.forge.gamecontent.worldgen;

import com.mojang.serialization.Codec;
import it.zerono.mods.extremereactors.gamecontent.Content;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.NonNullPredicate;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

import java.util.function.Supplier;

public record OreBiomeModifier(HolderSet<Biome> validBiomes, Holder<PlacedFeature> feature,
                               NonNullPredicate<Holder<Biome>> masterBiomePredicate,
                               Supplier<Codec<OreBiomeModifier>> codecSupplier)
        implements BiomeModifier {

    public static OreBiomeModifier yellorite(HolderSet<Biome> validBiomes, Holder<PlacedFeature> feature) {
        return new OreBiomeModifier(validBiomes, feature, $ -> true, Content.Biomes.OREGEN_YELLORITE);
    }

    public static OreBiomeModifier anglesite(HolderSet<Biome> validBiomes, Holder<PlacedFeature> feature) {
        return new OreBiomeModifier(validBiomes, feature, biomeHolder -> biomeHolder.containsTag(BiomeTags.IS_END),
                Content.Biomes.OREGEN_ANGLESITE);
    }

    public static OreBiomeModifier benitoite(HolderSet<Biome> validBiomes, Holder<PlacedFeature> feature) {
        return new OreBiomeModifier(validBiomes, feature, biomeHolder -> biomeHolder.containsTag(BiomeTags.IS_NETHER),
                Content.Biomes.OREGEN_BENITOITE);
    }

    //regiorn BiomeModifier

    @Override
    public void modify(final Holder<Biome> biome, final Phase phase, final ModifiableBiomeInfo.BiomeInfo.Builder builder) {

        if (Phase.ADD == phase && this.masterBiomePredicate.test(biome) &&
                (this.validBiomes.size() == 0 || this.validBiomes.contains(biome))) {
            builder.getGenerationSettings().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, this.feature);
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return this.codecSupplier.get();
    }

    //endregion
}
