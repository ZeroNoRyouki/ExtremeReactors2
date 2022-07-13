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
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerationHandler {

    public static final ResourceLocation TRANSPARENT_TEXTURE = ExtremeReactors.newID("block/transparent");

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {

        final DataGenerator generator = event.getGenerator();
        final ExistingFileHelper existing = event.getExistingFileHelper();

        // server stuff

        generator.addProvider(event.includeServer(), new BlockLootGenerator(generator));

        final BlockTagGenerator blockTagGenerator = new BlockTagGenerator(generator, existing);

        generator.addProvider(event.includeServer(), blockTagGenerator);
        generator.addProvider(event.includeServer(), new ItemTagGenerator(generator, blockTagGenerator, existing));

        generator.addProvider(event.includeServer(), new FluidTagGenerator(generator, existing));

        generator.addProvider(event.includeServer(), new GenericRecipeGenerator(generator));
        generator.addProvider(event.includeServer(), new ReactorRecipeGenerator(generator));
        generator.addProvider(event.includeServer(), new TurbineRecipeGenerator(generator));
        generator.addProvider(event.includeServer(), new ReprocessorRecipeGenerator(generator));
        generator.addProvider(event.includeServer(), new FluidizerRecipeGenerator(generator));

        // client stuff

        generator.addProvider(event.includeClient(), new BlockStateGenerator(generator, existing));
        generator.addProvider(event.includeClient(), new ReactorBlockStateGenerator(generator, existing));
        generator.addProvider(event.includeClient(), new TurbineBlockStateGenerator(generator, existing));
        generator.addProvider(event.includeClient(), new ReprocessorBlockStateGenerator(generator, existing));
        generator.addProvider(event.includeClient(), new FluidizerBlockStateGenerator(generator, existing));

        generator.addProvider(event.includeClient(), new ItemModelGenerator(generator, existing));
    }
}
