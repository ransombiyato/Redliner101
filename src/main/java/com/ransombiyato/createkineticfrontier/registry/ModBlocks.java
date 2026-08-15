package com.ransombiyato.createkineticfrontier.registry;

import com.ransombiyato.createkineticfrontier.KineticFrontier;
import com.ransombiyato.createkineticfrontier.block.FlywheelArrayBlock;
import com.ransombiyato.createkineticfrontier.block.GrappleWinchBlock;
import com.ransombiyato.createkineticfrontier.block.KineticBrakeBlock;
import com.ransombiyato.createkineticfrontier.block.KineticCannonBlock;
import com.ransombiyato.createkineticfrontier.block.KineticCoreBlock;
import com.ransombiyato.createkineticfrontier.block.KineticRailBlock;
import com.ransombiyato.createkineticfrontier.block.KineticSensorBlock;
import com.ransombiyato.createkineticfrontier.block.MomentumLauncherBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.minecraft.core.registries.Registries;

public final class ModBlocks {
    public static final Blocks BLOCKS = DeferredRegister.createBlocks(KineticFrontier.MOD_ID);

    private static BlockBehaviour.Properties machineProperties(float strength) {
        return BlockBehaviour.Properties.of().strength(strength).sound(SoundType.METAL).requiresCorrectToolForDrops();
    }

    public static final DeferredBlock<Block> KINETIC_CORE = BLOCKS.register("kinetic_core", () -> new KineticCoreBlock(machineProperties(3.5f)));
    public static final DeferredBlock<Block> MOMENTUM_LAUNCHER = BLOCKS.register("momentum_launcher", () -> new MomentumLauncherBlock(machineProperties(3.5f)));
    public static final DeferredBlock<Block> KINETIC_RAIL = BLOCKS.register("kinetic_rail", () -> new KineticRailBlock(BlockBehaviour.Properties.of().strength(1.2f).sound(SoundType.METAL).noOcclusion()));
    public static final DeferredBlock<Block> GRAPPLE_WINCH = BLOCKS.register("grapple_winch", () -> new GrappleWinchBlock(machineProperties(3.5f)));
    public static final DeferredBlock<Block> KINETIC_BRAKE = BLOCKS.register("kinetic_brake", () -> new KineticBrakeBlock(machineProperties(3.0f)));
    public static final DeferredBlock<Block> FLYWHEEL_ARRAY = BLOCKS.register("flywheel_array", () -> new FlywheelArrayBlock(machineProperties(4.0f)));
    public static final DeferredBlock<Block> KINETIC_CANNON = BLOCKS.register("kinetic_cannon", () -> new KineticCannonBlock(machineProperties(5.0f)));
    public static final DeferredBlock<Block> KINETIC_SENSOR = BLOCKS.register("kinetic_sensor", () -> new KineticSensorBlock(machineProperties(2.5f)));

    private ModBlocks() { }
}
