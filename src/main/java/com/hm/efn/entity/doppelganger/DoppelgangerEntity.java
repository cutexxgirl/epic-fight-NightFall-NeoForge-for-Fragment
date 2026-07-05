package com.hm.efn.entity.doppelganger;

import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.entity.EFNEntity;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.EFNWeaponCategories;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.registry.entries.EpicFightAttributes;

public class DoppelgangerEntity extends TamableAnimal {
   private static final double IDLE_OFFSET_DISTANCE = 1.25;
   private static final double PINCER_DISTANCE = 2.0;
   private static final int CHECK_COOLDOWN_TICKS = 10;
   private final Random random = new Random();
   @Nullable
   private PlayerPatch<?> ownerPatch;
   private Vec3 lastOwnerPosition;
   private int currentSide = -1;
   private int checkCooldown = 0;

   public DoppelgangerEntity(ServerPlayer owner) {
      super((EntityType)EFNEntity.DOPPELGANGER.get(), owner.level());
      this.tame(owner);
      this.setPersistenceRequired();
      this.setInvulnerable(true);
      this.currentSide = this.random.nextBoolean() ? 1 : -1;
   }

   public DoppelgangerEntity(EntityType<DoppelgangerEntity> doppelgangerEntityEntityType, Level level) {
      super(doppelgangerEntityEntityType, level);
   }

