package com.hm.efn.util;

import java.util.Objects;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.property.AnimationEvent.InPeriodEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.registry.entries.EpicFightSounds;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class WeaponTrailGroundSplitter {
   public static InPeriodEvent create(
      int startFrame,
      int endFrame,
      InteractionHand hand,
      Vec3 startOffset,
      Vec3 endOffset,
      float radius,
      ParticleOptions particle,
      int particleCount,
      float timeInterpolation,
      float positionDensity,
      int soundInterval
   ) {
      float start = frameToSeconds(startFrame);
      float end = frameToSeconds(endFrame);
      Joint joint = getWeaponJoint(hand);
      return InPeriodEvent.create(
         start,
         end,
         (entityPatch, self, params) -> {
            int[] soundCounter = new int[]{0};
            Level level = ((LivingEntity)entityPatch.getOriginal()).level();
            int soundIntervalMultiplier = 40;
            Vec3[] lastSoundPos = new Vec3[]{null};
            float prevTime = Objects.requireNonNull(entityPatch.getAnimator().getPlayerFor(null)).getPrevElapsedTime();
            float currentTime = Objects.requireNonNull(entityPatch.getAnimator().getPlayerFor(null)).getElapsedTime();
            float step = (currentTime - prevTime) / timeInterpolation;
            Vec3f trailDirection = new Vec3f(
               (float)(endOffset.x - startOffset.x),
               (float)(endOffset.y - startOffset.y),
               (float)(endOffset.z - startOffset.z)
            );

            for (float t = prevTime; t <= currentTime; t += step) {
               float timeProgress = Mth.clamp((t - start) / (end - start), 0.0F, 1.0F);
               boolean playSoundThisFrame = soundCounter[0]++ % (soundInterval * 40) == 0;
               Vec3 currentWeaponTip = calculateWeaponImpactPos(entityPatch, joint, new Vec3f(0.0F, 0.0F, -1.5F), t);
               Vec3 soundPos = lastSoundPos[0] != null ? lastSoundPos[0].lerp(currentWeaponTip, 0.3F) : currentWeaponTip;
               if (!entityPatch.isLogicalClient() && level instanceof ServerLevel serverLevel) {
                  if (playSoundThisFrame) {
                     float distanceToPlayer = (float)soundPos.distanceTo(((LivingEntity)entityPatch.getOriginal()).position());
                     float volume = Mth.clamp(0.4F - distanceToPlayer * 0.03F, 0.2F, 0.4F);
                     serverLevel.playSound(
                        null,
                        soundPos.x,
                        soundPos.y,
                        soundPos.z,
                        (SoundEvent)EpicFightSounds.SLAM_HEAVY.get(),
                        SoundSource.PLAYERS,
                        volume,
                        1.0F
                     );
                     lastSoundPos[0] = soundPos;
                  }

                  for (int i = 0; i < positionDensity; i++) {
                     float spaceProgress = i / positionDensity;
                     Vec3f pointOffset = new Vec3f(
                        (float)(startOffset.x + trailDirection.x * spaceProgress),
                        (float)(startOffset.y + trailDirection.y * spaceProgress),
                        (float)(startOffset.z + trailDirection.z * spaceProgress)
                     );
                     Vec3 basePos = calculateWeaponImpactPos(entityPatch, joint, pointOffset, t);

                     for (int dy = -1; dy <= 1; dy++) {
                        Vec3 groundPos = basePos.add(0.0, dy, 0.0);
                        LevelUtil.circleSlamFracture(
                           (LivingEntity)entityPatch.getOriginal(),
                           serverLevel,
                           groundPos,
                           radius * (0.8F + 0.2F * Mth.sin(timeProgress * (float) Math.PI)),
                           true,
                           true,
                           false
                        );
                     }

                     if (particle != null) {
                        Vec3 particlePos = basePos.add(0.0, 0.5, 0.0);
                        serverLevel.sendParticles(particle, particlePos.x, particlePos.y, particlePos.z, particleCount, 0.0, 0.0, 0.0, 0.05);
                     }
                  }
               }
            }
         },
         Side.SERVER
      );
   }

   private static Vec3 calculateWeaponImpactPos(LivingEntityPatch<?> entityPatch, Joint joint, Vec3f offset, float currentTime) {
      Pose pose = ((DynamicAnimation)Objects.requireNonNull(entityPatch.getAnimator().getPlayerFor(null)).getAnimation().get()).getRawPose(currentTime);
      OpenMatrix4f transformMatrix = entityPatch.getArmature().getBoundTransformFor(pose, joint);
      float yRot = ((LivingEntity)entityPatch.getOriginal()).getYRot();
      OpenMatrix4f rotation = new OpenMatrix4f().rotate(-((float)Math.toRadians(yRot + 180.0F)), new Vec3f(0.0F, 1.0F, 0.0F));
      OpenMatrix4f rotatedMatrix = new OpenMatrix4f();
      OpenMatrix4f.mul(rotation, transformMatrix, rotatedMatrix);
      OpenMatrix4f finalTransform = new OpenMatrix4f(rotatedMatrix);
      finalTransform.translate(offset);
      return new Vec3(
         finalTransform.m30 + ((LivingEntity)entityPatch.getOriginal()).getX(),
         finalTransform.m31 + ((LivingEntity)entityPatch.getOriginal()).getY(),
         finalTransform.m32 + ((LivingEntity)entityPatch.getOriginal()).getZ()
      );
   }

   private static float frameToSeconds(int frame) {
      return frame / 60.0F;
   }

   private static Joint getWeaponJoint(InteractionHand hand) {
      return hand == InteractionHand.MAIN_HAND ? ((HumanoidArmature)Armatures.BIPED.get()).toolR : ((HumanoidArmature)Armatures.BIPED.get()).toolL;
   }

   private static Vec3 lerp(Vec3 start, Vec3 end, float t) {
      return new Vec3(
         Mth.lerp(t, start.x, end.x), Mth.lerp(t, start.y, end.y), Mth.lerp(t, start.z, end.z)
      );
   }
}
