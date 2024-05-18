///*
// *
// * VaporIngredientRenderer.java
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
//import it.zerono.mods.extremereactors.api.coolant.Vapor;
//import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.screen.CachedSprites;
//import it.zerono.mods.zerocore.lib.client.gui.Orientation;
//import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
//import mezz.jei.api.ingredients.IIngredientRenderer;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.network.chat.Component;
//import net.minecraft.world.item.TooltipFlag;
//
//import java.util.List;
//
//public class VaporIngredientRenderer
//        implements IIngredientRenderer<Vapor> {
//
//    //region IIngredientRenderer<Vapor>
//
//    @Override
//    public void render(final GuiGraphics gfx, final Vapor vapor) {
//        ModRenderHelper.paintOrientedProgressBarSprite(gfx, Orientation.BottomToTop, CachedSprites.WATER_SOURCE.get(),
//                0, 0, 0, 16, 16, 1.0d, vapor.getColour());
//    }
//
//    @Override
//    public List<Component> getTooltip(final Vapor vapor, final TooltipFlag tooltipFlag) {
//        return ObjectLists.singleton(vapor.getTranslatedName());
//    }
//
//    //endregion
//}
