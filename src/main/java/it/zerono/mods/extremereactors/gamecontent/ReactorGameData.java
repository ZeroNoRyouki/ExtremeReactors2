/*
 *
 * ReactorGameData.java
 *
 * This file is part of Extreme Reactors 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * DO NOT REMOVE OR EDIT THIS HEADER
 *
 */

package it.zerono.mods.extremereactors.gamecontent;

import it.zerono.mods.extremereactors.api.coolant.FluidMappingsRegistry;
import it.zerono.mods.extremereactors.api.coolant.FluidsRegistry;
import it.zerono.mods.extremereactors.api.coolant.TransitionsRegistry;
import it.zerono.mods.extremereactors.api.reactor.*;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.Reactants;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

final class ReactorGameData {

    static void register() {

        registerReactants();
        registerReactantMappings();
        registerReactions();
        registerModerators();

        // register coolants and vapors
        registerCoolantsAndVapors();
    }

    //region internals

    private static void registerReactants() {

        // Yellorium (register it as a FuelProperties.DEFAULT fuel)
        registerReactant(Reactants.Yellorium);
        // Blutonium
        registerReactantFuel(Reactants.Blutonium, 2.23f, 0.6f, 2.0f, 0.0137f, 0.0006f);
        // Verderium
        registerReactantFuel(Reactants.Verderium, 3.74f, 0.8741f, 2.0049f, 0.0312f, 0.0081f);

        // Cyanite
        registerReactant(Reactants.Cyanite);
        // Magentite
        registerReactant(Reactants.Magentite);
        // Rossinite
        registerReactant(Reactants.Rossinite);
    }

    private static void registerReactantMappings() {

        registerIngotReactantMapping(Reactants.Yellorium, ContentTags.Items.INGOTS_YELLORIUM, 1);
        registerIngotReactantMapping(Reactants.Yellorium, ContentTags.Items.INGOTS_URANIUM, 1);
        registerIngotReactantMapping(Reactants.Yellorium, ContentTags.Items.BLOCKS_YELLORIUM, 9);
        registerIngotReactantMapping(Reactants.Yellorium, "c:storage_blocks/uranium", 9);

        registerIngotReactantMapping(Reactants.Blutonium, ContentTags.Items.INGOTS_BLUTONIUM, 1);
        registerIngotReactantMapping(Reactants.Blutonium, ContentTags.Items.INGOTS_PLUTONIUM, 1);
        registerIngotReactantMapping(Reactants.Blutonium, ContentTags.Items.BLOCKS_BLUTONIUM, 9);
        registerIngotReactantMapping(Reactants.Blutonium, "c:storage_blocks/plutonium", 9);

        registerIngotReactantMapping(Reactants.Cyanite, ContentTags.Items.INGOTS_CYANITE, 1);
        registerIngotReactantMapping(Reactants.Cyanite, ContentTags.Items.BLOCKS_CYANITE, 9);
        registerIngotReactantMapping(Reactants.Magentite, ContentTags.Items.INGOTS_MAGENTITE, 1);
        registerIngotReactantMapping(Reactants.Magentite, ContentTags.Items.BLOCKS_MAGENTITE, 9);

        registerFluidReactantMapping(Reactants.Yellorium, ContentTags.Fluids.YELLORIUM);
        registerFluidReactantMapping(Reactants.Cyanite, ContentTags.Fluids.CYANITE);
        registerFluidReactantMapping(Reactants.Blutonium, ContentTags.Fluids.BLUTONIUM);
        registerFluidReactantMapping(Reactants.Magentite, ContentTags.Fluids.MAGENTITE);
        registerFluidReactantMapping(Reactants.Verderium, ContentTags.Fluids.VERDERIUM);
        registerFluidReactantMapping(Reactants.Rossinite, ContentTags.Fluids.ROSSINITE);
    }

