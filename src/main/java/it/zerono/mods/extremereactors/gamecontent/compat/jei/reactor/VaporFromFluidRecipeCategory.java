/*
 *
 * VaporFromFluidRecipeCategory.java
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

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.api.IMapping;
import it.zerono.mods.extremereactors.api.coolant.FluidMappingsRegistry;
import it.zerono.mods.extremereactors.api.coolant.Vapor;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.compat.jei.ExtremeReactorsJeiPlugin;
import it.zerono.mods.zerocore.lib.compat.jei.AbstractModRecipeCategory;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VaporFromFluidRecipeCategory
        extends AbstractModRecipeCategory<Vapor> {

    public VaporFromFluidRecipeCategory(final IGuiHelper guiHelper) {

        super(ExtremeReactors.newID("jei_vaporsmappings"),
                new TranslatableComponent("compat.bigreactors.jei.vapormappings.recipecategory.title"),
                Content.Blocks.REACTOR_FLUID_ACCESSPORT_REINFORCED.get().createItemStack(), guiHelper,
                guiHelper.drawableBuilder(ExtremeReactors.newID("textures/gui/jei/mapping.png"), 0, 0, 144, 56)
                        .setTextureSize(144, 56)
                        .build());

        this._mappings = FluidMappingsRegistry.getVaporToFluidMap();
    }

    public List<Vapor> getVapors() {
        return new ArrayList<>(this._mappings.keySet());
    }

    //region AbstractModRecipeCategory

    @Override
    public Class<? extends Vapor> getRecipeClass() {
        return Vapor.class;
    }

    @Override
    public void setIngredients(final Vapor vapor, final IIngredients ingredients) {

        final List<FluidStack> inputs = new ObjectArrayList<>(16);

        for (final IMapping<Vapor, Tag.Named<Fluid>> mapping : this._mappings.get(vapor)) {
            TagsHelper.FLUIDS.getMatchingElements(mapping.getProduct())
                    .forEach(item -> inputs.add(new FluidStack(item, 1000)));
        }

        ingredients.setInputLists(VanillaTypes.FLUID, ObjectLists.singleton(inputs));
        ingredients.setOutput(ExtremeReactorsJeiPlugin.VAPOR_INGREDIENT_TYPE, vapor);
    }

    @Override
    public void setRecipe(final IRecipeLayout layout, final Vapor vapor, final IIngredients ingredients) {

        final IGuiIngredientGroup<Vapor> output = layout.getIngredientsGroup(ExtremeReactorsJeiPlugin.VAPOR_INGREDIENT_TYPE);
        final IGuiFluidStackGroup inputs = layout.getFluidStacks();

        // solid inputs
        inputs.init(0, true, 33, 20);
        inputs.set(ingredients);

        // output
        output.init(1, false, 95, 20);
        output.set(1, vapor);
    }

    //endregion
    //region internals

    private final Map<Vapor, List<IMapping<Vapor, Tag.Named<Fluid>>>> _mappings;

    //endregion
}
