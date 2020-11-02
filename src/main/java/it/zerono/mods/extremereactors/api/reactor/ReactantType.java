/*
 *
 * ReactantType.java
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

/**
 * The type of a Reactant. Fuel-Reactants are converted to Waste-Reactants inside a Reactor Fuel Rods
 */
public enum ReactantType {

    Fuel(0xbcba50),
    Waste(0x4d92b5);

    public boolean isFuel() {
        return Fuel == this;
    }

    public boolean isWaste() {
        return Waste == this;
    }

    public int getDefaultColour() {
        return this._rgbDefaultColor;
    }

    //region internals

    ReactantType(int defaultColour) {
        this._rgbDefaultColor = defaultColour;
    }

    private final int _rgbDefaultColor;

    //endregion
}
