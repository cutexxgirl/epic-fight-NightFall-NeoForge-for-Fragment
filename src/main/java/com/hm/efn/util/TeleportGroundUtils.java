package com.hm.efn.util;

import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class TeleportGroundUtils {
   public static InTimeEvent create(int startFrame, float offsetY) {
      return create(startFrame, offsetY, null, 0.0F, 0.0F);
   }

   public static InTimeEvent create(int startFrame, float offsetY, String jointName, float forwardOffset, float sideOffset) {
      float startTime = startFrame / 60.0F;
      return InTimeEvent.create(startTime, (entityPatch, self, params) -> {
         LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
         Vec3 groundPos = getGroundPosition(entityPatch, offsetY, jointName);
         if (forwardOffset != 0.0F || sideOffset != 0.0F) {
            Vec3 offset = calculateHorizontalOffset(entity, forwardOffset, sideOffset);
            groundPos = groundPos.add(offset);
         }

         teleportToPosition(entityPatch, groundPos);
      }, Side.BOTH);
   }

   public static Vec3 getGroundPosition(LivingEntityPatch<?> entityPatch, float offsetY, String jointName) {
      LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
      if (jointName != null && entityPatch.getArmature() != null) {
         Armature armature = entityPatch.getArmature();
         Joint joint = armature.searchJointByName(jointName);
         if (joint != null) {
            OpenMatrix4f transform = armature.getBoundTransformFor(entityPatch.getAnimator().getPose(1.0F), joint);
            OpenMatrix4f correction = new OpenMatrix4f().rotate((float)(-Math.toRadians(entity.getYRot() + 180.0F)), new Vec3f(0.0F, 1.0F, 0.0F));
            OpenMatrix4f.mul(correction, transform, transform);
            double x = transform.m30 + entity.getX();
            double y = transform.m31 + entity.getY() + offsetY;
            double z = transform.m32 + entity.getZ();
            return findActualGround(entity, x, y, z);
         }
      }

      return getSimpleGroundPosition(entity, offsetY);
   }

   private static Vec3 calculateHorizontalOffset(LivingEntity entity, float forward, float side) {
      float yRotRad = (float)Math.toRadians(entity.getYRot());
      float xOffset = -forward * (float)Math.sin(yRotRad) + side * (float)Math.cos(yRotRad);
      float zOffset = forward * (float)Math.cos(yRotRad) + side * (float)Math.sin(yRotRad);
      return new Vec3(xOffset, 0.0, zOffset);
   }

   private static void teleportToPosition(LivingEntityPatch<?> entityPatch, Vec3 targetPos) {
      LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
      Vec3 moveVec = new Vec3(targetPos.x - entity.getX(), targetPos.y - entity.getY(), targetPos.z - entity.getZ());
      entity.move(MoverType.SELF, moveVec);
      if (entity instanceof ServerPlayer serverPlayer) {
         serverPlayer.connection.teleport(targetPos.x, targetPos.y, targetPos.z, entity.getYRot(), entity.getXRot());
      }
   }

   private static Vec3 findActualGround(LivingEntity entity, double x, double y, double z) {
      MutableBlockPos pos = new MutableBlockPos(x, y, z);

      for (BlockState blockState = entity.level().getBlockState(pos);
         (blockState.isAir() || blockState.getBlock() instanceof BushBlock) && !blockState.is(Blocks.BEDROCK) && y > entity.level().getMinBuildHeight();
         blockState = entity.level().getBlockState(pos)
      ) {
         pos.set(x, --y, z);
      }

      return new Vec3(x, y + 1.0, z);
   }

   public static Vec3 getSimpleGroundPosition(LivingEntity entity, float offsetY) {
      double y = entity.getY() - entity.getBbHeight() / 2.0F + offsetY;
      return findActualGround(entity, entity.getX(), y, entity.getZ());
   }
}
