package com.ransombiyato.createkineticfrontier.blockentity;

import com.ransombiyato.createkineticfrontier.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public final class KineticRailBlockEntity extends KineticMachineBlockEntity {
    public KineticRailBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KINETIC_RAIL.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, KineticRailBlockEntity rail) {
        boolean active = level.hasNeighborSignal(pos);
        if (active) {
            for (AbstractMinecart cart : level.getEntitiesOfClass(AbstractMinecart.class, new AABB(pos).inflate(0.85, 0.6, 0.85))) {
                var velocity = cart.getDeltaMovement();
                double speed = Math.min(1.45, velocity.length() + 0.09);
                var direction = velocity.lengthSqr() > 0.01 ? velocity.normalize() : rail.forward();
                cart.setDeltaMovement(direction.scale(speed));
                cart.setDeltaMovement(cart.getDeltaMovement().add(0, 0.015, 0));
                cart.hasImpulse = true;
                rail.setActivity(20);
            }
        } else if (rail.activity > 0) {
            rail.setActivity(rail.activity - 1);
        }
        if (level.getGameTime() % 10 == 0) rail.updateClients();
    }

    public String getMachineState() { return activity > 0 ? "accelerating" : powered() ? "powered" : "idle"; }
}
