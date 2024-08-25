package it.zerono.mods.extremereactors.gamecontent.mekanism;

import it.zerono.mods.extremereactors.gamecontent.multiblock.common.part.fluidport.FluidPortHandlerMekanism;
import it.zerono.mods.zerocore.base.multiblock.part.AbstractMultiblockEntity;
import it.zerono.mods.zerocore.base.multiblock.part.io.fluid.IFluidPort;
import it.zerono.mods.zerocore.base.multiblock.part.io.fluid.IFluidPortHandler;
import it.zerono.mods.zerocore.lib.data.IoMode;
import it.zerono.mods.zerocore.lib.multiblock.cuboid.AbstractCuboidMultiblockController;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class MekanismService
        implements IMekanismService {

    @Override
    public <Controller extends AbstractCuboidMultiblockController<Controller>,
            T extends AbstractMultiblockEntity<Controller> & IFluidPort> IFluidPortHandler createHandler(IoMode mode,
                                                                                                         T part) {
        return new FluidPortHandlerMekanism<>(part);
    }

    @Override
    public <BE extends BlockEntity & IFluidPort> void registerGasCapabilityProvider(RegisterCapabilitiesEvent event,
                                                                                    BlockEntityType<BE> blockEntityType) {
        event.registerBlockEntity(Capabilities.CHEMICAL.block(), blockEntityType,
                (be, context) -> be.getFluidPortHandler() instanceof IChemicalHandler handler ? handler : null);
    }
}
