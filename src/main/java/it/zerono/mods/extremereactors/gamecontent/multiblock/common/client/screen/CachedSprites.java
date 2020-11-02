/*
 *
 * CachedSprites.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.zerocore.lib.client.gui.sprite.AtlasSpriteSupplier;
import it.zerono.mods.zerocore.lib.client.gui.sprite.AtlasSpriteTextureMap;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public final class CachedSprites {

    public static final Supplier<ISprite> REACTOR_FUEL_COLUMN_STILL;
    public static final Supplier<ISprite> REACTOR_FUEL_COLUMN_FLOWING;

    public static final Supplier<ISprite> DIRT;

    public static void initialize() {
    }

    static {

        REACTOR_FUEL_COLUMN_STILL = AtlasSpriteSupplier.create(ExtremeReactors.newID("fluid/fluid.fuelcolumn.still"), AtlasSpriteTextureMap.BLOCKS, true);
        REACTOR_FUEL_COLUMN_FLOWING = AtlasSpriteSupplier.create(ExtremeReactors.newID("fluid/fluid.fuelcolumn.flowing"), AtlasSpriteTextureMap.BLOCKS, true);

        DIRT = AtlasSpriteSupplier.create(new ResourceLocation("block/dirt"), AtlasSpriteTextureMap.BLOCKS);
    }

    //region internals

    private CachedSprites() {
    }

    //endregion
}
