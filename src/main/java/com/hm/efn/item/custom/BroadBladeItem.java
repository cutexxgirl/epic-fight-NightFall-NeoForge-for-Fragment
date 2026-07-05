package com.hm.efn.item.custom;

import net.minecraft.core.Holder;

import com.hm.efn.event.BroadBladeEffectManager;
import com.hm.efn.util.ItemStackData;
import com.merlin204.avalon.item.animationitem.IAvalonAnimationItem;
import com.p1nero.invincible.client.InvincibleKeyMappings;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Applicable;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent.Applicable.Result;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.world.item.WeaponItem;

@EventBusSubscriber(modid = "efn", bus = Bus.GAME)
public class BroadBladeItem extends WeaponItem implements IAvalonAnimationItem {
   private final ArmatureAccessor<?> armatureAccessor = ArmatureAccessor.create("efn", "weapon/broadblade", Armature::new);
   private static final String KILL_COUNT_KEY = "KillCount";

   public BroadBladeItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
      super(properties.attributes(net.minecraft.world.item.SwordItem.createAttributes(tier, attackDamage, attackSpeed)));
   }

   public static int getKillCount(ItemStack stack) {
      return ItemStackData.getInt(stack, KILL_COUNT_KEY);
   }

   private static void incrementKillCount(ItemStack stack) {
      int currentCount = ItemStackData.getInt(stack, KILL_COUNT_KEY);
      ItemStackData.putInt(stack, KILL_COUNT_KEY, currentCount + 1);
   }

   @SubscribeEvent
   public static void onLivingDeath(LivingDeathEvent event) {
      if (event.getSource().getEntity() instanceof Player player) {
         ItemStack mainHand = player.getMainHandItem();
         if (mainHand.getItem() instanceof BroadBladeItem) {
            incrementKillCount(mainHand);
         }
      }
   }

   @SubscribeEvent
   public static void onLivingKnockBack(LivingKnockBackEvent event) {
      if (event.getEntity() instanceof Player player && isHoldingWeapon(player)) {
         event.setCanceled(true);
      }
   }

   private static boolean isHoldingWeapon(Player player) {
      return player.getMainHandItem().getItem() instanceof BroadBladeItem || player.getOffhandItem().getItem() instanceof BroadBladeItem;
   }

   public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
      super.inventoryTick(stack, level, entity, slotId, isSelected);
      if (entity instanceof Player player && stack.getItem() instanceof BroadBladeItem) {
         BroadBladeEffectManager.applyImmunity(player);
         BroadBladeEffectManager.applyRandomGains(player);
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

   public ArmatureAccessor<? extends Armature> getArmature() {
      return (ArmatureAccessor<? extends Armature>)this.armatureAccessor;
   }

   public int getEnchantmentValue() {
      return 15;
   }

   public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
      pTooltipComponents.add(Component.translatable("item.efn.broadblade.description1"));
      if (InvincibleKeyMappings.KEY1.getKey().getValue() != 0) {
         pTooltipComponents.add(Component.translatable("tooltip.efn.keybind_warning"));
      }

      int killCount = getKillCount(pStack);
      pTooltipComponents.add(
         Component.translatable("tooltip.efn.broadblade.kill_count", new Object[]{killCount}).withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD)
      );
   }

   @SubscribeEvent
   public static void onEffectApplied(Applicable event) {
      if (event.getEntity() instanceof Player player) {
         ItemStack mainHand = player.getMainHandItem();
         if (mainHand.getItem() instanceof BroadBladeItem) {
            int killCount = getKillCount(mainHand);
            if (killCount > 100) {
               MobEffectInstance effect = event.getEffectInstance();
                Holder<MobEffect> mobEffect = effect.getEffect();
               if (BroadBladeEffectManager.IMMUNE_EFFECTS.contains(mobEffect)) {
                  event.setResult(Result.DO_NOT_APPLY);
               }
            }
         }
      }
   }
}
