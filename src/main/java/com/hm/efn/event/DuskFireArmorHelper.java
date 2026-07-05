package com.hm.efn.event;

import com.hm.efn.item.custom.DuskFireArmorItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class DuskFireArmorHelper {
   public static boolean isWearingFullDuskFireArmor(Player player) {
      if (player == null) {
         return false;
      }

      boolean head = isDuskFireArmor(player.getItemBySlot(EquipmentSlot.HEAD));
      boolean chest = isDuskFireArmor(player.getItemBySlot(EquipmentSlot.CHEST));
      boolean legs = isDuskFireArmor(player.getItemBySlot(EquipmentSlot.LEGS));
      boolean feet = isDuskFireArmor(player.getItemBySlot(EquipmentSlot.FEET));
      return head && chest && legs && feet;
   }

   private static boolean isDuskFireArmor(ItemStack stack) {
      return !stack.isEmpty() && stack.getItem() instanceof DuskFireArmorItem;
   }

   public static int getDuskFireArmorCount(Player player) {
      if (player == null) {
         return 0;
      }

      int count = 0;
      if (isDuskFireArmor(player.getItemBySlot(EquipmentSlot.HEAD))) {
         count++;
      }

      if (isDuskFireArmor(player.getItemBySlot(EquipmentSlot.CHEST))) {
         count++;
      }

      if (isDuskFireArmor(player.getItemBySlot(EquipmentSlot.LEGS))) {
         count++;
      }

      if (isDuskFireArmor(player.getItemBySlot(EquipmentSlot.FEET))) {
         count++;
      }

      return count;
   }
}
