/*
 *
 * IHeatEntity.java
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

package it.zerono.mods.extremereactors.api.reactor;

public interface IHeatEntity {

    /**
     * Normal ambient temperature
     */
    float AMBIENT_HEAT = 20.0f;

    // FE to transfer per tick per degree centigrade of difference on a single exposed face (1x1)

    float CONDUCTIVITY_AIR = 0.05f;
    float CONDUCTIVITY_RUBBER = 0.01f;
    float CONDUCTIVITY_WATER = 0.1f;
    float CONDUCTIVITY_STONE = 0.15f;
    float CONDUCTIVITY_GLASS = 0.3f;
    float CONDUCTIVITY_IRON = 0.6f; // Stainless steel, really.
    float CONDUCTIVITY_COPPER = 1f;
    float CONDUCTIVITY_SILVER = 1.5f;
    float CONDUCTIVITY_GOLD = 2f;
    float CONDUCTIVITY_EMERALD = 2.5f;
    float CONDUCTIVITY_DIAMOND = 3f;
    float CONDUCTIVITY_GRAPHENE = 5f;

    /**
     * @return The amount of heat in the entity, in Celsius.
     */
    double getHeat();

    /**
     * The thermal conductivity of the entity.
     * This is the amount of heat (in C) that this entity transfers
     * over a unit area (1x1 square) in one tick, per degree-C difference.
     * (Yes, I know centigrade != joules, it's an abstraction)
     *
     * @return Thermal conductivity constant, see above.
     */
    double getThermalConductivity();
}
