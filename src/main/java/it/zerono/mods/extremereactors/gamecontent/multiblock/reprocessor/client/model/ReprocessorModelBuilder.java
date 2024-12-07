/*
 *
 * ReprocessorModelBuilder.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reprocessor.client.model;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.zerocore.base.multiblock.client.model.AbstractCuboidMultiblockModelBuilder;

public class ReprocessorModelBuilder
    extends AbstractCuboidMultiblockModelBuilder {

    public ReprocessorModelBuilder() {
        this("assembledplating");
    }

    protected ReprocessorModelBuilder(final String templateModelName) {
        super("reprocessor", templateModelName, true, ExtremeReactors.ROOT_LOCATION);
    }

    @Override
    public void build() {

        this.addCasing(Content.Blocks.REPROCESSOR_CASING.get());
        this.addController(Content.Blocks.REPROCESSOR_CONTROLLER.get());

        this.addIoPort(Content.Blocks.REPROCESSOR_POWERPORT.get());
        this.addIoPort(Content.Blocks.REPROCESSOR_FLUIDINJECTOR.get(), "fluidinjector_connected");
        this.addIoPort(Content.Blocks.REPROCESSOR_OUTPUTPORT.get(), "outputport_connected");

        this.setFallbackModelData(Content.Blocks.REPROCESSOR_CASING.get());
    }
}
