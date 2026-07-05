package com.hm.efn.item.custom;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.ItemAbilities;

public class SheathItem extends Item {
   public SheathItem(Properties properties) {
      super(properties);
   }

   public boolean isDamageable(ItemStack stack) {
      return false;
   }

   public boolean canPerformAction(ItemStack stack, ItemAbility toolAction) {
      return toolAction == ItemAbilities.SWORD_SWEEP;
   }
}
