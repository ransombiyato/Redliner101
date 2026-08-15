package com.ransombiyato.createkineticfrontier.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class MomentumHookItem extends Item {
    public MomentumHookItem(Properties properties) { super(properties); }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && !player.getCooldowns().isOnCooldown(this)) {
            HitResult hit = player.pick(18.0, 0.0f, false);
            if (hit instanceof BlockHitResult blockHit && hit.getType() == HitResult.Type.BLOCK) {
                Vec3 anchor = blockHit.getLocation();
                Vec3 pull = anchor.subtract(player.position().add(0, player.getBbHeight() * 0.5, 0));
                double distance = pull.length();
                if (distance > 1.5 && distance < 18.0) {
                    player.setDeltaMovement(player.getDeltaMovement().add(pull.normalize().scale(Math.min(1.15, 0.28 + distance * 0.035))));
                    player.hurtMarked = true;
                    player.getCooldowns().addCooldown(this, 24);
                    stack.hurtAndBreak(1, player, Player.getSlotForHand(hand));
                }
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
