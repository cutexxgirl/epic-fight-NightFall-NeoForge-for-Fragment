package com.hm.efn.entity;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import yesman.epicfight.registry.entries.EpicFightAttributes;

public abstract class GeoVFXEntity extends PathfinderMob implements GeoEntity {
   protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(GeoVFXEntity.class, EntityDataSerializers.OPTIONAL_UUID);
   protected static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.defineId(GeoVFXEntity.class, EntityDataSerializers.INT);
   protected static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(GeoVFXEntity.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Float> Y_ROT_OFFSET = SynchedEntityData.defineId(GeoVFXEntity.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Float> X_ROT_OFFSET = SynchedEntityData.defineId(GeoVFXEntity.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Float> Z_ROT_OFFSET = SynchedEntityData.defineId(GeoVFXEntity.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Float> START_Y_ROT = SynchedEntityData.defineId(GeoVFXEntity.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Float> MOVE_SPEED = SynchedEntityData.defineId(GeoVFXEntity.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Float> MOVE_DISTANCE = SynchedEntityData.defineId(GeoVFXEntity.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Float> TRAVELED_DISTANCE = SynchedEntityData.defineId(GeoVFXEntity.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Integer> ATTACK_INTERVAL = SynchedEntityData.defineId(GeoVFXEntity.class, EntityDataSerializers.INT);
   protected static final EntityDataAccessor<Integer> ATTACK_COUNT = SynchedEntityData.defineId(GeoVFXEntity.class, EntityDataSerializers.INT);
   protected static final EntityDataAccessor<Float> ATTACK_DAMAGE = SynchedEntityData.defineId(GeoVFXEntity.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Float> ANIMATION_SPEED = SynchedEntityData.defineId(GeoVFXEntity.class, EntityDataSerializers.FLOAT);
   private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
   protected int lifetime = 0;

   public GeoVFXEntity(EntityType<? extends PathfinderMob> type, Level level) {
      super(type, level);
      this.initializeDefaults();
   }

   public GeoVFXEntity(EntityType<? extends PathfinderMob> type, Level level, double x, double y, double z) {
      super(type, level);
      this.initializeDefaults();
      this.setPos(x, y, z);
   }

   public GeoVFXEntity(EntityType<? extends PathfinderMob> type, Level level, double x, double y, double z, float yRot) {
      super(type, level);
      this.initializeDefaults();
      this.setPos(x, y, z);
      this.setYRot(yRot);
      this.setYBodyRot(yRot);
      this.setYHeadRot(yRot);
   }

   public GeoVFXEntity(EntityType<? extends PathfinderMob> type, Level level, double x, double y, double z, float yRot, LivingEntity owner) {
      super(type, level);
      this.initializeDefaults();
      this.setPos(x, y, z);
      this.setYRot(yRot);
      this.setYBodyRot(yRot);
      this.setYHeadRot(yRot);
      if (owner != null) {
         this.setOwner(owner);
      }
   }

   protected void initializeDefaults() {
      this.noPhysics = true;
      this.setNoGravity(true);
      this.noCulling = true;
      this.setInvulnerable(true);
      this.setScale(1.0F);
      this.setYRotOffset(0.0F);
      this.setXRotOffset(0.0F);
      this.setZRotOffset(0.0F);
      this.setStartYRot(0.0F);
      this.setMoveSpeed(0.0F);
      this.setMoveDistance(0.0F);
      this.setTraveledDistance(0.0F);
      this.setAttackInterval(0);
      this.setAttackCount(0);
      this.setAttackDamage(0.0F);
      this.setAnimationSpeed(1.0F);
   }

   public void tick() {
      super.tick();
      this.lifetime++;
      if (!this.level().isClientSide && this.getMoveSpeed() > 0.0F) {
         this.performMovement();
      }

      if (this.shouldDiscard()) {
         this.discard();
      } else if (!this.level().isClientSide && this.getOwner() != null && !this.getOwner().isAlive()) {
         this.discard();
      } else {
         this.onVFXUpdate();
      }
   }

   protected void performMovement() {
      float moveSpeed = this.getMoveSpeed();
      float moveDistance = this.getMoveDistance();
      float traveledDistance = this.getTraveledDistance();
      if (traveledDistance < moveDistance) {
         float yawRadians = (float)Math.toRadians(-this.getYRot());
         Vec3 moveDirection = new Vec3(Math.sin(yawRadians), 0.0, Math.cos(yawRadians)).normalize();
         float thisTickMove = moveSpeed;
         if (traveledDistance + thisTickMove > moveDistance) {
            thisTickMove = moveDistance - traveledDistance;
         }

         Vec3 movement = moveDirection.scale(thisTickMove);
         this.setPos(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);
         this.setTraveledDistance(traveledDistance + thisTickMove);
      }
   }

   protected boolean shouldPerformAttack() {
      return true;
   }

   protected abstract void performAttack();

   protected abstract boolean shouldDiscard();

   protected void onVFXUpdate() {
   }

   protected void defineSynchedData(SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_OWNER_UUID, Optional.empty());
      builder.define(DATA_OWNER_ID, 0);
      builder.define(SCALE, 1.0F);
      builder.define(Y_ROT_OFFSET, 0.0F);
      builder.define(X_ROT_OFFSET, 0.0F);
      builder.define(Z_ROT_OFFSET, 0.0F);
      builder.define(START_Y_ROT, 0.0F);
      builder.define(MOVE_SPEED, 0.0F);
      builder.define(MOVE_DISTANCE, 0.0F);
      builder.define(TRAVELED_DISTANCE, 0.0F);
      builder.define(ATTACK_INTERVAL, 0);
      builder.define(ATTACK_COUNT, 0);
      builder.define(ATTACK_DAMAGE, 0.0F);
      builder.define(ANIMATION_SPEED, 1.0F);
   }

   public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
      super.onSyncedDataUpdated(key);
      if (SCALE.equals(key)) {
         this.refreshDimensions();
      }
   }

   @NotNull
   protected EntityDimensions getDefaultDimensions(@NotNull Pose pose) {
      EntityDimensions originalDimensions = super.getDefaultDimensions(pose);
      return originalDimensions.scale(this.getScale());
   }

   public void refreshDimensions() {
      double x = this.getX();
      double y = this.getY();
      double z = this.getZ();
      super.refreshDimensions();
      this.setPos(x, y, z);
   }

   @Nullable
   public UUID getOwnerUUID() {
      return this.entityData.get(DATA_OWNER_UUID).orElse(null);
   }

   public void setOwnerUUID(@Nullable UUID uuid) {
      this.entityData.set(DATA_OWNER_UUID, Optional.ofNullable(uuid));
   }

   public int getOwnerID() {
      return (Integer)this.entityData.get(DATA_OWNER_ID);
   }

   public void setOwnerID(int id) {
      this.entityData.set(DATA_OWNER_ID, id);
   }

   public float getScale() {
      return (Float)this.entityData.get(SCALE);
   }

   public void setScale(float scale) {
      this.entityData.set(SCALE, scale);
   }

   public float getYRotOffset() {
      return (Float)this.entityData.get(Y_ROT_OFFSET);
   }

   public void setYRotOffset(float offset) {
      this.entityData.set(Y_ROT_OFFSET, offset);
   }

   public float getXRotOffset() {
      return (Float)this.entityData.get(X_ROT_OFFSET);
   }

   public void setXRotOffset(float offset) {
      this.entityData.set(X_ROT_OFFSET, offset);
   }

   public float getZRotOffset() {
      return (Float)this.entityData.get(Z_ROT_OFFSET);
   }

   public void setZRotOffset(float offset) {
      this.entityData.set(Z_ROT_OFFSET, offset);
   }

   public float getStartYRot() {
      return (Float)this.entityData.get(START_Y_ROT);
   }

   public void setStartYRot(float rot) {
      this.entityData.set(START_Y_ROT, rot);
   }

   public float getMoveSpeed() {
      return (Float)this.entityData.get(MOVE_SPEED);
   }

   public void setMoveSpeed(float speed) {
      this.entityData.set(MOVE_SPEED, speed);
   }

   public float getMoveDistance() {
      return (Float)this.entityData.get(MOVE_DISTANCE);
   }

   public void setMoveDistance(float distance) {
      this.entityData.set(MOVE_DISTANCE, distance);
   }

   public float getTraveledDistance() {
      return (Float)this.entityData.get(TRAVELED_DISTANCE);
   }

   public void setTraveledDistance(float distance) {
      this.entityData.set(TRAVELED_DISTANCE, distance);
   }

   public int getAttackInterval() {
      return (Integer)this.entityData.get(ATTACK_INTERVAL);
   }

   public void setAttackInterval(int interval) {
      this.entityData.set(ATTACK_INTERVAL, interval);
   }

   public int getAttackCount() {
      return (Integer)this.entityData.get(ATTACK_COUNT);
   }

   public void setAttackCount(int count) {
      this.entityData.set(ATTACK_COUNT, count);
   }

   public float getAttackDamage() {
      return (Float)this.entityData.get(ATTACK_DAMAGE);
   }

   public void setAttackDamage(float damage) {
      this.entityData.set(ATTACK_DAMAGE, damage);
   }

   public int getLifetime() {
      return this.lifetime;
   }

   public void setLifetime(int lifetime) {
      this.lifetime = lifetime;
   }

   public void setOwner(LivingEntity owner) {
      this.setOwnerUUID(owner.getUUID());
      this.setOwnerID(owner.getId());
   }

   @Nullable
   public LivingEntity getOwner() {
      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         Player player = this.level().getPlayerByUUID(uuid);
         if (player != null) {
            return player;
         }

         if (this.level() instanceof ServerLevel serverLevel) {
            if (serverLevel.getEntity(uuid) instanceof LivingEntity livingEntity) {
               return livingEntity;
            }
         } else if (this.level().getEntity(this.getOwnerID()) instanceof LivingEntity livingEntity) {
            return livingEntity;
         }
      }

      return null;
   }

   public void addAdditionalSaveData(@NotNull CompoundTag compound) {
      super.addAdditionalSaveData(compound);
      if (this.getOwnerUUID() != null) {
         compound.putUUID("Owner", this.getOwnerUUID());
      }

      compound.putFloat("Scale", this.getScale());
      compound.putFloat("YRotOffset", this.getYRotOffset());
      compound.putFloat("XRotOffset", this.getXRotOffset());
      compound.putFloat("ZRotOffset", this.getZRotOffset());
      compound.putFloat("StartYRot", this.getStartYRot());
      compound.putFloat("MoveSpeed", this.getMoveSpeed());
      compound.putFloat("MoveDistance", this.getMoveDistance());
      compound.putFloat("TraveledDistance", this.getTraveledDistance());
      compound.putInt("AttackInterval", this.getAttackInterval());
      compound.putInt("AttackCount", this.getAttackCount());
      compound.putFloat("AttackDamage", this.getAttackDamage());
      compound.putFloat("AnimationSpeed", this.getAnimationSpeed());
   }

   public void readAdditionalSaveData(@NotNull CompoundTag compound) {
      super.readAdditionalSaveData(compound);
      if (compound.hasUUID("Owner")) {
         this.setOwnerUUID(compound.getUUID("Owner"));
      }

      this.setScale(compound.getFloat("Scale"));
      this.setYRotOffset(compound.getFloat("YRotOffset"));
      this.setXRotOffset(compound.getFloat("XRotOffset"));
      this.setZRotOffset(compound.getFloat("ZRotOffset"));
      this.setStartYRot(compound.getFloat("StartYRot"));
      this.setMoveSpeed(compound.getFloat("MoveSpeed"));
      this.setMoveDistance(compound.getFloat("MoveDistance"));
      this.setTraveledDistance(compound.getFloat("TraveledDistance"));
      this.setAttackInterval(compound.getInt("AttackInterval"));
      this.setAttackCount(compound.getInt("AttackCount"));
      this.setAttackDamage(compound.getFloat("AttackDamage"));
      if (compound.contains("AnimationSpeed")) {
         this.setAnimationSpeed(compound.getFloat("AnimationSpeed"));
      }
   }

   public float getAnimationSpeed() {
      return (Float)this.entityData.get(ANIMATION_SPEED);
   }

   public void setAnimationSpeed(float speed) {
      this.entityData.set(ANIMATION_SPEED, Math.max(0.01F, speed));
   }

   public void registerControllers(ControllerRegistrar controllers) {
      controllers.add(new AnimationController[]{this.createSpawnController()});
   }

   private AnimationController<GeoVFXEntity> createSpawnController() {
      return new AnimationController<GeoVFXEntity>(this, "Spawn", 0, this::spawnPredicate).setAnimationSpeedHandler(entity -> (double)this.getAnimationSpeed());
   }

   protected PlayState spawnPredicate(AnimationState<GeoVFXEntity> state) {
      return state.setAndContinue(DefaultAnimations.SPAWN);
   }

   public AnimatableInstanceCache getAnimatableInstanceCache() {
      return this.cache;
   }

   public static AttributeSupplier getDefaultAttribute() {
      return Animal.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 1.0)
         .add(Attributes.ATTACK_DAMAGE, 0.0)
         .add(EpicFightAttributes.MAX_STRIKES, 1.0)
         .build();
   }

   public void move(@NotNull MoverType type, @NotNull Vec3 movement) {
   }

   public boolean isPushable() {
      return false;
   }

   public boolean isPickable() {
      return false;
   }

   public boolean canBeSeenAsEnemy() {
      return false;
   }

   public boolean isNoGravity() {
      return true;
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean hurt(@NotNull DamageSource source, float amount) {
      return false;
   }

   protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState state) {
   }

   public boolean causeFallDamage(float fallDistance, float multiplier, @NotNull DamageSource source) {
      return false;
   }

   public boolean canSpawnSprintParticle() {
      return false;
   }
}
