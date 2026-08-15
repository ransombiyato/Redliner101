package com.ransombiyato.createkineticfrontier.blockentity;

import com.ransombiyato.createkineticfrontier.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class MomentumLauncherBlockEntity extends KineticMachineBlockEntity {
    private int charge;
    private static final int MAX_CHARGE = 40;

    public MomentumLauncherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOMENTUM_LAUNCHER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MomentumLauncherBlockEntity launcher) {
        if (launcher.cooldown > 0) launcher.cooldown--;
        if (level.hasNeighborSignal(pos) && launcher.energy >= 4) {
            launcher.energy -= 4;
            launcher.charge = Math.min(MAX_CHARGE, launcher.charge + 1);
            launcher.setActivity(launcher.charge);
            if (launcher.charge == MAX_CHARGE) {
                launcher.launchNearbyItems(2.25, 1.05);
                launcher.pushNearbyEntities(1.6, 0.42);
                launcher.charge = 0;
                launcher.cooldown = 12;
                launcher.mechanicalClick(1.65f);
                launcher.setActivity(0);
            }
        } else if (launcher.charge > 0) {
            launcher.charge = Math.max(0, launcher.charge - 1);
            launcher.setActivity(launcher.charge);
        }
        if (level.getGameTime() % 10 == 0) launcher.updateClients();
    }

    public int getCharge() { return charge; }
    public int getMaxCharge() { return MAX_CHARGE; }
    public String getMachineState() { return charge > 0 ? "charging" : cooldown > 0 ? "cooldown" : "ready"; }
}
