/*
 *
 * DataGenerationHandler.java
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

package it.zerono.mods.extremereactors.datagen;

import it.zerono.mods.extremereactors.ExtremeReactors;
import it.zerono.mods.extremereactors.datagen.recipes.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerationHandler {

    public static final ResourceLocation TRANSPARENT_TEXTURE = ExtremeReactors.newID("block/transparent");

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {

        final DataGenerator generator = event.getGenerator();
        final ExistingFileHelper existing = event.getExistingFileHelper();

        if (event.includeServer()) {

            generator.addProvider(new BlockLootGenerator(generator));

            final BlockTagGenerator blockTagGenerator = new BlockTagGenerator(generator, existing);

            generator.addProvider(blockTagGenerator);
            generator.addProvider(new ItemTagGenerator(generator, blockTagGenerator, existing));

            generator.addProvider(new FluidTagGenerator(generator, existing));

            generator.addProvider(new GenericRecipeGenerator(generator));
            generator.addProvider(new ReactorRecipeGenerator(generator));
            generator.addProvider(new TurbineRecipeGenerator(generator));
            generator.addProvider(new ReprocessorRecipeGenerator(generator));
            generator.addProvider(new FluidizerRecipeGenerator(generator));
        }

        if (event.includeClient()) {

            generator.addProvider(new BlockStateGenerator(generator, existing));
            generator.addProvider(new ReactorBlockStateGenerator(generator, existing));
            generator.addProvider(new TurbineBlockStateGenerator(generator, existing));
            generator.addProvider(new ReprocessorBlockStateGenerator(generator, existing));
            generator.addProvider(new FluidizerBlockStateGenerator(generator, existing));

            generator.addProvider(new ItemModelGenerator(generator, existing));
        }
    }
}
