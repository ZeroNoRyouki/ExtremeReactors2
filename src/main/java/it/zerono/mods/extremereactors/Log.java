/*
 *
 * Log.java
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

package it.zerono.mods.extremereactors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public final class Log {

    public static final Logger LOGGER = LogManager.getLogger(ExtremeReactors.MOD_ID);

    public static final Marker CORE = MarkerManager.getMarker("core");
    public static final Marker REACTOR = MarkerManager.getMarker("reactor");
    public static final Marker TURBINE = MarkerManager.getMarker("turbine");
    public static final Marker REPROCESSOR = MarkerManager.getMarker("reprocessor");
    public static final Marker FLUIDIZER = MarkerManager.getMarker("fluidizer");

    private Log() {
    }
}
