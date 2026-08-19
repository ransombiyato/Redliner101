package at.petrak.hexcasting.api.advancements;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.criterion.MinMaxBounds;

import java.util.Optional;
import java.util.function.Function;

public record MinMaxLongs(
        Optional<Long> min,
        Optional<Long> max
) implements MinMaxBounds<Long> {
    private static final Codec<MinMaxBounds.Bounds<Long>> BOUNDS_CODEC =
            Codec.<Long, MinMaxBounds.Bounds<Long>>either(
                    Codec.LONG,
                    RecordCodecBuilder.<MinMaxBounds.Bounds<Long>>create(instance -> instance.group(
                            Codec.LONG.optionalFieldOf("min").forGetter(bounds -> bounds.min()),
                            Codec.LONG.optionalFieldOf("max").forGetter(bounds -> bounds.max())
                    ).apply(instance, (min, max) -> new MinMaxBounds.Bounds<>(min, max)))
            ).xmap(
                    either -> either.map(
                            value -> new MinMaxBounds.Bounds<Long>(Optional.of(value), Optional.of(value)),
                            bounds -> bounds
                    ),
                    bounds -> bounds.min().isPresent() && bounds.max().isPresent()
                            && bounds.min().get().equals(bounds.max().get())
                            ? Either.left(bounds.min().get())
                            : Either.right(bounds)
            ).validate(bounds -> bounds.areSwapped()
                    ? DataResult.error(() -> "min must be less than or equal to max")
                    : DataResult.success(bounds));

    public static final Codec<MinMaxLongs> CODEC = BOUNDS_CODEC
            .xmap(bounds -> new MinMaxLongs(bounds.min(), bounds.max()), MinMaxLongs::bounds);

    public static final MinMaxLongs ANY =
            new MinMaxLongs(Optional.empty(), Optional.empty());

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
    public MinMaxBounds.Bounds<Long> bounds() {
        return new MinMaxBounds.Bounds<>(min, max);
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
