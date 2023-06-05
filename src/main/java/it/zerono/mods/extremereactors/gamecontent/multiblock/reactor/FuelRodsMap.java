/*
 *
 * FuelRodsMap.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorFuelRodEntity;
import it.zerono.mods.zerocore.lib.data.UnmodifiableChildrenIterator;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FuelRodsMap
        implements Iterable<ReactorFuelRodEntity> {

    FuelRodsMap() {
        
        this._verticalSlices = new Int2ObjectArrayMap<>(32);
        this._elementCount = 0;
        this.onMapChanged();
    }
    
    public void add(ReactorFuelRodEntity rod) {

        this._verticalSlices.computeIfAbsent(rod.getWorldPosition().getY(), $ -> new LinkedList<>()).add(rod);
        ++this._elementCount;
        this.onMapChanged();
    }
    
    public void remove(ReactorFuelRodEntity rod) {

        final int y = rod.getWorldPosition().getY();
        final List<ReactorFuelRodEntity> list = this._verticalSlices.get(y);

        if (null != list) {

            if (list.remove(rod)) {

                if (list.isEmpty()) {
                    this._verticalSlices.remove(y);
                }

                --this._elementCount;
                this.onMapChanged();
            }
        }
    }
    
    public void clear() {

        this._verticalSlices.clear();
        this._elementCount = 0;
        this.onMapChanged();
    }

    public int size() {
        return this._elementCount;
    }
    
    public float getHeatTransferRate() {

        double rate = 0.0;

        for (final ReactorFuelRodEntity rod : this) {
            rate += rod.getHeatTransferRate();
        }

        return (float) rate;
    }

    public IIrradiationSource getNextIrradiationSource() {

        if (null == this._nextSource || !this._nextSource.hasNext()) {
            this._nextSource = this.iterator();
        }

        return this._nextSource.next();
    }

    public void markFuelRodsForRenderUpdate() {
        this.forEach(ReactorFuelRodEntity::markForRenderUpdate);
    }

    public void markFuelRodsForRenderUpdate(IntSet indices) {
        indices.forEach((int index) -> this.getSlice(index).forEach(ReactorFuelRodEntity::markForRenderUpdate));
    }

    public void reset() {
        this._nextSource = null;
    }

    //region Iterable<ReactorFuelRodEntity>

    @Override
    public Iterator<ReactorFuelRodEntity> iterator() {
        return new UnmodifiableChildrenIterator<>(this._verticalSlices.values().iterator(), List::iterator);
    }

    //endregion
    //region internals

    private List<ReactorFuelRodEntity> getSlice(int index) {

        if (Integer.MIN_VALUE == this._keyZero) {
            this._keyZero = this._verticalSlices.keySet().stream()
                    .mapToInt($ -> $)
                    .min()
                    .orElse(Integer.MIN_VALUE);
        }

        return this._verticalSlices.getOrDefault(this._keyZero + index, Collections.emptyList());
    }

    private void onMapChanged() {

        this._keyZero = Integer.MIN_VALUE;
        this._nextSource = null;
    }

    private final Int2ObjectMap<List<ReactorFuelRodEntity>> _verticalSlices;
    private int _elementCount;
    private int _keyZero;
    private Iterator<ReactorFuelRodEntity> _nextSource;

    //endregion
}
