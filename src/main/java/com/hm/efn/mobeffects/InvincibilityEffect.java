package com.hm.efn.mobeffects;

import com.hm.efn.EFN;
import com.hm.efn.registries.EFNMobEffectRegistry;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Expired;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Remove;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

@EventBusSubscriber(modid = "efn")
public class InvincibilityEffect {
   private static volatile boolean isProcessingRemoval = false;

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void onLivingAttack(LivingIncomingDamageEvent event) {
      if (!isProcessingRemoval) {
         LivingEntity target = event.getEntity();
         DamageSource source = event.getSource();
         if (hasInvincibilityEffect(target)) {
            EFN.LOGGER.debug("InvincibilityEffect: Blocking attack from {} to {}", source.getMsgId(), target.getName().getString());
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void onLivingHurt(LivingIncomingDamageEvent event) {
      if (!isProcessingRemoval) {
         LivingEntity target = event.getEntity();
         if (hasInvincibilityEffect(target)) {
            EFN.LOGGER.debug("InvincibilityEffect: Nullifying damage to {}", target.getName().getString());
            event.setAmount(0.0F);
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void onLivingKnockback(LivingKnockBackEvent event) {
      if (!isProcessingRemoval) {
         LivingEntity target = event.getEntity();
         if (hasInvincibilityEffect(target)) {
            EFN.LOGGER.debug("InvincibilityEffect: Preventing knockback to {}", target.getName().getString());
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void onPotionEffect(LivingIncomingDamageEvent event) {
      if (!isProcessingRemoval) {
         LivingEntity target = event.getEntity();
         if (hasInvincibilityEffect(target)) {
            DamageSource source = event.getSource();
            if (source.is(DamageTypes.MAGIC)
               || source.is(DamageTypes.EXPLOSION)
               || source.is(DamageTypeTags.BYPASSES_ARMOR)
               || source.is(DamageTypeTags.BYPASSES_SHIELD)
               || source.is(DamageTypeTags.BYPASSES_RESISTANCE)
               || source.is(DamageTypeTags.IS_FIRE)
               || source.is(DamageTypeTags.IS_PROJECTILE)
               || source.is(DamageTypeTags.IS_LIGHTNING)
               || source.is(DamageTypeTags.IS_DROWNING)
               || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
               EFN.LOGGER.debug("InvincibilityEffect: Blocking magical damage to {}", target.getName().getString());
               event.setCanceled(true);
            }
         }
      }
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public static void onEpicFightDamage(LivingIncomingDamageEvent event) {
      if (!isProcessingRemoval) {
         if (event.getSource() instanceof EpicFightDamageSource) {
            LivingEntity target = event.getEntity();
            if (hasInvincibilityEffect(target)) {
               EFN.LOGGER.debug("InvincibilityEffect: Blocking EpicFight damage to {}", target.getName().getString());
               event.setCanceled(true);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onEffectRemoved(Remove event) {
      if (event.getEffect().value() instanceof InvincibilityEffect.InvincibilityMobEffect) {
         isProcessingRemoval = true;

         try {
            LivingEntity entity = event.getEntity();
            EFN.LOGGER.debug("InvincibilityEffect: Removed from {}", entity.getName().getString());
         } finally {
            isProcessingRemoval = false;
         }
      }
   }

   @SubscribeEvent
   public static void onEffectExpired(Expired event) {
      if (event.getEffectInstance() != null && event.getEffectInstance().getEffect().value() instanceof InvincibilityEffect.InvincibilityMobEffect) {
         isProcessingRemoval = true;

         try {
            LivingEntity entity = event.getEntity();
            EFN.LOGGER.debug("InvincibilityEffect: Expired for {}", entity.getName().getString());
         } finally {
            isProcessingRemoval = false;
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeath(LivingDeathEvent event) {
      LivingEntity entity = event.getEntity();
      if (hasInvincibilityEffect(entity)) {
         isProcessingRemoval = true;

         try {
            EFN.LOGGER.debug("InvincibilityEffect: Entity died with invincibility effect - {}", entity.getName().getString());
         } finally {
            isProcessingRemoval = false;
         }
      }
   }

   private static boolean hasInvincibilityEffect(LivingEntity entity) {
      if (isProcessingRemoval) {
         return false;
      }

      if (entity != null && entity.isAlive()) {
         try {
            return entity.hasEffect(EFNMobEffectRegistry.INVINCIBILITY_EFFECT);
         } catch (Exception e) {
            EFN.LOGGER.warn("InvincibilityEffect: Error checking effect status for {}", entity.getName().getString(), e);
            return false;
         }
      } else {
         return false;
      }
   }

   public static class InvincibilityMobEffect extends MobEffect {
      public InvincibilityMobEffect() {
         super(MobEffectCategory.NEUTRAL, 16777215);
      }

      public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
         return true;
      }
   }
}
