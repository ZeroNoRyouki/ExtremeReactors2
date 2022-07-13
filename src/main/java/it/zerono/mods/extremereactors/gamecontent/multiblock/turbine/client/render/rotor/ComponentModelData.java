/*
 *
 * ComponentModelData.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.render.rotor;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.TurbinePartType;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorBladeState;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorShaftState;
import it.zerono.mods.zerocore.lib.client.model.data.GenericProperties;
import net.minecraftforge.client.model.data.ModelData;

public final class ComponentModelData {

    public static ModelData from(final RotorBladeState state) {
        return from(s_bladeCache, state.ordinal(), TurbinePartType.RotorBlade.ordinal(), state.ordinal());
    }

    public static ModelData from(final RotorShaftState state) {
        return from(s_shaftCache, state.ordinal(), TurbinePartType.RotorShaft.ordinal(), state.ordinal());
    }

    //region internals

    private static ModelData from(final Int2ObjectMap<ModelData> map, final int key,
                                  final int blockId, final int variantIndex) {
        return map.computeIfAbsent(key, k -> ModelData.builder()
                .with(GenericProperties.ID, blockId)
                .with(GenericProperties.VARIANT_INDEX, variantIndex)
                .build());
    }

    private static final Int2ObjectMap<ModelData> s_bladeCache = new Int2ObjectArrayMap<>(RotorBladeState.values().length);
    private static final Int2ObjectMap<ModelData> s_shaftCache = new Int2ObjectArrayMap<>(RotorShaftState.values().length);

    //endregion
}
