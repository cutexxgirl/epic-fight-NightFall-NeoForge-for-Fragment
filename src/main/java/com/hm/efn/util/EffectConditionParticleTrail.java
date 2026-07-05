package com.hm.efn.util;

import com.merlin204.avalon.util.AvalonAnimationUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationEvent.InPeriodEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;

public class EffectConditionParticleTrail {
   public static InPeriodEvent buffedParticleTrail(
      int startFrame,
      int endFrame,
      InteractionHand hand,
      Vec3 startOffset,
      Vec3 endOffset,
      float timeInterpolation,
      int particleCount,
      ParticleOptions particleOptions,
      float randomX,
      float randomY,
      float randomZ,
      float velocityX,
      float velocityY,
      float velocityZ,
      Holder<MobEffect> requiredBuff
   ) {
      float start = startFrame / 60.0F;
      float end = endFrame / 60.0F;
      Joint joint = null;
      switch (hand) {
         case MAIN_HAND:
            joint = ((HumanoidArmature)Armatures.BIPED.get()).toolR;
            break;
         case OFF_HAND:
            joint = ((HumanoidArmature)Armatures.BIPED.get()).toolL;
      }

      Joint finalJoint = joint;
      return InPeriodEvent.create(
         start,
         end,
         (entityPatch, self, params) -> {
            if (entityPatch.getOriginal() instanceof LivingEntity) {
               LivingEntity living = (LivingEntity)entityPatch.getOriginal();
               if (living.hasEffect(requiredBuff)) {
                  AnimationPlayer player = entityPatch.getAnimator().getPlayerFor(null);
                  float prevElapsedTime = 0.0F;
                  if (player != null) {
                     prevElapsedTime = player.getPrevElapsedTime();
                  }

                  float elapsedTime = 0.0F;
                  if (player != null) {
                     elapsedTime = player.getElapsedTime();
                  }

                  float step = (elapsedTime - prevElapsedTime) / timeInterpolation;
                  Vec3f trailDirection = new Vec3f(
                     (float)(endOffset.x - startOffset.x),
                     (float)(endOffset.y - startOffset.y),
                     (float)(endOffset.z - startOffset.z)
                  );

                  for (float f = prevElapsedTime; f <= elapsedTime; f += step) {
                     for (int i = 0; i <= particleCount; i++) {
                        float ratio = (float)i / particleCount;
                        Vec3f pointOffset = new Vec3f(
                           (float)(startOffset.x + trailDirection.x * ratio),
                           (float)(startOffset.y + trailDirection.y * ratio),
                           (float)(startOffset.z + trailDirection.z * ratio)
                        );
                        double randX = (Math.random() - 0.5) * randomX;
                        double randY = (Math.random() - 0.5) * randomY;
                        double randZ = (Math.random() - 0.5) * randomZ;
                        Vec3 worldPos = AvalonAnimationUtils.getJointWorldRawPos(entityPatch, finalJoint, f + step, pointOffset);
                        if (living.level().isClientSide) {
                           living.level()
                              .addParticle(
                                 particleOptions,
                                 worldPos.x + randX,
                                 worldPos.y + randY,
                                 worldPos.z + randZ,
                                 velocityX,
                                 velocityY,
                                 velocityZ
                              );
                        }
                     }
                  }
               }
            }
         },
         Side.CLIENT
      );
   }

   public static InPeriodEvent buffedParticleTrail(
      int startFrame,
      int endFrame,
      InteractionHand hand,
      Vec3 startOffset,
      Vec3 endOffset,
      float timeInterpolation,
      int particleCount,
      ParticleOptions particleOptions,
      float random,
      Holder<MobEffect> requiredBuff
   ) {
      return buffedParticleTrail(
         startFrame,
         endFrame,
         hand,
         startOffset,
         endOffset,
         timeInterpolation,
         particleCount,
         particleOptions,
         random,
         random,
         random,
         0.0F,
         0.0F,
         0.0F,
         requiredBuff
      );
   }

   public static InPeriodEvent buffedParticleTrail(
      int startFrame,
      int endFrame,
      InteractionHand hand,
      Vec3 startOffset,
      Vec3 endOffset,
      float timeInterpolation,
      int particleCount,
      ParticleOptions particleOptions,
      float randomX,
      float randomY,
      float randomZ,
      Holder<MobEffect> requiredBuff
   ) {
      return buffedParticleTrail(
         startFrame,
         endFrame,
         hand,
         startOffset,
         endOffset,
         timeInterpolation,
         particleCount,
         particleOptions,
         randomX,
         randomY,
         randomZ,
         0.0F,
         0.0F,
         0.0F,
         requiredBuff
      );
   }
}
