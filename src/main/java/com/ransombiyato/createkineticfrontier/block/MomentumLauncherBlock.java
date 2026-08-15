package com.ransombiyato.createkineticfrontier.block;

import com.ransombiyato.createkineticfrontier.blockentity.MomentumLauncherBlockEntity;
import com.mojang.serialization.MapCodec;
import com.ransombiyato.createkineticfrontier.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public final class MomentumLauncherBlock extends KineticMachineBlock {
    public static final MapCodec<MomentumLauncherBlock> CODEC = simpleCodec(MomentumLauncherBlock::new);
    @Override public MapCodec<MomentumLauncherBlock> codec() { return CODEC; }
    public MomentumLauncherBlock(Properties properties) { super(properties); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new MomentumLauncherBlockEntity(pos, state); }
    @Override public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.MOMENTUM_LAUNCHER.get(), MomentumLauncherBlockEntity::tick);
    }
}
