package com.hm.efn.mobeffects;

import java.util.UUID;
import java.util.Objects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Added;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Expired;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Remove;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = "efn")
public class KnockBackResistanceEffect {
   private static final UUID KNOCKBACK_RESISTANCE_UUID = UUID.fromString("a5d8a7e1-5734-11eb-ae93-0242ac130002");

   @SubscribeEvent
   public static void onEffectAdded(Added event) {
      if (event.getEffectInstance().getEffect().value() instanceof KnockBackResistanceEffect.ResistanceEffect) {
         ((KnockBackResistanceEffect.ResistanceEffect)event.getEffectInstance().getEffect().value()).addResistanceModifier(event.getEntity());
      }
   }

   @SubscribeEvent
   public static void onEffectRemoved(Remove event) {
      if (event.getEffect().value() instanceof KnockBackResistanceEffect.ResistanceEffect) {
         ((KnockBackResistanceEffect.ResistanceEffect)event.getEffect().value()).removeResistanceModifier(event.getEntity());
      }
   }

   @SubscribeEvent
   public static void onEffectExpired(Expired event) {
      if (Objects.requireNonNull(event.getEffectInstance()).getEffect().value() instanceof KnockBackResistanceEffect.ResistanceEffect) {
         ((KnockBackResistanceEffect.ResistanceEffect)event.getEffectInstance().getEffect().value()).removeResistanceModifier(event.getEntity());
      }
   }

   public static class ResistanceEffect extends MobEffect {
      public ResistanceEffect() {
         super(MobEffectCategory.NEUTRAL, 5921535);
      }

      public boolean applyEffectTick(LivingEntity entity, int amplifier) {
         if (!entity.level().isClientSide()) {
            this.addResistanceModifier(entity);
         }
         return true;
      }

      public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
         return true;
      }

      private void addResistanceModifier(LivingEntity entity) {
         EffectAttributeModifiers.addTransient(entity, Attributes.KNOCKBACK_RESISTANCE, KnockBackResistanceEffect.KNOCKBACK_RESISTANCE_UUID, 1.0, Operation.ADD_VALUE);
      }

      private void removeResistanceModifier(LivingEntity entity) {
         EffectAttributeModifiers.remove(entity, Attributes.KNOCKBACK_RESISTANCE, KnockBackResistanceEffect.KNOCKBACK_RESISTANCE_UUID);
      }
   }
}
