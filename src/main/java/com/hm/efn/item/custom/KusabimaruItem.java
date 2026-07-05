package com.hm.efn.item.custom;

import net.minecraft.core.Holder;

import com.merlin204.avalon.item.animationitem.IAvalonAnimationItem;
import com.p1nero.invincible.client.InvincibleKeyMappings;
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
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.world.item.WeaponItem;

public class KusabimaruItem extends WeaponItem implements IAvalonAnimationItem {
   private final ArmatureAccessor<?> armatureAccessor = ArmatureAccessor.create("efn", "weapon/kusabimaru", Armature::new);

   public KusabimaruItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
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

   public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
      pTooltipComponents.add(Component.translatable("item.efn.kusabimaru.description1"));
      if (InvincibleKeyMappings.KEY1.getKey().getValue() != 0) {
         pTooltipComponents.add(Component.translatable("tooltip.efn.keybind_warning"));
      }
   }
}
