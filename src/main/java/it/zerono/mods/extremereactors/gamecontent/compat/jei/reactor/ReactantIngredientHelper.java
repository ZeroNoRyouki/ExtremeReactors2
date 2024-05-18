///*
// *
// * ReactantIngredientHelper.java
// *
// * This file is part of Extreme Reactors 2 by ZeroNoRyouki, a Minecraft mod.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
// * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// * DEALINGS IN THE SOFTWARE.
// *
// * DO NOT REMOVE OR EDIT THIS HEADER
// *
// */
//
//package it.zerono.mods.extremereactors.gamecontent.compat.jei.reactor;
//
//import it.unimi.dsi.fastutil.objects.ObjectLists;
//import it.zerono.mods.extremereactors.ExtremeReactors;
//import it.zerono.mods.extremereactors.api.reactor.Reactant;
//import it.zerono.mods.extremereactors.gamecontent.compat.jei.ExtremeReactorsJeiPlugin;
//import mezz.jei.api.ingredients.IIngredientType;
//import net.minecraft.resources.ResourceLocation;
//
//public class ReactantIngredientHelper
//    extends AbstractIngredientHelper<Reactant> {
//
//    //region IIngredientHelper<Reactant>
//
//    @Override
//    public IIngredientType<Reactant> getIngredientType() {
//        return ExtremeReactorsJeiPlugin.REACTANT_INGREDIENT_TYPE;
//    }
//
//    @Override
//    public Iterable<Integer> getColors(final Reactant reactant) {
//        return ObjectLists.singleton(reactant.getColour().toRGBA());
//    }
//
//    @Override
//    public Reactant copyIngredient(final Reactant reactant) {
//        return reactant.copy();
//    }
//
//    @Override
//    public ResourceLocation getResourceLocation(final Reactant reactant) {
//        return ExtremeReactors.ROOT_LOCATION.buildWithSuffix("reactant_" + reactant.getName());
//    }
//
//    //endregion
//}
