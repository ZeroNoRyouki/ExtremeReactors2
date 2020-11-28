/*
 *
 * CoilMaterial.java
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

public final class CoilMaterial {

    public String BlockTagId;
    public float Efficiency;
    public float Bonus;
    public float ExtractionRate;

    public CoilMaterial() {
    }

    CoilMaterial(final String blockTagId, final float efficiency, final float bonus, final float extractionRate) {

        this.BlockTagId = blockTagId;
        this.Efficiency = efficiency;
        this.Bonus = bonus;
        this.ExtractionRate = extractionRate;
    }
}
