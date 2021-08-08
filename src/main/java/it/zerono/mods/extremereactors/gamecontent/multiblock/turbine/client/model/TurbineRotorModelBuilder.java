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
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.TurbinePartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorBladeState;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorShaftState;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.IMultiblockTurbineVariant;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.extremereactors.proxy.ClientProxy;
import it.zerono.mods.zerocore.lib.client.model.BlockVariantsModelBuilder;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class TurbineRotorModelBuilder
        extends BlockVariantsModelBuilder {

    public static final Supplier<BakedModel> BASIC_SHAFT = ClientProxy.getModelSupplier(getBlockStateRL(TurbineVariant.Basic, RotorShaftState.getDefault()));
    public static final Supplier<BakedModel> REINFORCED_SHAFT = ClientProxy.getModelSupplier(getBlockStateRL(TurbineVariant.Reinforced, RotorShaftState.getDefault()));

    public static final Supplier<BakedModel> BASIC_BLADE = ClientProxy.getModelSupplier(getBlockStateRL(TurbineVariant.Basic, RotorBladeState.getDefault()));
    public static final Supplier<BakedModel> REINFORCED_BLADE = ClientProxy.getModelSupplier(getBlockStateRL(TurbineVariant.Reinforced, RotorBladeState.getDefault()));

    public TurbineRotorModelBuilder(final IMultiblockTurbineVariant variant) {

        super(true, true, false);

        this.addBlock(TurbinePartType.RotorBlade.ordinal(), getBlockStateRL(variant, RotorBladeState.getDefault()), 0, true);

        for (final RotorBladeState state : RotorBladeState.VALUES) {
            this.addVariant(TurbinePartType.RotorBlade.ordinal(), getBlockStateRL(variant, state));
        }

        this.addBlock(TurbinePartType.RotorShaft.ordinal(), getBlockStateRL(variant, RotorShaftState.getDefault()), 0, true);

        for (final RotorShaftState state : RotorShaftState.VALUES) {
            this.addVariant(TurbinePartType.RotorShaft.ordinal(), getBlockStateRL(variant, state));
        }
    }

    public static ResourceLocation getBlockStateRL(final IMultiblockTurbineVariant variant, final RotorBladeState blockStateVariant) {
        return new ModelResourceLocation(ExtremeReactors.newID(variant.getName() + "_turbinerotorblade"),
                String.format("state=%s", blockStateVariant.getSerializedName()));
    }

    public static ResourceLocation getBlockStateRL(final IMultiblockTurbineVariant variant, final RotorShaftState blockStateVariant) {
        return new ModelResourceLocation(ExtremeReactors.newID(variant.getName() + "_turbinerotorshaft"),
                String.format("state=%s", blockStateVariant.getSerializedName()));
    }
}
