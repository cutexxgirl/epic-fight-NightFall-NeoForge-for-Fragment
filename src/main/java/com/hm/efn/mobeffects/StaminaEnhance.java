package com.hm.efn.mobeffects;

import java.util.UUID;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Added;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Expired;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Remove;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import yesman.epicfight.registry.entries.EpicFightAttributes;

@EventBusSubscriber(modid = "efn")
public class StaminaEnhance {
   private static final UUID STAMINA_REGEN_UUID = UUID.fromString("1c224694-19f3-11ec-9621-0242ac140105");
   private static final double STAMINA_REGEN_BONUS = 1.5;

   @SubscribeEvent
   public static void onEffectAdded(Added event) {
      if (event.getEffectInstance().getEffect().value() instanceof StaminaEnhance.StaminaEffect) {
         LivingEntity entity = event.getEntity();
         ((StaminaEnhance.StaminaEffect)event.getEffectInstance().getEffect().value()).applyModifiers(entity);
      }
   }

   @SubscribeEvent
   public static void onEffectRemoved(Remove event) {
      if (event.getEffect().value() instanceof StaminaEnhance.StaminaEffect) {
         ((StaminaEnhance.StaminaEffect)event.getEffect().value()).removeModifiers(event.getEntity());
      }
   }

   @SubscribeEvent
   public static void onEffectExpired(Expired event) {
      if (event.getEffectInstance() != null && event.getEffectInstance().getEffect().value() instanceof StaminaEnhance.StaminaEffect) {
         ((StaminaEnhance.StaminaEffect)event.getEffectInstance().getEffect().value()).removeModifiers(event.getEntity());
      }
   }

   public static class StaminaEffect extends MobEffect {
      public StaminaEffect() {
         super(MobEffectCategory.BENEFICIAL, 65280);
      }

      public boolean applyEffectTick(LivingEntity entity, int amplifier) {
         return true;
      }

      public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
         return false;
      }

      private void applyModifiers(LivingEntity entity) {
         this.safeAddModifier(entity, EpicFightAttributes.STAMINA_REGEN, StaminaEnhance.STAMINA_REGEN_UUID, "Stamina Enhance Regen", 1.5);
      }

      private void removeModifiers(LivingEntity entity) {
         this.safeRemoveModifier(entity, EpicFightAttributes.STAMINA_REGEN, StaminaEnhance.STAMINA_REGEN_UUID);
      }

      private void safeAddModifier(LivingEntity entity, Holder<Attribute> attribute, UUID uuid, String name, double amount) {
         EffectAttributeModifiers.addTransient(entity, attribute, uuid, amount, Operation.ADD_VALUE);
      }

      private void safeRemoveModifier(LivingEntity entity, Holder<Attribute> attribute, UUID uuid) {
         EffectAttributeModifiers.remove(entity, attribute, uuid);
      }
   }
}
