package com.hm.efn.item.custom;

import net.minecraft.core.Holder;

import com.p1nero.invincible.client.InvincibleKeyMappings;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent.Pre;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.world.item.WeaponItem;

@EventBusSubscriber(modid = "efn", bus = Bus.GAME)
public class HfBladeItem extends WeaponItem {
   public HfBladeItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
      super(properties.attributes(net.minecraft.world.item.SwordItem.createAttributes(tier, attackDamage, attackSpeed)));
   }

   @SubscribeEvent
   public static void onPlayerJump(LivingJumpEvent event) {
      if (event.getEntity() instanceof Player player && isHoldingHfBlade(player)) {
         player.setDeltaMovement(player.getDeltaMovement().add(0.0, 0.2, 0.0));
      }
   }

   @SubscribeEvent
   public static void onLivingFall(LivingFallEvent event) {
      if (event.getEntity() instanceof Player player && isHoldingHfBlade(player)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onLivingUpdate(Pre event) {
      Entity entity = event.getEntity();
      if (entity instanceof Player player && isHoldingHfBlade(player) && player.hasEffect(MobEffects.LEVITATION)) {
         player.removeEffect(MobEffects.LEVITATION);
      }
   }

   @SubscribeEvent
   public static void onLivingKnockBack(LivingKnockBackEvent event) {
      if (event.getEntity() instanceof Player player && isHoldingHfBlade(player)) {
         event.setCanceled(true);
      }
   }

   private static boolean isHoldingHfBlade(Player player) {
      return player.getMainHandItem().getItem() instanceof HfBladeItem || player.getOffhandItem().getItem() instanceof HfBladeItem;
   }

   public boolean isDamageable(ItemStack stack) {
      return false;
   }

   public boolean canEquip(ItemStack stack, EquipmentSlot slot, LivingEntity entity) {
      return slot == EquipmentSlot.MAINHAND;
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
      pTooltipComponents.add(Component.translatable("item.efn.hf_blade.description1").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.BOLD));
      if (InvincibleKeyMappings.KEY1.getKey().getValue() != 0) {
         pTooltipComponents.add(Component.translatable("tooltip.efn.keybind_warning"));
      }
   }
}
