package com.ransombiyato.createkineticfrontier.item;

import com.ransombiyato.createkineticfrontier.blockentity.FlywheelArrayBlockEntity;
import com.ransombiyato.createkineticfrontier.blockentity.KineticCoreBlockEntity;
import com.ransombiyato.createkineticfrontier.blockentity.KineticMachineBlockEntity;
import com.ransombiyato.createkineticfrontier.blockentity.MomentumLauncherBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class MomentumMeterItem extends Item {
    public MomentumMeterItem(Properties properties) { super(properties); }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockEntity blockEntity = context.getLevel().getBlockEntity(context.getClickedPos());
        if (context.getPlayer() != null && blockEntity instanceof KineticMachineBlockEntity machine) {
            String detail = "State: " + machine.getMachineState() + " | Energy: " + machine.getEnergy() + " | Activity: " + machine.getActivity();
            if (machine instanceof KineticCoreBlockEntity core) detail += " / " + core.getCapacity() + " capacity";
            if (machine instanceof FlywheelArrayBlockEntity array) detail += " / " + array.getConnectedFlywheels() + " flywheels";
            if (machine instanceof MomentumLauncherBlockEntity launcher) detail += " / charge " + launcher.getCharge() + "/" + launcher.getMaxCharge();
            context.getPlayer().displayClientMessage(Component.literal(detail), true);
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
        }
        return InteractionResult.PASS;
    }
}