    private static void registerReactions() {

        ReactionsRegistry.register(Reactants.Yellorium.getReactantName(), Reactants.Cyanite.getReactantName(), Reaction.STANDARD_REACTIVITY, Reaction.STANDARD_FISSIONRATE);
        ReactionsRegistry.register(Reactants.Blutonium.getReactantName(), Reactants.Magentite.getReactantName(), 1.0871f, 0.051f);
        ReactionsRegistry.register(Reactants.Verderium.getReactantName(), Reactants.Rossinite.getReactantName(), 1.0984f, 0.0743f);
    }

    private static void registerModerators() {

        ModeratorsRegistry.registerSolid("c:storage_block/apatite", 0.48f, 0.73f, 1.30f, IHeatEntity.CONDUCTIVITY_STONE);
        ModeratorsRegistry.registerSolid("c:storage_block/cinnabar", 0.48f, 0.75f, 1.32f, IHeatEntity.CONDUCTIVITY_STONE);
        ModeratorsRegistry.registerSolid("c:storage_blocks/iron", 0.50f, 0.75f, 1.40f, IHeatEntity.CONDUCTIVITY_IRON);
        ModeratorsRegistry.registerSolid("c:storage_blocks/manasteel", 0.60f, 0.75f, 1.50f, IHeatEntity.CONDUCTIVITY_IRON);
        ModeratorsRegistry.registerSolid("c:storage_blocks/elementium", 0.61f, 0.77f, 1.52f, IHeatEntity.CONDUCTIVITY_EMERALD);
        ModeratorsRegistry.registerSolid("c:storage_blocks/nickel", 0.51f, 0.77f, 1.40f, IHeatEntity.CONDUCTIVITY_IRON);
        ModeratorsRegistry.registerSolid("c:storage_blocks/gold", 0.52f, 0.80f, 1.45f, IHeatEntity.CONDUCTIVITY_GOLD);
        ModeratorsRegistry.registerSolid("c:storage_blocks/diamond", 0.55f, 0.85f, 1.50f, IHeatEntity.CONDUCTIVITY_DIAMOND);
        ModeratorsRegistry.registerSolid("c:storage_blocks/netherite", 0.55f, 0.95f, 1.65f, IHeatEntity.CONDUCTIVITY_DIAMOND);
        ModeratorsRegistry.registerSolid("c:storage_blocks/terrasteel", 0.57f, 0.87f, 1.52f, IHeatEntity.CONDUCTIVITY_DIAMOND);
        ModeratorsRegistry.registerSolid("c:storage_blocks/emerald", 0.55f, 0.85f, 1.50f, IHeatEntity.CONDUCTIVITY_EMERALD);
        ModeratorsRegistry.registerSolid("c:glass/colorless", 0.20f, 0.25f, 1.10f, IHeatEntity.CONDUCTIVITY_GLASS);
        ModeratorsRegistry.registerSolid("c:storage_blocks/copper", 0.50f, 0.75f, 1.40f, IHeatEntity.CONDUCTIVITY_COPPER);
        ModeratorsRegistry.registerSolid("c:storage_blocks/brass", 0.52f, 0.78f, 1.42f, IHeatEntity.CONDUCTIVITY_COPPER);
        ModeratorsRegistry.registerSolid("c:storage_blocks/osmium", 0.51f, 0.77f, 1.41f, IHeatEntity.CONDUCTIVITY_COPPER);
        ModeratorsRegistry.registerSolid("c:storage_blocks/refined_obsidian", 0.53f, 0.79f, 1.42f, IHeatEntity.CONDUCTIVITY_COPPER);
        ModeratorsRegistry.registerSolid("c:storage_blocks/refined_glowstone", 0.54f, 0.79f, 1.44f, IHeatEntity.CONDUCTIVITY_EMERALD);
        ModeratorsRegistry.registerSolid("c:storage_blocks/bronze", 0.51f, 0.77f, 1.41f, IHeatEntity.CONDUCTIVITY_COPPER);
        ModeratorsRegistry.registerSolid("c:storage_blocks/zinc", 0.51f, 0.77f, 1.41f, IHeatEntity.CONDUCTIVITY_COPPER);
        ModeratorsRegistry.registerSolid("c:storage_blocks/aluminum",0.50f, 0.78f, 1.42f, IHeatEntity.CONDUCTIVITY_IRON);
        ModeratorsRegistry.registerSolid("c:storage_blocks/steel", 0.50f, 0.78f, 1.42f, IHeatEntity.CONDUCTIVITY_IRON);
        ModeratorsRegistry.registerSolid("c:storage_blocks/invar", 0.50f, 0.79f, 1.43f, IHeatEntity.CONDUCTIVITY_IRON);
        ModeratorsRegistry.registerSolid("c:storage_blocks/tin", 0.50f, 0.73f, 1.38f, IHeatEntity.CONDUCTIVITY_SILVER);
        ModeratorsRegistry.registerSolid("c:storage_blocks/silver", 0.51f, 0.79f, 1.43f, IHeatEntity.CONDUCTIVITY_SILVER);
        ModeratorsRegistry.registerSolid("c:storage_blocks/signalum", 0.51f, 0.75f, 1.42f, IHeatEntity.CONDUCTIVITY_COPPER);
        ModeratorsRegistry.registerSolid("c:storage_blocks/lumium", 0.51f, 0.79f, 1.45f, IHeatEntity.CONDUCTIVITY_SILVER);
        ModeratorsRegistry.registerSolid("c:storage_blocks/lead", 0.75f, 0.75f, 1.75f, IHeatEntity.CONDUCTIVITY_SILVER);
        ModeratorsRegistry.registerSolid("c:storage_blocks/electrum", 0.53f, 0.82f, 1.47f, 2.2f); // Between gold and emerald
        ModeratorsRegistry.registerSolid("c:storage_blocks/platinum", 0.57f, 0.86f, 1.58f, IHeatEntity.CONDUCTIVITY_EMERALD);
        ModeratorsRegistry.registerSolid("c:storage_blocks/enderium", 0.60f, 0.88f, 1.60f, IHeatEntity.CONDUCTIVITY_DIAMOND);
        //blockTitanium
        ModeratorsRegistry.registerSolid("c:storage_blocks/draconium", 0.59f, 0.88f, 1.60f, 3.3f);
        ModeratorsRegistry.registerSolid("c:storage_blocks/draconium_awakened", 0.76f, 0.88f, 1.78f, 4.8f);
        ModeratorsRegistry.registerSolid("c:storage_blocks/graphite", 0.10f, 0.50f, 2.00f, IHeatEntity.CONDUCTIVITY_GOLD);

        // Thermal Extra
        ModeratorsRegistry.registerSolid("c:storage_blocks/dragonsteel", 0.55f, 0.81f, 1.46f, IHeatEntity.CONDUCTIVITY_SILVER);
        ModeratorsRegistry.registerSolid("c:storage_blocks/twinite", 0.50f, 0.78f, 1.42f, IHeatEntity.CONDUCTIVITY_COPPER);
        ModeratorsRegistry.registerSolid("c:storage_blocks/shellite", 0.52f, 0.78f, 1.42f, IHeatEntity.CONDUCTIVITY_GOLD);

        // allthemodium
        ModeratorsRegistry.registerSolid("c:storage_blocks/allthemodium", 0.697f, 0.942f, 3.84f, 3.65f);
        ModeratorsRegistry.registerSolid("c:storage_blocks/vibranium", 0.24f, 0.81f, 7.47f, 4.38f);
        ModeratorsRegistry.registerSolid("c:storage_blocks/unobtainium", 0.972f, 0.91f, 3.074f, IHeatEntity.CONDUCTIVITY_GRAPHENE);

        ModeratorsRegistry.registerSolid("minecraft:ice", 0.33f, 0.33f, 1.15f, IHeatEntity.CONDUCTIVITY_WATER);
        ModeratorsRegistry.registerSolid("bigreactors:dry_ice", 0.42f, 0.52f, 1.32f, IHeatEntity.CONDUCTIVITY_WATER);

        // fluids

        ModeratorsRegistry.registerFluid("bigreactors:cryomisi", 0.75f, 0.55f, 1.60f, IHeatEntity.CONDUCTIVITY_EMERALD);
        ModeratorsRegistry.registerFluid("bigreactors:cryomisi_flowing", 0.68f, 0.49f, 1.40f, IHeatEntity.CONDUCTIVITY_EMERALD);

        ModeratorsRegistry.registerFluid("bigreactors:tangerium", 0.90f, 0.75f, 2.00f, IHeatEntity.CONDUCTIVITY_GOLD);
        ModeratorsRegistry.registerFluid("bigreactors:tangerium_flowing", 0.84f, 0.69f, 1.70f, IHeatEntity.CONDUCTIVITY_GOLD);

        ModeratorsRegistry.registerFluid("bigreactors:redfrigium", 0.66f, 0.95f, 6.00f, IHeatEntity.CONDUCTIVITY_DIAMOND);
        ModeratorsRegistry.registerFluid("bigreactors:redfrigium_flowing", 0.57f, 0.86f, 5.00f, IHeatEntity.CONDUCTIVITY_DIAMOND);

        ModeratorsRegistry.registerFluid("minecraft:water", Moderator.WATER.getAbsorption(), Moderator.WATER.getHeatEfficiency(), Moderator.WATER.getModeration(), Moderator.WATER.getHeatConductivity());
        ModeratorsRegistry.registerFluid("minecraft:flowing_water", Moderator.WATER.getAbsorption(), Moderator.WATER.getHeatEfficiency(), Moderator.WATER.getModeration(), Moderator.WATER.getHeatConductivity());

        // Astral Sorcery
        ModeratorsRegistry.registerFluid("astralsorcery:liquid_starlight", 0.92f, 0.80f, 2.00f, IHeatEntity.CONDUCTIVITY_DIAMOND);
        ModeratorsRegistry.registerFluid("astralsorcery:liquid_starlight_flowing", 0.80f, 0.85f, 2.00f, IHeatEntity.CONDUCTIVITY_DIAMOND);

        // Blood Magic
        ModeratorsRegistry.registerFluid("bloodmagic:life_essence_fluid", 0.80f, 0.55f, 1.75f, IHeatEntity.CONDUCTIVITY_EMERALD);
        ModeratorsRegistry.registerFluid("bloodmagic:life_essence_fluid_flowing", 0.70f, 0.55f, 1.75f, IHeatEntity.CONDUCTIVITY_EMERALD);

        // Mekanism
        ModeratorsRegistry.registerFluid("mekanism:hydrofluoric_acid", 0.68f, 0.45f, 1.40f, IHeatEntity.CONDUCTIVITY_EMERALD);
        ModeratorsRegistry.registerFluid("mekanism:flowing_hydrofluoric_acid", 0.60f, 0.45f, 1.40f, IHeatEntity.CONDUCTIVITY_EMERALD);
        ModeratorsRegistry.registerFluid("mekanism:sodium", 0.28f, 0.60f, 1.70f, IHeatEntity.CONDUCTIVITY_COPPER);
        ModeratorsRegistry.registerFluid("mekanism:flowing_sodium", 0.23f, 0.60f, 1.70f, IHeatEntity.CONDUCTIVITY_COPPER);
        ModeratorsRegistry.registerFluid("mekanism:hydrogen_chloride", 0.38f, 0.65f, 1.70f, IHeatEntity.CONDUCTIVITY_COPPER);
        ModeratorsRegistry.registerFluid("mekanism:flowing_hydrogen_chloride", 0.31f, 0.65f, 1.70f, IHeatEntity.CONDUCTIVITY_COPPER);
        ModeratorsRegistry.registerFluid("mekanism:ethene", 0.45f, 0.65f, 1.90f, IHeatEntity.CONDUCTIVITY_SILVER); // Etilene
        ModeratorsRegistry.registerFluid("mekanism:flowing_ethene", 0.37f, 0.65f, 1.90f, IHeatEntity.CONDUCTIVITY_SILVER); // Etilene

        // Thermal
        ModeratorsRegistry.registerFluid("thermal:ender", 0.92f, 0.76f, 2.02f, IHeatEntity.CONDUCTIVITY_GOLD); // Resonant Ender
        ModeratorsRegistry.registerFluid("thermal:ender_flowing", 0.90f, 0.75f, 2.00f, IHeatEntity.CONDUCTIVITY_GOLD);
        ModeratorsRegistry.registerFluid("thermal:redstone", 0.77f, 0.56f, 1.61f, IHeatEntity.CONDUCTIVITY_EMERALD); // Destabilized Redstone
        ModeratorsRegistry.registerFluid("thermal:redstone_flowing", 0.75f, 0.55f, 1.60f, IHeatEntity.CONDUCTIVITY_EMERALD);

        // allthemodium
        ModeratorsRegistry.registerFluid("allthemodium:molten_allthemodium", 0.697f, 0.942f, 3.84f, 3.65f);
        ModeratorsRegistry.registerFluid("allthemodium:molten_vibranium", 0.24f, 0.81f, 7.47f, 4.38f);
        ModeratorsRegistry.registerFluid("allthemodium:molten_unobtainium", 0.972f, 0.91f, 3.074f, IHeatEntity.CONDUCTIVITY_GRAPHENE);

        /*

        ReactorInterior.registerFluid("liquidredstone", 0.65f, 0.45f, 1.50f, IHeatEntity.conductivityEmerald); // Substratum Liquid Redstone (toned down because a bit cheap on the crafting side)

        ReactorInterior.registerFluid("fluidtesla", 0.75f, 0.55f, 1.60f, IHeatEntity.conductivityEmerald); // Modularity

        ReactorInterior.registerFluid("liquidglowstone", 0.18f, 0.50f, 1.55f, IHeatEntity.conductivityCopper); // Substratum Liquid Glowstone (toned down because a bit cheap on the crafting side)

        ReactorInterior.registerFluid("cryotheum", 0.66f, 0.95f, 6.00f, IHeatEntity.conductivityDiamond); // Thermal Foundation Gelid Cryotheum: an amazing moderator!

        ReactorInterior.registerFluid("liquidenderpearl", 0.75f, 0.60f, 1.80f, IHeatEntity.conductivityGold); // Substratum Liquid Ender Pearl (toned down because a bit cheap on the crafting side)

        ReactorInterior.registerFluid("pyrotheum", 0.66f, 0.90f, 1.00f, IHeatEntity.conductivityIron); // Thermal Foundation Blazing Pyrotheum

        if (Config.SERVER.general.enableComedy.get()) {

            ReactorInterior.registerFluid("blueslime", 0.33f, 0.50f, 1.35f, IHeatEntity.conductivityGold); // From Tinker's Construct
            ReactorInterior.registerFluid("purpleslime", 0.56f, 0.75f, 1.65f, IHeatEntity.conductivityGold); // From Tinker's Construct
            ReactorInterior.registerFluid("meat", 0.40f, 0.60f, 1.33f, IHeatEntity.conductivityStone);
            ReactorInterior.registerFluid("pinkSlime", 0.45f, 0.70f, 1.50f, IHeatEntity.conductivityIron);
            ReactorInterior.registerFluid("sewage", 0.50f, 0.65f, 1.44f, IHeatEntity.conductivityIron);
        }
         */

//        ReactorInterior.registerBlock("blockSnow", 0.15f, 0.33f, 1.05f, IHeatEntity.conductivityWater / 2f);
//        ReactorInterior.registerBlock("blockElectrumFlux", 0.54f, 0.83f, 1.48f, 2.4f); // Between gold and emerald
//        ReactorInterior.registerBlock("blockShiny", 0.57f, 0.86f, 1.58f, IHeatEntity.conductivityEmerald); // Thermal Foundation Shiny block
//        ReactorInterior.registerBlock("blockTitanium", 0.58f, 0.87f, 1.59f, 2.7f); // Mariculture
//            ReactorInterior.registerBlock("blockMithril", 0.53f, 0.81f, 1.45f, IHeatEntity.conductivitySilver);
//            ReactorInterior.registerBlock("blockOrichalcum", 0.52f, 0.83f, 1.46f, 1.7f);    // Between silver and gold
//            ReactorInterior.registerBlock("blockQuicksilver", 0.53f, 0.84f, 1.48f, IHeatEntity.conductivityGold);
//            ReactorInterior.registerBlock("blockHaderoth", 0.54f, 0.84f, 1.49f, IHeatEntity.conductivityEmerald);
//            ReactorInterior.registerBlock("blockCelenegil", 0.54f, 0.84f, 1.49f, IHeatEntity.conductivityDiamond);
//            ReactorInterior.registerBlock("blockTartarite", 0.65f, 0.90f, 1.62f, 4f); // Between diamond and graphene
//            ReactorInterior.registerBlock("blockManyullyn", 0.68f, 0.88f, 1.75f, 4.5f);

    }

