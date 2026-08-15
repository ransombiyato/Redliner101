package com.ransombiyato.createkineticfrontier.blockentity;

import com.ransombiyato.createkineticfrontier.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public final class KineticBrakeBlockEntity extends KineticMachineBlockEntity {
    public KineticBrakeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KINETIC_BRAKE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, KineticBrakeBlockEntity brake) {
        if (level.hasNeighborSignal(pos)) {
            for (AbstractMinecart cart : level.getEntitiesOfClass(AbstractMinecart.class, new AABB(pos).inflate(1.2))) {
                cart.setDeltaMovement(cart.getDeltaMovement().scale(0.72));
                cart.hasImpulse = true;
                brake.setActivity(20);
            }
            for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, new AABB(pos).inflate(1.2))) {
                item.setDeltaMovement(item.getDeltaMovement().scale(0.74));
            }
        } else if (brake.activity > 0) {
            brake.setActivity(brake.activity - 1);
        }
        if (level.getGameTime() % 20 == 0 && brake.activity > 0) brake.mechanicalClick(0.65f);
    }

    public String getMachineState() { return activity > 0 ? "braking" : "idle"; }
}
