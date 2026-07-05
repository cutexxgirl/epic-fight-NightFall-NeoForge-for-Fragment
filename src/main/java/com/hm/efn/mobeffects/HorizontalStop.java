package com.hm.efn.mobeffects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class HorizontalStop extends MobEffect {
   public HorizontalStop() {
      super(MobEffectCategory.NEUTRAL, 16777215);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      Vec3 currentMotion = entity.getDeltaMovement();
      Vec3 newMotion = new Vec3(0.0, currentMotion.y(), 0.0);
      entity.setDeltaMovement(newMotion);
      entity.setPos(entity.xOld, entity.getY(), entity.zOld);
      entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0, 1.0, 0.0));
      return true;
   }
}
