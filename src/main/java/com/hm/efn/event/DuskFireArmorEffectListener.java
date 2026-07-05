package com.hm.efn.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = "efn")
public class DuskFireArmorEffectListener {
   @SubscribeEvent
   public static void onLivingDamage(LivingDamageEvent.Pre event) {
      if (event.getEntity() instanceof Player player) {
         handleFireDamageReduction(player, event);
      }
   }

   private static void handleFireDamageReduction(Player player, LivingDamageEvent.Pre event) {
      DamageSource source = event.getSource();
      boolean isFireDamage = source.is(DamageTypes.IN_FIRE)
         || source.is(DamageTypes.ON_FIRE)
         || source.is(DamageTypes.LAVA)
         || source.is(DamageTypes.HOT_FLOOR);
      if (isFireDamage) {
         int armorCount = DuskFireArmorHelper.getDuskFireArmorCount(player);
         if (armorCount > 0) {
            float damageReduction = calculateFireDamageReduction(armorCount);
            float originalDamage = event.getNewDamage();
            float reducedDamage = originalDamage * (1.0F - damageReduction);
            event.setNewDamage(reducedDamage);
         }
      }
   }

   private static float calculateFireDamageReduction(int armorCount) {
      return switch (armorCount) {
         case 1 -> 0.25F;
         case 2 -> 0.5F;
         case 3 -> 0.75F;
         case 4 -> 1.0F;
         default -> 0.0F;
      };
   }

   @SubscribeEvent
   public static void onLivingUpdate(EntityTickEvent.Pre event) {
      if (event.getEntity() instanceof Player player) {
         handleFireImmunity(player);
      }
   }

   private static void handleFireImmunity(Player player) {
      boolean wearingFullSet = DuskFireArmorHelper.isWearingFullDuskFireArmor(player);
      if (wearingFullSet) {
         if (player.isOnFire()) {
            player.clearFire();
         }

         if (player.isInLava() && player.getDeltaMovement().y < 0.1) {
            player.setDeltaMovement(player.getDeltaMovement().x, 0.1, player.getDeltaMovement().z);
         }
      }
   }
}
