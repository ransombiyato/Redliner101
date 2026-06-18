package com.ransombiyato.redliner.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class RedlinerConfig {
    public static final ModConfigSpec CLIENT_SPEC;
    public static final ModConfigSpec COMMON_SPEC;

    public static class Client {
        public final ModConfigSpec.BooleanValue enableGlow;
        public final ModConfigSpec.BooleanValue enableParticles;
        public final ModConfigSpec.IntValue hudPositionX;
        public final ModConfigSpec.IntValue hudPositionY;

        Client(ModConfigSpec.Builder builder) {
            builder.push("client");
            enableGlow = builder.define("enableGlow", true);
            enableParticles = builder.define("enableParticles", true);
            hudPositionX = builder.defineInRange("hudPositionX", 10, 0, 1000);
            hudPositionY = builder.defineInRange("hudPositionY", 10, 0, 1000);
            builder.pop();
        }
    }

    public static class Common {
        public final ModConfigSpec.DoubleValue dashSpeed;
        public final ModConfigSpec.DoubleValue maxDashCharges;
        public final ModConfigSpec.IntValue dashRechargeTime;

        Common(ModConfigSpec.Builder builder) {
            builder.push("common");
            dashSpeed = builder.defineInRange("dashSpeed", 2.0, 1.0, 5.0);
            maxDashCharges = builder.defineInRange("maxDashCharges", 3.0, 1.0, 10.0);
            dashRechargeTime = builder.defineInRange("dashRechargeTime", 200, 50, 1000);
            builder.pop();
        }
    }

    static {
        Pair<Client, ModConfigSpec> clientPair = new ModConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = clientPair.getRight();

        Pair<Common, ModConfigSpec> commonPair = new ModConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = commonPair.getRight();
    }
}