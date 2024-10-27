/*
 * EnergizerModelBuilder
 *
 * This file is part of Zero CORE 2 by ZeroNoRyouki, a Minecraft mod.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * Do not remove or edit this header
 *
 */

package it.zerono.mods.extremereactors.gamecontent.multiblock.energizer.client.model;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.zerocore.base.multiblock.client.model.AbstractCuboidMultiblockModelBuilder;

public class EnergizerModelBuilder
        extends AbstractCuboidMultiblockModelBuilder {

    public EnergizerModelBuilder() {
        super("energizer", "assembledplating", true, ExtremeReactors.ROOT_LOCATION);
    }

    @Override
    public void build() {

        this.addCasing(Content.Blocks.ENERGIZER_CASING.get());
        this.addController(Content.Blocks.ENERGIZER_CONTROLLER.get());
        this.addIoPort(Content.Blocks.ENERGIZER_CHARGINGPORT_FE.get(), "charging_assembled");
        this.addIoPort(Content.Blocks.ENERGIZER_POWERPORT_FE.get(),
                "powerportfe_assembled_input", "powerportfe_assembled_input_connected",
                "powerportfe_assembled_output", "powerportfe_assembled_output_connected");
        this.addIoPort(Content.Blocks.ENERGIZER_COMPUTERPORT.get(), "computerport_assembled_disconnected",
                "computerport_assembled_connected");

        this.setFallbackModelData(Content.Blocks.ENERGIZER_CASING.get());
    }
}
