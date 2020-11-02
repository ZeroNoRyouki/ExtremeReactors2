/*
 *
 * WasteEjectionSetting.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor;

import com.google.common.base.Strings;
import net.minecraft.nbt.CompoundNBT;

public enum WasteEjectionSetting {

    // Full auto, always remove waste
    Automatic,

    // Manual, only on button press
    Manual
    ;

    public boolean isAutomatic() {
        return Automatic == this;
    }

    public boolean isManual() {
        return Manual == this;
    }

    public static WasteEjectionSetting read(final CompoundNBT data, final String key, final WasteEjectionSetting defaultValue) {

        if (data.contains(key)) {

            final String value = data.getString(key);

            if (!Strings.isNullOrEmpty(value)) {
                return WasteEjectionSetting.valueOf(value);
            }
        }

        return defaultValue;
    }

    public static CompoundNBT write(final CompoundNBT data, final String key, final WasteEjectionSetting value) {

        data.putString(key, value.name());
        return data;
    }
}
