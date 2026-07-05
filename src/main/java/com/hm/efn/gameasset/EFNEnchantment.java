package com.hm.efn.gameasset;

import com.hm.efn.registries.EFNItem;
import java.util.Arrays;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class EFNEnchantment {
   public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(Registries.ENCHANTMENT, "efn");
   public static final DeferredHolder<Enchantment, Enchantment> YAMATO_SUMMONED_SWORD = register(
      "yamato_summoned_sword", EFNItem.YAMATO_DMC, EFNItem.YAMATO_DMC4, EFNItem.YAMATO_DMC_IN_SHEATH, EFNItem.YAMATO_DMC4_IN_SHEATH
   );
   public static final DeferredHolder<Enchantment, Enchantment> YAMATO_SUMMONED_SWORD_END = register(
      "yamato_summoned_sword_end", EFNItem.YAMATO_DMC, EFNItem.YAMATO_DMC4, EFNItem.YAMATO_DMC_IN_SHEATH, EFNItem.YAMATO_DMC4_IN_SHEATH
   );
   public static final DeferredHolder<Enchantment, Enchantment> YAMATO_JUDGEMENT_CUT_END = register(
      "yamato_judgement_cut_end", EFNItem.YAMATO_DMC, EFNItem.YAMATO_DMC4, EFNItem.YAMATO_DMC_IN_SHEATH, EFNItem.YAMATO_DMC4_IN_SHEATH
   );
   public static final DeferredHolder<Enchantment, Enchantment> YAMATO_GUARD = register(
      "yamato_guard", EFNItem.YAMATO_DMC, EFNItem.YAMATO_DMC4, EFNItem.YAMATO_DMC_IN_SHEATH, EFNItem.YAMATO_DMC4_IN_SHEATH
   );
   public static final DeferredHolder<Enchantment, Enchantment> YAMATO_DOPPELGANGER = register(
      "yamato_doppelganger", EFNItem.YAMATO_DMC, EFNItem.YAMATO_DMC4, EFNItem.YAMATO_DMC_IN_SHEATH, EFNItem.YAMATO_DMC4_IN_SHEATH
   );
   public static final DeferredHolder<Enchantment, Enchantment> YAMATO_HEAVY_RAIN = register(
      "yamato_heavy_rain", EFNItem.YAMATO_DMC, EFNItem.YAMATO_DMC4, EFNItem.YAMATO_DMC_IN_SHEATH, EFNItem.YAMATO_DMC4_IN_SHEATH
   );
   public static final DeferredHolder<Enchantment, Enchantment> SCYTHE_ENHANCE = register(
      "scythe_enhance", EFNItem.CRIMSON_MOON, EFNItem.CRIMSON_MOON_E
   );
   public static final DeferredHolder<Enchantment, Enchantment> BROAD_BLADE_ENHANCE = register("broad_blade_enhance", EFNItem.BROADBLADE);

   @SafeVarargs
   private static DeferredHolder<Enchantment, Enchantment> register(String name, Supplier<? extends Item>... supportedItems) {
      return ENCHANTMENTS.register(name, () -> create(name, supportedItems));
   }

   @SafeVarargs
   private static Enchantment create(String name, Supplier<? extends Item>... supportedItems) {
      HolderSet<Item> supported = supportedItems(supportedItems);
      return new Enchantment(
         Component.translatable("enchantment.efn." + name),
         Enchantment.definition(supported, supported, 1, 1, Enchantment.constantCost(1), Enchantment.constantCost(1), 1, EquipmentSlotGroup.MAINHAND),
         HolderSet.empty(),
         DataComponentMap.EMPTY
      );
   }

   @SafeVarargs
   private static HolderSet<Item> supportedItems(Supplier<? extends Item>... supportedItems) {
      return HolderSet.direct(Arrays.stream(supportedItems).map(EFNEnchantment::holder).toList());
   }

   @SuppressWarnings("unchecked")
   private static Holder<Item> holder(Supplier<? extends Item> item) {
      return (Holder<Item>)item;
   }

   public static int getLevel(ItemStack stack, Holder<Enchantment> enchantment) {
      return EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack);
   }

   public static int getLevel(ItemStack stack, ResourceKey<Enchantment> key) {
      ItemEnchantments enchantments = stack.getEnchantments();
      for (Holder<Enchantment> enchantment : enchantments.keySet()) {
         if (enchantment.is(key)) {
            return enchantments.getLevel(enchantment);
         }
      }

      return 0;
   }
}
