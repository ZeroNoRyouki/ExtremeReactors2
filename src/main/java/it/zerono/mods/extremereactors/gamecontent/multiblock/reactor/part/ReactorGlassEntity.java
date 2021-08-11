/*
 *
 * ReactorGlassEntity.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part;

import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.client.model.data.ModelTransformers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ReactorGlassEntity
        extends AbstractReactorEntity {

    public ReactorGlassEntity(final BlockPos position, final BlockState blockState) {
        super(Content.TileEntityTypes.REACTOR_GLASS.get(), position, blockState);
    }

    //region client render support

    @Override
    protected int getUpdatedModelVariantIndex() {
        return ModelTransformers.getGlassVariantIndexFrom(this);
    }

    //endregion
}