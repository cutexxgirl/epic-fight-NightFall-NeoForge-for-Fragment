package com.hm.efn.mobeffects;

import com.hm.efn.registries.EFNMobEffectRegistry;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Expired;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Remove;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

public class GradualHeal extends MobEffect {
   private static final Map<LivingEntity, Float> HEAL_PER_TICK_MAP = new WeakHashMap<>();

   public GradualHeal() {
      super(MobEffectCategory.BENEFICIAL, 16739179);
   }

   public boolean applyEffectTick(LivingEntity entity, int amplifier) {
      if (!entity.level().isClientSide && entity.getHealth() < entity.getMaxHealth()) {
         float healAmount = HEAL_PER_TICK_MAP.computeIfAbsent(entity, e -> {
            MobEffectInstance effect = e.getEffect(EFNMobEffectRegistry.GRADUAL_HEAL);
            if (effect != null) {
               float maxHealth = e.getMaxHealth();
               float totalHealAmount = maxHealth * (amplifier + 1) * 0.05F;
               int initialDuration = effect.getDuration();
               return initialDuration <= 0 ? 0.0F : totalHealAmount / initialDuration;
            } else {
               return 0.0F;
            }
         });
         if (healAmount > 0.0F) {
            entity.heal(healAmount);
         }
      }
      return true;
   }

   public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
      return true;
   }

   @EventBusSubscriber(modid = "efn")
   public static class EffectEvents {
      @SubscribeEvent
      public static void onEffectRemoved(Remove event) {
         if (event.getEffectInstance() != null && event.getEffectInstance().getEffect().value() instanceof GradualHeal) {
            GradualHeal.HEAL_PER_TICK_MAP.remove(event.getEntity());
         }
      }

      @SubscribeEvent
      public static void onEffectExpired(Expired event) {
         if (event.getEffectInstance() != null && event.getEffectInstance().getEffect().value() instanceof GradualHeal) {
            GradualHeal.HEAL_PER_TICK_MAP.remove(event.getEntity());
         }
      }
   }
}
