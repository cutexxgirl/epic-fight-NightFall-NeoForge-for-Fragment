package com.hm.efn.mobeffects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class GuardPunctureEffect extends MobEffect {
   public GuardPunctureEffect() {
      super(MobEffectCategory.NEUTRAL, 16777215);
   }

   public void onEffectStarted(LivingEntity entity, int amplifier) {
      super.onEffectStarted(entity, amplifier);
      if (!entity.level().isClientSide()) {
         entity.getPersistentData().putBoolean("efn_guard_puncture", true);
      }
   }

   public void onMobRemoved(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
      super.onMobRemoved(entity, amplifier, reason);
      if (!entity.level().isClientSide()) {
         entity.getPersistentData().remove("efn_guard_puncture");
      }
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return false;
   }
}
