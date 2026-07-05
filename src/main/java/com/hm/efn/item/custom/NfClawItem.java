package com.hm.efn.item.custom;

import net.minecraft.core.Holder;

import com.merlin204.avalon.item.animationitem.IAvalonAnimationItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.world.item.WeaponItem;

public class NfClawItem extends WeaponItem implements IAvalonAnimationItem {
   private final ArmatureAccessor<?> armatureAccessor = ArmatureAccessor.create("efn", "weapon/nf_claw", Armature::new);

   public NfClawItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
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

   public ArmatureAccessor<? extends Armature> getArmature() {
      return (ArmatureAccessor<? extends Armature>)this.armatureAccessor;
   }
}
