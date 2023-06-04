package it.zerono.mods.extremereactors.datagen.client;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.datagen.provider.client.model.ParentModel;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ReactorModelsDataProvider
        extends AbstractMultiblockModelsDataProvider {

    public ReactorModelsDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                     ResourceLocationBuilder modLocationRoot) {
        super(ExtremeReactors.MOD_NAME + " Reactor block states and models", output, lookupProvider, modLocationRoot);
    }

    //region BlockStateDataProvider

    @Override
    public void provideData() {

        String variant;

        //region basic

        variant = "reactor/basic";
        this.assembledPlatingModel(variant);
        this.multiblockFrame(Content.Blocks.REACTOR_CASING_BASIC, "casing", variant);
        this.multiblockGlass(Content.Blocks.REACTOR_GLASS_BASIC, "glass", variant);
        this.controller(Content.Blocks.REACTOR_CONTROLLER_BASIC, variant);
        this.controlRod(Content.Blocks.REACTOR_CONTROLROD_BASIC, variant);
        this.fuelRod(Content.Blocks.REACTOR_FUELROD_BASIC, variant);
        this.accessPort(Content.Blocks.REACTOR_SOLID_ACCESSPORT_BASIC, "solid", variant);
        this.redstonePort(Content.Blocks.REACTOR_REDSTONEPORT_BASIC, variant);
        this.genericPart(Content.Blocks.REACTOR_POWERTAP_FE_ACTIVE_BASIC, "powertap_fe_active", variant);
        this.genericPart(Content.Blocks.REACTOR_POWERTAP_FE_PASSIVE_BASIC, "powertap_fe_passive", variant);
        this.genericPart(Content.Blocks.REACTOR_CHARGINGPORT_FE_BASIC, "charging", variant);

        //endregion
        //region reinforced

        variant = "reactor/reinforced";
        this.assembledPlatingModel(variant);
        this.multiblockFrame(Content.Blocks.REACTOR_CASING_REINFORCED, "casing", variant);
        this.multiblockGlass(Content.Blocks.REACTOR_GLASS_REINFORCED, "glass", variant);
        this.controller(Content.Blocks.REACTOR_CONTROLLER_REINFORCED, variant);
        this.controlRod(Content.Blocks.REACTOR_CONTROLROD_REINFORCED, variant);
        this.fuelRod(Content.Blocks.REACTOR_FUELROD_REINFORCED, variant);
        this.accessPort(Content.Blocks.REACTOR_SOLID_ACCESSPORT_REINFORCED, "solid", variant);
        this.accessPort(Content.Blocks.REACTOR_FLUID_ACCESSPORT_REINFORCED, "fluid", variant);
        this.redstonePort(Content.Blocks.REACTOR_REDSTONEPORT_REINFORCED, variant);
        this.genericPart(Content.Blocks.REACTOR_POWERTAP_FE_ACTIVE_REINFORCED, "powertap_fe_active", variant);
        this.genericPart(Content.Blocks.REACTOR_POWERTAP_FE_PASSIVE_REINFORCED, "powertap_fe_passive", variant);
        this.fluidPort(Content.Blocks.REACTOR_FLUIDTPORT_FORGE_ACTIVE_REINFORCED, "fluidport_forge_active", variant);
        this.fluidPort(Content.Blocks.REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED, "fluidport_forge_passive", variant);
        this.fluidPort(Content.Blocks.REACTOR_FLUIDPORT_MEKANISM_PASSIVE_REINFORCED, "fluidport_mekanism_passive", variant);
        this.genericPart(Content.Blocks.REACTOR_CREATIVE_WATER_GENERATOR_REINFORCED, "creative_water_generator", variant);
        this.computerPort(Content.Blocks.REACTOR_COMPUTERPORT_REINFORCED, variant);
        this.genericPart(Content.Blocks.REACTOR_CHARGINGPORT_FE_REINFORCED, "charging", variant);

        //endregion
    }

    //endregion
    //region internals

    private <B extends Block> void controlRod(Supplier<B> block, String subFolder) {
        this.genericPart(block, "controlrod", subFolder);
    }

    private <B extends Block> void fuelRod(Supplier<B> block, String subFolder) {

        final var idBuilder = this.blocksRoot().appendPath(subFolder).append("fuelrod");
        final var parent = ParentModel.of(this.reactorRoot().buildWithSuffix("fuel_rod"), "cap", "side", "particle");
        final var model = this.models()
                .model(idBuilder.build())
                .delegateFor(block)
                .parent(parent)
                .texture("cap", idBuilder.buildWithSuffix("_cap"))
                .texture("side", idBuilder.buildWithSuffix("_vertical"))
                .texture("particle", idBuilder.buildWithSuffix("_vertical"))
                .build();

        this.axisAligned(block, model);
    }

    private <B extends Block> void accessPort(Supplier<B> block, String subType, String subFolder) {

        final var name = "accessport_" + subType;

        this.genericPart(block, name + "_in", subFolder);
        this.genericPartModel(name + "_in_connected", subFolder);
        this.genericPartModel(name + "_out", subFolder);
        this.genericPartModel(name + "_out_connected", subFolder);
    }


    //endregion
}
