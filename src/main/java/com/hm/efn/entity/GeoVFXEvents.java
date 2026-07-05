package com.hm.efn.entity;

import java.util.function.Supplier;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;

public class GeoVFXEvents {
   public static InTimeEvent createVFX(
      Supplier<EntityType<? extends GeoVFXEntity>> entityType,
      int startFrame,
      float scale,
      float damage,
      int attackInterval,
      int attackCount,
      double xOffset,
      double yOffset,
      double zOffset,
      float yRotOffset
   ) {
      return createVFXInternal(entityType, startFrame, scale, damage, attackInterval, attackCount, xOffset, yOffset, zOffset, yRotOffset, 0.0F, 0.0F);
   }

   public static InTimeEvent createVFX(
      Supplier<EntityType<? extends GeoVFXEntity>> entityType, int startFrame, float scale, float damage, int attackInterval, int attackCount
   ) {
      return createVFX(entityType, startFrame, scale, damage, attackInterval, attackCount, 0.0, 0.0, 0.0, 0.0F);
   }

   public static InTimeEvent createVFX(
      Supplier<EntityType<? extends GeoVFXEntity>> entityType,
      int startFrame,
      float scale,
      float damage,
      int attackInterval,
      int attackCount,
      double xOffset,
      double yOffset,
      double zOffset
   ) {
      return createVFX(entityType, startFrame, scale, damage, attackInterval, attackCount, xOffset, yOffset, zOffset, 0.0F);
   }

   public static InTimeEvent createVFX(
      Supplier<EntityType<? extends GeoVFXEntity>> entityType,
      int startFrame,
      float scale,
      float damage,
      int attackInterval,
      int attackCount,
      float yRotOffset
   ) {
      return createVFX(entityType, startFrame, scale, damage, attackInterval, attackCount, 0.0, 0.0, 0.0, yRotOffset);
   }

   public static InTimeEvent createVFX(Supplier<EntityType<? extends GeoVFXEntity>> entityType, int startFrame, float scale, float damage) {
      return createVFX(entityType, startFrame, scale, damage, 3, 8);
   }

   public static InTimeEvent createVFX(Supplier<EntityType<? extends GeoVFXEntity>> entityType, int startFrame) {
      return createVFX(entityType, startFrame, 1.0F, 1.0F);
   }

   public static InTimeEvent createMovingVFX(
      Supplier<EntityType<? extends GeoVFXEntity>> entityType,
      int startFrame,
      float scale,
      float damage,
      int attackInterval,
      int attackCount,
      double xOffset,
      double yOffset,
      double zOffset,
      float yRotOffset,
      float moveSpeed,
      float moveDistance
   ) {
      return createVFXInternal(
         entityType, startFrame, scale, damage, attackInterval, attackCount, xOffset, yOffset, zOffset, yRotOffset, moveSpeed, moveDistance
      );
   }

   public static InTimeEvent createMovingVFX(
      Supplier<EntityType<? extends GeoVFXEntity>> entityType,
      int startFrame,
      float scale,
      float damage,
      int attackInterval,
      int attackCount,
      float moveSpeed,
      float moveDistance
   ) {
      return createMovingVFX(entityType, startFrame, scale, damage, attackInterval, attackCount, 0.0, 0.0, 0.0, 0.0F, moveSpeed, moveDistance);
   }

   public static InTimeEvent createMovingVFX(Supplier<EntityType<? extends GeoVFXEntity>> entityType, int startFrame, float moveSpeed, float moveDistance) {
      return createMovingVFX(entityType, startFrame, 1.0F, 1.0F, 3, 8, moveSpeed, moveDistance);
   }

   public static InTimeEvent createCustomVFX(Supplier<EntityType<? extends GeoVFXEntity>> entityType, int startFrame, Consumer<GeoVFXEntity> configurator) {
      float time = startFrame / 60.0F;
      return InTimeEvent.create(time, (entityPatch, self, params) -> {
         if (!((LivingEntity)entityPatch.getOriginal()).level().isClientSide()) {
            LivingEntity owner = (LivingEntity)entityPatch.getOriginal();
            ServerLevel level = (ServerLevel)owner.level();
            GeoVFXEntity vfx = (GeoVFXEntity)((EntityType)entityType.get()).create(level);
            if (vfx != null) {
               vfx.setPos(owner.getX(), owner.getY(), owner.getZ());
               vfx.setOwner(owner);
               configurator.accept(vfx);
               level.addFreshEntity(vfx);
            }
         }
      }, Side.SERVER);
   }

