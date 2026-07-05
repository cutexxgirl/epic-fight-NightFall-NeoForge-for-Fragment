package com.hm.efn.client.events;

import com.hm.efn.entity.effect.TriggerEntity;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.util.EffekUnits;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Added;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Expired;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Remove;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = "efn")
public class AddTriggerEvent {
   private static final Map<UUID, Map<Holder<MobEffect>, TriggerEntity>> activeTriggers = new ConcurrentHashMap<>();
   private static final Map<Holder<MobEffect>, Vec3> EFFECT_TRIGGER_CONFIG = new LinkedHashMap<>();
   private static final Set<Holder<MobEffect>> VANILLA_TRIGGER_EFFECTS = new HashSet<>();

   @SubscribeEvent
   public static void onEffectAdded(Added event) {
      if (EffekUnits.VFXENABLE()) {
         LivingEntity entity = event.getEntity();
         MobEffectInstance effectInstance = event.getEffectInstance();
         Holder<MobEffect> effect = effectInstance.getEffect();
         if (!entity.level().isClientSide()) {
            Vec3 offset = getOffsetForEffect(effect);
            if (offset != null) {
               if (!hasActiveTrigger(entity, effect)) {
                  TriggerEntity trigger = TriggerEntity.createAndSpawn(entity, effect, offset);
                  if (trigger != null) {
                     activeTriggers.computeIfAbsent(entity.getUUID(), k -> new ConcurrentHashMap<>()).put(effect, trigger);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onEffectRemoved(Remove event) {
      if (EffekUnits.VFXENABLE()) {
         LivingEntity entity = event.getEntity();
         Holder<MobEffect> effect = event.getEffect();
         if (!entity.level().isClientSide()) {
            if (isConfiguredEffect(effect)) {
               removeAndDiscardTrigger(entity, effect);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onEffectExpired(Expired event) {
      if (EffekUnits.VFXENABLE()) {
         LivingEntity entity = event.getEntity();
         MobEffectInstance effectInstance = event.getEffectInstance();
         if (effectInstance != null && !entity.level().isClientSide()) {
            Holder<MobEffect> effect = effectInstance.getEffect();
            if (isConfiguredEffect(effect)) {
               removeAndDiscardTrigger(entity, effect);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeath(LivingDeathEvent event) {
      if (EffekUnits.VFXENABLE()) {
         LivingEntity entity = event.getEntity();
         removeAllTriggersForEntity(entity);
      }
   }

   @SubscribeEvent
   public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
      if (EffekUnits.VFXENABLE()) {
         Player player = event.getEntity();
         removeAllTriggersForEntity(player);
      }
   }

   @SubscribeEvent
   public static void onEntityTravelToDimension(EntityTravelToDimensionEvent event) {
      if (EffekUnits.VFXENABLE()) {
         if (event.getEntity() instanceof LivingEntity entity) {
            removeAllTriggersForEntity(entity);
         }
      }
   }

   private static boolean isConfiguredEffect(Holder<MobEffect> effect) {
      return EFFECT_TRIGGER_CONFIG.containsKey(effect) || VANILLA_TRIGGER_EFFECTS.contains(effect);
   }

   private static Vec3 getOffsetForEffect(Holder<MobEffect> effect) {
      Vec3 offset = EFFECT_TRIGGER_CONFIG.entrySet().stream().filter(entry -> entry.getKey().equals(effect)).map(Entry::getValue).findFirst().orElse(null);
      if (offset != null) {
         return offset;
      } else {
         return VANILLA_TRIGGER_EFFECTS.contains(effect) ? Vec3.ZERO : null;
      }
   }

   private static void removeAndDiscardTrigger(LivingEntity entity, Holder<MobEffect> effect) {
      Map<Holder<MobEffect>, TriggerEntity> entityTriggers = activeTriggers.get(entity.getUUID());
      if (entityTriggers != null) {
         TriggerEntity trigger = entityTriggers.remove(effect);
         if (trigger != null && trigger.isAlive()) {
            trigger.discard();
         }

         if (entityTriggers.isEmpty()) {
            activeTriggers.remove(entity.getUUID());
         }
      }
   }

   private static void removeAllTriggersForEntity(LivingEntity entity) {
      Map<Holder<MobEffect>, TriggerEntity> entityTriggers = activeTriggers.remove(entity.getUUID());
      if (entityTriggers != null) {
         entityTriggers.values().forEach(trigger -> {
            if (trigger != null && trigger.isAlive()) {
               trigger.discard();
            }
         });
      }
   }

   public static Map<Holder<MobEffect>, TriggerEntity> getActiveTriggers(LivingEntity entity) {
      return Collections.unmodifiableMap(activeTriggers.getOrDefault(entity.getUUID(), Collections.emptyMap()));
   }

   public static boolean hasActiveTrigger(LivingEntity entity, Holder<MobEffect> effect) {
      Map<Holder<MobEffect>, TriggerEntity> entityTriggers = activeTriggers.get(entity.getUUID());
      return entityTriggers != null && entityTriggers.containsKey(effect);
   }

   public static int getActiveTriggerCount(LivingEntity entity) {
      Map<Holder<MobEffect>, TriggerEntity> entityTriggers = activeTriggers.get(entity.getUUID());
      return entityTriggers != null ? entityTriggers.size() : 0;
   }

   public static TriggerEntity addCustomTrigger(LivingEntity entity, Holder<MobEffect> effect, Vec3 offset) {
      if (entity.level().isClientSide()) {
         return null;
      }

      if (hasActiveTrigger(entity, effect)) {
         return activeTriggers.get(entity.getUUID()).get(effect);
      }

      TriggerEntity trigger = TriggerEntity.createAndSpawn(entity, effect, offset);
      if (trigger != null) {
         activeTriggers.computeIfAbsent(entity.getUUID(), k -> new ConcurrentHashMap<>()).put(effect, trigger);
      }

      return trigger;
   }

   static {
      EFFECT_TRIGGER_CONFIG.put(EFNMobEffectRegistry.ATTACK_DAMAGE_INCREASE, new Vec3(0.0, 0.0, 0.0));
      EFFECT_TRIGGER_CONFIG.put(EFNMobEffectRegistry.ATTACK_SPEED_INCREASE, new Vec3(0.0, 0.0, 0.0));
      EFFECT_TRIGGER_CONFIG.put(EFNMobEffectRegistry.DAMAGE_REDUCTION, new Vec3(0.0, 0.0, 0.0));
      VANILLA_TRIGGER_EFFECTS.add(MobEffects.DAMAGE_BOOST);
      VANILLA_TRIGGER_EFFECTS.add(MobEffects.DAMAGE_RESISTANCE);
   }
}
