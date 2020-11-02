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

import it.zerono.mods.extremereactors.api.reactor.*;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;

final class ReactorGameData {

    static void register() {

        registerReactants();
        registerReactantMappings();
        registerReactions();
        registerModerators();
    }

    //region internals

    private static void registerReactants() {

        // Yellorium
        ReactantsRegistry.register(REACTANT_YELLORIUM, ReactantType.Fuel, 0xc6ba54, "reactant.bigreactors.yellorium");

        // Cyanite
        ReactantsRegistry.register(REACTANT_CYANITE, ReactantType.Waste, 0x5387b7, "reactant.bigreactors.cyanite");
    }

    private static void registerReactantMappings() {

        registerIngotReactantMapping(REACTANT_YELLORIUM, ContentTags.Items.INGOTS_YELLORIUM, 1);
        registerIngotReactantMapping(REACTANT_YELLORIUM, ContentTags.Items.BLOCKS_YELLORIUM, 9);

        registerIngotReactantMapping(REACTANT_CYANITE, ContentTags.Items.INGOTS_CYANITE, 1);
        registerIngotReactantMapping(REACTANT_CYANITE, ContentTags.Items.BLOCKS_CYANITE, 9);
    }

    private static void registerReactions() {

        ReactionsRegistry.register(REACTANT_YELLORIUM, REACTANT_CYANITE, Reaction.STANDARD_REACTIVITY, Reaction.STANDARD_FISSIONRATE);
        //blutonium
    }

