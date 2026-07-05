package com.hm.efn.skill.weapon_innate;

import com.google.common.collect.Lists;
import com.hm.efn.gameasset.EFNSKillDataKeys;
import com.hm.efn.gameasset.animations.EFNShortSwordAnimations;
import com.hm.efn.gameasset.combos.Shortsword;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.skill.EFNWeaponInnateBase;
import com.hm.efn.util.EFNSkillChecks;
import com.p1nero.invincible.attachment.InvincibleAttachments;
import com.p1nero.invincible.client.InvincibleKeyMappings;
import com.p1nero.invincible.skill.ComboBasicAttack.Builder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.api.event.EntityEventListener;
import com.hm.efn.compat.epicfight.eventlistener.DealDamageEvent.Attack;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public class ShortSwordInnate extends EFNWeaponInnateBase {
   private static final UUID DODGE_TRIGGER_UUID = UUID.fromString("e5f6a1b2-c8d1-11ed-a51b-0242ac121029");
   private static final UUID DAMAGE_LISTENER_UUID = UUID.fromString("f6e7b2c3-d9e2-12fd-b52c-1352bd232130");
   private static final UUID DAMAGE_ADDER_UUID = UUID.fromString("f6e7b2c3-d9e2-12fd-b52c-1352bd602102");

   public ShortSwordInnate(Builder builder) {
      super(builder);
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
      EntityEventListener listener = container.getExecutor().getEventListener();
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
         EventType.SKILL_CAST_EVENT,
         DODGE_TRIGGER_UUID,
         event -> {
            if (EFNSkillChecks.hasCategory(event.getSkillContainer(), SkillCategories.DODGE)
               && container.getExecutor() instanceof ServerPlayerPatch playerPatch) {
               ServerPlayer player = (ServerPlayer)playerPatch.getOriginal();
               if (this.isHoldingWeapon(container)) {
                  InvincibleAttachments.getPlayer(player).setCurrentNode(Shortsword.DodgeCounter);
               }
            }
         }
      );
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.DEAL_DAMAGE_EVENT_DAMAGE, DAMAGE_ADDER_UUID, event -> {
         if (container.getExecutor() instanceof ServerPlayerPatch serverPlayerPatch) {
            ServerPlayer player = (ServerPlayer)serverPlayerPatch.getOriginal();
            if (this.isHoldingWeapon(container) && event.getAttackDamage() > 0.0F) {
               this.recordDamage(container, event.getAttackDamage(), player.tickCount);
            }
         }
      });
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.DEAL_DAMAGE_EVENT_ATTACK, DAMAGE_LISTENER_UUID, event -> {
         if (container.getExecutor() instanceof ServerPlayerPatch serverPlayerPatch) {
            ServerPlayer player = (ServerPlayer)serverPlayerPatch.getOriginal();
            if (this.isHoldingWeapon(container) && event.getAttackDamage() > 0.0F && this.shouldApplyReplayBonus(serverPlayerPatch)) {
               this.applyReplayBonus(container, event, player.tickCount);
            }
         }
      });
   }

   private void recordDamage(SkillContainer container, float damage, int currentTick) {
      SkillDataManager data = container.getDataManager();
      Boolean isReplayDamage = (Boolean)data.getDataValue(EFNSKillDataKeys.IS_REPLAY_DAMAGE);
      if (Boolean.TRUE.equals(isReplayDamage)) {
         data.setData(EFNSKillDataKeys.IS_REPLAY_DAMAGE, false);
      } else {
         List<EFNSKillDataKeys.DamageRecord> history = new ArrayList<>(
            (Collection<? extends EFNSKillDataKeys.DamageRecord>)data.getDataValue(EFNSKillDataKeys.DAMAGE_HISTORY)
         );
         if (!history.isEmpty()) {
            this.cleanupExpiredRecords(history, currentTick);
         }

         history.add(new EFNSKillDataKeys.DamageRecord(damage, currentTick));
          data.setDataSync(EFNSKillDataKeys.DAMAGE_HISTORY, history);
      }
   }

   private void cleanupExpiredRecords(List<EFNSKillDataKeys.DamageRecord> history, int currentTick) {
      history.removeIf(record -> record.isExpired(currentTick));
   }

   private boolean shouldApplyReplayBonus(ServerPlayerPatch playerPatch) {
      if (playerPatch != null && playerPatch.getOriginal() != null) {
         boolean hasReplayEffect = ((ServerPlayer)playerPatch.getOriginal()).hasEffect(EFNMobEffectRegistry.REPLAY);
         boolean alreadyEnhanced = (Boolean)playerPatch.getSkill(this).getDataManager().getDataValue(EFNSKillDataKeys.REPLAY_ENHANCED);
         return hasReplayEffect && !alreadyEnhanced;
      } else {
         return false;
      }
   }

   private void applyReplayBonus(SkillContainer container, Attack event, int currentTick) {
      SkillDataManager data = container.getDataManager();
      List<EFNSKillDataKeys.DamageRecord> history = new ArrayList<>(
         (Collection<? extends EFNSKillDataKeys.DamageRecord>)data.getDataValue(EFNSKillDataKeys.DAMAGE_HISTORY)
      );
      if (!history.isEmpty()) {
         EFNSKillDataKeys.DamageRecord lastRecord = history.get(history.size() - 1);
         float bonusDamage = lastRecord.damage();
         int recordTimestamp = lastRecord.timestamp();
         if (new EFNSKillDataKeys.DamageRecord(0.0F, recordTimestamp).isExpired(currentTick)) {
            this.cleanupExpiredRecords(history, currentTick);
             data.setDataSync(EFNSKillDataKeys.DAMAGE_HISTORY, history);
         } else {
            this.cleanupExpiredRecords(history, currentTick);
            data.setData(EFNSKillDataKeys.IS_REPLAY_DAMAGE, true);
            EpicFightDamageSource damageSource = event.getDamageSource();
            damageSource.attachDamageModifier(ValueModifier.adder(bonusDamage));
            damageSource.attachArmorNegationModifier(ValueModifier.setter(100.0F));
             data.setDataSync(EFNSKillDataKeys.REPLAY_ENHANCED, true);
            ServerPlayer player = (ServerPlayer)((ServerPlayerPatch)container.getExecutor()).getOriginal();
            if (player.hasEffect(EFNMobEffectRegistry.REPLAY)) {
               player.removeEffect(EFNMobEffectRegistry.REPLAY);
            }

             data.setDataSync(EFNSKillDataKeys.DAMAGE_HISTORY, history);
         }
      }
   }

   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      if (!container.getExecutor().isLogicalClient() && container.getExecutor() instanceof ServerPlayerPatch serverPlayerPatch) {
         SkillDataManager manager = container.getDataManager();
         PlayerPatch<?> executor = container.getExecutor();
         if (manager.hasData(EFNSKillDataKeys.COMBO_COUNTER)) {
            float current = (Float)manager.getDataValue(EFNSKillDataKeys.COMBO_COUNTER);
            if (current < 1.0F) {
               float newValue = current + 0.05F;
               manager.setDataSync(EFNSKillDataKeys.COMBO_COUNTER, newValue);
            } else {
               manager.setDataSync(EFNSKillDataKeys.COMBO_COUNTER, 0.0F);
            }
         }

         if ((Boolean)manager.getDataValue(EFNSKillDataKeys.KEY3_PRESS)) {
            ServerPlayer player = (ServerPlayer)serverPlayerPatch.getOriginal();
            SkillContainer weaponInnate = container.getExecutor().getSkill(SkillSlots.WEAPON_INNATE);
            boolean hasEnergy = weaponInnate != null && weaponInnate.hasSkill() && (weaponInnate.getStack() >= 1 || player.getAbilities().instabuild);
            if (hasEnergy) {
               MobEffectInstance replayEffect = new MobEffectInstance(EFNMobEffectRegistry.REPLAY, 100, 0, false, false, false);
               if (!executor.getEntityState().inaction()) {
                  serverPlayerPatch.playAnimationSynchronized(EFNShortSwordAnimations.NF_SHORTSWORD_SKILL, 0.05F);
               }

               serverPlayerPatch.playSound(SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), 1.0F, 1.0F);
               player.addEffect(replayEffect);
               if (!player.getAbilities().instabuild) {
                  weaponInnate.getSkill().setStackSynchronize(weaponInnate, weaponInnate.getStack() - 1);
               }
            } else {
               serverPlayerPatch.playSound(SoundEvents.REDSTONE_TORCH_BURNOUT, 0.5F, 1.0F);
            }

            manager.setDataSync(EFNSKillDataKeys.KEY3_PRESS, false);
         }

         if (manager.hasData(EFNSKillDataKeys.REPLAY_ENHANCED)
            && !((ServerPlayer)serverPlayerPatch.getOriginal()).hasEffect(EFNMobEffectRegistry.REPLAY)) {
            manager.setDataSync(EFNSKillDataKeys.REPLAY_ENHANCED, false);
         }
      }
   }

   public void onRemoved(SkillContainer container) {
      super.onRemoved(container);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.SKILL_CAST_EVENT, DODGE_TRIGGER_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.DEAL_DAMAGE_EVENT_ATTACK, DAMAGE_LISTENER_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.DEAL_DAMAGE_EVENT_DAMAGE, DAMAGE_ADDER_UUID);
   }

   private boolean isHoldingWeapon(SkillContainer container) {
      ItemStack itemstack = ((Player)container.getExecutor().getOriginal()).getMainHandItem();
      return EFNSkillChecks.isInnateSkill(container.getExecutor(), itemstack, this);
   }

   public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerpatch) {
      List<Component> list = Lists.newArrayList();
      String keyName = Component.translatable(InvincibleKeyMappings.KEY3.getName()).getString();
      list.add(Component.translatable("skill.efn.shortsword.tooltip").withStyle(ChatFormatting.GRAY));
      list.add(
         Component.translatable("skill.efn.shortsword.tooltip1")
            .append(Component.literal(keyName))
            .append(": ")
            .withStyle(ChatFormatting.GRAY)
            .append(InvincibleKeyMappings.KEY3.getTranslatedKeyMessage())
      );
      list.add(Component.translatable("skill.efn.shortsword.tooltip2"));
      return list;
   }
}
