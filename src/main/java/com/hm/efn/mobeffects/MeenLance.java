package com.hm.efn.mobeffects;

import com.hm.efn.registries.EFNMobEffectRegistry;
import java.util.Objects;
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
public class MeenLance {
   private static final UUID ARMOR_NEGATION_UUID = UUID.fromString("b0a7436e-5734-11eb-ae93-0242ac130003");
   private static final UUID IMPACT_UUID = UUID.fromString("b0a745b2-5734-11eb-ae93-0242ac130003");
   private static final UUID STUN_ARMOR_UUID = UUID.fromString("b0a746ac-5734-11eb-ae93-0242ac130003");
   private static final UUID STAMINA_REGEN_UUID = UUID.fromString("1c224694-19f3-11ec-9621-0242ac130003");
   private static final UUID DAMAGE_UUID = UUID.fromString("1c2249f0-19f3-11ec-9621-0242ac130003");
   private static final double MULTIPLIER = 1.45;
   private static final double MULTIPLIER_MINUS_ONE = 0.44999999999999996;

   @SubscribeEvent
   public static void onEffectAdded(Added event) {
      if (event.getEffectInstance().getEffect().value() instanceof MeenLance.MeenLanceEffect) {
         ((MeenLance.MeenLanceEffect)event.getEffectInstance().getEffect().value()).applyModifiers(event.getEntity());
      }
   }

   @SubscribeEvent
   public static void onEffectRemoved(Remove event) {
      if (event.getEffect().value() instanceof MeenLance.MeenLanceEffect) {
         ((MeenLance.MeenLanceEffect)event.getEffect().value()).removeModifiers(event.getEntity());
      }
   }

   @SubscribeEvent
   public static void onEffectExpired(Expired event) {
      if (Objects.requireNonNull(event.getEffectInstance()).getEffect().value() instanceof MeenLance.MeenLanceEffect) {
         ((MeenLance.MeenLanceEffect)event.getEffectInstance().getEffect().value()).removeModifiers(event.getEntity());
      }
   }

   @SubscribeEvent
   public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
      if (event.getEntity() instanceof Player
         && event.getSlot() == EquipmentSlot.MAINHAND
         && event.getEntity().hasEffect(EFNMobEffectRegistry.MEEN_LANCE)) {
         event.getEntity().removeEffect(EFNMobEffectRegistry.MEEN_LANCE);
      }
   }

   public static class MeenLanceEffect extends MobEffect {
      public MeenLanceEffect() {
         super(MobEffectCategory.NEUTRAL, 16777215);
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
         this.safeAddModifier(entity, EpicFightAttributes.ARMOR_NEGATION, MeenLance.ARMOR_NEGATION_UUID, "MeenLance Armor Negation");
         this.safeAddModifier(entity, EpicFightAttributes.IMPACT, MeenLance.IMPACT_UUID, "MeenLance Impact");
         this.safeAddModifier(entity, EpicFightAttributes.STUN_ARMOR, MeenLance.STUN_ARMOR_UUID, "MeenLance Stun Armor");
         this.safeAddModifier(entity, EpicFightAttributes.STAMINA_REGEN, MeenLance.STAMINA_REGEN_UUID, "MeenLance Stamina Regen");
         this.safeAddModifier(entity, Attributes.ATTACK_DAMAGE, MeenLance.DAMAGE_UUID, "MeenLance Damage");
      }

      private void removeModifiers(LivingEntity entity) {
         this.safeRemoveModifier(entity, EpicFightAttributes.ARMOR_NEGATION, MeenLance.ARMOR_NEGATION_UUID);
         this.safeRemoveModifier(entity, EpicFightAttributes.IMPACT, MeenLance.IMPACT_UUID);
         this.safeRemoveModifier(entity, EpicFightAttributes.STUN_ARMOR, MeenLance.STUN_ARMOR_UUID);
         this.safeRemoveModifier(entity, EpicFightAttributes.STAMINA_REGEN, MeenLance.STAMINA_REGEN_UUID);
         this.safeRemoveModifier(entity, Attributes.ATTACK_DAMAGE, MeenLance.DAMAGE_UUID);
      }

      private void safeAddModifier(LivingEntity entity, Holder<Attribute> attribute, UUID uuid, String name) {
         EffectAttributeModifiers.addTransient(entity, attribute, uuid, 0.44999999999999996, Operation.ADD_MULTIPLIED_TOTAL);
      }

      private void safeRemoveModifier(LivingEntity entity, Holder<Attribute> attribute, UUID uuid) {
         EffectAttributeModifiers.remove(entity, attribute, uuid);
      }
   }
}
