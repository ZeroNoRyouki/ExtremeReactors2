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
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.item.inventory.container.ModContainer;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.AbstractData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.IBindableData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.IContainerData;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.Sampler;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.sync.AmountChangedEntry;
import it.zerono.mods.zerocore.lib.item.inventory.container.data.sync.ISyncedSetEntry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ReactantStackData
        extends AbstractData<ReactantStack>
        implements IContainerData {

    public static ReactantStackData immutable(ModContainer container, ReactantStack value) {
        return of(container, () -> value, CodeHelper.emptyConsumer());
    }

    public static ReactantStackData empty(ModContainer container) {
        return immutable(container, ReactantStack.EMPTY);
    }

    public static ReactantStackData sampled(int frequency, ModContainer container, Supplier<@NotNull ReactantStack> getter,
                                            Consumer<@NotNull ReactantStack> clientSideSetter) {
        return of(container, new Sampler<>(frequency, getter), clientSideSetter);
    }

    public static ReactantStackData sampled(int frequency, ModContainer container, Supplier<@NotNull ReactantStack> getter) {
        return of(container, new Sampler<>(frequency, getter), CodeHelper.emptyConsumer());
    }

    public static ReactantStackData of(ModContainer container, Supplier<@NotNull ReactantStack> getter,
                                       Consumer<@NotNull ReactantStack> clientSideSetter) {

        Preconditions.checkNotNull(container, "Container must not be null.");

        final ReactantStackData data = container.isClientSide() ?
                new ReactantStackData(getter, clientSideSetter) :
                new ReactantStackData(getter);

        container.addBindableData(data);
        return data;
    }

    public static ReactantStackData of(ModContainer container, Supplier<@NotNull ReactantStack> getter) {
        return of(container, getter, CodeHelper.emptyConsumer());
    }

    public IBindableData<Integer> amount() {

        if (null == this._amountData) {
            this._amountData = AbstractData.as(0, intConsumer ->
                    this.bind(stack -> intConsumer.accept(stack.getAmount())));
        }

        return this._amountData;
    }

    //region IContainerData

    @Override
    @Nullable
    public ISyncedSetEntry getChangedValue() {

        final var current = this.getValue();

        if (this._lastValue.isEmpty() && current.isEmpty()) {
            return null;
        }

        final boolean equalReactant = this._lastValue.isReactantEqual(current);

        if (!equalReactant || current.getAmount() != this._lastValue.getAmount()) {

            this._lastValue = current;

            if (equalReactant) {
                return new AmountChangedEntry(this._lastValue.getAmount());
            } else {
                return new ReactantStackEntry(this._lastValue);
            }
        }

        return null;
    }

    @Override
    public ISyncedSetEntry getValueFrom(RegistryFriendlyByteBuf buffer) {
        return AmountChangedEntry.from(buffer, ReactantStackEntry::from);
    }

    @Override
    public void updateFrom(ISyncedSetEntry entry) {

        if (entry instanceof ReactantStackEntry record) {

            // full stack

            this.setClientSideValue(record.value);
            this.notify(record.value);

        } else if (entry instanceof AmountChangedEntry record) {

            // amount only

            this.setClientSideValue(this.getValue().copyWithAmount(record.amount()));
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
    //region ISyncedSetEntry

    private record ReactantStackEntry(ReactantStack value)
            implements ISyncedSetEntry {

        private static ReactantStackEntry from(RegistryFriendlyByteBuf buffer) {
            return new ReactantStackEntry(ReactantStack.createFrom(buffer));
        }

        @Override
        public void accept(@NotNull RegistryFriendlyByteBuf buffer) {

            // mark this as a full stack update. See {@link AmountChangedEntry#from}
            buffer.writeByte(1);
            this.value.writeTo(buffer);
        }
    }

    //endregion

    private ReactantStackData(Supplier<ReactantStack> getter, Consumer<ReactantStack> clientSideSetter) {
        super(getter, clientSideSetter);
    }

    private ReactantStackData(Supplier<ReactantStack> getter) {

        super(getter);
        this._lastValue = ReactantStack.EMPTY;
    }

    private ReactantStack _lastValue;
    private IBindableData<Integer> _amountData;

    //endregion
}
