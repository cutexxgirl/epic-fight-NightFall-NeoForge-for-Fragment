package com.hm.efn.mobeffects;

import com.hm.efn.registries.EFNMobEffectRegistry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import yesman.epicfight.api.event.types.entity.StunnedEvent;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;

@EventBusSubscriber(modid = "efn")
public class SinStunImmunity {
   @SubscribeEvent
   public static void onLivingAttack(LivingIncomingDamageEvent event) {
      if (event.getSource() instanceof EpicFightDamageSource damageSource) {
         LivingEntity target = event.getEntity();
         if (target.hasEffect(EFNMobEffectRegistry.SIN_STUN_IMMUNITY)) {
            damageSource.setStunType(StunType.NONE);
         }
      }
   }

   @SubscribeEvent
   public static void onLivingKnockBack(LivingKnockBackEvent event) {
      if (event.getEntity().hasEffect(EFNMobEffectRegistry.SIN_STUN_IMMUNITY)) {
         event.setCanceled(true);
      }
   }

   public static void onEntityStun(StunnedEvent event) {
      LivingEntityPatch<?> entityPatch = event.getEntityPatch();
      if (entityPatch != null
         && entityPatch.getOriginal() instanceof LivingEntity livingEntity
         && livingEntity.hasEffect(EFNMobEffectRegistry.SIN_STUN_IMMUNITY)) {
         if (event.getDamageSource() != null) {
            event.getDamageSource().setStunType(StunType.NONE);
         }

         event.cancel();
      }
   }

   public static class SinStunImmunityEffect extends MobEffect {
      public SinStunImmunityEffect() {
         super(MobEffectCategory.BENEFICIAL, 8978431);
      }

      public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
         return true;
      }
   }
}
