package at.petrak.hexcasting.fabric.cc;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import at.petrak.hexcasting.api.casting.eval.ResolvedPattern;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.ladysnake.cca.api.v3.component.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CCPatterns implements Component {
    public static final String TAG_PATTERNS = "patterns";

    private final Player owner;

    private List<ResolvedPattern> patterns = Collections.emptyList();

    public CCPatterns(ServerPlayer owner) {
        this.owner = owner;
    }


    public List<ResolvedPattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<ResolvedPattern> patterns) {
        this.patterns = patterns;
    }

    @Override
    public void readData(ValueInput input) {
        this.patterns = input.read(TAG_PATTERNS, ResolvedPattern.CODEC.listOf()).orElseGet(Collections::emptyList);
    }

    @Override
    public void writeData(ValueOutput output) {
        output.store(TAG_PATTERNS, ResolvedPattern.CODEC.listOf(), this.patterns);
    }
}