    private static void registerCoolantsAndVapors() {

        // water -> steam (and vice versa)

        FluidsRegistry.registerCoolant("water", 100.0f,
                4.0f, // Thermal Expansion converts 1 mB steam into 2 RF of work in a steam turbine, so we assume it's 50% efficient.
                "coolant.bigreactors.water", 0x3F76E4);
        FluidMappingsRegistry.registerCoolantMapping("water", 1, ContentTags.Fluids.WATER);

        FluidsRegistry.registerVapor("steam", 10.0f, "vapor.bigreactors.steam", 0XFFFFFF);
        FluidMappingsRegistry.registerVaporMapping("steam", 1, ContentTags.Fluids.STEAM);

        TransitionsRegistry.register("water", "steam");
    }

    //region helpers

    private static void registerReactant(final Reactants reactant) {
        ReactantsRegistry.register(reactant.getReactantName(), reactant.getType(), reactant.getColour(), reactant.getLangKey());
    }

    private static void registerReactantFuel(final Reactants reactant, final float moderationFactor,
                                             final float absorptionCoefficient, final float hardnessDivisor,
                                             final float fissionEventsPerFuelUnit, final float fuelUnitsPerFissionEvent) {
        ReactantsRegistry.registerFuel(reactant.getReactantName(), reactant.getColour(), moderationFactor, absorptionCoefficient,
                hardnessDivisor, fissionEventsPerFuelUnit, fuelUnitsPerFissionEvent, reactant.getLangKey());
    }

    private static void registerIngotReactantMapping(final Reactants reactant, final TagKey<Item> tag, final int ingotsCount) {
        ReactantMappingsRegistry.registerSolid(reactant.getReactantName(), ingotsCount * ReactantMappingsRegistry.STANDARD_SOLID_REACTANT_AMOUNT, tag);
    }

    private static void registerIngotReactantMapping(final Reactants reactant, final String itemTagId, final int ingotsCount) {
        ReactantMappingsRegistry.registerSolid(reactant.getReactantName(), ingotsCount * ReactantMappingsRegistry.STANDARD_SOLID_REACTANT_AMOUNT, itemTagId);
    }

    private static void registerFluidReactantMapping(final Reactants reactant, final TagKey<Fluid> tag) {
        ReactantMappingsRegistry.registerFluid(reactant.getReactantName(), tag);
    }

    //endregion
    //endregion
}
