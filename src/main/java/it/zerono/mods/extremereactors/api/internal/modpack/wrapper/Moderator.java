/*
 *
 * Moderator.java
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

public final class Moderator {

    public String TagId;
    public float Absorption;
    public float HeatEfficiency;
    public float Moderation;
    public float HeatConductivity;

    public Moderator() {
    }

    Moderator(final String tagId, final float absorption, final float heatEfficiency,
              final float moderation, final float heatConductivity) {

        this.TagId = tagId;
        this.Absorption = absorption;
        this.HeatEfficiency = heatEfficiency;
        this.Moderation = moderation;
        this.HeatConductivity = heatConductivity;
    }
}
