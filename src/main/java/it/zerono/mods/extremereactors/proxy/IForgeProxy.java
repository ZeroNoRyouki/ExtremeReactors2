package it.zerono.mods.extremereactors.proxy;

import net.neoforged.bus.api.IEventBus;

public interface IForgeProxy
        extends IProxy {

    void initialize(IEventBus modEventBus);
}
