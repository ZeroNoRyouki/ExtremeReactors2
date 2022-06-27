/*
 *
 * VaporIngredientRenderer.java
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

package it.zerono.mods.extremereactors.gamecontent.compat.jei.reactor;

import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.extremereactors.api.coolant.Vapor;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CachedSprites;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.List;

public class VaporIngredientRenderer
        implements IIngredientRenderer<Vapor> {

    //region IIngredientRenderer<Vapor>

    @Override
    public void render(final MatrixStack matrix, final int x, final int y, @Nullable Vapor vapor) {

        if (null != vapor) {
            ModRenderHelper.paintVerticalProgressSprite(matrix, CachedSprites.WATER_SOURCE.get(),
                    vapor.getColour(), x, y, 0, 16, 16, 0, 1.0d);
        }
    }

    @Override
    public List<ITextComponent> getTooltip(final Vapor vapor, final ITooltipFlag tooltipFlag) {
        return ObjectLists.singleton(vapor.getTranslatedName());
    }

    //endregion
}
