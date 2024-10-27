/*
 *
 * ReprocessorIOModelBuilder.java
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

import it.zerono.mods.extremereactors.gamecontent.Content;

public class ReprocessorIOModelBuilder
        extends ReprocessorModelBuilder {

    public ReprocessorIOModelBuilder() {
        super("assembledplatingio");
    }

    @Override
    public void build() {

        this.addIoPort(Content.Blocks.REPROCESSOR_COLLECTOR.get());
        this.addIoPort(Content.Blocks.REPROCESSOR_WASTEINJECTOR.get(), "wasteinjector_connected");

        this.setFallbackModelData(Content.Blocks.REPROCESSOR_COLLECTOR.get());
    }
}
