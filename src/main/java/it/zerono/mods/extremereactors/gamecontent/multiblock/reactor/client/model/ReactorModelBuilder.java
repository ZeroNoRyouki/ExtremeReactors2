/*
 *
 * ReactorModelBuilder.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.model;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.ReactorVariant;
import it.zerono.mods.zerocore.base.multiblock.client.model.AbstractCuboidMultiblockModelBuilder;

public abstract class ReactorModelBuilder
        extends AbstractCuboidMultiblockModelBuilder {

    public static class Basic
            extends ReactorModelBuilder {

        public Basic() {
            super(ReactorVariant.Basic);
        }

        @Override
        public void build() {

            this.addCasing(Content.Blocks.REACTOR_CASING_BASIC.get());
            this.addController(Content.Blocks.REACTOR_CONTROLLER_BASIC.get());
            this.addDevice(Content.Blocks.REACTOR_CONTROLROD_BASIC.get());
            this.addIoPort(Content.Blocks.REACTOR_REDSTONEPORT_BASIC.get(), "redstoneport_on");
            this.addDevice(Content.Blocks.REACTOR_CHARGINGPORT_FE_BASIC.get());
            this.addIoPort(Content.Blocks.REACTOR_POWERTAP_FE_PASSIVE_BASIC.get());
            this.addIoPort(Content.Blocks.REACTOR_POWERTAP_FE_ACTIVE_BASIC.get());
            this.addIoPort(Content.Blocks.REACTOR_SOLID_ACCESSPORT_BASIC.get(),
                    "accessport_solid_in_connected",
                    "accessport_solid_out",
                    "accessport_solid_out_connected");
        }
    }

    public static class Reinforced
            extends ReactorModelBuilder {

        public Reinforced() {
            super(ReactorVariant.Reinforced);
        }

        @Override
        public void build() {

            this.addCasing(Content.Blocks.REACTOR_CASING_REINFORCED.get());
            this.addController(Content.Blocks.REACTOR_CONTROLLER_REINFORCED.get());
            this.addDevice(Content.Blocks.REACTOR_CONTROLROD_REINFORCED.get());
            this.addIoPort(Content.Blocks.REACTOR_REDSTONEPORT_REINFORCED.get(), "redstoneport_on");
            this.addDevice(Content.Blocks.REACTOR_CHARGINGPORT_FE_REINFORCED.get());
            this.addIoPort(Content.Blocks.REACTOR_POWERTAP_FE_PASSIVE_REINFORCED.get());
            this.addIoPort(Content.Blocks.REACTOR_POWERTAP_FE_ACTIVE_REINFORCED.get());
            this.addDevice(Content.Blocks.REACTOR_CREATIVE_WATER_GENERATOR_REINFORCED.get());
            this.addIoPort(Content.Blocks.REACTOR_COMPUTERPORT_REINFORCED.get(), "computerport_connected");
            this.addIoPort(Content.Blocks.REACTOR_SOLID_ACCESSPORT_REINFORCED.get(),
                    "accessport_solid_in_connected",
                    "accessport_solid_out",
                    "accessport_solid_out_connected");
            this.addIoPort(Content.Blocks.REACTOR_FLUID_ACCESSPORT_REINFORCED.get(),
                    "accessport_fluid_in_connected",
                    "accessport_fluid_out",
                    "accessport_fluid_out_connected");
            this.addIoPort(Content.Blocks.REACTOR_FLUIDTPORT_FORGE_ACTIVE_REINFORCED.get(),
                    "fluidport_forge_active_cold_connected",
                    "fluidport_forge_active_hot",
                    "fluidport_forge_active_hot_connected");
            this.addIoPort(Content.Blocks.REACTOR_FLUIDPORT_FORGE_PASSIVE_REINFORCED.get(),
                    "fluidport_forge_passive_cold_connected",
                    "fluidport_forge_passive_hot",
                    "fluidport_forge_passive_hot_connected");
            this.addIoPort(Content.Blocks.REACTOR_FLUIDPORT_MEKANISM_PASSIVE_REINFORCED.get(),
                    "fluidport_mekanism_passive_cold_connected",
                    "fluidport_mekanism_passive_hot",
                    "fluidport_mekanism_passive_hot_connected");
        }
    }

    protected ReactorModelBuilder(ReactorVariant variant) {
        super("assembledplating", true,
                ExtremeReactors.ROOT_LOCATION.appendPath("block", "reactor", variant.getName()));
    }
}
