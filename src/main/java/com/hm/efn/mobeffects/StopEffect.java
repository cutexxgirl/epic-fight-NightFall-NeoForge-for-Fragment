package com.hm.efn.mobeffects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class StopEffect extends MobEffect {
   public StopEffect() {
      super(MobEffectCategory.NEUTRAL, 16777215);
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int lv) {
      return true;
   }

   public boolean applyEffectTick(LivingEntity owner, int lv) {
      owner.setDeltaMovement(Vec3.ZERO);
      owner.setPos(owner.xOld, owner.yOld, owner.zOld);
      owner.setSpeed(0.0F);
      return true;
   }
}
