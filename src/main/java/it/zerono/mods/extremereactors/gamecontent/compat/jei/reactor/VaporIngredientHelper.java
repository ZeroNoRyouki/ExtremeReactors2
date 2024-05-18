///*
// *
// * VaporIngredientHelper.java
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
//import it.zerono.mods.extremereactors.api.coolant.Vapor;
//import it.zerono.mods.extremereactors.gamecontent.compat.jei.ExtremeReactorsJeiPlugin;
//import mezz.jei.api.ingredients.IIngredientType;
//import net.minecraft.resources.ResourceLocation;
//
//public class VaporIngredientHelper
//        extends AbstractIngredientHelper<Vapor> {
//
//    //region AbstractIngredientHelper<Vapor>
//
//    @Override
//    public IIngredientType<Vapor> getIngredientType() {
//        return ExtremeReactorsJeiPlugin.VAPOR_INGREDIENT_TYPE;
//    }
//
//    @Override
//    public Iterable<Integer> getColors(final Vapor vapor) {
//        return ObjectLists.singleton(vapor.getColour().toRGB());
//    }
//
//    @Override
//    public Vapor copyIngredient(final Vapor vapor) {
//        return vapor.copy();
//    }
//
//    @Override
//    public ResourceLocation getResourceLocation(final Vapor vapor) {
//        return ExtremeReactors.ROOT_LOCATION.buildWithSuffix("vapor_" + vapor.getName());
//    }
//
//    //endregion
//}
