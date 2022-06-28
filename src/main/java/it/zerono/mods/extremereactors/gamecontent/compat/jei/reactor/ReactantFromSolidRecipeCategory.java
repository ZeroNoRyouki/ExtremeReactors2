/*
 *
 * ReactantRecipeCategory.java
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
import it.zerono.mods.extremereactors.api.reactor.Reactant;
import it.zerono.mods.extremereactors.api.reactor.ReactantMappingsRegistry;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.compat.jei.ExtremeReactorsJeiPlugin;
import it.zerono.mods.zerocore.lib.compat.jei.AbstractModRecipeCategory;
import it.zerono.mods.zerocore.lib.tag.TagsHelper;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ingredient.IGuiIngredientGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReactantFromSolidRecipeCategory
        extends AbstractModRecipeCategory<Reactant> {

    public static ResourceLocation ID = ExtremeReactors.newID("jei_reactantsmappings_solid");

    public ReactantFromSolidRecipeCategory(final IGuiHelper guiHelper) {

        super(ID, new TranslatableComponent("compat.bigreactors.jei.reactantsmappings.solid.recipecategory.title"),
                Content.Blocks.REACTOR_FUELROD_BASIC.get().createItemStack(), guiHelper,
                guiHelper.drawableBuilder(ExtremeReactors.newID("textures/gui/jei/mapping.png"), 0, 0, 144, 56)
                        .setTextureSize(144, 56)
                        .build());

        this._mappings = ReactantMappingsRegistry.getToSolidMap();
    }

    public List<Reactant> getReactants() {
        return new ArrayList<>(this._mappings.keySet());
    }

    //region AbstractModRecipeCategory

    @Override
    public Class<? extends Reactant> getRecipeClass() {
        return Reactant.class;
    }

    @Override
    public void setIngredients(final Reactant reactant, final IIngredients ingredients) {

        final List<ItemStack> inputs = new ObjectArrayList<>(16);

        for (final IMapping<Reactant, ResourceLocation> mapping : this._mappings.get(reactant)) {
            TagsHelper.ITEMS.getTag(mapping.getProduct())
                    .ifPresent(tag -> TagsHelper.ITEMS.getMatchingElements(tag)
                            .forEach(item -> inputs.add(new ItemStack(item))));
        }

        ingredients.setInputLists(VanillaTypes.ITEM, ObjectLists.singleton(inputs));
        ingredients.setOutput(ExtremeReactorsJeiPlugin.REACTANT_INGREDIENT_TYPE, reactant);
    }

    @Override
    public void setRecipe(final IRecipeLayout layout, final Reactant reactant, final IIngredients ingredients) {

        final IGuiIngredientGroup<Reactant> output = layout.getIngredientsGroup(ExtremeReactorsJeiPlugin.REACTANT_INGREDIENT_TYPE);
        final IGuiItemStackGroup inputs = layout.getItemStacks();

        // solid inputs
        inputs.init(0, true, 33 - 1, 20 - 1);
        inputs.set(ingredients);

        // output
        output.init(1, false, 95, 20);
        output.set(1, reactant);
    }

    //endregion
    //region internals

    private final Map<Reactant, List<IMapping<Reactant, ResourceLocation>>> _mappings;

    //endregion
}
