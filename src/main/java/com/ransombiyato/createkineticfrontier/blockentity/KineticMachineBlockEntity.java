package com.ransombiyato.createkineticfrontier.blockentity;

import com.ransombiyato.createkineticfrontier.block.KineticMachineBlock;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.List;

public abstract class KineticMachineBlockEntity extends BlockEntity implements IHaveGoggleInformation {
    protected int energy;
    protected int cooldown;
    protected int activity;

    protected KineticMachineBlockEntity(net.minecraft.world.level.block.entity.BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected boolean powered() {
        return level != null && level.hasNeighborSignal(worldPosition);
    }

    protected Direction facing() {
        return getBlockState().hasProperty(KineticMachineBlock.FACING)
                ? getBlockState().getValue(KineticMachineBlock.FACING) : Direction.NORTH;
    }

    protected Vec3 forward() {
        return Vec3.atLowerCornerOf(facing().getNormal());
    }

    protected void updateClients() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    protected void mechanicalClick(float pitch) {
        if (level != null && !level.isClientSide) {
            level.playSound(null, worldPosition, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.35f, pitch);
        }
    }

    protected void launchEntity(Entity entity, double strength) {
        Vec3 impulse = forward().scale(strength).add(0, 0.12, 0);
        entity.setDeltaMovement(entity.getDeltaMovement().add(impulse));
        entity.hurtMarked = true;
    }

    protected void launchNearbyItems(double radius, double strength) {
        if (level == null) return;
        AABB box = new AABB(worldPosition).inflate(radius);
        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, box)) {
            launchEntity(item, strength);
        }
    }

    protected void pushNearbyEntities(double radius, double strength) {
        if (level == null) return;
        AABB box = new AABB(worldPosition).inflate(radius);
        for (Entity entity : level.getEntities((Entity) null, box, Entity::isPushable)) {
            Vec3 delta = entity.position().subtract(Vec3.atCenterOf(worldPosition));
            if (delta.lengthSqr() < 0.01) delta = forward();
            entity.setDeltaMovement(entity.getDeltaMovement().add(delta.normalize().scale(strength)));
            entity.hurtMarked = true;
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        tooltip.add(Component.literal("Kinetic Frontier"));
        tooltip.add(Component.literal("State: " + getMachineState()));
        tooltip.add(Component.literal("Energy: " + energy + " | Activity: " + activity));
        return true;
    }

    public int getEnergy() { return energy; }
    public int getActivity() { return activity; }
    public int getCooldown() { return cooldown; }
    public String getMachineState() { return activity > 0 ? "active" : cooldown > 0 ? "cooldown" : "idle"; }

    protected void setActivity(int value) {
        int next = Math.max(0, value);
        if (activity != next) {
            activity = next;
            updateClients();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Energy", energy);
        tag.putInt("Cooldown", cooldown);
        tag.putInt("Activity", activity);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energy = tag.getInt("Energy");
        cooldown = tag.getInt("Cooldown");
        activity = tag.getInt("Activity");
    }
}
