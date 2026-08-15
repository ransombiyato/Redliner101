package com.ransombiyato.createkineticfrontier.block;

import com.ransombiyato.createkineticfrontier.blockentity.KineticCoreBlockEntity;
import com.mojang.serialization.MapCodec;
import com.ransombiyato.createkineticfrontier.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class KineticCoreBlock extends KineticMachineBlock {
    public static final MapCodec<KineticCoreBlock> CODEC = simpleCodec(KineticCoreBlock::new);
    @Override public MapCodec<KineticCoreBlock> codec() { return CODEC; }
    public KineticCoreBlock(Properties properties) { super(properties); }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new KineticCoreBlockEntity(pos, state); }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.KINETIC_CORE.get(), KineticCoreBlockEntity::tick);
    }
}
