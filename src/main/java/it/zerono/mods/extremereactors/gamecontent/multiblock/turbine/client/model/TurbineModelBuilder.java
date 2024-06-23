/*
 *
 * TurbineModelBuilder.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.model;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.zerocore.base.multiblock.client.model.AbstractCuboidMultiblockModelBuilder;

public abstract class TurbineModelBuilder
        extends AbstractCuboidMultiblockModelBuilder {

    public static class Basic
            extends TurbineModelBuilder {

        public Basic() {
            super(TurbineVariant.Basic);
        }

        @Override
        public void build() {

            this.addCasing(Content.Blocks.TURBINE_CASING_BASIC.get());
            this.addController(Content.Blocks.TURBINE_CONTROLLER_BASIC.get());
            this.addDevice(Content.Blocks.TURBINE_ROTORBEARING_BASIC.get());
            this.addDevice(Content.Blocks.TURBINE_CREATIVE_STEAM_GENERATOR_BASIC.get());
            this.addIoPort(Content.Blocks.TURBINE_REDSTONEPORT_BASIC.get(), "redstoneport_on");
            this.addDevice(Content.Blocks.TURBINE_CHARGINGPORT_FE_BASIC.get());
            this.addIoPort(Content.Blocks.TURBINE_POWERTAP_FE_PASSIVE_BASIC.get());
            this.addIoPort(Content.Blocks.TURBINE_POWERTAP_FE_ACTIVE_BASIC.get());
            this.addIoPort(Content.Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_BASIC.get(),
                    "fluidport_forge_active_cold_connected",
                    "fluidport_forge_active_hot",
                    "fluidport_forge_active_hot_connected");
            this.addIoPort(Content.Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_BASIC.get(),
                    "fluidport_forge_passive_cold_connected",
                    "fluidport_forge_passive_hot",
                    "fluidport_forge_passive_hot_connected");
        }
    }

    public static class Reinforced
            extends TurbineModelBuilder {

        public Reinforced() {
            super(TurbineVariant.Reinforced);
        }

        @Override
        public void build() {

            this.addCasing(Content.Blocks.TURBINE_CASING_REINFORCED.get());
            this.addController(Content.Blocks.TURBINE_CONTROLLER_REINFORCED.get());
            this.addDevice(Content.Blocks.TURBINE_ROTORBEARING_REINFORCED.get());
            this.addDevice(Content.Blocks.TURBINE_CREATIVE_STEAM_GENERATOR_REINFORCED.get());
            this.addIoPort(Content.Blocks.TURBINE_REDSTONEPORT_REINFORCED.get(), "redstoneport_on");
            this.addDevice(Content.Blocks.TURBINE_CHARGINGPORT_FE_REINFORCED.get());
            this.addIoPort(Content.Blocks.TURBINE_POWERTAP_FE_PASSIVE_REINFORCED.get());
            this.addIoPort(Content.Blocks.TURBINE_POWERTAP_FE_ACTIVE_REINFORCED.get());
            this.addIoPort(Content.Blocks.TURBINE_COMPUTERPORT_REINFORCED.get(), "computerport_connected");
            this.addIoPort(Content.Blocks.TURBINE_FLUIDPORT_FORGE_PASSIVE_REINFORCED.get(),
                    "fluidport_forge_passive_cold_connected",
                    "fluidport_forge_passive_hot",
                    "fluidport_forge_passive_hot_connected");
            this.addIoPort(Content.Blocks.TURBINE_FLUIDPORT_FORGE_ACTIVE_REINFORCED.get(),
                    "fluidport_forge_passive_cold_connected",
                    "fluidport_forge_passive_hot",
                    "fluidport_forge_passive_hot_connected");
        }
    }

    protected TurbineModelBuilder(TurbineVariant variant) {
        super("assembledplating", true,
                ExtremeReactors.ROOT_LOCATION.appendPath("block", "turbine", variant.getName()));
    }
}
