package com.hm.efn.mobeffects;

import com.hm.efn.registries.EFNMobEffectRegistry;
import java.util.UUID;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Added;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Expired;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Remove;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import yesman.epicfight.registry.entries.EpicFightAttributes;

@EventBusSubscriber(modid = "efn")
public class BloodlustEnhance {
   private static final UUID ARMOR_NEGATION_UUID = UUID.fromString("b0a7436e-5734-11eb-ae93-0242ac130013");
   private static final UUID IMPACT_UUID = UUID.fromString("b0a745b2-5734-11eb-ae93-0242ac130013");
   private static final UUID STUN_ARMOR_UUID = UUID.fromString("b0a746ac-5734-11eb-ae93-0242ac130013");
   private static final UUID STAMINA_REGEN_UUID = UUID.fromString("1c224694-19f3-11ec-9621-0242ac130103");
   private static final UUID DAMAGE_UUID = UUID.fromString("1c2249f0-19f3-11ec-9621-0242ac130103");
   private static final double MULTIPLIER = 1.2;
   private static final double MULTIPLIER_MINUS_ONE = 0.19999999999999996;

   @SubscribeEvent
   public static void onEffectAdded(Added event) {
      if (event.getEffectInstance().getEffect().value() instanceof BloodlustEnhance.BloodlustEffect) {
         LivingEntity entity = event.getEntity();
         float maxHealth = entity.getMaxHealth();
         float initialCost = Math.max(maxHealth * 0.15F, 4.0F);
         if (entity.getHealth() > initialCost) {
            entity.hurt(entity.damageSources().magic(), initialCost);
         } else if (entity.getHealth() > 1.0F) {
            entity.hurt(entity.damageSources().magic(), entity.getHealth() - 1.0F);
         }

         ((BloodlustEnhance.BloodlustEffect)event.getEffectInstance().getEffect().value()).applyModifiers(entity);
      }
   }

   @SubscribeEvent
   public static void onEffectRemoved(Remove event) {
      if (event.getEffect().value() instanceof BloodlustEnhance.BloodlustEffect) {
         ((BloodlustEnhance.BloodlustEffect)event.getEffect().value()).removeModifiers(event.getEntity());
      }
   }

   @SubscribeEvent
   public static void onEffectExpired(Expired event) {
      if (event.getEffectInstance() != null && event.getEffectInstance().getEffect().value() instanceof BloodlustEnhance.BloodlustEffect) {
         ((BloodlustEnhance.BloodlustEffect)event.getEffectInstance().getEffect().value()).removeModifiers(event.getEntity());
      }
   }

   @SubscribeEvent
   public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
      if (event.getEntity() instanceof Player
         && event.getSlot() == EquipmentSlot.MAINHAND
         && event.getEntity().hasEffect(EFNMobEffectRegistry.BLODDLUST)) {
         event.getEntity().removeEffect(EFNMobEffectRegistry.BLODDLUST);
      }
   }

   public static class BloodlustEffect extends MobEffect {
      public BloodlustEffect() {
         super(MobEffectCategory.BENEFICIAL, 16711680);
      }

      public boolean applyEffectTick(LivingEntity entity, int amplifier) {
         if (!entity.level().isClientSide()) {
            this.applyModifiers(entity);
            if (entity.tickCount % 20 == 0) {
               float maxHealth = entity.getMaxHealth();
               float damage = Math.max(maxHealth * 0.05F, 1.5F);
               if (entity.getHealth() > damage) {
                  entity.hurt(entity.damageSources().magic(), damage);
               } else if (entity.getHealth() > 1.0F) {
                  entity.hurt(entity.damageSources().magic(), entity.getHealth() - 1.0F);
               }
            }
         }
         return true;
      }

      public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
         return true;
      }

      private void applyModifiers(LivingEntity entity) {
         this.safeAddModifier(entity, EpicFightAttributes.ARMOR_NEGATION, BloodlustEnhance.ARMOR_NEGATION_UUID, "Bloodlust Armor Negation");
         this.safeAddModifier(entity, EpicFightAttributes.IMPACT, BloodlustEnhance.IMPACT_UUID, "Bloodlust Impact");
         this.safeAddModifier(entity, EpicFightAttributes.STUN_ARMOR, BloodlustEnhance.STUN_ARMOR_UUID, "Bloodlust Stun Armor");
         this.safeAddModifier(entity, EpicFightAttributes.STAMINA_REGEN, BloodlustEnhance.STAMINA_REGEN_UUID, "Bloodlust Stamina Regen");
         this.safeAddModifier(entity, Attributes.ATTACK_DAMAGE, BloodlustEnhance.DAMAGE_UUID, "Bloodlust Damage");
      }

      private void removeModifiers(LivingEntity entity) {
         this.safeRemoveModifier(entity, EpicFightAttributes.ARMOR_NEGATION, BloodlustEnhance.ARMOR_NEGATION_UUID);
         this.safeRemoveModifier(entity, EpicFightAttributes.IMPACT, BloodlustEnhance.IMPACT_UUID);
         this.safeRemoveModifier(entity, EpicFightAttributes.STUN_ARMOR, BloodlustEnhance.STUN_ARMOR_UUID);
         this.safeRemoveModifier(entity, EpicFightAttributes.STAMINA_REGEN, BloodlustEnhance.STAMINA_REGEN_UUID);
         this.safeRemoveModifier(entity, Attributes.ATTACK_DAMAGE, BloodlustEnhance.DAMAGE_UUID);
      }

      private void safeAddModifier(LivingEntity entity, Holder<Attribute> attribute, UUID uuid, String name) {
         EffectAttributeModifiers.addTransient(entity, attribute, uuid, 0.19999999999999996, Operation.ADD_MULTIPLIED_TOTAL);
      }

      private void safeRemoveModifier(LivingEntity entity, Holder<Attribute> attribute, UUID uuid) {
         EffectAttributeModifiers.remove(entity, attribute, uuid);
      }
   }
}
