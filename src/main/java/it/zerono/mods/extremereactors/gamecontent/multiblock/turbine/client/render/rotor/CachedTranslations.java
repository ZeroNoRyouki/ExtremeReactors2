/*
 *
 * CachedTranslations.java
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

import it.zerono.mods.zerocore.lib.CodeHelper;
import net.minecraft.core.Direction;
import com.mojang.math.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Comparator;

@OnlyIn(Dist.CLIENT)
public final class CachedTranslations {

    public static Matrix4f getFor(final Direction direction) {
        return s_cache[direction.ordinal()];
    }

    //region internals

    private static final Matrix4f[] s_cache;

    static {

        s_cache = CodeHelper.directionStream()
                .sorted(Comparator.comparingInt(Enum::ordinal))
                .map(Direction::getNormal)
                .map(vector -> Matrix4f.createTranslateMatrix(vector.getX(), vector.getY(), vector.getZ()))
                .toArray(Matrix4f[]::new);
    }

    //endregion
}
