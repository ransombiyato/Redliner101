package com.ransombiyato.createkineticfrontier.registry;

import com.ransombiyato.createkineticfrontier.KineticFrontier;
import com.ransombiyato.createkineticfrontier.blockentity.FlywheelArrayBlockEntity;
import com.ransombiyato.createkineticfrontier.blockentity.GrappleWinchBlockEntity;
import com.ransombiyato.createkineticfrontier.blockentity.KineticBrakeBlockEntity;
import com.ransombiyato.createkineticfrontier.blockentity.KineticCannonBlockEntity;
import com.ransombiyato.createkineticfrontier.blockentity.KineticCoreBlockEntity;
import com.ransombiyato.createkineticfrontier.blockentity.KineticRailBlockEntity;
import com.ransombiyato.createkineticfrontier.blockentity.KineticSensorBlockEntity;
import com.ransombiyato.createkineticfrontier.blockentity.MomentumLauncherBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.Registries;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, KineticFrontier.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticCoreBlockEntity>> KINETIC_CORE = BLOCK_ENTITIES.register("kinetic_core", () -> BlockEntityType.Builder.of(KineticCoreBlockEntity::new, ModBlocks.KINETIC_CORE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MomentumLauncherBlockEntity>> MOMENTUM_LAUNCHER = BLOCK_ENTITIES.register("momentum_launcher", () -> BlockEntityType.Builder.of(MomentumLauncherBlockEntity::new, ModBlocks.MOMENTUM_LAUNCHER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticRailBlockEntity>> KINETIC_RAIL = BLOCK_ENTITIES.register("kinetic_rail", () -> BlockEntityType.Builder.of(KineticRailBlockEntity::new, ModBlocks.KINETIC_RAIL.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GrappleWinchBlockEntity>> GRAPPLE_WINCH = BLOCK_ENTITIES.register("grapple_winch", () -> BlockEntityType.Builder.of(GrappleWinchBlockEntity::new, ModBlocks.GRAPPLE_WINCH.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticBrakeBlockEntity>> KINETIC_BRAKE = BLOCK_ENTITIES.register("kinetic_brake", () -> BlockEntityType.Builder.of(KineticBrakeBlockEntity::new, ModBlocks.KINETIC_BRAKE.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FlywheelArrayBlockEntity>> FLYWHEEL_ARRAY = BLOCK_ENTITIES.register("flywheel_array", () -> BlockEntityType.Builder.of(FlywheelArrayBlockEntity::new, ModBlocks.FLYWHEEL_ARRAY.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticCannonBlockEntity>> KINETIC_CANNON = BLOCK_ENTITIES.register("kinetic_cannon", () -> BlockEntityType.Builder.of(KineticCannonBlockEntity::new, ModBlocks.KINETIC_CANNON.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KineticSensorBlockEntity>> KINETIC_SENSOR = BLOCK_ENTITIES.register("kinetic_sensor", () -> BlockEntityType.Builder.of(KineticSensorBlockEntity::new, ModBlocks.KINETIC_SENSOR.get()).build(null));

    private ModBlockEntities() { }
}
