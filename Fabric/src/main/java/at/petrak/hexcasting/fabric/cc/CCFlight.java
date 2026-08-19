package at.petrak.hexcasting.fabric.cc;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import at.petrak.hexcasting.api.player.FlightAbility;
import at.petrak.hexcasting.api.utils.HexUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.Component;

public class CCFlight implements Component {
    public static final String
        TAG_ALLOWED = "allowed", // Fake: use this as a null sentinel
        TAG_TIME_LEFT = "time_left",
        TAG_DIMENSION = "dimension",
        TAG_ORIGIN = "origin",
        TAG_RADIUS = "radius";

    private final ServerPlayer owner;
    @Nullable
    private FlightAbility flight = null;

    public CCFlight(ServerPlayer owner) {
        this.owner = owner;
    }


    @Nullable
    public FlightAbility getFlight() {
        return flight;
    }

    public void setFlight(FlightAbility flight) {
        this.flight = flight;
    }

    @Override
    public void readData(ValueInput input) {
        var allowed = input.getBooleanOr(TAG_ALLOWED, false);
        if (!allowed) {
            this.flight = null;
        } else {
            var timeLeft = input.getIntOr(TAG_TIME_LEFT, 0);
            var dim = ResourceKey.create(Registries.DIMENSION,
                Identifier.parse(input.getStringOr(TAG_DIMENSION, "minecraft:overworld")));
            var origin = HexUtils.vecFromNBT(input.read(TAG_ORIGIN, net.minecraft.nbt.CompoundTag.CODEC).orElse(new net.minecraft.nbt.CompoundTag()));
            var radius = input.getDoubleOr(TAG_RADIUS, 0.0);
            this.flight = new FlightAbility(timeLeft, dim, origin, radius);
        }
    }

    @Override
    public void writeData(ValueOutput output) {
        output.putBoolean(TAG_ALLOWED, this.flight != null);
        if (this.flight != null) {
            output.putInt(TAG_TIME_LEFT, this.flight.timeLeft());
            output.putString(TAG_DIMENSION, this.flight.dimension().identifier().toString());
            output.store(TAG_ORIGIN, net.minecraft.nbt.CompoundTag.CODEC, HexUtils.serializeToNBT(this.flight.origin()));
            output.putDouble(TAG_RADIUS, this.flight.radius());
        }
    }
}
