package com.hm.efn.entity.effect.SinSummonedSwordEntity;

import com.hm.efn.EFN;
import com.hm.efn.entity.EFNEntity;
import com.hm.efn.gameasset.EFNAnimations;
import com.merlin204.avalon.entity.IAvalonMeshEntity;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.registry.entries.EpicFightAttributes;

public class SinSummonedSwordEntity extends Mob implements IAvalonMeshEntity {
   protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.defineId(
      SinSummonedSwordEntity.class, EntityDataSerializers.OPTIONAL_UUID
   );
   protected static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.defineId(SinSummonedSwordEntity.class, EntityDataSerializers.INT);
   protected static final EntityDataAccessor<Float> SYNC_X_ROT = SynchedEntityData.defineId(SinSummonedSwordEntity.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Float> SCALE = SynchedEntityData.defineId(SinSummonedSwordEntity.class, EntityDataSerializers.FLOAT);
   protected static final EntityDataAccessor<Boolean> PLAY_ANIMATION = SynchedEntityData.defineId(
      SinSummonedSwordEntity.class, EntityDataSerializers.BOOLEAN
   );
   protected static final EntityDataAccessor<Boolean> SHOULD_RENDER = SynchedEntityData.defineId(SinSummonedSwordEntity.class, EntityDataSerializers.BOOLEAN);
   protected static final EntityDataAccessor<Boolean> DATA_IS_IN_STANDBY = SynchedEntityData.defineId(
      SinSummonedSwordEntity.class, EntityDataSerializers.BOOLEAN
   );
   protected static final EntityDataAccessor<Boolean> DATA_NO_AIM = SynchedEntityData.defineId(SinSummonedSwordEntity.class, EntityDataSerializers.BOOLEAN);
   protected static final EntityDataAccessor<Boolean> DATA_HAS_HIT_TARGET = SynchedEntityData.defineId(
      SinSummonedSwordEntity.class, EntityDataSerializers.BOOLEAN
   );
   protected static final EntityDataAccessor<Boolean> DATA_IS_STUCK = SynchedEntityData.defineId(SinSummonedSwordEntity.class, EntityDataSerializers.BOOLEAN);
   protected static final EntityDataAccessor<Boolean> DATA_IS_ANGEL = SynchedEntityData.defineId(SinSummonedSwordEntity.class, EntityDataSerializers.BOOLEAN);
   protected static final EntityDataAccessor<Boolean> DATA_IS_DEMON = SynchedEntityData.defineId(SinSummonedSwordEntity.class, EntityDataSerializers.BOOLEAN);
   protected static final EntityDataAccessor<Boolean> DATA_IS_BLAST = SynchedEntityData.defineId(SinSummonedSwordEntity.class, EntityDataSerializers.BOOLEAN);
   protected static final EntityDataAccessor<Boolean> DATA_IS_HEAVY_RAIN = SynchedEntityData.defineId(
      SinSummonedSwordEntity.class, EntityDataSerializers.BOOLEAN
   );
   protected static final EntityDataAccessor<Boolean> DATA_IS_DAMOCLES_MAIN = SynchedEntityData.defineId(
      SinSummonedSwordEntity.class, EntityDataSerializers.BOOLEAN
   );
   protected static final EntityDataAccessor<Boolean> DATA_IS_DAMOCLES_SUB = SynchedEntityData.defineId(
      SinSummonedSwordEntity.class, EntityDataSerializers.BOOLEAN
   );
   protected static final EntityDataAccessor<Integer> DATA_SHOOT_SPEED = SynchedEntityData.defineId(
      SinSummonedSwordEntity.class, EntityDataSerializers.INT
   );
   private int lifetimeTicks = 20;
   private boolean isDiscard = false;
   private boolean initialVelocityApplied = false;
   private Vec3 lockedTrajectory = Vec3.ZERO;
   private int discardDelayTicks = 12;

   public SinSummonedSwordEntity(EntityType<? extends Mob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.noCulling = true;
      this.setInvulnerable(true);
      this.noPhysics = true;
      this.setNoGravity(true);
   }

   public SinSummonedSwordEntity(EntityType<? extends Mob> pEntityType, LivingEntity owner, float scale, Level pLevel, boolean standby) {
      super(pEntityType, pLevel);
      LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class);
      this.tame(owner);
      this.noCulling = true;
      this.noPhysics = true;
      this.setInvulnerable(true);
      this.entityData.set(SCALE, scale);
      if (livingEntityPatch != null) {
         this.setYRot(((LivingEntity)livingEntityPatch.getOriginal()).getYHeadRot());
      }

      this.setXRot(owner.getXRot());
      this.setNoGravity(true);
      this.setInStandby(standby);
   }

