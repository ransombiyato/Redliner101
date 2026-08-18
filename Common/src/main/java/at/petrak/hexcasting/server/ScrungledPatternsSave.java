package at.petrak.hexcasting.server;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.math.EulerPathFinder;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps angle sigs to resource locations and their preferred start dir so we can look them up in the main registry.
 * Save this on the world in case the random algorithm changes.
 */
public class ScrungledPatternsSave extends SavedData {
    public static final String DATA_VERSION = "0.1.0";
    public static final String TAG_SAVED_DATA = "hexcasting.per-world-patterns." + DATA_VERSION;
    private static final String TAG_DIR = "startDir";
    private static final String TAG_KEY = "key";

    private static ResourceKey<Registry<ActionRegistryEntry>> actionRegistryKey() {
        return IXplatAbstractions.INSTANCE.getActionRegistry().key();
    }

    private static final Codec<ResourceKey<ActionRegistryEntry>> ACTION_KEY_CODEC = Codec.STRING.xmap(
        id -> ResourceKey.create(actionRegistryKey(), Identifier.parse(id)),
        key -> key.identifier().toString()
    );

    private static final Codec<HexDir> HEX_DIR_CODEC = Codec.BYTE.xmap(
        value -> HexDir.values()[value],
        value -> (byte) value.ordinal()
    );

    private static final Codec<PerWorldEntry> ENTRY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ACTION_KEY_CODEC.fieldOf(TAG_KEY).forGetter(PerWorldEntry::key),
        HEX_DIR_CODEC.fieldOf(TAG_DIR).forGetter(PerWorldEntry::canonicalStartDir)
    ).apply(instance, PerWorldEntry::new));

    private static final Codec<ScrungledPatternsSave> CODEC = Codec.unboundedMap(Codec.STRING, ENTRY_CODEC)
        .xmap(ScrungledPatternsSave::new, save -> save.lookup);

    private final Map<String, PerWorldEntry> lookup;
    private final Map<ResourceKey<ActionRegistryEntry>, String> reverseLookup;

    private ScrungledPatternsSave(Map<String, PerWorldEntry> lookup) {
        this.lookup = lookup;
        this.reverseLookup = new HashMap<>();
        this.lookup.forEach((sig, entry) -> this.reverseLookup.put(entry.key, sig));
    }

    @Nullable
    public PerWorldEntry lookup(String signature) {
        return this.lookup.get(signature);
    }

    @Nullable
    public Pair<String, PerWorldEntry> lookupReverse(ResourceKey<ActionRegistryEntry> key) {
        var sig = this.reverseLookup.get(key);
        if (sig == null) return null;
        return Pair.of(sig, this.lookup.get(sig));
    }

    public static SavedDataType<ScrungledPatternsSave> dataType(long seed) {
        return new SavedDataType<>(
            TAG_SAVED_DATA,
            () -> createFromScratch(seed),
            CODEC,
            DataFixTypes.PLAYER
        );
    }

    public static ScrungledPatternsSave createFromScratch(long seed) {
        var map = new HashMap<String, PerWorldEntry>();
        var registry = IXplatAbstractions.INSTANCE.getActionRegistry();

        // TODO: this version of the code doesn't have overlap protection
        // this means if some hilarious funny person makes a great spell that has the same shape as a normal spell
        // there might be overlap.
        // I'm going to file that under "don't do that"
        // (the number literal phial incident won't happen though because we check for special handlers first now)
        for (var key : registry.registryKeySet()) {
            var entry = registry.get(key).map(ref -> ref.value()).orElse(null);
            if (entry != null && HexUtils.isOfTag(registry, key, HexTags.Actions.PER_WORLD_PATTERN)) {
                var scrungledPat = EulerPathFinder.findAltDrawing(entry.prototype(), seed);
                map.put(scrungledPat.anglesSignature(), new PerWorldEntry(key, scrungledPat.getStartDir()));
            }
        }

        var out = new ScrungledPatternsSave(map);
        out.setDirty();
        return out;
    }

    public static ScrungledPatternsSave open(ServerLevel overworld) {
        return (ScrungledPatternsSave) overworld.getDataStorage().computeIfAbsent(dataType(overworld.getSeed()));
    }

    public record PerWorldEntry(ResourceKey<ActionRegistryEntry> key, HexDir canonicalStartDir) {
    }
}
