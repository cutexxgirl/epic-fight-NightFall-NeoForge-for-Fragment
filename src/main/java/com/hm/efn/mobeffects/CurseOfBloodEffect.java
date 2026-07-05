package com.hm.efn.mobeffects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class CurseOfBloodEffect extends MobEffect {
   public CurseOfBloodEffect() {
      super(MobEffectCategory.HARMFUL, 9109504);
   }

   public boolean applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
      pLivingEntity.hurt(pLivingEntity.damageSources().wither(), (pAmplifier + 1) * 0.5F);
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return pDuration % 20 == 0;
   }
}
