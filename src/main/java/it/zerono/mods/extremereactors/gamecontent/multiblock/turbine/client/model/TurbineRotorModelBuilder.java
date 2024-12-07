/*
 *
 * TurbineRotorModelBuilder.java
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
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.part.TurbineRotorComponentBlock;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorBladeState;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorShaftState;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.extremereactors.proxy.ClientProxy;
import it.zerono.mods.zerocore.base.multiblock.client.model.AbstractMultiblockModelBuilder;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

import java.util.Arrays;
import java.util.function.Supplier;

public abstract class TurbineRotorModelBuilder
        extends AbstractMultiblockModelBuilder {

    public static final Supplier<BakedModel> BASIC_SHAFT;
    public static final Supplier<BakedModel> REINFORCED_SHAFT;

    public static final Supplier<BakedModel> BASIC_BLADE;
    public static final Supplier<BakedModel> REINFORCED_BLADE;

    public static class Basic
            extends TurbineRotorModelBuilder {

        public Basic() {
            super(TurbineVariant.Basic);
        }

        @Override
        public void build() {

            this.addBlade(Content.Blocks.TURBINE_ROTORBLADE_BASIC.get());
            this.addShaft(Content.Blocks.TURBINE_ROTORSHAFT_BASIC.get());
            this.setFallbackModelData(Content.Blocks.TURBINE_ROTORSHAFT_BASIC.get());
        }
    }

    public static class Reinforced
            extends TurbineRotorModelBuilder {

        public Reinforced() {
            super(TurbineVariant.Reinforced);
        }

        @Override
        public void build() {

            this.addBlade(Content.Blocks.TURBINE_ROTORBLADE_REINFORCED.get());
            this.addShaft(Content.Blocks.TURBINE_ROTORSHAFT_REINFORCED.get());
            this.setFallbackModelData(Content.Blocks.TURBINE_ROTORSHAFT_REINFORCED.get());
        }
    }

    protected TurbineRotorModelBuilder(TurbineVariant variant) {
        super(ExtremeReactors.ROOT_LOCATION.appendPath("block", "turbine", variant.getName()));
    }

    protected void addBlade(TurbineRotorComponentBlock block) {
        this.addRotorComponent(block, RotorBladeState.values(), RotorBladeState.getDefault());
    }

    protected void addShaft(TurbineRotorComponentBlock block) {
        this.addRotorComponent(block, RotorShaftState.values(), RotorShaftState.getDefault());
    }

    //region internals

    static {

        BASIC_BLADE = ClientProxy.getModelSupplier(new ModelResourceLocation(ExtremeReactors.ROOT_LOCATION
                .buildWithSuffix("basic_turbinerotorblade"), "state=z_x_pos"));
        REINFORCED_BLADE = ClientProxy.getModelSupplier(new ModelResourceLocation(ExtremeReactors.ROOT_LOCATION
                .buildWithSuffix("reinforced_turbinerotorblade"), "state=z_x_pos"));

        BASIC_SHAFT = ClientProxy.getModelSupplier(new ModelResourceLocation(ExtremeReactors.ROOT_LOCATION
                .buildWithSuffix("basic_turbinerotorshaft"), "state=y_noblades"));
        REINFORCED_SHAFT = ClientProxy.getModelSupplier(new ModelResourceLocation(ExtremeReactors.ROOT_LOCATION
                .buildWithSuffix("reinforced_turbinerotorshaft"), "state=y_noblades"));
    }

    private <E extends Enum<E> & StringRepresentable> void addRotorComponent(TurbineRotorComponentBlock component,
                                                                             E[] properties, E defaultProperty) {

        final var blockId = BuiltInRegistries.BLOCK.getKey(component);
        final var originalModel = componentModelResourceLocation(blockId, defaultProperty);
        final ModelResourceLocation[] additionalModels = new ModelResourceLocation[properties.length];

        Arrays.setAll(additionalModels, idx -> componentModelResourceLocation(blockId, properties[idx]));
        this.addBlock(component.getPartType().getByteHashCode(), originalModel, true, additionalModels);
    }

    private static ModelResourceLocation componentModelResourceLocation(ResourceLocation blockId,
                                                                        StringRepresentable property) {
        return new ModelResourceLocation(blockId, "state=" + property.getSerializedName());
    }

    //endregion
}