   public static InTimeEvent createCustomVFX(
      Supplier<EntityType<? extends GeoVFXEntity>> entityType,
      int startFrame,
      double xOffset,
      double yOffset,
      double zOffset,
      Consumer<GeoVFXEntity> configurator
   ) {
      float time = startFrame / 60.0F;
      return InTimeEvent.create(time, (entityPatch, self, params) -> {
         if (!((LivingEntity)entityPatch.getOriginal()).level().isClientSide()) {
            LivingEntity owner = (LivingEntity)entityPatch.getOriginal();
            ServerLevel level = (ServerLevel)owner.level();
            Vec3 spawnPosition = calculateSpawnPosition(owner, xOffset, yOffset, zOffset);
            GeoVFXEntity vfx = (GeoVFXEntity)((EntityType)entityType.get()).create(level);
            if (vfx != null) {
               vfx.setPos(spawnPosition.x, spawnPosition.y, spawnPosition.z);
               vfx.setOwner(owner);
               configurator.accept(vfx);
               level.addFreshEntity(vfx);
            }
         }
      }, Side.SERVER);
   }

   private static InTimeEvent createVFXInternal(
      Supplier<EntityType<? extends GeoVFXEntity>> entityType,
      int startFrame,
      float scale,
      float damage,
      int attackInterval,
      int attackCount,
      double xOffset,
      double yOffset,
      double zOffset,
      float yRotOffset,
      float moveSpeed,
      float moveDistance
   ) {
      float time = startFrame / 60.0F;
      return InTimeEvent.create(time, (entityPatch, self, params) -> {
         if (!((LivingEntity)entityPatch.getOriginal()).level().isClientSide()) {
            LivingEntity owner = (LivingEntity)entityPatch.getOriginal();
            ServerLevel level = (ServerLevel)owner.level();
            Vec3 spawnPosition = calculateSpawnPosition(owner, xOffset, yOffset, zOffset);
            float finalYRot = owner.getYRot() + yRotOffset;
            GeoVFXEntity vfx = (GeoVFXEntity)((EntityType)entityType.get()).create(level);
            if (vfx != null) {
               vfx.setPos(spawnPosition.x, spawnPosition.y, spawnPosition.z);
               vfx.setOwner(owner);
               vfx.setScale(scale);
               vfx.setAttackDamage(damage);
               vfx.setAttackInterval(attackInterval);
               vfx.setAttackCount(attackCount);
               vfx.setXRotOffset(0.0F);
               vfx.setYRotOffset(yRotOffset);
               vfx.setZRotOffset(0.0F);
               vfx.setStartYRot(finalYRot);
               if (moveSpeed > 0.0F) {
                  vfx.setMoveSpeed(moveSpeed);
                  vfx.setMoveDistance(moveDistance);
                  vfx.setTraveledDistance(0.0F);
               }

               vfx.setYRot(finalYRot);
               vfx.setYBodyRot(finalYRot);
               vfx.setYHeadRot(finalYRot);
               vfx.setXRot(0.0F);
               level.addFreshEntity(vfx);
            }
         }
      }, Side.SERVER);
   }

   private static Vec3 calculateSpawnPosition(LivingEntity owner, double xOffset, double yOffset, double zOffset) {
      float yawRadians = (float)Math.toRadians(-owner.getYRot());
      Vec3 lookVec = new Vec3(Math.sin(yawRadians), 0.0, Math.cos(yawRadians)).normalize();
      Vec3 rightVec = new Vec3(Math.sin(yawRadians + (Math.PI / 2)), 0.0, Math.cos(yawRadians + (Math.PI / 2))).normalize();
      Vec3 upVec = new Vec3(0.0, 1.0, 0.0);
      Vec3 basePos = owner.position().add(0.0, owner.getEyeHeight(), 0.0);
      return basePos.add(lookVec.scale(zOffset)).add(rightVec.scale(xOffset)).add(upVec.scale(yOffset));
   }
}
