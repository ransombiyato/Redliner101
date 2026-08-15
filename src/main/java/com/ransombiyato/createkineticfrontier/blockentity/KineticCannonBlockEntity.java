package com.ransombiyato.createkineticfrontier.blockentity;

import com.ransombiyato.createkineticfrontier.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class KineticCannonBlockEntity extends KineticMachineBlockEntity {
    private int phase;

    public KineticCannonBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KINETIC_CANNON.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, KineticCannonBlockEntity cannon) {
        if (cannon.cooldown > 0) cannon.cooldown--;
        if (level.hasNeighborSignal(pos) && cannon.cooldown == 0) {
            cannon.energy = Math.min(2500, cannon.energy + 6);
            cannon.phase++;
            cannon.setActivity(cannon.phase);
            if (cannon.phase == 20 || cannon.phase == 50) cannon.mechanicalClick(0.8f + cannon.phase / 100f);
            if (cannon.phase >= 80) {
                cannon.launchNearbyItems(2.6, 1.8);
                cannon.pushNearbyEntities(2.0, 0.65);
                cannon.phase = 0;
                cannon.cooldown = 80;
                cannon.energy = Math.max(0, cannon.energy - 400);
                cannon.mechanicalClick(1.95f);
                cannon.setActivity(0);
            }
        } else if (cannon.phase > 0 && cannon.cooldown == 0) {
            cannon.phase = Math.max(0, cannon.phase - 2);
            cannon.setActivity(cannon.phase);
        }
        if (level.getGameTime() % 10 == 0) cannon.updateClients();
    }

    public int getPhase() { return phase; }
    public String getMachineState() { return cooldown > 0 ? "cooldown" : phase > 0 ? "preparing" : "idle"; }
}
