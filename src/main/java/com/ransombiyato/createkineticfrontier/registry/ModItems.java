package com.ransombiyato.createkineticfrontier.registry;

import com.ransombiyato.createkineticfrontier.KineticFrontier;
import com.ransombiyato.createkineticfrontier.item.KineticCapsuleItem;
import com.ransombiyato.createkineticfrontier.item.KineticBlockItem;
import com.ransombiyato.createkineticfrontier.item.MomentumHookItem;
import com.ransombiyato.createkineticfrontier.item.MomentumMeterItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(KineticFrontier.MOD_ID);

    public static final DeferredItem<Item> KINETIC_CORE = ITEMS.register("kinetic_core", () -> new KineticBlockItem(ModBlocks.KINETIC_CORE.get(), new Item.Properties(), "tooltip.createkineticfrontier.kinetic_core"));
    public static final DeferredItem<Item> MOMENTUM_LAUNCHER = ITEMS.register("momentum_launcher", () -> new KineticBlockItem(ModBlocks.MOMENTUM_LAUNCHER.get(), new Item.Properties(), "tooltip.createkineticfrontier.momentum_launcher"));
    public static final DeferredItem<Item> KINETIC_RAIL = ITEMS.register("kinetic_rail", () -> new KineticBlockItem(ModBlocks.KINETIC_RAIL.get(), new Item.Properties(), "tooltip.createkineticfrontier.kinetic_rail"));
    public static final DeferredItem<Item> GRAPPLE_WINCH = ITEMS.register("grapple_winch", () -> new KineticBlockItem(ModBlocks.GRAPPLE_WINCH.get(), new Item.Properties(), "tooltip.createkineticfrontier.grapple_winch"));
    public static final DeferredItem<Item> KINETIC_BRAKE = ITEMS.register("kinetic_brake", () -> new KineticBlockItem(ModBlocks.KINETIC_BRAKE.get(), new Item.Properties(), "tooltip.createkineticfrontier.kinetic_brake"));
    public static final DeferredItem<Item> FLYWHEEL_ARRAY = ITEMS.register("flywheel_array", () -> new KineticBlockItem(ModBlocks.FLYWHEEL_ARRAY.get(), new Item.Properties(), "tooltip.createkineticfrontier.flywheel_array"));
    public static final DeferredItem<Item> KINETIC_CANNON = ITEMS.register("kinetic_cannon", () -> new KineticBlockItem(ModBlocks.KINETIC_CANNON.get(), new Item.Properties(), "tooltip.createkineticfrontier.kinetic_cannon"));
    public static final DeferredItem<Item> KINETIC_SENSOR = ITEMS.register("kinetic_sensor", () -> new KineticBlockItem(ModBlocks.KINETIC_SENSOR.get(), new Item.Properties(), "tooltip.createkineticfrontier.kinetic_sensor"));

    public static final DeferredItem<Item> MOMENTUM_HOOK = ITEMS.register("momentum_hook", () -> new MomentumHookItem(new Item.Properties().stacksTo(1).durability(256)));
    public static final DeferredItem<Item> MOMENTUM_METER = ITEMS.register("momentum_meter", () -> new MomentumMeterItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<Item> CARGO_CAPSULE = ITEMS.register("cargo_capsule", () -> new KineticCapsuleItem(KineticCapsuleItem.Type.CARGO, new Item.Properties().stacksTo(16)));
    public static final DeferredItem<Item> IMPACT_CAPSULE = ITEMS.register("impact_capsule", () -> new KineticCapsuleItem(KineticCapsuleItem.Type.IMPACT, new Item.Properties().stacksTo(16)));
    public static final DeferredItem<Item> SIGNAL_CAPSULE = ITEMS.register("signal_capsule", () -> new KineticCapsuleItem(KineticCapsuleItem.Type.SIGNAL, new Item.Properties().stacksTo(16)));
    public static final DeferredItem<Item> UTILITY_CAPSULE = ITEMS.register("utility_capsule", () -> new KineticCapsuleItem(KineticCapsuleItem.Type.UTILITY, new Item.Properties().stacksTo(16)));

    private ModItems() { }
}
