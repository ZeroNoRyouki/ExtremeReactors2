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

        CoilMaterialRegistry.register("c:storage_blocks/iron", 1.0f, 1.0f, 1.0f);
        CoilMaterialRegistry.register("c:storage_blocks/manasteel", 1.2f, 1.03f, 1.23f);
        CoilMaterialRegistry.register("c:storage_blocks/elementium", 1.3f, 1.05f, 1.23f);
        CoilMaterialRegistry.register("c:storage_blocks/nickel", 1.15f, 1.06f, 1.0f);
        CoilMaterialRegistry.register("c:storage_blocks/gold", 2.0f, 1.0f, 1.75f);
        CoilMaterialRegistry.register("c:storage_blocks/netherite", 2.2f, 1.02f, 1.8f);
        CoilMaterialRegistry.register("c:storage_blocks/terrasteel", 2.1f, 1.01f, 1.75f);
        CoilMaterialRegistry.register("c:storage_blocks/copper", 1.2f, 1.0f, 1.2f);
        CoilMaterialRegistry.register("c:storage_blocks/tin", 1.1f, 1.0f, 1.1f);
        CoilMaterialRegistry.register("c:storage_blocks/osmium", 1.2f, 1.0f, 1.2f);
        CoilMaterialRegistry.register("c:storage_blocks/refined_obsidian", 1.2f, 1.03f, 1.28f);
        CoilMaterialRegistry.register("c:storage_blocks/refined_glowstone", 1.25f, 1.05f, 1.28f);
        CoilMaterialRegistry.register("c:storage_blocks/cobalt", 1.2f, 1.0f, 1.2f);
        CoilMaterialRegistry.register("c:storage_blocks/zinc", 1.35f, 1.0f, 1.3f);
        CoilMaterialRegistry.register("c:storage_blocks/ardite", 1.35f, 1.0f, 1.3f);
        CoilMaterialRegistry.register("c:storage_blocks/lead", 1.35f, 1.01f, 1.3f);
        CoilMaterialRegistry.register("c:storage_blocks/brass", 1.4f, 1f, 1.2f);
//        TurbineCoil.registerBlock("blockAlubrass", 1.4f, 1f, 1.2f);    // Tinkers' Construct
        CoilMaterialRegistry.register("c:storage_blocks/bronze", 1.4f, 1.0f, 1.2f);
        CoilMaterialRegistry.register("c:storage_blocks/aluminum", 1.5f, 1.0f, 1.3f);
        CoilMaterialRegistry.register("c:storage_blocks/steel", 1.5f, 1.0f, 1.3f);
        CoilMaterialRegistry.register("c:storage_blocks/invar", 1.5f, 1.0f, 1.4f);
        CoilMaterialRegistry.register("c:storage_blocks/silver", 1.7f, 1.0f, 1.5f);
        CoilMaterialRegistry.register("c:storage_blocks/signalum", 1.6f, 1.0f, 1.4f);
        CoilMaterialRegistry.register("c:storage_blocks/lumium", 1.7f, 1.02f, 1.7f);
        CoilMaterialRegistry.register("c:storage_blocks/electrum", 2.5f, 1.0f, 2.0f);
//        TurbineCoil.registerBlock("blockElectrumFlux", 2.5f, 1.01f, 2.2f);    // Redstone Arsenal, note small energy bonus (7% at 1000RF/t output)
        CoilMaterialRegistry.register("c:storage_blocks/platinum", 3.0f, 1.0f, 2.5f);
//        TurbineCoil.registerBlock("blockShiny", 3.0f, 1f, 2.5f);	// Thermal Foundation
        CoilMaterialRegistry.register("c:storage_blocks/manyullyn", 3.5f, 1f, 2.5f);
//        TurbineCoil.registerBlock("blockTitanium", 3.1f, 1f, 2.7f);    // Mariculture
        CoilMaterialRegistry.register("c:storage_blocks/enderium", 3.0f, 1.02f, 3.0f);

        CoilMaterialRegistry.register("c:storage_blocks/ludicrite", 5.23f, 1.03939f, 3.45f);
        CoilMaterialRegistry.register("c:storage_blocks/ridiculite", 7.47f, 1.2055f, 3.46f);
        CoilMaterialRegistry.register("c:storage_blocks/inanite", 9.76f, 1.3f, 6.9328f);
        CoilMaterialRegistry.register("c:storage_blocks/insanite", 13.74f, 1.41f, 6.9328f);
    }

    //endregion
}
