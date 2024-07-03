/*
 *
 * AddRemoveSection.java
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

package it.zerono.mods.extremereactors.api.internal.modpack.wrapper;

import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.stream.Stream;

public final class AddRemoveSection<AddT> {

    public boolean WipeExistingValuesBeforeAdding;
    public AddT[] Add;
    public String[] Remove;

    public AddRemoveSection() {
    }

    AddRemoveSection(final IntFunction<AddT[]> addFactory) {

        this.WipeExistingValuesBeforeAdding = false;
        this.Add = addFactory.apply(0);
        this.Remove = new String[0];
    }

    public Stream<AddT> additions() {
        return null != this.Add ? Arrays.stream(this.Add) : Stream.empty();
    }

    public Stream<String> removals() {
        return null != this.Remove ? Arrays.stream(this.Remove) : Stream.empty();
    }
}
