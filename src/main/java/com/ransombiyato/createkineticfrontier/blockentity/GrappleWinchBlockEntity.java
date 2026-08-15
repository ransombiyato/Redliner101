package com.ransombiyato.createkineticfrontier.blockentity;

import com.ransombiyato.createkineticfrontier.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class GrappleWinchBlockEntity extends KineticMachineBlockEntity {
    private static final double RANGE = 12.0;
    @Nullable private BlockPos anchor;

    public GrappleWinchBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GRAPPLE_WINCH.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GrappleWinchBlockEntity winch) {
        if (!level.hasNeighborSignal(pos)) {
            if (winch.anchor != null && level.getGameTime() % 10 == 0) {
                winch.anchor = null;
                winch.setActivity(0);
                winch.updateClients();
            }
            return;
        }

        LivingEntity target = null;
        double nearest = RANGE * RANGE;
        for (LivingEntity candidate : level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(RANGE))) {
            double distance = candidate.distanceToSqr(Vec3.atCenterOf(pos));
            if (distance < nearest && candidate.isAlive()) {
                target = candidate;
                nearest = distance;
            }
        }
        if (target != null) {
            winch.anchor = target.blockPosition();
            Vec3 pull = Vec3.atCenterOf(pos).subtract(target.position());
            if (pull.lengthSqr() > 0.5) {
                target.setDeltaMovement(target.getDeltaMovement().add(pull.normalize().scale(0.08)));
                target.hurtMarked = true;
            }
            winch.setActivity(20);
        }
        if (level.getGameTime() % 10 == 0) winch.updateClients();
    }

    @Nullable public BlockPos getAnchor() { return anchor; }
    public boolean hasAnchor() { return anchor != null; }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (anchor != null) tag.putLong("Anchor", anchor.asLong());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        anchor = tag.contains("Anchor") ? BlockPos.of(tag.getLong("Anchor")) : null;
    }
}
