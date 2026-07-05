package com.hm.efn.event;

import com.hm.efn.item.custom.BroadBladeItem;
import com.hm.efn.registries.EFNMobEffectRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

public class BroadBladeEffectManager {
   public static final List<Holder<MobEffect>> IMMUNE_EFFECTS = Arrays.asList(
      MobEffects.DARKNESS, MobEffects.BLINDNESS, MobEffects.MOVEMENT_SLOWDOWN, MobEffects.DIG_SLOWDOWN
   );
   private static final List<BroadBladeEffectManager.GainEffect> GAIN_EFFECTS = Arrays.asList(
      new BroadBladeEffectManager.GainEffect(MobEffects.DAMAGE_RESISTANCE, 1, 100),
      new BroadBladeEffectManager.GainEffect(MobEffects.DAMAGE_BOOST, 1, 100),
      new BroadBladeEffectManager.GainEffect(MobEffects.MOVEMENT_SPEED, 1, 100),
      new BroadBladeEffectManager.GainEffect(EFNMobEffectRegistry.GRADUAL_HEAL, 5, 100),
      new BroadBladeEffectManager.GainEffect(EFNMobEffectRegistry.SIN_STUN_IMMUNITY, 1, 100)
   );
   private static final Map<UUID, Long> lastGainTimeMap = new HashMap<>();

   private static boolean isHoldingBroadBlade(Player player) {
      ItemStack mainHand = player.getMainHandItem();
      ItemStack offHand = player.getOffhandItem();
      return mainHand.getItem() instanceof BroadBladeItem || offHand.getItem() instanceof BroadBladeItem;
   }

   private static ItemStack getHeldBroadBlade(Player player) {
      ItemStack mainHand = player.getMainHandItem();
      if (mainHand.getItem() instanceof BroadBladeItem) {
         return mainHand;
      }

      ItemStack offHand = player.getOffhandItem();
      return offHand.getItem() instanceof BroadBladeItem ? offHand : ItemStack.EMPTY;
   }

   public static void applyImmunity(Player player) {
      if (!player.level().isClientSide() && isHoldingBroadBlade(player)) {
         ItemStack broadBlade = getHeldBroadBlade(player);
         if (!broadBlade.isEmpty()) {
            int killCount = BroadBladeItem.getKillCount(broadBlade);
            if (killCount >= 100) {
               for (Holder<MobEffect> effect : IMMUNE_EFFECTS) {
                  if (player.hasEffect(effect)) {
                     player.removeEffect(effect);
                  }
               }
            }
         }
      }
   }

   public static void applyRandomGains(Player player) {
      if (!player.level().isClientSide() && isHoldingBroadBlade(player)) {
         ItemStack broadBlade = getHeldBroadBlade(player);
         if (!broadBlade.isEmpty()) {
            int killCount = BroadBladeItem.getKillCount(broadBlade);
            if (killCount >= 100) {
               UUID playerId = player.getUUID();
               long currentTime = System.currentTimeMillis();
               long lastGainTime = lastGainTimeMap.getOrDefault(playerId, 0L);
               if (currentTime - lastGainTime >= 10000L) {
                  lastGainTimeMap.put(playerId, currentTime);

                  for (BroadBladeEffectManager.GainEffect gainEffect : selectRandomEffects(2)) {
                     player.addEffect(new MobEffectInstance(gainEffect.effect, gainEffect.duration, gainEffect.amplifier, false, false, true));
                  }
               }
            }
         }
      }
   }

   public static void applyAllEffects(Player player) {
      applyImmunity(player);
      applyRandomGains(player);
   }

   private static List<BroadBladeEffectManager.GainEffect> selectRandomEffects(int count) {
      List<BroadBladeEffectManager.GainEffect> selected = new ArrayList<>();
      List<BroadBladeEffectManager.GainEffect> available = new ArrayList<>(GAIN_EFFECTS);
      Random random = new Random();
      count = Math.min(count, available.size());

      for (int i = 0; i < count && !available.isEmpty(); i++) {
         int index = random.nextInt(available.size());
         selected.add(available.get(index));
         available.remove(index);
      }

      return selected;
   }

   public static void clearPlayerTimer(Player player) {
      lastGainTimeMap.remove(player.getUUID());
   }

   public static int getCurrentKillCount(Player player) {
      if (isHoldingBroadBlade(player)) {
         ItemStack broadBlade = getHeldBroadBlade(player);
         if (!broadBlade.isEmpty()) {
            return BroadBladeItem.getKillCount(broadBlade);
         }
      }

      return 0;
   }

   public static boolean hasImmunity(Player player) {
      if (isHoldingBroadBlade(player)) {
         ItemStack broadBlade = getHeldBroadBlade(player);
         if (!broadBlade.isEmpty()) {
            return BroadBladeItem.getKillCount(broadBlade) >= 100;
         }
      }

      return false;
   }

   public static boolean hasGainEligibility(Player player) {
      if (isHoldingBroadBlade(player)) {
         ItemStack broadBlade = getHeldBroadBlade(player);
         if (!broadBlade.isEmpty()) {
            int killCount = BroadBladeItem.getKillCount(broadBlade);
            if (killCount >= 100) {
               UUID playerId = player.getUUID();
               long currentTime = System.currentTimeMillis();
               long lastGainTime = lastGainTimeMap.getOrDefault(playerId, 0L);
               return currentTime - lastGainTime >= 10000L;
            }
         }
      }

      return false;
   }

   @EventBusSubscriber(modid = "efn")
   public static class CleanupHandler {
      @SubscribeEvent
      public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
         BroadBladeEffectManager.lastGainTimeMap.remove(event.getEntity().getUUID());
      }
   }

   private static class GainEffect {
      final Holder<MobEffect> effect;
      final int amplifier;
      final int duration;

      GainEffect(Holder<MobEffect> effect, int amplifier, int duration) {
         this.effect = effect;
         this.amplifier = amplifier;
         this.duration = duration;
      }
   }
}
