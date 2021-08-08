/*
 *
 * ReactorGlassModelBuilder.java
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
import it.zerono.mods.zerocore.lib.block.property.BlockFacingsProperty;
import it.zerono.mods.zerocore.lib.client.model.BlockVariantsModelBuilder;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

public class ReactorGlassModelBuilder extends BlockVariantsModelBuilder {

    public ReactorGlassModelBuilder(final IMultiblockReactorVariant variant) {

        super(true, true, false);

        this.addBlock(ReactorPartType.Glass.ordinal(), getBlockStateRL(variant, BlockFacingsProperty.None), 0, false);

        for (final BlockFacingsProperty facing : BlockFacingsProperty.values()) {
            this.addVariant(ReactorPartType.Glass.ordinal(), getBlockStateRL(variant, facing));
        }
    }

    //region internals

    private static ResourceLocation getBlockStateRL(IMultiblockReactorVariant variant, BlockFacingsProperty blockStateVariant) {
        return new ModelResourceLocation(ExtremeReactors.newID(variant.getName() + "_reactorglass"), blockStateVariant.asVariantString());
    }

    //endregion
}
