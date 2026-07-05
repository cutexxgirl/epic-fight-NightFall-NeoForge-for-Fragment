package com.hm.efn.item.custom;

import net.minecraft.core.Holder;

import com.hm.efn.EFNCommonConfig;
import com.hm.efn.util.ItemStackData;
import com.p1nero.invincible.client.InvincibleKeyMappings;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent.Post;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
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
public class YamatoItem extends WeaponItem {
   private static final String KILL_COUNT_KEY = "KillCount";
   private static final String TOTAL_DAMAGE_KEY = "TotalDamage";
   private static final String REWARD_GIVEN_KEY = "RewardGiven";

   public YamatoItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
      super(properties.attributes(net.minecraft.world.item.SwordItem.createAttributes(tier, attackDamage, attackSpeed)));
   }

   public static int getKillCount(ItemStack stack) {
      return ItemStackData.getInt(stack, KILL_COUNT_KEY);
   }

   public static float getTotalDamage(ItemStack stack) {
      return ItemStackData.getFloat(stack, TOTAL_DAMAGE_KEY);
   }

   private static boolean isRewardGiven(ItemStack stack) {
      return ItemStackData.getBoolean(stack, REWARD_GIVEN_KEY);
   }

   private static void setRewardGiven(ItemStack stack, boolean given) {
      ItemStackData.putBoolean(stack, REWARD_GIVEN_KEY, given);
   }

   private static void incrementKillCount(ItemStack stack) {
      int currentCount = ItemStackData.getInt(stack, KILL_COUNT_KEY);
      ItemStackData.putInt(stack, KILL_COUNT_KEY, currentCount + 1);
   }

   private static void addTotalDamage(ItemStack stack, float damage) {
      float currentDamage = ItemStackData.getFloat(stack, TOTAL_DAMAGE_KEY);
      ItemStackData.putFloat(stack, TOTAL_DAMAGE_KEY, currentDamage + damage);
   }

   private static void checkAndGiveReward(ItemStack yamatoStack, Player player) {
      if ((Boolean)EFNCommonConfig.YAMATO_REWARD_ENABLED.get()) {
         int rewardThreshold = (Integer)EFNCommonConfig.YAMATO_REWARD_KILL_THRESHOLD.get();
         if (rewardThreshold > 0) {
            int killCount = getKillCount(yamatoStack);
            boolean rewardGiven = isRewardGiven(yamatoStack);
            if (killCount >= rewardThreshold && !rewardGiven) {
               setSkillReward(player);
               setRewardGiven(yamatoStack, true);
            }
         }
      }
   }

   private static void setSkillReward(Player player) {
      player.sendSystemMessage(
         Component.literal("MY POWER SHALL BE ABSOLUTE")
            .withStyle(ChatFormatting.BLUE)
            .withStyle(ChatFormatting.UNDERLINE)
            .withStyle(ChatFormatting.ITALIC)
            .withStyle(ChatFormatting.BOLD)
      );
      player.playSound(SoundEvents.ENDER_DRAGON_GROWL, 1.0F, 1.0F);
   }

   @SubscribeEvent
   public static void onLivingDamage(Post event) {
      if (event.getSource().getEntity() instanceof Player player) {
         ItemStack mainHand = player.getMainHandItem();
         if (mainHand.getItem() instanceof YamatoItem) {
            addTotalDamage(mainHand, event.getNewDamage());
         }
      }
   }

   @SubscribeEvent
   public static void onLivingDeath(LivingDeathEvent event) {
      if (event.getSource().getEntity() instanceof Player player) {
         ItemStack mainHand = player.getMainHandItem();
         if (mainHand.getItem() instanceof YamatoItem) {
            int oldKillCount = getKillCount(mainHand);
            incrementKillCount(mainHand);
            int newKillCount = getKillCount(mainHand);
            checkAndGiveReward(mainHand, player);
         }
      }
   }

   @SubscribeEvent
   public static void onLivingKnockBack(LivingKnockBackEvent event) {
      if (event.getEntity() instanceof Player player && isHoldingYamato(player)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onPlayerJump(LivingJumpEvent event) {
      if (event.getEntity() instanceof Player player && isHoldingYamato(player)) {
         player.setDeltaMovement(player.getDeltaMovement().add(0.0, 0.15, 0.0));
      }
   }

   @SubscribeEvent
   public static void onLivingFall(LivingFallEvent event) {
      if (event.getEntity() instanceof Player player && isHoldingYamato(player)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onLivingUpdate(Pre event) {
      Entity entity = event.getEntity();
      if (entity instanceof Player player) {
         if (isHoldingYamato(player) && player.hasEffect(MobEffects.LEVITATION)) {
            player.removeEffect(MobEffects.LEVITATION);
         }

         if (player.getRemainingFireTicks() > 0) {
            int fireTicks = player.getRemainingFireTicks();
            if (fireTicks > 20) {
               player.setRemainingFireTicks(fireTicks - 1);
               if (player.isOnFire()) {
                  player.clearFire();
               }
            }
         }
      }
   }

   private static boolean isHoldingYamato(Player player) {
      return player.getMainHandItem().getItem() instanceof YamatoItem || player.getOffhandItem().getItem() instanceof YamatoItem;
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
      return 100;
   }

   public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
      int killCount = getKillCount(stack);
      float totalDamage = getTotalDamage(stack);
      boolean rewardGiven = isRewardGiven(stack);
      tooltip.add(
         Component.translatable("item.efn.yamato.description1").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.UNDERLINE)
      );
      tooltip.add(
         Component.translatable("tooltip.efn.yamato.kill_count", new Object[]{killCount})
            .withStyle(ChatFormatting.BOLD)
            .withStyle(ChatFormatting.UNDERLINE)
            .withStyle(ChatFormatting.DARK_RED)
      );
      tooltip.add(
         Component.translatable("tooltip.efn.yamato.total_damage", new Object[]{String.format("%.1f", totalDamage)})
            .withStyle(ChatFormatting.BOLD)
            .withStyle(ChatFormatting.UNDERLINE)
            .withStyle(ChatFormatting.DARK_RED)
      );
      this.addSkillProgressTooltip(stack, tooltip, rewardGiven, killCount);
      if (InvincibleKeyMappings.KEY1.getKey().getValue() != 0) {
         tooltip.add(Component.translatable("tooltip.efn.keybind_warning"));
      }
   }

   private void addSkillProgressTooltip(ItemStack stack, List<Component> tooltip, boolean rewardGiven, int killCount) {
      if (!(Boolean)EFNCommonConfig.YAMATO_REWARD_ENABLED.get()) {
         tooltip.add(Component.translatable("tooltip.efn.yamato.skill_reward_disabled").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
      } else {
         int rewardThreshold = (Integer)EFNCommonConfig.YAMATO_REWARD_KILL_THRESHOLD.get();
         if (rewardThreshold <= 0) {
            tooltip.add(Component.translatable("tooltip.efn.yamato.skill_reward_unavailable").withStyle(ChatFormatting.GRAY).withStyle(ChatFormatting.ITALIC));
         } else {
            String progressBar = this.createProgressBar(killCount, rewardThreshold);
            int remainingKills = Math.max(0, rewardThreshold - killCount);
            tooltip.add(
               Component.translatable("tooltip.efn.yamato.skill_progress_title", new Object[]{remainingKills})
                  .withStyle(ChatFormatting.WHITE)
                  .withStyle(ChatFormatting.BOLD)
            );
            tooltip.add(Component.literal(progressBar).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
         }
      }
   }

   private String createProgressBar(int current, int max) {
      int barLength = 20;
      int filledLength = (int)((double)current / max * barLength);
      filledLength = Math.min(filledLength, barLength);
      return ChatFormatting.BLUE + "█".repeat(Math.max(0, filledLength)) + ChatFormatting.GRAY + "█".repeat(Math.max(0, barLength - filledLength));
   }
}
