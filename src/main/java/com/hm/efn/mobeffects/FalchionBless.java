package com.hm.efn.mobeffects;

import com.hm.efn.registries.EFNMobEffectRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class FalchionBless extends MobEffect {
   public FalchionBless() {
      super(MobEffectCategory.BENEFICIAL, 65280);
   }

   @SubscribeEvent
   public static void onLivingDamage(Post event) {
      LivingEntity entity = event.getEntity();
      if (entity.hasEffect(EFNMobEffectRegistry.FALCHION_BLESS)) {
         float healAmount = entity.getMaxHealth() * 0.1F;
         entity.heal(healAmount);
         if (!entity.level().isClientSide()) {
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.WARDEN_NEARBY_CLOSE, SoundSource.PLAYERS, 1.0F, 1.2F);
         }
      }
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }
}
