package com.hm.efn.util;

import net.minecraft.world.item.ItemStack;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

public class ItemCapabilityChecker {
   public static boolean hasCapabilityItem(ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      }

      CapabilityItem itemCap = EpicFightCapabilities.getItemStackCapability(stack);
      return !itemCap.isEmpty();
   }

   public static boolean hasWeaponCapability(ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      }

      CapabilityItem itemCap = EpicFightCapabilities.getItemStackCapability(stack);
      return !itemCap.isEmpty() && itemCap.getWeaponCategory() != null;
   }
}
