package com.hm.efn.entity.effect;

import com.hm.efn.entity.EFNEntity;
import com.hm.efn.entity.effect.SinSummonedSwordEntity.SinSummonedSwordEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.bus.api.SubscribeEvent;
import com.hm.efn.compat.neoforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.StunType;

public class DamoclesSwordEntity extends Entity {
   private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(DamoclesSwordEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(DamoclesSwordEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> MAIN_SWORD_ID = SynchedEntityData.defineId(DamoclesSwordEntity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Byte> CONTROLLER_STATE = SynchedEntityData.defineId(DamoclesSwordEntity.class, EntityDataSerializers.BYTE);
   private static final EntityDataAccessor<Float> TARGET_X = SynchedEntityData.defineId(DamoclesSwordEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> TARGET_Y = SynchedEntityData.defineId(DamoclesSwordEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> TARGET_Z = SynchedEntityData.defineId(DamoclesSwordEntity.class, EntityDataSerializers.FLOAT);
   private static final EntityDataAccessor<Float> HEIGHT_OFFSET = SynchedEntityData.defineId(DamoclesSwordEntity.class, EntityDataSerializers.FLOAT);
   private UUID ownerUUID;
   private UUID targetUUID;
   private UUID mainSwordUUID;
   private final List<UUID> childSwords = new ArrayList<>();
   private int stateTickCounter = 0;
   private Vec3 currentTargetPos;
   private double currentFallVelocity = -1.0;
   private boolean isRegisteredToBus = false;
   private final Object eventBusLock = new Object();

   public DamoclesSwordEntity(EntityType<?> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.noCulling = true;
      this.noPhysics = true;
   }

   public DamoclesSwordEntity(Level level, LivingEntity owner) {
      super((EntityType)EFNEntity.DAMOCLES_SWORD.get(), level);
      this.setOwner(owner);
      this.noCulling = true;
      this.noPhysics = true;
      Vec3 lookVec = owner.getLookAngle();
      Vec3 horizontalLook = new Vec3(lookVec.x, 0.0, lookVec.z).normalize();
      if (horizontalLook.lengthSqr() == 0.0) {
         horizontalLook = new Vec3(0.0, 0.0, 1.0);
      }

      this.currentTargetPos = owner.position().add(horizontalLook.scale(3.0));
      this.setTargetPos(this.currentTargetPos);
      this.setPos(this.currentTargetPos.x, this.currentTargetPos.y + 7.0, this.currentTargetPos.z);
   }

   public DamoclesSwordEntity(Level level, LivingEntity owner, LivingEntity target) {
      super((EntityType)EFNEntity.DAMOCLES_SWORD.get(), level);
      this.setOwner(owner);
      this.noCulling = true;
      this.noPhysics = true;
      this.setTarget(target);
      this.currentTargetPos = target.position();
      this.setTargetPos(this.currentTargetPos);
      this.setPos(this.currentTargetPos.x, this.currentTargetPos.y + 7.0, this.currentTargetPos.z);
   }

   public static void summon(Level level, LivingEntity owner) {
      if (!level.isClientSide) {
         DamoclesSwordEntity controller = new DamoclesSwordEntity(level, owner);
         level.addFreshEntity(controller);
         controller.spawnMainSword();
      }
   }

   public static void summon(Level level, LivingEntity owner, LivingEntity target) {
      if (!level.isClientSide) {
         DamoclesSwordEntity controller = new DamoclesSwordEntity(level, owner, target);
         level.addFreshEntity(controller);
         controller.spawnMainSword();
      }
   }

   protected void defineSynchedData(SynchedEntityData.Builder builder) {
      builder.define(OWNER_ID, -1);
      builder.define(TARGET_ID, -1);
      builder.define(MAIN_SWORD_ID, -1);
      builder.define(CONTROLLER_STATE, (byte)DamoclesSwordEntity.State.STANDBY.ordinal());
      builder.define(TARGET_X, 0.0F);
      builder.define(TARGET_Y, 0.0F);
      builder.define(TARGET_Z, 0.0F);
      builder.define(HEIGHT_OFFSET, 7.0F);
   }

   private void spawnMainSword() {
      LivingEntity owner = this.getOwner();
      if (owner != null && this.level() instanceof ServerLevel serverLevel) {
         SinSummonedSwordEntity mainSword = new SinSummonedSwordEntity(
            (EntityType<? extends Mob>)EFNEntity.SIN_SUMMONED_SWORD.get(), owner, 3.5F, this.level(), true
         );
         mainSword.setPos(this.getX(), this.getY(), this.getZ());
         mainSword.setDamoclesMain(true);
         mainSword.setNoAim(true);
         mainSword.setDeltaMovement(Vec3.ZERO);
         mainSword.setLifetimeTicks(1000);
         this.level().addFreshEntity(mainSword);
         this.mainSwordUUID = mainSword.getUUID();
         this.entityData.set(MAIN_SWORD_ID, mainSword.getId());

         for (int i = 0; i < 18; i++) {
            double angle = i * (Math.PI / 9);
            double dx = Math.cos(angle) * 0.75;
            double dz = Math.sin(angle) * 0.75;
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, mainSword.getX(), mainSword.getY(), mainSword.getZ(), 0, dx, 0.0, dz, 0.1);
            serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, mainSword.getX(), mainSword.getY(), mainSword.getZ(), 0, dx, 0.0, dz, 0.1);
         }
      } else {
         this.discard();
      }
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide) {
         synchronized (this.eventBusLock) {
            if (!this.isRegisteredToBus && !this.isRemoved()) {
               NeoForge.EVENT_BUS.register(this);
               this.isRegisteredToBus = true;
            }
         }
      } else {
         LivingEntity owner = this.getOwner();
         if (owner != null && !owner.isDeadOrDying()) {
            this.pinTargetInPlace();
            switch (this.getCurrentState()) {
               case STANDBY:
                  this.tickStandby();
                  break;
               case WINDUP:
                  this.tickWindup();
                  break;
               case LAUNCHING:
                  this.tickLaunching();
                  break;
               case SPLITTING:
                  this.tickSplitting();
                  break;
               case FINISHED:
                  this.tickFinished();
            }
         } else {
            this.cleanup();
         }
      }
   }

   @SubscribeEvent
   public void onClientTick(Post event) {
      if (true) {
         if (!this.isRemoved() && this.level().isClientSide) {
            int mainSwordId = (Integer)this.entityData.get(MAIN_SWORD_ID);
            if (mainSwordId != -1) {
               Entity entity = this.level().getEntity(mainSwordId);
               DamoclesSwordEntity.State currentState = this.getCurrentState();
               if (entity instanceof SinSummonedSwordEntity mainSword) {
                  float tx = (Float)this.entityData.get(TARGET_X);
                  float ty = (Float)this.entityData.get(TARGET_Y);
                  float tz = (Float)this.entityData.get(TARGET_Z);
                  if (currentState == DamoclesSwordEntity.State.STANDBY || currentState == DamoclesSwordEntity.State.WINDUP) {
                     float hOffset = (Float)this.entityData.get(HEIGHT_OFFSET);
                     Vec3 hoverPos = new Vec3(tx, ty + hOffset, tz);
                     mainSword.setPosRaw(hoverPos.x, hoverPos.y, hoverPos.z);
                     mainSword.xo = hoverPos.x;
                     mainSword.yo = hoverPos.y;
                     mainSword.zo = hoverPos.z;
                     mainSword.setDeltaMovement(Vec3.ZERO);
                  } else if (currentState == DamoclesSwordEntity.State.SPLITTING || currentState == DamoclesSwordEntity.State.FINISHED) {
                     mainSword.setPosRaw(tx, ty + 2.0, tz);
                     mainSword.xo = tx;
                     mainSword.yo = ty + 2.0;
                     mainSword.zo = tz;
                     mainSword.setDeltaMovement(Vec3.ZERO);
                  }
               }
            }
         } else {
            synchronized (this.eventBusLock) {
               if (this.isRegisteredToBus) {
                  NeoForge.EVENT_BUS.unregister(this);
                  this.isRegisteredToBus = false;
               }
            }
         }
      }
   }

   private void pinTargetInPlace() {
      LivingEntity target = this.getTarget();
      if (target != null && target.isAlive() && target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE) < 0.5) {
         Vec3 center = new Vec3(
            ((Float)this.entityData.get(TARGET_X)).floatValue(),
            ((Float)this.entityData.get(TARGET_Y)).floatValue(),
            ((Float)this.entityData.get(TARGET_Z)).floatValue()
         );
         target.moveTo(center.x, center.y, center.z);
         target.setDeltaMovement(Vec3.ZERO);
         target.hasImpulse = true;
      }
   }

   private void tickStandby() {
      this.stateTickCounter++;
      LivingEntity target = this.getTarget();
      if (target == null && this.targetUUID != null) {
         this.setCurrentState(DamoclesSwordEntity.State.LAUNCHING);
         this.stateTickCounter = 0;
      } else {
         this.updateMainSwordPos(this.currentTargetPos.add(0.0, ((Float)this.entityData.get(HEIGHT_OFFSET)).floatValue(), 0.0));
         if (this.stateTickCounter >= 6) {
            this.setCurrentState(DamoclesSwordEntity.State.WINDUP);
            this.stateTickCounter = 0;
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BELL_RESONATE, SoundSource.PLAYERS, 1.5F, 1.0F);
         }
      }
   }

