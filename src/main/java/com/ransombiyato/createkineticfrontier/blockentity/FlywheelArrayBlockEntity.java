package com.ransombiyato.createkineticfrontier.blockentity;

import com.ransombiyato.createkineticfrontier.registry.ModBlockEntities;
import com.ransombiyato.createkineticfrontier.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class FlywheelArrayBlockEntity extends KineticMachineBlockEntity {
    public FlywheelArrayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLYWHEEL_ARRAY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FlywheelArrayBlockEntity array) {
        int connected = 1;
        for (var direction : net.minecraft.core.Direction.values()) {
            if (level.getBlockState(pos.relative(direction)).is(ModBlocks.FLYWHEEL_ARRAY.get())) connected++;
        }
        int capacity = connected * 1200;
        if (level.hasNeighborSignal(pos)) {
            array.energy = Math.min(capacity, array.energy + connected * 4);
            array.setActivity(Math.min(20, connected * 4));
        } else if (array.energy > 0) {
            array.energy = Math.max(0, array.energy - 1);
            array.setActivity(Math.max(0, array.activity - 1));
        }
        if (level.getGameTime() % 10 == 0) array.updateClients();
    }

    public int getConnectedFlywheels() {
        if (level == null) return 1;
        int count = 1;
        for (var direction : net.minecraft.core.Direction.values()) {
            if (level.getBlockState(worldPosition.relative(direction)).is(ModBlocks.FLYWHEEL_ARRAY.get())) count++;
        }
        return count;
    }

    public int getCapacity() { return getConnectedFlywheels() * 1200; }
    public int getStoredEnergy() { return energy; }
}
