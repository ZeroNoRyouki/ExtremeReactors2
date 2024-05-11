/*
 *
 * FluidizerIcons.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.client.screen;

import it.zerono.mods.extremereactors.CommonLocations;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISprite;
import it.zerono.mods.zerocore.lib.client.gui.sprite.ISpriteBuilder;
import it.zerono.mods.zerocore.lib.client.gui.sprite.SpriteTextureMap;
import net.neoforged.neoforge.common.util.NonNullSupplier;

public enum FluidizerIcons
        implements NonNullSupplier<ISprite> {

    GuiBackground(builder().from(0, 0).ofSize(224, 166).build()),
    GuiInventory9x3(builder().from(0, 202).ofSize(162, 54).build()),
    GuiInventory9x1(builder().from(0, 202).ofSize(162, 18).build()),
    ProgressLeft(builder().from(224, 0).ofSize(24, 16).build()),
    ProgressLeftFilled(builder().from(224, 32).ofSize(24, 16).build()),
    ProgressRight(builder().from(224, 16).ofSize(24, 16).build()),
    ProgressRightFilled(builder().from(224, 48).ofSize(24, 16).build()),

    VertTopBottom(builder().from(224, 64).ofSize(16, 64).build()),
    VertBottomTop(builder().from(240, 64).ofSize(16, 64).build()),
    ;

    FluidizerIcons(final ISprite sprite) {
        this._sprite = sprite;
    }

    public static SpriteTextureMap getMap() {

        if (null == s_map) {
            s_map = new SpriteTextureMap(CommonLocations.TEXTURES_GUI_MULTIBLOCK.buildWithSuffix("fluidizer.png"),
                    256, 256);
        }

        return s_map;
    }

    //region NonNullSupplier<ISprite>

    @Override
    public ISprite get() {
        return this._sprite;
    }

    //endregion
    //region internals

    private static ISpriteBuilder builder() {
        return getMap().sprite();
    }

    private static SpriteTextureMap s_map;
    private final ISprite _sprite;

    //endregion
}
