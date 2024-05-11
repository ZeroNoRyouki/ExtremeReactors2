/*
 *
 * FluidizerRecipeWrapper.java
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

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerFluidMixingRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerSolidMixingRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.FluidizerSolidRecipe;
import it.zerono.mods.extremereactors.gamecontent.multiblock.fluidizer.recipe.IFluidizerRecipe;
import it.zerono.mods.zerocore.lib.IDebugMessages;
import it.zerono.mods.zerocore.lib.recipe.ModRecipe;
import it.zerono.mods.zerocore.lib.recipe.holder.IHeldRecipe;
import it.zerono.mods.zerocore.lib.recipe.holder.IRecipeHolder;
import it.zerono.mods.zerocore.lib.recipe.holder.RecipeHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.function.Function;

class FluidizerRecipeHolder<Recipe extends ModRecipe & IFluidizerRecipe>
        implements IFluidizerRecipeHolder {

    public static IFluidizerRecipeHolder solid(final IFluidizerRecipeHolder.Callbacks callbacks,
                                               final Function<IRecipeHolder<FluidizerSolidRecipe>, IHeldRecipe<FluidizerSolidRecipe>> factory) {
        return new FluidizerRecipeHolder<>(IFluidizerRecipe.Type.Solid,
                RecipeHolder.builder(factory, recipe -> recipe.getRecipeType().getTicks())
                        .onHasIngredientsChanged(callbacks::hasIngredientsChanged)
                        .onRecipeTickProcessed(callbacks::onRecipeTickProcessed)
                        .onCanProcess(callbacks::canProcessRecipe)
                        .onRecipeChanged(callbacks::onRecipeChanged)
                        .build());
    }

    public static IFluidizerRecipeHolder solidMixing(final IFluidizerRecipeHolder.Callbacks callbacks,
                                                     final Function<IRecipeHolder<FluidizerSolidMixingRecipe>, IHeldRecipe<FluidizerSolidMixingRecipe>> factory) {
        return new FluidizerRecipeHolder<>(IFluidizerRecipe.Type.SolidMixing,
                RecipeHolder.builder(factory, recipe -> recipe.getRecipeType().getTicks())
                        .onHasIngredientsChanged(callbacks::hasIngredientsChanged)
                        .onRecipeTickProcessed(callbacks::onRecipeTickProcessed)
                        .onCanProcess(callbacks::canProcessRecipe)
                        .onRecipeChanged(callbacks::onRecipeChanged)
                        .build());
    }

    public static IFluidizerRecipeHolder fluidMixing(final IFluidizerRecipeHolder.Callbacks callbacks,
                                                     final Function<IRecipeHolder<FluidizerFluidMixingRecipe>, IHeldRecipe<FluidizerFluidMixingRecipe>> factory) {
        return new FluidizerRecipeHolder<>(IFluidizerRecipe.Type.FluidMixing,
                RecipeHolder.builder(factory, recipe -> recipe.getRecipeType().getTicks())
                        .onHasIngredientsChanged(callbacks::hasIngredientsChanged)
                        .onRecipeTickProcessed(callbacks::onRecipeTickProcessed)
                        .onCanProcess(callbacks::canProcessRecipe)
                        .onRecipeChanged(callbacks::onRecipeChanged)
                        .build());
    }

    //region IFluidizerRecipeHolder

    @Override
    public IFluidizerRecipe.Type getRecipeType() {
        return this._recipeType;
    }

    @Override
    public double getProgress() {
        return this._recipeHolder.getHeldRecipe().map(IHeldRecipe::getProgress).orElse(0.0d);
    }

    @Override
    public int getCurrentTick() {
        return this._recipeHolder.getHeldRecipe().map(IHeldRecipe::getCurrentTick).orElse(-1);
    }

    @Override
    public boolean processRecipe() {
        return this._recipeHolder.getCurrentRecipe().map(IHeldRecipe::processRecipe).orElse(false);
    }

    @Override
    public void refresh() {
        this._recipeHolder.refresh();
    }

    @Override
    public int getEnergyUsageMultiplier() {
        return this._recipeHolder.getCurrentRecipe().map(held -> held.getRecipe().getEnergyUsageMultiplier()).orElse(1);
    }

    @Override
    public boolean isValidIngredient(final ItemStack stack) {
        return Content.Recipes.FLUIDIZER_RECIPE_TYPE.get().findFirst(recipe -> {

            if (recipe instanceof FluidizerSolidRecipe) {
                return ((FluidizerSolidRecipe) recipe).matchIgnoreAmount(stack);
            } else if (recipe instanceof FluidizerSolidMixingRecipe) {
                return ((FluidizerSolidMixingRecipe) recipe).matchIgnoreAmount(stack);
            } else {
                return false;
            }
        }).isPresent();
    }

    @Override
    public boolean isValidIngredient(final FluidStack stack) {
        return Content.Recipes.FLUIDIZER_RECIPE_TYPE.get().findFirst(recipe -> {

            if (recipe instanceof FluidizerFluidMixingRecipe) {
                return ((FluidizerFluidMixingRecipe) recipe).matchIgnoreAmount(stack);
            } else {
                return false;
            }
        }).isPresent();
    }

    //endregion
    //region ISyncableEntity

    /**
     * Sync the entity data from the given {@link CompoundTag}
     *
     * @param data       the {@link CompoundTag} to read from
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public void syncDataFrom(final CompoundTag data, final SyncReason syncReason) {

        this._recipeHolder.refresh();
        this.syncChildDataEntityFrom(this._recipeHolder, "recipe", data, syncReason);
    }

    /**
     * Sync the entity data to the given {@link CompoundTag}
     *
     * @param data       the {@link CompoundTag} to write to
     * @param syncReason the reason why the synchronization is necessary
     * @return the {@link CompoundTag} the data was written to (usually {@code data})
     */
    @Override
    public CompoundTag syncDataTo(final CompoundTag data, final SyncReason syncReason) {

        this.syncChildDataEntityTo(this._recipeHolder, "recipe", data, syncReason);
        return data;
    }

    //endregion
    //region IDebuggable

    /**
     * @param side     the LogicalSide of the caller
     * @param messages add your debug messages here
     */
    @Override
    public void getDebugMessages(LogicalSide side, IDebugMessages messages) {
        messages.addUnlocalized("Current tick %d", this._recipeHolder.getHeldRecipe().map(IHeldRecipe::getCurrentTick).orElse(-1));
    }

    //endregion
    //region internals

    protected FluidizerRecipeHolder(final IFluidizerRecipe.Type recipeType, final RecipeHolder<Recipe> holder) {

        this._recipeHolder = holder;
        this._recipeType = recipeType;
    }

    private final RecipeHolder<Recipe> _recipeHolder;
    private final IFluidizerRecipe.Type _recipeType;

    //endregion
}
