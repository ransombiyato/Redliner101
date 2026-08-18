package at.petrak.hexcasting.common.entities;

import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.lib.HexDataComponents;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.lib.HexSounds;
import at.petrak.hexcasting.common.msgs.MsgNewWallScrollS2C;
import at.petrak.hexcasting.common.msgs.MsgRecalcWallScrollDisplayS2C;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class EntityWallScroll extends HangingEntity {
    private static final EntityDataAccessor<Boolean> SHOWS_STROKE_ORDER = SynchedEntityData.defineId(
        EntityWallScroll.class,
        EntityDataSerializers.BOOLEAN);

    public ItemStack scroll;
    @Nullable
    public HexPattern pattern;
    public boolean isAncient;
    public int blockSize;

    public EntityWallScroll(EntityType<? extends EntityWallScroll> type, Level world) {
        super(type, world);
    }

    public EntityWallScroll(Level world, BlockPos pos, Direction dir, ItemStack scroll, boolean showStrokeOrder,
        int blockSize) {
        super(HexEntities.WALL_SCROLL, world, pos);
        this.setDirection(dir);
        this.blockSize = blockSize;

        this.entityData.set(SHOWS_STROKE_ORDER, showStrokeOrder);
        this.scroll = scroll;
        this.recalculateDisplay();
        this.recalculateBoundingBox();
    }

    public void recalculateDisplay() {
        this.pattern = scroll.get(HexDataComponents.PATTERN);
        this.isAncient = scroll.has(HexDataComponents.ACTION);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SHOWS_STROKE_ORDER, false);
    }

    public boolean getShowsStrokeOrder() {
        return this.entityData.get(SHOWS_STROKE_ORDER);
    }

    public void setShowsStrokeOrder(boolean b) {
        this.entityData.set(SHOWS_STROKE_ORDER, b);
    }

    @Override
    protected AABB calculateBoundingBox(BlockPos pos, Direction p_direction) {
        float f = 0.46875F;
        Vec3 vec3 = Vec3.atCenterOf(pos).relative(p_direction, -0.46875);
        double d0 = blockSize % 2 == 0 ? 0.5 : 0.0;
        Direction direction = p_direction.getCounterClockWise();
        Vec3 vec31 = vec3.relative(direction, d0).relative(Direction.UP, d0);
        Direction.Axis direction$axis = p_direction.getAxis();
        double d2 = direction$axis == Direction.Axis.X ? 0.0625 : blockSize;
        double d3 = blockSize;
        double d4 = direction$axis == Direction.Axis.Z ? 0.0625 : blockSize;
        return AABB.ofSize(vec31, d2, d3, d4);
    }

    @Override
    public void dropItem(ServerLevel serverLevel, @Nullable Entity pBrokenEntity) {
        if (serverLevel.getGameRules().get(GameRules.ENTITY_DROPS)) {
            this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
            if (pBrokenEntity instanceof Player player) {
                if (player.getAbilities().instabuild) {
                    return;
                }
            }

            this.spawnAtLocation(serverLevel, this.scroll);
        }
    }

    @Override
    public InteractionResult interactAt(Player pPlayer, Vec3 pVec, InteractionHand pHand) {
        var handStack = pPlayer.getItemInHand(pHand);
        if (handStack.is(HexItems.AMETHYST_DUST) && !this.getShowsStrokeOrder()) {
            if (!pPlayer.getAbilities().instabuild) {
                handStack.shrink(1);
            }
            this.setShowsStrokeOrder(true);

            pPlayer.level().playSound(pPlayer, this, HexSounds.SCROLL_DUST, SoundSource.PLAYERS, 1f, 1f);

            if (pPlayer.level() instanceof ServerLevel slevel) {
                IXplatAbstractions.INSTANCE.sendPacketNear(this.position(), 32.0, slevel,
                        new MsgRecalcWallScrollDisplayS2C(this.getId(), true));
            } else {
                // Beat the packet roundtrip to the punch to get a quicker visual
                this.recalculateDisplay();
            }
            return InteractionResult.SUCCESS;
        }
        return super.interactAt(pPlayer, pVec, pHand);
    }

    @Override
    public void playPlacementSound() {
        this.playSound(SoundEvents.PAINTING_PLACE, 1.0F, 1.0F);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
        return IXplatAbstractions.INSTANCE.toVanillaClientboundPacket(
            new MsgNewWallScrollS2C(new ClientboundAddEntityPacket(this, this.getDirection().get3DDataValue(), this.getPos()),
                pos, getDirection(), scroll, getShowsStrokeOrder(), blockSize));
    }

    public void readSpawnData(BlockPos pos, Direction dir, ItemStack scrollItem,
        boolean showsStrokeOrder, int blockSize) {
        this.pos = pos;
        this.scroll = scrollItem;
        this.blockSize = blockSize;

        this.setDirection(dir);
        this.setShowsStrokeOrder(showsStrokeOrder);

        this.recalculateDisplay();
        this.recalculateBoundingBox();
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        output.putByte("direction", (byte) this.getDirection().ordinal());
        output.store("scroll", ItemStack.CODEC, this.scroll);
        output.putBoolean("showsStrokeOrder", this.getShowsStrokeOrder());
        output.putInt("blockSize", this.blockSize);
        super.addAdditionalSaveData(output);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        var direction = Direction.values()[input.getByteOr("direction", (byte) Direction.DOWN.ordinal())];
        this.setDirection(direction);
        this.scroll = input.read("scroll", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.blockSize = input.getIntOr("blockSize", 1);
        this.setShowsStrokeOrder(input.getBooleanOr("showsStrokeOrder", false));

        this.recalculateDisplay();
        this.recalculateBoundingBox();

        super.readAdditionalSaveData(input);
    }

    public void moveTo(double pX, double pY, double pZ, float pYaw, float pPitch) {
        this.setPos(pX, pY, pZ);
    }

    public void lerpTo(double x, double y, double z, float yRot, float xRot, int steps) {
        BlockPos blockpos = this.pos.offset((int) (x - this.getX()), (int) (y - this.getY()), (int) (z - this.getZ()));
        this.setPos(blockpos.getX(), blockpos.getY(), blockpos.getZ());
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return this.scroll.copy();
    }
}