   private void tickWindup() {
      this.stateTickCounter++;
      float currentOffset = (Float)this.entityData.get(HEIGHT_OFFSET);
      float newOffset = currentOffset + 0.25F;
      this.entityData.set(HEIGHT_OFFSET, newOffset);
      this.updateMainSwordPos(this.currentTargetPos.add(0.0, newOffset, 0.0));
      if (this.stateTickCounter >= 3) {
         this.setCurrentState(DamoclesSwordEntity.State.LAUNCHING);
         this.stateTickCounter = 0;
         this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS, 3.0F, 0.1F);
         if (this.level() instanceof ServerLevel serverLevel && serverLevel.getEntity(this.mainSwordUUID) instanceof SinSummonedSwordEntity mainSword) {
            mainSword.setInStandby(false);
         }
      }
   }

   private void tickLaunching() {
      ServerLevel serverLevel = (ServerLevel)this.level();
      if (!(serverLevel.getEntity(this.mainSwordUUID) instanceof SinSummonedSwordEntity mainSword)) {
         this.cleanup();
      } else {
         this.currentFallVelocity = Math.max(-15.0, this.currentFallVelocity - 2.5);
         Vec3 currentPos = mainSword.position();
         Vec3 nextPos = currentPos.add(0.0, this.currentFallVelocity, 0.0);
         serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, currentPos.x, currentPos.y + 2.0, currentPos.z, 8, 0.2, 1.0, 0.2, 0.02);
         serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, currentPos.x, currentPos.y + 2.0, currentPos.z, 5, 0.3, 1.5, 0.3, 0.05);
         BlockHitResult hit = serverLevel.clip(new ClipContext(currentPos, nextPos, Block.COLLIDER, Fluid.NONE, mainSword));
         boolean isHit = hit.getType() == Type.BLOCK || nextPos.y <= this.currentTargetPos.y;
         if (isHit) {
            Vec3 hitPos = hit.getType() == Type.BLOCK ? hit.getLocation() : new Vec3(currentPos.x, this.currentTargetPos.y, currentPos.z);
            this.setTargetPos(hitPos);
            this.updateMainSwordPos(hitPos.add(0.0, 2.0, 0.0));
            this.triggerSlamEffect(hitPos);
            this.setCurrentState(DamoclesSwordEntity.State.SPLITTING);
            this.stateTickCounter = 0;
         } else {
            this.updateMainSwordPos(nextPos);
         }
      }
   }

   private void triggerSlamEffect(Vec3 hitPos) {
      ServerLevel serverLevel = (ServerLevel)this.level();
      LivingEntity owner = this.getOwner();
      BlockPos hitBlockPos = new BlockPos((int)hitPos.x, (int)(hitPos.y + 2.0), (int)hitPos.z);
      BlockState blockState = serverLevel.getBlockState(hitBlockPos);
      SoundType soundType = blockState.getSoundType(serverLevel, hitBlockPos, null);
      serverLevel.playSound(null, hitPos.x, hitPos.y, hitPos.z, soundType.getBreakSound(), SoundSource.PLAYERS, 2.5F, 0.5F);
      serverLevel.playSound(null, hitPos.x, hitPos.y, hitPos.z, SoundEvents.WITHER_BREAK_BLOCK, SoundSource.PLAYERS, 1.5F, 0.5F);
      if (owner != null) {
         LevelUtil.circleSlamFracture(owner, serverLevel, new Vec3(hitPos.x, hitPos.y - 0.5, hitPos.z), 2.0, true, true, false);
      }

      serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, hitPos.x, hitPos.y + 0.5, hitPos.z, 50, 2.0, 0.5, 2.0, 0.15);
      serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, hitPos.x, hitPos.y + 0.5, hitPos.z, 20, 1.5, 0.5, 1.5, 0.1);
      AABB stunBox = new AABB(hitPos.x, hitPos.y, hitPos.z, hitPos.x, hitPos.y, hitPos.z).inflate(3.5);

      for (LivingEntity t : serverLevel.getEntitiesOfClass(LivingEntity.class, stunBox, e -> e.isAlive() && e != owner)) {
         t.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 255, false, true, true));
      }

      double radius = 2.5;
      double halfAngleRad = Math.toRadians(20.0);
      double childHeightOffset = radius / Math.tan(halfAngleRad);
      Vec3 absoluteAimTarget = hitPos.add(0.0, 1.1F, 0.0);

      for (int i = 0; i < 5; i++) {
         double angle = (Math.PI * 2.0 / 5.0) * i;
         double cx = hitPos.x + radius * Math.cos(angle);
         double cy = hitPos.y + childHeightOffset + -1.8F;
         double cz = hitPos.z + radius * Math.sin(angle);
         Vec3 spawnPos = new Vec3(cx, cy, cz);
         SinSummonedSwordEntity child = new SinSummonedSwordEntity(
            (EntityType<? extends Mob>)EFNEntity.SIN_SUMMONED_SWORD.get(), owner, 1.9F, serverLevel, true
         );
         child.setPos(spawnPos);
         child.setDamoclesSub(true);
         child.setNoAim(true);
         child.setInStandby(true);
         child.setLifetimeTicks(1000);
         Vec3 aimDir = absoluteAimTarget.subtract(spawnPos).normalize();
         float yRot = (float)(Mth.atan2(aimDir.z, aimDir.x) * (180.0 / Math.PI)) - 90.0F;
         float xRot = (float)(-(Mth.atan2(aimDir.y, aimDir.horizontalDistance()) * (180.0 / Math.PI)));
         child.setYRot(yRot);
         child.setXRot(xRot);
         child.setSyncXRot(xRot);
         child.setYBodyRot(yRot);
         child.setYHeadRot(yRot);
         child.yRotO = yRot;
         child.xRotO = xRot;
         serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, spawnPos.x, spawnPos.y, spawnPos.z, 15, 0.2, 0.5, 0.2, 0.05);
         serverLevel.addFreshEntity(child);
         this.childSwords.add(child.getUUID());
      }
   }

   private void tickSplitting() {
      this.stateTickCounter++;
      ServerLevel serverLevel = (ServerLevel)this.level();
      Vec3 epicenter = new Vec3(
         ((Float)this.entityData.get(TARGET_X)).floatValue(),
         ((Float)this.entityData.get(TARGET_Y)).floatValue(),
         ((Float)this.entityData.get(TARGET_Z)).floatValue()
      );
      this.updateMainSwordPos(epicenter.add(0.0, 2.0, 0.0));
      Vec3 absoluteAimPoint = epicenter.add(0.0, 1.1F, 0.0);
      List<SinSummonedSwordEntity> standbyChildren = new ArrayList<>();

      for (UUID childId : this.childSwords) {
         if (serverLevel.getEntity(childId) instanceof SinSummonedSwordEntity child && child.isInStandby()) {
            standbyChildren.add(child);
            Vec3 aimDir = absoluteAimPoint.subtract(child.position()).normalize();
            float newYaw = (float)(Mth.atan2(aimDir.z, aimDir.x) * (180.0 / Math.PI)) - 90.0F;
            float newPitch = (float)(-(Mth.atan2(aimDir.y, aimDir.horizontalDistance()) * (180.0 / Math.PI)));
            child.xRotO = child.getXRot();
            child.yRotO = child.getYRot();
            child.setYRot(newYaw);
            child.setXRot(newPitch);
            child.setSyncXRot(newPitch);
            child.setYBodyRot(newYaw);
            child.setYHeadRot(newYaw);
         }
      }

      if (this.stateTickCounter >= 15) {
         if (standbyChildren.isEmpty()) {
            serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, epicenter.x, epicenter.y + 1.0, epicenter.z, 0, 0.0, 0.0, 0.0, 0.0);
            this.setCurrentState(DamoclesSwordEntity.State.FINISHED);
            this.stateTickCounter = 0;
         } else if ((this.stateTickCounter - 15) % 2 == 0) {
            int randomIndex = this.level().random.nextInt(standbyChildren.size());
            SinSummonedSwordEntity toLaunch = standbyChildren.get(randomIndex);
            Vec3 aimDir = absoluteAimPoint.subtract(toLaunch.position()).normalize();
            Vec3 velocity = aimDir.scale(3.5);
            toLaunch.setLockedTrajectory(velocity);
            toLaunch.setInStandby(false);
            toLaunch.launch(null);
            toLaunch.playSound(SoundEvents.TRIDENT_THROW.value(), 1.5F, 1.2F);
         }
      }
   }

   private void tickFinished() {
      this.stateTickCounter++;
      Vec3 groundCenter = new Vec3(
         ((Float)this.entityData.get(TARGET_X)).floatValue(),
         ((Float)this.entityData.get(TARGET_Y)).floatValue(),
         ((Float)this.entityData.get(TARGET_Z)).floatValue()
      );
      this.updateMainSwordPos(groundCenter.add(0.0, 2.0, 0.0));
      if (this.stateTickCounter >= 15) {
         ServerLevel serverLevel = (ServerLevel)this.level();
         LivingEntity owner = this.getOwner();
         if (owner != null) {
            DamageSource ds = EpicFightDamageSources.mobAttack(owner)
               .setAnimation(null)
               .setStunType(StunType.LONG)
               .setBaseImpact(2.0F)
               .setBaseArmorNegation(100.0F)
               .addRuntimeTag(EpicFightDamageTypeTags.FINISHER);
            AABB blastBox = new AABB(
                  groundCenter.x, groundCenter.y, groundCenter.z, groundCenter.x, groundCenter.y, groundCenter.z
               )
               .inflate(4.5);

            for (LivingEntity t : serverLevel.getEntitiesOfClass(LivingEntity.class, blastBox, e -> e.isAlive() && e != owner)) {
               t.hurt(ds, 7.0F);
            }
         }

         serverLevel.playSound(null, groundCenter.x, groundCenter.y, groundCenter.z, SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 3.5F, 1.2F);
         serverLevel.playSound(null, groundCenter.x, groundCenter.y, groundCenter.z, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.HOSTILE, 3.0F, 0.8F);

         for (int i = 0; i < 36; i++) {
            double angle = i * 0.08726646259971647;
            double dx = Math.cos(angle) * 3.5;
            double dz = Math.sin(angle) * 3.5;
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, groundCenter.x, groundCenter.y + 0.5, groundCenter.z, 0, dx, 0.05, dz, 1.0);
            serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, groundCenter.x, groundCenter.y + 0.5, groundCenter.z, 0, dx, 0.05, dz, 1.0);
         }

         serverLevel.sendParticles(ParticleTypes.SMOKE, groundCenter.x, groundCenter.y + 0.5, groundCenter.z, 100, 2.5, 1.5, 2.5, 0.2);
         serverLevel.sendParticles(ParticleTypes.POOF, groundCenter.x, groundCenter.y + 0.5, groundCenter.z, 50, 2.0, 1.0, 2.0, 0.2);
         int circleParticles = 15;
         double smokeRadius = 6.0;

         for (int i = 0; i < circleParticles; i++) {
            double angle = (Math.PI * 2) * i / circleParticles;
            double x = groundCenter.x + smokeRadius * Math.cos(angle);
            double z = groundCenter.z + smokeRadius * Math.sin(angle);
            double y = groundCenter.y + 0.5;
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 2, 0.2, 0.5, 0.2, 0.05);
            serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, x, y, z, 2, 0.2, 0.5, 0.2, 0.05);
         }

         for (int i = 0; i < 15; i++) {
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, groundCenter.x, groundCenter.y + i * 0.5, groundCenter.z, 5, 0.3, 0.5, 0.3, 0.01);
            serverLevel.sendParticles(ParticleTypes.SCULK_SOUL, groundCenter.x, groundCenter.y + i * 0.5, groundCenter.z, 5, 0.3, 0.5, 0.3, 0.01);
         }

         this.cleanup();
      }
   }

   private void updateMainSwordPos(Vec3 pos) {
      if (this.level() instanceof ServerLevel serverLevel && serverLevel.getEntity(this.mainSwordUUID) instanceof SinSummonedSwordEntity mainSword) {
         mainSword.setPos(pos);
         mainSword.setDeltaMovement(Vec3.ZERO);
      }
   }

   private void cleanup() {
      if (this.level() instanceof ServerLevel serverLevel) {
         Entity main = serverLevel.getEntity(this.mainSwordUUID);
         if (main != null) {
            main.discard();
         }

         for (UUID childId : this.childSwords) {
            Entity child = serverLevel.getEntity(childId);
            if (child != null) {
               child.discard();
            }
         }
      }

      this.discard();
   }

   public DamoclesSwordEntity.State getCurrentState() {
      return DamoclesSwordEntity.State.values()[this.entityData.get(CONTROLLER_STATE)];
   }

   private void setCurrentState(DamoclesSwordEntity.State state) {
      this.entityData.set(CONTROLLER_STATE, (byte)state.ordinal());
   }

   public void setOwner(LivingEntity owner) {
      this.ownerUUID = owner.getUUID();
      this.entityData.set(OWNER_ID, owner.getId());
   }

   @Nullable
   public LivingEntity getOwner() {
      if (!this.level().isClientSide && this.ownerUUID != null) {
         Entity entity = ((ServerLevel)this.level()).getEntity(this.ownerUUID);
         return entity instanceof LivingEntity ? (LivingEntity)entity : null;
      } else if (this.level().isClientSide) {
         Entity entity = this.level().getEntity((Integer)this.entityData.get(OWNER_ID));
         return entity instanceof LivingEntity ? (LivingEntity)entity : null;
      } else {
         return null;
      }
   }

   public void setTarget(LivingEntity target) {
      this.targetUUID = target.getUUID();
      this.entityData.set(TARGET_ID, target.getId());
   }

   @Nullable
   public LivingEntity getTarget() {
      return this.targetUUID != null && this.level() instanceof ServerLevel ? (LivingEntity)((ServerLevel)this.level()).getEntity(this.targetUUID) : null;
   }

   private void setTargetPos(Vec3 pos) {
      this.entityData.set(TARGET_X, (float)pos.x);
      this.entityData.set(TARGET_Y, (float)pos.y);
      this.entityData.set(TARGET_Z, (float)pos.z);
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

   public void remove(@NotNull RemovalReason pReason) {
      super.remove(pReason);
      synchronized (this.eventBusLock) {
         if (this.level().isClientSide && this.isRegisteredToBus) {
            NeoForge.EVENT_BUS.unregister(this);
            this.isRegisteredToBus = false;
         }
      }
   }

   protected void readAdditionalSaveData(CompoundTag pCompound) {
      if (pCompound.hasUUID("Owner")) {
         this.ownerUUID = pCompound.getUUID("Owner");
      }

      if (pCompound.hasUUID("Target")) {
         this.targetUUID = pCompound.getUUID("Target");
      }

      if (pCompound.hasUUID("MainSword")) {
         this.mainSwordUUID = pCompound.getUUID("MainSword");
      }

      if (pCompound.contains("State")) {
         this.setCurrentState(DamoclesSwordEntity.State.valueOf(pCompound.getString("State")));
      }

      this.stateTickCounter = pCompound.getInt("StateTicker");
      this.currentTargetPos = new Vec3(pCompound.getDouble("TX"), pCompound.getDouble("TY"), pCompound.getDouble("TZ"));
      if (pCompound.contains("HeightOffset")) {
         this.entityData.set(HEIGHT_OFFSET, pCompound.getFloat("HeightOffset"));
      }

      this.childSwords.clear();
      ListTag childListTag = pCompound.getList("ChildSwords", 8);
      childListTag.forEach(tag -> this.childSwords.add(UUID.fromString(tag.getAsString())));
   }

   protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      if (this.ownerUUID != null) {
         pCompound.putUUID("Owner", this.ownerUUID);
      }

      if (this.targetUUID != null) {
         pCompound.putUUID("Target", this.targetUUID);
      }

      if (this.mainSwordUUID != null) {
         pCompound.putUUID("MainSword", this.mainSwordUUID);
      }

      pCompound.putString("State", this.getCurrentState().name());
      pCompound.putInt("StateTicker", this.stateTickCounter);
      if (this.currentTargetPos != null) {
         pCompound.putDouble("TX", this.currentTargetPos.x);
         pCompound.putDouble("TY", this.currentTargetPos.y);
         pCompound.putDouble("TZ", this.currentTargetPos.z);
      }

      pCompound.putFloat("HeightOffset", (Float)this.entityData.get(HEIGHT_OFFSET));
      ListTag childListTag = new ListTag();
      this.childSwords.forEach(uuid -> childListTag.add(StringTag.valueOf(uuid.toString())));
      pCompound.put("ChildSwords", childListTag);
   }

   @NotNull
   public Packet<ClientGamePacketListener> getAddEntityPacket() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   public static class Config {
      public static final int STANDBY_TICKS = 6;
      public static final int WINDUP_TICKS = 3;
      public static final int CHILD_LAUNCH_START_DELAY = 15;
      public static final int CHILD_LAUNCH_INTERVAL = 2;
      public static final int FINISHED_TICKS = 15;
      public static final float MAIN_SWORD_SCALE = 3.5F;
      public static final float NO_TARGET_DISTANCE = 3.0F;
      public static final float SPAWN_HEIGHT_OFFSET = 7.0F;
      public static final float TARGET_SPAWN_HEIGHT = 7.0F;
      public static final double GRAVITY_ACCELERATION = 2.5;
      public static final double MAX_FALL_SPEED = -15.0;
      public static final int CHILD_SWORD_COUNT = 5;
      public static final float CHILD_SWORD_Y_OFFSET = -1.8F;
      public static final float CHILD_SWORD_SCALE = 1.9F;
      public static final double CONE_BASE_RADIUS = 2.5;
      public static final double CONE_FULL_ANGLE = 40.0;
      public static final double CHILD_LAUNCH_SPEED = 3.5;
      public static final double SLAM_STUN_RADIUS = 3.5;
      public static final int SLAM_STUN_TICKS = 60;
      public static final double EXPLOSION_RADIUS = 4.5;
      public static final float EXPLOSION_DAMAGE = 7.0F;
      public static final float ARMOR_NEGATION = 100.0F;
      public static final float BASE_IMPACT = 2.0F;
   }

   public enum State {
      STANDBY,
      WINDUP,
      LAUNCHING,
      SPLITTING,
      FINISHED;
   }
}
