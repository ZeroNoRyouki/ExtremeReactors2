/*
 *
 * CoolantIngredientHelper.java
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

import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.extremereactors.api.coolant.Coolant;
import it.zerono.mods.extremereactors.gamecontent.compat.jei.ExtremeReactorsJeiPlugin;
import mezz.jei.api.ingredients.IIngredientType;

public class CoolantIngredientHelper
        extends AbstractIngredientHelper<Coolant> {

    //region AbstractIngredientHelper<Coolant>

    @Override
    public IIngredientType<Coolant> getIngredientType() {
        return ExtremeReactorsJeiPlugin.COOLANT_INGREDIENT_TYPE;
    }

    @Override
    public Iterable<Integer> getColors(final Coolant coolant) {
        return ObjectLists.singleton(coolant.getColour().toRGB());
    }

    @Override
    public Coolant copyIngredient(final Coolant coolant) {
        return coolant.copy();
    }

    //endregion
}
