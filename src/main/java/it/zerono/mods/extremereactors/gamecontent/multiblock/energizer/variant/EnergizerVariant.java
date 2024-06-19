/*
 * EnergizerVariant
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * Do not remove or edit this header
 *
 */

package it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.variant;

import it.zerono.mods.extremereactors.config.Config;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.data.WideAmount;
import net.minecraft.world.level.block.Block;

public enum EnergizerVariant
        implements IMultiblockEnergizerVariant {

    INSTANCE;

    public static final int MULTIBLOCK_MAX_SIZE = 32;

    //region IMultiblockEnergizerVariant

    @Override
    public WideAmount getStorageCapacityPerBlock() {
        return CAPACITY_PER_BLOCK;
    }

    @Override
    public int getPartEnergyCapacity() {
        return 0;
    }

    @Override
    public float getEnergyGenerationEfficiency() {
        return 1;
    }

    @Override
    public WideAmount getMaxEnergyExtractionRate() {
        return WideAmount.MAX_VALUE;
    }

    @Override
    public double getChargerMaxRate() {
        return getMaxEnergyExtractionRate().doubleValue();
    }

    @Override
    public int getMaximumXSize() {
        return Math.min(Config.COMMON.energizer.maxEnergizerSize.get(), MULTIBLOCK_MAX_SIZE);
    }

    @Override
    public int getMaximumZSize() {
        return this.getMaximumXSize();
    }

    @Override
    public int getMaximumYSize() {
        return Math.min(Config.COMMON.energizer.maxEnergizerHeight.get(), MULTIBLOCK_MAX_SIZE);
    }

    @Override
    public int getMinimumPartsCount() {
        return 26;
    }

    @Override
    public int getId() {
        return this.ordinal();
    }

    @Override
    public String getName() {
        return CodeHelper.neutralLowercase(this.name());
    }

    @Override
    public String getTranslationKey() {
        return "variant.bigreactors.energizer";
    }

    @Override
    public Block.Properties getBlockProperties() {
        return this.getDefaultBlockProperties();
    }

    //endregion
    //region internals

    private static final WideAmount CAPACITY_PER_BLOCK = WideAmount.MAX_VALUE
            .divide((MULTIBLOCK_MAX_SIZE - 2) * (MULTIBLOCK_MAX_SIZE - 2) * (MULTIBLOCK_MAX_SIZE - 2))
            .toImmutable();

    //endregion
}
