package it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.client.model;

import it.zerono.mods.extremereactors.gamecontent.Content;

public class FluidizerIOModelBuilder
        extends FluidizerModelBuilder {

    public FluidizerIOModelBuilder() {
        super("assembledplatingio");
    }

    @Override
    public void build() {

        this.addIoPort(Content.Blocks.FLUIDIZER_SOLIDINJECTOR.get(), "solidinjector_connected");
        this.addIoPort(Content.Blocks.FLUIDIZER_FLUIDINJECTOR.get(), "fluidinjector_connected");
        this.addIoPort(Content.Blocks.FLUIDIZER_OUTPUTPORT.get(), "outputport_connected");
    }
}
