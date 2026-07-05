package com.hm.efn.item.custom;

import net.minecraft.core.Holder;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.world.item.WeaponItem;

public class ScytheItem extends WeaponItem {
   public ScytheItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
      super(properties.attributes(net.minecraft.world.item.SwordItem.createAttributes(tier, attackDamage, attackSpeed)));
   }

   public boolean isDamageable(ItemStack stack) {
      return false;
   }

   public boolean isEnchantable(@NotNull ItemStack stack) {
      return true;
   }

   public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
      return true;
   }

   public int getEnchantmentValue() {
      return 30;
   }

   public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
      pTooltipComponents.add(Component.translatable("item.efn.scythe.description1"));
   }
}
