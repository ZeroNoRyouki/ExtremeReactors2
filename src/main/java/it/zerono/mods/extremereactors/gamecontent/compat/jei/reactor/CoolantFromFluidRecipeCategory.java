/*
 *
 * CoolantFromFluidRecipeCategory.java
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
import it.zerono.mods.extremereactors.api.coolant.Coolant;
import it.zerono.mods.extremereactors.api.coolant.FluidMappingsRegistry;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.compat.jei.ExtremeReactorsJeiPlugin;
import it.zerono.mods.zerocore.lib.compat.jei.AbstractModRecipeCategory;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CoolantFromFluidRecipeCategory
        extends AbstractModRecipeCategory<Coolant> {

    public CoolantFromFluidRecipeCategory(final IGuiHelper guiHelper) {

        super(RecipeType.create(ExtremeReactors.MOD_ID, "jei_coolantsmappings", Coolant.class),
                Component.translatable("compat.bigreactors.jei.coolantmappings.recipecategory.title"),
                new ItemStack(Content.Blocks.REACTOR_FLUID_ACCESSPORT_REINFORCED.get()), guiHelper,
                ExtremeReactorsJeiPlugin.defaultMappingDrawable(guiHelper));

        this._mappings = FluidMappingsRegistry.getCoolantToFluidMap();
    }

    public List<Coolant> getCoolants() {
        return new ArrayList<>(this._mappings.keySet());
    }

    //region AbstractModRecipeCategory

    @Override
    public void setRecipe(final IRecipeLayoutBuilder builder, final Coolant coolant, final IFocusGroup focuses) {

        final List<FluidStack> inputs = new ObjectArrayList<>(16);

        for (final IMapping<Coolant, TagKey<Fluid>> mapping : this._mappings.get(coolant)) {
            TagsHelper.FLUIDS.getObjects(mapping.getProduct())
                    .forEach(item -> inputs.add(new FluidStack(item, 1000)));
        }

        builder.addSlot(RecipeIngredientRole.INPUT, 33, 20)
                .addIngredients(NeoForgeTypes.FLUID_STACK, inputs);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 20)
                .addIngredients(ExtremeReactorsJeiPlugin.COOLANT_INGREDIENT_TYPE, ObjectLists.singleton(coolant));
    }

    //endregion
    //region internals

    private final Map<Coolant, List<IMapping<Coolant, TagKey<Fluid>>>> _mappings;

    //endregion
}
