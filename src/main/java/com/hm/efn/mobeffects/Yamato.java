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
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@EventBusSubscriber(modid = "efn")
public class Yamato {
   private static final UUID DAMAGE_UUID = UUID.fromString("1c2249f0-19f3-11ec-0721-0242ac130003");
   private static final double MULTIPLIER = 1.35;
   private static final double MULTIPLIER_MINUS_ONE = 0.3500000000000001;

   @SubscribeEvent
   public static void onEffectAdded(Added event) {
      if (event.getEffectInstance().getEffect().value() instanceof Yamato.YamatoEffect) {
         if (Yamato.YamatoEffect.hasYamatoSkill(event.getEntity())) {
            event.getEntity().removeEffect(EFNMobEffectRegistry.YAMATO);
            return;
         }

         ((Yamato.YamatoEffect)event.getEffectInstance().getEffect().value()).applyModifiers(event.getEntity());
      }
   }

   @SubscribeEvent
   public static void onEffectRemoved(Remove event) {
      if (event.getEffect().value() instanceof Yamato.YamatoEffect) {
         ((Yamato.YamatoEffect)event.getEffect().value()).removeModifiers(event.getEntity());
      }
   }

   @SubscribeEvent
   public static void onEffectExpired(Expired event) {
      if (Objects.requireNonNull(event.getEffectInstance()).getEffect().value() instanceof Yamato.YamatoEffect) {
         ((Yamato.YamatoEffect)event.getEffectInstance().getEffect().value()).removeModifiers(event.getEntity());
      }
   }

   @SubscribeEvent
   public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
      if (event.getEntity() instanceof Player
         && event.getSlot() == EquipmentSlot.MAINHAND
         && event.getEntity().hasEffect(EFNMobEffectRegistry.YAMATO)) {
         event.getEntity().removeEffect(EFNMobEffectRegistry.YAMATO);
      }
   }

   public static class YamatoEffect extends MobEffect {
      public YamatoEffect() {
         super(MobEffectCategory.NEUTRAL, 16777215);
      }

      public static boolean hasYamatoSkill(LivingEntity entity) {
         if (!(entity instanceof Player)) {
            return true;
         }

         PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(entity, PlayerPatch.class);
         return playerPatch == null || playerPatch.getSkill(com.hm.efn.gameasset.combos.Yamato.yamato) == null;
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
         if (!hasYamatoSkill(entity)) {
            this.safeAddModifier(entity, Attributes.ATTACK_DAMAGE, Yamato.DAMAGE_UUID, "Yamato Damage");
         }
      }

      private void removeModifiers(LivingEntity entity) {
         this.safeRemoveModifier(entity, Attributes.ATTACK_DAMAGE, Yamato.DAMAGE_UUID);
      }

      private void safeAddModifier(LivingEntity entity, Holder<Attribute> attribute, UUID uuid, String name) {
         EffectAttributeModifiers.addTransient(entity, attribute, uuid, 0.3500000000000001, Operation.ADD_MULTIPLIED_TOTAL);
      }

      private void safeRemoveModifier(LivingEntity entity, Holder<Attribute> attribute, UUID uuid) {
         EffectAttributeModifiers.remove(entity, attribute, uuid);
      }
   }
}
