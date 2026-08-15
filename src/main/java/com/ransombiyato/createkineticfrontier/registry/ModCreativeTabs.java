package com.ransombiyato.createkineticfrontier.registry;

import com.ransombiyato.createkineticfrontier.KineticFrontier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.Registries;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, KineticFrontier.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> KINETIC_FRONTIER_TAB = TABS.register("kinetic_frontier", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.createkineticfrontier.kinetic_frontier"))
            .icon(() -> new ItemStack(ModItems.MOMENTUM_METER.get()))
            .displayItems((params, output) -> {
                output.accept(ModItems.MOMENTUM_HOOK.get());
                output.accept(ModItems.MOMENTUM_METER.get());
                output.accept(ModItems.KINETIC_CORE.get());
                output.accept(ModItems.MOMENTUM_LAUNCHER.get());
                output.accept(ModItems.KINETIC_RAIL.get());
                output.accept(ModItems.GRAPPLE_WINCH.get());
                output.accept(ModItems.KINETIC_BRAKE.get());
                output.accept(ModItems.FLYWHEEL_ARRAY.get());
                output.accept(ModItems.KINETIC_CANNON.get());
                output.accept(ModItems.KINETIC_SENSOR.get());
                output.accept(ModItems.CARGO_CAPSULE.get());
                output.accept(ModItems.IMPACT_CAPSULE.get());
                output.accept(ModItems.SIGNAL_CAPSULE.get());
                output.accept(ModItems.UTILITY_CAPSULE.get());
            }).build());

    private ModCreativeTabs() { }
}
