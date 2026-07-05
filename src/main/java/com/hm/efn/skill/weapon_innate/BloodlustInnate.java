package com.hm.efn.skill.weapon_innate;

import com.google.common.collect.Lists;
import com.guhao.vix.client.event.ScreenEffectEngine;
import com.hm.efn.EFNClientConfig;
import com.hm.efn.client.screeneffect.HsvFilterEffect;
import com.hm.efn.gameasset.combos.Bloodlust;
import com.hm.efn.registries.EFNMobEffectRegistry;
import com.hm.efn.skill.EFNWeaponInnateBase;
import com.hm.efn.util.EFNSkillChecks;
import com.p1nero.invincible.attachment.InvincibleAttachments;
import com.p1nero.invincible.client.InvincibleKeyMappings;
import com.p1nero.invincible.skill.ComboBasicAttack.Builder;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.api.event.EntityEventListener;
import com.hm.efn.compat.epicfight.eventlistener.PlayerEventListener.EventType;

public class BloodlustInnate extends EFNWeaponInnateBase {
   private static final UUID DODGE_TRIGGER_UUID = UUID.fromString("e5f6a1b2-c8d1-11ed-a51b-0242ac120019");

   public BloodlustInnate(Builder builder) {
      super(builder);
   }

   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      if (container.getExecutor().isLogicalClient() && (Boolean)EFNClientConfig.ENABLE_BLOODLUST_POSTEFFECT.get()) {
         HsvFilterEffect effect = new HsvFilterEffect(
            ((Player)container.getExecutor().getOriginal()).position(),
            20,
            ((Player)container.getExecutor().getOriginal()).hasEffect(EFNMobEffectRegistry.BLODDLUST)
         );
         ScreenEffectEngine.PushScreenEffectADD(effect);
      }
   }

   public void onInitiate(SkillContainer container, yesman.epicfight.api.event.EntityEventListener eventListener) {
      super.onInitiate(container, eventListener);
      SkillDataManager data = container.getDataManager();
      EntityEventListener listener = container.getExecutor().getEventListener();
      com.hm.efn.util.EFNEventBridge.addEventListener(listener, 
         EventType.SKILL_CAST_EVENT,
         DODGE_TRIGGER_UUID,
         event -> {
            if (EFNSkillChecks.hasCategory(event.getSkillContainer(), SkillCategories.DODGE)
               && container.getExecutor() instanceof ServerPlayerPatch playerPatch) {
               ServerPlayer player = (ServerPlayer)playerPatch.getOriginal();
               if (this.isHoldingWeapon(container)) {
                  InvincibleAttachments.getPlayer(player).setCurrentNode(Bloodlust.DodgeCounter);
               }
            }
         }
      );
   }

   private boolean isHoldingWeapon(SkillContainer container) {
      ItemStack itemstack = ((Player)container.getExecutor().getOriginal()).getMainHandItem();
      return EFNSkillChecks.isInnateSkill(container.getExecutor(), itemstack, this);
   }

   public void onRemoved(SkillContainer container) {
      super.onRemoved(container);
      com.hm.efn.util.EFNEventBridge.removeListener(container.getExecutor().getEventListener(), EventType.SKILL_CAST_EVENT, DODGE_TRIGGER_UUID);
   }

   public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerpatch) {
      List<Component> list = Lists.newArrayList();
      String keyName = Component.translatable(InvincibleKeyMappings.KEY3.getName()).getString();
      list.add(Component.translatable("skill.efn.bloodlust.tooltip").withStyle(ChatFormatting.RED));
      list.add(
         Component.translatable("skill.efn.bloodlust.tooltip1")
            .append(Component.literal(keyName))
            .append(": ")
            .withStyle(ChatFormatting.RED)
            .append(InvincibleKeyMappings.KEY3.getTranslatedKeyMessage())
      );
      list.add(Component.translatable("skill.efn.bloodlust.tooltip2"));
      return list;
   }
}
