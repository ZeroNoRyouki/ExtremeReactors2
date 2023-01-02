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
import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.client.gui.Theme;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public enum GuiTheme
        implements NonNullSupplier<Theme>, PreparableReloadListener {

    ER;

    GuiTheme() {
        CodeHelper.addResourceReloadListener(this);
    }

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
    //region PreparableReloadListener

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager resourceManager,
                                          ProfilerFiller profiler1, ProfilerFiller profiler2,
                                          Executor executor1, Executor executor2) {
        this.reload(resourceManager);
        return CompletableFuture.completedFuture(null);
    }

    //endregion
    //region internals

    private void reload(ResourceManager resourceManager) {

        try {
            this._theme = Theme.read(resourceManager.getResource(ID));
        } catch (IOException e) {
            Log.LOGGER.error("Filed to acquire GUI theme.", e);
        }
    }

    private final static ResourceLocation ID = ExtremeReactors.newID("er_gui_theme.json");

    @Nullable
    private Theme _theme;

    //endregion
}
