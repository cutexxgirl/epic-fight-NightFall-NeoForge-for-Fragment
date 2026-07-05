package com.hm.efn.util;

import com.merlin204.avalon.util.AvalonAnimationUtils;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationEvent.InPeriodEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.math.Vec3f;

public class GroundParticleAtk {
   public static InPeriodEvent groundParticleAtk(
      int startFrame, int endFrame, Joint joint, Vec3 startOffset, Vec3 endOffset, float timeInterpolation, int particleCount, ParticleOptions particleOptions
   ) {
      return groundParticleAtk(startFrame, endFrame, joint, startOffset, endOffset, timeInterpolation, particleCount, particleOptions, false);
   }

   public static InPeriodEvent groundParticleAtk(
      int startFrame,
      int endFrame,
      Joint joint,
      Vec3 startOffset,
      Vec3 endOffset,
      float timeInterpolation,
      int particleCount,
      ParticleOptions particleOptions,
      boolean test
   ) {
      float start = startFrame / 60.0F;
      float end = endFrame / 60.0F;
      Joint finalJoint = joint;
      return InPeriodEvent.create(
         start,
         end,
         (entityPatch, self, params) -> {
            AnimationPlayer player = entityPatch.getAnimator().getPlayerFor((AssetAccessor)null);
            float prevElapsedTime = player.getPrevElapsedTime();
            float elapsedTime = player.getElapsedTime();
            float step = (elapsedTime - prevElapsedTime) / timeInterpolation;
            Vec3 trailStartOffset = startOffset;
            Vec3 trailEndOffset = endOffset;
            Vec3f trailDirection = new Vec3f(
               (float)(trailEndOffset.x - trailStartOffset.x),
               (float)(trailEndOffset.y - trailStartOffset.y),
               (float)(trailEndOffset.z - trailStartOffset.z)
            );

            for (float f = prevElapsedTime; f <= elapsedTime; f += step) {
               for (int i = 0; i <= particleCount; i++) {
                  float ratio = (float)i / particleCount;
                  Vec3f pointOffset = new Vec3f(
                     (float)(trailStartOffset.x + trailDirection.x * ratio),
                     (float)(trailStartOffset.y + trailDirection.y * ratio),
                     (float)(trailStartOffset.z + trailDirection.z * ratio)
                  );
                  Vec3 worldPos = AvalonAnimationUtils.getJointWorldRawPos(entityPatch, finalJoint, f + step, pointOffset);
                  if (((LivingEntity)entityPatch.getOriginal()).level().isClientSide() && test) {
                     ((LivingEntity)entityPatch.getOriginal())
                        .level()
                        .addParticle(ParticleTypes.END_ROD, worldPos.x, worldPos.y, worldPos.z, 0.0, 0.0, 0.0);
                  } else {
                     ((LivingEntity)entityPatch.getOriginal())
                        .level()
                        .addParticle(particleOptions, worldPos.x, worldPos.y, worldPos.z, 0.0, 0.0, 0.0);
                  }
               }
            }
         },
         Side.BOTH
      );
   }
}
