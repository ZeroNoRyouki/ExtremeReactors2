/*
 * EnergizerModelsDataProvider
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

package it.zerono.mods.extremereactors.datagen.client;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.zerocore.lib.data.ResourceLocationBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class EnergizerModelsDataProvider
        extends AbstractMultiblockModelsDataProvider {

    public EnergizerModelsDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                       ResourceLocationBuilder modLocationRoot) {
        super(ExtremeReactors.MOD_NAME + " Energizer block states and models", output, lookupProvider, modLocationRoot);
    }

    //region BlockStateDataProvider

    @Override
    public void provideData() {

        final String folder = "energizer";

        this.assembledPlatingModel(folder);
        this.assembledPlatingModel("assembledplatingio", "plating", folder);
        this.multiblockFrame(Content.Blocks.ENERGIZER_CASING, "casing", folder);
        this.controller(Content.Blocks.ENERGIZER_CONTROLLER, folder);
        this.powerPort(Content.Blocks.ENERGIZER_POWERPORT_FE, "powerportfe", folder);
        this.genericPart(Content.Blocks.ENERGIZER_CHARGINGPORT_FE, "charging", folder, "_assembled");

        this.simpleItem(Content.Items.ENERGY_CORE);
        this.simpleBlock(Content.Blocks.ENERGIZER_CELL);
    }

    //endregion
}
