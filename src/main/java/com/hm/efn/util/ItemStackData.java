package com.hm.efn.util;

import java.util.function.Consumer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class ItemStackData {
   private ItemStackData() {
   }

   public static CompoundTag copyTag(ItemStack stack) {
      return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
   }

   public static boolean contains(ItemStack stack, String key) {
      return copyTag(stack).contains(key);
   }

   public static int getInt(ItemStack stack, String key) {
      return copyTag(stack).getInt(key);
   }

   public static float getFloat(ItemStack stack, String key) {
      return copyTag(stack).getFloat(key);
   }

   public static boolean getBoolean(ItemStack stack, String key) {
      return copyTag(stack).getBoolean(key);
   }

   public static void update(ItemStack stack, Consumer<CompoundTag> updater) {
      CompoundTag tag = copyTag(stack);
      updater.accept(tag);
      stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
   }

   public static void putInt(ItemStack stack, String key, int value) {
      update(stack, tag -> tag.putInt(key, value));
   }

   public static void putBoolean(ItemStack stack, String key, boolean value) {
      update(stack, tag -> tag.putBoolean(key, value));
   }

   public static void putFloat(ItemStack stack, String key, float value) {
      update(stack, tag -> tag.putFloat(key, value));
   }
}
