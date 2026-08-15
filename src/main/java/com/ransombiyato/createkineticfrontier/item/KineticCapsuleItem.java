package com.ransombiyato.createkineticfrontier.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class KineticCapsuleItem extends Item {
    public enum Type { CARGO, IMPACT, SIGNAL, UTILITY }

    private final Type type;

    public KineticCapsuleItem(Type type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public Type type() { return type; }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (!level.isClientSide && !player.getCooldowns().isOnCooldown(this)) {
            ItemStack payload = held.copyWithCount(1);
            ItemEntity projectile = new ItemEntity(level, player.getX(), player.getEyeY() - 0.15, player.getZ(), payload);
            projectile.setPickUpDelay(25);
            projectile.setDeltaMovement(player.getLookAngle().scale(type == Type.IMPACT ? 1.45 : 1.15));
            level.addFreshEntity(projectile);
            held.shrink(1);
            player.getCooldowns().addCooldown(this, 12);
        }
        return InteractionResultHolder.sidedSuccess(held, level.isClientSide());
    }
}
