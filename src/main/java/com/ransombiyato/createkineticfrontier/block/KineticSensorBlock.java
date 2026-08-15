package com.ransombiyato.createkineticfrontier.block;

import com.ransombiyato.createkineticfrontier.blockentity.KineticSensorBlockEntity;
import com.mojang.serialization.MapCodec;
import com.ransombiyato.createkineticfrontier.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

public final class KineticSensorBlock extends KineticMachineBlock {
    public static final MapCodec<KineticSensorBlock> CODEC = simpleCodec(KineticSensorBlock::new);
    @Override public MapCodec<KineticSensorBlock> codec() { return CODEC; }
    public KineticSensorBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.POWERED);
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new KineticSensorBlockEntity(pos, state); }
    @Override public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.KINETIC_SENSOR.get(), KineticSensorBlockEntity::tick);
    }
}