   public static AttributeSupplier getDefaultAttribute() {
      return Animal.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 9.99999999E8)
         .add(Attributes.ATTACK_DAMAGE, 10.0)
         .add(EpicFightAttributes.MAX_STRIKES, 6.0)
         .build();
   }

   private static boolean isHoldingYamato(LivingEntity player) {
      return Stream.of(player.getMainHandItem(), player.getOffhandItem())
         .<CapabilityItem>map(EpicFightCapabilities::getItemStackCapability)
         .filter(Objects::nonNull)
         .anyMatch(cap -> cap.getWeaponCategory() == EFNWeaponCategories.EFN_YAMATO);
   }

   public static void summon(ServerPlayerPatch serverPlayerPatch, boolean isSneaking) {
      if (isHoldingYamato((LivingEntity)serverPlayerPatch.getOriginal())) {
         DoppelgangerEntity doppelganger = new DoppelgangerEntity((ServerPlayer)serverPlayerPatch.getOriginal());
         LivingEntity target = serverPlayerPatch.getTarget();
         Vec3 spawnPos;
         if (isSneaking && target != null && target.isAlive()) {
            Vec3 ownerPos = ((ServerPlayer)serverPlayerPatch.getOriginal()).position();
            Vec3 targetPos = target.position();
            Vec3 ownerToTarget = targetPos.subtract(ownerPos);
            if (ownerToTarget.lengthSqr() < 0.01) {
               Vec3 look = ((ServerPlayer)serverPlayerPatch.getOriginal()).getLookAngle();
               ownerToTarget = new Vec3(look.x, 0.0, look.z);
            }

            Vec3 dir = new Vec3(ownerToTarget.x, 0.0, ownerToTarget.z).normalize();
            spawnPos = targetPos.add(dir.scale(2.0));
            spawnPos = new Vec3(spawnPos.x, targetPos.y, spawnPos.z);
         } else {
            Vec3 vec3 = ((ServerPlayer)serverPlayerPatch.getOriginal()).position();
            spawnPos = new Vec3(vec3.x + 1.0, vec3.y, vec3.z);
         }

         doppelganger.moveTo(spawnPos);
         ((ServerPlayer)serverPlayerPatch.getOriginal()).serverLevel().addFreshEntity(doppelganger);
         serverPlayerPatch.playSound((SoundEvent)EFNSounds.DOPPELGANGER_OPEN.get(), 1.0F, 1.0F, 1.0F);
         spawnDarkParticles(((ServerPlayer)serverPlayerPatch.getOriginal()).serverLevel(), spawnPos);
      }
   }

   public static void spawnDarkParticles(ServerLevel level, Vec3 position) {
      ParticleOptions particle = ParticleTypes.SMOKE;
      level.sendParticles(particle, position.x, position.y + 1.0, position.z, 50, 0.5, 0.5, 0.5, 0.1);
      level.sendParticles(ParticleTypes.POOF, position.x, position.y + 1.0, position.z, 20, 0.3, 0.3, 0.3, 0.2);
      spawnCircularParticles(level, position);
   }

   public static void spawnCircularParticles(ServerLevel level, Vec3 position) {
      ParticleOptions particle = ParticleTypes.LARGE_SMOKE;
      int circleParticles = 20;
      double radius = 1.5;
      double height = 2.0;

      for (int i = 0; i < circleParticles; i++) {
         double angle = (Math.PI * 2) * i / circleParticles;
         double x = position.x + radius * Math.cos(angle);
         double z = position.z + radius * Math.sin(angle);
         double y = position.y + height;
         level.sendParticles(particle, x, y, z, 1, 0.1, 0.1, 0.1, 0.05);
      }
   }

   @Nullable
   public PlayerPatch<?> getOwnerPatch() {
      if (this.ownerPatch != null) {
         return this.ownerPatch;
      } else if (this.getOwner() != null) {
         this.ownerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(this.getOwner(), PlayerPatch.class);
         return this.ownerPatch;
      } else {
         return null;
      }
   }

   public void push(@NotNull Entity pEntity) {
   }

   protected void pushEntities() {
   }

   protected void doPush(@NotNull Entity p_20971_) {
   }

   public boolean isPushable() {
      return false;
   }

   public boolean shouldBeSaved() {
      return true;
   }

   public void tame(@NotNull Player player) {
      super.tame(player);
      this.setItemSlot(EquipmentSlot.MAINHAND, player.getItemBySlot(EquipmentSlot.MAINHAND).copy());
   }

   public boolean hurt(@NotNull DamageSource source, float p_27568_) {
      return false;
   }

   public boolean canBeSeenAsEnemy() {
      return false;
   }

   @Nullable
   public AgeableMob getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {
      return null;
   }

   public boolean isFood(@NotNull ItemStack stack) {
      return false;
   }

   public void tick() {
      super.tick();
      if (this.getOwner() == null || !this.getOwner().isAlive() || this.getOwner().getHealth() <= 0.0F) {
         this.remove(RemovalReason.DISCARDED);
      } else if (!isHoldingYamato(this.getOwner()) && !this.getOwner().level().isClientSide()) {
         ServerPlayerPatch serverPlayerPatch = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(this.getOwner(), ServerPlayerPatch.class);
         if (serverPlayerPatch != null) {
            serverPlayerPatch.playSound((SoundEvent)EFNSounds.DOPPELGANGER_CLOSE.get(), 1.0F, 1.0F, 1.0F);
            spawnDarkParticles(((ServerPlayer)serverPlayerPatch.getOriginal()).serverLevel(), this.position());
         }

         this.remove(RemovalReason.DISCARDED);
      } else {
         LivingEntity owner = this.getOwner();
         this.checkCooldown--;
         if (this.checkCooldown <= 0) {
            this.checkAndAdjustSide(owner);
            this.checkCooldown = 10;
         }

         this.setNoAi(true);
         this.synchronizeWithOwnerAndTarget(owner);
         this.preventBlockCollision();
      }
   }

   private void synchronizeWithOwnerAndTarget(LivingEntity owner) {
      PlayerPatch<?> patch = this.getOwnerPatch();
      DoppelgangerPatch doppelgangerPatch = (DoppelgangerPatch)EpicFightCapabilities.getEntityPatch(this, DoppelgangerPatch.class);
      if (patch != null && doppelgangerPatch != null) {
         if (!doppelgangerPatch.getEntityState().inaction()) {
            SkillContainer skill = patch.getSkill(SkillSlots.WEAPON_INNATE);
            LivingEntity target = patch.getTarget();
            boolean hasActiveTarget = target != null && target.isAlive();
            boolean shouldPincer = false;
            if (hasActiveTarget) {
               double distOT = owner.distanceTo(target);
               double distTD = this.distanceTo(target);
               double distOD = owner.distanceTo(this);
               if (distOT + distTD <= distOD + 2.5 && distOD > distOT) {
                  shouldPincer = true;
               }
            }

            Vec3 targetPosition;
            if (shouldPincer) {
               Vec3 ownerToTarget = target.position().subtract(owner.position());
               Vec3 dir = new Vec3(ownerToTarget.x, 0.0, ownerToTarget.z).normalize();
               Vec3 sideOffset = new Vec3(-dir.z, 0.0, dir.x).scale(this.currentSide * 0.8);
               targetPosition = target.position().add(dir.scale(2.0)).add(sideOffset);
               targetPosition = new Vec3(targetPosition.x, target.getY(), targetPosition.z);
            } else {
               Vec3 offset = this.calculateIdleOffsetPosition(owner);
               targetPosition = owner.position().add(offset);
            }

            int style = skill.getDataManager().hasData(EFNSKillDataKeys.DOPPELGANGER_STYLE)
               ? (Integer)skill.getDataManager().getDataValue(EFNSKillDataKeys.DOPPELGANGER_STYLE)
               : 0;
            if (style == 1) {
               this.setPos(
                  this.lerp(this.getX(), targetPosition.x, 0.4),
                  this.lerp(this.getY(), targetPosition.y, 0.4),
                  this.lerp(this.getZ(), targetPosition.z, 0.4)
               );
            } else {
               this.setPos(
                  this.lerp(this.getX(), targetPosition.x, 0.8),
                  this.lerp(this.getY(), targetPosition.y, 0.8),
                  this.lerp(this.getZ(), targetPosition.z, 0.8)
               );
            }

            if (hasActiveTarget) {
               if (!doppelgangerPatch.getEntityState().turningLocked()) {
                  this.lookAt(Anchor.EYES, new Vec3(target.getX(), target.getEyeY(), target.getZ()));
                  this.yBodyRot = this.getYRot();
                  this.yHeadRot = this.getYRot();
               }
            } else if (!doppelgangerPatch.getEntityState().turningLocked()) {
               this.xRotO = owner.xRotO;
               this.yRotO = owner.yRotO;
               this.setYRot(owner.getYRot());
               this.setXRot(owner.getXRot());
               this.setYHeadRot(owner.getYHeadRot());
               this.yBodyRot = owner.yBodyRot;
            }

            this.setShiftKeyDown(owner.isShiftKeyDown());
            this.setSprinting(owner.isSprinting());
            this.setSwimming(owner.isSwimming());
            this.setPose(owner.getPose());
            this.lastOwnerPosition = owner.position();
            boolean hasDelay = skill.getDataManager().hasData(EFNSKillDataKeys.DOPPELGANGER_DELAY)
               && Boolean.TRUE.equals(skill.getDataManager().getDataValue(EFNSKillDataKeys.DOPPELGANGER_DELAY));
            double tpDistance = hasDelay ? 20.0 : 15.0;
            if (this.distanceTo(owner) > tpDistance) {
               this.teleportTo(owner.getX(), owner.getY(), owner.getZ());
            }
         }
      }
   }

   private void checkAndAdjustSide(LivingEntity owner) {
      Vec3 currentOffset = this.calculateOffsetForSideWithDistance(owner, this.currentSide, 1.25);
      Vec3 currentPos = owner.position().add(currentOffset);
      if (this.isPositionBlocked(currentPos)) {
         this.currentSide *= -1;
      }
   }

   private Vec3 calculateIdleOffsetPosition(LivingEntity owner) {
      return this.calculateOffsetForSideWithDistance(owner, this.currentSide, 1.25);
   }

   private Vec3 calculateOffsetForSideWithDistance(LivingEntity owner, int side, double distance) {
      double yawRad = Math.toRadians(owner.getYRot());
      double angleOffset = side == 1 ? Math.PI / 2 : -Math.PI / 2;
      double offsetX = -Math.sin(yawRad + angleOffset) * distance;
      double offsetZ = Math.cos(yawRad + angleOffset) * distance;
      return new Vec3(offsetX, 0.0, offsetZ);
   }

   private boolean isPositionBlocked(Vec3 position) {
      BlockPos blockPos = BlockPos.containing(position);
      return !this.level().isEmptyBlock(blockPos) || !this.level().isEmptyBlock(blockPos.above());
   }

   private double lerp(double start, double end, double factor) {
      return start + (end - start) * factor;
   }

   private void preventBlockCollision() {
      BlockPos currentPos = this.blockPosition();
      if (!this.level().isEmptyBlock(currentPos)) {
         this.setPos(this.getX(), this.getY() + 0.2, this.getZ());
      }
   }
}