    private static void registerModerators() {

        ModeratorsRegistry.registerSolid("forge:storage_blocks/iron", 0.50f, 0.75f, 1.40f, IHeatEntity.CONDUCTIVITY_IRON);
        ModeratorsRegistry.registerSolid("forge:storage_blocks/gold", 0.52f, 0.80f, 1.45f, IHeatEntity.CONDUCTIVITY_GOLD);
        ModeratorsRegistry.registerSolid("forge:storage_blocks/diamond", 0.55f, 0.85f, 1.50f, IHeatEntity.CONDUCTIVITY_DIAMOND);
        ModeratorsRegistry.registerSolid("forge:storage_blocks/emerald", 0.55f, 0.85f, 1.50f, IHeatEntity.CONDUCTIVITY_EMERALD);

        ModeratorsRegistry.registerSolid("forge:glass/colorless", 0.20f, 0.25f, 1.10f, IHeatEntity.CONDUCTIVITY_GLASS);

        ModeratorsRegistry.registerSolid("forge:storage_blocks/copper", 0.50f, 0.75f, 1.40f, IHeatEntity.CONDUCTIVITY_COPPER);
        ModeratorsRegistry.registerSolid("forge:storage_blocks/osmium", 0.51f, 0.77f, 1.41f, IHeatEntity.CONDUCTIVITY_COPPER);
        ModeratorsRegistry.registerSolid("forge:storage_blocks/bronze", 0.51f, 0.77f, 1.41f, IHeatEntity.CONDUCTIVITY_COPPER);
        ModeratorsRegistry.registerSolid("forge:storage_blocks/zinc", 0.51f, 0.77f, 1.41f, IHeatEntity.CONDUCTIVITY_COPPER);
        ModeratorsRegistry.registerSolid("forge:storage_blocks/aluminum",0.50f, 0.78f, 1.42f, IHeatEntity.CONDUCTIVITY_IRON);
        ModeratorsRegistry.registerSolid("forge:storage_blocks/steel", 0.50f, 0.78f, 1.42f, IHeatEntity.CONDUCTIVITY_IRON);
        ModeratorsRegistry.registerSolid("forge:storage_blocks/invar", 0.50f, 0.79f, 1.43f, IHeatEntity.CONDUCTIVITY_IRON);
        ModeratorsRegistry.registerSolid("forge:storage_blocks/silver", 0.51f, 0.79f, 1.43f, IHeatEntity.CONDUCTIVITY_SILVER);

        ModeratorsRegistry.registerSolid("forge:storage_blocks/lead", 0.75f, 0.75f, 1.75f, IHeatEntity.CONDUCTIVITY_SILVER);
        ModeratorsRegistry.registerSolid("forge:storage_blocks/electrum", 0.53f, 0.82f, 1.47f, 2.2f); // Between gold and emerald
        //blockElectrumFlux
        ModeratorsRegistry.registerSolid("forge:storage_blocks/platinum", 0.57f, 0.86f, 1.58f, IHeatEntity.CONDUCTIVITY_EMERALD);
        //blockShiny
        ModeratorsRegistry.registerSolid("forge:storage_blocks/enderium", 0.60f, 0.88f, 1.60f, IHeatEntity.CONDUCTIVITY_DIAMOND);
        //blockTitanium
        //blockDraconium
        //blockDraconiumAwakened
        ModeratorsRegistry.registerSolid("forge:storage_blocks/graphite", 0.10f, 0.50f, 2.00f, IHeatEntity.CONDUCTIVITY_GOLD);

        ModeratorsRegistry.registerSolid("minecraft:ice", 0.33f, 0.33f, 1.15f, IHeatEntity.CONDUCTIVITY_WATER);


//        ReactorInterior.registerBlock("blockSnow", 0.15f, 0.33f, 1.05f, IHeatEntity.conductivityWater / 2f);
//        ReactorInterior.registerBlock("blockElectrumFlux", 0.54f, 0.83f, 1.48f, 2.4f); // Between gold and emerald
//        ReactorInterior.registerBlock("blockShiny", 0.57f, 0.86f, 1.58f, IHeatEntity.conductivityEmerald); // Thermal Foundation Shiny block
//        ReactorInterior.registerBlock("blockTitanium", 0.58f, 0.87f, 1.59f, 2.7f); // Mariculture
//        ReactorInterior.registerBlock("blockDraconium", 0.59f, 0.88f, 1.60f, 3.3f);
//        ReactorInterior.registerBlock("blockDraconiumAwakened", 0.76f, 0.88f, 1.78f, 4.8f);
//            ReactorInterior.registerBlock("blockMithril", 0.53f, 0.81f, 1.45f, IHeatEntity.conductivitySilver);
//            ReactorInterior.registerBlock("blockOrichalcum", 0.52f, 0.83f, 1.46f, 1.7f);    // Between silver and gold
//            ReactorInterior.registerBlock("blockQuicksilver", 0.53f, 0.84f, 1.48f, IHeatEntity.conductivityGold);
//            ReactorInterior.registerBlock("blockHaderoth", 0.54f, 0.84f, 1.49f, IHeatEntity.conductivityEmerald);
//            ReactorInterior.registerBlock("blockCelenegil", 0.54f, 0.84f, 1.49f, IHeatEntity.conductivityDiamond);
//            ReactorInterior.registerBlock("blockTartarite", 0.65f, 0.90f, 1.62f, 4f); // Between diamond and graphene
//            ReactorInterior.registerBlock("blockManyullyn", 0.68f, 0.88f, 1.75f, 4.5f);

    }

    //region helpers

    private static void registerIngotReactantMapping(final String reactantName, final ITag.INamedTag<Item> tag, final int ingotsCount) {
        ReactantMappingsRegistry.register(reactantName,
                ingotsCount * ReactantMappingsRegistry.STANDARD_SOLID_REACTANT_AMOUNT, tag);
    }

//    @Deprecated
//    private static void registerIngotReactantMapping(final String reactantName, final ResourceLocation tagId,
//                                                     final int ingotsCount) {
//        ReactantMappingsRegistry.register(reactantName, ingotsCount * ReactantMappingsRegistry.STANDARD_SOLID_REACTANT_AMOUNT,
//                TagsHelper.ITEMS.createTag(tagId));
//    }

//    @Deprecated
//    private static void registerIngotReactantMapping(final String reactantName, final String tagName, final int ingotsCount) {
//        registerIngotReactantMapping(reactantName, ExtremeReactors.newID(tagName), ingotsCount);
//    }

    //endregion

    private static final String REACTANT_YELLORIUM = "yellorium";
    private static final String REACTANT_CYANITE = "cyanite";

    //endregion
}
