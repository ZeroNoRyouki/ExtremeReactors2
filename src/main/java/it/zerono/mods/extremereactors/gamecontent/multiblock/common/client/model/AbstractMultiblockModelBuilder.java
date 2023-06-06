/*
 *
 * AbstractMultiblockModelBuilder.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.zerocore.lib.block.multiblock.IMultiblockPartType;
import it.zerono.mods.zerocore.lib.client.model.multiblock.CuboidPartVariantsModelBuilder;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import it.zerono.mods.zerocore.lib.multiblock.variant.IMultiblockVariant;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class AbstractMultiblockModelBuilder<PartType extends Enum<PartType> & IMultiblockPartType>
        extends CuboidPartVariantsModelBuilder {

    protected AbstractMultiblockModelBuilder(String multiblockShortName, ResourceLocationBuilder blockRoot,
                                             ResourceLocation template, boolean ambientOcclusion) {
        super(template, ambientOcclusion);

        Preconditions.checkArgument(!Strings.isNullOrEmpty(multiblockShortName),
                "Multiblock short name must not be null or empty");
        Preconditions.checkNotNull(blockRoot, "Block root must not be null");

        this._multiblockShortName = multiblockShortName;
        this._blockRoot = blockRoot;
    }

    protected void addBlockWithVariants(final PartType partType, final IMultiblockVariant variant,
                                      final Predicate<PartType> isPartCompatibleWithVariant, final String blockCommonName,
                                      final String... additionalVariantsModelNames) {

        this.addBlock(partType, variant, isPartCompatibleWithVariant, blockCommonName);
        this.addBlockVariants(partType, variant, isPartCompatibleWithVariant, blockCommonName, additionalVariantsModelNames);
    }

    protected void addBlock(final PartType partType, final IMultiblockVariant variant,
                          final Predicate<PartType> isPartCompatibleWithVariant, final String blockCommonName) {

        if (isPartCompatibleWithVariant.test(partType)) {
            super.addBlock(partType.ordinal(), getBlockStateRL(variant, blockCommonName), 0, false);
        }
    }

    protected void addBlockVariants(final PartType partType, final IMultiblockVariant variant,
                                    final Predicate<PartType> isPartCompatibleWithVariant,
                                    final String blockCommonName,
                                    final String... additionalVariantsModelNames) {

        if (isPartCompatibleWithVariant.test(partType)) {

            final List<ResourceLocation> variants = Lists.newArrayListWithCapacity(1 + additionalVariantsModelNames.length);

            variants.add(getBlockStateRL(variant, blockCommonName));
            Arrays.stream(additionalVariantsModelNames)
                    .map(name -> getModelRL(variant, name))
                    .collect(Collectors.toCollection(() -> variants));

            this.addModels(partType.ordinal(), variants);
        }
    }

    protected ResourceLocation getBlockStateRL(final IMultiblockVariant variant, final String blockCommonName) {
        return getBlockStateRL(variant, blockCommonName, "");
    }

    protected ResourceLocation getBlockStateRL(final IMultiblockVariant variant, final String blockCommonName,
                                               final String blockStateVariant) {
        return new ModelResourceLocation(ExtremeReactors.ROOT_LOCATION.buildWithSuffix(variant.getName() + "_" +
                        this._multiblockShortName + blockCommonName), blockStateVariant);
    }

    protected ResourceLocation getModelRL(final IMultiblockVariant variant, final String modelName) {
        return this._blockRoot
                .appendPath(variant.getName())
                .buildWithSuffix(modelName);
    }

    //region internals

    private final String _multiblockShortName;
    private final ResourceLocationBuilder _blockRoot;

    //endregion
}
