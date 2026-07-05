package com.hm.efn.mobeffects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class VerticalStop extends MobEffect {
   public VerticalStop() {
      super(MobEffectCategory.NEUTRAL, 16777215);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      Vec3 currentMotion = entity.getDeltaMovement();
      Vec3 newMotion = new Vec3(currentMotion.x(), 0.0, currentMotion.z());
      entity.setDeltaMovement(newMotion);
      entity.setPos(entity.getX(), entity.yOld, entity.getZ());
      return true;
   }
}
