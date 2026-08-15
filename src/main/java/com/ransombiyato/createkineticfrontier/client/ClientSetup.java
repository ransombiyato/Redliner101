package com.ransombiyato.createkineticfrontier.client;

import com.ransombiyato.createkineticfrontier.registry.ModBlockEntities;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class ClientSetup {
    private ClientSetup() { }

    public static void init(IEventBus modBus) {
        modBus.addListener(ClientSetup::registerRenderers);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.KINETIC_CORE.get(), KineticMachineRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MOMENTUM_LAUNCHER.get(), KineticMachineRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.KINETIC_RAIL.get(), KineticMachineRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.GRAPPLE_WINCH.get(), KineticMachineRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.KINETIC_BRAKE.get(), KineticMachineRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.FLYWHEEL_ARRAY.get(), KineticMachineRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.KINETIC_CANNON.get(), KineticMachineRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.KINETIC_SENSOR.get(), KineticMachineRenderer::new);
    }
}
