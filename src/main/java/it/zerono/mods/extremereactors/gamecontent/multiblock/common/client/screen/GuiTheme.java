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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.common.util.NonNullSupplier;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public enum GuiTheme
        implements NonNullSupplier<Theme>, PreparableReloadListener {

    ER;

    GuiTheme() {
        this._theme = Theme.DEFAULT;
    }

    //region NonNullSupplier<Theme>

    @NotNull
    @Override
    public Theme get() {
        return this._theme;
    }

    //endregion
    //region PreparableReloadListener

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager resourceManager,
                                          ProfilerFiller prepareProfiler, ProfilerFiller applyProfiler,
                                          Executor prepareExecutor, Executor applyExecutor) {
        return CompletableFuture.supplyAsync(() -> loadTheme(resourceManager), prepareExecutor)
                .thenCompose(barrier::wait)
                .thenAcceptAsync(this::setTheme, applyExecutor);
    }

    //endregion
    //region internals

    private static Theme loadTheme(ResourceManager manager) {
        return manager.getResource(ID)
                .map(Theme::read)
                .orElse(Theme.DEFAULT);
    }

    private void setTheme(Theme theme) {
        this._theme = theme;
    }

    private final static ResourceLocation ID = ExtremeReactors.ROOT_LOCATION.buildWithSuffix("er_gui_theme.json");

    private Theme _theme;

    //endregion
}
