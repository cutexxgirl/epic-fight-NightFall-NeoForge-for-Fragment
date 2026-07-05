package com.hm.efn.entity;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import com.hm.efn.compat.neoforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Abstract3DParticleEntity extends Entity {
   protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(
      Abstract3DParticleEntity.class, EntityDataSerializers.OPTIONAL_UUID
   );
   protected static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.defineId(
      Abstract3DParticleEntity.class, EntityDataSerializers.INT
   );
   protected static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(Abstract3DParticleEntity.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Float> X_ROT_OFFSET = SynchedEntityData.defineId(Abstract3DParticleEntity.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Float> Y_ROT_OFFSET = SynchedEntityData.defineId(Abstract3DParticleEntity.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Float> Z_ROT_OFFSET = SynchedEntityData.defineId(Abstract3DParticleEntity.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Float> MOVE_SPEED = SynchedEntityData.defineId(Abstract3DParticleEntity.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Float> MOVE_DISTANCE = SynchedEntityData.defineId(Abstract3DParticleEntity.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Float> TRAVELED_DISTANCE = SynchedEntityData.defineId(
      Abstract3DParticleEntity.class, EntityDataSerializers.FLOAT
   );
   protected static final EntityDataAccessor<Integer> MAX_LIFETIME = SynchedEntityData.defineId(
      Abstract3DParticleEntity.class, EntityDataSerializers.INT
   );
   protected static final EntityDataAccessor<Float> ATTACK_DAMAGE = SynchedEntityData.defineId(Abstract3DParticleEntity.class, EntityDataSerializers.FLOAT);
   private boolean hasSpawnedClientParticle = false;

   public Abstract3DParticleEntity(EntityType<?> type, Level level) {
      super(type, level);
      this.noPhysics = true;
      this.noCulling = true;
   }

   protected void defineSynchedData(SynchedEntityData.Builder builder) {
      builder.define(DATA_OWNER_UUID, Optional.empty());
      builder.define(DATA_OWNER_ID, -1);
      builder.define(SCALE, 1.0F);
      builder.define(X_ROT_OFFSET, 0.0F);
      builder.define(Y_ROT_OFFSET, 0.0F);
      builder.define(Z_ROT_OFFSET, 0.0F);
      builder.define(MOVE_SPEED, 0.0F);
      builder.define(MOVE_DISTANCE, 0.0F);
      builder.define(TRAVELED_DISTANCE, 0.0F);
      builder.define(MAX_LIFETIME, 20);
      builder.define(ATTACK_DAMAGE, 0.0F);
   }

   public void tick() {
      super.tick();
      if (this.tickCount >= this.getMaxLifetime()) {
         this.discard();
      } else if (!this.level().isClientSide && this.getOwner() != null && !this.getOwner().isAlive()) {
         this.discard();
      } else {
         if (this.getMoveSpeed() > 0.0F) {
            this.performMovement();
         }

         if (!this.level().isClientSide && this.shouldPerformAttack()) {
            this.performAttack();
         }

         if (this.level().isClientSide && !this.hasSpawnedClientParticle) {
            this.spawnClientParticle();
            this.hasSpawnedClientParticle = true;
         }
      }
   }

   protected void performMovement() {
      float moveSpeed = this.getMoveSpeed();
      float moveDistance = this.getMoveDistance();
      float traveledDistance = this.getTraveledDistance();
      if (traveledDistance < moveDistance) {
         float yawRadians = (float)Math.toRadians(-this.getYRotOffset());
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

   protected abstract void spawnClientParticle();

   protected abstract boolean shouldPerformAttack();

   protected abstract void performAttack();

   public void setOwner(LivingEntity owner) {
      this.entityData.set(DATA_OWNER_UUID, Optional.of(owner.getUUID()));
      this.entityData.set(DATA_OWNER_ID, owner.getId());
   }

   @Nullable
   public LivingEntity getOwner() {
      UUID uuid = (UUID)((Optional)this.entityData.get(DATA_OWNER_UUID)).orElse(null);
      if (uuid != null) {
         if (this.level() instanceof ServerLevel serverLevel) {
            if (serverLevel.getEntity(uuid) instanceof LivingEntity livingEntity) {
               return livingEntity;
            }
         } else if (this.level().getEntity((Integer)this.entityData.get(DATA_OWNER_ID)) instanceof LivingEntity livingEntity) {
            return livingEntity;
         }
      }

      return null;
   }

   public float getScale() {
      return (Float)this.entityData.get(SCALE);
   }

   public void setScale(float scale) {
      this.entityData.set(SCALE, scale);
   }

   public float getXRotOffset() {
      return (Float)this.entityData.get(X_ROT_OFFSET);
   }

   public void setXRotOffset(float rot) {
      this.entityData.set(X_ROT_OFFSET, rot);
   }

   public float getYRotOffset() {
      return (Float)this.entityData.get(Y_ROT_OFFSET);
   }

   public void setYRotOffset(float rot) {
      this.entityData.set(Y_ROT_OFFSET, rot);
   }

   public float getZRotOffset() {
      return (Float)this.entityData.get(Z_ROT_OFFSET);
   }

   public void setZRotOffset(float rot) {
      this.entityData.set(Z_ROT_OFFSET, rot);
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

   public void setMoveDistance(float dist) {
      this.entityData.set(MOVE_DISTANCE, dist);
   }

   public float getTraveledDistance() {
      return (Float)this.entityData.get(TRAVELED_DISTANCE);
   }

   public void setTraveledDistance(float dist) {
      this.entityData.set(TRAVELED_DISTANCE, dist);
   }

   public int getMaxLifetime() {
      return (Integer)this.entityData.get(MAX_LIFETIME);
   }

   public void setMaxLifetime(int ticks) {
      this.entityData.set(MAX_LIFETIME, ticks);
   }

   public float getAttackDamage() {
      return (Float)this.entityData.get(ATTACK_DAMAGE);
   }

   public void setAttackDamage(float damage) {
      this.entityData.set(ATTACK_DAMAGE, damage);
   }

   protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
      if (tag.hasUUID("OwnerUUID")) {
         this.entityData.set(DATA_OWNER_UUID, Optional.of(tag.getUUID("OwnerUUID")));
      }

      this.setScale(tag.getFloat("Scale"));
      this.setMaxLifetime(tag.getInt("MaxLifetime"));
      this.setXRotOffset(tag.getFloat("XRot"));
      this.setYRotOffset(tag.getFloat("YRot"));
      this.setZRotOffset(tag.getFloat("ZRot"));
   }

   protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
      this.entityData.get(DATA_OWNER_UUID).ifPresent(uuid -> tag.putUUID("OwnerUUID", uuid));
      tag.putFloat("Scale", this.getScale());
      tag.putInt("MaxLifetime", this.getMaxLifetime());
      tag.putFloat("XRot", this.getXRotOffset());
      tag.putFloat("YRot", this.getYRotOffset());
      tag.putFloat("ZRot", this.getZRotOffset());
   }

   @NotNull
   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
      super.onSyncedDataUpdated(key);
      if (SCALE.equals(key)) {
         this.refreshDimensions();
      }
   }

   @NotNull
   public EntityDimensions getDimensions(@NotNull Pose pose) {
      float currentScale = this.getScale();
      EntityDimensions originalDimensions = super.getDimensions(pose);
      return originalDimensions.scale(currentScale);
   }

   public void refreshDimensions() {
      double x = this.getX();
      double y = this.getY();
      double z = this.getZ();
      super.refreshDimensions();
      this.setPos(x, y, z);
   }

   public void move(@NotNull MoverType type, @NotNull Vec3 movement) {
   }

   public boolean isPushable() {
      return false;
   }

   public boolean isPickable() {
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
