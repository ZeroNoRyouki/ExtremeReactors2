/*
 *
 * ReactantStackData.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.container.data;

import com.google.common.base.Preconditions;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactantStack;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.AbstractData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.IBindableData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.IContainerData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.Sampler;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.common.util.NonNullSupplier;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ReactantStackData
        extends AbstractData<ReactantStack>
        implements IContainerData {

    public static ReactantStackData immutable(ModContainer container, boolean isClientSide, ReactantStack value) {
        return of(container, isClientSide, () -> () -> value);
    }

    public static ReactantStackData empty(boolean isClientSide) {
        return isClientSide ? new ReactantStackData() : new ReactantStackData(() -> () -> ReactantStack.EMPTY);
    }

    public static ReactantStackData sampled(int frequency, ModContainer container, boolean isClientSide,
                                            NonNullSupplier<Supplier<ReactantStack>> serverSideGetter) {
        return of(container, isClientSide, () -> new Sampler<>(frequency, serverSideGetter));
    }

    public static ReactantStackData of(ModContainer container, boolean isClientSide,
                                       NonNullSupplier<Supplier<ReactantStack>> serverSideGetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");
        Preconditions.checkNotNull(serverSideGetter, "Server side getter must not be null.");

        final ReactantStackData data = isClientSide ? new ReactantStackData() : new ReactantStackData(serverSideGetter);

        container.addBindableData(data);
        return data;
    }

    public IBindableData<Integer> amount() {

        if (null == this._amountData) {
            this._amountData = AbstractData.of(0);
        }

        return this._amountData;
    }

    //region IContainerData

    @Nullable
    @Override
    public NonNullConsumer<PacketBuffer> getContainerDataWriter() {

        final ReactantStack current = new ReactantStack(this._getter.get());

        if (this._lastValue.isEmpty() && current.isEmpty()) {
            return null;
        }

        final boolean equalReactant = this._lastValue.isReactantEqual(current);

        if (!equalReactant || current.getAmount() != this._lastValue.getAmount()) {

            this._lastValue = current;

            if (equalReactant) {
                return buffer -> {

                    buffer.writeByte(1);
                    buffer.writeInt(current.getAmount());
                };
            } else {
                return buffer -> {

                    buffer.writeByte(0);
                    current.writeTo(buffer);
                };
            }
        }

        return null;
    }

    @Override
    public void readContainerData(final PacketBuffer dataSource) {

        switch (dataSource.readByte()) {

            case 0: {
                // full stack

                final ReactantStack data = ReactantStack.createFrom(dataSource);

                this.notify(data);

                if (null != this._amountData) {
                    this._amountData.notify(data.getAmount());
                }

                break;
            }

            case 1: {
                // count only

                if (null != this._amountData) {
                    this._amountData.notify(dataSource.readInt());
                }

                break;
            }
        }
    }

    //endregion
    //region IBindableData<ReactantStack>

    @Nullable
    @Override
    public ReactantStack defaultValue() {
        return ReactantStack.EMPTY;
    }

    //endregion
    //region internals

    private ReactantStackData() {
    }

    private ReactantStackData(NonNullSupplier<Supplier<ReactantStack>> serverSideGetter) {

        super(serverSideGetter);
        this._lastValue = ReactantStack.EMPTY;
    }

    private ReactantStack _lastValue;
    private AbstractData<Integer> _amountData;

    //endregion
}
