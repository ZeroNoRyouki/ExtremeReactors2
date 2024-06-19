/*
 *
 * ReactantStack.java
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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.zerono.mods.extremereactors.Log;
import it.zerono.mods.extremereactors.api.IMapping;
import it.zerono.mods.extremereactors.api.reactor.Reactant;
import it.zerono.mods.extremereactors.api.reactor.ReactantType;
import it.zerono.mods.extremereactors.api.reactor.ReactantsRegistry;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
import it.zerono.mods.zerocore.lib.data.stack.IStackAdapter;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.*;

@SuppressWarnings({"WeakerAccess"})
public class ReactantStack
        implements ISyncableEntity {

    public static final ReactantStack EMPTY = new ReactantStack();

    public static final IStackAdapter<ReactantStack, Reactant> ADAPTER;

    public ReactantStack(final Reactant reactant) {
        this(reactant, 0);
    }

    public ReactantStack(final Reactant reactant, final int amount) {

        this._reactant = reactant;
        this._amount = amount;
        this.updateEmptyState();
    }

    @SuppressWarnings("CopyConstructorMissesField")
    public ReactantStack(final ReactantStack other) {
        this(other._reactant, other._amount);
    }

    public ReactantStack(final IMapping<TagKey<Item>, Reactant> mapping, final int itemCount) {
        this(mapping.getProduct(), mapping.getProductAmount(itemCount));
    }

    public static ReactantStack createFrom(final FriendlyByteBuf data) {

        if (!data.readBoolean()) {
            return ReactantStack.EMPTY;
        }

        final int amount = data.readInt();
        final String name = data.readUtf(1024);

        return ReactantsRegistry.get(name)
                .map(reactant -> new ReactantStack(reactant, amount))
                .orElse(ReactantStack.EMPTY);
    }

    public void writeTo(final FriendlyByteBuf data) {

        if (this.isEmpty()) {

            data.writeBoolean(false);

        } else {

            data.writeBoolean(true);
            data.writeInt(this._amount);
            data.writeUtf(this._reactant.getName());
        }
    }

    public static boolean areIdentical(final ReactantStack stack1, final ReactantStack stack2) {
        return stack1.isReactantEqual(stack2) && stack1.getAmount() == stack2.getAmount();
    }

    /**
     * @return true if this stack does not contain any units of Reactant
     */
    public boolean isEmpty() {

        if (this == EMPTY) {
            return true;
        } else if (this.getReactant().isPresent()) {
            return this.getAmount() <= 0;
        } else {
            return true;
        }
    }

    public Optional<Reactant> getReactant() {
        return this._isEmpty ? Optional.empty() : Optional.ofNullable(this._reactant);
    }

    public int getAmount() {
        return this._isEmpty ? 0 : this._amount;
    }

    public void setAmount(final int amount) {

        this._amount = Math.max(0, amount);
        this.updateEmptyState();
    }

    public void modifyAmount(final int delta) {
        this.setAmount(this.getAmount() + delta);
    }

    public boolean isReactantEqual(final Reactant other) {
        return !this.isEmpty() && this.getReactant().map(reactant -> reactant.equals(other)).orElse(false);
    }

    public boolean isReactantEqual(final ReactantStack other) {
        return !this.isEmpty() && !other.isEmpty() && this.getReactant().equals(other.getReactant());
    }

    public boolean contains(final ReactantType type) {
        return this.getReactant()
                .filter(reactant -> reactant.test(type))
                .isPresent();
    }

    public boolean containsFuel() {
        return this.contains(ReactantType.Fuel);
    }

    public boolean containsWaste() {
        return this.contains(ReactantType.Waste);
    }

    public ReactantStack copyWithAmount(int amount) {
        return new ReactantStack(this._reactant, amount);
    }

    //region ISyncableEntity

    /**
     * Sync the entity data from the given NBT compound
     *
     * @param data       the data
     * @param syncReason the reason why the synchronization is necessary
     */
    @Override
    public void syncDataFrom(CompoundTag data, HolderLookup.Provider registries, SyncReason syncReason) {

        if (data.contains("rstack_name")) {

            final String name = data.getString("rstack_name");

            if (!Strings.isNullOrEmpty(name) && !"EMPTY".equals(name)) {

                CodeHelper.optionalIfPresentOrElse(ReactantsRegistry.get(name),
                        reactant -> {

                            if (data.contains("rstack_amount")) {

                                this._reactant = reactant;
                                this.setAmount(data.getInt("rstack_amount"));

                            } else {

                                Log.LOGGER.warn(Log.REACTOR, "Loading a ReactantStack containing an unknown amount");
                                this._reactant = null;
                                this.setAmount(0);
                            }
                        },
                        (() -> {

                            Log.LOGGER.warn(Log.REACTOR, "Loading a ReactantStack containing an unknown Reactant: {}", name);
                            this._reactant = null;
                            this.setAmount(0);
                        }));
            }
        }

        this.updateEmptyState();
    }

    /**
     * Sync the entity data to the given {@link CompoundTag}
     *
     * @param data       the {@link CompoundTag} to write to
     * @param syncReason the reason why the synchronization is necessary
     * @return the {@link CompoundTag} the data was written to (usually {@code data})
     */
    @Override
    public CompoundTag syncDataTo(CompoundTag data, HolderLookup.Provider registries, SyncReason syncReason) {

        if (!this.isEmpty()) {

            data.putString("rstack_name", this.getReactant().map(Reactant::getName).orElse("EMPTY"));
            data.putInt("rstack_amount", this.getAmount());
        }

        return data;
    }

    //endregion
    //region Object

    @Override
    public String toString() {
        return this.isEmpty() ? "EMPTY" : String.format("%s, %d mB",
                this.getReactant()
                        .map(Reactant::getName)
                        .orElse("NO REACTANT"),
                this.getAmount());
    }

    //endregion
    //region internals

    private ReactantStack() {

        this._reactant = null;
        this._amount = 0;
    }

    private void updateEmptyState() {
        this._isEmpty = this.isEmpty();
    }

    private Reactant _reactant;
    private int _amount;
    private boolean _isEmpty;

    static {

        ADAPTER = new IStackAdapter<ReactantStack, Reactant>() {

            @Override
            public Optional<Reactant> getContent(final ReactantStack stack) {
                return stack.getReactant();
            }

            @Override
            public int getAmount(final ReactantStack stack) {
                return stack.getAmount();
            }

            @Override
            public ReactantStack setAmount(final ReactantStack stack, final int amount) {

                if (!stack.isEmpty()) {
                    stack.setAmount(amount);
                }

                return stack;
            }

            @Override
            public ReactantStack modifyAmount(final ReactantStack stack, final int delta) {

                if (!stack.isEmpty()) {
                    stack.modifyAmount(delta);
                }

                return stack;
            }

            @Override
            public ReactantStack getEmptyStack() {
                return EMPTY;
            }

            @Override
            public boolean isEmpty(final ReactantStack stack) {
                return stack.isEmpty();
            }

            @Override
            public boolean isStackContentEqual(final ReactantStack stack1, final ReactantStack stack2) {
                return stack1.isReactantEqual(stack2);
            }

            @Override
            public boolean isContentEqual(final Reactant content1, final Reactant content2) {
                return content1.equals(content2);
            }

            @Override
            public boolean areIdentical(final ReactantStack stack1, final ReactantStack stack2) {
                return ReactantStack.areIdentical(stack1, stack2);
            }

            @Override
            public ReactantStack create(final Reactant content, final int amount) {
                return new ReactantStack(content, amount);
            }

            @Override
            public ReactantStack create(final ReactantStack stack) {
                return new ReactantStack(stack);
            }

            @Override
            public ReactantStack[] createArray(final int length) {
                return new ReactantStack[length];
            }

            @Override
            public List<ReactantStack> createList() {
                return Lists.newArrayList();
            }

            @Override
            public Set<ReactantStack> createSet() {
                return Sets.newHashSet();
            }

            @Override
            public ReactantStack deserialize(HolderLookup.Provider registries, Tag input) {

                if (input instanceof CompoundTag compound) {

                    final ReactantStack stack = new ReactantStack();

                    stack.syncDataFrom(compound, registries, SyncReason.FullSync);
                    return stack;
                }

                throw new IllegalArgumentException("Input must be a CompoundTag instance");
            }

            @Override
            public Tag serialize(HolderLookup.Provider registries, ReactantStack stack, Tag output) {

                if (output instanceof CompoundTag tag) {
                    return stack.syncDataTo(tag, registries, SyncReason.FullSync);
                }

                throw new IllegalArgumentException("Output must be a CompoundTag instance");
            }

            @Override
            public String toString(final ReactantStack stack) {
                return stack.toString();
            }

            @Override
            public <T> T map(ReactantStack stack, Function<Reactant, T> mapper, T defaultValue) {
                return stack.isEmpty() ? defaultValue : mapper.apply(stack._reactant);
            }

            @Override
            public <T> T map(ReactantStack stack, IntFunction<T> mapper, T defaultValue) {
                return stack.isEmpty() ? defaultValue : mapper.apply(stack._amount);
            }

            @Override
            public <T> T map(ReactantStack stack, BiFunction<Reactant, Integer, T> mapper, T defaultValue) {
                return stack.isEmpty() ? defaultValue : mapper.apply(stack._reactant, stack._amount);
            }

            @Override
            public void accept(ReactantStack stack, Consumer<Reactant> consumer) {

                if (!stack.isEmpty()) {
                    consumer.accept(stack._reactant);
                }
            }

            @Override
            public void accept(ReactantStack stack, IntConsumer consumer) {

                if (!stack.isEmpty()) {
                    consumer.accept(stack._amount);
                }
            }

            @Override
            public void accept(ReactantStack stack, BiConsumer<Reactant, Integer> consumer) {

                if (!stack.isEmpty()) {
                    consumer.accept(stack._reactant, stack._amount);
                }
            }
        };
    }

    //endregion
}
