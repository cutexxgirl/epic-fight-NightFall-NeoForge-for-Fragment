package com.hm.efn.entity;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class ParticleEntitySpawnUtil {
   @Nullable
   public static <T extends Abstract3DParticleEntity> T spawnAbsolute(
      Level level, EntityType<T> type, double x, double y, double z, float xRot, float yRot, float zRot, float scale
   ) {
      if (level.isClientSide()) {
         return null;
      }

      T vfxEntity = (T)type.create(level);
      if (vfxEntity != null) {
         vfxEntity.setPos(x, y, z);
         vfxEntity.setXRotOffset(xRot);
         vfxEntity.setYRotOffset(yRot);
         vfxEntity.setZRotOffset(zRot);
         vfxEntity.setScale(scale);
         level.addFreshEntity(vfxEntity);
      }

      return vfxEntity;
   }

   public static Vec3 calculateRelativePosition(LivingEntity owner, double forward, double height, double side) {
      Vec3 horizontalLook = new Vec3(Math.sin(-owner.getYRot() * (Math.PI / 180.0)), 0.0, Math.cos(owner.getYRot() * (Math.PI / 180.0))).normalize();
      Vec3 right = new Vec3(-horizontalLook.z, 0.0, horizontalLook.x).normalize();
      return owner.getEyePosition().add(horizontalLook.scale(forward)).add(right.scale(side)).add(0.0, height, 0.0);
   }

   @Nullable
   public static <T extends Abstract3DParticleEntity> T spawnRelative(
      LivingEntity owner, EntityType<T> type, double forwardDist, double heightOffset, double sideOffset, float xRot, float yRot, float zRot, float scale
   ) {
      Level level = owner.level();
      if (level.isClientSide()) {
         return null;
      }

      Vec3 spawnPos = calculateRelativePosition(owner, forwardDist, heightOffset, sideOffset);
      T vfxEntity = (T)type.create(level);
      if (vfxEntity != null) {
         vfxEntity.setOwner(owner);
         vfxEntity.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
         vfxEntity.setXRotOffset(xRot);
         vfxEntity.setYRotOffset(yRot);
         vfxEntity.setZRotOffset(zRot);
         vfxEntity.setScale(scale);
         level.addFreshEntity(vfxEntity);
      }

      return vfxEntity;
   }

   @Nullable
   public static <T extends Abstract3DParticleEntity> T spawnVFXSmart(
      LivingEntity owner,
      EntityType<T> type,
      double forwardDist,
      double heightOffset,
      double sideOffset,
      float xRot,
      float yRot,
      float zRot,
      float scale,
      double safeOffset,
      double minForward
   ) {
      Level level = owner.level();
      LivingEntityPatch<?> ownerPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class);
      if (level.isClientSide()) {
         return null;
      }

      if (ownerPatch == null) {
         return null;
      }

      Vec3 baseSpawnPos = calculateRelativePosition(owner, forwardDist, heightOffset, sideOffset);
      Vec3 ownerPos = owner.position();
      double adjustedForwardDist = forwardDist;
      boolean hasObstruction = false;
      BlockHitResult blockHit = level.clip(new ClipContext(ownerPos, baseSpawnPos, Block.COLLIDER, Fluid.NONE, owner));
      if (blockHit.getType() != Type.MISS) {
         double hitDistance = blockHit.getLocation().distanceTo(ownerPos);
         if (hitDistance < forwardDist) {
            adjustedForwardDist = Math.max(hitDistance - safeOffset, minForward);
            hasObstruction = true;
         }
      }

      if (!hasObstruction) {
         AABB pathBox = new AABB(ownerPos, baseSpawnPos).inflate(1.5);
         List<Entity> entitiesInPath = level.getEntities(
            owner, pathBox, entityx -> entityx instanceof LivingEntity && entityx != owner && entityx.isPickable() && ((LivingEntity)entityx).canBeSeenAsEnemy()
         );
         if (!entitiesInPath.isEmpty()) {
            double nearestDistance = Double.MAX_VALUE;

            for (Entity entity : entitiesInPath) {
               double distance = entity.distanceTo(owner);
               if (distance < nearestDistance) {
                  nearestDistance = distance;
               }
            }

            if (nearestDistance < forwardDist) {
               adjustedForwardDist = Math.max(nearestDistance - safeOffset, minForward);
            }
         }
      }

      Vec3 finalSpawnPos = calculateRelativePosition(owner, adjustedForwardDist, heightOffset, sideOffset);
      T vfxEntity = (T)type.create(level);
      if (vfxEntity != null) {
         vfxEntity.setOwner(owner);
         vfxEntity.setPos(finalSpawnPos.x, finalSpawnPos.y, finalSpawnPos.z);
         vfxEntity.setXRotOffset(xRot);
         vfxEntity.setYRotOffset(yRot + ownerPatch.getYRot());
         vfxEntity.setZRotOffset(zRot);
         vfxEntity.setScale(scale);
         level.addFreshEntity(vfxEntity);
      }

      return vfxEntity;
   }

   public static <T extends Abstract3DParticleEntity> InTimeEvent createSmartVFXEvent(
      float startFrame,
      EntityType<T> type,
      double forwardDist,
      double heightOffset,
      double sideOffset,
      float xRot,
      float yRot,
      float zRot,
      float scale,
      double safeOffset,
      double minForward,
      @Nullable Predicate<LivingEntityPatch<?>> condition
   ) {
      float startTime = startFrame / 60.0F;
      return InTimeEvent.create(startTime, (entityPatch, self, params) -> {
         if (condition == null || condition.test(entityPatch)) {
            LivingEntity owner = (LivingEntity)entityPatch.getOriginal();
            if (owner != null && owner.isAlive()) {
               spawnVFXSmart(owner, type, forwardDist, heightOffset, sideOffset, xRot, yRot, zRot, scale, safeOffset, minForward);
            }
         }
      }, Side.SERVER);
   }
}
