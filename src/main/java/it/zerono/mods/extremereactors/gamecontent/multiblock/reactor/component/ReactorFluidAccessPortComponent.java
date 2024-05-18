package it.zerono.mods.extremereactors.gamecontent.multiblock.reactor.component;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.zerono.mods.extremereactors.gamecontent.Content;
import it.zerono.mods.zerocore.lib.data.IoDirection;
import it.zerono.mods.zerocore.lib.data.ModCodecs;
import it.zerono.mods.zerocore.lib.data.component.FluidTankComponent;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record ReactorFluidAccessPortComponent(IoDirection direction, FluidTankComponent fuel, FluidTankComponent waste) {

    public static final ModCodecs<ReactorFluidAccessPortComponent, RegistryFriendlyByteBuf> CODECS = new ModCodecs<>(
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            IoDirection.CODECS.field("direction", ReactorFluidAccessPortComponent::direction),
                            FluidTankComponent.CODECS.field("fuel", ReactorFluidAccessPortComponent::fuel),
                            FluidTankComponent.CODECS.field("waste", ReactorFluidAccessPortComponent::waste)
                    ).apply(instance, ReactorFluidAccessPortComponent::new)
            ),
            StreamCodec.composite(
                    IoDirection.CODECS.streamCodec(), ReactorFluidAccessPortComponent::direction,
                    FluidTankComponent.CODECS.streamCodec(), ReactorFluidAccessPortComponent::fuel,
                    FluidTankComponent.CODECS.streamCodec(), ReactorFluidAccessPortComponent::waste,
                    ReactorFluidAccessPortComponent::new
            )
    );

    public static DataComponentType<ReactorFluidAccessPortComponent> getComponentType() {
        return Content.DataComponents.REACTOR_FLUID_ACCESSPORT_COMPONENT_TYPE.get();
    }
}
