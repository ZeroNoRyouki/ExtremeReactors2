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
import it.zerono.mods.extremereactors.api.reactor.IHeatEntity;
import it.zerono.mods.extremereactors.api.reactor.Moderator;
import it.zerono.mods.extremereactors.api.reactor.ModeratorsRegistry;
import it.zerono.mods.extremereactors.api.reactor.ReactantMappingsRegistry;
import it.zerono.mods.extremereactors.api.reactor.radiation.EnergyConversion;
import it.zerono.mods.extremereactors.api.reactor.radiation.IRadiationModerator;
import it.zerono.mods.extremereactors.api.reactor.radiation.IrradiationData;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.FuelRodsLayout;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.IIrradiationSource;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.IReactorReader;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.ClientFuelRodsLayout;
import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.client.model.ReactorFuelRodModelData;
import it.zerono.mods.zerocore.lib.CodeHelper;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.PartPosition;
import it.zerono.mods.zerocore.lib.multiblock.validation.IMultiblockValidator;
import it.zerono.mods.zerocore.lib.world.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

public class ReactorFuelRodEntity
        extends AbstractReactorEntity
        implements IRadiationModerator, IHeatEntity, IIrradiationSource {

    public static final int FUEL_CAPACITY_PER_FUEL_ROD = 4 * ReactantMappingsRegistry.STANDARD_SOLID_REACTANT_AMOUNT; // 4 ingots per rod

    public ReactorFuelRodEntity() {

        super(Content.TileEntityTypes.REACTOR_FUELROD.get());
        this._controlRod = null;
        this._rodIndex = -1;
        this._occluded = false;
    }

    public double getHeatTransferRate() {

        final Direction.Plane fuelAssemblyPlane = CodeHelper.perpendicularPlane(this.getFuelRodsLayout().getAxis());
        final World world = this.getPartWorldOrFail();
        final BlockPos rodPosition = this.getPos();
        double heatTransferRate = 0d;

        for (Direction dir: fuelAssemblyPlane) {

            final BlockPos targetPosition = rodPosition.offset(dir);

            heatTransferRate += WorldHelper.getBlockState(world, targetPosition)
                    .map(state -> {

                        // Is it air ?

                        if (state.isAir(world, targetPosition)) {
                            return (double)IHeatEntity.CONDUCTIVITY_AIR;
                        }

                        // No, is it a tile entity or a moderator maybe?

                        return WorldHelper.getTile(world, targetPosition)
                                // We don't transfer to other fuel rods, due to heat pooling.
                                .filter(te -> !(te instanceof ReactorFuelRodEntity))
                                // Is it an IHeatEntity?
                                .filter(te -> te instanceof IHeatEntity)
                                .map(te -> (IHeatEntity)te)
                                .map(IHeatEntity::getThermalConductivity)
                                // If not, is it a moderator?
                                .orElse(this.getConductivityFromBlock(state));
                    })
                    .orElse(0d); // it's something very strange... ignore it
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
    protected IModelData getUpdatedModelData() {

        final FuelRodsLayout layout = this.getFuelRodsLayoutForRendering();

        if (layout instanceof ClientFuelRodsLayout) {

            final ClientFuelRodsLayout clientLayout = (ClientFuelRodsLayout)layout;
            final ClientFuelRodsLayout.FuelData fuelData = clientLayout.getFuelData(this.getFuelRodIndex());

            return ReactorFuelRodModelData.from(fuelData, this.isOccluded());
        }

        return EmptyModelData.INSTANCE;
    }

    public FuelRodsLayout getFuelRodsLayout() {
        return this.evalOnController(IReactorReader::getFuelRodsLayout, FuelRodsLayout.EMPTY);
    }

    public FuelRodsLayout getFuelRodsLayoutForRendering() {
        return this.isMachineAssembled() &&
                this.testOnController(it.zerono.mods.extremereactors.gamecontent.multiblock.common.AbstractMultiblockController::isInteriorVisible) ?
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

        // Fuel absorptiveness is determined by control rod + a heat modifier.
        // Starts at 1 and decays towards 0.05, reaching 0.6 at 1000 and just under 0.2 at 2000. Inflection point at about 500-600.
        // Harder radiation makes absorption more difficult.
        final float baseAbsorption = (float)(1.0 - (0.95 * Math.exp(-10 * Math.exp(-0.0022 * fuelHeat)))) *
                (1f - (radiation.hardness / this.getFuelHardnessDivisor()));

        // Some fuels are better at absorbing radiation than others
        final float scaledAbsorption = Math.min(1f, baseAbsorption * this.getFuelAbsorptionCoefficient());

        // Control rods increase total neutron absorption, but decrease the total neutrons which fertilize the fuel
        // Absorb up to 50% better with control rods inserted.
        final float controlRodBonus = (1f - scaledAbsorption) * controlRodInsertion * 0.5f;
        final float controlRodPenalty = scaledAbsorption * controlRodInsertion * 0.5f;

        final float radiationAbsorbed = (scaledAbsorption + controlRodBonus) * radiation.intensity;
        final float fertilityAbsorbed = (scaledAbsorption - controlRodPenalty) * radiation.intensity;

        float fuelModerationFactor = this.getFuelModerationFactor();

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

    //TODO Fuel Registry
    // 1, upwards. How well does this fuel moderate, but not stop, radiation? Anything under 1.5 is "poor", 2-2.5 is "good", above 4 is "excellent".
    private float getFuelModerationFactor() {
        return 1.5f;
    }

    //TODO Fuel Registry
    // 0..1. How well does this fuel absorb radiation?
    private float getFuelAbsorptionCoefficient() {
        // TODO: Lookup type of fuel and getValue data from there
        return 0.5f;
    }

    //TODO Fuel Registry
    // Goes up from 1. How tolerant is this fuel of hard radiation?
    private float getFuelHardnessDivisor() {
        return 1.0f;
    }

    private double getConductivityFromBlock(BlockState blockState) {
        return ModeratorsRegistry.getFrom(blockState).orElse(Moderator.AIR).getHeatConductivity();
    }

    private ReactorControlRodEntity _controlRod;
    private int _rodIndex;
    private boolean _occluded;

    //endregion
}
