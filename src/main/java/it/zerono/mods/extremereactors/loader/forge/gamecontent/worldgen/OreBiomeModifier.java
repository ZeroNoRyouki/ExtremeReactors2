package it.zerono.mods.extremereactors.loader.forge.gamecontent.worldgen;

import com.mojang.serialization.MapCodec;
import it.zerono.mods.extremereactors.gamecontent.Content;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.function.Supplier;

public record OreBiomeModifier(HolderSet<Biome> validBiomes, Holder<PlacedFeature> feature,
                               Predicate<Holder<Biome>> masterBiomePredicate,
                               Supplier<@NotNull MapCodec<OreBiomeModifier>> codecSupplier)
        implements BiomeModifier {

    public static OreBiomeModifier yellorite(HolderSet<Biome> validBiomes, Holder<PlacedFeature> feature) {
        return new OreBiomeModifier(validBiomes, feature, $ -> true, Content.Biomes.OREGEN_YELLORITE);
    }

    public static OreBiomeModifier anglesite(HolderSet<Biome> validBiomes, Holder<PlacedFeature> feature) {
        return new OreBiomeModifier(validBiomes, feature, biomeHolder -> biomeHolder.is(BiomeTags.IS_END),
                Content.Biomes.OREGEN_ANGLESITE);
    }

    public static OreBiomeModifier benitoite(HolderSet<Biome> validBiomes, Holder<PlacedFeature> feature) {
        return new OreBiomeModifier(validBiomes, feature, biomeHolder -> biomeHolder.is(BiomeTags.IS_NETHER),
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
    public MapCodec<? extends BiomeModifier> codec() {
        return this.codecSupplier.get();
    }

    //endregion
}
