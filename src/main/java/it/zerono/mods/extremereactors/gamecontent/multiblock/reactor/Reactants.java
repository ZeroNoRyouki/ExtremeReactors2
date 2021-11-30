/*
 *
 * Reactants.java
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

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.zerono.mods.extremereactors.api.reactor.ReactantType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.NonNullConsumer;

import javax.annotation.Nonnull;
import java.util.List;

public enum Reactants
        implements NonNullConsumer<LivingEntity> {

    Yellorium(ReactantType.Fuel, "yellorium", 0xc6ba54, MobEffects.WEAKNESS, MobEffects.MOVEMENT_SLOWDOWN),
    Cyanite(ReactantType.Waste, "cyanite", 0x5387b7, MobEffects.WEAKNESS, MobEffects.MOVEMENT_SLOWDOWN, MobEffects.HUNGER),
    Blutonium(ReactantType.Fuel, "blutonium", 0x17179c, MobEffects.WEAKNESS, MobEffects.MOVEMENT_SLOWDOWN, MobEffects.HUNGER, MobEffects.POISON),
    Magentite(ReactantType.Waste, "magentite", 0xe41de4, MobEffects.WEAKNESS, MobEffects.MOVEMENT_SLOWDOWN, MobEffects.HUNGER, MobEffects.WITHER),
    Verderium(ReactantType.Fuel, "verderium", 0x00FF00, MobEffects.WITHER, MobEffects.HUNGER, MobEffects.POISON, MobEffects.WEAKNESS, MobEffects.BLINDNESS),
    Rossinite(ReactantType.Waste, "rossinite", 0xFF0000, MobEffects.MOVEMENT_SPEED, MobEffects.DIG_SPEED, MobEffects.SLOW_FALLING, MobEffects.HERO_OF_THE_VILLAGE),
    ;

    public ReactantType getType() {
        return this._type;
    }

    public String getReactantName() {
        return this._name;
    }

    public String getTagName() {
        return this._name;
    }

    public String getLangKey() {
        return "reactant.bigreactors." + this._name;
    }

    public String getBlockName() {
        return this._name + "_block";
    }

    public String getIngotName() {
        return this._name + "_ingot";
    }

    public String getDustName() {
        return this._name + "_dust";
    }

    public String getBucketName() {
        return this._name + "_bucket";
    }

    public String getFluidName() {
        return this._name + "_fluid";
    }

    public String getFluidSourceName() {
        return this._name;
    }

    public String getFluidFlowingName() {
        return this._name + "_flowing";
    }

    public int getColour() {
        return this._colour;
    }

    //region NonNullConsumer<LivingEntity>

    @Override
    public void accept(@Nonnull LivingEntity entity) {
        this._effects.forEach(effect -> entity.addEffect(new MobEffectInstance(effect, 400, 0, true, true, true)));
    }

    //endregion
    //region internal

    Reactants(final ReactantType type, final String name, final int colour, final MobEffect... effects) {

        this._type = type;
        this._name = name;
        this._colour = colour;
        this._effects = new ObjectArrayList<>(effects);
    }

    private final ReactantType _type;
    private final String _name;
    private final int _colour;
    private final List<MobEffect> _effects;

    //endregion
}
