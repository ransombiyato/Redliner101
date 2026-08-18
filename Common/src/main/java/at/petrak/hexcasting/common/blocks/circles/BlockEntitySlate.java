package at.petrak.hexcasting.common.blocks.circles;

import at.petrak.hexcasting.api.block.HexBlockEntity;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.lib.HexBlockEntities;
import at.petrak.hexcasting.common.lib.HexDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockEntitySlate extends HexBlockEntity {
    public static final String TAG_PATTERN = "pattern";

    @Nullable
    public HexPattern pattern;

    public BlockEntitySlate(BlockPos pos, BlockState state) {
        super(HexBlockEntities.SLATE_TILE, pos, state);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        if (this.pattern != null) {
            components.set(HexDataComponents.PATTERN, this.pattern);
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter componentInput) {
        super.applyImplicitComponents(componentInput);
        var pat = componentInput.get(HexDataComponents.PATTERN);
        if (pat != null) {
            this.pattern = pat;
        }
    }

    @Override
    protected void saveModData(ValueOutput output) {
        if (this.pattern != null) {
            output.store(TAG_PATTERN, HexPattern.CODEC, this.pattern);
        }
    }

    @Override
    protected void loadModData(ValueInput input) {
        this.pattern = input.read(TAG_PATTERN, HexPattern.CODEC).orElse(null);
    }

}
