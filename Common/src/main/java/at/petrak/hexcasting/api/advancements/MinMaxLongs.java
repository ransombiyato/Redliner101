package at.petrak.hexcasting.api.advancements;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.advancements.criterion.MinMaxBounds;

import java.util.Optional;
import java.util.function.Function;

public record MinMaxLongs(
        Optional<Long> min,
        Optional<Long> max
) implements MinMaxBounds<Long> {
    public static final Codec<MinMaxLongs> CODEC =
            MinMaxBounds.Bounds.createCodec(Codec.LONG)
                    .xmap(bounds -> new MinMaxLongs(bounds.min(), bounds.max()), MinMaxLongs::bounds);

    public static final MinMaxLongs ANY =
            new MinMaxLongs(Optional.empty(), Optional.empty());


    private static Optional<Long> squareOpt(Optional<Long> value) {
        return value.map(v -> v * v);
    }

    public static MinMaxLongs exactly(long value) {
        return new MinMaxLongs(Optional.of(value), Optional.of(value));
    }

    public static MinMaxLongs between(long min, long max) {
        return new MinMaxLongs(Optional.of(min), Optional.of(max));
    }

    public static MinMaxLongs atLeast(long min) {
        return new MinMaxLongs(Optional.of(min), Optional.empty());
    }

    public static MinMaxLongs atMost(long max) {
        return new MinMaxLongs(Optional.empty(), Optional.of(max));
    }

    @Override
    public MinMaxBounds.Bounds bounds() {
        return new MinMaxBounds.Bounds(min, max);
    }

    public boolean matches(long value) {
        return (this.min.isEmpty() || this.min.get() <= value)
                && (this.max.isEmpty() || this.max.get() >= value);
    }

    public static MinMaxLongs fromReader(StringReader reader) throws CommandSyntaxException {
        return fromReader(reader, l -> l);
    }

    public static MinMaxLongs fromReader(StringReader reader, Function<Long, Long> formatter)
            throws CommandSyntaxException {
        var bounds = MinMaxBounds.Bounds.fromReader(
                reader,
                text -> formatter.apply(Long.parseLong(text)),
                CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidLong
        );
        return new MinMaxLongs(bounds.min(), bounds.max());
    }
}

