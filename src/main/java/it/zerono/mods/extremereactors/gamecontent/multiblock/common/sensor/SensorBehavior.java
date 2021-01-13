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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common.sensor;

public enum SensorBehavior {

    Disabled,
    SetFromSignal,
    SetFromSignalLevel,
    SetOnPulse,
    ToggleOnPulse,
    InsertOnPulse,
    RetractOnPulse,
    EjectOnPulse,
    ActiveWhileAbove,
    ActiveWhileBelow,
    ActiveWhileBetween
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

    public boolean outputTest(final int current, final int v1, final int v2) {

        switch (this) {

            case ActiveWhileAbove:
                return current > v1;

            case ActiveWhileBelow:
                return current < v1;

            case ActiveWhileBetween:
                return current >= v1 && current <= v2;

            default:
                return false;
        }
    }
}
