package com.ransombiyato.createkineticfrontier;

import com.ransombiyato.createkineticfrontier.registry.ModBlockEntities;
import com.ransombiyato.createkineticfrontier.registry.ModBlocks;
import com.ransombiyato.createkineticfrontier.registry.ModCreativeTabs;
import com.ransombiyato.createkineticfrontier.registry.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(KineticFrontier.MOD_ID)
public final class KineticFrontier {
    public static final String MOD_ID = "createkineticfrontier";

    public KineticFrontier(IEventBus modBus) {
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
    }
}
