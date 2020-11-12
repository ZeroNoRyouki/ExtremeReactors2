/*
 *
 * Client.java
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

package it.zerono.mods.extremereactors.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Calendar;

public class Client {

//    public final ForgeConfigSpec.BooleanValue disableTurbineRotorRender;
    public final ForgeConfigSpec.BooleanValue disableReactorParticles;
    public final ForgeConfigSpec.BooleanValue disableTurbineParticles;
    public final ForgeConfigSpec.BooleanValue disableApiTooltips;
    public final boolean isValentinesDay;

    Client(final ForgeConfigSpec.Builder builder) {

        builder.comment("Client only settings").push("client");

//        disableTurbineRotorRender = builder
//                .comment("If true, disables the rendering of the rotor animation of an active Turbine. Restart needed.")
//                .translation("config.bigreactors.client.disableturbinerotorrender")
//                .worldRestart()
//                .define("disableTurbineRotorRender", false);

        disableReactorParticles = builder
                .comment("If true, disables all particle effects in the Reactor.")
                .translation("config.bigreactors.client.disablereactorparticles")
                .define("disableReactorParticles", false);

        disableTurbineParticles = builder
                .comment("If true, disables all particle effects in the Turbine.")
                .translation("config.bigreactors.client.disableturbineparticles")
                .define("disableTurbineParticles", false);

        disableApiTooltips = builder
                .comment("If true, no (advanced) tooltips will be added to blocks and items that can be used inside the Reactor or Turbine or as a fuel source.")
                .translation("config.bigreactors.client.disableapitooltips")
                .define("disableTurbineParticles", false);

        builder.pop();

        final Calendar calendar = Calendar.getInstance();

        isValentinesDay = (calendar.get(Calendar.MONTH) == 1 && calendar.get(Calendar.DAY_OF_MONTH) == 14);
    }
}
