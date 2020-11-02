/*
 *
 * ClientFuelRodsLayout.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import it.zerono.mods.extremereactors.api.reactor.Reactant;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.FuelRodsLayout;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.IFuelContainer;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.model.FuelRodFluidModel;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorFuelRodEntity;
import it.zerono.mods.zerocore.lib.data.gfx.Colour;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class ClientFuelRodsLayout
        extends FuelRodsLayout {

    public enum FuelRodFluidStatus {

        Empty,
        FuelOnly,
        WasteOnly,
        Mixed,
        FullFuelOnly,
        FullWasteOnly
    }

    public ClientFuelRodsLayout(Direction direction, int length) {

        super(direction, length);

        this.setReactantsChanged(false);
        this.setAssemblyFuelQuota(0f);
        this.setAssemblyWasteQuota(0f);

        this.setFuelColor(Colour.WHITE);
        this.setWasteColor(Colour.WHITE);

        this._rodsFuelData = new FuelData[this.getRodLength()];
        Arrays.setAll(this._rodsFuelData, FuelData::new);

        this._modelsCache = Maps.newHashMapWithExpectedSize(5);
        this.resetModels();
    }

    public FuelData getFuelData(final int fuelRodIndex) {

        Preconditions.checkArgument(fuelRodIndex >= 0 && fuelRodIndex < this._rodsFuelData.length);
        return this._rodsFuelData[Direction.Plane.VERTICAL == this.getOrientation() ? fuelRodIndex : 0];
    }

    public boolean isFuelDataChanged(final int fuelRodIndex) {
        return this.getFuelData(fuelRodIndex).isChanged() || this.isReactantsChanged();
    }

    @OnlyIn(Dist.CLIENT)
    public Colour getFuelColor() {
        return this._fuelColor;
    }

    @OnlyIn(Dist.CLIENT)
    public Colour getWasteColor() {
        return this._wasteColor;
    }

    public float getAssemblyFuelQuota() {
        return this._assemblyFuelQuota;
    }

    public float getAssemblyWasteQuota() {
        return this._assemblyWasteQuota;
    }

    public float getFuelRodFuelQuota() {
        return this.getAssemblyFuelQuota() / this.getRodLength();
    }

    public float getFuelRodWasteQuota() {
        return this.getAssemblyWasteQuota() / this.getRodLength();
    }

    public IBakedModel getModelFor(final FuelData rodData) {
        return this.modelsCache().computeIfAbsent(rodData.getFluidStatus(), s -> new FuelRodFluidModel(rodData, this.getAxis()));
    }

    public Colour getModelTint(final int tintIndex) {

        switch (tintIndex) {

            case 0:
                return this.getFuelColor();

            case 1:
                return this.getWasteColor();

            default:
                return Colour.WHITE;
        }
    }

    //region FuelRodsLayout

    @Override
    public void updateFuelData(final IFuelContainer fuelContainer, int fuelRodsInReactor) {

        super.updateFuelData(fuelContainer, fuelRodsInReactor);

        // fuel/waste colors
        this.updateGfx(fuelContainer);

        // fuel/waste quota for each fuel rod
        this.updateQuotas(fuelContainer, fuelRodsInReactor);

        // split fuel/waste between fuel rods

        if (Direction.Axis.Y == this.getAxis()) {
            // vertical column, fluids pool to the bottom (waste first)
            this.updateFuelDataVertically();
        } else {
            // horizontal column, fluids distribute equally
            this.updateFuelDataHorizontally();
        }
    }

    private void updateFuelDataVertically() {

        // vertical column, reactants pool to the bottom (waste first)

        final float rodCapacity = ReactorFuelRodEntity.FUEL_CAPACITY_PER_FUEL_ROD;
        float remainingWaste = this.getAssemblyWasteQuota();
        float remainingFuel = this.getAssemblyFuelQuota();
        float fuelAmount, wasteAmount;
        final EnumSet<FuelRodFluidStatus> modelsToBeRemoved = EnumSet.noneOf(FuelRodFluidStatus.class);

        for (int i = 0; i < this.getRodLength(); ++i) {

            if (remainingWaste > 0.0f) {

                wasteAmount = Math.min(remainingWaste, rodCapacity);
                remainingWaste -= wasteAmount;

            } else {

                wasteAmount = 0.0f;
            }

            if (remainingFuel > 0.0f) {

                fuelAmount = Math.min(remainingFuel, rodCapacity - wasteAmount);
                remainingFuel -= fuelAmount;

            } else {

                fuelAmount = 0.0f;
            }

            final FuelData data = this.getFuelData(i);
            final FuelRodFluidStatus oldStatus = data.getFluidStatus();

            if (data.update(fuelAmount, wasteAmount)) {

                modelsToBeRemoved.add(oldStatus);
                modelsToBeRemoved.add(data.getFluidStatus());
            }
        }

        this.removeOutdatedModel(modelsToBeRemoved);
    }

    private void updateFuelDataHorizontally() {

        // horizontal column, reactants distribute equally

        final float fuelPerRod = this.getFuelRodFuelQuota();
        final float wastePerRod = this.getFuelRodWasteQuota();
        final EnumSet<FuelRodFluidStatus> modelsToBeRemoved = EnumSet.noneOf(FuelRodFluidStatus.class);

        for (int i = 0; i < this.getRodLength(); ++i) {

            final FuelData data = this.getFuelData(i);
            final FuelRodFluidStatus oldStatus = data.getFluidStatus();

            if (data.update(fuelPerRod, wastePerRod)) {

                modelsToBeRemoved.add(oldStatus);
                modelsToBeRemoved.add(data.getFluidStatus());
            }
        }

        this.removeOutdatedModel(modelsToBeRemoved);
    }

    @Override
    public void updateFuelRodsOcclusion(final Set<ReactorFuelRodEntity> fuelRods) {

        final Direction[] directions = this.getRadiateDirections();

        for (final ReactorFuelRodEntity fuelRod : fuelRods) {

            final BlockPos rodPosition = fuelRod.getWorldPosition();
            boolean occluded = true;

            for (final Direction direction : directions) {

                final BlockPos checkPosition = rodPosition.offset(direction);

                if (fuelRod.getPartWorldOrFail().isAirBlock(checkPosition) ||
                        !RenderTypeLookup.canRenderInLayer(fuelRod.getPartWorldOrFail().getBlockState(checkPosition), RenderType.getSolid())) {

                    occluded = false;
                    break;
                }
            }

            fuelRod.setOccluded(occluded);
        }
    }

    //endregion
    //region FuelData

    public static class FuelData {

        FuelData(int rodIndex) {

            this.setFuelAmount(0);
            this.setWasteAmount(0);
            this.setFuelHeight(0);
            this.setWasteHeight(0);
            this.setChanged(false);
            this.setFluidStatus(FuelRodFluidStatus.Empty);
        }

        public float getFuelAmount() {
            return this._fuelAmount;
        }

        public float getWasteAmount() {
            return this._wasteAmount;
        }

        public float getFuelHeight() {
            return this._fuelHeight;
        }

        public float getWasteHeight() {
            return this._wasteHeight;
        }

        public boolean isChanged() {
            return this._changed;
        }

        public FuelRodFluidStatus getFluidStatus() {
            return this._fluidStatus;
        }

        //region internals

        private boolean update(float fuelAmount, float wasteAmount) {

            this.setChanged(this.getFuelAmount() != fuelAmount || this.getWasteAmount() != wasteAmount);
            this.setFuelAmount(fuelAmount);
            this.setWasteAmount(wasteAmount);
            this.setFuelHeight(this.computeReactantHeight(this.getFuelAmount()));
            this.setWasteHeight(this.computeReactantHeight(this.getWasteAmount()));

            if (0.0 == this.getFuelAmount() && 0.0 == this.getWasteAmount()) {
                this.setFluidStatus(FuelRodFluidStatus.Empty);
            } else if (ReactorFuelRodEntity.FUEL_CAPACITY_PER_FUEL_ROD == this.getFuelAmount()) {
                this.setFluidStatus(FuelRodFluidStatus.FullFuelOnly);
            } else if (ReactorFuelRodEntity.FUEL_CAPACITY_PER_FUEL_ROD == this.getWasteAmount()) {
                this.setFluidStatus(FuelRodFluidStatus.FullWasteOnly);
            } else if (0.0 < this.getFuelAmount() && 0.0 == this.getWasteAmount()) {
                this.setFluidStatus(FuelRodFluidStatus.FuelOnly);
            } else if (0.0 < this.getWasteAmount() && 0.0 == this.getFuelAmount()) {
                this.setFluidStatus(FuelRodFluidStatus.WasteOnly);
            } else {
                this.setFluidStatus(FuelRodFluidStatus.Mixed);
            }

            return this.isChanged();
        }

        private void setFuelAmount(float value) {
            this._fuelAmount = verifyFloat(value);
        }

        private void setWasteAmount(float value) {
            this._wasteAmount = verifyFloat(value);
        }

        private void setFuelHeight(float value) {
            this._fuelHeight = verifyFloat(value);
        }

        private void setWasteHeight(float value) {
            this._wasteHeight = verifyFloat(value);
        }

        private float computeReactantHeight(float reactantAmount) {
            return reactantAmount / ReactorFuelRodEntity.FUEL_CAPACITY_PER_FUEL_ROD;
        }

        private void setChanged(boolean value) {
            this._changed = value;
        }

        private void setFluidStatus(FuelRodFluidStatus value) {
            this._fluidStatus = value;
        }

        private float _fuelAmount;
        private float _wasteAmount;
        private float _fuelHeight;
        private float _wasteHeight;
        private boolean _changed;
        private FuelRodFluidStatus _fluidStatus;

        //endregion
    }

    //endregion
    //region internals

    private boolean isReactantsChanged() {
        return this._reactantsChanged;
    }

    private void setReactantsChanged(boolean value) {
        this._reactantsChanged = value;
    }

    private void setAssemblyFuelQuota(final float quota) {
        this._assemblyFuelQuota = verifyFloat(quota);
    }

    private void setAssemblyWasteQuota(final float quota) {
        this._assemblyWasteQuota = verifyFloat(quota);
    }

    private void setFuelColor(final Colour colour) {
        this._fuelColor = colour;
    }

    private void setWasteColor(final Colour colour) {
        this._wasteColor = colour;
    }

    private Map<FuelRodFluidStatus, IBakedModel> modelsCache() {
        return this._modelsCache;
    }

    private static float verifyFloat(float value) {
        return Float.isNaN(value) || Float.isInfinite(value) ? 0f : value;
    }

    private static float computeAssemblyReactantQuota(int reactantAmount, int fuelRodsInAssembly, int fuelRodsInReactor) {
        return (float) reactantAmount / (float) fuelRodsInReactor * (float) fuelRodsInAssembly;
    }

    private void updateQuotas(final IFuelContainer fuelContainer, int fuelRodsInReactor) {

        fuelRodsInReactor = Math.max(1, fuelRodsInReactor);

        this.setAssemblyFuelQuota(computeAssemblyReactantQuota(fuelContainer.getFuelAmount(), this.getRodLength(), fuelRodsInReactor));
        this.setAssemblyWasteQuota(computeAssemblyReactantQuota(fuelContainer.getWasteAmount(), this.getRodLength(), fuelRodsInReactor));
    }

    private void updateGfx(final IFuelContainer fuelContainer) {

        final Colour oldFuelColor = this.getFuelColor();
        final Colour oldWasteColor = this.getWasteColor();

        this.setFuelColor(fuelContainer.getFuel()
                .map(Reactant::getColour)
                .orElse(Colour.WHITE));

        this.setWasteColor(fuelContainer.getWaste()
                .map(Reactant::getColour)
                .orElse(Colour.WHITE));

        this.setReactantsChanged(!this.getFuelColor().equals(oldFuelColor) || !this.getWasteColor().equals(oldWasteColor));

        if (this.isReactantsChanged()) {
            this.resetModels();
        }
    }

    private void removeOutdatedModel(final Set<FuelRodFluidStatus> toBeRemoved) {

        --this._modelsUpdateSkipCount;

        if (0 == this._modelsUpdateSkipCount) {

            toBeRemoved.stream()
                    .filter(s -> s != FuelRodFluidStatus.Empty && s != FuelRodFluidStatus.FullFuelOnly && s != FuelRodFluidStatus.FullWasteOnly)
                    .forEach(this.modelsCache()::remove);

            this._modelsUpdateSkipCount = 100;
        }
    }

    private void resetModels() {

        this._modelsCache.clear();
        this._modelsCache.put(FuelRodFluidStatus.FullFuelOnly, new FuelRodFluidModel(1.0f, 0.0f, this.getAxis()));
        this._modelsCache.put(FuelRodFluidStatus.FullWasteOnly, new FuelRodFluidModel(0.0f, 1.0f, this.getAxis()));
        this._modelsUpdateSkipCount = 100;
    }

    private final FuelData[] _rodsFuelData;
    private float _assemblyFuelQuota;
    private float _assemblyWasteQuota;
    private boolean _reactantsChanged;

    private Colour _fuelColor;
    private Colour _wasteColor;

    private final Map<FuelRodFluidStatus, IBakedModel> _modelsCache;
    private int _modelsUpdateSkipCount;

    //endregion
}
