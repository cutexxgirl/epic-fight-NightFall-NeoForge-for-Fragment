package com.hm.efn.mobeffects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class BypassDodgeEffect extends MobEffect {
   public BypassDodgeEffect() {
      super(MobEffectCategory.NEUTRAL, 16777215);
   }

   public void onEffectStarted(LivingEntity entity, int amplifier) {
      super.onEffectStarted(entity, amplifier);
      if (!entity.level().isClientSide()) {
         entity.getPersistentData().putBoolean("efn_bypass_dodge", true);
      }
   }

   public void onMobRemoved(LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
      super.onMobRemoved(entity, amplifier, reason);
      if (!entity.level().isClientSide()) {
         entity.getPersistentData().remove("efn_bypass_dodge");
      }
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return false;
   }
}
