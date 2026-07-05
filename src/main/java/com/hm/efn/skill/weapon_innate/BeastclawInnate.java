package com.hm.efn.skill.weapon_innate;

import com.google.common.collect.Lists;
import com.guhao.vix.client.event.ScreenEffectEngine;
import com.hm.efn.EFNClientConfig;
import com.hm.efn.client.screeneffect.RedColorEffect;
import com.hm.efn.gameasset.combos.Beastclaw;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.skill.EFNWeaponInnateBase;
import com.hm.efn.util.EFNSkillChecks;
import com.p1nero.invincible.attachment.InvincibleAttachments;
import com.p1nero.invincible.client.InvincibleKeyMappings;
import com.p1nero.invincible.skill.ComboBasicAttack.Builder;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.dodge.DodgeSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.api.event.EntityEventListener;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public class BeastclawInnate extends EFNWeaponInnateBase {
   private static final UUID DODGE_TRIGGER_UUID = UUID.fromString("e5f6a1b2-c8d1-11ed-a05b-0242ac120018");
   private static final UUID STAMINA_REDUCTION_UUID = UUID.fromString("4074c6de-0268-11ee-be56-0242ac120003");

   public BeastclawInnate(Builder builder) {
      super(builder);
   }

   public void updateContainer(SkillContainer container) {
      if (container.getExecutor().isLogicalClient() && (Boolean)EFNClientConfig.ENABLE_BEASTCLAW_POSTEFFECT.get()) {
         RedColorEffect effect = new RedColorEffect(
            ((Player)container.getExecutor().getOriginal()).position(),
            600,
            ((Player)container.getExecutor().getOriginal()).hasEffect(EFNMobEffectRegistry.CLAW),
            0.28F
         );
         ScreenEffectEngine.PushScreenEffect(effect);
      }

      super.updateContainer(container);
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
               if (this.isHoldingFist(player)) {
                  InvincibleAttachments.getPlayer(player).setCurrentNode(Beastclaw.DodgeCounter);
               }
            }
         }
      );
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, EventType.SKILL_CONSUME_EVENT, STAMINA_REDUCTION_UUID, event -> {
         if (event.getSkill() instanceof DodgeSkill && ((Player)container.getExecutor().getOriginal()).hasEffect(EFNMobEffectRegistry.CLAW)) {
            event.setAmount(event.getAmount() * 0.5F);
         }
      });
   }

   public void onRemoved(SkillContainer container) {
      super.onRemoved(container);
      EntityEventListener listener = container.getExecutor().getEventListener();
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.SKILL_CAST_EVENT, DODGE_TRIGGER_UUID);
      com.hm.efn.util.EFNEventBridge.removeListener(listener, EventType.SKILL_CONSUME_EVENT, STAMINA_REDUCTION_UUID);
      if (container.getExecutor().isLogicalClient()) {
         RedColorEffect effect = new RedColorEffect(
            ((Player)container.getExecutor().getOriginal()).position(),
            600,
            ((Player)container.getExecutor().getOriginal()).hasEffect(EFNMobEffectRegistry.CLAW),
            0.08F
         );
         ScreenEffectEngine.RemoveScreenEffect(effect);
      }
   }

   private boolean isHoldingFist(ServerPlayer player) {
      return Stream.of(player.getMainHandItem(), player.getOffhandItem())
         .<CapabilityItem>map(EpicFightCapabilities::getItemStackCapability)
         .filter(Objects::nonNull)
         .anyMatch(cap -> cap.getWeaponCategory() == WeaponCategories.FIST);
   }

   public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerpatch) {
      List<Component> list = Lists.newArrayList();
      String keyName = Component.translatable(InvincibleKeyMappings.KEY3.getName()).getString();
      list.add(Component.translatable("skill.efn.beastclaw.tooltip").withStyle(ChatFormatting.YELLOW));
      list.add(
         Component.translatable("skill.efn.beastclaw.tooltip1")
            .append(Component.literal(keyName))
            .append(": ")
            .withStyle(ChatFormatting.YELLOW)
            .append(InvincibleKeyMappings.KEY3.getTranslatedKeyMessage())
      );
      list.add(Component.translatable("skill.efn.beastclaw.tooltip2"));
      return list;
   }
}
