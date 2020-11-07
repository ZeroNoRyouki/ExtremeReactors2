/*
 *
 * FluidType.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common;

import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.stack.AllowedHandlerAction;

public enum FluidType {

    Gas,
    Liquid;

    public boolean isGas() {
        return this == Gas;
    }

    public boolean isLiquid() {
        return this == Liquid;
    }

    public AllowedHandlerAction getAllowedAction() {

        switch (this) {

            default:
            case Gas:
                return AllowedHandlerAction.ExtractOnly;

            case Liquid:
                return AllowedHandlerAction.InsertOnly;
        }
    }

    public static FluidType from(IoDirection direction) {

        switch (direction) {

            default:
            case Input:
                return Liquid;

            case Output:
                return Gas;
        }
    }
}
