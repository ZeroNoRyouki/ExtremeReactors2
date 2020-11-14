/*
 *
 * VentStatus.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine;

import java.util.function.Predicate;

public enum VentSetting
    implements Predicate<VentSetting> {

    VentOverflow,
    VentAll,
    DoNotVent
    ;

    public static VentSetting getDefault() {
        return VentOverflow;
    }

    public boolean isVentOverflow() {
        return VentOverflow == this;
    }

    public boolean isVentAll() {
        return VentAll == this;
    }

    public boolean isDoNotVent() {
        return DoNotVent == this;
    }

    //region Predicate<VentSetting>

    @Override
    public boolean test(final VentSetting ventSetting) {
        return this == ventSetting;
    }

    //endregion
}
