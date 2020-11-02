//package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor;
//
//import it.zerono.mods.extremereactors.api.radiation.RadiationPacket;
//import it.zerono.mods.extremereactors.api.reactor.Moderator;
//import it.zerono.mods.extremereactors.api.reactor.ModeratorsRegistry;
//import it.zerono.mods.extremereactors.api.reactor.radiation.EnergyConversion;
//import it.zerono.mods.extremereactors.api.reactor.radiation.IRadiationModerator;
//import it.zerono.mods.extremereactors.api.reactor.radiation.IrradiationData;
//import it.zerono.mods.extremereactors.config.Config;
//import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorControlRodEntity;
//import it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.part.ReactorFuelRodEntity;
//import it.zerono.mods.zerocore.lib.data.nbt.IMergeableEntity;
//import it.zerono.mods.zerocore.lib.data.nbt.ISyncableEntity;
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.Blocks;
//import net.minecraft.fluid.Fluid;
//import net.minecraft.nbt.CompoundNBT;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.Direction;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.World;
//import net.minecraftforge.fluids.IFluidBlock;
//
//import java.util.Optional;
//
//public class RadiationHelper implements ISyncableEntity, IMergeableEntity {
//
//    public RadiationHelper() {
//        this.setFertility(1f);
//    }
//
//    /*
//    only used by Reactor UPDATE
//
//    source (a fuel rod) used for:
//    - get it's control rod
//    - get the starting (the fuel rod's) position in the world
//
//    the source controlRod is used for:
//    - get it's insertion rate
//    - get the fuel rods layout from the reactor controller
//
//    world used for:
//    - passed to performIrradiation()
//     */
//    public Optional<IrradiationData> radiate(World world, FuelContainer fuelContainer, ReactorFuelRodEntity source,
//                                             double fuelHeat, double environmentHeat, int numControlRods) {
//
//        final ReactorControlRodEntity controlRod = source.getControlRod().orElseThrow(IllegalStateException::new);
//
//        // No fuel? No radiation!
//        if (fuelContainer.getFuelAmount() <= 0) {
//            return Optional.empty();
//        }
//
//        // Determine radiation amount & intensity, heat amount, determine fuel usage
//        final IrradiationData data = new IrradiationData();
//
//        data.fuelAbsorbedRadiation = 0f;
//
//        // Base value for radiation production penalties. 0-1, caps at about 3000C;
//        double radiationPenaltyBase = Math.exp(-15 * Math.exp(-0.0025 * fuelHeat));
//
//        // Raw amount - what's actually in the tanks
//        // Effective amount - how
//        int baseFuelAmount = fuelContainer.getFuelAmount() + (fuelContainer.getWasteAmount() / 100);
//        float fuelReactivity = fuelContainer.getFuelReactivity();
//
//        // Intensity = how strong the radiation is, hardness = how energetic the radiation is (penetration)
//        float rawRadIntensity = (float)baseFuelAmount * FISSION_EVENTS_PER_FUEL_UNIT;
//
//        // Scale up the "effective" intensity of radiation, to provide an incentive for bigger reactors in general.
//        float scaledRadIntensity = (float)Math.pow(rawRadIntensity, fuelReactivity);
//
//        // Scale up a second time based on scaled amount in each fuel rod. Provides an incentive for making reactors that aren't just pancakes.
//        scaledRadIntensity = (float)Math.pow(scaledRadIntensity / numControlRods, fuelReactivity) * numControlRods;
//
//        // Apply control rod moderation of radiation to the quantity of produced radiation. 100% insertion = 100% reduction.
//        float controlRodModifier = (float)(100 - controlRod.getInsertionRatio()) / 100f;
//
//        scaledRadIntensity = scaledRadIntensity * controlRodModifier;
//        rawRadIntensity = rawRadIntensity * controlRodModifier;
//
//        // Now nerf actual radiation production based on heat.
//        float effectiveRadIntensity = scaledRadIntensity * (1f + (float)(-0.95f * Math.exp(-10f * Math.exp(-0.0012f * fuelHeat))));
//
//        // Radiation hardness starts at 20% and asymptotically approaches 100% as heat rises.
//        // This will make radiation harder and harder to capture.
//        float radHardness = 0.2f + (float)(0.8 * radiationPenaltyBase);
//
//        // Calculate based on propagation-to-self
//
//        final float rawFuelUsage = (FUEL_PER_RADIATION_UNIT * rawRadIntensity / getFertilityModifier()) *
//                Config.COMMON.general.fuelUsageMultiplier.get().floatValue(); // Not a typo. Fuel usage is thus penalized at high heats.
//
//        data.fuelEnergyAbsorption = EnergyConversion.ENERGY_PER_RADIATION_UNIT * effectiveRadIntensity;
//        data.environmentEnergyAbsorption = 0f;
//
//        // Propagate radiation to others
//        BlockPos originCoord = source.getWorldPosition();
//        BlockPos currentCoord;
//
//        effectiveRadIntensity *= 0.25f; // We're going to do this four times, no need to repeat
//
//        final RadiationPacket radPacket = new RadiationPacket();
//        final FuelRodsLayout layout = controlRod.getMultiblockController()
//                .flatMap(MultiblockReactor::getFuelRodsLayout)
//                .orElseThrow(IllegalStateException::new);
//
//        for (Direction dir : layout.getRadiateDirections()) {
//
//            radPacket.hardness = radHardness;
//            radPacket.intensity = effectiveRadIntensity;
//
//            int ttl = 4; //TODO variants?
//
//            currentCoord = originCoord;
//
//            while (ttl > 0 && radPacket.intensity > 0.0001f) {
//
//                ttl--;
//                currentCoord = currentCoord.offset(dir);
//                this.performIrradiation(world, data, radPacket, currentCoord);
//            }
//        }
//
//        // Apply changes
//        this._fertility += data.fuelAbsorbedRadiation;
//        data.fuelAbsorbedRadiation = 0f;
//
//        // Inform fuelContainer
//        fuelContainer.onIrradiation(rawFuelUsage);
//        data.fuelUsage = rawFuelUsage;
//
//        return Optional.of(data);
//    }
//
//    // only used by Reactor UPDATE
//    public void update(final boolean isReactorActive) {
//
//        float denominator = 20f;
//
//        if (!isReactorActive) {
//            // Much slower decay when off
//            denominator *= 200f;
//        }
//
//        // Fertility decay, at least 0.1 rad/t, otherwise halve it every 10 ticks
//
//        this._fertility = Math.max(0f, this._fertility - Math.max(0.1f, this._fertility / denominator));
//    }
//
//    // only used to save state
//    public float getFertility() {
//        return this._fertility;
//    }
//
//    //region ISyncableEntity
//
//    /**
//     * Sync the entity data from the given NBT compound
//     *
//     * @param data       the data
//     * @param syncReason the reason why the synchronization is necessary
//     */
//    @Override
//    public void syncDataFrom(CompoundNBT data, SyncReason syncReason) {
//
//        if (data.contains("fertility")) {
//            this.setFertility(data.getFloat("fertility"));
//        }
//    }
//
//    /**
//     * Sync the entity data to the given NBT compound
//     *
//     * @param data       the data
//     * @param syncReason the reason why the synchronization is necessary
//     */
//    @Override
//    public CompoundNBT syncDataTo(CompoundNBT data, SyncReason syncReason) {
//
//        data.putFloat("fertility", this._fertility);
//        return data;
//    }
//
//    //region IMergeableEntity
//
//    /**
//     * Sync the entity data from another IMergeableEntity
//     *
//     * @param other the IMergeableEntity to sync from
//     */
//    @Override
//    public void syncDataFrom(IMergeableEntity other) {
//
//        if (other instanceof RadiationHelper) {
//            this._fertility = Math.max(this._fertility, ((RadiationHelper)other).getFertility());
//        }
//    }
//
//    //region internals
//
//    /*
//    world is used for:
//    - generally speaking, retriving a block/fluid/te from the world (isAirBlock, getTileEntity, getBlockState, block.isAir)
//     */
//    private void performIrradiation(World world, IrradiationData data, RadiationPacket radiation, BlockPos position) {
//
//        if (world.isAirBlock(position)) {
//
//            moderateByAir(data, radiation);
//
//        } else {
//
//            final TileEntity te = world.getTileEntity(position);
//
//            if (te instanceof IRadiationModerator) {
//
//                ((IRadiationModerator)te).moderateRadiation(data, radiation);
//
//            } else {
//
//                BlockState blockState = world.getBlockState(position);
//                Block block = blockState.getBlock();
//
//                if (block.isAir(blockState, world, position)) {
//
//                    moderateByAir(data, radiation);
//
//                } else if(block instanceof IFluidBlock) {
//                    //TODO fluids
//                    moderateByFluid(data, radiation, ((IFluidBlock)block).getFluid());
//
//                } else {
//                    // Go by block
//                    moderateByBlock(data, radiation, blockState);
//                }
//
//                // Do it based on fluid?
//            }
//        }
//    }
//
//    private static void moderateByAir(IrradiationData data, RadiationPacket radiation) {
//        applyModerationFactors(data, radiation, Moderator.AIR);
//    }
//
//    private static void moderateByBlock(IrradiationData data, RadiationPacket radiation, BlockState blockState) {
//
//        Moderator moderator = null;
//        final Block block = blockState.getBlock();
//
//        //TODO fluids
//        //TODO how to handle water?
//        if (block == Blocks.WATER /*|| block == Blocks.FLOWING_WATER*/) { //TODO ignore flowing water?
//            moderator = Moderator.WATER;
//        } else {
//            moderator = ModeratorsRegistry.getFromSolid(block).orElse(Moderator.AIR);
//        }
//
//        applyModerationFactors(data, radiation, moderator);
//    }
//
//    //TODO fluids
//    private static void moderateByFluid(IrradiationData data, RadiationPacket radiation, Fluid fluid) {
//
//        Moderator moderator = null; //ModeratorsRegistry.getFluidData(fluid.getName()); //TODO fluids
//
//        if (null == moderator) {
//            moderator = Moderator.WATER;
//        }
//
//        applyModerationFactors(data, radiation, moderator);
//    }
//
//    private static void applyModerationFactors(IrradiationData data, RadiationPacket radiation, Moderator moderator) {
//
//        final float radiationAbsorbed = radiation.intensity * moderator.getAbsorption() * (1f - radiation.hardness);
//
//        radiation.intensity = Math.max(0f, radiation.intensity - radiationAbsorbed);
//        radiation.hardness /= moderator.getModeration();
//        data.environmentEnergyAbsorption += moderator.getHeatEfficiency() * radiationAbsorbed * EnergyConversion.ENERGY_PER_RADIATION_UNIT;
//    }
//
//    private float getFertilityModifier() {
//
//        if (this._fertility <= 1f) {
//            return 1f;
//        } else {
//            return (float)(Math.log10(this._fertility) + 1);
//        }
//    }
//
//    private void setFertility(float newFertility) {
//
//        if (Float.isNaN(newFertility) || Float.isInfinite(newFertility)) {
//            this._fertility = 1f;
//        } else if (newFertility < 0f) {
//            this._fertility = 0f;
//        } else {
//            this._fertility = newFertility;
//        }
//    }
//
//    // fuel units used per fission event
//    private static final float FUEL_PER_RADIATION_UNIT = 0.0007f;
//
//    // 1 fission event per 100 mB
//    private static final float FISSION_EVENTS_PER_FUEL_UNIT = 0.01f;
//
//    private float _fertility;
//
//    //endregion
//}
