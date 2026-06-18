package com.ransombiyato.redliner.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.entity.LivingEntity;

public class RedlinerKatana extends SwordItem {
    private static final int MAX_DASH_CHARGES = 3;
    private static final float DASH_SPEED = 2.0f;
    private static final float BASE_SLASH_DAMAGE = 8.0f;
    private static final float BASH_DAMAGE = 5.0f;
    private static final int DASH_RECHARGE_TICKS = 200; // 10 seconds

    public RedlinerKatana(ToolMaterial toolMaterial, int attackDamage, float attackSpeed) {
        super(toolMaterial, attackDamage, attackSpeed);
    }

    /**
     * Calculate slash damage based on player movement speed
     */
    public float getScaledSlashDamage(Player player) {
        float speed = (float) Math.sqrt(player.getDeltaMovement().lengthSqr());
        return BASE_SLASH_DAMAGE + (speed * 2.0f);
    }

    /**
     * Calculate bash damage based on player movement speed
     */
    public float getScaledBashDamage(Player player) {
        float speed = (float) Math.sqrt(player.getDeltaMovement().lengthSqr());
        return BASH_DAMAGE + (speed * 1.5f);
    }

    /**
     * Get glow intensity based on speed (0.0 to 1.0)
     */
    public float getGlowIntensity(Player player) {
        float speed = (float) Math.sqrt(player.getDeltaMovement().lengthSqr());
        return Math.min(speed / 2.0f, 1.0f);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // TODO: Implement speed-scaled damage
        return super.hurtEnemy(stack, target, attacker);
    }
}