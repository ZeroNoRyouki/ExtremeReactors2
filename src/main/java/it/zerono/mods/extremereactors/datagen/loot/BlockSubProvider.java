package it.zerono.mods.extremereactors.datagen.loot;

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.component.ReactorFluidAccessPortComponent;
import it.zerono.mods.zerocore.lib.data.component.FluidStackListComponent;
import it.zerono.mods.zerocore.lib.data.component.ItemStackListComponent;
import it.zerono.mods.zerocore.lib.datagen.provider.loot.ModBlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;

import java.util.Set;

public class BlockSubProvider
        extends ModBlockLootSubProvider {

    public BlockSubProvider() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {

        this.dropSelf(Content.Blocks.YELLORIUM_BLOCK, Content.Blocks.CYANITE_BLOCK, Content.Blocks.GRAPHITE_BLOCK,
                Content.Blocks.BLUTONIUM_BLOCK, Content.Blocks.MAGENTITE_BLOCK,
                Content.Blocks.LUDICRITE_BLOCK, Content.Blocks.RIDICULITE_BLOCK, Content.Blocks.INANITE_BLOCK,
                Content.Blocks.INSANITE_BLOCK,
                Content.Blocks.YELLORITE_ORE_BLOCK);

        this.dropOre(Content.Blocks.ANGLESITE_ORE_BLOCK, Content.Items.ANGLESITE_CRYSTAL);
        this.dropOre(Content.Blocks.BENITOITE_ORE_BLOCK, Content.Items.BENITOITE_CRYSTAL);

        this.dropSelf(Content.Blocks.REACTOR_CASING_BASIC, Content.Blocks.REACTOR_GLASS_BASIC,
                Content.Blocks.REACTOR_CONTROLLER_BASIC, Content.Blocks.REACTOR_FUELROD_BASIC,
                Content.Blocks.REACTOR_CONTROLROD_BASIC, Content.Blocks.REACTOR_SOLID_ACCESSPORT_BASIC,
                Content.Blocks.REACTOR_POWERTAP_FE_ACTIVE_BASIC, Content.Blocks.REACTOR_POWERTAP_FE_PASSIVE_BASIC,
                Content.Blocks.REACTOR_CHARGINGPORT_FE_BASIC, Content.Blocks.REACTOR_REDSTONEPORT_BASIC);

        this.dropSelf(Content.Blocks.REACTOR_CASING_REINFORCED, Content.Blocks.REACTOR_GLASS_REINFORCED,
                Content.Blocks.REACTOR_CONTROLLER_REINFORCED, Content.Blocks.REACTOR_FUELROD_REINFORCED,
                Content.Blocks.REACTOR_CONTROLROD_REINFORCED, Content.Blocks.REACTOR_SOLID_ACCESSPORT_REINFORCED,
                Content.Blocks.REACTOR_POWERTAP_FE_ACTIVE_REINFORCED, Content.Blocks.REACTOR_POWERTAP_FE_PASSIVE_REINFORCED,
                Content.Blocks.REACTOR_CHARGINGPORT_FE_REINFORCED, Content.Blocks.REACTOR_CREATIVE_WATER_GENERATOR_REINFORCED,
                Content.Blocks.REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED, Content.Blocks.REACTOR_FLUIDTPORT_FORGE_ACTIVE_REINFORCED,
                Content.Blocks.REACTOR_REDSTONEPORT_REINFORCED, Content.Blocks.REACTOR_COMPUTERPORT_REINFORCED);

        this.dropWithComponents(Content.Blocks.REACTOR_FLUID_ACCESSPORT_REINFORCED, ReactorFluidAccessPortComponent.getComponentType());

        this.dropSelf(Content.Blocks.TURBINE_CASING_BASIC, Content.Blocks.TURBINE_GLASS_BASIC,
                Content.Blocks.TURBINE_CONTROLLER_BASIC, Content.Blocks.TURBINE_ROTORBEARING_BASIC,
                Content.Blocks.TURBINE_ROTORSHAFT_BASIC, Content.Blocks.TURBINE_ROTORBLADE_BASIC,
                Content.Blocks.TURBINE_POWERTAP_FE_ACTIVE_BASIC, Content.Blocks.TURBINE_POWERTAP_FE_PASSIVE_BASIC,
                Content.Blocks.TURBINE_CHARGINGPORT_FE_BASIC,
                Content.Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC, Content.Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC,
                Content.Blocks.TURBINE_CREATIVE_STEAM_GENERATOR_BASIC, Content.Blocks.TURBINE_REDSTONEPORT_BASIC);

        this.dropSelf(Content.Blocks.TURBINE_CASING_REINFORCED, Content.Blocks.TURBINE_GLASS_REINFORCED,
                Content.Blocks.TURBINE_CONTROLLER_REINFORCED, Content.Blocks.TURBINE_ROTORBEARING_REINFORCED,
                Content.Blocks.TURBINE_ROTORSHAFT_REINFORCED, Content.Blocks.TURBINE_ROTORBLADE_REINFORCED,
                Content.Blocks.TURBINE_POWERTAP_FE_ACTIVE_REINFORCED, Content.Blocks.TURBINE_POWERTAP_FE_PASSIVE_REINFORCED,
                Content.Blocks.TURBINE_CHARGINGPORT_FE_REINFORCED,
                Content.Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED, Content.Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED,
                Content.Blocks.TURBINE_CREATIVE_STEAM_GENERATOR_REINFORCED,
                Content.Blocks.TURBINE_REDSTONEPORT_REINFORCED, Content.Blocks.TURBINE_COMPUTERPORT_REINFORCED);

        this.dropSelf(Content.Blocks.REPROCESSOR_CASING, Content.Blocks.REPROCESSOR_GLASS, Content.Blocks.REPROCESSOR_CONTROLLER,
                Content.Blocks.REPROCESSOR_WASTEINJECTOR, Content.Blocks.REPROCESSOR_FLUIDINJECTOR, Content.Blocks.REPROCESSOR_OUTPUTPORT,
                Content.Blocks.REPROCESSOR_POWERPORT, Content.Blocks.REPROCESSOR_COLLECTOR);

        this.dropSelf(Content.Blocks.FLUIDIZER_CASING, Content.Blocks.FLUIDIZER_GLASS, Content.Blocks.FLUIDIZER_CONTROLLER,
                Content.Blocks.FLUIDIZER_OUTPUTPORT, Content.Blocks.FLUIDIZER_POWERPORT);

        this.dropWithComponents(Content.Blocks.FLUIDIZER_FLUIDINJECTOR, FluidStackListComponent.getComponentType());
        this.dropWithComponents(Content.Blocks.FLUIDIZER_SOLIDINJECTOR, ItemStackListComponent.getComponentType());
    }
}
