/*
 *
 * SensorBehavior.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.sensor;

import com.google.common.base.Strings;
import net.minecraft.nbt.CompoundNBT;

public enum SensorBehavior {

    Disabled,
    SetFromSignal,
    SetOnPulse,
    ToggleOnPulse,
    InsertOnPulse,
    RetractOnPulse,
    EjectOnPulse,
    ActiveWhileAbove,
    ActiveWhileBelow
    ;

    public boolean onPulse() {

        switch (this) {

            case SetOnPulse:
            case ToggleOnPulse:
            case InsertOnPulse:
            case RetractOnPulse:
            case EjectOnPulse:
                return true;

            default:
                return false;
        }
    }

    public boolean outputTest(final int current, final int threshold) {

        switch (this) {

            case ActiveWhileAbove:
                return current > threshold;

            case ActiveWhileBelow:
                return current < threshold;

            default:
                return false;
        }
    }

    public static SensorBehavior read(final CompoundNBT data, final String key, final SensorBehavior defaultValue) {

        if (data.contains(key)) {

            final String value = data.getString(key);

            if (!Strings.isNullOrEmpty(value)) {
                return SensorBehavior.valueOf(value);
            }
        }

        return defaultValue;
    }

    public static void write(final CompoundNBT data, final String key, final SensorBehavior value) {
        data.putString(key, value.name());
    }
}
