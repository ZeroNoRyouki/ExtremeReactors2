/*
 *
 * IFluidizerRecipeHolder.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer;

import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.IFluidizerRecipe;
import it.zerono.mods.zerocore.lib.IDebuggable;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

interface IFluidizerRecipeHolder
        extends ISyncableEntity, IDebuggable {

    interface Callbacks {

        boolean canProcessRecipe(IFluidizerRecipe recipe);

        boolean hasIngredientsChanged();

        void onRecipeTickProcessed(int currentTick);

        void onRecipeChanged(IFluidizerRecipe recipe);
    }

    IFluidizerRecipe.Type getRecipeType();

    double getProgress();

    int getCurrentTick();

    boolean processRecipe();

    void refresh();

    int getEnergyUsageMultiplier();

    boolean isValidIngredient(final ItemStack stack);

    boolean isValidIngredient(final FluidStack stack);
}
