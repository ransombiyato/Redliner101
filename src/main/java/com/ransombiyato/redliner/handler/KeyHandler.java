package com.ransombiyato.redliner.handler;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.api.distmarker.OnlyIn;
import net.minecraft.client.KeyMapping;

@OnlyIn(Dist.CLIENT)
public class KeyHandler {
    public static KeyMapping DASH_KEY = new KeyMapping(
            "key.redliner.dash",
            -1, // TODO: Set default key (e.g., com.mojang.blaze3d.platform.InputConstants.KEY_V)
            "key.categories.redliner"
    );

    public static void onKeyInput(int key, int scanCode, int action, int modifiers) {
        if (action == 1) { // GLFW_PRESS
            // Handle dash key press
            // TODO: Implement dash logic
        }
    }
}