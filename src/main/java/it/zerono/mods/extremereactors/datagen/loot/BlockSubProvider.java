/*
 *
 * BlockSubProvider.java
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

package it.zerono.mods.extremereactors.datagen.loot;

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.component.ReactorFluidAccessPortComponent;
import it.zerono.mods.zerocore.lib.data.component.FluidStackListComponent;
import it.zerono.mods.zerocore.lib.data.component.ItemStackListComponent;
import it.zerono.mods.zerocore.lib.datagen.provider.loot.ModBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.flag.FeatureFlags;

import java.util.Set;

public class BlockSubProvider
        extends ModBlockLootSubProvider {

    public BlockSubProvider(HolderLookup.Provider provider) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
    }

    @Override
    protected void generate() {

        this.dropSelf(Content.Blocks.YELLORIUM_BLOCK, Content.Blocks.RAW_YELLORIUM_BLOCK,
                Content.Blocks.CYANITE_BLOCK, Content.Blocks.GRAPHITE_BLOCK,
                Content.Blocks.BLUTONIUM_BLOCK, Content.Blocks.MAGENTITE_BLOCK,
                Content.Blocks.LUDICRITE_BLOCK, Content.Blocks.RIDICULITE_BLOCK, Content.Blocks.INANITE_BLOCK,
                Content.Blocks.INSANITE_BLOCK);

        this.dropOre(Content.Blocks.YELLORITE_ORE_BLOCK, Content.Items.RAW_YELLORIUM);
        this.dropOre(Content.Blocks.DEEPSLATE_YELLORITE_ORE_BLOCK, Content.Items.RAW_YELLORIUM);
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

        this.dropSelf(Content.Blocks.ENERGIZER_CELL, Content.Blocks.ENERGIZER_CASING, Content.Blocks.ENERGIZER_CONTROLLER,
                Content.Blocks.ENERGIZER_POWERPORT_FE, Content.Blocks.ENERGIZER_CHARGINGPORT_FE, Content.Blocks.ENERGIZER_STATUS_DISPLAY);

    }
}
