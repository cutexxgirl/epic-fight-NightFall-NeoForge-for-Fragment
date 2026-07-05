package com.hm.efn.item.custom;

import net.minecraft.core.Holder;

import com.p1nero.invincible.client.InvincibleKeyMappings;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.world.item.WeaponItem;

@EventBusSubscriber(modid = "efn")
public class Meen_SpearItem extends WeaponItem {
   public Meen_SpearItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
      super(properties.attributes(net.minecraft.world.item.SwordItem.createAttributes(tier, attackDamage, attackSpeed)));
   }

   private static boolean isHoldingMeenSpear(Player player) {
      ItemStack mainHand = player.getMainHandItem();
      ItemStack offHand = player.getOffhandItem();
      return mainHand.getItem() instanceof Meen_SpearItem || offHand.getItem() instanceof Meen_SpearItem;
   }

   @SubscribeEvent
   public static void onLivingAttack(LivingIncomingDamageEvent event) {
      if (event.getSource().is(DamageTypeTags.IS_FIRE) && event.getEntity() instanceof Player player && isHoldingMeenSpear(player)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onLivingDamage(Post event) {
      if (event.getSource().is(DamageTypeTags.IS_FIRE) && event.getEntity() instanceof Player player && isHoldingMeenSpear(player)) {
         player.clearFire();
      }
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

   public boolean hurtEnemy(@NotNull ItemStack stack, LivingEntity target, @NotNull LivingEntity attacker) {
      if (!target.fireImmune()) {
         target.igniteForTicks(100);
      }

      return super.hurtEnemy(stack, target, attacker);
   }

   public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
      super.inventoryTick(stack, level, entity, slotId, isSelected);
      if (!level.isClientSide() && stack.getEnchantmentLevel(level.holderOrThrow(Enchantments.FIRE_ASPECT)) < 3) {
         stack.enchant(level.holderOrThrow(Enchantments.FIRE_ASPECT), 3);
      }
   }

   @NotNull
   public ItemStack getDefaultInstance() {
      ItemStack stack = new ItemStack(this);
      return stack;
   }

   public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
      pTooltipComponents.add(Component.translatable("item.efn.meen_spear.description1"));
      if (InvincibleKeyMappings.KEY1.getKey().getValue() != 0) {
         pTooltipComponents.add(Component.translatable("tooltip.efn.keybind_warning"));
      }
   }
}
