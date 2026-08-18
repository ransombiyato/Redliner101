package at.petrak.hexcasting.api.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import java.util.function.Supplier;

/** Kotlin interop bridge for DataFixerUpper's static interface methods. */
public final class CodecHelper {
    private CodecHelper() {
    }

    public static <T> Codec<T> unit(Supplier<T> value) {
        return MapCodec.unit(value.get()).codec();
    }
}
