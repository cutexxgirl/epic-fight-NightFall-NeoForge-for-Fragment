package com.hm.efn.mobeffects;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.StunType;

public class DieMarkEffect extends MobEffect {
   public DieMarkEffect() {
      super(MobEffectCategory.NEUTRAL, 65280);
   }

   public boolean applyEffectTick(@NotNull LivingEntity pLivingEntity, int pAmplifier) {
      super.applyEffectTick(pLivingEntity, pAmplifier);
      if (!pLivingEntity.level().isClientSide && pLivingEntity.isAlive()) {
         DamageSource damageSource = EpicFightDamageSources.mobAttack(pLivingEntity)
            .addRuntimeTag(BattleContinuationEffect.BATTLE_CONTINUATION_POST_EFFECT)
            .setAnimation(null)
            .setInitialPosition(pLivingEntity.position())
            .setStunType(StunType.NONE)
            .setBaseImpact(0.0F)
            .addRuntimeTag(DamageTypeTags.BYPASSES_RESISTANCE)
            .addRuntimeTag(EpicFightDamageTypeTags.EXECUTION)
            .addRuntimeTag(DamageTypeTags.BYPASSES_ARMOR)
            .addRuntimeTag(DamageTypeTags.BYPASSES_INVULNERABILITY)
            .addRuntimeTag(DamageTypeTags.BYPASSES_COOLDOWN)
            .addRuntimeTag(DamageTypeTags.BYPASSES_SHIELD)
            .addRuntimeTag(EpicFightDamageTypeTags.FINISHER);
         pLivingEntity.hurt(damageSource, Float.MAX_VALUE);
         pLivingEntity.hurt(pLivingEntity.damageSources().genericKill(), Float.MAX_VALUE);
         pLivingEntity.hurt(pLivingEntity.damageSources().fellOutOfWorld(), Float.MAX_VALUE);
      }
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
      return true;
   }
}
