package it.zerono.mods.extremereactors.gamecontent.mekanism;

import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity;
import it.zerono.mods.zerocore.base.multiblock.part.io.fluid.IFluidPort;
import it.zerono.mods.zerocore.base.multiblock.part.io.fluid.IFluidPortHandler;
import it.zerono.mods.zerocore.lib.compat.ModDependencyServiceLoader;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public interface IMekanismService {

    ModDependencyServiceLoader<IMekanismService> SERVICE = new ModDependencyServiceLoader<>("mekanism",
            IMekanismService.class, FallbackIMekanismService::new);

    <Controller extends AbstractCuboidMultiblockController<Controller>,
            T extends AbstractMultiblockEntity<Controller> & IFluidPort>
    IFluidPortHandler createHandler(IoMode mode, T part);

    <BE extends BlockEntity & IFluidPort> void registerGasCapabilityProvider(RegisterCapabilitiesEvent event,
                                                                             BlockEntityType<BE> blockEntityType);
}
