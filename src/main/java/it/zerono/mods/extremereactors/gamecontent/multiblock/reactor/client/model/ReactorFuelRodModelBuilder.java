/*
 *
 * ReactorFuelRodModelBuilder.java
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
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactorPartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.variant.IMultiblockReactorVariant;
import it.zerono.mods.zerocore.lib.client.model.BlockVariantsModelBuilder;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

public class ReactorFuelRodModelBuilder extends BlockVariantsModelBuilder {

    public ReactorFuelRodModelBuilder(final IMultiblockReactorVariant variant) {

        super(true, true, false);

        this.addBlock(ReactorPartType.FuelRod.ordinal(), getBlockStateRL(variant, Direction.Axis.Y), 0, true);

        for (final Direction.Axis axis : Direction.Axis.values()) {
            this.addVariant(ReactorPartType.FuelRod.ordinal(), getBlockStateRL(variant, axis));
        }
    }

    //region internals

    private static ResourceLocation getBlockStateRL(IMultiblockReactorVariant variant, Direction.Axis blockStateVariant) {
        return new ModelResourceLocation(ExtremeReactors.newID(variant.getName() + "_reactorfuelrod"),
                String.format("axis=%s", blockStateVariant.getName2()));
    }

    //endregion
}
