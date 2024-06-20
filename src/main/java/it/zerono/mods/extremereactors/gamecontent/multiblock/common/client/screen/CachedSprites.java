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
import it.zerono.mods.extremereactors.gamecontent.CommonConstants;
import it.zerono.mods.zerocore.lib.client.gui.sprite.AtlasSpriteSupplier;
import it.zerono.mods.zerocore.lib.client.gui.sprite.AtlasSpriteTextureMap;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public final class CachedSprites {

    public static final ResourceLocation REACTOR_FUEL_COLUMN_STILL_ID;
    public static final Supplier<ISprite> REACTOR_FUEL_COLUMN_STILL;
    public static final ResourceLocation REACTOR_FUEL_COLUMN_FLOWING_ID;
    public static final Supplier<ISprite> REACTOR_FUEL_COLUMN_FLOWING;
    public static final Supplier<ISprite> WATER_SOURCE;

    public static final ResourceLocation GUI_CHARGINGPORT_SLOT_ID;
    public static final Supplier<ISprite> GUI_CHARGINGPORT_SLOT;

    public static final Supplier<ISprite> VANILLA_BUCKET;

    public static void initialize() {
    }

    static {

        final var fuelColumnIdBuilder = ExtremeReactors.ROOT_LOCATION.appendPath("fluid").append("fluid.fuelcolumn.");

        REACTOR_FUEL_COLUMN_STILL_ID = fuelColumnIdBuilder.buildWithSuffix("still");
        REACTOR_FUEL_COLUMN_FLOWING_ID = fuelColumnIdBuilder.buildWithSuffix("flowing");

        REACTOR_FUEL_COLUMN_STILL = AtlasSpriteSupplier.create(REACTOR_FUEL_COLUMN_STILL_ID, AtlasSpriteTextureMap.BLOCKS);
        REACTOR_FUEL_COLUMN_FLOWING = AtlasSpriteSupplier.create(REACTOR_FUEL_COLUMN_FLOWING_ID, AtlasSpriteTextureMap.BLOCKS);
        WATER_SOURCE = AtlasSpriteSupplier.create(CommonConstants.FLUID_TEXTURE_SOURCE_WATER, AtlasSpriteTextureMap.BLOCKS);

        GUI_CHARGINGPORT_SLOT_ID = ExtremeReactors.ROOT_LOCATION.appendPath("gui", "multiblock").buildWithSuffix("charging");
        GUI_CHARGINGPORT_SLOT = AtlasSpriteSupplier.create(GUI_CHARGINGPORT_SLOT_ID, AtlasSpriteTextureMap.BLOCKS);

        VANILLA_BUCKET  = AtlasSpriteSupplier.create(ResourceLocation.parse("minecraft:item/bucket"), AtlasSpriteTextureMap.BLOCKS);
    }

    //region internals

    private CachedSprites() {
    }

    //endregion
}
