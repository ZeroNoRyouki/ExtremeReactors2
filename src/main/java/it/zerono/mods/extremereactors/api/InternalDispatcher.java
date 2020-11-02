/*
 *
 * InternalDispatcher.java
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

package it.zerono.mods.extremereactors.api;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import it.zerono.mods.zerocore.lib.CodeHelper;
import net.minecraftforge.fml.InterModComms;

public final class InternalDispatcher {

    public static void dispatch(final String method, final Runnable action) {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(method));
        Preconditions.checkNotNull(action);

        if ("bigreactors".equals(CodeHelper.getModIdFromActiveModContainer())) {
            action.run();
        } else {
            InterModComms.sendTo("bigreactors", method, () -> action);
        }
    }

    private InternalDispatcher() {
    }
}
