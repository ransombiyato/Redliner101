package com.ransombiyato.createkineticfrontier.blockentity;

import com.ransombiyato.createkineticfrontier.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;

public final class KineticSensorBlockEntity extends KineticMachineBlockEntity {
    public KineticSensorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KINETIC_SENSOR.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, KineticSensorBlockEntity sensor) {
        boolean detected = false;
        for (Entity entity : level.getEntities((Entity) null, new AABB(pos).inflate(4.0), Entity::isAlive)) {
            if (candidateSpeed(entity) > 0.35) {
                detected = true;
                break;
            }
        }
        if (state.hasProperty(BlockStateProperties.POWERED) && state.getValue(BlockStateProperties.POWERED) != detected) {
            level.setBlock(pos, state.setValue(BlockStateProperties.POWERED, detected), 3);
            level.updateNeighborsAt(pos, state.getBlock());
            sensor.setActivity(detected ? 20 : 0);
        }
        if (level.getGameTime() % 10 == 0) sensor.updateClients();
    }

    private static double candidateSpeed(Entity entity) {
        return entity.getDeltaMovement().length();
    }

    public boolean isDetecting() { return activity > 0; }
}
