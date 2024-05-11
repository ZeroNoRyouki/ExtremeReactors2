/*
 *
 * GuiTheme.java
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
import it.zerono.mods.zerocore.lib.client.gui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.neoforge.common.util.NonNullSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public enum GuiTheme
        implements NonNullSupplier<Theme>, ResourceManagerReloadListener {

    ER;

    //region NonNullSupplier<Theme>

    @Nonnull
    @Override
    public Theme get() {

        if (null == this._theme) {
            this.reload(Minecraft.getInstance().getResourceManager());
        }

        return this._theme;
    }

    //endregion
    //region ResourceManagerReloadListener

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        this.reload(resourceManager);
    }

    //endregion
    //region internals

    private void reload(ResourceManager resourceManager) {
        this._theme = resourceManager.getResource(ID)
                .map(Theme::read)
                .orElse(Theme.DEFAULT);
    }

    private final static ResourceLocation ID = ExtremeReactors.ROOT_LOCATION.buildWithSuffix("er_gui_theme.json");

    @Nullable
    private Theme _theme;

    //endregion
}
