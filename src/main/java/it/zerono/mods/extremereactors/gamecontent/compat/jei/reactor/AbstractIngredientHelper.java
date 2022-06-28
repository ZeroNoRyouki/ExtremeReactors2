/*
 *
 * AbstractIngredientHelper.java
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

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.api.internal.AbstractNamedValue;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;

import javax.annotation.Nullable;

public abstract class AbstractIngredientHelper<T extends AbstractNamedValue>
        implements IIngredientHelper<T> {
    
    //region IIngredientHelper<T>

    @Nullable
    @Override
    public T getMatch(final Iterable<T> ingredients, final T ingredientToMatch, final UidContext context) {

        for (final T ingredient : ingredients) {
            if (ingredientToMatch.equals(ingredient)) {
                return ingredient;
            }
        }

        return null;
    }

    @Override
    public String getDisplayName(final T ingredient) {
        return ingredient.getTranslatedName().getString();
    }

    @Override
    public String getUniqueId(final T ingredient, final UidContext context) {
        return ExtremeReactors.MOD_ID + ":" + ingredient.getName();
    }

    @Override
    public String getModId(final T ingredient) {
        return ExtremeReactors.MOD_ID;
    }

    @Override
    public String getResourceId(final T ingredient) {
        return ingredient.getName();
    }

    @Override
    public String getErrorInfo(final @Nullable T ingredient) {
        return null == ingredient ? "" : ingredient.getName();
    }

    //endregion
}
