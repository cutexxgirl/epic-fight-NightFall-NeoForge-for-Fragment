package com.hm.efn.mobeffects;

import com.hm.efn.registries.EFNMobEffectRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;

public class MurasamaSheathEffect extends MobEffect {
   public MurasamaSheathEffect() {
      super(MobEffectCategory.BENEFICIAL, 14423100);
      this.addAttributeModifier(
         Attributes.ATTACK_DAMAGE, EffectAttributeModifiers.id("8b1a2d3c-4e5f-6a7b-8c9d-e0f1a2b3c4d5"), 0.1, Operation.ADD_MULTIPLIED_TOTAL
      );
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (!entity.level().isClientSide()) {
         CompoundTag data = entity.getPersistentData();
         int maxAmplifier = 29;
         if (amplifier < maxAmplifier) {
            int ticks = data.getInt("EFNMurasamaSheathTicks") + 1;
            if (ticks >= 20) {
               ticks = 0;
               entity.addEffect(new MobEffectInstance(EFNMobEffectRegistry.MURASAMA_SHEATH, -1, amplifier + 1, false, false, true));
               entity.level().playSound(null, entity.blockPosition(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5F, 0.8F + amplifier * 0.1F);
            }

            data.putInt("EFNMurasamaSheathTicks", ticks);
         } else if (amplifier == maxAmplifier && !data.getBoolean("EFNMurasamaMaxSoundPlayed")) {
            entity.level().playSound(null, entity.blockPosition(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
            data.putBoolean("EFNMurasamaMaxSoundPlayed", true);
         }
      }
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }
}
