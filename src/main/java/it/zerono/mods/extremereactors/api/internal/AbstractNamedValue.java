/*
 *
 * AbstractNamedValue.java
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

package it.zerono.mods.extremereactors.api.internal;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractNamedValue {

    protected AbstractNamedValue(final String name, final String translationKey) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(translationKey));

        for (int i = 0; i < name.length(); ++i) {
            if (!ResourceLocation.isAllowedInResourceLocation(name.charAt(i))) {
                throw new IllegalArgumentException("Names should be valid for use in a ResourceLocation");
            }
        }

        this._name = name;
        this._translationKey = translationKey;
    }

    public String getName() {
        return this._name;
    }

    public String getTranslationKey() {
        return this._translationKey;
    }

    public Component getTranslatedName() {
        return Component.translatable(this.getTranslationKey());
    }

    //region Object

    @Override
    public boolean equals(final Object obj) {

        if (obj instanceof AbstractNamedValue) {

            final AbstractNamedValue other = (AbstractNamedValue)obj;

            return (this == other) || (this.getName().equals(other.getName()));
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }

    @Override
    public String toString() {
        return this.getName();
    }

    //endregion
    //region internals

    private final String _name;
    private final String _translationKey;

    //endregion
}
