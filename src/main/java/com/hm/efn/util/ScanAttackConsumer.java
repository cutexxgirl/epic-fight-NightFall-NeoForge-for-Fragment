package com.hm.efn.util;

import com.hm.efn.animations.types.ScanAttackAnimation;
import java.util.Objects;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@FunctionalInterface
public interface ScanAttackConsumer {
   void consume(ScanAttackAnimation var1, LivingEntityPatch<?> var2, float var3, float var4, EntityState var5, EntityState var6, Phase var7);

   default ScanAttackConsumer andThen(ScanAttackConsumer after) {
      Objects.requireNonNull(after);
      return (animation, entitypatch, prevET, eT, prevState, state, phase) -> {
         this.consume(animation, entitypatch, prevET, eT, prevState, state, phase);
         after.consume(animation, entitypatch, prevET, eT, prevState, state, phase);
      };
   }
}
