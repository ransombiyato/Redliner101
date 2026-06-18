package com.ransombiyato.redliner.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

@OnlyIn(Dist.CLIENT)
public class DashHUD {
    private static final int MAX_CHARGES = 3;
    private int currentCharges = MAX_CHARGES;
    private int chargeRechargeTimer = 0;
    private static final int RECHARGE_COOLDOWN = 200; // 10 seconds between charges

    public void updateCharges() {
        if (currentCharges < MAX_CHARGES) {
            chargeRechargeTimer++;
            if (chargeRechargeTimer >= RECHARGE_COOLDOWN) {
                currentCharges++;
                chargeRechargeTimer = 0;
            }
        }
    }

    public void useDashCharge() {
        if (currentCharges > 0) {
            currentCharges--;
            chargeRechargeTimer = 0;
        }
    }

    public void restoreCharge() {
        if (currentCharges < MAX_CHARGES) {
            currentCharges++;
        }
    }

    public void render(GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        // TODO: Render HUD with dash charges
        // Display charges at bottom right of screen
    }

    public int getCurrentCharges() {
        return currentCharges;
    }

    public int getMaxCharges() {
        return MAX_CHARGES;
    }
}