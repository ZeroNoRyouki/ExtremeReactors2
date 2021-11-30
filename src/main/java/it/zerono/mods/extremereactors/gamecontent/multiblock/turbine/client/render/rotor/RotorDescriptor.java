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
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.client.model.TurbineRotorModelBuilder;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.rotor.RotorShaftState;
import it.zerono.mods.extremereactors.gamecontent.multiblock.turbine.variant.TurbineVariant;
import it.zerono.mods.zerocore.lib.client.render.ModRenderHelper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraftforge.common.util.NonNullConsumer;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class RotorDescriptor
    implements NonNullConsumer<PoseStack> {

    final Direction RotorDirection;
    final int Length;
    final ShaftSection[] Sections;
    final Matrix4f Translation;

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

        public Builder section(final RotorShaftState state, final NonNullConsumer<ShaftSection.Builder> consumer) {

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
        stack.last().pose().multiply(this.Translation);
    }

    //endregion
    //region internals

    private RotorDescriptor(final TurbineVariant variant, final Direction rotorDirection, final int length,
                            final List<ShaftSection> sections) {

        this.RotorDirection = rotorDirection;
        this.Length = length;
        this.Sections = sections.toArray(new ShaftSection[0]);
        this.Translation = CachedTranslations.getFor(rotorDirection);

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

        final float rotationOffsetX = 0 == this._initRotorDirectionVector.x() ? 0.5f : 0.0f;
        final float rotationOffsetY = 0 == this._initRotorDirectionVector.y() ? 0.5f : 0.0f;
        final float rotationOffsetZ = 0 == this._initRotorDirectionVector.z() ? 0.5f : 0.0f;

        this._initTranslate1 = Matrix4f.createTranslateMatrix(rotationOffsetX, rotationOffsetY, rotationOffsetZ);
        this._initTranslate2 = Matrix4f.createTranslateMatrix(-rotationOffsetX, -rotationOffsetY, -rotationOffsetZ);

        this.InitMatrix = this::initMatrix;
    }

    private void initMatrix(final PoseStack stack, final float rotorAngle) {

        final PoseStack.Pose entry = stack.last();

        final Matrix4f matrix = entry.pose();
        final Matrix3f normal = entry.normal();
        final Quaternion rotation = this._initRotorDirectionVector.rotationDegrees(rotorAngle);

        // ie: stack.translate(rotationOffsetX, rotationOffsetY, rotationOffsetZ);
        matrix.multiply(this._initTranslate1);

        // ie: stack.rotate(rotorDirectionVector.rotationDegrees(angle));
        matrix.multiply(rotation);
        normal.mul(rotation);

        // ie: stack.translate(-rotationOffsetX, -rotationOffsetY, -rotationOffsetZ);
        matrix.multiply(this._initTranslate2);
    }

    private final Vector3f _initRotorDirectionVector;
    private final Matrix4f _initTranslate1;
    private final Matrix4f _initTranslate2;

    //endregion
}
