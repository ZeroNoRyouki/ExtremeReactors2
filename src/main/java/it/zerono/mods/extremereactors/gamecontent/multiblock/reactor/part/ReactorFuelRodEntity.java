/*
 *
 * ReactorFuelRodEntity.java
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

package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part;

import it.zerono.mods.extremereactors.api.radiation.RadiationPacket;
import it.zerono.mods.extremereactors.api.reactor.FuelProperties;
import it.zerono.mods.extremereactors.api.reactor.IHeatEntity;
import it.zerono.mods.extremereactors.api.reactor.Moderator;
import it.zerono.mods.extremereactors.api.reactor.ReactantMappingsRegistry;
import it.zerono.mods.extremereactors.api.reactor.radiation.EnergyConversion;
import it.zerono.mods.extremereactors.api.reactor.radiation.IRadiationModerator;
import it.zerono.mods.extremereactors.api.reactor.radiation.IrradiationData;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.FuelRodsLayout;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.IIrradiationSource;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.IReactorReader;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.ReactantHelper;
import it.zerono.mods.zerocore.base.multiblock.AbstractMultiblockController;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class ReactorFuelRodEntity
        extends AbstractReactorEntity
        implements IRadiationModerator, IHeatEntity, IIrradiationSource {

    public static final int FUEL_CAPACITY_PER_FUEL_ROD = 4 * ReactantMappingsRegistry.STANDARD_SOLID_REACTANT_AMOUNT; // 4 ingots per rod

    public ReactorFuelRodEntity(final BlockPos position, final BlockState blockState) {

        super(Content.TileEntityTypes.REACTOR_FUELROD.get(), position, blockState);
        this._controlRod = null;
        this._rodIndex = -1;
        this._occluded = false;
    }

    public double getHeatTransferRate() {

        final Direction.Plane fuelAssemblyPlane = CodeHelper.perpendicularPlane(this.getFuelRodsLayout().getAxis());
        final Level world = this.getPartWorldOrFail();
        final BlockPos rodPosition = this.getBlockPos();
        double heatTransferRate = 0d;

        for (final Direction dir: fuelAssemblyPlane) {

            final BlockPos targetPosition = rodPosition.relative(dir);
            final BlockState state = world.getBlockState(targetPosition);

            // Is it air ?

            if (state.isAir()) {

                heatTransferRate += IHeatEntity.CONDUCTIVITY_AIR;
                continue;
            }

            // No, is it a tile entity or a moderator maybe?

            final BlockEntity te = WorldHelper.getLoadedTile(world, targetPosition);

            if (!(te instanceof ReactorFuelRodEntity) && te instanceof IHeatEntity) {

                // We don't transfer to other fuel rods, due to heat pooling, only from an IHeatEntity
                heatTransferRate += ((IHeatEntity)te).getThermalConductivity();

            } else {

                // If not, is it a moderator?
                heatTransferRate += this.getConductivityFromBlock(state);
            }
        }

        return heatTransferRate;
    }

    public void linkToControlRod(final ReactorControlRodEntity controlRod, final int rodIndex) {

        this._controlRod = controlRod;
        this._rodIndex = rodIndex;
    }

    public boolean isLinked() {
        return null != this._controlRod;
    }

    public int getFuelRodIndex() {
        return this._rodIndex;
    }

    //region client render support

    public void setOccluded(final boolean occluded) {
        this._occluded = occluded;
    }

    public boolean isOccluded() {
        return this._occluded;
    }

    @Override
    protected ModelData getUpdatedModelData() {
        return this.getFuelRodsLayoutForRendering().getFuelRodModelData(this.getFuelRodIndex(), this.isOccluded());
    }

    public FuelRodsLayout getFuelRodsLayout() {
        return this.evalOnController(IReactorReader::getFuelRodsLayout, FuelRodsLayout.EMPTY);
    }

    public FuelRodsLayout getFuelRodsLayoutForRendering() {
        return this.isMachineAssembled() &&
                this.testOnController(AbstractMultiblockController::isInteriorVisible) ?
                this.getFuelRodsLayout() : FuelRodsLayout.EMPTY;
    }

//    /**
//     * See {@link Block#eventReceived} for more information. This must return true serverside before it is called
//     * clientside.
//     *
//     * @param id
//     * @param type
//     */
//    @Override
//    public boolean receiveClientEvent(int id, int type) {
//
//        if (id == 1) {
//
//            final BlockPos pos = this.getWorldPosition();
//
//            this.callOnLogicalClient(world -> {
//                world.addParticle(ParticleTypes.HEART, pos.getX() + 0.5 + Math.random() * 0.4 - 0.2,
//                        pos.getY() + 0.75,
//                        pos.getZ() + 0.5 + Math.random() * 0.4 - 0.2, 0, 0, 0);
//            });
//
//            return true;
//        }
//
//        return super.receiveClientEvent(id, type);
//    }
//
//    private void sparkle() {
//        this.forPartWorld(w -> w.addBlockEvent(this.getWorldPosition(), this.getBlockType(), 1, 0));
//    }

    //endregion
    //region IRadiationModerator

    @Override
    public void moderateRadiation(IrradiationData irradiationData, RadiationPacket radiation) {

        if (!this.isConnected() || !this.isLinked()) {
            return;
        }

        final double fuelHeat = this.evalOnController(c -> c.getFuelHeat().getAsDouble(), 0.0);
        final float controlRodInsertion = null != this._controlRod ? this._controlRod.getInsertionPercentage() : 100.0f;
        final FuelProperties fuelData = this.evalOnController(IReactorReader::getFuelProperties, FuelProperties.INVALID);

        // Fuel absorptiveness is determined by control rod + a heat modifier.
        // Starts at 1 and decays towards 0.05, reaching 0.6 at 1000 and just under 0.2 at 2000. Inflection point at about 500-600.
        // Harder radiation makes absorption more difficult.
        final float baseAbsorption = (float)(1.0 - (0.95 * Math.exp(-10 * Math.exp(-0.0022 * fuelHeat)))) *
                (1f - (radiation.hardness / fuelData.getHardnessDivisor()));

        // Some fuels are better at absorbing radiation than others
        final float scaledAbsorption = Math.min(1f, baseAbsorption * fuelData.getAbsorptionCoefficient());

        // Control rods increase total neutron absorption, but decrease the total neutrons which fertilize the fuel
        // Absorb up to 50% better with control rods inserted.
        final float controlRodBonus = (1f - scaledAbsorption) * controlRodInsertion * 0.5f;
        final float controlRodPenalty = scaledAbsorption * controlRodInsertion * 0.5f;

        final float radiationAbsorbed = (scaledAbsorption + controlRodBonus) * radiation.intensity;
        final float fertilityAbsorbed = (scaledAbsorption - controlRodPenalty) * radiation.intensity;

        float fuelModerationFactor = fuelData.getModerationFactor();

        // Full insertion doubles the moderation factor of the fuel as well as adding its own level
        fuelModerationFactor += fuelModerationFactor * controlRodInsertion + controlRodInsertion;

        radiation.intensity = Math.max(0f, radiation.intensity - radiationAbsorbed);
        radiation.hardness /= fuelModerationFactor;

        // Being irradiated both heats up the fuel and also enhances its fertility
        irradiationData.fuelEnergyAbsorption += radiationAbsorbed * EnergyConversion.ENERGY_PER_RADIATION_UNIT;
        irradiationData.fuelAbsorbedRadiation += fertilityAbsorbed;

//        // fx
//        this.sparkle();
    }

    //endregion
    //region IHeatEntity

    @Override
    public double getThermalConductivity() {
        return IHeatEntity.CONDUCTIVITY_COPPER;
    } //TODO variant?

    //endregion
    //region IIrradiationSource

    @Override
    public byte getControlRodInsertionRatio() {
        return null != this._controlRod ? this._controlRod.getInsertionRatio() : 0;
    }

    @Override
    public Direction[] getIrradiationDirections() {
        return this.getFuelRodsLayout().getRadiateDirections();
    }

    //endregion
    //region AbstractReactorEntity

    @Override
    public boolean isGoodForPosition(PartPosition position, IMultiblockValidator validatorCallback) {

        if (PartPosition.Interior != position) {

            validatorCallback.setLastError(this.getWorldPosition(), "multiblock.validation.reactor.invalid_fuelrod_position");
            return false;
        }

        return true;
    }

    //endregion
    //region internals

    private double getConductivityFromBlock(BlockState blockState) {
        return ReactantHelper.getModeratorFrom(blockState, Moderator.AIR).getHeatConductivity();
    }

    private ReactorControlRodEntity _controlRod;
    private int _rodIndex;
    private boolean _occluded;

    //endregion
}
