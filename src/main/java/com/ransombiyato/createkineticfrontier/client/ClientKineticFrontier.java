package com.ransombiyato.createkineticfrontier.client;

import com.ransombiyato.createkineticfrontier.KineticFrontier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = KineticFrontier.MOD_ID, dist = Dist.CLIENT)
public final class ClientKineticFrontier {
    public ClientKineticFrontier(IEventBus modBus) {
        ClientSetup.init(modBus);
    }
}
