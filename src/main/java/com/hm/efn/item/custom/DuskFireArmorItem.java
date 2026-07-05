package com.hm.efn.item.custom;

import net.minecraft.core.Holder;

import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class DuskFireArmorItem extends ArmorItem {
   private static final Holder<ArmorMaterial> DUSKFIRE_MATERIAL = Holder.direct(
      new ArmorMaterial(
         Map.of(Type.BOOTS, 7, Type.LEGGINGS, 11, Type.CHESTPLATE, 13, Type.HELMET, 7),
         50,
         SoundEvents.ARMOR_EQUIP_NETHERITE,
         () -> Ingredient.of(),
         List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("efn", "duskfire"))),
         8.0F,
         0.2F
      )
   );

   public DuskFireArmorItem(Type type, Properties properties) {
      super(DUSKFIRE_MATERIAL, type, properties.rarity(Rarity.EPIC).fireResistant());
   }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
      super.inventoryTick(stack, level, entity, slotId, isSelected);
      if (!level.isClientSide() && entity instanceof Player player && player.getItemBySlot(this.type.getSlot()) == stack) {
         this.applyIndividualFireResistance(player, stack);
      }
   }

   private void applyIndividualFireResistance(Player player, ItemStack armorStack) {
      if (player.getRemainingFireTicks() > 0) {
         int fireTicks = player.getRemainingFireTicks();
         if (fireTicks > 20) {
            player.setRemainingFireTicks(fireTicks - 1);
         }
      }
   }

   public boolean isDamageable(ItemStack stack) {
      return false;
   }

   public static class Boots extends DuskFireArmorItem {
      public Boots() {
         super(Type.BOOTS, new Properties());
      }

      public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
         return "efn:textures/armor/duskfirearmor.png";
      }

      public boolean isEnchantable(@NotNull ItemStack stack) {
         return true;
      }

      public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
         return true;
      }

      public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
         pTooltipComponents.add(Component.translatable("item.efn.duskfire_armor.description1"));
      }
   }

   public static class Chestplate extends DuskFireArmorItem {
      public Chestplate() {
         super(Type.CHESTPLATE, new Properties());
      }

      public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
         return "efn:textures/armor/duskfirearmor.png";
      }

      public boolean isEnchantable(@NotNull ItemStack stack) {
         return true;
      }

      public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
         return true;
      }

      public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
         pTooltipComponents.add(Component.translatable("item.efn.duskfire_armor.description1"));
      }
   }

   public static class Helmet extends DuskFireArmorItem {
      public Helmet() {
         super(Type.HELMET, new Properties());
      }

      public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
         return "efn:textures/armor/duskfirearmor.png";
      }

      public boolean isEnchantable(@NotNull ItemStack stack) {
         return true;
      }

      public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
         return true;
      }

      public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
         pTooltipComponents.add(Component.translatable("item.efn.duskfire_armor.description1"));
      }
   }

   public static class Leggings extends DuskFireArmorItem {
      public Leggings() {
         super(Type.LEGGINGS, new Properties());
      }

      public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
         return "efn:textures/armor/duskfirearmor.png";
      }

      public boolean isEnchantable(@NotNull ItemStack stack) {
         return true;
      }

      public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
         return true;
      }

      public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
         pTooltipComponents.add(Component.translatable("item.efn.duskfire_armor.description1"));
      }
   }
}
