/*
 *
 * TurbineGameData.java
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

import it.zerono.mods.extremereactors.api.turbine.CoilMaterialRegistry;

final class TurbineGameData {

    static void register() {

        registerCoils();
    }

    //region internals

    private static void registerCoils() {

        CoilMaterialRegistry.register("forge:storage_blocks/iron", 1.0f, 1.0f, 1.0f);
        CoilMaterialRegistry.register("forge:storage_blocks/manasteel", 1.2f, 1.03f, 1.23f);
        CoilMaterialRegistry.register("forge:storage_blocks/elementium", 1.3f, 1.05f, 1.23f);
        CoilMaterialRegistry.register("forge:storage_blocks/nickel", 1.15f, 1.06f, 1.0f);
        CoilMaterialRegistry.register("forge:storage_blocks/gold", 2.0f, 1.0f, 1.75f);
        CoilMaterialRegistry.register("forge:storage_blocks/netherite", 2.2f, 1.02f, 1.8f);
        CoilMaterialRegistry.register("forge:storage_blocks/terrasteel", 2.1f, 1.01f, 1.75f);
        CoilMaterialRegistry.register("forge:storage_blocks/copper", 1.2f, 1.0f, 1.2f);
        CoilMaterialRegistry.register("forge:storage_blocks/tin", 1.1f, 1.0f, 1.1f);
        CoilMaterialRegistry.register("forge:storage_blocks/osmium", 1.2f, 1.0f, 1.2f);
        CoilMaterialRegistry.register("forge:storage_blocks/refined_obsidian", 1.2f, 1.03f, 1.28f);
        CoilMaterialRegistry.register("forge:storage_blocks/refined_glowstone", 1.25f, 1.05f, 1.28f);
        CoilMaterialRegistry.register("forge:storage_blocks/cobalt", 1.2f, 1.0f, 1.2f);
        CoilMaterialRegistry.register("forge:storage_blocks/zinc", 1.35f, 1.0f, 1.3f);
        CoilMaterialRegistry.register("forge:storage_blocks/ardite", 1.35f, 1.0f, 1.3f);
        CoilMaterialRegistry.register("forge:storage_blocks/lead", 1.35f, 1.01f, 1.3f);
        CoilMaterialRegistry.register("forge:storage_blocks/brass", 1.4f, 1f, 1.2f);
//        TurbineCoil.registerBlock("blockAlubrass", 1.4f, 1f, 1.2f);    // Tinkers' Construct
        CoilMaterialRegistry.register("forge:storage_blocks/bronze", 1.4f, 1.0f, 1.2f);
        CoilMaterialRegistry.register("forge:storage_blocks/aluminum", 1.5f, 1.0f, 1.3f);
        CoilMaterialRegistry.register("forge:storage_blocks/steel", 1.5f, 1.0f, 1.3f);
        CoilMaterialRegistry.register("forge:storage_blocks/invar", 1.5f, 1.0f, 1.4f);
        CoilMaterialRegistry.register("forge:storage_blocks/silver", 1.7f, 1.0f, 1.5f);
        CoilMaterialRegistry.register("forge:storage_blocks/signalum", 1.6f, 1.0f, 1.4f);
        CoilMaterialRegistry.register("forge:storage_blocks/lumium", 1.7f, 1.02f, 1.7f);
        CoilMaterialRegistry.register("forge:storage_blocks/electrum", 2.5f, 1.0f, 2.0f);
//        TurbineCoil.registerBlock("blockElectrumFlux", 2.5f, 1.01f, 2.2f);    // Redstone Arsenal, note small energy bonus (7% at 1000RF/t output)
        CoilMaterialRegistry.register("forge:storage_blocks/platinum", 3.0f, 1.0f, 2.5f);
//        TurbineCoil.registerBlock("blockShiny", 3.0f, 1f, 2.5f);	// Thermal Foundation
        CoilMaterialRegistry.register("forge:storage_blocks/manyullyn", 3.5f, 1f, 2.5f);
//        TurbineCoil.registerBlock("blockTitanium", 3.1f, 1f, 2.7f);    // Mariculture
        CoilMaterialRegistry.register("forge:storage_blocks/enderium", 3.0f, 1.02f, 3.0f);
//        TurbineCoil.registerBlock("blockLudicrite", 3.5f, 1.02f, 3.5f);

//        if (enableFantasyMetals) {
//            // Metallurgy fantasy metals
//            TurbineCoil.registerBlock("blockMithril", 2.2f, 1f, 1.5f);
//            TurbineCoil.registerBlock("blockOrichalcum", 2.3f, 1f, 1.7f);
//            TurbineCoil.registerBlock("blockQuicksilver", 2.6f, 1f, 1.8f);
//            TurbineCoil.registerBlock("blockHaderoth", 3.0f, 1f, 2.0f);
//            TurbineCoil.registerBlock("blockCelenegil", 3.3f, 1f, 2.25f);
//            TurbineCoil.registerBlock("blockTartarite", 3.5f, 1f, 2.5f);
//        }
    }

    //endregion
}
