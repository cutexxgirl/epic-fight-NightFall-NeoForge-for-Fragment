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
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Pre;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Added;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Expired;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Remove;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import yesman.epicfight.registry.entries.EpicFightAttributes;

@EventBusSubscriber(modid = "efn")
public class ClawEnhance {
   private static final UUID ARMOR_NEGATION_UUID = UUID.fromString("b0a7436e-5734-11eb-ae93-0242ac130014");
   private static final UUID IMPACT_UUID = UUID.fromString("b0a745b2-5734-11eb-ae93-0242ac130014");
   private static final UUID STUN_ARMOR_UUID = UUID.fromString("b0a746ac-5734-11eb-ae93-0242ac130014");
   private static final UUID STAMINA_REGEN_UUID = UUID.fromString("1c224694-19f3-11ec-9621-0242ac130104");
   private static final UUID DAMAGE_UUID = UUID.fromString("1c2249f0-19f3-11ec-9621-0242ac130104");
   private static final UUID SPEED_UUID = UUID.fromString("1c2249f0-19f3-22ec-9621-0242ac130104");
   private static final UUID JUMP_UUID = UUID.fromString("1c2249f0-19f3-33ec-9621-0242ac130104");
   private static final UUID ATKSPEED_UUID = UUID.fromString("1c2249f0-19f3-44ec-9621-0242ac130104");
   private static final UUID DAMAGE_VULNERABILITY_UUID = UUID.fromString("1c2249f1-19f3-11ec-9621-0242ac130105");
   private static final double MULTIPLIER = 1.3;
   private static final double MULTIPLIER_MINUS_ONE = 0.30000000000000004;
   private static final double DAMAGE_VULNERABILITY = 0.25;

   @SubscribeEvent
   public static void onEffectAdded(Added event) {
      if (event.getEffectInstance().getEffect().value() instanceof ClawEnhance.ClawEffect) {
         LivingEntity entity = event.getEntity();
         ((ClawEnhance.ClawEffect)event.getEffectInstance().getEffect().value()).applyModifiers(entity);
      }
   }

   @SubscribeEvent
   public static void onEffectRemoved(Remove event) {
      if (event.getEffect().value() instanceof ClawEnhance.ClawEffect) {
         ((ClawEnhance.ClawEffect)event.getEffect().value()).removeModifiers(event.getEntity());
      }
   }

   @SubscribeEvent
   public static void onEffectExpired(Expired event) {
      if (event.getEffectInstance() != null && event.getEffectInstance().getEffect().value() instanceof ClawEnhance.ClawEffect) {
         ((ClawEnhance.ClawEffect)event.getEffectInstance().getEffect().value()).removeModifiers(event.getEntity());
      }
   }

   @SubscribeEvent
   public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
      if (event.getEntity() instanceof Player
         && event.getSlot() == EquipmentSlot.MAINHAND
         && event.getEntity().hasEffect(EFNMobEffectRegistry.CLAW)) {
         event.getEntity().removeEffect(EFNMobEffectRegistry.CLAW);
      }
   }

   @SubscribeEvent
   public static void onLivingDamage(Pre event) {
      LivingEntity entity = event.getEntity();
      if (entity.hasEffect(EFNMobEffectRegistry.CLAW)) {
         event.setNewDamage((float)(event.getNewDamage() * 1.25));
      }
   }

   public static class ClawEffect extends MobEffect {
      public ClawEffect() {
         super(MobEffectCategory.NEUTRAL, 9127187);
      }

      public boolean applyEffectTick(LivingEntity entity, int amplifier) {
         if (!entity.level().isClientSide()) {
            this.applyModifiers(entity);
         }
         return true;
      }

      public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
         return true;
      }

      private void applyModifiers(LivingEntity entity) {
         this.safeAddModifier(entity, EpicFightAttributes.ARMOR_NEGATION, ClawEnhance.ARMOR_NEGATION_UUID, "Claw Armor Negation");
         this.safeAddModifier(entity, EpicFightAttributes.IMPACT, ClawEnhance.IMPACT_UUID, "Claw Impact");
         this.safeAddModifier(entity, EpicFightAttributes.STUN_ARMOR, ClawEnhance.STUN_ARMOR_UUID, "Claw Stun Armor");
         this.safeAddModifier(entity, EpicFightAttributes.STAMINA_REGEN, ClawEnhance.STAMINA_REGEN_UUID, "Claw Stamina Regen");
         this.safeAddModifier(entity, Attributes.ATTACK_DAMAGE, ClawEnhance.DAMAGE_UUID, "Claw Damage");
         this.safeAddModifier(entity, Attributes.MOVEMENT_SPEED, ClawEnhance.SPEED_UUID, "Claw Speed");
         this.safeAddModifier(entity, Attributes.ATTACK_SPEED, ClawEnhance.ATKSPEED_UUID, "Claw AtkSpeed");
         this.safeAddModifier(entity, Attributes.JUMP_STRENGTH, ClawEnhance.JUMP_UUID, "Claw Jump");
         this.safeAddModifier(entity, Attributes.ARMOR, ClawEnhance.DAMAGE_VULNERABILITY_UUID, "Claw Vulnerability", -0.25);
      }

      private void removeModifiers(LivingEntity entity) {
         this.safeRemoveModifier(entity, EpicFightAttributes.ARMOR_NEGATION, ClawEnhance.ARMOR_NEGATION_UUID);
         this.safeRemoveModifier(entity, EpicFightAttributes.IMPACT, ClawEnhance.IMPACT_UUID);
         this.safeRemoveModifier(entity, EpicFightAttributes.STUN_ARMOR, ClawEnhance.STUN_ARMOR_UUID);
         this.safeRemoveModifier(entity, EpicFightAttributes.STAMINA_REGEN, ClawEnhance.STAMINA_REGEN_UUID);
         this.safeRemoveModifier(entity, Attributes.ATTACK_DAMAGE, ClawEnhance.DAMAGE_UUID);
         this.safeRemoveModifier(entity, Attributes.MOVEMENT_SPEED, ClawEnhance.SPEED_UUID);
         this.safeRemoveModifier(entity, Attributes.ATTACK_SPEED, ClawEnhance.ATKSPEED_UUID);
         this.safeRemoveModifier(entity, Attributes.JUMP_STRENGTH, ClawEnhance.JUMP_UUID);
         this.safeRemoveModifier(entity, Attributes.ARMOR, ClawEnhance.DAMAGE_VULNERABILITY_UUID);
      }

      private void safeAddModifier(LivingEntity entity, Holder<Attribute> attribute, UUID uuid, String name) {
         this.safeAddModifier(entity, attribute, uuid, name, 0.30000000000000004);
      }

      private void safeAddModifier(LivingEntity entity, Holder<Attribute> attribute, UUID uuid, String name, double amount) {
         EffectAttributeModifiers.addTransient(entity, attribute, uuid, amount, Operation.ADD_MULTIPLIED_TOTAL);
      }

      private void safeRemoveModifier(LivingEntity entity, Holder<Attribute> attribute, UUID uuid) {
         EffectAttributeModifiers.remove(entity, attribute, uuid);
      }
   }
}