   public void travel(@NotNull Vec3 pTravelVector) {
      if (!this.isInStandby()) {
         super.travel(pTravelVector);
      }
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide) {
         if (this.isHeavyRain() || this.isDamoclesMain()) {
            this.setXRot(90.0F);
            this.xRotO = 90.0F;
            this.setYRot(0.0F);
            this.yRotO = 0.0F;
            this.yHeadRot = 0.0F;
            this.yHeadRotO = 0.0F;
            this.yBodyRot = 0.0F;
            this.yBodyRotO = 0.0F;
         } else if (!this.isBlast()) {
            this.xRotO = this.getXRot();
            this.setXRot(this.getSyncXrot());
         }
      } else {
         if (this.lockedTrajectory != Vec3.ZERO) {
            this.setDeltaMovement(this.lockedTrajectory);
            this.setNoGravity(true);
         }

         if (this.hasHitTarget()) {
            this.noPhysics = true;
         }

         if (!this.isInStandby()) {
            if (this.tickCount >= this.lifetimeTicks) {
               this.discard();
               return;
            }

            if (!this.initialVelocityApplied && (this.isBlast() || this.isHeavyRain() || this.isDamoclesMain() || this.tickCount > 1)) {
               this.applyInitialVelocity(this.getShootSpeed());
               this.initialVelocityApplied = true;
            }

            if (this.hasHitTarget() && !this.isDiscard && !this.isHeavyRain() && !this.isDamoclesMain()) {
               this.isDiscard = true;
               EFN.queueServerWork(this.discardDelayTicks, this::discard);
            }
         }
      }
   }

   public void launch(@Nullable LivingEntity target) {
      if (!this.level().isClientSide && this.isInStandby()) {
         this.setInStandby(false);
         this.tickCount = 0;
         if (!this.isNoAim()) {
            LivingEntity owner = this.getOwner();
            LivingEntityPatch<?> ownerPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class);
            if (target != null) {
               this.aimAtEntity(target);
            } else if (owner != null) {
               if (ownerPatch != null) {
                  this.setYRot(((LivingEntity)ownerPatch.getOriginal()).yHeadRotO);
               }

               this.setSyncXRot(owner.getXRot());
            }
         }
      }
   }

   public static void summon(ServerPlayerPatch serverPlayerPatch, boolean isAngel, boolean isDemon) {
      ServerPlayer player = (ServerPlayer)serverPlayerPatch.getOriginal();
      ServerLevel level = (ServerLevel)player.level();
      double baseDistance = -2.5;
      Vec3 lookVec = player.getLookAngle();
      Vec3 basePos = player.getEyePosition().add(lookVec.scale(baseDistance));
      boolean isLeft = player.getRandom().nextBoolean();
      double randomAngle = player.getRandom().nextDouble() * (Math.PI / 6) + (Math.PI / 3);
      if (isLeft) {
         randomAngle = -randomAngle;
      }

      double randomRadius = player.getRandom().nextDouble() * 2.5 + 1.0;
      Vec3 right = new Vec3(-lookVec.z, 0.0, lookVec.x).normalize();
      Vec3 spawnPos = basePos.add(right.scale(Math.sin(randomAngle) * randomRadius))
         .add(0.0, -0.4 + (player.getRandom().nextDouble() * 0.8 - 0.3), 0.0);
      SinSummonedSwordEntity sword = new SinSummonedSwordEntity(
         (EntityType<? extends Mob>)EFNEntity.SIN_SUMMONED_SWORD.get(),
         (LivingEntity)serverPlayerPatch.getOriginal(),
         2.5F,
         ((ServerPlayer)serverPlayerPatch.getOriginal()).level(),
         false
      );
      sword.setLifetimeTicks(80);
      sword.setAngel(isAngel);
      sword.setDemon(isDemon);
      LivingEntity target = getInitialSummonedSwordTarget(serverPlayerPatch, player);

      if (target != null && target.isAlive()) {
         sword.setPos(spawnPos);
         sword.aimAtEntity(target);
         ((ServerPlayer)serverPlayerPatch.getOriginal()).level().addFreshEntity(sword);
      } else {
         sword.setPos(spawnPos);
         sword.setYRot(((ServerPlayer)serverPlayerPatch.getOriginal()).yHeadRotO);
         ((ServerPlayer)serverPlayerPatch.getOriginal()).level().addFreshEntity(sword);
      }

      level.sendParticles(ParticleTypes.END_ROD, spawnPos.x, spawnPos.y, spawnPos.z, 2, 0.2, 0.2, 0.2, 0.05);
      if (isAngel) {
         level.sendParticles(ParticleTypes.FIREWORK, spawnPos.x, spawnPos.y, spawnPos.z, 3, 0.2, 0.2, 0.2, 0.05);
      }

      if (isDemon) {
         level.sendParticles(ParticleTypes.SOUL, spawnPos.x, spawnPos.y, spawnPos.z, 3, 0.2, 0.2, 0.2, 0.05);
      }
   }

   @Nullable
   private static LivingEntity getInitialSummonedSwordTarget(ServerPlayerPatch serverPlayerPatch, ServerPlayer player) {
      LivingEntity target = serverPlayerPatch.getTarget();
      if (isValidInitialSummonedSwordTarget(target, player)) {
         return target;
      }

      for (LivingEntity hitTarget : serverPlayerPatch.getCurrentlyActuallyHitEntities()) {
         if (isValidInitialSummonedSwordTarget(hitTarget, player)) {
            return hitTarget;
         }
      }

      target = player.getLastHurtMob();
      return isValidInitialSummonedSwordTarget(target, player) ? target : null;
   }

   private static boolean isValidInitialSummonedSwordTarget(@Nullable LivingEntity target, ServerPlayer player) {
      return target != null && target != player && target.isAlive() && !target.isRemoved() && target.level() == player.level();
   }

   protected void defineSynchedData(SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_OWNER_UUID, Optional.empty());
      builder.define(DATA_OWNER_ID, 0);
      builder.define(SYNC_X_ROT, 0.0F);
      builder.define(SCALE, 1.0F);
      builder.define(PLAY_ANIMATION, false);
      builder.define(SHOULD_RENDER, false);
      builder.define(DATA_IS_IN_STANDBY, false);
      builder.define(DATA_NO_AIM, false);
      builder.define(DATA_HAS_HIT_TARGET, false);
      builder.define(DATA_IS_STUCK, false);
      builder.define(DATA_IS_ANGEL, false);
      builder.define(DATA_IS_DEMON, false);
      builder.define(DATA_IS_BLAST, false);
      builder.define(DATA_IS_HEAVY_RAIN, false);
      builder.define(DATA_IS_DAMOCLES_MAIN, false);
      builder.define(DATA_IS_DAMOCLES_SUB, false);
      builder.define(DATA_SHOOT_SPEED, 5);
   }

   public void setDamoclesMain(boolean damoclesMain) {
      this.entityData.set(DATA_IS_DAMOCLES_MAIN, damoclesMain);
   }

   public boolean isDamoclesMain() {
      return (Boolean)this.entityData.get(DATA_IS_DAMOCLES_MAIN);
   }

   public void setDamoclesSub(boolean damoclesSub) {
      this.entityData.set(DATA_IS_DAMOCLES_SUB, damoclesSub);
   }

   public boolean isDamoclesSub() {
      return (Boolean)this.entityData.get(DATA_IS_DAMOCLES_SUB);
   }

   public void setShootSpeed(int speed) {
      this.entityData.set(DATA_SHOOT_SPEED, speed);
   }

   public int getShootSpeed() {
      return (Integer)this.entityData.get(DATA_SHOOT_SPEED);
   }

   public boolean isInStandby() {
      return (Boolean)this.entityData.get(DATA_IS_IN_STANDBY);
   }

   public void setInStandby(boolean standby) {
      this.entityData.set(DATA_IS_IN_STANDBY, standby);
   }

   public static AttributeSupplier getDefaultAttribute() {
      return Animal.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 19.9F)
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(EpicFightAttributes.MAX_STRIKES, 10.0)
         .build();
   }

   public boolean hasHitTarget() {
      return (Boolean)this.entityData.get(DATA_HAS_HIT_TARGET);
   }

   public void setHasHitTarget(boolean hasHitTarget) {
      this.entityData.set(DATA_HAS_HIT_TARGET, hasHitTarget);
   }

   public boolean isStuckInBlock() {
      return (Boolean)this.entityData.get(DATA_IS_STUCK);
   }

   public void setStuckInBlock(boolean stuck) {
      this.entityData.set(DATA_IS_STUCK, stuck);
   }

   public boolean isAngel() {
      return (Boolean)this.entityData.get(DATA_IS_ANGEL);
   }

   public void setAngel(boolean isAngel) {
      this.entityData.set(DATA_IS_ANGEL, isAngel);
   }

   public boolean isDemon() {
      return (Boolean)this.entityData.get(DATA_IS_DEMON);
   }

   public void setDemon(boolean isDemon) {
      this.entityData.set(DATA_IS_DEMON, isDemon);
   }

   public void setBlast(boolean blast) {
      this.entityData.set(DATA_IS_BLAST, blast);
   }

   public boolean isBlast() {
      return (Boolean)this.entityData.get(DATA_IS_BLAST);
   }

   public void setHeavyRain(boolean heavyRain) {
      this.entityData.set(DATA_IS_HEAVY_RAIN, heavyRain);
   }

   public boolean isHeavyRain() {
      return (Boolean)this.entityData.get(DATA_IS_HEAVY_RAIN);
   }

   public boolean getPlayAnimation() {
      return (Boolean)this.entityData.get(PLAY_ANIMATION);
   }

   public void setPlayAnimation(boolean b) {
      this.entityData.set(PLAY_ANIMATION, b);
   }

   public boolean getShouldRender() {
      return (Boolean)this.entityData.get(SHOULD_RENDER);
   }

   public void setShouldRender(boolean b) {
      this.entityData.set(SHOULD_RENDER, b);
   }

   public void setLockedTrajectory(Vec3 trajectory) {
      this.lockedTrajectory = trajectory;
   }

   public void setDiscardDelayTicks(int ticks) {
      this.discardDelayTicks = ticks;
   }

   public boolean isNoAim() {
      return (Boolean)this.entityData.get(DATA_NO_AIM);
   }

   public void setNoAim(boolean noAim) {
      this.entityData.set(DATA_NO_AIM, noAim);
   }

   public void tame(LivingEntity livingEntity) {
      this.setOwnerUUID(livingEntity.getUUID());
      this.setOwnerID(livingEntity.getId());
   }

   public void setSyncXRot(float f) {
      this.entityData.set(SYNC_X_ROT, f);
   }

   public float getSyncXrot() {
      return (Float)this.entityData.get(SYNC_X_ROT);
   }

   @Nullable
   public LivingEntity getOwner() {
      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         if (this.level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(uuid);
            return entity instanceof LivingEntity ? (LivingEntity)entity : null;
         } else {
            Player player = this.level().getPlayerByUUID(uuid);
            if (player != null) {
               return player;
            }

            Entity entity = this.level().getEntity(this.getOwnerID());
            return entity instanceof LivingEntity ? (LivingEntity)entity : null;
         }
      } else {
         return null;
      }
   }

   public void aimAtEntity(Entity target) {
      if (target != null) {
         Vec3 eyePos = this.getEyePosition();
         Vec3 targetPos = new Vec3(target.getX(), target.getY() + target.getBbHeight() * 0.7, target.getZ());
         Vec3 direction = targetPos.subtract(eyePos).normalize();
         double horizontalDistance = direction.horizontalDistance();
         float yRot = (float)(Mth.atan2(direction.z, direction.x) * (180.0 / Math.PI)) - 90.0F;
         float xRot = (float)(-(Mth.atan2(direction.y, horizontalDistance) * (180.0 / Math.PI)));
         this.setYRot(yRot);
         this.setYBodyRot(yRot);
         this.setYHeadRot(yRot);
         this.setSyncXRot(xRot);
         this.yRotO = yRot;
         this.xRotO = xRot;
      }
   }

   private void applyInitialVelocity(float flightSpeed) {
      if (this.lockedTrajectory == Vec3.ZERO) {
         float yRotRad = this.getYRot() * (float) (Math.PI / 180.0);
         float xRotRad = this.getSyncXrot() * (float) (Math.PI / 180.0);
         double motionX = -Math.sin(yRotRad) * Math.cos(xRotRad);
         double motionY = -Math.sin(xRotRad);
         double motionZ = Math.cos(yRotRad) * Math.cos(xRotRad);
         Vec3 motion = new Vec3(motionX, motionY, motionZ).normalize().scale(flightSpeed);
         this.setDeltaMovement(motion);
         this.lockedTrajectory = motion;
      }
   }

   @Nullable
   public UUID getOwnerUUID() {
      return (UUID)((Optional)this.entityData.get(DATA_OWNER_UUID)).orElse(null);
   }

   public void setOwnerUUID(@Nullable UUID pUuid) {
      this.entityData.set(DATA_OWNER_UUID, Optional.ofNullable(pUuid));
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

   public void setLifetimeTicks(int lifetimeTicks) {
      this.lifetimeTicks = lifetimeTicks;
   }

   public int getLifetimeTicks() {
      return this.lifetimeTicks;
   }

   public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> pKey) {
      super.onSyncedDataUpdated(pKey);
      if (DATA_IS_HEAVY_RAIN.equals(pKey) && this.isHeavyRain() && this.level().isClientSide) {
         this.setXRot(90.0F);
         this.xRotO = 90.0F;
         this.setYRot(0.0F);
         this.yRotO = 0.0F;
      }

      if (DATA_IS_DAMOCLES_MAIN.equals(pKey) && this.isDamoclesMain() && this.level().isClientSide) {
         this.setXRot(90.0F);
         this.xRotO = 90.0F;
         this.setYRot(0.0F);
         this.yRotO = 0.0F;
      }
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

   protected void doPush(@NotNull Entity pEntity) {
   }

   public void push(@NotNull Entity pEntity) {
   }

   @Nullable
   public AnimationAccessor<? extends StaticAnimation> getIdleAnimation() {
      return EFNAnimations.SUMMONED_SWORD_IDLE;
   }

   @Nullable
   public AnimationAccessor<? extends StaticAnimation> getDefaultAnimation() {
      return EFNAnimations.SUMMONED_SWORD_SIN;
   }

   @Nullable
   public Armature getArmature() {
      return ArmatureAccessor.create("efn", "entity/effect/summoned_sword", Armature::new).get();
   }

   @Nullable
   public AssetAccessor<? extends SkinnedMesh> getMesh() {
      return MeshAccessor.create("efn", "entity/effect/summoned_sword", jsonModelLoader -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));
   }

   @Nullable
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("efn", "textures/entity/summoned_sword.png");
   }

   @Nullable
   public ResourceLocation getLitTexture() {
      return ResourceLocation.fromNamespaceAndPath("efn", "textures/entity/summoned_sword.png");
   }

   public boolean hurt(@NotNull DamageSource source, float p_21017_) {
      return false;
   }

   protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pState) {
   }

   public boolean causeFallDamage(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
      return false;
   }

   public boolean canSpawnSprintParticle() {
      return false;
   }
}
