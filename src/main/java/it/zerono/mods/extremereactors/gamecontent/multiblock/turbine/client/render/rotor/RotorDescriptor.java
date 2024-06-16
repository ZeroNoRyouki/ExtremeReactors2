/*
 *
 * RotorDescriptor.java
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

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.model.TurbineRotorModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorShaftState;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RotorDescriptor
    implements Consumer<@NotNull PoseStack> {

    final Direction RotorDirection;
    final int Length;
    final ShaftSection[] Sections;
    final Vector3fc Translation;

    final Supplier<BakedModel> ShaftModel;
    final Supplier<BakedModel> BladeModel;

    final BiConsumer<PoseStack, Float> InitMatrix;

    public static Builder builder(final TurbineVariant variant, final Direction rotorDirection, final int length) {
        return new Builder(variant, rotorDirection, length);
    }

    public static class Builder {

        public RotorDescriptor build() {
            return new RotorDescriptor(this._variant, this._rotorDirection, this._length, this._shaft);
        }

        public Builder section(final RotorShaftState state, final Consumer<ShaftSection.@NotNull Builder> consumer) {

            final ShaftSection.Builder builder = ShaftSection.builder(state);

            consumer.accept(builder);
            this._shaft.add(builder.build());
            return this;
        }

        //region internals

        private Builder(final TurbineVariant variant, final Direction rotorDirection, final int length) {

            this._variant = variant;
            this._rotorDirection = rotorDirection;
            this._length = length;
            this._shaft = Lists.newArrayList();
        }

        private final TurbineVariant _variant;
        private final Direction _rotorDirection;
        private final int _length;
        private final List<ShaftSection> _shaft;

        //endregion
    }

    //region NonNullConsumer<PoseStack>

    @Override
    public void accept(final PoseStack stack) {
        stack.last().pose().translate(this.Translation);
    }

    //endregion
    //region internals

    private RotorDescriptor(final TurbineVariant variant, final Direction rotorDirection, final int length,
                            final List<ShaftSection> sections) {

        this.RotorDirection = rotorDirection;
        this.Length = length;
        this.Sections = sections.toArray(new ShaftSection[0]);
        this.Translation = rotorDirection.step();

        switch (variant) {

            case Basic:
                this.ShaftModel = TurbineRotorModelBuilder.BASIC_SHAFT;
                this.BladeModel = TurbineRotorModelBuilder.BASIC_BLADE;
                break;

            case Reinforced:
                this.ShaftModel = TurbineRotorModelBuilder.REINFORCED_SHAFT;
                this.BladeModel = TurbineRotorModelBuilder.REINFORCED_BLADE;
                break;

            default:
                this.ShaftModel = this.BladeModel = ModRenderHelper::getMissingModel;
        }

        // initial matrix setup for angle and block center

        this._initRotorDirectionVector = this.RotorDirection.step();
        this._initRotationOffsetX = 0 == this._initRotorDirectionVector.x() ? 0.5f : 0.0f;
        this._initRotationOffsetY = 0 == this._initRotorDirectionVector.y() ? 0.5f : 0.0f;
        this._initRotationOffsetZ = 0 == this._initRotorDirectionVector.z() ? 0.5f : 0.0f;

        this.InitMatrix = this::initMatrix;
    }

    private void initMatrix(final PoseStack stack, final float rotorAngle) {

        final PoseStack.Pose entry = stack.last();
        final Quaternionf rotation = Axis.of(this._initRotorDirectionVector).rotationDegrees(rotorAngle);

        // ie:
        // - stack.translate(rotationOffsetX, rotationOffsetY, rotationOffsetZ);
        // - stack.rotate(rotorDirectionVector.rotationDegrees(angle));
        // - stack.translate(-rotationOffsetX, -rotationOffsetY, -rotationOffsetZ);
        entry.pose().translate(this._initRotationOffsetX, this._initRotationOffsetY, this._initRotationOffsetZ)
                .rotate(rotation)
                .translate(-this._initRotationOffsetX, -this._initRotationOffsetY, -this._initRotationOffsetZ);
        entry.normal().rotate(rotation);
    }

    private final Vector3f _initRotorDirectionVector;
    private final float _initRotationOffsetX;
    private final float _initRotationOffsetY;
    private final float _initRotationOffsetZ;

    //endregion
}
