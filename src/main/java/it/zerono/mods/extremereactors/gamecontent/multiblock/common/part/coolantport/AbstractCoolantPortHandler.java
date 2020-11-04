/*
 *
 * AbstractCoolantPortHandler.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.coolantport;

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.AbstractGeneratorMultiblockController;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.AbstractIOPortHandler;
import it.zerono.mods.extremereactors.gamecontent.multiblock.common.variant.IMultiblockGeneratorVariant;
import it.zerono.mods.zerocore.lib.data.IIoEntity;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockPart;

public abstract class AbstractCoolantPortHandler<Controller extends AbstractGeneratorMultiblockController<Controller, V>,
            V extends IMultiblockGeneratorVariant, P extends AbstractCuboidMultiblockPart<Controller> & IIoEntity>
        extends AbstractIOPortHandler<Controller, V>
        implements ICoolantPortHandler<Controller, V> {

    protected AbstractCoolantPortHandler(final CoolantPortType type, final P part, final IoMode mode) {

        super(part, mode);
        this._type = type;
    }

    public P getIoEntity() {
        //noinspection unchecked
        return (P)this.getPart();
    }

    //region ICoolantPortHandler

    /**
     * Get the {@link CoolantPortType} supported by this ICoolantPortHandler
     *
     * @return the supported {@link CoolantPortType}
     */
    @Override
    public CoolantPortType getCoolantPortType() {
        return this._type;
    }

    //endregion
    //region internals

    private final CoolantPortType _type;


    //endregion
}
