package com.ransombiyato.createkineticfrontier.blockentity;

import com.ransombiyato.createkineticfrontier.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class KineticCoreBlockEntity extends KineticMachineBlockEntity {
    public static final int CAPACITY = 5000;

    public KineticCoreBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KINETIC_CORE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, KineticCoreBlockEntity core) {
        if (core.cooldown > 0) core.cooldown--;
        boolean input = level.hasNeighborSignal(pos);
        if (input && core.energy < CAPACITY) {
            core.energy = Math.min(CAPACITY, core.energy + 10);
            core.setActivity(Math.min(20, core.activity + 2));
            if (level.getGameTime() % 20 == 0) core.mechanicalClick(1.15f);
        } else if (!input && core.activity > 0) {
            core.setActivity(core.activity - 1);
        }

        BlockEntity target = level.getBlockEntity(pos.relative(core.facing()));
        if (!input && target instanceof KineticMachineBlockEntity machine && core.energy > 0) {
            int transfer = Math.min(8, core.energy);
            core.energy -= transfer;
            machine.energy = Math.min(5000, machine.energy + transfer);
            core.setActivity(Math.min(20, core.activity + 1));
        }
        if (level.getGameTime() % 10 == 0) core.updateClients();
    }

    public int getStoredEnergy() { return energy; }
    public int getCapacity() { return CAPACITY; }
    public String getMachineState() { return energy >= CAPACITY ? "charged" : energy > 0 ? "charging" : "idle"; }
}
