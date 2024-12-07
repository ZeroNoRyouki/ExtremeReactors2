package it.zerono.mods.extremereactors.datagen.client;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineRotorComponentBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorBladeState;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorShaftState;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.datagen.provider.client.model.ParentModel;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class TurbineModelsDataProvider
        extends AbstractMultiblockModelsDataProvider {

    public TurbineModelsDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                     ResourceLocationBuilder modLocationRoot) {
        super(ExtremeReactors.MOD_NAME + " Turbine block states and models", output, lookupProvider, modLocationRoot);
    }

    //region BlockStateDataProvider

    @Override
    public void provideData() {

        String variant;

        //region basic

        variant = "turbine/basic";
        this.assembledPlatingModel(variant);
        this.multiblockFrame(Content.Blocks.TURBINE_CASING_BASIC, "casing", variant);
        this.multiblockGlass(Content.Blocks.TURBINE_GLASS_BASIC, "glass", variant);
        this.controller(Content.Blocks.TURBINE_CONTROLLER_BASIC, variant);
        this.genericPart(Content.Blocks.TURBINE_ROTORBEARING_BASIC, "bearing", variant);
        this.shaft(Content.Blocks.TURBINE_ROTORSHAFT_BASIC, variant);
        this.blade(Content.Blocks.TURBINE_ROTORBLADE_BASIC, variant);
        this.redstonePort(Content.Blocks.TURBINE_REDSTONEPORT_BASIC, variant);
        this.genericPart(Content.Blocks.TURBINE_POWERTAP_FE_ACTIVE_BASIC, "powertap_fe_active", variant);
        this.genericPart(Content.Blocks.TURBINE_POWERTAP_FE_PASSIVE_BASIC, "powertap_fe_passive", variant);
        this.fluidPort(Content.Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC, "fluidport_forge_active", variant);
        this.fluidPort(Content.Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC, "fluidport_forge_passive", variant);
        this.genericPart(Content.Blocks.TURBINE_CREATIVE_STEAM_GENERATOR_BASIC, "creative_steam_generator", variant);
        this.genericPart(Content.Blocks.TURBINE_CHARGINGPORT_FE_BASIC, "charging", variant);

        //endregion
        //region reinforced

        variant = "turbine/reinforced";
        this.assembledPlatingModel(variant);
        this.multiblockFrame(Content.Blocks.TURBINE_CASING_REINFORCED, "casing", variant);
        this.multiblockGlass(Content.Blocks.TURBINE_GLASS_REINFORCED, "glass", variant);
        this.controller(Content.Blocks.TURBINE_CONTROLLER_REINFORCED, variant);
        this.genericPart(Content.Blocks.TURBINE_ROTORBEARING_REINFORCED, "bearing", variant);
        this.shaft(Content.Blocks.TURBINE_ROTORSHAFT_REINFORCED, variant);
        this.blade(Content.Blocks.TURBINE_ROTORBLADE_REINFORCED, variant);
        this.redstonePort(Content.Blocks.TURBINE_REDSTONEPORT_REINFORCED, variant);
        this.genericPart(Content.Blocks.TURBINE_POWERTAP_FE_ACTIVE_REINFORCED, "powertap_fe_active", variant);
        this.genericPart(Content.Blocks.TURBINE_POWERTAP_FE_PASSIVE_REINFORCED, "powertap_fe_passive", variant);
        this.fluidPort(Content.Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED, "fluidport_forge_active", variant);
        this.fluidPort(Content.Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED, "fluidport_forge_passive", variant);
        this.genericPart(Content.Blocks.TURBINE_CREATIVE_STEAM_GENERATOR_REINFORCED, "creative_steam_generator", variant);
        this.computerPort(Content.Blocks.TURBINE_COMPUTERPORT_REINFORCED, variant);
        this.genericPart(Content.Blocks.TURBINE_CHARGINGPORT_FE_REINFORCED, "charging", variant);

        //endregion
    }

    //endregion
    //region internals

    private <B extends Block> void shaft(Supplier<B> block, String subFolder) {

        final var idBuilder = this.blocksRoot().appendPath(subFolder);
        final var texture = idBuilder.buildWithSuffix("rotorshaft");
        final var z0c = this.models()
                .model(idBuilder.buildWithSuffix("rotorshaft_z_0c"))
                .parent(ParentModel.of(this.turbineRoot().buildWithSuffix("rotor_shaft_z_0c"), "0", "1", "particle"))
                .texture("0", texture)
                .texture("1", texture)
                .texture("particle", texture)
                .build();
        final var z2c = this.models()
                .model(idBuilder.buildWithSuffix("rotorshaft_z_2c"))
                .parent(ParentModel.of(this.turbineRoot().buildWithSuffix("rotor_shaft_z_2c"), "0", "1", "particle"))
                .texture("0", texture)
                .texture("1", texture)
                .texture("particle", texture)
                .build();
        final var z2cy = this.models()
                .model(idBuilder.buildWithSuffix("rotorshaft_z_2cy"))
                .parent(ParentModel.of(this.turbineRoot().buildWithSuffix("rotor_shaft_z_2cy"), "0", "1", "particle"))
                .texture("0", texture)
                .texture("1", texture)
                .texture("particle", texture)
                .build();
        final var z4c = this.models()
                .model(idBuilder.buildWithSuffix("rotorshaft_z_4c"))
                .delegateFor(block)
                .parent(ParentModel.of(this.turbineRoot().buildWithSuffix("rotor_shaft_z_4c"), "0", "1", "particle"))
                .texture("0", texture)
                .texture("1", texture)
                .texture("particle", texture)
                .build();

        this.multiVariant(block)
                .selector(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.HIDDEN, variant -> variant.model(this.transparentBlockModel()))
                .selector(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.Y_NOBLADES, variant -> variant
                        .model(z0c)
                        .xRotation(270))
                .selector(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.Y_X, variant -> variant
                        .model(z2c)
                        .xRotation(270))
                .selector(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.Y_Z, variant -> variant
                        .model(z2c)
                        .xRotation(270)
                        .yRotation(90))
                .selector(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.Y_XZ, variant -> variant
                        .model(z4c)
                        .xRotation(270))
                .selector(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.X_NOBLADES, variant -> variant
                        .model(z0c)
                        .yRotation(270))
                .selector(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.X_Y, variant -> variant
                        .model(z2cy)
                        .yRotation(90))
                .selector(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.X_Z, variant -> variant
                        .model(z2c)
                        .yRotation(270))
                .selector(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.X_YZ, variant -> variant
                        .model(z4c)
                        .yRotation(270))
                .selector(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.Z_NOBLADES, variant -> variant.model(z0c))
                .selector(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.Z_Y, variant -> variant.model(z2cy))
                .selector(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.Z_X, variant -> variant.model(z2c))
                .selector(TurbineRotorComponentBlock.ROTOR_SHAFT_STATE, RotorShaftState.Z_XY, variant -> variant.model(z4c));
    }

    private <B extends Block> void blade(Supplier<B> block, String subFolder) {

        final var idBuilder = this.blocksRoot().appendPath(subFolder);
        final var texture = idBuilder.buildWithSuffix("rotorblade");
        final var z = this.models()
                .model(idBuilder.buildWithSuffix("rotorblade_z"))
                .delegateFor(block)
                .parent(ParentModel.of(this.turbineRoot().buildWithSuffix("rotor_blade_z"), "0", "1", "particle"))
                .texture("0", texture)
                .texture("1", texture)
                .texture("particle", texture)
                .build();
        final var zy = this.models()
                .model(idBuilder.buildWithSuffix("rotorblade_zy"))
                .parent(ParentModel.of(this.turbineRoot().buildWithSuffix("rotor_blade_zy"), "0", "1", "particle"))
                .texture("0", texture)
                .texture("1", texture)
                .texture("particle", texture)
                .build();

        this.multiVariant(block)
                .selector(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.HIDDEN, variant -> variant.model(this.transparentBlockModel()))
                .selector(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.Y_X_POS, variant -> variant
                        .model(z)
                        .xRotation(90))
                .selector(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.Y_X_NEG, variant -> variant
                        .model(z)
                        .xRotation(270)
                        .yRotation(180))
                .selector(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.Y_Z_POS, variant -> variant
                        .model(z)
                        .xRotation(90)
                        .yRotation(90))
                .selector(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.Y_Z_NEG, variant -> variant
                        .model(z)
                        .xRotation(270)
                        .yRotation(270))
                .selector(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.X_Y_POS, variant -> variant
                        .model(zy)
                        .yRotation(90))
                .selector(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.X_Y_NEG, variant -> variant
                        .model(zy)
                        .xRotation(180)
                        .yRotation(270))
                .selector(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.X_Z_POS, variant -> variant
                        .model(z)
                        .yRotation(90))
                .selector(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.X_Z_NEG, variant -> variant
                        .model(z)
                        .yRotation(270))
                .selector(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.Z_Y_POS, variant -> variant
                        .model(zy)
                        .yRotation(180))
                .selector(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.Z_Y_NEG, variant -> variant
                        .model(zy)
                        .xRotation(180))
                .selector(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.Z_X_POS, variant -> variant
                        .model(z)
                        .xRotation(180))
                .selector(TurbineRotorComponentBlock.ROTOR_BLADE_STATE, RotorBladeState.Z_X_NEG, variant -> variant
                        .model(z)
                        .xRotation(180)
                        .yRotation(180));
    }

    //endregion
}
